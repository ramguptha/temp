/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policythirdpartyapp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ThirdPartyAppIdPolicyIdMapping {
	
	private long thirdPartyAppId;
	private long policyId;

	/**
	 * The third party applications Id list
	 */
	public long getThirdPartyAppId() {
		return thirdPartyAppId;
	}
	public void setThirdPartyAppId(long thirdPartyAppId) {
		this.thirdPartyAppId = thirdPartyAppId;
	}
	
	/**
	 * The Policy Id list
	 */
	public long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(long policyId) {
		this.policyId = policyId;
	}
	
	@Override
	public String toString() {	
		return "ThirdPartyAppIdPolicyIdMapping: thirdPartyAppId=" + thirdPartyAppId
				+ " policyId=" + policyId;
	}

}
