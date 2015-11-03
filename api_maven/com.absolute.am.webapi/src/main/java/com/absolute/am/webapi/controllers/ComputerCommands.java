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
import java.util.Map;

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
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.ComputerCommandFactory;
import com.absolute.am.dal.IDal;
import com.absolute.am.model.MetaData;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.model.command.ComputerGatherInventoryInformation;
import com.absolute.am.model.command.ComputerSendMessage;
import com.absolute.am.model.command.DeleteCommands;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.CommandPermission;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.CommandPermission.AMCommand;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Computer Commands API</h3>
 * <p>Unless otherwise stated, the following APIs may return these status codes:
 * <ul>
 * <li><strong>204 No Content</strong>: The request has been accepted and will be delivered to the device as soon as possible.</li>
 * <li><strong>400 Bad Request</strong>: One or more devices were not found.</li>
 * </ul></p>
 * @author maboulkhoudoud
 *
 */
@Path("/computercommands")
public class ComputerCommands {
	@Context
	ServletContext sc;

	private static Logger m_logger = LoggerFactory
			.getLogger(ComputerCommands.class.getName());

	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String VIEW_NAME_ALL_COMPUTERS_COMMAND_HISTORY = "commandhistoryforcomputers";
	private static final String VIEW_NAME_COMMANDS_HISTORY_BY_COMPUTERS = "commandhistorybycomputers";
	private static final String VIEW_NAME_ALL_QUEUED_COMPUTER_COMMANDS = "queuedcommandsforcomputers";
	private static final String VIEW_NAME_QUEUED_COMMANDS_BY_COMPUTERS = "queuedcommandsbycomputers";
	
	private static final String VIEW_NAME_COMMAND_DETAILS_BY_ID = "commanddetailsbyid";

	private final String SQL_SELECT_COMPUTER_IDS_FOR_ADMIN = "SELECT agent_info_record_id AS id FROM admin_computer_info WHERE AdminUUID = '%1$s'";

	private static final ViewDescription[] m_viewCommandsHistoryDescriptions = new ViewDescription[] { new ViewDescription(
			ViewHelper.VH_VIEW_NAME_ALL, "All Commands History", 0) };

	private static final ViewDescription[] m_viewQueuedCommandsDescriptions = new ViewDescription[] { new ViewDescription(
			ViewHelper.VH_VIEW_NAME_ALL, "All Queued Commands", 0) };

	/**
	 * The servlet request. This is injected by JAX-RS when the object is
	 * created.
	 */
	private @Context
	HttpServletRequest m_servletRequest;

	/**
	 * <p>Returns a list of all available views for the Commands History end-point.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowViewCommandsWindow </p>
	 *    
	 * @return Returns a list of all available views
	 * @throws Exception
	 */
	@GET
	@Path("/history/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCommandsWindow)
	@StatusCodes ({
		@ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
	})
	public ViewDescriptionList getViewsForCommandsHistory() throws Exception {
		m_logger.debug("Commands.getViewsForCommandsHistory called");

		ViewDescriptionList result = new ViewDescriptionList();
		result.setViewDescriptions(m_viewCommandsHistoryDescriptions);

		return result;
	}

	/**
	 * <p>Returns a list of all available views for the Queued Commands end-point.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowViewCommandsWindow </p>
	 *    
	 * @return Returns a list of all available views
	 * @throws Exception
	 */
	@GET
	@Path("/queued/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCommandsWindow)
	@StatusCodes ({
		@ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
	})
	public ViewDescriptionList getViewsForQueuedCommands() throws Exception {
		m_logger.debug("Commands.getViewsForQueuedCommands called");

		ViewDescriptionList result = new ViewDescriptionList();
		result.setViewDescriptions(m_viewQueuedCommandsDescriptions);

		return result;
	}

	/**
	 * <p>Returns a list of the commands which have been executed with the device specified with <strong>viewname</strong>.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowViewCommandsWindow </p>
	 * 
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns a list of the commands
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/history/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCommandsWindow)
	public Result getHistoryView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {

		MDC.put("viewname", viewname);
		m_logger.debug("Commands.getHistoryView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		if (!ViewUtilities.isValidViewName(viewname,
				m_viewCommandsHistoryDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base,
					"viewname", viewname);
		}

		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_COMPUTERS_COMMAND_HISTORY;
		}

		Result result = null;
		IDal dal = Application.getDal(session);

		String adminUUID = session.getAttribute("adminUUID").toString();
		dal.getComputerAdminAccessHandler().refreshAccessForAdmin(adminUUID);

		Map<String, Object> logonResult = SessionState
				.getLogonResultParameters(session);

		Map<String, Object> adminInfo = (Map<String, Object>) logonResult
				.get("AdminInfo");

		Long canSeeAllRecords = (Long) adminInfo
				.get(Login.kCobra_Admin_CanSeeAllRecords_Param);

		ArrayList<String> viewParams = new ArrayList<String>();

		if (canSeeAllRecords != 1) {
			// use view with device id filter
			if (viewname
					.compareToIgnoreCase(VIEW_NAME_ALL_COMPUTERS_COMMAND_HISTORY) == 0) {
				viewname = VIEW_NAME_COMMANDS_HISTORY_BY_COMPUTERS;
			}

			String selectStatement = String.format(
					SQL_SELECT_COMPUTER_IDS_FOR_ADMIN, adminUUID);
			viewParams.add(selectStatement);
		}

		result = ViewHelper.getViewDetails(dal, viewname,
				ViewHelper.getQueryParameters(ui, session), viewParams,
				dbLocaleSuffix);

		MDC.remove("viewname");

		return result;
	}

	/**
	 * <p>Returns a list of the commands which have been queued to the devices specified by the <strong>viewname</strong>.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns a list of commands.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/queued/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getQueuedView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {

		MDC.put("viewname", viewname);
		m_logger.debug("Commands.getQueuedView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		if (!ViewUtilities.isValidViewName(viewname,
				m_viewQueuedCommandsDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base,
					"viewname", viewname);
		}

		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_QUEUED_COMPUTER_COMMANDS;
		}

		Result result = null;

		IDal dal = Application.getDal(session);

		String adminUUID = session.getAttribute("adminUUID").toString();
		dal.getComputerAdminAccessHandler().refreshAccessForAdmin(adminUUID);

		Map<String, Object> logonResult = SessionState
				.getLogonResultParameters(session);
		Map<String, Object> adminInfo = (Map<String, Object>) logonResult
				.get("AdminInfo");
		Long canSeeAllRecords = (Long) adminInfo
				.get(Login.kCobra_Admin_CanSeeAllRecords_Param);

		ArrayList<String> viewParams = new ArrayList<String>();

		if (canSeeAllRecords != 1) {
			// use view with device id filter
			if (viewname.compareToIgnoreCase(VIEW_NAME_ALL_QUEUED_COMPUTER_COMMANDS) == 0) {
				viewname = VIEW_NAME_QUEUED_COMMANDS_BY_COMPUTERS;
			}

			String selectStatement = String.format(
					SQL_SELECT_COMPUTER_IDS_FOR_ADMIN, adminUUID);
			viewParams.add(selectStatement);
		}

		result = ViewHelper.getViewDetails(dal, viewname,
				ViewHelper.getQueryParameters(ui, session), viewParams,
				dbLocaleSuffix);

		MDC.remove("viewname");

		return result;
	}

	/**
	 * <p>Submits a send message command for a list of computers.</p>
     * <p>The body of the request looks like this:</p>
	 * <pre>
	 *{
	 * &emsp;"serialNumbers":[123,456],
	 * &emsp;"message":"Greetings from AM."
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * <p>Command permissions bit required:<br/>
	 * Bit 1.</p>
	 * 
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
	@CommandPermission(AMCommand.kCobra_AgentSendMessage_Command)
	public void postSendMessageCommand(@Context HttpServletRequest req,
			ComputerSendMessage sendMessage) throws IOException,
			GeneralSecurityException, ParserConfigurationException,
			SAXException, AMServerProtocolException {

		// TODO: replace with annotated validation
		MDC.put("request", sendMessage.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (sendMessage == null || sendMessage.getSerialNumbers() == null
				|| sendMessage.getSerialNumbers().length <= 0) {
			throw new BadRequestException(
					"COMMANDS_MUST_SPECIFY_SERIALS_TO_SEND_MESSAGE", null,
					locale, m_Base);
		}

		// TODO: replace with annotated validation
		if (sendMessage.getMessage() == null
				|| sendMessage.getMessage().length() <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_MESSAGE",
					null, locale, m_Base);
		}
		
		String timeoutLocalizedString = ResourceUtilities
				.getResourceStringForLocale("COMMAND_TEXT_TIMEOUT_COUNTER",
						m_Base, locale);

		String headerLocalizedString = ResourceUtilities
				.getResourceStringForLocale("COMMAND_TEXT_HEADER",
						m_Base, locale);
		
		CobraAdminMiscDatabaseCommand command = ComputerCommandFactory
				.createSendMessageCommand(SessionState.getAdminUUID(session),
						sendMessage.getSerialNumbers(),
						sendMessage.getMessage(),
						headerLocalizedString,
						timeoutLocalizedString);
		
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(
				SessionState.getAMServerProtocolSettings(session));

		try {
			String contextMessage = ResourceUtilities
					.getResourceStringForLocale("COMMANDS_SEND_MESSAGE_FAILED",
							m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command,
					contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,
					locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}
	
	/**
	 * <p>Submits a gather inventory command for a list of computers.</p>
	 * <p>The body of the request looks like this:</p>
	 * <pre>
	 *{
	 * &emsp;"serialNumbers":[123,456],
	 * &emsp;"fullInv":true,
	 * &emsp;"withFonts":false,
	 * &emsp;"withPrinters":false,
	 * &emsp;"withServices":false,
	 * &emsp;"withStartupItems":false
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * <p>Command permissions bit required:<br/>
	 * Bit 16.</p>
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 */
	@POST
	@Path("/gatherinventory")
	@Consumes(MediaType.APPLICATION_JSON)
	@CommandPermission(AMCommand.kCobra_AgentGetFullInventoryInfo_Command)
	public void postGatherInventoryCommand(@Context HttpServletRequest req,
			ComputerGatherInventoryInformation gatherInformation) throws IOException,
			GeneralSecurityException, ParserConfigurationException,
			SAXException, AMServerProtocolException {

		// TODO: replace with annotated validation
		MDC.put("request", gatherInformation.toString());
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (gatherInformation == null || gatherInformation.getSerialNumbers() == null
				|| gatherInformation.getSerialNumbers().length <= 0) {
			throw new BadRequestException(
					"COMMANDS_MUST_SPECIFY_SERIALS_TO_GATHER_INVENTORY", null,
					locale, m_Base);
		}
		
		CobraAdminMiscDatabaseCommand command = ComputerCommandFactory
				.createForceFullInventoryCommand(SessionState.getAdminUUID(session),
						gatherInformation.getSerialNumbers(),
						gatherInformation.isFullInv(),
						gatherInformation.isWithFonts(),
						gatherInformation.isWithPrinters(),
						gatherInformation.isWithServices(),
						gatherInformation.isWithStartupItems());
		
		MDC.put("command", command.toXml());

		AMServerProtocol amServerProtocol = new AMServerProtocol(
				SessionState.getAMServerProtocolSettings(session));

		try {
			String contextMessage = ResourceUtilities
					.getResourceStringForLocale("COMMANDS_GATHER_INVENTORY_FAILED",
							m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command,
					contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,
					locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("command");
		MDC.remove("request");
	}
	
	private static final String RESULT_MD_COLUMN_DATA_TYPE = "ColumnDataType";
	private static final String RESULT_MD_COLUMN_DATA_TYPE_PLIST = "propertyList";
	
	/**
	 * <p>Returns the output of a named view for the Queued Commands end-point.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given command id
	 * @return Returns the command details of the command specified with command id
	 * @throws Exception
	 */
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getCommandDetailsById(@PathParam("id") int id,
			@Context UriInfo ui) throws Exception {

		MDC.put("viewname", VIEW_NAME_COMMAND_DETAILS_BY_ID);
		m_logger.debug("Commands.getQueuedView called.");
		HttpSession session = m_servletRequest.getSession();
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		String locale = SessionState.getLocale(session);

		Result result = null;

		IDal dal = Application.getDal(session);
		try {

			String adminUUID = session.getAttribute("adminUUID").toString();
			dal.getComputerAdminAccessHandler().refreshAccessForAdmin(adminUUID);

			ArrayList<String> viewParams = new ArrayList<String>();
			viewParams.add(Long.toString(id));

			result = ViewHelper.getViewDetails(dal, VIEW_NAME_COMMAND_DETAILS_BY_ID,
					ViewHelper.getQueryParameters(ui, session), viewParams,
					dbLocaleSuffix);
			
			if(result.getRows().length > 0){
				result = getPropertyListFromResult(result); 
			} else {
				throw new NotFoundException(
						"COMMANDS_SPECIFIED_INVALID_ID", null,
						locale, m_Base);
			}
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,
					locale, "/command/Command");
			throw ex;
		} 
		
		MDC.remove("viewname");

		return result;
	}
	
	/**
	 * <p>Delete computer history commands.</p>
	 *    
	 * <p>Rights required:</br>
	 *    AllowRemoveiOSHistoryCommands </p>
	 *     
	 * @return 
	 * @throws Exception 
	 */
	@POST @Path("/history/delete")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowRemoveiOSHistoryCommands)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deleteHistoryCommands(DeleteCommands historyCommand) throws Exception  {
				
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (historyCommand == null || historyCommand.getCommandIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_DELETE_COMMANDS", null, locale, m_Base);
		}
	
		CobraAdminMiscDatabaseCommand command = ComputerCommandFactory.createDeleteHistoryCommandsCommand(
				SessionState.getAdminUUID(session), 
				historyCommand.getCommandIds());
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_DELETE_HIST_COMMAND_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}
	
	/**
	 * <p>Delete computer queued commands.</p>
	 *    
	 * <p>Rights required:</br>
	 *    AllowViewCommandsWindow </p>
	 *     
	 * @return 
	 * @throws Exception 
	 */
	@POST @Path("/queued/delete")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCommandsWindow)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deleteQueuedCommands(DeleteCommands queuedCommand) throws Exception  {
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (queuedCommand == null || queuedCommand.getCommandIds().length <= 0) {
			throw new BadRequestException("COMMANDS_MUST_SPECIFY_IDS_TO_DELETE_COMMANDS", null, locale, m_Base);
		}
	
		CobraAdminMiscDatabaseCommand command = ComputerCommandFactory.createDeleteQueuedCommandsCommand(
				SessionState.getAdminUUID(session), 
				queuedCommand.getCommandIds());
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("COMMANDS_DELETE_QUEUED_COMMAND_FAILED", m_Base, locale);
			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}
	
	private static Result getPropertyListFromResult(Result result) throws ParserConfigurationException, SAXException, IOException{
		MetaData metaData = result.getMetaData();
		ArrayList<Map<String, Object>> colMetaData = metaData.getColumnMetaData();
		for (int i=0; i < colMetaData.size(); i++) {
			Map<String, Object> thisColumn = colMetaData.get(i);
			if (thisColumn.containsKey(RESULT_MD_COLUMN_DATA_TYPE)) {
				String colType = (String)thisColumn.get(RESULT_MD_COLUMN_DATA_TYPE);
				if (colType.compareToIgnoreCase(RESULT_MD_COLUMN_DATA_TYPE_PLIST) == 0) {
					//Since we are getting the command by ID, there will only be one row to worry about
					Object[] row = (Object[]) result.getRows()[0];
					byte[] blob2 = (byte[])row[i];
					
					//The PLIST for commands does not have a root key, we can just dump the raw blob
					row[i] = PropertyList.fromByteArray(blob2);
				}
			}
		}
		
		return result;
	}
}
