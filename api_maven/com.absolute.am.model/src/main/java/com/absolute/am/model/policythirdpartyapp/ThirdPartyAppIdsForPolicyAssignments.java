/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policythirdpartyapp;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.util.StringUtilities;

@XmlRootElement
public class ThirdPartyAppIdsForPolicyAssignments {
	
	private long[] thirdPartyAppIds;
	private PolicyAssignment[] policyAssignments;
	
	/**
	 * The third party application Id for policy assignments 
	 */
	public long[] getThirdPartyAppIds() {
		return thirdPartyAppIds;
	}
	public void setThirdPartyAppIds(long[] thirdPartyAppIds) {
		this.thirdPartyAppIds = thirdPartyAppIds;
	}

	/**
	 * The policy assignments
	 */
	public PolicyAssignment[] getPolicyAssignments() {
		return policyAssignments;
	}

	public void setPolicyAssignments(PolicyAssignment[] policyAssignments) {
		this.policyAssignments = policyAssignments;
	}
	
	@Override
	public String toString() {	
		return "ThirdPartyAppIdListToPolicyAssociation: thirdPartyAppIds="
				+ StringUtilities.arrayToString(thirdPartyAppIds, ",")
				+ " policyAssignments=" + StringUtilities.arrayToString(policyAssignments, ",");
	}

}
