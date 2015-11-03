package com.absolute.am.webapi.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CPLATPassword;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraCommandDefs;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.ColumnConstants;
import com.absolute.am.dal.IActionHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IEnumHandler;
import com.absolute.am.dal.model.MobileAction;
import com.absolute.am.model.MetaData;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.model.action.Action;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.controllers.ViewHelper;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * <h3>Actions API</h3>
 * <p>The Actions API is used to create new actions, edit or delete existing actions, and query actions in the system.</p>
 * 
 * @author rchen
 *
 */

@Path ("/actions")
public class Actions {

	private static Logger m_logger = LoggerFactory.getLogger(Actions.class.getName()); 
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
    private static final String VIEW_NAME_ONE_MOBILE_ACTION = "onemobileaction";
    private static final String VIEW_NAME_ONE_MOBILE_ACTION_WALLPAPER = "onemobileactionwallpaper";
    private static final String VIEW_NAME_POLICIES_FOR_ACTION = "policiesforaction";
    
    public static final String VIEW_NAME_ALL_ACTIONS = "allmobileactions";
    
    private static final String RESULT_MD_COLUMN_DATA_TYPE = "ColumnDataType";
	private static final String RESULT_MD_COLUMN_DATA_TYPE_PLIST = "propertyList";
	private static final String RESULT_DATA_TYPE = "DataType";
	private static final String RESULT_DATA_VALUE = "DataValue";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private static final int ACTION_TYPE_FREEZE_DEVICE = 7;
	private static final int ACTION_TYPE_SET_WALLPAPER = 9;
	private static final int ACTION_TYPE_SET_CUSTOM_FIELD = 14;
	
	private static final int CUSTOM_FIELD_DATA_TYPE_NUMBER = 2;
	private static final int CUSTOM_FIELD_DATA_TYPE_DATE = 4;
	private static final int CUSTOM_FIELD_DATA_TYPE_FILE_VERSION = 5;
	
	//"version number", always 1 for a new object
	private static final int SEED_FOR_NEW_OBJECT = 1;
	private static final String FREEZE_DEVICE_PASSWORD = "Password";
	private static final String ENUM_TABLE_NAME_MOBILE_ACTION_TYPE = "enum_MobileActionTypes";
	private static final String ENUM_VALUE_ACTION_TYPE_SET_WALLPAPER = "Set Wallpaper Action";
	
	private static final String SET_WALLPAPER_WALLPAPER_PICTURE = "WallpaperPicture";
	private static final String WALLPAPER_IMAGE_LIMIT_BYTES = "com.absolute.webapi.controllers.action.wallpaperImageLimitBytes";
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	// made this public to test code can see it.
	public static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Actions", 0)
	};

	/**
	 * <p>Returns a list of named views available for actions. Please refer to <strong>/api/mobiledevices/views</strong> -
	 *    GET for an example of the response to this request.</p>
	 * <p>There is only one view and it is called "All", which means all the actions in the systems.</p>   
	 *    
	 * @return Returns all list of available views for actions.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public ViewDescriptionList getViewsForActions() throws Exception  {
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view. 
	 *    The result is a multi-row result set, but the exact content depends on the definition of the view.
	 *    </p>
	 * 
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "The <strong>viewname</strong> is not found.")
		})
	public Result getView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
				
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_ACTIONS;
		}

		Result result = null;
		IDal dal = Application.getDal(session);
		result = ViewHelper.getViewDetails(
					dal,
					viewname, 
					ui.getQueryParameters(),
					null,
					dbLocaleSuffix);
		
		MDC.remove("viewname");
		return result;
	}
    
	/**
	 * <p>Get the action identified by {id}.<br/>
	 *    The response is a single row result set with meta data. It includes the attributes Id, action Name, description, etc. 
	 *    and also includes the detail data for different action types; for example, the 'Set Wallpaper' action 
	 *    includes the wallpaper picture formatted as a Base64 string, the 'Send Message' action includes message text. 
	 *    </p>
	 * 
	 * @param actionId The given Action Id
	 * @return Returns the attributes of a action.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no such action.")
		})
	public Result getActionForId(
			@Context UriInfo ui,
			@PathParam("id") String actionId) throws Exception  {

		MDC.put("actionId", actionId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		IActionHandler handler = dal.getActionHandler();
		
		MobileAction action = handler.getAction(Long.parseLong(actionId));
		
		if (null == action) {
			throw new NotFoundException("NO_ACTION_FOUND_FOR_ID", new Object[]{actionId}, locale, m_Base);
		}
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(actionId);			
		
		result = ViewHelper.getViewDetails(
					dal, 
					VIEW_NAME_ONE_MOBILE_ACTION, 
					ViewHelper.getQueryParameters(ui, session), 
					userParams,
					dbLocaleSuffix);
		
		if (!convertActionDataToPropertyList(result, false, ResourceUtilities.getLanguageFromLocale(locale))) {
			throw new NotFoundException("ACTIONS_FAILS_TO_POPULATE_PLIST", new Object[]{actionId}, SessionState.getLocale(session), m_Base);
		}

		MDC.remove("actionId");
		
		if (result.getRows().length < 1) {
			throw new NotFoundException("NO_ACTION_FOUND_FOR_ID", new Object[]{actionId}, locale, m_Base);
		}
		
		return result;
	}
	
	/**
	 * <p>Get the wallpaper picture data for the 'Set Wallpaper' action identified by {id}.<br/>
	 *    The response is binary data with appropriate MIME type specified in the response Content-Type header. 
	 *    Two formats of images are supported: PNG and JPEG. If the wallpaper picture is of an unknown format, an empty response is returned.</p>
	 * 
	 * @param actionId The given Action Id
	 * @return Returns the wallpaper blob associated with a given action id 
	 * @throws Exception 
	 */
	@GET @Path("/{id}/wallpaper")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The action is not 'Set Wallpaper' typed action."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no such action.")
		})
	public Result getActionWallpaperPictureForId(
			@Context UriInfo ui,
			@PathParam("id") String actionId) throws Exception  {

		MDC.put("actionId", actionId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		IActionHandler handler = dal.getActionHandler();
		
		int actionType_SetWallpaper = Integer.parseInt(
				dal.getEnumHandler().getEnumKeyForValue(ENUM_TABLE_NAME_MOBILE_ACTION_TYPE, ENUM_VALUE_ACTION_TYPE_SET_WALLPAPER));
		MobileAction action = handler.getAction(Long.parseLong(actionId));
		
		if (null == action) {
			throw new NotFoundException("NO_ACTION_FOUND_FOR_ID", new Object[]{actionId}, locale, m_Base);
		}
		if (action.getActionType() !=  actionType_SetWallpaper) {
			throw new BadRequestException("ACTIONS_NOT_SET_WALLPAPER_TYPED", null, locale, m_Base);
		}
		
		if (action.getActionData() != null){
			ArrayList<String> userParams = new ArrayList<String>();
			userParams.add(actionId);			
			
			result = ViewHelper.getViewDetails(
						dal,
						VIEW_NAME_ONE_MOBILE_ACTION_WALLPAPER,
						ViewHelper.getQueryParameters(ui, session),
						userParams,
						dbLocaleSuffix);
			
			if (!convertActionDataToPropertyList(result, true, ResourceUtilities.getLanguageFromLocale(locale))) {
				throw new NotFoundException("ACTIONS_FAILS_TO_POPULATE_PLIST", new Object[]{actionId}, locale, m_Base);
			}
		}
	
		MDC.remove("actionId");
		
		return result;
	}
	
	/**
	 * <p>Returns the list of policies that this action is associated with. The response is a multi-row result set with meta data. 
	 *    The response includes the following attributes: Policy Name, Initial Delay, Repeat Interval, Repeat Count</p>
	 * 
	 * @param actionId the given action id
	 * @return Returns the list of policies that this action is associated with
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}/policies")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no action item with this id.")
		})

	public Result getPoliciesForActionId(
			@Context UriInfo ui,
			@PathParam("id") long actionId) throws Exception {
		
		MDC.put("actionId", "getPoliciesForId:" + actionId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		Result result = null;
		IDal dal = Application.getDal(session);
		MobileAction action = dal.getActionHandler().getAction(actionId);
		if (action == null) {
			throw new NotFoundException("NO_ACTION_FOUND_FOR_ID", new Object[]{actionId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(action.getUniqueID());
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_POLICIES_FOR_ACTION, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("actionId");
		return result;
	}

	/**
	 * <p>This endpoint is used to create an action of one of the following sixteen types:</p>
	 * <ol>
	 * <li>Send Message To Device</li>
	 * <li>Set Roaming Options</li>
	 * <li>Send Email</li>
	 * <li>Demote To Unmanaged Device</li>
	 * <li>Remove Configuration Profile</li>
	 * <li>Send SMS</li>
	 * <li>Freeze Device</li>
	 * <li>Update Device Information</li>
	 * <li>Set Wallpaper</li>
	 * <li>Set Activation Lock Options</li>
	 * <li>Register User In VPP</li>
	 * <li>Retire User From VPP</li>
	 * <li>Set Device Name</li>
	 * <li>Set Custom Field Value</li>
	 * <li>Attention Mode</li>
	 * <li>Send VPP Invitation</li>
	 * </ol>
	 * <p>Rights required:</br>
	 *    AllowModifyMobileActions </p>
	 * 
	 * @param action Action with action detail data included; The action detail data is a list of the key-value-pair values 
	 * like '{"SMSMessageText":"Send SMS Message","SMSPhonenumber":"604-000-0001"}'. However, different action types have 
	 * different action detail data. For example, the 'Set Wallpaper' action should include Base64 picture string, while 
	 * the 'Send Message to Device' action should include message text. The following table lists the sample action data for all 
	 * action types.
	 * <table>
	 * <tr><td><b>Action Type</b></td><td><b>Action Data Sample</b></td></tr>
	 * <tr><td>Send Message To Device</td><td>{"name":"Send Message to Device","actionType":1,"description":"here is test sample: Send Message to Device","supportedPlatforms":3, "actionData":{"MessageText":"Here is message: Send Message to Device"}}</td></tr>
	 * <tr><td>Set Roaming Option</td><td>{"name":"Set Roaming Options","actionType":2,"description":"here is test sample: Set Roaming Options","supportedPlatforms":3, "actionData":{"DataRoaming":-1,"VoiceRoaming":1}}</td></tr>
	 * <tr><td>Send Email</td><td>{"name":"Send Email","actionType":3,"description":"here is test sample: Send Email","supportedPlatforms":3, "actionData":{"EmailTo":"roger123@email-to.com","EmailCC":"roger123@email-cc.com","EmailSubject":"Here is email subject: Send Email","EmailMessageText":"Here is email message text: Send Email"}}</td></tr>
	 * <tr><td>Demote To Unmanaged Device</td><td>{"name":"Demote to Unmanaged Device","actionType":4,"description":"here is test sample: Demote to Unmanaged Device","supportedPlatforms":2}</td></tr>
	 * <tr><td>Remove Configuration Profile</td><td>{"name":"Remove Configuration Profile","actionType":5,"description":"here is test sample: Remove Configuration Profile","supportedPlatforms":3, "actionData":{"PayloadIdentifier":"com.mycompany.profile.6FB7A86C-4C3B-46BE-BB4B-967D59714DA0"}}</td></tr>
	 * <tr><td>Send SMS</td><td>{"name":"Send SMS","actionType":6,"description":"here is test sample: Send SMS","supportedPlatforms":3, "actionData":{"SMSMessageText":"Here is SMS message: Send SMS","SMSPhonenumber":"604-000-0001"}}</td></tr>
	 * <tr><td>Freeze Device</td><td>{"name":"Freeze Device","actionType":7,"description":"here is test sample: Freeze Device","supportedPlatforms":3, "actionData":{"Password":"12345678"}}</td></tr>
	 * <tr><td>Update Device Information</td><td>{"name":"Update Device Information","actionType":8,"description":"here is test sample: Update Device Information","supportedPlatforms":3}</td></tr>
	 * <tr><td>Set Wallpaper</td><td>{"name":"Set Wallpaper","actionType":9,"description":"here is test sample: Set Wallpaper","supportedPlatforms":3, "actionData":{"ApplyToHomeScreen":true,"ApplyToLockScreen":true,"WallpaperPicture":"Here is the Base64 formatted image string, which can be converted with Javascript."}}</td></tr>
	 * <tr><td>Set Activation Lock Options</td><td>{"name":"Set Activation Lock Options","actionType":10,"description":"here is test sample: Set Activation Lock Options","supportedPlatforms":3, "actionData":{"ChangeActivationLock":1}}</td></tr>
	 * <tr><td>Register User In VPP</td><td>{"name":"Register User in VPP","actionType":11,"description":"here is test sample: Register User in VPP","supportedPlatforms":3,"actionData":{"InviteMessage":"Register User in VPP: Invite Message Text",</br>"InviteSMSMessage":"Register User in VPP: Invite SMS Message Text",</br>"InviteSubject":"Register User in VPP:InviteSubject","SendInviteViaAMAgent":false,"SendInviteViaAbsoluteApps":true,"SendInviteViaEmail":false,</br>"SendInviteViaMDM":true,"SendInviteViaSMS":false,</br>"SendInviteViaWebClip":true,"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * <tr><td>Retire User From VPP</td><td>{"name":"Retire User From VPP","actionType":12,"description":"here is test sample: Retire User From VPP","supportedPlatforms":3, "actionData":{"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * <tr><td>Set Device Name</td><td>{"name":"Update Device Information","actionType":13,"description":"here is test sample: Update Device Information","supportedPlatforms":3, "actionData":{"DeviceName":"your device name"}}</td></tr>
	 * <tr><td>Set Custom Field Value</td><td>{"name":"Set Custom Field with String Typed","actionType":14,"description":"here is test sample: Set Custom Field with String Typed","supportedPlatforms":3, "actionData":{"DataType":1,"DataValue":"Here is the string-typed data value: ROGER - TEST","FieldID":"F7DEF31B-54D0-43E0-860F-44297A7CA3C6","Name":"Test String field","RemoveValue":0}}</td></tr>
	 * <tr><td>Attention Mode</td><td>{"name":"Attention Mode","actionType":15,"description":"here is test sample: Attention Mode","supportedPlatforms":3, "actionData":{"AttentionModeEnabled":false,"LockScreenMessage":"Here is attention message text: Attention Mode"}}</td></tr>
	 * <tr><td>Send VPP Invitation</td><td>{"name":"Send VPP Invitation","actionType":16,"description":"here is test sample: Send VPP Invitation","supportedPlatforms":3,"actionData":{"InviteMessage":"Send VPP Invitation: Invite Message Text",</br>"InviteSMSMessage":"Send VPP Invitation: Invite SMS Message Text",</br>"InviteSubject":"Send VPP Invitation:InviteSubject","SendInviteViaAMAgent":false,"SendInviteViaAbsoluteApps":true,"SendInviteViaEmail":false,</br>"SendInviteViaMDM":true,"SendInviteViaSMS":false,</br>"SendInviteViaWebClip":true,"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * </table>
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileActions)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The action data is not valid."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 409, condition = "A action with the same name already exists.")
		})
	public void createAction(
			@Context HttpServletRequest req,
			Action action) throws Exception  {
		String actionName = action.getName();
	
		MDC.put("actionName", actionName);
		m_logger.debug("createAction called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (actionName == null || actionName.isEmpty()) {
			throw new BadRequestException("ACTIONS_NO_ACTION_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		} else {
			actionName = actionName.trim();
		}
		
		IDal dal = Application.getDal(session);
		if (0 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal,
				actionName, VIEW_NAME_ALL_ACTIONS, ColumnConstants.COLUMN_INFO_ITEM_ID_MOBILE_ACTIONS_ACTION_NAME)){
			throw new WebAPIException(Response.Status.CONFLICT, "ACTIONS_ACTION_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, locale, m_Base, "ActionName", actionName);
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand createActionCommand = CommandFactory.createActionCommand(
				SessionState.getAdminUUID(session),
				UUID.randomUUID().toString().toUpperCase(),
				SEED_FOR_NEW_OBJECT,
				action.getActionType(),
				action.getSupportedPlatforms(),
				action.getName(),
				action.getDescription(),
				createActionData(action, null)
			);
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
				"ACTIONS_CREATE_FAILED", 
				null, 
				locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
				createActionCommand, contextMessage);
			// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response);				
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}

		MDC.remove("actionName");
	}
	
	/**
	 * <p>This endpoint is used to update an existing action of one of the following sixteen types:</p>
	 * <ol>
	 * <li>Send Message To Device</li>
	 * <li>Set Roaming Options</li>
	 * <li>Send Email</li>
	 * <li>Demote To Unmanaged Device</li>
	 * <li>Remove Configuration Profile</li>
	 * <li>Send SMS</li>
	 * <li>Freeze Device</li>
	 * <li>Update Device Information</li>
	 * <li>Set Wallpaper</li>
	 * <li>Set Activation Lock Options</li>
	 * <li>Register User In VPP</li>
	 * <li>Retire User From VPP</li>
	 * <li>Set Device Name</li>
	 * <li>Set Custom Field Value</li>
	 * <li>Attention Mode</li>
	 * <li>Send VPP Invitation</li>
	 * </ol>
	 * <p>Rights required:</br>
	 *    AllowModifyMobileActions </p>
	 * 
	 * @param action Action with action detail data included; The following table lists the sample action data for all action types.
	 * <table>
	 * <tr><td><b>Action Type</b></td><td><b>Action Data Sample</b></td></tr>
	 * <tr><td>Send Message To Device</td><td>{""seed":2,"name":"Send Message to Device","actionType":1,"description":"here is test sample: Send Message to Device","supportedPlatforms":3, "actionData":{"MessageText":"Here is message: Send Message to Device"}}</td></tr>
	 * <tr><td>Set Roaming Option</td><td>{""seed":1,"name":"Set Roaming Options","actionType":2,"description":"here is test sample: Set Roaming Options","supportedPlatforms":3, "actionData":{"DataRoaming":-1,"VoiceRoaming":1}}</td></tr>
	 * <tr><td>Send Email</td><td>{"seed":3,"name":"Send Email","actionType":3,"description":"here is test sample: Send Email","supportedPlatforms":3, "actionData":{"EmailTo":"roger123@email-to.com","EmailCC":"roger123@email-cc.com","EmailSubject":"Here is email subject: Send Email","EmailMessageText":"Here is email message text: Send Email"}}</td></tr>
	 * <tr><td>Demote To Unmanaged Device</td><td>{"seed":1,"name":"Demote to Unmanaged Device","actionType":4,"description":"here is test sample: Demote to Unmanaged Device","supportedPlatforms":2}</td></tr>
	 * <tr><td>Remove Configuration Profile</td><td>{"seed":1,"name":"Remove Configuration Profile","actionType":5,"description":"here is test sample: Remove Configuration Profile","supportedPlatforms":3, "actionData":{"PayloadIdentifier":"com.mycompany.profile.6FB7A86C-4C3B-46BE-BB4B-967D59714DA0"}}</td></tr>
	 * <tr><td>Send SMS</td><td>{"seed":1,"name":"Send SMS","actionType":6,"description":"here is test sample: Send SMS","supportedPlatforms":3, "actionData":{"SMSMessageText":"Here is SMS message: Send SMS","SMSPhonenumber":"604-000-0001"}}</td></tr>
	 * <tr><td>Freeze Device</td><td>{"seed":4,"name":"Freeze Device","actionType":7,"description":"here is test sample: Freeze Device","supportedPlatforms":3, "actionData":{"Password":"12345678"}}</td></tr>
	 * <tr><td>Update Device Information</td><td>{"seed":1,"name":"Update Device Information","actionType":8,"description":"here is test sample: Update Device Information","supportedPlatforms":3}</td></tr>
	 * <tr><td>Set Wallpaper</td><td>{"seed":2,"name":"Set Wallpaper","actionType":9,"description":"here is test sample: Set Wallpaper","supportedPlatforms":3, "actionData":{"ApplyToHomeScreen":true,"ApplyToLockScreen":true,"WallpaperPicture":"Here is the Base64 formatted image string, which can be converted with Javascript."}}</td></tr>
	 * <tr><td>Set Activation Lock Options</td><td>{"seed":1,"name":"Set Activation Lock Options","actionType":10,"description":"here is test sample: Set Activation Lock Options","supportedPlatforms":3, "actionData":{"ChangeActivationLock":1}}</td></tr>
	 * <tr><td>Register User In VPP</td><td>{"seed":1,"name":"Register User in VPP","actionType":11,"description":"here is test sample: Register User in VPP","supportedPlatforms":3,"actionData":{"InviteMessage":"Register User in VPP: Invite Message Text",</br>"InviteSMSMessage":"Register User in VPP: Invite SMS Message Text",</br>"InviteSubject":"Register User in VPP:InviteSubject","SendInviteViaAMAgent":false,"SendInviteViaAbsoluteApps":true,"SendInviteViaEmail":false,</br>"SendInviteViaMDM":true,"SendInviteViaSMS":false,</br>"SendInviteViaWebClip":true,"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * <tr><td>Retire User From VPP</td><td>{"seed":1,"name":"Retire User From VPP","actionType":12,"description":"here is test sample: Retire User From VPP","supportedPlatforms":3, "actionData":{"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * <tr><td>Set Device Name</td><td>{"seed":3,"name":"Update Device Information","actionType":13,"description":"here is test sample: Update Device Information","supportedPlatforms":3, "actionData":{"DeviceName":"your device name"}}</td></tr>
	 * <tr><td>Set Custom Field Value</td><td>{"seed":1,"name":"Set Custom Field with String Typed","actionType":14,"description":"here is test sample: Set Custom Field with String Typed","supportedPlatforms":3, "actionData":{"DataType":1,"DataValue":"Here is the string-typed data value: ROGER - TEST","FieldID":"F7DEF31B-54D0-43E0-860F-44297A7CA3C6","Name":"Test String field","RemoveValue":0}}</td></tr>
	 * <tr><td>Attention Mode</td><td>{"seed":2,"name":"Attention Mode","actionType":15,"description":"here is test sample: Attention Mode","supportedPlatforms":3, "actionData":{"AttentionModeEnabled":false,"LockScreenMessage":"Here is attention message text: Attention Mode"}}</td></tr>
	 * <tr><td>Send VPP Invitation</td><td>{"name":"Send VPP Invitation","actionType":16,"description":"here is test sample: Send VPP Invitation","supportedPlatforms":3,"actionData":{"InviteMessage":"Send VPP Invitation: Invite Message Text",</br>"InviteSMSMessage":"Send VPP Invitation: Invite SMS Message Text",</br>"InviteSubject":"Send VPP Invitation:InviteSubject","SendInviteViaAMAgent":false,"SendInviteViaAbsoluteApps":true,"SendInviteViaEmail":false,</br>"SendInviteViaMDM":true,"SendInviteViaSMS":false,</br>"SendInviteViaWebClip":true,"VPPAccountRecordID":1,"VPPAccountUniqueID":"0E382F7B-F937-4BA3-B592-CE30EEFF9D46"}}</td></tr>
	 * </table>
	 * @return
	 * @throws Exception 
	 */	
	@POST @Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileActions)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The action data is not valid."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no action with this id."),
		  @ResponseCode ( code = 409, condition = "A action with the same name already exists.")
		})
	public void updateAction(
			@Context HttpServletRequest req,
			@PathParam("id") long actionId,
			Action action) throws Exception  {
		
		String actionName = action.getName();
	
		MDC.put("actionName", actionName);
		m_logger.debug("createAction called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (actionName == null || actionName.isEmpty()) {
			throw new BadRequestException("ACTIONS_NO_ACTION_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		} else {
			actionName = actionName.trim();
		}
		
		IDal dal = Application.getDal(session);
		IActionHandler handler = dal.getActionHandler();
		MobileAction oldAction = handler.getAction(actionId);
		
		// the action name shout not be empty
		if (null == oldAction){
			throw new NotFoundException("NO_ACTION_FOUND_FOR_ID", new Object[]{actionId}, locale, m_Base);
		}
		
		if (oldAction.getSeed() != action.getSeed()) {
			throw new WebAPIException(Response.Status.CONFLICT, "ACTIONS_THIS_ACTION_HAS_BEEN_MODIFIED_BY_ANOTHER_USER",
					"SEED_MISMATCH", null, locale, m_Base, "ActionId", actionId);
		}

		// check if the action type of the new action matches the old one
		if (oldAction.getActionType() != action.getActionType()) {
			throw new BadRequestException("ACTIONS_ACTION_TYPE_MISMATCH",
					new Object[]{actionId, oldAction.getActionType(), action.getActionType()}, 
					locale, m_Base);
		}
		
		// check the action name is duplicated in the systems
		String[] existingActionUuids = handler.getActionUniqueIdsForActionNames(action.getName());
		for (String uuid : existingActionUuids){
			if (!uuid.equalsIgnoreCase(oldAction.getUniqueID())){
				throw new WebAPIException(Response.Status.CONFLICT, "ACTIONS_ACTION_NAME_ALREADY_EXISTS",
						"DUPLICATE_NAME", null, SessionState.getLocale(session), m_Base, "ActionName", actionName);
			}
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand updateActionCommand = CommandFactory.updateActionCommand(
				SessionState.getAdminUUID(session),
				// the non-changed parameter values for the action
				(int) actionId, oldAction.getUniqueID(), oldAction.getSeed(),
				// the parameter values for the new action
				action.getActionType(), 
				action.getSupportedPlatforms(), 
				action.getName(), 
				action.getDescription(), 
				createActionData(action, oldAction),
				// the parameter values for the old action
				oldAction.getActionType(), 
				oldAction.getSupportedPlatforms(), 
				oldAction.getDisplayName(), 
				oldAction.getDescription()
			);
			
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
				"ACTIONS_UPDATE_FAILED", 
				null, 
				locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
					updateActionCommand, contextMessage);
			// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response);				
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}

		MDC.remove("actionName");
	}

	/**
	 * <p>Delete the action identified by {id}.</p>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSActions </p>
	 * 
	 * @param actionId The given Action Id
	 * @return
	 * @throws Exception 
	 */
	@DELETE @Path("/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileActions)
	@StatusCodes ({
		  @ResponseCode ( code = 204, condition = "The action was deleted, there is no content/body in the response."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no action with this id.")
		})
	public void deleteActionForId(
			@Context UriInfo ui,
			@PathParam("id") int actionId) throws Exception {
		
		MDC.put("actionId", Integer.toString(actionId));
		m_logger.debug("deleteActionForId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (actionId <= 0) {
			throw new BadRequestException("ACTIONS_MUST_SPECIFY_ACTION_ID_TO_DELETE", null, locale, m_Base, "actionId", actionId);
		}
		
		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		
		try {
			IActionHandler handler = dal.getActionHandler();
			MobileAction action = handler.getAction((long) actionId);
			if (null == action) {
				throw new NotFoundException("ACTIONS_CANNOT_FIND_ACTION_FOR_DELETE", 
					new Object[] {actionId}, locale, m_Base);
			}
			amServerProtocol = deleteActions(new int[] {actionId}, locale);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("actionId");
	}
	
	/**
	 * <p>Delete multiple actions identified by an array of the action Ids; for example, [1,2,3].</p>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSActions </p>
	 * 
	 * @param actionIds The given Action Ids that are to be deleted from the system. The list of the action Ids must be included in
	 * parentheses, for example, [1,2,3]. 
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileActions)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "Action Ids is empty or one or more of the actions do not exist (the existing actions do not get deleted)"),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deleteActions(
			@Context UriInfo ui,
			int[] actionIds) throws Exception {
		
		MDC.put("actionIds", Arrays.toString(actionIds));
		m_logger.debug("deleteActions called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (actionIds.length <= 0) {
			throw new BadRequestException("ACTION_MUST_SPECIFY_ACTION_IDS_TO_DELETE", null, locale, m_Base, "actionId", actionIds);
		}
		
		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		
		try {
			IActionHandler handler = dal.getActionHandler();
			for(int actionId : actionIds){
				if (null == handler.getAction((long) actionId)) {
					throw new BadRequestException("ACTIONS_CANNOT_FIND_ACTION_FOR_DELETE", 
							new Object[] {actionId}, locale, m_Base);
				}
			}
			
			amServerProtocol = deleteActions(actionIds, locale);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("actionIds");
	}
	
	// convert the value in 'ActionData' column to the Property List
	// if wallPaperAction == true, then we're building the result plist specifically for the wallPaper action
	// in which case only the WallpaperPicture element is present
	private Boolean convertActionDataToPropertyList(Result result, boolean wallPaperAction, String locale){
		MetaData metaData = result.getMetaData();
		ArrayList<Map<String, Object>> colMetaData = metaData.getColumnMetaData();
		for (int i=0; i < colMetaData.size(); i++) {
			Map<String, Object> thisColumn = colMetaData.get(i);
			
			if (thisColumn.containsKey(RESULT_MD_COLUMN_DATA_TYPE)) {
				String colType = (String)thisColumn.get(RESULT_MD_COLUMN_DATA_TYPE);
				if (colType.compareToIgnoreCase(RESULT_MD_COLUMN_DATA_TYPE_PLIST) == 0) {
					Object[] rows = result.getRows();
					Object[] row = (Object[]) rows[0];
					byte[] blob = (byte[])row[i];
					
					try {
						// For some actions such as 'Update Device Information' and 'Demote to Unmanaged Device', there is no PList included.
						if (blob != null){
							PropertyList pl = PropertyList.fromByteArray(blob);
							
							if(wallPaperAction) {
								Iterator<Entry<String, Object>> iter = pl.entrySet().iterator();
								while (iter.hasNext()) {
								    Entry<String, Object> entry = iter.next();
								    
								    if(!entry.getKey().equals("WallpaperPicture")){
								        iter.remove();
								    }
								}
							} else {
								pl.remove("WallpaperPicture");
								
								if( pl.get(RESULT_DATA_TYPE) != null ){
									IEnumHandler enumHandler = Application.getDal(m_servletRequest.getSession()).getEnumHandler();
									String enumValueString = enumHandler.getEnumValueForKey("enum_CustomFieldDataType", pl.get(RESULT_DATA_TYPE).toString(), locale);
									pl.put("DataTypeString", enumValueString);
									
									int customFieldDataType = Integer.parseInt(pl.get(RESULT_DATA_TYPE).toString());
									// 5 = File Version
									if (customFieldDataType == CUSTOM_FIELD_DATA_TYPE_FILE_VERSION && pl.get(RESULT_DATA_VALUE) != null ) {
										int[] dataValues = StringUtilities.Convert64BasedIntegerTo32BasedIntegers(pl.get("DataValue").toString());
										pl.put("DataValueLow32", dataValues[0]);
										pl.put("DataValueHigh32", dataValues[1]);
									}
								}
							}
  						    
							row[i] = pl;
						}
					} catch (Exception ex) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private PropertyList createActionData(Action newAction, MobileAction oldMobileAction) 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		PropertyList plist = null; 
		String locale = SessionState.getLocale(m_servletRequest.getSession());
		
		LinkedHashMap<String, Object> actionDataMap = (LinkedHashMap<String, Object>) newAction.getActionData();
		if (actionDataMap != null)
		{
			plist = new PropertyList(actionDataMap);
			
			// 1. code the password for the 'Freeze Device (id=7)' typed action; if no passcode passed in (length=0), then 
			//    passcode should NOT be updated
			// 2. convert the image data to binary bytes from Base64 String for the 'Set Wallpaper (id=9)' typed action;
			// 3. for the 'Add Custom Fields (id=14)' actions, if the data type is 'File Version (data type=5)', combine
			//    DataValueHigh32 and DataValueLow32 to 64-based long value, then remove 'DataValueHigh32' and 'DataValueLow32'
			//    from the PList.
			if (newAction.getActionType() == ACTION_TYPE_FREEZE_DEVICE) {
				String password = (String) plist.get(FREEZE_DEVICE_PASSWORD);
				if (password != null && password.length() > 0) {
					String codedPassword = CPLATPassword.Encrypt(password, CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8"));
					plist.put(FREEZE_DEVICE_PASSWORD, codedPassword);
				} else if (oldMobileAction != null && oldMobileAction.getActionData() != null) {
					plist = PropertyList.fromByteArray(oldMobileAction.getActionData());
				}
			} else if (newAction.getActionType() == ACTION_TYPE_SET_WALLPAPER) {
				int wallpaperImageLimit = 0;
				String limit = m_servletRequest.getServletContext().getInitParameter(WALLPAPER_IMAGE_LIMIT_BYTES);
				if (null != limit && !limit.isEmpty()) {
					wallpaperImageLimit = Integer.parseInt(limit);
				} else {
					throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_NOT_FOUND", new Object[]{WALLPAPER_IMAGE_LIMIT_BYTES}, locale, m_Base);
				}
				
				String imageString = (String) plist.get(SET_WALLPAPER_WALLPAPER_PICTURE);
				byte[] imageContent = org.apache.commons.codec.binary.Base64.decodeBase64(imageString.getBytes());
				if (imageContent.length <= wallpaperImageLimit) {
					plist.put(SET_WALLPAPER_WALLPAPER_PICTURE, imageContent);
				} else {
					throw new BadRequestException("ACTIONS_WALLPAPER_IMAGE_OVER_LIMIT", new Object[]{wallpaperImageLimit}, locale, m_Base);
				}
			} else if (newAction.getActionType() == ACTION_TYPE_SET_CUSTOM_FIELD) {
				int customFieldDataType = Integer.parseInt(plist.get(RESULT_DATA_TYPE).toString());
				Object datavalue = plist.get(RESULT_DATA_VALUE);
				// 5 = File Version
				if (customFieldDataType == CUSTOM_FIELD_DATA_TYPE_FILE_VERSION && datavalue != null ) {
					long dataValueLow32 = 0;
					if (plist.get("DataValueLow32") != null){
						dataValueLow32 = Long.parseLong(plist.get("DataValueLow32").toString());
						plist.remove("DataValueLow32");
					}
					long dataValueHigh32 = 0;
					if (plist.get("DataValueHigh32") != null){
						dataValueHigh32 = Long.parseLong(plist.get("DataValueHigh32").toString());
						plist.remove("DataValueHigh32");
					}
					
					plist.put(RESULT_DATA_VALUE, (dataValueHigh32 << 32) | (dataValueLow32 & 0xFFFFFFFFL));
				} else if (customFieldDataType == CUSTOM_FIELD_DATA_TYPE_NUMBER && datavalue != null) {
					if (!StringUtilities.isStringNumeric(datavalue.toString())){
						throw new BadRequestException("ACTIONS_DATA_VALUE_IS_NOT_NUMERIC", new Object[]{ datavalue }, locale, m_Base);
					}
				} else if (customFieldDataType == CUSTOM_FIELD_DATA_TYPE_DATE && datavalue != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
					try {
						dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
						plist.put(RESULT_DATA_VALUE, dateFormat.parse(datavalue.toString()));						
					} catch (ParseException e) {
						throw new BadRequestException("ACTIONS_DATA_VALUE_IS_NOT_DATE", new Object[]{ datavalue }, locale, m_Base);
					}
				}
			}
		}
		
		return plist;
	}
	
	/*
	 * Performs the actual deleting single action or multiple actions specified with a list of the UUIDs
	 */
	private AMServerProtocol deleteActions(int[] actionIds, String locale) throws Exception {
		AMServerProtocol amServerProtocol = null;
		HttpSession session = m_servletRequest.getSession();
		
		CobraAdminMiscDatabaseCommand createDeleteActionsCommand = CommandFactory.createDeleteActionsCommand(
				SessionState.getAdminUUID(session),
				actionIds);
		
		amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		
		String contextMessage = ResourceUtilities.getResourceStringForLocale("ACTIONS_DELETE_ACTION_FAILED", m_Base, locale);
		PropertyList response = amServerProtocol.sendCommandAndValidateResponse(createDeleteActionsCommand, contextMessage);

		// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
		Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response );
		
		return amServerProtocol;
	}
}
