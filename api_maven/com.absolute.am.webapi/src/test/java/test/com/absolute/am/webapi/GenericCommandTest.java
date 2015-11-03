package test.com.absolute.am.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class GenericCommandTest extends LoggedInTest {
	private static final String GENERIC_COMMAND_API = Helpers.WEBAPI_BASE_URL + "api/commands/generic/" + "4100";
	private static final String TEST_POLICY_NAME = "WEB API TMP TEST POLICY";
	public static String cookie = null;

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		can_add_policy_through_generic_endpoint();
		can_remove_policy_through_generic_endpoint();
		cannot_run_command_with_empty_command_params();
		cannot_run_command_for_non_superadmin_user();
	}

	public void cannot_run_command_for_non_superadmin_user() throws Exception {
	
		doLogon(serverName, serverPort, "test_admin", password, locale);
		StringBuilder jsonCmd = new StringBuilder();
		
		jsonCmd.append("{");
		jsonCmd.append("\"commandParameters\" : { \"NewData\" : { \"Name\" : \"" + TEST_POLICY_NAME + "\", \"Seed\" : 1, \"UniqueID\" : \""
				+ UUID.randomUUID().toString() + "\" }, \"OperationType\" : 9 }");
		jsonCmd.append("}");
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_COMMAND_API, jsonCmd.toString(), HttpStatus.SC_FORBIDDEN);
	}
	
	public void can_add_policy_through_generic_endpoint() throws IllegalStateException, IOException, KeyManagementException,
			NoSuchAlgorithmException, InterruptedException {

		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("\"commandParameters\" : { \"NewData\" : { \"Name\" : \"" + TEST_POLICY_NAME + "\", \"Seed\" : 1, \"UniqueID\" : \""
				+ UUID.randomUUID().toString() + "\" }, \"OperationType\" : 9 }");
		jsonCmd.append("}");

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_COMMAND_API, jsonCmd.toString(), HttpStatus.SC_NO_CONTENT);

		Thread.sleep(5000);

		assertTrue(Helpers.getPolicyIdsForPolicyNames(logonCookie, TEST_POLICY_NAME)[0] != null);
	}

	public void cannot_run_command_with_empty_command_params() throws IllegalStateException, IOException, KeyManagementException,
		NoSuchAlgorithmException, InterruptedException {
	
		StringBuilder jsonCmd = new StringBuilder();
		
		jsonCmd.append("{");
		jsonCmd.append("\"commandParameters\" : {}");
		jsonCmd.append("}");
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_COMMAND_API, jsonCmd.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	public void can_remove_policy_through_generic_endpoint() throws KeyManagementException, ClientProtocolException,
			UnsupportedEncodingException, NoSuchAlgorithmException, IOException, InterruptedException {
		String policyId = Helpers.getPolicyUuidsForPolicyNames(logonCookie, TEST_POLICY_NAME)[0];
		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("\"commandParameters\" : { \"Data\" : { \"PolicyIDs\" : [\"" + policyId + "\"], \"RemovePolicyLockedProfiles\" : false }, \"OperationType\" : 10 }");
		jsonCmd.append("}");

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_COMMAND_API, jsonCmd.toString(), HttpStatus.SC_NO_CONTENT);

		Thread.sleep(5000);
		
		try {
			Helpers.getPolicyIdsForPolicyNames(logonCookie, TEST_POLICY_NAME);
			Assert.assertTrue(false);
		} catch (RuntimeException ex) {
			Assert.assertTrue(true);
		}
	}
}