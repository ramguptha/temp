package com.absolute.am.dal;

import java.util.ArrayList;
import java.util.Map;

public interface IEnumHandler {

	public ArrayList<Map<String, String>> getValuesForTable(String tableName, String localeSuffix) throws Exception;
	
	public String getEnumKeyForValue (String enumTable, String value);
	public String getEnumKeyForValue (String enumTable, String value, String locale);
	
	public String getEnumValueForKey(String enumTable,	String key);
	public String getEnumValueForKey(String enumTable,	String key, String locale);
}
