package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrganizationInfo {
	
	private int[] deviceIds;
	private String name;
	private String phone;
	private String email;
	private String address;
	private String custom;
	
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
	 * The organization name
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * The organization phone
	 */
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * The organization email
	 */
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * The organization address
	 */
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * The organization custom field
	 */
	public String getCustom() {
		return custom;
	}
	public void setCustom(String custom) {
		this.custom = custom;
	}
	
	@Override
	public String toString() {			
		return "OrganizationInfo: name=" + name + 
				" phone=" + phone +
				" email=" + email +
				" address=" + address +
				" custom=" + custom +
				" deviceIds=" + Arrays.toString(deviceIds) +
				".";
	}
}