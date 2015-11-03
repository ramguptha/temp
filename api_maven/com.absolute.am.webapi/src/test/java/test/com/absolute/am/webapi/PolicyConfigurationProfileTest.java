package test.com.absolute.am.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.absolute.am.command.iOSDevicesDefines;

import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

public class PolicyConfigurationProfileTest extends LoggedInTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {

		//test order is of the essence
		test_can_add_one_configuration_profile_to_one_policy();
		test_can_delete_one_configuration_profile_from_one_policy();
		test_can_add_multiple_configuration_profile_to_multiple_policies();
		test_can_delete_multiple_configuration_profile_from_multiple_policies();
	}

	
	public void test_can_add_one_configuration_profile_to_one_policy() throws Exception {
		
		String startTime = "22:50";
		String endTime = "22:51";
		String assignmentType = String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfile_PolicyLocked);
		String availabilitySelector = String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_DailyInterval);
		String configurationProfileId = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, "WebAPI Config Profile iOS 1")[0];
		String policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[3])[0];
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"configurationProfileIds\":");
		sb.append("["+configurationProfileId+"]");
		sb.append(",\"policyAssignments\":");
		sb.append("[{");
		sb.append("\"policyId\":");
		sb.append(policyId);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType);
		sb.append(",\"availabilitySelector\":");
		sb.append(availabilitySelector);
		sb.append(",\"startTime\":\"");
		sb.append(startTime);
		sb.append("\",\"endTime\":\"");
		sb.append(endTime);
		sb.append("\"}]}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONFIGURATION_PROFILE_API, sb.toString(), 
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		// check that the configuration profile was added successfully
		assertEquals("Configuration profile was not added successfully", configurationProfileId,
				Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyId, "WebAPI Config Profile iOS 1")[0]);
	}
	
	
	public void test_can_delete_one_configuration_profile_from_one_policy() throws Exception {
		
		String configurationProfileId = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, "WebAPI Config Profile iOS 1")[0];
		String policyId = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[3])[0];
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"associations\":");
		sb.append("[{");
		sb.append("\"configurationProfileId\":");
		sb.append(configurationProfileId);
		sb.append(",\"policyId\":");
		sb.append(policyId);
		sb.append("}]}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONFIGURATION_PROFILE_DELETE_API, sb.toString(), 
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		
		// check that the configuration profile was removed successfully
		List<String> installedConfigProfiles = new ArrayList<String>();
		try {
			installedConfigProfiles.add(Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyId, "WebAPI Config Profile iOS 1")[0]);
		} catch(RuntimeException e){
			assertTrue(e.getMessage().startsWith("ID not found for configuration profile"));
		}
		
		assertTrue(installedConfigProfiles.size()==0);
	}
	
	
	public void test_can_add_multiple_configuration_profile_to_multiple_policies() throws Exception {
		
		String startTime = "22:50";
		String endTime = "22:51";
		String assignmentType = String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfile_PolicyLocked);
		String availabilitySelector = String.valueOf(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_DailyInterval);
		String[] configurationProfileIds = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, 
				"WebAPI Config Profile iOS 1", "WebAPI Config Profile iOS 2");
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[3], Policies.STANDARD_POLICY_NAMES[4]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"configurationProfileIds\":");
		sb.append("["+configurationProfileIds[0]);
		sb.append("," +configurationProfileIds[1]+"]");
		sb.append(",\"policyAssignments\":");
		sb.append("[{");
		sb.append("\"policyId\":");
		sb.append(policyIds[0]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType);
		sb.append(",\"availabilitySelector\":");
		sb.append(availabilitySelector);
		sb.append(",\"startTime\":\"");
		sb.append(startTime);
		sb.append("\",\"endTime\":\"");
		sb.append(endTime);
		sb.append("\"},");
		sb.append("{\"policyId\":");
		sb.append(policyIds[1]);
		sb.append(",\"assignmentType\":");
		sb.append(assignmentType);
		sb.append(",\"availabilitySelector\":");
		sb.append(availabilitySelector);
		sb.append(",\"startTime\":\"");
		sb.append(startTime);
		sb.append("\",\"endTime\":\"");
		sb.append(endTime);
		sb.append("\"}]}");
		System.out.println("Request body=" + sb.toString());

		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONFIGURATION_PROFILE_API, sb.toString(), 
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		// check that the configuration profiles were added successfully
		assertEquals("Configuration profile was not added successfully", configurationProfileIds[0],
				Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[0], "WebAPI Config Profile iOS 1")[0]);
		assertEquals("Configuration profile was not added successfully", configurationProfileIds[1],
				Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[0], "WebAPI Config Profile iOS 2")[0]);
		assertEquals("Configuration profile was not added successfully", configurationProfileIds[0],
				Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[1], "WebAPI Config Profile iOS 1")[0]);
		assertEquals("Configuration profile was not added successfully", configurationProfileIds[1],
				Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[1], "WebAPI Config Profile iOS 2")[0]);
	}
	
	
	public void test_can_delete_multiple_configuration_profile_from_multiple_policies() throws Exception {
		
		String[] configurationProfileIds = Helpers.getConfigurationProfileIdsForConfigurationProfileNames(logonCookie, 
				"WebAPI Config Profile iOS 1", "WebAPI Config Profile iOS 2");
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[3], Policies.STANDARD_POLICY_NAMES[4]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"associations\":");
		sb.append("[{");
		sb.append("\"configurationProfileId\":");
		sb.append(configurationProfileIds[0]);
		sb.append(",\"policyId\":");
		sb.append(policyIds[0]);
		sb.append("},");
		sb.append("{\"configurationProfileId\":");
		sb.append(configurationProfileIds[0]);
		sb.append(",\"policyId\":");
		sb.append(policyIds[1]);
		sb.append("},");
		sb.append("{\"configurationProfileId\":");
		sb.append(configurationProfileIds[1]);
		sb.append(",\"policyId\":");
		sb.append(policyIds[0]);
		sb.append("},");
		sb.append("{\"configurationProfileId\":");
		sb.append(configurationProfileIds[1]);
		sb.append(",\"policyId\":");
		sb.append(policyIds[1]);
		sb.append("}");
		sb.append("]}");
		
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_CONFIGURATION_PROFILE_DELETE_API, sb.toString(), 
				HttpStatus.SC_NO_CONTENT, HttpStatus.SC_NO_CONTENT);
		
		List<String> installedConfigProfiles = new ArrayList<String>();
		
		// check that the configuration profiles were removed successfully
		try {
			installedConfigProfiles.add(Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[0], "WebAPI Config Profile iOS 1")[0]);
		} catch(RuntimeException e){
			assertTrue(e.getMessage().startsWith("ID not found for configuration profile"));
		}
		try {
			installedConfigProfiles.add(Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[0], "WebAPI Config Profile iOS 2")[0]);
		} catch(RuntimeException e){
			assertTrue(e.getMessage().startsWith("ID not found for configuration profile"));
		}
		try {
			installedConfigProfiles.add(Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[1], "WebAPI Config Profile iOS 1")[0]);
		} catch(RuntimeException e){
			assertTrue(e.getMessage().startsWith("ID not found for configuration profile"));
		}
		try {
			installedConfigProfiles.add(Helpers.getInstalledConfigurationProfileIdsForConfigurationProfileNamesAndPolicyId(logonCookie, policyIds[1], "WebAPI Config Profile iOS 2")[0]);
		} catch(RuntimeException e){
			assertTrue(e.getMessage().startsWith("ID not found for configuration profile"));
		}
		
		assertTrue(installedConfigProfiles.size()==0);
	}
}
