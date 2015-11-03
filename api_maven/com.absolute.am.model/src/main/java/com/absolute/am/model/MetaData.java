/**
 * 
 */
package com.absolute.am.model;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * MetaData describing the result (number of rows, column titles/types, etc.).
 */
@XmlRootElement
public class MetaData {
	
	private int totalRows;
	
	/**
	 * Total rows property
	 */
	public int getTotalRows()
	{
		return totalRows;
	}
	public void setTotalRows(int theTotalRows)
	{
		totalRows = theTotalRows;
	}
	
	private ArrayList<Map<String, Object>> columnMetaData;

	/**
	 * Column metaData list
	 */
	public ArrayList<Map<String, Object>> getColumnMetaData() {		
		return columnMetaData;
	}
	public void setColumnMetaData(ArrayList<Map<String, Object>> columnMetaData) {		
		this.columnMetaData = columnMetaData;
	}			
}
