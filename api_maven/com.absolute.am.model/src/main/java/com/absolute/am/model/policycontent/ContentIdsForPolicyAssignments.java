/**
 * 
 */
package com.absolute.am.model.policycontent;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.util.StringUtilities;


/**
 * @author klavin
 *
 */
@XmlRootElement
public class ContentIdsForPolicyAssignments {

	private long[] contentIds;
	private PolicyAssignment[] policyAssignments;
	
	/**
	 * The content Id list
	 */
	public long[] getContentIds() {
		return contentIds;
	}
	public void setContentIds(long[] contentIds) {
		this.contentIds = contentIds;
	}

	/**
	 * The policy assignment list
	 */
	public PolicyAssignment[] getPolicyAssignments() {
		return policyAssignments;
	}
	public void setPolicyAssignments(PolicyAssignment[] policyAssignments) {
		this.policyAssignments = policyAssignments;
	}
	
	@Override
	public String toString() {	
		return "ContentIdListToPolicyAssociation: contentIds=" + StringUtilities.arrayToString(contentIds, ",")
				+ " policyAssignments=" + StringUtilities.arrayToString(policyAssignments, ",");
	}

}
