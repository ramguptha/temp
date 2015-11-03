package com.absolute.am.command;

import com.absolute.util.PropertyList;

/**
 * @author ephilippov
 */
public class CobraUserCommand implements ICobraCommand {

	private PropertyList commandParameters;
	private CommandInfoData commandInfo;
	
	public CobraUserCommand(CommandInfoData commandInfo, PropertyList commandParameters) {
		this.commandParameters = commandParameters;
		this.commandInfo = commandInfo;
	}

	/**
	 * Converts the command into a PropertyList.
	 * @return the PropertyList representation of the command.
	 */
	public PropertyList buildCommandDictionary() {

		PropertyList retVal = new PropertyList();
		
		retVal.put(CobraProtocol.kCobra_XML_ServerID, commandInfo.ServerType);
		retVal.put(CobraProtocol.kCobra_XML_CommandID, commandInfo.CommandID);
		retVal.put(CobraProtocol.kCobra_XML_CommandUUID, commandInfo.CommandUUID);
		retVal.put(CobraProtocol.kCobra_XML_CommandVersion, commandInfo.CommandVersion);
		retVal.put(CobraProtocol.kCobra_XML_CommandExecutionDateTime, commandInfo.CommandExecutionDateTime);
		
		retVal.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param, commandParameters);

		return retVal;
	}

	/**
	 * Converts the command to a PropertyList and calls toXMLString() on that object.
	 * {@link PropertyList#toXMLString()}
	 * @return XML representation of the command (an XML PLIST).
	 */
	public String toXml() {
		String xml = buildCommandDictionary().toXMLString();
		return xml;
	}
}
