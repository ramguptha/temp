package com.absolute.am.model.customfieldmobiledevice;

public class CustomFieldItem {
	public String id;
	public String value;
	public int type;
	
	// 'File Version' type needs these to be able to correctly deserialize a version string
	public String valueHigh32;
	public String valueLow32;
}