package test.com.absolute.am.dal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.ResultSet;
import com.absolute.am.dal.Row;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class ResultSetTest {	
	
	private static final String[] columnTypes = {
		"Number",
		"String",
		"Date",
		"Boolean"		
	};
	
	// same order as above
	private static final String[] formatTypes = {
		"FormatDecimal",
		"FormatString",
		"FormatRelativDateTime",
		"FormatBoolean"		
	};
	
	private ArrayList<Map<String, Object>> createFakeColumnMetaData(int numColumns) {

		ArrayList<Map<String, Object>> retVal = new ArrayList<Map<String, Object>>();
		
		for (int i=0; i<numColumns; i++) {
			
			LinkedHashMap<String, Object> colMeta = new LinkedHashMap<String, Object>();
			colMeta.put(ResultSet.COL_COLUMNDATATYPE, columnTypes[i % columnTypes.length]);
			colMeta.put(ResultSet.COL_DISPLAYTYPE, formatTypes[ i % formatTypes.length]);
			colMeta.put(ResultSet.COL_DESCRIPTION, "This is a long description of column " + i + " that should be localized.");			
			colMeta.put(ResultSet.COL_DISPLAYNAME, "Column " + i);
			colMeta.put(ResultSet.COL_SHORTDISPLAYNAME, "Col " + i);
			retVal.add(colMeta);
		}
		
		return retVal;
	}
	
	private ResultSet createFakeResultSet(int numberOfColumns, int numberOfRows) {

		ResultSet resultSet = new ResultSet();
		// Add column meta data
		resultSet.setColumnMetaData(createFakeColumnMetaData(numberOfColumns));
	
		// create fake row data
		for (int row=0; row<numberOfRows; row++) {
			
			Object[] dataItems = new Object[numberOfColumns];
			for (int col=0; col<numberOfColumns; col++) {
				
				// When we get past number of column types, start using null as the value, to confirm
				// that nulls are also handled correctly.
				if (col >= columnTypes.length) {
					dataItems[col] = null;
				} else {
					String dataType = (String)resultSet.getColumnMetaData().get(col).get(ResultSet.COL_COLUMNDATATYPE);
					if (dataType.equals("Number")) {
						dataItems[col] = new Integer(3);
					} else if (dataType.equals("String")) {					
						dataItems[col] = "String value in column:" + col;
					} else if (dataType.equals("Date")) {
						dataItems[col] = new Date();
					} else if (dataType.equals("Boolean")) {
						dataItems[col] = new Boolean(true);
					}
				}
			}
			try {
				resultSet.addRow(new Row(dataItems));
			} catch (AMWebAPILocalizableException e) {
				fail("Unexpected number of rows. Exception message:\n" + e.getLocalizedMessage());
			}

		}
		return resultSet;
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_have_empty_resultset() {
		ResultSet resultSet = new ResultSet();
		assertNull("getColumnMetaData should return null", resultSet.getColumnMetaData());
		
		assertTrue("default metaDataControl check", ResultSet.MetaDataControl.NO_META_DATA == resultSet.getMetaDataControl());
		
		assertNotNull("getRows not null", resultSet.getRows());
		assertTrue("getRows().length is zero", resultSet.getRows().length == 0);
		assertTrue("getRowCount() is zero", resultSet.getRowCount() == 0);
		
		assertTrue("getRowsOffset is zero", resultSet.getRowsOffset() == 0);
		assertTrue("getTotalRowsAvailable is zero", resultSet.getTotalRowsAvailable() == 0);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_access_column_meta_data() {
		final int numColumns =  columnTypes.length * 2;
		ResultSet resultSet = createFakeResultSet(numColumns, 0);
		
		// As the meta data is fake, there is little point in checking it.
		// But we can at least verify that we can access the meta data for each column.
		for (int i=0; i<numColumns; i++) {
			Map<String, Object> columnMetaData = resultSet.getColumnMetaData().get(i);
			System.out.println("Returned columnMetaData[" + columnMetaData.toString() + "].");
			
			assertTrue("should have column data type", columnMetaData.containsKey(ResultSet.COL_COLUMNDATATYPE));
			assertTrue("should have a Description", columnMetaData.containsKey(ResultSet.COL_DESCRIPTION));
			assertTrue("should have DisplayName", columnMetaData.containsKey(ResultSet.COL_DISPLAYNAME));
			assertTrue("should have DisplayType", columnMetaData.containsKey(ResultSet.COL_DISPLAYTYPE));
			assertTrue("should have ShortDisplayName", columnMetaData.containsKey(ResultSet.COL_SHORTDISPLAYNAME));
		}
		
		assertTrue("check that there is meta data for at least one column", resultSet.getColumnMetaData().size() > 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_access_column_data() {
		final int numColumns =  columnTypes.length * 2;
		final int numRows = 5;
		ResultSet resultSet = createFakeResultSet(numColumns, numRows);

		assertEquals("row count", numRows, resultSet.getRowCount());
		assertEquals("row array length", numRows, resultSet.getRows().length);
		
		// Verify we can access all columns of all rows.
		Row[] rows = resultSet.getRows();
		for (int rowIndex=0; rowIndex<numRows; rowIndex++) {
			
			// note: row.toString() accesses every attribute/field.
			System.out.println("row[" + rowIndex + "] is " + rows[rowIndex].toString());			
		}			
	}
	
	@Test(expected = com.absolute.util.exception.AMWebAPILocalizableException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_add_incorrect_row_size() throws AMWebAPILocalizableException {
		final int numColumns =  columnTypes.length * 2;
		final int numRows = 1;
		ResultSet resultSet = createFakeResultSet(numColumns, numRows);

		assertEquals("row count", numRows, resultSet.getRowCount());
		assertEquals("row array length", numRows, resultSet.getRows().length);
		
		// Try to add a row with the wrong number of columns. This should throw the IllegalArgumentException.
		Row rowWithOneColumn = new Row(
				new Object[]{
						new Integer(4)
					});	// a row with one column.
		resultSet.addRow(rowWithOneColumn);
		
		fail("Should not get to here.");
	}
	
}
