package com.absolute.am.model.ssp.command;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 */
@XmlRootElement
public class Lock extends GenericCommand {

	private String passcode, message, phoneNumber;
	
	/**
	 * The passcode
	 */	
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
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
	
	/**
	 * The phone number
	 */	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
