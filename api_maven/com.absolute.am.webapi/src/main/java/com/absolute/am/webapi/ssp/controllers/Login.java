/**
 * 
 */
package com.absolute.am.webapi.ssp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.absolute.am.webapi.Application;
import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CobraUserCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.model.LogonResult;
import com.absolute.am.model.SSPLogonRequest;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * @author ephilippov
 *
 */
@Path ("/ssp/login")
public class Login {

    private static Logger m_logger = LoggerFactory.getLogger(Login.class.getName()); 
		
	/**
	 * The name used for the SessionTimeout value in the Logon result. 
	 */
	private static final String SESSION_TIMEOUT_RESULT_PARAM = "SessionTimeout";
	
	/**
	 * The name used for the SessionToken value in the AM Server's user login result. 
	 */
	public static final String SESSION_TOKEN_PARAM = "SessionToken";
	
	/**
	 * A session variable name that stores the value for whether we have an SSP session or not.
	 */
	public static final String IS_SSP_SESSION = "IsSSPLogin";
	
	/**
	 * A temporary copy of the protocol settings that will be saved in the session
	 * if the login is successful. 
	 */
	private AMServerProtocolSettings m_protocolSettings;
		
	/**
	 * The following values are added to the login result specifically for the purposes of the webapi.
	 */
	public static final String kCobra_Admin_CanSeeAllRecords_Param = "CanSeeAllRecords";
	private static final String SERVER_NAME_PARAM = "ServerName";
	private static final String SERVER_PORT_PARAM = "ServerPort";
	private static final String USER_NAME_PARAM = "UserName";
	private static final String WEB_API_VERSION_PARAM = "WebAPIVersion";
	private static final String LOCALE_PARAM = "Locale";
	public static final String INI_SSP_FORCE_SERVER_PROMPT = "com.absolute.am.webapi.ssp.controllers.Login.SspForceServerPrompt";
	private static final String INI_LOGIN_ENABLED = "com.absolute.am.webapi.ssp.controllers.Login.Enabled";
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * Used to confirm that the end point is responding. This operation originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this operation is executed using credentials of the end user, not the administrator.
	 * @return echo the data returned during POST login
	 * @throws UnsupportedEncodingException 
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public LogonResult DoGet(@Context HttpServletRequest req) throws UnsupportedEncodingException {
		m_logger.debug("SSP.Login.DoGet called");
		LogonResult retVal = new LogonResult();
		HttpSession session = req.getSession(false);

		boolean loginEnabled = Application.getRuntimeProperty(INI_LOGIN_ENABLED).equals("true");
		
		// throw exceptions in English since we don't have the user's locale preference yet
		if (session != null && session.getAttribute(com.absolute.am.webapi.ssp.controllers.Login.IS_SSP_SESSION) != null) {
			retVal.setResultParameters(SessionState.getLogonResultParameters(req.getSession()));	
		} else if (!loginEnabled){
			throw new AMWebAPILocalizedException(Response.Status.FORBIDDEN, "FORBIDDEN", "FORBIDDEN", null, "es_US", m_Base);
		} else {
			throw new AMWebAPILocalizedException(Response.Status.UNAUTHORIZED, "UNAUTHORIZED", "UNAUTHORIZED", null, "es_US", m_Base);
		}
		
		return retVal;
	}	
	
	/**
	 * Accepts a login request, validates the credentials and establishes the
	 * session. If the credentials are invalid, an UNAUTHORIZED exception is thrown. This operation originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this operation is executed using credentials of the end user, not the administrator.
	 * @param req
	 * @param userCredentials ( domain, username and password )
	 * @return session and AM Server related data ( retrieved from AM Server )
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public LogonResult PostLoginCredentials(
			@Context HttpServletRequest req,
			SSPLogonRequest logonRequest) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception 
	{
		MDC.put("userCredentials", logonRequest.toString());

		boolean loginEnabled = Application.getRuntimeProperty(INI_LOGIN_ENABLED).equals("true");
		if(!loginEnabled){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
		
		// Always invalidate the current session.
		HttpSession session = req.getSession(false);
		PropertyList logonResult = new PropertyList();
		
		if (session != null) {
			session.invalidate(); 
		}
		
		//locale is extracted first, so that exceptions can be localized
		String locale = logonRequest.getLocale(), serverName = null, serverPort = null;
		if (null == locale || locale.length()==0) {
			locale = "en_US";
		}
				
		try {
			logonResult = logonToServer(req.getServletContext(), logonRequest);
		} catch (AMServerProtocolException ape) {
			// logonToServer does not call the Validate() version of the API, so the only exceptions that are thrown
			// are due to connectivity issues.
			m_logger.info("Failed to logon with {} due to this error:{}",  logonRequest.toString(), ape.getMessage());
			throw new WebAPIException(Response.Status.SERVICE_UNAVAILABLE, "LOGIN_LOGIN_FAILED", "LOGIN_FAILED_TO_CONNECT", null, locale, ResourceUtilities.WEBAPI_BASE);
		} catch (Exception e) {
			// This is deliberately vague to prevent hackers from learning about our internal network.
			e.printStackTrace();
			throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
		}
		
		long resultError = (Long)logonResult.get(CobraProtocol.kCobra_XML_CommandResultError);	
		Map<String, Object> resultParams = (HashMap<String, Object>)logonResult.get(CobraProtocol.kCobra_XML_CommandResultParameters);
		
		if (resultError != 0 || (resultParams.get("AuthenticationFailed") != null && resultParams.get("AuthenticationFailed").toString().equals("true")) ||
				resultParams.get(SESSION_TOKEN_PARAM) == null) {
			m_logger.debug("resultError={} logonResult={}.", resultError, logonResult.toString());
			
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		
		try {
			if (logonRequest.getServerName() != null && ! logonRequest.getServerName().isEmpty() &&
					logonRequest.getServerPort() != null && ! logonRequest.getServerPort().isEmpty()) {
				serverName = logonRequest.getServerName();
				serverPort = logonRequest.getServerPort();
			} else {
				serverName = Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_NAME);
				serverPort = Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_PORT);
			}
		} catch (RuntimeException e) {
			throw new InternalServerErrorException("LOGIN_FAILED_TO_CONNECT", "LOGIN_NO_DEFAULT_SERVER_NAME_OR_PORT", null, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		session = req.getSession(true); // create one if none exists.
		if (null == session) {
			throw new InternalServerErrorException("LOGIN_FAILED_TO_CREATE_SESSION", "LOGIN_GETSESSION_RETURNED_NULL", null, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		// Add the sessionid to the MDC as soon as we get one. For all other requests this is done by the filter.
		Application.putSessionIdInMDC(session.getId());		
		int sessionTimeoutSeconds = Integer.parseInt(resultParams.get(SESSION_TIMEOUT_RESULT_PARAM).toString());

		session.setMaxInactiveInterval(sessionTimeoutSeconds);
		session.setAttribute(SESSION_TOKEN_PARAM, resultParams.get(SESSION_TOKEN_PARAM));
		session.setAttribute(IS_SSP_SESSION, true);
		
		// Prepare the result for the client.
		LogonResult retVal = new LogonResult();
		
		// add the WebAPI specific data
		resultParams.put(SESSION_TIMEOUT_RESULT_PARAM, sessionTimeoutSeconds);
		resultParams.put(SERVER_NAME_PARAM, serverName);
		resultParams.put(SERVER_PORT_PARAM, serverPort);
		resultParams.put(USER_NAME_PARAM, logonRequest.getUserName());
		resultParams.put(WEB_API_VERSION_PARAM, Application.WEB_API_VERSION);
		resultParams.put(LOCALE_PARAM, locale);
		
		// Save the response so it can be returned at any time via GET.
		retVal.setResultParameters(resultParams);

		// update the session state.
		SessionState.setLogonResultParameters(session, retVal.getResultParameters());
		SessionState.setAMServerProtocolSettings(session, m_protocolSettings);
		SessionState.setLocale(session, locale);
		SessionState.setLocaleDbSuffix(session, ResourceUtilities.getSupportedLocaleDbSuffix(locale, Application.getRuntimeProperty("dbSupportedLocales")));

		MDC.remove("userCredentials");
		
		m_logger.debug("SSP User {} successfully logged in.", logonRequest.getUserName());
		
		return retVal;
	}
	
	/**
	 * Accepts a login request, validates the credentials and establishes the
	 * session. If the credentials are invalid, an UNAUTHORIZED exception is thrown. This operation originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this operation is executed using credentials of the end user, not the administrator.
	 * @param req
	 * @param userCredentials
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON })
	public LogonResult PostLoginCredentials(
			@Context HttpServletRequest req,
			@FormParam("DefaultServerName") String serverName,
			@FormParam("DefaultServerPort") String serverPort,
			@FormParam("Domain") String domain,
			@FormParam("UserName") String userName,
			@FormParam("Password") String password,
			@DefaultValue("en_US") @FormParam("Locale") String locale
			) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
		
		SSPLogonRequest loginCredentials = new SSPLogonRequest();
		boolean forceSSPLoginPrompt = Application.getRuntimeProperty(INI_SSP_FORCE_SERVER_PROMPT).equals("true");

		loginCredentials.setUserName(userName);
		loginCredentials.setPassword(password);
		loginCredentials.setLocale(locale);
		loginCredentials.setDomain(domain);
		loginCredentials.setServerName(serverName);
		loginCredentials.setServerPort(serverPort);
		
		LogonResult logonResult = PostLoginCredentials(req, loginCredentials);
		
		// write the server name and port # to settings.txt when login is successful
		if (!forceSSPLoginPrompt && logonResult != null) {
			Map<String, Object> resultParams = logonResult.getResultParameters();
			if (resultParams != null) {
				if (resultParams.get(SERVER_NAME_PARAM) != null &&
						resultParams.get(SERVER_PORT_PARAM) != null &&
						resultParams.get(USER_NAME_PARAM) != null) {
					if((serverName != null && serverPort != null && Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_NAME) == null
							&& Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_PORT) == null) || forceSSPLoginPrompt){
						Application.setRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_NAME, serverName);
						Application.setRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_PORT, serverPort);
					}
					
					DefaultServerInfo.setDefaultServerNameAndPort(serverName, serverPort, userName);
				}
			}
		}
		
		return logonResult;
	}

	
	/**
	 * Implements logout as a DELETE operation. This operation originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this operation is executed using credentials of the end user, not the administrator.
	 * @param req
	 */
	@DELETE
	public void Logout(@Context HttpServletRequest req) {		
		m_logger.debug("logging out.");
		// Always invalidate the current session.
		HttpSession session = req.getSession(false);
		if (session != null) {
	
			// Note: all session listeners are triggered at this point.
			session.invalidate();
			m_logger.debug("after session.invalidate()");
			
			// The sessionid in the MDC is no longer valid, remove it.
			Application.removeSessionIdFromMDC();
		}				
	}
	
	/**
	 * Reloads the logback config file, c:/am_mdm/AM_MDM_Logback.xml. This operation originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this operation is executed using credentials of the end user, not the administrator.
	 */
	@POST
	public void PostReloadLoggingConfig() {
		try {
			InitialContext initialContext = new javax.naming.InitialContext();   
			String fileLocation = (String)initialContext.lookup("java:comp/env/logback_config_file");
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory(); 
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);   
			configurator.doConfigure(fileLocation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private PropertyList logonToServer(ServletContext sc, SSPLogonRequest logonRequest) 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, SAXException, AMServerProtocolException {
		
		String locale = logonRequest.getLocale();
		PropertyList loginResult = null;
		CobraUserCommand loginCommand = CommandFactory.createSSPLoginCommand(
				logonRequest.getUserName(),
				logonRequest.getPassword(),
				logonRequest.getDomain());

		String pathToTrustedCerts = sc.getInitParameter(Application.INI_PATH_TO_TRUSTED_CERTS);
		if (null == pathToTrustedCerts ) {
			String exMessage = ResourceUtilities.getLocalizedFormattedString("SETTING_IS_REQUIRED", new String[]{Application.INI_PATH_TO_TRUSTED_CERTS}, locale, ResourceUtilities.WEBAPI_BASE);
			throw new IllegalArgumentException(exMessage);
		}
	
		// Make sure it ends in /
		if (!pathToTrustedCerts.endsWith(File.separator)) {
			pathToTrustedCerts = pathToTrustedCerts + File.separator; 
		}
		
		if (logonRequest.getServerName() != null && !logonRequest.getServerName().isEmpty() && 
			logonRequest.getServerPort() != null && !logonRequest.getServerPort().isEmpty()) {
			m_protocolSettings = new AMServerProtocolSettings(
					logonRequest.getServerName(), 
					Short.parseShort(logonRequest.getServerPort()),
					pathToTrustedCerts
					);
		} else {
			m_protocolSettings = new AMServerProtocolSettings(
					Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_NAME), 
					Short.parseShort(Application.getRuntimeProperty(DefaultServerInfo.PROP_DEFAULT_SERVER_PORT)),
					pathToTrustedCerts
					);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(m_protocolSettings);
		
		try {
			loginResult = amServerProtocol.sendCommandAndGetResponse(loginCommand);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		return loginResult;
	}
}
