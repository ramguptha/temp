/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstallConfigurationProfilesOptions {

	private long[] deviceIds;
	private long[] configurationProfileIds;
	
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
	 * The configuration profile Id list
	 */
	public long[] getconfigurationProfileIds() {
		return configurationProfileIds;
	}
	public void setconfigurationProfileIds(long[] configurationProfileIds) {
		this.configurationProfileIds = configurationProfileIds;
	}
	
	@Override
	public String toString() {			
		return "InstallConfigurationProfilesOptions: deviceIds=" + Arrays.toString(deviceIds) + 
				" configurationProfileIds=" + Arrays.toString(configurationProfileIds) + 
				".";
	}
}