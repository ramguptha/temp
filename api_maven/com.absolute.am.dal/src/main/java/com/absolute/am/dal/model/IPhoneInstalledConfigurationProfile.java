package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "iphone_installed_profile_info")
public class IPhoneInstalledConfigurationProfile implements Serializable {
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
	
	@Column(name = "IsEncrypted")
	private Integer isEncrypted;
	
	@Column(name = "IsManaged")
	private Integer isManaged;
	
	@Column(name = "PayloadRemovalOptions")
	private Integer payloadRemovalOptions;
	
	@Column(name = "PayloadVersion")
	private Integer payloadVersion;
	
	@Column(name = "PayloadUUID")
	private String payloadUUID;
	
	@Column(name = "PayloadIdentifier")
	private String payloadIdentifier;
	
	@Column(name = "PayloadDisplayName")
	private String payloadDisplayName;
	
	@Column(name = "PayloadOrganization")
	private String payloadOrganization;
	
	@Column(name = "PayloadDescription")
	private String payloadDescription;
	
	@Column(name = "SignerCertificates")
	private byte[] signerCertificates;
	
	@Column(name = "PayloadContent")
	private byte[] payloadContent;
	
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
	public Long getIphoneInfoRecordId() {
		return iphoneInfoRecordId;
	}
	public void setIphoneInfoRecordId(Long iphoneInfoRecordId) {
		this.iphoneInfoRecordId = iphoneInfoRecordId;
	}
	public Integer getIsEncrypted() {
		return isEncrypted;
	}
	public void setIsEncrypted(Integer isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	public Integer getIsManaged() {
		return isManaged;
	}
	public void setIsManaged(Integer isManaged) {
		this.isManaged = isManaged;
	}
	public Integer getPayloadRemovalOptions() {
		return payloadRemovalOptions;
	}
	public void setPayloadRemovalOptions(Integer payloadRemovalOptions) {
		this.payloadRemovalOptions = payloadRemovalOptions;
	}
	public Integer getPayloadVersion() {
		return payloadVersion;
	}
	public void setPayloadVersion(Integer payloadVersion) {
		this.payloadVersion = payloadVersion;
	}
	public String getPayloadUUID() {
		return payloadUUID;
	}
	public void setPayloadUUID(String payloadUUID) {
		this.payloadUUID = payloadUUID;
	}
	public String getPayloadIdentifier() {
		return payloadIdentifier;
	}
	public void setPayloadIdentifier(String payloadIdentifier) {
		this.payloadIdentifier = payloadIdentifier;
	}
	public String getPayloadDisplayName() {
		return payloadDisplayName;
	}
	public void setPayloadDisplayName(String payloadDisplayName) {
		this.payloadDisplayName = payloadDisplayName;
	}
	public String getPayloadOrganization() {
		return payloadOrganization;
	}
	public void setPayloadOrganization(String payloadOrganization) {
		this.payloadOrganization = payloadOrganization;
	}
	public String getPayloadDescription() {
		return payloadDescription;
	}
	public void setPayloadDescription(String payloadDescription) {
		this.payloadDescription = payloadDescription;
	}
	public byte[] getSignerCertificates() {
		return signerCertificates;
	}
	public void setSignerCertificates(byte[] signerCertificates) {
		this.signerCertificates = signerCertificates;
	}
	public byte[] getPayloadContent() {
		return payloadContent;
	}
	public void setPayloadContent(byte[] payloadContent) {
		this.payloadContent = payloadContent;
	}
	public Integer getConfigurationType() {
		return configurationType;
	}
	public void setConfigurationType(Integer configurationType) {
		this.configurationType = configurationType;
	}
}