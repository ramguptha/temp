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
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


public class CreateInstallAppCommandTest extends LoggedInTest {

	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_install_in_house_app() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException{ 
	
		// TODO: Read this from a config file, or lookup the id from the database.
		int[] deviceIds = new int[1];
		deviceIds[0] = 6; // Toshiba AT100
		boolean isAndroid = true;
		
		iOSApplications inHouseAppDetails = new iOSApplications();
		inHouseAppDetails.setAppSize(67119);
		inHouseAppDetails.setBinaryPackageMD5("be9d6acd30b6117d9ae57f3845c41b6f");
		inHouseAppDetails.setBinaryPackageName("com.absolute.android.persistencetestinstaller");
		inHouseAppDetails.setBundleIdentifier("com.absolute.android.persistencetestinstaller");
		inHouseAppDetails.setDisplayName("ABTTestInstaller");
		inHouseAppDetails.setEncryptionKey("5B06E272846FE552A33F0F7BE8235E5CCB560A88484EDCA0682FD26A05350C1D899200491E617F60");
		inHouseAppDetails.setMinOSVersion(50364416);
		inHouseAppDetails.setName("ABTTestInstaller");
		inHouseAppDetails.setOriginalFileName("ABTTestInstaller_min11.apk");
		inHouseAppDetails.setPlatformType(11);
		inHouseAppDetails.setPreventAppDataBackup(false);
		inHouseAppDetails.setRemoveWhenMDMIsRemoved(false);
		inHouseAppDetails.setSeed(1);
		inHouseAppDetails.setUniqueID("9FF7797F-4D53-48F6-A015-F2DE4909EDEE");
		
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallInHouseApplicationCommand(loginReturnedAdminUUID, 
				deviceIds, isAndroid, inHouseAppDetails);
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "Install in-house app");
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
	public void can_install_third_party_app() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException{ 
	
		// TODO: Read this from a config file, or lookup the id from the database.
		int[] deviceIds = new int[1];
		deviceIds[0] = 6; // Toshiba AT100
		
		iOSAppStoreApplications thirdPartyAppDetails = new iOSAppStoreApplications();
		thirdPartyAppDetails.setAppStoreID("com.metago.astro");
		thirdPartyAppDetails.setAppStoreURL("https://play.google.com/store/apps/details?id=com.metago.astro");
		thirdPartyAppDetails.setCategory("Utilities");
		// WARNING: had to correct a badly pasted non-UTF-8 character in the string below and as such the below test may fail
		thirdPartyAppDetails.setLongDescription("ASTRO helps organize & view your pictures, music, video, document & other files. ASTRO File Manager has 30 million downloads on the Android Market and 250,000 reviews! It's like Windows Explorer or Mac's Finder for your phone or tablet and allows you to easily browse and organize all of your pictures, music, videos and documents. It also gives you the ability to stop processes that burn battery life and backup your apps in case you lose or change phones.");
		thirdPartyAppDetails.setMinOSVersion(35684352);
		thirdPartyAppDetails.setName("Astro File Manager / Browser");
		thirdPartyAppDetails.setPlatformType(11);
		thirdPartyAppDetails.setSeed(1);
		thirdPartyAppDetails.setShortDescription("Astro File Manager");
		thirdPartyAppDetails.setUniqueID("0DCC956E-22BA-4D8B-8C13-032E09173673");
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallThirdPartyApplicationCommand(loginReturnedAdminUUID, 
				deviceIds, thirdPartyAppDetails);
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "Install 3rd-party app");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}		

}
