package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IProvisioningProfileHandler;
import com.absolute.am.dal.model.ProvisioningProfile;

public class TestProvisioningProfileHandler {
	
	private static final long PROVISIONING_PROFILE_ID_HELPDESK = 1;
	private static final String PROVISIONING_PROFILE_UUID_HELPDESK = "BDB799AB-5CB6-453E-A6B0-6FA47EBA5397";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_provisioning_profile() throws Exception {
		IDal dal = Util.getDal();
		
		IProvisioningProfileHandler provisioningProfileHandler = dal.getProvisioningProfileHandler();

		ProvisioningProfile profile = provisioningProfileHandler.getProvisioningProfile(PROVISIONING_PROFILE_ID_HELPDESK);
		
		assertNotNull(profile);
		assertTrue(profile.getId() == PROVISIONING_PROFILE_ID_HELPDESK);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_provisioning_profile_unique_ids() throws Exception {
		List<Long> provisioningProfileIds = Arrays.asList(PROVISIONING_PROFILE_ID_HELPDESK);
		IDal dal = Util.getDal();
		
		IProvisioningProfileHandler provisioningProfileHandler = dal.getProvisioningProfileHandler();

		UUID[] uuids = provisioningProfileHandler.getProvisioningProfileUniqueIds(provisioningProfileIds);
		
		assertNotNull(uuids);
		assertTrue(uuids[0].toString().compareToIgnoreCase(PROVISIONING_PROFILE_UUID_HELPDESK) == 0);
	}	
	
}
