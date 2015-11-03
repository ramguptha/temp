/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveConfigurationProfilesOptions {

	private long[] deviceIds;
	// installedConfigurationProfileIds refers to association id from the column [iphone_installed_profile_info].[id] 
	private long[] installedConfigurationProfileIds;
	
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
	public long[] getInstalledConfigurationProfileIds() {
		return installedConfigurationProfileIds;
	}
	public void setInstalledConfigurationProfileIds(long[] configurationProfileIds) {
		this.installedConfigurationProfileIds = configurationProfileIds;
	}
	
	@Override
	public String toString() {			
		return "DeleteConfigurationProfilesOptions: deviceIds=" +  Arrays.toString(deviceIds) + 
				" installedConfigurationProfileIds=" + Arrays.toString(installedConfigurationProfileIds) + 
				".";
	}
	
}
