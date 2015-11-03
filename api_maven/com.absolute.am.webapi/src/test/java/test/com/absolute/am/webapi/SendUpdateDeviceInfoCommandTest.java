package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendUpdateDeviceInfoCommandTest extends LoggedInTest {

	private static final String SEND_UPDATE_DEVICE_INFO_COMMAND_API = COMMANDS_API + "/updatedeviceinfo";
	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_update_device_info_command() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");		
	    sb.append("\"deviceIds\":[" + deviceIds[0] + "," + deviceIds[1] + "]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_UPDATE_DEVICE_INFO_COMMAND_API, 
					sb.toString(),
					200,
					299
					);
		
		System.out.println("Response:" + response);
	}	
	
}
