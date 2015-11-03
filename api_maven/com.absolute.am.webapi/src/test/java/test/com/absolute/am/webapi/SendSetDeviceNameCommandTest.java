package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendSetDeviceNameCommandTest extends LoggedInTest {

	private static final String SEND_SET_DEVICE_NAME_COMMAND_API = COMMANDS_API + "/setdevicename";
	private static final String NON_EXISTING_DEVICE_ID = "999999";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_set_enrollment_user_command() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
	    sb.append("\"deviceId\":" + deviceIds[0] + "");
		sb.append(",");
		sb.append("\"name\":\"" + "ronnewsham2" + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_SET_DEVICE_NAME_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_missing_device_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();

		sb.append("{");		
		sb.append("\"deviceId\":" + null);
		sb.append(",");
		sb.append("\"name\":\"" + "ronnewsham" + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_DEVICE_NAME_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_missing_device_name() throws Exception {
		
		//String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  DEVICE_NAME_1);
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
				
		StringBuilder sb = new StringBuilder();

		sb.append("{");		
		sb.append("\"deviceId\":" + deviceIds[0] + "");
		sb.append(",");
		sb.append("\"name\":\"\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_DEVICE_NAME_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_invalid_device_id() throws Exception {
		
		//String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  DEVICE_NAME_1);
		String[] deviceIds = new String[] {NON_EXISTING_DEVICE_ID};
				
		StringBuilder sb = new StringBuilder();

		sb.append("{");		
		sb.append("\"deviceId\":" + deviceIds[0] + "");
		sb.append(",");
		sb.append("\"name\":\"" + "ronnewsham2" + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_DEVICE_NAME_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	
	
}