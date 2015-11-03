/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author dlavin
 *
 */
@XmlRootElement
public class SendMessage {

	private String message;
	private int[] deviceIds;

	

	/**
	 * The device Id list
	 */
	public int[] getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(int[] deviceIds) {
		this.deviceIds = deviceIds;
	}


	/**
	 * The message
	 */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {	
		
		return "SendMessage: message=" + message + 
				" deviceIds=" + Arrays.toString(deviceIds);
	}			
}
