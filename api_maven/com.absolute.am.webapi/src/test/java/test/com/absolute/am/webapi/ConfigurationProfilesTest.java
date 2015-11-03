package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

public class ConfigurationProfilesTest extends LoggedInTest {
	
	private static final String CONFIGURATION_PROFILES_VIEWS_API = Helpers.WEBAPI_BASE_URL + "api/configurationprofiles/views";
	public static String cookie = null;

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws IllegalStateException, IOException,
			KeyManagementException, NoSuchAlgorithmException {
		can_get_list_of_views();
		can_get_view_all();
		cannot_get_view_nonexisting();
	}

	public void can_get_list_of_views() throws IllegalStateException,
			IOException, KeyManagementException, NoSuchAlgorithmException {

		StringBuilder views = new StringBuilder();
		views.append("{");
		views.append("\"viewDescriptions\":[{\"viewName\":\"all\",\"viewDisplayName\":\"All Configuration Profiles\",\"id\":0}]");
		views.append("}");
		String expected = views.toString();

		String actual = test.com.absolute.testutil.Helpers
				.doGETCheckStatusReturnBody(logonCookie, CONFIGURATION_PROFILES_VIEWS_API, 
						HttpStatus.SC_OK,
						HttpStatus.SC_OK);

		boolean isSame = (actual.compareToIgnoreCase(expected) == 0);

		Assert.assertTrue(isSame);
	}

	public void can_get_view_all() throws IllegalStateException, IOException,
			KeyManagementException, NoSuchAlgorithmException {

		String actual = test.com.absolute.testutil.Helpers
				.doGETCheckStatusReturnBody(logonCookie, CONFIGURATION_PROFILES_VIEWS_API + "/all",
						HttpStatus.SC_OK, 
						HttpStatus.SC_OK);

		boolean isExpectedResult = (actual.indexOf("{\"metaData\":{") == 0 && actual.indexOf("\"rows\":[") > 0);
		Object[] rows = Helpers.get_rows_from_result(actual);

		Assert.assertTrue(isExpectedResult);
		Assert.assertTrue(rows.length > 0);
	}

	public void cannot_get_view_nonexisting() throws IllegalStateException,
			IOException, KeyManagementException, NoSuchAlgorithmException {

		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(logonCookie, 
				CONFIGURATION_PROFILES_VIEWS_API + "/nonexisting_view", 
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);

		// test passed if reached this line
		Assert.assertTrue(true);
	}
}