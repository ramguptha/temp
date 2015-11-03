package com.absolute.am.model.command;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeviceOwnership {
	
	private int[] deviceIds;
	private int ownershipType;
	
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
	 * The device ownership type
	 */
	public int getOwnershipType() {
		return ownershipType;
	}
	public void setOwnershipType(int ownershipType) {
		this.ownershipType = ownershipType;
	}
	
	@Override
	public String toString() {			
		return "DeviceOwnership: ownershipType=" + ownershipType + 
				" deviceIds=" + Arrays.toString(deviceIds) +
				".";
	}
}
