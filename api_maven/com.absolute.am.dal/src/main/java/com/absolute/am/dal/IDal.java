/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.dal;

import java.util.Properties;

/**
 * Interface to an AM compatible Data Access Layer.
 *
 */
public interface IDal {

	/**
	 * Set the run time properties for this database accessor
	 * @param runtimeProperties
	 * @param noSync
	 */
	public void initialize(Properties runtimeProperties, boolean noSync);
	
	/**
	 * Get a handler for executing views on this database
	 * @return
	 * @throws Exception
	 */
	public IViewHandler getViewHandler() throws Exception;
	
	/**
	 * Instantiate UserPrefHandler object.
	 * @return
	 * @throws Exception
	 */
	public IUserPrefHandler getUserPrefHandler() throws Exception;
		
	/**
	 * Instantiate ContentHandler object.
	 * @return
	 * @throws Exception
	 */
	public IContentHandler getContentHandler() throws Exception;

	/**
	 * Instantiate PolicyHandler object.
	 * @return
	 * @throws Exception
	 */
	public IPolicyHandler getPolicyHandler() throws Exception;
	
	/**
	 * Instantiate DeviceHandler object.
	 * @return
	 * @throws Exception
	 */
	public IDeviceHandler getDeviceHandler() throws Exception;
	
	/**
	 * Instantiate AdminHandler object.
	 * @return
	 * @throws Exception
	 */
	public IAdminAccessHandler getAdminAccessHandler() throws Exception;
	
	/**
	 * Instantiate IApplicationsHandler object
	 * @return
	 * @throws Exception
	 */
	public IApplicationsHandler getApplicationsHandler() throws Exception;
	
	/**
	 * Instantiate IConfigurationProfileHandler object.
	 * @return
	 * @throws Exception
	 */
	public IConfigurationProfileHandler getConfigurationProfileHandler() throws Exception;
	
	/**
	 * Instantiate IProvisioningProfileHandler object.
	 * @return
	 * @throws Exception
	 */
	public IProvisioningProfileHandler getProvisioningProfileHandler() throws Exception;

	/**
	 * Instantiate EnumHandler object.
	 * @return
	 * @throws Exception
	 */
	public IEnumHandler getEnumHandler() throws Exception;
	
	/**
	 * Instantiate InfoItemHandler object.
	 * @return
	 * @throws Exception
	 */
	public IInfoItemHandler getInfoItemHandler() throws Exception;

	/**
	 * Instantiate GlobalDataHandler object.
	 * @return
	 * @throws Exception
	 */
	public IGlobalDataHandler getGlobalDataHandler() throws Exception;
	
	/**
	 * Instantiate AdminHandler object.
	 * @return
	 * @throws Exception
	 */
	public IComputerAdminAccessHandler getComputerAdminAccessHandler() throws Exception;
	
	/**
	 * Instantiate ComputerHandler object.
	 * @return
	 * @throws Exception
	 */
	public IComputerHandler getComputerHandler() throws Exception;
	
	/**
	 * Instantiate ActionHandler object.
	 * @return
	 * @throws Exception
	 */
	public IActionHandler getActionHandler() throws Exception;
	
	/**
	 * Instantiate CustomFieldHandler object.
	 * @return
	 * @throws Exception
	 */
	public ICustomFieldHandler getCustomFieldHandler() throws Exception;	
}
