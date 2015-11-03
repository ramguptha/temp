package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

/**
*
*
*/
public class MobileDevicesCertificatesTest extends LoggedInTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_certificate_for_ios_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/certificates GET
				
		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0]; // qaams1

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/certificates", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		Helpers.check_first_2_entries_of_resultset(resultAsString, "Unique Device Identity", 1);
		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_get_certificate_for_android_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/certificates GET
				
		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0]; // qaams1

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/certificates", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		
		// no rows - only metadata is returned
		//String expectedResultString = "{\"metaData\":{\"totalRows\":0,\"columnMetaData\":[{\"ShortDisplayName\":\"Certificate Name\",\"MaxWidth\":1000,\"Description\":\"The name of the certificate.\",\"MinWidth\":50,\"DisplayName\":\"Mobile Device Installed Certificate Name\",\"Truncation\":3,\"ColumnDataType\":\"String\",\"InfoItemID\":\"D0F8C0AC-5080-4C75-A127-FC1C62EE8FC9\",\"Alignment\":1,\"Width\":150},{\"ShortDisplayName\":\"Certificate Is Identity\",\"MaxWidth\":1000,\"Description\":\"Whether the certificate is the identity certificate of the device.\",\"MinWidth\":50,\"DisplayName\":\"Mobile Device Installed Certificate Is Identity\",\"Truncation\":3,\"ColumnDataType\":\"Number\",\"InfoItemID\":\"61786DBB-CBA3-4C37-837A-E2E203A1A0DF\",\"Alignment\":3,\"Width\":150,\"DisplayType\":\"FormatBoolean\"}]},\"rows\":[]}";
		//org.junit.Assert.assertEquals(expectedResultString, resultAsString);
		Helpers.check_for_empty_resultset(resultAsString);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_get_certificate_for_nonexisting_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/certificates GET
				
		String deviceId = "9999"; //non-existing device id

		Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/certificates", 404, 404);

		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
		
	}
	
}
