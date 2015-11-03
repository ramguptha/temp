/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstallProvisioningProfilesOptions {

	private long[] deviceIds;
	private long[] provisioningProfileIds;
	
	/**
	 * The configuration profile Id list
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
	public long[] getProvisioningProfileIds() {
		return provisioningProfileIds;
	}
	public void setProvisioningProfileIds(long[] provisioningProfileIds) {
		this.provisioningProfileIds = provisioningProfileIds;
	}
	
	@Override
	public String toString() {			
		return "InstallProvisioningProfilesOptions: deviceIds=" + Arrays.toString(deviceIds) + 
				" provisioningProfileIds=" + Arrays.toString(provisioningProfileIds) + 
				".";
	}
}