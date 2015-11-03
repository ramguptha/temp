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
 * @author klavin
 *
 */
public class DevicesForContent extends LoggedInTest {	

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_devices_for_content() throws Exception {
		
		String contentIds[] = Helpers.getContentIdsForContentNames(logonCookie, ContentFiles.CONTENT_FILE_NAMES[0]);
		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + contentIds[0] + "/devices", 200, 200);
		System.out.println("ResultAsString: " + resultAsString);
		assertTrue("Check for " + MobileDevices.MOBILE_DEVICE_NAMES[0] + " in result for " + ContentFiles.CONTENT_FILE_NAMES[0], resultAsString.contains(MobileDevices.MOBILE_DEVICE_NAMES[0]));
		assertTrue("Check for " + MobileDevices.MOBILE_DEVICE_NAMES[1] + " in result for " + ContentFiles.CONTENT_FILE_NAMES[0], resultAsString.contains(MobileDevices.MOBILE_DEVICE_NAMES[1]));
	}
	
}
