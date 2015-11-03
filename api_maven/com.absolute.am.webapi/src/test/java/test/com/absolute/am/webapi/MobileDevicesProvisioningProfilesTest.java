package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testdata.configuration.ProvisioningProfiles;
import test.com.absolute.testutil.Helpers;


/**
 *
 *
 */
public class MobileDevicesProvisioningProfilesTest extends LoggedInTest {
	
	private String deviceIdiOS;
	private String deviceIdAndroid;
	private	String provisioningProfileId1;
	private	String provisioningProfileId2;
	//note that "installed id's" are from iphone_installed_provisioningprofile_info
	//vs iOS_provisioning_profiles table
	private	String installedProvisioningProfileId1;
	private	String installedProvisioningProfileId2;
	private	final String provisioningProfileIdNonExisting = "9999999";
		
	@Test
	@Category(com.absolute.util.helper.DeviceDependentTest.class)
	public void test_master_list() throws Exception {
		
		provisioningProfileId1 = Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, ProvisioningProfiles.PROVISIONING_PROFILE_NAMES[0])[0];
		provisioningProfileId2 = Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, ProvisioningProfiles.PROVISIONING_PROFILE_NAMES[0])[0];
		deviceIdAndroid = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0];
		deviceIdiOS = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];
		
		
		//test order is of the essence
//most tests are commented out because corresponding endpoints are not implemented
//		cannot_install_provisioning_profile_without_deviceIds();
//		cannot_install_provisioning_profile_without_provisioning_profile_id();
//		cannot_install_provisioning_profile_for_mixed_devices();
//		cannot_install_provisioning_profile_for_missing_provisioning_profile_id();
//		can_install_provisioning_profile_on_ios_device();
		can_get_provisioning_profiles_for_ios_device();
//		can_remove_provisioning_profile_from_ios_device();
	}

	public void cannot_install_provisioning_profile_without_deviceIds() throws Exception {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append("],");
		sb.append("\"provisioningProfileIds\":[");
		sb.append(provisioningProfileId1);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installprovisioningprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void cannot_install_provisioning_profile_without_provisioning_profile_id() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("],");
		sb.append("\"provisioningProfileIds\":[]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installprovisioningprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void cannot_install_provisioning_profile_for_mixed_devices() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append(",");
		sb.append(deviceIdAndroid);
		sb.append("],");
		sb.append("\"provisioningProfileIds\":[");
		sb.append(provisioningProfileId1);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installprovisioningprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	public void cannot_install_provisioning_profile_for_missing_provisioning_profile_id() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("],");
		sb.append("\"provisioningProfileIds\":[");
		sb.append(provisioningProfileIdNonExisting);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installprovisioningprofile", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	public void can_install_provisioning_profile_on_ios_device() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(deviceIdiOS);
		sb.append("],");
		sb.append("\"provisioningProfileIds\":[");
		sb.append(provisioningProfileId1);
		sb.append(",");
		sb.append(provisioningProfileId2);
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installprovisioningprofile", 
				sb.toString(), 200, 299);
	}

	public void can_get_provisioning_profiles_for_ios_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/provisioningprofiles GET
				
		// Check iPhone type device
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceIdiOS + "/provisioningprofiles", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		boolean result = Helpers.check_resultset_for_2_strings(resultAsString, ProvisioningProfiles.PROVISIONING_PROFILE_NAMES[0], ProvisioningProfiles.PROVISIONING_PROFILE_NAMES[0]);
		assertTrue(result);
	}
	
	public void can_remove_provisioning_profile_from_ios_device() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceId\":");
		sb.append(deviceIdiOS);
		sb.append(",");
		sb.append("\"provisioningProfileIds\":[");
		sb.append(installedProvisioningProfileId1);
		sb.append(",");
		sb.append(installedProvisioningProfileId2);
		sb.append("]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/deleteprovisioningprofile", 
				sb.toString(), 200, 299);

	}

}
