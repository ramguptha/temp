/**
 * 
 */
package test.com.absolute.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.util.StringUtilities;
import com.absolute.util.ZeroPaddedCipherOutputStream;

/**
 * @author dlavin
 *
 */
public class ZeroPaddedCipherOutputStreamTest {

	/**
	 * Test method for {@link com.absolute.util.ZeroPaddedCipherOutputStream}.
	 * Confirms that we can encrypt different data lengths, especially lengths that
	 * are not an even multiple of the cipher block length.
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_encrypt_decrypt_different_block_lengths() throws GeneralSecurityException, IOException {
		
		for (int i=0; i<18; i++) {		
			// generate source data of length==i
			byte[] plainText = generateRandomData(i);
			assertEquals(i, plainText.length);
			// encrypt the data using the output stream
			byte[] cipherText = blowfishEncryptUsingStream(plainText, keyForAMSample);

			// check that the cipher length is a multiple of 8
			System.out.println("i=" + i + " cipherText length=" + cipherText.length + " calculateExpectedCipherLength=" + calculateExpectedCipherLength(i));
			assertTrue("cipherText length must be a multiple of 8.", (cipherText.length %8) == 0);
			assertTrue("cipherText has the correct length", cipherText.length == calculateExpectedCipherLength(i));
			
			// decrypt the data using the input stream
			byte[] newPlainText = blowfishDecryptUsingStream(cipherText, keyForAMSample);
			byte[] newPlainTextTruncated = Arrays.copyOf(newPlainText, plainText.length);
			
			// compare to source data				
			boolean same = Arrays.equals(plainText, newPlainTextTruncated);
			assertTrue("decrypted cipherText != plainText for length=" + i, same);
		}
	}

	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_encrypt_decrypt_sample_cipher_from_AM() throws GeneralSecurityException, IOException {

		// encrypt the data using the output stream
		byte[] cipherText = blowfishEncryptUsingStream(plainTextAM, keyForAMSample);

		// check that the cipher length is a multiple of 8
		System.out.println("cipherText length=" + cipherText.length + " calculateExpectedCipherLength=" + calculateExpectedCipherLength(plainTextAM.length));
		assertTrue("cipherText length must be a multiple of 8.", (cipherText.length %8) == 0);
		assertTrue("cipherText has the correct length", cipherText.length == calculateExpectedCipherLength(plainTextAM.length));
		
		// decrypt the data using the input stream
		byte[] newPlainText = blowfishDecryptUsingStream(cipherTextAM, keyForAMSample);
		byte[] newPlainTextTruncated = Arrays.copyOf(newPlainText, plainTextAM.length);
		
		// compare to source data				
		boolean same = Arrays.equals(plainTextAM, newPlainTextTruncated);
		assertTrue("newPlainTextTruncated should match plainTextAM", same);
	}

	
	private int calculateExpectedCipherLength(int plainTextLen) {
		
		return ((plainTextLen+7)/8) * 8;
	}

	/**
	 * Helper method to generate an array of random data of the requested length.
	 * @param length how many bytes to return
	 * @return a byte array of random data
	 */
	private static byte[] generateRandomData(int length) {
		
		byte[] retVal = new byte[length];
		Random rand = new Random();
		rand.nextBytes(retVal);
		return retVal;
	}


	/**
	 * Helper method to encrypt some plainText with the given key, using the ZeroPaddedCipherOutputStream.
	 * @param plainText The plaintext to encrypt.
	 * @param key The key to use.
	 * @return the cipherText
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static byte[] blowfishEncryptUsingStream(byte[] plainText, byte[] key) 
			throws GeneralSecurityException, IOException {
			
		SecretKeySpec KS = new SecretKeySpec(key, "Blowfish");
		Cipher cipher = null;

		cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, KS);

		// wrap the input data in a stream
		ByteArrayInputStream bais = new ByteArrayInputStream(plainText);
		
		// create a new output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZeroPaddedCipherOutputStream zpcos = new ZeroPaddedCipherOutputStream(baos, cipher);
		
		// encrypt the data in chunks where the length is not equal to the block size (8).
		byte[] buf = new byte[9];
		int i = bais.read(buf);
		while (i != -1) {
			zpcos.write(buf, 0, i);
			i = bais.read(buf);
		}
		zpcos.flush();
		zpcos.close();
		baos.flush();

		// return the result
		byte[] cipherText = baos.toByteArray();
		
		return cipherText;
	}
	
	/**
	 * Helper method to decrypt a blowfish cipher and return the plainText as a byte array.
	 * @param cipherText - the cipher text to decrypt
	 * @param key - the Blowfish key to use 
	 * @return the plainText
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static byte[] blowfishDecryptUsingStream(byte[] cipherText, byte[] key)
			throws GeneralSecurityException, IOException {
		
		SecretKeySpec KS = new SecretKeySpec(key, "Blowfish");
		Cipher cipher = null;

		cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, KS);

		// wrap the cipherText in a stream
		ByteArrayInputStream bais = new ByteArrayInputStream(cipherText);		
		CipherInputStream cis = new CipherInputStream(bais, cipher);
		
		// create an output stream to store the result
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[9]; // deliberately choose something that is not a multiple of 8
		int i = cis.read(buf);
		while (i != -1) {
			baos.write(buf, 0, i);
			i = cis.read(buf);
		}
		baos.flush();
		cis.close();

		byte[] plainText = baos.toByteArray();
		return plainText;			
	}	

	// AM generated sample cipher
	private static byte[] keyForAMSample; {
		try {
			keyForAMSample = "Es war einmal und ist nicht mehr...".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Exception initializing keyForAMSample, e=" + e.toString());
			e.printStackTrace();
			keyForAMSample = null;
		}	
	}
	private static byte[] cipherTextAM = StringUtilities.fromHexString("D8D0A31C117BE008CC1C667D9627EFBC5BCBF03E72AC1D2B7035EC641831503BCC1C667D9627EFBC5BCBF03E72AC1D2B7035EC641831503BCC1C667D9627EFBC5BCBF03E72AC1D2B7035EC641831503BCC1C667D9627EFBC5BCBF03E72AC1D2B7035EC641831503BCC1C667D9627EFBC5BCBF03E72AC1D2B7035EC641831503BCC1C667D9627EFBC5BCBF03E72AC1D2B4D517CE44CD03A2E");
	private static byte[] plainTextAM = StringUtilities.fromHexString("9000008048656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E48656C6C6F20776F726C642E00000000");	
	
}
