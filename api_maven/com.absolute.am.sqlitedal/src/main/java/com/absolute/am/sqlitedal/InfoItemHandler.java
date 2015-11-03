/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.sqlitedal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.absolute.am.dal.IInfoItemHandler;

public class InfoItemHandler implements IInfoItemHandler{

	private DatabaseHelper m_databaseHelper;
	
	// These values relate to custom_field_definitions.DeviceType but the related enum does not seem to be defined anywhere
	private final String DESKTOP_ENUM = "1",
			MOBILE_ENUM = "2",
			MANUAL_FIELD_TYPE_ENUM = "2";
	
	public InfoItemHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
		m_databaseHelper = new DatabaseHelper(runtimeProperties);
	}
	
	@Override
	// Retrieve and return the custom field data ( pair of id + name )
	// forMobile specifies whether only the mobile or only the desktop info items should be returned
	public ArrayList<Map<String, String>> getCustomInfoItemInfo(boolean forMobile) throws Exception{

		ArrayList<Map<String, String>> customInfoItems = new ArrayList<Map<String, String>>();
		String platformEnum = MOBILE_ENUM;
		
		if(!forMobile){
			platformEnum = DESKTOP_ENUM;
		}
		
		Connection connection = null;
		Statement statement = null;
		java.sql.ResultSet resultSet = null;
		try {
			connection = m_databaseHelper.connectToDatabase("custom");
			statement = connection.createStatement();
			statement.setQueryTimeout(30);

			String selectStmt = "SELECT id, Name, DataType, DisplayType, EnumerationList FROM custom_field_definitions WHERE DeviceType=" + platformEnum + " AND EvaluationMethod=" + MANUAL_FIELD_TYPE_ENUM;
			
			// execute the query
			resultSet = statement.executeQuery(selectStmt);
			while (resultSet.next()) {
				Map<String, String> customItem = new HashMap<String, String>();
				customItem.put("id", resultSet.getString(1));
				customItem.put("name", resultSet.getString(2));
				customItem.put("dataType", resultSet.getString(3));
				customItem.put("displayType", resultSet.getString(4));
				byte[] enumListBytes = resultSet.getBytes(5);
				
				if( enumListBytes != null){
					customItem.put("enumerationList", new String(enumListBytes));
				}
				
				customInfoItems.add(customItem);
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
		
		return customInfoItems;
	}
}
