package com.absolute.localization.helpers;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.absolute.util.PropertyList;

public class Helpers {

	public final static int RETURN_SUCCESS = 0, 
			RETURN_PARTIAL_SUCCESS = 1, 
			RETURN_FAILURE = 100;
			
	private final static String[] EXCLUDED_TABLES_FOR_INFO_ITEMS = {"admin_agents", "admin_mobile_devices", "ds_users", "ds_groups", "ds_users_groups"};
	
	// Returns a list of all the GUIDs for the rootTableName and all the tables associated with it based on the rules outlined in addAllRelatedTraversalTables()
	public static ArrayList<String> getSmartPolicyFilterItems(HashMap<String, Object> itemDefinitions, String relationsFilePath) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
		
		PropertyList dbRelations = PropertyList.fromInputStream(new FileInputStream(relationsFilePath));
		ArrayList<String> tablesRelatedToRoot = new ArrayList<String>(), result = new ArrayList<String>();
		
		addAllRelatedTraversalTables(dbRelations, tablesRelatedToRoot, "iphone_info", PropertyList.getElementAsString(dbRelations, "iphone_info/TableClass"));
		
		for (Map.Entry<String, Object> item : itemDefinitions.entrySet()) {
		    @SuppressWarnings("unchecked")
			Map<String, Object> infoItemProperties = (Map<String, Object>) item.getValue();
		    
		    if(tablesRelatedToRoot.contains(infoItemProperties.get("DB_TableName")) &&
		    		!(Boolean)infoItemProperties.get("Ignore") ){
		    	result.add(item.getKey());
		    }
		}
		
		return result;
	}
	
	// Traverse through the dbRelations to find all the tables related to tableRelatedToRoot that also have the same tableClass
	private static void addAllRelatedTraversalTables(PropertyList dbRelations, ArrayList<String> tablesRelatedToRoot, String tableRelatedToRoot, String tableClassRelatedToRoot){
		
		tablesRelatedToRoot.add(tableRelatedToRoot);
		String[] firstTablesRelatedToRoot = PropertyList.getElementAsMap(dbRelations, tableRelatedToRoot + "/Relations").keySet().toArray(new String[0]);
		Map<String, Object> fixedRelationPathsTablesForRootTableMap = PropertyList.getElementAsMap(dbRelations, tableRelatedToRoot + "/FixedRelationPaths");
		
		for(int i = 0; i < firstTablesRelatedToRoot.length; i++){
			String tableClass = PropertyList.getElementAsString(dbRelations, firstTablesRelatedToRoot[i] + "/TableClass");
			
			// Only add the table if it has a defined TableClass, the TableClass is equal to the root table's or "Other", 
			// the table name isn't listed in EXCLUDED_TABLES_FOR_INFO_ITEMS and it hasn't already been added
			if( tableClass != null && ( tableClass.equals(tableClassRelatedToRoot) || tableClass.equals("Other") ) && 
					!tablesRelatedToRoot.contains(firstTablesRelatedToRoot[i]) && !Arrays.asList(EXCLUDED_TABLES_FOR_INFO_ITEMS).contains(firstTablesRelatedToRoot[i])){
				addAllRelatedTraversalTables(dbRelations, tablesRelatedToRoot, firstTablesRelatedToRoot[i], tableClassRelatedToRoot);
			}
		}
		
		if( fixedRelationPathsTablesForRootTableMap != null){
			String[] fixedRelationPathsTablesForRootTable = fixedRelationPathsTablesForRootTableMap.keySet().toArray(new String[0]);
			
			for(int i = 0; i < fixedRelationPathsTablesForRootTable.length; i++){
				if(!tablesRelatedToRoot.contains(fixedRelationPathsTablesForRootTable[i])){
					addAllRelatedTraversalTables(dbRelations, tablesRelatedToRoot, fixedRelationPathsTablesForRootTable[i], tableClassRelatedToRoot);
				}
			}
		}
	}
	
	/**
	 * Takes <dict> node and parses it to create a Map object.
	 * 
	 * @param node
	 * @return
	 */
	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	public static Map<String, Object> parseMapFromDict(Node node) {
		if (node.getNodeName().equals("dict")) {
			NodeList children = node.getChildNodes();
			Map<String, Object> map = new HashMap<String, Object>(
					children.getLength() / 2);
			int index = 0;
			while (children.item(index) != null) {
				try {
					Node keyNode = children.item(index);
					if (keyNode != null && keyNode.getNodeName().equals("key")) {
						String key = keyNode.getFirstChild().getNodeValue();
						Object value = getObjectFromNode(children
								.item(index + 1));
						if (value != null) {
							map.put(key, value);
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				index = index + 2;
			}
			return map;
		} else {
			return null;
		}
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	public static Element getPList(String plist) {

		Element rootEle = getRootElementFromString(plist);
		if (rootEle != null && rootEle.getTagName().equals("plist")) {
			return rootEle;
		}

		return null;
	}

	// naming with 2 to distinguish from the function in
	// com.absolute.util.FileUtilities - code is different
	public static String loadResourceFileAsString2(String path)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));

		return new String(encoded, "UTF8");
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	public static String buildXml(Map<String, Object> dict) {

		StringBuilder xml = new StringBuilder();
		xml.append(XMLHelper.XMLHeader() + "\n");
		xml.append(XMLHelper.startPList() + "\n");
		xml.append(buildDict(dict, "") + "\n");
		xml.append(XMLHelper.endPList() + "\n");
		return xml.toString();
	}

	/**
	 * Saves a string to a UTF8-encoded text file.
	 * 
	 * @param contents
	 *            - string to be saved
	 * @param filePath
	 *            - full path of the file to be saved
	 * @return
	 */
	public static void saveStringToFile(String contents, String filePath) {

		try {
			File fileDir = new File(filePath);

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileDir), "UTF8"));

			// out.append(contents).append("\n");
			out.append(contents);

			out.flush();
			out.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Determines if input string can be parsed to an integer.
	 * 
	 * @param s
	 *            - input string
	 * @return true if input string can parsed to an int
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns 2-letter lowercase language code from the file name. Example:
	 * returns "es" from "InfoItemEnumerations_es.properties"
	 * 
	 * @param fileName
	 *            - file name
	 * @return language code
	 */
	public static String getLanguageFromFileName(String fileName) {
		int i = fileName.lastIndexOf('.');
		String lang = "";
		if (i > 0) {
			lang = fileName.substring(0, i);
		}
		i = lang.lastIndexOf('_');
		if (i > 0) {
			lang = lang.substring(i + 1);
			if (lang.length() != 2) {
				lang = "";
			}
		} else {
			lang = "";
		}
		if ("" == lang) {
			lang = "en";
		}
		return lang;
	}

	/**
	 * Escapes delimiter characters in property key.
	 * 
	 * @param key
	 * @return escaped key
	 */
	public static String escapeKey(String key) {
		if (key != null) {
			key = key.replaceAll(" ", "\\\\ ");
			key = key.replaceAll(":", "\\\\:"); // unlikely
			key = key.replaceAll("=", "\\\\="); // unlikely
		}
		return key;
	}

	/**
	 * Converts a node into the corresponding java object. If the object is not
	 * recognized it returns a String object containing the nodes content.
	 * 
	 * @param node
	 * @return
	 */
	// GE: code borrowed from com.absoluteapps.mdm - with changes!
	private static Object getObjectFromNode(Node node) {

		Object ret = null;
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

				ret = stringToDate(dateStr);
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
			ret = parseMapFromDict(node);

		} else if (nodeName.equals("true")) {
			ret = true;
		} else if (nodeName.equals("false")) {
			ret = false;
		} else if (nodeName.equals("array")) {
			NodeList list = node.getChildNodes();
			List<Object> array = new ArrayList<Object>(list.getLength());
			String lastValidPair = ""; // to store last successfully processed
										// node
			for (int i = 0; i < list.getLength(); i++) {
				Object obj = null;
				try {
					obj = getObjectFromNode(list.item(i));
				} catch (NullPointerException e) {
					String excMessage = "ERROR: possibly invalid plist.";
					if (!lastValidPair.isEmpty()) {
						excMessage += "\nHint: check plist after the following key-value pair:\n"
								+ lastValidPair;
					}
					throw new NullPointerException(excMessage);
				}
				lastValidPair = obj.toString();
				if (obj != null)
					array.add(obj);
			}

			ret = array;
		} else {
			ret = new String(node.getFirstChild().getNodeValue());
		}
		return ret;
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static Date stringToDate(String dateStr) {
		// SimpleDateFormat class doesn't respect 'Z' as the symbol for GMT
		// timezone.
		dateStr = dateStr.replaceFirst("Z$", "GMT");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		try {
			Date temp = df.parse(dateStr);
			return new Date(temp.getTime());
		} catch (ParseException e) {
			// print e?
			return null;
		}
	}

	// GE: code borrowed from com.absoluteapps.mdm - with changes!
	private static Element getRootElementFromString(String plist) {

		if (plist != null && plist.length() > 0) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setCoalescing(true);
			dbf.setIgnoringComments(true);
			DocumentBuilder db = null;
			Document dom;
			try {
				db = dbf.newDocumentBuilder();
				byte[] plistBytes = plist.getBytes("UTF-8");
				ByteArrayInputStream bais = new ByteArrayInputStream(plistBytes);
				dom = db.parse(bais);
			} catch (Exception e) {
				// print e?
				return null;
			}

			Element rootEle = dom.getDocumentElement();
			if (rootEle != null) {
				removeEmptyTextNodes(rootEle);
			}
			return rootEle;
		}

		return null;
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static void removeEmptyTextNodes(Node node) {

		if (node.getNodeName().equalsIgnoreCase("string")) {
			return;
		}

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

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static String buildDict(Map<String, Object> dict, String indent) {

		indent = (indent == null) ? "" : indent;
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHelper.startDict() + "\n");

		String[] keys = dict.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (String key : keys) {
			Object obj = dict.get(key);
			if (obj != null) {
				xml.append(indent);
				xml.append("\t" + XMLHelper.keyElement(key) + "\n");
				xml.append(indent);
				xml.append("\t" + buildObject(obj, indent + "\t") + "\n");
			}
		}
		xml.append(indent);
		xml.append(XMLHelper.endDict());

		return xml.toString();
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static String buildArray(List<?> array, String indent) {

		StringBuilder xml = new StringBuilder();
		if (array.size() > 0) {
			if (array.get(0) instanceof String) {
				return buildStrArray(ConvertToStrArray(array), indent);
			}

			xml.append(XMLHelper.startArray() + "\n");
			for (Object str : array) {
				if (str != null) {
					xml.append(indent);
					xml.append("\t" + buildObject(str, indent + "\t") + "\n");
				}
			}
			xml.append(indent);
			xml.append(XMLHelper.endArray());
		} else {
			xml.append(XMLHelper.emptyArray());
		}

		return xml.toString();
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static List<String> ConvertToStrArray(List<?> array) {

		List<String> res = new ArrayList<String>();
		for (Object o : array) {
			res.add(o.toString());
		}
		return res;
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static String buildStrArray(List<String> array, String indent) {

		StringBuilder xml = new StringBuilder();
		if (array.size() > 0) {
			xml.append(XMLHelper.startArray() + "\n");
			for (String str : array) {
				if (str != null) {
					xml.append(indent);
					xml.append("\t<string>" + str + "</string>\t\n");
				}
			}
			xml.append(indent);
			xml.append(XMLHelper.endArray());
		} else {
			xml.append(XMLHelper.emptyArray());
		}

		return xml.toString();
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	@SuppressWarnings("unchecked")
	private static String buildObject(Object obj, String indent) {

		StringBuilder xml = new StringBuilder();
		if (obj.getClass().equals(Integer.class)
				|| obj.getClass().equals(Long.class)) {
			xml.append(XMLHelper.integerElement(obj.toString()));
		} else if (obj.getClass().equals(Double.class)
				|| obj.getClass().equals(Float.class)) {
			xml.append(XMLHelper.realElement(obj.toString()));
		} else if (obj.getClass().equals(Boolean.class)) {
			xml.append(XMLHelper.booleanElement((Boolean) obj));
		} else if (obj.getClass().equals(Date.class)) {
			xml.append(buildDate((Date) obj));
		} else if (obj.getClass().equals(String.class)) {
			xml.append(XMLHelper.stringElement((String) obj));
		} else if (obj instanceof Map<?, ?>) {
			xml.append(buildDict((Map<String, Object>) obj, indent));
		} else if (obj instanceof List<?>) {
			try {
				xml.append(buildArray((List<?>) obj, indent));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			xml.append(XMLHelper.stringElement(obj.toString()));
		}

		return xml.toString();
	}

	// GE: code borrowed from com.absoluteapps.mdm with cosmetic changes
	private static String buildDate(Date date) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return XMLHelper.dateElement(df.format(date));
	}

}
