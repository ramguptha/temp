/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class DevicesForSetActivationLockOptions {

	private int[] deviceIds;
	private int activationLock;
	
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
	 * The device activation lock flag
	 */
	public int getActivationLock() {
		return activationLock;
	}
	public void setActivationLock(int activationLock) {
		this.activationLock = activationLock;
	}

	@Override
	public String toString() {	
		return "DevicesToLock: deviceIds=" + Arrays.toString(deviceIds) + " activationLock=" + activationLock;
	}			
}