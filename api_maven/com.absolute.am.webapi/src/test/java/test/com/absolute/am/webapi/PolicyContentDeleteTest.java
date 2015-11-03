/**
 * 
 */
package test.com.absolute.am.webapi;

import java.security.MessageDigest;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.am.webapi.ContentUpload;
import test.com.absolute.testdata.configuration.ContentFiles;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;


import static org.junit.Assert.*;


/**
 * @author klavin
 *
 */
public class PolicyContentDeleteTest extends LoggedInTest {
	
	private static final String POLICY_CONTENT_DELETE_API = POLICY_CONTENT_API + "/delete";
	private static final String EMPTY_ROWS = "\"rows\":[]";


	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cant_remove_missing_media_from_policy() throws Exception {
		
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0]);
		StringBuilder sb = new StringBuilder();		

		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{");
		sb.append("\"contentId\":0,"); 
		sb.append("\"policyId\":" + policyIds[0]); 
		sb.append("}");
		sb.append("]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cant_remove_media_from_missing_policy() throws Exception {
		
		String[] contentIds = Helpers.getContentIdsForContentNames(logonCookie, ContentFiles.CONTENT_FILE_NAMES[0]);
		StringBuilder sb = new StringBuilder();		

		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{");
		sb.append("\"contentId\":" + contentIds[0] + ","); 
		sb.append("\"policyId\":0"); 
		sb.append("}");
		sb.append("]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}


	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_can_remove_media_from_policy() throws Exception {
		
		// ***************************************
		// Upload the files first
		// ***************************************
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String[] displayFilenames = new String[] {"WebAPIUnitTestPDF_policy_content.pdf", "WebAPIUnitTestPNG_policy_content.png"};
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		// Upload all of the files.
		String[] remoteFileNames = new String[] {"WebAPIUnitTestPDF.pdf", "WebAPIUnitTestPNG.png"};
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();

		for(String remoteFileName: remoteFileNames) {
			String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\" + remoteFileName;
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileName, localFilePath.substring(1), digest);			
		}

		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);
		Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFilenames, policyIds);

		// Give the server time to add the files.
		Helpers.waitForAdminConsoleToCatchUp();

		// Check the media file exists
		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);
		String mediaIdFile1 = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayFilenames[0], mediaIdFile1);
		
		// Check /api/content/id/policy for file 1 does not have empty rows
		// ie there are policies assigned to this file.
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaIdFile1 + "/policies", 200, 200);
		assertFalse("File with id = " + mediaIdFile1 + " has no policies associated with it", viewResultFile.contains(EMPTY_ROWS));

		// Check the media file exists
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[1], 200, 200);
		String mediaIdFile2 = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayFilenames[1], mediaIdFile2);
		
		// Check /api/content/id/policy for file 2 does not have empty rows
		// ie there are policies assigned to this file.
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaIdFile2 + "/policies", 200, 200);
		assertFalse("File with id = " + mediaIdFile2 + " has no policies associated with it", viewResultFile.contains(EMPTY_ROWS));

		
		// ***************************************
		// remove media from policy
		// ***************************************
//		{
//			  [ {"contentId":1, "policyId":9}, 
//			    {"contentId":2, "policyId":10} ]
//			}

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{");
		sb.append("\"contentId\":" + mediaIdFile1 + ","); 
		sb.append("\"policyId\":" + policyIds[0]); 
		sb.append("},");
		sb.append("{");
		sb.append("\"contentId\":" + mediaIdFile1 + ","); 
		sb.append("\"policyId\":" + policyIds[1]); 
		sb.append("},");
		sb.append("{");
		sb.append("\"contentId\":" + mediaIdFile2 + ","); 
		sb.append("\"policyId\":" + policyIds[0]); 
		sb.append("},");
		sb.append("{");
		sb.append("\"contentId\":" + mediaIdFile2 + ","); 
		sb.append("\"policyId\":" + policyIds[1]); 
		sb.append("}");
		sb.append("]}");

		System.out.println("Request body=" + sb.toString());
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONTENT_DELETE_API, sb.toString());
		
		// Give the server time to upload the files.
		System.out.println("TODO: remove when sync service is ready. Thread.sleep() to allow Admin Console to sync up.");
		Thread.sleep(5000);

		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaIdFile1 + "/policies", 200, 200);
		assertTrue("File with id = " + mediaIdFile1 + " has no policies associated with it", viewResultFile.contains(EMPTY_ROWS));

		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaIdFile2 + "/policies", 200, 200);
		assertTrue("File with id = " + mediaIdFile2 + " has no policies associated with it", viewResultFile.contains(EMPTY_ROWS));
		
	}
	
}
