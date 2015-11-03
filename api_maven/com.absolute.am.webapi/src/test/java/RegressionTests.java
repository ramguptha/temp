import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.com.absolute.am.webapi.ComputerGetsTest;
import test.com.absolute.am.webapi.ContentBatch;
import test.com.absolute.am.webapi.ContentForDevices;
import test.com.absolute.am.webapi.ContentGets;
import test.com.absolute.am.webapi.ContentIcons;
import test.com.absolute.am.webapi.DevicesForContent;
import test.com.absolute.am.webapi.ExceptionHandlingTest;
import test.com.absolute.am.webapi.GetViewTest;
import test.com.absolute.am.webapi.JobStatusTest;
import test.com.absolute.am.webapi.MobileDevicesApplications;
import test.com.absolute.am.webapi.MobileDevicesIdGetTest;
import test.com.absolute.am.webapi.PolicyContentAddTest;
import test.com.absolute.am.webapi.PolicyContentDeleteTest;
import test.com.absolute.am.webapi.PolicyIdGetTest;
import test.com.absolute.am.webapi.PolicyMobileDeviceTest;
import test.com.absolute.am.webapi.RemoveMediaTest;
import test.com.absolute.am.webapi.SendClearPasscodeCommandTest;
import test.com.absolute.am.webapi.SendSetRoamingOptionsCommandTest;
import test.com.absolute.am.webapi.SendUpdateDeviceInfoCommandTest;
import test.com.absolute.am.webapi.UpdateMediaTest;
import test.com.absolute.am.webapi.UserPrefsTest;

/**
 * This is a suite of test cases that should be used as a regression test on a new build.
 * @author dlavin
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	ComputerGetsTest.class,
	ContentBatch.class, 
	ContentForDevices.class,
	ContentGets.class,
	ContentIcons.class,
	DevicesForContent.class,
	ExceptionHandlingTest.class,
	GetViewTest.class,
	JobStatusTest.class,
	MobileDevicesApplications.class,
	MobileDevicesIdGetTest.class,
//	MobileDevicesCustomFieldsTest.class,
	PolicyContentAddTest.class,
	PolicyContentDeleteTest.class,
	PolicyIdGetTest.class,
	PolicyMobileDeviceTest.class,
	RemoveMediaTest.class,
	SendClearPasscodeCommandTest.class,
	SendSetRoamingOptionsCommandTest.class,
	SendUpdateDeviceInfoCommandTest.class,
	UpdateMediaTest.class,
	UserPrefsTest.class})
public class RegressionTests {

}
