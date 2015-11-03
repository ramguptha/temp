/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.ContentFiles;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 *
 */
public class ContentForDevices extends LoggedInTest {	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content_for_device() throws Exception {
		
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);

		// Device 1: iPhone
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceIds[0] + "/assigned/content", 200, 200);
		System.out.println(MobileDevices.MOBILE_DEVICE_NAMES[0] + " assigned content:" + resultAsString);
		assertTrue("Checking for " + ContentFiles.CONTENT_FILE_NAMES[0] + " in result for " + MobileDevices.MOBILE_DEVICE_NAMES[0], resultAsString.contains(ContentFiles.CONTENT_FILE_NAMES[0]));
		
		// Device 2: Samsung phone
		resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + MOBILE_DEVICES_API + "/" + deviceIds[1] + "/assigned/content", 200, 200);
		System.out.println(MobileDevices.MOBILE_DEVICE_NAMES[1] + " assigned content:" + resultAsString);
		assertTrue("Checking for " + ContentFiles.CONTENT_FILE_NAMES[0] + " in result for " + MobileDevices.MOBILE_DEVICE_NAMES[1], resultAsString.contains(ContentFiles.CONTENT_FILE_NAMES[0]));
	}
}
