package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iphone_installed_software_info")
public class IPhoneInstalledSoftwareInfo implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "last_modified")
	private String lastModified;

	@Column(name = "iphone_info_record_id")
	private Long iphoneInfoRecordId;

	@Column(name = "Name")
	private String name;

	@Column(name = "Version")
	private Integer version;

	@Column(name = "VersionString")
	private String versionString;

	@Column(name = "BuildNumber")
	private Integer buildNumber;

	@Column(name = "BundleIdentifier")
	private String bundleIdentifier;

	@Column(name = "FileType")
	private Integer fileType;

	@Column(name = "FileCreator")
	private Integer fileCreator;

	@Column(name = "InfoString")
	private String infoString;

	@Column(name = "CopyrightString")
	private String copyrightString;

	@Column(name = "ApplicationType")
	private String applicationType;

	@Column(name = "ApplicationDSID")
	private Integer applicationDSID;

	@Column(name = "CR")
	private Integer cR;

	@Column(name = "BundleSize")
	private Integer bundleSize;

	@Column(name = "Label")
	private String label;

	@Column(name = "Description")
	private String description;

	@Column(name = "HardwareAccelerated")
	private Integer hardwareAccelerated;

	@Column(name = "PermissionRequested")
	private String permissionRequested;

	@Column(name = "PermissionDefined")
	private String permissionDefined;

	@Column(name = "Persistent")
	private Integer persistent;

	@Column(name = "InstalledBy")
	private String installedBy;

	@Column(name = "DataDirectory")
	private String dataDirectory;

	@Column(name = "InstallationDate")
	private String installationDate;

	@Column(name = "UpdatedDate")
	private String updatedDate;

	@Column(name = "Services")
	private String services;


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public String getVersionString() {
		return versionString;
	}


	public void setVersionString(String versionString) {
		this.versionString = versionString;
	}


	public Integer getBuildNumber() {
		return buildNumber;
	}


	public void setBuildNumber(Integer buildNumber) {
		this.buildNumber = buildNumber;
	}


	public String getBundleIdentifier() {
		return bundleIdentifier;
	}


	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}


	public Integer getFileType() {
		return fileType;
	}


	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}


	public Integer getFileCreator() {
		return fileCreator;
	}


	public void setFileCreator(Integer fileCreator) {
		this.fileCreator = fileCreator;
	}


	public String getInfoString() {
		return infoString;
	}


	public void setInfoString(String infoString) {
		this.infoString = infoString;
	}


	public String getCopyrightString() {
		return copyrightString;
	}


	public void setCopyrightString(String copyrightString) {
		this.copyrightString = copyrightString;
	}


	public String getApplicationType() {
		return applicationType;
	}


	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}


	public Integer getApplicationDSID() {
		return applicationDSID;
	}


	public void setApplicationDSID(Integer applicationDSID) {
		this.applicationDSID = applicationDSID;
	}


	public Integer getcR() {
		return cR;
	}


	public void setcR(Integer cR) {
		this.cR = cR;
	}

	public Integer getBundleSize() {
		return bundleSize;
	}


	public void setBundleSize(Integer bundleSize) {
		this.bundleSize = bundleSize;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Integer getHardwareAccelerated() {
		return hardwareAccelerated;
	}


	public void setHardwareAccelerated(Integer hardwareAccelerated) {
		this.hardwareAccelerated = hardwareAccelerated;
	}


	public String getPermissionRequested() {
		return permissionRequested;
	}


	public void setPermissionRequested(String permissionRequested) {
		this.permissionRequested = permissionRequested;
	}


	public String getPermissionDefined() {
		return permissionDefined;
	}


	public void setPermissionDefined(String permissionDefined) {
		this.permissionDefined = permissionDefined;
	}


	public Integer getPersistent() {
		return persistent;
	}


	public void setPersistent(Integer persistent) {
		this.persistent = persistent;
	}


	public String getInstalledBy() {
		return installedBy;
	}


	public void setInstalledBy(String installedBy) {
		this.installedBy = installedBy;
	}


	public String getDataDirectory() {
		return dataDirectory;
	}


	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}


	public String getInstallationDate() {
		return installationDate;
	}


	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}


	public String getUpdatedDate() {
		return updatedDate;
	}


	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}


	public String getServices() {
		return services;
	}


	public void setServices(String services) {
		this.services = services;
	}


}