package test.com.absolute.testutil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.absolute.am.model.JobIdResult;
import com.absolute.am.model.JobStatusResult;
import com.absolute.am.model.Result;
import com.absolute.util.FileUtilities;
import com.absolute.util.StringUtilities;

public class Helpers {

	public static String WEBAPI_BASE_URL = "https://webadmin-qaammdm8.absolute.com/com.absolute.am.webapi/";
	//public static String WEBAPI_BASE_URL = "https://amweb.absolute.com/com.absolute.am.webapi/";
	private static String CONTENT_API = "api/content";
	private static final String BATCH_CONTENT_API = "api/content/batch";
	private static final String JOB_API = "api/job/";
	protected static final String LOGIN_API = "api/login";
	protected static final String DEFAULT_LOCALE = "en-US";
	
	public static String USER_DEFINED_VIEW_ALL_ANDROID_TABLETS = ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
			+ "<plist version=\"1.0\">"
			+ "<dict>"
			+ 		"<key>View Name Definition</key>"
			+ 		"<string>allandroidtablets</string>"
			+ 		"<key>View Definitions</key>"
			+		"<dict>"
			+ 			"<key>allandroidtablets</key>"
			+ 			"<dict>"
			+ 				"<key>Filter</key>"
			+ 				"<dict>"
			+ 					"<key>CompareValue</key>"
			+ 					"<array>"
            + 						"<dict>"
            + 							"<key>CachedInfoItemName</key>"
            + 							"<string>Mobile Device OS Platform</string>"
            + 							"<key>CompareValue</key>"
            + 							"<string>Android</string>"
            + 							"<key>CompareValue2</key>"
            + 							"<string></string>"
            + 							"<key>CompareValueUnits</key>"
            + 							"<string>Minutes</string>"
            + 							"<key>InfoItemID</key>"
            + 							"<string>8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5</string>"
            + 							"<key>IsCustomField</key>"
            + 							"<false/>"
            + 							"<key>Operator</key>"
            + 							"<string>==</string>"
            + 							"<key>UseNativeType</key>"
            +		 					"<false/>"
            + 						"</dict>"
            + 						"<dict>"
            + 							"<key>CachedInfoItemName</key>"
            + 							"<string>Mobile Device Is Tablet</string>"
            + 							"<key>CompareValue</key>"
            + 							"<true/>"
            + 							"<key>CompareValue2</key>"
            + 							"<string></string>"
            + 							"<key>CompareValueUnits</key>"
            + 							"<string>Minutes</string>"
            + 							"<key>InfoItemID</key>"
            + 							"<string>FA7F74E7-7E68-4C6A-ABFE-F8EFABD2F291</string>"
            + 							"<key>IsCustomField</key>"
            + 							"<false/>"
            + 							"<key>Operator</key>"
            + 							"<string>==</string>"
            + 							"<key>UseNativeType</key>"
            + 							"<true/>"
            + 						"</dict>"
            + 					"</array>"
            +					"<key>CriteriaFieldType</key>"
            + 					"<integer>0</integer>"
            + 					"<key>Operator</key>"
            + 					"<string>AND</string>"
            + 				"</dict>"
            + 				"<key>ColumnOrder</key>"
            + 				"<array>"
            + 					"<string>column_device_id</string>"
            + 					"<string>column_device_name</string>"
            + 					"<string>column_device_model</string>"
            + 					"<string>column_ios_version</string>"
            + 					"<string>column_device_serialnumber</string>"
            + 					"<string>column_device_last_contact</string>"
            + 					"<string>column_device_platform</string>"
            + 					"<string>column_device_platform_numeric</string>"
            + 					"<string>column_device_managed</string>"
            + 					"<string>column_device_absapps_version</string>"
            + 					"<string>column_device_cellular_technology</string>"
            + 				"</array>"
            + 				"<key>ColumnProperties</key>"
            + 				"<dict>"
            + 					"<key>column_device_id</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>39f3f074-b8a2-4df1-ac02-eb1f25f3f98e</string>"
            + 					"</dict>"
            + 					"<key>column_device_last_contact</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>4A8A81E0-0159-471D-B8D3-32E316CB81EF</string>"
            + 					"</dict>"
            + 					"<key>column_device_model</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>61479324-9E16-46FD-85E5-68F9865A7D6D</string>"
            + 					"</dict>"
            + 					"<key>column_device_name</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>FE5A9F56-228C-4BDA-99EC-8666292CB5C1</string>"
            + 					"</dict>"
            + 					"<key>column_device_serialnumber</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>B20868B8-CAEA-446B-BE8D-BEC97368E839</string>"
            + 					"</dict>"
            + 					"<key>column_ios_version</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0</string>"
            + 					"</dict>"
            + 					"<key>column_device_platform</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5</string>"
            + 					"</dict>"
            + 					"<key>column_device_platform_numeric</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>AE64A047-ACF2-40E2-B0A3-3F5565150FFA</string>"
            + 					"</dict>"
            + 					"<key>column_device_managed</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>26B03C68-0BF5-41ED-AD06-85903D5FBDFE</string>"
            + 					"</dict>"
            + 					"<key>column_device_absapps_version</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>00C2627C-E3D9-4C50-8948-0D96DDB36ACF</string>"
            + 					"</dict>"
            + 					"<key>column_device_cellular_technology</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>57856DAA-29CA-4721-B68B-101E321D30B6</string>"
            + 					"</dict>"
            +				"</dict>"
            +				"<key>RootTable</key>"
            + 				"<string>iphone_info</string>"
            +				"<key>SortOrder</key>"
            + 				"<array>"
            + 					"<dict>"
            + 						"<key>Ascending</key>"
            + 						"<true/>"
            + 						"<key>ColumnID</key>"
            + 						"<string>column_device_name</string>"
            + 						"</dict>"
            + 				"</array>"
            + 			"</dict>"
    		+ 		"</dict>"		
    		+ 	"</dict>"		
    		+ "</plist>"		
    			;
		
	public static String USER_DEFINED_VIEW_ALL_MOBILE_CONTENT = ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
			+ "<plist version=\"1.0\">"
			+ "<dict>"
			+ 		"<key>View Name Definition</key>"
			+ 		"<string>allmobilecontent</string>"
			+ 		"<key>View Definitions</key>"
			+		"<dict>"
			+ 			"<key>allmobilecontent</key>"
		    +			"<dict>"
		    + 				"<key>ColumnOrder</key>"
		    +				"<array>"
		    +					"<string>column_media_id</string>"
		    +					"<string>column_media_name</string>"
		    +					"<string>column_media_file_size</string>"
		    +					"<string>column_media_can_leave_app</string>"
		    +					"<string>column_can_be_emailed</string>"
		    +					"<string>column_can_be_printed</string>"
		    +					"<string>column_is_wifi_only</string>"
		    +					"<string>column_media_file_type</string>"
		    +					"<string>column_category</string>"
		    +					"<string>column_media_file_name</string>"
		    + 					"<string>column_last_modified</string>"
		    +					"<string>column_password</string>"
		    +				"</array>"
		    +				"<key>ColumnProperties</key>"
		    +				"<dict>"
		    +					"<key>column_password</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>68f6bbc2-3b7d-42da-a622-9428b409ac7e</string>"
		    +					"</dict>"
		    +					"<key>column_can_be_printed</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>0D15DD58-8895-48DD-9C8D-861D2824506B</string>"
		    +					"</dict>"
		    +					"<key>column_can_be_emailed</key>"
		    + 					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>8C8D7E49-DAEA-47A1-8B91-DB19F6E27578</string>"
		    +					"</dict>"
		    +					"<key>column_media_id</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>10413EEE-81C4-4AC7-9C7F-52581699FABB</string>"
		    +					"</dict>"
		    +					"<key>column_last_modified</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>410394C2-C903-4223-817C-8AF8125FC74F</string>"
		    +					"</dict>"
		    +					"<key>column_category</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>859F46B9-3710-45B9-B914-109F4F95CC68</string>"
		    +					"</dict>"
		    +					"<key>column_is_wifi_only</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>99D10C64-401E-4B93-8C11-E251ADFEC506</string>"
		    +					"</dict>"
		    +					"<key>column_media_can_leave_app</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>D17156FE-379D-45D3-8E41-A1D77EFEAFA0</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_name</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>F775E8A9-DBC6-4873-B9C2-1DCA3AF4369A</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_size</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>A332932E-DD61-4D8A-BCC6-53CAF45B513E</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_type</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>729FD2C9-FDBC-4B6A-96E0-0465CFCC602A</string>"
		    +					"</dict>"
		    +					"<key>column_media_name</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>0AE86506-A5B1-43C7-9037-5BF40C15F18A</string>"
		    +					"</dict>"
		    +				"</dict>"
		    +				"<key>OriginalRootTable</key>"
		    +				"<string>mobile_media</string>"
		    +				"<key>RootTable</key>"
		    +				"<string>mobile_media</string>"
		    +				"<key>SortOrder</key>"
		    +				"<array>"
		    +					"<dict>"
		    +						"<key>Ascending</key>"
		    +						"<true/>"
		    +						"<key>ColumnID</key>"
		    +						"<string>column_media_name</string>"
		    +					"</dict>"
		    +				"</array>"
		    +			"</dict>"
    		+ 		"</dict>"		
    		+ 	"</dict>"		
    		+ "</plist>"		
    			;

	public static String logonToWebAPI(
			String serverName,
			short serverPort,
			String userName,
			String password,
			String locale) throws Exception {
		return logonToWebAPI(WEBAPI_BASE_URL + LOGIN_API, serverName, serverPort, userName, password, DEFAULT_LOCALE);
	}
	
	// do the login with user credential included in the request body, the user credential must
	// be json formatted, for example:
	//  {"ServerName"::qaams8:,"ServerPort":3971,"UserName":"admin","Password":"qa;pass"}
	public static String logonToWebAPI(
			String loginUrl,
			String serverName,
			short serverPort,
			String userName,
			String password,
			String locale) throws Exception {

        HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpPost postRequest = new HttpPost(loginUrl);
 
		StringBuilder body = new StringBuilder();
		body.append("{");
		body.append("\"serverName\":\"").append(serverName).append("\"");
		body.append(",\"serverPort\":\"").append(serverPort).append("\"");
		body.append(",\"userName\":\"").append(userName).append("\"");
		body.append(",\"password\":\"").append(password).append("\"");
		body.append(",\"locale\":\"").append(locale).append("\"");
		body.append("}");

		StringEntity input = new StringEntity(body.toString());
		input.setContentType("application/json");
		postRequest.setEntity(input);
 
		HttpResponse response = httpClient.execute(postRequest);
 
		return getLoginCookieFromLoginResponse(response);
	}

	// do the login with user credential included in the Url, for example 
	//  'api/login?ServerName=qaams8&ServerPort=3971&UserName=admin&Password=qa%3Bpass&Locale=en_US'
	public static String logonToWebAPI(
			String loginUrlWithLogonCredential) throws Exception {

        HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpPost postRequest = new HttpPost(WEBAPI_BASE_URL + loginUrlWithLogonCredential);

		StringEntity input = new StringEntity("");
		input.setContentType("application/x-www-form-urlencoded");
		postRequest.setEntity(input);
 
		HttpResponse response = httpClient.execute(postRequest);
 
		return getLoginCookieFromLoginResponse(response);
	}

	/*
	 * Create a new HttpClient that doesn't check for any certificate errors.
	 * This is required as most of our QA environments have bad/expired SSL certificates.
	 */
	public static HttpClient createHttpClientWithoutCertificateChecking() throws NoSuchAlgorithmException, KeyManagementException{
		SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null,
                new TrustManager[]{new X509TrustManager() {
                	@Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

					@Override
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {}
					
					@Override
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {}
                }}, new SecureRandom());

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        return HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();
	}
	
	
	public static void logoffFromWebAPI(
			String loginCookie,
			String loginUrl) throws Exception {

		HttpClient httpClient = createHttpClientWithoutCertificateChecking();

		HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(loginUrl);
		deleteRequest.addHeader("Cookie", loginCookie);
		 
		HttpResponse response = httpClient.execute(deleteRequest);
 
		if (response.getStatusLine().getStatusCode() != 204) {
			throw new RuntimeException("Failed : Unexpected HTTP status code : "
				+ response.getStatusLine().getStatusCode());
		}
	}

	/**
	 * Helper method to post a request, check for a 2xx status code, and return the response body.
	 * @param loginCookie - the cookie returned in the login command
	 * @param postURL - the URL to post the request to.
	 * @param jsonPostBody - the body of the post message
	 * @return the response body
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String postJsonRequestAndGetResult(
			String loginCookie,
			String postURL, 
			String jsonPostBody) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {

		return 	postJsonRequestGetResultCheckStatus(
				loginCookie,
				postURL, 
				jsonPostBody,
				200,			// 200-299 are the default success codes
				299);
	}
	
	/**
	 * Helper method to post a request, check the status code, and return the response body.
	 * @param loginCookie - the cookie returned in the login command
	 * @param postURL - the URL to post the request to.
	 * @param jsonPostBody - the body of the post message
	 * @param expectedStatus - the status code to expect. A runtime exception is thrown if
	 * 							this status code is not returned. 
	 * @return the response body
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String postJsonRequestGetResultCheckStatus(
			String loginCookie,
			String postURL, 
			String jsonPostBody,
			int expectedStatus) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {

		return 	postJsonRequestGetResultCheckStatus(
				loginCookie,
				postURL, 
				jsonPostBody,
				expectedStatus,
				expectedStatus);
	}
	
	/**
	 * Helper method to post a request, check the status code, and return the response body.
	 * @param loginCookie - the cookie returned in the login command
	 * @param postURL - the URL to post the request to.
	 * @param jsonPostBody - the body of the post message
	 * @param minStatusCode - the minimum status code to accept as good.
	 * @param maxStatusCode - the maximum status code to accept as good.
	 * @return the response body
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String postJsonRequestGetResultCheckStatus(
			String loginCookie,
			String postURL, 
			String jsonPostBody,
			int minStatusCode,
			int maxStatusCode) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		
		return postRequestGetResultCheckStatus(
				loginCookie,
				postURL, 
				ContentType.APPLICATION_JSON.getMimeType(),//"application/json",
				jsonPostBody,
				minStatusCode,
				maxStatusCode);
	}
	
	private static String getResponseBody(HttpEntity responseEntity) throws IllegalStateException, IOException {
		
		StringBuilder responseBody = new StringBuilder();
		if (responseEntity != null &&
				responseEntity.getContent() != null) {
			BufferedReader br = new BufferedReader(
	                new InputStreamReader(responseEntity.getContent()));
	
			String output;
			while ((output = br.readLine()) != null) {
				responseBody.append(output);
			}
		}
		return responseBody.toString();
	}
	
	public static String deleteRequestGetResultCheckStatus(
			String loginCookie,
			String deleteURL,
			String jsonDeleteBody) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
		
		return deleteRequestGetResultCheckStatus(loginCookie, deleteURL, jsonDeleteBody, 200, 299);
	}
	
	public static String deleteRequestGetResultCheckStatus(
			String loginCookie,
			String deleteURL,
			String jsonDeleteBody,
			int minStatusCode,
			int maxStatusCode) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		
		HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpDeleteWithBody deleteRequest = new HttpDeleteWithBody(deleteURL);
		deleteRequest.addHeader("Cookie", loginCookie);

		if(jsonDeleteBody != null){
			StringEntity input = new StringEntity(jsonDeleteBody);
			input.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			deleteRequest.setEntity(input);
		}

		HttpResponse response = httpClient.execute(deleteRequest);
 
		if (response.getStatusLine().getStatusCode() < minStatusCode || response.getStatusLine().getStatusCode() > maxStatusCode) {
			StringBuilder message = new StringBuilder();
			message.append("" +
					"Unexpected status code:").append(response.getStatusLine().getStatusCode());
			message.append(" Response Body:");
			message.append(getResponseBody(response.getEntity()));
			throw new RuntimeException(message.toString());
		}
							  
		return getResponseBody(response.getEntity());
	}


	/**
	 * Helper method to post a HTTP request and confirm that the returned HTTP status is
	 * within a specified range. A RuntimeException is thrown if the status is not as expected.
	 * @param loginCookie - the cookie returned in the login command
	 * @param postURL - the URL to post the request to.
	 * @param postBody - the body of the post message
	 * @param contentType - the string to use for the Content-Type header
	 * @param minStatusCode - the minimum status code to expect, e.g. 200.
	 * @param maxStatusCode - the maximum status code to expect, e.g. 299. Set to 
	 * 						the same value as minStatusCode to check that a specific code
	 * 						is returned.
	 * @return the response body
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String postRequestGetResultCheckStatus(
			String loginCookie,
			String postURL, 
			String contentType,
			String jsonPostBody,
			int minStatusCode,
			int maxStatusCode) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		
		HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpPost postRequest = new HttpPost(postURL);
		postRequest.addHeader("Cookie", loginCookie);
 
		StringEntity input = new StringEntity(jsonPostBody);
		input.setContentType(contentType);
		postRequest.setEntity(input);
 
		HttpResponse response = httpClient.execute(postRequest);
 
		if (response.getStatusLine().getStatusCode() < minStatusCode || response.getStatusLine().getStatusCode() > maxStatusCode) {
			StringBuilder message = new StringBuilder();
			message.append("" +
					"Unexpected status code:").append(response.getStatusLine().getStatusCode());
			message.append(" Response Body:");
			message.append(getResponseBody(response.getEntity()));
			throw new RuntimeException(message.toString());
		}
							  
		return getResponseBody(response.getEntity());
	}
	
	/**
	 * Helper method to post a HTTP request with a file and confirm that the returned HTTP status is
	 * within a specified range. A RuntimeException is thrown if the status is not as expected.
	 * @param loginCookie - the cookie returned in the login command
	 * @param postURL - the URL to post the request to.
	 * @param data - byte array of data
	 * @param contentType - the string to use for the Content-Type header
	 * @param minStatusCode - the minimum status code to expect, e.g. 200.
	 * @param maxStatusCode - the maximum status code to expect, e.g. 299. Set to the same value as minStatusCode
	 * 				to check that a specific code is returned.
	 * @return the response body
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String postDataGetResultCheckStatus(
			String loginCookie,
			String postURL, 
			String contentType,
			byte[] data,
			int minStatusCode,
			int maxStatusCode) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		
		HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpPost postRequest = new HttpPost(postURL);
		postRequest.addHeader("Cookie", loginCookie);
		postRequest.addHeader("Content-Type", contentType);
		
		ByteArrayEntity binaryInput = new ByteArrayEntity(data);
		binaryInput.setContentType(contentType);
		postRequest.setEntity(binaryInput);
		
		HttpResponse response = httpClient.execute(postRequest);
 
		if (response.getStatusLine().getStatusCode() < minStatusCode || response.getStatusLine().getStatusCode() > maxStatusCode) {
			StringBuilder message = new StringBuilder();
			message.append("" +
					"Unexpected status code:").append(response.getStatusLine().getStatusCode());
			message.append(" Response Body:");
			message.append(getResponseBody(response.getEntity()));
			throw new RuntimeException(message.toString());
		}
							  
		return getResponseBody(response.getEntity());
	}

	public static String doGETCheckStatusReturnBody(
			String loginCookie,
			String getURL,
			int minStatusCode,
			int maxStatusCode) throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {

        HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpGet getRequest = new HttpGet(getURL);
		getRequest.addHeader("Cookie", loginCookie);
 
		HttpResponse response = httpClient.execute(getRequest);
 
		if (response.getStatusLine().getStatusCode() < minStatusCode || response.getStatusLine().getStatusCode() > maxStatusCode) {
			StringBuilder message = new StringBuilder();
			message.append("" +
					"Unexpected status code:").append(response.getStatusLine().getStatusCode());
			message.append(" Response Body:");
			message.append(getResponseBody(response.getEntity()));
			throw new RuntimeException(message.toString());
		}
							  
		return getResponseBody(response.getEntity());
	}	
	
	// The row Id (557) is the first element in the row. 
	// [557, WebAPIUnitTestPDF_for_update.pdf...]
	// The row id is the number from the start of the string to the first "," 
	public static String getRowId(String viewAsString) throws JsonParseException, JsonMappingException, IOException {
		String id = null;
		if (viewAsString != null) {
			ObjectMapper mapper = new ObjectMapper();
			Result result = mapper.readValue(viewAsString, Result.class);
			assertNotNull(result);
			Object[] obj = result.getRows();
			if (obj.length > 0) {
				String rowAsString = obj[0].toString();
				int startIndex = getIdStartIndex(rowAsString);
				id = rowAsString.substring(startIndex, rowAsString.indexOf(","));
				System.out.println("ROW = " + rowAsString + " ID = " + id);
			}
		}
		return id;
	}
	
	//helper function to make getRowId() more robust
	//when id is preceded by space or other character
	private static int getIdStartIndex(String str) {
		
		if(str.length() > 0) {
			for(int i=0; i<str.length(); i++) {
				String chr = String.valueOf(str.charAt(i));
				if(StringUtilities.isStringNumeric(chr)) {
					return i;
				}
			}
		}
		return 0;
	}
	
	// The row serial (D1DFFF1B-9B0C-4E52-8EEF-B79815DE4DA8) is the first element in the row. 
	// ["D1DFFF1B-9B0C-4E52-8EEF-B79815DE4DA8",16,"machine_not_available"....
	// The row serial is the UUID from the start of the string to the first "," 
	public static String getRowSerial(String viewAsString) throws JsonParseException, JsonMappingException, IOException {
		String id = null;
		if (viewAsString != null) {
			ObjectMapper mapper = new ObjectMapper();
			Result result = mapper.readValue(viewAsString, Result.class);
			assertNotNull(result);
			Object[] obj = result.getRows();
			if (obj.length > 0) {
				String rowAsString = obj[0].toString();
				int startIndex = getSerialStartIndex(rowAsString);
				id = rowAsString.substring(startIndex, rowAsString.indexOf(","));
				System.out.println("ROW = " + rowAsString + " ID = " + id);
			}
		}
		return id;
	}
	
	//helper function to make getRowSerial() more robust
	//when serial is preceded by space or other character
	private static int getSerialStartIndex(String str) {
		
		if(str.length() > 0) {
			for(int i=0; i<str.length(); i++) {
				if(Character.isLetterOrDigit(str.charAt(i))) {
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Helper method to read the contents of a file into a byte array.
	 * An IOException is thrown if the file cannot be read.
	 * @param file - the file to be read
	 * @return byte array
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int)length];
    
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        
        is.close();
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file '" + file.getName() + "'");
        }
        
        return bytes;
    }
	
	public static byte[] postRequestGetResponse(
			String logonCookie,
			byte[] body,
			String endpoint,
			String contentType) throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpPost postRequest = new HttpPost(endpoint);
		postRequest.addHeader("Cookie", logonCookie);

		ByteArrayEntity binaryInput = new ByteArrayEntity(body);
		binaryInput.setContentType(contentType);
		postRequest.setEntity(binaryInput);
 
		HttpResponse response = httpClient.execute(postRequest);
 
		if (response.getStatusLine().getStatusCode() != 200 &&
				response.getStatusLine().getStatusCode() != 204) {
			String responseBody = getResponseBody(response.getEntity());
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode() +
				" Body: [" + responseBody + "].");
		}
		
		for (int i=0; i<response.getAllHeaders().length; i++) {
			System.out.println("Header[" + i + "]=[" + response.getAllHeaders()[i].toString());
		}
		
		Header[] setCookieHeader = response.getHeaders("Set-Cookie");
		if (setCookieHeader.length > 0) {
			setCookieHeader[0].getValue();
		}

		byte[] responseBody = getResponseBodyAsBinary(response.getEntity());
		return responseBody;		
	}

	public static byte[] getResponseBodyAsBinary(HttpEntity responseEntity) throws IllegalStateException, IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (responseEntity != null &&
				responseEntity.getContent() != null) {
			BufferedInputStream bis = new BufferedInputStream(responseEntity.getContent());
			
			int len = 0;
			byte[] tmp = new byte[1024];
			len = bis.read(tmp);
			while (len != -1) {
				baos.write(tmp, 0, len);
				len = bis.read(tmp);
			}			
		}
		return baos.toByteArray();
	}


	public static byte[] doGetReturnResponse(
			String endpoint, String logonCookie) throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		HttpClient httpClient = createHttpClientWithoutCertificateChecking();
		HttpGet getRequest = new HttpGet(endpoint);
		getRequest.addHeader("Cookie", logonCookie);
 		
		HttpResponse response = httpClient.execute(getRequest);
 
		if (response.getStatusLine().getStatusCode() != 200 &&
				response.getStatusLine().getStatusCode() != 204 &&
				response.getStatusLine().getStatusCode() != 404) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		for (int i=0; i<response.getAllHeaders().length; i++) {
			System.out.println("Header[" + i + "]=[" + response.getAllHeaders()[i].toString());
		}
		
		Header[] setCookieHeader = response.getHeaders("Set-Cookie");
		if (setCookieHeader.length > 0) {
			setCookieHeader[0].getValue();
		}

		byte[] responseBody = getResponseBodyAsBinary(response.getEntity());
		return responseBody;		
	}

	/**
	 * Helper method to wait for the default delay for the Admin Console to catch up. This can 
	 * be removed once the real SyncService has been added.
	 * @throws InterruptedException
	 */
	public static void waitForAdminConsoleToCatchUp() throws InterruptedException {
		// Before the SyncService was available, we used to have to wait for the AdminConsole to sync the data in the background.
		
		//waitForAdminConsoleToCatchUp(5000);
		
		waitForAdminConsoleToCatchUp(1);
	}
	
	/**
	 * Helper method to wait a specified delay for the Sync  
	 * @param msToWait
	 * @throws InterruptedException
	 */
	public static void waitForAdminConsoleToCatchUp(int msToWait) throws InterruptedException {
		System.out.println("TODO: remove when sync service is ready. Thread.sleep(" + msToWait + ") to allow Admin Console to sync up.");
		Thread.sleep(msToWait);
	}
	
	/**
	 * The filename supplied should be the display filename. This should be unique 
	 * within the system. If there are multiple rows found for a given display name
	 * then the first row's id will be the file deleted. Others wont be deleted.
	 * So its important that the display filename be unique within the system.
	 * 
	 * @param logonCookie
	 * @param filenames
	 * @throws InterruptedException
	 */
	public static void deleteFilesFromSystem(String logonCookie, String... filenames) throws InterruptedException {
		for (String filename: filenames) {
			try {
				String viewResultFile = doGETCheckStatusReturnBody(logonCookie, 
						WEBAPI_BASE_URL + "api/views/allmobilecontent?$search=" + URLEncoder.encode(filename, "UTF-8"), 200, 200);
				// Note: getRowId only gets the id for the first file in the Row[]. If there are multiple fiels returned from the above search, 
				// then only the first will be deleted. 
				String mediaId = getItemValue(viewResultFile, "id");
				if (mediaId != null) {
					deleteRequestGetResultCheckStatus(logonCookie, WEBAPI_BASE_URL + CONTENT_API + "/" + mediaId, null, 200, 299);
				}
			} catch (Exception e) {
				throw new RuntimeException("deleteFileFromSystem failed for filename:" + filename, e);
			}
		}
		
		waitForAdminConsoleToCatchUp();
	}
	
	public static String addFilesToSystem(String logonCookie, String[] filenames, String[] displayFilenames, String[] policyIds) throws Exception {
		if (filenames.length != displayFilenames.length) {
			throw new Exception("filenames.length != displayFilenames.length");
		}
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"newFiles\":[");

		int i = 0;
		for (String filename : filenames) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("{");
			sb.append("\"fileName\":\"" + filename + "\",");
			sb.append("\"displayName\":\"" + displayFilenames[i] + "\",");
			sb.append("\"description\":\"This is my " + displayFilenames[i] + ".\",");
			
			// TODO: extract the extension of the file and use that as the fileType, also support case where there is no extension. 
			String fileExtension = FileUtilities.getExtensionFromFilePath(filename);
			
			// Assign different options/settings based on the file extension.
			if (fileExtension.equals("pdf")) {
				sb.append("\"category\":\"Documents\",");
				sb.append("\"fileType\":\"").append(fileExtension).append("\",");
				sb.append("\"canLeaveApp\":false,");
				sb.append("\"canEmail\":false,");
				sb.append("\"canPrint\":false,");
				sb.append("\"transferOnWifiOnly\":true,");
				sb.append("\"passphrase\":\"secret\",");
			} else if (fileExtension.toLowerCase().equals("png")) {
				sb.append("\"category\":\"Pictures\",");
				sb.append("\"fileType\":\"").append(fileExtension).append("\",");
				sb.append("\"canLeaveApp\":false,");
				sb.append("\"canEmail\":false,");
				sb.append("\"canPrint\":false,");
				sb.append("\"transferOnWifiOnly\":true,");
				sb.append("\"passphrase\":\"secret\",");
			} else {
				sb.append("\"category\":\"Other\",");
				sb.append("\"fileType\":\"").append(fileExtension).append("\",");
				sb.append("\"canLeaveApp\":true,");
				sb.append("\"canEmail\":true,");
				sb.append("\"canPrint\":true,");
				sb.append("\"transferOnWifiOnly\":false,");
				sb.append("\"passphrase\":\"\",");
			}
			sb.append("\"fileModDate\":\"2012-10-12T22:39:31Z\"");
			sb.append("}");	
			i++;
		}
		sb.append("],");
		sb.append("\"assignToPolicies\":[");
		i = 0;
		for (String policyId : policyIds) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("{");
			sb.append("\"policyId\": " + policyId + ",");	// WebAPIUnitTest1 policy
			sb.append("\"availabilitySelector\": 0,");				// always
			sb.append("\"assignmentType\": 1,");					// Policy Optional (i.e. on-demand, auto remove)
			sb.append("\"startTime\":\"2012-10-18T19:01:00Z\",");	// ignored for this availabilitySelector
			sb.append("\"endTime\":\"2012-10-19T20:12:00Z\"");		// ignored for this availabilitySelector
			sb.append("}");
			i++;
		}
		sb.append("]");
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		// upload the request.		
		String result = Helpers.postJsonRequestAndGetResult(logonCookie, WEBAPI_BASE_URL + BATCH_CONTENT_API, sb.toString());
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);
		// Wait until the content/batch job reaches 100%
		waitForContentBatchToComplete(jobId.getJobId(), logonCookie);
		return result;
	}
	
	/**
	 * 
	 * @param jobId - the job to wait for
	 * @param logonCookie
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static void waitForContentBatchToComplete(String jobId, String logonCookie) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		boolean jobInProgress = true;
		int counter = 0; 
		int waitCount = 120;
		// When counter = 120, 2 minutes have passed
		while (jobInProgress && counter < waitCount) {
			try {
				counter++;
				Thread.sleep(1000);
			}
			catch (Exception e) {
			}
			String jobStatusResult = Helpers.doGETCheckStatusReturnBody(logonCookie, WEBAPI_BASE_URL + JOB_API + jobId + "/status", 200, 200);
			ObjectMapper mapper = new ObjectMapper();
			JobStatusResult jobStatus = mapper.readValue(jobStatusResult, JobStatusResult.class);
			if (jobStatus.getPercentComplete() == 100) {
				jobInProgress = false;
			}
		}	
		// We want the loop to finish because the upload got to 100% not because of a timeout.
		assertTrue(counter != waitCount);
	}
	
	/**
	 * 
	 * @param url - the url for the query
	 * @param logonCookie
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static Object[] get_rows_from_query_url(String logonCookie, String url) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {
		
		String viewResult = doGETCheckStatusReturnBody(logonCookie, 
				url, 
				200, 200);
		
		return get_rows_from_result(viewResult);
	}
	
	/**
	 * 
	 * @param viewResult - the result string from query
	 * @param logonCookie
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static Object[] get_rows_from_result(String viewResult) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		
		return result.getRows();
	}
	
	/**
	 * Loop over all rows in the restulSet to see if there is one whose first 2 values 
	 * match val1 and val2. 
	 * @param viewResult
	 * @param val1 - first value to check
	 * @param val2 - second value to check
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void check_first_2_entries_of_resultset(
			String viewResult, Object val1, Object val2) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		Object[] rows = (Object[])result.getRows();
		boolean found = false;
		for (int i = 0; i < rows.length; i++) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> row = (ArrayList<Object>)rows[i];
			Object object1 = row.get(0);
			Object object2 = row.get(1);
			boolean foundVal1 = false;
			boolean foundVal2 = false;

			if (object1 != null && val1 != null) {
				if ((String.valueOf(object1)).equals(String.valueOf(val1))) {
					foundVal1 = true;
				}
			} else {
				if (object1 == null && val1 == null) {
					foundVal1 = true;
				}
			}
			if (object2 != null && val2 != null) {
				if ((String.valueOf(object2)).equals(String.valueOf(val2))) {
					foundVal2 = true;
				}
			} else {
				if (object2 == null && val1 == null) {
					foundVal2 = true;
				}
			}
			if (foundVal1 && foundVal2) {
				found = true;
				break;
			}
		}
		String message = "looking for row with col[0]=[";
		if (val1 != null) {
			message += val1.toString();
		} else {
			message += "null";
		}
		message += "] and col[1]=[";
		if (val2 != null) {
			message += " " + val2.toString();
		} else {
			message += "null";
		}
		message += "].";
		assertTrue(message, found);
	}
	
	/**
	 * Loop over all rows in the restulSet to see if there are rows that contain specified values 
	 * val1 and val2. 
	 * @param viewResult
	 * @param val1 - first value to check
	 * @param val2 - second value to check
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static boolean check_resultset_for_2_strings(
			String viewResult, Object val1, Object val2) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		Object[] rows = (Object[])result.getRows();
		boolean foundVal1 = false, foundVal2 = false;
		for (int i = 0; i < rows.length; i++) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> row = (ArrayList<Object>)rows[i];
			for (int j = 0; j < row.toArray().length; j++) {
				Object obj = row.get(j);
				if (obj != null && val1 != null) {
					if (obj.toString().indexOf(val1.toString()) >= 0) {
						foundVal1 = foundVal1 || true;
					}
				} else {
					if (obj == null && val1 == null) {
						foundVal1 = true;
					}
				}
				if (obj != null && val2 != null) {
					if (obj.toString().indexOf(val2.toString()) >= 0) {
						foundVal2 = foundVal2 || true;
					}
				} else {
					if (obj == null && val2 == null) {
						foundVal2 = true;
					}
				}
			}
		}
		
		return foundVal1 && foundVal2;
	}
	
	/**
	 * Check the first field in the first row in the restulSet to see if its value matches val1. 
	 * @param viewResult
	 * @param val1 - first value to check
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void check_first_field_of_first_entry_of_resultset(
			String viewResult, Object val1) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		Object[] rows = (Object[])result.getRows();
		@SuppressWarnings("unchecked")
		ArrayList<Object> row = (ArrayList<Object>)rows[0];
		Object object1 = row.get(0);
		boolean found = false;
		if (object1.equals(val1)) {
				found = true;
		}
		assertTrue("looking for row with col[0]=[" + val1.toString() + "].", found);
	}
	
	/**
	 * Check to verify that empty result set is returned. 
	 * @param viewResult
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void check_for_empty_resultset(
			String viewResult) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		Object[] rows = (Object[])result.getRows();
		boolean isEmpty = false;
		if (rows.length == 0) {
			isEmpty = true;
		}
		assertTrue("Empty result set.", isEmpty);
	}
	
	/**
	 * Helper method to get the ID for one or more policies by name. Pass the policy names in sequence, e.g.
	 * <p><code>String policyIds[] = Helpers.getPolicyIdsForPolicyNames(logonCookie, "My Policy", "Smart policy 1", "other policy");</code></p>
	 * The id for each policy will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param policyNames - a sequence of policy names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getPolicyIdsForPolicyNames(String logonCookie, String ...policyNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		return getItemIdsForNames(logonCookie, "api/policies/views/all?$search=", "policy", "id", policyNames);
	}
	
	public static String[] getPolicyUuidsForPolicyNames(String logonCookie, String ...policyNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		return getItemIdsForNames(logonCookie, "api/policies/views/all?$search=", "policy", "uniqueid", policyNames);
	}
	
	/**
	 * Helper method to get the ID for one or more devices by name. Pass the device names in sequence, e.g.
	 * <p><code>String deviceIds[] = Helpers.getDeviceIdsForDeviceNames(logonCookie, "My device", "My device 2", "other device");</code></p>
	 * The id for each device will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param deviceNames - a sequence of device names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */	
	public static String[] getDeviceIdsForDeviceNames(String logonCookie, String ...deviceNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/mobiledevices/views/all?$search=", "device", "id", deviceNames);
	}	

	/**
	 * Helper method to get the ID for one or more devices by name. Pass the device names in sequence, e.g.
	 * <p><code>String deviceIds[] = Helpers.getDeviceIdsForDeviceNames(logonCookie, "My device", "My device 2", "other device");</code></p>
	 * The id for each device will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param computerNames - a sequence of device names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */	
	public static String[] getComputerSerialsForComputerNames(String logonCookie, String ...computerNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String[] result = new String[computerNames.length];
		
		for (int i=0; i<computerNames.length; i++) {
			String resultBody = Helpers.doGETCheckStatusReturnBody(logonCookie, 
					WEBAPI_BASE_URL + "api/computers/views/all?$search=" + URLEncoder.encode(computerNames[i], "UTF-8"), 200, 200);
			String computerSerial = Helpers.getRowSerial(resultBody);
			if (computerSerial == null) {
				throw new RuntimeException("ID not found for device " + computerNames[i] + ".");
			}
			
			result[i] = computerSerial;
		}
		
		return result;
	}
	
	public static String[] getComputerIdsForComputerName(String logonCookie, String ...computerNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		return getItemIdsForNames(logonCookie, "api/computers/views/all?$search=", "computer", "id", computerNames);
	}
	
	/**
	 * Helper method to get the ID for one or more content items by name. Pass the content names in sequence, e.g.
	 * <p><code>String contentIds[] = Helpers.getContentIdsForContentNames(logonCookie, "My content", "My content 2", "other content");</code></p>
	 * The id for each content item will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param contentNames - a sequence of content names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getContentIdsForContentNames(String logonCookie, String ...contentNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/content/views/all?$search=", "content", "id", contentNames);
	}	

	/**
	 * Helper method to get the ID for one or more configuration profiles by name. Pass the configuration profile names in sequence, e.g.
	 * <p><code>String configurationProfileIds[] = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, "My config profile 1", "My config profile 2", "other configuration profile");</code></p>
	 * The id for each configuration profile will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param configurationProfilesNames - a sequence of configuration profile names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getConfigurationProfileIdsForConfigurationProfileNames(String logonCookie, String ...configurationProfilesNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException{
		return getItemIdsForNames(logonCookie, "api/configurationprofiles/views/all?$search=", "configuration profile", "id", configurationProfilesNames);
	}

	public static String[] getConfigurationProfileUuidsForConfigurationProfileNames(String logonCookie, String ...configurationProfilesNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException{
		return getItemIdsForNames(logonCookie, "api/configurationprofiles/views/all?$search=", "configuration profile", "uniqueid", configurationProfilesNames);
	}
	/**
	 * Helper method to get the ID for one or more provisioning profiles by name. Pass the provisioning profile names in sequence, e.g.
	 * <p><code>String provisioningProfileIds[] = Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, "My provisioning profile 1", "My provisioning profile 2", "other provisioning profile");</code></p>
	 * The id for each provisioning profile will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param provisioningProfilesNames - a sequence of provisioning profile names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getProvisioningProfileIdsForProvisioningProfileNames(String logonCookie, String ...provisioningProfilesNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/provisioningprofiles/views/all?$search=", "provisioning profile", "id", provisioningProfilesNames);
	}	

	/**
	 * Helper method to get the ID for one or more installed configuration profiles by names and device ID. Pass the configuration profile names in sequence, e.g.
	 * <p><code>String configurationProfileIds[] = Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNames(logonCookie, "My config profile 1", "My config profile 2", "other configuration profile");</code></p>
	 * The id for each configuration profile will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * Note that these IDs are from iphone_installed_profile_info table, not the same as in iOS_configuration_profile table
	 * 
	 * @param logonCookie - the logon cookie
	 * @param deviceId - the id of mobile device
	 * @param configurationProfilesNames - a sequence of configuration profile names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndDeviceId(String logonCookie, String deviceId, String ...configurationProfilesNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/mobiledevices/" + deviceId + "/configurationprofiles?$search=", "configuration profile", "id", configurationProfilesNames);
	}
	
	/**
	 * Helper method to get the ID for one or more installed configuration profiles by names and policy ID. Pass the configuration profile names in sequence.
	 * The id for each configuration profile will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param policyId - the id of mobile policy
	 * @param configurationProfilesNames - a sequence of configuration profile names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(String logonCookie, String policyId, String ...configurationProfilesNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/policies/" + policyId + "/configurationprofiles?$search=", "configuration profile", "id", configurationProfilesNames);
	}
	
	/**
	 * Helper method to get the ID for one or more available third party applications by name. Pass the application names in sequence.
	 * The id for each third party application will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param appNames - a sequence of third party application names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getThirdPartyAppIdsForThirdPartyAppNames(String logonCookie, String ...appNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/thirdpartyapps/views/all?$search=", "third party application", "id", appNames);
	}
	
	/**
	 * Helper method to get the ID for one or more available in-house applications by name. Pass the application names in sequence.
	 * The id for each in-house application will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param appNames - a sequence of in-house application names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getInHouseAppIdsForInHouseAppNames(String logonCookie, String ...appNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/inhouseapps/views/all?$search=", "in-house application", "id", appNames);
	}
	
	public static String[] getMediaUuidsForMediaNames(String logonCookie, String ...appNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/views/allmobilecontent?$search=", "media", "media unique identifier", appNames);
	}
	
	/**
	 * Helper method to get the ID for one or more available books by name. Pass the book names in sequence.
	 * The id for each book will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param bookNames - a sequence of book names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getBookIdsForBookNames(String logonCookie, String ...bookNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/books/views/all?$search=", "book", "id", bookNames);
	}
	
	public static String[] getCustomFieldIdsForCustomFieldNames(String logonCookie, String ...customFieldNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/customfields/views/all?$search=", "custom field", "id", customFieldNames);
	}
	
	public static String[] getCustomFieldForMobileDeviceDataForCustomFieldNames(String logonCookie, String deviceId, String ...customFieldNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/mobiledevices/" + deviceId +  "/customfields?$search=", "custom field", "data", customFieldNames);
	}
	
	/**
	 * Helper method to get the ID for one or more available action by name. Pass the action names in sequence.
	 * The id for each action will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param actionNames - a sequence of action names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getActionIdsForActionNames(String logonCookie, String ...actionNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/actions/views/all?$search=", "action", "id", actionNames);
	}
	
	public static String[] getActionUuidsForActionNames(String logonCookie, String ...actionNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		return getItemIdsForNames(logonCookie, "api/actions/views/all?$search=", "action", "Mobile Action Unique Identifier", actionNames);
	}
	
	public static String getMobileDevicePerformActionHistoryIdByMobileDeviceId(String logonCookie, String mobileDeviceId) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		String result = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL +"api/mobiledevices/" + mobileDeviceId + "/actions", 
				200, 200);
		return getItemValue(result, "Performed Action History Id");
	}
	/**
	 * Helper method to get the IDs for multiple actions which are assigned to the policy specified by policy Id. 
	 * Pass the action names in sequence.
	 * The id for each action will be returned in the same order in the array.
	 * If the id cannot be found, an exception will be thrown. 
	 * 
	 * @param logonCookie - the logon cookie
	 * @param policyId - the policy Id which the actions are associated with
	 * @param actionNames - a sequence of action names
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static String[] getActionIdsForActionNamesAndPolicyId(String logonCookie, String policyId, String ...actionNames) throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return getItemIdsForNames(logonCookie, "api/policies/" + policyId + "/actions?$search=", "action", "id", actionNames);
	}
	
	// Get an array of item values by querying the provided URL ( logonCookie required for this ) by comparing the itemRowName with each one of the provided names
	// Note that the itemName param is used for debugging purposes only
	private static String[] getItemIdsForNames(String logonCookie, String queryUrl, String itemName, String itemRowName, String ...names) 
			throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException  {
		String[] result = new String[names.length];
		
		for (int i=0; i<names.length; i++) {
			String allPoliciesResult = doGETCheckStatusReturnBody(logonCookie, 
					WEBAPI_BASE_URL + queryUrl + URLEncoder.encode(names[i], "UTF-8"), 200, 200);
			String id = getItemValue(allPoliciesResult, itemRowName);
			
			if (id == null) {
				throw new RuntimeException("ID not found for " + itemName + " " + names[i] + ".");
			}
			
			result[i] = id;
		}
		
		return result;
	}
	
	// Get an item value given the name of the item ( must match up to ShortDisplayName or DisplayName ) and the view represented as a string
	private static String getItemValue(String viewAsString, String item) throws JsonParseException, JsonMappingException, IOException {
		String value = null;
		
		if (viewAsString != null) {
			ObjectMapper mapper = new ObjectMapper();
			Result result = mapper.readValue(viewAsString, Result.class);
			
			assertNotNull(result);
			
			Object[] rows = result.getRows();
			ArrayList<Map<String, Object>> columnMetaData = result.getMetaData().getColumnMetaData();
			int dataColumnNum = -1;

			// find the data column location in the view given that the "ShortDisplayName" is unique in a view
			for(int i=0; i<columnMetaData.size(); i++){
				Object displayName = columnMetaData.get(i).get("ShortDisplayName");
				if( displayName == null){
					displayName = columnMetaData.get(i).get("DisplayName");
				}
				
				if (displayName.toString().equalsIgnoreCase(item)){
					dataColumnNum=i;
					break;
				}
			}
			
			// check if the data exists
			if (rows.length != 0 && dataColumnNum != -1) {
				String rowString = rows[0].toString();
				value = rowString.substring(1, rowString.length()-1).split(",")[dataColumnNum].trim();
				System.out.println("ROW = " + rows[0].toString() + ", ITEM = " + item + ", VALUE = " + value);
			}
		}
		
		return value;
	}
	
	private static String getLoginCookieFromLoginResponse(HttpResponse response) 
			throws IllegalStateException, IOException
	{
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		for (int i=0; i<response.getAllHeaders().length; i++) {
			System.out.println("Header[" + i + "]=[" + response.getAllHeaders()[i].toString());
		}
		
		String loginCookie = null;
		Header[] setCookieHeader = response.getHeaders("Set-Cookie");
		if (setCookieHeader.length > 0) {
			loginCookie = setCookieHeader[0].getValue();
		}
						 
		BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));
 
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
 
		//httpClient.getConnectionManager().shutdown(); 

		return loginCookie;
	}
}