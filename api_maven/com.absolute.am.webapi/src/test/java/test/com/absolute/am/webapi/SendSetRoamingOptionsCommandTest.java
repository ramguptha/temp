package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendSetRoamingOptionsCommandTest extends LoggedInTest {

	private static final String SEND_SET_ROAMING_OPTIONS_COMMAND_API = COMMANDS_API + "/setroamingoptions";
	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_set_roaming_options_command() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
	    sb.append("\"deviceIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"voice\":false");
		sb.append(",");
		sb.append("\"data\":false");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_SET_ROAMING_OPTIONS_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_bad_data() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");		
		sb.append("\"deviceIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"voice\":null");
		sb.append(",");
		sb.append("\"data\":null");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ROAMING_OPTIONS_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	

	
}
