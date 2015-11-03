package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.webapi.util.ResourceUtilities;

import test.com.absolute.testdata.configuration.Actions;
import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testutil.Helpers;

public class MobileDevicesPerformedActionsTest extends LoggedInTest {

	private static final String NONEXISTING_DEVICE_ID = "123456789";
	
	private String deviceId, actionHistoryId;
	private String[] actionUuids;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		setup();

		// tests for deleting performed actions from the mobile device
		test_can_delete_performed_actions_from_mobile_device();
		test_cannot_delete_performed_actions_for_empty_performed_action_id();
		
		//tests for re-execute performed action on the device
		test_can_execute_performed_actions_for_mobile_device();
		test_cannot_execute_performed_actions_for_empty_device_ids();
		test_cannot_execute_performed_actions_for_empty_performed_action_uuids();
		test_cannot_execute_performed_actions_for_non_existing_device_id();
	}
	
	public void setup() throws Exception {
		deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0])[0];
		actionHistoryId = Helpers.getMobileDevicePerformActionHistoryIdByMobileDeviceId(logonCookie, deviceId);
		// The device 'WebAPI Device 2' will be used for tests if there's no performed actions under the device 'WebAPI Device 1'
		if (actionHistoryId == null){
			deviceId = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[1])[0];
			actionHistoryId = Helpers.getMobileDevicePerformActionHistoryIdByMobileDeviceId(logonCookie, deviceId);
		}
		// pass dumb id for the actionHistoryId if it is null still
		if (actionHistoryId == null) {
			actionHistoryId = "99999";
		}
		actionUuids = Helpers.getActionUuidsForActionNames(logonCookie, Actions.ACTION_NAMES[0], Actions.ACTION_NAMES[1]);
		
		Assert.assertTrue("The device '" + MobileDevices.MOBILE_DEVICE_NAMES[0] + "' or '" + MobileDevices.MOBILE_DEVICE_NAMES[1] + "' is not existing in the system.", deviceId.length() > 0);
		Assert.assertTrue("There's no performed actions against the device '" + MobileDevices.MOBILE_DEVICE_NAMES[0] + "' or '" + MobileDevices.MOBILE_DEVICE_NAMES[1] + "' existing in the system.", actionHistoryId.length() > 0);
		Assert.assertTrue("The actions '" + Actions.ACTION_NAMES[0] + "' and '" + Actions.ACTION_NAMES[1] + "'should be existing in the system.", 
				actionUuids.length == 2);
	}
	
	private void test_can_delete_performed_actions_from_mobile_device() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionHistoryIds\":[" + actionHistoryId + "]}");
		
		// delete performed action from the device
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/removeactionsfromdevices", sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		// check that the perform action have been deleted successfully
		Assert.assertFalse(actionHistoryId == Helpers.getMobileDevicePerformActionHistoryIdByMobileDeviceId(logonCookie, deviceId));
	}
	
	private void test_cannot_delete_performed_actions_for_empty_performed_action_id() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("COMMANDS_ACTION_HISTORY_IDS_ARE_EMPTY_IN_REMOVEACTIONSFROMDEVICES", 
				null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_DEVICE_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionHistoryIds\":[]}");
		
		// delete performed action from the device
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/removeactionsfromdevices", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		// check that the perform action have been deleted successfully
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_can_execute_performed_actions_for_mobile_device() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"deviceIds\":[" + deviceId + "],");
		sb.append("\"actionUuids\":[\"" + actionUuids[0] + "\",\"" + actionUuids[1]+ "\"],\"executeImmediately\":true}");
		
		// re-execute performed action from the device
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsondevices", sb.toString(),
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	private void test_cannot_execute_performed_actions_for_empty_device_ids() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("COMMANDS_DEVICE_IDS_ARE_EMPTY_IN_EXECUTEACTIONSONDEVICES", 
				null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_DEVICE_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"deviceIds\":[],");
		sb.append("\"actionUuids\":[\"" + actionUuids[0] + "\",\"" + actionUuids[1]+ "\"],\"executeImmediately\":true}");
		
		// re-execute performed action from the device
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsondevices", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		// check that the perform action have been deleted successfully
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_cannot_execute_performed_actions_for_empty_performed_action_uuids() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("COMMANDS_ACTION_HISTORY_IDS_ARE_EMPTY_IN_EXECUTEACTIONSONDEVICES", 
				null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_DEVICE_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"deviceIds\":[" + deviceId + "],");
		sb.append("\"actionUuids\":[]}");
		
		// re-execute performed action from the device
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsondevices", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		// check that the perform action have been deleted successfully
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	private void test_cannot_execute_performed_actions_for_non_existing_device_id() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("NO_DEVICE_FOUND_FOR_ID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_DEVICE_ID);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\"deviceIds\":[" + NONEXISTING_DEVICE_ID + "],");
		sb.append("\"actionUuids\":[\"" + actionUuids[0] + "\",\"" + actionUuids[1]+ "\"],\"executeImmediately\":true}");
		
		// re-execute performed action from the device
		String response = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/executeactionsondevices", sb.toString(),
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		// check that the perform action have been deleted successfully
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
}