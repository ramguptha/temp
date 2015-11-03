package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IApplicationsHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;

public class TestApplicationsHandler {
	private static final long IOS_APPLICATION_ID = 1;	//in-house application
	private static final long iOS__APPSTORE_APPLICATION_ID = 1; // 3rd party application
	private static final String IOS_APPLICATION_UNIQUE_ID_FOR_HELP_DESK = "D65E2751-02EC-4AAF-A55D-276E1C57C1AE";
	private static final String IOS_APPSTORE_APPLICATION_UNIQUE_ID_FOR_ASTRO = "0DCC956E-22BA-4D8B-8C13-032E09173673";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_in_house_application() throws Exception {
		IDal dal = Util.getDal();
		 
		IApplicationsHandler applicationsHandler = dal.getApplicationsHandler();

		iOSApplications app = applicationsHandler.getInHouseApplication(IOS_APPLICATION_ID);
		assertTrue(app.getId() == IOS_APPLICATION_ID);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_third_party_application() throws Exception {
		IDal dal = Util.getDal();
		 
		IApplicationsHandler applicationsHandler = dal.getApplicationsHandler();

		iOSAppStoreApplications app = applicationsHandler.getThirdPartyApplication(iOS__APPSTORE_APPLICATION_ID);
		assertTrue(app.getId() == iOS__APPSTORE_APPLICATION_ID);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_third_party_application_icon() throws Exception {
		IDal dal = Util.getDal();
		 
		IApplicationsHandler applicationsHandler = dal.getApplicationsHandler();

		byte[] iconImageAsByte = applicationsHandler.getIcon(iOS__APPSTORE_APPLICATION_ID, IApplicationsHandler.iconType.thirdPartyApp);
		assertTrue(iconImageAsByte.length > 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_in_house_application_uuids() throws Exception {
		List<Long> applicationIds = Arrays.asList(IOS_APPLICATION_ID);
		IDal dal = Util.getDal();
		 
		IApplicationsHandler applicationsHandler = dal.getApplicationsHandler();

		UUID[] uuids = applicationsHandler.getInHouseAppUniqueIds(applicationIds);
		assertTrue(uuids.length == 1);
		assertTrue(uuids[0].toString().compareToIgnoreCase(IOS_APPLICATION_UNIQUE_ID_FOR_HELP_DESK) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_third_party_application_uuids() throws Exception {
		List<Long> applicationIds = Arrays.asList(iOS__APPSTORE_APPLICATION_ID);
		IDal dal = Util.getDal();
		 
		IApplicationsHandler applicationsHandler = dal.getApplicationsHandler();

		UUID[] uuids = applicationsHandler.getThirdPartyAppUniqueIds(applicationIds);
		assertTrue(uuids.length == 1);
		assertTrue(uuids[0].toString().compareToIgnoreCase(IOS_APPSTORE_APPLICATION_UNIQUE_ID_FOR_ASTRO) == 0);
	}
}
