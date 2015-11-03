/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeleteApplicationsOptions {

	private long deviceId;
	private long[] applicationIds;
	
    /**
	 * The device id
	 */
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}	

	/**
	 * The application Id list
	 */
	public long[] getApplicationIds() {
		return applicationIds;
	}
	public void setApplicationIds(long[] applicationIds) {
		this.applicationIds = applicationIds;
	}
	
	@Override
	public String toString() {			
		return "DeleteApplicationsOptions: deviceId=" + deviceId + 
				" applicationIds=" + Arrays.toString(applicationIds) + 
				".";
	}

}
