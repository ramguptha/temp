package com.absolute.am.webapi;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.SyncServicesCommandDefines;
import com.absolute.am.command.SyncServicesCommandFactory;
import com.absolute.am.command.iOSDevicesDefines;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class SyncServiceManager implements ISyncService {

	private static final String PROP_SYNC_SERVICE_PORT = "com.absolute.am.webapi.SyncServiceManager.port";
	
	private static final String DATABASE_FOLDER_RESULT = CobraProtocol.kCobra_XML_CommandResultParameters + "/" + SyncServicesCommandDefines.kCobra_SyncServices_StartSync_LocalDatabasesFolder_Result ;
	private static final String SESSION_TOKEN_RESULT = CobraProtocol.kCobra_XML_CommandResultParameters + "/" + SyncServicesCommandDefines.kCobra_SyncServices_StartSync_SessionToken_Result ;
	private static final String DBCHANGEINFO_RESULT = CobraProtocol.kCobra_XML_CommandResultParameters + "/" + iOSDevicesDefines.kCobra_iOS_DatabaseOperation_DBChangeInfo_Result ;
	
    private static final String SYNC_STARTED_SERVER_NAME_PARAM_KEY = 
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_ServerAddress_Param;
    private static final String SYNC_STARTED_SERVER_PORT_PARAM_KEY = 
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_ServerPort_Param;
    private static final String SYNC_STARTED_SESSION_TOKEN_PARAM_KEY =
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncStarted_Notification_SessionToken_Param;
    

    private static final String SYNC_COMPLETED_SERVER_NAME_PARAM_KEY = 
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_ServerAddress_Param;
    private static final String SYNC_COMPLETED_SERVER_PORT_PARAM_KEY = 
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_ServerPort_Param;
    private static final String SYNC_COMPLETED_SESSION_TOKEN_PARAM_KEY =
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_ParamKey + "/" +
    		SyncServicesCommandDefines.kSyncServices_SyncCompleted_Notification_SessionToken_Param;

	
	private static AMServerProtocolSettings m_amServerProtocolSettings;
    private static Logger m_logger = LoggerFactory.getLogger(SyncServiceManager.class.getName());
    
    private Map<String, String> m_serverUniqueIdBySyncSessionToken = new HashMap<String, String>();
    private Map<String, SyncServiceSessionContext> m_syncServiceSessionContextByServer = new HashMap<String, SyncServiceSessionContext>();
    
    private enum ESyncSessionState {    	
    	SYNCING,
    	DEAD
    }
    
    private class SyncServiceSessionContext {
    	private ESyncSessionState sessionState;
    	private int userCount;
    	private String localDatabasePath;
    	private String sessionToken;

    	// The following attributes are needed to recover a sync session when the sync service is restarted.
    	private String serverHost;
    	private short serverPort;
    	private byte[] serverCertificate;
    	private String notifyEndpoint;
    	
    	SyncServiceSessionContext(String serverHost, short serverPort, byte[] serverCertificate, String notifyEndpoint) {
    		this.serverHost = serverHost;
    		this.serverPort = serverPort;
    		this.serverCertificate = serverCertificate;
    		this.notifyEndpoint = notifyEndpoint;
    		this.userCount = 0;    		
    	}
    	
		@Override
		public String toString() {			
			return "SyncServiceSessionContext state: " + sessionState + " token: " + sessionToken + " localDatabasePath: " + localDatabasePath;
		}    	
    	    	
    }
    
	//TODO: Set this up as part of application init, that way it won't need to be synchronized.
    private synchronized AMServerProtocolSettings getAMServerProtocolSettingsForSyncService() {
    	
    	if (m_amServerProtocolSettings == null) {
    		short syncServicePort = Application.getRuntimePropertyAsShort(PROP_SYNC_SERVICE_PORT);
    		m_amServerProtocolSettings = new AMServerProtocolSettings(
    				"localhost",	// Fixed, the sync service is always local 
    				syncServicePort, 
    				"");	// empty string means ignore certificates	
    	}
    	
    	return m_amServerProtocolSettings;
    }
        
    // ISyncService implementation.
	public Object startSync(
			String serverHost, 
			short serverPort,			
			byte[] serverCertificate,
			String serverUniqueId,
			String notifyEndpoint) {

		m_logger.debug("+SyncServiceManager.startSync() called.");
		String retVal = serverUniqueId;
		
		StringUtilities.throwIfNullOrEmpty(serverHost, "serverHost");
		StringUtilities.throwIfNullOrEmpty(serverUniqueId, "serverUniqueId");
		StringUtilities.throwIfNullOrEmpty(notifyEndpoint, "notifyEndpoint");
		
		if (serverPort <= 0) {
			throw new IllegalArgumentException("serverPort:[" + serverPort + "] is not valid.");
		}
		
		if (null == serverCertificate || (serverCertificate != null && serverCertificate.length <=0)) {
			throw new IllegalArgumentException("serverCertificate:[" + serverCertificate + "] is not valid.");
		}		

		// Algorithm.
		//    If active user count for the sync session with this server <= 0,
		//	  synchronized(this) {
		//	}
		//          send StartSync to sync service
		//			if success, save session id and server path
		//				return server uniqueid as token.
		//          else
		//				throw exception.
		//    else
		//        increment session count
		// 		  return server uniqueid as token
		
	
		synchronized(this) {
			
			SyncServiceSessionContext syncContext = m_syncServiceSessionContextByServer.get(serverUniqueId);
			
			if (syncContext == null) {
				syncContext = new SyncServiceSessionContext(serverHost, serverPort, serverCertificate, notifyEndpoint);
				m_syncServiceSessionContextByServer.put(serverUniqueId, syncContext);
			}
			
			if (syncContext.userCount > 0 && syncContext.sessionState != ESyncSessionState.DEAD) {
				syncContext.userCount++;	// updating the reference, so the HashMap is updated too.		
			} else {
				
				// NOTE: Get here for new sync session, and also when recovering from a dead sync session

				// send a start sync
				// if successful, increment userCountForThisServer, return serverUniqueId as token
				// else throw exception
				sendStartSyncAndUpdateContext(serverUniqueId, syncContext);
				
				syncContext.userCount++;					
			}
		}
				
		m_logger.debug("-SyncServiceManager.startSync() retVal={}.", retVal);
		return retVal;
	}
	
	private void sendStartSyncAndUpdateContext(
			String serverUniqueId,
			SyncServiceSessionContext ctx) {
		try {
			
			CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createStartSyncCommand(
					ctx.serverHost, 
					ctx.serverPort, 				
					ctx.serverCertificate,
					ctx.notifyEndpoint);
			
			AMServerProtocol amProtocol = new AMServerProtocol(getAMServerProtocolSettingsForSyncService());
			
			try {
				PropertyList response = amProtocol.sendCommandAndValidateResponse(command, "Send startSync to SyncServices");
				
				// Extract the DB path, and the session id.
				ctx.localDatabasePath = PropertyList.getElementAsString(response, DATABASE_FOLDER_RESULT);
				ctx.sessionToken = PropertyList.getElementAsString(response, SESSION_TOKEN_RESULT);			
				ctx.sessionState = ESyncSessionState.SYNCING;
				
				// Update the sessionToken to serverUniqueId map. This is used to process notification messages from the SyncService.
				m_serverUniqueIdBySyncSessionToken.put(ctx.sessionToken, serverUniqueId);
				
			} finally {
				amProtocol.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("StartSync failed.", e);
		}
	}

	public String getSyncedDatabaseFolder(Object sessionToken) {
		
		String serverUniqueId = (String)sessionToken;
		String retVal = null;
		synchronized(this) {
			SyncServiceSessionContext sessionContext = m_syncServiceSessionContextByServer.get(serverUniqueId);
			if (null == sessionContext) {
				throw new RuntimeException("sync session context not found, sessionToken=" + sessionToken.toString());
			}
			retVal = sessionContext.localDatabasePath;
		}
		
		return retVal;
	}

	public void stopSync(Object sessionToken) {
		
		m_logger.debug("AdminSyncManager.stopSync() called for sessionToken=" + sessionToken.toString());
		
		String serverUniqueId = (String)sessionToken;
		synchronized(this) {
			SyncServiceSessionContext sessionContext = m_syncServiceSessionContextByServer.get(serverUniqueId);
			if (null == sessionContext) {
				// This session has already been stopped. This should never be requested a second time.
				throw new RuntimeException("sync session context not found, sessionToken=" + sessionToken.toString());
			}

			sessionContext.userCount--;
			if (sessionContext.userCount <= 0) {
				m_syncServiceSessionContextByServer.remove(serverUniqueId);
				m_serverUniqueIdBySyncSessionToken.remove(sessionContext.sessionToken);
				try {
					CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createStopSyncCommand(sessionContext.sessionToken);			
					AMServerProtocol amProtocol = new AMServerProtocol(getAMServerProtocolSettingsForSyncService());
					try {
						amProtocol.sendCommandAndValidateResponse(command, "Send stopSync to SyncServices with token:" + sessionContext.sessionToken);								
					} finally {
						amProtocol.close();
					}
				} catch (Exception e) {
					throw new RuntimeException("StopSync failed.", e);
				} 				
			}			
		}		
	}

	public void prioritySync(
			Object sessionToken,
			PropertyList amProtocolResponse) {
				
		Map<String, Object> dbChangeInfo = PropertyList.getElementAs(amProtocolResponse, DBCHANGEINFO_RESULT);
		
		m_logger.debug("AdminSyncManager.prioritySync() called for sessionToken={} and {}", 
				sessionToken.toString(), 
				dbChangeInfo == null ? "dbChangeInfo == null" : "dbChangeInfo != null");
		
		if (dbChangeInfo == null) {
			// As there is no DBChangeInfo parameter in the result, there is nothing to priority sync
			return;			
		} else {
			
			String serverUniqueId = (String)sessionToken;
			String syncSessionToken = null; 
			synchronized(this) {
				syncSessionToken = m_syncServiceSessionContextByServer.get(serverUniqueId).sessionToken;
			}
			
			try {
				CobraAdminMiscDatabaseCommand command = SyncServicesCommandFactory.createPrioritySyncCommand(syncSessionToken, dbChangeInfo);			
				AMServerProtocol amProtocol = new AMServerProtocol(getAMServerProtocolSettingsForSyncService());
				try {
					amProtocol.sendCommandAndValidateResponse(command, "Send prioritySync to SyncServices with token:" + syncSessionToken);								
				} finally {
					amProtocol.close();
				}
			} catch (Exception e) {
				// TODO: Should this cause a state change?
				throw new RuntimeException("PrioritySync failed.", e);
			}
		}
	}

	@Override
	public void notifyStartup() {
		
		m_logger.debug("notifyStartup() called.");
		
		// iterate through all current sync sessions and restart them.
		synchronized(this) {
			// all current session tokens are invalid, so wipe the serverUniqueId by sessionToken lookup map.
			m_serverUniqueIdBySyncSessionToken.clear();
			for (String serverUniqueId: m_syncServiceSessionContextByServer.keySet()) {
				try {
					
					m_logger.debug("notifyStartup() restarting serverUniqueId={}.", serverUniqueId);
					
					SyncServiceSessionContext ctx = m_syncServiceSessionContextByServer.get(serverUniqueId);
					sendStartSyncAndUpdateContext(serverUniqueId, ctx);
					
					m_logger.debug("notifyStartup() restarted serverUniqueId={}, new sessionToken={}.", serverUniqueId, ctx.sessionToken);
					
				} catch (Exception e) {
					// log, completely disable this sessions, continue
					m_logger.error("Attempt to reestablish sync session following SyncServices startup notification failed. e={}", e);
					// TODO: disable this session					
				}
				// continue onto the next session
			}
		}	    		
	}

	@Override
	public void notifyShutdown() {
		m_logger.debug("notifyShutdown() called.");
		
	}

	@Override
	public void notifySyncStarted(PropertyList syncStartedEvent) {

		String server = PropertyList.getElementAsString(syncStartedEvent, SYNC_STARTED_SERVER_NAME_PARAM_KEY);
		Long port = PropertyList.getElementAs(syncStartedEvent, SYNC_STARTED_SERVER_PORT_PARAM_KEY);
		String sessionToken = PropertyList.getElementAsString(syncStartedEvent, SYNC_STARTED_SESSION_TOKEN_PARAM_KEY);
		
		m_logger.debug("SyncStartedEvent: sessionToken={} server={} port={}", sessionToken, server, port);		
		
		// TODO: For the moment we are ignoring sync notifications. TBD if we need to disable access while a sync is in progress.		
	}

	@Override
	public void notifySyncCompleted(PropertyList syncCompletedEvent) {
	
		String server = PropertyList.getElementAsString(syncCompletedEvent, SYNC_COMPLETED_SERVER_NAME_PARAM_KEY);
		Long port = PropertyList.getElementAs(syncCompletedEvent, SYNC_COMPLETED_SERVER_PORT_PARAM_KEY);
		String sessionToken = PropertyList.getElementAsString(syncCompletedEvent, SYNC_COMPLETED_SESSION_TOKEN_PARAM_KEY);
		
		m_logger.debug("SyncCompletedEvent: sessionToken={} server={} port={}", sessionToken, server, port);		
	
		// TODO: For the moment we are ignoring sync notifications. TBD if we need to disable access while a sync is in progress.
	}
	
}
