package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.ConfigurationProfiles;
import test.com.absolute.testdata.configuration.ContentFiles;
import test.com.absolute.testdata.configuration.InhouseApplications;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testdata.configuration.ThirdPartyApplications;
import test.com.absolute.testutil.Helpers;


public class MobileDevicesAssignableItems extends LoggedInTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];
		
		test_can_view_assigned_3rd_party_apps(deviceId);
		test_can_view_assigned_inhouse_party_apps(deviceId);
		test_can_view_assigned_profiles(deviceId);
		test_can_view_assigned_content(deviceId);
	}
	
	public void test_can_view_assigned_3rd_party_apps(String deviceId) throws Exception {
		
		String appName = Helpers.getThirdPartyAppIdsForThirdPartyAppNames(logonCookie, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0])[0];
		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/assigned/thirdpartyapplications", 200, 200);
		System.out.println("Retrieved assigned 3rd party apps:");
		System.out.println(resultAsString);
		
		Helpers.check_resultset_for_2_strings(resultAsString, appName, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[0]);
	}
	
	public void test_can_view_assigned_inhouse_party_apps(String deviceId) throws Exception{
		String appId = Helpers.getInHouseAppIdsForInHouseAppNames(logonCookie, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3])[0];
		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/assigned/inhouseapplications", 200, 200);
		System.out.println("Retrieved assigned in-house apps:");
		System.out.println(resultAsString);
		
		Helpers.check_resultset_for_2_strings(resultAsString, appId, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[3]);
	}
	
	public void test_can_view_assigned_profiles(String deviceId) throws Exception{
		String profileName = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[0])[0];
		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/assigned/configurationprofiles", 200, 200);
		System.out.println("Retrieved assigned profiles:");
		System.out.println(resultAsString);
		
		Helpers.check_resultset_for_2_strings(resultAsString, profileName, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[0]);
	}
	
	public void test_can_view_assigned_content(String deviceId) throws Exception{
		String contentName = Helpers.getContentIdsForContentNames(logonCookie, ContentFiles.CONTENT_FILE_NAMES[0])[0];
		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/assigned/content", 200, 200);
		System.out.println("Retrieved assigned content:");
		System.out.println(resultAsString);
		
		Helpers.check_resultset_for_2_strings(resultAsString, contentName, ContentFiles.CONTENT_FILE_NAMES[0]);
	}
}
