package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iOS_policies_media")

public class iOsPoliciesMedia implements Serializable{
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
	
	@Column(name = "MediaUniqueID")
	private String mediaUniqueId;
	
	@Column(name = "AvailabilitySelector")
	private long availabilitySelector;

	@Column(name = "AvailabilityStartTime")
	private String availabilityStartTime;

	@Column(name = "AvailabilityEndTime")
	private String availabilityEndTime;

	@Column(name = "State")
	private long state;

		
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
	public String getMediaUniqueId() {
		return mediaUniqueId;
	}
	public void setMediaUniqueId(String mediaUniqueId) {
		this.mediaUniqueId = mediaUniqueId;
	}
	public long getAvailabilitySelector() {
		return availabilitySelector;
	}
	public void setAvailabilitySelector(long availabilitySelector) {
		this.availabilitySelector = availabilitySelector;
	}
	public String getAvailabilityStartTime() {
		return availabilityStartTime;
	}
	public void setAvailabilityStartTime(String availabilityStartTime) {
		this.availabilityStartTime = availabilityStartTime;
	}
	public String getAvailabilityEndTime() {
		return availabilityEndTime;
	}
	public void setAvailabilityEndTime(String availabilityEndTime) {
		this.availabilityEndTime = availabilityEndTime;
	}
	public long getState() {
		return state;
	}
	public void setState(long state) {
		this.state = state;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}