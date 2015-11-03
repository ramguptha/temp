package test.com.absolute.am.webapi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class InfoItemsTest extends LoggedInTest{

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_info_items_for_smart_policies() throws Exception {		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_MOBILE_DEVICE, 200, 200);
		
		assertTrue(resultAsString.contains("408A8D10-D908-4A9E-A00C-3FFB27E7EA81"));
		assertTrue(resultAsString.contains("8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5"));
		assertTrue(resultAsString.contains("EFD8C1F6-770D-4C5B-B502-AE74A50B1D42"));

		assertTrue(resultAsString.contains("enum_AgentPlatform"));
		assertTrue(resultAsString.contains("OS X"));

	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_info_items_for_smart_policies_By_IA() throws Exception {		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_IA, 200, 200);
		
		assertTrue(resultAsString.contains("5C7C9375-88D7-479F-A27A-4C1E038E8746"));
		assertTrue(resultAsString.contains("233FF13A-0A51-422E-85E5-FF19281B3966"));

		assertTrue(resultAsString.contains("Mobile Device Installed App Name"));
		assertTrue(resultAsString.contains("Mobile Device Installed App Bundle Identifier"));
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_info_items_for_smart_policies_By_ICP() throws Exception {		
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + INFO_ITEMS_FILTER_CRITERIA_SMART_POLICIES_API_BY_ICP, 200, 200);
		
		assertTrue(resultAsString.contains("B78AAB04-4384-431F-A473-C555DDC649DD"));
		assertTrue(resultAsString.contains("B2D31F8A-BE85-442C-83B2-BA0E1579EBC6"));
		assertTrue(resultAsString.contains("E2AE18C1-B9AC-49C0-89A3-B7F410038D37"));
		assertTrue(resultAsString.contains("6AA7C2C9-C66B-47AE-8481-07C6D551CD4B"));
		assertTrue(resultAsString.contains("0B2D8180-E77E-4736-A329-F7CB83A5BB77"));
		assertTrue(resultAsString.contains("4543AD1C-A764-4288-B672-110EE7A9A548"));
		assertTrue(resultAsString.contains("A3EAFEBA-833A-4F7B-AA66-74FC11A669A3"));

		assertTrue(resultAsString.contains("Mobile Device Installed Profile Name"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile Description"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile Organization"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile Identifier"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile UUID"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile Encrypted"));
		assertTrue(resultAsString.contains("Mobile Device Installed Profile Managed"));

	}

	
}
