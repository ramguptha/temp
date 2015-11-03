package com.absolute.am.webapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.model.policymobiledevice.MobileDeviceIdPolicyIdMappingAssociation;
import com.absolute.am.model.policymobiledevice.MobileDeviceIdForPolicyIdArray;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.ArrayUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Policy Mobile Device Assignment API</h3>
 * <p>This endpoint is used to assign mobile devices to policies,
 *    effectively creating a relationship between one or more policies and one or more mobile devices.</p>
 *    
 */
@Path ("/policy_mobiledevice")
public class PolicyMobileDevice {

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyMobileDevice.class.getName()); 
	
	/**
	 * <p>This request is used to create a relationship between policies and mobile devices.</p>
	 * <p>Sample request bodies are shown below.</p>
	 *    
	 * <p>Example (A), assign one mobile device to multiple policies:</p>   
	 * <pre>
	 *{
	 * &emsp;"deviceIds":[53],
	 * &emsp;"policyIds":[7,9,236]
	 *}
	 * </pre>
	 * 
	 * <p>Example (B), assign multiple devices to one policy:</p>   
	 * <pre>
	 *{
	 * &emsp;"deviceIds":[53,92,61,107],
	 * &emsp;"policyIds":[9]
	 *}
	 * </pre>
	 * 
	 * <p>Implementation Note: The AM protocol command to the AM server consists of a device list and a single policy. 
	 *    Operations will need to be mapped to this format and grouped for efficiency.</p>   
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies</p>
	 *    
	 * @param mappings MobileDeviceIdForPolicyIdArray
	 * @return
	 * @throws Exception 
	 */
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If any of the policies or mobile devices do not exist, or if any of the policies are Smart policies (Smart policies cannot be assigned to)."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void assignMobileDevicesToPolicy(MobileDeviceIdForPolicyIdArray mappings) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;
		
		if (mappings == null) {
			throw new BadRequestException("POLICYMOBILEDEVICE_NO_POLICY_DEVICE_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("mappings", mappings.toString());
		
		long[] deviceIds = mappings.getDeviceIds();
		if (deviceIds == null || deviceIds.length == 0) {
			throw new BadRequestException("POLICYMOBILEDEVICE_NO_MOBILE_DEVICE_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
				
		List<Long> deviceIdsAsList = ArrayUtilities.<Long>toList(deviceIds);
		
		long[] policyIds = mappings.getPolicyIds();
		if (policyIds == null || policyIds.length == 0) {
			throw new BadRequestException("NO_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		UUID[] deviceUuids = null;
		String[] policyUniqueIds = null;
		String[] policyNames = null;
		IDal dal = Application.getDal(m_servletRequest.getSession());

		String[] deviceUniqueIds = null;
		deviceUniqueIds = dal.getDeviceHandler().getMobileDeviceUniqueIdsAsString(deviceIdsAsList);
		
		if (deviceUniqueIds == null || deviceUniqueIds.length != deviceIds.length) {
			throw new BadRequestException("POLICYMOBILEDEVICE_ONE_OR_MORE_DEVICE_IDS_WERE_INVALID", null, locale, m_Base,
					"requested", deviceIds.length, 
					"found", deviceUniqueIds != null ? deviceUniqueIds.length : 0);
		}
		
		deviceUuids = new UUID[deviceUniqueIds.length];
		for (int i = 0; i < deviceUniqueIds.length; i++) {
			deviceUuids[i] = UUID.fromString(deviceUniqueIds[i]);
		}
		deviceUniqueIds = null;	// Don't need this anymore.

		List<Long> policyIdsAsList = new ArrayList<Long>();
		policyNames = new String[policyIds.length];
		for (int i = 0; i < policyIds.length; i++) {
			iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyIds[i]);
			if (null == policy) {
				throw new BadRequestException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyIds[i]}, locale, m_Base);
			}
			//check if the policy is a "smart" one - this interface is only for non-smart policies
			byte[] filterCriteria = policy.getFilterCriteria();
			if (filterCriteria != null && filterCriteria.length > 0) {
				throw new BadRequestException("POLICYMOBILEDEVICE_POLICY_IS_SMART_AND_CANNOT_BE_ASSIGNED_TO_USING_THIS_METHOD", new Object[]{policyIds[i]}, locale, m_Base);
			}
			policyIdsAsList.add(policyIds[i]);
			policyNames[i] = policy.getName();
		}
		
		policyUniqueIds = dal.getPolicyHandler().getPolicyUniqueIdsAsString(policyIdsAsList);
		
		if (policyUniqueIds == null || policyUniqueIds.length != policyIds.length) {
			throw new BadRequestException("POLICYMOBILEDEVICE_INCORRECT_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			for (int i = 0; i < policyUniqueIds.length; i++) {
				MDC.put("policyUniqueId", policyUniqueIds[i]);
							
				CobraAdminMiscDatabaseCommand assignDevicesToPolicyCommand = CommandFactory.createAssignDevicesToPolicyCommand(
						deviceUuids,
						UUID.fromString(policyUniqueIds[i]),
						SessionState.getAdminUUID(m_servletRequest.getSession())
						);
				
				contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"POLICYMOBILEDEVICE_ASSIGN_DEVICES_TO_POLICY_FAILED", 
						new String[]{policyNames[i]}, 
						locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
						assignDevicesToPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);				
			}
			MDC.remove("policyUniqueId");
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}

		MDC.remove("mappings");
	}
	
	
	/**
	 * <p>Remove one or more relationships between policies and mobile devices. 
	 *    The most common use cases are; remove one mobile device from multiple policies, remove multiple mobile devices from one policy.
	 *    In both cases, the body would be a list of deviceId/policyId tuples.</p>
	 *    
	 * <p>Example body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;{"deviceId":1, "policyId":9}, 
	 * &emsp;{"deviceId":2, "policyId":10} ]
	 *}
	 * </pre>
	 * 
	 * <p>Implementation Note: The AM protocol command to the AM server consists of a device list and a single policy. 
	 *    Operations will need to be mapped to this format and grouped for efficiency.</p>   
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
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or content do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deleteMobileDevicesFromPolicies(MobileDeviceIdPolicyIdMappingAssociation mappings) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;
		
		if (mappings == null) {
			throw new BadRequestException("POLICYMOBILEDEVICE_NO_POLICY_DEVICE_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("mappings", mappings.toString());
		
		ArrayList<String> mobileDeviceUUIDs = new ArrayList<String>();
		ArrayList<String> policyUUIDs = new ArrayList<String>();
		ArrayList<String> policyNames = new ArrayList<String>();
		IDal dal = Application.getDal(session);

		for (int i = 0; i < mappings.getAssociations().length; i++) {
			
			Long deviceId = mappings.getAssociations()[i].getDeviceId();
			IPhoneInfo device = dal.getDeviceHandler().getDevice(deviceId);
			
			if (device == null) {
				throw new BadRequestException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
			}
			Long policyId = mappings.getAssociations()[i].getPolicyId();
			iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyId);
			if (policy == null) {
				throw new BadRequestException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
			}
			//check if the policy is a "smart" one - this interface is only for non-smart policies
			byte[] filterCriteria = policy.getFilterCriteria();
			if (filterCriteria != null && filterCriteria.length > 0) {
				throw new BadRequestException("POLICYMOBILEDEVICE_POLICY_IS_SMART_AND_CANNOT_BE_ASSIGNED_TO_USING_THIS_METHOD", new Object[]{policyId}, locale, m_Base);
			}
			
			mobileDeviceUUIDs.add(device.getUniqueId());			
			policyUUIDs.add(policy.getUniqueId());
			policyNames.add(policy.getName());
		}

		ArrayList<String> policyIdsProcessed = new ArrayList<String>();
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			for (int i = 0; i < policyUUIDs.size(); i++) {
				String policyUniqueId = policyUUIDs.get(i);
				if (policyIdsProcessed.contains(policyUniqueId)) {
					continue;
				}
				ArrayList<UUID> mobileDeviceUUIDsforPolicy = new ArrayList<UUID>();
				for (int j = i; j < policyUUIDs.size(); j++) {
					if (policyUniqueId.compareTo(policyUUIDs.get(j)) == 0) {
						mobileDeviceUUIDsforPolicy.add(UUID.fromString(mobileDeviceUUIDs.get(j)));
					}
				}
				policyIdsProcessed.add(policyUniqueId);
	
				CobraAdminMiscDatabaseCommand removeMobileDevicesFromPolicyCommand = CommandFactory.createRemoveDevicesFromPolicyCommand(
						mobileDeviceUUIDsforPolicy.toArray(new UUID[mobileDeviceUUIDsforPolicy.size()]),
						UUID.fromString(policyUniqueId), 
						SessionState.getAdminUUID(session));
				
				contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"POLICYMOBILEDEVICE_REMOVE_DEVICES_FROM_POLICY_FAILED", 
						new String[]{policyNames.get(i)}, 
						locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeMobileDevicesFromPolicyCommand, contextMessage);

				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);								
			}
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		
		MDC.remove("mappings");
	}
}
