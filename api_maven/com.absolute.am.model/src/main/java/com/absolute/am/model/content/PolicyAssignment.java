/**
 * 
 */
package com.absolute.am.model.content;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author dlavin
 * 
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAssignment {
	private int policyId;
	private int assignmentType;
	private int availabilitySelector;
	private String startTime;
	private String endTime;

	/**
	 * The policy Id
	 */
	public int getPolicyId() {
		return policyId;
	}
	public void setPolicyId(int policyID) {
		this.policyId = policyID;
	}

	/**
	 * The assignment type
	 */
	public int getAssignmentType() {
		return assignmentType;
	}
	public void setAssignmentType(int assignmentType) {
		this.assignmentType = assignmentType;
	}

	/**
	 * The availability selector
	 */
	public int getAvailabilitySelector() {
		return availabilitySelector;
	}

	public void setAvailabilitySelector(int availabilitySelector) {
		this.availabilitySelector = availabilitySelector;
	}

	/**
	 * The start time
	 */
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * The end time
	 */
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		sb.append("PolicyAssignment");
		sb.append(" policyId:").append(policyId);
		sb.append(" assignmentType:").append(assignmentType);
		sb.append(" availabilitySelector:").append(availabilitySelector);
		sb.append(" startTime:").append(startTime);
		sb.append(" endTime:").append(endTime);
		return sb.toString();
	}

}
