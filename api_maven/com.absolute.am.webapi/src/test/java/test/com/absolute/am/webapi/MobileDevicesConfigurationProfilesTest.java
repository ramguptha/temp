package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.ConfigurationProfiles;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;


/**
 *
 *
 */
public class MobileDevicesConfigurationProfilesTest extends LoggedInTest {
	
	private String deviceIdiOS;
	private	String deviceIdAndroid;
	private	String configProfileIdiOS1;
	private	String configProfileIdiOS2;
	private	String configProfileIdAndroid1;
	private	String configProfileIdAndroid2;
	//note that "installed id's" are from iphone_installed_profile_info
	//vs iOS_configuration_profile table
	private	String installedConfigProfileIdiOS1;
	private	String installedConfigProfileIdiOS2;
	private	String installedConfigProfileIdAndroid1;
	private	String installedConfigProfileIdAndroid2;
	private	final String configProfileIdNonExisting = "9999999";
		
	
	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void test_master_list() throws Exception {
		
		deviceIdiOS = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];
		deviceIdAndroid = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0];
		configProfileIdiOS1 = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[0])[0];
		configProfileIdiOS2 = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[1])[0];
		configProfileIdAndroid1 = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[0])[0];
		configProfileIdAndroid2 = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[1])[0];
		
		
		//test order is of the essence
		cannot_install_configuration_profile_without_deviceIds();
		cannot_install_configuration_profile_without_configuration_profile_id();
		cannot_install_configuration_profile_for_mixed_devices();
		cannot_install_configuration_profile_for_missing_configuration_profile_id();
		can_install_configuration_profile_on_ios_device();
		Thread.sleep(10000);
		can_install_configuration_profiles_on_android_device();
		Thread.sleep(60000); // 1 minutes - Sometimes it takes too much time to install profiles
		can_get_configuration_profiles_for_ios_device();
		can_get_configuration_profiles_for_android_device();
		getInstalledConfigProfileIds();
		can_remove_configuration_profile_from_ios_device();
		Thread.sleep(5000); // Without pause, sync service does not refresh data correctly. Needed it for multiple UNit test run
		can_remove_configuration_profiles_from_android_device();
	}

	public void cannot_install_configuration_profile_without_deviceIds() throws Exception {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append("],");
		sb.append("\"configurationProfileIds\":[");
		sb.append(configProfileIdiOS1);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void cannot_install_configuration_profile_without_configuration_profile_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdAndroid);
		sb.append("],");
		sb.append("\"configurationProfileIds\":[]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void cannot_install_configuration_profile_for_mixed_devices() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append(",");
		sb.append(deviceIdAndroid);
		sb.append("],");
		sb.append("\"configurationProfileIds\":[");
		sb.append(configProfileIdiOS1);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	public void cannot_install_configuration_profile_for_missing_configuration_profile_id() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("],");
		sb.append("\"configurationProfileIds\":[");
		sb.append(configProfileIdNonExisting);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void can_install_configuration_profile_on_ios_device() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("],");
		sb.append("\"configurationProfileIds\":[");
		sb.append(configProfileIdiOS1);
		sb.append(",");
		sb.append(configProfileIdiOS2);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), 200, 299);
	}

	public void can_install_configuration_profiles_on_android_device() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdAndroid);
		sb.append("],");
		sb.append("\"configurationProfileIds\":[");
		sb.append(configProfileIdAndroid1);
		sb.append(",");
		sb.append(configProfileIdAndroid2);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installconfigurationprofile", 
				sb.toString(), 200, 299);
	}

	public void can_get_configuration_profiles_for_ios_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/configurationprofiles GET
				
		// Check iPhone type device
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceIdiOS + "/configurationprofiles", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[0], ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[1]);
		assertTrue(result);
		
	}

	public void can_get_configuration_profiles_for_android_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/configurationprofiles GET
				
		// Check iPhone type device
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceIdAndroid + "/configurationprofiles", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[0], ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[1]);
		assertTrue(result);
	}
	
	public void can_remove_configuration_profile_from_ios_device() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("]");
		sb.append(",");
		sb.append("\"configurationProfileIds\":[");
		sb.append(installedConfigProfileIdiOS1);
		sb.append(",");
		sb.append(installedConfigProfileIdiOS2);
		sb.append("]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/removeconfigurationprofile", 
				sb.toString(), 200, 299);

	}

	public void can_remove_configuration_profiles_from_android_device() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdAndroid);
		sb.append("]");
		sb.append(",");
		sb.append("\"configurationProfileIds\":[");
		sb.append(installedConfigProfileIdAndroid1);
		sb.append(",");
		sb.append(installedConfigProfileIdAndroid2);
		sb.append("]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/removeconfigurationprofile", 
				sb.toString(), 200, 299);

	}
	
	private void getInstalledConfigProfileIds() throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		installedConfigProfileIdiOS1 = Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndDeviceId(logonCookie, deviceIdiOS, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[0])[0];
		installedConfigProfileIdiOS2 = Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndDeviceId(logonCookie, deviceIdiOS, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS[1])[0];
		installedConfigProfileIdAndroid1 = Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndDeviceId(logonCookie, deviceIdAndroid, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[0])[0];
		installedConfigProfileIdAndroid2 = Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndDeviceId(logonCookie, deviceIdAndroid, ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID[1])[0];
	}

}
