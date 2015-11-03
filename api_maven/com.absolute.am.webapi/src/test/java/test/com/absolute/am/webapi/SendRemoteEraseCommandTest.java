package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class SendRemoteEraseCommandTest extends LoggedInTest {

	private static final String SEND_REMOTE_ERASE_COMMAND_API = COMMANDS_API + "/remoteerase";
	

	//	@Test
	// Be careful what devices you choose to wipe here. Some of the devices
	// are peoples personal devices and they should not be wiped without their consent.
	//@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_remote_erase_command() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");		
	    sb.append("\"androidIds\":[XXX]"); // replace XXX with an android device
	    sb.append(",");
		sb.append("\"iOsIds\":[YYY]"); // replace YYY with an iOS device
		sb.append(",");
		sb.append("\"includeSDCard\":false");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestAndGetResult(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_REMOTE_ERASE_COMMAND_API,
					sb.toString());
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_send_remote_erase_command_with_empty_devicelist() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"androidIds\":[]");
		sb.append(", \"iOsIds\":[]");				
		sb.append(",\"includeSDCard\":false");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_REMOTE_ERASE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
		
	}	
	
}
