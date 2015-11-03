/**
 * 
 */
package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IAdminAccessHandler;
import com.absolute.am.dal.IDal;

/**
 * @author klavin
 *
 */
public class TestAdminHandler {
	
	private static String TEST_ADMIN_UUID = "C383D5AC-6954-4F6E-9273-4D0D0DB580E6";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_prepareAccessForAdmin() throws Exception{
		IDal dal = Util.getDal();
		IAdminAccessHandler adminHandler = dal.getAdminAccessHandler();
		
		adminHandler.refreshAccessForAdmin(TEST_ADMIN_UUID);
		adminHandler.prepareAccessForAdmin(TEST_ADMIN_UUID);
		Thread.sleep(500);
		
		adminHandler.refreshAccessForAdmin(TEST_ADMIN_UUID);
		adminHandler.refreshAccessForAdmin(TEST_ADMIN_UUID);
		
		
		// Based on the static data in the database, check that the data has been processed correctly.
		long deviceId = 1;
		boolean hasAccess = adminHandler.adminHasAccessToDevice(TEST_ADMIN_UUID, deviceId);
		assertFalse("Should deny access to device " + deviceId, hasAccess);
		deviceId = 5;
		hasAccess = adminHandler.adminHasAccessToDevice(TEST_ADMIN_UUID, deviceId);
		assertFalse("Should deny access to device " + deviceId, hasAccess);
		 
		deviceId = 5;
		hasAccess = adminHandler.adminHasAccessToDevice(null, deviceId);	// NOTE: null as parameter.
		assertTrue("Should allow access to device " + deviceId + " when adminUUID=null", hasAccess);

		deviceId = 2;
		hasAccess = adminHandler.adminHasAccessToDevice(TEST_ADMIN_UUID, deviceId);
		assertTrue("Should allow access to device " + deviceId, hasAccess);
		
		deviceId = 14;
		hasAccess = adminHandler.adminHasAccessToDevice(TEST_ADMIN_UUID, deviceId);
		assertTrue("Should allow access to device " + deviceId, hasAccess);
		
		deviceId = 14;
		hasAccess = adminHandler.adminHasAccessToDevice(null, deviceId);
		assertTrue("Should allow access to device " + deviceId + " when adminUUID=null", hasAccess);
				
	}
	
}
