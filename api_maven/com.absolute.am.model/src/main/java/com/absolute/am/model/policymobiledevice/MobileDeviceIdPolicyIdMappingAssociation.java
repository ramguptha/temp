package com.absolute.am.model.policymobiledevice;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;

@XmlRootElement
public class MobileDeviceIdPolicyIdMappingAssociation {

	private MobileDeviceIdPolicyIdMapping[] associations;

	/**
	 * The associations between MobileDeviceId and PolicyId
	 */
	public MobileDeviceIdPolicyIdMapping[] getAssociations() {
		return associations;
	}

	public void setAssociations(MobileDeviceIdPolicyIdMapping[] associations) {
		this.associations = associations;
	}

	@Override
	public String toString() {	
		return "MobileDeviceIdPolicyIdMappingAssociation: associations=" + StringUtilities.arrayToString(associations, ",");
	}
}
