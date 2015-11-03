package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author gefimov
 *
 */
@XmlRootElement
public class EnrollmentUser {
	
	private int[] deviceIds;
	private String username;
	private String domain;
	
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
	 * The user name
	 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * The domain
	 */
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public String toString() {			
		return "EnrollmentUser: username=" + username +
				" domain=" + domain +
				" deviceIds=" + Arrays.toString(deviceIds) +
				".";
	}
}