/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.model.JobIdResult;
import com.absolute.util.FileUtilities;
import com.absolute.util.StringUtilities;

import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 * 
 */
public class ContentBatch extends LoggedInTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_file_zero_length() throws Exception {

		String[] remoteFileNames = new String[] { "Zero.txt" };
		String[] displayFileNames = new String[] { "WebAPIUnitTestTXT_upload_zero_length" };

		Helpers.deleteFilesFromSystem(logonCookie, displayFileNames);
		// Upload all of the files.
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie,
				Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);

		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFileNames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		// TODO: confirm that the uploaded files are available for download.
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$top=500", 200,
				200);

		System.out.println("view/allmobilecontent Result=" + viewResult);
		int foundInAllMobileContentCount = 0;
		for (String remoteFileName : remoteFileNames) {
			if (viewResult.indexOf(remoteFileName) != -1) {
				foundInAllMobileContentCount++;
			} else {
				System.out.println("File not found:" + remoteFileName);
			}
			// Also check that the temp file is no longer available.
			// This asserts of an unexpected result code is sent back.
			Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL
					+ CONTENT_UPLOAD_API + "/" + remoteFileName, 404, 404);
		}
		assertEquals(remoteFileNames.length, foundInAllMobileContentCount);

	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_emptybatch() throws ClientProtocolException,
			IOException, NoSuchAlgorithmException, InterruptedException,
			KeyManagementException {

		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"newFiles\":[");
		sb.append("],");
		sb.append("\"assignToPolicies\":[");
		sb.append("]}");

		System.out.println("Request body=" + sb.toString());

		String result = Helpers.postJsonRequestAndGetResult(logonCookie,
				Helpers.WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());
		Helpers.waitForAdminConsoleToCatchUp();
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);
		Helpers.waitForContentBatchToComplete(jobId.getJobId(), logonCookie);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_batch() throws Exception {

		String[] remoteFileNames = new String[] { "WebAPIUnitTestPDF.pdf",
				"WebAPIUnitTestPNG.png" };
		String[] displayFileNames = new String[] {
				"WebAPIUnitTestPDF_upload_batch.pdf",
				"WebAPIUnitTestPNG_upload_batch.png" };

		Helpers.deleteFilesFromSystem(logonCookie, displayFileNames);

		// Upload all of the files.
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie,
				Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);

		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFileNames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		// TODO: confirm that the uploaded files are available for download.
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$top=500", 200,
				200);

		System.out.println("view/allmobilecontent Result=" + viewResult);
		int foundInAllMobileContentCount = 0;
		for (String remoteFileName : remoteFileNames) {
			if (viewResult.indexOf(remoteFileName) != -1) {
				foundInAllMobileContentCount++;
			} else {
				System.out.println("File not found:" + remoteFileName);
			}
			// Also check that the temp file is no longer available.
			// This asserts of an unexpected result code is sent back.
			Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL
					+ CONTENT_UPLOAD_API + "/" + remoteFileName, 404, 404);
		}
		assertEquals(remoteFileNames.length, foundInAllMobileContentCount);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_media_password_missing_or_null() throws Exception {

		String[] displayFilenames = new String[] { "WebAPIUnitTestPDF File",
				"WebAPIUnitTestPNG File", "WebAPIUnitTestZIP File" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "WebAPIUnitTestPDF.pdf",
				"WebAPIUnitTestPNG.png", "WebAPIUnitTestZIP.zip" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		// Create the request body.
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"newFiles\":[");
		sb.append("{"); // "password":null
		sb.append("\"fileName\":\"" + remoteFileNames[0] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[0] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[0]
				+ ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[0])
				+ "\",");
		sb.append("\"canLeaveApp\":false,");
		sb.append("\"canEmail\":false,");
		sb.append("\"canPrint\":false,");
		sb.append("\"transferOnWifiOnly\":true,");
		sb.append("\"passphrase\":null");
		sb.append("},");
		sb.append("{"); // "password" is missing
		sb.append("\"fileName\":\"" + remoteFileNames[1] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[1] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[1]
				+ ".\",");
		sb.append("\"category\":\"Pictures\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[1])
				+ "\",");
		sb.append("\"canLeaveApp\":true,");
		sb.append("\"canEmail\":true,");
		sb.append("\"canPrint\":true,");
		sb.append("\"transferOnWifiOnly\":false");
		sb.append("},");
		sb.append("{"); // "password" is set to something.
		sb.append("\"fileName\":\"" + remoteFileNames[2] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[2] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[2]
				+ ".\",");
		sb.append("\"category\":\"Other\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[2])
				+ "\",");
		sb.append("\"canLeaveApp\":true,");
		sb.append("\"canEmail\":true,");
		sb.append("\"canPrint\":true,");
		sb.append("\"transferOnWifiOnly\":true,");
		sb.append("\"passphrase\":\"secret\"");
		sb.append("}");

		sb.append("],");

		sb.append("\"assignToPolicies\":[]");

		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		String result = Helpers.postJsonRequestAndGetResult(logonCookie,
				Helpers.WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());

		// Give the server time to upload the files.
		Helpers.waitForAdminConsoleToCatchUp();
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);

		Helpers.waitForContentBatchToComplete(jobId.getJobId(), logonCookie);

		for (int i = 0; i < displayFilenames.length; i++) {
			String viewResultFile = Helpers.doGETCheckStatusReturnBody(
					logonCookie, Helpers.WEBAPI_BASE_URL
							+ "api/views/allmobilecontent?$search="
							+ URLEncoder.encode(displayFilenames[i], "UTF-8"),
					200, 200);
			String mediaId = Helpers.getRowId(viewResultFile);
			assertNotNull("Checking that " + displayFilenames[i]
					+ " exists on the server.", mediaId);
		}
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_with_different_availability_selectors()
			throws Exception {

		String[] displayFilenames = new String[] { "WebAPIUnitTestPDF File_3" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "WebAPIUnitTestPDF.pdf" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie,
				Policies.STANDARD_POLICY_NAMES[0], 
				Policies.STANDARD_POLICY_NAMES[1],
				Policies.STANDARD_POLICY_NAMES[2]);

		// Create the request body.
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"newFiles\":[");
		sb.append("{");
		sb.append("\"fileName\":\"" + remoteFileNames[0] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[0] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[0]
				+ ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[0])
				+ "\",");
		sb.append("\"canLeaveApp\":false,");
		sb.append("\"canEmail\":false,");
		sb.append("\"canPrint\":false,");
		sb.append("\"transferOnWifiOnly\":true,");
		sb.append("\"passphrase\":\"secret\"");
		sb.append("}");
		sb.append("],");

		// These uniqueID values are for the QAAM1 environment.
		// TODO: query the WebAPI for the policy uniqueIDs, and then use the
		// values here instead of hard coding them.
		sb.append("\"assignToPolicies\":[{");
		sb.append("\"policyId\":" + policyIds[0] + ","); 
		sb.append("\"assignmentType\": 1,"); // Policy Optional (i.e. on-demand,
												// auto remove)
		sb.append("\"availabilitySelector\": 0"); // always
		sb.append("},");
		sb.append("{");
		sb.append("\"policyId\":" + policyIds[1] + ","); 
		sb.append("\"assignmentType\": 4,"); // On demand.
		sb.append("\"availabilitySelector\": 1,"); // Daily Interval
		sb.append("\"startTime\":\"13:01\",");
		sb.append("\"endTime\":\"14:00\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"policyId\":" + policyIds[2] + ","); // WebAPIUnitTest3
															// policy
		sb.append("\"assignmentType\": 4,"); // On demand.
		sb.append("\"availabilitySelector\": 2,"); // Fixed period
		// pick some times in the future.
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.add(Calendar.MONTH, 1);
		String startTime = StringUtilities.toISO8601W3CString(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		String endTime = StringUtilities.toISO8601W3CString(cal.getTime());

		sb.append("\"startTime\":\"").append(startTime).append("\",");
		sb.append("\"endTime\":\"").append(endTime).append("\"");
		sb.append("}]");

		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		String result = Helpers.postJsonRequestAndGetResult(logonCookie,
				Helpers.WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());

		// Give the server time to upload the files.
		Helpers.waitForAdminConsoleToCatchUp();
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);

		Helpers.waitForContentBatchToComplete(jobId.getJobId(), logonCookie);

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				Helpers.WEBAPI_BASE_URL
						+ "api/views/allmobilecontent?$search="
						+ URLEncoder
								.encode("WebAPIUnitTestPDF File_3", "UTF-8"),
				200, 200);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server.", mediaId);

		// TODO: Check that the file is added to each policy.

	}

	/**
	 * Test that extra fields are ignored by the server.
	 * 
	 * @throws Exception
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_media_with_extra_fields() throws Exception {

		String[] displayFilenames = new String[] { "WebAPIUnitTestPDF File_2" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "WebAPIUnitTestPDF.pdf" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie,
				Policies.STANDARD_POLICY_NAMES[0]);

		// Create the request body.
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"newFiles\":[");
		sb.append("{"); // "password":null
		sb.append("\"fileName\":\"" + remoteFileNames[0] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[0] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[0]
				+ ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[0])
				+ "\",");
		sb.append("\"canLeaveApp\":false,");
		sb.append("\"canEmail\":false,");
		sb.append("\"canPrint\":false,");
		sb.append("\"transferOnWifiOnly\":true,");
		sb.append("\"passphrase\":null,");
		sb.append("\"extraField1\":true,");
		sb.append("\"extraField2\":\"whatever\"");
		sb.append("}");

		sb.append("],");

		// These uniqueID values are for the QAAM1 environment.
		// TODO: query the WebAPI for the policy uniqueIDs, and then use the
		// values here instead of hard coding them.
		sb.append("\"assignToPolicies\":[{");
		sb.append("\"policyId\":" + policyIds[0] + ","); 
		sb.append("\"assignmentType\": 1,"); // Policy Optional (i.e. on-demand,
												// auto remove)
		sb.append("\"availabilitySelector\": 0,"); // always
		sb.append("\"extraPolicyField\": \"some extra value\"");
		sb.append("}]");

		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		String result = Helpers.postJsonRequestAndGetResult(logonCookie,
				Helpers.WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);

		Helpers.waitForContentBatchToComplete(jobId.getJobId(), logonCookie);

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ URLEncoder.encode(displayFilenames[0], "UTF-8"), 200,
				200);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server.", mediaId);
	}

	/**
	 * Test that extra fields are ignored by the server.
	 * 
	 * @throws Exception
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_unknown_file_type() throws Exception {

		String[] displayFilenames = new String[] { "webAPIUnitTestUnknownFileType_unknown_type.xyz" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "webAPIUnitTestUnknownFileType.xyz" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = new String[] {}; // Assigning to a policy is not
												// important here.
		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ URLEncoder.encode(displayFilenames[0], "UTF-8"), 200,
				200);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server.", mediaId);
	}

	/**
	 * Test that extra fields are ignored by the server.
	 * 
	 * @throws Exception
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_unknown_filetype_and_category() throws Exception {

		String[] displayFilenames = new String[] { "webAPIUnitTestUnknownCategoryAndType.xyz" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "webAPIUnitTestUnknownFileType.xyz" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		// Create the request body.
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"newFiles\":[");
		sb.append("{"); // "password":null
		sb.append("\"fileName\":\"" + remoteFileNames[0] + "\",");
		sb.append("\"displayName\":\"" + displayFilenames[0] + "\",");
		sb.append("\"description\":\"This is my " + displayFilenames[0]
				+ ".\",");
		sb.append("\"category\":\"My new category\",");
		sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\",");
		sb.append("\"fileType\":\""
				+ FileUtilities.getExtensionFromFilePath(remoteFileNames[0])
				+ "\",");
		sb.append("\"canLeaveApp\":false,");
		sb.append("\"canEmail\":false,");
		sb.append("\"canPrint\":false,");
		sb.append("\"transferOnWifiOnly\":true,");
		sb.append("\"passphrase\":null");
		sb.append("}");

		sb.append("],");
		sb.append("\"assignToPolicies\":[]");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		String result = Helpers.postJsonRequestAndGetResult(logonCookie,
				Helpers.WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);

		Helpers.waitForContentBatchToComplete(jobId.getJobId(), logonCookie);

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ displayFilenames[0], 200, 200);

		System.out.println("viewResultFile:" + viewResultFile);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server.", mediaId);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void get_205_when_file_with_same_displayname_already_exists()
			throws Exception {

		String[] displayFilenames = new String[] {
				"webAPIUnitTestDuplicateDisplayName.txt",
				"webAPIUnitTestDuplicateDisplayName2.txt" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload the first version of the file.
		String[] remoteFileNames = new String[] { "webAPIUnitTestDuplicateDisplayName.txt" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = new String[] {};
		displayFilenames = new String[] { "webAPIUnitTestDuplicateDisplayName.txt" };

		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ URLEncoder.encode(displayFilenames[0], "UTF-8"), 200,
				200);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server (1).", mediaId);

		// Now try to post this file again, confirming that 205 is returned.
		digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.

			// NOTE: checking for 205 on the first block. If 205 is not
			// returned, the test will fail.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest, 205);
		}

		displayFilenames = new String[] { "webAPIUnitTestDuplicateDisplayName2.txt" };
		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ URLEncoder.encode(displayFilenames[0], "UTF-8"), 200,
				200);
		mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull(
				"Checking that the file with a new display name exists on server.",
				mediaId);

	}

	/**
	 * Test that extra fields are ignored by the server.
	 * 
	 * @throws Exception
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_file_with_no_filetype() throws Exception {

		String[] displayFilenames = new String[] { "WebAPIUnitTest_fileWithNoExtension" };
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] { "fileWithNoExtension" };
		MessageDigest digest = MessageDigest.getInstance("MD5");
		for (String remoteFileName : remoteFileNames) {
			String localFilePath = ContentUpload.class
					.getResource(remoteFileName).getPath().substring(1); // remove
																			// preceeding
																			// /
																			// that
																			// is
																			// on
																			// the
																			// filename
																			// when
																			// getResource()
																			// is
																			// used.
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(),
					remoteFileName, localFilePath, digest);
		}

		String[] policyIds = new String[] {};
		Helpers.addFilesToSystem(logonCookie, remoteFileNames,
				displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();

		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie,
				Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search="
						+ displayFilenames[0], 200, 200);

		System.out.println("viewResultFile:" + viewResultFile);
		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Checking that file exists on server.", mediaId);
	}

}
