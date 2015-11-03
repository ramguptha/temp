package com.absolute.localization.PlistPropertyExporter;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.absolute.localization.helpers.Helpers;

public class PlistPropertyExporter {

	/**
	 * Takes InfoItemEnumerations.plist, ErrorMessages.plist or InformationItems.plist file as an input,
	 * extracts all key-value property pairs and creates a properties file with that data.
	 * Each Enumeration is prepended with Description string from the input plist.
	 * Example of output:
	 * # Icon names for status column in agent_availability table
	 * enum_agent_availability_icon.0=machine_not_available
	 * enum_agent_availability_icon.1=machine_available
	 * ...
	 * @param args 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws Exception {
		
		//example of command line:
		//java -jar ExportPlistResources.jar PlistFilePath="D:\\InfoItemEnumerations.plist" PropertiesFilePath="D:\\InfoItemEnumerations.properties"
		String commandLineExample = "java -jar ExportPlistResources.jar PlistFilePath=\"D:\\\\InfoItemEnumerations.plist\" PropertiesFilePath=\"D:\\\\InfoItemEnumerations.properties\" RelationsFilePath=\"D:\\\\DatabaseRelations.properties\"";
		if (args.length != 2 && args.length != 3) {
			System.err.println("\nERROR: Incorrect number of parameters!");
			System.err.println("\nExample of command line:\n");
			System.err.println(commandLineExample);
            System.exit(Helpers.RETURN_FAILURE);
		}
		
		String plistFilePath = null, propertiesFilePath = null, relationsFilePath = null;
		
		for (String param : args) {
			String[] tokens = param.split("=");
			if (tokens.length != 2) {
				System.err.println("\nERROR: Incorrect parameter!");
				System.err.println("\nExample of command line:\n");
				System.err.println(commandLineExample);
	            System.exit(Helpers.RETURN_FAILURE);
			}

			if (tokens[0].equalsIgnoreCase("PlistFilePath")) {
				plistFilePath = tokens[1];
			} else if (tokens[0].equalsIgnoreCase("PropertiesFilePath")) {
				propertiesFilePath = tokens[1];
			} else if (tokens[0].equalsIgnoreCase("RelationsFilePath")) {
				relationsFilePath = tokens[1];
			} else {
				System.err.println("\nERROR: Unexpected parameter: " + tokens[0] + " !");
				System.err.println("\nExample of command line:\n");
				System.err.println(commandLineExample);
	            System.exit(Helpers.RETURN_FAILURE);
			}
		}

		if (plistFilePath == null || plistFilePath.length() < 7) {
			System.err.println("\nERROR: Missing or bad parameter: PlistFilePath!");
			System.err.println("\nExample of command line:\n");
			System.err.println(commandLineExample);
            System.exit(Helpers.RETURN_FAILURE);
		}
		
		if (!plistFilePath.endsWith(".plist") && !plistFilePath.endsWith(".xml")) {
			System.err.println("\nERROR: incorrect PlistFilePath!");
			System.err.println("\nPlistFilePath must be a .plist file.");
            System.exit(Helpers.RETURN_FAILURE);
		}
		
		if (propertiesFilePath == null || propertiesFilePath.length() < 12) {
			System.err.println("\nERROR: Missing or bad parameter: PropertiesFilePath!");
			System.err.println("\nExample of command line:\n");
			System.err.println(commandLineExample);
            System.exit(Helpers.RETURN_FAILURE);
		}
		
		if (!propertiesFilePath.endsWith(".properties")) {
			System.err.println("\nERROR: incorrect PropertiesFilePath!");
			System.err.println("\nPropertiesFilePath must be a .properties file.");
            System.exit(Helpers.RETURN_FAILURE);
		}
		
		String plist = Helpers.loadResourceFileAsString2(plistFilePath);
				
		Element rootElem = Helpers.getPList(plist);
		if (rootElem == null) {
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		Node dict = rootElem.getFirstChild();
		
		if(dict != null && !dict.getNodeName().equalsIgnoreCase("dict")) {
			System.err.println("\nInvalid plist [" + plistFilePath + "]:");
			System.err.println("Expecting root element's first child to be <dict>");
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		int returnCode = Helpers.RETURN_FAILURE;
		try {
			Map<String, Object> retMap = Helpers.parseMapFromDict(dict);
			//create resulting properties file
			String propertiesFileName = propertiesFilePath;
			
			//not using FileWriter because we want UTF8-encoded file		
			File propFile = new File(propertiesFileName);
			Writer propertiesFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propFile), "UTF8"));
			
			String newLine = System.getProperty("line.separator");
			
			// If enumerations aren't found then assume that we're working with an InformationItems file
			if ( retMap.get("Enumerations") != null ){
				returnCode = handleEnums((HashMap<String, Object>)retMap.get("Enumerations"), newLine, propertiesFile);
			} else if ( retMap.get("ItemDefinitions") != null ){
				// DB relations are provided so we can filter the item definitions to include only the relevant smart policy filter items
				returnCode = handleInfoItems((HashMap<String, Object>)retMap.get("ItemDefinitions"), newLine, propertiesFile, relationsFilePath);
			} else {
				returnCode = handleErrorMessages((ArrayList<HashMap<String, Object>>)retMap.get("Values"), newLine, propertiesFile);
			}
							
			propertiesFile.flush();
			propertiesFile.close();
			
			System.out.println("File created: " + propertiesFileName);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.exit(returnCode);
	}
	
	// Handle exporting of info items
	private static int handleErrorMessages(ArrayList<HashMap<String, Object>> items, String newLine, Writer propertiesFile) throws IOException{
		String value, key;
		
		propertiesFile.append("#do not delete this line" + newLine);
				
		for(int i=0; i<items.size(); i++) {
			HashMap<String, Object> valuesDict = items.get(i);
			
			value = valuesDict.get("value").toString();
			key = valuesDict.get("key").toString();
			
			propertiesFile.append(key + "=" + value.replace("\n", "\\n\\\n") + newLine);
		}
		
		return Helpers.RETURN_SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
	// Handle exporting of info items
	private static int handleInfoItems(HashMap<String, Object> items, String newLine, Writer propertiesFile, String relationsFilePath) throws IOException, ParserConfigurationException, SAXException{
		int propertyStringCount = 0;
		ArrayList<String> smartPolicyFilterItems = null;
		String itemDescription, propertyKey;
		Object objItemDescription, propertyValue;
		boolean filterItems = false;
		
		if(relationsFilePath != null){		
			filterItems = true;
			
			smartPolicyFilterItems = Helpers.getSmartPolicyFilterItems(items, relationsFilePath);
		}
		
		for(String key : items.keySet()) {
			
			if( filterItems && !smartPolicyFilterItems.contains(key) ){
				System.out.println("\nSkipping " + key + " since it's not a smart policy filter item.");
				continue;
			}
			
			HashMap<String, Object> item = (HashMap<String, Object>)items.get(key);
			
			//add description in comments
			objItemDescription = item.get("Description");
			
			if( objItemDescription == null ){
				itemDescription = "#" + newLine;
			} else {
				itemDescription =  "# " + objItemDescription.toString().replace("\n", " ") + newLine;
			}
			
			propertiesFile.append(itemDescription);
			
			propertyValue = item.get("DisplayName");
			
			if( propertyValue != null ){
				propertyKey = key + "." + "DisplayName";
				propertyKey = Helpers.escapeKey(propertyKey);
				propertyValue = item.get("DisplayName");
				
				propertiesFile.append(propertyKey + "=" + propertyValue.toString().replace("\n", "\\n\\\n") + newLine);
				propertyStringCount++;
			}
		}
		
		System.out.println("Number of properties exported: " + propertyStringCount);
		
		return Helpers.RETURN_SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
	// Handle exporting of enumerations
	private static int handleEnums(HashMap<String, Object> items, String newLine, Writer propertiesFile) throws IOException{
		int propertyStringCount = 0;
		
		for(String key : items.keySet()) {
			HashMap<String, Object> item = (HashMap<String, Object>)items.get(key);
			
			//add description in comments
			String itemDescription =  "# " + item.get("Description") + newLine;
			propertiesFile.append(itemDescription);
			
			ArrayList<HashMap<String, Object>> itemValues = (ArrayList<HashMap<String, Object>>)item.get("Values");
			
			for(int i=0; i<itemValues.size(); i++) {
				HashMap<String, Object> valuesDict = itemValues.get(i);
				String propertyKey = key + "." + valuesDict.get("key").toString();		//example: enum_ATADeviceType.1

				propertyKey = Helpers.escapeKey(propertyKey);
				String propertyValue = valuesDict.get("value").toString();				//example: Hard Disk
				
				//special handling of incorrectly encoded strings with a non-ASCII character
				if(propertyValue.contains("Norwegian - Norway (Bokm")) {
					propertyValue = "Norwegian - Norway (BokmÃ¯Â¿Â½l)";
				}
				if(propertyValue.contains("Lao People") && propertyValue.endsWith("Democratic Republic")) {
					propertyValue = "Lao People's Democratic Republic";
				}
				
				propertiesFile.append(propertyKey + "=" + propertyValue + newLine);		//example: enum_ATADeviceType.1=Hard Disk
				propertyStringCount++;
			}
			
		}	
				
		System.out.println("Number of properties exported: " + propertyStringCount);
		
		return Helpers.RETURN_SUCCESS;
	}
}
