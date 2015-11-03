package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iOS_appstore_applications")
public class iOSAppStoreApplications implements Serializable{

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
	
	@Column(name = "Name")
	private String name;

	@Column(name = "Category")
	private String category;

	@Column(name = "MinOSVersion")
	private Integer minOSVersion;

	@Column(name = "Platform")
	private Integer platform;
	
	@Column(name = "IsUniversalApp")
	private Integer isUniversalApp;

	@Column(name = "VPPCodesPurchased")
	private Integer vPPCodesPurchased;

	@Column(name = "VPPCodesRedeemed")
	private Integer vPPCodesRedeemed;

	@Column(name = "VPPCodesRemaining")
	private Integer vPPCodesRemaining;

	@Column(name = "VPPOrderNumber")
	private String vPPOrderNumber;

	@Column(name = "AppStoreCountry")
	private String appStoreCountry;

	@Column(name = "AppStoreURL")
	private String appStoreURL;

	@Column(name = "ShortDescription")
	private String shortDescription;

	@Column(name = "LongDescription")
	private String longDescription;

	@Column(name = "AppIcon")
	private byte[] appIcon;

	@Column(name = "PlatformType")
	private Integer platformType;

	@Column(name = "AppStoreID")
	private String appStoreID;

	@Column(name = "RemoveWhenMDMIsRemoved")
	private Integer removeWhenMDMIsRemoved;

	@Column(name = "PreventAppDataBackup")
	private Integer preventAppDataBackup;

	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getMinOSVersion() {
		return minOSVersion;
	}


	public void setMinOSVersion(Integer minOSVersion) {
		this.minOSVersion = minOSVersion;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public Integer getIsUniversalApp() {
		return isUniversalApp;
	}

	public void setIsUniversalApp(Integer isUniversalApp) {
		this.isUniversalApp = isUniversalApp;
	}

	public Integer getvPPCodesPurchased() {
		return vPPCodesPurchased;
	}

	public void setvPPCodesPurchased(Integer vPPCodesPurchased) {
		this.vPPCodesPurchased = vPPCodesPurchased;
	}

	public Integer getvPPCodesRedeemed() {
		return vPPCodesRedeemed;
	}

	public void setvPPCodesRedeemed(Integer vPPCodesRedeemed) {
		this.vPPCodesRedeemed = vPPCodesRedeemed;
	}

	public Integer getvPPCodesRemaining() {
		return vPPCodesRemaining;
	}

	public void setvPPCodesRemaining(Integer vPPCodesRemaining) {
		this.vPPCodesRemaining = vPPCodesRemaining;
	}

	public String getvPPOrderNumber() {
		return vPPOrderNumber;
	}

	public void setvPPOrderNumber(String vPPOrderNumber) {
		this.vPPOrderNumber = vPPOrderNumber;
	}

	public String getAppStoreCountry() {
		return appStoreCountry;
	}

	public void setAppStoreCountry(String appStoreCountry) {
		this.appStoreCountry = appStoreCountry;
	}

	public String getAppStoreURL() {
		return appStoreURL;
	}

	public void setAppStoreURL(String appStoreURL) {
		this.appStoreURL = appStoreURL;
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

	public byte[] getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(byte[] appIcon) {
		this.appIcon = appIcon;
	}

	public Integer getPlatformType() {
		return platformType;
	}


	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public String getAppStoreID() {
		return appStoreID;
	}

	public void setAppStoreID(String appStoreID) {
		this.appStoreID = appStoreID;
	}

	public Integer getRemoveWhenMDMIsRemoved() {
		return removeWhenMDMIsRemoved;
	}

	public void setRemoveWhenMDMIsRemoved(Integer removeWhenMDMIsRemoved) {
		this.removeWhenMDMIsRemoved = removeWhenMDMIsRemoved;
	}

	public Integer getPreventAppDataBackup() {
		return preventAppDataBackup;
	}


	public void setPreventAppDataBackup(Integer preventAppDataBackup) {
		this.preventAppDataBackup = preventAppDataBackup;
	}

	
}