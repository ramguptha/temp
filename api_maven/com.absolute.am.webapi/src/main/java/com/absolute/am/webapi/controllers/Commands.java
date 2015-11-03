/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.webapi.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.enunciate.Facet;
import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.IApplicationsHandler;
import com.absolute.am.dal.IConfigurationProfileHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IDeviceHandler;
import com.absolute.am.dal.IProvisioningProfileHandler;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.dal.model.IPhoneInstalledConfigurationProfile;
import com.absolute.am.dal.model.IPhoneInstalledProvisioningProfile;
import com.absolute.am.dal.model.IPhoneInstalledSoftwareInfo;
import com.absolute.am.dal.model.ProvisioningProfile;
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.am.dal.model.ConfigurationProfile;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.CommandPermission;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.CommandPermission.AMCommand;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.model.command.DeleteApplicationsOptions;
import com.absolute.am.model.command.DeleteCommands;
import com.absolute.am.model.command.DevicesForSetActivationLockOptions;
import com.absolute.am.model.command.ExecuteActionsOnDevices;
import com.absolute.am.model.command.ExecuteActionsOnPolicies;
import com.absolute.am.model.command.GenericCommand;
import com.absolute.am.model.command.RemoveActionsFromDevices;
import com.absolute.am.model.command.RemoveConfigurationProfilesOptions;
import com.absolute.am.model.command.RemoveProvisioningProfilesOptions;
import com.absolute.am.model.command.DeviceList;
import com.absolute.am.model.command.ClearPasscode;
import com.absolute.am.model.command.DeviceName;
import com.absolute.am.model.command.DeviceOwnership;
import com.absolute.am.model.command.DevicesToLock;
import com.absolute.am.model.command.EnrollmentUser;
import com.absolute.am.model.command.InstallApplicationsOptions;
import com.absolute.am.model.command.InstallConfigurationProfilesOptions;
import com.absolute.am.model.command.InstallProvisioningProfilesOptions;
import com.absolute.am.model.command.OrganizationInfo;
import com.absolute.am.model.command.RemoteErase;
import com.absolute.am.model.command.RoamingOptions;
import com.absolute.am.model.command.SendMessage;
import com.absolute.am.model.command.MobileDevicePerformedAction;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;
import com.absolute.util.StringUtilities;

/**
 * <h3>Commands API</h3>
 * <p>
 * Unless otherwise stated, the following APIs may return these status codes:
 * </p>
 * <p>
 * <strong>204 No Content</strong>: The request has been accepted and will be
 * delivered to the device as soon as possible.
 * </p>
 * <p>
 * <strong>400 Bad Request</strong>: One or more devices were not found.
 * </p>
 * 
 * @author dlavin
 * 
 */
@Path("/commands")
public class Commands {
	@Context
	ServletContext sc;

	private static Logger m_logger = LoggerFactory.getLogger(Commands.class.getName());

	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String VIEW_NAME_ALL_COMMANDS_HISTORY = "allcommandshistory";
	private static final String VIEW_NAME_COMMANDS_HISTORY_BY_DEVICES = "commandshistorybydevices";
	private static final String VIEW_NAME_ALL_QUEUED_COMMANDS = "allqueuedcommands";
	private static final String VIEW_NAME_QUEUED_COMMANDS_BY_DEVICES = "queuedcommandsbydevices";

	private final String SQL_SELECT_DEVICE_IDS_FOR_ADMIN = "SELECT iphone_info_record_id AS id FROM admin_iphone_info WHERE AdminUUID = '%1$s'";

	private static final ViewDescription[] m_viewCommandsHistoryDescriptions = new ViewDescription[] { new ViewDescription(
			ViewHelper.VH_VIEW_NAME_ALL, "All Commands History", 0) };

	private static final ViewDescription[] m_viewQueuedCommandsDescriptions = new ViewDescription[] { new ViewDescription(
			ViewHelper.VH_VIEW_NAME_ALL, "All Queued Commands", 0) };

	/**
	 * The servlet request. This is injected by JAX-RS when the object is
	 * created.
	 */
	private @Context HttpServletRequest m_servletRequest;

	/**
	 * <p>
	 * Delete history commands.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowRemoveiOSHistoryCommands
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/history/delete")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowRemoveiOSHistoryCommands)
	@StatusCodes({ @ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void deleteHistoryCommands(DeleteCommands historyCommand) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (historyCommand == null || historyCommand.getCommandIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_DELETE_COMMANDS", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteHistoryCommandsCommand(SessionState.getAdminUUID(session),
				historyCommand.getCommandIds());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_DELETE_HIST_COMMAND_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}

	/**
	 * <p>
	 * Delete queued commands.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/queued/delete")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({ @ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void deleteQueuedCommands(DeleteCommands queuedCommand) throws Exception {
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (queuedCommand == null || queuedCommand.getCommandIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_DELETE_COMMANDS", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteQueuedCommandsCommand(SessionState.getAdminUUID(session),
				queuedCommand.getCommandIds());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_DELETE_QUEUED_COMMAND_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}

	/**
	 * <p>
	 * Get a list of named views available for commands history. Please refer to
	 * /api/mobiledevices/views – GET for an example of the response to this
	 * request.
	 * </p>
	 * <p>
	 * Currently there is only one view and it is called "All".
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @return Returns list of all available views for the Commands History
	 *         end-point.
	 * @throws Exception
	 */
	@GET
	@Path("/history/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForCommandsHistory() throws Exception {
		m_logger.debug("Commands.getViewsForCommandsHistory called");

		ViewDescriptionList result = new ViewDescriptionList();
		result.setViewDescriptions(m_viewCommandsHistoryDescriptions);

		return result;
	}

	/**
	 * <p>
	 * Get a list of named views available for queued commands. Please refer to
	 * /api/mobiledevices/views – GET for an example of the response to this
	 * request.
	 * </p>
	 * <p>
	 * Currently there is only one view and it is called "All".
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @return Returns list of named views available for queued commands.
	 * @throws Exception
	 */
	@GET
	@Path("/queued/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes({
		@ResponseCode(code = 404, condition = "The <strong>viewName</strong> is not found.") 
	})
	public ViewDescriptionList getViewsForQueuedCommands() throws Exception {
		m_logger.debug("Commands.getViewsForQueuedCommands called");

		ViewDescriptionList result = new ViewDescriptionList();
		result.setViewDescriptions(m_viewQueuedCommandsDescriptions);

		return result;
	}

	/**
	 * <p>
	 * Get the output of a named view. The result is a multi-row result set that
	 * should include the same columns as in the Admin Console. For the "All"
	 * view columns are: Command, Device Name, Device Model, DeviceId [note:
	 * this attribute is not displayed in the Admin Console], Mobile Device OS
	 * Version, Time Issued, Finish Time, Command Error, Command Error Info.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param viewname
	 *            The given <strong>viewname</strong>
	 * @return Returns the output of a named view for the Commands History
	 *         end-point.
	 * @throws Exception
	 */
	@GET
	@Path("/history/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes({
		@ResponseCode(code = 404, condition = "The <strong>viewName</strong> is not found.") 
	})
	public Result getHistoryView(@PathParam("viewname") String viewname, @Context UriInfo ui) throws Exception {

		MDC.put("viewname", viewname);
		m_logger.debug("Commands.getHistoryView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		if (!ViewUtilities.isValidViewName(viewname, m_viewCommandsHistoryDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}

		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_COMMANDS_HISTORY;
		}

		Result result = null;
		IDal dal = Application.getDal(session);

		String adminUUID = session.getAttribute("adminUUID").toString();
		dal.getAdminAccessHandler().refreshAccessForAdmin(adminUUID);

		Map<String, Object> logonResult = SessionState.getLogonResultParameters(session);
		@SuppressWarnings("unchecked")
		Map<String, Object> adminInfo = (Map<String, Object>) logonResult.get("AdminInfo");
		Long canSeeAllRecords = (Long) adminInfo.get(Login.kCobra_Admin_CanSeeAllRecords_Param);

		ArrayList<String> viewParams = new ArrayList<String>();

		if (canSeeAllRecords != 1) {
			// use view with device id filter
			if (viewname.compareToIgnoreCase(VIEW_NAME_ALL_COMMANDS_HISTORY) == 0) {
				viewname = VIEW_NAME_COMMANDS_HISTORY_BY_DEVICES;
			}

			String selectStatement = String.format(SQL_SELECT_DEVICE_IDS_FOR_ADMIN, adminUUID);
			viewParams.add(selectStatement);
		}

		result = ViewHelper.getViewDetails(dal, viewname, ViewHelper.getQueryParameters(ui, session), viewParams, dbLocaleSuffix);

		MDC.remove("viewname");

		return result;
	}

	/**
	 * <p>
	 * Get the output of a named view. The result is a multi-row result set that
	 * should include the same columns as in the Admin Console. For the "All"
	 * view columns are: Command, Device Name, Device Model, DeviceId [note:
	 * this attribute is not displayed in the Admin Console], OS Version,
	 * Status, Time Issued.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param viewname
	 *            The given <strong>viewname</strong>
	 * @return Returns the output of a named view for the Queued Commands
	 *         end-point.
	 * @throws Exception
	 */
	@GET
	@Path("/queued/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getQueuedView(@PathParam("viewname") String viewname, @Context UriInfo ui) throws Exception {

		MDC.put("viewname", viewname);
		m_logger.debug("Commands.getQueuedView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		if (!ViewUtilities.isValidViewName(viewname, m_viewQueuedCommandsDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}

		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_QUEUED_COMMANDS;
		}

		Result result = null;

		IDal dal = Application.getDal(session);

		String adminUUID = session.getAttribute("adminUUID").toString();
		dal.getAdminAccessHandler().refreshAccessForAdmin(adminUUID);

		Map<String, Object> logonResult = SessionState.getLogonResultParameters(session);
		@SuppressWarnings("unchecked")
		Map<String, Object> adminInfo = (Map<String, Object>) logonResult.get("AdminInfo");
		Long canSeeAllRecords = (Long) adminInfo.get(Login.kCobra_Admin_CanSeeAllRecords_Param);

		ArrayList<String> viewParams = new ArrayList<String>();

		if (canSeeAllRecords != 1) {
			// use view with device id filter
			if (viewname.compareToIgnoreCase(VIEW_NAME_ALL_QUEUED_COMMANDS) == 0) {
				viewname = VIEW_NAME_QUEUED_COMMANDS_BY_DEVICES;
			}

			String selectStatement = String.format(SQL_SELECT_DEVICE_IDS_FOR_ADMIN, adminUUID);
			viewParams.add(selectStatement);
		}

		result = ViewHelper.getViewDetails(dal, viewname, ViewHelper.getQueryParameters(ui, session), viewParams, dbLocaleSuffix);
		
		MDC.remove("viewname");

		return result;
	}

	/**
	 * <p>
	 * Submits a set activation lock options command for a list of devices.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:</ br> Bit 39.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param devices
	 *            DevicesForSetActivationLockOptions
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 * @throws AMWebAPILocalizedException
	 */
	@POST
	@Path("/setactivationlockoptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_DeviceLock)
	public void postSetActivationLockOptinsCommand(@Context HttpServletRequest req, DevicesForSetActivationLockOptions devices)
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		MDC.put("request", devices.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (devices == null || devices.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_SET_ACTIVATION_LOCK_OPTIONS", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetActivationLockOptionsCommand(SessionState.getAdminUUID(session),
				devices.getDeviceIds(), devices.getActivationLock());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_SET_ACTIVATION_LOCK_OPTION_FAILED", m_Base,
					locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send a lock command to one or more mobile devices.
	 * </p>
	 * 
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[321,8002,45]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * If a device lock is being sent to a device or a collection of devices,
	 * one of which does not have a passcode already set up on it, the new
	 * passcode is sent to all devices listed in deviceIds[]. The new passcode
	 * will only be applied to devices that do not have a passcode. Devices
	 * with passcodes will not have their passcode updated as a result of
	 * issuing this command. The request will look like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2,3,4],
	 *  &emsp;"passcode":"1234"
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:</ br> Bit 39.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param devicesToLock
	 *            DevicesToLock
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@StatusCodes({ @ResponseCode(code = 400, condition = "deviceIds is empty."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	@POST
	@Path("/lock")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_DeviceLock)
	public void postLockCommand(@Context HttpServletRequest req, DevicesToLock devicesToLock) throws IOException, GeneralSecurityException,
			ParserConfigurationException, SAXException, AMServerProtocolException {

		MDC.put("request", devicesToLock.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (devicesToLock == null || devicesToLock.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_LOCK", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createLockDevicesCommand(SessionState.getAdminUUID(session),
				devicesToLock.getDeviceIds(), devicesToLock.getPasscode());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_LOCK_DEVICE_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send a message to one or more mobile devices.
	 * </p>
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[321,8002,45],
	 *  &emsp;"message":"Greetings from AM."
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 43.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param sendMessage
	 *            SendMessage
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/sendmessage")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_SendMessageToDevice)
	@StatusCodes({ @ResponseCode(code = 400, condition = "deviceIds is empty or the message is empty."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void postSendMessageCommand(@Context HttpServletRequest req, SendMessage sendMessage) throws IOException,
			GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		// TODO: replace with annotated validation
		MDC.put("request", sendMessage.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (sendMessage == null || sendMessage.getDeviceIds() == null || sendMessage.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_SEND_MESSAGE", null, locale, m_Base);
		}

		// TODO: replace with annotated validation
		if (sendMessage.getMessage() == null || sendMessage.getMessage().length() <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_MESSAGE", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSendMessageCommand(SessionState.getAdminUUID(session),
				sendMessage.getMessage(), sendMessage.getDeviceIds());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));

		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_SEND_MESSAGE_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send a Clear Passcode command to one or more devices. In the case of
	 * Android devices, a new passcode may be specified, or the existing
	 * passcode may be removed by specifying an empty string. This option is
	 * ignored for iOS devices, but should still be provided (set it to an empty
	 * string).
	 * </p>
	 * 
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"iOsIds":[321,8002,45],
	 *  &emsp;"androidIds":[4,92],
	 *  &emsp;"passcode":"1234"
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:</ br> Bit 40.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param clearPasscode
	 *            ClearPasscode
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/clearpasscode")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_ClearPasscode)
	@StatusCodes({ @ResponseCode(code = 400, condition = "When there are no iOsIds or no androidIds specified."),
			@ResponseCode(code = 400, condition = "When at least one android id is provided but passcode is null."),
			@ResponseCode(code = 400, condition = "When there are only iOS ids provided and a passcode is provided."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendClearPasscodeCommand(@Context HttpServletRequest req, ClearPasscode clearPasscode) throws IOException,
			GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		MDC.put("request", clearPasscode.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// Algorithm:
		// For iOS devices: You can only clear the passcode. You can not set it.
		// For android devices: You can clear and set the passcode.
		// RecordIDList_Param: contains the android and iOs device Id's
		// If there are android devices, these alone go into
		// AndroidRecordIDList_Param
		// Clearing iOS passcode: NewLockPassword_Param is ommitted from the
		// message
		// Clearing android passcode: NewLockPassword_Param is present and set
		// to ""
		if (!clearPasscode.hasDevices()) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}
		if (clearPasscode.hasAndroidDevices() && clearPasscode.getPasscode() == null) {
			throw new BadRequestException("COMMANDS_PASSCODE_REQUIRED_FOR_ANDROID", null, locale, m_Base);
		}
		if (!clearPasscode.hasAndroidDevices() && clearPasscode.hasPasscode()) {
			throw new BadRequestException("COMMANDS_CANNOT_SET_PASSCODE_FOR_IOS", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createClearPasscodeCommand(SessionState.getAdminUUID(session),
				clearPasscode.getAndroidIds(), clearPasscode.getiOsIds(), clearPasscode.getPasscode());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_CLEAR_PASSCODE_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send a Remote Erase command to one or more devices. In the case of
	 * Android devices, the includeSDCard option is used to request erasure of
	 * the SD card. This option is ignored for iOS devices but should still be
	 * provided (set it to false).
	 * </p>
	 * 
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"iOsIds":[321,8002,45],
	 *  &emsp;"androidIds":[4,92],
	 *  &emsp;"includeSDCard":false
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 41.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param remoteErase
	 *            RemoteErase
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/remoteerase")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_RemoteErase)
	@StatusCodes({ @ResponseCode(code = 400, condition = "When there are no iOsIds or no androidIds specified."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendRemoteEraseCommand(@Context HttpServletRequest req, RemoteErase remoteErase) throws IOException,
			GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		MDC.put("request", remoteErase.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (remoteErase == null || !remoteErase.hasDevices()) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_FROM_REMOTEERASE_COMMAND", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoteEraseCommand(SessionState.getAdminUUID(session),
				remoteErase.getAndroidIds(), remoteErase.getiOsIds(), remoteErase.getIncludeSDCard());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_REMOTE_ERASE_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send an Update Device Info command to one or more mobile devices. The
	 * commands are queued on the server and will be delivered when the devices
	 * come online.
	 * </p>
	 * 
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[321,8002,45]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 42.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param deviceList
	 *            DeviceList
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/updatedeviceinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_UpdateDeviceInfo)
	@StatusCodes({ @ResponseCode(code = 400, condition = "When there are no deviceIds specified."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendUpdateDeviceInfoCommand(@Context HttpServletRequest req, DeviceList deviceList) throws IOException,
			GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {

		MDC.put("request", deviceList.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (deviceList == null || deviceList.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_FROM_UPDATEDEVICEINFO_COMMAND", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createUpdateDeviceInfoCommand(SessionState.getAdminUUID(session),
				deviceList.getDeviceIds());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));

		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_UPDATE_DEVICE_INFO_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Send a Change Roaming Options command to one or more mobile devices. The
	 * commands are queued on the server and will be delivered when the devices
	 * come online. The “voice” and “data” parameters can have the values
	 * “true”, “false”, or null.
	 * </p>
	 * 
	 * <p>
	 * The body of the request looks like this:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[321,8002,45],
	 *  &emsp;"voice":false
	 *  &emsp;"data":false
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 46.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param roamingOptions
	 *            RoamingOptions
	 * @throws Exception
	 */
	@POST
	@Path("/setroamingoptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_ChangeRoamingOptions)
	@StatusCodes({ @ResponseCode(code = 400, condition = "When there are no deviceIds specified or both voice and data are null."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendSetRoamingOptionsCommand(@Context HttpServletRequest req, RoamingOptions roamingOptions) throws Exception {

		MDC.put("request", roamingOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (roamingOptions == null || roamingOptions.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_IN_SETROAMINGOPTIONS", null, locale, m_Base);
		}
		if (roamingOptions.getData() == null && roamingOptions.getVoice() == null) {
			throw new BadRequestException("COMMANDS_ROAMING_OPTIONS_ARE_NULL", null, locale, m_Base);
		}

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetRoamingOptionsCommand(SessionState.getAdminUUID(session),
				roamingOptions.getDeviceIds(), roamingOptions.getVoice(), roamingOptions.getData());
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_SET_ROAMING_OPTIONS", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * This request is used to install an application or a list of applications
	 * on one or more devices.
	 * </p>
	 * 
	 * <p>
	 * NOTE: iOS applications can only be assigned to iOS devices and Android
	 * applications can only be assigned to Android devices. The devices ids
	 * must all refer to the same type of device and the applications must all
	 * be of the appropriate type for those devices.
	 * </p>
	 * 
	 * <p>
	 * Example request body: assign many applications to multiple devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,3,5],
	 *  &emsp;"inHouseAppIds":[2,4,6],
	 *  &emsp;"thirdPartyAppIds":[2,5,7]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 44.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param installAppsOptions
	 *            InstallApplicationsOptions
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/installapplication")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_InstallApplication)
	@StatusCodes({
			@ResponseCode(code = 403, condition = "If any of the devices or applications do not exist, or if the devices and applications are not all of the type type, e.g. iOS or Android."),
			@ResponseCode(code = 404, condition = "The user is not authorized to access this endpoint.") })
	public void sendInstallApplication(@Context HttpServletRequest req, InstallApplicationsOptions installAppsOptions) throws Exception {
		MDC.put("request", installAppsOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (installAppsOptions == null || installAppsOptions.getDeviceIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MISSING_PARAMETERS_IN_INSTALLAPPLICATION", null, locale, m_Base);
		}
		if (installAppsOptions.getDeviceIds() == null || installAppsOptions.getDeviceIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_IN_INSTALLAPPLICATION", null, locale, m_Base);
		}
		if ((installAppsOptions.getInHouseAppIds() == null || installAppsOptions.getInHouseAppIds().length == 0)
				&& (installAppsOptions.getThirdPartyAppIds() == null || installAppsOptions.getThirdPartyAppIds().length == 0)) {
			throw new BadRequestException("COMMANDS_APPIDS_ARE_EMPTY_IN_INSTALLAPPLICATION", null, locale, m_Base);
		}
		// Make sure all device ID's, inHouseAppIds and thirdPartyAppIds are of
		// the same type, either iOS or Android
		int[] deviceIds = installAppsOptions.getDeviceIds();

		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		String contextMessage;
		int PLATFORM_ANDROID = Integer.parseInt(Application.getRuntimeProperties().get(Application.AGENT_PLATFORM_ANDROID).toString());

		try {
			IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceIds[0]);
			int expectedPlatformType = deviceInfo.getPlatformType();
			IDeviceHandler dh = dal.getDeviceHandler();
			for (int i = 1; i < deviceIds.length; i++) {
				int devicePlatformType = dh.getDevice(deviceIds[i]).getPlatformType();
				if (devicePlatformType != expectedPlatformType) {
					throw new BadRequestException("COMMANDS_DEVICE_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLAPPLICATION", new Object[] {
							deviceIds[i], devicePlatformType, expectedPlatformType }, locale, m_Base);
				}
			}

			int[] inHouseAppIds = installAppsOptions.getInHouseAppIds();
			iOSApplications[] inHouseAppsDetails = new iOSApplications[inHouseAppIds.length];
			IApplicationsHandler ah = dal.getApplicationsHandler();
			for (int i = 0; i < inHouseAppIds.length; i++) {
				inHouseAppsDetails[i] = ah.getInHouseApplication(inHouseAppIds[i]);
				if (inHouseAppsDetails[i] == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_INHOUSE_APP_FOR_INSTALLAPPLICATION",
							new Object[] { inHouseAppIds[i] }, locale, m_Base);
				}
				int appPlatformType = inHouseAppsDetails[i].getPlatformType();
				if (appPlatformType != expectedPlatformType) {
					throw new BadRequestException("COMMANDS_INHOUSE_APP_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLAPPLICATION",
							new Object[] { inHouseAppIds[i], appPlatformType, expectedPlatformType }, locale, m_Base);
				}
			}

			int[] thirdPartyAppIds = installAppsOptions.getThirdPartyAppIds();
			iOSAppStoreApplications[] thirdPartyAppsDetails = new iOSAppStoreApplications[thirdPartyAppIds.length];
			for (int i = 0; i < thirdPartyAppIds.length; i++) {
				thirdPartyAppsDetails[i] = ah.getThirdPartyApplication(thirdPartyAppIds[i]);
				if (thirdPartyAppsDetails[i] == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_THIRDPARTY_APP_FOR_INSTALLAPPLICATION",
							new Object[] { thirdPartyAppIds[i] }, locale, m_Base);
				}

				int appPlatformType = thirdPartyAppsDetails[i].getPlatformType();

				if (appPlatformType != expectedPlatformType) {
					throw new BadRequestException("COMMANDS_THIRDPARTY_APP_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLAPPLICATION",
							new Object[] { thirdPartyAppIds[i], appPlatformType, expectedPlatformType }, locale, m_Base);
				}
			}

			// Install each in-house app on multiple devices
			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));

			boolean isAndroid = (expectedPlatformType == PLATFORM_ANDROID);
			contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_INSTALL_INHOUSE_APP", m_Base, locale);
			for (int i = 0; i < inHouseAppIds.length; i++) {
				CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallInHouseApplicationCommand(
						SessionState.getAdminUUID(session), deviceIds, isAndroid, inHouseAppsDetails[i]);
				MDC.put("command", command.toXml());
				amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
			}

			// Install each third party app on multiple devices
			contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_INSTALL_THIRDPARTY_APP", m_Base, locale);
			for (int i = 0; i < thirdPartyAppIds.length; i++) {
				CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallThirdPartyApplicationCommand(
						SessionState.getAdminUUID(session), deviceIds, thirdPartyAppsDetails[i]);
				MDC.put("command", command.toXml());
				amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
			}
			// catch exceptions from AMServerProtocol.send... calls
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Removes one or more applications from a single device. The
	 * applicationId’s are the ones returned from the API
	 * /api/mobiledevices/id/applications.
	 * </p>
	 * 
	 * <p>
	 * Example body:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceId":5
	 *  &emsp;"applicationIds":[2,4,6]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 45.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param deleteAppsOptions
	 *            DeleteApplicationsOptions
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/deleteapplication")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_RemoveApplication)
	@StatusCodes({ @ResponseCode(code = 400, condition = "One more of the devices or applications do not exist."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendRemoveApplication(@Context HttpServletRequest req, DeleteApplicationsOptions deleteAppsOptions) throws Exception {
		MDC.put("request", deleteAppsOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (deleteAppsOptions.getApplicationIds() == null || deleteAppsOptions.getApplicationIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_APPIDS_IN_DELETEAPPLICATION", null, locale, m_Base);
		}

		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;

		try {

			long deviceIdtoDeleteFrom = deleteAppsOptions.getDeviceId();
			long[] applicationIds = deleteAppsOptions.getApplicationIds();
			IDeviceHandler dh = dal.getDeviceHandler();
			for (int i = 0; i < applicationIds.length; i++) {
				IPhoneInstalledSoftwareInfo swDetails = dh.getDetailsForInstalledSoftwareId(applicationIds[i]);
				// if the app is not found, this will be null.
				if (swDetails == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_APP_FOR_DELETEAPPLICATION", new Object[] { applicationIds[i],
							deviceIdtoDeleteFrom }, locale, m_Base);
				}

				if (swDetails == null || (swDetails != null && swDetails.getIphoneInfoRecordId() != deviceIdtoDeleteFrom)) {
					throw new BadRequestException("COMMANDS_APPLICATION_NOT_INSTALLED_ON_DEVICE", new Object[] { applicationIds[i],
							deviceIdtoDeleteFrom }, locale, m_Base);
				}
			}
			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
			CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteApplicationCommand(SessionState.getAdminUUID(session),
					deleteAppsOptions.getDeviceId(), deleteAppsOptions.getApplicationIds());
			MDC.put("command", command.toXml());
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_REMOVE_APP", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");

	}

	/**
	 * <p>
	 * This request is used to install one or more configuration profiles on one
	 * or more devices.
	 * </p>
	 * <p>
	 * NOTE: iOS configuration profiles can only be installed on iOS devices,
	 * Android configuration profiles can only be installed on Android devices,
	 * and Windows Phone configuration profiles can only by installed on Windows
	 * Phone devices. The devices ids must all refer to the same type of device
	 * and the configuration profiles must all be of the appropriate type for
	 * those devices.
	 * </p>
	 * 
	 * <p>
	 * Example request body: install multiple configuration profiles on multiple
	 * devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2,4],
	 *  &emsp;"configurationProfileIds":[2,7,18]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:</ br> Bit 35.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param installConfigProfileOptions
	 *            InstallConfigurationProfilesOptions
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/installconfigurationprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_InstallProfile)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If any of the devices or configuration profiles do not exist, or if the devices and configuration profiles are not all of the same type, e.g. iOS or Android or Windows Phone, or data is missing in the POST."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendInstallConfigurationProfile(@Context HttpServletRequest req,
			InstallConfigurationProfilesOptions installConfigProfileOptions) throws Exception {
		MDC.put("request", installConfigProfileOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (installConfigProfileOptions.getDeviceIds() == null || installConfigProfileOptions.getDeviceIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_IN_INSTALLCONFIGURATIONPROFILE", null, locale, m_Base);
		}
		if ((installConfigProfileOptions.getconfigurationProfileIds() == null || installConfigProfileOptions.getconfigurationProfileIds().length == 0)) {
			throw new BadRequestException("COMMANDS_CONFIGPROFILEIDS_ARE_EMPTY_IN_INSTALLCONFIGURATIONPROFILE", null, locale, m_Base);
		}
		// Make sure all device ID's and configurationProfileIds are of
		// the same type, either iOS or Android
		long[] deviceIds = installConfigProfileOptions.getDeviceIds();

		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		try {
			IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceIds[0]);
			int expectedPlatformType = deviceInfo.getPlatformType();
			IDeviceHandler dh = dal.getDeviceHandler();
			for (int i = 1; i < deviceIds.length; i++) {
				int devicePlatformType = dh.getDevice(deviceIds[i]).getPlatformType();
				if (devicePlatformType != expectedPlatformType) {
					throw new BadRequestException("COMMANDS_DEVICE_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLCONFIGURATIONPROFILE",
							new Object[] { deviceIds[i], devicePlatformType, expectedPlatformType }, locale, m_Base);
				}
			}

			long[] configurationProfileIds = installConfigProfileOptions.getconfigurationProfileIds();
			ConfigurationProfile[] configProfileDetails = new ConfigurationProfile[configurationProfileIds.length];
			IConfigurationProfileHandler cph = dal.getConfigurationProfileHandler();
			for (int i = 0; i < configurationProfileIds.length; i++) {
				configProfileDetails[i] = cph.getConfigurationProfile(configurationProfileIds[i]);
				if (configProfileDetails[i] == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_CONFIGURATION_PROFILE_FOR_INSTALLCONFIGURATIONPROFILE",
							new Object[] { configurationProfileIds[i] }, locale, m_Base);
				}
				int configProfilePlatformType = configProfileDetails[i].getPlatformType();
				if (configProfilePlatformType != expectedPlatformType) {
					throw new BadRequestException(
							"COMMANDS_CONFIGURATION_PROFILE_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLCONFIGURATIONPROFILE", new Object[] {
									configurationProfileIds[i], configProfilePlatformType, expectedPlatformType }, locale, m_Base);
				}
			}

			// Install each configuration profile on multiple devices
			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));

			for (int i = 0; i < configurationProfileIds.length; i++) {
				CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallConfigurationProfileCommand(
						SessionState.getAdminUUID(session), deviceIds, configProfileDetails[i]);
				MDC.put("command", command.toXml());
				String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_INSTALL_CONFIGURATION_PROFILE_FAILED",
						m_Base, locale);
				amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
			}
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * Removes one or more configuration profiles from a single device. The
	 * configurationProfileIds are the ones returned from the API
	 * /api/mobiledevices/{id}/configurationprofiles.
	 * </p>
	 * 
	 * <p>
	 * Example body:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":4,
	 *  &emsp;"configurationProfileIds":[2,9,6]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 36.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param removeConfigurationProfilesOptions
	 *            RemoveConfigurationProfilesOptions
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/removeconfigurationprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_RemoveProfile)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "One more of the devices or configuration profiles do not exist, or data is missing in the POST."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendRemoveConfigurationProfile(@Context HttpServletRequest req,
			RemoveConfigurationProfilesOptions removeConfigurationProfilesOptions) throws Exception {
		MDC.put("request", removeConfigurationProfilesOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (removeConfigurationProfilesOptions.getInstalledConfigurationProfileIds() == null
				|| removeConfigurationProfilesOptions.getInstalledConfigurationProfileIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_CONFIGPROFILEIDS_IN_DELETECONFIGURATIONPROFILE", null, locale, m_Base);
		}

		if (removeConfigurationProfilesOptions.getDeviceIds() == null || removeConfigurationProfilesOptions.getDeviceIds().length == 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;

		try {
			long[] deviceIdsToRemoveFrom = removeConfigurationProfilesOptions.getDeviceIds();
			long[] configurationProfileIds = removeConfigurationProfilesOptions.getInstalledConfigurationProfileIds();
			IDeviceHandler dh = dal.getDeviceHandler();

			String deviceIdstoRemoveFromStr = String.valueOf(deviceIdsToRemoveFrom[0]);
			for (int j = 0; j < deviceIdsToRemoveFrom.length; j++) {
				deviceIdstoRemoveFromStr += ", " + String.valueOf(deviceIdsToRemoveFrom[j]);
				IPhoneInfo deviceInfo = dh.getDevice(deviceIdsToRemoveFrom[j]);
				if (null == deviceInfo) {
					// invalid device included in the request
					throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
				}
			}

			for (int i = 0; i < configurationProfileIds.length; i++) {
				IPhoneInstalledConfigurationProfile configProfile = dh
						.getDetailsForInstalledConfigurationProfileId(configurationProfileIds[i]);
				// If the config profile is not found, this will be null.
				// This can happen when the config profile was already removed,
				// but the local database hasn't caught up with the changes.
				if (configProfile == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_CONFIGURATION_PROFILE_FOR_DELETECONFIGURATIONPROFILE",
							new Object[] { configurationProfileIds[i], deviceIdstoRemoveFromStr }, locale, m_Base);
				}
			}

			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
			CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveConfigurationProfileFromDeviceCommand(
					SessionState.getAdminUUID(session), removeConfigurationProfilesOptions.getInstalledConfigurationProfileIds());
			MDC.put("command", command.toXml());
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_REMOVE_CONFIGURATION_PROFILE_FAILED", m_Base,
					locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");

	}

	/**
	 * <p>
	 * This request is used to install one or more provisioning profiles on one
	 * or more devices.
	 * </p>
	 * 
	 * <p>
	 * NOTE: provisioning profiles can only be installed on iOS devices. The
	 * devices ids must all refer to iOS devices.
	 * </p>
	 * 
	 * <p>
	 * Example request body: install multiple provisioning profiles on multiple
	 * devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2,4],
	 *  &emsp;"provisioningProfileIds":[2,7,18]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 37
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param installProvisioningProfileOptions
	 *            InstallProvisioningProfilesOptions
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/installprovisioningprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_InstallProvisioningProfile)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If any of the devices or provisioning profiles do not exist, or if any device is not of iOS type, or data is missing in the POST. No data is updated."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendInstallProvisioningProfile(@Context HttpServletRequest req,
			InstallProvisioningProfilesOptions installProvisioningProfileOptions) throws Exception {
		MDC.put("request", installProvisioningProfileOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		// TODO: replace with annotated validation
		if (installProvisioningProfileOptions.getDeviceIds() == null || installProvisioningProfileOptions.getDeviceIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_IDS_IN_INSTALLPROVISIONINGPROFILE", null, locale, m_Base);
		}
		if ((installProvisioningProfileOptions.getProvisioningProfileIds() == null || installProvisioningProfileOptions
				.getProvisioningProfileIds().length == 0)) {
			throw new BadRequestException("COMMANDS_PROVISIONINGPROFILEIDS_ARE_EMPTY_IN_INSTALLPROVISIONINGPROFILE", null, locale, m_Base);
		}
		// Make sure all device ID's are of iOS type
		long[] deviceIds = installProvisioningProfileOptions.getDeviceIds();
		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		int PLATFORM_IOS = Integer.parseInt(Application.getRuntimeProperties().get(Application.AGENT_PLATFORM_IOS).toString());

		try {
			IDeviceHandler dh = dal.getDeviceHandler();
			for (int i = 1; i < deviceIds.length; i++) {
				int devicePlatformType = dh.getDevice(deviceIds[i]).getPlatformType();
				if (devicePlatformType != PLATFORM_IOS) {
					throw new BadRequestException("COMMANDS_DEVICE_IS_NOT_OF_EXPECTED_PLATFORM_TYPE_IN_INSTALLPROVISIONINGPROFILE",
							new Object[] { deviceIds[i], devicePlatformType, PLATFORM_IOS }, locale, m_Base);
				}
			}

			long[] provisioningProfileIds = installProvisioningProfileOptions.getProvisioningProfileIds();
			ProvisioningProfile[] provisioningProfileDetails = new ProvisioningProfile[provisioningProfileIds.length];
			IProvisioningProfileHandler pph = dal.getProvisioningProfileHandler();
			for (int i = 0; i < provisioningProfileIds.length; i++) {
				provisioningProfileDetails[i] = pph.getProvisioningProfile(provisioningProfileIds[i]);
				if (provisioningProfileDetails[i] == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_PROVISIONING_PROFILE_FOR_INSTALLPROVISIONINGPROFILE",
							new Object[] { provisioningProfileIds[i] }, locale, m_Base);
				}
			}

			// Install each provisioning profile on multiple devices
			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));

			for (int i = 0; i < provisioningProfileIds.length; i++) {
				CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallProvisioningProfileCommand(
						SessionState.getAdminUUID(session), deviceIds, provisioningProfileDetails[i]);
				MDC.put("command", command.toXml());
				String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_INSTALL_PROVISIONING_PROFILE_FAILED",
						m_Base, locale);
				amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
			}
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * This request is used to remove one or more provisioning profiles from one
	 * or more devices.
	 * </p>
	 * 
	 * <p>
	 * NOTE: provisioning profiles can only be removed from iOS devices. The
	 * devices ids must all refer to iOS devices.
	 * </p>
	 * 
	 * <p>
	 * Example request body: install multiple provisioning profiles on multiple
	 * devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2,4],
	 *  &emsp;"provisioningProfileIds":[2,7,18]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * Bit 37
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> None
	 * </p>
	 * 
	 * @param removeProvisioningProfilesOptions
	 *            RemoveProvisioningProfilesOptions
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/removeprovisioningprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_RemoveProvisioningProfile)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If any of the devices or provisioning profiles do not exist, or if any device is not of iOS type, or data is missing in the POST. No data is updated."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void sendRemoveProvisioningProfile(@Context HttpServletRequest req,
			RemoveProvisioningProfilesOptions removeProvisioningProfilesOptions) throws Exception {
		MDC.put("request", removeProvisioningProfilesOptions.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (removeProvisioningProfilesOptions.getInstalledProvisioningProfileIds() == null
				|| removeProvisioningProfilesOptions.getInstalledProvisioningProfileIds().length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_PROVISIONINGPROFILEIDS_IN_REMOVEPROVISIONINGPROFILE", null, locale, m_Base);
		}

		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;

		try {
			long[] deviceIdsToRemoveFrom = removeProvisioningProfilesOptions.getDeviceIds();
			long[] provisioningProfileIds = removeProvisioningProfilesOptions.getInstalledProvisioningProfileIds();
			IDeviceHandler dh = dal.getDeviceHandler();

			String deviceIdstoRemoveFromStr = String.valueOf(deviceIdsToRemoveFrom[0]);
			for (int j = 0; j < deviceIdsToRemoveFrom.length; j++) {
				deviceIdstoRemoveFromStr += ", " + String.valueOf(deviceIdsToRemoveFrom[j]);
				IPhoneInfo deviceInfo = dh.getDevice(deviceIdsToRemoveFrom[j]);
				if (null == deviceInfo) {
					// invalid device included in the request
					throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
				}
			}

			for (int i = 0; i < provisioningProfileIds.length; i++) {
				IPhoneInstalledProvisioningProfile provisioningProfile = dh
						.getDetailsForInstalledProvisioningProfileId(provisioningProfileIds[i]);
				// If the provisioning profile is not found, this will be null.
				// This can happen when the provisioning profile was already
				// removed, but the local database hasn't caught up with the
				// changes.
				if (provisioningProfile == null) {
					throw new BadRequestException("COMMANDS_CANNOT_FIND_PROVISIONING_PROFILE_FOR_REMOVEPROVISIONINGPROFILE", new Object[] {
							provisioningProfileIds[i], deviceIdstoRemoveFromStr }, locale, m_Base);
				}
			}

			amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
			CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveProvisioningProfileFromDeviceCommand(
					SessionState.getAdminUUID(session), removeProvisioningProfilesOptions.getInstalledProvisioningProfileIds());
			MDC.put("command", command.toXml());
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_REMOVE_PROVISIONING_PROFILE_FAILED", m_Base,
					locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");
	}

	/**
	 * <p>
	 * This request is used to create <span
	 * style="text-decoration: underline;">or update</span> ownership for mobile
	 * devices.
	 * </p>
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <p>
	 * Example - assign ownership of multiple devices to a Guest user:
	 * </p>
	 * 
	 * <pre>
	 * {
	 * &emsp;"deviceIds":[1,2],
	 * &emsp;"ownershipType":3
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The <strong>ownershipType</strong> value is in the range of 0–3, where
	 * these values mean:<br/>
	 * 0 = Undefined (kCobra_MobileDeviceOwnership_Unknown), <br/>
	 * 1 = Company (kCobra_MobileDeviceOwnership_Company), <br/>
	 * 2 = User (personal device) (kCobra_MobileDeviceOwnership_User), <br/>
	 * 3 = Guest (kCobra_MobileDeviceOwnership_Guest),<br/>
	 * 4 = kCobra_MobileDeviceOwnership_Custom1,<br/>
	 * 5 = kCobra_MobileDeviceOwnership_Custom2,<br/>
	 * 6 = kCobra_MobileDeviceOwnership_Custom3,<br/>
	 * 7 = kCobra_MobileDeviceOwnership_Custom4,<br/>
	 * 8 = kCobra_MobileDeviceOwnership_Custom5,<br/>
	 * 20 = kCobra_MobileDeviceOwnership_Supervised.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:</br> None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @param deviceOwnership
	 *            DeviceOwnership
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/setownership")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({ @ResponseCode(code = 400, condition = "If any of the mobile device Ids or the ownershipType do not exist."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void setDeviceOwnership(@Context HttpServletRequest req, DeviceOwnership deviceOwnership) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (deviceOwnership == null) {
			throw new BadRequestException("COMMANDS_NO_DEVICE_OWNERSHIP_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		MDC.put("deviceOwnership", deviceOwnership.toString());

		int[] deviceIds = deviceOwnership.getDeviceIds();
		if (deviceIds == null || deviceIds.length == 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		int ownershipType = deviceOwnership.getOwnershipType();

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand setDevicesOwnershipCommand = CommandFactory.setDevicesOwnershipCommand(
					SessionState.getAdminUUID(session), deviceIds, ownershipType);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_SET_DEVICES_OWNERSHIP_FAILED", new String[] {},
					locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(setDevicesOwnershipCommand, contextMessage);
			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's
			// tables based on the changes described in the result.
			// commented out because AM Server does not return DBChangeInfo in
			// the response for this command
			Application.getSyncService().prioritySync(SessionState.getSyncServiceSession(session), response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("deviceOwnership");
	}

	/**
	 * <p>
	 * This request is used to update the enrollment user for mobile devices.
	 * </p>
	 * 
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <p>
	 * Example – update enrollment user for multiple devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2],
	 *  &emsp;"username":"myuser",
	 *  &emsp;"domain":"mydomain"
	 * }
	 * </pre>
	 * <p>
	 * Command permissions bit required:<br/>
	 * None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @param enrollmentUser
	 *            EnrollmentUser
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/setenrollmentuser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({
			@ResponseCode(code = 403, condition = "If any of the mobile device Ids do not exist, or username is empty and domain name is not."),
			@ResponseCode(code = 404, condition = "The user is not authorized to access this endpoint.") })
	public void setEnrollmentUser(@Context HttpServletRequest req, EnrollmentUser enrollmentUser) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		MDC.put("enrollmentUser", enrollmentUser.toString());

		int[] deviceIds = enrollmentUser.getDeviceIds();
		if (deviceIds == null || deviceIds.length == 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		String username = enrollmentUser.getUsername();
		String domain = enrollmentUser.getDomain();

		if (((null == username) || (username.isEmpty())) && null != domain && !domain.isEmpty()) {
			throw new BadRequestException("COMMANDS_USERNAME_REQUIRED_WHEN_DOMAIN_SUPPLIED", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand setEnrollmentUserCommand = CommandFactory.setEnrollmentUserCommand(
					SessionState.getAdminUUID(session), deviceIds, username, domain);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_SET_ENROLLMENT_USER_FAILED", new String[] {}, locale,
					m_Base);

			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(setEnrollmentUserCommand, contextMessage);

			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's
			// tables based on the changes described in the result.
			// commented out because AM Server does not return DBChangeInfo in
			// the response for this command
			Application.getSyncService().prioritySync(SessionState.getSyncServiceSession(session), response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("enrollmentUser");
	}

	/**
	 * <p>
	 * This request is used to <span
	 * style="text-decoration: underline;">update</span> the device name for a
	 * mobile device.
	 * </p>
	 * 
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <p>
	 * Example – update device name for a device:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceId":1,
	 *  &emsp;"name":"name"
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The "name" and "deviceId" cannot be empty.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @param deviceName
	 *            DeviceName
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/setdevicename")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({ @ResponseCode(code = 400, condition = "If deviceId or name is missing or the mobile device Id does not exist."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint. ") })
	public void setDeviceName(@Context HttpServletRequest req, DeviceName deviceName) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		MDC.put("deviceName", deviceName.toString());

		int deviceId = 0;

		try {
			deviceId = deviceName.getDeviceId();
			if (deviceId <= 0) {
				throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
			}
		} catch (Exception e) {
			throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
		}

		IDal dal = Application.getDal(session);
		IDeviceHandler dh = dal.getDeviceHandler();
		
		if (dh.getDeviceForName(deviceName.getName()).size() != 0) {
			throw new WebAPIException(Response.Status.CONFLICT, "COMMANDS_DEVICE_NAME_ALREADY_EXISTS", "DUPLICATE_NAME", null, locale, m_Base);
		}

		IPhoneInfo ph = dh.getDevice(deviceId);
		if (null == ph) {
			throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
		}

		String name = deviceName.getName();
		if (null == name || name.isEmpty()) {
			throw new BadRequestException("COMMANDS_DEVICE_NAME_MISSING", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand setDeviceNameCommand = CommandFactory.setDeviceNameCommand(SessionState.getAdminUUID(session),
					deviceId, name);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_SET_DEVICE_NAME_FAILED", new String[] {}, locale,
					m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(setDeviceNameCommand, contextMessage);
			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's
			// tables based on the changes described in the result.
			// commented out because AM Server does not return DBChangeInfo in
			// the response for this command
			Application.getSyncService().prioritySync(SessionState.getSyncServiceSession(session), response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("deviceName");
	}

	/**
	 * <p>
	 * This request is used to <span
	 * style="text-decoration: underline;">update</span> organization
	 * information for multiple iOS 7+ devices.
	 * </p>
	 * 
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <p>
	 * Example – update organization information for multiple devices:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2],
	 *  &emsp;"name":"Absolute",
	 *  &emsp;"phone":"1-604-730-9851",
	 *  &emsp;"email":"info@absolute.com",
	 *  &emsp;"address":"1600-1055 Dunsmuir Street, Vancouver, BC V7X 1K8, Canada",
	 *  &emsp;"custom":"my comments"
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The "deviceIds" cannot be empty, any other field can.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * 59.
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> none
	 * </p>
	 * 
	 * @param organizationInfo
	 *            OrganizationInfo
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/setorganizationinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_Admin_SendMDMCommandPseudo_SetOrganizationInfo)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If deviceIds is missing or any device Id does not exist, or any device Id belongs to a non-iOS7 device, no data is updated."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void setOrganizationInfo(@Context HttpServletRequest req, OrganizationInfo organizationInfo) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		MDC.put("organizationInfo", organizationInfo.toString());

		// this command is valid for iOS7+ devices only
		final int iOSplatform = 10; // iOS
		final int minOSversion = 7;

		int[] deviceIds = organizationInfo.getDeviceIds();
		if (deviceIds == null || deviceIds.length == 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		String name = organizationInfo.getName();
		String phone = organizationInfo.getPhone();
		String email = organizationInfo.getEmail();
		String address = organizationInfo.getAddress();
		String custom = organizationInfo.getCustom();

		IDal dal = Application.getDal(session);
		IDeviceHandler dh = dal.getDeviceHandler();

		for (int i = 0; i < deviceIds.length; i++) {
			IPhoneInfo deviceInfo = dh.getDevice(deviceIds[i]);
			if (null == deviceInfo) {
				// invalid device included in the request
				throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
			}
			int platformType = deviceInfo.getPlatformType();
			int OSversion = deviceInfo.getOSVersion();
			String OSverAsString = StringUtilities.convertIntVersionToString(OSversion);
			int versionCompResult = StringUtilities.compareStringVersionNumbers(OSverAsString, String.valueOf(minOSversion));

			if (platformType != iOSplatform || versionCompResult < 0) {
				// invalid device included in the request
				throw new BadRequestException("COMMANDS_MINIMUM_IOS_VERSION_REQUIRED", new Object[] { String.valueOf(minOSversion) },
						locale, m_Base);
			}
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand setOrganizationInfoCommand = CommandFactory.setOrganizationInfoCommand(
					SessionState.getAdminUUID(session), deviceIds, name, phone, email, address, custom);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_SET_ORGANIZATION_INFO_FAILED", new String[] {},
					locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(setOrganizationInfoCommand, contextMessage);
			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's
			// tables based on the changes described in the result.
			// commented out because AM Server does not return DBChangeInfo in
			// the response for this command
			Application.getSyncService().prioritySync(SessionState.getSyncServiceSession(session), response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("organizationInfo");
	}

	/**
	 * <p>
	 * This request is used to re-attempt installing configuration and
	 * provisioning profiles that failed during an earlier installation. This
	 * request can target multiple devices.
	 * </p>
	 * 
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The "deviceIds" field cannot be empty.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @param deviceList
	 *            DeviceList
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/retryallfailedprofiles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({ @ResponseCode(code = 400, condition = "If deviceIds is missing or the mobile device Id does not exist."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void retryAllFailedProfiles(@Context HttpServletRequest req, DeviceList deviceList) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		MDC.put("request", deviceList.toString());

		int[] deviceIds = deviceList.getDeviceIds();

		if (null == deviceIds || deviceIds.length <= 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		UUID[] deviceUniqueIds = new UUID[deviceIds.length];

		IDal dal = Application.getDal(session);
		IDeviceHandler dh = dal.getDeviceHandler();

		for (int i = 0; i < deviceIds.length; i++) {
			IPhoneInfo deviceInfo = dh.getDevice(deviceIds[i]);
			if (null == deviceInfo) {
				// invalid device included in the request
				throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
			} else {
				deviceUniqueIds[i] = UUID.fromString(deviceInfo.getUniqueId());
			}
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand retryAllFailedProfilesCommand = CommandFactory.retryAllFailedProfilesCommand(
					SessionState.getAdminUUID(session), deviceUniqueIds);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_RETRY_ALL_FAILED_PROFILES_FAILED", new String[] {},
					locale, m_Base);
			amServerProtocol.sendCommandAndValidateResponse(retryAllFailedProfilesCommand, contextMessage);
			amServerProtocol.close();

		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("request");
	}

	/**
	 * <p>
	 * This request is used to re-attempt installing applications that failed
	 * during an earlier installation. This request can target multiple devices.
	 * </p>
	 * 
	 * <p>
	 * Sample request body is shown below.
	 * </p>
	 * 
	 * <pre>
	 * {
	 *  &emsp;"deviceIds":[1,2]
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The "deviceIds" field cannot be empty.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> AllowManageiOSDevices
	 * </p>
	 * 
	 * @param deviceList
	 *            DeviceList
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/retryallfailedapplications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({ @ResponseCode(code = 400, condition = "If deviceIds is missing or the mobile device Id does not exist."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void retryAllFailedApplications(@Context HttpServletRequest req, DeviceList deviceList) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		MDC.put("request", deviceList.toString());

		int[] deviceIds = deviceList.getDeviceIds();

		if (null == deviceIds || deviceIds.length <= 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_MISSING", null, locale, m_Base);
		}

		UUID[] deviceUniqueIds = new UUID[deviceIds.length];

		IDal dal = Application.getDal(session);
		IDeviceHandler dh = dal.getDeviceHandler();

		for (int i = 0; i < deviceIds.length; i++) {
			IPhoneInfo deviceInfo = dh.getDevice(deviceIds[i]);
			if (null == deviceInfo) {
				// invalid device included in the request
				throw new BadRequestException("COMMANDS_INVALID_DEVICE_ID", null, locale, m_Base);
			} else {
				deviceUniqueIds[i] = UUID.fromString(deviceInfo.getUniqueId());
			}
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand retryAllFailedProfilesCommand = CommandFactory.retryAllFailedApplicationsCommand(
					SessionState.getAdminUUID(session), deviceUniqueIds);

			contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMANDS_RETRY_ALL_FAILED_APPLICATIONS_FAILED",
					new String[] {}, locale, m_Base);
			amServerProtocol.sendCommandAndValidateResponse(retryAllFailedProfilesCommand, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("request");
	}

	/**
	 * <p>
	 * This endpoint is used to send any type of command to the AM Server. It
	 * requires the commandId and commandParameters to be sent in to build the
	 * command.
	 * </p>
	 * 
	 * <p>
	 * A sample ( JSON ) request is as follows:
	 * </p>
	 * 
	 * <pre>
	 * { "commandParameters": 
	 *  {
	 *  	"NewData": {
	 *  		"ActionData":
	 *  		{
	 *  			"IsReenrollment": false,
	 *  			"MessageText": "message text",
	 *  			"Timestamp": "2015-03-26T20:55:45Z"
	 *  		},
	 *  			"ActionType": 1,
	 *  			"Description": "action description",
	 *  			"DisplayName": "send message test",
	 *  			"Seed": 1,
	 *  			"SupportedPlatforms": 3,
	 *  			"UniqueID": "8e8b3075-aede-484e-a55f-07441a0323a6"
	 *  		},
	 *  		"OperationType": 43
	 *  	}
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The "commandId" field or "commandParameters" fields cannot be empty.
	 * </p>
	 * 
	 * <p>
	 * Command permissions bit required:<br/>
	 * None
	 * </p>
	 * 
	 * <p>
	 * Rights required:</br> IsSuperAdmin, CanLogin
	 * </p>
	 * 
	 * @param commandId
	 *            the id of the command
	 * @param command
	 *            GenericCommand
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/generic/{commandId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right({ AMRight.IsSuperAdmin, AMRight.CanLogin })
	@Facet(name = "exclude")
	public void genericCommand(@PathParam("commandId") int commandId, GenericCommand command) throws Exception {
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session), contextMessage;

		if (command.getCommandParameters() == null) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			CobraAdminMiscDatabaseCommand genericCommand = CommandFactory.createGenericCommand(SessionState.getAdminUUID(session),
					commandId, command.getCommandParameters());

			contextMessage = ResourceUtilities.getLocalizedFormattedString("GENERIC_COMMAND_FAILED", new String[] {}, locale, m_Base);
			amServerProtocol.sendCommandAndValidateResponse(genericCommand, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}
	
    /**
	 * <p>Removes one or more performed actions from the mobile devices.</p>
	 *    
	 * <p>Example body: The array of the ids of the performed action history records, for example:</p>   
	 * <pre>
	 *  {
	 *  &emsp;"actionHistoryIds":[1,26]
	 *  }
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * @param deletePerformedActions The object with an array of the ids of the deleted performed action history records included
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/removeactionsfromdevices")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If action history ids are empty."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void removeActionsFromDevices(@Context HttpServletRequest req,
			RemoveActionsFromDevices removeActionsFromDevices) throws Exception {
		MDC.put("request", removeActionsFromDevices.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (removeActionsFromDevices.getActionHistoryIds() == null || removeActionsFromDevices.getActionHistoryIds().length == 0) {
			throw new BadRequestException("COMMANDS_ACTION_HISTORY_IDS_ARE_EMPTY_IN_REMOVEACTIONSFROMDEVICES", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteMobileDevicePerformedActionsCommand(
					SessionState.getAdminUUID(session),
					removeActionsFromDevices.getActionHistoryIds()
				);
				String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"COMMANDS_REMOVE_ACTIONS_FROM_MOBILE_DEVICES_FAILED", 
					null, 
					locale, m_Base);
				MDC.put("command", command.toXml());
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response);

		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}

		MDC.remove("command");
		MDC.remove("request");
	}

    /**
	 * <p>Re-execute performed actions on the mobile devices identified by the ids.</p>
	 *    
	 * <p>Example body: The array of the device ids, and the array of the unique ids of the actions, for example:</p>   
	 * <pre>
	 *  {
	 *  &emsp;"deviceIds":[1,26],
	 *  &emsp;"actionUuids":["0BDE1CF9-53F5-43AA-B409-7BD3065A0F0A","B85DBCE8-098F-4194-8495-5DB7BBAF1B6E"],
	 *  &emsp;"executeImmediately":true
	 *  }
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * @param executePerformedActions The object with an array of the unique ids of the actions included
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/executeactionsondevices")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If any of the devices or actions do not exist"),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void executeActionsOnDevices(@Context HttpServletRequest req,
			ExecuteActionsOnDevices executeActionsOnDevices) throws Exception {
		MDC.put("request", executeActionsOnDevices.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (executeActionsOnDevices.getDeviceIds() == null || executeActionsOnDevices.getDeviceIds().length == 0) {
			throw new BadRequestException("COMMANDS_DEVICE_IDS_ARE_EMPTY_IN_EXECUTEACTIONSONDEVICES", null, locale, m_Base);
		}
		if (executeActionsOnDevices.getActionUuids() == null || executeActionsOnDevices.getActionUuids().length == 0) {
			throw new BadRequestException("COMMANDS_ACTION_HISTORY_IDS_ARE_EMPTY_IN_EXECUTEACTIONSONDEVICES", null, locale, m_Base);
		}
		
		IDal dal = Application.getDal(session);

		for (long deviceId : executeActionsOnDevices.getDeviceIds()) {
			IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
			if (deviceInfo == null) {
				throw new BadRequestException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
			}
		}
		
		List<MobileDevicePerformedAction> performedActionList = new ArrayList<MobileDevicePerformedAction>();
		for (long deviceId : executeActionsOnDevices.getDeviceIds()) {
	    	for (String actionUuid : executeActionsOnDevices.getActionUuids()) {
	    		MobileDevicePerformedAction pa = new MobileDevicePerformedAction();
	    		pa.setActionUniqueID(actionUuid);
	    		pa.setMobileDeviceId(deviceId);
	    		performedActionList.add(pa);
	    	}
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand command = CommandFactory.createReExecuteMobileDevicePerformedActionsCommand(
					SessionState.getAdminUUID(session),
					performedActionList.toArray(new MobileDevicePerformedAction[performedActionList.size()]),
					executeActionsOnDevices.getExecuteImmediately()
				);
				String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"COMMANDS_EXECUTE_PERFORMED_ACTIONS_FOR_MOBILE_DEVICE_FAILED", 
					null, 
					locale, m_Base);
				MDC.put("command", command.toXml());
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("command");
		MDC.remove("request");
	}

    /**
	 * <p>Re-execute selected actions for the policies.</p>
	 *    
	 * <p>Example body: The array of mappings of the action unique id and policy unique id, for example:</p>   
	 * <pre>
	 *  {
	 *  &emsp;"policyUuidActionUuidMappings":[
	 *  &emsp;{"actionUuid":"0BDE1CF9-53F5-43AA-B409-7BD3065A0F0A","policyUuid":"248787BC-B4AA-4160-9C42-90D2837EB387"}, 
	 *  &emsp;{"actionUuid":"0CB0CA9E-49E0-49D6-9CF9-3AC3AB5788A7","policyUuid":"408F294E-0F82-49CA-B359-D330E727A980"}
	 *  &emsp;],
	 *  &emsp;"executeImmediately":true
	 *  }
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * @param executeActionsOnPolicies The object with an array of the mappings of action unique id and policy unique id.
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/executeactionsonpolicies")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes({
			@ResponseCode(code = 400, condition = "If mappng data of action unique id and policy unique id is not supplied."),
			@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") })
	public void executeActionsOnPolicies(@Context HttpServletRequest req,
			ExecuteActionsOnPolicies executeActionsOnPolicies) throws Exception {
		MDC.put("request", executeActionsOnPolicies.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (executeActionsOnPolicies.getPolicyUuidActionUuidMappings() == null || executeActionsOnPolicies.getPolicyUuidActionUuidMappings().length == 0) {
			throw new BadRequestException("POLICYACTION_NO_POLICY_ACTION_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand command = CommandFactory.createReExecuteActionsForPoliciesCommand(
					SessionState.getAdminUUID(session),
					executeActionsOnPolicies.getPolicyUuidActionUuidMappings(),
					executeActionsOnPolicies.getExecuteImmediately()
				);
				String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"COMMANDS_EXECUTE_ACTIONS_FOR_POLICIES_FAILED", 
					null, 
					locale, m_Base);
				MDC.put("command", command.toXml());
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/command/Command");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("command");
		MDC.remove("request");
	}

}
