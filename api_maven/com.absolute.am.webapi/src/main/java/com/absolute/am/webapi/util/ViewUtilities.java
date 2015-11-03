/**
 * 
 */
package com.absolute.am.webapi.util;

import com.absolute.am.model.ViewDescription;

public final class ViewUtilities {
	
	/**
	 * Check if the viewname exists in ViewDescription[].
	 * @param viewname the name of the view being validated
	 * @param viewdescriptions the array that contains all valid views
	 * @return true if viewname is found
	 */
	public static boolean isValidViewName(String viewname, ViewDescription[] viewdescriptions) {
		
		boolean isValidName = false;
		for (int i = 0; i < viewdescriptions.length; i++) {
			if (viewdescriptions[i].getViewName().equalsIgnoreCase(viewname)) {
				isValidName = true;
				break;
			}
		}
		
		return isValidName;
	}

}
