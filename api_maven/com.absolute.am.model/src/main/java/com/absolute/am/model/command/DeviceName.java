package com.absolute.am.model.command;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeviceName {
	
	private int deviceId;
	private String name;
	
	/**
	 * The device Id
	 */
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * The device name
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {			
		return "DeviceName: name=" + name + 
				" deviceId=" + String.valueOf(deviceId) +
				".";
	}
}
