/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.ContentFiles;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.controllers.Computers;
import com.absolute.am.webapi.controllers.InHouseApps;
import com.absolute.am.webapi.controllers.MobileDevices;
import com.absolute.am.webapi.controllers.ViewHelper;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.StringUtilities;


/**
 * @author klavin
 *
 */
public class GetViewTest extends LoggedInTest {
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Ignoring because user defined views are not officially supported yet.
	public void can_get_user_defined_view_all_android_tablets() throws Exception {
		String deviceName1 = test.com.absolute.testdata.configuration.MobileDevices.MOBILE_DEVICE_NAMES[0];
		String deviceName2 = test.com.absolute.testdata.configuration.MobileDevices.MOBILE_DEVICE_NAMES[1];
		String[] deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, deviceName1, deviceName2);

		System.out.println("deviceIds=" + StringUtilities.arrayToString(deviceIds, ","));
		
		String view = "api/views/userdefined";

		String viewResult = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + view, 
				Helpers.USER_DEFINED_VIEW_ALL_ANDROID_TABLETS, 200, 299);
		System.out.println("viewResult = " + viewResult);
		Helpers.check_first_2_entries_of_resultset(viewResult, Long.parseLong(deviceIds[0]), deviceName1);
		Helpers.check_first_2_entries_of_resultset(viewResult, Long.parseLong(deviceIds[1]), deviceName2);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Ignoring because user defined views are not officially supported yet.
	public void can_get_user_defined_view_all_mobile_content() throws Exception {
		String view = "api/views/userdefined";

		String viewResult = Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + view, 
				Helpers.USER_DEFINED_VIEW_ALL_MOBILE_CONTENT, 200, 299);
		System.out.println("viewResult = " + viewResult);
		Helpers.check_first_2_entries_of_resultset(viewResult, 1290, "WebAPIUnitTestPDF File");
		Helpers.check_first_2_entries_of_resultset(viewResult, 1291, "WebAPIUnitTestPNG File");
	}

	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content_views() throws Exception {
		String view = "api/content/views";
		String contentViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(contentViews);
		System.out.println(view + " = " + contentViews);
		
		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(contentViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 1);
		assertTrue(descriptions[0].getViewName().compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content_views_all() throws Exception {
		String contentView = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/content/views/all", 
				200, 200);
		assertNotNull(contentView);
		assertTrue("check view contains " + ContentFiles.CONTENT_FILE_NAMES[0], contentView.contains(ContentFiles.CONTENT_FILE_NAMES[0]));		
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_content_views_other() throws Exception {
		String contentViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/content/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(contentViews);
	}

	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policies_views() throws Exception {
		String view = "api/policies/views";
		String policiesViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(policiesViews);
		System.out.println(view + " = " + policiesViews);

		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(policiesViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 3);  //all, smart & standard
		assertTrue(descriptions[0].getViewName().compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policies_views_all() throws Exception {
		String view = "api/policies/views/all";
		String policiesView = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(policiesView);
		System.out.println(view + " = " + policiesView);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(policiesView, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);
		
		// check that the built-in unit test policies exist.
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[0], policiesView.contains(Policies.STANDARD_POLICY_NAMES[0]));
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[1], policiesView.contains(Policies.STANDARD_POLICY_NAMES[1]));
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[2], policiesView.contains(Policies.STANDARD_POLICY_NAMES[2]));
		assertTrue("result contains " + Policies.SMART_POLICY_NAMES[0], policiesView.contains(Policies.SMART_POLICY_NAMES[0]));		
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_policies_views_other() throws Exception {
		String policiesViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/policies/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(policiesViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_mobile_devices_views() throws Exception {
		String view = "api/mobiledevices/views";
		String mdViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(mdViews);
		System.out.println(view + "= " + mdViews);
		
		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(mdViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length > 1);

		// Check descriptions contains the same values as allViews. As a view
		// is found, remove it from allViews. When the processing is finished allViews should be empty.
		ViewDescription[] viewDescriptions = MobileDevices.m_viewDescriptions;
		assertTrue(viewDescriptions.length == descriptions.length);
		for (ViewDescription description : descriptions) {
			assertTrue(ViewUtilities.isValidViewName(description.getViewName(), 
					viewDescriptions));
		}
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_mobile_devices_views_all() throws Exception {
		String deviceName1 = test.com.absolute.testdata.configuration.MobileDevices.MOBILE_DEVICE_NAMES[0];
		String deviceName2 = test.com.absolute.testdata.configuration.MobileDevices.MOBILE_DEVICE_NAMES[1];
		String view = "api/mobiledevices/views/all";
		String mdViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(mdViews);
		System.out.println(view + "= " + mdViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(mdViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);

		// Check that the built-in unit test devices exist
		assertTrue("Check that result contains entry for " + deviceName1, mdViews.contains(deviceName1));
		assertTrue("Check that result contains entry for " + deviceName2, mdViews.contains(deviceName2));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_mobile_devices_views_other() throws Exception {
		String mdViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/mobiledevices/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(mdViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_computers_views() throws Exception {
		String view = "api/computers/views";
		String computersViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(computersViews);
		System.out.println(view + "= " + computersViews);
		
		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(computersViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length > 1);

		// Check descriptions contains the same values as allViews. As a view
		// is found, remove it from allViews. When the processing is finished allViews should be empty.
		ViewDescription[] viewDescriptions = Computers.m_viewDescriptions;
		assertTrue(viewDescriptions.length == descriptions.length);
		for (ViewDescription description : descriptions) {
			assertTrue(ViewUtilities.isValidViewName(description.getViewName(), 
					viewDescriptions));
		}
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_computers_views_all() throws Exception {
		String computerName1 = test.com.absolute.testdata.configuration.Computers.COMPUTER_NAMES[0];
		String computerName2 = test.com.absolute.testdata.configuration.Computers.COMPUTER_NAMES[1];
		String view = "api/computers/views/all";
		String computersViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(computersViews);
		System.out.println(view + "= " + computersViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(computersViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);

		// Check that the built-in unit test devices exist
		assertTrue("Check that result contains entry for " + computerName1, computersViews.contains(computerName1));
		assertTrue("Check that result contains entry for " + computerName2, computersViews.contains(computerName2));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_computers_views_other() throws Exception {
		String computersViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/computers/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(computersViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_in_house_apps_views() throws Exception {
		String view = "api/inhouseapps/views";
		String inHouseAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(inHouseAppsViews);
		System.out.println(view + "= " + inHouseAppsViews);
		
		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(inHouseAppsViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 1);

		// Check descriptions contains the same values as allViews. As a view
		// is found, remove it from allViews. When the processing is finished allViews should be empty.
		ViewDescription[] viewDescriptions = InHouseApps.m_viewDescriptions;
		assertTrue(viewDescriptions.length == descriptions.length);
		for (ViewDescription description : descriptions) {
			assertTrue(ViewUtilities.isValidViewName(description.getViewName(), 
					viewDescriptions));
		}
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_getin_house_apps_views_all() throws Exception {
		String view = "api/inhouseapps/views/all";
		String inHouseAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(inHouseAppsViews);
		System.out.println(view + "= " + inHouseAppsViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(inHouseAppsViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);

		// Check that the built-in unit test devices exist
		assertTrue("Check that result contains entry for AbsoluteSafe", inHouseAppsViews.contains("AbsoluteSafe"));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_in_house_apps_views_other() throws Exception {
		String inHouseAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/inhouseapps/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(inHouseAppsViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_third_party_apps_views() throws Exception {

		String view = "api/thirdpartyapps/views";
		String thirdPartyAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(thirdPartyAppsViews);
		System.out.println(view + "= " + thirdPartyAppsViews);
		
		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(thirdPartyAppsViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 1);

		// Check descriptions contains the same values as allViews. As a view
		// is found, remove it from allViews. When the processing is finished allViews should be empty.
		ViewDescription[] viewDescriptions = InHouseApps.m_viewDescriptions;
		assertTrue(viewDescriptions.length == descriptions.length);
		for (ViewDescription description : descriptions) {
			assertTrue(ViewUtilities.isValidViewName(description.getViewName(), 
					viewDescriptions));
		}
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_getin_third_party_views_all() throws Exception {
		String view = "api/thirdpartyapps/views/all";
		String thirdPartyAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(thirdPartyAppsViews);
		System.out.println(view + "= " + thirdPartyAppsViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(thirdPartyAppsViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);

		// Check that the built-in unit test devices exist
		assertTrue("Check that result contains entry for WebAPI 3rd Party App 1", thirdPartyAppsViews.contains("WebAPI 3rd Party App 1"));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_third_party_apps_views_other() throws Exception {
		String thirdPartyAppsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + "api/thirdpartyapps/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(thirdPartyAppsViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_commands_history_views() throws Exception {
		String view = COMMANDS_API + "/history/views";
		String commandsHistoryViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(commandsHistoryViews);
		System.out.println(view + " = " + commandsHistoryViews);

		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(commandsHistoryViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 1);
		assertTrue(descriptions[0].getViewName().compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_commands_history_views_all() throws Exception {
		String view = COMMANDS_API + "/history/views/all?$top=10";	// There are typically thousands of entries in the command history, so limit result to 10.
		String commandsHistoryView = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(commandsHistoryView);
		System.out.println(view + " = " + commandsHistoryView);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(commandsHistoryView, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue("Check that there is at least one command in the history, unless it is a brand new environment, this should pass.", 
				result.getRows().length > 0);

	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_commands_history_views_other() throws Exception {
		String commandsHistoryViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/history/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(commandsHistoryViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_queued_commands_views() throws Exception {
		String view = COMMANDS_API + "/queued/views";
		String queuedCommandsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 200, 200);
		assertNotNull(queuedCommandsViews);
		System.out.println(view + " = " + queuedCommandsViews);

		ObjectMapper mapper = new ObjectMapper();
		ViewDescriptionList result = mapper.readValue(queuedCommandsViews, ViewDescriptionList.class);
		ViewDescription[] descriptions = result.getViewDescriptions();
		assertTrue(descriptions.length == 1);
		assertTrue(descriptions[0].getViewName().compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_queued_commands_views_all() throws Exception {
		String view = COMMANDS_API + "/queued/views/all";
		String queuedCommandsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(queuedCommandsViews);
		System.out.println(view + " = " + queuedCommandsViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(queuedCommandsViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		
		// Disabled verification for rows. May not have any records
		//assertTrue(result.getRows().length > 0);
		
		// check that the built-in unit test policies exist.
		assertTrue("result contains Status Description in metadata: The current status of the command.", 
				queuedCommandsViews.contains("The current status of the command."));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_queued_commands_views_other() throws Exception {
		String commandsHistoryViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + COMMANDS_API + "/queued/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(commandsHistoryViews);
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_actions_views_all() throws Exception {
		String view = ACTIONS_API + "/views/all";
		String actionsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + view, 
				200, 200);
		assertNotNull(actionsViews);
		System.out.println(view + " = " + actionsViews);
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(actionsViews, Result.class);
		assertTrue(result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue(result.getRows().length > 0);
		
		// check if specific meta data 'action description' exists in the returned result.
		assertTrue("result contains action description in metadata: The description of the action, as entered by the administrator.", 
				actionsViews.contains("The description of the action, as entered by the administrator."));
	}
	
	@Test	
	@Category(com.absolute.util.helper.FastTest.class)
	public void cant_get_actions_views_other() throws Exception {
		String actionsViews = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				Helpers.WEBAPI_BASE_URL + ACTIONS_API + "/views/other", 
				HttpStatus.SC_NOT_FOUND, HttpStatus.SC_NOT_FOUND);
		assertNotNull(actionsViews);
	}
}
