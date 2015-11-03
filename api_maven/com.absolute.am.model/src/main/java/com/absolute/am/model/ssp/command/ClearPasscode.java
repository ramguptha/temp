/**
 * 
 */
package com.absolute.am.model.ssp.command;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClearPasscode  {
	
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
	 * hasPasscode
	 * @return flag
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
