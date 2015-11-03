/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
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
import com.absolute.am.dal.model.ConfigurationProfile;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.am.model.policyconfigurationprofile.ConfigurationProfileIdPolicyIdMapping;
import com.absolute.am.model.policyconfigurationprofile.ConfigurationProfileIdPolicyIdMappingArray;
import com.absolute.am.model.policyconfigurationprofile.ConfigurationProfileIdsForPolicyAssignments;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.ArrayUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * <h3>Policy Configuration Profile Assignment API</h3>
 * <p>This endpoint is used to assign configuration profiles to policies,
 *    effectively creating a relationship between one or more policies and one or more configuration profiles.</p>
 *    
 */
@Path ("/policy_configurationprofile")
public class PolicyConfigurationProfile {
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyConfigurationProfile.class.getName()); 
	
	/**
	 * <p>This request is used to create <span style="text-decoration: underline;">or update</span> a relationship between policies and configuration profiles.</p>
	 * <p>Sample request bodies are shown below. The objects in the policyAssignments array are the same as those used with /api/content/batch.
	 *    Please refer to that endpoint for details on the attributes.</p>
	 *    
	 * <p>Example (A) assign one configuration profile to multiple policies:</p>   
	 * <pre>
	 *{
	 * &emsp;"configurationProfileIds":[1],
	 * &emsp;"policyAssignments":[
	 * &emsp;                {"policyId": 9,
	 * &emsp;                "assignmentType": 1,
 	 * &emsp;                "availabilitySelector": 2,
	 * &emsp;                "startTime":"2012-10-18T19:01:00Z",
	 * &emsp;                "endTime":"2012-10-19T20:12:00Z"},
	 * &emsp;                {"policyId": 10,
	 * &emsp;                "assignmentType": 1,
	 * &emsp;                "availabilitySelector": 1,
	 * &emsp;                "startTime":"2013-10-18T19:01:00Z",
	 * &emsp;                "endTime":"2014-10-19T20:12:00Z"}]
	 *}
	 * </pre>
	 * 
	 * <p>Example (B), assign multiple configuration profiles to one policy:</p>   
	 * <pre>
	 *{
	 * &emsp;"configurationProfileIds":[1,2,3,4],
	 * &emsp;"policyAssignments":[
	 * &emsp;            {"policyId": 9,
	 * &emsp;            "assignmentType": 1,
	 * &emsp;            "availabilitySelector": 2,
	 * &emsp;            "startTime":"2012-10-18T19:01:00Z",
	 * &emsp;            "endTime":"2014-10-19T20:12:00Z"}]
	 *}
	 * </pre>
	 * 
	 * <p>This API can also be used to assign multiple configuration profiles to multiple policies. 
	 *    Each configuration profile is assigned to each of the policies provided.</p>   
	 *    
	 * <p>Note: assignmentType values for Configuration Profiles are different from those for Media:</br>
	 *    kCobra_iOS_Policy_ConfigProfile_Forbidden = 0;</br>
	 *    kCobra_iOS_Policy_ConfigProfile_Required = 1;</br>
	 *    kCobra_iOS_Policy_ConfigProfile_OnDemand = 2;</br>
	 *    kCobra_iOS_Policy_ConfigProfile_PolicyLocked = 3;</br>
	 *    kCobra_iOS_Policy_ConfigProfile_PolicyOptional = 4</br>
	 *    (as defined in iOSDevicesDefines)</p>   
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * @param assignments ConfigurationProfileIdsForPolicyAssignments
	 * @return
	 * @throws Exception 
	 */
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If policy or any of the configuration profiles do not exist (no data is updated)."),
		  @ResponseCode ( code = 404, condition = "The user is not authorized to access this endpoint.")
		})
	public void assignConfigurationProfileToPolicy(ConfigurationProfileIdsForPolicyAssignments assignments) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (assignments == null) {
			throw new BadRequestException("POLICYCONFIGPROFILE_NO_CONFIGPROFILE_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("assignments", assignments.toString());
		
		long[] configurationProfileIds = assignments.getConfigurationProfileIds();
		if (configurationProfileIds == null || configurationProfileIds.length == 0) {
			throw new BadRequestException("POLICYCONFIGPROFILE_NO_CONFIGURATION_PROFILE_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
				
		List<Long> configurationProfileIdsAsList = ArrayUtilities.<Long>toList(configurationProfileIds); 
		
		PolicyAssignment[] policyAssignments = assignments.getPolicyAssignments();
		if (policyAssignments == null || policyAssignments.length == 0) {
			throw new BadRequestException("NO_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		UUID[] configurationProfileUUIDArr = null;
		String[] policyUniqueIds = null;		
		IDal dal = Application.getDal(session);

		configurationProfileUUIDArr = dal.getConfigurationProfileHandler().getConfigurationProfileUniqueIds(configurationProfileIdsAsList);

		if (configurationProfileUUIDArr == null || configurationProfileUUIDArr.length != configurationProfileIds.length) {
			throw new BadRequestException("POLICYCONFIGPROFILE_ONE_OR_MORE_CONFIGURATION_PROFILE_IDS_ARE_INVALID", null, locale, m_Base);
		}

		List<Long> policyIdsAsList = new ArrayList<Long>();
		for (int i = 0; i < policyAssignments.length; i++) {
			policyIdsAsList.add((long)policyAssignments[i].getPolicyId());
		}
		
		policyUniqueIds = dal.getPolicyHandler().getPolicyUniqueIdsAsString(policyIdsAsList);
		if (policyUniqueIds == null || policyUniqueIds.length != policyAssignments.length) {
			throw new BadRequestException("ONE_OR_MORE_POLICY_IDS_ARE_INVALID", null, locale, m_Base);
		}

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			for (int i = 0; i < policyAssignments.length; i++) {
				MDC.put("policyAssignments",
						String.format("Policy [%1$s] uniqueID=%2$s assignmentType=%3$s availabilitySelector=%4$s startTime=%5$s endTime=%6$s.", 
						i,
						policyUniqueIds[i],
						policyAssignments[i].getAssignmentType(),
						policyAssignments[i].getAvailabilitySelector(),
						policyAssignments[i].getStartTime(),
						policyAssignments[i].getEndTime()));
							
				CobraAdminMiscDatabaseCommand assignToPolicyCommand = 
					CommandFactory.createAssignConfigurationProfileToPolicyCommand(
						configurationProfileUUIDArr, 
						UUID.fromString(policyUniqueIds[i]),
						policyAssignments[i].getAssignmentType(),
						SessionState.getAdminUUID(session)
						);
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYCONFIGPROFILE_ASSIGN_CONFIGURATION_PROFILE_TO_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(assignToPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);

	
				// TODO: If the AvailabilitySelector is the default value, then we don't need to do anything here.
				CobraAdminMiscDatabaseCommand setAvailabilityTimeCommand = CommandFactory.createSetAvailabilityTimeForPolicyConfigProfileCommand(
						configurationProfileUUIDArr, 
						UUID.fromString(policyUniqueIds[i]),
						policyAssignments[i].getAvailabilitySelector(),
						policyAssignments[i].getStartTime(),
						policyAssignments[i].getEndTime(),						
						SessionState.getAdminUUID(session)
						);
					
				//m_logger.debug("Set availability command={}", setAvailabilityTimeCommand.ToXml());
				contextMessage = ResourceUtilities.getResourceStringForLocale("SET_AVAILABILITY_TIME_FAILED", m_Base, locale);
				PropertyList setAvailabilityTimeResult = amServerProtocol.sendCommandAndValidateResponse(setAvailabilityTimeCommand, contextMessage);
//				m_logger.debug("Set availability time result={}",  setAvailabilityTimeResult.toXMLString());
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						setAvailabilityTimeResult);
								
			}
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}

		MDC.remove("policyAssignments");
		MDC.remove("assignments");		
	}
	
	/**
	 * <p>Removes one or more policy/configuration profile relationships. 
	 *    The most common use cases are; remove one configuration profile from multiple policies, remove multiple configuration profiles from one policy.
	 *     In both cases, the body would be a list of configurationProfileId/policyId tuples.</p>
	 *    
	 * <p>Example body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;{"configurationProfileId":1, "policyId":9}, 
	 * &emsp;{"configurationProfileId":2, "policyId":10} ]
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 *    
	 * @param mappings ConfigurationProfileIdPolicyIdMappingArray
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or configuration profiles do not exist (no data is updated)."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void removeConfigurationProfileFromPolicy(ConfigurationProfileIdPolicyIdMappingArray mappings) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (mappings == null) {
			throw new BadRequestException("POLICYCONFIGPROFILE_NO_CONFIGPROFILE_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("mappings", mappings.toString());

		ConfigurationProfileIdPolicyIdMapping[] associations = mappings.getAssociations();
		if (associations == null || associations.length == 0) {
			throw new BadRequestException("POLICYCONFIGPROFILE_NO_CONFIGPROFILE_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		ArrayList<String> configurationProfileUUIDs = new ArrayList<String>();
		ArrayList<String> policyUUIDs = new ArrayList<String>();

		IDal dal = Application.getDal(session);

		for (int i = 0; i < associations.length; i++) {
			Long configurationProfileId = associations[i].getConfigurationProfileId();
			ConfigurationProfile configurationProfile = dal.getConfigurationProfileHandler().getConfigurationProfile(configurationProfileId);
			if (configurationProfile == null) {
				throw new BadRequestException("POLICYCONFIGPROFILE_NO_CONFIGURATION_PROFILE_FOUND_FOR_ID", new Object[]{configurationProfileId}, locale, m_Base);
			}
			Long policyId = associations[i].getPolicyId();
			iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyId);
			if (policy == null) {
				throw new BadRequestException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
			}
			configurationProfileUUIDs.add(configurationProfile.getUniqueId());
			policyUUIDs.add(policy.getUniqueId());
		}
		
		ArrayList<String> policyIdsProcessed = new ArrayList<String>();

		AMServerProtocol amServerProtocol = new AMServerProtocol(
				SessionState.getAMServerProtocolSettings(session));
		
		try {			
			for (int i = 0; i < policyUUIDs.size(); i++) {
				
				String policyUniqueId = policyUUIDs.get(i);
				if (policyIdsProcessed.contains(policyUniqueId)) {
					continue;
				}
				ArrayList<UUID> configurationProfileUUIDsforPolicy = new ArrayList<UUID>();
				for (int j = i; j < policyUUIDs.size(); j++) {
					if (policyUniqueId.compareTo(policyUUIDs.get(j)) == 0) {
						configurationProfileUUIDsforPolicy.add(UUID.fromString(configurationProfileUUIDs.get(j)));
					}
				}
				policyIdsProcessed.add(policyUniqueId);
	
				CobraAdminMiscDatabaseCommand removeConfigurationProfileFromPolicyCommand = 
					CommandFactory.createRemoveConfigurationProfileFromPolicyCommand(
						configurationProfileUUIDsforPolicy.toArray(new UUID[configurationProfileUUIDsforPolicy.size()]),
						UUID.fromString(policyUniqueId), 
						SessionState.getAdminUUID(session));
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYCONFIGPROFILE_REMOVE_CONFIGURATION_PROFILE_FROM_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeConfigurationProfileFromPolicyCommand, contextMessage);
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

