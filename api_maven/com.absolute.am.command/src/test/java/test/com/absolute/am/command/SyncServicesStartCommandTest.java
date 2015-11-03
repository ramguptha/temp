package test.com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.SyncServicesCommandFactory;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class SyncServicesStartCommandTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore //FIXME
	public void can_issue_start_sync_command() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
	
		// TODO: Read this from a config file, or lookup the id from the database.
		String serverAddress = "172.16.171.65";//"qaams8";
		short serverPort = 3971;
		String notifyEndpoint = "https://webadmin-qaammdm8.absolute.com/com.absolute.am.webapi/api/syncnotify";
		byte[] serverCertificate = StringUtilities.fromHexString("308203B23082031BA003020102021100F5F97B19A90A4D24A46744AA188C390B300D06092A864886F70D010105050030819E311F301D060355040313164162736F6C757465204D616E61676520536572766572312D302B060355042D132431424245423044452D464536312D343832302D423443332D343934313437363344433543311A3018060355040A13114162736F6C75746520536F6674776172653123302106092A864886F70D0109011614737570706F7274406162736F6C7574652E636F6D310B30090603550406130243413022180F31383939313231323132303030305A180F32313030303130313132303030305A30819E311F301D060355040313164162736F6C757465204D616E61676520536572766572312D302B060355042D132431424245423044452D464536312D343832302D423443332D343934313437363344433543311A3018060355040A13114162736F6C75746520536F6674776172653123302106092A864886F70D0109011614737570706F7274406162736F6C7574652E636F6D310B300906035504061302434130819F300D06092A864886F70D010101050003818D00308189028181009C2B8606B52EA442F274AE9DFD4442A8727825865DB3DF70A2A96B927C133A23F7E16166AB0796CE3110F8B89975B3299F372444B4DDA7EB9C5BE9B6EDB33633B8105E3F73D7290F6B3970919C08BEC7F3CFE8DC475D0C9E2156ED9469231A59CB0B8ABAC6BF7506BFC2D7C72E785FE7AAAF82E1C46A1AE8FE02863ACE2CC33B0203010001A381E93081E6300F0603551D130101FF040530030101FF300E0603551D0F0101FF040403020106301D0603551D0E041604144AD54D0BDAF0AB0FC74A4AC5BDA7B5C718AF845630560603551D110101FF044C304A8648636F6D2E6162736F6C7574652E6162736F6C7574655F6D616E6167655F7365727665722E31424245423044452D464536312D343832302D423443332D343934313437363344433543304C06096086480186F842010D043F163D4162736F6C757465204D616E61676520536572766572205B31424245423044452D464536312D343832302D423443332D3439343134373633444335435D300D06092A864886F70D01010505000381810007C37667E18A758314897978F671A7FCB32A37D5A5448E8FB9A605C657CD4A1C53F63781935C98EBAE466869A5C7636417ADB6321D1F33805A320325B17E7F736D98B2261C147D4EBCD8E542491B250C892CDC07CC8FE468867F17C6798463F4D076E70A7FBB86932D0462CB123EF0236BA4613FEB3F326ACCC1CDE139EC0957");
		System.out.println("serverCertificate in hex:" + StringUtilities.toHexString(serverCertificate));
		
		CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createStartSyncCommand(
				serverAddress,
				serverPort, 			
				serverCertificate,
				notifyEndpoint);
				
		System.out.println("command=" + command.toXml());
		
		AMServerProtocolSettings amServerProtocolSettings = new AMServerProtocolSettings("webadmin-qaammdm8", (short)9668, "");
		AMServerProtocol amServerProtocol = new AMServerProtocol(amServerProtocolSettings);
		
		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "start sync");
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
	public void can_create_stop_sync_command() throws Exception {
	
		String fakeSessionToken = "helloworld1234567890";
		CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createStopSyncCommand(
				fakeSessionToken);
				
		System.out.println("command=" + command.toXml());
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_priority_sync_command() throws Exception {
	
		String fakeSessionToken = "helloworld1234567890";
		
		PropertyList dbChangeInfo = PropertyList.fromString(SAMPLE_DBCHANGEINFO);
		
		CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createPrioritySyncCommand(
				fakeSessionToken,
				dbChangeInfo
				);
				
		System.out.println("command=" + command.toXml());
	}
	
	private static String SAMPLE_DBCHANGEINFO =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"+
		"<plist version=\"1.0\">"+
		"<dict>"+
			"<key>NewRecords</key>" +
			"<array>"+
				"<dict>"+
					"<key>RecordData</key>"+ 
					"<array>"+
						"<dict>"+
							"<key>CanEmail</key>"+ 
							"<integer>0</integer>"+ 
							"<key>CanLeaveApp</key>"+ 
							"<integer>0</integer>"+ 
							"<key>CanPrint</key>"+ 
							"<integer>0</integer>"+ 
							"<key>Category</key>"+ 
							"<string>Documents</string>"+ 
							"<key>Description</key>"+ 
							"<string>This is a new file added to Absolute Manage.</string>"+ 
							"<key>DisplayName</key>"+ 
							"<string>SampleFileForAM</string>"+ 
							"<key>EncryptionKey</key>"+
							"<string>B994845F81692DAFDF65031DE4B5E5857D44D96059D687FCA11BD4588BE583CDCB2B54471CF93FE0</string>"+ 
							"<key>FileMD5</key>"+ 
							"<string>085617fc2b5951860914e4b4e7ad3f16</string>"+ 
							"<key>FileModDate</key>"+ 
							"<string>2012-10-11T22:48:52Z</string>"+ 
							"<key>FileSize</key>"+ 
							"<integer>38</integer>"+ 
							"<key>FileType</key>"+ 
							"<string>TEXT</string>"+ 
							"<key>Filename</key>"+ 
							"<string>SampleFileForAM.txt</string>"+ 
							"<key>PassphraseHash</key>"+ 
							"<string>5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8</string>"+ 
							"<key>Seed</key>"+ 
							"<integer>1</integer>"+ 
							"<key>TransferOnWifiOnly</key>"+ 
							"<integer>0</integer>"+ 
							"<key>UniqueID</key>"+ 
							"<string>FC8BB7CB-F644-4304-B299-383D5476E019</string>"+ 
							"<key>id</key>"+ 
							"<integer>343</integer>"+ 
							"<key>last_modified</key>"+ 
							"<string>2012-10-11T22:50:43Z</string>"+ 
						"</dict>"+
					"</array>"+
					"<key>TableName</key>"+ 
					"<string>mobile_media</string>"+ 
				"</dict>"+
			"</array>"+
		"</dict>" +		
		"</plist>"		
			;

}
