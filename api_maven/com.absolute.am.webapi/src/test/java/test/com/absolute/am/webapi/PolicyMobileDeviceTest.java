package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.MobileDevices;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;

public class PolicyMobileDeviceTest extends LoggedInTest {
	
	private String[] m_deviceIds;
	private String[] m_policyIds;
	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		m_deviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, MobileDevices.MOBILE_DEVICE_NAMES[0], MobileDevices.MOBILE_DEVICE_NAMES[1]);
		m_policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1], Policies.STANDARD_POLICY_NAMES[2], Policies.SMART_POLICY_NAMES[0]);
		
		test_cant_add_missing_mobiledevice_to_policy();
		test_cant_assign_mobiledevices_to_missing_policy();
		test_cant_assign_mobiledevices_to_smart_policy();
		test_can_assign_multiple_devices_to_multiple_policies();
		test_cant_delete_missing_mobiledevice_from_policy();
		test_cant_delete_mobiledevices_from_missing_policy();
		test_cant_delete_mobiledevices_from_smart_policy();
		test_can_delete_multiple_devices_from_multiple_policies();
	}

	public void test_cant_add_missing_mobiledevice_to_policy() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append("0,99999],");		//non-existing device ids
		sb.append("\"policyIds\": [" + m_policyIds[0] + "," + m_policyIds[1] + "," + m_policyIds[1] + "]");	//existing policy ids
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	public void test_cant_assign_mobiledevices_to_missing_policy() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(m_deviceIds[0] + "," + m_deviceIds[1] + "],");						//existing device ids
		sb.append("\"policyIds\": [9999]");		//non-existing policy id
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	
	public void test_cant_assign_mobiledevices_to_smart_policy() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(m_deviceIds[0] + "," + m_deviceIds[1] + "],");						//existing device ids
		sb.append("\"policyIds\": [" + m_policyIds[0] + "," + m_policyIds[3] + "]");		//mix of dumb and smart policies
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	public void test_can_assign_multiple_devices_to_multiple_policies() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(m_deviceIds[0] + "," + m_deviceIds[1] + "],");						//existing device ids
		sb.append("\"policyIds\": [");
		sb.append(m_policyIds[0] + "," + m_policyIds[1] + "]");				//existing policy ids
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_API, 
				sb.toString(), HttpStatus.SC_NO_CONTENT);
	}

	public void test_cant_delete_missing_mobiledevice_from_policy() throws ClientProtocolException, IOException, NoSuchAlgorithmException, InterruptedException, KeyManagementException {
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{\"deviceId\":0,");		//non-existing device id
		sb.append("\"policyId\":" + m_policyIds[0] + "},");		//existing policy id
		sb.append("{\"deviceId\":0,");		//non-existing device id
		sb.append("\"policyId\":" + m_policyIds[1] + "},");		//existing policy id
		sb.append("{\"deviceId\":99999,");	//non-existing device id
		sb.append("\"policyId\":" + m_policyIds[2] + "}");		//existing policy id
		sb.append("]}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);
	}
	
	public void test_cant_delete_mobiledevices_from_missing_policy() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{\"deviceId\":" + m_deviceIds[0] + ",");		//existing device id
		sb.append("\"policyId\":9999},");		//non-existing policy id
		sb.append("{\"deviceId\":" + m_deviceIds[1] + ",");	//existing device id
		sb.append("\"policyId\":9999}]");		//non-existing policy id
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	public void test_cant_delete_mobiledevices_from_smart_policy() throws Exception {
		
		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{\"deviceId\":" + m_deviceIds[0] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[0] + "},");		//dumb policy id
		sb.append("{\"deviceId\":" + m_deviceIds[1] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[3] + "}]");		//smart policy id
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_DELETE_API, 
				sb.toString(), HttpStatus.SC_BAD_REQUEST);

	}
	
	public void test_can_delete_multiple_devices_from_multiple_policies() throws Exception {

		StringBuilder sb = new StringBuilder();		
		sb.append("{");
		sb.append("\"associations\":[");
		sb.append("{\"deviceId\":" + m_deviceIds[0] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[0] + "},");		//existing policy id
		sb.append("{\"deviceId\":" + m_deviceIds[0] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[1] + "},");		//existing policy id
		sb.append("{\"deviceId\":" + m_deviceIds[1] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[0] + "},");		//existing policy id
		sb.append("{\"deviceId\":" + m_deviceIds[1] + ",");		//existing device id
		sb.append("\"policyId\":" + m_policyIds[1] + "}]");		//existing policy id
		sb.append("}");
		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + POLICY_MOBILE_DEVICE_DELETE_API, 
				sb.toString(), HttpStatus.SC_NO_CONTENT);
	}

}
