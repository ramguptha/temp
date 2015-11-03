/**
 * 
 */
package com.absolute.am.model.command;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClearPasscode extends GenericDeviceCommand  {
	
	private String passcode;


	/**
	 * The passcode to which devices will be set
	 */	
	public String getPasscode() {
		return passcode;
	}

	
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}	
	
	
    /**
	 * The device has a passcode
	 */
	public boolean hasPasscode()
	{		
		return this.getPasscode() != null && this.getPasscode().length() > 0;		
	}
	
	@Override
	public String toString() {	
		
		// For security reasons, we shouldn't show the passcode itself. However, we can show some properties
		// of the passcode that would be useful for debugging.
		return "ClearPasscode: passcode is null=" + (passcode == null) +
				" passcode.length=" + (passcode == null ? 0 : passcode.length()) + 
				" " + super.toString();
	}	
}
