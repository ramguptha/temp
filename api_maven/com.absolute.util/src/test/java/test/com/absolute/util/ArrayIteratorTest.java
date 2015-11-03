package test.com.absolute.util;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.util.ArrayIterator;

public class ArrayIteratorTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_iterate_empty_collection() {
		// Create some test data with a regular pattern.
		Object data[] = new Object[0];

		int count = 0;
		ArrayIterator<Object> arrayIterator = new ArrayIterator<Object>(data);
		while (arrayIterator.hasNext()) {
			count++;
		}
		assertEquals(0, count);
	}
	
	@Test(expected=NullPointerException.class)
	@Category(com.absolute.util.helper.FastTest.class)
	public void cannot_create_null_collection() {
		// Create some test data with a regular pattern.
		Object data[] = null;

		int count = 0;
		ArrayIterator<Object> arrayIterator = new ArrayIterator<Object>(data);
		while (arrayIterator.hasNext()) {
			count++;
		}
		assertEquals(0, count);
	}
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_iterate_valid_collection() {
		// Create some test data with a regular pattern.
		Object data[] = new Object[5];
		for (int i=0; i<data.length; i++) {
			data[i] = new Integer(i);
		}

		int index = 0;
		ArrayIterator<Object> arrayIterator = new ArrayIterator<Object>(data);
		while (arrayIterator.hasNext()) {
			Object x = arrayIterator.next();
			assertTrue("Object has correct type at index " + index, 
					x.getClass().equals(Integer.class));
			int value = (Integer)x;
			assertEquals("Object at index " + index + " has the expected value", index, value);
			index++;
		}
		
		assertEquals("Check that all elements were iterated over.", data.length, index);
	}

	private class IterableArray<T> implements Iterable<T> {
		private ArrayIterator<T> arrayIterator;
		
		public IterableArray(T array[]) {
			arrayIterator = new ArrayIterator<T>(array);
		}
		
		public Iterator<T> iterator() {
			return arrayIterator;
		}
		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	// Confirm that an ArrayIterator is compatible with the for(T x: collection) loop construct.
	public void can_use_for_to_iterate_valid_collection() {
		// Create some test data with a regular pattern.
		Object data[] = new Object[5];
		for (int i=0; i<data.length; i++) {
			data[i] = new Integer(i);
		}
		
		int index = 0;
		IterableArray<Object> iterableArray = new IterableArray<Object>(data);		
		for(Object x: iterableArray) {
			assertTrue("Object has correct type at index " + index, 
					x.getClass().equals(Integer.class));
			int value = (Integer)x;
			assertEquals("Object at index " + index + " has the expected value", index, value);
			index++;		
		}

		assertEquals("Check that all elements were iterated over.", data.length, index);
	}	
}
