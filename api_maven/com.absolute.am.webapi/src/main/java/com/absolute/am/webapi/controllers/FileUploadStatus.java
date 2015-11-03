/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.security.MessageDigest;

/**
 * @author dlavin
 *
 */
public class FileUploadStatus {
	private String localFilePath;
	private long totalLength;
	private long currentLength;
	private MessageDigest messageDigest;	
	

	public FileUploadStatus () {
		
	}
	
	public FileUploadStatus(String localFilePath, long totalLength, long currentLength, MessageDigest messageDigest) {
		this.localFilePath = localFilePath;
		this.totalLength = totalLength;
		this.currentLength = currentLength;
		this.messageDigest = messageDigest;
	}
	
	/**
	 * @return the localFilePath
	 */
	public String getLocalFilePath() {
		return localFilePath;
	}
	/**
	 * @param localFilePath the localFilePath to set
	 */
	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}
	/**
	 * @return the totalLength
	 */
	public long getTotalLength() {
		return totalLength;
	}
	/**
	 * @param totalLength the totalLength to set
	 */
	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}
	/**
	 * @return the currentLength
	 */
	public long getCurrentLength() {
		return currentLength;
	}
	/**
	 * @param currentLength the currentLength to set
	 */
	public void setCurrentLength(long currentLength) {
		this.currentLength = currentLength;
	}

	/**
	 * @return the digest
	 */
	public MessageDigest getMessageDigest() {
		return messageDigest;
	}

	/**
	 * @param digest the digest to set
	 */
	public void setMessageDigest(MessageDigest messageDigest) {
		this.messageDigest = messageDigest;
	}	
}
