package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "mobile_media")

public class MobileMedia implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "UniqueId")
	private String uniqueId;
	
	@Column(name = "Seed")
	private Integer seed;
	
	@Column(name = "DisplayName")
	private String displayName;
	
	@Column(name = "Category")
	private String category;
	
	@Column(name = "Filename")
	private String filename;
	
	@Column(name = "FileModDate")
	private String fileModDate;
	
	@Column(name = "FileSize")
	private Long fileSize;
	
	@Column(name = "FileType")
	private String fileType;
	
	@Column(name = "FileMD5")
	private String fileMD5;
	
	@Column(name = "Description")
	private String description;
	
	@Column(name = "CanLeaveApp")
	private Boolean canLeaveApp;
	
	@Column(name = "CanEmail")
	private Boolean canEmail;
	
	@Column(name = "CanPrint")
	private Boolean canPrint;
	
	@Column(name = "EncryptionKey")
	private String encryptionKey;
	
	@Column(name = "PassPhraseHash")
	private String passPhraseHash;
	
	@Column(name = "Icon")
	private byte[] icon;
	
	@Column(name = "TransferOnWifiOnly")
	private Boolean transferOnWifiOnly;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public Integer getSeed() {
		return seed;
	}
	public void setSeed(Integer seed) {
		this.seed = seed;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileModDate() {
		return fileModDate;
	}
	public void setFileModDate(String fileModDate) {
		this.fileModDate = fileModDate;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileMD5() {
		return fileMD5;
	}
	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getCanLeaveApp() {
		return canLeaveApp;
	}
	public void setCanLeaveApp(Boolean canLeaveApp) {
		this.canLeaveApp = canLeaveApp;
	}
	public Boolean getCanEmail() {
		return canEmail;
	}
	public void setCanEmail(Boolean canEmail) {
		this.canEmail = canEmail;
	}
	public Boolean getCanPrint() {
		return canPrint;
	}
	public void setCanPrint(Boolean canPrint) {
		this.canPrint = canPrint;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public String getPassPhraseHash() {
		return passPhraseHash;
	}
	public void setPassPhraseHash(String passPhraseHash) {
		this.passPhraseHash = passPhraseHash;
	}
	
	public byte[] getIcon() {
		return icon;
	}
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	
	public Boolean getTransferOnWifiOnly() {
		return transferOnWifiOnly;
	}
	public void setTransferOnWifiOnly(Boolean transferOnWifiOnly) {
		this.transferOnWifiOnly = transferOnWifiOnly;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}