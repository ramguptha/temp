/**
 * 
 */
package test.com.absolute.am.webapi;

import java.net.URLEncoder;
import java.security.MessageDigest;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.am.webapi.ContentUpload;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;


import static org.junit.Assert.*;


/**
 * @author klavin
 *
 */
public class PolicyContentAddTest extends LoggedInTest {
	
	private static final String EMPTY_ROWS = "\"rows\":[]";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cant_add_missing_media_to_policy() throws Exception {
		
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"contentIds\":[");
		sb.append("0],");
		sb.append("\"policyAssignments\":[{");
		sb.append("\"policyId\":\"" + policyIds[0] + "\",");
		sb.append("\"assignmentType\": 1,");
		sb.append("\"availabilitySelector\": 2,");
		sb.append("\"startTime\":\"2012-10-18T19:01:00Z\",");
		sb.append("\"endTime\":\"2012-10-19T20:12:00Z\"");
		sb.append("}]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cant_add_media_to_missing_policy() throws Exception {
		// ***************************************
		// Upload the files first
		// ***************************************
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String[] displayFilenames = new String[] {"WebAPIUnitTestPDF_policy_content_missing_policy.pdf"};
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] {"WebAPIUnitTestPDF.pdf"};
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();

		for(String remoteFileName: remoteFileNames) {
			String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\" + remoteFileName;
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileName, localFilePath.substring(1), digest);			
		}

		String[] policyIds = new String[] {};
		Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();
		
		// Check the media file exists
		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);
		String mediaIdFile1 = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayFilenames[0], mediaIdFile1);

		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"contentIds\":[");
		sb.append(mediaIdFile1 + "],");
		sb.append("\"policyAssignments\":[{");
		sb.append("\"policyId\": 0,"); // non existent policy policy
		sb.append("\"assignmentType\": 1,");
		sb.append("\"availabilitySelector\": 2,");
		sb.append("\"startTime\":\"2012-10-18T19:01:00Z\",");
		sb.append("\"endTime\":\"2012-10-19T20:12:00Z\"");
		sb.append("}]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_add_multiple_media_to_multiple_policies() throws Exception {
		// ***************************************
		// Upload the files first
		// ***************************************
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String[] displayFilenames = new String[] {"WebAPIUnitTestPDF_policy_content_add.pdf", "WebAPIUnitTestPNG_policy_content_add.png"};
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] {"WebAPIUnitTestPDF.pdf", "WebAPIUnitTestPNG.png"};
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		
		for(String remoteFileName: remoteFileNames) {
			String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\" + remoteFileName;
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileName, localFilePath.substring(1), digest);			
		}

		Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFilenames, new String[] {});

		Helpers.waitForAdminConsoleToCatchUp();

		// Check the media file exists
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);
		String mediaIdFile1 = Helpers.getRowId(viewResult);
		assertNotNull(displayFilenames[0] + " not found", mediaIdFile1);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[1], 200, 200);
		String mediaIdFile2 = Helpers.getRowId(viewResult);
		assertNotNull(displayFilenames[1] + " not found", mediaIdFile2);

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.SMART_POLICY_NAMES[0]);
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"contentIds\":[");
		sb.append(mediaIdFile1 + "," + mediaIdFile2 + "],");
		sb.append("\"policyAssignments\":[");
		sb.append("{");
		sb.append("\"policyId\":\"" + policyIds[0] + "\",");
		sb.append("\"assignmentType\": 1,");
		sb.append("\"availabilitySelector\": 2,");
		sb.append("\"startTime\":\"2012-10-18T19:01:00Z\",");
		sb.append("\"endTime\":\"2013-10-19T20:12:00Z\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"policyId\":\"" + policyIds[1] + "\",");; // webAPISmartPolicyUnitTest1 policy
		sb.append("\"assignmentType\": 1,");
		sb.append("\"availabilitySelector\": 2,");
		sb.append("\"startTime\":\"2012-10-18T15:01:00Z\",");
		sb.append("\"endTime\":\"2013-10-19T22:12:00Z\"");
		sb.append("}");
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_API, sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();
		
		// check the results using /api/content/id/policies
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/content/" + mediaIdFile1 + "/policies?$search=" + URLEncoder.encode(Policies.STANDARD_POLICY_NAMES[0], "UTF-8"), 200, 200);
		assertFalse(mediaIdFile1 + " is not associated with policy " + Policies.STANDARD_POLICY_NAMES[0] , viewResult.contains(EMPTY_ROWS));
		Helpers.check_first_2_entries_of_resultset(viewResult, Integer.parseInt(policyIds[0]), Policies.STANDARD_POLICY_NAMES[0]);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/content/" + mediaIdFile1 + "/policies?$search=" + URLEncoder.encode(Policies.SMART_POLICY_NAMES[0], "UTF-8"), 200, 200);
		assertFalse(mediaIdFile1 + " is not associated with policy " + Policies.SMART_POLICY_NAMES[0], viewResult.contains(EMPTY_ROWS));
		Helpers.check_first_2_entries_of_resultset(viewResult, Integer.parseInt(policyIds[1]), Policies.SMART_POLICY_NAMES[0]);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/content/" + mediaIdFile2 + "/policies?$search=" + URLEncoder.encode(Policies.STANDARD_POLICY_NAMES[0], "UTF-8"), 200, 200);
		assertFalse(mediaIdFile2 + " is not associated with policy " + Policies.STANDARD_POLICY_NAMES[0], viewResult.contains(EMPTY_ROWS));
		Helpers.check_first_2_entries_of_resultset(viewResult, Integer.parseInt(policyIds[0]), Policies.STANDARD_POLICY_NAMES[0]);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/content/" + mediaIdFile2 + "/policies?$search=" + URLEncoder.encode(Policies.SMART_POLICY_NAMES[0], "UTF-8"), 200, 200);
		assertFalse(mediaIdFile2 + " is not associated with policy webAPISmartPolicyUnitTest1", viewResult.contains(EMPTY_ROWS));
		Helpers.check_first_2_entries_of_resultset(viewResult, Integer.parseInt(policyIds[1]), Policies.SMART_POLICY_NAMES[0]);

		// check the results using /api/policies/id/content
		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/content?$search=" + displayFilenames[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, displayFilenames[0], 1);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[0] + "/content?$search=" + displayFilenames[1], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, displayFilenames[1], 1);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/content?$search=" + displayFilenames[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, displayFilenames[0], 1);

		viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/policies/" + policyIds[1] + "/content?$search=" + displayFilenames[1], 200, 200);
		Helpers.check_first_2_entries_of_resultset(viewResult, displayFilenames[1], 1);

	}
	
}
