/**
 * 
 */
package com.absolute.am.model.command;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoteErase extends GenericDeviceCommand {

	private boolean includeSDCard;
	
	/**
	 * Flag to determine whether to include SD card or not in remote erase command execution. This is applied to Android devices only.
	 */	
	public boolean getIncludeSDCard() {
		return this.includeSDCard;
	}
	public void setIncludeSDCard(boolean includeSDCard) {
		this.includeSDCard = includeSDCard;
	}
	
	@Override
	public String toString() {	
		
		// For security reasons, we shouldn't show the passcode itself. However, we can show some properties
		// of the passcode that would be useful for debugging.
		return "RemoteErase: includeSDCard=" + includeSDCard + 
				" " + super.toString();
	}	
}
