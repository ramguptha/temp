/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dlavin
 *
 */
@XmlRootElement
public class DeviceList {

	private int[] deviceIds;

	
	/**
	 * The devices
	 */
	public int[] getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(int[] deviceIds) {
		this.deviceIds = deviceIds;
	}

	@Override
	public String toString() {	
		
		return "DeviceList: deviceIds=" + Arrays.toString(deviceIds);
	}			
}
