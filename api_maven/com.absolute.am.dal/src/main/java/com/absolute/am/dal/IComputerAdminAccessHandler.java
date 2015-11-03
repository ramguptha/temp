/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.dal;

/**
 * @author maboulkhoudoud
 * 
 */

public interface IComputerAdminAccessHandler {
	
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
	 * Returns true if the administrator has access to the computer with this id.
	 * This does not check the CanSeeAllRecords flag for the administrator.
	 * @param adminUUID the UUID of the administrator
	 * @param deviceId the record id of the computer
	 * @return true if this admin should be denied access, false otherwise.
	 * @throws Exception
	 */
	public boolean adminHasAccessToComputer(String adminUUID, long computerId) throws Exception;
	
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
	 * Refreshes data in webapi.admin_mobile_computers_webapi table
	 * that holds admin-to-computer assignments,
	 * including manual and smart appointments
	 * @param
	 * @return
	 * @throws Exception
	 */
	public void refreshAllAdmins() throws Exception;
	
	/**
	 * Refreshes data in webapi.admin_computers_webapi table
	 * that holds admin-to-computer assignments,
	 * including manual and smart appointments,
	 * for given computer
	 * @param computerId the ID of the computer (agent_info_id) to refresh Administrators data
	 * @return
	 * @throws Exception
	 */
	public void refreshAdminsForComputer(long deviceId) throws Exception;
}
