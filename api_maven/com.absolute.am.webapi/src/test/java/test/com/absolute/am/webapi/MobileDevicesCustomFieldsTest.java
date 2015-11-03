package test.com.absolute.am.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class MobileDevicesCustomFieldsTest extends LoggedInTest {

	private static final String TEST_CUSTOM_FIELD_NAME = "test_custom_field123";
	private static String testCustomFieldId, testDeviceId;
	
	/* RCHEN comment: 
	 * Since there's following error happening with the AM Synch Service, which stops this test. We currently comment out this test so that we can create build.
	 * This test must be de-comment after the problem is solved. 
	 * Error while trying to update custom field definition tables: database error: database is locked (error code 5)
	 * */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		try{
			setup();
			
			can_get_custom_fields_for_device();
			can_set_custom_field_data_for_device("test123");
			can_set_custom_field_data_for_device("test321");
			can_remove_custom_field_data_for_device();
		} finally{
			clean_up();
		}
		
	}
	
	private void setup() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException, InterruptedException{
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
		
		testCustomFieldId = Helpers.getCustomFieldIdsForCustomFieldNames(logonCookie,TEST_CUSTOM_FIELD_NAME)[0];
	}
	
	private void can_get_custom_fields_for_device() throws KeyManagementException, ClientProtocolException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
		testDeviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];

		Helpers.doGETCheckStatusReturnBody(logonCookie,	Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + testDeviceId + "/customfields", HttpStatus.SC_OK, HttpStatus.SC_OK);
		
		Assert.assertTrue(true);
	}
	
	private void can_set_custom_field_data_for_device(String data) throws KeyManagementException, ClientProtocolException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"associations\":[{");
		sb.append("\"deviceIds\":[");
		sb.append(testDeviceId);
		sb.append("], \"items\": [{");
		sb.append("\"id\": \"" + testCustomFieldId + "\"");
		sb.append(", \"value\" : \"" + data + "\", \"type\" : 1");
		sb.append("}] }] }");
		
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_MOBILE_DEVICE_API, sb.toString());
		
		Thread.sleep(5000);
		
		String[] response = Helpers.getCustomFieldForMobileDeviceDataForCustomFieldNames(logonCookie, testDeviceId, TEST_CUSTOM_FIELD_NAME);
		
		Assert.assertTrue(response.length == 1);
		Assert.assertTrue(response[0].equals(data));
	}
	
	private void can_remove_custom_field_data_for_device() throws KeyManagementException, ClientProtocolException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"associations\":[{");
		sb.append("\"deviceIds\":[");
		sb.append(testDeviceId);
		sb.append("], \"items\": [{");
		sb.append("\"id\": \"" + testCustomFieldId + "\"");
		sb.append("}] }] }");
		
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_MOBILE_DEVICE_API + "/delete", sb.toString());
		
		Thread.sleep(5000);
		
		try {
			Helpers.getCustomFieldForMobileDeviceDataForCustomFieldNames(logonCookie, testDeviceId, TEST_CUSTOM_FIELD_NAME);
		} catch (RuntimeException ex) {
			//custom field does not exist
			Assert.assertTrue(true);
		}
	}
	
	private void clean_up() throws KeyManagementException, ClientProtocolException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"ids\":[\"");
		sb.append(testCustomFieldId);
		sb.append("\"]");
		sb.append("}");
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CUSTOM_FIELDS_API + "/delete", sb.toString(), HttpStatus.SC_NO_CONTENT);
	}

}
