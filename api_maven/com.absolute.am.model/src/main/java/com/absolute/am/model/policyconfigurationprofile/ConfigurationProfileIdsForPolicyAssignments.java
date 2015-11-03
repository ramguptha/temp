package com.absolute.am.model.policyconfigurationprofile;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.util.StringUtilities;

@XmlRootElement
public class ConfigurationProfileIdsForPolicyAssignments {
	
	private long[] configurationProfileIds;
	private PolicyAssignment[] policyAssignments;
	
	/**
	 * The configuration profile Id
	 */
	public long[] getConfigurationProfileIds() {
		return configurationProfileIds;
	}
	public void setConfigurationProfileIds(long[] configurationProfileIds) {
		this.configurationProfileIds = configurationProfileIds;
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
		return "ConfigurationProfileIdListToPolicyAssociation: configurationProfileIds="
				+ StringUtilities.arrayToString(configurationProfileIds, ",")
				+ " policyAssignments=" + StringUtilities.arrayToString(policyAssignments, ",");
	}

}
