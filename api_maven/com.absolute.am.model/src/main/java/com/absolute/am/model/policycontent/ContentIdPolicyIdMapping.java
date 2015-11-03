/**
 * 
 */
package com.absolute.am.model.policycontent;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author klavin
 *
 */
@XmlRootElement
public class ContentIdPolicyIdMapping {

	private long contentId;
	private long policyId;

	/**
	 * The content Id
	 */
	public long getContentId() {
		return contentId;
	}
	public void setContentId(long contentId) {
		this.contentId = contentId;
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
		return "ContentIdPolicyIdMapping: contentId=" + contentId
				+ " policyId=" + policyId;
	}			
}
