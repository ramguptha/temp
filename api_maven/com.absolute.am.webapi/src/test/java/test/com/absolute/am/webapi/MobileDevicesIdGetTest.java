/**
 * 
 */
package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;


/**
 * @author klavin
 *
 */
public class MobileDevicesIdGetTest extends LoggedInTest {
	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_info_for_device() throws Exception {
		// this test test end point /api/mobiledevices/id GET
				
		String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];
		Integer deviceIdAsInt = Integer.parseInt(deviceId);
		
		// device "Bill's iPad" has id = 20
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId, 200, 200);
		System.out.println("ResultString:");
		System.out.println(resultAsString);
		Helpers.check_first_2_entries_of_resultset(resultAsString, deviceIdAsInt, MobileDevices.MOBILE_DEVICE_NAMES[0]);
	}
	
}
