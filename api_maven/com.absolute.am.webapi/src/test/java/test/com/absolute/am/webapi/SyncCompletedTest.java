/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

import com.absolute.util.PropertyList;

/**
 * @author dlavin
 *
 */
public class SyncCompletedTest extends LoggedInTest {	
	private static final String SYNC_COMPLETED_API = "api/syncnotify/synccompleted";
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_send_sync_completed() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		PropertyList syncCompletedEvent = new PropertyList();
		syncCompletedEvent.put("Server", "blah.blah");
		syncCompletedEvent.put("Port", 3971);
		syncCompletedEvent.put("UpdatedTables", new String[] {"iphone_info", "iOS_policies", "iOS_policies_media", "iOS_policies_devices"});
		
		PropertyList event = new PropertyList();
		event.put("SyncCompletedEvent", syncCompletedEvent);
		
		String requestBody = event.toXMLString();
		System.out.println("Request body=" + requestBody);
				
		Helpers.postRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SYNC_COMPLETED_API, 
				"application/x-www-form-urlencoded",//"application/xml", 
				requestBody, 
				200, 
				299);
	}
	
}
