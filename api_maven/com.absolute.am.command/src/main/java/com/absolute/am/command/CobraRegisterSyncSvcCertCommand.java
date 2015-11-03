/**
 * 
 */
package com.absolute.am.command;

import java.util.UUID;

import com.absolute.util.PropertyList;

/**
 * @author klavin
 * 
 */
public class CobraRegisterSyncSvcCertCommand  extends CobraAdminCommand{

	public CobraRegisterSyncSvcCertCommand(CommandInfoData inCommandInfoData,
			UUID inAdminID, PropertyList inParamList) {
		super(inCommandInfoData, inAdminID, inParamList);
	}

//	private PropertyList m_paramList;
//
//	
//	public String ToXml() {
//		String xml = BuildCommandDictionary().toXMLString();
//		return xml;
//	}
//
//	@Override
//	public PropertyList BuildCommandDictionary() {
//		PropertyList retVal = new PropertyList();
//		retVal.put(CobraProtocol.kCobra_XML_CommandID, CobraCommandDefs.kCobra_RegisterSyncSvcCert_Command);
//		retVal.put(CobraProtocol.kCobra_XML_CommandParams, m_paramList);
//
//		return retVal;
//
//	}
	
}
