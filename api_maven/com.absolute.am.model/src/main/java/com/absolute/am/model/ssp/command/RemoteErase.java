package com.absolute.am.model.ssp.command;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 */
@XmlRootElement
public class RemoteErase extends GenericCommand {

	private boolean includeSDCard;
	private String passcode;

	/**
	 * Flag to determine whether to include SD card or not in remote erase command execution. This is applied to Android devices only.
	 */	
	public boolean getIncludeSDCard() {
		return this.includeSDCard;
	}
	public void setIncludeSDCard(boolean includeSDCard) {
		this.includeSDCard = includeSDCard;
	}
	
	/**
	 * The passcode
	 */	
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
}
