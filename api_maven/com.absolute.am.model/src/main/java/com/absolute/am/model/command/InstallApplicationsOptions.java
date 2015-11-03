/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstallApplicationsOptions {

	private int[] deviceIds;
	private int[] inHouseAppIds;
	private int[] thirdPartyAppIds;
	
	/**
	 * The device Id list
	 */
	public int[] getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(int[] deviceIds) {
		this.deviceIds = deviceIds;
	}	
	
	/**
	 * The in-house application list
	 */
	public int[] getInHouseAppIds() {
		return inHouseAppIds;
	}
	public void setInHouseAppIds(int[] inHouseAppIds) {
		this.inHouseAppIds = inHouseAppIds;
	}

	/**
	 * The third party application list
	 */
	public int[] getThirdPartyAppIds() {
		return thirdPartyAppIds;
	}
	public void setThirdPartyAppIds(int[] thirdPartyAppIds) {
		this.thirdPartyAppIds = thirdPartyAppIds;
	}
	
	@Override
	public String toString() {			
		return "InstallApplicationsOptions: deviceIds=" + Arrays.toString(deviceIds) + 
				" inHouseAppIds=" + Arrays.toString(inHouseAppIds) + 
				" thirdPartyAppIds=" + Arrays.toString(thirdPartyAppIds) +
				".";
	}

}
