package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class GenericViewTest extends LoggedInTest {
	private static final String GENERIC_VIEW_API = Helpers.WEBAPI_BASE_URL + "api/views/generic/";
	public static String cookie = null;

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		can_retrieve_a_simple_user_defined_view_endpoint();
		can_retrieve_a_complex_user_defined_view_endpoint();
		cannot_run_command_with_empty_command_params();
		cannot_run_command_for_non_superadmin_user();
	}

	public void can_retrieve_a_simple_user_defined_view_endpoint() throws IllegalStateException, IOException, KeyManagementException,
			NoSuchAlgorithmException, InterruptedException {

		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("\"guids\" : [\"39f3f074-b8a2-4df1-ac02-eb1f25f3f98e\", \"FE5A9F56-228C-4BDA-99EC-8666292CB5C1\", \"8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5\"],");
		jsonCmd.append("\"rootTable\" : \"iphone_info\",");
		jsonCmd.append("\"sortBy\" : \"FE5A9F56-228C-4BDA-99EC-8666292CB5C1\"");
		jsonCmd.append("}");

		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_VIEW_API, jsonCmd.toString(), HttpStatus.SC_OK);
		System.out.println("response = " + response);
	}

	public void can_retrieve_a_complex_user_defined_view_endpoint() throws IllegalStateException, IOException, KeyManagementException,
			NoSuchAlgorithmException, InterruptedException {

		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("\"guids\" : [\"39f3f074-b8a2-4df1-ac02-eb1f25f3f98e\", \"FE5A9F56-228C-4BDA-99EC-8666292CB5C1\", \"8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5\"],");
		jsonCmd.append("\"rootTable\" : \"iphone_info\",");
		jsonCmd.append("\"sortBy\" : \"FE5A9F56-228C-4BDA-99EC-8666292CB5C1\",");
		jsonCmd.append("\"sortDir\" : \"Descending\",");
		jsonCmd.append("\"filter\" : {\"CompareValue\" : [{\"CachedInfoItemName\" : \"Mobile Device OS Platform\", \"CompareValue\" : \"Android\", \"CompareValue2\": \"\", \"CompareValueUnits\" : \"Minutes\", \"InfoItemID\" : \"8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5\", \"IsCustomField\" : false, \"Operator\" : \"==\", \"UseNativeType\" : false}], \"CriteriaFieldType\" : 0, \"Operator\" : \"AND\"}");
		jsonCmd.append("}");

		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_VIEW_API, jsonCmd.toString(), HttpStatus.SC_OK);
		System.out.println("response = " + response);
	}

	public void cannot_run_command_for_non_superadmin_user() throws Exception {

		doLogon(serverName, serverPort, "test_admin", password, locale);
		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("}");

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_VIEW_API, jsonCmd.toString(), HttpStatus.SC_FORBIDDEN);
	}

	public void cannot_run_command_with_empty_command_params() throws IllegalStateException, IOException, KeyManagementException,
			NoSuchAlgorithmException, InterruptedException {

		StringBuilder jsonCmd = new StringBuilder();

		jsonCmd.append("{");
		jsonCmd.append("}");

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, GENERIC_VIEW_API, jsonCmd.toString(), HttpStatus.SC_BAD_REQUEST);
	}
}