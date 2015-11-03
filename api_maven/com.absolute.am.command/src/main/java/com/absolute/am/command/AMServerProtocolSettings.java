package com.absolute.am.command;

public class AMServerProtocolSettings {
	
	private  String serverHostname;
	private  short serverPort;
	private  String pathToTrustedCertificates;
	
	public AMServerProtocolSettings() {
		serverHostname = "";
		pathToTrustedCertificates = "";			
	}
	
	public AMServerProtocolSettings(String serverHostname, short serverPort, String pathToTrustedCertificates) {
		this.serverHostname = serverHostname;
		this.serverPort = serverPort;
		this.pathToTrustedCertificates = pathToTrustedCertificates;
	}

	/**
	 * @return the serverHostname
	 */
	public String getServerHostname() {
		return serverHostname;
	}
	
	/**
	 * @param serverHostname the serverHostname to set
	 */
	public void setServerHostname(String serverHostname) {
		this.serverHostname = serverHostname;
	}
	
	/**
	 * @return the serverPort
	 */
	public short getServerPort() {
		return serverPort;
	}
	
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(short serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * @return the pathToTrustedCertificates
	 */
	public String getPathToTrustedCertificates() {
		return pathToTrustedCertificates;
	}
	
	/**
	 * @param pathToTrustedCertificates the pathToTrustedCertificates to set
	 */
	public void setPathToTrustedCertificates(String pathToTrustedCertificates) {
		this.pathToTrustedCertificates = pathToTrustedCertificates;
	}


	/**
	 * Helper method to return the server hostname and port number as a single string for logging/messaging purposes.
	 * @return
	 */
	public String getServerHostAndPort() {
		return serverHostname + ":" + serverPort; 
	}
}
