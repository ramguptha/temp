/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveProvisioningProfilesOptions {

	private long[] deviceIds;
	// installedProvisioningProfileIds refers to association id from the column [iphone_installed_provisioningprofile_info].[id] 
	private long[] installedProvisioningProfileIds;
	
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
	 * The provisioning profile Id list
	 */
	public long[] getInstalledProvisioningProfileIds() {
		return installedProvisioningProfileIds;
	}
	public void setInstalledProvisioningProfileIds(long[] provisioningProfileIds) {
		this.installedProvisioningProfileIds = provisioningProfileIds;
	}
	
	@Override
	public String toString() {			
		return "DeleteProvisioningProfilesOptions: deviceIds=" +  Arrays.toString(deviceIds) + 
				" installedProvisioningProfileIds=" + Arrays.toString(installedProvisioningProfileIds) + 
				".";
	}
	
}
