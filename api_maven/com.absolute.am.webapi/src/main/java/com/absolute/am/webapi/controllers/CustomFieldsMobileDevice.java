package com.absolute.am.webapi.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.model.customfieldmobiledevice.CustomFieldItem;
import com.absolute.am.model.customfieldmobiledevice.MobileDeviceCustomFieldAssociation;
import com.absolute.am.model.customfieldmobiledevice.MobileDeviceCustomFieldAssociations;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Custom Fields Device Assignment API</h3>
 * <p>This endpoint is used to assign custom fields to mobile devices,
 *    effectively creating a relationship between one or more custom fields and one or more mobile devices.</p>
 *    
 * @author ephilippov
 * 
 */
@Path ("/customfields_mobiledevice")
public class CustomFieldsMobileDevice {

	// The servlet request. This is injected by JAX-RS when the object is created.
	private @Context HttpServletRequest m_servletRequest;
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(CustomFieldsMobileDevice.class.getName()); 
	
	/**
	 * <p>Modify the existing custom field data for a mobile device.</p>
	 *    
	 * <p>Example request body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;{"deviceIds":[1], "items":[{"id": "F7DEF31B-54D0-43E0-860F-44297A7CA3C6", "value" : "abc", "type" : 2]}, 
	 * &emsp;{"deviceIds":[2], "items":[{"id": "ABCDE312-5AD0-43D0-860F-43123A7CA3C7", "value" : "red", "type" : 3}]} 
	 * ]
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 *    
	 * @param mappings MobileDeviceIdPolicyIdMappingAssociation
	 * @return
	 * @throws Exception 
	 */
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowEnterCustomFieldData)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or content do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void modifyMobileDevicesFromPolicies(MobileDeviceCustomFieldAssociations associations) throws Exception {
			
		handleCustomFieldsMobileDeviceCommand(associations, false);
    }
    
	/**
	 * <p>Remove a custom field item from a mobile device.</p>
	 *    
	 * <p>Example request body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;{"deviceIds":[1], "items":[{"id": "F7DEF31B-54D0-43E0-860F-44297A7CA3C6"}]}, 
	 * &emsp;{"deviceIds":[2], "items":[{"id": "ABCDE312-5AD0-43D0-860F-43123A7CA3C7"}]} 
	 * ]
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 *    
	 * @param mappings MobileDeviceIdPolicyIdMappingAssociation
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowEnterCustomFieldData)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or content do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deleteMobileDevicesFromPolicies(MobileDeviceCustomFieldAssociations associations) throws Exception {
		
		handleCustomFieldsMobileDeviceCommand(associations, true);
	}
	
	private void handleCustomFieldsMobileDeviceCommand(MobileDeviceCustomFieldAssociations associations, boolean deleteCommand) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;
		
		if (associations == null){
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		IDal dal = Application.getDal(session);

		for (MobileDeviceCustomFieldAssociation association : associations.associations){
			if (association.deviceIds == null || association.items == null){
				throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
			}
			
			if (!deleteCommand){
				for (CustomFieldItem item : association.items){
					if (item.value == null || item.type == 0) {
						throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
					}
				}
			}
						
			for (Long deviceId : association.deviceIds){
				IPhoneInfo device = dal.getDeviceHandler().getDevice(deviceId);
				
				if (device == null) {
					throw new BadRequestException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
				}
								
				AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
				
				// need to convert the file version correctly if it was sent in with two parts
				if(!deleteCommand){
					for (CustomFieldItem item : association.items){
						// if data type is file version (5), and the value pass in is zero (0)
						if(item.type == 5 && item.value.equals("0")){
							long dataValueLow32 = 0, dataValueHigh32 = 0;
							
							if (item.valueLow32 != null){
								dataValueLow32 = Long.parseLong(item.valueLow32);
							}
							if (item.valueHigh32 != null){
								dataValueHigh32 = Long.parseLong(item.valueHigh32);
							}
							
							item.value = String.valueOf((dataValueHigh32 << 32) | (dataValueLow32 & 0xFFFFFFFFL));
						}
					}
				}
				
				try {		
					CobraAdminMiscDatabaseCommand modifyCustomFieldFromDeviceCommand = CommandFactory.modifyCustomFieldFromDeviceCommand(
							deviceId,
							device.getTargetIdentifier(),
							association.items,
							deleteCommand,
							SessionState.getAdminUUID(session));

					contextMessage = ResourceUtilities.getLocalizedFormattedString("COMMAND_FAILED", null, locale, m_Base);
					PropertyList response = amServerProtocol.sendCommandAndValidateResponse(modifyCustomFieldFromDeviceCommand, contextMessage);

					// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
					Application.getSyncService().prioritySync(SessionState.getSyncServiceSession(session), response);								
				} catch (AMWebAPILocalizableException e) {
					//localize and re-throw
					AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
					throw ex;
				} finally {
					amServerProtocol.close();
				}
			}
		}
    }
}
