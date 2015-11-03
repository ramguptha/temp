package com.absolute.am.model;

public class DefaultServerInfoResult {

	private String serverName;
	private String serverPort;
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * The server name
	 */
	public String getServerName() {
		return serverName;
	}
	
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * The server port
	 */
	public String getServerPort() {
		return serverPort;
	}
}
