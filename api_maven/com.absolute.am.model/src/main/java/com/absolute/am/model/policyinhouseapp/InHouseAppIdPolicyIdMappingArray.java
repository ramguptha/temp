/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policyinhouseapp;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;

@XmlRootElement
public class InHouseAppIdPolicyIdMappingArray {
	
	private InHouseAppIdPolicyIdMapping[] associations;

	/**
	 * The associations between InHouseAppId and PolicyId
	 */
	public InHouseAppIdPolicyIdMapping[] getAssociations() {
		return associations;
	}
	public void setAssociations(InHouseAppIdPolicyIdMapping[] associations) {
		this.associations = associations;
	}

	@Override
	public String toString() {	
		return "InHouseAppIdPolicyIdMappingArray: associations=" + StringUtilities.arrayToString(associations, ",");
	}	

}
