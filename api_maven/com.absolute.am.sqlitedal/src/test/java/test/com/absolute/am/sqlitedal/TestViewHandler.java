/**
 * 
 */
package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IViewHandler;
import com.absolute.am.dal.ResultSet;
import com.absolute.am.dal.Row;
import com.absolute.am.dal.ViewConstants;

/**
 * @author klavin
 *
 */
public class TestViewHandler {
	
	private static String COLMETA_COLUMN_DATA_TYPE = "ColumnDataType";
	private static String COLMETA_DESCRIPTION = "Description";
	private static String COLMETA_DISPLAY_NAME = "DisplayName";
	private static String TEST_ADMIN_UUID = "C383D5AC-6954-4F6E-9273-4D0D0DB580E6";
	
	// the below two are copied from Helpers.java to eliminate the circular dependency between the two projects
	public final String USER_DEFINED_VIEW_ALL_ANDROID_TABLETS = ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
			+ "<plist version=\"1.0\">"
			+ "<dict>"
			+ 		"<key>View Name Definition</key>"
			+ 		"<string>allandroidtablets</string>"
			+ 		"<key>View Definitions</key>"
			+		"<dict>"
			+ 			"<key>allandroidtablets</key>"
			+ 			"<dict>"
			+ 				"<key>Filter</key>"
			+ 				"<dict>"
			+ 					"<key>CompareValue</key>"
			+ 					"<array>"
            + 						"<dict>"
            + 							"<key>CachedInfoItemName</key>"
            + 							"<string>Mobile Device OS Platform</string>"
            + 							"<key>CompareValue</key>"
            + 							"<string>Android</string>"
            + 							"<key>CompareValue2</key>"
            + 							"<string></string>"
            + 							"<key>CompareValueUnits</key>"
            + 							"<string>Minutes</string>"
            + 							"<key>InfoItemID</key>"
            + 							"<string>8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5</string>"
            + 							"<key>IsCustomField</key>"
            + 							"<false/>"
            + 							"<key>Operator</key>"
            + 							"<string>==</string>"
            + 							"<key>UseNativeType</key>"
            +		 					"<false/>"
            + 						"</dict>"
            + 						"<dict>"
            + 							"<key>CachedInfoItemName</key>"
            + 							"<string>Mobile Device Is Tablet</string>"
            + 							"<key>CompareValue</key>"
            + 							"<true/>"
            + 							"<key>CompareValue2</key>"
            + 							"<string></string>"
            + 							"<key>CompareValueUnits</key>"
            + 							"<string>Minutes</string>"
            + 							"<key>InfoItemID</key>"
            + 							"<string>FA7F74E7-7E68-4C6A-ABFE-F8EFABD2F291</string>"
            + 							"<key>IsCustomField</key>"
            + 							"<false/>"
            + 							"<key>Operator</key>"
            + 							"<string>==</string>"
            + 							"<key>UseNativeType</key>"
            + 							"<true/>"
            + 						"</dict>"
            + 					"</array>"
            +					"<key>CriteriaFieldType</key>"
            + 					"<integer>0</integer>"
            + 					"<key>Operator</key>"
            + 					"<string>AND</string>"
            + 				"</dict>"
            + 				"<key>ColumnOrder</key>"
            + 				"<array>"
            + 					"<string>column_device_id</string>"
            + 					"<string>column_device_name</string>"
            + 					"<string>column_device_model</string>"
            + 					"<string>column_ios_version</string>"
            + 					"<string>column_device_serialnumber</string>"
            + 					"<string>column_device_last_contact</string>"
            + 					"<string>column_device_platform</string>"
            + 					"<string>column_device_platform_numeric</string>"
            + 					"<string>column_device_managed</string>"
            + 					"<string>column_device_absapps_version</string>"
            + 					"<string>column_device_cellular_technology</string>"
            + 				"</array>"
            + 				"<key>ColumnProperties</key>"
            + 				"<dict>"
            + 					"<key>column_device_id</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>39f3f074-b8a2-4df1-ac02-eb1f25f3f98e</string>"
            + 					"</dict>"
            + 					"<key>column_device_last_contact</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>4A8A81E0-0159-471D-B8D3-32E316CB81EF</string>"
            + 					"</dict>"
            + 					"<key>column_device_model</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>61479324-9E16-46FD-85E5-68F9865A7D6D</string>"
            + 					"</dict>"
            + 					"<key>column_device_name</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>FE5A9F56-228C-4BDA-99EC-8666292CB5C1</string>"
            + 					"</dict>"
            + 					"<key>column_device_serialnumber</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>B20868B8-CAEA-446B-BE8D-BEC97368E839</string>"
            + 					"</dict>"
            + 					"<key>column_ios_version</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0</string>"
            + 					"</dict>"
            + 					"<key>column_device_platform</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5</string>"
            + 					"</dict>"
            + 					"<key>column_device_platform_numeric</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>AE64A047-ACF2-40E2-B0A3-3F5565150FFA</string>"
            + 					"</dict>"
            + 					"<key>column_device_managed</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>26B03C68-0BF5-41ED-AD06-85903D5FBDFE</string>"
            + 					"</dict>"
            + 					"<key>column_device_absapps_version</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>00C2627C-E3D9-4C50-8948-0D96DDB36ACF</string>"
            + 					"</dict>"
            + 					"<key>column_device_cellular_technology</key>"
            + 					"<dict>"
            + 						"<key>InfoItemID</key>"
            + 						"<string>57856DAA-29CA-4721-B68B-101E321D30B6</string>"
            + 					"</dict>"
            +				"</dict>"
            +				"<key>RootTable</key>"
            + 				"<string>iphone_info</string>"
            +				"<key>SortOrder</key>"
            + 				"<array>"
            + 					"<dict>"
            + 						"<key>Ascending</key>"
            + 						"<true/>"
            + 						"<key>ColumnID</key>"
            + 						"<string>column_device_name</string>"
            + 						"</dict>"
            + 				"</array>"
            + 			"</dict>"
    		+ 		"</dict>"		
    		+ 	"</dict>"		
    		+ "</plist>"		
    			;
		
	private final String USER_DEFINED_VIEW_ALL_MOBILE_CONTENT = ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
			+ "<plist version=\"1.0\">"
			+ "<dict>"
			+ 		"<key>View Name Definition</key>"
			+ 		"<string>allmobilecontent</string>"
			+ 		"<key>View Definitions</key>"
			+		"<dict>"
			+ 			"<key>allmobilecontent</key>"
		    +			"<dict>"
		    + 				"<key>ColumnOrder</key>"
		    +				"<array>"
		    +					"<string>column_media_id</string>"
		    +					"<string>column_media_name</string>"
		    +					"<string>column_media_file_size</string>"
		    +					"<string>column_media_can_leave_app</string>"
		    +					"<string>column_can_be_emailed</string>"
		    +					"<string>column_can_be_printed</string>"
		    +					"<string>column_is_wifi_only</string>"
		    +					"<string>column_media_file_type</string>"
		    +					"<string>column_category</string>"
		    +					"<string>column_media_file_name</string>"
		    + 					"<string>column_last_modified</string>"
		    +					"<string>column_password</string>"
		    +				"</array>"
		    +				"<key>ColumnProperties</key>"
		    +				"<dict>"
		    +					"<key>column_password</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>68f6bbc2-3b7d-42da-a622-9428b409ac7e</string>"
		    +					"</dict>"
		    +					"<key>column_can_be_printed</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>0D15DD58-8895-48DD-9C8D-861D2824506B</string>"
		    +					"</dict>"
		    +					"<key>column_can_be_emailed</key>"
		    + 					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>8C8D7E49-DAEA-47A1-8B91-DB19F6E27578</string>"
		    +					"</dict>"
		    +					"<key>column_media_id</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>10413EEE-81C4-4AC7-9C7F-52581699FABB</string>"
		    +					"</dict>"
		    +					"<key>column_last_modified</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>410394C2-C903-4223-817C-8AF8125FC74F</string>"
		    +					"</dict>"
		    +					"<key>column_category</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>859F46B9-3710-45B9-B914-109F4F95CC68</string>"
		    +					"</dict>"
		    +					"<key>column_is_wifi_only</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>99D10C64-401E-4B93-8C11-E251ADFEC506</string>"
		    +					"</dict>"
		    +					"<key>column_media_can_leave_app</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>D17156FE-379D-45D3-8E41-A1D77EFEAFA0</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_name</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>F775E8A9-DBC6-4873-B9C2-1DCA3AF4369A</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_size</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>A332932E-DD61-4D8A-BCC6-53CAF45B513E</string>"
		    +					"</dict>"
		    +					"<key>column_media_file_type</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>729FD2C9-FDBC-4B6A-96E0-0465CFCC602A</string>"
		    +					"</dict>"
		    +					"<key>column_media_name</key>"
		    +					"<dict>"
		    +						"<key>InfoItemID</key>"
		    +						"<string>0AE86506-A5B1-43C7-9037-5BF40C15F18A</string>"
		    +					"</dict>"
		    +				"</dict>"
		    +				"<key>OriginalRootTable</key>"
		    +				"<string>mobile_media</string>"
		    +				"<key>RootTable</key>"
		    +				"<string>mobile_media</string>"
		    +				"<key>SortOrder</key>"
		    +				"<array>"
		    +					"<dict>"
		    +						"<key>Ascending</key>"
		    +						"<true/>"
		    +						"<key>ColumnID</key>"
		    +						"<string>column_media_name</string>"
		    +					"</dict>"
		    +				"</array>"
		    +			"</dict>"
    		+ 		"</dict>"		
    		+ 	"</dict>"		
    		+ "</plist>";

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_user_defined_view() throws Exception{
		System.out.println(" ***********   User Defined View - allandroidtablets  ************");
		
		runTest(USER_DEFINED_VIEW_ALL_ANDROID_TABLETS, null, null, null,  null, "300", "0", "allpages", 9,
				9, 7, "Row:15,tfield,GT-P6210,52461568,4641195c03a4911f,2012-10-28T13:29:00Z,Android,11,1,17924096,None", 
				11, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   User Defined View - allmobilecontent  ************");
		runTest(USER_DEFINED_VIEW_ALL_MOBILE_CONTENT, null, null, null, null, "300", "0", "none", 0,
				92, 35, "Row:92,MovieMOVsample_iTunes,3284257,1,0,0,0,Quicktime Movie,Multimedia,MovieMOVsample_iTunes.mov,2005-10-17T22:56:00Z,null", 
				12, 6, "{ShortDisplayName=Wi-Fi only, MaxWidth=1000, Description=Whether the mobile media file is downloaded only when the device is connected over WiFi., MinWidth=50, DisplayName=Media File Is WiFi-Only, Truncation=3, ColumnDataType=Number, InfoItemID=99D10C64-401E-4B93-8C11-E251ADFEC506, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);

	}
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_all_in_house_apps() throws Exception{
		System.out.println(" ***********   allinhouseapplications   ************");
		runTest("allinhouseapplications", null, null, null, null, "300", "0", "none", 0,
				2, 1, "Row:2,Marketing,iOS,10,1.0,107,16259,Marketing,com.absolute.Marketing,67141632,1,iPhone, iPod Touch, iPad,Helpdesk Sample App 2012,2013-08-12T11:42:30Z,2012-09-06T16:02:57Z,647728B3-9AE4-415A-9E07-461D49923037,1,BDB799AB-5CB6-453E-A6B0-6FA47EBA5397,16809984,Marketing_1.0.ipa,Marketing,23ad7d2b9fda38d877272ec048ce70f9,com.absolute.Marketing,blahhhhh,Hi dude!,null,null,1,0", 
				29, 3, "{MaxWidth=1000, ShortDisplayName=Platform, Description=The numeric value of the platform type., MinWidth=50, DisplayName=Platform Type, Truncation=3, ColumnDataType=Number, InfoItemID=3e2b3fba-3441-4641-8dca-7320e0e7568e, Alignment=4, Width=150}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_all_third_party_apps() throws Exception{
		System.out.println(" ***********   allthirdpartyapplications   ************");
		runTest("allthirdpartyapplications", null, null, null, null, "300", "0", "none", 0,
				6, 0, "Row:4,iAlarm,Utilities,83918848,1,iPhone, iPod Touch, iPad,Alarm,1,1,0,0,0,2012-09-07T16:39:57Z,E60AF4B4-F14C-4A7A-BD6E-278EAE94AA09,11,iPhone, iPod Touch, iPad,iOS,10,,,http://itunes.apple.com/ca/app/alarm-clock-free/id476555713?mt=8,Free Alarm app,476555713", 
				23, 3, "{ShortDisplayName=Min OS Version, MaxWidth=1000, Description=The minimum version of iOS required to use the app., MinWidth=50, DisplayName=App Min OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=605E53AD-01F3-41DA-8760-4E7C924C9C2E, Alignment=4, Width=150, DisplayType=FormatVersion}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Fix later
	public void can_get_all_computers() throws Exception{
		System.out.println(" ***********   allcomputers   ************");
		runTest("allcomputers", null, null, null, null, "300", "0", "none", 0,
				9, 3, "Row:machine_not_available,QA�s MacBook Pro Lion,MacBook Pro 13\" (Feb 2011, Oct 2011),Mac OS X,4213,2886735656,QA", 
				7, 3, "{MaxWidth=1000, Description=The general type and flavor of the operating system, e.g., Windows XP Professional or Mac OS X., MinWidth=50, Enumeration=enum_OSPlatform, DisplayName=OS Platform, Truncation=3, ColumnDataType=Number, InfoItemID=671D1208-CA16-11D9-839A-000D93B66ADA, Alignment=1, Width=150}",
				null);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Fix later
	public void can_get_all_macs() throws Exception{
		System.out.println(" ***********   macsonly   ************");
		runTest("maconly", null, null, null, null, "300", "0", "none", 0,
				1, 0, "Row:machine_not_available,QA�s MacBook Pro Lion,MacBook Pro 13\" (Feb 2011, Oct 2011),Mac OS X,4213,2886735656,QA", 
				7, 3, "{MaxWidth=1000, Description=The general type and flavor of the operating system, e.g., Windows XP Professional or Mac OS X., MinWidth=50, Enumeration=enum_OSPlatform, DisplayName=OS Platform, Truncation=3, ColumnDataType=Number, InfoItemID=671D1208-CA16-11D9-839A-000D93B66ADA, Alignment=1, Width=150}",
				null);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_all_pcs() throws Exception{
		System.out.println(" ***********   pcsonly   ************");
		runTest("pconly", null, null, null, null, "300", "0", "none", 0,
				8, 3, "Row:machine_not_available,TABLETWIN8,PC Compatible,75,1568,2886735698,", 
				7, 3, "{MaxWidth=1000, Description=The general type and flavor of the operating system, e.g., Windows XP Professional or Mac OS X., MinWidth=50, Enumeration=enum_OSPlatform, DisplayName=OS Platform, Truncation=3, ColumnDataType=Number, InfoItemID=671D1208-CA16-11D9-839A-000D93B66ADA, Alignment=1, Width=150}",
				null);
	}


	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_devices_for_smart_policy() throws Exception{
		System.out.println(" ***********   devices_for_smart_policies   ************");
		StringBuilder filterQuery = new StringBuilder();
		filterQuery.append("select __iphone_info_id as id from (");
		filterQuery.append("SELECT  iphone_info.id AS __iphone_info_id,iphone_info.UniqueID AS UniqueID,IFNULL((SELECT value_en FROM enum_MobileDeviceModel WHERE enum_MobileDeviceModel.key=+iphone_info.MachineModel), iphone_info.MachineModel) AS iphone_info_MachineModel,iphone_info.MDMManagedDevice AS iphone_info_MDMManagedDevice FROM iphone_info WHERE (((iphone_info_MachineModel COLLATE NOCASE = 'gt-p3113') AND iphone_info_MDMManagedDevice = 1))  ");
		filterQuery.append(")");
		runTest("devicesforsmartpolicy", null, null, null, filterQuery.toString(), "300", "0", "allpages", 1,
			1, 0, "Row:13,SamsungGalaxyTab2,GT-P3113,67403776,c08087d1f1ff77f,null,2012-10-29T17:37:05Z,null,null,Android,11", 
			11, 6, "{ShortDisplayName=Last Contact, MaxWidth=1000, Description=The time when the last communication from the mobile device was received by the MDM server., MinWidth=50, DisplayName=Mobile Device Last Contact, Truncation=3, ColumnDataType=DateTime, InfoItemID=4A8A81E0-0159-471D-B8D3-32E316CB81EF, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
			null);
		
		// The device in the smart policy is not assigned to this user
		System.out.println(" ***********   devices_for_smart_policies  with filterByAdmin ************");
		runTest("devicesforsmartpolicy", null, null, null, filterQuery.toString(), "300", "0", "allpages", 0,
				0, 0, "Row:13,SamsungGalaxyTab2,GT-P3113,67403776,c08087d1f1ff77f,null,2012-10-29T17:37:05Z,null,null,Android,11", 
				11, 6, "{ShortDisplayName=Last Contact, MaxWidth=1000, Description=The time when the last communication from the mobile device was received by the MDM server., MinWidth=50, DisplayName=Mobile Device Last Contact, Truncation=3, ColumnDataType=DateTime, InfoItemID=4A8A81E0-0159-471D-B8D3-32E316CB81EF, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
				TEST_ADMIN_UUID);		
	}
	
	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Ignoring because the database snapshot used by the test suite does not currently have any ipod touch devices 
	public void can_query_allipodtouchdevices() throws Exception{
		System.out.println(" ***********   allipodtouchdevices   ************");
		runTest("allipodtouchdevices", null, null, null, null, "300", "0", "allpages", 7,
				7, 3, "Row:158,Jason Fielding-Tweedie�s iPod,iPod,83984384,C3TGPR0LDNQY,2012-03-09T00:53:11Z,9A405", 
				12, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allipads() throws Exception{
		System.out.println(" ***********   allipads   ************");
		runTest("allipads", null, null, null, null, "300", "0", "allpages", 4,
				4, 1, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	@Ignore // TODO: Ignoring because the database snapshot used by the test suite does not currently have any iphone devices
	public void can_query_alliphones() throws Exception{
		System.out.println(" ***********   alliphones   ************");
		runTest("alliphones", null, null, null, null, "300", "0", "allpages", 26, 
				26, 8, "Row:174,Kimmy,iPhone 4 GSM (16 GB, black),83984384,7S0447A6A4S,+16049993754,2012-01-19T22:20:55Z,9A405,8930 2720 4010 0988 7475,01 254600 940046 4", 
				17, 6, "{ShortDisplayName=Last Contact, MaxWidth=1000, Description=The time when the last communication from the mobile device was received by the MDM server., MinWidth=50, DisplayName=Mobile Device Last Contact, Truncation=3, ColumnDataType=DateTime, InfoItemID=4A8A81E0-0159-471D-B8D3-32E316CB81EF, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
				null);
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_alliosdevices() throws Exception{
		System.out.println(" ***********   alliosdevices   ************");
		runTest("alliosdevices", null, null, null, null, "300", "0", "allpages", 4, 
				4, 1, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   alliosdevices with filterByAdmin  ************");
		runTest("alliosdevices", null, null, null, null, "300", "0", "allpages", 0, 
				0, 0, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);		
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allandroidphones() throws Exception{
		System.out.println(" ***********   allandroidphones   ************");
		runTest("allandroidphones", null, null, null, null, "300", "0", "none", 0,
				2, 1, "Row:12,Samsung,SAMSUNG-SGH-I896,35684352,00000000000,17786281167,2012-09-10T08:20:44Z,89302720400072136869,354182041117712,Android,11,1,17858560,GSM,1", 
				15, 6, "{ShortDisplayName=Last Contact, MaxWidth=1000, Description=The time when the last communication from the mobile device was received by the MDM server., MinWidth=50, DisplayName=Mobile Device Last Contact, Truncation=3, ColumnDataType=DateTime, InfoItemID=4A8A81E0-0159-471D-B8D3-32E316CB81EF, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
				null);
		
		System.out.println(" ***********   allandroidphones with filterByAdmin  ************");
		runTest("allandroidphones", null, null, null, null, "300", "0", "none", 0,
				1, 0, "Row:4,Michaelqiu,HTC EVO 3D X515m,36995072,HT211V201748,null,2012-07-04T22:08:51Z,null,356871048089613,Android,11,0,17858560,GSM,1", 
				15, 6, "{ShortDisplayName=Last Contact, MaxWidth=1000, Description=The time when the last communication from the mobile device was received by the MDM server., MinWidth=50, DisplayName=Mobile Device Last Contact, Truncation=3, ColumnDataType=DateTime, InfoItemID=4A8A81E0-0159-471D-B8D3-32E316CB81EF, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
				TEST_ADMIN_UUID);		
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allandroidtablets() throws Exception{
		System.out.println(" ***********   allandroidtablets   ************");
		runTest("allandroidtablets", null, null, null,  null, "300", "0", "allpages", 9,
				9, 7, "Row:15,tfield,GT-P6210,52461568,4641195c03a4911f,2012-10-28T13:29:00Z,Android,11,1,17924096,None,1", 
				12, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   allandroidtablets with filterByAdmin   ************");
		runTest("allandroidtablets", null, null, null,  null, "300", "0", "allpages", 3,
				3, 2, "Row:3,michaelqiu,A500,52527104,11624470615,2012-10-29T15:35:52Z,Android,11,1,17924096,None,0", 
				12, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);
		
	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobiledevices() throws Exception{
		System.out.println(" ***********   allmobiledevices_inlinecount_none   ************");
		runTest("allmobiledevices", null, null, null, null, "300", "0", "none", 0,
				15, 8, "Row:16,Motorola Xoom Tablet,Xoom,52854784,161c11024280e317,2012-10-29T17:49:31Z,Android,11,1,17924096,None,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   allmobiledevices_inlinecount_allpages   ************");
		runTest("allmobiledevices", null, null, null, null, "300", "0", "allpages", 15,
				15, 8, "Row:16,Motorola Xoom Tablet,Xoom,52854784,161c11024280e317,2012-10-29T17:49:31Z,Android,11,1,17924096,None,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   allmobiledevices_inlinecount_allpages_2_2   ************");
		runTest("allmobiledevices", null, null, null, null, "2", "2", "allpages", 15,
				2, 1, "Row:7,Lenovo Tablet,ThinkPad Tablet,67338240,MP063F1,2012-09-06T20:13:54Z,Android,11,0,17858560,None,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_inlinecount_allpages with filterByAdminUUID   ************");
		runTest("allmobiledevices", null, null, null, null, "300", "0", "allpages", 4,
				4, 3, "Row:4,Michaelqiu,HTC EVO 3D X515m,36995072,HT211V201748,2012-07-04T22:08:51Z,Android,11,0,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);

	}

	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobilecontent() throws Exception{
		System.out.println(" ***********   allmobilecontent   ************");
		runTest("allmobilecontent", null, null, null, null, "300", "0", "none", 0,
				92, 35, "Row:92,MovieMOVsample_iTunes,3284257,1,0,0,0,Quicktime Movie,Multimedia,MovieMOVsample_iTunes.mov,2005-10-17T22:56:00Z,null", 
				12, 6, "{ShortDisplayName=Wi-Fi only, MaxWidth=1000, Description=Whether the mobile media file is downloaded only when the device is connected over WiFi., MinWidth=50, DisplayName=Media File Is WiFi-Only, Truncation=3, ColumnDataType=Number, InfoItemID=99D10C64-401E-4B93-8C11-E251ADFEC506, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);
	}
	
	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobilepolicies() throws Exception{
		System.out.println(" ***********   allmobilepolicies   ************");
		runTest("allmobilepolicies", null, null, null, null, "300", "0", "none", 0,
				19, 8, "Row:22,Mqiu's Samsung Galaxy Tab 2,5DD5866F-1B07-40C2-836A-98C0BD29762D,1", 
				4, 2, "{MaxWidth=100, ShortDisplayName=UniqueID, Description=The universally unique identifier (UUID) of the policy., MinWidth=30, DisplayName=Id, Truncation=3, ColumnDataType=String, InfoItemID=E652CD7A-909C-465D-AE76-B97428711B6B, Alignment=1, Width=50}",
				null);
	}
	
	/**
	 * Test method for {@link com.absolute.am.sqlitedal.ViewHandler#QueryView(java.lang.String)}.
	 */
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allandroiddevices() throws Exception{
		System.out.println(" ***********   allandroiddevices   ************");
		runTest("allandroiddevices", null, null, null, null, "300", "0", "none", 0,
				11, 10, "Row:9,Toshiba Tablet,AT100,51412992,033c11c04300c097,2012-08-23T17:40:54Z,Android,11,1,17858560,None,1", 
				12, 2, "{ShortDisplayName=Model, MaxWidth=1000, Description=The type of the connected mobile device., MinWidth=50, Enumeration=enum_MobileDeviceModel, DisplayName=Mobile Device Model, EnumerationKeyIsFallback=true, Truncation=3, ColumnDataType=String, InfoItemID=61479324-9E16-46FD-85E5-68F9865A7D6D, Alignment=1, Width=150}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_onemobilecontent() throws Exception{
		System.out.println(" ***********   onemobilecontent   ************");
		runTest("onemobilecontent", null, null, null, "146", "300", "0", "none", 0,
				1, 0, "Row:146,event.logtags,5624,1,0,0,1,LOGTAGS,null,event.logtags,2011-10-03T23:44:03Z,null,23052764-1F1C-44C7-AF34-5D1D120209CA,Bla bla bla,1",
				15, 6, "{ShortDisplayName=Wi-Fi only, MaxWidth=1000, Description=Whether the mobile media file is downloaded only when the device is connected over WiFi., MinWidth=50, DisplayName=Media File Is WiFi-Only, Truncation=3, ColumnDataType=Number, InfoItemID=99D10C64-401E-4B93-8C11-E251ADFEC506, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_onemobilepolicy() throws Exception{
		System.out.println(" ***********   onemobilepolicy   ************");
		runTest("onemobilepolicy", null, null, null, "25", "300", "0", "none", 0,
				1, 0, "Row:25,webAPIUnitTest1,0", 
				3, 2, "{ShortDisplayName=Smart policy, MaxWidth=1000, Description=Is the mobile device policy a smart policy?, MinWidth=50, DisplayName=Is Smart Policy, Truncation=3, ColumnDataType=Blob, InfoItemID=F6917EE8-9F39-43F3-B8C9-D3C461170FE5, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_onemobiledevice() throws Exception{
		System.out.println(" ***********   onemobiledevice   ************");
		runTest("onemobiledevice", null, null, null, "5", "300", "0", "none", 0,
				1, 0, "Row:5,TerryiPad,iPad Wifi+3G (64 GB),MC497C,85032960,9B206,GB0450SFETV,null,Company,2012-10-02T17:46:16Z,01 233000 979124 2,074d4eed4979da092706119d7a466bc9e5c8dd99,62405189632,60679454720,b8:ff:61:53:93:f0,b8:ff:61:53:93:ef,2886869835,null,null,null,1,100,0,2012-07-10T22:56:36Z,null,2010-11-08T08:00:00Z,54432000,Out of Warranty,null,Online,1,null,19955712,493,07.11.01,null,Apple,Apple A4,1000000000,768 x 1024,1,Block-level and File-level,1,1,1,1,0,null,GSM,null,null,000,00,null,00,null,null,null,null,null,null,Apple,null,null,null,null,null,null,null,null,null,null,null,null,null,2012-08-17T20:45:36Z,2012-07-25T18:55:32Z,2012-07-25T18:33:16Z,2012-07-25T18:33:17Z,2012-07-15T00:24:29Z,iOS,10,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null", 
				97, 0, "",
				null);
		
		System.out.println(" ***********   onemobiledevice with filterByAdmin  ************");
		runTest("onemobiledevice", null, null, null, "5", "300", "0", "none", 0,
				0, 0, "", 
				97, 0, "",
				TEST_ADMIN_UUID);		
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allandroiddevices_sort_device_name() throws Exception{
		System.out.println(" ***********   allandroiddevices_sort_device_name   ************");
		runTest("allandroiddevices", null, null, "FE5A9F56-228C-4BDA-99EC-8666292CB5C1 asc", null, "300", "0", "none", 0,
				11, 10, "Row:9,Toshiba Tablet,AT100,51412992,033c11c04300c097,2012-08-23T17:40:54Z,Android,11,1,17858560,None,1",
				12, 2, "{ShortDisplayName=Model, MaxWidth=1000, Description=The type of the connected mobile device., MinWidth=50, Enumeration=enum_MobileDeviceModel, DisplayName=Mobile Device Model, EnumerationKeyIsFallback=true, Truncation=3, ColumnDataType=String, InfoItemID=61479324-9E16-46FD-85E5-68F9865A7D6D, Alignment=1, Width=150}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allandroiddevices_sort_device_model() throws Exception{
		System.out.println(" ***********   allandroiddevices_sort_device_model   ************");
		runTest("allandroiddevices", null, null, "61479324-9E16-46FD-85E5-68F9865A7D6D asc", null, "300", "0", "none", 0,
				11, 8, "Row:12,Samsung,SAMSUNG-SGH-I896,35684352,00000000000,2012-09-10T08:20:44Z,Android,11,1,17858560,GSM,1", 
				12, 2, "{ShortDisplayName=Model, MaxWidth=1000, Description=The type of the connected mobile device., MinWidth=50, Enumeration=enum_MobileDeviceModel, DisplayName=Mobile Device Model, EnumerationKeyIsFallback=true, Truncation=3, ColumnDataType=String, InfoItemID=61479324-9E16-46FD-85E5-68F9865A7D6D, Alignment=1, Width=150}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobiledevices_sort_device_model() throws Exception{
		System.out.println(" ***********   allmobiledevices_sort_device_model   ************");
		runTest("allmobiledevices", null, null, "61479324-9E16-46FD-85E5-68F9865A7D6D desc", null, "300", "0", "none", 0,
				15, 4, "Row:11,LG 3G DATA,LG-V905R,50429952,012641000005741,2012-10-29T18:01:03Z,Android,11,1,17924096,None,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_policiesforcontent() throws Exception{
		System.out.println(" ***********   policiesforcontent   ************");
		runTest("policiesforcontent", null, null, null, "2FF4ADC2-E686-40A5-994E-91F74189AD8C", "300", "0", "none", 0,
				2, 1, "Row:15,Smart Policy 3 LG Dell,43F3D4E7-A46D-4DC1-B0E4-41DE95D80C3C,1,1,0,null,null",
				8, 2, "{MaxWidth=100, ShortDisplayName=UniqueID, Description=The universally unique identifier (UUID) of the policy., MinWidth=30, DisplayName=Id, Truncation=3, ColumnDataType=String, InfoItemID=E652CD7A-909C-465D-AE76-B97428711B6B, Alignment=1, Width=50}",
				null);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobiledevices_top5_skip3() throws Exception {
		System.out.println(" ***********   allmobiledevices_top5_skip3   ************");
		runTest("allmobiledevices", null, null, "61479324-9E16-46FD-85E5-68F9865A7D6D desc", null, "5", "3", "none", 0,
				5, 3, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
	}

	@Test 
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobiledevices_search_by_col() throws Exception {
		System.out.println(" ***********   allmobiledevices_search_devicename   ************");
		runTest("allmobiledevices", "FE5A9F56-228C-4BDA-99EC-8666292CB5C1", "moopad", null, null, "50", "0", "none", 0,
				1, 0, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_search_machine_model   ************");
		runTest("allmobiledevices", "61479324-9E16-46FD-85E5-68F9865A7D6D", "ipad", null, null, "50", "0", "none", 0,
				4, 0, "Row:1,CI11339DDESOUS,iPad 2 Wifi (16 GB, black),84967424,DR5HH64YDFHW,2012-07-10T22:06:40Z,iOS,10,1,19955712,None,null,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_search_platform   ************");
		runTest("allmobiledevices", "8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5", "android", null, null, "50", "0", "none", 0,
				11, 0, "Row:2,Dell,Dell Streak,35749888,,2012-09-06T20:35:50Z,Android,11,1,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_search_platform  with filterByAdmin  ************");
		runTest("allmobiledevices", "8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5", "android", null, null, "50", "0", "none", 0,
				4, 0, "Row:2,Dell,Dell Streak,35749888,,2012-09-06T20:35:50Z,Android,11,1,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);

		System.out.println(" ***********   allmobiledevices_search_machine_model with filterByAdmin  ************");
		runTest("allmobiledevices", "61479324-9E16-46FD-85E5-68F9865A7D6D", "streak", null, null, "50", "0", "none", 0,
				1, 0, "Row:2,Dell,Dell Streak,35749888,,2012-09-06T20:35:50Z,Android,11,1,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);

	}
	
	@Test 
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_allmobiledevices_search() throws Exception {
		
		System.out.println(" ***********   allmobiledevices_search_not_there   ************");
		runTest("allmobiledevices", null, "not_there", null, null, "50", "0", "none", 0,
				0, 0, "", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   allmobiledevices_search_dell   ************");
		runTest("allmobiledevices", null, "dell", null, null, "50", "0", "none", 0,
				1, 0, "Row:2,Dell,Dell Streak,35749888,,2012-09-06T20:35:50Z,Android,11,1,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_search_Dell   ************");
		runTest("allmobiledevices", null, "Dell", null, null, "50", "0", "none", 0,
				1, 0, "Row:2,Dell,Dell Streak,35749888,,2012-09-06T20:35:50Z,Android,11,1,17858560,GSM,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allmobiledevices_search_ipad   ************");
		runTest("allmobiledevices", null, "ipad", null, null, "50", "0", "none", 0,
				4, 1, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		System.out.println(" ***********   allandroidtablets_search_tab   ************");
		runTest("allandroidtablets", null, "tab", null,  null, "300", "0", "none", 0,
				4, 2, "Row:13,SamsungGalaxyTab2,GT-P3113,67403776,c08087d1f1ff77f,2012-10-29T17:37:05Z,Android,11,1,17924096,None,0", 
				12, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);

		
		System.out.println(" ***********   allmobilepolicies_search_mqiu   ************");
		runTest("allmobilepolicies", null, "mqiu", null,  null, "300", "0", "none", 0,
				5, 2, "Row:19,Mqiu's iPad Smart Policy,F18E908C-E6D5-45B8-BEBC-37FE818028D2,1", 
				4, 2, "{MaxWidth=100, ShortDisplayName=UniqueID, Description=The universally unique identifier (UUID) of the policy., MinWidth=30, DisplayName=Id, Truncation=3, ColumnDataType=String, InfoItemID=E652CD7A-909C-465D-AE76-B97428711B6B, Alignment=1, Width=50}",
				null);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_devicesfornonsmartpolicy() throws Exception {
		System.out.println(" ***********   devices_for_non_smart_policies   ************");
		runTest("devicesfornonsmartpolicy", null, null, null,  "6", "300", "0", "none", 0,
				2, 1, "Row:9,Toshiba Tablet,AT100,51412992,033c11c04300c097,null,2012-08-23T17:40:54Z,null,null,Android,11,3,6", 
				13, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		System.out.println(" ***********   devices_for_non_smart_policies   ************");
		runTest("devicesfornonsmartpolicy", null, null, null,  "6", "300", "0", "none", 0,
				1, 0, "Row:2,Dell,Dell Streak,35749888,,17786281167,2012-09-06T20:35:50Z,89302720400072136869,011954000873021,Android,11,4,6", 
				13, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_query_iospolicymembers_smart_policy() throws Exception {
		System.out.println(" ***********   iospolicymemberssmartpolicy   ************");
		runTest("devicesforsmartpolicy", null, null, null,  "16", "300", "0", "none", 0,
				1, 0, "Row:16,Motorola Xoom Tablet,Xoom,52854784,161c11024280e317,null,2012-10-29T17:49:31Z,null,99000052039154,Android,11", 
				11, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		// The device that is a member of the smart policy is not assigned to this admin, so it should not be returned.
		System.out.println(" ***********   iospolicymemberssmartpolicy with filterByAdmin  ************");		
		runTest("devicesforsmartpolicy", null, null, null,  "16", "300", "0", "none", 0,
				0, 0, "", 
				11, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				TEST_ADMIN_UUID);		
	}


	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_contentforpolicy() throws Exception{
		System.out.println(" ***********   contentforpolicy   ************");
		runTest("contentforpolicy", null, null, null, "20", "300", "0", "none", 0,
				38, 1, "Row:0XML1 Wifi,2,193,1,XML Document,Documents,2012-10-28T04:53:10Z,0,null,null,95,20,183",
				13, 6, "{ShortDisplayName=Modified, MaxWidth=1000, Description=The date when the mobile media file object (as opposed to the file that it specifies) was last edited., MinWidth=50, DisplayName=Media File Last Modified, Truncation=3, ColumnDataType=DateTime, InfoItemID=410394C2-C903-4223-817C-8AF8125FC74F, Alignment=1, Width=150, DisplayType=FormatRelativDateTime}",
				null);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_allwindowsphonedevices() throws Exception{
		System.out.println(" ***********   allwindowsphonedevices   ************");
		runTest("allwindowsphonedevices", null, null, null, "20", "300", "0", "none", 0,
				0, 0, "",
				8, 6, "{MaxWidth=1000, Description=The name of the EAS policy active on this device., MinWidth=50, DisplayName=EAS Policy Name, Truncation=3, ColumnDataType=String, InfoItemID=4BE7DE57-7E92-47AE-BDF2-A3F0E84CFE62, Alignment=1, Width=150}",
				null);
	}


	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_search_special_columns() throws Exception{
		// we have several special columns for types 'FormatBoolean', 'FormatVersion', 'FormatSmartBytes'
		System.out.println(" ***********   FormatBoolean, FormatVersion, FormatSmartBytes   ************");
		// FormatBoolean test
		runTest("allmobiledevices", "26B03C68-0BF5-41ED-AD06-85903D5FBDFE", "no", null, null, "50", "0", "none", 0,
				3, 0, "Row:7,Lenovo Tablet,ThinkPad Tablet,67338240,MP063F1,2012-09-06T20:13:54Z,Android,11,0,17858560,None,0,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);	
		
		// FormatBoolean test
		runTest("allmobiledevices", "F5384281-6943-487F-A0F8-FC4EA254C489", "yes", null, null, "50", "0", "none", 0,
				10, 0, "Row:1,CI11339DDESOUS,iPad 2 Wifi (16 GB, black),84967424,DR5HH64YDFHW,2012-07-10T22:06:40Z,iOS,10,1,19955712,None,null,null,1", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		// FormatVersion test
		runTest("allmobiledevices", "1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0", "4.0.4", null, null, "50", "0", "none", 0,
				1, 0, "Row:13,SamsungGalaxyTab2,GT-P3113,67403776,c08087d1f1ff77f,2012-10-29T17:37:05Z,Android,11,1,17924096,None,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		// FormatVersion test
		runTest("allmobiledevices", "1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0", "5.1.1", null, null, "50", "0", "none", 0,
				2, 0, "Row:6,MooPad,iPad 3. Gen, Wifi+3G GSM (16 GB, black),85032960,DMPHVQ8RDVGG,2012-07-23T23:53:39Z,iOS,10,0,null,GSM,0,null,0", 
				14, 3, "{ShortDisplayName=OS Version, MaxWidth=1000, Description=The version number of the operating system installed on the mobile device., MinWidth=50, DisplayName=Mobile Device OS Version, Truncation=3, ColumnDataType=Number, InfoItemID=1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0, Alignment=1, Width=150, DisplayType=FormatVersion}",
				null);
		
		// FormatSmartBytes
		runTest("allmobilecontent", "A332932E-DD61-4D8A-BCC6-53CAF45B513E", "187.10 kb", null, null, "300", "0", "none", 0,
				1, 0, "Row:2,Objective-C Coding Standard,191592,0,0,0,0,Microsoft Word Document,Documents,Objective-C Coding Standard.docx,2012-06-25T17:33:15Z,null", 
				12, 6, "{ShortDisplayName=Wi-Fi only, MaxWidth=1000, Description=Whether the mobile media file is downloaded only when the device is connected over WiFi., MinWidth=50, DisplayName=Media File Is WiFi-Only, Truncation=3, ColumnDataType=Number, InfoItemID=99D10C64-401E-4B93-8C11-E251ADFEC506, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);
		
		// FormatSmartBytes
		runTest("allmobilecontent", "A332932E-DD61-4D8A-BCC6-53CAF45B513E", "4.93 mb", null, null, "300", "0", "none", 0,
				1, 0, "Row:6,MP3 Amy Winehouse,5164442,1,1,1,0,MP3 audio file,Multimedia,Back_To_Black_-_01_-_Rehab.mp3,2008-05-28T15:50:17Z,null", 
				12, 6, "{ShortDisplayName=Wi-Fi only, MaxWidth=1000, Description=Whether the mobile media file is downloaded only when the device is connected over WiFi., MinWidth=50, DisplayName=Media File Is WiFi-Only, Truncation=3, ColumnDataType=Number, InfoItemID=99D10C64-401E-4B93-8C11-E251ADFEC506, Alignment=3, Width=150, DisplayType=FormatBoolean}",
				null);
	}
	
	private void runTest(String viewname, String searchCol, String searchText, String sortParams, 
			String selectParams, String top, String skip, String inlineCount, int totalRowCount,
			int rowCount, int rowIndex, String rowText, 
			int columnCount, int colIndex, String colText,
			String filterByAdmin) throws Exception {

		IDal dal = Util.getDal();
		
		IViewHandler queryHandler = dal.getViewHandler();
		HashMap<String, String> uiParams = new HashMap<String, String>();
		if (searchCol != null) {
			String searchKey = ViewConstants.PARAM_SEARCH + ":" + searchCol;
			uiParams.put(searchKey, searchText);
		} else {
			uiParams.put(ViewConstants.PARAM_SEARCH, searchText);
		}
		uiParams.put(ViewConstants.PARAM_ORDERBY, sortParams);
		uiParams.put(ViewConstants.PARAM_TOP, top);
		uiParams.put(ViewConstants.PARAM_SKIP, skip);
		uiParams.put(ViewConstants.PARAM_INLINE_COUNT, inlineCount);
		if (filterByAdmin != null && filterByAdmin.length()>0) {
			uiParams.put(ViewConstants.PARAM_FILTER_BY_ADMIN, filterByAdmin);
		}
		
		ArrayList<String> userParams = null;
		if (selectParams != null) {
			userParams = new ArrayList<String>();
			userParams.add(selectParams);
		}

		ResultSet result = queryHandler.queryView(viewname, uiParams, userParams, "_en");
		

		// Validate the column meta data.
		ArrayList<Map<String, Object>> cm = result.getColumnMetaData();
		assertNotNull("Check meta data was returned.", cm);

		for (int i=0; i<cm.size(); i++) {
			System.out.println("ColumnMetaData[" + i + "]=" + cm.get(i));
			Map<String, Object> thisColumn = cm.get(i);
			//String columnDataType = (String)thisColumn.get("ColumnDataType");
			assertMapHasStringAndGetValue(thisColumn, COLMETA_COLUMN_DATA_TYPE, "Check column[" + i + "] meta data");
			assertMapHasStringAndGetValue(thisColumn, COLMETA_DESCRIPTION, "Check column[" + i + "] meta data");
			assertMapHasStringAndGetValue(thisColumn, COLMETA_DISPLAY_NAME, "Check column[" + i + "] meta data");
		}
		assertTrue("Check there is meta data for the expected (" + columnCount + ") number of columns, actual=" + cm.size() + ".", 
				cm.size() == columnCount);		

		if (colIndex > 0) {
			String colAsString = cm.get(colIndex).toString();
			System.out .println("colAsString = " + colAsString);
			assertTrue("Check that column " + colIndex + " has the expected metadata", colAsString.compareTo(colText) == 0);
		}
						
		Row[] resultRows = result.getRows();
		for (int i=0; i<resultRows.length; i++) {
			System.out.println("row[" + i + "]=" + resultRows[i]);
			assertTrue("Check that each row has the right number of columns.", 
					resultRows[i].getValues().length == cm.size() );
		}
		assertTrue("Check row count, expected=" + rowCount + " actual=" + resultRows.length + ".", resultRows.length == rowCount);

		if (inlineCount.equalsIgnoreCase("allpages")) {
			System.out.println("getTotalRowsAvailable = " + result.getTotalRowsAvailable());
			assertTrue("Check that getTotalRowsAvailable is correct:", result.getTotalRowsAvailable() == totalRowCount);
		} else {
			assertTrue("Check that getTotalRowsAvailable() is not set.", result.getTotalRowsAvailable() == 0);
		}
		
		if (rowCount > 0) {
			String rowAsString = resultRows[rowIndex].toString();
			System.out .println("rowAsString = " + rowAsString);
			assertTrue("Check row data, actual=[" + rowAsString + "] expected=[" + rowText + "].",
					rowAsString.compareTo(rowText) == 0);
		}

	}
	
	private static String assertMapHasStringAndGetValue(Map<String, Object> theMap, String key, String message) {
		assertTrue(message + " contains " + key, theMap.containsKey(key));
		String value = (String)theMap.get(key);
		assertTrue(message + " not empty " + key, value != null && value.length()>0);
		return value;
	}
}
