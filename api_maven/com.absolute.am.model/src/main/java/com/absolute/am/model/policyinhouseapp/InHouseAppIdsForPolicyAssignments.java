/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policyinhouseapp;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.util.StringUtilities;

@XmlRootElement
public class InHouseAppIdsForPolicyAssignments {
	
	private long[] inHouseAppIds;
	private PolicyAssignment[] policyAssignments;
	
	/**
	 * The in-house application Id list
	 */
	public long[] getInHouseAppIds() {
		return inHouseAppIds;
	}
	public void setInHouseAppIds(long[] inHouseAppIds) {
		this.inHouseAppIds = inHouseAppIds;
	}

	/**
	 * The policy assignments list
	 */
	public PolicyAssignment[] getPolicyAssignments() {
		return policyAssignments;
	}

	public void setPolicyAssignments(PolicyAssignment[] policyAssignments) {
		this.policyAssignments = policyAssignments;
	}
	
	@Override
	public String toString() {	
		return "InHouseAppIdListToPolicyAssociation: inHouseAppIds="
				+ StringUtilities.arrayToString(inHouseAppIds, ",")
				+ " policyAssignments=" + StringUtilities.arrayToString(policyAssignments, ",");
	}

}
