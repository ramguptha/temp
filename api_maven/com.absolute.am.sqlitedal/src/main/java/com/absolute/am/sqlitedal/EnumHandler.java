/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.sqlitedal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.dal.IEnumHandler;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class EnumHandler implements IEnumHandler{

	private DatabaseHelper m_databaseHelper;		

	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(EnumHandler.class.getName()); 

	private static final String PLIST_FILE_INFO_ITEMS_ENUMS = "InfoItemEnumerations";	
	
    private static final String DB_COLUMN_KEY = "key";
    private static final String DB_COLUMN_VALUE = "value";
    private static final String MAP_NAME = "name";
    private static final String MAP_VALUE = "value";

	private static final String INFO_ITEMS_ENUM_VALUES = "Enumerations/%1$s/Values";
	private static final String INFO_ITEMS_ENUM_VALUE = "value";
	private static final String INFO_ITEMS_ENUM_KEY = "key";
	
	private HashMap<String,PropertyList> m_infoItemsEnumsList;
	
	public EnumHandler(Properties runtimeProperties, boolean noSync) throws ParserConfigurationException, SAXException, IOException {
		
		InputStream is;
		String fileName;
		String[] supportedDbLocales = ((String)runtimeProperties.get("dbSupportedLocales")).split(",");
		m_infoItemsEnumsList = new HashMap<String,PropertyList>();
		
		if(!noSync){
			m_databaseHelper = new DatabaseHelper(runtimeProperties);
		}
		
		for(String supportedLocale : supportedDbLocales){
			if(supportedLocale.equals("en")){
				fileName = (String)runtimeProperties.get(Dal.PROP_VIEW_CONFIG_FOLDER) + "/" + PLIST_FILE_INFO_ITEMS_ENUMS +  ".xml";
			} else {
				fileName = (String)runtimeProperties.get(Dal.PROP_VIEW_CONFIG_FOLDER) + "/" + PLIST_FILE_INFO_ITEMS_ENUMS + "_" + supportedLocale + ".xml";
			}
			
			is = new FileInputStream(fileName);
			m_infoItemsEnumsList.put(supportedLocale, PropertyList.fromInputStream(is));
			is.close();
		}
	}

	@Override
	public ArrayList<Map<String, String>> getValuesForTable(String tableName, String localeSuffix)
			throws Exception {

		MDC.put("TableName", tableName);

		ArrayList<Map<String, String>> resultArray = new ArrayList<Map<String, String>>();

		Connection connection = null;
		Statement statement = null;
		java.sql.ResultSet resultSet = null;
		try {
			connection = m_databaseHelper.connectToDatabase("enum");
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec. // TODO: make this configurable.

			String selectStmt = "Select " + DB_COLUMN_KEY + ", " + DB_COLUMN_VALUE + localeSuffix + " from " + tableName;
			// attach to any logical databases needed for this query
			m_databaseHelper.AttachToLogicalDatabasesForQuery(statement, selectStmt);
			
			// execute the query
			resultSet = statement.executeQuery(selectStmt);
			while (resultSet.next()) {
				Map<String, String> resultMap = new HashMap<String, String>();
				resultMap.put(MAP_VALUE, resultSet.getString(1));
				resultMap.put(MAP_NAME, resultSet.getString(2));
				resultArray.add(resultMap);
			}
			resultSet.close();
			resultSet = null;
		} finally {
			if (resultSet != null) {
				resultSet.close();
				resultSet = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
		MDC.remove("TableName");
		return resultArray;
	}
	
	public String getEnumKeyForValue (String enumTable, String value) {
		return getEnumKeyForValue(enumTable, value, "en");
	}
	
	public String getEnumKeyForValue (String enumTable, String value, String locale) {
		String key = value;
		
		if (enumTable != null) {
			ArrayList<Map<String, Object>> enums = PropertyList.getElementAsArrayListMap(m_infoItemsEnumsList.get(locale), String.format(INFO_ITEMS_ENUM_VALUES, enumTable));
			
			if (enums != null) {
				for (int j = 0; j < enums.size(); j++) {
					Map<String, Object> enumItem = enums.get(j);
					
					if (enumItem.containsKey(INFO_ITEMS_ENUM_VALUE)) {
						if (enumItem.get(INFO_ITEMS_ENUM_VALUE).toString().compareToIgnoreCase(value) == 0) {
							key = StringUtilities.objectToString(enumItem.get(INFO_ITEMS_ENUM_KEY));
							break;
						}
					}
					
					if (key.isEmpty()) {
						key = value;
					}
				}
			}
		}
		
		return key;
	}
	
	public String getEnumValueForKey (String enumTable, String key){
		return getEnumValueForKey (enumTable, key, "en");
	}
	
	public String getEnumValueForKey (String enumTable, String key, String locale) {
		String value = key;
		
		if (enumTable != null) {
			ArrayList<Map<String, Object>> enums = PropertyList.getElementAsArrayListMap(m_infoItemsEnumsList.get(locale), String.format(INFO_ITEMS_ENUM_VALUES, enumTable));
			
			if (enums != null) {
				for (int j = 0; j < enums.size(); j++) {
					Map<String, Object> enumItem = enums.get(j);
					
					if (enumItem.containsKey(INFO_ITEMS_ENUM_KEY)) {
						if (enumItem.get(INFO_ITEMS_ENUM_KEY).toString().compareToIgnoreCase(value) == 0) {
							value = StringUtilities.objectToString(enumItem.get(INFO_ITEMS_ENUM_VALUE));
							break;
						}
					}
				}
			}
		}
		
		return value;
	}
}
