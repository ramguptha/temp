package test.com.absolute.am.webapi;

import org.junit.Assert;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;


public class PoliciesTest extends LoggedInTest {
	
	private static final String TEST_POLICY_NAME = "test_policy1";
	private static final String RENAMED_TEST_POLICY_NAME = "test_policy2";
	private static final String NONEXISTING_POLICY_ID = "123456789";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		//test order is of the essence
		setup();
		test_can_create_standard_policy();
		test_cant_create_standard_policy_with_nonunique_name(); 
		test_can_rename_standard_policy();
		test_cant_rename_standard_policy_to_non_unique_name();
		test_delete_single_standard_policy();
		test_can_create_standard_policy(); // run this a second time since we need another test policy
		test_can_delete_policy();
		test_cant_create_policy_with_empty_name();
		test_cant_delete_policy_with_empty_id_list();
		test_cant_delete_policy_with_nonexisting_id();
		
	}
	
	public void setup() throws Exception {
		//delete test policies if they exist
		String testPolicyId = null;
		String RenamedTestPolicyId = null;
		
		try {
			testPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
			Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + testPolicyId, 
					null, HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);
		} catch (RuntimeException ex) {
			//policy does not exist
		}
		
		try {
			RenamedTestPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,RENAMED_TEST_POLICY_NAME)[0];
			Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + RenamedTestPolicyId, 
					null, HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);
		} catch (RuntimeException ex) {
			//policy does not exist
		}
	}
	
	public void test_cant_create_standard_policy_with_nonunique_name() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"");
		sb.append(TEST_POLICY_NAME);
		sb.append("\"");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_STANDARD_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		//all good if reached this point
		Assert.assertTrue(true);
	}
	
	public void test_can_rename_standard_policy() throws Exception {
			
		String id = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"name\":");
		sb.append("\"");
		sb.append(RENAMED_TEST_POLICY_NAME);
		sb.append("\"");
		sb.append(",");
		sb.append("\"seed\":");
		sb.append(1);
		sb.append(",");
		sb.append("\"id\":");
		sb.append("\"");
		sb.append(id);
		sb.append("\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_STANDARD_API + "/" + id, sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT );
		
		String renamedTestPolicyId = null;
		
		try {
			renamedTestPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,RENAMED_TEST_POLICY_NAME)[0];
			Assert.assertNotNull(renamedTestPolicyId);
		} catch (RuntimeException ex) {
			//policy does not exist
			Assert.assertTrue(false);
		}
	}
	
	public void test_cant_rename_standard_policy_to_non_unique_name() throws Exception {
		
		String id = Helpers.getPolicyIdsForPolicyNames(logonCookie,RENAMED_TEST_POLICY_NAME)[0];
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"name\":");
		sb.append("\"");
		sb.append(RENAMED_TEST_POLICY_NAME);
		sb.append("\"");
		sb.append(",");
		sb.append("\"seed\":");
		sb.append(2);
		sb.append(",");
		sb.append("\"id\":");
		sb.append("\"");
		sb.append(id);
		sb.append("\"");
		sb.append("}");

		
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_STANDARD_API + "/" + id, sb.toString(),
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT );
		
		//all good if reached this point
		Assert.assertTrue(true);
	}
	
	public void test_cant_create_policy_with_empty_name() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"name\":");
		sb.append("\"\"");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_STANDARD_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		//all good if reached this point
		Assert.assertTrue(true);
	}
	
	public void test_can_create_standard_policy() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"");
		sb.append(TEST_POLICY_NAME);
		sb.append("\"");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_STANDARD_API, sb.toString());

		// can't create another policy with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_CREATE_STANDARD_API, sb.toString(), 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		
		String testPolicyId = null;
		
		try {
			testPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
			Assert.assertNotNull(testPolicyId);
		} catch (RuntimeException ex) {
			//policy does not exist
			Assert.assertTrue(false);
		}
	}
	
	//delete using POST method
	public void test_can_delete_policy() throws Exception {
		
		String testPolicyId = null;
		testPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"policyIds\":[");
		sb.append(testPolicyId);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_DELETE_API, sb.toString());
		
		testPolicyId = null;
		try {
			testPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
			Assert.assertNull(testPolicyId);
		} catch (RuntimeException ex) {
			//policy does not exist
			Assert.assertTrue(true);
		}
	}
	
	public void test_cant_delete_policy_with_empty_id_list() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"policyIds\":[");
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		//all good if reached this point
		Assert.assertTrue(true);
	}
	
	public void test_cant_delete_policy_with_nonexisting_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"policyIds\":[");
		sb.append(NONEXISTING_POLICY_ID);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		//note expected return code is different for DELETE compared to POST above
		Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + NONEXISTING_POLICY_ID, 
				null, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		
		//all good if reached this point
		Assert.assertTrue(true);
	}
	
	//delete using DELETE method
	public void test_delete_single_standard_policy() throws Exception {
		
		// assume that a policy with the name "RENAMED_TEST_POLICY_NAME" has been successfully created in the previous tests
		String renamedTestPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,RENAMED_TEST_POLICY_NAME)[0];
		System.out.println("Deleting policy with id=" + renamedTestPolicyId);
		Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + renamedTestPolicyId, 
				null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT );
		
		renamedTestPolicyId = null;
		try {
			renamedTestPolicyId = Helpers.getPolicyIdsForPolicyNames(logonCookie,TEST_POLICY_NAME)[0];
			Assert.assertNull(renamedTestPolicyId);
		} catch (RuntimeException ex) {
			//policy does not exist
			Assert.assertTrue(true);
		}
	}
}
