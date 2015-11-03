/**
 * 
 */
package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 *
 */
public class LockDeviceTest extends LoggedInTest {
	private static final String LOCK_DEVICE_COMMAND_API = COMMANDS_API + "/lock";
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_lock_device() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1]);
//		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, "Kevin''s Fujitsu");

		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIds[0]);	
		sb.append("]}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + LOCK_DEVICE_COMMAND_API, sb.toString());
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_lock_empty_deviceid_list() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append("]}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + LOCK_DEVICE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_lock_device_with_passcode() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1]);
//		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, "Kevin''s Fujitsu");

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIds[0]);	
		sb.append("],");
		sb.append("\"passcode\":\"1234\"}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + LOCK_DEVICE_COMMAND_API, sb.toString());
	}

}
