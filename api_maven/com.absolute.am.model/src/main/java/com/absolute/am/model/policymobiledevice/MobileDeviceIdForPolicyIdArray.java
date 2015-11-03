package com.absolute.am.model.policymobiledevice;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.util.StringUtilities;

@XmlRootElement
public class MobileDeviceIdForPolicyIdArray {

	private long[] deviceIds;
	private long[] policyIds;
	
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
	 * The policy Id list
	 */
	public long[] getPolicyIds() {
		return policyIds;
	}
	public void setPolicyIds(long[] policyIds) {
		this.policyIds = policyIds;
	}
	
	@Override
	public String toString() {	
		return "MobileDeviceIdForPolicyIdArray: deviceIds=" + StringUtilities.arrayToString(deviceIds,  ",")
				+ " policyIds=" + StringUtilities.arrayToString(policyIds, ",");
	}
}

