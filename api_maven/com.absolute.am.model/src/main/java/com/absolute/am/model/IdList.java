/**
 * 
 */
package com.absolute.am.model;

import java.util.Arrays;

/**
 * @author klavin
 *
 */
public class IdList {

	private int[] ids;

	
	/**
	 * The Id list
	 */
	public int[] getIds() {
		return ids;
	}


	public void setIds(int[] ids) {
		this.ids = ids;
	}


	@Override
	public String toString() {	
		
		return "IdList: ids=" + Arrays.toString(ids);
	}			
}
