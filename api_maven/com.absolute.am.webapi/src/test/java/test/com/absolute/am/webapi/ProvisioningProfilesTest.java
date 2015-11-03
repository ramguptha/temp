package test.com.absolute.am.webapi;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;


public class ProvisioningProfilesTest extends LoggedInTest {
	private static final String INSTALL_PROVISIONING_PROFILES_API = Helpers.WEBAPI_BASE_URL + LoggedInTest.COMMANDS_API + "/installprovisioningprofile";
	@SuppressWarnings("unused")
	private static final String REMOVE_PROVISIONING_PROFILES_API = Helpers.WEBAPI_BASE_URL + LoggedInTest.COMMANDS_API + "/removeprovisioningprofile";
	private static final String PROVISIONING_PROFILES_VIEWS_API = Helpers.WEBAPI_BASE_URL + "api/provisioningprofiles/views";
	private static final String PROVISIONING_PROFILE_NAME_1 = "AbsoluteApps InHouse 2013";
	public static String cookie = null;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {
		can_get_list_of_views();
		can_get_view_all();
		cannot_get_view_nonexisting();
	}
	
	public void can_get_list_of_views() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		StringBuilder views = new StringBuilder();
		views.append("{");
		views.append("\"viewDescriptions\":[{\"viewName\":\"all\",\"viewDisplayName\":\"All Provisioning Profiles\",\"id\":0}]");
		views.append("}");
		String expected = views.toString();
		
		String actual = test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				PROVISIONING_PROFILES_VIEWS_API,
				HttpStatus.SC_OK,
				HttpStatus.SC_OK);

		boolean isSame = (actual.compareToIgnoreCase(expected) == 0);
		
		Assert.assertTrue(isSame);
	}
	
	public void can_get_view_all() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String actual = test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				PROVISIONING_PROFILES_VIEWS_API + "/all",
				HttpStatus.SC_OK,
				HttpStatus.SC_OK);

		//check that output begins with {"metaData":{  and contains "rows":[
		//TODO: replace with actual data verification when test data is available
		boolean isExpectedResult = (actual.indexOf("{\"metaData\":{") == 0
							&& actual.indexOf("\"rows\":[") > 0
							);
		
		Assert.assertTrue(isExpectedResult);
	}
	
	public void cannot_get_view_nonexisting() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				PROVISIONING_PROFILES_VIEWS_API + "/nonexisting_view",
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);

		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void can_install_profiles() throws Exception {

		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, "WebAPI Device 1")[0];	//iOS7 device
		String profileIds1 = Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, PROVISIONING_PROFILE_NAME_1)[0];
//		String profileIds2 = Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, "AbsoluteSafe InHouse 2013")[0];
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"deviceIds\":");
		sb.append("[");
		sb.append(deviceId);
		sb.append("],");
		sb.append("\"provisioningProfileIds\":");
		sb.append("[");
		sb.append(profileIds1);
//		sb.append("," + profileIds2);
		sb.append("]");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie,INSTALL_PROVISIONING_PROFILES_API, sb.toString());
		
		Thread.sleep(10000);
		
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/mobiledevices/" + deviceId + "/provisioningprofiles?$search=" + URLEncoder.encode(PROVISIONING_PROFILE_NAME_1, "UTF-8"), 200, 200);
		System.out.println("viewResult:");
		System.out.println(viewResult);

		Assert.assertTrue("The provisioning profile '" + PROVISIONING_PROFILE_NAME_1 + "' should be installed on the device 'WebAPI Device 1'.", viewResult.contains(PROVISIONING_PROFILE_NAME_1));;
	}
}