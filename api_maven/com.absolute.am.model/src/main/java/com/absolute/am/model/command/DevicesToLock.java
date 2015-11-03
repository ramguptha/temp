/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author klavin
 *
 */
@XmlRootElement
public class DevicesToLock {

	private int[] deviceIds;
	private String passcode;
	
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
	 * The passcode that applies to deviceIdsForPasscode
	 */
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	@Override
	public String toString() {	
		return "DevicesToLock: deviceIds=" + Arrays.toString(deviceIds)
				+ " passcode=" + passcode;
	}			
}
