package test.com.absolute.am.webapi;

import java.io.File;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.FileUtilities;

import test.com.absolute.testutil.Helpers;

public class ActionTest extends LoggedInTest {
	private static final String TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION = "test_action_update_device_information_1";
	private static final String TEST_ACTION_NAME_SEND_EMAIL = "test_action_send_email_1";
	private static final String TEST_ACTION_NAME_SET_WALLPAPER = "test_action_set_wallpaper_1";
	private static final String TEST_ACTION_NAME_SET_WALLPAPER_OVER_SIZE_IMAGE = "test_action_set_wallpaper_over_size_image";
	private static final String TEST_ACTION_NAME_SET_CUSTOM_FIELD_WITH_INVALID_NUMBER = "test_action_set_custom_field_with_invalid_number";
	private static final String NONEXISTING_ACTION_ID = "123456789";
	private static final String DUMMY_FIELD_UUID = "9D4DA03C-2877-4293-9512-D919F51900C8";
	
	private String base64_Image_3KB = "";
	private String base64_Image_5MB = ""; 
	
	public void setup() throws Exception {
		String fileUrlLocation = ActionTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String base64_Image_5KB_FilePath =  new File(fileUrlLocation).getParent() + "\\..\\src\\test\\resources\\files\\Base64_Image_String_3KB.txt";
		String base64_Image_5MB_FilePath =  new File(fileUrlLocation).getParent() + "\\..\\src\\test\\resources\\files\\Base64_Image_String_5MB.txt";
		
		base64_Image_3KB = FileUtilities.loadTextFileAsString(base64_Image_5KB_FilePath);;
		base64_Image_5MB = FileUtilities.loadTextFileAsString(base64_Image_5MB_FilePath);; 
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		setup();

		//test order is of the essence
		// create actions 
		test_can_create_action_update_device_information();
		test_can_create_action_send_email();
		test_can_create_action_set_wallpaper();
		
		Thread.sleep(5000);
		
		// get wallpaper picture
		test_can_get_wallpaper_picture();
		test_cannot_get_wallpaper_picture_with_non_existsing_action_id();
		test_cannot_get_wallpaper_picture_for_non_set_wallpaper_typed_action();
		
		// fail to create actions
		test_cannot_create_action_set_wallpaper_with_over_size_image();
		test_cannot_create_action_set_custom_field_with_invalid_number();		
		test_cannot_create_action_set_custom_field_with_invalid_date();

		// update actions
		test_can_update_action_update_device_information();
		test_can_update_action_send_email();
		test_can_update_action_set_wallpaper();

		Thread.sleep(5000);
		
		// fail to update actions
		test_cannot_update_action_for_non_existing_action_id();
		test_cannot_update_action_for_duplicate_action_name();
		test_cannot_update_action_for_incorrect_seed();

		// delete actions
		test_delete_action_for_action_id();
		test_delete_actions();
		
		// fail to delete actions
		test_cannot_delete_action_for_non_existing_action_id();
		test_cannot_delete_actions_for_non_existing_action_id_included();
	}
	
	public void test_can_create_action_update_device_information() throws Exception {
		// create 'UpdateDeviceInformation(8)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "\","
				+ "\"actionType\":8,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "\","
				+ "\"supportedPlatforms\":3}";
				
		test_create_action(TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION, requestBody);
	}
	
	public void test_can_create_action_send_email() throws Exception {
		// create 'SendEmail(3)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_SEND_EMAIL + "\","
				+ "\"actionType\":3,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SEND_EMAIL + "\","
				+ "\"supportedPlatforms\":3, "
				+ "\"actionData\":{\"EmailTo\":\"roger123@email-to.com\","
				+ "\"EmailCC\":\"roger123@email-cc.com\","
				+ "\"EmailSubject\":\"Here is email subject: " + TEST_ACTION_NAME_SEND_EMAIL + "\","
				+ "\"EmailMessageText\":\"Here is email message text: " + TEST_ACTION_NAME_SEND_EMAIL + "\"}}";
		
		test_create_action(TEST_ACTION_NAME_SEND_EMAIL, requestBody);
	}
	
	public void test_can_create_action_set_wallpaper() throws Exception {
		// create 'SetWallpaper(9)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_SET_WALLPAPER + "\","
				+ "\"actionType\":9,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SET_WALLPAPER + "\","
				+ "\"supportedPlatforms\":1, "
				+ "\"actionData\":{\"ApplyToHomeScreen\":true,"
				+ "\"ApplyToLockScreen\":true,"
				+ "\"WallpaperPicture\":\"" + base64_Image_3KB + "\"}}";
		
		test_create_action(TEST_ACTION_NAME_SET_WALLPAPER, requestBody);
	}

	public void test_can_get_wallpaper_picture() throws Exception
	{
		String[] actionIds = Helpers.getActionIdsForActionNames(logonCookie, TEST_ACTION_NAME_SET_WALLPAPER);
		Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + actionIds[0] + "/wallpaper",
				HttpStatus.SC_OK,
				HttpStatus.SC_OK);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	public void test_cannot_get_wallpaper_picture_with_non_existsing_action_id() throws Exception
	{
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("NO_ACTION_FOUND_FOR_ID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_ACTION_ID);
		
		String result = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + NONEXISTING_ACTION_ID + "/wallpaper",
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);
		
		Assert.assertTrue(result.contains(expectedErrorMessage));
	}
	
	public void test_cannot_get_wallpaper_picture_for_non_set_wallpaper_typed_action() throws Exception
	{
		String[] actionIds = Helpers.getActionIdsForActionNames(logonCookie, TEST_ACTION_NAME_SEND_EMAIL);
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("ACTIONS_NOT_SET_WALLPAPER_TYPED", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_ACTION_ID);
		
		String result = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + actionIds[0] + "/wallpaper",
				HttpStatus.SC_BAD_REQUEST,
				HttpStatus.SC_BAD_REQUEST);
		
		Assert.assertTrue(result.contains(expectedErrorMessage));
	}
	
	public void test_cannot_create_action_set_wallpaper_with_over_size_image() throws Exception {
		// create 'SetWallpaper(9)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_SET_WALLPAPER_OVER_SIZE_IMAGE + "\","
				+ "\"actionType\":9,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SET_WALLPAPER_OVER_SIZE_IMAGE + "\","
				+ "\"supportedPlatforms\":1, "
				+ "\"actionData\":{\"ApplyToHomeScreen\":true,"
				+ "\"ApplyToLockScreen\":true,"
				+ "\"WallpaperPicture\":\"" + base64_Image_5MB + "\"}}";
		// can't create another action with over size image
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API, requestBody, 
						HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
	}
	
	public void test_cannot_create_action_set_custom_field_with_invalid_number() throws Exception {
		// create 'SetCustomField(14)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_SET_CUSTOM_FIELD_WITH_INVALID_NUMBER + "\","
				+ "\"actionType\":14,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SET_CUSTOM_FIELD_WITH_INVALID_NUMBER + "\","
				+ "\"supportedPlatforms\":1, "
				+ "\"actionData\":{\"DataType\":2,"
				+ "\"DataValue\":true,"
				+ "\"FieldID\":\"" + DUMMY_FIELD_UUID + "\","
				+ "\"Name\":\"dummy custom field\","
				+ "\"RemoveValue\":0}}";
		// can't create action when the invalid number passed in
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API, requestBody, 
						HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
	}
	
	public void test_cannot_create_action_set_custom_field_with_invalid_date() throws Exception {
		// create 'SetCustomField(14)' typed action
		String requestBody = "{\"name\":\"" + TEST_ACTION_NAME_SET_CUSTOM_FIELD_WITH_INVALID_NUMBER + "\","
				+ "\"actionType\":14,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SET_CUSTOM_FIELD_WITH_INVALID_NUMBER + "\","
				+ "\"supportedPlatforms\":1, "
				+ "\"actionData\":{\"DataType\":4,"
				+ "\"DataValue\":\"2015-04-14TAA:46:09Z\","
				+ "\"FieldID\":\"" + DUMMY_FIELD_UUID + "\","
				+ "\"Name\":\"dummy custom field\","
				+ "\"RemoveValue\":0}}";
		// can't create action when the invalid number passed in
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API, requestBody, 
						HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
	}
	
	public void test_can_update_action_update_device_information() throws Exception {
		// get the action id by the name, the id should be retrieved since this action has been added with previous tests
		String testactionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION)[0];
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":1,\"name\":\"" + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"actionType\":8,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"supportedPlatforms\":3}";
		
		test_update_action(testactionId, requestBody);
	}
	
	public void test_can_update_action_send_email() throws Exception {
		// get the action id by the name, the id should be retrieved since this action has been added with previous tests
		String testactionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_SEND_EMAIL)[0];
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":1,\"name\":\"" + TEST_ACTION_NAME_SEND_EMAIL + "-updated\","
				+ "\"actionType\":3,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SEND_EMAIL + "-updated\","
				+ "\"supportedPlatforms\":3, "
				+ "\"actionData\":{\"EmailTo\":\"roger123@email-to.com\","
				+ "\"EmailCC\":\"roger123@email-cc.com\","
				+ "\"EmailSubject\":\"Here is email subject: " + TEST_ACTION_NAME_SEND_EMAIL + "-updated\","
				+ "\"EmailMessageText\":\"Here is email message text: " + TEST_ACTION_NAME_SEND_EMAIL + "-updated\"}}";
		
		test_update_action(testactionId, requestBody);
	}
	
	public void test_can_update_action_set_wallpaper() throws Exception {
		// get the action id by the name, the id should be retrieved since this action has been added with previous tests
		String testactionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_SET_WALLPAPER)[0];
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":1,\"name\":\"" + TEST_ACTION_NAME_SET_WALLPAPER + "-updated\","
				+ "\"actionType\":9,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_SET_WALLPAPER + "-updated\","
				+ "\"supportedPlatforms\":1, "
				+ "\"actionData\":{\"ApplyToHomeScreen\":true,"
				+ "\"ApplyToLockScreen\":true,"
				+ "\"WallpaperPicture\":\"" + base64_Image_3KB + "\"}}";
		
		test_update_action(testactionId, requestBody);
	}
	
	public void test_cannot_update_action_for_non_existing_action_id() throws Exception {
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":1,\"name\":\"" + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"actionType\":8,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"supportedPlatforms\":3}";
		System.out.println("Request body=" + requestBody);

		// put the request and check the response status
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + NONEXISTING_ACTION_ID, 
				requestBody, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	public void test_cannot_update_action_for_duplicate_action_name() throws Exception {
		// get the action id by the name, the id should be retrieved since this action has been added with previous tests
		String testactionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated")[0];
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":1,\"name\":\"" + TEST_ACTION_NAME_SEND_EMAIL + "-updated\","
				+ "\"actionType\":8,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"supportedPlatforms\":3}";
		System.out.println("Request body=" + requestBody);

		// put the request and check the response status
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + testactionId, 
				requestBody, HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	public void test_cannot_update_action_for_incorrect_seed() throws Exception {
		// get the action id by the name, the id should be retrieved since this action has been added with previous tests
		String testactionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated")[0];
		// create Json string for 'UpdateDeviceInformation(8)' typed action whcih added with previous tests
		String requestBody = "{\"seed\":100,\"name\":\"" + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"actionType\":8,"
				+ "\"description\":\"here is test sample: " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION + "-updated\","
				+ "\"supportedPlatforms\":3}";
		System.out.println("Request body=" + requestBody);

		// put the request and check the response status
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + testactionId, 
				requestBody, HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	public void test_delete_action_for_action_id() throws Exception {
		// assume that a action with the name "TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION" has been successfully created in the previous tests
		String actionId = Helpers.getActionIdsForActionNames(logonCookie, TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION)[0];
		System.out.println("Deleting action with id=" + actionId);
		Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/delete/" + actionId, 
				null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT );
		
		actionId = null;
		try {
			actionId = Helpers.getActionIdsForActionNames(logonCookie,TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION)[0];
			Assert.assertNull(actionId);
		} catch (RuntimeException ex) {
			//action should not exist
			Assert.assertTrue(ex.getMessage().contains("ID not found for action " + TEST_ACTION_NAME_UPDATE_DEVICE_INFORMATION));
			Assert.assertTrue(true);
		}
	}
	
	public void test_cannot_delete_action_for_non_existing_action_id() throws Exception {
		System.out.println("Deleting action with id=" + NONEXISTING_ACTION_ID);
		Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/delete/" + NONEXISTING_ACTION_ID, 
				null, HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	public void test_delete_actions() throws Exception {
		// assume that a action with the name "TEST_ACTION_NAME_SEND_EMAIL" and "TEST_ACTION_NAME_SET_WALLPAPER" have been successfully created in the previous tests
		String[] actionIds = Helpers.getActionIdsForActionNames(logonCookie, TEST_ACTION_NAME_SEND_EMAIL + "-updated", TEST_ACTION_NAME_SET_WALLPAPER + "-updated");
		String requestBody = Arrays.toString(actionIds);
		System.out.println("Deleting actions: " + requestBody);
		
		// Call the 'api/actions/delete' to delete both actions, then check the status of the response
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/delete", requestBody, 
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		actionIds = null;
		try {
			actionIds = Helpers.getActionIdsForActionNames(logonCookie, TEST_ACTION_NAME_SEND_EMAIL + "-updated", TEST_ACTION_NAME_SET_WALLPAPER + "-updated");
			Assert.assertNull(actionIds);
		} catch (RuntimeException ex) {
			//action should not exist
			Assert.assertTrue(ex.getMessage().contains("ID not found for action " + TEST_ACTION_NAME_SEND_EMAIL));
			Assert.assertTrue(true);
		}
	}
	
	public void test_cannot_delete_actions_for_non_existing_action_id_included() throws Exception {
		String requestBody = "[1," + NONEXISTING_ACTION_ID + "]";

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/delete", requestBody, 
				HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_REQUEST);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	private void test_create_action(String actionName, String requestBody) throws Exception {
		System.out.println("Request body=" + requestBody);

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API, requestBody);

		// can't create another action with a duplicate name
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API, requestBody, 
				HttpStatus.SC_CONFLICT, HttpStatus.SC_CONFLICT);
		
		String testactionId = null;
		
		try {
			testactionId = Helpers.getActionIdsForActionNames(logonCookie,actionName)[0];
			Assert.assertNotNull(testactionId);
		} catch (RuntimeException ex) {
			//action does not exist
			Assert.assertTrue(false);
		}
	}
	
	private void test_update_action(String actionId, String requestBody) throws Exception {
		System.out.println("Request body=" + requestBody);

		// put the request and check the response status
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/" + actionId, 
				requestBody, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		//test passed if reached this line
		Assert.assertTrue(true);
	}
}
