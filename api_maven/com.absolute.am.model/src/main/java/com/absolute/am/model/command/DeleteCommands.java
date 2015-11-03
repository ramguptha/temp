package com.absolute.am.model.command;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class DeleteCommands {
	private int[] commandIds;
	
	public int[] getCommandIds() {
		return commandIds;
	}
	public void setCommandIds(int[] commandIds) {
		this.commandIds = commandIds;
	}
}
