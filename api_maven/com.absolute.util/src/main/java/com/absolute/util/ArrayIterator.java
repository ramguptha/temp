/**
 * 
 */
package com.absolute.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for an array.
 * @see http://stackoverflow.com/a/11392283 
 *
 */
public class ArrayIterator<T> implements Iterator<T> {

	private T array[];
	private int pos = 0;

	public ArrayIterator(T array[]) {
		if (null == array)
			throw new NullPointerException();
		this.array = array;
	}

	public boolean hasNext() {
		return pos < array.length;
	}

	public T next() throws NoSuchElementException {
		if (hasNext())
			return array[pos++];
		else
			throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();   
	}

}
