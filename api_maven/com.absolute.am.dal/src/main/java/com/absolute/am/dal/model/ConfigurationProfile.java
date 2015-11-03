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
@Table(name = "iOS_configuration_profiles")
public class ConfigurationProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "UniqueId")
	private String uniqueId;	
	
	@Column(name = "Seed")
	private Integer seed;

	@Column(name = "PayloadUUID")
	private String payloadUUID;

	@Column(name = "PayloadName")
	private String payloadName;

	@Column(name = "PayloadDescription")
	private String payloadDescription;

	@Column(name = "PayloadIdentifier")
	private String payloadIdentifier;

	@Column(name = "PayloadOrganization")
	private String payloadOrganization;	
	
	@Column(name = "PayloadRemovalOptions")
	private Integer payloadRemovalOptions;

	@Column(name = "OriginalFileName")
	private String originalFileName;	

	@Column(name = "BinaryPackageMD5")
	private String binaryPackageMD5;

	@Column(name = "BinaryPackageName")
	private String binaryPackageName;	

	@Column(name = "VariablesUsed")
	private String variablesUsed;
	
	@Column(name = "PlatformType")
	private Integer platformType;
	
	@Column(name = "ConfigurationType")
	private Integer configurationType;



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
	public String getPayloadUUID() {
		return payloadUUID;
	}
	public void setPayloadUUID(String payloadUUID) {
		this.payloadUUID = payloadUUID;
	}
	public String getPayloadName() {
		return payloadName;
	}
	public void setPayloadName(String payloadName) {
		this.payloadName = payloadName;
	}
	public String getPayloadDescription() {
		return payloadDescription;
	}
	public void setPayloadDescription(String payloadDescription) {
		this.payloadDescription = payloadDescription;
	}
	public String getPayloadIdentifier() {
		return payloadIdentifier;
	}
	public void setPayloadIdentifier(String payloadIdentifier) {
		this.payloadIdentifier = payloadIdentifier;
	}
	public String getPayloadOrganization() {
		return payloadOrganization;
	}
	public void setPayloadOrganization(String payloadOrganization) {
		this.payloadOrganization = payloadOrganization;
	}	
	public Integer getPayloadRemovalOptions() {
		return payloadRemovalOptions;
	}
	public void setPayloadRemovalOptions(Integer payloadRemovalOptions) {
		this.payloadRemovalOptions = payloadRemovalOptions;
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
	public String getVariablesUsed() {
		return variablesUsed;
	}
	public void setVariablesUsed(String variablesUsed) {
		this.variablesUsed = variablesUsed;
	}
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	public Integer getConfigurationType() {
		return configurationType;
	}
	public void setConfigurationType(Integer configurationType) {
		this.configurationType = configurationType;
	}	
}
