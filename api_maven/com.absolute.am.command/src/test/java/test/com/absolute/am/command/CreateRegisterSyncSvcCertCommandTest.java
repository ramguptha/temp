package test.com.absolute.am.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CobraRegisterSyncSvcCertCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class CreateRegisterSyncSvcCertCommandTest {
	// For now this needs to be tested out on dv2wlssmdm1
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_command() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
		try {
			// read in the sync services cert.der file
			String DERFilename = "src/test/resources/trusted_certs/QAAMS3_SyncSvcCert.der";
			File DERfile = new File(DERFilename);
			Assert.assertTrue(DERfile.exists());
			if (DERfile.exists()) {
				byte []buffer = new byte[4096];
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        InputStream is = new FileInputStream(DERFilename);
		        int read = 0;
		        while ( (read = is.read(buffer)) != -1 ) {
		            baos.write(buffer, 0, read);
		        }
		        is.close();
		        byte[] DERData = baos.toByteArray();
				CobraRegisterSyncSvcCertCommand command = CommandFactory.createRegisterSyncSvcCertCommand(
						"admin", "qa;pass", 
						DERData);
				System.out.println("command=" + command.toXml());
				
				AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings("qaams8", (short)3971, "src\\test\\resources\\trusted_certs\\");
				AMServerProtocol amServerProtocol = new AMServerProtocol(protocolSettings);
				PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "register sync svc cert");
				System.out.println("Final result=" + finalResult.toXMLString());
				long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
				Assert.assertEquals(0, resultError);

			}
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}
