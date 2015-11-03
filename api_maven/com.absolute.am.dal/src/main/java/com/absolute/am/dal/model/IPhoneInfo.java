package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iphone_info")

public class IPhoneInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "agent_info_record_id")
	private Integer agentInfoRecordId;
	
	@Column(name = "UniqueID")
	private String uniqueId;
	
	@Column(name = "SerialNumber")
	private String serialNumber;
	
	@Column(name = "DeviceName")
	private String deviceName;
	
	@Column(name = "DisplayName")
	private String displayName;
	
	@Column(name = "GUID")
	private String GUID;
	
	@Column(name = "ICCID")
	private String ICCID;
	
	@Column(name = "IMEI")
	private String IMEI;
	
	@Column(name = "PhoneNumber")
	private String phoneNumber;
	
	@Column(name = "MachineModel")
	private String machineModel;
	
	@Column(name = "OSVersion")
	private Integer OSVersion;
	
	@Column(name = "OSBuildNumber")
	private String OSBuildNumber;
	
	@Column(name = "TargetIdentifier")
	private String targetIdentifier;
	
	@Column(name = "UniqueIdentifier")
	private String uniqueIdentifier;
	
	@Column(name = "LastBackupDate")
	private String lastBackupDate;
	
	@Column(name = "JB")
	private Integer JB;
	
	@Column(name = "MDMManagedDevice")
	private Integer MDMManagedDevice;
	
	@Column(name = "ModelName")
	private String modelName;
	
	@Column(name = "Model")
	private String model;
	
	@Column(name = "DeviceCapacity")
	private Integer deviceCapacity;
	
	@Column(name = "ModemFirmwareVersion")
	private String modemFirmwareVersion;
	
	@Column(name = "BluetoothMAC")
	private String bluetoothMAC;
	
	@Column(name = "WiFiMAC")
	private String wifiMAC;
	
	@Column(name = "SIMCarrierNetwork")
	private String SIMCarrierNetwork;
	
	@Column(name = "CarrierSettingsVersion")
	private String carrierSettingsVersion;
	
	@Column(name = "DataRoamingEnabled")
	private Integer dataRoamingEnabled;
	
	@Column(name = "SIMMCC")
	private String SIMMCC;
	
	@Column(name = "SIMMNC")
	private String SIMMNC;
	
	@Column(name = "HardwareEncryptionCaps")
	private Integer hardwareEncryptionCaps;
	
	@Column(name = "PasscodePresent")
	private Integer passcodePresent;
	
	@Column(name = "PasscodeCompliant")
	private Integer passcodeCompliant;
	
	@Column(name = "PasscodeCompliantWithProfiles")
	private Integer passcodeCompliantWithProfiles;
	
	@Column(name = "DeviceProductionDate")
	private String deviceProductionDate;
	
	@Column(name = "AppleProductName")
	private String appleProductName;
	
	@Column(name = "ApplePurchaseDate")
	private String applePurchaseDate;
	
	@Column(name = "AppleWarrantyStatus")
	private String appleWarrantyStatus;
	
	@Column(name = "AppleWarrantyEnd")
	private String appleWarrantyEnd;
	
	@Column(name = "PlatformType")
	private Integer platformType;
	
	@Column(name = "DeviceType")
	private Integer deviceType;
	
	@Column(name = "GPSCapable")
	private Integer GPSCapable;
	
	@Column(name = "DeviceTrackingEnabled")
	private Integer deviceTrackingEnabled;
	
	@Column(name = "DeviceTrackingOptions")
	private Integer deviceTrackingOptions;
	
	@Column(name = "DeviceTrackingInterval")
	private Integer deviceTrackingInterval;
	
	@Column(name = "DeviceTrackingAccuracy")
	private Integer deviceTrackingAccuracy;
	
	@Column(name = "DeviceTrackingPIN")
	private String deviceTrackingPIN;
	
	@Column(name = "DeviceUserUniqueID")
	private String deviceUserUniqueID;
	
	@Column(name = "CellularTechnology")
	private Integer cellularTechnology;
	
	@Column(name = "AppStoreUUID")
	private String appStoreUUID;
	
	@Column(name = "Manufacturer")
	private String manufacturer;
	
	@Column(name = "Brand")
	private String brand;
	
	@Column(name = "DeviceInfo")
	private String deviceInfo;
	
	@Column(name = "Board")
	private String board;
	
	@Column(name = "Product")
	private String product;
	
	@Column(name = "IMEISV")
	private String IMEISV;
	
	@Column(name = "CPUName")
	private String CPUName;
	
	@Column(name = "CPUSpeed")
	private Integer CPUSpeed;
	
	@Column(name = "SDCard1Total")
	private Integer SDCard1Total;
	
	@Column(name = "SDCard2Total")
	private Integer SDCard2Total;
	
	@Column(name = "InternalStorageTotal")
	private Integer internalStorageTotal;
	
	@Column(name = "SystemStorageTotal")
	private Integer systemStorageTotal;
	
	@Column(name = "SystemCacheTotal")
	private Integer systemCacheTotal;
	
	@Column(name = "SystemMemoryTotal")
	private Integer systemMemoryTotal;
	
	@Column(name = "KernelVersion")
	private String kernelVersion;
	
	@Column(name = "IsTablet")
	private Integer isTablet;
	
	@Column(name = "DisplayResolution")
	private String displayResolution;
	
	@Column(name = "VoiceRoamingEnabled")
	private Integer voiceRoamingEnabled;
	
	@Column(name = "RecordCreationDate")
	private String recordCreationDate;
	
	@Column(name = "AbsoluteAppsVersion")
	private Integer absoluteAppsVersion;
	
	@Column(name = "AbsoluteAppsBuildNumber")
	private Integer absoluteAppsBuildNumber;
	
	@Column(name = "MDMProfileUpToDate")
	private Integer MDMProfileUpToDate;
	
	@Column(name = "SupportsGeoTracking")
	private Integer supportsGeoTracking;
	
	@Column(name = "DeviceOwnership")
	private Integer deviceOwnership;
	
	@Column(name = "HasPersistenceSupport")
	private Integer hasPersistenceSupport;
	
	@Column(name = "ESN")
	private String ESN;
	
	
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
	public Integer getAgentInfoRecordId() {
		return agentInfoRecordId;
	}
	public void setAgentInfoRecordId(Integer agentInfoRecordId) {
		this.agentInfoRecordId = agentInfoRecordId;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getGUID() {
		return GUID;
	}
	public void setGUID(String GUID) {
		this.GUID = GUID;
	}
	public String getICCID() {
		return ICCID;
	}
	public void setICCID(String ICCID) {
		this.ICCID = ICCID;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String IMEI) {
		this.IMEI = IMEI;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMachineModel() {
		return machineModel;
	}
	public void setMachineModel(String machineModel) {
		this.machineModel = machineModel;
	}
	public Integer getOSVersion() {
		return OSVersion;
	}
	public void setOSVersion(Integer OSVersion) {
		this.OSVersion = OSVersion;
	}
	public String getOSBuildNumber() {
		return OSBuildNumber;
	}
	public void setOSBuildNumber(String OSBuildNumber) {
		this.OSBuildNumber = OSBuildNumber;
	}
	public String getTargetIdentifier() {
		return targetIdentifier;
	}
	public void setTargetIdentifier(String targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	public String getLastBackupDate() {
		return lastBackupDate;
	}
	public void setLastBackupDate(String lastBackupDate) {
		this.lastBackupDate = lastBackupDate;
	}
	public Integer getJB() {
		return JB;
	}
	public void setJB(Integer JB) {
		this.JB = JB;
	}
	public Integer getMDMManagedDevice() {
		return MDMManagedDevice;
	}
	public void setMDMManagedDevice(Integer MDMManagedDevice) {
		this.MDMManagedDevice = MDMManagedDevice;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Integer getDeviceCapacity() {
		return deviceCapacity;
	}
	public void setDeviceCapacity(Integer deviceCapacity) {
		this.deviceCapacity = deviceCapacity;
	}
	public String getModemFirmwareVersion() {
		return modemFirmwareVersion;
	}
	public void setModemFirmwareVersion(String modemFirmwareVersion) {
		this.modemFirmwareVersion = modemFirmwareVersion;
	}
	public String getBluetoothMAC() {
		return bluetoothMAC;
	}
	public void setBluetoothMAC(String bluetoothMAC) {
		this.bluetoothMAC = bluetoothMAC;
	}
	public String getWifiMAC() {
		return wifiMAC;
	}
	public void setWifiMAC(String wifiMAC) {
		this.wifiMAC = wifiMAC;
	}
	public String getSIMCarrierNetwork() {
		return SIMCarrierNetwork;
	}
	public void setSIMCarrierNetwork(String SIMCarrierNetwork) {
		this.SIMCarrierNetwork = SIMCarrierNetwork;
	}
	public String getCarrierSettingsVersion() {
		return carrierSettingsVersion;
	}
	public void setCarrierSettingsVersion(String carrierSettingsVersion) {
		this.carrierSettingsVersion = carrierSettingsVersion;
	}
	public Integer getDataRoamingEnabled() {
		return dataRoamingEnabled;
	}
	public void setDataRoamingEnabled(Integer dataRoamingEnabled) {
		this.dataRoamingEnabled = dataRoamingEnabled;
	}
	public String getSIMMCC() {
		return SIMMCC;
	}
	public void setSIMMCC(String SIMMCC) {
		this.SIMMCC = SIMMCC;
	}
	public String getSIMMNC() {
		return SIMMNC;
	}
	public void setSIMMNC(String SIMMNC) {
		this.SIMMNC = SIMMNC;
	}
	public Integer getHardwareEncryptionCaps() {
		return hardwareEncryptionCaps;
	}
	public void setHardwareEncryptionCaps(Integer hardwareEncryptionCaps) {
		this.hardwareEncryptionCaps = hardwareEncryptionCaps;
	}
	public Integer getPasscodePresent() {
		return passcodePresent;
	}
	public void setPasscodePresent(Integer passcodePresent) {
		this.passcodePresent = passcodePresent;
	}
	public Integer getPasscodeCompliant() {
		return passcodeCompliant;
	}
	public void setPasscodeCompliant(Integer passcodeCompliant) {
		this.passcodeCompliant = passcodeCompliant;
	}
	public Integer getPasscodeCompliantWithProfiles() {
		return passcodeCompliantWithProfiles;
	}
	public void setPasscodeCompliantWithProfiles(
			Integer passcodeCompliantWithProfiles) {
		this.passcodeCompliantWithProfiles = passcodeCompliantWithProfiles;
	}
	public String getDeviceProductionDate() {
		return deviceProductionDate;
	}
	public void setDeviceProductionDate(String deviceProductionDate) {
		this.deviceProductionDate = deviceProductionDate;
	}
	public String getAppleProductName() {
		return appleProductName;
	}
	public void setAppleProductName(String appleProductName) {
		this.appleProductName = appleProductName;
	}
	public String getApplePurchaseDate() {
		return applePurchaseDate;
	}
	public void setApplePurchaseDate(String applePurchaseDate) {
		this.applePurchaseDate = applePurchaseDate;
	}
	public String getAppleWarrantyStatus() {
		return appleWarrantyStatus;
	}
	public void setAppleWarrantyStatus(String appleWarrantyStatus) {
		this.appleWarrantyStatus = appleWarrantyStatus;
	}
	public String getAppleWarrantyEnd() {
		return appleWarrantyEnd;
	}
	public void setAppleWarrantyEnd(String appleWarrantyEnd) {
		this.appleWarrantyEnd = appleWarrantyEnd;
	}
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	public Integer getGPSCapable() {
		return GPSCapable;
	}
	public void setGPSCapable(Integer GPSCapable) {
		this.GPSCapable = GPSCapable;
	}
	public Integer getDeviceTrackingEnabled() {
		return deviceTrackingEnabled;
	}
	public void setDeviceTrackingEnabled(Integer deviceTrackingEnabled) {
		this.deviceTrackingEnabled = deviceTrackingEnabled;
	}
	public Integer getDeviceTrackingOptions() {
		return deviceTrackingOptions;
	}
	public void setDeviceTrackingOptions(Integer deviceTrackingOptions) {
		this.deviceTrackingOptions = deviceTrackingOptions;
	}
	public Integer getDeviceTrackingInterval() {
		return deviceTrackingInterval;
	}
	public void setDeviceTrackingInterval(Integer deviceTrackingInterval) {
		this.deviceTrackingInterval = deviceTrackingInterval;
	}
	public Integer getDeviceTrackingAccuracy() {
		return deviceTrackingAccuracy;
	}
	public void setDeviceTrackingAccuracy(Integer deviceTrackingAccuracy) {
		this.deviceTrackingAccuracy = deviceTrackingAccuracy;
	}
	public String getDeviceTrackingPIN() {
		return deviceTrackingPIN;
	}
	public void setDeviceTrackingPIN(String deviceTrackingPIN) {
		this.deviceTrackingPIN = deviceTrackingPIN;
	}
	public String getDeviceUserUniqueID() {
		return deviceUserUniqueID;
	}
	public void setDeviceUserUniqueID(String deviceUserUniqueID) {
		this.deviceUserUniqueID = deviceUserUniqueID;
	}
	public Integer getCellularTechnology() {
		return cellularTechnology;
	}
	public void setCellularTechnology(Integer cellularTechnology) {
		this.cellularTechnology = cellularTechnology;
	}
	public String getAppStoreUUID() {
		return appStoreUUID;
	}
	public void setAppStoreUUID(String appStoreUUID) {
		this.appStoreUUID = appStoreUUID;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getBoard() {
		return board;
	}
	public void setBoard(String board) {
		this.board = board;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getIMEISV() {
		return IMEISV;
	}
	public void setIMEISV(String IMEISV) {
		this.IMEISV = IMEISV;
	}
	public String getCPUName() {
		return CPUName;
	}
	public void setCPUName(String CPUName) {
		this.CPUName = CPUName;
	}
	public Integer getCPUSpeed() {
		return CPUSpeed;
	}
	public void setCPUSpeed(Integer CPUSpeed) {
		this.CPUSpeed = CPUSpeed;
	}
	public Integer getSDCard1Total() {
		return SDCard1Total;
	}
	public void setSDCard1Total(Integer SDCard1Total) {
		this.SDCard1Total = SDCard1Total;
	}
	public Integer getSDCard2Total() {
		return SDCard2Total;
	}
	public void setSDCard2Total(Integer SDCard2Total) {
		this.SDCard2Total = SDCard2Total;
	}
	public Integer getInternalStorageTotal() {
		return internalStorageTotal;
	}
	public void setInternalStorageTotal(Integer internalStorageTotal) {
		this.internalStorageTotal = internalStorageTotal;
	}
	public Integer getSystemStorageTotal() {
		return systemStorageTotal;
	}
	public void setSystemStorageTotal(Integer systemStorageTotal) {
		this.systemStorageTotal = systemStorageTotal;
	}
	public Integer getSystemCacheTotal() {
		return systemCacheTotal;
	}
	public void setSystemCacheTotal(Integer systemCacheTotal) {
		this.systemCacheTotal = systemCacheTotal;
	}
	public Integer getSystemMemoryTotal() {
		return systemMemoryTotal;
	}
	public void setSystemMemoryTotal(Integer systemMemoryTotal) {
		this.systemMemoryTotal = systemMemoryTotal;
	}
	public String getKernelVersion() {
		return kernelVersion;
	}
	public void setKernelVersion(String kernelVersion) {
		this.kernelVersion = kernelVersion;
	}
	public Integer getIsTablet() {
		return isTablet;
	}
	public void setIsTablet(Integer isTablet) {
		this.isTablet = isTablet; 
	}
	public String getDisplayResolution() {
		return displayResolution;
	}
	public void setDisplayResolution(String displayResolution) {
		this.displayResolution = displayResolution;
	}
	public Integer getVoiceRoamingEnabled() {
		return voiceRoamingEnabled;
	}
	public void setVoiceRoamingEnabled(Integer voiceRoamingEnabled) {
		this.voiceRoamingEnabled = voiceRoamingEnabled;
	}
	public String getRecordCreationDate() {
		return recordCreationDate;
	}
	public void setRecordCreationDate(String recordCreationDate) {
		this.recordCreationDate = recordCreationDate;
	}
	public Integer getAbsoluteAppsVersion() {
		return absoluteAppsVersion;
	}
	public void setAbsoluteAppsVersion(Integer absoluteAppsVersion) {
		this.absoluteAppsVersion = absoluteAppsVersion;
	}
	public Integer getAbsoluteAppsBuildNumber() {
		return absoluteAppsBuildNumber;
	}
	public void setAbsoluteAppsBuildNumber(Integer absoluteAppsBuildNumber) {
		this.absoluteAppsBuildNumber = absoluteAppsBuildNumber;
	}
	public Integer getMDMProfileUpToDate() {
		return MDMProfileUpToDate;
	}
	public void setMDMProfileUpToDate(Integer MDMProfileUpToDate) {
		this.MDMProfileUpToDate = MDMProfileUpToDate;
	}
	public Integer getSupportsGeoTracking() {
		return supportsGeoTracking;
	}
	public void setSupportsGeoTracking(Integer supportsGeoTracking) {
		this.supportsGeoTracking = supportsGeoTracking;
	}
	public Integer getDeviceOwnership() {
		return deviceOwnership;
	}
	public void setDeviceOwnership(Integer deviceOwnership) {
		this.deviceOwnership = deviceOwnership;
	}
	public Integer getHasPersistenceSupport() {
		return hasPersistenceSupport;
	}
	public void setHasPersistenceSupport(Integer hasPersistenceSupport) {
		this.hasPersistenceSupport = hasPersistenceSupport;
	}
	public String getESN() {
		return ESN;
	}
	public void setESN(String ESN) {
		this.ESN = ESN;
	}

	

}
