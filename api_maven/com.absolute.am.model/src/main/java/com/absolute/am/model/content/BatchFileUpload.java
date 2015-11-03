/**
 * 
 */
package com.absolute.am.model.content;

import com.absolute.am.model.policy.PolicyAssignment;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dlavin
 *
 */
@XmlRootElement
public class BatchFileUpload {
	FileInfo[] newFiles;
	PolicyAssignment[] assignToPolicies;
	
	/**
	 * Set new files to upload
	 */
	public FileInfo[] getNewFiles() {
		return newFiles;
	}
	public void setNewFiles(FileInfo[] newFiles) {
		this.newFiles = newFiles;
	}
	
	/**
	 * The polices to assign to
	 */
	public PolicyAssignment[] getAssignToPolicies() {
		return assignToPolicies;
	}
	public void setAssignToPolicies(PolicyAssignment[] assignToPolicies) {
		this.assignToPolicies = assignToPolicies;
	}
}
