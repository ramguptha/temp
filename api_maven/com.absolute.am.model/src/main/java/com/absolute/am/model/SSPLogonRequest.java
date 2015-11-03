package com.absolute.am.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class SSPLogonRequest {

	private String domain = null;
	private String userName;
	private String password;
	private String locale;
	private String serverName;
	private String serverPort;
		
	/**
	 * @return The user name
	 */
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return The password
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
	
	/**	
	 * The domain
	 */
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	/**
	 * The Server Name
	 */
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * The Port Number
	 */
	public String getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	@Override
	public String toString() {		
		return "domain:" + domain 
				+ " userName:" + userName 
				+ " locale:" + locale;
	}			
}
