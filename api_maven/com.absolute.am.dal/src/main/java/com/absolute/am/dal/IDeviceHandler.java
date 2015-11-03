/**
 * 
 */
package com.absolute.am.dal;

import java.util.List;

import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.dal.model.IPhoneInstalledConfigurationProfile;
import com.absolute.am.dal.model.IPhoneInstalledProvisioningProfile;
import com.absolute.am.dal.model.IPhoneInstalledSoftwareInfo;

/**
 * @author klavin
 * 
 */

public interface IDeviceHandler {

	/**
	 * Get all the details for a given device
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	public IPhoneInfo getDevice(long deviceId) throws Exception; 
	
	/**
	 * Get all the devices for a given device name
	 * @param deviceName
	 * @return
	 * @throws Exception
	 */
	public List<IPhoneInfo> getDeviceForName(String deviceName) throws Exception;
	
	/**
	 * Get UniqueIds for a given list of device Ids
	 * @param deviceIds
	 * @return UniqueIds
	 * @throws Exception
	 */
	public String[] getMobileDeviceUniqueIdsAsString(List<Long> deviceIds) throws Exception; 	

	/**
	 * Get the IPhoneInstalledSoftwareInfo for the installed software with id 
	 * @param installedSoftwareId
	 * @return
	 * @throws Exception
	 */
	public IPhoneInstalledSoftwareInfo getDetailsForInstalledSoftwareId(long installedSoftwareId) throws Exception;

	/**
	 * Get the IPhoneInstalledConfigurationProfile for the installed configuration profile with id 
	 * @param configurationProfileId
	 * @return IPhoneInstalledConfigurationProfile
	 * @throws Exception
	 */
	public IPhoneInstalledConfigurationProfile getDetailsForInstalledConfigurationProfileId(long configurationProfileId) throws Exception;

	/**
	 * Get the IPhoneInstalledProvisioningProfile for the installed provisioning profile with id 
	 * @param provisioningProfileId
	 * @return IPhoneInstalledProvisioningProfile
	 * @throws Exception
	 */
	public IPhoneInstalledProvisioningProfile getDetailsForInstalledProvisioningProfileId(long provisioningProfileId) throws Exception;
}
