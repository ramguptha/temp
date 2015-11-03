/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class GenericDeviceCommand {

	private int[] androidIds;
	private int[] iOsIds;	
	

	/**
	 * An array of android device ids
	 */
	public int[] getAndroidIds() {		
		return androidIds;
	}
	public void setAndroidIds(int[] androidIds) {
		this.androidIds = androidIds;
	}

	/**
	 * An array of IOS device ids
	 */	
	public int[] getiOsIds() {
		return iOsIds;
	}
	public void setiOsIds(int[] iOsIds) {
		this.iOsIds = iOsIds;
	}
	
	/**
	 * If there are android devices, return true
	 */		
	public boolean hasAndroidDevices()
	{
		return getAndroidIds() != null && getAndroidIds().length > 0;
	}
	
	/**
	 * If there are IOS devices, return true
	 */		
	public boolean hasIOSDevices()
	{
		return getiOsIds() != null && getiOsIds().length > 0;
	}
	
	/**
	 * If there are either android or/and IOS devices, return true
	 */			
	public boolean hasDevices()
	{
		return hasIOSDevices() || hasAndroidDevices();
	}		
	
	@Override
	public String toString() {	
		
		return "GenericDeviceCommand: iOsIds=" + Arrays.toString(iOsIds) +
				" androidIds=" + Arrays.toString(androidIds);
	}	
}
