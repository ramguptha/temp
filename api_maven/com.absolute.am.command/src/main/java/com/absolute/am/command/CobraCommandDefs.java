/**
 * 
 */
package com.absolute.am.command;

/**
 * @author dlavin
 *
 */
public class CobraCommandDefs {

	static final String kBlowfishPasswordKey = "It ain't over until the fat lady sing."; // Blowfish key used encode/decode any kind of password
//	static final String kGeoLocationPINBlowfishPasswordKey =  "rArVCjPJSvyxTyh8oHvBuVfciMaOSLmzJogCKilwmXmmrqe3Te";	// Blowfish key used encode/decode GeoLocationPIN
	public static final String kBlowfishAdminLogingPasswordKey = "zDhLFWwQmnTONfL3dHkXLxXSoaAYvkuLdZvIzJUM4kkHHOvxyG";	// Blowfish key used encode/decode admin password during login password
//	static final String kBlowfishAdminUUIDKey = "lWMUKZWsNR2THXdlOjqibFMNCKSX2BtIzDWuGRPSWxaqyYKHDY";	// Blowfish key used encode/decode admin UUID when send via MDMProxy
	public static final String kBlowfishMDMPayloadKey = "QGaUUIKMxeVNLXKhHNWLVxYonlDZEsZzFjLD8SCN0hdecTNQbQ";	// Blowfish key used encode/decode the stored encryption key for MDM payload files
	public static final String kBlowfishMDMDeviceLockPasswordKey = "YKZnwPSFajzpI9HGZEiWBO5QEfpIbIGXNMraDpnKtHKCQpgXhC";	// Blowfish key used encode/decode the password for lock device
//	static final String kConfigurationProfileDataPasswordKey = "WWK3qMWUKwtFcJTtLIKZGzMR6NPruDPmrYJfbZPiTclFk3oYAi";	// Blowfish key used encode/decode config profile data
	public static final String kSelfServiceLoginCredentialsKey = "leStm0obo5oLj6ZJQfztLsoBfpgMz0GbTWI5MwU1p8AO2ZVG4J";
	
	
    public static final String kCobra_XML_AdminAppUUID	= "AdminAppUUID";									//!< \a UUID; a UUID which identifies the admin app
    public static final String kCobra_XML_AdminAppListenPort = "AdminAppListenPort";						//!< \a number; the port the admin is listening on

    public static final String kCobra_Admin_CommandPermissions_Param = "CommandPermissions";				//!< \a number; bit mask for the permission of a user

    // ------------------------------------------------------ Login Command --------------------------------------------------------------------------
    public static final int kCobra_Admin_Login_Command = 1007;								//!< \a send from the admin when user logs in
    public static final String kCobra_AdminLogin_AdminName_Param = "AdminName";					//!< \a string; name of the admin
    public static final String kCobra_AdminLogin_AdminPassword_Param = "AdminPassword";			//!< \a string; password of the admin

    public static final int kCobra_SelfService_Command = 4130;
    
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_Username_Param = "Username";
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_Password_Param = "Password";
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_Domain_Param = "Domain";
    
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam = "SessionToken";
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_SessionTimeout_ResultParam = "SessionTimeout";
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_UserNotInAllowedGroup_ResultParam = "UserNotInAllowedGroup";
    public static final String kCobra_SelfService_OperationType_SelfServiceLogin_AuthenticationFailed_ResultParam = "AuthenticationFailed";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_SessionToken_Param = "SessionToken";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_DeviceList_ResultParam = "DeviceList";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_SessionTokenExpired_ResultParam = "SessionTokenExpired";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_SessionTokenInvalid_ResultParam = "SessionTokenInvalid";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_SessionToken_Param = "SessionToken";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param = "DeviceTargetIdentifier";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param = "DeviceType";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_CommandID_Param = "CommandID";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param = "CommandParameters";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_CommandUUID_ResultParam = "CommandUUID";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_SessionTokenExpired_ResultParam = "SessionTokenExpired";
    public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_SessionTokenInvalid_ResultParam = "SessionTokenInvalid";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnMobileDevices_Param = "ReturnMobileDevices";
    public static final String kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnDesktopDevices_Param = "ReturnDesktopDevices";
    
    public static final int kCobra_SelfService_OperationType_SelfServiceLogin = 1;
    public static final int kCobra_SelfService_OperationType_GetDeviceListForUser = 2;
    public static final int kCobra_SelfService_OperationType_PerformDeviceCommand = 3;
    
    public static final String kCobra_AdminLogin_AdminInfo_Result = "AdminInfo";					//!< \a dict; info about admin
    public static final String kCobra_AdminLogin_RegistrationInfo_Result = "RegistationInfo";		//!< \a dict; info about registration info
    public static final String kCobra_AdminLogin_AdminIPv4_Result = "AdminIPv4";					//!< \a number; admin ip seen by the server
    public static final String kCobra_AdminLogin_ServerVersion_Result = "ServerVersion";			//!< \a string; server version
    public static final String kCobra_AdminLogin_ServerBuildNumber_Result = "ServerBuildNumber";	//!< \a number; server build number
    public static final String kCobra_AdminLogin_ServerSettings_Result = "ServerSettings";		//!< \a dict; server settings
    public static final String kCobra_AdminLogin_ServerHasMDMFleetManagementKey_Result = "ServerHasMDMFleetManagementKey"; //!< \a boolean;



    // ------------------------------------------------------------ Apple MDM Command -----------------------------------------------------------------------
    
    public static final int kCobra_Admin_PushChangesToServer_Command = 1025;
    public static final int kCobra_Admin_ModifyCustomFieldFromDevice_Command = 1032;
    public static final int kCobra_Admin_SendMDMCommand_Command = 1043;									//!< \a Send to MDM Proxy

    // these are pseudo command so we are able to have them in the admin privileges list
    public static final int kCobra_Admin_SendMDMCommandPseudo_InstallProfile = 1050;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_RemoveProfile = 1051;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_InstallProvisioningProfile = 1052;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_RemoveProvisioningProfile = 1053;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_DeviceLock = 1054;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_ClearPasscode = 1055;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_RemoteErase = 1056;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_UpdateDeviceInfo = 1057;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_SendMessageToDevice = 1058;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_InstallApplication = 1059;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_RemoveApplication = 1063;									//!< \a Pseudo command
    public static final int kCobra_Admin_SendMDMCommandPseudo_ChangeRoamingOptions = 1070;									//!< \a Pseudo command

    public static final String kCobra_Admin_SendMDMCommand_CommandID_Param	= "CommandID";						//!< \a number;
    public static final String kCobra_Admin_SendMDMCommand_RecordIDList_Param = "RecordIDList";				//!< \a array; of UInt64 agent record IDs
    public static final String kCobra_Admin_SendMDMCommand_DeviceRecordIDs_Param = "DeviceRecordIDs";				//!< \a array; of UInt64 device IDs
    public static final String kCobra_Admin_SendMDMCommand_RequestData_Param = "RequestData";					//!< \a dictionary; Data
    public static final String kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param = "MessageText";	// dlavin added. See // See \Cobra\Admin\Source\Win\CIOSSendMessageDialog.cpp for these definitions. They are not defined in CobraCommandDefs.h
    public static final String kCobra_Admin_SendMDMCommand_RequestData_Timestamp_Param = "Timestamp";	// dlavin added. See // See \Cobra\Admin\Source\Win\CIOSSendMessageDialog.cpp for these definitions. They are not defined in CobraCommandDefs.h


    public static final int kCobra_Admin_SendMDMCommand_InstallProfile = 1;
    public static final int kCobra_Admin_SendMDMCommand_RemoveProfile = 2;
    public static final int kCobra_Admin_SendMDMCommand_InstallProvisioningProfile = 3;
    public static final int kCobra_Admin_SendMDMCommand_RemoveProvisioningProfile = 4;
    public static final int kCobra_Admin_SendMDMCommand_DeviceLock = 5;
    public static final int kCobra_Admin_SendMDMCommand_ClearPasscode = 6;
    public static final int kCobra_Admin_SendMDMCommand_RemoteErase = 7;
    public static final int kCobra_Admin_SendMDMCommand_UpdateDeviceInfo = 8;
    public static final int kCobra_Admin_SendMDMCommand_InstallProfileFromRepository = 9;
    public static final int kCobra_Admin_SendMDMCommand_InstallProvisioningProfileFromRepository = 10;
    public static final int kCobra_Admin_SendMDMCommand_InstallApplication = 11;
    public static final int kCobra_Admin_SendMDMCommand_RemoveApplication = 12;
    public static final int kCobra_Admin_SendMDMCommand_InstallApplicationFromRepository = 13;
    public static final int kCobra_Admin_SendMDMCommand_SetActivationLockOptions = 18;
    public static final int kCobra_Admin_SendMDMCommand_SetOrganizationInfo = 19;

    public static final String kCobra_Admin_LockMDMCommand_RequestData_Message_Param = "Message";
    public static final String kCobra_Admin_LockMDMCommand_RequestData_PhoneNumber_Param = "PhoneNumber";
    
    public static final String kCobra_Admin_SendMDMCommand_RequestData_ChangeActivationLock_Param = "ChangeActivationLock";
    public static final String kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param	= "Password";							//!< \a string
    public static final String kCobra_Admin_SendMDMCommand_RequestData_iOSRecordIDList_Param	= "iOSRecordIDList";				//!< \a array; of UInt64 agent record IDs
    public static final String kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param	= "AndroidRecordIDList"; //!< \a array; of UInt64 agent record IDs
    public static final String kCobra_Admin_SendMDMCommand_RequestData_EraseSDCard_Param	= "EraseSDCard";
    public static final String kCobra_Admin_SendMDMCommand_RequestData_DataRoaming_Param    = "DataRoaming"; 
    public static final String kCobra_Admin_SendMDMCommand_RequestData_VoiceRoaming_Param    = "VoiceRoaming";
    
    public static final String kCobra_Admin_SendMDMCommand_DeviceOwnership_Param    = "DeviceOwnership";
    public static final String kCobra_Admin_SendMDMCommand_EnrollmentDomain_Param    = "EnrollmentDomain";
    public static final String kCobra_Admin_SendMDMCommand_EnrollmentUser_Param    = "EnrollmentUser";
    public static final String kCobra_Admin_SendMDMCommand_DeviceName_Param    = "DeviceName";
    public static final String kCobra_Admin_SendMDMCommand_OrganizationName_Param    = "OrganizationName";
    public static final String kCobra_Admin_SendMDMCommand_OrganizationPhone_Param    = "OrganizationPhone";
    public static final String kCobra_Admin_SendMDMCommand_OrganizationEmail_Param    = "OrganizationEmail";
    public static final String kCobra_Admin_SendMDMCommand_OrganizationAddress_Param    = "OrganizationAddress";
    public static final String kCobra_Admin_SendMDMCommand_OrganizationMagic_Param    = "OrganizationMagic";

    // Custom field related values
    public static final String kCobra_Admin_CustomField_AutoAssignToAllMachines = "AutoAssignToAllMachines";
    public static final String kCobra_Admin_CustomField_DataType = "DataType";
    public static final String kCobra_Admin_CustomField_Description = "Description";
    public static final String kCobra_Admin_CustomField_DeviceType = "DeviceType";
    public static final String kCobra_Admin_CustomField_DisplayType = "DisplayType";
    public static final String kCobra_Admin_CustomField_EvaluationMethod = "EvaluationMethod";
    public static final String kCobra_Admin_CustomField_FieldID = "FieldID";
    public static final String kCobra_Admin_CustomField_Name = "Name";
    public static final String kCobra_Admin_CustomField_Seed = "Seed";
    public static final String kCobra_Admin_CustomField_EnumerationList = "EnumerationList";
    public static final String kCobra_Admin_CustomField_VariableName = "VariableName";
    public static final String kCobra_Admin_CustomField_DynamicFields = "DynamicFields";
    public static final String kCobra_Admin_CustomField_RecordIDListMobileDevices = "RecordIDListMobileDevices";
    public static final String kCobra_Admin_CustomField_RemovedFields = "RemovedFields";
    public static final String kCobra_Admin_CustomField_TargetIdentifierList = "TargetIdentifierList";
    public static final String kCobra_Admin_CustomField_SharedDynamicFields = "SharedDynamicFields";
    public static final String kCobra_Admin_CustomField_DateValue = "DateValue";
    public static final String kCobra_Admin_CustomField_NumberValue = "NumberValue";
    public static final String kCobra_Admin_CustomField_Value = "Value";
    
    // our own Commands
    public static final int kCobra_Admin_SendMDMCommand_ShowMessage = 20;
    public static final int kCobra_Admin_SendMDMCommand_ChangeRoamingOptions = 21;											// this will then be translated into ApplySettings
    public static final int kCobra_Admin_SendMDMCommand_ChangeDeviceName = 22;
    public static final int kCobra_Admin_SendMDMCommand_SendReenrollmentMessage = 23;

	public static final int kCobra_RegisterSyncSvcCert_Command	= 4001;									//!< \a various iOS Database operations;
	public static String kCobra_RegisterSyncSvcCert_AdminName_Param	= "AdminName";
	public static String kCobra_RegisterSyncSvcCert_AdminPassword_Param	= "AdminPassword";
	public static String kCobra_RegisterSyncSvcCert_CertificateData_Param = "CertificateData";
	public static String kCobra_RegisterSyncSvcCert_CertificateDataFormat_Param = "CertificateDataFormat";

	// -------------------------------------------------- Delete Mobile Device Commands Command ------------------------------------------------------
	
	public static final int kCobra_DeleteCommands_Command = 4100;
	public static final int kCobra_DeleteCommands_History_OperationType = 22;
	public static final int kCobra_DeleteCommands_Queued_OperationType = 21;
	
	// ----------------------------------------------------- Computer Commands -----------------------------------------------------------------------
	// -------------------------------------------------- Commands send to Agent ---------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------
	
	public static final String kCobra_Admin_Computer_CommandID_Param				= "CommandID";				//!< \a number; command ID
	public static final String kCobra_Admin_Computer_AgentSerialList_Param			= "AgentSerialList";		//!< \a array; of UUIDS
	public static final String kCobra_Admin_Computer_CommandParameters_Param 		= "CommandParameters";
	public static final int kCobra_Admin_Queue_Command								= 1000;						//!< \a send from the admin to the server to queue commands
	
	// -------------------------------------------------- Gather Command -----------------------------------------------------------------------
	public static final int kCobra_AgentGetFullInventoryInfo_Command				= 2019;						//!< \a Get all the inventory info (software, hardware, agent setting etc)

	public static final String kCobra_GetFullInventory_ForceFullInventory_Param		= "ForceFullInventory";		//!< \a boolean; if true then a full inventory (no delta) is done
	public static final String kCobra_GetFullInventory_WithFonts_Param				= "WithFonts";				//!< \a boolean; if true also return font info as part of the inventory
	public static final String kCobra_GetFullInventory_WithPrinters_Param			= "WithPrinters";			//!< \a boolean; if true also return printers info as part of the inventory
	public static final String kCobra_GetFullInventory_WithStartupItems_Param		= "WithStartupItems";		//!< \a boolean; if true also return StartupItems info as part of the inventory
	public static final String kCobra_GetFullInventory_WithWinServices_Param		= "WithWinServices"	;		//!< \a boolean; if true also return Services info as part of the inventory
	
	// -------------------------------------------------- Miscellaneous ------------------------------------------------------------------------------
	
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param       = "AgentSerial";
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_Param       = "RequestType";
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_EraseDevice = "EraseDevice";
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_DeviceLock  = "DeviceLock";
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_PIN_Param               = "PIN";
	public static final String kCobra_SelfService_OperationType_PerformDeviceCommand_IsDeviceCommand_Param   = "IsDeviceCommand";
    public static final String kCobra_Data_Param = "Data";
    public static final String kCobra_OperationType_Param = "OperationType";
	
	// -------------------------------------------------- Send Message Command -----------------------------------------------------------------------
	
	public static final int kCobra_AgentSendMessage_Command							= 2000;						//!< \a will display a message to on the client machine
	public static final String kCobra_AgentSendMessage_Text_Param					= "MessageText";			//!< \a string; the message to display
	public static final String kCobra_AgentSendMessage_Timeout_Param				= "Timeout";				//!< \a number; if existing number of seconds after which dialog closes automatically [optional; default=unlimited]
	public static final String kCobra_AgentSendMessage_WithCancel_Param				= "WithCancel";				//!< \a boolan; add a cancel button [optional; default=false]
	public static final String kCobra_AgentSendMessage_HeaderText_Param				= "HeaderText";				//!< \a string; the header text of the message [optional;]
	public static final String kCobra_AgentSendMessage_OKButtonText_Param			= "OKButtonText";			//!< \a string; the text of OK button [optional; default="OK"]
	public static final String kCobra_AgentSendMessage_CancelButtonText_Param		= "CancelButtonText";		//!< \a string; the text of OK button [optional; default="Cancel"]
	public static final String kCobra_AgentSendMessage_TimeoutCounterText_Param		= "TimeoutCounterText";		//!< \a string; used to hold the timeout string
	public static final String kCobra_AgentSendMessage_ButtonPressed_Result			= "ButtonPressed";			//!< \a number; the button that was pressed
	
	// -------------------------------------------------- Send Message Command -----------------------------------------------------------------------
	
	public static final int kCobra_AgentLock_Command = 2500;	
	
	// -------------------------------------------------- Erase Command ------------------------------------------------------------------------------
	
	public static final int kCobra_AgentErase_Command = 2501;
	
	// -------------------------------------------------- Delete Computer Commands Command -----------------------------------------------------------
	
	public static final int kCobra_DeleteComputerCommands_Command = 1011;
	
	public static final String kCobra_DeleteComputerCommands_CommandQueueRecordIDList  = "CommandQueueRecordIDList";
	public static final String kCobra_DeleteComputerCommands_CommandRecordID           = "CommandRecordID";
	public static final String kCobra_DeleteComputerCommands_DeleteFromHistoryDB       = "DeleteFromHistoryDB";
	public static final String kCobra_DeleteComputerCommands_LogToHistory              = "LogToHistory";
}
