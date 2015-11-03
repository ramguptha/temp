package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class SendSetOrganizationInfoCommandTest extends LoggedInTest {

	private static final String SEND_SET_ORGANIZATION_INFO_COMMAND_API = COMMANDS_API + "/setorganizationinfo";
	private static final String NON_EXISTING_DEVICE_ID = "999999";
	private static final String ORGANIZATION_NAME = "Absolute Software";
	private static final String ORGANIZATION_PHONE = "1-604-730-9851";
	private static final String ORGANIZATION_EMAIL = "info@absolute.com";
	private static final String ORGANIZATION_ADDRESS = "1600-1055 Dunsmuir Street, Vancouver, BC V7X 1K8, Canada";
	private static final String ORGANIZATION_CUSTOM = "absolute";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_set_organization_info_iOS7_device_command() throws Exception {
		
		//String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  DEVICE_NAME_1);
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"name\":\"" + ORGANIZATION_NAME + "\"");
		sb.append(",");
		sb.append("\"phone\":\"" + ORGANIZATION_PHONE + "\"");
		sb.append(",");
		sb.append("\"email\":\"" + ORGANIZATION_EMAIL + "\"");
		sb.append(",");
		sb.append("\"address\":\"" + ORGANIZATION_ADDRESS + "\"");
		sb.append(",");
		sb.append("\"custom\":\"" + ORGANIZATION_CUSTOM + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
					logonCookie, 
					Helpers.WEBAPI_BASE_URL + SEND_SET_ORGANIZATION_INFO_COMMAND_API, sb.toString(),
					200, 299);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_mixed_platform_types() throws Exception {
		
		String[] deviceIdsiOs = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		String[] deviceIdsAndroid = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + deviceIdsiOs[0] + "," + deviceIdsAndroid[0] + "]");
		sb.append(",");
		sb.append("\"name\":\"" + ORGANIZATION_NAME + "\"");
		sb.append(",");
		sb.append("\"phone\":\"" + ORGANIZATION_PHONE + "\"");
		sb.append(",");
		sb.append("\"email\":\"" + ORGANIZATION_EMAIL + "\"");
		sb.append(",");
		sb.append("\"address\":\"" + ORGANIZATION_ADDRESS + "\"");
		sb.append(",");
		sb.append("\"custom\":\"" + ORGANIZATION_CUSTOM + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ORGANIZATION_INFO_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_insufficient_os_version() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + deviceIds[0] + "," + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"name\":\"" + ORGANIZATION_NAME + "\"");
		sb.append(",");
		sb.append("\"phone\":\"" + ORGANIZATION_PHONE + "\"");
		sb.append(",");
		sb.append("\"email\":\"" + ORGANIZATION_EMAIL + "\"");
		sb.append(",");
		sb.append("\"address\":\"" + ORGANIZATION_ADDRESS + "\"");
		sb.append(",");
		sb.append("\"custom\":\"" + ORGANIZATION_CUSTOM + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ORGANIZATION_INFO_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_missing_device_ids() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[]");
		sb.append(",");
		sb.append("\"name\":\"" + ORGANIZATION_NAME + "\"");
		sb.append(",");
		sb.append("\"phone\":\"" + ORGANIZATION_PHONE + "\"");
		sb.append(",");
		sb.append("\"email\":\"" + ORGANIZATION_EMAIL + "\"");
		sb.append(",");
		sb.append("\"address\":\"" + ORGANIZATION_ADDRESS + "\"");
		sb.append(",");
		sb.append("\"custom\":\"" + ORGANIZATION_CUSTOM + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ORGANIZATION_INFO_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_exception_with_invalid_device_id() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie,  MobileDevices.MOBILE_DEVICE_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");		
		sb.append("\"deviceIds\":[" + NON_EXISTING_DEVICE_ID + "," + deviceIds[0] + "]");
		sb.append(",");
		sb.append("\"name\":\"" + ORGANIZATION_NAME + "\"");
		sb.append(",");
		sb.append("\"phone\":\"" + ORGANIZATION_PHONE + "\"");
		sb.append(",");
		sb.append("\"email\":\"" + ORGANIZATION_EMAIL + "\"");
		sb.append(",");
		sb.append("\"address\":\"" + ORGANIZATION_ADDRESS + "\"");
		sb.append(",");
		sb.append("\"custom\":\"" + ORGANIZATION_CUSTOM + "\"");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		String response = Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SEND_SET_ORGANIZATION_INFO_COMMAND_API, 
				sb.toString(), 
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("Response:" + response);
	}	
	
}