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
public class JobStatusResult {

	private int percentComplete;

	/**
	 * The Percent Complete
	 */
	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}			

	@Override
	public String toString() {	
		return "JobStatusResult: percentComplete=" + percentComplete;
	}

}
