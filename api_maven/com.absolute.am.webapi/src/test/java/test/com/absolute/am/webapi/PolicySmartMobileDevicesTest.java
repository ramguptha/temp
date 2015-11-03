package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class PolicySmartMobileDevicesTest extends LoggedInTest{
	
	private static final String SMART_POLICY_TEST_54321 = "WebApiSmartPolicy_Test54321";
	private static final String SMART_POLICY_TEST_54321_UPDATE = "WebApiSmartPolicy_Test54321Update";

	private static final String SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321 = "WebApiSmartPolicyByIA_SomeMissing_Test54321";
	private static final String SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321_UPDATE = "WebApiSmartPolicyByIA_SomeMissing_Test54321Update";

	private static final String SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321 = "WebApiSmartPolicyByIA_SomeInstalled_Test54321";
	private static final String SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321_UPDATE = "WebApiSmartPolicyByIA_SomeInstalled_Test54321Update";

	private static final String SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321 = "WebApiSmartPolicyByIA_AllMissing_Test54321";
	private static final String SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321_UPDATE = "WebApiSmartPolicyByIA_AllMissing_Test54321Update";

	private static final String SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321 = "WebApiSmartPolicyByIA_AllInstalled_Test54321";
	private static final String SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321_UPDATE = "WebApiSmartPolicyByIA_AllInstalled_Test54321Update";

	private static final String SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321 = "WebApiSmartPolicyByICP_SomeMissing_Test54321";
	private static final String SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321_UPDATE = "WebApiSmartPolicyByICP_SomeMissing_Test54321Update";

	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_create_delete_update_smart_policy() throws Exception {
		test_can_create_smart_policy();
//		test_can_update_smart_policy();
//		test_can_delete_smart_policy();
	}
	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_create_delete_update_smart_policy_by_IA_some_missing() throws Exception {
		test_can_create_smart_policy_by_IA_some_missing();
		test_can_update_smart_policy_by_IA_some_missing();
		test_can_delete_smart_policy_by_IA_some_missing();
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_create_delete_update_smart_policy_by_IA_some_installed() throws Exception {
		test_can_create_smart_policy_by_IA_some_installed();
		test_can_update_smart_policy_by_IA_some_installed(); // Not Implemented
		test_can_delete_smart_policy_by_IA_some_installed();
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_create_delete_update_smart_policy_by_IA_all_missing() throws Exception {
		test_can_create_smart_policy_by_IA_all_missing();
		test_can_update_smart_policy_by_IA_all_missing();
		test_can_delete_smart_policy_by_IA_all_missing();
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_create_delete_update_smart_policy_by_IA_all_installed() throws Exception {
		test_can_create_smart_policy_by_IA_all_installed();
		test_can_update_smart_policy_by_IA_all_installed();
		test_can_delete_smart_policy_by_IA_all_installed();
	}

//	@Test
//	@Category(com.absolute.util.helper.FastTest.class)
//	public void test_create_delete_update_smart_policy_by_ICP_some_missing() throws Exception {
//		test_can_create_smart_policy_by_ICP_some_missing();
//		test_can_update_smart_policy_by_ICP_some_missing();
//		test_can_delete_smart_policy_by_ICP_some_missing();
//	}

	
	/************************************************************
	 * Base Smart Policy
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		//Check if SMART_POLICY_TEST_54321 exists first, and if so, delete it.
		deleteSmartPolicy(SMART_POLICY_TEST_54321);

		StringBuilder sb = new StringBuilder();	

		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_TEST_54321 + "\","); 
		sb.append("\"filterType\":1,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append(" \"CompareValue\":[");
		sb.append("{	\"CachedInfoItemName\":\"Mobile Device Manufacturer\",");
		sb.append(" 	\"CompareValue\":\"Samsung\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"408A8D10-D908-4A9E-A00C-3FFB27E7EA81\",");
		sb.append("		\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CachedInfoItemName\":\"Mobile Device OS Platform\",");
		sb.append("		\"CompareValue\":\"Android\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append("		\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5\",");
		sb.append("		\"IsCustomField\":false,");
		sb.append(" 	\"Operator\":\"==\",");
		sb.append("		\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CachedInfoItemName\":\"Mobile Device OS Build Number\",");
		sb.append("		\"CompareValue\":\"1\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append("		\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"EFD8C1F6-770D-4C5B-B502-AE74A50B1D42\",");
		sb.append("		\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"IsNotEmpty\",");
		sb.append("		\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"CriteriaFieldType\":0,");
		sb.append("	\"Operator\":\"OR\"");
		sb.append("}");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_STANDARD_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "Samsung", "Android");
		assertTrue(result);
	}
	
	public void test_can_update_smart_policy() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		// Assume this runs after test_can_create_smart_policy, 
		// so SP WebApiSmartPolicy_Test54321 is created.
		
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();			
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":1,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 
		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append(" \"CompareValue\":[");
		sb.append("{	\"CachedInfoItemName\":\"Mobile Device Model\",");
		sb.append(" 	\"CompareValue\":\"Anything\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"61479324-9E16-46FD-85E5-68F9865A7D6D\",");
		sb.append("		\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CachedInfoItemName\":\"Mobile Device Phone Number\",");
		sb.append("		\"CompareValue\":\"1234567\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append("		\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"CE678571-F939-4C26-8189-6B246BD46A42\",");
		sb.append("		\"IsCustomField\":false,");
		sb.append(" 	\"Operator\":\"==\",");
		sb.append("		\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"CriteriaFieldType\":0,");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);

		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "Samsung", "Android");
		org.junit.Assert.assertFalse(result);
		result = Helpers.check_resultset_for_2_strings(resultAsString, "Anything", "1234567");
		org.junit.Assert.assertTrue(result);
	}
	
	public void test_can_delete_smart_policy() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_TEST_54321
		deleteSmartPolicy(SMART_POLICY_TEST_54321);
	}

	/************************************************************
	 * Smart Policy by IA - some/missing
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy_by_IA_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		deleteSmartPolicy(SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);
		
		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321 + "\","); 
		sb.append("\"filterType\":2,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someRubbish\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"missingNothing\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

//		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);

		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someRubbish", "missingNothing");
		assertTrue(result);
	}
	
	public void test_can_update_smart_policy_by_IA_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someRubbishUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"missingNothingUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someRubbishUpdate", "missingNothingUpdate");
		assertTrue(result);
	}
	
	public void test_can_delete_smart_policy_by_IA_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321
		deleteSmartPolicy(SMART_POLICY_BY_IA_SOME_MISSING_TEST_54321);

	}

	/************************************************************
	 * Smart Policy by IA - some/installed
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy_by_IA_some_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		StringBuilder sb = new StringBuilder();	
		deleteSmartPolicy(SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
				
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321 + "\","); 
		sb.append("\"filterType\":2,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someRubbish\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"installedNothing\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"IN\",");
		sb.append("	\"Operator\":\"OR\"");
		sb.append("}");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

//		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);

		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someRubbish", "installedNothing");
		assertTrue(result);
	}
	
	public void test_can_update_smart_policy_by_IA_some_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someRubbishUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"installedNothingUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someRubbishUpdate", "installedNothingUpdate");
		assertTrue(result);
	}
	
	public void test_can_delete_smart_policy_by_IA_some_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321
		deleteSmartPolicy(SMART_POLICY_BY_IA_SOME_INSTALLED_TEST_54321);
	}

	/************************************************************
	 * Smart Policy by IA - all/missing
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy_by_IA_all_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		StringBuilder sb = new StringBuilder();	

		deleteSmartPolicy(SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);

		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321 + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"allRubbish\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"missingNothing\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"OR\"");
		sb.append("}");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

//		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);

		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "allRubbish", "missingNothing");
		assertTrue(result);
	}
	
	public void test_can_update_smart_policy_by_IA_all_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 
		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"allRubbishUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"missingNothingUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"OR\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "allRubbishUpdate", "missingNothingUpdate");
		assertTrue(result);
	}
	
	public void test_can_delete_smart_policy_by_IA_all_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321
		deleteSmartPolicy(SMART_POLICY_BY_IA_ALL_MISSING_TEST_54321);
	}

	/************************************************************
	 * Smart Policy by IA - all/installed
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy_by_IA_all_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		StringBuilder sb = new StringBuilder();	

		deleteSmartPolicy(SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
				
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321 + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"allRubbish\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"installedNothing\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

//		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);

		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "allRubbish", "installedNothing");
		assertTrue(result);
	}

	public void test_can_update_smart_policy_by_IA_all_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":2,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 
		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"allRubbishUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"installedNothingUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"5C7C9375-88D7-479F-A27A-4C1E038E8746\",");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"OR\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "allRubbishUpdate", "installedNothingUpdate");
		assertTrue(result);
	}
	
	public void test_can_delete_smart_policy_by_IA_all_installed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321
		deleteSmartPolicy(SMART_POLICY_BY_IA_ALL_INSTALLED_TEST_54321);
	}

	public void deleteSmartPolicy(String name) throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Check if it exists first
		try {
			String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, name);
			if (policyId != null && policyId.length > 0) {
				StringBuilder sb = new StringBuilder();		
				sb.append("{");
				sb.append("\"policyIds\":[" + policyId[0] + "]");
				sb.append("}");
				System.out.println("Request body=" + sb.toString());

				Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_DELETE_API, sb.toString());

				Helpers.waitForAdminConsoleToCatchUp();
			}
		} catch (Exception e) {
			// exception thrown if the smart policy does not exist.
		}
	}

	/************************************************************
	 * Smart Policy by ICP - some/missing
	 * @throws KeyManagementException 
	 ************************************************************/

	public void test_can_create_smart_policy_by_ICP_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		deleteSmartPolicy(SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);
		
		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321 + "\","); 
		sb.append("\"filterType\":3,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someMissing\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"B78AAB04-4384-431F-A473-C555DDC649DD\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":true,");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"A3EAFEBA-833A-4F7B-AA66-74FC11A669A3\",");
		sb.append(" 	\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":true");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"12345678\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"6AA7C2C9-C66B-47AE-8481-07C6D551CD4B\",");
		sb.append(" 	\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();

//		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_SMART_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);

		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someMissing", "12345678");
		assertTrue(result);
	}
	
	public void test_can_update_smart_policy_by_ICP_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);
		assertTrue(policyId != null);
		assertTrue(policyId.length == 1);

		String[] uniqueIds = Helpers.getPolicyUuidsForPolicyNames(logonCookie, SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);
		assertTrue(uniqueIds != null);
		assertTrue(uniqueIds.length == 1);

		StringBuilder sb = new StringBuilder();	
		
		sb.append("{");
		sb.append("\"name\":\"" + SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321_UPDATE + "\","); 
		sb.append("\"filterType\":5,"); 
		sb.append("\"id\":" + policyId[0] + ","); 
		sb.append("\"uniqueID\":\"" + uniqueIds[0] + "\","); 
		sb.append("\"seed\":1,"); 

		sb.append("\"smartPolicyUserEditableFilter\":");
		sb.append("{");
		sb.append("\"CompareValue\":[");
		sb.append("{ 	\"CompareValue\":\"someMissingUpdate\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"B78AAB04-4384-431F-A473-C555DDC649DD\",");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("},");
		sb.append("{	\"CompareValue\":false,");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"A3EAFEBA-833A-4F7B-AA66-74FC11A669A3\",");
		sb.append(" 	\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"==\",");
		sb.append(" 	\"UseNativeType\":true");
		sb.append("},");
		sb.append("{	\"CompareValue\":\"13579\",");
		sb.append("		\"CompareValue2\":\"\",");
		sb.append(" 	\"CompareValueUnits\":\"Minutes\",");
		sb.append("		\"InfoItemID\":\"6AA7C2C9-C66B-47AE-8481-07C6D551CD4B\",");
		sb.append(" 	\"IsCustomField\":false,");
		sb.append("		\"Operator\":\"NotContains\",");
		sb.append(" 	\"UseNativeType\":false");
		sb.append("}");
		sb.append("],");
		sb.append("	\"ContainmentOperator\":\"NOT IN\",");
		sb.append("	\"Operator\":\"AND\"");
		sb.append("}");
		sb.append("}");
		
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_SMART_API + "/" + policyId[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyId[0]), SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321_UPDATE);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, "someRubbishUpdate", "13579");
		assertTrue(result);
	}
	public void test_can_delete_smart_policy_by_ICP_some_missing() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException {
		// Try to delete SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321
		deleteSmartPolicy(SMART_POLICY_BY_ICP_SOME_MISSING_TEST_54321);

	}

}
