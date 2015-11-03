package com.absolute.am.dal.model;

public class UserPreference {
	
	private String _name;
	private String _contentType;
	private String _value;
	private int _isFile;
	private String _filePath;
	
	public UserPreference(String name, String contentType, String value, int isFile, String filePath) {
		setName(name);
		setContentType(contentType);
		setValue(value);
		setIsFile(isFile);
		setFilePath(filePath);
	}
	public void setName(String name) {
		_name = name;
	}
	public String getName() {
		return _name;
	}
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	public String getContentType() {
		return _contentType;
	}	
	public void setValue(String value) {
		_value = value;
	}
	public String getValue() {
		return _value;
	}
	public void setIsFile(int isFile) {
		_isFile = isFile;
	}
	public int getIsFile() {
		return _isFile;
	}
	public void setFilePath(String filePath) {
		_filePath = filePath;
	}
	public String getFilePath() {
		return _filePath;
	}
}