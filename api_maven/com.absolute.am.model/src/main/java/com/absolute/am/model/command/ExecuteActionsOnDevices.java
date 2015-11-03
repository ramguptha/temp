package com.absolute.am.model.command;

import java.util.Arrays;

public class ExecuteActionsOnDevices {
	private long[] deviceIds;
	private String[] actionUuids;
	private boolean executeImmediately;
	
	/**
	 * The device Id list
	 */
	public long[] getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(long[] deviceIds) {
		this.deviceIds = deviceIds;
	}	
	
	/**
	 * The action unique Id list
	 */
	public String[] getActionUuids() {
		return actionUuids;
	}
	public void setActionUuids(String[] actionUuids) {
		this.actionUuids = actionUuids;
	}
	
	/**
	 * Whether the actions are executed immediately on the devices, or wait until next scheduled check-in
	 */
	public boolean getExecuteImmediately() {
		return executeImmediately;
	}
	public void setExecuteImmediately(boolean executeImmediately) {
		this.executeImmediately = executeImmediately;
	}
	
	@Override
	public String toString() {			
		return "ExecuteActionsOnDevices: deviceIds=" + Arrays.toString(deviceIds) + 
				" actionUuids=" + Arrays.toString(actionUuids) + 
				" executeImmediately=" + Boolean.toString(executeImmediately) +
				".";
	}
}
