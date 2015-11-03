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
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.am.model.policyinhouseapp.InHouseAppIdPolicyIdMappingArray;
import com.absolute.am.model.policyinhouseapp.InHouseAppIdsForPolicyAssignments;
import com.absolute.am.model.policyinhouseapp.InHouseAppIdPolicyIdMapping;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.ArrayUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Policy In-house Application Assignment API</h3>
 * <p>This endpoint is used to assign in-house applications to policies, effectively creating a relationship
 *    between one or more policies and one or more in-house applications.</p>
 *    
 */
@Path ("/policy_inhouseapp")
public class PolicyInHouseApp {

	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyInHouseApp.class.getName()); 
	
    
	/**
	 * <p>This request is used to create <span style="text-decoration: underline;">or update</span> a relationship between policies and in-house application.</p>
	 * <p>Sample request bodies are shown below.</p>
	 *    
	 * <p>Example (A) assign one in-house application to multiple policies:</p>   
	 * <pre>
	 *{
	 * &emsp;"inHouseAppIds":[7],
	 * &emsp;"policyAssignments":[
	 * &emsp;	{"policyId":50,
	 * &emsp;	"assignmentType":0},
	 * &emsp;	{"policyId":51,
	 * &emsp;	"assignmentType":2}]
	 *}
	 * </pre>
	 * 
	 * <p>Example (B), assign multiple in-house applications to one policy:</p>   
	 * <pre>
	 *{
	 * &emsp;"inHouseAppIds":[7,9],
	 * &emsp;"policyAssignments":[
	 * &emsp;	{"policyId":50,
	 * &emsp;	"assignmentType":0}]
	 *}
	 * </pre>
	 * 
	 * <p>"assignmentType" enum can have the following values:</p>   
	 * <p>1 - auto-install<br/>
	 * 2 - on demand<br/>
	 * 3 - auto-install, auto-remove<br/>
	 * 4 - on demand, auto-remove<br/>
	 * 0 - forbidden</p>   
	 * 
	 * <p>You can also use this API to assign multiple in-house applications to multiple policies. 
	 *    Each application provided, is assigned to each of the policies provided.</p>   
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies</p>
	 *    
	 * @param assignments InHouseAppIdsForPolicyAssignments
	 * @return
	 * @throws Exception 
	 */
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If any of the policies or in-house applications do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void addInHouseAppToPolicy(InHouseAppIdsForPolicyAssignments assignments) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (assignments == null) {
			throw new BadRequestException("POLICYINHOUSEAPP_NO_INHOUSEAPP_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("assignments", assignments.toString());
		
		long[] inHouseAppIds = assignments.getInHouseAppIds();
		if (inHouseAppIds == null || inHouseAppIds.length == 0) {
			throw new BadRequestException("POLICYINHOUSEAPP_NO_INHOUSEAPP_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
				
		List<Long> inHouseAppIdsAsList = ArrayUtilities.<Long>toList(inHouseAppIds);

		PolicyAssignment[] policyAssignments = assignments.getPolicyAssignments();
		if (policyAssignments == null || policyAssignments.length == 0) {
			throw new BadRequestException("NO_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		UUID[] inHouseAppUUIDArr = null;
		String[] policyUniqueIds = null;		
		IDal dal = Application.getDal(session);

		inHouseAppUUIDArr = dal.getApplicationsHandler().getInHouseAppUniqueIds(inHouseAppIdsAsList);

		if (inHouseAppUUIDArr == null || inHouseAppUUIDArr.length != inHouseAppIds.length) {
			throw new BadRequestException("POLICYINHOUSEAPP_ONE_OR_MORE_INHOUSEAPP_IDS_ARE_INVALID", null, locale, m_Base);
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
						String.format("Policy [%1$s] uniqueID=%2$s assignmentType=%3$s.", 
						i,
						policyUniqueIds[i],
						policyAssignments[i].getAssignmentType()));
							
				CobraAdminMiscDatabaseCommand assignToPolicyCommand = 
					CommandFactory.createAssignInHouseAppToPolicyCommand(
						inHouseAppUUIDArr, 
						UUID.fromString(policyUniqueIds[i]),
						policyAssignments[i].getAssignmentType(),
						SessionState.getAdminUUID(session)
						);
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYINHOUSEAPP_ASSIGN_INHOUSEAPP_TO_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(assignToPolicyCommand, contextMessage);
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

		MDC.remove("policyAssignments");
		MDC.remove("assignments");		
	}
	
	/**
	 * <p>Removes one or more policy/in-house application relationships. The most common use cases are; remove one application from multiple policies,
	 *    remove multiple applications from one policy. In both cases, the body would be a list of inHouseAppId/policyId tuples.</p>
	 *    
	 * <p>Example body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;     {"inHouseAppId":1, "policyId":9}, 
	 * &emsp;     {"inHouseAppId":2, "policyId":10} ]
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies</p>
	 *    
	 * @param mappings InHouseAppIdPolicyIdMappingArray
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or applications do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void removeInHouseAppFromPolicy(InHouseAppIdPolicyIdMappingArray mappings) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (mappings == null) {
			throw new BadRequestException("POLICYINHOUSEAPP_NO_INHOUSEAPP_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("mappings", mappings.toString());

		InHouseAppIdPolicyIdMapping[] associations = mappings.getAssociations();
		if (associations == null || associations.length == 0) {
			throw new BadRequestException("POLICYINHOUSEAPP_NO_INHOUSEAPP_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		ArrayList<String> inHouseAppUUIDs = new ArrayList<String>();
		ArrayList<String> policyUUIDs = new ArrayList<String>();

		IDal dal = Application.getDal(session);

		for (int i = 0; i < associations.length; i++) {
			Long inHouseAppId = associations[i].getInHouseAppId();
			iOSApplications inHouseApp = dal.getApplicationsHandler().getInHouseApplication(inHouseAppId);
			if (inHouseApp == null) {
				throw new BadRequestException("POLICYINHOUSEAPP_NO_INHOUSEAPP_FOUND_FOR_ID", new Object[]{inHouseAppId}, locale, m_Base);
			}
			Long policyId = associations[i].getPolicyId();
			iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyId);
			if (policy == null) {
				throw new BadRequestException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
			}
			inHouseAppUUIDs.add(inHouseApp.getUniqueID());
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
				ArrayList<UUID> inHouseAppUUIDsforPolicy = new ArrayList<UUID>();
				for (int j = i; j < policyUUIDs.size(); j++) {
					if (policyUniqueId.compareTo(policyUUIDs.get(j)) == 0) {
						inHouseAppUUIDsforPolicy.add(UUID.fromString(inHouseAppUUIDs.get(j)));
					}
				}
				policyIdsProcessed.add(policyUniqueId);
	
				CobraAdminMiscDatabaseCommand removeInHouseAppsFromPolicyCommand = 
					CommandFactory.createRemoveInHouseAppFromPolicyCommand(
						inHouseAppUUIDsforPolicy.toArray(new UUID[inHouseAppUUIDsforPolicy.size()]),
						UUID.fromString(policyUniqueId), 
						SessionState.getAdminUUID(session));
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYINHOUSEAPP_REMOVE_INHOUSEAPP_FROM_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeInHouseAppsFromPolicyCommand, contextMessage);
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

