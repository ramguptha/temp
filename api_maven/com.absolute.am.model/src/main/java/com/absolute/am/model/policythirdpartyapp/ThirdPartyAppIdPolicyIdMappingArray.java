/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policythirdpartyapp;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;

@XmlRootElement
public class ThirdPartyAppIdPolicyIdMappingArray {
	
	private ThirdPartyAppIdPolicyIdMapping[] associations;

	/**
	 * The associations between third party application Id and policy Id
	 */
	public ThirdPartyAppIdPolicyIdMapping[] getAssociations() {
		return associations;
	}
	public void setAssociations(ThirdPartyAppIdPolicyIdMapping[] associations) {
		this.associations = associations;
	}

	@Override
	public String toString() {	
		return "ThirdPartyAppIdPolicyIdMappingArray: associations=" + StringUtilities.arrayToString(associations, ",");
	}	

}
