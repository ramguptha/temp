package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.webapi.util.ResourceUtilities;

import test.com.absolute.testdata.configuration.Actions;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

/**
 * @author rchen
 *
 */

public class PolicyActionsTest extends LoggedInTest {

	private static final String NONEXISTING_POLICY_ID = "123456789";
	private static final String NONEXISTING_ACTION_ID = "123456789";
	
	private String[] policyIds;
	private String[] actionIds;

	public void setup() throws Exception {
		policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.SMART_POLICY_NAMES[1], Policies.SMART_POLICY_NAMES[2]);
		actionIds = Helpers.getActionIdsForActionNames(logonCookie, Actions.ACTION_NAMES[0], Actions.ACTION_NAMES[1]);
		
		Assert.assertTrue("The policies '" + Policies.SMART_POLICY_NAMES[1] + "' and '" + Policies.SMART_POLICY_NAMES[2] + "' should be existing in the system.", policyIds.length == 2);
		Assert.assertTrue("The actions '" + Actions.ACTION_NAMES[0] + "' and '" + Actions.ACTION_NAMES[1] + "'should be existing in the system.", actionIds.length == 2);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		setup();

		// tests for assigning actions to policies
		// test#1: assign multiple actions to one policy
		test_can_assign_actions_to_policies(actionIds, new String[] {policyIds[0]});
		// test#2: assign one action to multiple policy
		test_can_assign_actions_to_policies(new String[] {actionIds[0]}, policyIds);
		// test#3: assign multiple actions to multiple policies
		test_can_assign_actions_to_policies(actionIds, policyIds);
		
		test_cannot_assign_actions_to_policies_when_non_existing_action_id_included();
		test_cannot_assign_actions_to_policies_when_non_existing_policy_id_included();
		
		// tests for removing actions from the policies
		test_can_remove_actions_from_policies();
		test_cannot_remove_actions_from_policies_when_non_existing_action_id_included();
		test_cannot_remove_actions_from_policies_when_non_existing_policy_id_included();
	}
	
	private void test_can_assign_actions_to_policies(String[] actionIds, String[] policyIds) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":" + Arrays.toString(actionIds) + ",");
		sb.append("\"policyIds\":" + Arrays.toString(policyIds) + ",");
		sb.append("\"initialDelay\":3600,");
		sb.append("\"repeatInterval\":5400,");
		sb.append("\"repeatCount\":1}");
		// 
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(Actions.ACTION_NAMES[0]);
		if (actionIds.length == 2) {
			actionNames.add(Actions.ACTION_NAMES[1]);
		}
		
		// Add the action/policy relationship by calling the 'api/policiy_actions' endpoint
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API, sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		// check if the actions has been assigned to the policies successfully
		for(String policyId : policyIds) {
			String[] actionIds2 = Helpers.getActionIdsForActionNamesAndPolicyId(logonCookie, policyId, 
					actionNames.toArray(new String[actionNames.size()]));
			
			org.junit.Assert.assertTrue(actionIds.length == actionIds.length);
			for (String actionId : actionIds) {
				org.junit.Assert.assertTrue(Arrays.asList(actionIds2).contains(actionId));
			}
		}		
	}
	
	private void test_cannot_assign_actions_to_policies_when_non_existing_action_id_included() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("POLICYACTION_ONE_OR_MORE_ACTION_IDS_ARE_INVALID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_ACTION_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":[" + actionIds[0] + "," + NONEXISTING_ACTION_ID + "],");
		sb.append("\"policyIds\":" + Arrays.toString(policyIds) + ",");
		sb.append("\"initialDelay\":3600,");
		sb.append("\"repeatInterval\":5400,");
		sb.append("\"repeatCount\":1}");

		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API, sb.toString(), 
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		// check if the returned error message matches the error message setting
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_cannot_assign_actions_to_policies_when_non_existing_policy_id_included() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("ONE_OR_MORE_POLICY_IDS_ARE_INVALID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_POLICY_ID);		
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":" + Arrays.toString(actionIds) + ",");
		sb.append("\"policyIds\":[" + policyIds[0] + "," + NONEXISTING_POLICY_ID + "],");
		sb.append("\"initialDelay\":3600,");
		sb.append("\"repeatInterval\":5400,");
		sb.append("\"repeatCount\":1}");

		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API, sb.toString(), 
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		// check if the returned error message matches the error message setting
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_can_remove_actions_from_policies() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":" + Arrays.toString(actionIds) + ",");
		sb.append("\"policyIds\":" + Arrays.toString(policyIds) + "}");
		// Add the action/policy relationship by calling the 'api/policiy_actions' endpoint
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API + "/delete", sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		// check that the actions have been removed successfully
		for(String policyId : policyIds) {
			try {
				Helpers.getActionIdsForActionNamesAndPolicyId(logonCookie, policyId, 
						Actions.ACTION_NAMES[0], Actions.ACTION_NAMES[1]);
			} catch(RuntimeException e){
				assertTrue(e.getMessage().startsWith("ID not found for action register user test."));
			}
		}	
	}
	
	private void test_cannot_remove_actions_from_policies_when_non_existing_action_id_included() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("POLICYACTION_ONE_OR_MORE_ACTION_IDS_ARE_INVALID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_ACTION_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":[" + actionIds[0] + "," + NONEXISTING_ACTION_ID + "],");
		sb.append("\"policyIds\":" + Arrays.toString(policyIds) + "}");
		// Add the action/policy relationship by calling the 'api/policiy_actions' endpoint
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API + "/delete", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);

		// check if the returned error message matches the error message setting
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_cannot_remove_actions_from_policies_when_non_existing_policy_id_included() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("ONE_OR_MORE_POLICY_IDS_ARE_INVALID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_POLICY_ID);	
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionIds\":" + Arrays.toString(actionIds) + ",");
		sb.append("\"policyIds\":[" + policyIds[0] + "," + NONEXISTING_POLICY_ID + "]}");
		// Add the action/policy relationship by calling the 'api/policiy_actions' endpoint
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIY_ACTIONS_API + "/delete", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);

		// check if the returned error message matches the error message setting 
		Assert.assertTrue(response.contains(expectedErrorMessage));	
	}
}
