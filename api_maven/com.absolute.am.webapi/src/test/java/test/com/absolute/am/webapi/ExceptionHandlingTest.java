/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 *
 */
public class ExceptionHandlingTest extends LoggedInTest {
	
	/**
	 * These endpoints are for test purposes only and they are designed to always throw exceptions. 
	 */
	private static final String TESTS_API = "api/tests";
	private static final String TEST_UNHANDLED_EXCEPTION_API = TESTS_API + "/unhandledexception";
	private static final String TEST_WEBAPI_EXCEPTION_API = TESTS_API + "/webapiexception";
	private static final String TEST_BADREQUEST_EXCEPTION_API = TESTS_API + "/badrequestexception";
	private static final String TEST_INTERNALSERVERERROR_EXCEPTION_API = TESTS_API + "/internalservererrorexception";
	private static final String TEST_AM_SERVER_PROTOCOL_EXCEPTION1_API = TESTS_API + "/amserverprotocolexception1";
	private static final String TEST_AM_SERVER_PROTOCOL_EXCEPTION2_API = TESTS_API + "/amserverprotocolexception2";
	private static final String TEST_NO_SUCH_RESOURCE = TESTS_API + "/nosuchresource";
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_unhandled_exceptions() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
							logonCookie, 
							Helpers.WEBAPI_BASE_URL + TEST_UNHANDLED_EXCEPTION_API,
							HttpStatus.SC_INTERNAL_SERVER_ERROR,
							HttpStatus.SC_INTERNAL_SERVER_ERROR);
		
		System.out.println("can_process_unhandled_exceptions, Response is: " + response);
				
		assertNotNull(response);
		assertTrue("response.length()>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_webapi_exceptions() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_WEBAPI_EXCEPTION_API,
				HttpStatus.SC_CONFLICT,
				HttpStatus.SC_CONFLICT);
		
		System.out.println("can_process_webapi_exceptions, Response is: " + response);
				
		assertNotNull(response);
		assertTrue("response.length>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);		
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);
		
		// extra context specific data where this exception was thrown from.
		assertTrue("response has a code", response.indexOf("\"code\":") != -1);
		assertTrue("response has a fileName", response.indexOf("\"filename\":") != -1);
	}		
		
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_badrequest_exceptions() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_BADREQUEST_EXCEPTION_API,
				HttpStatus.SC_BAD_REQUEST,
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("can_process_badrequest_exceptions, Response is: " + response);
				
		assertNotNull(response);
		assertTrue("response.length>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);			
	}		

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_internalservererror_exceptions() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_INTERNALSERVERERROR_EXCEPTION_API,
				HttpStatus.SC_INTERNAL_SERVER_ERROR,
				HttpStatus.SC_INTERNAL_SERVER_ERROR);
		
		System.out.println("can_process_internalservererror_exceptions, Response is: " + response);
				
		assertNotNull(response);
		assertTrue("response.length>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);
	}		
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_am_server_protocol_exceptions() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_AM_SERVER_PROTOCOL_EXCEPTION1_API,
				HttpStatus.SC_BAD_REQUEST,
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("can_process_am_server_protocol_exceptions - can_process_am_server_protocol_exceptions, Response is: " + response);
			
		assertNotNull(response);
		assertTrue("response.length>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);
		
	}	
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_am_server_protocol_exceptions2() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_AM_SERVER_PROTOCOL_EXCEPTION2_API,
				HttpStatus.SC_BAD_REQUEST,
				HttpStatus.SC_BAD_REQUEST);
		
		System.out.println("can_process_am_server_protocol_exceptions - can_process_am_server_protocol_exceptions2, Response is: " + response);
		
		assertNotNull(response);
		assertTrue("response.length>0", response.length() > 0);
		assertTrue("response has a message", response.indexOf("\"message\":") != -1);
		assertTrue("response has an errorDescription", response.indexOf("\"errorDescription\":") != -1);
		assertTrue("response has an errorCode", response.indexOf("\"errorCode\":") != -1);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_process_am_404_exception() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		String response = Helpers.doGETCheckStatusReturnBody(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + TEST_NO_SUCH_RESOURCE,
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);
		
		System.out.println("can_process_am_server_protocol_exceptions - can_process_am_404_exception, Response is: " + response);
		
		// our 404s don't return any content
		assertTrue("response.length == 0", response.length() == 0);
	}	
	
}
