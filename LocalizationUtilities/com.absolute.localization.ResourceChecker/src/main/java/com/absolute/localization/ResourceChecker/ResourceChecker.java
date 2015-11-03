package com.absolute.localization.ResourceChecker;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.absolute.localization.helpers.Helpers;

/**
 * @author ephilippov
 * 
 * A utility written for the German team that compares the keys between a resource file and its translated version, and reports
 * on any differences between the files.
 *
 */
public class ResourceChecker {

	private static String RESOURCE_FILE_PATH = "ResourceFilePath",
			TRANSLATED_RESOURCE_FILE_PATH = "TranslatedResourceFilePath";
	
	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws Exception {
			
		final String commandLineExample = "java -jar ResourceChecker.jar ResourceFilePath=\"...\" TranslatedResourceFilePath=\"...\"";
		HashMap<String, String> argsMap = new HashMap<String, String>();
		boolean found = false;
		int returnCode = Helpers.RETURN_FAILURE;
		
		argsMap.put(RESOURCE_FILE_PATH, null);
		argsMap.put(TRANSLATED_RESOURCE_FILE_PATH, null);
		
		// Retrieve the accepted arguments from the command line
		for (String param : args) {
			String[] tokens = param.split("=");
	
			for (String key : argsMap.keySet()) {
				if (tokens[0].equalsIgnoreCase(key)) {
					argsMap.put(key, tokens[1]);
					found = true;
					break;
				}
			}
			
			if(!found){
				System.err.println("ERROR: Unexpected parameter: " + tokens[0]);
				System.err.println("Example of command line:");
				System.err.println(commandLineExample);
	            System.exit(Helpers.RETURN_FAILURE);
			} else {
				found = false;
			}
		}
		
		// Make sure all the arguments were set
		for (String key : argsMap.keySet()) { 
			if( argsMap.get(key) == null || argsMap.get(key).length() == 0 ) {
				System.err.println("ERROR: Missing parameter: " + key);
				System.err.println("Example of command line:");
				System.err.println(commandLineExample);
		        System.exit(Helpers.RETURN_FAILURE);
			}
		}
	
		Map<String, Object> resourceMap = getResourceMap(argsMap.get(RESOURCE_FILE_PATH)),
				resourceMap2 = getResourceMap(argsMap.get(TRANSLATED_RESOURCE_FILE_PATH));
		
		try {
			// Specific case for InformationItems.xml since we have multiple .plist roots there
			if( resourceMap.get("ItemDefinitions") != null ){
				returnCode = compareResources((HashMap<String, Object>)resourceMap.get("ItemDefinitions"), (HashMap<String, Object>)resourceMap2.get("ItemDefinitions"));
			} else { 
				// Generic cases
				for( String key : resourceMap.keySet()){
					if (resourceMap.get(key) instanceof Map<?,?>){ // Root element contains a map of entries
						returnCode = compareResources((HashMap<String, Object>)resourceMap.get(key), (HashMap<String, Object>)resourceMap2.get(key));
					} else if (resourceMap.get(key) instanceof ArrayList<?>){ // Root element contains a list of values
						returnCode = compareResourcesArray((ArrayList<HashMap<String, Object>>)resourceMap.get(key), (ArrayList<HashMap<String, Object>>)resourceMap2.get(key));
					}
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR:" + e.getMessage());
			e.printStackTrace();
		}
		
		System.exit(returnCode);
	}
	
	private static int compareResourcesArray(ArrayList<HashMap<String, Object>> resourceArr, ArrayList<HashMap<String, Object>> translatedResourceArr){
		HashMap<String, Object> resourceMap = new HashMap<String, Object>(), translatedResourceMap = new HashMap<String, Object>();
		
		// Put the array values in maps so that we can compare them easier
		for(HashMap<String, Object> h : resourceArr){
			resourceMap.put(h.get("key").toString(), h.get("value").toString());
		}
		for(HashMap<String, Object> h : translatedResourceArr){
			translatedResourceMap.put(h.get("key").toString(), h.get("value").toString());
		}

		return compareResources(resourceMap, translatedResourceMap);
	}
	
	private static int compareResources(HashMap<String, Object> resourceMap, HashMap<String, Object> translatedResourceMap){
		String newLine = System.getProperty("line.separator");
		HashMap<String, Set<String>> keys = doCompareResourceMaps(resourceMap, translatedResourceMap);
			
		System.out.println("Missing entries from the resource file: ");
		for(String added : keys.get("addedKeys")){
			System.out.println(added);
		}
		System.out.println("Missing entries from the translated resource file: ");
		for(String removed : keys.get("removedKeys")){
			System.out.println(removed);
		}
		System.out.println(newLine + newLine + "Number of missing entries from the resource file: " + keys.get("addedKeys").size());
		System.out.println("Number of missing entries from the translated resource file: " + keys.get("removedKeys").size());
		
		return (keys.get("addedKeys").size() == 0 && keys.get("removedKeys").size() == 0) ? Helpers.RETURN_SUCCESS : Helpers.RETURN_PARTIAL_SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, Set<String>> doCompareResourceMaps(HashMap<String, Object> resourceMap, HashMap<String, Object> translatedResourceMap){
		
		Set<String> removedKeys = new HashSet<String>(resourceMap.keySet()), addedKeys = new HashSet<String>(translatedResourceMap.keySet());
		HashMap<String, Object> itemValues = null, translatedItemValues = null;
		HashMap<String, Set<String>> keysMap = new HashMap<String, Set<String>>();
		
		removedKeys.removeAll(translatedResourceMap.keySet());
		addedKeys.removeAll(resourceMap.keySet());
		
		// This is a deeper comparison in case any of the values from a list are missing/were added
		for (String key : resourceMap.keySet()){
			if(!addedKeys.contains(key) && !removedKeys.contains(key) && resourceMap.get(key) instanceof HashMap<?,?>){
				HashMap<String, Object> item = (HashMap<String, Object>) resourceMap.get(key),
						item2 = (HashMap<String, Object>) translatedResourceMap.get(key);

				if ( item.get("Values") instanceof ArrayList<?>){
					itemValues = new HashMap<String, Object>();
					translatedItemValues = new HashMap<String, Object>();
					
					for(HashMap<String, Object> h : (ArrayList<HashMap<String, Object>>)item.get("Values")){
						itemValues.put(h.get("key").toString(), h.get("value").toString());
					}
					for(HashMap<String, Object> h : (ArrayList<HashMap<String, Object>>)item2.get("Values")){
						translatedItemValues.put(h.get("key").toString(), h.get("value").toString());
					}
					HashMap<String, Set<String>> keys = doCompareResourceMaps(itemValues, translatedItemValues);
					
					Set<String> deeperAddedKeys = keys.get("addedKeys"), deeperRemovedKeys = keys.get("removedKeys");
					// Give the added/missing keys custom names to help track them easier
					for(String s : deeperAddedKeys){
						addedKeys.add(key + ".Values." + s);
					}
					for(String s : deeperRemovedKeys){
						removedKeys.add(key + ".Values." + s);
					}
				}
			}
		}
		
		keysMap.put("addedKeys", addedKeys);
		keysMap.put("removedKeys", removedKeys);
				
		return keysMap;
	}
	
	// Validate the file paths and corresponding files that were passed in as arguments
	private static Map<String, Object> getResourceMap(String path) throws IOException{
		File resourceFile = new File(path);
		
		if (!resourceFile.isFile()) {
			System.err.println("ERROR: File does not exist: " + path);
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		Element rootElem = Helpers.getPList(Helpers.loadResourceFileAsString2(path));
		if (rootElem == null) {
			System.err.println("ERROR: The provided file ( " + path + " ) is not a valid plist.");
			System.exit(Helpers.RETURN_FAILURE);
		}
		Node dict = rootElem.getFirstChild();
		if(dict != null && !dict.getNodeName().equalsIgnoreCase("dict")) {
			System.err.println("ERROR: Invalid plist ( " + path + " ): Expecting root element's first child to be <dict>");
			System.exit(Helpers.RETURN_FAILURE);
		}
		
		return Helpers.parseMapFromDict(dict);
	}
}
