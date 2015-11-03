package com.absolute.am.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class UserDevice {
	
	private static final String enumTableForMobileDevicesModels = "enum_MobileDeviceModel",
			enumTableForDesktopDevicesModels = "enum_MachineModel",
			enumTableForDesktopDevicesPlatform = "enum_OSPlatform";
	
	private String deviceIdentifier, deviceName, deviceModel, phoneNumber, agentSerial, osPlatform, 
	batteryLevelModifiedDate, deviceSerialNumber;
	
	private Long deviceType, osVersion, deviceCapacity, batteryLevel, isTablet, passcodePresent,
	batteryCurrentCapacity, batteryDesignCapacity, batteryFullyCharged, batteryMaxCapacity;
	
	public UserDevice(Map<String, Object> device, Method getEnumValueForKey, Object enumHandler) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
				
		if( device.containsKey("DeviceTargetIdentifier")){
			deviceIdentifier = (String) device.get("DeviceTargetIdentifier");
		}
		
		if( device.containsKey("AgentSerial")){
			agentSerial = (String) device.get("AgentSerial");
		}
		
		if( device.containsKey("DeviceName")){
			deviceName = (String) device.get("DeviceName");
		}
		
		if( device.containsKey("DeviceType")){
			deviceType = (Long) device.get("DeviceType");
		}
		
		if( device.containsKey("DeviceModel")){
			if( deviceIdentifier != null ){ //mobile device
				Object[] parameters = {enumTableForMobileDevicesModels, device.get("DeviceModel").toString()};
				deviceModel = getEnumValueForKey.invoke(enumHandler, parameters).toString();
			} else { // desktop device
				Object[] parameters = {enumTableForDesktopDevicesModels, device.get("DeviceModel").toString()};
				deviceModel = getEnumValueForKey.invoke(enumHandler, parameters).toString();
			}
		}
		
		if( device.containsKey("OSVersion")){
			osVersion = (Long) device.get("OSVersion");
		}
		
		if( device.containsKey("PhoneNumber")){
			phoneNumber = (String) device.get("PhoneNumber");
		}
		
		if( device.containsKey("PasscodePresent")){
			passcodePresent = (Long) device.get("PasscodePresent");
		}
		
		if( device.containsKey("DeviceCapacity")){
			deviceCapacity = (Long) device.get("DeviceCapacity");
		}
		
		if( device.containsKey("BatteryLevel")){
			batteryLevel = (Long) device.get("BatteryLevel");
		}
		
		if( device.containsKey("IsTablet")){
			isTablet = (Long) device.get("IsTablet");
		}
		
		if( device.containsKey("BatteryCurrentCapacity")){
			batteryCurrentCapacity = (Long) device.get("BatteryCurrentCapacity");
		}
		
		if( device.containsKey("BatteryDesignCapacity")){
			batteryDesignCapacity = (Long) device.get("BatteryDesignCapacity");
		}
		
		if( device.containsKey("BatteryFullyCharged")){
			batteryFullyCharged = (Long) device.get("BatteryFullyCharged");
		}
		
		if( device.containsKey("BatteryMaxCapacity")){
			batteryMaxCapacity = (Long) device.get("BatteryMaxCapacity");
		}
		
		if( device.containsKey("OSPlatform")){
			Object[] parameters = {enumTableForDesktopDevicesPlatform, device.get("OSPlatform").toString()};
			osPlatform = getEnumValueForKey.invoke(enumHandler, parameters).toString();
		}
		
		if( device.containsKey("BatteryLevelModifiedDate")){
			batteryLevelModifiedDate = (String) device.get("BatteryLevelModifiedDate");
		}
		
		if( device.containsKey("DeviceSerialNumber")){
			deviceSerialNumber = (String) device.get("DeviceSerialNumber");
		}
	}
	
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}
	
    /**
	 * The device SerialNumber
	 */
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}
	
	public void setBatteryCurrentCapacity(Long batteryCurrentCapacity) {
		this.batteryCurrentCapacity = batteryCurrentCapacity;
	}
	
    /**
	 * The battery current capacity
	 */
	public Long getBatteryCurrentCapacity() {
		return batteryCurrentCapacity;
	}
	
	public void setBatteryDesignCapacity(Long batteryDesignCapacity) {
		this.batteryDesignCapacity = batteryDesignCapacity;
	}
	
    /**
	 * The battery design capacity
	 */
	public Long getBatteryDesignCapacity() {
		return batteryDesignCapacity;
	}
	
	public void setBatteryFullyCharged(Long batteryFullyCharged) {
		this.batteryFullyCharged = batteryFullyCharged;
	}
	
    /**
	 * The battery fully charged
	 */
	public Long getBatteryFullyCharged() {
		return batteryFullyCharged;
	}
	
	public void setBatteryMaxCapacity(Long batteryMaxCapacity) {
		this.batteryMaxCapacity = batteryMaxCapacity;
	}
	
    /**
	 * The battery MaxCapacity
	 */
	public Long getBatteryMaxCapacity() {
		return batteryMaxCapacity;
	}
	
	public void setBatteryLevelModifiedDate(String batteryLevelModifiedDate) {
		this.batteryLevelModifiedDate = batteryLevelModifiedDate;
	}
	
    /**
	 * The battery level modified date
	 */
	public String getBatteryLevelModifiedDate() {
		return batteryLevelModifiedDate;
	}
	
	public void setOsPlatform(String osPlatform) {
		this.osPlatform = osPlatform;
	}
	
    /**
	 * The OsPlatform
	 */
	public String getOsPlatform() {
		return osPlatform;
	}
	
    /**
	 * The OsVersion
	 */
	public Long getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(Long osVersion) {
		this.osVersion = osVersion;
	}
	
    /**
	 * The phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
    /**
	 * The passcode present
	 */
	public Long getPasscodePresent() {
		return passcodePresent;
	}

	public void setPasscodePresent(Long passcodePresent) {
		this.passcodePresent = passcodePresent;
	}
	
    /**
	 * The device capacity
	 */
	public Long getDeviceCapacity() {
		return deviceCapacity;
	}

	public void setDeviceCapacity(Long deviceCapacity) {
		this.deviceCapacity = deviceCapacity;
	}
	
    /**
	 * The agent serial number
	 */
	public String getAgentSerial() {
		return agentSerial;
	}

	public void setAgentSerial(String agentSerial) {
		this.agentSerial = agentSerial;
	}
	
    /**
	 * The device identifier
	 */
	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}
	
    /**
	 * The battery level
	 */
	public Long getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Long batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	
    /**
	 * The device name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
    /**
	 * The device type
	 */
	public Long getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Long deviceType) {
		this.deviceType = deviceType;
	}
	
    /**
	 * The device model
	 */
	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	
    /**
	 * The tablet device
	 */
	public Long getIsTablet() {
		return isTablet;
	}

	public void setIsTablet(Long isTablet) {
		this.isTablet = isTablet;
	}
}
