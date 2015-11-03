package com.absolute.am.model.command;

import java.util.Arrays;

public class RemoveActionsFromDevices {
	private long[] actionHistoryIds;
	
	/**
	 * The mobile device performed action history Id list
	 */
	public long[] getActionHistoryIds() {
		return actionHistoryIds;
	}
	public void setActionHistoryIds(long[] actionHistoryIds) {
		this.actionHistoryIds = actionHistoryIds;
	}
	
	@Override
	public String toString() {			
		return "RemoveActionsFromDevices: actionHistoryIds=" + Arrays.toString(actionHistoryIds) + ".";
	}
}
