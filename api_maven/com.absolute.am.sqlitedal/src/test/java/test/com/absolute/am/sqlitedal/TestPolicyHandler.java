package test.com.absolute.am.sqlitedal;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;





import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.model.iOsPolicies;

import static org.junit.Assert.*;

public class TestPolicyHandler {
	// Note: These tests are running against a saved local copy of AdminDatabase. 
	// So device id=15 exists in that database and its got policies 1 and 28
	// assigned to it.

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_non_smart_policy_ids_for_devices() throws Exception {
		IDal dal = Util.getDal();
		IPolicyHandler policyHandler = dal.getPolicyHandler();
	
		String deviceId = "7F5CC5EB-7584-46D3-B427-1D2E9434B02E";  // = 8,6 (canned)
//		String deviceId = "7F5CC5EB-7584-46D3-B427-1D2E9434B02E";  // = 10, 21

		String policyIdsAsString = policyHandler.getNonSmartPolicyIdsForDeviceAsString(deviceId);
		assertNotNull(policyIdsAsString);
		assertTrue(policyIdsAsString.length() > 0);
		String[] smartPolicyIds = policyIdsAsString.split(",");		
		String[] knownSmartPolicyIds = {"6", "8"};
		assertArrayEquals(smartPolicyIds, knownSmartPolicyIds);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore //TODO: fix later
	public void can_get_smart_policy_ids_for_devices() throws Exception {		
		IDal dal = Util.getDal();

		IPolicyHandler policyHandler = dal.getPolicyHandler();

		Long deviceId = (long) 15;
		String smartPolicyIdsAsString = policyHandler.getSmartPolicyIdsForDeviceAsString(deviceId);
		assertNotNull(smartPolicyIdsAsString);
		assertTrue(smartPolicyIdsAsString.length() > 0);
		String[] smartPolicyIds = smartPolicyIdsAsString.split(",");		
		String[] knownSmartPolicyIds = {"4", "28"};
		assertArrayEquals(smartPolicyIds, knownSmartPolicyIds);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policy() throws Exception {
		IDal dal = Util.getDal();
		IPolicyHandler policyHandler = dal.getPolicyHandler();

		iOsPolicies policy = policyHandler.getPolicy((long)25);
		assertTrue(policy.getName().compareToIgnoreCase("webAPIUnitTest1") == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policy_ids_for_media() throws Exception {
		IDal dal = Util.getDal();

		IPolicyHandler policyHandler = dal.getPolicyHandler();
		String mediaId = "2FF4ADC2-E686-40A5-994E-91F74189AD8C";  // = 8,15 (canned)
//		String mediaId = "2FF4ADC2-E686-40A5-994E-91F74189AD8C";  // = Visio-AMWebUI_SystemComponents_2 (8, 23, 36, 24, 15)

		String policyIdsAsString = policyHandler.getPolicyIdsForMediaAsString(mediaId);
		assertNotNull(policyIdsAsString);
		assertTrue(policyIdsAsString.length() > 0);
		String[] policyIds = policyIdsAsString.split(",");		
		String[] knownPolicyIds = {"8", "15"};
		assertArrayEquals(policyIds, knownPolicyIds);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policy_uuids_as_string() throws Exception {
		List<Long> policyIds = Arrays.asList((long)15);
		IDal dal = Util.getDal();

		IPolicyHandler policyHandler = dal.getPolicyHandler();

		String[] uuids = policyHandler.getPolicyUniqueIdsAsString(policyIds);
		assertNotNull(uuids);
		assertTrue(uuids.length == policyIds.size());
		assertTrue(uuids[0].length() > 0);
	}
}
