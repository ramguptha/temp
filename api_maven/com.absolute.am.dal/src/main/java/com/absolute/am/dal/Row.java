/**
 * 
 */
package com.absolute.am.dal;

import java.util.Iterator;

import com.absolute.util.ArrayIterator;

/**
 * A single row of a result set. 
 *
 */
public class Row implements Iterable<Object> {

	private Object[] values;
	
	protected Row() {
		// prevent creation of empty rows.
	}
	
	public Row(Object[] values) {
		this.values = values;
	}
	
	public Iterator<Object> iterator() {
		return new ArrayIterator<Object>(values);
	}	
	
	public Object[] getValues() {
		return values;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Row:");
		boolean first = true;
		for(Object x: this) {
			if (!first) {
				sb.append(",");
			}
			
			sb.append(x);
			first = false;
		}
		return sb.toString();
	}
}
