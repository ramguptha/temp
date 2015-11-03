/**
 * 
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
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.am.model.policycontent.ContentIdPolicyIdMapping;
import com.absolute.am.model.policycontent.ContentIdPolicyIdMappingArray;
import com.absolute.am.model.policycontent.ContentIdsForPolicyAssignments;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.ArrayUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * <h3>Policy Content Assignment API</h3>
 * <p>This endpoint is used to assign content to policies, effectively creating a relationship between one or more policies and one or more content files.</p>
 * 
 * @author klavin
 *
 */
@Path ("/policy_content")
public class PolicyContent {
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyContent.class.getName()); 
	
	/**
	 * <p>This request is used to create <span style="text-decoration: underline;">or update</span> a relationship between policies and content.</p>
	 * <p>Sample request bodies are shown below. The objects in the policyAssignments array are the same as those used with /api/content/batch.
	 *    Please refer to that endpoint for details on the attributes.</p>
	 *    
	 * <p>Example (A) assign one file to multiple policies:</p>   
	 * <pre>
	 *{
	 * &emsp;"contentIds":[1],
	 * &emsp;"policyAssignments":[
	 * &emsp;               {"policyId": 9,
	 * &emsp;               "assignmentType": 1,
	 * &emsp;               "availabilitySelector": 2,
	 * &emsp;               "startTime":"2012-10-18T19:01:00Z",
	 * &emsp;                "endTime":"2012-10-19T20:12:00Z"},
	 * &emsp;                {"policyId": 10,
	 * &emsp;                "assignmentType": 1,
	 * &emsp;                "availabilitySelector": 1,
	 * &emsp;                "startTime":"19:01",
	 * &emsp;                "endTime":"20:12"}]
	 *}
	 * </pre>
	 * 
	 * <p>Example (B), assign multiple files to one policy:</p>   
	 * <pre>
	 *{
	 * &emsp;"contentIds":[1,2,3,4],
	 * &emsp;"policyAssignments":[
	 * &emsp;               {"policyId": 9,
	 * &emsp;               "assignmentType": 1,
	 * &emsp;               "availabilitySelector": 2,
	 * &emsp;               "startTime":"2012-10-18T19:01:00Z",
	 * &emsp;               "endTime":"2012-10-19T20:12:00Z"}]
	 *}
	 * 
	 * </pre>
	 * 
	 * <p>You can also use this API to assign multiple files to multiple policies. 
	 *    Each file provided, is assigned to each of the policies provided.</p>   
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * @param assignments ContentIdsForPolicyAssignments
	 * @return
	 * @throws Exception 
	 */
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If any of the policies or media do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint")
		})
	public void assignContentToPolicy(ContentIdsForPolicyAssignments assignments) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (assignments == null) {
			throw new BadRequestException("POLICYCONTENT_NO_CONTENT_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("assignments", assignments.toString());
		
		long[] contentIds = assignments.getContentIds();
		if (contentIds == null || contentIds.length == 0) {
			throw new BadRequestException("POLICYCONTENT_NO_CONTENT_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
				
		List<Long> contentIdsAsList = ArrayUtilities.<Long>toList(contentIds);

		PolicyAssignment[] policyAssignments = assignments.getPolicyAssignments();
		if (policyAssignments == null || policyAssignments.length == 0) {
			throw new BadRequestException("NO_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		UUID[] mediaUUIDArr = null;
		String[] policyUniqueIds = null;		
		IDal dal = Application.getDal(session);

		mediaUUIDArr = dal.getContentHandler().getMediaUniqueIds(contentIdsAsList);

		if (mediaUUIDArr == null || mediaUUIDArr.length != contentIds.length) {
			throw new BadRequestException("POLICYCONTENT_ONE_OR_MORE_CONTENT_IDS_ARE_INVALID", null, locale, m_Base);
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
							
				CobraAdminMiscDatabaseCommand assignToPolicyCommand = CommandFactory.createAssignMediaToPolicyCommand(
						mediaUUIDArr, 
						UUID.fromString(policyUniqueIds[i]),
						policyAssignments[i].getAssignmentType(),
						SessionState.getAdminUUID(session)
						);
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYCONTENT_ASSIGN_CONTENT_TO_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(assignToPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);

	
				// TODO: If the AvailabilitySelector is the default value, then we don't need to do anything here.
				CobraAdminMiscDatabaseCommand setAvailabilityTimeCommand = CommandFactory.createSetAvailabilityTimeForPolicyMediaCommand(
						mediaUUIDArr, 
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
	 * <p>Removes one or more policy/content relationships. The most common use cases are;
	 *  remove one file from multiple policies, remove multiple files from one policy. 
	 *  In both cases, the body would be a list of contentId/policyId tuples.</p>
	 *    
	 * <p>Example body:</p>   
	 * <pre>
	 *{
	 * &emsp;"associations":[
	 * &emsp;{"contentId":1, "policyId":9}, 
	 * &emsp;{"contentId":2, "policyId":10} ]
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * @param mappings ContentIdPolicyIdMappingArray
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "One more of the policies or content do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint")
		})
	public void deleteContentFromPolicy(ContentIdPolicyIdMappingArray mappings) throws Exception {
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String contextMessage;

		if (mappings == null) {
			throw new BadRequestException("POLICYCONTENT_NO_CONTENT_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		MDC.put("mappings", mappings.toString());

		ContentIdPolicyIdMapping[] associations = mappings.getAssociations();
		if (associations == null || associations.length == 0) {
			throw new BadRequestException("POLICYCONTENT_NO_CONTENT_POLICY_MAPPINGS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}

		ArrayList<String> mediaUUIDs = new ArrayList<String>();
		ArrayList<String> policyUUIDs = new ArrayList<String>();

		IDal dal = Application.getDal(session);

		for (int i = 0; i < associations.length; i++) {
			Long contentId = associations[i].getContentId();
			MobileMedia mobileMedia = dal.getContentHandler().getContent(contentId);
			if (mobileMedia == null) {
				throw new BadRequestException("POLICYCONTENT_NO_CONTENT_FOUND_FOR_ID", new Object[]{contentId}, locale, m_Base);
			}
			Long policyId = associations[i].getPolicyId();
			iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyId);
			if (policy == null) {
				throw new BadRequestException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
			}
			mediaUUIDs.add(mobileMedia.getUniqueId());
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
				ArrayList<UUID> mediaUUIDsforPolicy = new ArrayList<UUID>();
				for (int j = i; j < policyUUIDs.size(); j++) {
					if (policyUniqueId.compareTo(policyUUIDs.get(j)) == 0) {
						mediaUUIDsforPolicy.add(UUID.fromString(mediaUUIDs.get(j)));
					}
				}
				policyIdsProcessed.add(policyUniqueId);
	
				CobraAdminMiscDatabaseCommand removeMediaCommand = CommandFactory.createRemoveMediaFromPolicyCommand(
						mediaUUIDsforPolicy.toArray(new UUID[mediaUUIDsforPolicy.size()]),
						UUID.fromString(policyUniqueId), 
						SessionState.getAdminUUID(session));
				
				contextMessage = ResourceUtilities.getResourceStringForLocale("POLICYCONTENT_REMOVE_CONTENT_FROM_POLICY_FAILED", m_Base, locale);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeMediaCommand, contextMessage);
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
