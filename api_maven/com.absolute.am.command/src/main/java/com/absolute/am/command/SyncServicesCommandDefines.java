/**
 * 
 */
package com.absolute.am.command;

/**
 * @author dlavin
 *
 */
public class SyncServicesCommandDefines {

	/// @name StartSync Command
	/// @{
	public static final int kCobra_SyncServices_StartSync_Command = 3000; //!< \a send from the WebUI to SyncServices to start syncing with a specific server

	public static final String kCobra_SyncServices_StartSync_ServerAddress_Param = "ServerAddress";							//!< \a string; the address of the server to sync with
	public static String kCobra_SyncServices_StartSync_ServerPort_Param = "ServerPort";								//!< \a integer; the port the server is listening on
	public static String kCobra_SyncServices_StartSync_NotifyEndpoint_Param = "NotifyEndpoint";						//!< \a string; the URL to post notifications for this sync request to
	public static String kCobra_SyncServices_StartSync_WebAPIEventKey_Param = "WebAPIEventKey";						//!< \a string; the token to pass to notification endpoint to identify the WebAPI session
	public static String kCobra_SyncServices_StartSync_ServerCertificate_Param = "ServerCertificate";					//!< \a data; the server certificate in binary form
	public static String kCobra_SyncServices_StartSync_ServerCertificateFormat_Param = "ServerCertificateFormat";		//!< \a string; the format of the server certificate data; "PEM" or "DER"
	public static String kCobra_SyncServices_StartSync_ServerCertificateFormat_PEM = "PEM";
	public static String kCobra_SyncServices_StartSync_ServerCertificateFormat_DER	= "DER";

	public static String kCobra_SyncServices_StartSync_SessionToken_Result = "SessionToken";						//!< \a string; the token for this sync session; needs to be passed with all subsequent commands after StartSync
	public static String kCobra_SyncServices_StartSync_LocalDatabasesFolder_Result = "LocalDatabasesFolder";		//!< \a string; the path to the local databases folder for this sync instance
	/// @}

	/// @name StopSync Command
	/// @{
	public static final int kCobra_SyncServices_StopSync_Command = 3001;								//!< \a send from the WebUI to SyncServices to stop syncing with a specific server

	public static String kCobra_SyncServices_StopSync_SessionToken_Param = "SessionToken";							//!< \a string; the session token identifying this sync session (returned by StartSync command)
	/// @}


	/// @name PrioritySync Command
	/// @{
	public static final int kCobra_SyncServices_PrioritySync_Command = 3002;								//!< \a send from the WebUI to SyncServices to force a priority sync with a specific server

	public static String kCobra_SyncServices_PrioritySync_SessionToken_Param = "SessionToken";							//!< \a string; the session token identifying this sync session (returned by StartSync command)
	public static String kCobra_SyncServices_PrioritySync_DBChangeInfo_Param = "DBChangeInfo";							//!< \a dict; the change info returned for by AM server to be integrated into the cached DB
	/// @}


	// ------------------------------------------------------ Notifications for FrontEnd --------------------------------------------------------------------------

	public static String kSyncServices_Notification_SessionToken_Param = "SessionToken";		//!< \a string; the SyncServices token for this sync session
	public static String kSyncServices_Notification_WebAPIEventKey_Param = "WebAPIEventKey";	//!< \a string; the WebAPI token for this sync session

	/// @name Startup Notification
	/// @{
	public static String kSyncServices_Startup_Notification = "startup";
	/// @}

	/// @name SyncStarted Notification
	/// @{
	public static String kSyncServices_SyncStarted_Notification = "syncstarted";

	public static String kSyncServices_SyncStarted_Notification_ParamKey = "SyncStartedEvent";
	public static String kSyncServices_SyncStarted_Notification_ServerAddress_Param = "Server";					//!< \a string; the address of the server passed in the "StartSync" command
	public static String kSyncServices_SyncStarted_Notification_ServerPort_Param = "Port";						//!< \a integer; the port of the server passed in the "StartSync" command
	public static String kSyncServices_SyncStarted_Notification_UpdateTables_Param = "UpdateTables";			//!< \a array; list of tables that will be checked for updates
	public static String kSyncServices_SyncStarted_Notification_SessionToken_Param = "SessionToken";			//!< \a string; the SyncServices token for this session.
	/// @}

	/// @name SyncCompleted Notification
	/// @{
	public static String kSyncServices_SyncCompleted_Notification = "synccompleted";
	public static String kSyncServices_SyncCompleted_Notification_ParamKey = "SyncCompletedEvent";
	public static String kSyncServices_SyncCompleted_Notification_ServerAddress_Param = "Server";					//!< \a string; the address of the server passed in the "StartSync" command
	public static String kSyncServices_SyncCompleted_Notification_ServerPort_Param = "Port";						//!< \a integer; the port of the server passed in the "StartSync" command
	public static String kSyncServices_SyncCompleted_Notification_UpdatedTables_Param = "UpdatedTables";			//!< \a array; list of tables that were updated in the sync
	public static String kSyncServices_SyncCompleted_Notification_SessionToken_Param = "SessionToken";			    //!< \a string; the SyncServices token for this session.
	
	/// @}

	/// @name PrioritySyncDone Notification
	/// @{
	public static String kSyncServices_PrioritySyncDone_Notification = "syncstopped";
	public static String kSyncServices_PrioritySyncDone_Notification_ParamKey = "PrioritySyncDoneEvent";
}
