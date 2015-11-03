/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "iOS_provisioning_profiles")
public class ProvisioningProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "UniqueID")
	private String uniqueId;	
	
	@Column(name = "Seed")
	private Integer seed;

	@Column(name = "ProfileUUID")
	private String profileUUID;

	@Column(name = "ProfileExpiryDate")
	private String profileExpiryDate;

	@Column(name = "ProfileName")
	private String profileName;

	@Column(name = "OriginalFileName")
	private String originalFileName;

	@Column(name = "BinaryPackageMD5")
	private String binaryPackageMD5;

	@Column(name = "BinaryPackageName")
	private String binaryPackageName;	


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
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public Integer getSeed() {
		return seed;
	}
	public void setSeed(Integer seed) {
		this.seed = seed;
	}
	public String getProfileUUID() {
		return profileUUID;
	}
	public void setProfileUUID(String profileUUID) {
		this.profileUUID = profileUUID;
	}
	public String getProfileExpiryDate() {
		return profileExpiryDate;
	}
	public void setProfileExpiryDate(String profileExpiryDate) {
		this.profileExpiryDate = profileExpiryDate;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getBinaryPackageMD5() {
		return binaryPackageMD5;
	}
	public void setBinaryPackageMD5(String binaryPackageMD5) {
		this.binaryPackageMD5 = binaryPackageMD5;
	}	
	public String getBinaryPackageName() {
		return binaryPackageName;
	}
	public void setBinaryPackageName(String binaryPackageName) {
		this.binaryPackageName = binaryPackageName;
	}
}
