/**
 * 
 */
package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.webapi.util.ResourceUtilities;

import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;


/**
 * @author klavin
 *
 */
public class PolicyIdGetTest extends LoggedInTest {
	private static final String NONEXISTING_POLICY_ID = "123456789";
	
	// following test case test all the endpoints with policy views, i.e. all, smart & standard
	String[] getPolicyViewUrls = {
			POLICIES_API + "/views/all",
			POLICIES_API + "/views/smart",
			POLICIES_API + "/views/standard"
	};

	// following test case test all the endpoints with single policy
	String[] getPolicyInfoUrls = {
			POLICIES_API + "/{policyId}/devices",
			POLICIES_API + "/{policyId}/content",
			POLICIES_API + "/{policyId}/configurationprofiles",
			POLICIES_API + "/{policyId}/inhouseapps",
			POLICIES_API + "/{policyId}/thirdpartyapps",
			POLICIES_API + "/{policyId2}/actions"    // the 'smart policy 1' should be used since actions can only be assigned to smart policies.
	};
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_views() throws Exception {
		for (String policyUrl : getPolicyViewUrls){
			Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + policyUrl,
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
	
		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cannot_get_views_for_no_existing_view_name() throws Exception {
		String noExistingViewName = "NoExistingViewName";
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("VIEW_NOT_FOUND", null, locale, ResourceUtilities.WEBAPI_BASE), 
				noExistingViewName);
		String response = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/views/" + noExistingViewName,
					HttpStatus.SC_NOT_FOUND,
					HttpStatus.SC_NOT_FOUND);
	
		Assert.assertTrue(response.contains(expectedErrorMessage));
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_info_for_policy() throws Exception {
		// this test case tests end point /api/policies/id GET

		String policyIds[] = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.SMART_POLICY_NAMES[0]);
		// policy WebAPI Standard 1
		String resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + policyIds[0], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyIds[0]), Policies.STANDARD_POLICY_NAMES[0]);
		
		// policy WebAPI Smart 1
		resultAsString = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + POLICIES_API + "/" + policyIds[1], 200, 200);
		Helpers.check_first_2_entries_of_resultset(resultAsString, Integer.parseInt(policyIds[1]), Policies.SMART_POLICY_NAMES[0]);
		
		for (String policyUrl : getPolicyInfoUrls){
			policyUrl = policyUrl.replace("{policyId}", policyIds[0]).replace("{policyId2}",policyIds[1]);
			Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + policyUrl,
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
	
		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cannot_get_info_for_non_existing_policy_id() throws Exception {
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("NO_POLICY_FOUND_FOR_ID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_POLICY_ID);
		
		for (String policyUrl : getPolicyInfoUrls){
			policyUrl = policyUrl.replace("{policyId}", NONEXISTING_POLICY_ID).replace("{policyId2}", NONEXISTING_POLICY_ID);
			String response = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + policyUrl,
					HttpStatus.SC_NOT_FOUND,
					HttpStatus.SC_NOT_FOUND);
			
			Assert.assertTrue(response.contains(expectedErrorMessage));
		}
	
		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
}
