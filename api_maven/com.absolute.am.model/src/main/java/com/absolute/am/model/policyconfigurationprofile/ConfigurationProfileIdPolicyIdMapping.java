package com.absolute.am.model.policyconfigurationprofile;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConfigurationProfileIdPolicyIdMapping {
	
	private long configurationProfileId;
	private long policyId;

	/**
	 * The configuration profile Id
	 */
	public long getConfigurationProfileId() {
		return configurationProfileId;
	}
	public void setConfigurationProfileId(long configurationProfile) {
		this.configurationProfileId = configurationProfile;
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
		return "ConfigurationProfileIdPolicyIdMapping: configurationProfileId=" + configurationProfileId
				+ " policyId=" + policyId;
	}

}
