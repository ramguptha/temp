package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iOS_applications")
public class iOSApplications implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "UniqueID")
	private String uniqueID;

	@Column(name = "Seed")
	private Integer seed;

	@Column(name = "ProfileUniqueID")
	private String profileUniqueID;

	@Column(name = "AppSize")
	private Integer appSize;

	@Column(name = "Name")
	private String name;

	@Column(name = "AppVersion")
	private Integer appVersion;

	@Column(name = "AppBuildNumber")
	private Integer appBuildNumber;

	@Column(name = "AppVersionString")
	private String appVersionString;

	@Column(name = "BundleIdentifier")
	private String bundleIdentifier;

	@Column(name = "OriginalFileName")
	private String originalFileName;

	@Column(name = "DisplayName")
	private String displayName;

	@Column(name = "BinaryPackageMD5")
	private String binaryPackageMD5;

	@Column(name = "BinaryPackageName")
	private String binaryPackageName;

	@Column(name = "ShortDescription")
	private String shortDescription;

	@Column(name = "LongDescription")
	private String longDescription;

	@Column(name = "UpdateDescription")
	private String updateDescription;

	@Column(name = "PlatformType")
	private Integer platformType;

	@Column(name = "Category")
	private String category;

	@Column(name = "EncryptionKey")
	private String encryptionKey;

	@Column(name = "RemoveWhenMDMIsRemoved")
	private Boolean removeWhenMDMIsRemoved;

	@Column(name = "PreventAppDataBackup")
	private Boolean preventAppDataBackup;

	@Column(name = "MinOSVersion")
	private Integer minOSVersion;

	@Column(name = "SupportedDevices")
	private Integer supportedDevices;

	@Column(name = "IsUniversalApp")
	private Integer isUniversalApp;

	
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


	public String getUniqueID() {
		return uniqueID;
	}


	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}


	public Integer getSeed() {
		return seed;
	}


	public void setSeed(Integer seed) {
		this.seed = seed;
	}


	public String getProfileUniqueID() {
		return profileUniqueID;
	}


	public void setProfileUniqueID(String profileUniqueID) {
		this.profileUniqueID = profileUniqueID;
	}


	public Integer getAppSize() {
		return appSize;
	}


	public void setAppSize(Integer appSize) {
		this.appSize = appSize;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getAppVersion() {
		return appVersion;
	}


	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}


	public Integer getAppBuildNumber() {
		return appBuildNumber;
	}


	public void setAppBuildNumber(Integer appBuildNumber) {
		this.appBuildNumber = appBuildNumber;
	}


	public String getAppVersionString() {
		return appVersionString;
	}


	public void setAppVersionString(String appVersionString) {
		this.appVersionString = appVersionString;
	}


	public String getBundleIdentifier() {
		return bundleIdentifier;
	}


	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}


	public String getOriginalFileName() {
		return originalFileName;
	}


	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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


	public String getShortDescription() {
		return shortDescription;
	}


	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	public String getLongDescription() {
		return longDescription;
	}


	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}


	public String getUpdateDescription() {
		return updateDescription;
	}


	public void setUpdateDescription(String updateDescription) {
		this.updateDescription = updateDescription;
	}


	public Integer getPlatformType() {
		return platformType;
	}


	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getEncryptionKey() {
		return encryptionKey;
	}


	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}


	public Boolean getRemoveWhenMDMIsRemoved() {
		return removeWhenMDMIsRemoved;
	}


	public void setRemoveWhenMDMIsRemoved(Boolean removeWhenMDMIsRemoved) {
		this.removeWhenMDMIsRemoved = removeWhenMDMIsRemoved;
	}


	public Boolean getPreventAppDataBackup() {
		return preventAppDataBackup;
	}


	public void setPreventAppDataBackup(Boolean preventAppDataBackup) {
		this.preventAppDataBackup = preventAppDataBackup;
	}


	public Integer getMinOSVersion() {
		return minOSVersion;
	}


	public void setMinOSVersion(Integer minOSVersion) {
		this.minOSVersion = minOSVersion;
	}


	public Integer getSupportedDevices() {
		return supportedDevices;
	}


	public void setSupportedDevices(Integer supportedDevices) {
		this.supportedDevices = supportedDevices;
	}


	public Integer getIsUniversalApp() {
		return isUniversalApp;
	}


	public void setIsUniversalApp(Integer isUniversalApp) {
		this.isUniversalApp = isUniversalApp;
	}
	
}