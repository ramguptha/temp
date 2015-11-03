package test.com.absolute.am.webapi;

	import org.junit.Test;

import test.com.absolute.testdata.configuration.Administrators;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

import org.junit.experimental.categories.Category;
	
/**
*
*
*/
public class MobileDevicesAdministratorsTest extends LoggedInTest {

		@Test
		@Category(com.absolute.util.helper.FastTest.class)
		public void can_get_administrators_for_device() throws Exception {
			// this tests end point /api/mobiledevices/{id}/administrators GET
					
			String deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0]; // qaams1  id=9

			String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceId + "/administrators", 200, 200);
			System.out.println("ResultString:");
			System.out.println(resultAsString);
			Helpers.check_first_field_of_first_entry_of_resultset(resultAsString, Administrators.ACTION_NAMES[0]);
			
		}
		
}
