/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;
import static org.junit.Assert.*;


/**
 * @author dlavin
 *
 */
public class RemoveMediaTest extends LoggedInTest {
	private static final String CONTENT_DELETE_API = CONTENT_API + "/delete";

	// This test case is only useful for debugging the feature.
	//@Test	
	//@Category(com.absolute.util.helper.FastTest.class)
	public void can_remove_media() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {		
		
		int mediaId = 187; //TODO: Need to load this from config file, or run an add media op first.

		Helpers.deleteRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaId, 
				null, 200, 299);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_remove_multiple_media() throws Exception {
		
		// Upload the files first
		String[] displayFilenames = new String[] {"WebAPIUnitTestPDF_removemediatest.pdf",
				"WebAPIUnitTestPNG_removemediatest.png", "WebAPIUnitTestZIP_removemediatest.zip"};
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);

		String[] remoteFileNames = new String[] {"WebAPIUnitTestPDF.pdf", 
				"WebAPIUnitTestPNG.png", "WebAPIUnitTestZIP.zip"};

		MessageDigest digest = MessageDigest.getInstance("MD5");
		
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		
		for(String remoteFileName: remoteFileNames) {
			String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\" + remoteFileName;
			uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileName, localFilePath.substring(1), digest);			
		}
		
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie,  Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);
		Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();
		
		String viewResultFile1 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);
		assertNotNull("file1: Check got response for file search from server.", viewResultFile1);
		System.out.println("viewResultFile1=" + viewResultFile1);
		String idFile1 = Helpers.getRowId(viewResultFile1);
		assertNotNull("Checking file1 id from server.", idFile1);
		
		String viewResultFile2 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[1], 200, 200);
		assertNotNull("file2: Check got response for file search from server.", viewResultFile2);
		System.out.println("viewResultFile2=" + viewResultFile2);
		String idFile2 = Helpers.getRowId(viewResultFile2);
		assertNotNull("Checking file2 id from server.", idFile2);
		
		String viewResultFile3 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[2], 200, 200);
		assertNotNull("file3: Check got response for file search from server.", viewResultFile3);
		System.out.println("viewResultFile3=" + viewResultFile3);
		String idFile3 = Helpers.getRowId(viewResultFile3);
		assertNotNull("Checking file3 id from server.", idFile3);
				
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"contentIds\":[");
		sb.append(idFile1);
		sb.append(",");
		sb.append(idFile2);
		sb.append(",");
		sb.append(idFile3);
		sb.append("]}");

		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + CONTENT_DELETE_API, 
				sb.toString());

		Helpers.waitForAdminConsoleToCatchUp();
		
		// check all 3 files are deleted
		viewResultFile1 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);
		viewResultFile2 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[1], 200, 200);
		viewResultFile3 = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[2], 200, 200);

		idFile1 = Helpers.getRowId(viewResultFile1);
		idFile2 = Helpers.getRowId(viewResultFile2);
		idFile3 = Helpers.getRowId(viewResultFile3);
		
		assertNull("Check that file1 is no longer on server.", idFile1);
		assertNull("Check that file2 is no longer on server.", idFile2);
		assertNull("Check that file3 is no longer on server.", idFile3);

	}
			
}
