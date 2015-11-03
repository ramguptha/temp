/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.model.policyinhouseapp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InHouseAppIdPolicyIdMapping {
	
	private long inHouseAppId;
	private long policyId;

	/**
	 * The in-house application Id
	 */
	public long getInHouseAppId() {
		return inHouseAppId;
	}
	public void setInHouseAppId(long inHouseAppId) {
		this.inHouseAppId = inHouseAppId;
	}
	
	/**
	 * The policy Id
	 */
	public long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(long policyId) {
		this.policyId = policyId;
	}
	
	@Override
	public String toString() {	
		return "InHouseAppIdPolicyIdMapping: inHouseAppId=" + inHouseAppId
				+ " policyId=" + policyId;
	}

}
