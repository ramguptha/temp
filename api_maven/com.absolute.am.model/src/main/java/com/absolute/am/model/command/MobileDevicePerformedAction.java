package com.absolute.am.model.command;

public class MobileDevicePerformedAction {
	private long mobileDeviceId;
	private String actionUniqueID;
	
	public long getMobileDeviceId() {
		return mobileDeviceId;
	}
	public void setMobileDeviceId(long mobileDeviceId) {
		this.mobileDeviceId = mobileDeviceId;
	}

	public String getActionUniqueID() {
		return actionUniqueID;
	}
	public void setActionUniqueID(String actionUniqueID) {
		this.actionUniqueID = actionUniqueID;
	}
}
