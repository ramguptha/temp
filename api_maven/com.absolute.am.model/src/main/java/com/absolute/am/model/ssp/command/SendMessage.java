package com.absolute.am.model.ssp.command;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 */
@XmlRootElement
public class SendMessage extends GenericCommand {

	private String message;
	private boolean withCancel;
	private int timeout;
	
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
	 * The Timeout
	 */	
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * The WithCancel flag
	 */	
	public boolean getWithCancel() {
		return withCancel;
	}

	public void setWithCancel(boolean withCancel) {
		this.withCancel = withCancel;
	}
}
