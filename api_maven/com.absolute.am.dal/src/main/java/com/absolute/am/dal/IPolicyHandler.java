/**
 * 
 */
package com.absolute.am.dal;

import java.util.List;

import com.absolute.am.dal.model.iOsPolicies;

/**
 * @author klavin
 * 
 */

public interface IPolicyHandler {

	/**
	 * Get all the details for a given policy
	 * @param policyId
	 * @return
	 * @throws Exception
	 */
	public iOsPolicies getPolicy(long policyId) throws Exception; 
	
	/**
	 * Get an array of policy Unique Id's
	 * @param policyIds
	 * @return
	 * @throws Exception
	 */
	public String[] getPolicyUniqueIdsAsString(List<Long> policyIds) throws Exception; 	

	/**
	 * Get the id's of all smart policies
	 * @return
	 * @throws Exception
	 */
	public String getSmartPolicyIdsForDeviceAsString(long deviceId) throws Exception;
	
	/**
	 * Get the id's of all non smart policies
	 * @param deviceUniqueId
	 * @return
	 * @throws Exception
	 */
	public String getNonSmartPolicyIdsForDeviceAsString(String deviceUniqueId) throws Exception;

	/**
	 * Get the id's of all policies for a given media
	 * @param mediaUniqueID
	 * @return
	 * @throws Exception
	 */
	public String getPolicyIdsForMediaAsString(String mediaUniqueID) throws Exception;

}
