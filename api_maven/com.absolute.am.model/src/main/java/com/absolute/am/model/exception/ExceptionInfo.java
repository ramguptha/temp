package com.absolute.am.model.exception;

import java.util.LinkedHashMap;

/**
 * This is a helper class to create a map of name=value pairs on a single line. It is only
 * to be used when creating exceptions.
 * @author dlavin
 *
 */
public class ExceptionInfo extends LinkedHashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructs an ExceptionInfo object using the context[] as the members of the map. Each pair 
	 * of entries in the array are used to create a key=value pair. Here is an example:
	 *  </br>
	 *  <code>ExceptionInfo myExceptionInfo = new ExceptionInfo("key1", "value1", "key2", 27)</code>
	 *  </br>
	 * If an entry is itself an array, then that array is processed in turn, creating a key=value
	 * entry for each pair in the array.
	 * @param context a variable number of objects used in pairs to create key=value pairs in the map
	 */
	public ExceptionInfo(Object ...context) {
		if (context != null && context.length > 0) {
			this.putAll(objectArrayToMap(context));
		}
	}

	/**
	 * Internal helper method to convert an array of objects into a map of key value pairs.
	 * @param objects
	 * @return
	 */
	private static LinkedHashMap<String, Object> objectArrayToMap(Object[] objects) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i=0; i<objects.length; i++) {

			// if the object is another array, then recurse and append it's entries too
			if (objects[i].getClass().isArray()) {
				map.putAll(objectArrayToMap((Object[]) objects[i]));
			} else {
				// else insert this object as the KEY and the next object as the VALUE.
				String key = objects[i].toString();
				i++;
				Object value = null;
				if (i < objects.length) {
					value = objects[i];			
				}
				map.put(key, value);
			}
		}
		return map;
	}	
}
