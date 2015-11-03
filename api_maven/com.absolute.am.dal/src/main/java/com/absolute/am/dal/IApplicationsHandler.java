/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.dal;

import java.util.List;
import java.util.UUID;

import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;

/**
 * @author klavin
 * 
 */


public interface IApplicationsHandler {

	public enum iconType {
	    thirdPartyApp, book
	}
	
	public iOSApplications getInHouseApplication(long applicationId) throws Exception;
	
	public iOSAppStoreApplications getThirdPartyApplication(long applicationId) throws Exception;
	
	public byte[] getIcon(long id, iconType type) throws Exception;
	
	public UUID[] getInHouseAppUniqueIds(List<Long> inHouseAppIds) throws Exception;
	
	public UUID[] getThirdPartyAppUniqueIds(List<Long> thirdPartyAppIds) throws Exception;

}

