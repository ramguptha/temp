package test.com.absolute.testdata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.BeforeClass;

import test.com.absolute.testutil.Helpers;

public class ValidationBase {
	public static String logonCookie = "";
	
	@BeforeClass
	public static void logonToWebAPI() throws Exception {
		String logonUrl = populateLoginUrl("qaams8", "admin", "qa;pass", (short) 3971);
		
		logonCookie = Helpers.logonToWebAPI(logonUrl);
	}
		
	private static String populateLoginUrl(String serverName, String userName, String password, short serverPort) 
			throws UnsupportedEncodingException {
		StringBuilder sb= new StringBuilder();
		
		sb.append("api/login?");
		sb.append("ServerName=" + serverName);
		sb.append("&ServerPort=" + serverPort);
		sb.append("&UserName=" + userName);
		sb.append("&Password=" + URLEncoder.encode(password, "UTF-8"));
		sb.append("&Locale=en_US");
		
		return sb.toString();
	}


}
