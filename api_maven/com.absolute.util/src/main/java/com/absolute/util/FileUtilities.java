/**
 * 
 */
package com.absolute.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dlavin
 *
 */
public class FileUtilities {
    private static Logger m_logger = LoggerFactory.getLogger(FileUtilities.class.getName()); 

	public static final int MAX_FILE_SIZE_TO_READ_INTO_MEMORY = (32 * 1024);
	
	/**
	 * Loads a file into memory and returns it as a byte array. If the file exceeds
	 * MAX_FILE_SIZE_TO_READ_INTO_MEMORY, then a RuntimeException will be thrown.
	 *   
	 * @param filePath The name of the file to load.
	 * @return byte array with the content of the file 
	 * @throws IOException
	 */
	public static byte[] loadFile(String filePath) throws IOException {
		
		File theFile = new File(filePath);		
		if (theFile.length() > MAX_FILE_SIZE_TO_READ_INTO_MEMORY) {
			throw new RuntimeException("FileSize too big to load into memory. Size=" + theFile.length());
		}

		int fileLength = (int) theFile.length();
		byte[] retVal = new byte[fileLength];

		FileInputStream fis = new FileInputStream(theFile);
		try {
			
			int lenRead = fis.read(retVal);
			int offset = lenRead;		
			while ((lenRead != -1) && offset < fileLength) {
				lenRead = fis.read(retVal, offset, fileLength - offset);
				offset += lenRead;			
			}
			
		} finally {
			fis.close();
		}
				
		return retVal;
	}

	/**
	 * Helper method to calculate the hash of a given file.
	 * @param filePath
	 * @param digestName
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] hashFile(String filePath, String digestName) throws NoSuchAlgorithmException, IOException {
		File theFile = new File(filePath);
		long fileLength = theFile.length();
		m_logger.debug("hashFile fileLength=" + fileLength);
		FileInputStream fis = new FileInputStream(theFile);
		return hashFile(fis, fileLength, digestName);

	}
	/**
	 * Helper method to calculate the hash of a given file.
	 * @param is The inputStream to calculate the hash on.
	 * @param digestName The type of hash to generate, e.g. "MD5"
	 * @return a byte array containing the generated hash. The length depends on the type of hash.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] hashFile(InputStream is, long inputLength, String digestName) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance(digestName);
		digest.reset();
		
		DigestInputStream dis = new DigestInputStream(is, digest);

		try {
			byte[] throwAway = new byte [4 * 1024];
			long offset = 0;
			while (offset < inputLength) {
				offset += dis.read(throwAway);					
			}
		} finally {
			dis.close();
		}
		byte[] finalDigest = digest.digest();
		return finalDigest;
	}
	
	/**
	 * Helper method to split a path into path, baseName, extension.
	 * @param filePath
	 * @return String[3], [0]=path, [1]=basename, [2]=extension. Any of these could be an empty string "".
	 */
	public static String[] splitFilePath(String filePath) {
		
		int indexOfDot = filePath.lastIndexOf('.');
		if (indexOfDot == -1) {
			indexOfDot = filePath.length();
		}
		
		int indexOfPathSeparator = filePath.lastIndexOf('\\');
		if (indexOfPathSeparator == -1) {
			indexOfPathSeparator = filePath.lastIndexOf('/');
		}
		if (indexOfPathSeparator == -1) {
			indexOfPathSeparator = 0;
		}
		
		if (indexOfDot < indexOfPathSeparator) {
			indexOfDot = filePath.length();
		}
		
		String path = filePath.substring(0, indexOfPathSeparator);
		String baseName = filePath.substring(indexOfPathSeparator > 0 ? indexOfPathSeparator + 1 : 0, indexOfDot);
		String ext = filePath.substring(indexOfDot < filePath.length() ? indexOfDot+1 : indexOfDot, filePath.length());
		return new String[] {path, baseName, ext};
	}
	
	/**
	 * Get the path portion of a file path.
	 * @param filePath The file path to parse.
	 * @return the path portion (may be an empty string, i.e. "").
	 */
	public static String getPathFromFilePath(String filePath) {
		return splitFilePath(filePath)[0];
	}
	
	/**
	 * Get the baseName portion of a file path.
	 * @param filePath The file path to parse.
	 * @return the baseName portion (may be an empty string, i.e. "").
	 */
	public static String getBaseNameFromFilePath(String filePath) {
		return splitFilePath(filePath)[1];
	}
	
	/**
	 * Get the file extension part of a file path.
	 * @param filePath The file path to parse.
	 * @return the file extension portion (may be an empty string, i.e. "").
	 */
	public static String getExtensionFromFilePath(String filePath) {
		return splitFilePath(filePath)[2];
	}
	
	/**
	 * Loads a resource file into memory and returns it as a string.
	 * Unlike loadFile method above, loadResourceFileAsString uses relative path to the file.
	 * NB: it removes Byte Order Mark from UTF-8-encoded resources.
	 *   
	 * @param path The relative path to the file.
	 * @return string with the content of the file 
	 * @throws IOException
	 */
	public static String loadResourceFileAsString(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream is = discardUtf8BOM(FileUtilities.class.getResourceAsStream(path));
		int content;
		while ((content = is.read()) != -1) {
			sb.append((char) content);
		}
		String result = sb.toString();
		if (is != null) {
			is.close();
		}
		return result;
	}
	
	public static String loadTextFileAsString(String path) throws IOException 
	{
		return loadTextFileAsString(path, StandardCharsets.UTF_8);
	}
	
	public static String loadTextFileAsString(String path, Charset encoding) throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	/**
	 * Helper method to remove BOM from the input stream when reading a file in UTF-8 encoding.
	 * @param is The inputStream to remove BOM from.
	 * @return InputStream - same stream, but without preceding BOM if it was present.
	 * @throws IOException
	 */
	private static InputStream discardUtf8BOM(InputStream is) throws IOException {
	    PushbackInputStream pbis = new PushbackInputStream(new BufferedInputStream(is), 3);
	    byte[] bom = new byte[3];
	    if (pbis.read(bom) != -1) {
	        if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
	            pbis.unread(bom);
	        }
	    }
	    return pbis; 
	}
}
