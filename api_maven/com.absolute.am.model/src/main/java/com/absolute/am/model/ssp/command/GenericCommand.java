package com.absolute.am.model.ssp.command;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 */
@XmlRootElement
public abstract class GenericCommand {

	private String deviceIdentifier, agentSerial;
	private Integer deviceType;
	
	/**
	 * The device identifier
	 */	
	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}
	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}
	
	/**
	 * The agent serial number
	 */	
	public String getAgentSerial() {
		return agentSerial;
	}
	public void setAgentSerial(String agentSerial) {
		this.agentSerial = agentSerial;
	}
	
	/**
	 * The device type
	 */	
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
}
