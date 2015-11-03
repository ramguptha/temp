/**
 * 
 */
package com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import com.absolute.util.PropertyList;

/**
 * @author dlavin
 * 
 */
public class SyncServicesCommandFactory {

	public enum CertificateFormat {
		PEM,
		DER
	}
	
	public static CobraAdminMiscDatabaseCommand createStartSyncCommand(
			String serverAddress, 
			short serverPort,			
			byte[] serverCertificate,
			String notifyEndpoint) throws IOException,
			GeneralSecurityException {

		PropertyList startSyncCommandParameters = new PropertyList();
		startSyncCommandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerAddress_Param, serverAddress);
		startSyncCommandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerPort_Param, serverPort);
		startSyncCommandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerCertificate_Param, serverCertificate);
		startSyncCommandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerCertificateFormat_Param,
						SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerCertificateFormat_DER);		
		startSyncCommandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_NotifyEndpoint_Param, notifyEndpoint);
//		startSyncCommandParameters.put(
//				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_WebAPIEventKey_Param, "none");
		
		
		CommandInfoData inCommandInfoData = new CommandInfoData(
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_Command,
				CobraProtocol.kCobraSyncServicesServer);
		CobraAdminMiscDatabaseCommand startSyncCommand = new CobraAdminMiscDatabaseCommand(
				inCommandInfoData, null, startSyncCommandParameters);
		return startSyncCommand;
	}


	public static CobraAdminMiscDatabaseCommand createStopSyncCommand(
			String syncSessionToken) throws Exception {
		
		PropertyList commandParameters = new PropertyList();
		commandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StopSync_SessionToken_Param, syncSessionToken);
		
		CommandInfoData inCommandInfoData = new CommandInfoData(
				SyncServicesCommandDefines.kCobra_SyncServices_StopSync_Command,
				CobraProtocol.kCobraSyncServicesServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(
				inCommandInfoData, null, commandParameters);
		return command;
	}

	public static CobraAdminMiscDatabaseCommand createPrioritySyncCommand(
			String syncSessionToken,
			Map<String, Object> dbChangeInfoResult) throws Exception {
		
		PropertyList commandParameters = new PropertyList();
		commandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_StopSync_SessionToken_Param, syncSessionToken);
		commandParameters.put(
				SyncServicesCommandDefines.kCobra_SyncServices_PrioritySync_DBChangeInfo_Param, dbChangeInfoResult);
		
		CommandInfoData inCommandInfoData = new CommandInfoData(
				SyncServicesCommandDefines.kCobra_SyncServices_PrioritySync_Command,
				CobraProtocol.kCobraSyncServicesServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(
				inCommandInfoData, null, commandParameters);
		return command;
	}
	
}
