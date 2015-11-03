/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;





import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.ResultHelper;
import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.ISyncService;
import com.absolute.am.model.LogonRequest;
import com.absolute.am.model.LogonResult;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * The login API is used to authenticate the client. 
 * 
 * @author dlavin
 *
 */
@Path ("/login")
public class Login {

    private static Logger m_logger = LoggerFactory.getLogger(Login.class.getName()); 

	/**
	 * How long the session can be inactive before it becomes invalid.
	 */
	public static final String INI_SESSION_TIMEOUT_SECONDS = "com.absolute.am.webapi.controllers.Login.SessionTimeoutSeconds";
	
	/**
	 * The default AM server port number
	 */
	public static final String INI_DEFAULT_SERVER_PORT = "com.absolute.webapi.controllers.login.defaultServerPort";
	
	/**
	 * A temporary copy of the protocol settings that will be saved in the session
	 * if the login is successful. 
	 */
	private AMServerProtocolSettings m_protocolSettings;
	
	/** 
	 * A temporary copy of the server certificate. This is initialized when a connection to the server is successful.
	 */
	private byte[] m_serverCertificate;
	
	/** 
	 * A temporary copy of the server unique id. This is initialized when a connection to the server is successful.
	 */
	private String m_serverUniqueId;
	
	/**
	 * The name used for the SessionTimeout value in the Logon result. 
	 */
	private static final String SESSION_TIMEOUT_RESULT_PARAM = "SessionTimeout";

	/**
	 * The name used for the EnableLiveDataUpdates value in the Logon result.
	 */
	private static final String ENABLE_LIVE_DATA_UPDATES_PARAM = "EnableLiveDataUpdates";

	/**
	 * The name used for the DelayLiveDataUpdates value in the Logon result.
	 */
	private static final String DELAY_LIVE_DATA_UPDATES_PARAM = "DelayLiveDataUpdates";
	
	/*
	 *  This is the key value for the enum_ErrlrMsg table. It maps to an error when "the account is disabled".
	 *  For us this usually means that the admin user does not have the "Login enabled" permission as can be seen in AM Admin. 
	 */
	private static final long ACCOUNT_DISABLED_ENUM_KEY = 536883925;
	
	/**
	 * The following values are added to the login result specifically for the purposes of the webapi.
	 */
	private static final String kCobra_Admin_CommandPermissionsLow32_Param = com.absolute.am.command.CobraCommandDefs.kCobra_Admin_CommandPermissions_Param + "Low32";
	private static final String kCobra_Admin_CommandPermissionsHigh32_Param = com.absolute.am.command.CobraCommandDefs.kCobra_Admin_CommandPermissions_Param + "High32";
	public static final String kCobra_Admin_CanSeeAllRecords_Param = "CanSeeAllRecords";
	private static final String SERVER_NAME_PARAM = "ServerName";
	private static final String SERVER_PORT_PARAM = "ServerPort";
	private static final String USER_NAME_PARAM = "UserName";
	public static final String WEB_API_VERSION_PARAM = "WebAPIVersion";
	private static final String LOCALE_PARAM = "Locale";
	
	/**
	 * If the user is authenticated and the session is still live, the response will match that of the POST above.  
	 * 
	 * @return LogonResult object. 
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	@StatusCodes ({
		  @ResponseCode ( code = 401, condition = "the user is not logged on."),
		})
	public LogonResult DoGet(@Context HttpServletRequest req) {
		m_logger.debug("Login.DoGet called");
		LogonResult retVal = new LogonResult();
		HttpSession session = req.getSession(false);
		
		if (session != null && session.getAttribute(com.absolute.am.webapi.ssp.controllers.Login.IS_SSP_SESSION) == null) {
			retVal.setResultParameters(SessionState.getLogonResultParameters(req.getSession()));	
		} else {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		
		return retVal;
	}	
	
	/**
	 * <span id="shortVersionLogonTag"></span>
	 * <p>The body of the POST includes User Credentials parameter <strong>logonRequest</strong> with server name, server port, username, password and locale.</p>
	 * <p>See <a href="#fullVersionLogonTag">another version</a> of input parameters and full description</p>
	 * 
	 * @param req
	 * @param logonRequest User Credentials parameter
	 * @return LogonResult object is returning. 
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 200, condition = "If access is permitted, then a HTTP 200 status code will be returned with a set-cookie header containing the authentication token."),
		  @ResponseCode ( code = 401, condition = "The the credentials are not valid for the given server and port."),
		  @ResponseCode ( code = 403, condition = "A command is not available to a user. Required right has not been assigned to the current user."),
		  @ResponseCode ( code = 503, condition = "A connection cannot be established with the server. This can happen for a variety of reasons, including unknown serverName, incorrect serverPort, network connectivity issues, untrusted certificate, etc.")
		})
	public LogonResult PostLoginCredentials(
			@Context HttpServletRequest req,
			LogonRequest logonRequest) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception 
	{
		MDC.put("userCredentials", logonRequest.toString());

		// Always invalidate the current session.
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate(); 
		}
		
		PropertyList logonResult;
		//locale is extracted first, so that exceptions can be localized
		String locale = logonRequest.getLocale();
		if (null == locale || locale.length()==0) {
			locale = "en_US";
		}
		
		// reset the Server Port with default value if no port passed in 
		if (logonRequest.getServerPort() <= 0) {
			logonRequest.setServerPort(getDefaultServerPort(req, locale));
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
			throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
		}

		long resultError = (Long)logonResult.get(CobraProtocol.kCobra_XML_CommandResultError);	

		if( resultError == ACCOUNT_DISABLED_ENUM_KEY ){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		} else if (resultError != 0) {
			m_logger.debug("resultError={} logonResult={}.", resultError, logonResult.toString());
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
			
		// start the sync session.		
		Object syncServiceSession = startSyncServiceSession(Application.getRuntimeProperty(ISyncService.PROP_SYNC_NOTIFY_URL));

		session = req.getSession(true); // create one if none exists.
		if (null == session) {
			throw new InternalServerErrorException("LOGIN_FAILED_TO_CREATE_SESSION", "LOGIN_GETSESSION_RETURNED_NULL", null, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		// Add the sessionid to the MDC as soon as we get one. For all other requests this is done by the filter.
		Application.putSessionIdInMDC(session.getId());
		
		// Save the sync session Token
		SessionState.setSyncServiceSession(session, syncServiceSession);

		// Read and validate the session timeout value.		
		String sessionTimeoutSecondsString = req.getServletContext().getInitParameter(INI_SESSION_TIMEOUT_SECONDS);
		if (null == sessionTimeoutSecondsString || sessionTimeoutSecondsString.length()==0) {
			throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_NOT_FOUND", new Object[]{INI_SESSION_TIMEOUT_SECONDS}, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		int sessionTimeoutSeconds = Integer.parseInt(sessionTimeoutSecondsString);
		if (sessionTimeoutSeconds <=0) {
			throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_MUST_BE_GREATER_THAN_ZERO", new Object[]{INI_SESSION_TIMEOUT_SECONDS}, locale, ResourceUtilities.WEBAPI_BASE);
		}

		session.setMaxInactiveInterval(sessionTimeoutSeconds);

		// Prepare the result for the client.
		Map<String, Object> resultParameters = PropertyList.getElementAs(logonResult, CobraProtocol.kCobra_XML_CommandResultParameters); 
		LogonResult retVal = new LogonResult();
		Map<String, Object> cleanLogonResult = cleanLogonResultParameters(resultParameters);
		
		// add the WebAPI specific data
		cleanLogonResult.put(ENABLE_LIVE_DATA_UPDATES_PARAM, Application.getRuntimeProperty(Application.INI_ENABLE_LIVE_DATA_UPDATES).equals("true"));
		cleanLogonResult.put(DELAY_LIVE_DATA_UPDATES_PARAM, Application.getRuntimeProperty(Application.INI_DELAY_LIVE_DATA_UPDATES));
		cleanLogonResult.put(SESSION_TIMEOUT_RESULT_PARAM, sessionTimeoutSeconds);
		cleanLogonResult.put(SERVER_NAME_PARAM, logonRequest.getServerName());
		cleanLogonResult.put(SERVER_PORT_PARAM, logonRequest.getServerPort());
		cleanLogonResult.put(USER_NAME_PARAM, logonRequest.getUserName());
		cleanLogonResult.put(WEB_API_VERSION_PARAM, Application.WEB_API_VERSION);
		cleanLogonResult.put(LOCALE_PARAM, locale);
		
		// Javascript can only perform bitwise operations on 32 bit values, provide the CommandPermissions value in two parts.
		Map<String, Object> adminInfo = PropertyList.getElementAsMap(cleanLogonResult, "AdminInfo");
		Long commandPermissions = (Long)adminInfo.get(com.absolute.am.command.CobraCommandDefs.kCobra_Admin_CommandPermissions_Param);
		long commandPermissionsLow = (commandPermissions & 0xFFFFFFFFL);
		adminInfo.put(kCobra_Admin_CommandPermissionsLow32_Param, commandPermissionsLow);
		long commandPermissionsHigh = (int) ((commandPermissions & 0xFFFFFFFF00000000L) >> 32);
		adminInfo.put(kCobra_Admin_CommandPermissionsHigh32_Param, commandPermissionsHigh);
		Long canSeeAllRecords = (Long)adminInfo.get(kCobra_Admin_CanSeeAllRecords_Param);	// needed below.		

		
		// Save the response so it can be returned at anytime via GET.
		retVal.setResultParameters(cleanLogonResult);

		// update the session state.
		SessionState.setLogonResultParameters(session, retVal.getResultParameters());
		SessionState.setAMServerProtocolSettings(session, m_protocolSettings);
		UUID adminUUID = ResultHelper.getAdminUUIDFromLogonResult(logonResult);
		SessionState.setAdminUUID(session, adminUUID);
		String supportedDbLocales = Application.getRuntimeProperty("dbSupportedLocales");
		String localeDbSuffix = ResourceUtilities.getSupportedLocaleDbSuffix(locale, supportedDbLocales);
			
		SessionState.setLocale(session, locale);
		SessionState.setLocaleDbSuffix(session, localeDbSuffix);
		
		IDal dal = Application.getDal(session);
		
		// populate admin-to-mobile device assignments
		// dal.getAdminAccessHandler().refreshAllAdmins();
		
		// If the user is not configured to "see all records", set the AdminUUID as a filter here. 
		if (canSeeAllRecords != 1) {
			String adminUUIDAsString = StringUtilities.UUIDToStringUpper(adminUUID);
			SessionState.setFilterByAdmin(session, adminUUIDAsString);	
			dal.getAdminAccessHandler().prepareAccessForAdmin(adminUUIDAsString);
		}

		MDC.remove("userCredentials");
		m_logger.debug("User {} successfully logged in.", logonRequest.getUserName());
		return retVal;
	}
	
	/**
	 * <span id="fullVersionLogonTag"></span>
	 * <p>The body of the POST includes: server name, server port, username, password and locale.</p>
	 * <p>See <a href="#shortVersionLogonTag">version with logonRequest</a> as an input parameter</p>
	 * 
	 * <p>This is an example:</p>
	 * <pre>
	 * {
	 * &emsp;	"serverName":"dv2wlssmdm1",
	 * &emsp;	"serverPort":3971,
	 * &emsp;	"userName":"theboss",
	 * &emsp;	"password":"pass;word",
	 * &emsp;	"locale":"en-ca"
	 * }
	 * </pre>
	 * 
	 * <p>The server name and port are used to connect to the AM server.<br/> The user credentials are validated by the AM Server.</p>
	 * <p>If access is permitted, then a <strong>HTTP 200</strong> status code will be returned with a set-cookie header containing the authentication token. 
	 *    Subsequent requests from this client will include the authentication token as a cookie. The AM Web API may update the cookie at any time.</p>
	 * <p>The body of the response will consist of a subset of the <strong>kCobra_XML_CommandResultParameters</strong> portion of the AM server response. 
	 *    This includes information about the AM Server and the user’s capabilities, etc.  </p>
	 * <p>Here is an example of the response:</p>
	 * <pre>
	 * {
	 * &emsp;"resultParameters":{
	 * &emsp;		"AdminInfo":{
	 * &emsp;			"AdminUUID":"A7400562-0CB0-4244-80AD-CA527295179",
	 * &emsp;			"AllowChangeAgentClientInfoSettings":1,
	 * &emsp;			"AllowChangeAgentCustomFieldSettings":1,
	 * &emsp;			"AllowChangeAgentGeneralSettings":1,
	 * &emsp;			"AllowChangeAgentServerSettings":1,
	 * &emsp;			"AllowChangeComputerTracking":1,
	 * &emsp;			"AllowChangeComputerTrackingScreenshot":1,
	 * &emsp;			"AllowChangeCustomFields":1,
	 * &emsp;			"AllowChangeHistoryOptions":1,
	 * &emsp;			"AllowChangeMobileDeviceTracking":1,
	 * &emsp;			"AllowEnterCustomFieldData":1,
	 * &emsp;			"AllowManageiOSDevices":1,
	 * &emsp;			"AllowModifyMobileActions":1,
	 * &emsp;			"AllowModifyMobileMedia":1,
	 * &emsp;			"AllowModifySDGroups":1,
	 * &emsp;			"AllowModifySDImages":1,
	 * &emsp;			"AllowModifySDPackages":1,
	 * &emsp;			"AllowModifySLPackages":1,
	 * &emsp;			"AllowModifyStagingServers":1,
	 * &emsp;			"AllowModifyiOSApplications":1,
	 * &emsp;			"AllowModifyiOSConfigurationProfiles":1,
	 * &emsp;			"AllowModifyiOSPolicies":1,
	 * &emsp;			"AllowRemoteControl":1,
	 * &emsp;			"AllowRemoveCommandsFromHistory":1,
	 * &emsp;			"AllowRemoveComputerRecords":1,
	 * &emsp;			"AllowRemoveInventoryData":1,
	 * &emsp;			"AllowRemoveLicenseReports":1,
	 * &emsp;			"AllowRemoveSDLogEntry":1,
	 * &emsp;			"AllowRemoveiOSDeviceRecords":1,
	 * &emsp;			"AllowRemoveiOSHistoryCommands":1,
	 * &emsp;			"AllowResetSDPackges":1,
	 * &emsp;			"AllowRetrySDPackges":1,
	 * &emsp;			"AllowViewAdminCenter":1,
	 * &emsp;			"AllowViewCommandsWindow":1,
	 * &emsp;			"AllowViewComputerTrackingData":1,
	 * &emsp;			"AllowViewComputerTrackingScreenshot":1,
	 * &emsp;			"AllowViewCustomFields":1,
	 * &emsp;			"AllowViewMobileDeviceTrackingData":1,
	 * &emsp;			"AllowViewSDCenter":1,
	 * &emsp;			"AllowViewSLCenter":1,
	 * &emsp;			"AllowViewServerStatus":1,
	 * &emsp;			"CanChangeServerSettings":1,
	 * &emsp;			"CanDeployAgents":1,
	 * &emsp;			"CanLogin":1,
	 * &emsp;			"CanSeeAllRecords":1,
	 * &emsp;			"CommandPermissions":140728898420686,
	 * &emsp;			"CommandPermissionsHigh32": 32765
	 * &emsp;			"CommandPermissionsLow32": 4294967246
	 * &emsp;			"IsADUser":0,
	 * &emsp;			"IsSuperAdmin":1,
	 * &emsp;			"id":2
	 * &emsp;		},
	 * &emsp;		"ServerBuildNumber":2505,
	 * &emsp;		"ServerVersion":"6.1.5",
	 * &emsp;		"SessionTimeout":1800
	 * &emsp;	}
	 * }
	 * </pre>
	 * 
	 * <p>Each of the <strong>Allow*</strong> properties can be used to determine if a user has been assigned a specific administrator <strong>right</strong>. 
	 *    When the <strong>right</strong> has been assigned to the user, the Allow* property will be set to 1.
	 *    Any endpoint that checks the user rights will return <strong>403 Forbidden</strong> when the required right has not been assigned to the current user.</p>
	 * <p>The <strong>CommandPermissions</strong> property is a bitmap of commands that the user is authorized to issue. 
	 *     This is a 64 bit value, and the bits relevant to this interface are defined as follows:</p>
	 * 
	 * <table>
	 * <tr><td>Bit 35</td><td>/api/commands/configurationprofile</td></tr>
	 * <tr><td>Bit 36</td><td>/api/commands/deleteconfigurationprofile</td></tr>
	 * <tr><td>Bit 39</td><td>/api/commands/lock</td></tr>
	 * <tr><td>Bit 40</td><td>/api/commands/clearpasscode</td></tr>
	 * <tr><td>Bit 41</td><td>/api/commands/remoteerase</td></tr>
	 * <tr><td>Bit 42</td><td>/api/commands/updatedeviceinfo</td></tr>
	 * <tr><td>Bit 43</td><td>/api/commands/sendmessage</td></tr>
	 * <tr><td>Bit 44</td><td>/api/commands/installapplication</td></tr>
	 * <tr><td>Bit 45</td><td>/api/commands/deleteapplication</td></tr>
	 * <tr><td>Bit 46</td><td>/api/commands/setroamingoptions</td></tr>
	 * </table>
	 * 
	 * <p>Bit numbers are in the range 0-63.</p>
	 * <p>Javascript bit operators are limited to 32 bits so the <strong>CommandPermissions</strong> value is also provided as two 32bit values, namely <strong>CommandPermissionsHigh32</strong> and <strong>CommandPermissionsLow32</strong>. 
	 *    Simply subtract 32 from the above bit numbers to find the corresponding bit in <strong>CommandPermissionsHigh32</strong>.</p>
	 * <p>When a bit is set to one, the user is authorized to issue that specific command. Each of the command endpoints will return <strong>403 Forbidden</strong> when a command is not available to a user.</p>
	 * <p>Error status codes:<br/>
	 *    <strong>401 Unauthorized</strong>: the credentials are not valid for the given server and port.<br/>
	 *    <strong>503 Service Unavailable</strong>: a connection cannot be established with the server. This can happen for a variety of reasons, including unknown serverName, incorrect serverPort, network connectivity issues, untrusted certificate, etc.
	 * </p>
	 * @param req
	 * @param serverName Server Name
	 * @param serverPort Server Port
	 * @param userName User Name
	 * @param password User Password
	 * @param locale Current locale
	 * @return LogonResult object. 
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 200, condition = "If access is permitted, then a HTTP 200 status code will be returned with a set-cookie header containing the authentication token."),
		  @ResponseCode ( code = 401, condition = "The the credentials are not valid for the given server and port."),
		  @ResponseCode ( code = 403, condition = "A command is not available to a user. Required right has not been assigned to the current user."),
		  @ResponseCode ( code = 503, condition = "A connection cannot be established with the server. This can happen for a variety of reasons, including unknown serverName, incorrect serverPort, network connectivity issues, untrusted certificate, etc.")
		})
	public LogonResult PostLoginCredentials(
			@Context HttpServletRequest req,
			@DefaultValue("localhost") @FormParam("ServerName") String serverName,
			@FormParam("ServerPort") String serverPort,
			@FormParam("UserName") String userName,
			@FormParam("Password") String password,
			@DefaultValue("en_US") @FormParam("Locale") String locale
			) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception 
	{	    
		LogonRequest loginCredentials = new LogonRequest();
		loginCredentials.setServerName(serverName);
		
		// reset the AM server port number if no server port number passed in
		if (serverPort == null || serverPort.length() == 0)
		{
			loginCredentials.setServerPort(getDefaultServerPort(req, locale));	
		} else {
			try {
				loginCredentials.setServerPort(Short.parseShort(serverPort));
			} catch (Exception ex) {
				//invalid port number
				throw new WebAPIException(Response.Status.SERVICE_UNAVAILABLE, "LOGIN_LOGIN_FAILED", 
						"LOGIN_INVALID_PORT_NUMBER", null, locale, ResourceUtilities.WEBAPI_BASE);
			}
		}
		
		loginCredentials.setUserName(userName);
		loginCredentials.setPassword(password);
		loginCredentials.setLocale(locale);
		
		return PostLoginCredentials(req, loginCredentials);
	}

	
	/**
	 * <p>Implements logout as a DELETE operation.</p>
	 * 
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
	 * Reloads the logback config file, such as C:\Program Files\Apache Software Foundation\Tomcat 8.0\webapps\com.absolute.am.webapi\WEB-INF\classes\logback.xml
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

	
	private PropertyList logonToServer(ServletContext sc, LogonRequest logonRequest) 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, SAXException, AMServerProtocolException {
		
		String locale = logonRequest.getLocale();
		
		CobraAdminMiscDatabaseCommand loginCommand = CommandFactory.createLoginCommand(
				logonRequest.getUserName(),
				logonRequest.getPassword());

		String pathToTrustedCerts = sc.getInitParameter(Application.INI_PATH_TO_TRUSTED_CERTS);
		if (null == pathToTrustedCerts ) {
			String exMessage = ResourceUtilities.getLocalizedFormattedString("SETTING_IS_REQUIRED", new String[]{Application.INI_PATH_TO_TRUSTED_CERTS}, locale, ResourceUtilities.WEBAPI_BASE);
			throw new IllegalArgumentException(exMessage);
		}
	
		// Make sure it ends in /
		if (!pathToTrustedCerts.endsWith(File.separator)) {
			pathToTrustedCerts = pathToTrustedCerts + File.separator; 
		}
		
		m_protocolSettings = new AMServerProtocolSettings(
				logonRequest.getServerName(), 
				logonRequest.getServerPort(),
				pathToTrustedCerts
				);
		AMServerProtocol amServerProtocol = new AMServerProtocol(m_protocolSettings);
	
		PropertyList loginResult = null;
		try {
			loginResult = amServerProtocol.sendCommandAndGetResponse(loginCommand);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			if (loginResult != null) {	// the connection was successful
				// Save a copy of the server certificate to be passed on to the sync service.
				try {
					m_serverCertificate = amServerProtocol.getCertificateProvidedByPeer().getEncoded();
					m_serverUniqueId = amServerProtocol.getPeerServerUniqueId();
				} catch (AMWebAPILocalizableException e) {
					//localize and re-throw
					AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
					throw ex;
				}
			}
			amServerProtocol.close();
		}

		return loginResult;

	}
	
	private Map<String, Object> cleanLogonResultParameters(Map<String, Object> logonResultParameters) {
		
		// Remove any unwanted private stuff.
		Map<String, Object> retVal = new LinkedHashMap<String, Object>();
		String[] keysToRemove = new String[] {
				"AdminIPv4",
				"AdminInfo/Name",
				"AdminInfo/Password",
				"AdminInfo/last_modified",
				"RegistationInfo",	// Intentional misspelling! it has to match the message content.
				"ServerHasMDMFleetManagementKey",
				"ServerSettings"				
		};
		for (int i=0; i<keysToRemove.length; i++) {
			PropertyList.removeElement(logonResultParameters, keysToRemove[i]);
		}
		
		retVal.putAll(logonResultParameters);
				 
		return retVal;
	}
	
	/**
	 * Helper method to start the sync service session token.
	 * @param notifyUrl The URL that the SyncService can POST notifications to.
	 * @return
	 */
	private Object startSyncServiceSession(String notifyUrl) {

		m_logger.debug("Starting sync service session. notifyUrl={}", notifyUrl);
		
		// This is a blocking call. It does not return until the sync service has established contact with the 
		// AM server, and has created the initial database.
		Object syncServiceSessionToken = Application.getSyncService().startSync(
				m_protocolSettings.getServerHostname(), 
				m_protocolSettings.getServerPort(),  
				m_serverCertificate,
				m_serverUniqueId,
				notifyUrl
				);
		m_logger.debug("Sync service session token={}", syncServiceSessionToken);
		
		return syncServiceSessionToken;
	}
	
	private short getDefaultServerPort(HttpServletRequest req, String locale) throws UnsupportedEncodingException
	{
		String defaultServerPortString = req.getServletContext().getInitParameter(INI_DEFAULT_SERVER_PORT);
		if (null == defaultServerPortString || defaultServerPortString.length()==0) {
			throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_NOT_FOUND", new Object[]{INI_DEFAULT_SERVER_PORT}, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		short defaultServerPort = Short.parseShort(defaultServerPortString);
		if (defaultServerPort <= 0) {
			throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_MUST_BE_GREATER_THAN_ZERO", new Object[]{INI_DEFAULT_SERVER_PORT}, locale, ResourceUtilities.WEBAPI_BASE);
		}
		
		return defaultServerPort;
	}
}
