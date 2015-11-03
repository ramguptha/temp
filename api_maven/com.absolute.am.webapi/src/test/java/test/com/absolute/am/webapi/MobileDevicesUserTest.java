package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

/**
*
*
*/
public class MobileDevicesUserTest extends LoggedInTest {
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_user_for_device() throws Exception {
		// this tests end point /api/mobiledevices/{id}/user GET
				
		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];

		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/user", 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		Helpers.check_resultset_for_2_strings(resultAsString, "test_user", "test_domain");
	}
}
