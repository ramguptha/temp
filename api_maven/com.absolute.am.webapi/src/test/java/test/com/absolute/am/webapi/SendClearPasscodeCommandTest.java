/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendClearPasscodeCommandTest extends LoggedInTest  {
	private static final String SEND_CLEAR_PASSCODE_COMMAND_API = COMMANDS_API + "/clearpasscode";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_set_device_passcode() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		
		String deviceIds[] = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"androidIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"iOsIds\":[]");
		sb.append(",");
		sb.append("\"passcode\":\"1234\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestAndGetResult(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_CLEAR_PASSCODE_COMMAND_API,
					sb.toString());
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_set_device_passcode_for_ios() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"androidIds\":[]");
		sb.append(",");
		sb.append("\"iOsIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"passcode\":\"1234\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_CLEAR_PASSCODE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}

	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_issue_clear_passcode_with_empty_devicelist() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"androidIds\":[]");
		sb.append(",");
		sb.append("\"iOsIds\":[]");
		sb.append(",");
		sb.append("\"passcode\":\"1212\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_CLEAR_PASSCODE_COMMAND_API, 
				sb.toString(),  
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
		
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_issue_clear_passcode_for_android_with_empty_passcode() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"androidIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"iOsIds\":[]");
		sb.append(",");
		sb.append("\"passcode\":\"\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_CLEAR_PASSCODE_COMMAND_API, 
				sb.toString(),  
				200, 
				299);
			
		System.out.println("Response:" + response);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_issue_clear_passcode_for_android_with_null_passcode() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {

		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"androidIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"iOsIds\":[]");
		//sb.append(",");
		//sb.append("\"passcode\":\"\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_CLEAR_PASSCODE_COMMAND_API,
				sb.toString(),
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
		
	}		
	
}
