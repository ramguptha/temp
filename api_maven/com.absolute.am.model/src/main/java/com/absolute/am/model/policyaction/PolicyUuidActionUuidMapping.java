package com.absolute.am.model.policyaction;

public class PolicyUuidActionUuidMapping {
	private String policyUuid;
	private String actionUuid;
	
	/**
	 * The policy unique Id
	 */
	public String getPolicyUuid() {
		return policyUuid;
	}
	public void setPolicyUuid(String policyUuid) {
		this.policyUuid = policyUuid;
	}
	
	/**
	 * The action unique Id
	 */
	public String getActionUuid() {
		return actionUuid;
	}
	public void setActionUuid(String actionUuid) {
		this.actionUuid = actionUuid;
	}
	
	@Override
	public String toString() {
		return "PolicyUuidActionUuidMapping: actionUuid=" + actionUuid + 
				", policyUuid=" + policyUuid +
				".";
	}
}
