package com.absolute.am.webapi.ssp.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraUserCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.model.ssp.command.Lock;
import com.absolute.am.model.ssp.command.RemoteErase;
import com.absolute.am.model.ssp.command.SendMessage;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * @author ephilippov
 */
@Path ("/ssp/commands")
public class Commands {
	
	private @Context HttpServletRequest m_servletRequest;
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * Submits a send message command for a list of devices. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @param deviceType enumeration for the device type
	 * @param deviceIdentifier identifier ( mobile only )
	 * @param agentSerial identifier ( desktop only )
	 * @param message message to send to the device
	 * @param withCancel whether to display the cancel button or not ( desktop only )
	 * @param timeout message timeout ( desktop only )
	 */
	@POST @Path("/sendmessage")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postSendMessageCommand(
			@Context HttpServletRequest req,
			SendMessage command) throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, SAXException, AMServerProtocolException {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String headerText = ResourceUtilities.getResourceStringForLocale("USER_SEND_COMMAND_HEADER_TEXT", m_Base, locale),
				timeoutCounterText = ResourceUtilities.getResourceStringForLocale("USER_SEND_COMMAND_TIMEOUT_COUNTER_TEXT", m_Base, locale);
		
		if (command.getMessage() == null || (command.getDeviceIdentifier() == null && command.getAgentSerial() == null)) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		sendCommand(CommandFactory.createSSPSendMessageCommand(command.getMessage(), command.getWithCancel(), command.getTimeout(),
				command.getDeviceIdentifier(), command.getAgentSerial(), command.getDeviceType(),
				session.getAttribute(Login.SESSION_TOKEN_PARAM).toString(), headerText, timeoutCounterText), session, locale);
	}
	
    /**
	 * Submits a lock command for a user device. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @param deviceType enumeration for the device type
	 * @param deviceIdentifier identifier ( mobile only )
	 * @param agentSerial identifier ( desktop only )
	 * @param passcode to be entered to unlock the device ( android and desktop only, must be exactly 6 characters for OS X )
	 * @param message ( iOS only )
	 * @param phoneNumber ( iOS only )
	 */
	@POST @Path("/lock")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postLockCommand(
			@Context HttpServletRequest req,
			Lock command) throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if ((command.getDeviceIdentifier() == null && command.getAgentSerial() == null) || command.getDeviceType() == null) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		sendCommand(CommandFactory.createSSPLockDevicesCommand(command.getPasscode(), command.getMessage(), command.getPhoneNumber(), 
				command.getDeviceIdentifier(), command.getAgentSerial(), command.getDeviceType(), 
				session.getAttribute(Login.SESSION_TOKEN_PARAM).toString()), session, locale);
	}
	
	/**
	 * Submits a remote erase command for a user device. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @param deviceType enumeration for the device type
	 * @param deviceIdentifier identifier ( mobile only )
	 * @param agentSerial identifier ( desktop only )
	 * @param passcode to be entered to unlock the device ( android and desktop only, must be exactly 6 characters for OS X )
	 * @param includeSSDCard erase the data on the ssd card too ( mobile only )
	 */
	@POST @Path("/remoteerase")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendRemoteEraseCommand(
			@Context HttpServletRequest req,
			RemoteErase command) throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if ((command.getDeviceIdentifier() == null && command.getAgentSerial() == null) || command.getDeviceType() == null) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		sendCommand(CommandFactory.createSSPRemoteEraseCommand(command.getIncludeSDCard(), command.getPasscode(), command.getDeviceIdentifier(), command.getAgentSerial(),
				command.getDeviceType(), session.getAttribute(Login.SESSION_TOKEN_PARAM).toString()), session, locale);
	}
	
	/**
	 * Submits a clear pass code command for a list of devices. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @param deviceType enumeration for the device type
	 * @param deviceIdentifier identifier ( mobile only )
	 * @param passcode to be entered to unlock the device ( android only )
	 */
	@POST @Path("/clearpasscode")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendClearPasscodeCommand(				
					@Context HttpServletRequest req,
					Lock command) throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
				
		if ((command.getDeviceIdentifier() == null && command.getAgentSerial() == null) || command.getDeviceType() == null) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		sendCommand(CommandFactory.createSSPClearPasscodeCommand(command.getPasscode(), command.getDeviceIdentifier(),
				command.getDeviceType(), session.getAttribute(Login.SESSION_TOKEN_PARAM).toString()), session, locale);
	}
	
	private void sendCommand(CobraUserCommand command, HttpSession session, String locale) throws UnsupportedEncodingException, RuntimeException, IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
	
		try {
			amServerProtocol.sendCommandAndValidateResponse(command, ResourceUtilities.getResourceStringForLocale("COMMANDS_REMOTE_ERASE_FAILED", m_Base, locale));
		} catch (AMWebAPILocalizableException e) {
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}
}
