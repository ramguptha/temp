/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.model.policy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.PropertyList;

/**
 * @author dlavin
 *
 */
@XmlRootElement
public class Policy {
	private  int id;
						// last_modified VARCHAR(30),
	private UUID uniqueID;
	
	private int seed;
	
	private String name; 
	private PropertyList smartPolicyUserEditableFilter;
						// FilterQuery TEXT, 
						// schemaVersion;
						// FilterTables TEXT, 
	private int filterType;
	
	private static final DateFormat ISO8601_DATE = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/**
	 * The policy id
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * The unique Id
	 */
	public UUID getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(UUID uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	/**
	 * The seed
	 */
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	/**
	 * The name
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * The filter type
	 */
	public int getFilterType() {
		return filterType;
	}
	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}
	
	/**
	 * The smart policy user editable filter
	 */
	public PropertyList getSmartPolicyUserEditableFilter() {
		return smartPolicyUserEditableFilter;
	}
	public void setSmartPolicyUserEditableFilter(PropertyList userEditableFilter) {
		smartPolicyUserEditableFilter = userEditableFilter;
	}
	
	/**
	 * User filter dates are sent through as strings with a UseNativeType = true. They need to be converted to Date objects
	 * in order to be parsed correctly by the PropertyList.objectToXML().
	 * TODO: need to investigate how to generalize this function and possibly put it in the PropertyList class so that we may unit test it
	 * @throws ParseException 
	 */
	public void correctUserEditableFilterDates() throws ParseException {
		ArrayList<Map<String, Object>> compareValues = PropertyList.getElementAsArrayListMap(smartPolicyUserEditableFilter, "CompareValue");
		if (compareValues != null) {
			for (Map<String, Object> compareValue : compareValues) {
				if ((Boolean)compareValue.get("UseNativeType")){
					for (String compareValueStr : new String[]{"CompareValue", "CompareValue2"}){
						Object value = compareValue.get(compareValueStr);
						if (value instanceof String && ((String) value).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")){
							Date date = ISO8601_DATE.parse((String) value);
							compareValue.put(compareValueStr, date);
						}
					}
				}
			}
		}
	}
	
}
