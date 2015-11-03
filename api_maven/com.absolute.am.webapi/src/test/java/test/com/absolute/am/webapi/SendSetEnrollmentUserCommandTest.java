package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendSetEnrollmentUserCommandTest extends LoggedInTest {

	private static final String SEND_SET_ENROLLMENT_USER_COMMAND_API = COMMANDS_API + "/setenrollmentuser";
	private static final String ENROLLMENT_USER = "test_user";
	private static final String ENROLLMENT_DOMAIN = "test_domain";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_set_enrollment_user_command() throws Exception {
		
		//String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  DEVICE_NAME_1);
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  "WebAPI Device 1");
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
	    sb.append("\"deviceIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"username\":\"" + ENROLLMENT_USER + "\"");
		sb.append(",");
		sb.append("\"domain\":\"" + ENROLLMENT_DOMAIN + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_SET_ENROLLMENT_USER_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_set_enrollment_user_empty_command() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		//String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  "ronnewsham");
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
	    sb.append("\"deviceIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"username\":");
		sb.append("\"\"");
		sb.append(",");
		sb.append("\"domain\":");
		sb.append("\"\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_SET_ENROLLMENT_USER_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_bad_data() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		// an invalid post - missing deviceIds
		sb.append("{");		
		sb.append("\"deviceIds\":[]");
		sb.append(",");
		sb.append("\"username\":");
		sb.append("\"\"");
		sb.append(",");
		sb.append("\"domain\":");
		sb.append("\"\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ENROLLMENT_USER_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	

	
}