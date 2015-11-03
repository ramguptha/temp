package com.absolute.am.model.command;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class GenericCommand {

	private Map<String, Object> commandParameters;
	
	public Map<String, Object> getCommandParameters() {
		return commandParameters;
	}
	
	public void setCommandParameters(Map<String, Object> commandParameters){
		this.commandParameters = commandParameters;
	}
	
	@Override
	public String toString() {	
		return "Generic Command: commandParameters=" + commandParameters.toString();
	}			
}