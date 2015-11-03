/**
 * 
 */
package com.absolute.am.dal;

/**
 * @author dlavin
 * 
 */

public interface IAdminAccessHandler {

	/**
	 * Prepares the system for access by a specific administrator. The DAL will initialize
	 * any internal structures/tables, etc. required to implement restricted access restrictions
	 * to various device data. 
	 * @param adminUUID the UUID of the Administrator
	 * @return
	 * @throws Exception
	 */
	public void prepareAccessForAdmin(String adminUUID) throws Exception; 
	
	/**
	 * Returns true if the administrator has access to the device with this id.
	 * This does not check the CanSeeAllRecords flag for the administrator.
	 * @param adminUUID the UUID of the administrator
	 * @param deviceId the record id of the device
	 * @return true if this admin should be denied access, false otherwise.
	 * @throws Exception
	 */
	public boolean adminHasAccessToDevice(String adminUUID, long deviceId) throws Exception;
	
	/**
	 * Refreshes the system for access by a specific administrator to take into account any
	 * changes since the last call to prepareAccessForAdmin(). The DAL should update
	 * any internal structures/tables, etc. required to implement restricted access 
	 * to various device data. 
	 * @param adminUUID the UUID of the Administrator
	 * @return
	 * @throws Exception
	 */	
	public void refreshAccessForAdmin(String adminUUID) throws Exception;
	
	/**
	 * Refreshes data in webapi.admin_mobile_devices_webapi table
	 * that holds admin-to-mobile device assignments,
	 * including manual and smart appointments
	 * @param
	 * @return
	 * @throws Exception
	 */
	public void refreshAllAdmins() throws Exception;
	
	/**
	 * Refreshes data in webapi.admin_mobile_devices_webapi table
	 * that holds admin-to-mobile device assignments,
	 * including manual and smart appointments,
	 * for given device
	 * @param deviceId the ID of the device (iphone_info_id) to refresh Administrators data
	 * @return
	 * @throws Exception
	 */
	public void refreshAdminsForDevice(long deviceId) throws Exception;
	
}
