/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import org.apache.http.HttpStatus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.InhouseApplications;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testdata.configuration.ThirdPartyApplications;
import test.com.absolute.testutil.Helpers;


/**
 * @author klavin
 *
 */
public class MobileDevicesApplications extends LoggedInTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_applications_for_device() throws Exception {
		// this test test end point /api/mobiledevices/id/applications GET

		String deviceIds[] = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);
		String iOSDeviceId = deviceIds[0];
		String androidDeviceId = deviceIds[1];

		// Check iPhone type device
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + iOSDeviceId + "/applications", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		assertTrue("AbsoluteApps should be installed.", resultAsString.contains(InhouseApplications.IN_HOUSE_APPLICATION_NAMES[0]));
		
		// Check Android type device
		resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + androidDeviceId + "/applications", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		assertTrue("AbsoluteApps should be installed.", resultAsString.contains(InhouseApplications.IN_HOUSE_APPLICATION_NAMES[0]));
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_install_app_without_deviceIds() throws Exception {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[],");
		sb.append("\"inHouseAppIds\":[2],");
		sb.append("\"thirdPartyAppIds\":[11]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_install_app_without_app_ids() throws Exception {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[5],");
		sb.append("\"inHouseAppIds\":[],");
		sb.append("\"thirdPartyAppIds\":[]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_install_in_house_app_for_mixed_devices() throws Exception {
		String deviceIds[] = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);
		
		String androidDeviceId = deviceIds[1];
		String iOSDeviceId = deviceIds[0];

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(androidDeviceId);
		sb.append(",");
		sb.append(iOSDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[2],");
		sb.append("\"thirdPartyAppIds\":[11]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_install_in_house_app_for_missing_app_id() throws Exception {
		String androidDeviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0];

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(androidDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[21],");
		sb.append("\"thirdPartyAppIds\":[]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(androidDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[2],");
		sb.append("\"thirdPartyAppIds\":[11]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_install_in_house_app_for_mixed_app_types() throws Exception {
		String iOSDeviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(iOSDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[2],"); // Android - Astro File Manager / Browser
		sb.append("\"thirdPartyAppIds\":[1]"); // Android - InHouseAndroidForAM.apk
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
		
		sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(iOSDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[],"); // Android - Astro File Manager / Browser
		sb.append("\"thirdPartyAppIds\":[11]"); // Android - InHouseAndroidForAM.apk
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);


	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_install_apps_on_android_device() throws Exception {
		String androidDeviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0];
		
		String[] thirdPartyAppIds = Helpers.getThirdPartyAppIdsForThirdPartyAppNames(logonCookie, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[1]);
		String thirdPartyAppId = thirdPartyAppIds[0];

		String[] inHouseAppIds = Helpers.getInHouseAppIdsForInHouseAppNames(logonCookie, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[1]);
		String inHouseAppId = inHouseAppIds[0];
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(androidDeviceId);
		sb.append("],");
		sb.append("\"inHouseAppIds\":[" + inHouseAppId + "],");
		sb.append("\"thirdPartyAppIds\":[" + thirdPartyAppId + "]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/installapplication", 
				sb.toString(), 200, 299);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore
	public void can_remove_applications_from_device() throws Exception {
		String androidDeviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0]; 

		String[] thirdPartyAppIds = Helpers.getThirdPartyAppIdsForThirdPartyAppNames(logonCookie, ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES[1]);
		String thirdPartyAppId = thirdPartyAppIds[0];

		String[] inHouseAppIds = Helpers.getInHouseAppIdsForInHouseAppNames(logonCookie, InhouseApplications.IN_HOUSE_APPLICATION_NAMES[1]);
		String inHouseAppId = inHouseAppIds[0];
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceId\":");
		sb.append(androidDeviceId);
		sb.append(",");
		sb.append("\"applicationIds\":[" + thirdPartyAppId + "," + inHouseAppId + "]");
		sb.append("}");
		
		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/deleteapplication", 
				sb.toString(), 200, 299);

	}

}
