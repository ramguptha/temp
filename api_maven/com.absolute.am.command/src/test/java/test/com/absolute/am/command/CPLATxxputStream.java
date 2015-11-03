package test.com.absolute.am.command;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.CPLATInputStream;
import com.absolute.am.command.CPLATOutputStream;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class CPLATxxputStream {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_strings() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		System.out.println("Write a bunch of test strings.");
		String[] testStrings = new String[] {"", "1", "1234", "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"};

		for (int i=0; i<testStrings.length; i++) {			
			cplatos.writeString(testStrings[i]);	
			System.out.println("string[" + i + "]=" + testStrings[i]);
		}
		
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized string is:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		
		System.out.println("read the strings back and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		for (int i=0; i<testStrings.length; i++) {
			String readString = cplatis.readString();
			System.out.println("string[" + i + "]=" + readString);
			assertEquals(testStrings[i], readString);
		}		
		cplatis.close();
	}
	
	private <T> void exception_when_insufficient_data_for_Type(T val) throws IOException {

		Class<?> valClass = val.getClass();
		System.out.println("Write a " + valClass.toString() + " to the stream.");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
		
		if (valClass.equals(String.class)) {
			cplatos.writeString((String)val);
		} else if (valClass.equals(UUID.class)) {
			cplatos.writeUUID((UUID)val);
		} else if (valClass.equals(Byte.class)) {
			cplatos.writeByte((Byte)val);
		} else if (valClass.equals(Short.class)) {
			cplatos.writeShort((Short)val);
		} else if (valClass.equals(Integer.class)) {
			cplatos.writeInt((Integer)val);
		} else if (valClass.equals(Long.class)) {
			cplatos.writeLong((Long)val);			
		}

		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		
		System.out.println("truncate the output stream, and try to read back the " + valClass.toString());	
		byte[] theStream = baos.toByteArray();
		byte[] truncatedStream = Arrays.copyOf(theStream,  theStream.length - 1);
		System.out.println("truncated stream contains: [" + StringUtilities.toHexString(truncatedStream) + "]");
		ByteArrayInputStream bais = new ByteArrayInputStream(truncatedStream);
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		
		if (valClass.equals(String.class)) {
			@SuppressWarnings("unused")
			String readVal = cplatis.readString();
		} else if (valClass.equals(UUID.class)) {
			@SuppressWarnings("unused")
			UUID uuidVal = cplatis.readUUID();
		} else if (valClass.equals(Byte.class)) {
			@SuppressWarnings("unused")
			byte byteVal = cplatis.readByte();			
		} else if (valClass.equals(Short.class)) {
			@SuppressWarnings("unused")
			short shortVal = cplatis.readShort();			
		} else if (valClass.equals(Integer.class)) {
			@SuppressWarnings("unused")
			int intVal = cplatis.readInt();
		} else if (valClass.equals(Long.class)) {
			@SuppressWarnings("unused")
			long longVal = cplatis.readLong();
		}
			
		cplatis.close();
		fail("Should never get here.");				
	}
	
	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_string() throws IOException {
		String testString = "Hello world";
		exception_when_insufficient_data_for_Type(testString);		
	}		

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_numbers() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		System.out.println("Write a bunch of test numbers.");
		byte byteValue = (byte)0xFA;
		short shortValue = (short)0xFABB;
		int intValue = (int)0xFABBCCDD;
		long longValue = (long)0x001122334455667788L;

		cplatos.writeByte(byteValue);
		cplatos.writeShort(shortValue);
		cplatos.writeInt(intValue);
		cplatos.writeLong(longValue);
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		
		System.out.println("read the numbers back and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		byte byteValueRead = cplatis.readByte();
		short shortValueRead = cplatis.readShort();
		int intValueRead = cplatis.readInt();
		long longValueRead = cplatis.readLong();
		cplatis.close();
		
		assertEquals("byteValue", byteValue, byteValueRead);
		assertEquals("shortValue", shortValue, shortValueRead);
		assertEquals("intValue", intValue, intValueRead);
		assertEquals("longValue", longValue, longValueRead);
	}	

	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_Byte() throws IOException {
		Byte byteVal = new Byte((byte)0xAA);
		exception_when_insufficient_data_for_Type(byteVal);		
	}		

	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_Short() throws IOException {
		Short shortVal = new Short((short)0xAABB);
		exception_when_insufficient_data_for_Type(shortVal);		
	}		

	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_Int() throws IOException {
		Integer intVal = new Integer((int)0xAABBCCDD);
		exception_when_insufficient_data_for_Type(intVal);		
	}
	
	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_Long() throws IOException {
		Long longVal = new Long((long)0xAABBCCDDEEFF1122L);
		exception_when_insufficient_data_for_Type(longVal);		
	}		
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_uuids() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		System.out.println("Write some uuids.");
		UUID first = UUID.randomUUID();
		UUID second = UUID.randomUUID();
		UUID third = UUID.randomUUID();		

		cplatos.writeUUID(first);
		cplatos.writeUUID(second);
		cplatos.writeUUID(third);
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		
		System.out.println("read the UUIDs back and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		UUID firstRead = cplatis.readUUID();
		UUID secondRead = cplatis.readUUID();
		UUID thirdRead = cplatis.readUUID();
		cplatis.close();
		assertEquals("first", first, firstRead);
		assertEquals("second", second, secondRead);
		assertEquals("third", third, thirdRead);
	}	

	@Test(expected=java.io.EOFException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void exception_when_insufficient_data_for_uuid() throws IOException {
		UUID testUUID = UUID.randomUUID();
		exception_when_insufficient_data_for_Type(testUUID);		
	}	

	private static final String beforeString = "Before";
	private static final String afterString = "After";
	private static final String intValueName = "intValue";
	private static final int intValue = -99;
	private static final String stringValueName = "stringValue";
	private static final String stringValue = "Hello this is a string.";
	
	private void assertPlistHasIntAndStringValues(Map<?,?> theMap) {
		Long readBackIntValue = (Long)theMap.get(intValueName);
		assertEquals(intValue, readBackIntValue.intValue());
		
		String readBackStringValue = (String)theMap.get(stringValueName);
		assertEquals(readBackStringValue, stringValue);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_small_propertylist() throws IOException, ParserConfigurationException, SAXException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		
		System.out.println("Write a string before the property list.");
		cplatos.writeString(beforeString);
		System.out.println("Prepare and write the property list.");
		PropertyList plist = new PropertyList(); {		
			plist.put(intValueName, intValue);
			plist.put(stringValueName, stringValue);			
		}

		cplatos.writePropertyList(plist);
		
		System.out.println("Write a string after the property list.");
		cplatos.writeString(afterString);
		
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		System.out.println("                 printable:[" + StringUtilities.toPrintableString(baos.toByteArray()) + "]");
		
		System.out.println("read the two strings and the property list and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		
		String beforeStringRead = cplatis.readString();
		System.out.println("Before string is:" + beforeStringRead);
		assertEquals(beforeString, beforeStringRead);
		
		PropertyList plistReadBack = cplatis.readPropertyList();
		// Check that the PropertyList has the correct values
		assertPlistHasIntAndStringValues(plistReadBack);
				
		String afterStringReadBack = cplatis.readString();
		System.out.println("After string is:" + afterStringReadBack);	
		assertEquals(afterString, afterStringReadBack);
		cplatis.close();
	}	

	
	private static final String nestedPropertiesName = "nestedProperties";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_nested_propertylist() throws IOException, ParserConfigurationException, SAXException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		final UUID uuidToExpect = UUID.fromString("4443C232-CA02-41F7-A8CD-FBBDC63CC555");
		
		System.out.println("Write a string before the property list.");
		cplatos.writeString(beforeString);
		
		System.out.println("Prepare and write the property list.");
		PropertyList plist = new PropertyList(); {		
			plist.put(intValueName, intValue);			
			plist.put("uuidValue", uuidToExpect);
			plist.put("BooleanValue", true);
			plist.put("intArray", new int[]{-8,-9,259,3847});
			
			PropertyList nested = new PropertyList();
			nested.put(intValueName, intValue);			
			nested.put("uuidValue", uuidToExpect);
			nested.put("BooleanValue", false);
			nested.put("intArray", new int[]{99999,-8,-9,259,3847});
			nested.put(stringValueName, stringValue);
			
			plist.put("nestedProperties", nested);
			plist.put(stringValueName, stringValue);
		}

		cplatos.writePropertyList(plist);
		
		System.out.println("Write a string after the property list.");
		cplatos.writeString(afterString);
		
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		System.out.println("                 printable:[" + StringUtilities.toPrintableString(baos.toByteArray()) + "]");
		
		System.out.println("read the two strings and the property list and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		
		String beforeStringReadBack = cplatis.readString();
		System.out.println("Before string is:" + beforeStringReadBack);
		assertEquals(beforeString, beforeStringReadBack);
		
		PropertyList plistReadBack = cplatis.readPropertyList();
		// Check that the PropertyList has the correct values
		assertPlistHasIntAndStringValues(plistReadBack);

		Map<?,?> nestedProperties = (Map<?,?>)plistReadBack.get(nestedPropertiesName);
		assertPlistHasIntAndStringValues(nestedProperties);
				
		String afterStringReadBack = cplatis.readString();
		System.out.println("After string is:" + afterString);
		assertEquals(afterString, afterStringReadBack);		
		cplatis.close();
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_read_write_empty_propertylist() throws IOException, ParserConfigurationException, SAXException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CPLATOutputStream cplatos = new CPLATOutputStream(baos);
	
		System.out.println("Write a string before the property list.");
		cplatos.writeString(beforeString);
		
		PropertyList emptyPropertyList = new PropertyList();
		cplatos.writePropertyList(emptyPropertyList);
		
		System.out.println("Write a string after the property list.");
		cplatos.writeString(afterString);
		
		cplatos.flush();
		cplatos.close();
		
		System.out.println("log the content of the stream.");
		System.out.println("serialized stream contains:[" + StringUtilities.toHexString(baos.toByteArray()) + "]");
		System.out.println("                 printable:[" + StringUtilities.toPrintableString(baos.toByteArray()) + "]");
		
		System.out.println("read the two strings and the property list and compare to originals.");		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		CPLATInputStream cplatis = new CPLATInputStream(bais);
		
		String beforeStringReadBack = cplatis.readString();
		System.out.println("Before string is:" + beforeStringReadBack);
		assertEquals(beforeString, beforeStringReadBack);
		
		PropertyList plistReadBack = cplatis.readPropertyList();
		// Check that the PropertyList is empty
		assertTrue(plistReadBack.isEmpty());

		String afterStringReadBack = cplatis.readString();
		System.out.println("After string is:" + afterString);
		assertEquals(afterString, afterStringReadBack);		
		cplatis.close();
	}		
	
}
