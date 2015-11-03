package com.absolute.am.model.policyaction;

public class PolicyActionAssignment {
	private long[] actionIds;
	private long[] policyIds;
	private int initialDelay;
	private int repeatInterval;
	private int repeatCount;

	/**
	 * The action IDs
	 */
	public long[] getActionIds() {
		return actionIds;
	}
	public void setActionIds(long[] actionIds) {
		this.actionIds = actionIds;
	}
	
	/**
	 * The policy IDs
	 */
	public long[] getPolicyIds() {
		return policyIds;
	}
	public void setPolicyId(long[] policyIds) {
		this.policyIds = policyIds;
	}
	
	/**
	 * The initial delay seconds
	 */
	public int getInitialDelay() {
		return initialDelay;
	}
	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}
	
	/**
	 * The repeat interval seconds
	 */
	public int getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	
	/**
	 * The repeat count
	 */
	public int getRepeatCount() {
		return repeatCount <= 0? 1 : repeatCount;
	}
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
}
