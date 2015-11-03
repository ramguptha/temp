package test.com.absolute.am.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.IProgressReporter;
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.util.FileUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class MediaTest extends LoggedInTest implements IProgressReporter{

	private int[] fileIds = new int[] { 98 };
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		//test order is of the essence
		can_add_media();
		can_remove_media();
		time_read_media_file();
	}
	
	@SuppressWarnings("unchecked")
	public void can_add_media() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
		
		String fileUrlLocation = MediaTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		
		//String sourceFile = MediaTest.class.getResource("webAPIUnitTestCommand.pdf").getFile();
		//String iconFile = MediaTest.class.getResource("pdf.png").getFile();
		String sourceFile = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\java\\test\\com\\absolute\\am\\command\\webAPIUnitTestCommand.pdf";
		String iconFile = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\java\\test\\com\\absolute\\am\\command\\pdf.png";
		
		UUID newMediaUUID = UUID.randomUUID();
		
		File theFile = new File(sourceFile);
		String fileModifiedDate = StringUtilities.toISO8601W3CString(theFile.lastModified());
		long fileSize = theFile.length();
		
		MobileMedia mediaInfo = new MobileMedia();
		mediaInfo.setSeed(1);
		mediaInfo.setUniqueId(newMediaUUID.toString());
		mediaInfo.setFilename(sourceFile.substring(1));
		mediaInfo.setDescription("This is a test text file.");
		mediaInfo.setFileType("PDF");
		mediaInfo.setFileModDate(fileModifiedDate);
		mediaInfo.setFileSize(fileSize);
		mediaInfo.setDisplayName("webAPIUnitTestCommand.PDF");
		mediaInfo.setIcon(FileUtilities.loadFile(iconFile));
		mediaInfo.setFileMD5(StringUtilities.toHexString(FileUtilities.hashFile(sourceFile, "MD5")));
		mediaInfo.setEncryptionKey(StringUtilities.generateRandomString(StringUtilities.DEFAULT_RANDOM_PASSWORD_CHARSET, 16));
		mediaInfo.setCategory("Documents");
		mediaInfo.setPassPhraseHash("");
		mediaInfo.setCanEmail(true);
		mediaInfo.setCanPrint(false);
		mediaInfo.setCanLeaveApp(true);
		mediaInfo.setTransferOnWifiOnly(false);

		CobraAdminMiscDatabaseCommand command =  CommandFactory.createAddMediaCommand(
				mediaInfo, loginReturnedAdminUUID);

		System.out.println("command=" + command.toXml());
		System.out.println("loginAdminUUID is=" + loginReturnedAdminUUID);
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			amServerProtocol.sendCommandAndValidateResponse(command, "Add " + mediaInfo.getDisplayName());
		} catch (AMServerProtocolException e) {
			System.out.println("Exception: e.getMessage()=" + e.getMessage());
			System.out.println("Exception: e.getErrorCode()=" + e.getCode());
			throw e;
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
		
		amServerProtocol.sendFile(sourceFile, this);
		PropertyList finalResult = amServerProtocol.getResponse();
		ArrayList<Map<String, Object>> test = PropertyList.getElementAsArrayListMap(finalResult, "CommandResultParameters/DBChangeInfo/NewRecords");
		Object recordData[] = (Object[]) test.get(0).get("RecordData");
		fileIds = new int[] { (int)(long) ((Map<String, Object>) recordData[0]).get("id")};
		 
		System.out.println("Final result=" + finalResult.toXMLString());
	}
	
	public void can_remove_media() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException {
					
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveMediaCommandForFileIds(
				fileIds,
				loginReturnedAdminUUID);		
		
		System.out.println("command=" + command.toXml());
		System.out.println("loginAdminUUID is=" + loginReturnedAdminUUID);
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "remove content");
			System.out.println("Final result=" + finalResult.toXMLString());
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
	
	public void time_read_media_file() throws IOException {
	
		String fileUrlLocation = MediaTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		
		//String sourceFile = MediaTest.class.getResource("webAPIUnitTestCommand.pdf").getFile();
		String sourceFile = fileUrlLocation + "\\..\\..\\src\\test\\java\\test\\com\\absolute\\am\\command\\webAPIUnitTestCommand.pdf";

		Date startTime = new Date();
		System.out.println("starting to read file at: " + startTime);
		
		File theFile = new File(sourceFile);
		long fileLength64 = theFile.length();
		System.out.println("file length=" + fileLength64);
		
		// open the file, read in blocks and write them out.
		FileInputStream fis = new FileInputStream(theFile);
		byte[] temp = new byte[64*1024];
		int len = fis.read(temp);
		long offset = 0;
		while (len != -1) {
			System.out.println("readfile offset =" + offset);
			len = fis.read(temp);
			offset += len;
		}
		
		Date endTime = new Date();
		System.out.println("finished reading file at: " + endTime + " difference=" + (endTime.getTime() - startTime.getTime()) + "ms");

		fis.close();
		//fail("Not yet implemented");
	}

	@Override
	public void reportProgress(int progress) {
		// TODO Auto-generated method stub
		
	}

}
