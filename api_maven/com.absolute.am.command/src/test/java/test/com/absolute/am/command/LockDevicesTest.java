package test.com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CommandFactory;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class LockDevicesTest extends LoggedInTest {


	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_lock_devices_no_passcode() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
	
		// TODO: Read this from a config file, or lookup the id from the database.
		int[] deviceIds = new int[] {3}; // qaams2 - Kevin's Fujitsu
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createLockDevicesCommand(loginReturnedAdminUUID, deviceIds, null);
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "lock device");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
		
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_lock_devices_with_passcode() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
	
		// TODO: Read this from a config file, or lookup the id from the database.
		int[] deviceIds = new int[] {3}; // qaams2 - Kevin's Fujitsu
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createLockDevicesCommand(loginReturnedAdminUUID, deviceIds, "1234");
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "lock device");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}	

}
