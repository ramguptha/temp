/**
 * 
 */
package com.absolute.am.model;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author dlavin
 *
 */
@XmlRootElement
public class LogonRequest {

	private String serverName;
	private short serverPort;
	private String userName;
	private String password;
	private String locale;

	/**
	 * The server name
	 */
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}	
	
	/**
	 * The server port
	 */
	public short getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(short serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * The user name
	 */
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * The password
	 */
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**	
	 * The locale
	 */
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@Override
	public String toString() {		
		return "serverName:" + serverName 
				+ " serverPort:" + serverPort
				// Don't include the password.
				+ " userName:" + userName 
				+ " locale:" + locale;
	}			
}
