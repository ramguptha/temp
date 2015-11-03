package test.com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import test.com.absolute.testutil.Helpers;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.ResultHelper;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class LoggedInTest {
	// After login, both of these values will hold meaningful data.
	protected static PropertyList loginResult = null;
	protected static UUID loginReturnedAdminUUID = null;
	protected static String logonCookie = null;
	
	protected static String serverName = "qaams8";
	protected static short serverPort = 3971;
	
	//TODO: Read these settings from a config file so we can easily point the test cases at different environments.
//	protected static String serverHostName = "dvamctdev1";
//	protected static final short serverPort = 3971;
//	protected static String userName = "Dodo";
//	protected static String password = "password";
	
//	protected static String serverHostName = "mdm-eval9.absolute.com";
//	protected static final short serverPort = 3971;
//	protected static String userName = "demo";
//	protected static String password = "mdmdemo";
	
	protected static AMServerProtocolSettings protocolSettings = null;
	protected static String userName = "admin";
	protected static String password = "qa;pass";
	protected static String locale = "en_US";

//	protected static AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings("qaams2", (short)3971, "src\\test\\resources\\trusted_certs\\");
//	protected static String userName = "admin";
//	protected static String password = "qa;pass";

//	protected static AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings("qaams3", (short)3971, "src\\test\\resources\\trusted_certs\\");
//	protected static String userName = "admin";
//	protected static String password = "password";

//	protected static AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings("dv2wlssmdm1", (short)3971, "src\\test\\resources\\trusted_certs\\");
//	protected static String userName = "admin";
//	protected static String password = "absdemo";
	
//	protected static AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings("mdm-eval11.absolute.com", (short)3971, "src\\test\\resources\\trusted_certs\\");
//	protected static String userName = "admin";
//	protected static String password = "mdm2012";

	protected static AMServerProtocol amServerProtocol;
	
	protected static AMServerProtocolSettings getAMServerProtocolSettings() {
		return protocolSettings;
	}
	
	protected static synchronized AMServerProtocol getAMServerProtocol() {
		if (amServerProtocol == null) {
			amServerProtocol = new AMServerProtocol(getAMServerProtocolSettings());
		}
		return amServerProtocol;
	}

	
	@BeforeClass
	public static void logonToServers() throws Exception{
		String serverNameEnv = System.getenv("AM_TEST_SERVER_NAME");
		String serverPortEnv = System.getenv("AM_TEST_SERVER_PORT");
		
		if( serverNameEnv != null){
			serverName = serverNameEnv;
		}
		
		if( serverNameEnv != null){
			serverPort = Short.valueOf(serverPortEnv);
		}
		
		protocolSettings = new AMServerProtocolSettings(serverName, serverPort, "src\\test\\resources\\trusted_certs\\");
		
		logonToAMServer();
		logonToWebAPI();
	}
	
	public static void logonToAMServer () 
			throws RuntimeException, IOException, GeneralSecurityException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {

		CobraAdminMiscDatabaseCommand loginCommand = CommandFactory.createLoginCommand(userName, password);
		
		try {
			loginResult = getAMServerProtocol().sendCommandAndValidateResponse(loginCommand, "Login " + userName);
			loginReturnedAdminUUID = ResultHelper.getAdminUUIDFromLogonResult(loginResult);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
	

	@BeforeClass
	public static void logonToWebAPI () throws Exception {
		logonCookie = Helpers.logonToWebAPI(serverName, serverPort, userName, password, locale);		
	}
  
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
    public void can_logon() {
    	// Don't have to do anything. If the @BeforeClass method succeeded, then we are logged on.
    	assertNotNull(loginResult);
    	assertNotNull(loginReturnedAdminUUID);
    	System.out.println("Logged on. AdminUUID=" + loginReturnedAdminUUID);
    }
    
    @AfterClass
    public static void closeAMServerProtocol() throws IOException {
    	System.out.println("Closing the protocol.");    	
    	getAMServerProtocol().close();   	
    	System.out.println("Done.");
    }

}
