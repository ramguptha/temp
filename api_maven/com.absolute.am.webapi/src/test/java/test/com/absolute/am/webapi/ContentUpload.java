/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.am.webapi.ContentUpload;
import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 *
 */
public class ContentUpload extends LoggedInTest {

	private static String CONTENT_UPLOAD_API_PREFIX =  Helpers.WEBAPI_BASE_URL + CONTENT_UPLOAD_API + "/" ;
		
	@Test(timeout=3000)
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_237KB_file() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
			
		String[] remoteFileNames = new String[] {"237KB.png"};
		//Helpers.deleteFilesFromSystem(logonCookie, remoteFileNames);

		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\237KB.png";

		uploadAndDownloadFileCheckHashes(remoteFileNames[0], localFilePath.substring(1));
	}
	
	@Test(timeout=3000)
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_536KB_file() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] remoteFileNames = new String[] {"536KB.png"};
		//Helpers.deleteFilesFromSystem(logonCookie, remoteFileNames);

		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\536KB.png";
		
    	uploadAndDownloadFileCheckHashes(remoteFileNames[0], localFilePath.substring(1));
	}
	
	@Test(timeout=3000)
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_upload_833KB_file() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		String[] remoteFileNames = new String[] {"833KB.png"};
		//Helpers.deleteFilesFromSystem(logonCookie, remoteFileNames);

		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\833KB.png";

		uploadAndDownloadFileCheckHashes(remoteFileNames[0], localFilePath.substring(1));
	}
	
	private void uploadAndDownloadFileCheckHashes(
			String remoteFileName, 
			String localFilePath
			) throws NoSuchAlgorithmException, ClientProtocolException, IOException, KeyManagementException {
		
		MessageDigest digest = MessageDigest.getInstance("MD5");
		HttpClient httpClient = Helpers.createHttpClientWithoutCertificateChecking();
		byte[] uploadHash = uploadFile(httpClient, remoteFileName, localFilePath, digest);

		// GET the file and compare to original.
		digest.reset();
		byte[] downloadHash = getFile(httpClient, CONTENT_UPLOAD_API_PREFIX + remoteFileName, null, digest);
	
		assertArrayEquals("Upload and download files dont match", uploadHash, downloadHash);						
		
	}
	
}
