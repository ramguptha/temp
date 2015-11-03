/**
 * 
 */
package test.com.absolute.am.webapi;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class SendMessageTest extends LoggedInTest {
	private static final String SEND_MESSAGE_COMMAND_API = COMMANDS_API + "/sendmessage";
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_message_to_devices() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0]);
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIds[0]);
		sb.append("],");
		sb.append("\"message\":\"Hello from test.com.absolute.am.webapi test at " + StringUtilities.toISO8601W3CString(new Date()) + ".\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + SEND_MESSAGE_COMMAND_API, sb.toString());
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_send_message_empty_deviceid_list() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append("],");
		sb.append("\"message\":\"Hello from test.com.absolute.am.webapi test at " + StringUtilities.toISO8601W3CString(new Date()) + ".\"");
		sb.append("}");

		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_MESSAGE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_send_message_empty_message() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIds[0]);	// TODO: read this id from a config file or get it via the webapi.		
		sb.append("],");
		sb.append("\"message\":null");
		sb.append("}");

		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_MESSAGE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}
	
}
