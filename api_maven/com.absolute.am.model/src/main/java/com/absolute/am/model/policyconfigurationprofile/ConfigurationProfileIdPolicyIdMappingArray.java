package com.absolute.am.model.policyconfigurationprofile;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;

@XmlRootElement
public class ConfigurationProfileIdPolicyIdMappingArray {
	
	private ConfigurationProfileIdPolicyIdMapping[] associations;

	/**
	 * The associations between ConfigurationProfileId and PolicyId
	 */
	public ConfigurationProfileIdPolicyIdMapping[] getAssociations() {
		return associations;
	}
	public void setAssociations(ConfigurationProfileIdPolicyIdMapping[] associations) {
		this.associations = associations;
	}

	@Override
	public String toString() {	
		return "ConfigurationProfileIdPolicyIdMappingArray: associations=" + StringUtilities.arrayToString(associations, ",");
	}	

}
