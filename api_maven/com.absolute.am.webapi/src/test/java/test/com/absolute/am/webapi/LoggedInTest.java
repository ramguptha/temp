/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 * 
 */
public class LoggedInTest {

	protected static final String BATCH_CONTENT_API = "api/content/batch";

	// dv2wlssmdm1
	// protected static final String serverName = "dv2wlssmdm1";
	// protected static final short serverPort = 3971;
	// protected static final String userName = "admin";
	// protected static final String password = "absdemo";

	// protected static final String serverName = "qaams3";
	// protected static final short serverPort = 3971;
	// protected static final String userName = "admin";
	// protected static final String password = "qa;pass";
	// protected static final String locale = "en_US";

	// protected static final String serverName = "qaams1";
	// protected static final short serverPort = 3971;
	// protected static final String userName = "admin";
	// protected static final String password = "qa;pass";
	// protected static final String locale = "en_US";

	protected static String serverName = "qaams8";
	protected static short serverPort = 3971;
	protected static final String userName = "admin";
	protected static final String password = "qa;pass";
	protected static final String locale = "en_US";

	// protected static final String serverName = "qaams2";
	// protected static final short serverPort = 3971;
	// protected static final String userName = "admin";
	// protected static final String password = "qa;pass";
	// protected static final String locale = "en_US";

	protected static final String LOGIN_API = "api/login";

	protected static final String CONTENT_UPLOAD_API = "api/content/upload";
	protected static final String CONTENT_API = "api/content";
	protected static final String POLICIES_API = "api/policies";
	protected static final String POLICIES_STANDARD_API = "api/policies/standard";
	protected static final String POLICIES_CREATE_STANDARD_API = POLICIES_STANDARD_API + "/";
	protected static final String POLICIES_DELETE_API = "api/policies/delete";
	protected static final String MOBILE_DEVICES_API = "api/mobiledevices";
	protected static final String CUSTOM_FIELDS_API = "api/customfields";
	protected static final String CUSTOM_FIELDS_MOBILE_DEVICE_API = "api/customfields_mobiledevice";
	protected static final String COMMANDS_API = "api/commands";
	protected static final String COMPUTER_COMMANDS_API = "api/computercommands";
	protected static final String PUSH_API = "api/push";
	protected static final String POLICIES_SMART_API = "api/policies/smart";
	protected static final String POLICIES_CREATE_SMART_API = POLICIES_SMART_API + "/";
	protected static final String INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_MOBILE_DEVICE = "api/infoitems/filtercriteria/smartpolicies/bymobiledevices";
	protected static final String INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_IA = "api/infoitems/filtercriteria/smartpolicies/byinstalledapplications";
	protected static final String INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_ICP = "api/infoitems/filtercriteria/smartpolicies/byinstalledconfigprofiles";

	protected static final String POLICY_CONFIGURATION_PROFILE_API = "api/policy_configurationprofile";
	protected static final String POLICY_CONFIGURATION_PROFILE_DELETE_API = "api/policy_configurationprofile/delete";

	protected static final String POLICY_CONTENT_API = "api/policy_content";
	protected static final String POLICY_INHOUSEAPP_API = "api/policy_inhouseapp";
	protected static final String POLICY_THIRDPARTYAPP_API = "api/policy_thirdpartyapp";
	protected static final String POLICY_MOBILE_DEVICE_API = "api/policy_mobiledevice";
	protected static final String POLICY_MOBILE_DEVICE_DELETE_API = "api/policy_mobiledevice/delete";

	protected static final String COMPUTERS_API = "api/computers";
	protected static final String ACTIONS_API = "api/actions";
	protected static final String POLICIY_ACTIONS_API = "api/policy_actions";
	
	protected static String logonCookie;

	@BeforeClass
	public static void logonToWebAPI() throws Exception {
		doLogon(serverName, serverPort, userName, password, locale);
	}

	public static void doLogon(String serverName, short serverPort, String userName, String password, String locale) throws Exception {
		String serverNameEnv = System.getenv("AM_TEST_SERVER_NAME");
		String serverPortEnv = System.getenv("AM_TEST_SERVER_PORT");

		if (serverNameEnv != null) {
			serverName = serverNameEnv;
		}

		if (serverNameEnv != null) {
			serverPort = Short.valueOf(serverPortEnv);
		}

		logonCookie = Helpers.logonToWebAPI(serverName, serverPort, userName, password, locale);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_logon_to_webAPI() throws Exception {
		// test login with credential, server name & server port included in the request body 
		can_logon_to_webAPI_with_request_body(serverName, serverPort, userName, password, locale);
		// test login with credential, server name included in the request body (no server port passed in) 
		can_logon_to_webAPI_with_request_body(serverName, (short) 0, userName, password, locale);
		// test login with the login url, which includes user credential & server name & server port
		String loginUrl = LOGIN_API + String.format("?ServerName=%s&ServerPort=%s&UserName=%s&Password=%s", serverName, serverPort, userName, password);
		can_logon_to_webAPI_with_login_url(loginUrl);
		// test login with the login url, which includes user credential & server name (without server port)
		loginUrl = LOGIN_API + String.format("?ServerName=%s&UserName=%s&Password=%s", serverName, userName, password);
		can_logon_to_webAPI_with_login_url(loginUrl);
	}

	private void can_logon_to_webAPI_with_request_body(String serverName, short serverPort, String userName, String password, String locale) 
			throws Exception {
		String loginCookie2 = Helpers.logonToWebAPI(serverName, serverPort, userName, password, locale);
		
		// the session id should be returned
		assertNotNull(loginCookie2);
		assertTrue(loginCookie2.contains("JSESSIONID="));
	}
	
	private void can_logon_to_webAPI_with_login_url(String loginUrl) throws Exception {
		String loginCookie2 = Helpers.logonToWebAPI(loginUrl);
		
		// the session id should be returned
		assertNotNull(loginCookie2);
		assertTrue(loginCookie2.contains("JSESSIONID="));
	}
	
	/**
	 * Helper method to upload a file to the api/content/upload endpoint.
	 * 
	 * @param httpClient
	 *            - the http client to use
	 * @param remoteFileName
	 *            - the name the file will be assigned on the server
	 * @param localFilePath
	 *            - the path to the local file content
	 * @param digest
	 *            - the digest to use when calculating the hash
	 * @return a hash of the uploaded file
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] uploadFile(HttpClient httpClient, String remoteFileName, String localFilePath, MessageDigest digest)
			throws ClientProtocolException, IOException, NoSuchAlgorithmException {
		return uploadFile(httpClient, remoteFileName, localFilePath, digest, -1);
	}

	/**
	 * Helper method to upload a file to the api/content/upload endpoint.
	 * 
	 * @param httpClient
	 *            - the http client to use
	 * @param remoteFileName
	 *            - the name the file will be assigned on the server
	 * @param localFilePath
	 *            - the path to the local file content
	 * @param digest
	 *            - the digest to use when calculating the hash
	 * @param expectedStatusCodeForFirstBlock
	 *            - the status code to check for on the first block - use -1 to
	 *            ignore.
	 * @return a hash of the uploaded file
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] uploadFile(HttpClient httpClient, String remoteFileName, String localFilePath, MessageDigest digest,
			int expectedStatusCodeForFirstBlock) throws ClientProtocolException, IOException, NoSuchAlgorithmException {

		String uploadUrlWithFileName = Helpers.WEBAPI_BASE_URL + CONTENT_UPLOAD_API + "/" + remoteFileName;

		long fileLength = new File(localFilePath).length();
		System.out.println("File = " + localFilePath + " length=" + fileLength);

		long chunkSize = 1024 * 10; // 10K chunks.

		long offset = 0;
		while (offset <= fileLength) {
			System.out.println("Sending chunk offset=" + offset);
			if (offset == 0) {
				sendFileChunk(localFilePath, fileLength, offset, chunkSize, uploadUrlWithFileName, httpClient, digest,
						expectedStatusCodeForFirstBlock);
			} else {
				sendFileChunk(localFilePath, fileLength, offset, chunkSize, uploadUrlWithFileName, httpClient, digest, -1);
			}

			offset += chunkSize;
		}

		byte[] finalDigest = digest.digest();
		System.out.println("uploadFile() done, digest=" + StringUtilities.toHexString(finalDigest));
		return finalDigest;
	}

	/**
	 * Helper method to download a file previously uploaded with uploadFile().
	 * 
	 * @param httpClient
	 * @param remoteFileName
	 * @param localFileName
	 * @param digest
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected byte[] getFile(HttpClient httpClient, String remoteFileName, String localFileName, MessageDigest digest)
			throws ClientProtocolException, IOException {

		HttpGet getRequest = new HttpGet(remoteFileName);
		getRequest.addHeader("accept", "application/octet-stream");
		getRequest.addHeader("Cookie", logonCookie);

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}

		OutputStream os = null;
		if (localFileName != null && localFileName.length() > 0) {
			File outFile = new File(localFileName);
			if (outFile.exists()) {
				outFile.delete();
			}
			os = new FileOutputStream(localFileName);
		}

		DigestInputStream dis = new DigestInputStream(response.getEntity().getContent(), digest);
		byte[] tmp = new byte[1024];
		int len = dis.read(tmp);
		while (len > 0) {
			if (os != null) {
				os.write(tmp, 0, len);
			}
			len = dis.read(tmp);
		}
		if (os != null) {
			os.close();
		}

		byte[] finalDigest = digest.digest();
		System.out.println("getFile() done, digest=" + StringUtilities.toHexString(finalDigest));
		return finalDigest;
	}

	/**
	 * Send a chunk of a file to he server.
	 * 
	 * @param filePath
	 *            the full path to the file to send
	 * @param offset
	 *            the offset within the file to start sending from
	 * @param chunkSizeToSend
	 *            the size of an individual chunk to send
	 * @param uploadUrlWithFileName
	 *            the Url to post the file to
	 * @param httpClient
	 *            the HttpClient object to use for the post
	 * @param digest
	 *            the message digest to append to
	 * @param specificStatusCodeCheck
	 *            a specific status code to check for, set to -1 to ignore
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private void sendFileChunk(String filePath, long totalBytes, long offset, long chunkSizeToSend, String uploadUrlWithFileName,
			HttpClient httpClient, MessageDigest digest, int specificStatusCodeCheck) throws IOException {

		// Here are some examples for the Content-Range header
		// (http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html):
		// The first 500 bytes:
		// bytes 0-499/1234
		// The second 500 bytes:
		// bytes 500-999/1234
		// All except for the first 500 bytes:
		// bytes 500-1233/1234
		// The last 500 bytes:
		// bytes 734-1233/1234

		long thisChunkLength = Math.min(chunkSizeToSend, totalBytes - offset);
		String contentRangeHeader = "bytes " + offset + "-" + (offset + thisChunkLength - 1) + "/" + totalBytes;
		HttpPost postRequest = new HttpPost(uploadUrlWithFileName);
		postRequest.addHeader(HttpHeaders.CONTENT_RANGE, contentRangeHeader);
		postRequest.addHeader("Cookie", logonCookie);

		FileInputStream fis = new FileInputStream(new File(filePath));
		fis.skip(offset);
		DigestInputStream dis = new DigestInputStream(fis, digest);

		InputStreamEntity ise = new InputStreamEntity(dis, thisChunkLength);
		ise.setContentType("application/octet-stream");
		postRequest.setEntity(ise);

		HttpResponse response = httpClient.execute(postRequest);
		System.out.println(response.getStatusLine().toString());

		// Requested to check for a specific status code.
		if (specificStatusCodeCheck != -1) {
			if (response.getStatusLine().getStatusCode() != specificStatusCodeCheck) {
				throw new RuntimeException(
						"Failed : specificStatusCodeCheck = "
								+ specificStatusCodeCheck
								+ " returned HTTP status code : "
								+ response.getStatusLine().getStatusCode());
			}
		}

		if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() > 299) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}

		if (response.getStatusLine().getStatusCode() == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		}
	}

}
