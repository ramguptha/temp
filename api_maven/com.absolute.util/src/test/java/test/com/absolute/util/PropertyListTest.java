/**
 * 
 */
package test.com.absolute.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class PropertyListTest {

	// Base64 encoders usually wrap the string at length 76, we don't want that, so this string is intentionally longer so we can check that wrapping doesn't happen 
	private static final byte[] bigByteArray = StringUtilities.fromHexString("3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C21444F435459504520706C697374205055424C494320222D2F2F4170706C6520436F6D70716D703C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C21444F435459504520706C697374205055424C494320222D2F2F4170706C6520436F6D70716D70");
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_serialize_different_types() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {		
		PropertyList plist = new PropertyList();
		plist.put("intValue", (int)-27);
		plist.put("StringValue", "Hello this is a string.");
		plist.put("emptyString", "");
		Calendar calSample = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calSample.set(2012, 8, 19, 12, 11, 10); 		

		Date dateSample = calSample.getTime();
		plist.put("dateValue", dateSample);
		plist.put("calendarValue", calSample);
		plist.put("uuidValue", uuidToExpect);
		plist.put("BooleanValue", true);
		plist.put("AnotherBooleanValue", false);
		plist.put("intArray", new int[]{-8,-9,259,3847});
		plist.put("emptyIntArray", new int[]{});
		plist.put("byteArray", bigByteArray);
		plist.put("emptyByteArray", new byte[]{});
		plist.put("uuidArray", new UUID[]{uuidToExpect, uuidToExpect});
		plist.put("emptyUUIDArray", new UUID[]{});
		plist.put("stringArray", new String[]{"stringVal1", "stringVal2"});
		plist.put("emptyStringArray", new String[]{});
		
		PropertyList nested = new PropertyList();
		nested.put("intValue", (int)500);
		nested.put("StringValue", "string.");
		nested.put("uuidValue", uuidToExpect);
		nested.put("BooleanValue", false);
		nested.put("intArray", new int[]{99999,-8,-9,259,3847});
		
		plist.put("nestedProperties", nested);
		
		LinkedHashMap<String, Object> nested2 = new LinkedHashMap<String, Object>();
		nested2.put("intValue", 33);
		nested2.put("StringValue", "another string");
		plist.put("nested2", nested2);
		
		String plistAsXML = plist.toXMLString();
		System.out.println("The plist as XML is:" + plistAsXML);
		
		PropertyList readBackPlist = PropertyList.fromByteArray(plistAsXML.getBytes("UTF-8"));
		String readBackAsXML = readBackPlist.toXMLString();
		
		System.out.println("The readBack plist as XML is:" + readBackAsXML);
		System.out.println("Comparison readBackAsXML.equals(plistAsXML) is " + readBackAsXML.equals(plistAsXML));
		System.out.println("                        plistAsXML.getBytes(\"UTF-8\") as hex=" + StringUtilities.toHexString(plistAsXML.getBytes("UTF-8")));
		System.out.println("       readBackPlist.toXMLString().getBytes(\"UTF-8\") as hex=" + StringUtilities.toHexString(readBackPlist.toXMLString().getBytes("UTF-8")));
		System.out.println(" expectedXmlPlistForDifferentTypes.getBytes(\"UTF-8\") as hex=" + StringUtilities.toHexString(expectedXmlPlistForDifferentTypes.getBytes("UTF-8")));

		assertEquals(plistAsXML, readBackAsXML);
		assertEquals(expectedXmlPlistForDifferentTypes, plistAsXML);
	}

	private final UUID uuidToExpect = UUID.fromString("4443C232-CA02-41F7-A8CD-FBBDC63CC555");
	private final String expectedXmlPlistForDifferentTypes = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
"<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n" +
"<plist version=\"1.0\">\r\n" +
"<dict>\r\n" +
"    <key>intValue</key>\r\n" +
"    <integer>-27</integer>\r\n" +
"    <key>StringValue</key>\r\n" +
"    <string>Hello this is a string.</string>\r\n" +
"    <key>emptyString</key>\r\n" +
"    <string/>\r\n" +
"    <key>dateValue</key>\r\n" +
"    <date>2012-09-19T12:11:10Z</date>\r\n" +
"    <key>calendarValue</key>\r\n" +
"    <date>2012-09-19T12:11:10Z</date>\r\n" +
"    <key>uuidValue</key>\r\n" +
"    <string>4443C232-CA02-41F7-A8CD-FBBDC63CC555</string>\r\n" +
"    <key>BooleanValue</key>\r\n" +
"    <true/>\r\n" +
"    <key>AnotherBooleanValue</key>\r\n" +
"    <false/>\r\n" +
"    <key>intArray</key>\r\n" +
"    <array>\r\n" +
"        <integer>-8</integer>\r\n" +
"        <integer>-9</integer>\r\n" +
"        <integer>259</integer>\r\n" +
"        <integer>3847</integer>\r\n" +
"    </array>\r\n" +
"    <key>emptyIntArray</key>\r\n" +
"    <array>\r\n" +
"    </array>\r\n" +
"    <key>byteArray</key>\r\n" +
"    <data>\r\n" +
"    PD94bWwgdmVyc2lvbj0i\r\n" +
"    MS4wIiBlbmNvZGluZz0i\r\n" +
"    VVRGLTgiPz4NCjwhRE9D\r\n" +
"    VFlQRSBwbGlzdCBQVUJM\r\n" +
"    SUMgIi0vL0FwcGxlIENv\r\n" +
"    bXBxbXA8P3htbCB2ZXJz\r\n" +
"    aW9uPSIxLjAiIGVuY29k\r\n" +
"    aW5nPSJVVEYtOCI/Pg0K\r\n" +
"    PCFET0NUWVBFIHBsaXN0\r\n" +
"    IFBVQkxJQyAiLS8vQXBw\r\n" +
"    bGUgQ29tcHFtcA==\r\n" +
"    </data>\r\n" +
"    <key>emptyByteArray</key>\r\n" +
"    <data>\r\n" +
"    </data>\r\n" +
"    <key>uuidArray</key>\r\n" +
"    <array>\r\n" +
"        <string>4443C232-CA02-41F7-A8CD-FBBDC63CC555</string>\r\n" +
"        <string>4443C232-CA02-41F7-A8CD-FBBDC63CC555</string>\r\n" +
"    </array>\r\n" +
"    <key>emptyUUIDArray</key>\r\n" +
"    <array>\r\n" +
"    </array>\r\n" +
"    <key>stringArray</key>\r\n" +
"    <array>\r\n" +
"        <string>stringVal1</string>\r\n" +
"        <string>stringVal2</string>\r\n" +
"    </array>\r\n" +
"    <key>emptyStringArray</key>\r\n" +
"    <array>\r\n" +
"    </array>\r\n" +
"    <key>nestedProperties</key>\r\n" +
"    <dict>\r\n" +
"        <key>intValue</key>\r\n" +
"        <integer>500</integer>\r\n" +
"        <key>StringValue</key>\r\n" +
"        <string>string.</string>\r\n" +
"        <key>uuidValue</key>\r\n" +
"        <string>4443C232-CA02-41F7-A8CD-FBBDC63CC555</string>\r\n" +
"        <key>BooleanValue</key>\r\n" +
"        <false/>\r\n" +
"        <key>intArray</key>\r\n" +
"        <array>\r\n" +
"            <integer>99999</integer>\r\n" +
"            <integer>-8</integer>\r\n" +
"            <integer>-9</integer>\r\n" +
"            <integer>259</integer>\r\n" +
"            <integer>3847</integer>\r\n" +
"        </array>\r\n" +
"    </dict>\r\n" +
"    <key>nested2</key>\r\n" +
"    <dict>\r\n" +
"        <key>intValue</key>\r\n" +
"        <integer>33</integer>\r\n" +
"        <key>StringValue</key>\r\n" +
"        <string>another string</string>\r\n" +
"    </dict>\r\n" +
"</dict>\r\n" +
"</plist>\r\n"; 
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_parse_different_types() throws ParserConfigurationException, SAXException, IOException {
		byte[] plistAsByteArray = expectedXmlPlistForDifferentTypes.getBytes("UTF-8");
		
		PropertyList plist = PropertyList.fromByteArray(plistAsByteArray);
		assertTrue(plist.containsKey("intValue"));
		String plistAsString = plist.toString();
		System.out.println("PropertyList as xml [" + plistAsString + "]");
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_element_as_type() throws ParserConfigurationException, SAXException, IOException {

		PropertyList plist = null;
		// try-with-resources ensures stream gets closed (JDK7)
		try (InputStream is = getClass().getResourceAsStream("/PropertyListTest.xml")) {
			plist = PropertyList.fromInputStream(is);
		}

		assertTrue(PropertyList.elementExists(plist, "View Definitions/All Android Tablets/Filter"));
		assertFalse(PropertyList.elementExists(plist, "DoesntExist/View Definitions/All Android Tablets/Filter"));
		
		String xmlPath = "View Definitions/All Android Tablets/ColumnProperties";
		Map<String, Object> colProps = PropertyList.getElementAs(plist, xmlPath);
		assertTrue(colProps.containsKey("column_device_model"));		
		Object colProps2 = PropertyList.getElementAs(plist, xmlPath);
		assertTrue("check that getElementAs returns the same object", colProps2 == colProps);
		Map<String, Object> colProps3 = PropertyList.getElementAsMap(plist, xmlPath);
		assertTrue("check that getElementAsMap returns the same object", colProps3 == colProps);
	
		xmlPath = "View Definitions/All Android Tablets/RootTable";
		String rootTable = PropertyList.getElementAs(plist, xmlPath);
		assertEquals(rootTable, "iphone_info");
		Object rootTable2 = PropertyList.getElementAs(plist, xmlPath);
		assertTrue("check that getElementAtPath returns the same object", rootTable2 == rootTable);
		String rootTable3 = PropertyList.getElementAsString(plist, xmlPath);
		assertTrue("check that getElementAtPathString returns the same object", rootTable3 == rootTable);
		
		
		ArrayList<String> columnOrder = PropertyList.getElementAsArrayListString(plist, "View Definitions/All Android Tablets/ColumnOrder");		
		assertTrue(columnOrder.indexOf("column_device_model") > 0);

		ArrayList<Map<String, Object>> sortOrders = PropertyList.getElementAsArrayListMap(plist, "View Definitions/All Android Tablets/SortOrder");
		assertEquals(sortOrders.size(), 1);
		Map<String, Object> sortOrder = sortOrders.get(0);
		assertTrue(sortOrder.containsKey("Ascending"));
		
		assertFalse(PropertyList.elementExists(plist, "No/Such/Entry"));
		Map<String, Object> notThere = PropertyList.getElementAs(plist, "No/Such/Entry");
		assertNull(notThere);		
		
		System.out.println("Finished PropertyList tests!");
	}
	
	@Test(expected=ClassCastException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_get_element_as_wrong_type() throws ParserConfigurationException, SAXException, IOException {

		PropertyList plist = null;
		// try-with-resources ensures stream gets closed (JDK7)
		try (InputStream is = getClass().getResourceAsStream("/PropertyListTest.xml")) {
			plist = PropertyList.fromInputStream(is);
		}
		
		final String thePath = "View Definitions/All Android Tablets/ColumnProperties/column_device_model/Width";

		// First, confirm that the value can be retrieved as the correct type.
		long shouldWork = PropertyList.getElementAs(plist, thePath);
		assertTrue("shouldWork didn't work!", shouldWork == 160);
		
		// Now confirm that it fails when the wrong type is used.
		@SuppressWarnings("unused")
		Map<String, Object> colProps3 = PropertyList.getElementAs(plist, thePath);
		
		fail("Expected exception not thrown.");
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_remove_elements() throws ParserConfigurationException, SAXException, IOException {
		
		PropertyList plist = null;
		// try-with-resources ensures stream gets closed (JDK7)
		try (InputStream is = getClass().getResourceAsStream("/PropertyListTest.xml")) {
			plist = PropertyList.fromInputStream(is);
		}
		
		int oriXmlLength = plist.toXMLString().length();
		assertTrue(PropertyList.elementExists(plist, "View Definitions/All Android Tablets/Filter"));
		PropertyList.removeElement(plist, "View Definitions/All Android Tablets/Filter");
		int newXmlLength = plist.toXMLString().length();
		assertFalse(PropertyList.elementExists(plist, "View Definitions/All Android Tablets/Filter"));
		assertTrue("check new length < old length after removing an element", newXmlLength < oriXmlLength);
		
		assertTrue(PropertyList.elementExists(plist, "View Definitions/All Android Tablets"));
		PropertyList.removeElement(plist, "View Definitions/All Android Tablets");
		newXmlLength = plist.toXMLString().length();
		assertFalse(PropertyList.elementExists(plist, "View Definitions/All Android Tablets"));
		assertTrue("check new length < old length after removing an element", newXmlLength < oriXmlLength);
		
		assertTrue(PropertyList.elementExists(plist, "View Definitions"));
		PropertyList.removeElement(plist, "View Definitions");
		newXmlLength = plist.toXMLString().length();
		assertFalse(PropertyList.elementExists(plist, "View Definitions"));
		assertTrue("check new length < old length after removing an element", newXmlLength < oriXmlLength);
				
	}
}
