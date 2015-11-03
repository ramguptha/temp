package test.com.absolute.testdata;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.*;
import test.com.absolute.testutil.Helpers;

public class ValidationTest extends ValidationBase {
	private static StringBuilder errorStringBuilder;
	
	@BeforeClass
	public static void setup() throws Exception
	{
		errorStringBuilder = new StringBuilder();
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void verify_test_data() throws Exception
	{
		// check if the computer exists
		boolean computersExist = checkComputersExist();
		boolean mobileDevicesExist = checkMobileDevicesExist();
		boolean standardPoliciesExist = checkStandardPoliciesExist();
		boolean smartPoliciesExist = checkSmartPoliciesExist();
		boolean contentFilesExist = checkContentFilesExist();
		boolean iosConfigurationProilesExist = checkConfigurationProfilesExist(ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_IOS);
		boolean androidConfigurationProilesExist = checkConfigurationProfilesExist(ConfigurationProfiles.CONFIGURATION_PROFILE_NAMES_ANDROID);
		boolean provisioningProfilesExist = checkProvisioningProfilesExist();
		boolean inHouseApplicationsExist = checkInHouseApplicationsExist();
		boolean thirdPartyApplicationsExist= checkThirdPartyApplicationsExist();
		boolean actionsExist = checkActionsExist();
		boolean booksExist = checkBooksExist();
		boolean administratorsExist = checkAdministratorsExist();
		
		// check if all the data are available; if not, show the message.
		Assert.assertTrue(errorStringBuilder.toString(), 
						  computersExist && 
						  mobileDevicesExist &&
						  standardPoliciesExist &&
						  smartPoliciesExist &&
						  contentFilesExist &&
						  iosConfigurationProilesExist && androidConfigurationProilesExist &&
						  provisioningProfilesExist &&
						  inHouseApplicationsExist &&
						  thirdPartyApplicationsExist &&
						  actionsExist &&
						  booksExist &&
						  administratorsExist);
	}
	
	
	private boolean checkComputersExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : Computers.COMPUTER_NAMES) {
			try {
				String[] computers= Helpers.getComputerIdsForComputerName(logonCookie, name);
				if (computers.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}

		if (!exists) {
			errorStringBuilder.append("\r\n===== Computers Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n-----------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkMobileDevicesExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : MobileDevices.MOBILE_DEVICE_NAMES) {
			try {
				String[] mobileDevices= Helpers.getDeviceIdsForDeviceNames(logonCookie, name);
				if (mobileDevices.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Mobile Devices Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n---------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkStandardPoliciesExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : Policies.STANDARD_POLICY_NAMES) {
			try {
				String[] policies= Helpers.getPolicyIdsForPolicyNames(logonCookie, name);
				if (policies.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Standard Policies Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n*  Both 'WebAPIUnitTest1' & 'WebAPIUnitTest2' are standard policies.");
			errorStringBuilder.append("\r\n-------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkSmartPoliciesExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : Policies.SMART_POLICY_NAMES) {
			try {
				String[] policies= Helpers.getPolicyIdsForPolicyNames(logonCookie, name);
				if (policies.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Smart Policies Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n* Smart policy 'WebAPI Smart 1' should be created with with criteria \"include all, Mobile Device Name contains 'WebAPI'\".");
			errorStringBuilder.append("\r\n----------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkContentFilesExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : ContentFiles.CONTENT_FILE_NAMES) {
			try {
				String[] contentFiles= Helpers.getContentIdsForContentNames(logonCookie, name);
				if (contentFiles.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Content Files Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n---------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkConfigurationProfilesExist(String[] configurationProfileNames) {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : configurationProfileNames) {
			try {
				String[] configurationProfiles= Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, name);
				if (configurationProfiles.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Configuration Profiles Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n---------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkProvisioningProfilesExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : ProvisioningProfiles.PROVISIONING_PROFILE_NAMES) {
			try {
				String[] provisioningProfiles= Helpers.getProvisioningProfileIdsForProvisioningProfileNames(logonCookie, name);
				if (provisioningProfiles.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Provisioning Profiles Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n---------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkInHouseApplicationsExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : InhouseApplications.IN_HOUSE_APPLICATION_NAMES) {
			try {
				String[] inhouseApplications= Helpers.getInHouseAppIdsForInHouseAppNames(logonCookie, name);
				if (inhouseApplications.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== In-house Applications Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n-----------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkThirdPartyApplicationsExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : ThirdPartyApplications.THIRD_PARTY_APPLICATION_NAMES) {
			try {
				String[] thirdPartyApplications= Helpers.getThirdPartyAppIdsForThirdPartyAppNames(logonCookie, name);
				if (thirdPartyApplications.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Third Party Applications Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n--------------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkActionsExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : Actions.ACTION_NAMES) {
			try {
				String[] actions= Helpers.getActionIdsForActionNames(logonCookie, name);
				if (actions.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + name;
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Actions Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n--------------------------------------------\r\n");
		}
			
		return exists;
	}
	
	private boolean checkBooksExist() {
		boolean exists = true;
		String errorMessage = "";
		
		for (String name : Books.BOOK_NAMES) {
			try {
				String[] books= Helpers.getBookIdsForBookNames(logonCookie, name);
				if (books.length <= 0) {
					exists = false;
				}
			} catch (Exception e) {
				exists = false;
				errorMessage += "\r\n - " + (name.length() == 0? "Empty name book (the title of the boos is empty)" : name);
			}
		}
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Books Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n* Use the Url 'https://itunes.apple.com/ca/app/kindle/id302584613?mt=8' to register one book with empty name.");
			errorStringBuilder.append("\r\n-------------------------\r\n");
		}
			
		return exists;
	}
	
	// TODO: To check if the administrators listing in the 'configuration.Administrators.java' exist
	private boolean checkAdministratorsExist() {
		boolean exists = true;
		String errorMessage = "";
		
		if (!exists) {
			errorStringBuilder.append("\r\n===== Administrators Missing =====" + errorMessage);
			errorStringBuilder.append("\r\n----------------------------------\r\n");
		}
		
		return exists;
	}
}
