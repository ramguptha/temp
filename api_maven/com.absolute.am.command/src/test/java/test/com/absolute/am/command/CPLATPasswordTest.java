/**
 * 
 */
package test.com.absolute.am.command;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.command.CPLATInputStream;
import com.absolute.am.command.CPLATPassword;
import com.absolute.am.command.CobraCommandDefs;
import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class CPLATPasswordTest {

	/**
	 * Test method for {@link com.absolute.am.command.CPLATPassword#Encrypt(byte[])}.
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_encrypt_differnt_lengths() throws IOException, GeneralSecurityException {
        class EncodeStringCase 
        {
        	public EncodeStringCase(String inputData, String expected) {
        		this.inputData = inputData;
        		this.expected = expected;
        	}
            public String inputData;
            public String expected;

        };

        // Dictionary where "Key" is the string to be encoded, "Value" is the expected result.
        ArrayList<EncodeStringCase> testData = new ArrayList<EncodeStringCase>();
        //Dictionary<String, String> testData = new Dictionary<string, string>();
        testData.add(new EncodeStringCase("", "97015F5AFC6E289C"));	// An empty string is serialized as a 4 byte length.
        testData.add(new EncodeStringCase("1", "43D7AF9013632F0B"));
        testData.add(new EncodeStringCase("12", "C439351C4E2D2FCA"));
        testData.add(new EncodeStringCase("123", "A622717A5CD0D0FF61AE99C204EF65D5"));
        testData.add(new EncodeStringCase("1234", "F4C080B41C3B644A86784520ABA0CEB5"));
                              //f4c080b41c3b644a86784520aba0ceb5
                             // f4c080b41c3b644ac93f8b03b6d5cc84

        // Without encryption - modify CPLATPassword to disable encryption - this can help to troubleshoot the root cause of a failure. 
        // DoStreamTesting zeroLength=[] oneLength=[000000020031] twoLength=[0000000400310032] threeLength=[00000006003100320033] fourLength=[000000080031003200330034]
        //testData.add(new EncodeStringCase("", "00000000"));
//        testData.add(new EncodeStringCase("1", "000000020031"));
//        testData.add(new EncodeStringCase("12", "0000000400310032"));
//        testData.add(new EncodeStringCase("123", "00000006003100320033"));
//        testData.add(new EncodeStringCase("1234", "000000080031003200330034"));


        String kBlowfishPasswordKey = "It ain't over until the fat lady sing.";
        byte[] keyBytes = kBlowfishPasswordKey.getBytes("UTF-8");

        for (EncodeStringCase sample: testData)
        {
        	CPLATPassword cplatPassword = new CPLATPassword(sample.inputData);
            String actual;
            actual = cplatPassword.Encrypt(keyBytes);
            System.out.println("Sample string=[" + sample.inputData + "] actual=[" + actual + "] expected=[" + sample.expected + "].");
            assertEquals("Sample:" + sample.expected + " differs from actual: " + actual,
            		sample.expected, actual);
        }
        //"ADD5B7911FB565D8C7048418D92C624776E7FBCEF72CA010"	"795BD5ADD865B51F188404C747622CD9CEFBE77610A02CF7"
        String decryptedPassword = decryptPassword("ADD5B7911FB565D8C7048418D92C624776E7FBCEF72CA010", CobraCommandDefs.kBlowfishAdminLogingPasswordKey.getBytes("UTF-8"));
        System.out.println("decryptedPassword is [" + decryptedPassword + "].");
	}

	private static String decryptPassword(String hexStringCipherText, byte[] key) throws GeneralSecurityException, IOException {
		
		System.out.println("Key=[" + StringUtilities.toHexString(key) + "]");
		byte[] byteArray = StringUtilities.fromHexString(hexStringCipherText);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		
		SecretKeySpec KS = new SecretKeySpec(key, "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, KS);

		CipherInputStream cis = new CipherInputStream(bais, cipher);
		
/*		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[4];
		int len = cis.read(buf);
		while (len != -1) {
			baos.write(buf, 0, len);
			len = cis.read(buf);
		}
		System.out.println("ClearText=[" + StringUtilities.toHexString(baos.toByteArray()));
*/		
		CPLATInputStream cplis = new CPLATInputStream(cis);
		String clearTextPassword = cplis.readString();
		cplis.close();
		return clearTextPassword;
	}
}
