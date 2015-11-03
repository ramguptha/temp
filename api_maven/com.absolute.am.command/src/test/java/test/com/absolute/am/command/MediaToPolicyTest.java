package test.com.absolute.am.command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import test.com.absolute.testutil.Helpers;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.iOSDevicesDefines;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class MediaToPolicyTest extends LoggedInTest {

	private UUID[] mediaUUIDs;
	private UUID policyUUID;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		//test order is of the essence
		setup();
		can_assign_media_to_policies();
		can_set_media_availability_time();
	}
	
	public void setup() throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException{
		mediaUUIDs = new UUID[] {
				UUID.fromString(Helpers.getMediaUuidsForMediaNames(logonCookie, "WebAPIUnitTestPDF2")[0]),
				UUID.fromString(Helpers.getMediaUuidsForMediaNames(logonCookie, "WebAPIUnitTestPNG2")[0])
		};
		
		policyUUID = UUID.fromString(Helpers.getPolicyUuidsForPolicyNames(logonCookie, "WebAPIUnitTest1")[0]);
	}
	
	public void can_assign_media_to_policies() 
		throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
			SAXException, AMServerProtocolException, AMWebAPILocalizableException {

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignMediaToPolicyCommand(
				mediaUUIDs, 
				policyUUID,
				iOSDevicesDefines.kCobra_iOS_Policy_MediaFile_PolicyOptional,
				loginReturnedAdminUUID);
		
		System.out.println("command=" + command.toXml());

		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "assign media to policy");
			System.out.println("Final result=" + finalResult.toXMLString());
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
	
	public void can_set_media_availability_time() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetAvailabilityTimeForPolicyMediaCommand(
				mediaUUIDs, 
				policyUUID, 
				iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_DailyInterval, 
				"13:01",
				"14:00",
				loginReturnedAdminUUID);
		
		System.out.println("command=" + command.toXml());

		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "set media availability time");
			System.out.println("Final result=" + finalResult.toXMLString());
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
}
