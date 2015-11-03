package test.com.absolute.am.command;

import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.Test;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CommandFactory;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


public class CreateSetRoamingOptionsCommandTest extends LoggedInTest {

	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void can_set_roaming_options() throws Exception {
	
		// TODO: Read this from a config file, or lookup the id from the database.
		Boolean voiceRoaming = false;
		Boolean dataRoaming = false;
		sendandValidateRoamingCommand(voiceRoaming, dataRoaming);
		
		System.out.println("TODO: remove when sync service is ready. Thread.sleep() to allow Admin Console to sync up.");
		Thread.sleep(5000);

		voiceRoaming = null;
		dataRoaming = false;
		sendandValidateRoamingCommand(voiceRoaming, dataRoaming);

		System.out.println("TODO: remove when sync service is ready. Thread.sleep() to allow Admin Console to sync up.");
		Thread.sleep(5000);

		voiceRoaming = false;
		dataRoaming = null;
		sendandValidateRoamingCommand(voiceRoaming, dataRoaming);

		System.out.println("TODO: remove when sync service is ready. Thread.sleep() to allow Admin Console to sync up.");
		Thread.sleep(5000);
		
		voiceRoaming = true;
		dataRoaming = true;
		sendandValidateRoamingCommand(voiceRoaming, dataRoaming);

	}	
	
	private void sendandValidateRoamingCommand(Boolean voiceRoaming, Boolean dataRoaming) throws Exception {
		int[] iosDeviceIds = new int[]{29}; //AM YVR iPhone 5
		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetRoamingOptionsCommand(
				loginReturnedAdminUUID, iosDeviceIds, voiceRoaming, dataRoaming);
		System.out.println("command=" + command.toXml());

		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "set roaming options");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
}
