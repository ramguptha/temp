package test.com.absolute.am.webapi;

import org.junit.Assert;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;


public class CustomFieldsTest extends LoggedInTest {
	
	private static final String TEST_CUSTOM_FIELD_NAME = "test_custom_field1";
	private static final String RENAMED_TEST_CUSTOM_FIELD_NAME = "test_custom_field2";
	private static final String NONEXISTING_CUSTOM_FIELD_ID = "123456789";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		test_can_create_custom_field();
		test_can_rename_custom_field();
		test_can_delete_custom_field();

		test_cant_create_custom_field_with_empty_name();
		test_cant_delete_custom_field_with_empty_id_list();
		test_cant_delete_custom_field_with_nonexisting_id();
	}
	
	public void test_can_create_custom_field() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"");
		sb.append(TEST_CUSTOM_FIELD_NAME);
		sb.append("\", \"variableName\": \"variableName4UnitTests\"");
		sb.append(", \"description\": \"abc\"");
		sb.append(", \"dataType\": 1");
		sb.append(", \"displayType\": 1");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API, sb.toString());
		
		Thread.sleep(5000);
		
		String customFieldId = null;
		
		try {
			customFieldId = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,TEST_CUSTOM_FIELD_NAME)[0];
			Assert.assertNotNull(customFieldId);
		} catch (RuntimeException ex) {
			//custom field does not exist
			Assert.assertTrue(false);
		}
	}
		
	public void test_can_rename_custom_field() throws Exception {
			
		String id = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,TEST_CUSTOM_FIELD_NAME)[0];
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"name\":");
		sb.append("\"");
		sb.append(RENAMED_TEST_CUSTOM_FIELD_NAME);
		sb.append("\"");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API + "/" + id, sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT );
		
		Thread.sleep(5000);
		
		String renamedTestCustomFieldId = null;
		
		try {
			renamedTestCustomFieldId = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,RENAMED_TEST_CUSTOM_FIELD_NAME)[0];
			Assert.assertNotNull(renamedTestCustomFieldId);
		} catch (RuntimeException ex) {
			//custom field does not exist
			Assert.assertTrue(false);
		}
	}
		
	public void test_can_delete_custom_field() throws Exception {
		
		String customFieldId = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,RENAMED_TEST_CUSTOM_FIELD_NAME)[0];
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"ids\":[\"");
		sb.append(customFieldId);
		sb.append("\"]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API + "/delete", sb.toString(), HttpStatus.SC_NO_CONTENT);
		
		Thread.sleep(5000);
		
		try {
			customFieldId = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,RENAMED_TEST_CUSTOM_FIELD_NAME)[0];
			Assert.assertNull(customFieldId);
		} catch (RuntimeException ex) {
			//custom field does not exist
			Assert.assertTrue(true);
		}
	}
	
	public void test_cant_create_custom_field_with_empty_name() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"name\":");
		sb.append("\"\"");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		Assert.assertTrue(true);
	}
	
	public void test_cant_delete_custom_field_with_empty_id_list() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"ids\":[]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API + "/delete", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		Assert.assertTrue(true);
	}
	
	public void test_cant_delete_custom_field_with_nonexisting_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"ids\":[");
		sb.append(NONEXISTING_CUSTOM_FIELD_ID);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		//note expected return code is different for DELETE compared to POST above
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API + "/delete", 
				sb.toString(), HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		
		//all good if reached this point
		Assert.assertTrue(true);
	}
}