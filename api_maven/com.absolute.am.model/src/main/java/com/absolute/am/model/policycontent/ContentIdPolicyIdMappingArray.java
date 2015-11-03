/**
 * 
 */
package com.absolute.am.model.policycontent;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;


/**
 * @author klavin
 *
 */
@XmlRootElement
public class ContentIdPolicyIdMappingArray {

	private ContentIdPolicyIdMapping[] associations;

	/**
	 * The associations between content Id and policy Id
	 */
	public ContentIdPolicyIdMapping[] getAssociations() {
		return associations;
	}

	public void setAssociations(ContentIdPolicyIdMapping[] associations) {
		this.associations = associations;
	}

	@Override
	public String toString() {	
		return "ContentIdPolicyIdMappingArray: associations=" + StringUtilities.arrayToString(associations, ",");
	}			
}
