package test.com.absolute.am.sqlitedal;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IDeviceHandler;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.dal.model.IPhoneInstalledConfigurationProfile;
import com.absolute.am.dal.model.IPhoneInstalledProvisioningProfile;
import com.absolute.am.dal.model.IPhoneInstalledSoftwareInfo;

public class TestDeviceHandler {

	private static final long DEVICE_ID_TOSHIBA_TABLET = 9;
	private static final String DEVICE_NAME_TOSHIBA_TABLET = "Toshiba Tablet";
	private static final long INSTALL_SOFTWARE_ID_CAMERA = 730;
	private static final long INSTALL_PROFILE_ID_CHANGE_PASSWORD = 8;
	private static final long INSTALL_PROVISIONING_PROFILE_ID_ABSOLUTE_APPS_2011 = 3;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_device() throws Exception {
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();

		IPhoneInfo device = deviceHandler.getDevice(DEVICE_ID_TOSHIBA_TABLET);
		assertTrue(device.getId() == DEVICE_ID_TOSHIBA_TABLET);
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_device_for_name() throws Exception {
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();

		List<IPhoneInfo> deviceList = deviceHandler.getDeviceForName(DEVICE_NAME_TOSHIBA_TABLET);
		assertTrue(deviceList.size() == 1);
		assertTrue(((IPhoneInfo) deviceList.toArray()[0]).getDisplayName().compareToIgnoreCase(DEVICE_NAME_TOSHIBA_TABLET) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_device_uuids_as_string() throws Exception {
		List<Long> deviceIds = Arrays.asList(DEVICE_ID_TOSHIBA_TABLET);
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();

		String[] deviceUuids = deviceHandler.getMobileDeviceUniqueIdsAsString(deviceIds);
		assertTrue(deviceUuids.length == deviceIds.size());
		assertTrue(deviceUuids[0].length() > 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_details_for_installed_software_id() throws Exception {
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();

		IPhoneInstalledSoftwareInfo info = deviceHandler.getDetailsForInstalledSoftwareId(INSTALL_SOFTWARE_ID_CAMERA);
		assertNotNull(info);
		assertTrue(info.getId() == INSTALL_SOFTWARE_ID_CAMERA);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_details_for_installed_configuration_profile_id() throws Exception {
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();

		IPhoneInstalledConfigurationProfile profile = deviceHandler.getDetailsForInstalledConfigurationProfileId(INSTALL_PROFILE_ID_CHANGE_PASSWORD);
		assertNotNull(profile);
		assertTrue(profile.getId() == INSTALL_PROFILE_ID_CHANGE_PASSWORD);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_details_for_installed_provisioning_profile_id() throws Exception {
		IDal dal = Util.getDal();
		IDeviceHandler deviceHandler = dal.getDeviceHandler();
		 
		IPhoneInstalledProvisioningProfile profile = deviceHandler.getDetailsForInstalledProvisioningProfileId(INSTALL_PROVISIONING_PROFILE_ID_ABSOLUTE_APPS_2011);
		assertNotNull(profile);
		assertTrue(profile.getId() == INSTALL_PROVISIONING_PROFILE_ID_ABSOLUTE_APPS_2011);
	}
}
