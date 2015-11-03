/**
 * 
 */
package com.absolute.am.webapi.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.IDal;
import com.absolute.am.model.policyaction.PolicyActionAssignment;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.ArrayUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * <h3>Policy Action Assignment API</h3>
 * <p>This endpoint is used to assign actions to smart policies, effectively creating a relationship between one or more smart policies and one or more actions.</p>
 * 
 * @author rchen
 *
 */
@Path ("/policy_actions")
public class PolicyActions {
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyActions.class.getName()); 
	
    /**
	 * <p>This request is used to create <span style="text-decoration: underline;">or update</span> a relationship between smart policies and actions.
	 *  You can use it to assign multiple actions to one policy (Example A), or add one action to multiple policies (Example B).
	 * </p>
	 * <p>Sample request bodies are shown below.</p>
	 *    
	 * <p>Example (A), assign multiple actions to one smart policy:</p>   
	 * <pre>
	 * {
	 * &emsp;"actionIds":[48,49],
	 * &emsp;"policyIds":[485],
	 * &emsp;"initialDelay":3600,
	 * &emsp;"repeatInterval":5400,
	 * &emsp;"repeatCount":1
	 * }
	 * 
	 * </pre>
	 * 
	 * <p>Example (B) assign one action to multiple smart policies:</p>   
	 * <pre>
	 * {
	 * &emsp;"actionIds":[50],
	 * &emsp;"policyIds":[350,351],
	 * &emsp;"initialDelay":3600,
	 * &emsp;"repeatInterval":5400,
	 * &emsp;"repeatCount":1
	 * }
	 * </pre>
	
	 * <p>You can also use this API to assign multiple actions to multiple smart policies. 
	 *    Each action provided is assigned to each of the policies provided.</p>   
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * @param assignment PolicyActionAssignment, which contains a list of action IDs, a list of the policy IDs, and the 
	 *  settings for the assignment, such as initial delay seconds, repeat interval seconds, and repeat count.
	 * @return
	 * @throws Exception 
	 */
    @POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If any of the policies or actions do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint")
		})
	public void assignActionToPolicy(PolicyActionAssignment assignment) throws Exception {
    	HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		MDC.put("assignments", assignment.toString());
		
		String[] actionUuids = getActionUuids(assignment.getActionIds());
		String[] policyUuids  = getPolicyUuids(assignment.getPolicyIds());
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			for (int i = 0; i < policyUuids.length; i++) {
				CobraAdminMiscDatabaseCommand assignActionsToPolicyCommand = CommandFactory.createAssignActionsToPolicyCommand(
					SessionState.getAdminUUID(session),
					actionUuids,
					assignment.getInitialDelay(),
					assignment.getRepeatInterval(),
					assignment.getRepeatCount(),
					policyUuids[i]
				);
				String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"POLICYACTION_ASSIGN_ACTIONS_TO_POLICY_FAILED", 
					null, 
					locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
						assignActionsToPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response);
			}
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		
		MDC.remove("assignment");	
    }
	
    /**
	 * <p>Removes one or more smart policy/action relationships. The most common use cases are:
	 *  <ul>
	 *  <li>Remove one action from multiple policies, or</li> 
	 *  <li>Remove multiple actions from one policy.</li> 
	 *  </ul>
	 *  </p>
	 *    
	 * <p>Example body:</p>   
	 * <pre>
	 *  {"actionIds":[37,33],"policyIds":[351,485]}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * @param associations The list of the action IDs, and the list of the policy IDs
	 * @return
	 * @throws Exception 
	 */
    @POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "If any of the policies or actions do not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint")
		})
	public void removeActionsFromPolicy(PolicyActionAssignment associations) throws Exception {
    	HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		MDC.put("assignments", associations.toString());
		
		String[] actionUuids = getActionUuids(associations.getActionIds());
		String[] policyUuids  = getPolicyUuids(associations.getPolicyIds());
	
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			for (int i = 0; i < policyUuids.length; i++) {
				CobraAdminMiscDatabaseCommand assignActionsToPolicyCommand = CommandFactory.createRemoveActionsFromPolicyCommand(
					SessionState.getAdminUUID(session),
					actionUuids,
					policyUuids[i]
				);
				String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"POLICYACTION_ASSIGN_ACTIONS_TO_POLICY_FAILED", 
					null, 
					locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
						assignActionsToPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response);
			}
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e, locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		
		MDC.remove("assignment");
    }
    
    private String[] getActionUuids(long[] actionIds) throws Exception{
    	HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		IDal dal = Application.getDal(session);
		
		if (actionIds == null || actionIds.length == 0) {
			throw new BadRequestException("POLICYACTION_NO_ACTION_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
    	
		String[] actionUuids = dal.getActionHandler().getActionUniqueIds(ArrayUtilities.<Long>toList(actionIds));
		if (actionUuids == null || actionUuids.length != actionIds.length) {
			throw new BadRequestException("POLICYACTION_ONE_OR_MORE_ACTION_IDS_ARE_INVALID", null, locale, m_Base);
		}
		
		return actionUuids;
    }
    
    private String[] getPolicyUuids(long[] policyIds) throws Exception{
    	HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		IDal dal = Application.getDal(session);
		
    	if (policyIds == null || policyIds.length == 0) {
			throw new BadRequestException("POLICYACTION_NO_POLICY_IDS_SUPPLIED_WITH_REQUEST", null, locale, m_Base);			
		}
    	
		String[] policyUuids = dal.getPolicyHandler().getPolicyUniqueIdsAsString(ArrayUtilities.<Long>toList(policyIds));
		if (policyUuids == null || policyUuids.length != policyIds.length) {
			throw new BadRequestException("ONE_OR_MORE_POLICY_IDS_ARE_INVALID", null, locale, m_Base);
		}
		
		return policyUuids;
    }

}
