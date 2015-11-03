package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendRetryAllFailedProfilesCommandTest extends LoggedInTest {

	private static final String SEND_RETRY_ALL_FAILED_PROFILES_COMMAND_API = COMMANDS_API + "/retryallfailedprofiles";
	private static final String NON_EXISTING_DEVICE_ID = "999999";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_retry_all_failed_profiles_command() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + deviceIds[0] + "," + deviceIds[1] + "]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_RETRY_ALL_FAILED_PROFILES_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_missing_device_ids() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_RETRY_ALL_FAILED_PROFILES_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_invalid_device_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + NON_EXISTING_DEVICE_ID + "]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_RETRY_ALL_FAILED_PROFILES_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	
	
}