package test.com.absolute.am.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraAdminCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CommandFactory;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

import static org.junit.Assert.*;

public class CobraCommandTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_lock_device_command() throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
        UUID fakeAdminGuid = UUID.fromString("00DBA3E8-D1F5-4A0D-8E4D-7B6C62EEBD37"); 
        CobraAdminMiscDatabaseCommand lockDeviceCommand = CreateLockDeviceCommand(fakeAdminGuid, new int[] { 2 });
        logXmlAndBinary(lockDeviceCommand, "LockDevice");
	}

	private void logXmlAndBinary(CobraAdminCommand command, String loggingPrefix) 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {

        PropertyList commandAsPropertyList = command.buildCommandDictionary();
        System.out.println(loggingPrefix + ": command is: " + commandAsPropertyList.toString());
        System.out.println(loggingPrefix + ": command in XML is: " + commandAsPropertyList.toXMLString());

        byte[] buf = CobraProtocol.commandToBytes(command);
        System.out.println(loggingPrefix + ": binary command length=" + buf.length + " data=" + StringUtilities.toHexString(buf));
        
        // confirm that we can read it back       
        PropertyList readBackCommand = CobraProtocol.readCommand(new ByteArrayInputStream(buf));
        assertNotNull("Couldn't read back the command", readBackCommand);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_login_command() throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		        
		CobraAdminMiscDatabaseCommand loginCommand = CreateLoginCommand("demo", "mdmdemo");
		logXmlAndBinary(loginCommand, "Login");
	}


	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_login_to_real_AMServer() throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		        
		CobraAdminMiscDatabaseCommand loginCommand = CreateLoginCommand("admin", "password");
	
		AMServerProtocolSettings amServerProtocolSettings = new AMServerProtocolSettings("qaams8", (short)3971, "");
		AMServerProtocol amServerProtocol = new AMServerProtocol(amServerProtocolSettings);

		try {
			PropertyList result = amServerProtocol.sendCommandAndGetResponse(loginCommand);
			System.out.println("Result:" + result.toXMLString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			amServerProtocol.close();
		}
		
	}


	// TODO: Where does the value for TestAdminAppUUID come from?
	private final UUID TestAdminAppUUID = UUID.fromString("25141286-95B0-4F98-BEEB-7DB3952615F2");
	
    private CobraAdminMiscDatabaseCommand CreateLockDeviceCommand(
            UUID adminGuid,
            int[] recordIDs) throws IOException, GeneralSecurityException {

            return CommandFactory.createLockDevicesCommand(TestAdminAppUUID, recordIDs, null);
    }
        
    private CobraAdminMiscDatabaseCommand CreateLoginCommand(String userName, String password) throws IOException, GeneralSecurityException {

        return CommandFactory.createLoginCommand(userName, password);    	    	
    }

}
