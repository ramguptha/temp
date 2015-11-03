/**
 * 
 */
package com.absolute.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dlavin
 *
 */
public final class StringUtilities {
	
	private static final String ISO8601W3CDateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static char[] DEFAULT_RANDOM_PASSWORD_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-+=#$&/*(){}[]!.;:".toCharArray();
	
	/**
	 * Turn an Object into its String representation.
	 * @param object to turn to a String
	 * @return object in String form
	 */
	public static String objectToString(Object object) {
		String objAsString = "";
		if (object instanceof Long) {
			objAsString = ((Long)object).toString();
		} else if (object instanceof Double) {
			objAsString = ((Double)object).toString();
		} else if (object instanceof Boolean) {
			objAsString = (Boolean)object ? "1" : "0";
		} else {
			objAsString = ((String)object).toString();
		}
		
		return objAsString;
	}
	
	/**
	 * Convert a collection of objects into a string of values.
	 * @param arrayOfObjects the collection of objects to convert
	 * @param separator the separator to insert between the objects in the string
	 * @return the string representation of the object array
	 */
	public static <T extends Iterable<?>> String CollectionToString(T collection, String separator) {
        StringBuilder sb = new StringBuilder();
        if (collection != null) {
            boolean firstItem = true; // used to control appending the separator
            for (Object item: collection) {
                if (firstItem) {
                    // item may be null, so don't call ToString(). StringBuilder handles nulls.
                	sb.append(firstItem);
                    firstItem = false;
                } else {
                    // item may be null, so don't call ToString(). StringBuilder handles nulls.
                	sb.append(separator);
                	sb.append(item);
                }
            }
        }
        return sb.toString();
	}

	/**
	 * Convert an array of objects into a string of values.
	 * @param arrayOfObjects the array of objects to convert
	 * @param separator the separator to insert between the objects in the string
	 * @return the string representation of the object array
	 */
	public static String arrayToString(Object theArray, String separator) {
		
		if (theArray != null && !theArray.getClass().isArray()) {
			throw new IllegalArgumentException("this method only supports arrays");
		}
		
        StringBuilder sb = new StringBuilder();

        if (theArray != null) {
	        for (int i=0; i<Array.getLength(theArray); i++) {
	            if (i == 0) {
	            	sb.append(Array.get(theArray, i));
	            }
	            else {
	            	sb.append(separator);
	            	sb.append(Array.get(theArray, i));
	            }            	
	        }
        }
        
        return sb.toString();
	}
	
	/**
	 * Convert an array of objects into an array of string.
	 * @param objArray the array of objects to convert
	 * @return the string array representation of the object array
	 */
	public static String[] objectArrayToStringArray(Object[] objArray) {
		
		if (null == objArray) {
			return null;
		} else {
			if (!objArray.getClass().isArray()) {
				throw new IllegalArgumentException("objectArrayToStringArray method only supports arrays");
			}
			
			String[] strArray = new String[objArray.length];
			for (int i = 0; i < objArray.length; i++)
			{
				if(null == objArray[i]) {
					strArray[i] = "";
				} else {
					strArray[i] = new String(objArray[i].toString());
				}
			}
			return strArray;
		}
	}

	public static String toHexString(byte[] buf) {
		if (null == buf) {
			return "";
		} else {
			return toHexString(buf,  0, buf.length);
		}
	}
	
	public static String toHexString(byte[] buf, int offset, int count) {
		// TODO: test if this could be replaced by: javax.xml.bind.DatatypeConverter.printHexBinary(buf)
		// Sanity check the arguments.
		if (buf != null && (count + offset) > buf.length) {
			throw new IllegalArgumentException("(count + offset) > buf.length");
		}
		
		if ((null == buf) ||
				(buf != null && buf.length == 0)) {
			return "";
		}
		
		final char[] hexArray = { 
				'0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[count * 2];
		int v;
		for (int j = offset; j < (offset + count); j++) {
			v = buf[j] & 0xFF;
			hexChars[(j - offset) * 2] = hexArray[v >>> 4];
			hexChars[(j - offset) * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String toPrintableString(byte[] buf) {
		if (null == buf) {
			return "";
		} else {
			return toPrintableString(buf,  0, buf.length);
		}
	}

	public static String toPrintableString(byte[] buf, int offset, int count) {
		// Sanity check the arguments.
		if (buf != null && (count + offset) > buf.length) {
			throw new IllegalArgumentException("(count + offset) > buf.length");
		}
		
		if ((null == buf) ||
				(buf != null && buf.length == 0)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		byte[] chs = new byte[1];
		for (int j = offset; j < (offset + count); j++) {
			chs[0] = buf[j];			
			try {
				String asStr = new String(chs, "UTF-8");				
				char ch = asStr.charAt(0);
				if (Character.isDigit(ch) || Character.isLetter(ch) || //Character.isWhitespace(ch) ||
						"[]{}<>(),.\\/?!@#$%^&*`~;:'\"".indexOf(ch) != -1) {
					sb.append(" " + ch);
				} else {
					sb.append("..");
				}
			} catch (UnsupportedEncodingException e) {
				sb.append("..");
			}
		}
		
		return sb.toString();
	}
	

	public static byte[] fromHexString(String hexString) {
		return javax.xml.bind.DatatypeConverter.parseHexBinary(hexString);

	}
	
	public static String toBase64String(byte[] buf) {
		return javax.xml.bind.DatatypeConverter.printBase64Binary(buf);
	}
	
	public static byte[] fromBase64String(String base64String) {
		return javax.xml.bind.DatatypeConverter.parseBase64Binary(base64String);
	}

	
	public static String toISO8601W3CString(long theDateAsALong) {
		return toISO8601W3CString(new Date(theDateAsALong));		
	}

	public static String toISO8601W3CString(Date theDate) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601W3CDateFormatPattern);
		sdf.setCalendar(cal);
		return sdf.format(theDate);		
	}
	
	/**
	 * Parses the given string representation of a date and returns a Date object.
	 * @param dateStr the string to parse
	 * @return a Date object, or null if it cannot be parsed.
	 */
	public static Date fromISO8601W3CString(String dateStr) {
		// SimpleDateFormat class doesn't respect 'Z' as the symbol for GMT
		// timezone.
		dateStr = dateStr.replaceFirst("Z$", "GMT");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		try {
			Date temp = df.parse(dateStr);
			return new Date(temp.getTime());
		} catch (ParseException e) {
			return null;
		}
	}
	

	/**
	 * Converts the UUID to a string, using all upper case characters.
	 * @param aUUID
	 * @return
	 */
	public static String UUIDToStringUpper(UUID aUUID) {
		return aUUID.toString().toUpperCase();
	}


	/**
	 * Generates a random string, of the required length, consisting of characters taken from
	 * the provided character set.
	 * @param inputCharSet the character set to pick characters from
	 * @param lengthRequired the length of the desired random string
	 * @return a random string
	 */
    public static String generateRandomString(char[] inputCharSet, int lengthRequired) {
        if (null == inputCharSet ||
            inputCharSet.length == 0 ||
            lengthRequired == 0) {
            return "";
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomKeys = new byte[lengthRequired];
        secureRandom.nextBytes(randomKeys);

        char[] randomChars = new char[lengthRequired];
        for (int i = 0; i < randomChars.length; i++)
        {
            randomChars[i] = inputCharSet[((short)(randomKeys[i] & 0xFF)) % (inputCharSet.length)];
        }
        return new String(randomChars);
    }
    
    /**
     * Formats an exception to include the exception message (Exception.toString()) followed
     * by the stack trace.
     * @param e the exception to format
     * @return the formatted string
     */
    public static String formatExceptionWithStackTrace(Exception e) {
    	// Because this method is typically used on a catch, any exceptions that occur
    	// here are deliberately caught and silently ignored. In this situation, toString()
    	// of the original Exception is returned.
    	try {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(e.toString()).append("\n");
	    	StackTraceElement[] stackTrace = e.getStackTrace();
	    	for (StackTraceElement s : stackTrace) {
	    		sb.append("\t").append(s.toString()).append("\n");    		
	    	}
	    	return sb.toString(); 
    	} catch (Exception ex) {
    		return e.toString();
    	}
    }
    
    /**
     * Retrieves value of charset attribute in HTTP Content-Type
     * Example of Content-Type: text/plain; charset=iso-8859-1
     * @param contentType the value of Content-Type HTTP Header
     * @return the charset value
     */
	public static String getCharsetFromContentType(String contentType) {
		String result = "";
		Pattern pattern = Pattern.compile("(?<=charset\\s?=)[^;]*");
		Matcher regexMatcher = pattern.matcher(contentType);
		if (regexMatcher.find()) {
            result = regexMatcher.group().trim();
        }

		return result;
	}
	
	public static void throwIfNullOrEmpty(String arg, String contextMessage) {
		if (null == arg ||
				(arg != null && arg.length() <= 0)) {
			throw new IllegalArgumentException(contextMessage + " (" + arg + ") is not valid.");
		}
	}
	
	/**
     * Determines whether input string is a number or not
     * Locale-safe
     * @param str - input string to be tested
     * @return true if input string can be parsed to a number
     */
	public static boolean isStringNumeric(String str)
	{
	    DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
	    char localeMinusSign = currentLocaleSymbols.getMinusSign();

	    if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != localeMinusSign) return false;

	    boolean isDecimalSeparatorFound = false;
	    char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

	    for (char c : str.substring(1).toCharArray())
	    {
	        if (!Character.isDigit(c))
	        {
	            if (c == localeDecimalSeparator && !isDecimalSeparatorFound)
	            {
	                isDecimalSeparatorFound = true;
	                continue;
	            }
	            return false;
	        }
	    }
	    return true;
	}
	
	/**
     * Converts int OS version number supplied by AM
     * to a human-readable major.minor version number
     * example: 83984384 -> 10.5
     * @param version - integer version number to be converted
     * @return string version number
     */
	public static String convertIntVersionToString(int version) {
		int v1 = (version >> 24) & 0xff;
		int v2 = (version >> 20) & 0xf;
		int v3 = (version >> 16) & 0xf;
		return "" + v1 + "." + v2 + "."  + v3;
	}
	
	/**
     * Compares 2 string version numbers
     * returns 1 if 1st version is greater than 2nd,
     * returns -1 if 1st version is lesser than 2nd,
     * returns 0 if the versions are equal
     * @param ver1 - string version number to be compared
     * @param ver2 - string version number to be compared against
     * @return int result of comparison
     */
	public static int compareStringVersionNumbers(String ver1, String ver2) {
		
		ArrayList<String> vals1 =  new ArrayList<String>(Arrays.asList(ver1.split("\\.")));
		//remove trailing zeros
		for(int j=vals1.size()-1; j>=0; j--) {
			if(vals1.get(j).equals("0")) {
				vals1.remove(j);
			} else {
				break;
			}
		}
		ArrayList<String> vals2 =  new ArrayList<String>(Arrays.asList(ver2.split("\\.")));
		//remove trailing zeros
		for(int j=vals2.size()-1; j>=0; j--) {
			if(vals2.get(j).equals("0")) {
				vals2.remove(j);
			} else {
				break;
			}
		}
		int i=0;
		while(i<vals1.size() && i<vals2.size() && vals1.get(i).equals(vals2.get(i))) {
		  i++;
		}

		if (i<vals1.size() && i<vals2.size()) {
		    int diff = Integer.valueOf(vals1.get(i)).compareTo(Integer.valueOf(vals2.get(i)));
		    return Integer.signum(diff);
		}

		return Integer.signum(vals1.size() - vals2.size());
	}
	
	/**
	 * Concatenates two comma separated string lists.
	 * 
	 * @param list1 The first list to concatenate
	 * @param list2 The second list to concatenate
	 * @return list1 and list2 
	 */
	public static String concatenateCommaSeparatedLists(String list1, String list2){
		
		String result = "";
		
		if (list1 != null && list1.length() != 0){
			result = list1;
		}

		if (list2 != null && list2.length() != 0){
			if (result.length() != 0){
				result = result.concat(",");
			}
			result = result.concat(list2);
		}
		
		return result;
	}
	
	public static byte[] serializeObjToBytes(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    
	    return out.toByteArray();
	}
	
	public static ArrayList<String> extractArrayAsStringFromPlist(String plist){
		Pattern pattern = Pattern.compile("(<string>)(.+)(</string>)");
		Matcher matcher = pattern.matcher(plist);
		ArrayList<String> enums = new ArrayList<String>();
		
		while (matcher.find()) {
			enums.add(matcher.group(2));
		}
		
		return enums;
	}
	
	// Split long number into two integers: the first one is low part, and the other is high part
	public static int[] Convert64BasedIntegerTo32BasedIntegers(String integer64Based) {
		int[] retVal = new int[2];
		Long dataValue = Long.parseLong(integer64Based);
		retVal[0] = (int) (dataValue & 0xFFFFFFFFL);
		retVal[1] = (int) ((dataValue & 0xFFFFFFFF00000000L) >> 32);
		
		return retVal;
	}
}