package com.absolute.am.model.policy;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PolicyList {

	private int[] policyIds;

	
	/**
	 * The policy ids
	 */
	public int[] getPolicyIds() {
		return policyIds;
	}
	public void setPolicyIds(int[] policyIds) {
		this.policyIds = policyIds;
	}


	@Override
	public String toString() {	
		
		return "PolicyList: policyIds=" + Arrays.toString(policyIds);
	}			
}