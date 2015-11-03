/**
 * 
 */
package com.absolute.am.command;

import java.util.UUID;

import com.absolute.util.PropertyList;

/**
 * @author dlavin
 * 
 */
public class CobraAdminCommand implements ICobraCommand {

	public CobraAdminCommand() {
	}

	public CobraAdminCommand(CommandInfoData inCommandInfoData, UUID inAdminID,
			PropertyList inParamList) {
		CommandInfo = inCommandInfoData;
		AdminUUID = inAdminID;
		ParamList = inParamList;
		ParamListAvailable = true;
	}

	/**
	 * Converts the command into a PropertyList.
	 * @return the PropertyList representation of the command.
	 */
	public PropertyList buildCommandDictionary() {
		// This is the equivalent C++.
		// inOutDict->SetNumberValue(kCobra_XML_ServerID,inServerID);
		// inOutDict->SetNumberValue(kCobra_XML_CommandID,fCommandInfoData.CommandID);
		// inOutDict->SetUUIDValue(kCobra_XML_CommandUUID,fCommandInfoData.CommandUUID);
		// inOutDict->SetNumberValue(kCobra_XML_CommandVersion,fCommandInfoData.CommandVersion);
		// inOutDict->SetDateTimeValue(kCobra_XML_CommandExecutionDateTime,fCommandInfoData.CommandExecutionDateTime);
		// if (inParamList)
		// inOutDict->SetValue(kCobra_XML_CommandParameters,*inParamList);
		// if (fAdminUUID!=CP_UUID::null)
		// inOutDict->SetUUIDValue(kCobra_XML_AdminUUID,fAdminUUID);

		PropertyList retVal = new PropertyList();
		retVal.put(CobraProtocol.kCobra_XML_ServerID, CommandInfo.ServerType);
		retVal.put(CobraProtocol.kCobra_XML_CommandID, CommandInfo.CommandID);
		retVal.put(CobraProtocol.kCobra_XML_CommandUUID, CommandInfo.CommandUUID);
		retVal.put(CobraProtocol.kCobra_XML_CommandVersion, CommandInfo.CommandVersion);
		retVal.put(CobraProtocol.kCobra_XML_CommandExecutionDateTime, CommandInfo.CommandExecutionDateTime);
		
		if (AdminUUID != null) {
			retVal.put(CobraProtocol.kCobra_XML_AdminUUID, AdminUUID);
		}
		
		if (ParamListAvailable) {
			retVal.put(CobraProtocol.kCobra_XML_CommandParameters, ParamList);
		}


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

	protected CommandInfoData CommandInfo;
	protected PropertyList ParamList;
	protected UUID AdminUUID = null;
	protected boolean ParamListAvailable = false;
}
