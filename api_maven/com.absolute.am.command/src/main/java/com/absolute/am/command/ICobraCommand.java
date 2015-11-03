package com.absolute.am.command;

import com.absolute.util.PropertyList;

public interface ICobraCommand {
	public PropertyList buildCommandDictionary();
	public String toXml();
}
