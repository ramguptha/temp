/**
 * 
 */
package com.absolute.am.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dlavin
 *
 */
@XmlRootElement
public class LogonResult {

	private Map<String, Object> resultParameters;

	public void setResultParameters(Map<String, Object> resultParameters) {
		this.resultParameters = resultParameters;
	}
	
	/**
	 * Result parameters
	 */
	public Map<String, Object> getResultParameters() {
		return resultParameters;
	}
 
}
