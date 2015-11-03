package test.com.absolute.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.util.FileUtilities;
import com.absolute.util.StringUtilities;

public class FileUtilitiesTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void testLoadFile() throws IOException, NoSuchAlgorithmException {
		String testFile = this.getClass().getResource("/pdf.png").getFile();
		byte[] theLoadedFile = FileUtilities.loadFile(testFile);
		System.out.println("theLoadedFile.length=" + theLoadedFile.length);
		
		String digestName = "MD5";
		byte[] expectedHash = FileUtilities.hashFile(testFile, digestName);
		System.out.println("expectedHash=" + StringUtilities.toHexString(expectedHash));
		
		// hash the data that is in memory
		MessageDigest msgDigest = MessageDigest.getInstance(digestName);
		msgDigest.reset();
		DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(theLoadedFile), msgDigest);
		byte[] throwAway = new byte[4096];
		int readLen = 0;
		
		while (readLen < theLoadedFile.length) {
			readLen += dis.read(throwAway);
		}
		byte[] inMemoryHash = msgDigest.digest();
		System.out.println("inMemoryHash=" + StringUtilities.toHexString(inMemoryHash));
		
		assertArrayEquals(expectedHash, inMemoryHash);		
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_split_file_paths() {
		class TestCase {
			
			public String filePath;
			public String[] expectedResult;
			
			public TestCase(String filePath, String[] expectedResult) {
				this.filePath = filePath;
				this.expectedResult = expectedResult;				
			}
		};
		
		TestCase[] testCases = new TestCase[] {
				new TestCase("filewithextension.pdf", new String[] {"", "filewithextension", "pdf"}),
				new TestCase(".extensiononly", new String[] {"", "", "extensiononly"}),
				new TestCase("filenamewithoutextension", new String[] {"", "filenamewithoutextension", ""}),
				new TestCase("\\path\\filewithfolder.pdf", new String[] {"\\path", "filewithfolder", "pdf"}),
				new TestCase("/path/subpath/filewithfolder.pdf", new String[] {"/path/subpath", "filewithfolder", "pdf"}),
				new TestCase("x", new String[] {"", "x", ""}),
				new TestCase("", new String[] {"", "", ""}),
		}; 

		for (int i=0; i<testCases.length; i++) {
			String[] retVal = FileUtilities.splitFilePath(testCases[i].filePath);
			System.out.println("retVal[0]=" + retVal[0] + " retVal[1]=" + retVal[1] + " retVal[2]=" + retVal[2]);
			
			assertArrayEquals("compare splitFilePath() returned value to expected array", retVal, testCases[i].expectedResult);
			assertEquals("check path", FileUtilities.getPathFromFilePath(testCases[i].filePath), testCases[i].expectedResult[0]);
			assertEquals("check baseName", FileUtilities.getBaseNameFromFilePath(testCases[i].filePath), testCases[i].expectedResult[1]);
			assertEquals("check ext", FileUtilities.getExtensionFromFilePath(testCases[i].filePath), testCases[i].expectedResult[2]);
		}
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_load_resource_file_as_string() throws IOException {
		String retVal = FileUtilities.loadResourceFileAsString("/teststring.txt");
		String expected = "this is a test string";
		assertEquals(expected, retVal);
	}

}
