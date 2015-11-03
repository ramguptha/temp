package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.Actions;
import test.com.absolute.testutil.Helpers;

import com.absolute.am.webapi.util.ResourceUtilities;

/**
 * @author rchen
 *
 */

public class ActionGetsTest extends LoggedInTest {

	private static final String NONEXISTING_ACTION_ID = "123456789";
	private static final String[] ACTION_URLS = {
			Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/{action_id}",
			Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/{action_id}/policies"
		};

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_action_id() throws Exception
	{
		String[] actionIds = Helpers.getActionIdsForActionNames(logonCookie, Actions.ACTION_NAMES[0]);
		for (String url : ACTION_URLS) {
			Helpers.doGETCheckStatusReturnBody(logonCookie, url.replace("{action_id}", actionIds[0]),
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cannot_get_for_non_existing_action_id() throws Exception
	{
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("NO_ACTION_FOUND_FOR_ID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_ACTION_ID);
		
		for (String url : ACTION_URLS) {
			String result = Helpers.doGETCheckStatusReturnBody(logonCookie, url.replace("{action_id}", NONEXISTING_ACTION_ID),
					HttpStatus.SC_NOT_FOUND,
					HttpStatus.SC_NOT_FOUND);
			
			Assert.assertTrue(result.contains(expectedErrorMessage));
		}
	
		//test passed if reached this line
		Assert.assertTrue(true);
	}

}
