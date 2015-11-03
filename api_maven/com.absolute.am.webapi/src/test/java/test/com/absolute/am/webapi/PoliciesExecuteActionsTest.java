package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.webapi.util.ResourceUtilities;

import test.com.absolute.testdata.configuration.Actions;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

public class PoliciesExecuteActionsTest extends LoggedInTest {
	
	private String actionUuid = "";
	private String policyUuid = "";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		setup();

		//tests for re-execute performed actions on the policies
		test_can_execute_actions_for_policies();
		test_cannot_execute_actions_for_empty_mapping_data();
	}
	
	public void setup() throws Exception {
		String[] policyUuids = Helpers.getPolicyUuidsForPolicyNames(logonCookie, Policies.SMART_POLICY_NAMES[0]);
		String[] actionUuids = Helpers.getActionUuidsForActionNames(logonCookie, Actions.ACTION_NAMES[0]);
		
		if (policyUuids != null & policyUuids.length > 0) {
			policyUuid = policyUuids[0];
		}
		if (actionUuids != null & actionUuids.length > 0) {
			actionUuid = actionUuids[0];
		}
		
		Assert.assertTrue("The smart policy '" + Policies.SMART_POLICY_NAMES[0] + " does not exist in the system.", policyUuid.length() > 0);
		Assert.assertTrue("The action '" + Actions.ACTION_NAMES[0] + " does not exist in the system.", actionUuid.length() > 0);
	}
	
	private void test_can_execute_actions_for_policies() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"policyUuidActionUuidMappings\":[");
		sb.append("{\"actionUuid\":\"" + actionUuid + "\",\"policyUuid\":\"" + policyUuid + "\"}");		
		sb.append("]");
		sb.append(",\"executeImmediately\":true}");
		
		// re-execute performed action from the device
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsonpolicies", sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	private void test_cannot_execute_actions_for_empty_mapping_data() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("POLICYACTION_NO_POLICY_ACTION_MAPPINGS_SUPPLIED_WITH_REQUEST", 
				null, locale, ResourceUtilities.WEBAPI_BASE));
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"policyUuidActionUuidMappings\":[");
		sb.append("]");
		sb.append(",\"executeImmediately\":true}");
		
		// re-execute performed action from the device
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsonpolicies", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		// check if the response body contain the expected error message
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
}
