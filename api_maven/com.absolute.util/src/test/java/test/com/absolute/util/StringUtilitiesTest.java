package test.com.absolute.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.util.StringUtilities;

public class StringUtilitiesTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void toHexString_can_convert_to_hex() {
		byte[] byteArray = new byte[] {
				(byte) 0xA1, (byte) 0xB1, (byte) 0xC1, (byte) 0xD1, (byte) 0xE1, (byte) 0xF1, 
				0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F };
		String hexStr = StringUtilities.toHexString(byteArray);
		System.out.println("1. Converted hex string is:" + hexStr);
		String expectedStr = "A1B1C1D1E1F11A1B1C1D1E1F";
		assertEquals(hexStr, expectedStr);
		
		hexStr = StringUtilities.toHexString(byteArray,  0, byteArray.length);
		System.out.println("2. Converted hex string is:" + hexStr);
		assertEquals(hexStr, expectedStr);
		
		expectedStr = expectedStr.substring(2, expectedStr.length()-2);
		hexStr = StringUtilities.toHexString(byteArray, 1, byteArray.length - 2);
		System.out.println("3. Converted hex string is:" + hexStr + " expecting:" + expectedStr);
		assertEquals(hexStr, expectedStr);
				
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void toHexString_accepts_null_and_zero_length_input() {

		// Try null input.
		System.out.println("1. Try null.");
		String hexStr = StringUtilities.toHexString(null);
		System.out.println("Converted hex string is:" + hexStr);
		String expectedStr = "";
		assertEquals(hexStr, expectedStr);
		
		System.out.println("2. Try zero length.");
		hexStr = StringUtilities.toHexString(new byte[0]);
		System.out.println("Converted hex string is:" + hexStr);
		assertEquals(hexStr, expectedStr);
		
		System.out.println("3. Try specifying offset with count==0.");
		hexStr = StringUtilities.toHexString(new byte[]{0x0, 0x1}, 0, 0);
		System.out.println("Converted hex string is:" + hexStr);
		assertEquals(hexStr, expectedStr);
						
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void toHexString_checks_offset() {

		@SuppressWarnings("unused")
		String hexStr = StringUtilities.toHexString(new byte[]{0,1}, 2, 1);
		fail("Should not get to here.");					
	}

	@Test(expected=java.lang.IllegalArgumentException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void toHexString_checks_count() {
		
		@SuppressWarnings("unused")
		String hexStr = StringUtilities.toHexString(new byte[]{0,1}, 0, 3);
		fail("Should not get to here.");					
	}
	
	//lastModified =1351027739386 formatted=2012-10-23T21:28:59Z
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_parse_ISO8601W3CDateTimeString() {
		// These values were determined by using the methods and visually inspecting the result.
		String input = "2012-10-23T21:28:59Z";
		long expectedResult = 1351027739000L;
		Date theResult = StringUtilities.fromISO8601W3CString(input);
		assertEquals(expectedResult, theResult.getTime());		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_format_ISO8601W3CDateTimeString() {
		// These values were determined by using the methods and visually inspecting the result.		
		long input = 1351027739386L;
		String expectedResult = "2012-10-23T21:28:59Z";
		String theResult = StringUtilities.toISO8601W3CString(input);
		assertEquals(expectedResult, theResult);		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_generate_random_strings() {
		for (int i=0; i<400; i++) {
			String result = StringUtilities.generateRandomString(StringUtilities.DEFAULT_RANDOM_PASSWORD_CHARSET, i);
			System.out.println("Generated random string for i=" + i + " is [" + result + "]");
			assertTrue("returned length check", result.length() == i);
			
			// confirm that each character in the string is from the given character set
			String charset = new String(StringUtilities.DEFAULT_RANDOM_PASSWORD_CHARSET);
			for (int j=0; j<result.length(); j++ ) {
				char ch = result.charAt(j);
				assertTrue("character in original character set", charset.indexOf(ch) != -1);
			}
		}
	}
	
	private void throwExceptionAfterRecursiveCalls(int recursionCount) throws Exception {
		if (recursionCount <= 0) {
			throw new Exception("It can't be done.");
		} else {
			throwExceptionAfterRecursiveCalls(recursionCount - 1);
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_format_exception_strings() {
				
		// Calculate the current stack trace depth
		int initiaStackTraceCount = new Exception().getStackTrace().length;
		
		int recursionCount = 20;
		// First, generate an exception with a reasonable call stack.
		try {
			throwExceptionAfterRecursiveCalls(recursionCount);
		} catch (Exception e) {
			
			System.out.println("Exception caught. e=" + e.toString());
			System.out.println("Calling printStackTrace.");
			e.printStackTrace();
			String formattedString = StringUtilities.formatExceptionWithStackTrace(e);
			System.out.println("formatExceptionWithStackTrace returned:" + formattedString);
			
			assertTrue("check that the formatted version starts with the exception description",
					formattedString.startsWith(e.toString()));
			
			int newLineCount = 0;
			for (int i=0; i<formattedString.length(); i++) {
				if (formattedString.charAt(i) == '\n') {
					newLineCount++;
				}
			}
			assertTrue("stackTrace should have minimum number of entries",
					newLineCount > (recursionCount + initiaStackTraceCount));
		}
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void extract_charset_from_content_type() {

		// Try null input.
		System.out.println("1. Try empty.");
		String contentType = "Content-Type: text/plain";
		String result = StringUtilities.getCharsetFromContentType(contentType);
		System.out.println("Result:" + result);
		assertEquals(result, "");
		
		System.out.println("2. Try happy path.");
		contentType = "text/plain; charset=iso-8859-1";
		result = StringUtilities.getCharsetFromContentType(contentType);
		System.out.println("Result:" + result);
		assertEquals(result, "iso-8859-1");
		
		System.out.println("3. Try happy path, with whitespace.");
		contentType = "text/plain; charset = iso-8859-1";
		result = StringUtilities.getCharsetFromContentType(contentType);
		System.out.println("Result:" + result);
		assertEquals(result, "iso-8859-1");
		
		System.out.println("4. Try happy path, extended Content-Type.");
		contentType = "text/plain; charset=UTF-8; format=flowed";
		result = StringUtilities.getCharsetFromContentType(contentType);
		System.out.println("Result:" + result);
		assertEquals(result, "UTF-8");
						
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_array_of_ints_to_string() {
		int[] data = new int[] {0,1,2,3,-4,5};
		String expected = "0,1,2,3,-4,5";
		String actual = StringUtilities.arrayToString(data, ",");
		assertEquals(expected, actual);
		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_convert_empty_array_to_string() {
		int[] data = new int[0];
		String expected = "";
		
		String actual = StringUtilities.arrayToString(data, ",");
		assertEquals(expected, actual);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void isNumericString() {
		String input = "98734.38";
		boolean expectedResult = true;
		boolean actual = StringUtilities.isStringNumeric(input);
		assertEquals(expectedResult, actual);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void isNotNumericString() {
		String input = "test";
		boolean expectedResult = false;
		boolean actual = StringUtilities.isStringNumeric(input);
		assertEquals(expectedResult, actual);		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_int_version_number_to_string1() {
		int intVersion = 83984384;
		String expected = "5.0.1";
		String actual = StringUtilities.convertIntVersionToString(intVersion);
		assertEquals(expected, actual);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_int_version_number_to_string2() {
		int intVersion = 101875712;
		String expected = "6.1.2";
		String actual = StringUtilities.convertIntVersionToString(intVersion);
		assertEquals(expected, actual);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_compare_string_version_numbers_lesser() {
		String ver1 = "10.2.4.8";
		String ver2 = "11.0.1";
		int expected = -1;
		int actual = StringUtilities.compareStringVersionNumbers(ver1, ver2);
		assertEquals(expected, actual);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_compare_string_version_numbers_greater() {
		String ver1 = "11.1";
		String ver2 = "6.3.5";
		int expected = 1;
		int actual = StringUtilities.compareStringVersionNumbers(ver1, ver2);
		assertEquals(expected, actual);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_compare_string_version_numbers_equal1() {
		String ver1 = "1.1.0";
		String ver2 = "1.1";
		int expected = 0;
		int actual = StringUtilities.compareStringVersionNumbers(ver1, ver2);
		assertEquals(expected, actual);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_compare_string_version_numbers_equal2() {
		String ver1 = "1";
		String ver2 = "1.0.0";
		int expected = 0;
		int actual = StringUtilities.compareStringVersionNumbers(ver1, ver2);
		assertEquals(expected, actual);
	}
	
	
}
