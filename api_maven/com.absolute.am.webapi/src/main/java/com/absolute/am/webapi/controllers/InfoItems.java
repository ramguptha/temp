/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.webapi.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IInfoItemHandler;
import com.absolute.am.sqlitedal.Dal;
import com.absolute.am.webapi.Application;
import com.absolute.am.model.Result;
import com.absolute.util.PropertyList;

/**
 * <h3>InfoItems API</h3>
 * <p>This API is used to get Information Items and other data points (such as Custom Fields).</p>
 * 
 * @author klavin
 *
 */

@Path ("/infoitems")
public class InfoItems {

    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(InfoItems.class.getName());

    private final String ENUMERATION_KEY = "Enumeration",
    		ENUMERATION_VALUES_KEY = "EnumerationValues",
    		PLIST_FILE_DATABASE_RELATIONS = "DatabaseRelations.xml",
    		PLIST_FILE_INFO_ITEMS = "InformationItems.xml",
    		PLIST_FILE_INFO_ITEM_ENUMS = "InfoItemEnumerations.xml",
    		PLIST_FILE_INFO_ITEMS_ITEM_DEFINITIONS = "ItemDefinitions",
    		PLIST_FILE_INFO_ITEMS_TABLE_NAME = "DB_TableName",
    		PLIST_FILE_INFO_ITEMS_IGNORE = "Ignore",
    		PLIST_FILE_DATABASE_RELATIONS_TABLE_CLASS = "TableClass",
    		PLIST_FILE_DATABASE_RELATIONS_RELATIONS = "Relations",
    		PLIST_FILE_DATABASE_RELATIONS_OTHER = "Other",
    		PLIST_FILE_DATABASE_RELATIONS_TABLE_FIXED_RELATION_PATHS = "FixedRelationPaths",
    		PLIST_FILE_INFO_ITEM_ENUMS_ENUMERATIONS = "Enumerations",
    		PLIST_FILE_INFO_ITEM_ENUMS_CUST_FIELD_TABLE = "enum_CustomFieldDataType",
    		PLIST_FILE_INFO_ITEM_ENUMS_VALUES = "Values",
    		ENUM_DECIMAL_TYPE = "1",
    		ENUM_DECIMAL_NO_SEP_TYPE = "2",
    		ENUM_BYTES_TYPE = "3";

    private final String[] EXCLUDED_TABLES_FOR_INFO_ITEMS = {"admin_agents", "admin_mobile_devices", "ds_users", "ds_groups", "ds_users_groups"};
    
    private static String[] filterCriteriabyMobileDevicesCache = null;
    
	private @Context HttpServletRequest m_servletRequest;

	/**
	 * <p>Get the list of Information Items and Custom Fields for populating the filter criteria drop-down box when creating/editing Smart Policies for Mobile Devices. 
	 * The response is a ResultSet with meta data and no data rows. If an Information Item or Custom Field is of enum type, the set of possible enum values is also returned.
	 * Note that real ResultSet will return over 300 items, the example data below only includes 2 items.</p>
	 * 
	 * <pre>
	 *{
	 * &emsp;metaData: 
	 * &emsp;	{
	 * &emsp;		totalRows: 0,
	 * &emsp;		columnMetaData: 
	 * &emsp;		[
	 * &emsp;			{
	 * &emsp;				ShortDisplayName: "Manufacturer",
	 * &emsp;				MaxWidth: 1000,
	 * &emsp;				Description: "The company which produced the mobile device.",
	 * &emsp;				MinWidth: 50,
	 * &emsp;				DisplayName: "Mobile Device Manufacturer",
	 * &emsp;				Truncation: 3,
	 * &emsp;				ColumnDataType: "String",
	 * &emsp;				InfoItemID: "408A8D10-D908-4A9E-A00C-3FFB27E7EA81",
	 * &emsp;				Alignment: 1,
	 * &emsp;				Width: 150
	 * &emsp;			},
	 * &emsp;			{
	 * &emsp;				ShortDisplayName: "OS Platform",
	 * &emsp;				MaxWidth: 1000,
	 * &emsp;				Description: "The operating system family used on this device. Currently, this is always â€œiOSâ€.",
	 * &emsp;				MinWidth: 50,
	 * &emsp;				Enumeration: "enum_AgentPlatform",
	 * &emsp;				DisplayName: "Mobile Device OS Platform",
	 * &emsp;				EnumerationValues: 
	 * &emsp;				[
	 * &emsp;					{
	 * &emsp;						name: "0",
	 * &emsp;						value: "Any"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "1",
	 * &emsp;						value: "Mac OS X"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "2",
	 * &emsp;						value: "Windows"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "3",
	 * &emsp;						value: "Linux"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "10",
	 * &emsp;						value: "iOS"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "11",
	 * &emsp;						value: "Android"
	 * &emsp;					},
	 * &emsp;					{
	 * &emsp;						name: "12",
	 * &emsp;						value: "Windows Phone"
	 * &emsp;					}
	 * &emsp;				],
	 * &emsp;				Truncation: 3,
	 * &emsp;				ColumnDataType: "Number",
	 * &emsp;				InfoItemID: "8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5",
	 * &emsp;				Alignment: 1,
	 * &emsp;				Width: 150
	 * &emsp;			},
	 * &emsp;			{
	 * &emsp;				ShortDisplayName: "OS Build Number",
	 * &emsp;				MaxWidth: 1000,
	 * &emsp;				Description: "The build number of the operating system version installed on the mobile device.",
	 * &emsp;				MinWidth: 50,
	 * &emsp;				DisplayName: "Mobile Device OS Build Number",
	 * &emsp;				Truncation: 3,
	 * &emsp;				ColumnDataType: "String",
	 * &emsp;				InfoItemID: "EFD8C1F6-770D-4C5B-B502-AE74A50B1D42",
	 * &emsp;				Alignment: 1,
	 * &emsp;				Width: 150
	 * &emsp;			}
	 * &emsp;		]
	 * &emsp;	},
	 * &emsp;	rows: null
	 *}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return Get the list of Information Items and Custom Fields.
	 * @throws Exception 
	 */
	@GET @Path("/filtercriteria/smartpolicies/bymobiledevices")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getSmartPolicyFilterCriteriabyMobileDevices() throws Exception  {
		return getFilterCriteriaForInfoItems(filterCriteriabyMobileDevicesCache == null ? getInfoItemsForRootTable("iphone_info") : filterCriteriabyMobileDevicesCache, true);
	}
	
	/**
	 * <p>Get the list of Information Items and Custom Fields for populating the filter criteria drop-down box when creating/editing Smart Policies for Mobile Devices. 
	 * The response is a ResultSet with meta data and no data rows. If an Information Item or Custom Field is of enum type, the set of possible enum values is also returned.
	 * Note that real ResultSet will return over 300 items, the example data below only includes 2 items.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return Get the list of Information Items and Custom Fields 
	 * @throws Exception 
	 */
	@GET @Path("/filtercriteria/smartpolicies/byinstalledapplications")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getSmartPolicyFilterCriteriaByIA() throws Exception  {
		
		String[] infoItemIds = {
			"5C7C9375-88D7-479F-A27A-4C1E038E8746",
			"233FF13A-0A51-422E-85E5-FF19281B3966"};
		
		return getFilterCriteriaForInfoItems(infoItemIds);
	}
	
	/**
	 * <p>Get the list of Information Items and Custom Fields for populating the filter criteria drop-down box when creating/editing Smart Policies for Mobile Devices. 
	 * The response is a ResultSet with meta data and no data rows. If an Information Item or Custom Field is of enum type, the set of possible enum values is also returned.
	 * Note that real ResultSet will return over 300 items, the example data below only includes 2 items.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return Get the list of Information Items and Custom Fields 
	 * @throws Exception 
	 */
	@GET @Path("/filtercriteria/smartpolicies/byinstalledconfigprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getSmartPolicyFilterCriteriaByICP() throws Exception  {
		
		String[] infoItemIds = {
			"B78AAB04-4384-431F-A473-C555DDC649DD",
			"B2D31F8A-BE85-442C-83B2-BA0E1579EBC6",
			"E2AE18C1-B9AC-49C0-89A3-B7F410038D37",
			"6AA7C2C9-C66B-47AE-8481-07C6D551CD4B",
			"0B2D8180-E77E-4736-A329-F7CB83A5BB77",
			"4543AD1C-A764-4288-B672-110EE7A9A548",
			"A3EAFEBA-833A-4F7B-AA66-74FC11A669A3"};
		
		return getFilterCriteriaForInfoItems(infoItemIds);
	}

	private Result getFilterCriteriaForInfoItems(String[] infoItemIds, boolean addCustomInfoItems) throws Exception {
		
		Result result = getFilterCriteriaForInfoItems(infoItemIds);
		
		if(addCustomInfoItems){
			String viewConfigPath = (String) Application.getRuntimeProperties().get(Dal.PROP_VIEW_CONFIG_FOLDER), infoItemDataType, infoItemDisplayType;
			PropertyList infoItemEnums = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_INFO_ITEM_ENUMS));
			IInfoItemHandler iInfoItemHandler = Application.getDal(m_servletRequest.getSession()).getInfoItemHandler();
			ArrayList<Map<String, String>> customInfoItems = iInfoItemHandler.getCustomInfoItemInfo(true);
			ArrayList<Map<String, Object>> infoItemMetaData = result.getMetaData().getColumnMetaData(), 
					dataTypes = PropertyList.getElementAsArrayListMap(infoItemEnums, PLIST_FILE_INFO_ITEM_ENUMS_ENUMERATIONS + "/" + PLIST_FILE_INFO_ITEM_ENUMS_CUST_FIELD_TABLE + "/" + PLIST_FILE_INFO_ITEM_ENUMS_VALUES),
					infoItemEnumListMap;
			Map<String, Object> infoItemMap, infoItemEnumItem;
			
			for(Map<String, String> customInfoItem : customInfoItems){
				infoItemMap = new HashMap<String, Object>();
				infoItemMap.put("InfoItemID", customInfoItem.get("id").toString());
				infoItemMap.put("DisplayName", customInfoItem.get("name").toString());
				infoItemDataType = customInfoItem.get("dataType").toString();
				infoItemDisplayType = customInfoItem.get("displayType").toString();
				
				for(Map<String, Object> dataType : dataTypes){
					if( ((Long)dataType.get("key")).toString().equals(infoItemDataType) ){
						infoItemMap.put("isCustomField", true);
						
						if(dataType.get("value").toString().equals("Enumeration")){
							infoItemMap.put("ColumnDataType", "String");
							
							// need to extract the enumeration values from a plist ( cannot be easily converted to a PropertyList object )
							Pattern pattern = Pattern.compile("(<string>)(.+)(</string>)");
							String enumerationList = customInfoItem.get("enumerationList");
							
							// skip over enumerations without any enumerationList values
							if( enumerationList == null){
								continue;
							}
							
						    Matcher matcher = pattern.matcher(enumerationList);
						    
						    infoItemEnumListMap = new ArrayList<Map<String, Object>>();
						    
						    while (matcher.find()) {    
						    	infoItemEnumItem = new HashMap<String, Object>();
						    	infoItemEnumItem.put("name", matcher.group(2));
						    	infoItemEnumItem.put("value", matcher.group(2)); // assume that the value is the same as the name
						    	infoItemEnumListMap.add(infoItemEnumItem);
						    }
						    
						    infoItemMap.put("EnumerationValues", infoItemEnumListMap);
						} else if(dataType.get("value").toString().equals("Number")){
							infoItemMap.put("ColumnDataType", dataType.get("value").toString());
							
							// Numbers have some selectable displayTypes
							// Had to hardcode the enums as they cannot be found elsewhere
							if(infoItemDisplayType.equals(ENUM_DECIMAL_TYPE)){
								infoItemMap.put("DisplayType", "FormatDecimal");
							} else if(infoItemDisplayType.equals(ENUM_DECIMAL_NO_SEP_TYPE)){
								infoItemMap.put("DisplayType", "FormatDecimalNoThousandsSep");
							} else if(infoItemDisplayType.equals(ENUM_BYTES_TYPE)){
								infoItemMap.put("DisplayType", "FormatSmartBytes");
							}
						} else if(dataType.get("value").toString().equals("Boolean")){
							infoItemMap.put("DisplayType", "FormatBoolean");
						} else if(dataType.get("value").toString().equals("Date")){
							infoItemMap.put("DisplayType", "FormatShortDateTime");
						} else if(dataType.get("value").toString().equals("File Version")){
							infoItemMap.put("DisplayType", "FormatVersion");
						} else if(dataType.get("value").toString().equals("IP Address")){
							infoItemMap.put("DisplayType", "FormatIPv4Address");
						} else {
							infoItemMap.put("ColumnDataType", (String)dataType.get("value"));
						}
						
						break;
					}
				}
				
				infoItemMetaData.add(infoItemMap);
			}
		}
		
		return result;
	}

	private Result getFilterCriteriaForInfoItems(String[] infoItemIds) throws Exception {
		
		String viewPList = createPListFromInfoItems(infoItemIds);

		Result result = null;
		HttpSession session = m_servletRequest.getSession();

		IDal dal = Application.getDal(session);
		String localeSuffix = SessionState.getLocaleDbSuffix(session);

		result = ViewHelper.getViewColumnMetaDataResultSet(dal, 
				viewPList, localeSuffix);
		
		// Some of the columns may be of type Enumeration. If so, we need
		// to add an extra field to the result, listing the enum values
		ArrayList<Map<String, Object>> allColumnData = result.getMetaData().getColumnMetaData();
		for (Map<String, Object> columnData : allColumnData) {
			if (columnData.containsKey(ENUMERATION_KEY)) {
				String enumTable = (String)columnData.get(ENUMERATION_KEY);
				columnData.put(ENUMERATION_VALUES_KEY,
						dal.getEnumHandler().getValuesForTable(enumTable, localeSuffix));				
			}
		}
		
		return result;
	}
	
	private String createPListFromInfoItems(String[] infoItemIds){
		StringBuilder viewPList = new StringBuilder();
		
		viewPList.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		viewPList.append("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
		viewPList.append("<plist version=\"1.0\">");
		viewPList.append("<dict>");
		viewPList.append("  <key>View Name Definition</key>");
		viewPList.append("  <string>InfoItemPListView</string>");
		viewPList.append("  <key>View Definitions</key>");
		viewPList.append("  <dict>");
		viewPList.append("    <key>InfoItemPListView</key>");
		viewPList.append("    <dict>");
		viewPList.append("      <key>ColumnOrder</key>");
		viewPList.append("      <array>");
		for (String infoItemId : infoItemIds) {
			String columnName = "column_" + infoItemId;
			viewPList.append("        <string>" + columnName + "</string>");
		}
		viewPList.append("      </array>");
		viewPList.append("      <key>ColumnProperties</key>");
        viewPList.append("      <dict>");
		for (String infoItemId : infoItemIds) {
			String columnName = "column_" + infoItemId;
			viewPList.append("        <key>" + columnName + "</key>");
			viewPList.append("        <dict>");
			viewPList.append("          <key>InfoItemID</key>");
			viewPList.append("          <string>" + infoItemId + "</string>");
			viewPList.append("        </dict>");
		}
		viewPList.append("      </dict>");
		viewPList.append("    </dict>");
		viewPList.append("  </dict>");
		viewPList.append("</dict>");
		viewPList.append("</plist>");

		return viewPList.toString();
	}

	// Returns a list of all the GUIDs for the rootTableName and all the tables associated with it based on the rules outlined in addAllRelatedTraversalTables()
	private String[] getInfoItemsForRootTable(String rootTableName) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException{
		
		String viewConfigPath = (String) Application.getRuntimeProperties().get(Dal.PROP_VIEW_CONFIG_FOLDER);
		PropertyList dbRelations = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_DATABASE_RELATIONS)), 
				infoItems = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_INFO_ITEMS));
		Map<String, Object> itemDefinitions = PropertyList.getElementAsMap(infoItems, PLIST_FILE_INFO_ITEMS_ITEM_DEFINITIONS);
		ArrayList<String> tablesRelatedToRoot = new ArrayList<String>(), result = new ArrayList<String>();
		
		addAllRelatedTraversalTables(dbRelations, tablesRelatedToRoot, rootTableName, PropertyList.getElementAsString(dbRelations, rootTableName + "/" + PLIST_FILE_DATABASE_RELATIONS_TABLE_CLASS));
		
		for (Map.Entry<String, Object> item : itemDefinitions.entrySet()) {
		    @SuppressWarnings("unchecked")
			Map<String, Object> infoItemProperties = (Map<String, Object>) item.getValue();
		    
		    if(tablesRelatedToRoot.contains(infoItemProperties.get(PLIST_FILE_INFO_ITEMS_TABLE_NAME)) &&
		    		!(Boolean)infoItemProperties.get(PLIST_FILE_INFO_ITEMS_IGNORE) ){
		    	result.add(item.getKey());
		    }
		}
		
		filterCriteriabyMobileDevicesCache = result.toArray(new String[0]);
		
		return filterCriteriabyMobileDevicesCache;
	}
	
	// Traverse through the dbRelations to find all the tables related to tableRelatedToRoot that also have the same tableClass
	private void addAllRelatedTraversalTables(PropertyList dbRelations, ArrayList<String> tablesRelatedToRoot, String tableRelatedToRoot, String tableClassRelatedToRoot){
		
		tablesRelatedToRoot.add(tableRelatedToRoot);
		String[] firstTablesRelatedToRoot = PropertyList.getElementAsMap(dbRelations, tableRelatedToRoot + "/" + PLIST_FILE_DATABASE_RELATIONS_RELATIONS).keySet().toArray(new String[0]);
		Map<String, Object> fixedRelationPathsTablesForRootTableMap = PropertyList.getElementAsMap(dbRelations, tableRelatedToRoot + "/" + PLIST_FILE_DATABASE_RELATIONS_TABLE_FIXED_RELATION_PATHS);
		
		for(int i = 0; i < firstTablesRelatedToRoot.length; i++){
			String tableClass = PropertyList.getElementAsString(dbRelations, firstTablesRelatedToRoot[i] + "/" + PLIST_FILE_DATABASE_RELATIONS_TABLE_CLASS);
			
			// Only add the table if it has a defined TableClass, the TableClass is equal to the root table's or "Other", 
			// the table name isn't listed in EXCLUDED_TABLES_FOR_INFO_ITEMS and it hasn't already been added
			if( tableClass != null && ( tableClass.equals(tableClassRelatedToRoot) || tableClass.equals(PLIST_FILE_DATABASE_RELATIONS_OTHER) ) && 
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
}
