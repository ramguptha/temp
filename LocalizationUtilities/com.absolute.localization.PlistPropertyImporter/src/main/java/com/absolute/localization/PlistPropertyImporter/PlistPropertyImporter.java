package com.absolute.localization.PlistPropertyImporter;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.absolute.localization.helpers.Helpers;

public class PlistPropertyImporter {

	/**
	 * Takes InfoItemEnumerations_<language>.properties, ErrorMessages_<language>.properties InformationItems_<language>.properties file (InfoItemEnumerations.properties for English) as an input,
	 * reads all key-value property pairs and populates values in the existing InfoItemEnumerations.plist file.
	 * Also updates Language value in the plist file according to the _<language> suffix of the properties file name.
	 * @param args 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws Exception {
			
		//example of command line:
		//java -jar ImportPropertyResources.jar PropertiesFilePath="D:\\InfoItemEnumerations_es.properties" PlistTemplateFilePath="D:\\InfoItemEnumerations.plist PlistOutputFilePath="D:\\InfoItemEnumerations_imported.plist"
		String commandLineExample = "java -jar ImportPropertyResources.jar PropertiesFilePath=\"D:\\\\InfoItemEnumerations_es.properties\" " +
				"PlistTemplateFilePath=\"D:\\\\InfoItemEnumerations.plist\" PlistOutputFilePath=\"D:\\\\InfoItemEnumerations_imported.plist\"";
		if (args.length != 3) {
			System.err.println("ERROR: Incorrect number of parameters!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		
		String plistTemplateFilePath = null;
		String propertiesFilePath = null;
		String plistOutputFilePath = null;
	
		for (String param : args) {
			String[] tokens = param.split("=");
			if (tokens.length != 2) {
				System.err.println("ERROR: Incorrect parameter!");
				System.err.println("Example of command line:");
				System.err.println(commandLineExample);
	            System.exit(Helpers.RETURN_FAILURE);
			}
	
			if (tokens[0].equalsIgnoreCase("PlistTemplateFilePath")) {
				plistTemplateFilePath = tokens[1];
			} else if (tokens[0].equalsIgnoreCase("PropertiesFilePath")) {
				propertiesFilePath = tokens[1];
			} else if (tokens[0].equalsIgnoreCase("PlistOutputFilePath")) {
				plistOutputFilePath = tokens[1];
			} else {
				System.err.println("ERROR: Unexpected parameter: " + tokens[0] + " !");
				System.err.println("Example of command line:");
				System.err.println(commandLineExample);
	            System.exit(Helpers.RETURN_FAILURE);
			}
		}
	
		if (plistTemplateFilePath == null || plistTemplateFilePath.length() == 0) {
			System.err.println("ERROR: Missing parameter: PlistTemplateFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		if (!plistTemplateFilePath.endsWith(".plist") && !plistTemplateFilePath.endsWith(".xml")) {
			System.err.println("ERROR: Invalid parameter: PlistTemplateFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		if (propertiesFilePath == null || propertiesFilePath.length() == 0) {
			System.err.println("ERROR: Missing parameter: PropertiesFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		if (!propertiesFilePath.endsWith(".properties")) {
			System.err.println("ERROR: Invalid parameter: PropertiesFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		if (plistOutputFilePath == null || plistOutputFilePath.length() == 0) {
			System.err.println("ERROR: Missing parameter: PlistOutputFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		if (!plistOutputFilePath.endsWith(".plist") && !plistOutputFilePath.endsWith(".xml")) {
			System.err.println("ERROR: Invalid parameter: PlistOutputFilePath!");
			System.err.println("Example of command line:");
			System.err.println(commandLineExample);
	        System.exit(Helpers.RETURN_FAILURE);
		}
		
		File templateFile = new File(plistTemplateFilePath);
		if (!templateFile.isFile()) {
			System.err.println("ERROR: Template plist file does not exist: " + plistTemplateFilePath);
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		File propertiesFile = new File(propertiesFilePath);
		if (!propertiesFile.isFile()) {
			System.err.println("ERROR: Properties file does not exist: " + propertiesFilePath);
			System.exit(Helpers.RETURN_FAILURE);
		}
		String propertiesFileName = propertiesFile.getName();
		String lang = Helpers.getLanguageFromFileName(propertiesFileName);
	
		String templatePlist = Helpers.loadResourceFileAsString2(plistTemplateFilePath);
		
		Element rootElem = Helpers.getPList(templatePlist);
		if (rootElem == null) {
			System.exit(Helpers.RETURN_FAILURE);
		}
		Node dict = rootElem.getFirstChild();
		if(dict != null && !dict.getNodeName().equalsIgnoreCase("dict")) {
			System.err.println("ERROR: Invalid plist [" + plistTemplateFilePath + "]: Expecting root element's first child to be <dict>");
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		Properties propFile = new Properties();
	
		Map<String, Object> retMap = Helpers.parseMapFromDict(dict), retMap_new = new HashMap<String, Object>();
	
		try {
			int plistVersion = Integer.parseInt(retMap.get("Version").toString());
			retMap_new.put("Version", plistVersion);
		} catch (Exception e) {
			System.out.println("WARNING: Invalid Version in the template plist [" + plistTemplateFilePath + "]");
		}
		
		int returnCode = Helpers.RETURN_FAILURE;
		try {
			propFile.load(new InputStreamReader(new FileInputStream(propertiesFilePath), "UTF8"));
							
			if ( retMap.get("Enumerations") != null ){
				retMap_new.put("Language", lang);
				returnCode = handleEnums((HashMap<String, Object>)retMap.get("Enumerations"), propFile, retMap_new);
			} else if ( retMap.get("ItemDefinitions") != null ){
				retMap_new = retMap;
				retMap_new.put("Language", lang);
				returnCode = handleInfoItems((HashMap<String, Object>)retMap.get("ItemDefinitions"), propFile, retMap_new);
			} else {
				retMap_new = retMap;
				retMap_new.put("Language", lang);
				returnCode = handleErrorMessages((ArrayList<HashMap<String, Object>>)retMap.get("Values"), propFile, retMap_new);
			}
			
			String newPlist = Helpers.buildXml(retMap_new);
			Helpers.saveStringToFile(newPlist, plistOutputFilePath);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.exit(returnCode);
	}
	
	// Handle importing of error messages
	private static int handleErrorMessages(ArrayList<HashMap<String, Object>> items, Properties propFile, Map<String, Object> retMap_new){
		int importedStringCount = 0, notImportedStringCount = 0;
		String key, propertyValue;
		
		for(int i=0; i<items.size(); i++) {
			HashMap<String, Object> item = (HashMap<String, Object>)items.get(i);
			
			key = item.get("key").toString();
			propertyValue = (String) propFile.get(key);
			
			if ( propertyValue != null ) {
				item.put("value", propertyValue);
				importedStringCount++;
			} else {
				System.out.println("WARNING: missing value for the key [" + key + "]");
				notImportedStringCount++;
			}
		}

		System.out.println("DONE");
		System.out.println("Number of values imported: " + importedStringCount);
		System.out.println("Number of missing values: " + notImportedStringCount);
		
		return notImportedStringCount == 0 ? Helpers.RETURN_SUCCESS : Helpers.RETURN_PARTIAL_SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
	// Handle importing of info items
	private static int handleInfoItems(HashMap<String, Object> items, Properties propFile, Map<String, Object> retMap_new){
		int importedStringCount = 0, notImportedStringCount = 0;
		
		for(String key : items.keySet()) {	
			HashMap<String, Object> item = (HashMap<String, Object>)items.get(key);
			
			String propertyKey = key + "." + "DisplayName";
			
			//retrieve value by key from properties file
			String propertyValue = (String) propFile.get(propertyKey);
			
			if ( propertyValue != null ) {
				item.put("DisplayName", propertyValue);
				importedStringCount++;
			} else if(item.containsKey("DisplayName")){
				System.out.println("WARNING: missing value for the key [" + propertyKey + "]");
				notImportedStringCount++;
			}
		}
		
		retMap_new.put("ItemDefinitions", items);

		System.out.println("DONE");
		System.out.println("Number of values imported: " + importedStringCount);
		System.out.println("Number of missing values: " + notImportedStringCount);
		
		return notImportedStringCount == 0 ? Helpers.RETURN_SUCCESS : Helpers.RETURN_PARTIAL_SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
	// Handle importing of enumerations
	private static int handleEnums(HashMap<String, Object> items, Properties propFile, Map<String, Object> retMap_new){
		int importedStringCount = 0, notImportedStringCount = 0;
		HashMap<String, Object> items_new = new HashMap<String, Object>();
		
		for(String key : items.keySet()) {
			HashMap<String, Object> item = (HashMap<String, Object>)items.get(key);
			HashMap<String, Object> item_new = new HashMap<String, Object>();
			item_new.put("Description", item.get("Description"));
			
			ArrayList<HashMap<String, Object>> itemValues = (ArrayList<HashMap<String, Object>>)item.get("Values");
			ArrayList<HashMap<String, Object>> itemValues_new = new ArrayList<HashMap<String, Object>>();
			
			//if any of the keys in current node is a string -
			//all keys must be of "string" type
			boolean isIntegerKeyNode = true;
			
			for(int i=0; i<itemValues.size(); i++) {
				HashMap<String, Object> valuesDictProperties = itemValues.get(i);
				HashMap<String, Object> valuesDictPlist = new HashMap<String, Object>(); 
				String subKey = valuesDictProperties.get("key").toString();
				String propertyKey = key + "." + subKey;		//example: enum_ATADeviceType.1
			
				//retrieve value by key from properties file
				String propertyValue = propFile.getProperty(propertyKey);
				
				//if found - update value in the HashMap
				if (null != propertyValue && !propertyValue.isEmpty()) {
					if(propertyValue.contains("Norwegian - Norway (Bokm")) {
						propertyValue = "Norwegian - Norway (BokmÃ¯Â¿Â½l)";
					}
					
					if (isIntegerKeyNode && Helpers.isInteger(subKey)) {
						int subKeyInt = Integer.parseInt(subKey);
						valuesDictPlist.put("key", subKeyInt);
					} else {
						valuesDictPlist.put("key", subKey);
						isIntegerKeyNode = false;
					}
					valuesDictPlist.put("value", propertyValue);
					importedStringCount++;
				} else {
					System.out.println("WARNING: missing value for the key [" + propertyKey + "]");
					notImportedStringCount++;
					if (isIntegerKeyNode && Helpers.isInteger(subKey)) {
						int subKeyInt = Integer.parseInt(subKey);
						valuesDictPlist.put("key", subKeyInt);
					} else {
						valuesDictPlist.put("key", subKey);
						isIntegerKeyNode = false;
					}
					valuesDictPlist.put("value", valuesDictProperties.get("value"));
				}
				itemValues_new.add(valuesDictPlist);
			}
			item_new.put("Values", itemValues_new);
			items_new.put(key, item_new);
		}
		
		retMap_new.put("Enumerations", items_new);
		
		System.out.println("DONE");
		System.out.println("Number of values imported: " + importedStringCount);
		System.out.println("Number of missing values: " + notImportedStringCount);
		
		return notImportedStringCount == 0 ? Helpers.RETURN_SUCCESS : Helpers.RETURN_PARTIAL_SUCCESS;
	}

}
