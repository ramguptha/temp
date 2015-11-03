package com.absolute.am.model.userprefs;

public class KeyContentType {
	/*
	* sample data:
	* 		{ "name": "FirstName", "contentType": "text/plain" },
	* 		{ "name": "LastName", "contentType": "text/plain" },
	* 		{ "name": "Photo", "contentType": "image/png" }
	*/	
	private String _name;
	private String _contentType;
	
	public KeyContentType(String name, String contentType) {
		setName(name);
		setContentType(contentType);
	}
	
	/**
	 * The name
	 */	
	public void setName(String name) {
		_name = name;
	}
	public String getName() {
		return _name;
	}
	
	/**
	 * The content type
	 */	
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	public String getContentType() {
		return _contentType;
	}	
}
