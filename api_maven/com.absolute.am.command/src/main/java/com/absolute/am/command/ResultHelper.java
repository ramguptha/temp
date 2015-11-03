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
public class ResultHelper {

	/**
	 * Given a standard logon result, this helper method extracts the AdminUUID.
	 * @param logonResult
	 * @return
	 */
	public static UUID getAdminUUIDFromLogonResult(PropertyList logonResult) {

		// Extract the AdminUUID.
		String path = CobraProtocol.kCobra_XML_CommandResultParameters
				+ "/"
				+ CobraCommandDefs.kCobra_AdminLogin_AdminInfo_Result 
				+ "/" 
				+ CobraProtocol.kCobra_XML_AdminUUID;
		
		String adminUUIDString = PropertyList.getElementAsString(logonResult, path);
		UUID retVal = UUID.fromString(adminUUIDString);		
		return retVal;
	}
}
