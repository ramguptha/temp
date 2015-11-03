/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeleteConfigurationProfilesOptions {

	private long deviceId;
	private long[] configurationProfileIds;
	
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
	 * Configuration profile Id list
	 */
	public long[] getconfigurationProfileIds() {
		return configurationProfileIds;
	}
	public void setconfigurationProfileIds(long[] configurationProfileIds) {
		this.configurationProfileIds = configurationProfileIds;
	}
	
	@Override
	public String toString() {			
		return "DeleteConfigurationProfilesOptions: deviceId=" + deviceId + 
				" configurationProfileIds=" + Arrays.toString(configurationProfileIds) + 
				".";
	}
	
}
