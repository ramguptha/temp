/**
 * 
 */
package com.absolute.util;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for generating plists (Apple) in XML format. Note that the
 * class extends LinkedHashMap. This is done to ensure that the elements of the
 * plist are serialized in the order they were added. It is not strictly
 * required, however it makes testing and validation of the generated XML
 * easier.
 */
public class PropertyList extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private int indentLevel = 0;
	private final int indentSize = 4; // Indentation is 4 spaces.

	public PropertyList() {
	}
	
	public PropertyList(Map<String, Object> dict) {
		this.putAll(dict);
	}
	
	public void put(String key, int value) {

		put(key, new Long(value));
	}

	/**
	 * Test if an element/property exists. 
	 * @param map The map to search
	 * @param xmlPath The path to the element. This should be of the format 
	 * part1/part2/.../partN where partN is the element to be returned.
	 * @return true if the element exists.
	 */
	public static boolean elementExists(Map<String, Object> map, String xmlPath) {
		return (getElementAs(map, xmlPath) != null);
	}
	
	/**
	 * Retrieve the element of the specified type at the specified path. The type returned
	 * is determined by the type of the object the result is being assigned to. For example,
	 * to get an element as a string, do this <code>String retVal = PropertyList.getElementAs(plist, "root/node1/value3");</code>
	 * If the type of the value does not match, an exception will be thrown. 	
	 * @param map the map to search
	 * @param xmlPath the path where the element should be found
	 * @return the element, or null if not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getElementAs(Map<String, Object> map, String xmlPath) {
		
		T retVal = null;
		
		String[] pathParts = xmlPath.split("/");
		if (pathParts.length > 0) {
			Map<String, Object> childView = map;
			for (int i = 0; i < pathParts.length-1; i++) {
				childView = getChildMapFromMap(pathParts[i], childView);
				if (childView == null) {
					break;
				}
			}
			
			if (childView != null) {
				retVal = (T)childView.get(pathParts[pathParts.length-1]);
			}
			
		} else {
			retVal = null;
		}
		
		return retVal;
	}
	

	/**
	 * Extract the Map<String, Object> at the location denoted by xmlPath
	 * 
	 * @param map the property map to be walked.
	 * @param xmlPath This should be of the format part1/part2/.../partN where partN is 
	 * the element to be returned as a string.
	 * @return The element as a Map<String, Object>, or null if element can not be found
	 */
	public static Map<String, Object> getElementAsMap(Map<String, Object> map, String xmlPath) {
		Map<String, Object> elementMap = getElementAs(map, xmlPath);
		return elementMap;
	}

	/**
	 * Extracts the element denoted by xmlPath, as a string
	 *  
	 * @param map The property map to be walked.
	 * @param xmlPath This should be of the format part1/part2/.../partN where partN 
	 * is the element to be returned as a string.
	 * @return The element as a String, or null if element can not be found
	 */
	public static String getElementAsString(Map<String, Object> map, String xmlPath) {
		String elementString = getElementAs(map, xmlPath);
		return elementString;
	}

	/**
	 * Extracts the element denoted by xmlPath, as an ArrayList<String>.
	 * 
	 * @param propList the property map to be walked.
	 * @param xmlPath the XML representation of the property list
	 * This should be of the format part1/part2/.../partN where partN is the element to
	 * be returned as an ArrayList<String>.
	 * @return The element as an ArrayList<String>, or null if element can not be found
	 */
	public static ArrayList<String> getElementAsArrayListString(Map<String, Object> childView, String xmlPath) {
		ArrayList<String> elementArray = null;
		
		Object theObj = getElementAs(childView, xmlPath);
		Class<?> theClass = theObj.getClass();
		if (theClass.isArray()) {
			elementArray = new ArrayList<String>();
			for (int i = 0; i < Array.getLength(theObj); i++) {
				Object val = Array.get(theObj, i);				
				// As all objects can be converted to a string, this explicit check confirms that the object is an actual string.
				if (!(val instanceof String)) {
					throw new RuntimeException("Object of type " + val.getClass().toString() + " cannot be converted to String.");
				}
				elementArray.add((String)Array.get(theObj, i));
			}
		}
		return elementArray;
	}

	/**
	 * Extracts the element denoted by xmlPath, as an ArrayList<Map<String, Object>>.
	 * 
	 * @param propList the property map to be walked.
	 * @param xmlPath the XML representation of the property list
	 * This should be of the format part1/part2/.../partN where partN is the element to
	 * be returned as an ArrayList<String>.
	 * @return The element as an ArrayList<Map<String, Object>>, or null if element can not be found
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Map<String, Object>> getElementAsArrayListMap(Map<String, Object> childView, String xmlPath) {
		ArrayList<Map<String, Object>> elementArray = null;
				
		Object theObj = getElementAs(childView, xmlPath);
		
		if (theObj != null){
			if( theObj instanceof  ArrayList<?>){
				return (ArrayList<Map<String, Object>>) theObj;
			}
			
			Class<?> theClass = theObj.getClass();
			if (theClass.isArray()) {
				elementArray = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < Array.getLength(theObj); i++) {
					elementArray.add((Map<String, Object>)Array.get(theObj, i));
				}
			}
		}
		
		return elementArray;
	}
		
	/**
	 * Generates the XML representation of this property list. All keys and values in the 
	 * property list are serialized. 
	 *  
	 * @return plist in XML
	 */
	public String toXMLString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		indentLevel = 0;
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
		pw.println("<plist version=\"1.0\">");

		dictionaryToXML(pw, this);

		pw.println();
		pw.println("</plist>");
		pw.flush();
		pw.close();
		return sw.toString();
	}
	
	/**
	 * Creates an instance of PropertyList from the given XML string.
	 * 
	 * @param xmlString the XML representation of the property list
	 * @return a PropertyList object populated from the XML string.
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static PropertyList fromString(String xmlString)
			throws UnsupportedEncodingException, ParserConfigurationException,
			SAXException, IOException {

		return fromByteArray(xmlString.getBytes("UTF-8"));
	}

	/**
	 * Creates an instance of PropertyList from the given UTF-8 byte array.
	 * @param propListByteArray the UTF-8 byte array to parse
	 * @return a PropertyList object populated from the byte array
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static PropertyList fromByteArray(byte[] propListByteArray)
			throws ParserConfigurationException, SAXException, IOException {

		ByteArrayInputStream bais = new ByteArrayInputStream(propListByteArray);
		return fromInputStream(bais);
	}

	/**
	 * An entity resolved class that does nothing. This prevents the parser from
	 * downloading the DTD from Apple each time it parses a plist.
	 *
	 */
	private class NullEntityResolver implements EntityResolver {
	    public InputSource resolveEntity(String publicID, String systemID)
	        throws SAXException {
	        
	        return new InputSource(new StringReader(""));
	    }
	}

	/**
	 * Creates an instance of PropertyList from teh given input stream. The input
	 * stream reads UTF-8 bytes.
	 * @param is the InputStream to read from 
	 * @return a PropertyList object populated from the InputStream.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static PropertyList fromInputStream(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {

		PropertyList retVal = new PropertyList();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setCoalescing(true);
		dbf.setIgnoringComments(true);
		dbf.setValidating(false); // TODO: could set validating to true, and
									// provide the DTD inline.
		

		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(retVal.new NullEntityResolver());
		Document dom = db.parse(is);

		Element rootElement = dom.getDocumentElement();
		if (rootElement == null
				|| (rootElement != null && !rootElement.getTagName().equals(
						"plist"))) {
			throw new EOFException("<plist> not found.");
		}

		removeEmptyTextNodes(rootElement);

		Node dictNode = rootElement.getFirstChild();
		if (dictNode != null
				&& !dictNode.getNodeName().equalsIgnoreCase("dict")) {
			throw new EOFException("<dict> not found.");
		}

		parseAnyMapFromDictNode(dictNode, retVal);
		return retVal;
	}
	
	/**
	 * Removes an element at the location denoted by xmlPath.
	 * 
	 * @param childView the map to remove the element from.
	 * @param xmlPath the XML representation of the location in the property list
	 * This should be of the format part1/part2/.../partN where partN is the element to
	 * be removed.
	 */
	public static void removeElement(Map<String, Object> childView, String xmlPath) {		
		String[] pathParts = xmlPath.split("/");
		int pathPartsIndex = 0;
		if (pathParts.length > 1) {
			// length - 1 so the final key name is left.
			while (pathPartsIndex < (pathParts.length - 1)) {			
				childView = getChildMapFromMap(pathParts[pathPartsIndex], childView);
				if (childView == null) {
					// no such element, it is already removed.
					return;
				}
				pathPartsIndex++;
			}
		}
		if (childView != null) {
			childView.remove(pathParts[pathPartsIndex]);
		}
	}

	/**
	 * Populates the given map object with the contents of the dictionary node.
	 * 
	 * @param dictNode
	 *            the dictionary node
	 * @param theMap
	 *            the map to populate
	 * @throws EOFException when the complete dictionary cannot be parsed
	 */
	private static void parseAnyMapFromDictNode(Node dictNode,
			Map<String, Object> theMap) throws EOFException {

		if (null == theMap) {
			throw new IllegalArgumentException("theMap cannot be null");
		}

		if (!dictNode.getNodeName().equals("dict")) {
			throw new EOFException("dict not found");
		}

		theMap.clear();

		NodeList children = dictNode.getChildNodes();

		int index = 0;
		while (children.item(index) != null) {
			Node keyNode = children.item(index);
			if (keyNode != null && keyNode.getNodeName().equals("key")) {
				String key = keyNode.getFirstChild().getNodeValue();
				Object value = parseObject(children.item(index + 1));
				if (value != null)
					theMap.put(key, value);
			}
			index = index + 2;
		}

	}

	/**
	 * Converts a node into the corresponding java object. If the object is not
	 * recognized it returns a String object containing the nodes content.
	 * 
	 * @param node
	 * @return
	 * @throws EOFException when the complete object cannot be parsed
	 */
	private static Object parseObject(Node node) throws EOFException {
		Object ret;
		String nodeName = node.getNodeName();
		Node child = node.getFirstChild();

		if (nodeName.equals("string")) {
			if (child == null) {
				ret = "";
			} else {
				ret = new String(child.getNodeValue());
			}
		} else if (nodeName.equals("date")) {
			if (child == null) {
				ret = null;
			} else {
				String dateStr = child.getNodeValue();

				ret = parseDate(dateStr);
			}
		} else if (nodeName.equals("real")) {
			if (child == null) {
				ret = null;
			} else {
				ret = Double.parseDouble(node.getFirstChild().getNodeValue());
			}
		} else if (nodeName.equals("integer")) {
			if (child == null) {
				ret = null;
			} else {
				ret = Long.parseLong(node.getFirstChild().getNodeValue());
			}
		} else if (nodeName.equals("dict")) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			parseAnyMapFromDictNode(node, map);
			ret = map;
		} else if (nodeName.equals("true")) {
			ret = true;
		} else if (nodeName.equals("false")) {
			ret = false;
		} else if (nodeName.equals("array")) {
			NodeList list = node.getChildNodes();
			List<Object> array = new ArrayList<Object>(list.getLength());
			for (int i = 0; i < list.getLength(); i++) {
				Object obj = parseObject(list.item(i));
				if (obj != null)
					array.add(obj);
			}
			ret = array.toArray();
		} else if (nodeName.equals("data")) {
			String base64 = "";
			if (child != null)
				base64 = child.getNodeValue();
			byte[] data = StringUtilities.fromBase64String(base64);
			ret = data;
		} else {
			ret = new String(node.getFirstChild().getNodeValue());
		}

		return ret;
	}

	/**
	 * Parses the given string representation of a date and returs a Date object.
	 * @param dateStr the string to parse
	 * @return a Date object
	 */
	private static Date parseDate(String dateStr) {
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

	// TODO: Write a test case that proves removeEmptyTextNodes() is necessary
	// and effective.
	private static void removeEmptyTextNodes(Node node) {
		if (node.getNodeName().equalsIgnoreCase("string"))
			return;

		NodeList nodeList = node.getChildNodes();
		Node childNode;
		for (int x = nodeList.getLength() - 1; x >= 0; x--) {
			childNode = nodeList.item(x);
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				if (childNode.getNodeValue().trim().equals("")) {
					node.removeChild(childNode);
				}
			} else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				removeEmptyTextNodes(childNode);
			}
		}
	}

	/**
	 * Generates the XML representation of the given dictionary.
	 * @param pw where to write the result
	 * @param dict the dictionary to write
	 */
	private void dictionaryToXML(PrintWriter pw, Map<?, ?> dict) {

		pw.println("<dict>");
		indentLevel++;

		if (dict != null) {
			for (Object key : dict.keySet()) {
				if( dict.get(key) != null){
					pw.print(getIndent());
					pw.print("<key>");
					pw.print(escapeXMLChars(key.toString()));
					pw.println("</key>");
					objectToXML(pw, dict.get(key), key.toString());
				}
			}
			/*
			 * // By iterating the keys in reverse order, the values are
			 * serialized in the order they were added. String[] keys =
			 * dict.keySet().toArray(new String[dict.keySet().size()]); for (int
			 * i= keys.length-1; i>=0; i--) { String key = keys[i];
			 * pw.print(getIndent()); pw.print("<key>"); pw.print(key);
			 * pw.println("</key>"); writeElement(pw, dict.get(key), key); }
			 */
		}

		indentLevel--;
		pw.print(getIndent());
		pw.print("</dict>");
	}

	/**
	 * The format to use when serializing dates.
	 */
	private static final String DateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	/**
	 * Helper method to serialize a string. This was separated out because Strings and
	 * UUIDs are both serialized in the same way.
	 * @param pw
	 * @param theObject
	 * @param objectName
	 */
	protected void stringToXML(PrintWriter pw, String theObject, String objectName) {
		if (theObject.length() > 0) {
			pw.print("<string>");
			pw.print(escapeXMLChars(theObject));
			pw.print("</string>");
		} else {
			pw.print("<string/>");
		}		
	}
	
	
	protected void byteArrayToXML(PrintWriter pw, byte[] theObject, String objectName) {
		
		pw.println("<data>");
		// The base64 string should be broken into 20 character chunks.
		final int BASE64_CHUNK_LENGTH = 20;
		String unformattedBase64String = StringUtilities.toBase64String(theObject);
		int offset = 0;
		while (offset < unformattedBase64String.length()) {
			int thisChunkLength = Math.min(BASE64_CHUNK_LENGTH, unformattedBase64String.length() - offset);
			pw.print(getIndent());
			pw.println(unformattedBase64String.substring(offset, offset + thisChunkLength));
			offset += thisChunkLength;
		}

		pw.print(getIndent());
		pw.print("</data>");
		
	}
	
	/**
	 * Generates the XML representation of the given object.
	 * @param pw where to write the output to
	 * @param theObject the object to serialize
	 * @param objectName the name of this object - only used for diagnostics
	 */
	protected void objectToXML(PrintWriter pw, Object theObject, String objectName) {

		pw.print(getIndent());

		if (null == theObject) {
			throw new IllegalArgumentException("The object [" + objectName
					+ "] is null and cannot be serialized.");
		}

		Class<?> theClass = theObject.getClass();

		if (theClass.equals(String.class)) {
			String s = (String) theObject;
			stringToXML(pw,  s, objectName);			
		} else if (theClass.equals(Integer.class)
				|| theClass.equals(Long.class)) {
			pw.print("<integer>");
			pw.print(theObject.toString());
			pw.print("</integer>");
		} else if (theClass.equals(Double.class)
				|| theClass.equals(Float.class)) {
			pw.print("<real>");
			pw.print(theObject.toString());
			pw.print("</real>");
		} else if (theClass.equals(Date.class)) {
			Date value = (Date) theObject;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat sdf = new SimpleDateFormat(DateFormatPattern);
			sdf.setCalendar(cal);

			pw.print("<date>");
			pw.print(sdf.format(value));
			pw.print("</date>");

		} else if (Calendar.class.isAssignableFrom(theClass)) { 
			// TODO: Is it necessary to support multiple similar types (e.g. Calendar & Date, List & array )?
			Calendar value = (Calendar) theObject;
			SimpleDateFormat sdf = new SimpleDateFormat(DateFormatPattern);
			sdf.setCalendar(value);

			pw.print("<date>");
			pw.print(sdf.format(value.getTime()));
			pw.print("</date>");
		} else if (theClass.equals(Boolean.class)) {
			if (((Boolean) theObject) == true) {
				pw.print("<true/>");
			} else {
				pw.print("<false/>");
			}
		} else if (theClass.equals(UUID.class)) {
			// serialize a UUID as a string.
			stringToXML(pw, theObject.toString().toUpperCase(), objectName);
		} else if (theObject instanceof byte[]) { // NOTE: Must appear before the check
											// for an array.
			byte[] data = (byte[]) theObject;
			byteArrayToXML(pw, data, objectName);
			
		} else if (theClass.isArray()) {
			arrayToXML(pw, theObject, objectName);
		} else if (theObject instanceof List<?>) {
			List<?> l = (List<?>) theObject;
			arrayToXML(pw, l.toArray(), objectName);
		} else if (theObject instanceof Map<?, ?>) {
			dictionaryToXML(pw, (Map<?, ?>) theObject);
		} else {
			throw new IllegalArgumentException("Do not know how to serialize ["
					+ objectName + "] of type:" + theObject.getClass().toString());
		}

		pw.println();
	}

	/**
	 * Converts the given array to XML.
	 * 
	 * @param pw
	 *            where to write the result
	 * @param o
	 *            the array to convert
	 * @param keyName
	 *            the name of this key - used for diagnostics only.
	 */
	private void arrayToXML(PrintWriter pw, Object o, String keyName) {

		pw.println("<array>");
		indentLevel++;

		for (int i = 0; i < Array.getLength(o); i++) {
			objectToXML(pw, Array.get(o, i), keyName + "[" + i + "]");
		}

		indentLevel--;
		pw.print(getIndent() + "</array>");
	}

	/**
	 * Helper method to generate the indent string.
	 * 
	 * @return a string of spaces.
	 */
	private String getIndent() {

		char[] indentChars = new char[indentLevel * indentSize];
		Arrays.fill(indentChars, ' ');
		return new String(indentChars);
	}

	/**
	 * Helper method to escape any XML special characters in the given string.
	 * 
	 * @param value
	 * @return
	 */
	private static String escapeXMLChars(String value) {
		if (value != null) {
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			// to prevent double escaping, only escape & if it's not already followed by any other escaped characters
			value = value.replaceAll("&(?!lt;)(?!gt;)(?!amp;)(?!quot;)(?!pos;)", "&amp;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("'", "&apos;");
		}
		return value;
	}
	
	/**
	 * Utility function for extracting a child map from the parent map. This function is used
	 * for "walking" the parent map 
	 * 
	 * @param key: The name of the child map to extract
	 * @param parentMap: The parent map to extract key from
	 * @return The child map or null if key could not be found in parentMap.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getChildMapFromMap(String key, Map<String, Object> parentMap) {
		Map<String, Object> child = null;
		if (parentMap.containsKey(key)) {
			child = (Map<String, Object>) parentMap.get(key);
		}
		return child;
	}

}
