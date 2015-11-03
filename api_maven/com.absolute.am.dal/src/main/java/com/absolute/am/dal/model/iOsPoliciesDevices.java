package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iOS_policies_devices")

public class iOsPoliciesDevices implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "iOS_policies_record_id")
	private long iOSPoliciesRecordId;
	
	@Column(name = "DeviceUniqueID")
	private String deviceUniqueId;
	
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public long getiOSPoliciesRecordId() {
		return iOSPoliciesRecordId;
	}
	public void setiOSPoliciesRecordId(long iOSPoliciesRecordId) {
		this.iOSPoliciesRecordId = iOSPoliciesRecordId;
	}
	public String getDeviceUniqueId() {
		return deviceUniqueId;
	}
	public void setDeviceUniqueId(String deviceUniqueId) {
		this.deviceUniqueId = deviceUniqueId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}