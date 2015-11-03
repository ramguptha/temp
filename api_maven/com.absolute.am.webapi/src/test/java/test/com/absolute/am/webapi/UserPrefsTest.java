package test.com.absolute.am.webapi;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class UserPrefsTest extends LoggedInTest {
	private static final String USER_API = Helpers.WEBAPI_BASE_URL
			+ "api/user/prefs";
	private static final String KEY_JSON = "jsonTestValue";
	private static final String KEY_JSON1 = "jsonTestValue1";
	private static final String KEY_JSON2 = "jsonTestValue2";
	private static final String KEY_IMAGE = "imageTestValue";
	private static final String KEY_IMAGE_BIG = "imageBigTestValue";
	public static String cookie = null;

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws IllegalStateException, IOException,
			NoSuchAlgorithmException, KeyManagementException {
		can_put_jsonValue_and_get_it_back();
		can_delete_jsonValue();
		can_put_image_and_get_it_back();
		can_delete_image();
		can_get_list();
		//cannot_post_file_that_is_too_big();
		cleanup();
	}

	public void can_put_jsonValue_and_get_it_back()
			throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		StringBuilder body = new StringBuilder();
		body.append("{");
		body.append("\"serverName\":\"someServerName");
		body.append(",\"serverPort\":\"3751");
		body.append("}");
		byte[] binaryBody = body.toString().getBytes("UTF8");

		String keyId = KEY_JSON;
		String putSetting = USER_API + "/" + keyId;

		test.com.absolute.testutil.Helpers.postRequestGetResponse(logonCookie,
				binaryBody, putSetting, "application/json");

		byte[] getResponse = test.com.absolute.testutil.Helpers
				.doGetReturnResponse(putSetting, logonCookie);
		String responseAsString = new String(getResponse);
		System.out.println("get response=" + responseAsString);

		org.junit.Assert.assertArrayEquals(binaryBody, getResponse);
	}

	public void can_put_image_and_get_it_back() throws IllegalStateException,
			IOException, KeyManagementException, NoSuchAlgorithmException {

		File file = new File("src\\test\\resources\\files\\16KB.png");
		String localFilePath = file.getAbsolutePath();
		byte[] binaryBody = com.absolute.util.FileUtilities
				.loadFile(localFilePath);

		String keyId = KEY_IMAGE;
		String putSetting = USER_API + "/" + keyId;

		test.com.absolute.testutil.Helpers.postRequestGetResponse(logonCookie,
				binaryBody, putSetting, "image/png");

		byte[] getResponse = test.com.absolute.testutil.Helpers
				.doGetReturnResponse(putSetting, logonCookie);

		org.junit.Assert.assertArrayEquals(binaryBody, getResponse);
	}

	public void can_delete_jsonValue() throws ClientProtocolException,
			IOException, NoSuchAlgorithmException, KeyManagementException {

		String keyId = KEY_JSON;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		String getSettingUrl = USER_API + "/" + keyId;
		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie, getSettingUrl, HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);
	}

	public void can_delete_image() throws ClientProtocolException, IOException,
			NoSuchAlgorithmException, KeyManagementException {

		String keyId = KEY_IMAGE;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		String getSettingUrl = USER_API + "/" + keyId;
		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie, getSettingUrl, HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);
	}

	public void can_get_list() throws IllegalStateException, IOException,
			KeyManagementException, NoSuchAlgorithmException {

		StringBuilder body1 = new StringBuilder();
		body1.append("{");
		body1.append("\"serverName1\":\"someServerName1");
		body1.append(",\"serverPort1\":\"3751");
		body1.append("}");
		byte[] binaryBody = body1.toString().getBytes("UTF8");

		String keyId = KEY_JSON1;
		String putSetting = USER_API + "/" + keyId;

		test.com.absolute.testutil.Helpers.postRequestGetResponse(logonCookie,
				binaryBody, putSetting, "application/json");

		StringBuilder body2 = new StringBuilder();
		body2.append("{");
		body2.append("\"serverName2\":\"someServerName2");
		body2.append(",\"serverPort2\":\"3751");
		body2.append("}");
		binaryBody = body2.toString().getBytes("UTF8");

		keyId = KEY_JSON2;
		putSetting = USER_API + "/" + keyId;

		test.com.absolute.testutil.Helpers.postRequestGetResponse(logonCookie,
				binaryBody, putSetting, "application/json");

		String result = Helpers.doGETCheckStatusReturnBody(logonCookie,
				USER_API, HttpStatus.SC_OK, HttpStatus.SC_OK);

		boolean isCorrectList = result.contains(KEY_JSON1)
				&& result.contains(KEY_JSON2);

		org.junit.Assert.assertTrue(isCorrectList);
	}

	// make sure the file is larger than the limit in the application's web.xml
	public void cannot_post_file_that_is_too_big()
			throws ClientProtocolException, IOException,
			NoSuchAlgorithmException, KeyManagementException {

		String testKey = KEY_IMAGE_BIG;
		String testContentType = "application/pdf";
		File file = new File("src\\test\\resources\\files\\4MB.pdf");
		byte[] data = Helpers.getBytesFromFile(file);

		String response = Helpers.postDataGetResultCheckStatus(logonCookie,
				USER_API + "/" + testKey, testContentType, data, 413, 413);

		boolean isCorrectResponse = response.contains("HTTP Status 413");
		org.junit.Assert.assertTrue(isCorrectResponse);

	}

	private void cleanup() throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String keyId = KEY_JSON;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		keyId = KEY_JSON1;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		keyId = KEY_JSON2;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		keyId = KEY_IMAGE;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);

		keyId = KEY_IMAGE_BIG;

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, USER_API + "/"
				+ keyId, null, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
	}

}