package com.absolute.am.webapi;

import com.absolute.util.PropertyList;

public interface ISyncService {
	
	public final static String PROP_SYNC_NOTIFY_URL = "com.absolute.am.webapi.ISyncService.notifyUrl";
	
	/**
	 * Signal to the SyncService that we have a client for this specific server. The SyncService will not respond until it
	 * has created the initial database. 
	 * @param serverHost - The hostname or IP address of the AM server this client is authenticated against.
	 * @param serverPort - The port number that the AM Server is listening on for connections.
	 * @param serverCertificate - The server certificate obtained during authentication with the AM Server.
	 * @param serverUniqueId - The unique id of the server.
	 * @param notifyEndpoint - The endpoint that the Sync Service should POST notifications to.
	 * @return sessionToken - an opaque token that is used in subsequent method calls (stopSync/prioritySync) to identify 
	 * this session. 
	 */
	public Object startSync(
			String serverHost,
			short serverPort,
			byte[] serverCertificate,
			String serverUniqueId,
			String notifyEndpoint
			);
	
	/** 
	 * Get the local database folder for the current sync session.
	 * @param sessionToken - the session token returned by startSync.
	 * @return the folder used to store the locally sync'd database.
	 */
	public String getSyncedDatabaseFolder(
			Object sessionToken);

	/**
	 * Notify the SyncService that synchronization with this AM server is no longer required.
	 * @param sessionToken - the session token of the current sync session.
	 */
	public void stopSync(
			Object sessionToken);
	
	/**
	 * Provide the SyncService with the result of a remote database operation on the AM server. The 
	 * result includes a list of database changed that should be applied locally so that the service
	 * doesn't have to wait for the regular sync to happen before those changes take effect. 
	 * If the amProtocolResponse does not contain the required DBChangeInfo data, no action will 
	 * be taken.
	 * @param sessionToken - the session token returned from startSync().
	 * @param amProtocolResponse - the complete protocol response received from the AM server.
	 */
	public void prioritySync(
			Object sessionToken,
			PropertyList amProtocolResponse);
	
	/**
	 * The SyncService has sent a notification that it has been restarted. A startSync() should be requested for all
	 * current sync sessions. 
	 */
	public void notifyStartup();
	
	/**
	 * The SyncService has been shutdown. All current sync services are disabled.
	 */
	public void notifyShutdown();
	
	/**
	 * Notification that a synchronization activity has started for a session.
	 * @param syncStartedEvent - the sync started event as received.
	 */
	public void notifySyncStarted(PropertyList syncStartedEvent);

	/**
	 * Notification that a synchronization activity has completed for a session.
	 * @param syncCompletedEvent - the sync completed event as received.
	 */
	public void notifySyncCompleted(PropertyList syncCompletedEvent);
	
}

