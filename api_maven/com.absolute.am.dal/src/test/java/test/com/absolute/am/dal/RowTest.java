package test.com.absolute.am.dal;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.Row;
import com.absolute.util.StringUtilities;

public class RowTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_iterate_columns() {
		Object[] testData = createSomeTestData();
		Row row = new Row(testData);
		
		int index=0;
		for(Object x: row) {

			System.out.println("Index is " + index);
			
			// skip for null objects.
			if (x != null) {
			// Object type check
				assertEquals("Object type check.", testData[index].getClass(), x.getClass());
			}
			
			// Object value check
			assertEquals("Object value check.", testData[index], x);
			
			index++;
		}
		
		assertEquals("All objects iterated check.", testData.length, index);
		System.out.println("Collection is:" + StringUtilities.CollectionToString(row, ","));
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_values_as_array() throws InterruptedException {
		Object[] testData = createSomeTestData();
		System.out.println("testData is " + testData);
		Row row = new Row(testData);
		
		Object[] newData = row.getValues();
		System.out.println("newData is " + newData);
		assertTrue("Arrays are equal.", Arrays.equals(testData, newData));
				
	}

	/**
	 * Creates an array with a mixture of different object types. This is used as test data.
	 * @return
	 */
	private Object[] createSomeTestData() {		
		 return new Object[] { 
			 new Integer(1),
			 "hello", 
			 new Float(2.3),
			 new Date(), 
			 new Integer(5), 
			 null };
	}

}
