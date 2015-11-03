/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.com.absolute.testutil.Helpers;

import com.absolute.util.PropertyList;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.ws.WebSocket;
import com.ning.http.client.ws.WebSocketTextListener;
import com.ning.http.client.ws.WebSocketUpgradeHandler;

public class PushTest extends LoggedInTest {

	private static final String SYNC_COMPLETED_API = "api/syncnotify/synccompleted";
	private final String testEndpointName = "allmobiledevices";
	private final int defaultSleepTime = 2000;
	private final String websocketProtocol = "wss://";
	private WebSocket websocket;
	private AsyncHttpClient client;
	private static Logger m_logger = LoggerFactory.getLogger(PushTest.class.getName());
	private volatile boolean assertionFailed;
	
	// slow test as it should be ran manually
	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void test_master_list() throws Exception {

		can_connect_to_websocket();
		can_subscribe_successfully();
		can_unsubscribe_successfully();
		can_get_messages_on_data_updates();
		
		client.close();
	}
	
	@BeforeClass
	public static void logonToWebAPI() throws Exception {			
		logonCookie = Helpers.logonToWebAPI(serverName, serverPort, userName, password, locale);
	}
	
	public void can_get_messages_on_data_updates() throws InterruptedException, KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {
		PropertyList syncCompletedEvent = new PropertyList(), event = new PropertyList();
		
		websocket.sendMessage("{\"action\": \"subscribe\", \"endpoint\": \"" + testEndpointName + "\"}");
		
		WebSocketTextListener listener = new WebSocketTextListener() {
	        @Override
	        public void onError(Throwable t) {}
	
			@Override
			public void onMessage(String message) {
				m_logger.debug("can_get_messages_on_data_updates() got message: " + message);
				assertionFailed = ! message.equals("{\"endpoint\":\"allmobiledevices\",\"updated\":true}");
			}
	
			@Override
			public void onOpen(WebSocket websocket) {}
	
			@Override
			public void onClose(WebSocket websocket) {}
	     };
	     
		
		syncCompletedEvent.put("UpdatedTables", new String[] {"iphone_info"});
		event.put("SyncCompletedEvent", syncCompletedEvent);
		
		websocket.addWebSocketListener(listener);	
		
		Helpers.postRequestGetResultCheckStatus(
				logonCookie, 
				Helpers.WEBAPI_BASE_URL + SYNC_COMPLETED_API, 
				"application/x-www-form-urlencoded", 
				event.toXMLString(), 
				200, 
				299);
			
	   
	   	Thread.sleep(defaultSleepTime);
	   	Assert.assertFalse(assertionFailed);
	}
	
	public void can_unsubscribe_successfully() throws InterruptedException {
		websocket.sendMessage("{\"action\": \"unsubscribe\", \"endpoint\": \"" + testEndpointName + "\"}");
		
		WebSocketTextListener listener = new WebSocketTextListener() {
	         @Override
	         public void onError(Throwable t) {}
	
			@Override
			public void onMessage(String message) {
				m_logger.debug("can_unsubscribe() got message: " + message);
				assertionFailed = message.contains(testEndpointName);
			}

			@Override
			public void onOpen(WebSocket websocket) {}

			@Override
			public void onClose(WebSocket websocket) {}
	     };
		
	    websocket.addWebSocketListener(listener);
	    websocket.sendMessage("{\"action\": \"list\"}");
	   	Thread.sleep(defaultSleepTime);
	   	websocket.removeWebSocketListener(listener);
	   	Assert.assertFalse(assertionFailed);
	}
	
	public void can_subscribe_successfully() throws InterruptedException {
		websocket.sendMessage("{\"action\": \"subscribe\", \"endpoint\": \"" + testEndpointName + "\"}");
		
		WebSocketTextListener listener = new WebSocketTextListener() {
	         @Override
	         public void onError(Throwable t) {}
	
			@Override
			public void onMessage(String message) {	
				m_logger.debug("can_subscribe() got message: " + message);
				
				assertionFailed = !message.contains(testEndpointName);
			}

			@Override
			public void onOpen(WebSocket websocket) {}

			@Override
			public void onClose(WebSocket websocket) {}
	     };
	     
		websocket.addWebSocketListener(listener);
		websocket.sendMessage("{\"action\": \"list\"}");
	   	Thread.sleep(defaultSleepTime);
	   	websocket.removeWebSocketListener(listener);
	   	Assert.assertFalse(assertionFailed);
	}
	
	public void can_connect_to_websocket() throws Exception {
		// Need to create a new asyncHttpClient that doesn't explode when working with self signed certificates
		SSLContext sslContext = SSLContext.getInstance("SSL");
		Builder builder = new AsyncHttpClientConfig.Builder();
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
	    builder.setSSLContext(sslContext);
	
		client = new AsyncHttpClient(builder.build());
	    //	private final String hostPath = "amweb.absolute.com/com.absolute.am.webapi/";
	    BoundRequestBuilder request = client.prepareGet(Helpers.WEBAPI_BASE_URL.replaceAll("https://", websocketProtocol) + LoggedInTest.PUSH_API);
	    request.addHeader("Cookie", logonCookie);

		websocket = request.execute(new WebSocketUpgradeHandler.Builder().build()).get();
		
		Assert.assertTrue(websocket.isOpen());
	}
}
