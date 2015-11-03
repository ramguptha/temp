/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.sqlitedal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.dal.ViewConstants;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class ViewConfigMgr {
    private static Logger m_logger = LoggerFactory.getLogger(ViewConfigMgr.class.getName()); 
	
	private static final String VIEW_INFO_XML_TAG = "<?xml";	
	private static final String VIEW_INFO_PLIST_TAG = "<plist";	

	private static final String PLIST_FILE_INFORMATION_ITEMS = "InformationItems";	
	
	// WVD = Web View Definition
	private static final String WVD_VIEW_NAME_DEFINITION = "View Name Definition";	

	private static final String WVD_VIEW_DEFINITION = "View Definitions/%1$s";
	private static final String WVD_FILTER = "View Definitions/%1$s/Filter";
	private static final String WVD_COMPARE_VALUE_ARRAY = "View Definitions/%1$s/Filter/CompareValue";
	private static final String WVD_FILTER_OPERATOR = "View Definitions/%1$s/Filter/Operator";
	private static final String WVD_COMPARE_VALUE = "CompareValue";
	private static final String WVD_COMPARE_VALUE_USER_DEFINED = "user_defined";

	private static final String WVD_OPERATOR = "Operator";
	private static final String WVD_COLUMN_ORDER = "View Definitions/%1$s/ColumnOrder";
	private static final String WVD_ROOT_TABLE = "View Definitions/%s/RootTable";
	private static final String WVD_ADDITIONAL_JOIN_TABLE = "View Definitions/%s/AdditionalJoinTable";
	private static final String WVD_SORT_ORDER = "View Definitions/%s/SortOrder";
	private static final String WVD_SORT_ORDER_ASCENDING = "Ascending";
	private static final String WVD_SORT_ORDER_COLUMN_ID = "ColumnID";
	private static final String WVD_COLUMN_PROP_INFO_ITEM_ID = "View Definitions/%1$s/ColumnProperties/%2$s/InfoItemID";

	private static final String INFO_ITEMS_ITEM_DEFINITIONS = "ItemDefinitions/%1$s";
	private static final String INFO_ITEMS_COLUMN_NAME = "ItemDefinitions/%1$s/DB_ColumnName";
	private static final String INFO_ITEMS_TABLE_NAME = "ItemDefinitions/%1$s/DB_TableName";
	private static final String INFO_ITEMS_SUB_QUERY = "ItemDefinitions/%1$s/DB_Subquery";
	private static final String INFO_ITEMS_ENUMERATION = "ItemDefinitions/%1$s/Enumeration";
	private static final String INFO_ITEMS_COLUMN_DATA_TYPE = "ItemDefinitions/%1$s/ColumnDataType";
	private static final String INFO_ITEMS_DISPLAY_TYPE = "ItemDefinitions/%1$s/DisplayType";
	private static final String INFO_ITEMS_SEARCHABLE = "ItemDefinitions/%1$s/Searchable";
	
	private static final String DB_RELATIONS_ROOT_TBL_2ND_TBL_FOREIGN_COL = "%1$s/Relations/%2$s/ForeignKeyColumn";
	private static final String DB_RELATIONS_ROOT_TBL_2ND_TBL_FOREIGN_LIST = "%1$s/Relations/%2$s/ForeignKeyList";

	private static final String DB_RELATIONS_ROOT_TBL_2ND_TBL_REFERENCED_COL = "%1$s/Relations/%2$s/ReferencedColumn";

	private static final String DB_RELATIONS_FOREIGN_COL = "ForeignKeyColumn";
	private static final String DB_RELATIONS_REFERENCED_COL = "ReferencedColumn";
	
	private static final String DB_DEFAULT_ENUM_COL = "value_en";

	private static final String TABLE_PROP_INFO_ITEM_ID = "InfoItemID";
	
	private static final Set<String> UNSEARCHABLE_DISPLAY_TYPES = new HashSet<String>(Arrays.asList(
		new String[] {"FormatDateInterval", "FormatLongDateTime", "FormatRelativDateOnly", "FormatRelativDateTime", "FormatShortDateOnly", "FormatShortDateTime"}
	));
	
	public final static String SQLITEDAL_BASE = "sqlitedal";
	
	private static final String[] m_itemDefinitionAttributesThatArePrivate = new String[] {
		"DB_ColumnName",
		"DB_TableName",
		"DB_Subquery",
		"Ignore",
		"Status"
	};

	private static final String[] m_tableColumnsToExcludeFromSearching = new String[] {
		"iOS_Policies.UniqueID"
	};
	private Map<String, ArrayList<String>> m_excludeColumnsFromSearchMap = new HashMap<String, ArrayList<String>>();

	// this holds the view definitions as read in from WebAPI_StandardViewDefinitions.xml
	private static PropertyList m_webViewDefnList;
	// When generating the SQL for the view, the view definition can come from
	// either WebAPI_StandardViewDefinitions.xml or it can be passed in.
	// This variable holds the view definitions to work with while generating the SQL.
	private static PropertyList m_viewDefnWorkingSet;

	private static EnumHandler m_enumHandler;
	
	private static PropertyList m_infoItemsList;
	private static PropertyList m_dbRelations;
	private static PropertyList m_extendedDbRelations;
	private static PropertyList m_extendedInfoItemsList;

	private String m_viewName;
	private String m_rootTable;
	private String m_viewConfigPath;
	private HashMap<String, String> m_uiParams;
	private ArrayList<String> m_userParams;
	private int m_userParamsIndex;
	
	public ViewConfigMgr(Properties runtimeProperties, HashMap<String, PropertyList> m_plists, EnumHandler enumHandler) throws ParserConfigurationException, SAXException, IOException {
		
		m_viewConfigPath = (String)runtimeProperties.get(Dal.PROP_VIEW_CONFIG_FOLDER);
		m_logger.debug("ViewConfigMgr CTOR called: viewConfigPath = " + m_viewConfigPath);
		
		m_enumHandler = enumHandler;
		m_webViewDefnList = m_plists.get("WebViewDefnList");
		m_infoItemsList = m_plists.get("InfoItemsList");
		m_dbRelations = m_plists.get("DbRelations");
		m_extendedDbRelations = m_plists.get("ExtendedDbRelations");
		m_extendedInfoItemsList = m_plists.get("ExtendedInfoItemsList");

		// set up m_excludeColumnsFromSearchMap
		for (String excludedColInfo : m_tableColumnsToExcludeFromSearching) {
			String[] excludedColInfoTokens = excludedColInfo.split("\\.");
			if (excludedColInfoTokens.length == 2) {
				
				ArrayList<String> listOfCols = m_excludeColumnsFromSearchMap.get(excludedColInfoTokens[0]);
				if (listOfCols == null) {
					listOfCols = new ArrayList<String>();
				}
				listOfCols.add(excludedColInfoTokens[1]);
				m_excludeColumnsFromSearchMap.put(excludedColInfoTokens[0], listOfCols);

			}
		}
	}

	public String genSelectCountQuery(String sqlSelectStatement) {
		StringBuilder selectCountStatement = new StringBuilder(sqlSelectStatement);
		// The sqlSelectStatement is of the form:
		// 	SELECT columns
		// 		FROM table
		// 		LEFT JOIN ... ON...
		// 		LEFT JOIN ... ON...
		// 		WHERE ... AND (...)
		// 		ORDER BY ...
		// 		LIMIT ...
		// replace columns with count(*)
		// delete ORDER BY...
		// delete LIMIT...
		String select = "SELECT", orderBy = "ORDER BY";

		int indexOfSelectEnd = selectCountStatement.indexOf(select) + select.length();
		
		selectCountStatement.insert(indexOfSelectEnd, "\nCOUNT(*),");
		
		int indexOfOrderBy = selectCountStatement.indexOf(orderBy);
		selectCountStatement.delete(indexOfOrderBy, selectCountStatement.length());

		return selectCountStatement.toString();
	}
	
			
	public String genSqlForView(String viewInfo, HashMap<String, String> uiParams, 
			ArrayList<String> userParams, String dbLocaleSuffix) throws Exception {
		m_viewName = setUpViewDefnWorkingSet(viewInfo);
		m_uiParams = uiParams;
		m_userParams = userParams;

		// The sqlSelectStatement is of the form:
		// 	SELECT columns
		// 		FROM table
		// 		LEFT JOIN ... ON...
		// 		LEFT JOIN ... ON...
		// 		WHERE ... AND (...)
		// 		ORDER BY ...
		// 		LIMIT ...
		// If the SELECT statement format changes here then the genSelectCountQuery
		// function may need to change. It processes the SELECT statement based
		// on knowing its format. 
		
		StringBuilder sqlSelectPart = new StringBuilder();
		StringBuilder sqlFromPart = new StringBuilder();
		StringBuilder sqlJoinPart = new StringBuilder();
		StringBuilder sqlWherePart = new StringBuilder();
		StringBuilder sqlSearchPart = new StringBuilder();
		StringBuilder sqlSortPart = new StringBuilder();
		StringBuilder sqlLimitPart = new StringBuilder();
		StringBuilder sqlStatement  = new StringBuilder();
		ResourceBundle translations = ResourceBundle.getBundle(SQLITEDAL_BASE, new Locale(dbLocaleSuffix.substring(1)));
		
		if (m_viewDefnWorkingSet == null || m_infoItemsList == null || m_dbRelations == null 
				|| m_extendedInfoItemsList == null || m_extendedDbRelations == null) {
			throw new Exception("Cant generate SQL statment for view - plist file is null");
		}
		String elementName = String.format(WVD_VIEW_DEFINITION, m_viewName);
		if (!PropertyList.elementExists(m_viewDefnWorkingSet, elementName)) {
			throw new IllegalArgumentException("View does not exist: " + elementName);
		} 
		
		elementName = String.format(WVD_ROOT_TABLE, m_viewName);
		m_rootTable = PropertyList.getElementAsString(m_viewDefnWorkingSet, elementName);
		if (m_rootTable == null) {
			throw new IllegalArgumentException("View element does not exist: " + elementName);
		}
		
		String filterByAdmin = getUiParam(ViewConstants.PARAM_FILTER_BY_ADMIN);
		// If we are filtering by admin and the request comes from a mobile device table
		if (filterByAdmin != null && filterByAdmin.length() > 0 && m_rootTable.equalsIgnoreCase("iphone_info")) {
			sqlFromPart.append("\n FROM admin_iphone_info");
			sqlJoinPart.append("\n LEFT JOIN iphone_info ON iphone_info.id = admin_iphone_info.iphone_info_record_id");
			sqlWherePart.append("\n WHERE admin_iphone_info.adminUUID = '").append(filterByAdmin).append("'");

		// If we are filtering by admin and the request comes from a computer table
		} else if (filterByAdmin != null && filterByAdmin.length() > 0 && m_rootTable.equalsIgnoreCase("agent_info")) {
			sqlFromPart.append("\n FROM admin_computer_info");
			sqlJoinPart.append("\n LEFT JOIN agent_info ON agent_info.id = admin_computer_info.agent_info_record_id");
			sqlWherePart.append("\n WHERE admin_computer_info.adminUUID = '").append(filterByAdmin).append("'");
		} else {
			sqlFromPart.append("\n FROM " + m_rootTable);
		}

		sqlSelectPart.append("\nSELECT");

		elementName = String.format(WVD_FILTER, m_viewName);
		if (PropertyList.elementExists(m_viewDefnWorkingSet, elementName)) {
			StringBuilder tempWherePart = getWherePart(sqlJoinPart);
			if (sqlWherePart.length() == 0) {
				sqlWherePart.append("\n WHERE ").append(tempWherePart.toString());
			} else {
				sqlWherePart.append("\n AND (").append(tempWherePart.toString()).append(")");
			}
		}

		String top = getUiParam(ViewConstants.PARAM_TOP);
		String skip = getUiParam(ViewConstants.PARAM_SKIP);
		if (top != null ) {
			sqlLimitPart.append("\n LIMIT " + top);
		}  
		if (skip != null) {
			sqlLimitPart.append("\n OFFSET " + skip);
		}  

		ArrayList<String> columnOrder = null;
		elementName = String.format(WVD_COLUMN_ORDER, m_viewName);
		columnOrder = PropertyList.getElementAsArrayListString(m_viewDefnWorkingSet, elementName);

		if (columnOrder == null) {
			throw new IllegalArgumentException("ColumnOrder element does not exist: " + elementName);
		}
		
		// We may have to look up items in m_extendedInfoItemsList if they don't
		// exist in m_infoItemsList. Assume they are in m_infoItemsList for now.
		PropertyList infoItemsListToUse = m_infoItemsList;
		String searchText = getUiParamSearchText();
		String searchColumnId = getUiParamSearchColumn();

		for (int i = 0; columnOrder != null && i < columnOrder.size(); i++) {
			String colName = columnOrder.get(i);
			String infoItemID = PropertyList.getElementAsString(m_viewDefnWorkingSet, 
					String.format(WVD_COLUMN_PROP_INFO_ITEM_ID, m_viewName, colName));
			infoItemsListToUse = m_infoItemsList;
			MDC.put("infoItemID", infoItemID);
			String dBColName = PropertyList.getElementAsString(infoItemsListToUse, 
					String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
			// The items could be in the infoItems.xml file or the
			// extendedInfoItems.xml
			if (dBColName == null) {
				dBColName = PropertyList.getElementAsString(m_extendedInfoItemsList, 
						String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
				if (dBColName == null) {
					throw new Exception(
							String.format("DB_ColumnName for info item %1$s for view %2$s can not be found.", 
									colName, m_viewName));
				}
				infoItemsListToUse = m_extendedInfoItemsList;
			}
			String dBTableName = PropertyList.getElementAsString(infoItemsListToUse, 
					String.format(INFO_ITEMS_TABLE_NAME, infoItemID));

			if (dBTableName == null) {
				throw new Exception(
						String.format("DB_TableName for info item %1$s for view %2$s can not be found.", 
								infoItemID, m_viewName));
			}

			String enumTable = PropertyList.getElementAsString(infoItemsListToUse, 
					String.format(INFO_ITEMS_ENUMERATION, infoItemID));
			String dBSubQuery = PropertyList.getElementAsString(infoItemsListToUse, 
					String.format(INFO_ITEMS_SUB_QUERY, infoItemID));
			
			if (dBTableName.compareToIgnoreCase(m_rootTable) != 0) {
				buildJoinStatement(sqlJoinPart, dBTableName);
			}
			
			String enumColName = "value" + dbLocaleSuffix;
			if ((dbLocaleSuffix == null) || (dbLocaleSuffix.equals(""))) {
				enumColName = DB_DEFAULT_ENUM_COL;
			}
			
			if (enumTable != null) {
				sqlSelectPart.append("\n ifnull(" + enumTable + "." + enumColName + ", "
						+ dBTableName + "." + dBColName + ") as " + dBColName + ",");
				String joinString = " LEFT JOIN " + enumTable + " ON " + dBTableName + "." + dBColName
						+ " = " + enumTable + ".key";
				if (sqlJoinPart.indexOf(joinString) == -1) {
					sqlJoinPart.append("\n" + joinString);
				}
				// treat enums as string columns
				// if searchColumnId == null: no search column specified = search all cols
				// if searchColumnId == infoItemID: search this column
				if (searchText != null && searchText.length() > 0 && !ignoreTablecolumn(dBTableName, dBColName) && 
						(searchColumnId == null || searchColumnId.compareToIgnoreCase(infoItemID) == 0)) {
					
					if (sqlSearchPart.length() > 0) {
						sqlSearchPart.append(" OR ");
					}
					sqlSearchPart.append("\n ifnull(" + enumTable + "." + enumColName + ", "
					+ dBTableName + "." + dBColName + ")" 
							+ " LIKE '%" + searchText + "%' ESCAPE '\\'");
				}
			} else if (dBSubQuery != null) {
				//String formattedSubQuery = dBSubQuery.replaceAll(dBColName, dBTableName + "." + dBColName),
				String resAliasName = "subQuery" + Integer.toString(i);
				sqlSelectPart.append("\n " + dBSubQuery + " as " + resAliasName + ",");
				
				buildSearchPart(searchText, dBTableName,  dBColName, searchColumnId, infoItemID, sqlSearchPart, resAliasName, infoItemsListToUse, translations);
			} else {
				String columnName =  dBTableName + "." + dBColName;
				sqlSelectPart.append("\n" + columnName + ",");
				// if searchColumnId == null: no search column specified = search all cols
				// if searchColumnId == infoItemID: search this column
				buildSearchPart(searchText, dBTableName,  dBColName, searchColumnId, infoItemID, sqlSearchPart, columnName, infoItemsListToUse, translations);
			}
		}
		// remove trailing "," from SELECT part
		String selectPart = sqlSelectPart.toString();
		if (selectPart.endsWith(",")) {
			selectPart = selectPart.substring(0, selectPart.length() - 1);
		}
		// append searchPart to wherePart
		// where filter1=X and filter2=y and (col1 like '%text%" or col2 like '%text$')
		if (sqlSearchPart.length() > 0) {
			if (sqlWherePart.length() > 0) {
				// we'll need to surround the existing where clause with parentheses here to avoid
				// semantically incorrect queries consisting of statements connected with "OR"
				// Example:
				//SELECT
				//*
				//FROM iOS_policies
				//LEFT JOIN iOS_policies_devices ON iOS_policies.id = iOS_policies_devices.iOS_policies_record_id
				//WHERE   iOS_policies_devices.DeviceUniqueID  =  '80F1D313-9701-4AB3-89B3-792561914EF6'
				//OR  iOS_policies.id in (28,113) 
				//AND (
				//iOS_policies.Name LIKE '%TES%' ESCAPE '\');
				sqlWherePart.insert(sqlWherePart.indexOf("WHERE") + "WHERE".length(), "(");
				sqlWherePart.append(")");
				sqlWherePart.append("\n AND (" + sqlSearchPart + ")");
			} else {
				sqlWherePart.append("\nWHERE ");
				sqlWherePart.append(sqlSearchPart);
			}
		}
		sqlSortPart = getSortPart(dbLocaleSuffix);

		sqlStatement.append(selectPart);
		sqlStatement.append(sqlFromPart);
		sqlStatement.append(sqlJoinPart);
		sqlStatement.append(sqlWherePart);
		sqlStatement.append(sqlSortPart);
		sqlStatement.append(sqlLimitPart);

		MDC.remove("infoItemID");

		return sqlStatement.toString();
	}
	
	public ArrayList<Map<String, Object>> getColumnMetaDataForView(String viewInfo, String dbLocaleSuffix) throws Exception {
		ArrayList<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		if (m_infoItemsList == null || m_extendedInfoItemsList == null) {
			throw new Exception("Cant generate column metadata for view - plist file is null");
		}

		PropertyList infoItemsList = m_infoItemsList;
		
		if( !dbLocaleSuffix.equals("_en") ){
			String infoItemPath = m_viewConfigPath + "/" + PLIST_FILE_INFORMATION_ITEMS + dbLocaleSuffix + ".xml";
			infoItemsList = PropertyList.fromInputStream(new FileInputStream(infoItemPath));
			m_logger.debug("infoItemPath is " + infoItemPath);
		}
		
		// This sets up m_viewDefnWorkingSet
		String viewName = setUpViewDefnWorkingSet(viewInfo);

		// the viewName parameter could be either a view name or a view definition.
		String elementName = String.format(WVD_VIEW_DEFINITION, viewName);
		if (!PropertyList.elementExists(m_viewDefnWorkingSet, elementName)) {
			throw new IllegalArgumentException("View does not exist: " + elementName);
		} 

		ArrayList<String> columnOrder = null;
		elementName = String.format(WVD_COLUMN_ORDER, viewName);
		columnOrder = PropertyList.getElementAsArrayListString(m_viewDefnWorkingSet, elementName);
		
		if (columnOrder == null) {		
			throw new Exception("ColumnOrder element does not exist" + elementName);
		}
		
		for (int i = 0; columnOrder != null && i < columnOrder.size(); i++) {
			String colName = columnOrder.get(i);
			String infoItemID = PropertyList.getElementAsString(m_viewDefnWorkingSet, 
					String.format(WVD_COLUMN_PROP_INFO_ITEM_ID, viewName, colName));
    		elementName = String.format(INFO_ITEMS_ITEM_DEFINITIONS, infoItemID);
    		Map<String, Object> baseItemDefinition = PropertyList.getElementAsMap(infoItemsList, elementName);
    		if (baseItemDefinition == null) {
    			baseItemDefinition = new LinkedHashMap<String, Object>();
    		}
        	
        	Map<String, Object> baseItemDeepCopy = new HashMap<String, Object>(baseItemDefinition);
    		Map<String, Object> extendedItemDefinition = PropertyList.getElementAsMap(m_extendedInfoItemsList, elementName);
    		if (extendedItemDefinition != null) {
    			baseItemDeepCopy.putAll(extendedItemDefinition);
    		}
    		metadata.add(fliterOutPrivateAttributes(baseItemDeepCopy));
		}
		return metadata;
	}
	
	private void buildSearchPart(String searchText, String dBTableName, String dBColName, String searchColumnId, 
		String infoItemID, StringBuilder sqlSearchPart, String resAliasName, PropertyList infoItemsListToUse, ResourceBundle translations) throws UnsupportedEncodingException{
		if (searchText != null && searchText.length() > 0 && !ignoreTablecolumn(dBTableName, dBColName) && 
				(searchColumnId == null || searchColumnId.compareToIgnoreCase(infoItemID) == 0)) {
			
			String columnDisplayType = PropertyList.getElementAsString(infoItemsListToUse, String.format(INFO_ITEMS_DISPLAY_TYPE, infoItemID));
			Boolean columnSearchable = PropertyList.getElementAs(infoItemsListToUse, String.format(INFO_ITEMS_SEARCHABLE, infoItemID));
			
			if (columnDisplayType == null){
				columnSearchable = PropertyList.getElementAs(m_extendedInfoItemsList, String.format(INFO_ITEMS_SEARCHABLE, infoItemID));
			}
			
			if ((columnDisplayType == null || !UNSEARCHABLE_DISPLAY_TYPES.contains(columnDisplayType)) && (columnSearchable == null || columnSearchable)){
				if (sqlSearchPart.length() > 0) {
					sqlSearchPart.append(" OR ");
				}
				
				if (columnDisplayType != null ){
					if (columnDisplayType.equals("FormatBoolean")){
						// do a case insensitive search for booleans ( 'Yes' == 'yes' )
						searchText = searchText.toLowerCase();
						String yesStr = new String(translations.getString("BOOLEAN_YES").getBytes("ISO-8859-1"), "UTF-8"),
								noStr = new String(translations.getString("BOOLEAN_NO").getBytes("ISO-8859-1"), "UTF-8");

						if (yesStr.contains(searchText) && !noStr.contains(searchText)){
							searchText = "1";
						} else if (noStr.contains(searchText) && !yesStr.contains(searchText)){
							searchText = "0";
						} else {
							// break the user's ability to search for '1' and '0'
							// going to assume that FormatBoolean types cannot be equal to 2
							searchText = "2";
						}
					} else if (columnDisplayType.equals("FormatVersion")){
						resAliasName = "FormatVersion(" + resAliasName + ")";
					} else if (columnDisplayType.equals("FormatSmartBytes")){
						String bytesStr = new String(translations.getString("BYTES").getBytes("ISO-8859-1"), "UTF-8");

						// ATTENTION: our FormatSmartBytes function defined in LoadableSQLiteExtensions does not localize and only operates in
						// English for the time being. "bytes" is usually localized, so does it here.
						searchText = searchText.replace(bytesStr, "bytes");
						resAliasName = "FormatSmartBytes(" + resAliasName + ")";
					}
				}

				sqlSearchPart.append("\n" + resAliasName + " LIKE '%" + searchText + "%' ESCAPE '\\'");
			}
		}
	}
	
	private String setUpViewDefnWorkingSet(String viewInfo) throws Exception {
		String viewName = null;
		if (viewInfo.startsWith(VIEW_INFO_XML_TAG) &&
				viewInfo.contains(VIEW_INFO_PLIST_TAG)) {
			// The viewInfo is an XML file
			m_viewDefnWorkingSet = PropertyList.fromString(viewInfo);	
			if (!PropertyList.elementExists(m_viewDefnWorkingSet, WVD_VIEW_NAME_DEFINITION)) {
				throw new Exception("User defined view does not contain element " + WVD_VIEW_NAME_DEFINITION);
			}
			viewName = PropertyList.getElementAsString(m_viewDefnWorkingSet, WVD_VIEW_NAME_DEFINITION);
		} else {
			// The viewInfo is a view name
			viewName = viewInfo.toLowerCase();
			m_viewDefnWorkingSet = m_webViewDefnList;
		}
		return viewName;
	}
	
	private String comparisonToString(String filterOperator, String keyValue, String columnDataType) {
		String comparisonAsString = "";
		if (filterOperator.compareToIgnoreCase("contains") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " LIKE '%" + keyValue + "%' ESCAPE '\\'";
			} else {
				comparisonAsString = " LIKE %" + keyValue + "%";
			} 
		} else if (filterOperator.compareToIgnoreCase("NotContains") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " NOT LIKE '%" + keyValue + "%' ESCAPE '\\'";
			} else {
				comparisonAsString = " NOT LIKE %" + keyValue + "%";
			}
		} else if (filterOperator.compareToIgnoreCase("BeginsWith") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " LIKE '" + keyValue + "%' ESCAPE '\\'";
			} else {
				comparisonAsString = " LIKE " + keyValue + "%";
			}
		} else if (filterOperator.compareToIgnoreCase("NotBeginsWith") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " NOT LIKE '" + keyValue + "%' ESCAPE '\\'";
			} else {
				comparisonAsString = " NOT LIKE " + keyValue + "%";
			} 
		} else if (filterOperator.compareToIgnoreCase("EndsWith") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " LIKE '" + keyValue + "' ESCAPE '\\'";
			} else {
				comparisonAsString = " LIKE " + keyValue + "";
			}
		} else if (filterOperator.compareToIgnoreCase("NotEndsWith") == 0) {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				comparisonAsString = " NOT LIKE '%" + keyValue + "' ESCAPE '\\'";
			} else {
				comparisonAsString = " NOT LIKE %" + keyValue + "";
			} 
		} else if (filterOperator.compareToIgnoreCase("IsNotNULL") == 0) {
			comparisonAsString = " IS NOT NULL ";
		} else 	if (filterOperator.compareToIgnoreCase("IsNULL") == 0) {
			filterOperator = " ISNULL ";
		} else if (filterOperator.compareToIgnoreCase("IsEmpty") == 0) {
			comparisonAsString = " = ''";
		} else if (filterOperator.compareToIgnoreCase("IsOneOf") == 0) {
			comparisonAsString = " in (" + keyValue + ") ";
		} else {
			if (columnDataType.compareToIgnoreCase("String") == 0) {
				keyValue = "'" + keyValue + "'";
			}
			if (filterOperator.compareToIgnoreCase("==") == 0) {
				filterOperator = " = ";
			} else 	if (filterOperator.compareToIgnoreCase("<>") == 0) {
				filterOperator = " <> ";
			} else 	if (filterOperator.compareToIgnoreCase("<") == 0) {
				filterOperator = " < ";
			} else 	if (filterOperator.compareToIgnoreCase("<=") == 0) {
				filterOperator = " <= ";
			} else 	if (filterOperator.compareToIgnoreCase(">") == 0) {
				filterOperator = " > ";
			} else 	if (filterOperator.compareToIgnoreCase(">=") == 0) {
				filterOperator = " >= ";
			}
			comparisonAsString = " " + filterOperator + " " + keyValue;
		}
		return comparisonAsString;
	}

	private boolean isValueSameAsCompareType(String columnDataType, String value) {
		boolean theSame = false;
		try {
			if (columnDataType.compareToIgnoreCase("Number") == 0) {
				if (Integer.parseInt(value) >= 0) {
					theSame = true;
				}
			} else if (columnDataType.compareToIgnoreCase("String") == 0) {
				theSame = true;
			}
//			else if (compareType.compareToIgnoreCase("DateTime") == 0) {	
//			} else if (compareType.compareToIgnoreCase("Blob") == 0) {	
//			}
	
		} catch (Exception e) {
			// gobble this exception - we are expecting some of these
		}				
		return theSame;
	}


	
	private StringBuilder getWherePart(StringBuilder sqlJoinPart) throws Exception {
		StringBuilder sqlWherePart = new StringBuilder();
		String elementName = String.format(WVD_COMPARE_VALUE_ARRAY, m_viewName);
		ArrayList<Map<String, Object>> outterCompareValues = PropertyList.getElementAsArrayListMap(m_viewDefnWorkingSet, elementName);
		elementName = String.format(WVD_FILTER_OPERATOR, m_viewName);
		String multipleFilterOperator = PropertyList.getElementAsString(m_viewDefnWorkingSet, elementName);
		m_userParamsIndex = 0;
		String whereStatement = null;
		for (int outterCompareIndex = 0; outterCompareIndex < outterCompareValues.size(); outterCompareIndex++) {
			Map<String, Object> outterCompareValue = outterCompareValues.get(outterCompareIndex);  
			if (outterCompareValue.containsKey(TABLE_PROP_INFO_ITEM_ID)) {
				whereStatement = buildWhereStatement(outterCompareValue, sqlJoinPart);
			} else {
				// Is this view is referencing a smart policy then the compare value
				// could be an array of Compare values.
				// Try and get the Compare as an array
				ArrayList<Map<String, Object>> innerCompareValuesArray = PropertyList.getElementAsArrayListMap(outterCompareValue, WVD_COMPARE_VALUE);

				if (innerCompareValuesArray != null) {
					StringBuilder whereForCompares = new StringBuilder();
					String multipleComparesOperator = PropertyList.getElementAsString(outterCompareValue, WVD_OPERATOR);
					int innerCompareIndex = 0;
					for (innerCompareIndex = 0; innerCompareIndex < innerCompareValuesArray.size(); innerCompareIndex++) {
						Map<String, Object> innerCompareValue = innerCompareValuesArray.get(innerCompareIndex);
						whereStatement = buildWhereStatement(innerCompareValue, sqlJoinPart);
						if (innerCompareIndex > 0) {
							whereForCompares.append("\n " + multipleComparesOperator);
						}
						whereForCompares.append(" " + whereStatement);
					}
					if (innerCompareIndex > 1) {
						// there was more that one inner compare so surround the whereForCompares with ()
						whereForCompares.append(")");
						whereForCompares.insert(0, "(");
					}
					whereStatement = whereForCompares.toString();
				}
			}

			if (outterCompareIndex > 0) {
				sqlWherePart.append("\n " + multipleFilterOperator);
			}
			
			sqlWherePart.append(" " + whereStatement);
		}
		return sqlWherePart;
	}
	
	private String buildWhereStatement(Map<String, Object> compareValue, 
			StringBuilder sqlJoinPart) throws Exception {
		StringBuilder whereStatement = new StringBuilder();
		String infoItemID = (String) compareValue.get(TABLE_PROP_INFO_ITEM_ID);
		String compareValueField = StringUtilities.objectToString(compareValue.get(WVD_COMPARE_VALUE));
		String filterOperator = (String) compareValue.get(WVD_OPERATOR);

		// The items could be in the infoItems.xml file or the
		// extendedInfoItems.xml
		PropertyList infoItemList = m_infoItemsList;
		String columnName = PropertyList.getElementAsString(infoItemList, 
				String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
		if (columnName == null) {
			infoItemList = m_extendedInfoItemsList;
			columnName = PropertyList.getElementAsString(infoItemList, 
					String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
			if (columnName == null) {
				throw new Exception(
						String.format("DB_ColumnName for info item %1$s for view %2$s can not be found.", 
								infoItemID, m_viewName));
			}
		}
		String enumTable = PropertyList.getElementAsString(infoItemList, 
				String.format(INFO_ITEMS_ENUMERATION, infoItemID));
		String columnDataType = PropertyList.getElementAsString(infoItemList, 
				String.format(INFO_ITEMS_COLUMN_DATA_TYPE, infoItemID));
		String dBTableName = PropertyList.getElementAsString(infoItemList, 
				String.format(INFO_ITEMS_TABLE_NAME, infoItemID));
		if (dBTableName == null) {
			throw new Exception(
					String.format("DB_TableName for info item %1$s for view %2$s can not be found.", 
							infoItemID, m_viewName));
		}
		String keyValue = "";
		if (compareValueField.compareToIgnoreCase(WVD_COMPARE_VALUE_USER_DEFINED) == 0) {
			String userParam = null;
			if (m_userParams != null && m_userParamsIndex < m_userParams.size()) {
				userParam = m_userParams.get(m_userParamsIndex);
				m_userParamsIndex++;
			}

			if (userParam != null) {
				keyValue = userParam;
			} else {
				throw new Exception("Expected endpoint parameter {param} instead of null");
			}
		} else if (isValueSameAsCompareType(columnDataType, compareValueField)) {
			keyValue = compareValueField;
		} else {
			keyValue = m_enumHandler.getEnumKeyForValue(enumTable, compareValueField);
		}

		whereStatement.append(" " + dBTableName + "." + columnName
				+ comparisonToString(filterOperator, keyValue, columnDataType));
		if (dBTableName.compareToIgnoreCase(m_rootTable) != 0) {
			buildJoinStatement(sqlJoinPart, dBTableName);
		}

		return whereStatement.toString();
		
	}
	
	private StringBuilder getSortPart(String dbLocaleSuffix) throws Exception {
		StringBuilder sqlSortPart = new StringBuilder();
		String orderbyParams = getUiParam(ViewConstants.PARAM_ORDERBY);
		if (orderbyParams == null) {
			// use the sorting as specified in the view
			String elementName = String.format(WVD_SORT_ORDER, m_viewName);
			ArrayList<Map<String, Object>> sortOrders = PropertyList.getElementAsArrayListMap(
					m_viewDefnWorkingSet, elementName);
			if (sortOrders != null) {
				for (int sortIndex = 0; sortIndex < sortOrders.size(); sortIndex++) {
					Map<String, Object> sortOrder = sortOrders.get(sortIndex);  
					Boolean ascending = (Boolean)sortOrder.get(WVD_SORT_ORDER_ASCENDING); 
					String columnId = (String)sortOrder.get(WVD_SORT_ORDER_COLUMN_ID); 
					if (ascending != null && columnId != null) {
						String infoItemID = PropertyList.getElementAsString(m_viewDefnWorkingSet, 
								String.format(WVD_COLUMN_PROP_INFO_ITEM_ID, m_viewName, columnId));
						sqlSortPart.append(buildSortStatement(infoItemID, sortIndex, ascending? "ASC" : "DESC", dbLocaleSuffix));
					}
				}
			}
		} else {
			// use the sorting as specified by the user
			// The orderby parameter will be of the following format:
			// colGuid1 asc, ColGuid2 desc. 
			// The $orderby= has already been parsed out. Just process the rhs now
			// the asc/desc can be omitted, in which case asc is assumed
			String[] sortParams = orderbyParams.split(",");
			for (int paramIndex = 0; paramIndex < sortParams.length; paramIndex++) {
				String sortParam = sortParams[paramIndex];
				// This colParam is of the form "colGuid1 asc" or "colGuid1" now
				sortParam.trim();
				String sortOrder = "ASC";
				String[] paramValues = sortParam.split(" ");
				if (paramValues.length == 0) {
					throw new Exception("Bad orderby parameters: " + orderbyParams);
				}
				if (paramValues.length == 2
						&& (paramValues[1].compareToIgnoreCase("DESC") == 0)) {
					sortOrder = "DESC";
				}
				sqlSortPart.append(buildSortStatement(paramValues[0], paramIndex, sortOrder, dbLocaleSuffix));
			}
		}
		return sqlSortPart;
	}
	
	private void buildJoinStatement(StringBuilder joinStatement, String dBTableName) throws Exception {

		String foreignKeyColString = String.format(DB_RELATIONS_ROOT_TBL_2ND_TBL_FOREIGN_COL, m_rootTable, dBTableName);
		String referencedColString = String.format(DB_RELATIONS_ROOT_TBL_2ND_TBL_REFERENCED_COL, m_rootTable, dBTableName);
		String alternativeJoinTable = null;
		
		String foreignKeyCol = PropertyList.getElementAsString(m_dbRelations, foreignKeyColString);
		if (foreignKeyCol == null) {
			foreignKeyCol = PropertyList.getElementAsString(m_extendedDbRelations, foreignKeyColString);
		}
		
		if (foreignKeyCol == null) {
			alternativeJoinTable = PropertyList.getElementAsString(m_viewDefnWorkingSet, String.format(WVD_ADDITIONAL_JOIN_TABLE, m_viewName));
			foreignKeyColString = String.format(DB_RELATIONS_ROOT_TBL_2ND_TBL_FOREIGN_COL, alternativeJoinTable, dBTableName);
			referencedColString = String.format(DB_RELATIONS_ROOT_TBL_2ND_TBL_REFERENCED_COL, alternativeJoinTable, dBTableName);
			
			foreignKeyCol = PropertyList.getElementAsString(m_dbRelations, foreignKeyColString);
			
			if (foreignKeyCol == null) {
				foreignKeyCol = PropertyList.getElementAsString(m_extendedDbRelations, foreignKeyColString);
			}
		}
		
		if (foreignKeyCol == null) {
			String foreignKeyListString = String.format(DB_RELATIONS_ROOT_TBL_2ND_TBL_FOREIGN_LIST, m_rootTable, dBTableName);
			ArrayList<Map<String, Object>> foreignKeyList = PropertyList.getElementAsArrayListMap(m_dbRelations, foreignKeyListString);
			
			if (foreignKeyList == null) {
				foreignKeyList = PropertyList.getElementAsArrayListMap(m_extendedDbRelations, foreignKeyListString);
			}
			
			if (foreignKeyList == null) {			
				if (foreignKeyList == null) {
					throw new Exception(
							String.format("ForeignKeyColumn or ForeignKeyList %1$s for view %2$s can not be found.", 
									foreignKeyColString, m_viewName));
				}
			}
			for (int keyIndex = 0; keyIndex < foreignKeyList.size(); keyIndex++) {
				Map<String, Object> foreignKeyMapping = foreignKeyList.get(keyIndex);  
				foreignKeyCol = (String)foreignKeyMapping.get(DB_RELATIONS_FOREIGN_COL);
				String referencedCol = (String)foreignKeyMapping.get(DB_RELATIONS_REFERENCED_COL);
				if (foreignKeyCol == null) {
					throw new Exception(
							String.format("ForeignKeyColumn %1$s in ForeignKeyList for view %2$s can not be found.", 
									foreignKeyColString, m_viewName));
				}
				if (referencedCol == null) {
					throw new Exception(
							String.format("ReferencedColumn %1$s in ForeignKeyList for view %2$s can not be found.", 
									referencedColString, m_viewName));
				}
				if (keyIndex == 0) {
					
				}
				String joinString = " " + m_rootTable + "." + foreignKeyCol
						+ " = " + dBTableName + "." + referencedCol;
				if (joinStatement.indexOf(joinString) == -1) {
					if (keyIndex == 0) {
						joinStatement.append("\nLEFT JOIN " + dBTableName + " ON");
					} else {
						joinStatement.append(" AND ");
					}
					joinStatement.append(joinString);
				}
			}
		} else {
			String referencedCol = PropertyList.getElementAsString(m_dbRelations, referencedColString);
			
			if (referencedCol == null) {
				referencedCol = PropertyList.getElementAsString(m_extendedDbRelations, referencedColString);
			}
			
			if (referencedCol == null) {
				throw new Exception(
						String.format("ReferencedColumn %1$s for view %2$s can not be found.", 
								referencedColString, m_viewName));
			}

			String joinString = "LEFT JOIN " + dBTableName + " ON " + (alternativeJoinTable != null ? alternativeJoinTable : m_rootTable) + "." + foreignKeyCol
					+ " = " + dBTableName + "." + referencedCol;
			if (joinStatement.indexOf(joinString) == -1) {
				joinStatement.append("\n" + joinString);
			}
		}
	}

	private String buildSortStatement(String infoItemID, int sortIndex, String sortOrder, String dbLocaleSuffix) throws Exception {
		StringBuilder sortStatement = new StringBuilder();
		String dBTableName = PropertyList.getElementAsString(
				m_infoItemsList, String.format(INFO_ITEMS_TABLE_NAME, infoItemID));
		String enumTable = PropertyList.getElementAsString(m_infoItemsList, String.format(INFO_ITEMS_ENUMERATION, infoItemID));
		
		if(enumTable == null){
			enumTable = PropertyList.getElementAsString(m_extendedInfoItemsList, String.format(INFO_ITEMS_ENUMERATION, infoItemID));
		}
		
		String dBColName;
		if (dBTableName == null) {
			dBTableName = PropertyList.getElementAsString(m_extendedInfoItemsList, 
					String.format(INFO_ITEMS_TABLE_NAME, infoItemID));
			dBColName = PropertyList.getElementAsString(m_extendedInfoItemsList,
					String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
		} else {
			dBColName = PropertyList.getElementAsString(m_infoItemsList,
					String.format(INFO_ITEMS_COLUMN_NAME, infoItemID));
		}
		if (dBTableName == null) {
			throw new Exception(
					String.format("DB_TableName for info item %1$s for view %2$s can not be found.", 
							infoItemID, m_viewName));
		}
		if (dBColName == null) {
			throw new Exception(
					String.format("DB_ColumnName for info item %1$s for view %2$s can not be found.", 
							infoItemID, m_viewName));
		}
		
		if (sortIndex == 0) {
			sortStatement.append("\n ORDER BY");
		} else {
			sortStatement.append(",");
		}
		
		if (enumTable != null) {
			String enumColName = "value" + dbLocaleSuffix;
			if ((dbLocaleSuffix == null) || (dbLocaleSuffix.equals(""))) {
				enumColName = DB_DEFAULT_ENUM_COL;
			}
			
			sortStatement.append(" ifnull(" + enumTable + "." + enumColName + ", " + dBTableName + "." + dBColName + ")" + " COLLATE NOCASE " + sortOrder);
		} else {
			// we need to order on the value of the sub-query if that's the data that we're returning  
			String subQuery = PropertyList.getElementAsString(m_infoItemsList, String.format(INFO_ITEMS_SUB_QUERY, infoItemID));
			if(subQuery == null){
				subQuery = PropertyList.getElementAsString(m_extendedInfoItemsList, String.format(INFO_ITEMS_SUB_QUERY, infoItemID));
			}
			
			if(subQuery != null){
				sortStatement.append(" " + subQuery + " COLLATE NOCASE " + sortOrder);
			} else {
				sortStatement.append(" " + dBTableName + "." + dBColName + " COLLATE NOCASE " + sortOrder);
			}			
		}
		

		return sortStatement.toString();
	}

    /**
     * Helper method to remove private attributes from an item definition.
     * @param itemDefinition the itemDefinition to remove private attributes from
     * @return the filtered itemDefinition.
     */
    private Map<String, Object> fliterOutPrivateAttributes(Map<String, Object> itemDefinition) {
    	for(String key: m_itemDefinitionAttributesThatArePrivate) {
    		itemDefinition.remove(key);
    	}
    	return itemDefinition;
    }

    /**
     * Helper method to extract a uiParam
     */
    private String getUiParam(String paramName) {
    	String uiParam = null;
    	if (m_uiParams != null) {
    		uiParam = m_uiParams.get(paramName);
    	}
    	return uiParam;
    }
    
    /**
     * Inserts escape characters into a string so it can be used as part of a SQL statement
     * with SQLite. Single quotes are replaced with two single quotes. 
     * @param inputString the string to process
     * @param escapeLIKESpecialChars when true, _ and % are also escaped using the provided escapeChar
     * @param escapeChar the escape char to use when escaping LIKE special chars, _ and %.
     * @return
     */
    public static String escapeStringForSqlite(
    		String inputString, 
    		boolean escapeSpecialCharsForLIKE, 
    		char escapeChar) {
    	
    	String retVal = inputString;
    	
    	if (inputString != null && !inputString.isEmpty()) {
    		
	    	// escape any embedded single quotes with double quotes. See http://www.sqlite.org/lang_expr.html.    		
    		retVal = inputString.replace("'", "''");
	
    		if (escapeSpecialCharsForLIKE) {
				String underscoreEscape = "" + escapeChar + "_";
				retVal = retVal.replace("_", underscoreEscape);
				
				String percentEscape = "" + escapeChar + "%";
				retVal = retVal.replace("%", percentEscape);				
    		}
    	}
    	
    	return retVal;
    }
    
    public String addNewAdHocViefDef(String GUIDs[], String rootTable, String sortBy, String sortDir, Map<String, Object> filter) throws Exception{
    	String viewName = "generic";
    	PropertyList viewData = new PropertyList();
    	HashMap<String, HashMap<String, String>> columnProperties = new HashMap<String, HashMap<String, String>>();
    	
    	ArrayList<HashMap<String, Object>> sortOrderArr = new ArrayList<HashMap<String, Object>>();
    	
    	ArrayList<String> columnOrder = new ArrayList<String>();
    	String columnName, sortByColumnName = null;
    	
    	for(int i = 0; i < GUIDs.length; i++){
    		columnName = "column_" + Integer.toString(i);
    		HashMap<String, String> columnProperty = new HashMap<String, String>();
    		
    		columnProperty.put("InfoItemID", GUIDs[i]);
    		columnOrder.add(columnName);
    		columnProperties.put(columnName, columnProperty);
    		
    		if(GUIDs[i].equals(sortBy)){
    			sortByColumnName = columnName;
    		}
    	}
    	
    	if(sortBy != null && sortByColumnName == null){
    		throw new Exception("The column to sort by must be in the guids list");
    	} else {
    		HashMap<String, Object> sortOrder = new HashMap<String, Object>();
    		
        	if(sortDir != null && sortDir.toLowerCase().equals("ascending")){
        		sortOrder.put("Ascending", true);
        	} else {
        		sortOrder.put("Ascending", false);
        	}
        	
        	sortOrder.put("ColumnID", sortByColumnName);
        	sortOrderArr.add(sortOrder);
        	viewData.put("SortOrder", sortOrderArr.toArray());
    	}

    	viewData.put("RootTable", rootTable);
    	viewData.put("ColumnOrder", columnOrder.toArray());
    	viewData.put("ColumnProperties", columnProperties);
    	
    	if(filter != null){
    		viewData.put("Filter", filter);
    	}
    	    	
    	@SuppressWarnings("unchecked")
		HashMap<String, Object> viewDefs = (HashMap<String, Object>) m_webViewDefnList.get("View Definitions");
    	viewDefs.put(viewName, viewData);
		
    	return viewName;
    }
    
    /**
     * Helper method to extract the search text from the $SEARCH parameter
     */
    private String getUiParamSearchText() {
    	String searchText = null;
    	if (m_uiParams != null) {
			Iterator<String> it = m_uiParams.keySet().iterator();
		    while (it.hasNext()) {
		    	String key = (String) it.next();
		    	if (key.startsWith(ViewConstants.PARAM_SEARCH)) {
		    		searchText = escapeStringForSqlite(m_uiParams.get(key), true, '\\');
		    		break;
		    	}
		    }
    	}
    	return searchText;
    }

    /**
     * Helper method to extract the column guid from the $SEARCH parameter 
     */
    private String getUiParamSearchColumn() {
    	String searchColId = null;
    	if (m_uiParams != null) {
			Iterator<String> it = m_uiParams.keySet().iterator();
		    while (it.hasNext()) {
		    	String key = (String) it.next();
		    	if (key.startsWith(ViewConstants.PARAM_SEARCH)) {
	    			String[] parts = key.split(":");
	    			if (parts.length == 2) {
	    				searchColId = parts[1];
	    			}
		    		break;
		    	}
		    }
    	}
    	return searchColId;
    }

    private boolean ignoreTablecolumn(String tableName, String columnName) {
    	boolean ignore = false;
    	ArrayList<String> colsToExclude = m_excludeColumnsFromSearchMap.get(tableName);
    	if (colsToExclude!= null) {
    		if (colsToExclude.contains(columnName)) {
    			ignore = true;
    		}
    	}
    	return ignore;
    	
    }
}
