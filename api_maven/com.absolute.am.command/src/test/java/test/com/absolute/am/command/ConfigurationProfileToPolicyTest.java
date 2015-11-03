package test.com.absolute.am.command;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
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

public class ConfigurationProfileToPolicyTest extends LoggedInTest {
	
	private UUID[] configurationProfileUUIDs;
	private UUID policyUUID;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		//test order is of the essence
		setup();
		can_assign_configuration_profile_to_policies();
		can_set_configuration_profile_availability_time();
		can_remove_configuration_profile_from_policies();
	}
	
	public void setup() throws ClientProtocolException, UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException{
		configurationProfileUUIDs = new UUID[] {
				UUID.fromString(Helpers.getConfigurationProfileUuidsForConfigurationProfileNames(logonCookie, "WebAPI Config Profile iOS 1")[0]),
				UUID.fromString(Helpers.getConfigurationProfileUuidsForConfigurationProfileNames(logonCookie, "WebAPI Config Profile iOS 2")[0])
			};
		policyUUID = UUID.fromString(Helpers.getPolicyUuidsForPolicyNames(logonCookie, "WebAPIUnitTest1")[0]);
	}
		
	@SuppressWarnings("unchecked")
	public void can_assign_configuration_profile_to_policies() 
		throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
			SAXException, AMServerProtocolException, AMWebAPILocalizableException {
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignConfigurationProfileToPolicyCommand(
				configurationProfileUUIDs, 
				policyUUID,
				iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfile_Required,
				loginReturnedAdminUUID);
				
		System.out.println("command=" + command.toXml());

		AMServerProtocol amServerProtocol = getAMServerProtocol();
		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "assign configuration profile to policy failed");
			System.out.println("Final result=" + finalResult.toXMLString());
			
			Object recordData[] = (Object[]) PropertyList.getElementAsArrayListMap(finalResult, "CommandResultParameters/DBChangeInfo/NewRecords").get(0).get("RecordData");
			
			String UUID1 = (String) ((Map<String, Object>) recordData[1]).get("ProfileUniqueID"),
					UUID2 = (String) ((Map<String, Object>) recordData[0]).get("ProfileUniqueID");
			
			String assgnmentType1 = String.valueOf(((Map<String, Object>) recordData[1]).get("State")),
					assgnmentType2 = String.valueOf(((Map<String, Object>) recordData[0]).get("State"));
			
			// check that the correct UUIDs were linked
			assertTrue(UUID1.equalsIgnoreCase(configurationProfileUUIDs[1].toString()));
			assertTrue(UUID2.equalsIgnoreCase(configurationProfileUUIDs[0].toString()));
			
			// check that the correct assignment types were linked
			assertTrue(assgnmentType1.equals(String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfile_Required)));
			assertTrue(assgnmentType2.equals(String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfile_Required)));
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
	
	public void can_set_configuration_profile_availability_time() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetAvailabilityTimeForPolicyConfigProfileCommand(
				configurationProfileUUIDs, 
				policyUUID, 
				iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_DailyInterval, 
				"13:01",
				"14:00",
				loginReturnedAdminUUID);
		
		System.out.println("command=" + command.toXml());

		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "set configuration profile availability time failed");
			System.out.println("Final result=" + finalResult.toXMLString());
			// no further testing is possible as the command response doesn't contain any relevant data
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
	
	public void can_remove_configuration_profile_from_policies() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
			
			CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveConfigurationProfileFromPolicyCommand(
					configurationProfileUUIDs, 
					policyUUID,
					loginReturnedAdminUUID);
					
			System.out.println("command=" + command.toXml());

			AMServerProtocol amServerProtocol = getAMServerProtocol();

			try {
				PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "assign configuration profile to policy failed");
				System.out.println("Final result=" + finalResult.toXMLString());
				// no further testing is possible as the command response doesn't contain any relevant data besides the deleted record IDs
			} catch (AMWebAPILocalizableException e) {
				System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
				throw e;
			}
		}
}