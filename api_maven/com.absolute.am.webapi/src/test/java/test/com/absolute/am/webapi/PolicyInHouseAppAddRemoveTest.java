package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.InhouseApplications;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

public class PolicyInHouseAppAddRemoveTest extends LoggedInTest {
	
	private static String[] policyIds;
	private static String[] inHouseAppIds;
	private static String inHouseAppId;
	private static int assignmentType1 = 2;	//kCobra_iOS_Policy_AppState_OnDemand
	private static int assignmentType2 = 0;	//kCobra_iOS_Policy_AppState_Forbidden
	
	private static final String NON_EXISTING_ITEM_ID = "9999";
	
	@Test
	@Category(com.absolute.util.helper.DeviceDependentTest.class)
	public void master_test() throws Exception {
		setup();
		test_cannot_add_inhouseapp_to_policy_with_non_existing_inhouseapp_id();
		test_cannot_add_inhouseapp_to_policy_with_non_existing_policy_id();
		test_can_add_inhouseapp_to_multiple_policies();
		test_can_remove_inhouseapps_from_multiple_policies();
	}

	public void setup() throws Exception {
		policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);
		inHouseAppIds = Helpers.getInHouseAppIdsForInHouseAppNames(logonCookie, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3]);
		inHouseAppId = inHouseAppIds[0];
	}
	
	public void test_cannot_add_inhouseapp_to_policy_with_non_existing_inhouseapp_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"inHouseAppIds\":");
		sb.append("[" + NON_EXISTING_ITEM_ID + "]");
		sb.append(",\"policyAssignments\":");
		sb.append("[{");
		sb.append("\"policyId\":");
		sb.append(policyIds[0]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType1);
		sb.append("},");
		sb.append("{\"policyId\":");
		sb.append(policyIds[1]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType2);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_INHOUSEAPP_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	public void test_cannot_add_inhouseapp_to_policy_with_non_existing_policy_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"inHouseAppIds\":");
		sb.append("[" + inHouseAppId + "]");
		sb.append(",\"policyAssignments\":");
		sb.append("[{");
		sb.append("\"policyId\":");
		sb.append(policyIds[0]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType1);
		sb.append("},");
		sb.append("{\"policyId\":");
		sb.append(NON_EXISTING_ITEM_ID);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType2);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_INHOUSEAPP_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void test_can_add_inhouseapp_to_multiple_policies() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"inHouseAppIds\":");
		sb.append("[" + inHouseAppId + "]");
		sb.append(",\"policyAssignments\":");
		sb.append("[{");
		sb.append("\"policyId\":");
		sb.append(policyIds[0]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType1);
		sb.append("},");
		sb.append("{\"policyId\":");
		sb.append(policyIds[1]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType2);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_INHOUSEAPP_API, 
				sb.toString());
		
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/inhouseapps?$search=" + InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, inHouseAppId, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3]);
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/inhouseapps?$search=" + InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, inHouseAppId, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3]);
	}
	
	public void test_can_remove_inhouseapps_from_multiple_policies() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"associations\":");
		sb.append("[{");
		sb.append("\"inHouseAppId\":");
		sb.append(inHouseAppId);
		sb.append(",\"policyId\":");
		sb.append(policyIds[0]);
		sb.append("},");
		sb.append("{\"inHouseAppId\":");
		sb.append(inHouseAppId);
		sb.append(",\"policyId\":");
		sb.append(policyIds[1]);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_INHOUSEAPP_API + "/delete", 
				sb.toString());
		
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/inhouseapps?$search=" + InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3], 200, 200);
		Helpers.check_for_empty_resultset(viewResult);
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/inhouseapps?$search=" + InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3], 200, 200);
		Helpers.check_for_empty_resultset(viewResult);
	}
}