/**
 * 
 */
package com.absolute.am.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.absolute.util.StringUtilities;
import com.absolute.util.ZeroPaddedCipherOutputStream;

/**
 * @author dlavin
 * Implements password encryption that is compatible with CPLAT. The password is serialized
 * as a CPLAT string (4 byte length + characters of the string), this is then encrypted
 * using Blowfish, and the result is converted to a hex string.
 */
public class CPLATPassword {
	String password = "";
	
	/**
	 * Get the password.
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Default constructor. 
	 */
	public CPLATPassword(){}
	
	/**
	 * Convenience constructor to initialize the password field.
	 * @param password The password to use.
	 */
	public CPLATPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Encrypts and encodes the password.
	 * @param key the Blowfish key to use
	 * @return a hex string of the encoded password
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public String Encrypt(byte[] key) throws IOException, GeneralSecurityException {
		if (null == key ||
				key.length == 0) {
			throw new IllegalArgumentException();			
		}

		//  Initialize the key and cipher.
		SecretKeySpec KS = new SecretKeySpec(key, "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, KS);

		// create a new output streams
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZeroPaddedCipherOutputStream zpcos = new ZeroPaddedCipherOutputStream(baos, cipher);
		CPLATOutputStream cpos = new CPLATOutputStream(zpcos);
		
		// write the data
		cpos.writeString(password);
		cpos.flush();
		cpos.close();

		// return the result as a hex string
		byte[] cipherText = baos.toByteArray();
		return StringUtilities.toHexString(cipherText);		
	}

	/**
	 * Convenience method to return the encrypted passcode in a single call. 
	 * @param password The password to encrypt.
	 * @param key The Blowfish key to use for encryption.
	 * @return The encoded password.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static String Encrypt(String password, byte[] key) throws IOException, GeneralSecurityException {
		CPLATPassword cplatPassword = new CPLATPassword(password);
		return cplatPassword.Encrypt(key);
	}
	
	/**
	 * Convenience method to return the SHA1 hash of a password.
	 * @param password The password to generate the hash for.
	 * @return the password hash as a lower case hex string
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String Hash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] utf8Bytes = password.getBytes("UTF8");
		byte[] passwordHash = digest.digest(utf8Bytes);
		return StringUtilities.toHexString(passwordHash).toLowerCase();
	}
}
