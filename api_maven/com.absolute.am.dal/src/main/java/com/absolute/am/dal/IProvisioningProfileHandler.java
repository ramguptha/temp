/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.dal;

import java.util.List;
import java.util.UUID;

import com.absolute.am.dal.model.ProvisioningProfile;

public interface IProvisioningProfileHandler {
	
	public ProvisioningProfile getProvisioningProfile(long provisioningProfileId) throws Exception;
	
	public UUID[] getProvisioningProfileUniqueIds(List<Long> provisioningProfileIds) throws Exception;

}
