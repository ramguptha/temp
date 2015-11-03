/**
 * 
 */
package test.com.absolute.am.webapi;

import java.security.MessageDigest;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;


import static org.junit.Assert.*;


/**
 * @author klavin
 *
 */
public class UpdateMediaTest extends LoggedInTest {
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_update_media() throws Exception {
		
		// ***************************************
		// Upload the files first
		// ***************************************
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String[] displayFilenames = new String[] {"WebAPIUnitTestPDF_for_update.pdf",
				"WebAPIUnitTestPDF_for_update_1.pdf", "WebAPIUnitTestPDF_for_update_2.pdf"};
		Helpers.deleteFilesFromSystem(logonCookie, displayFilenames);
		
		String[] remoteFileNames = new String[] {"WebAPIUnitTestPDF.pdf"};
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		
		String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\" + remoteFileNames[0];
		uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileNames[0], localFilePath.substring(1), digest);			

		String[] policyIds = new String[]{};	// Assigning to policies is not relevant to this test case.
		displayFilenames = new String[] {"WebAPIUnitTestPDF_for_update.pdf"};
		Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFilenames, policyIds);

		Helpers.waitForAdminConsoleToCatchUp();
		
		String viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayFilenames[0], 200, 200);

		String mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayFilenames[0], mediaId);
		
		// ***************************************
		// Change passcode
		// ***************************************
		String displayName = "WebAPIUnitTestPDF_for_update_1.pdf";
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"seed\":\"1\","); 
		sb.append("\"fileName\":\"WebAPIUnitTestPDF.pdf\",");
		sb.append("\"displayName\":\"" + displayName + "\",");
		sb.append("\"description\":\"This is my " + displayName + ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-06-06T22:39:31Z\",");
		sb.append("\"fileType\":\"PDF\",");
		sb.append("\"canLeaveApp\":false,");
		sb.append("\"canEmail\":false,");
		sb.append("\"canPrint\":false,");
		sb.append("\"transferOnWifiOnly\":false,");
		sb.append("\"passphrase\":\"another secret\"");
		sb.append("}");


		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaId, 
				sb.toString(),
				200,
				299);

		Helpers.waitForAdminConsoleToCatchUp();
		
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayName, 200, 200);
		mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayName, mediaId);
		
		// ***************************************
		// remove password.
		// ***************************************
		displayName = "WebAPIUnitTestPDF_for_update_2.pdf";
		sb = new StringBuilder();
		sb.append("{");
		sb.append("\"seed\":\"2\",");
		sb.append("\"fileName\":\"WebAPIUnitTestPDF.pdf\",");
		sb.append("\"displayName\":\"" + displayName + "\",");
		sb.append("\"description\":\"This is my " + displayName + ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-06-06T22:39:31Z\",");
		sb.append("\"fileType\":\"PDF\",");
		sb.append("\"canLeaveApp\":true,");
		sb.append("\"canEmail\":true,");
		sb.append("\"canPrint\":true,");
		sb.append("\"transferOnWifiOnly\":false,");
		sb.append("\"passphrase\":\"\"");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaId, 
				sb.toString(),
				200, 299);

		Helpers.waitForAdminConsoleToCatchUp();
		
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayName, 200, 200);
		mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayName, mediaId);

		// ***************************************
		// incorrect seed.
		// ***************************************

		displayName = "WebAPIUnitTestPDF_for_update_2.pdf";
		sb = new StringBuilder();
		sb.append("{");
		sb.append("\"seed\":\"5\",");
		sb.append("\"fileName\":\"WebAPIUnitTestPDF.pdf\",");
		sb.append("\"displayName\":\"" + displayName + "\",");
		sb.append("\"description\":\"This is my " + displayName + ".\",");
		sb.append("\"category\":\"Documents\",");
		sb.append("\"fileModDate\":\"2012-06-06T22:39:31Z\",");
		sb.append("\"fileType\":\"PDF\",");
		sb.append("\"canLeaveApp\":true,");
		sb.append("\"canEmail\":true,");
		sb.append("\"canPrint\":true,");
		sb.append("\"transferOnWifiOnly\":false,");
		sb.append("\"passphrase\":\"\"");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + mediaId, 
					sb.toString(), HttpStatus.SC_CONFLICT);

		Helpers.waitForAdminConsoleToCatchUp();
		
		viewResultFile = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + displayName, 200, 200);
		mediaId = Helpers.getRowId(viewResultFile);
		assertNotNull("Cant find id for filename: " + displayName, mediaId);
	}

}
