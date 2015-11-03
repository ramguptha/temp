package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IConfigurationProfileHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.ConfigurationProfile;

public class TestConfigurationProfileHandler {
	
	private static final long IOS_CONFIGURATION_PROFILE_ID = 8;
	private static final String IOS_CONFIGURATION_PROFILE_UUID_FOR_NEW_DEVICE_CAMERA="2DBD81E1-36D7-4BF3-85E4-162E7F62C437";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_configuration_profile() throws Exception {
		IDal dal = Util.getDal();
		IConfigurationProfileHandler configurationProfileHandler = dal.getConfigurationProfileHandler();

		ConfigurationProfile configurationProfile = configurationProfileHandler.getConfigurationProfile(IOS_CONFIGURATION_PROFILE_ID);
		assertTrue(configurationProfile.getId() == IOS_CONFIGURATION_PROFILE_ID);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_configuration_profile_unique_ids() throws Exception {
		List<Long> configurationProfileIds = Arrays.asList(IOS_CONFIGURATION_PROFILE_ID);
		IDal dal = Util.getDal();
		IConfigurationProfileHandler configurationProfileHandler = dal.getConfigurationProfileHandler();

		UUID[] configurationProfileUuids = configurationProfileHandler.getConfigurationProfileUniqueIds(configurationProfileIds);
		assertTrue(configurationProfileUuids.length == configurationProfileIds.size());
		assertTrue(configurationProfileUuids[0].toString().compareToIgnoreCase(IOS_CONFIGURATION_PROFILE_UUID_FOR_NEW_DEVICE_CAMERA) == 0);
	}
}
