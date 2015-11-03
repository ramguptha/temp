package com.absolute.am.dal;

import java.util.List;
import java.util.UUID;

import com.absolute.am.dal.model.ConfigurationProfile;

public interface IConfigurationProfileHandler {
	
	public ConfigurationProfile getConfigurationProfile(long configurationProfileId) throws Exception;
	
	public UUID[] getConfigurationProfileUniqueIds(List<Long> configurationProfileIds) throws Exception;

}
