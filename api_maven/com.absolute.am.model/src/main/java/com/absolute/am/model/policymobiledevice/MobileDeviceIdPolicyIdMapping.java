package com.absolute.am.model.policymobiledevice;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MobileDeviceIdPolicyIdMapping {
	
	private long deviceId;
	private long policyId;

	/**
	 * The device Id
	 */
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	
	/**
	 * The policy Id
	 */
	public long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(long policyId) {
		this.policyId = policyId;
	}
	
	@Override
	public String toString() {	
		return "MobileDeviceIdPolicyIdMapping: deviceId=" + deviceId
				+ " policyId=" + policyId;
	}			

}
