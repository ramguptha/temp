/**
 * 
 */
package com.absolute.am.model;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author klavin
 *
 */
@XmlRootElement
public class JobIdResult {

	private String jobId;
	
	/**
	 * The job Id
	 */
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Override
	public String toString() {	
		return "JobIdResult: jobId=" + jobId;
	}

}
