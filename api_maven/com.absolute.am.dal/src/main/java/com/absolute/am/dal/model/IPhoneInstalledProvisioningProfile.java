package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "iphone_installed_provisioningprofile_info")
public class IPhoneInstalledProvisioningProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "iphone_info_record_id")
	private Long iphoneInfoRecordId;
	
	@Column(name = "UUID")
	private String uuid;
	
	@Column(name = "ExpiryDate")
	private String expiryDate;
	
	@Column(name = "Name")
	private String name;


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
	public Long getIphoneInfoRecordId() {
		return iphoneInfoRecordId;
	}
	public void setIphoneInfoRecordId(Long iphoneInfoRecordId) {
		this.iphoneInfoRecordId = iphoneInfoRecordId;
	}
	public String getUUID() {
		return uuid;
	}
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}