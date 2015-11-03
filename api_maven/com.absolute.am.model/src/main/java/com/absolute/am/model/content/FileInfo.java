package com.absolute.am.model.content;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfo {

	protected int seed;
	protected String fileName;
	protected String displayName;
	protected String description;
	protected String category;
	protected String fileModDate;
	protected String fileType;
	protected boolean canLeaveApp;
	protected boolean canEmail;
	protected boolean canPrint;
	protected boolean transferOnWifiOnly;
	private String passphrase;
	protected static String DateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public FileInfo() {
		super();
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		sb.append("MediaFile");
		sb.append(" seed:").append(seed);
		sb.append(" fileName:").append(fileName);
		sb.append(" displayName:").append(displayName);
		sb.append(" description:").append(description);
		sb.append(" category:").append(category);
		sb.append(" fileModDate:").append(fileModDate);
		sb.append(" canLeaveApp:").append(canLeaveApp);
		sb.append(" canEmail:").append(canEmail);
		sb.append(" canPrint:").append(canPrint);
		sb.append(" transferOnWifiOnly:").append(transferOnWifiOnly);
		// Probably not safe to be dumping this.
		// sb.append(" passphrase").append(passphrase);
		return sb.toString();
	}

	/**
	 * The seed
	 */
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}

	
	/**
	 * The fileName
	 */
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * The display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * The description
	 */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The category
	 */
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * The file modification date
	 */
	public String getFileModDate() {
		return fileModDate;
	}
	public void setFileModDate(String fileModDate) {
		this.fileModDate = fileModDate;
	}

	/**
	 * The file type
	 */
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * The canLeaveApp flag
	 */
	public boolean isCanLeaveApp() {
		return canLeaveApp;
	}
	public void setCanLeaveApp(boolean canLeaveApp) {
		this.canLeaveApp = canLeaveApp;
	}

	/**
	 * The canEmail flag
	 */
	public boolean isCanEmail() {
		return canEmail;
	}
	public void setCanEmail(boolean canEmail) {
		this.canEmail = canEmail;
	}

	/**
	 * The canPrint flag
	 */
	public boolean isCanPrint() {
		return canPrint;
	}
	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}

	/**
	 * The transferOnWifiOnly flag
	 */
	public boolean isTransferOnWifiOnly() {
		return transferOnWifiOnly;
	}
	public void setTransferOnWifiOnly(boolean transferOnWifiOnly) {
		this.transferOnWifiOnly = transferOnWifiOnly;
	}

	/**
	 * The passphrase
	 */
	public String getPassphrase() {
		return passphrase;
	}
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
}
