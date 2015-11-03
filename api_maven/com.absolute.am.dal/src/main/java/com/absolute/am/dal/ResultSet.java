/**
 * 
 */
package com.absolute.am.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * A set of rows that represents the output of a query.
 *
 */
public class ResultSet {

    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ResultSet.class.getName()); 

	/**
	 * Attributes that describe a column (i.e. the meta data). 
	 */
	public static final String COL_COLUMNDATATYPE = "ColumnDataType";
	public static final String COL_DISPLAYTYPE = "DisplayType";
	public static final String COL_DESCRIPTION = "Description";
	public static final String COL_DISPLAYNAME = "DisplayName";
	public static final String COL_SHORTDISPLAYNAME = "ShortDisplayName";
	
	/**
	 * When paging is used, this value is the total number of rows available in the full result 
	 * set. -1 when not known. Use getRows().length to get the number of rows in this result set.
	 */
	private int totalRowsAvailable = 0;
	
	/**
	 * When paging is used, this value is the offset of the first row within the full result 
	 * set. -1 if the offset is not known. 
	 */
	private int rowsOffset = 0;
	
	/**
	 * The rows in the result set. It may be empty.
	 */
	private ArrayList<Row> rows = new ArrayList<Row>();

	/**
	 * Meta data describing the columns in the result set. 
	 */
	private ArrayList<Map<String, Object>> columnMetaData;
	
	/**
	 * Set the meta data for the columns. The meta data is a map of attribute=value pairs. The content
	 * is not specified here.
	 * TODO: should we check for a minimum set of attributes, e.g. name, type, display type.
	 * @param columnMetaData
	 */
	public void setColumnMetaData(ArrayList<Map<String, Object>> columnMetaData) {
		// TODO: check for case where the column meta data is added after the first row, and it has a different number of columns.
		this.columnMetaData = columnMetaData;
	}
	
	/**
	 * Get the column meta data. This may return null.
	 * @return the column meta data.
	 */
	public ArrayList<Map<String, Object>> getColumnMetaData() {
		return this.columnMetaData;
	}
	
	/**
	 * The meta data in the result set can vary depending on what was available and what was
	 * requested. The following enumeration gives an indication of what is present.
	 */
	public enum MetaDataControl {
		//TODO: consider using an EnumSet for MetaDataControl and giving more options to select what data is returned.
		/**
		 * There is no meta data with this result set.
		 */
		NO_META_DATA, 
		/**
		 * The only meta data available (or requested) is the column names.
		 */
		COLUMN_NAMES, 
		/**
		 * All available meta data is included.
		 */
		FULL		
	};
	
	private MetaDataControl metaDataControl = MetaDataControl.NO_META_DATA;

	/**
	 * @return the totalRowsAvailable
	 */
	public int getTotalRowsAvailable() {
		return totalRowsAvailable;
	}

	/**
	 * @param totalRowsAvailable the totalRowsAvailable to set
	 */
	public void setTotalRowsAvailable(int totalRowsAvailable) {
		this.totalRowsAvailable = totalRowsAvailable;
	}

	/**
	 * @return the rowsOffset
	 */
	public int getRowsOffset() {
		return rowsOffset;
	}

	/**
	 * @param rowsOffset the rowsOffset to set
	 */
	public void setRowsOffset(int rowsOffset) {
		this.rowsOffset = rowsOffset;
	}

	/**
	 * Get all of the rows in the result set.
	 * @return the rows
	 */
	public Row[] getRows() {
		return (Row[]) rows.toArray(new Row[rows.size()]);
	}
	
	public Object[] getRowsAsObjectArrays() {
		ArrayList<Object> retVal = new ArrayList<Object>();
		for(Row row: rows) {			
			retVal.add(row.getValues());
		}
		return retVal.toArray();
	}
	
	/**
	 * Add a row to the result set.
	 * @param row
	 * @throws AMWebAPILocalizableException
	 */
	public void addRow(Row row) throws AMWebAPILocalizableException {
		final int numValuesInRow = row.getValues().length;  
		final int expectedNum;
		// If the column meta data is set, then get the number of expected columns from that
		if (columnMetaData.size() > 0) {
			expectedNum = columnMetaData.size();
		}
		// without column data, make sure any new rows have the same number of columns as existing rows.
		else if (rows.size() > 0) {
			expectedNum = rows.get(0).getValues().length;
		}
		// this is the first row added, so the number will always be correct.
		else {
			expectedNum = numValuesInRow;
		}		
			
		if (numValuesInRow != expectedNum) {
			AMWebAPILocalizableException ex = new AMWebAPILocalizableException(
					createExceptionMap(
							"RESULTSET_UNEXPECTED_NUMBER_OF_ROWS",
							new String[]{String.valueOf(expectedNum), String.valueOf(numValuesInRow)},
							null,
							null));
			throw ex;
		} else {		
			this.rows.add(row);
		}
	}
	
	private Map<String, Object> createExceptionMap(String message, String[] msgParams, String description, String[] descrParams) {
		Map<String, Object> exParams = new HashMap<String, Object>();
		exParams.put(AMWebAPILocalizableException.MESSAGE_KEY, message);
		exParams.put(AMWebAPILocalizableException.MESSAGE_KEY_PARAMS, msgParams );
		exParams.put(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY, description);
		exParams.put(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY_PARAMS, descrParams );
		return exParams;
	}
	
	/**
	 * Get the count of rows in the result set.
	 * @return
	 */
	public int getRowCount() {
		return rows.size();
	}


	/**
	 * @return the metaDataControl
	 */
	public MetaDataControl getMetaDataControl() {
		return metaDataControl;
	}

	/**
	 * @param metaDataControl the metaDataControl to set
	 */
	public void setMetaDataControl(MetaDataControl metaDataControl) {
		this.metaDataControl = metaDataControl;
	}
	
}
