package com.absolute.am.webapi.controllers;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.ICustomFieldHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.model.CustomField;
import com.absolute.am.model.IdStrList;
import com.absolute.am.model.MetaData;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Custom Fields API</h3>
 * 
 * @author ephilippov
 * 
 */
@Path("/customfields")
public class CustomFields {
	private static final String VIEW_ALL_CUSTOM_FIELDS = "allcustomfields";
	private static final String VIEW_ONE_CUSTOM_FIELDS = "onecustomfield";

	private static final String RESULT_MD_COLUMN_DATA_TYPE = "ColumnDataType", RESULT_MD_COLUMN_DATA_TYPE_PLIST = "propertyList";;
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	private @Context HttpServletRequest m_servletRequest;

	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] { new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL,
			"All Custom Fields", 0), };

	/**
	 * <p>Get a list of named views available for custom fields. Refer to /api/mobiledevices/views – GET for an example of the response to this request.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *
	 * 
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForCustomFields() throws Exception  {

		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Returns the data for a given view.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *
	 * @param viewname The given <strong>viewname</strong>
	 * @return Get a list of named views available for policies. 
	 * @throws Exception 
	 */
	@GET
	@Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCustomFields)
	@StatusCodes({
		@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint."),
		@ResponseCode(code = 404, condition = "The <strong>viewName</strong> is not found.") 
	})
	public Result getView(@PathParam("viewname") String viewname, @Context UriInfo ui) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}

		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_ALL_CUSTOM_FIELDS;
		}

		Result result = null;
		IDal dal = Application.getDal(session);
		result = ViewHelper.getViewDetails(dal, viewname, ui.getQueryParameters(), null, dbLocaleSuffix);

		return result;
	}

	/**
	 * <p>Returns the data for a custom field identified by the provided customFieldId.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param ui
	 * @param customFieldId The id ( GUID ) of a custom field.
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowViewCustomFields)
	@StatusCodes({
		@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint."),
		@ResponseCode(code = 404, condition = "There is no such custom field.") 
	})
	public Result getCustomFieldForId(@Context UriInfo ui, @PathParam("id") String customFieldId) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		String locale = SessionState.getLocale(session);
		Result result = null;
		IDal dal = Application.getDal(session);

		ArrayList<String> viewParams = new ArrayList<String>();
		viewParams.add(customFieldId);
		result = ViewHelper.getViewDetails(dal, VIEW_ONE_CUSTOM_FIELDS, null, viewParams, dbLocaleSuffix);

		if (result.getRows().length == 0) {
			throw new NotFoundException("NO_CUSTOM_ITEM_FOUND_FOR_ID", new Object[] { customFieldId }, locale, m_Base);
		}
		
		if (!populatePropertyList(result)) {
			throw new NotFoundException("UNEXPECTED_ERROR_MESSAGE", null, locale, m_Base);
		}

		return result;
	}

	/**
	 * <p>Update the data (name, description, variableName, dataType, displayType, enumerationList) contained in a custom field that is associated with the customFieldId.</p>
	 *
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 *
	 * @param customFieldId the id of the custom field to modify
	 * @param customField new custom field data
	 * @throws Exception
	 */
	@POST
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowChangeCustomFields)
	@StatusCodes({ 
		@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint."),
		@ResponseCode(code = 404, condition = "The custom field associated with the id cannot be found.")
	})
	public void updateCustomField(@PathParam("id") String customFieldId, CustomField customField) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		IDal dal = Application.getDal(session);

		try {
			ICustomFieldHandler cfh = dal.getCustomFieldHandler();
			if (cfh.getCustomField(customFieldId) == null){
				throw new NotFoundException("NO_CUSTOM_ITEM_FOUND_FOR_ID", new Object[] { customFieldId }, locale, m_Base);
			}
			ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields = cfh.getCustomFieldsWithActions();

			CobraAdminMiscDatabaseCommand command = CommandFactory.updateCustomFieldCommand(customFieldId, customField, allCustomFields,
					SessionState.getAdminUUID(session));

			String contextMessage = ResourceUtilities.getLocalizedFormattedString("CUSTOM_FIELD_UPDATE_FAILED", null, locale, m_Base);

			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage).toXMLString();
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/webapi/Webapi");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}

	/**
	 * <p>Create a new custom field given a list.</p>
	 *
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 *    
	 * @param customField
	 * @throws Exception
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowChangeCustomFields)
	@StatusCodes({ 
		@ResponseCode(code = 400, condition = "A required parameter ( custom field name or data type ) is missing or a duplicate variable name was provided."),
		@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint.") 
	})
	public void createCustomField(CustomField customField) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (customField.name == null || customField.name.isEmpty() || customField.dataType == null || customField.displayType == null) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		IDal dal = Application.getDal(session);
		ICustomFieldHandler cfh = dal.getCustomFieldHandler();
		ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields = cfh.getCustomFieldsWithActions();
		
		if(customField.variableName != null){
			for(com.absolute.am.dal.model.CustomField cf : allCustomFields){
				if(cf.variableName != null && cf.variableName.equals(customField.variableName)){
					throw new BadRequestException("DUPLICATE_VARIABLE_NAME", null, locale, m_Base);
				}
			}
		}
		
		try {
			CobraAdminMiscDatabaseCommand command = CommandFactory.createCustomFieldCommand(customField, allCustomFields,
					SessionState.getAdminUUID(session));

			String contextMessage = ResourceUtilities.getLocalizedFormattedString("CUSTOM_FIELD_CREATE_FAILED", null, locale, m_Base);

			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage);
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/webapi/Webapi");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}

	
	/**
	 * <p>Delete some custom field items given a list of their IDs.</p>
	 *
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 *    
	 * @param customFieldIds a list of ids representing custom field items to be deleted
	 * @throws Exception
	 */
	@POST
	@Path("/delete")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowChangeCustomFields)
	@StatusCodes({ 
		@ResponseCode(code = 400, condition = "A required parameter ( custim field id list ) is missing."),
		@ResponseCode(code = 403, condition = "The user is not authorized to access this endpoint."),
		@ResponseCode(code = 404, condition = "A custom field with the provided id could not be found.") 
	})
	public void deleteCustomFields(IdStrList customFieldIds) throws Exception {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (customFieldIds.ids == null || customFieldIds.ids.length == 0) {
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		IDal dal = Application.getDal(session);

		try {
			ICustomFieldHandler cfh = dal.getCustomFieldHandler();
			for (String customFieldId : customFieldIds.ids) {
				if (cfh.getCustomField(customFieldId) == null) {
					throw new NotFoundException("CUSTOM_FIELD_CANNOT_FIND_FOR_DELETE", new Object[] { customFieldId }, locale, m_Base);
				}
			}
			
			ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields = cfh.getCustomFieldsWithActions();
			
			CobraAdminMiscDatabaseCommand command = CommandFactory.deleteCustomFieldCommand(customFieldIds.ids, allCustomFields,
					SessionState.getAdminUUID(session));

			String contextMessage = ResourceUtilities.getLocalizedFormattedString("CUSTOM_FIELD_DELETE_FAILED", null, locale, m_Base);

			amServerProtocol.sendCommandAndValidateResponse(command, contextMessage).toXMLString();
		} catch (AMWebAPILocalizableException e) {
			// localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/webapi/Webapi");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
	}
	
	private Boolean populatePropertyList(Result result){
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
					if (blob != null){
						try {
							row[i] = StringUtilities.extractArrayAsStringFromPlist(new String(blob));
						} catch (Exception ex) {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
}
