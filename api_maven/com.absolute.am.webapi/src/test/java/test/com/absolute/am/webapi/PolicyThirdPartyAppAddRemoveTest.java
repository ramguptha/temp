package test.com.absolute.am.webapi;

import java.net.URLEncoder;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testdata.configuration.ThirdPartyApplications;
import test.com.absolute.testutil.Helpers;

public class PolicyThirdPartyAppAddRemoveTest extends LoggedInTest {
	
	private static String[] policyIds;
	private static String[] thirdPartyAppIds;
	private static String thirdPartyAppId;
	// both of these are on-demand since we don't have a VPP license for this app
	private static int assignmentType1 = 2;	//kCobra_iOS_Policy_AppState_OnDemand
	
	private static final String NON_EXISTING_ITEM_ID = "9999";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void master_test() throws Exception {
		setup();
		test_cannot_add_thirdpartyapp_to_policy_with_non_existing_thirdpartyapp_id();
		test_cannot_add_thirdpartyapp_to_policy_with_non_existing_policy_id();
		test_can_add_thirdpartyapp_to_multiple_policies();
		test_can_remove_thirdpartyapps_from_multiple_policies();
	}

	public void setup() throws Exception {
		policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);
		thirdPartyAppIds = Helpers.getThirdPartyAppIdsForThirdPartyAppNames(logonCookie, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0]);
		thirdPartyAppId = thirdPartyAppIds[0];
	}
	
	public void test_cannot_add_thirdpartyapp_to_policy_with_non_existing_thirdpartyapp_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"thirdPartyAppIds\":");
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
		sb.append(assignmentType1);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_THIRDPARTYAPP_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	public void test_cannot_add_thirdpartyapp_to_policy_with_non_existing_policy_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"thirdPartyAppIds\":");
		sb.append("[" + thirdPartyAppId + "]");
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
		sb.append(assignmentType1);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_THIRDPARTYAPP_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void test_can_add_thirdpartyapp_to_multiple_policies() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"thirdPartyAppIds\":");
		sb.append("[" + thirdPartyAppId + "]");
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
		sb.append(assignmentType1);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_THIRDPARTYAPP_API, 
				sb.toString());
		
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/thirdpartyapps?$search=" + URLEncoder.encode(ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0], "UTF-8"), 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, thirdPartyAppId, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0]);
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/thirdpartyapps?$search=" + URLEncoder.encode(ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0], "UTF-8"), 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, thirdPartyAppId, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0]);
	}
	
	public void test_can_remove_thirdpartyapps_from_multiple_policies() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"associations\":");
		sb.append("[{");
		sb.append("\"thirdPartyAppId\":");
		sb.append(thirdPartyAppId);
		sb.append(",\"policyId\":");
		sb.append(policyIds[0]);
		sb.append("},");
		sb.append("{\"thirdPartyAppId\":");
		sb.append(thirdPartyAppId);
		sb.append(",\"policyId\":");
		sb.append(policyIds[1]);
		sb.append("}]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_THIRDPARTYAPP_API + "/delete", sb.toString());
		
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/thirdpartyapps?$search=" + URLEncoder.encode(ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0], "UTF-8"), 200, 200);
		Helpers.check_for_empty_resultset(viewResult);
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/thirdpartyapps?$search=" + URLEncoder.encode(ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0], "UTF-8"), 200, 200);
		Helpers.check_for_empty_resultset(viewResult);
	}
}