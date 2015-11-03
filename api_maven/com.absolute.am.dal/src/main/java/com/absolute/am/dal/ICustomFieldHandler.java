/**
 * 
 */
package com.absolute.am.dal;

import java.util.ArrayList;

import com.absolute.am.dal.model.CustomField;

/**
 * @author ephilippov
 * 
 */

public interface ICustomFieldHandler {
	public ArrayList<CustomField> getCustomFields();
	
	public CustomField getCustomField(String id);

	ArrayList<CustomField> getCustomFieldsWithActions() throws Exception;
}
