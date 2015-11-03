/*
 *  iOSDevicesDefines.h
 *
 *  Created by Martin Bestmann on 10.08.10.
 *  Copyright 2010 Absolute Software. All rights reserved.
 *
 */

package com.absolute.am.command;

public class iOSDevicesDefines {

	public static final int errCobra_MDMError_MissingParam = CobraProtocol.errCobra_MDMErrorBase+1;

	public static final int errCobra_MDMError_MissingIdentityCertificateTemplate						= CobraProtocol.errCobra_MDMErrorBase+2;
	public static final int errCobra_MDMError_BadIdentityCertificateTemplate								= CobraProtocol.errCobra_MDMErrorBase+3;
	public static final int errCobra_MDMError_MissingMDMProfileTemplate											= CobraProtocol.errCobra_MDMErrorBase+4;
	public static final int errCobra_MDMError_BadMDMProfileTemplate													= CobraProtocol.errCobra_MDMErrorBase+5;
	public static final int errCobra_MDMError_MDMNotSetup																		= CobraProtocol.errCobra_MDMErrorBase+6;
	public static final int errCobra_MDMError_MDMEnrollmentBootstrapTemplate								= CobraProtocol.errCobra_MDMErrorBase+7;
	public static final int errCobra_MDMError_BadIdentityMDMEnrollmentBootstrapTemplate			= CobraProtocol.errCobra_MDMErrorBase+8;
	public static final int errCobra_MDMError_UnknownMessageType														= CobraProtocol.errCobra_MDMErrorBase+9;
	public static final int errCobra_MDMError_MissingMDMDataType														= CobraProtocol.errCobra_MDMErrorBase+10;
	public static final int errCobra_MDMError_IdentityCertificateMismatch										= CobraProtocol.errCobra_MDMErrorBase+11;
	public static final int errCobra_MDMError_CommandTimeout																= CobraProtocol.errCobra_MDMErrorBase+12;
	public static final int errCobra_MDMError_CantResetPassphrase_DeviceIsTracked						= CobraProtocol.errCobra_MDMErrorBase+13;
	public static final int errCobra_MDMError_PassphraseMissmatch														= CobraProtocol.errCobra_MDMErrorBase+14;
	public static final int errCobra_MDMError_CantTrackDevice_ResetPassphraseIssued					= CobraProtocol.errCobra_MDMErrorBase+15;
	public static final int errCobra_MDMError_CantGetGeoLocation_ResetPassphraseIssued			= CobraProtocol.errCobra_MDMErrorBase+16;
	public static final int errCobra_MDMError_CantGetGeoLocation_DeviceTrackingEnabled			= CobraProtocol.errCobra_MDMErrorBase+17;
	public static final int errCobra_MDMError_CantTrackDevice_LocationServicesDisabled			= CobraProtocol.errCobra_MDMErrorBase+18;
	public static final int errCobra_MDMError_CantResetPassphrase_DeviceHasGeoTrackingToken	= CobraProtocol.errCobra_MDMErrorBase+19;
	public static final int errCobra_MDMError_CantResetPassphrase_GeoLocationDisabled				= CobraProtocol.errCobra_MDMErrorBase+20;
	public static final int errCobra_MDMError_UserCanceledSetFleetManagementToken						= CobraProtocol.errCobra_MDMErrorBase+21;
	public static final int errCobra_MDMError_MissingPrivilegesToPerformOperation						= CobraProtocol.errCobra_MDMErrorBase+22;
	public static final int errCobra_MDMError_AppAlreadyInstalledError											= CobraProtocol.errCobra_MDMErrorBase+23;
	public static final int errCobra_MDMError_NotSupportedError															= CobraProtocol.errCobra_MDMErrorBase+24;
	public static final int errCobra_MDMError_CouldNotVerifyAppIDError											= CobraProtocol.errCobra_MDMErrorBase+25;
	public static final int errCobra_MDMError_AppStoreDisabledError													= CobraProtocol.errCobra_MDMErrorBase+26;
	public static final int errCobra_MDMError_UnsupportedRequestType												= CobraProtocol.errCobra_MDMErrorBase+27;
	public static final int errCobra_MDMError_AppAlreadyQueuedError													= CobraProtocol.errCobra_MDMErrorBase+28;
	public static final int errCobra_MDMError_NotAnAppError																	= CobraProtocol.errCobra_MDMErrorBase+29;
	
	// for AppStore
	public static final int errCobra_MDMError_MissingProvisioningProfile										= CobraProtocol.errCobra_MDMErrorBase+30;
	public static final int errCobra_MDMError_MissingDeviceRecord														= CobraProtocol.errCobra_MDMErrorBase+31;
	public static final int errCobra_MDMError_BadProvisioningProfile												= CobraProtocol.errCobra_MDMErrorBase+32;
	public static final int errCobra_MDMError_InstallProvisioningProfileTimeout							= CobraProtocol.errCobra_MDMErrorBase+33;
	public static final int errCobra_MDMError_InstallConfigurationProfileTimeout						= CobraProtocol.errCobra_MDMErrorBase+34;
	public static final int errCobra_MDMError_MissingConfigurationProfile										= CobraProtocol.errCobra_MDMErrorBase+35;
	public static final int errCobra_MDMError_BadConfigurationProfile												= CobraProtocol.errCobra_MDMErrorBase+36;
	public static final int errCobra_MDMError_CantSetFleetManagementToken_GeoLocationDisabled			= CobraProtocol.errCobra_MDMErrorBase+37;
	public static final int errCobra_MDMError_WrongPlatform																	= CobraProtocol.errCobra_MDMErrorBase+38;
	public static final int errCobra_MDMError_ConfigProfileNotSupportedOnDevice							= CobraProtocol.errCobra_MDMErrorBase+39;
	;
	// for WinPhone support
	public static final int errCobra_MDMError_CanNotConnectToExchangeServer									= CobraProtocol.errCobra_MDMErrorBase+40;
	
	public static final int errCobra_MDMError_CommandFormatError														= CobraProtocol.errCobra_MDMErrorBase+50;
	public static final int errCobra_MDMError_CantTrackDevice_GeoLocationDisabled						= CobraProtocol.errCobra_MDMErrorBase+51;
	
	public static final int errCobra_ThirdPartyConfigurationFailed_NoAppForBundleID					= CobraProtocol.errCobra_MDMErrorBase+52;
	public static final int errCobra_ThirdPartyConfigurationFailed_InstallErrorFromApp			= CobraProtocol.errCobra_MDMErrorBase+53;
	public static final int errCobra_ThirdPartyConfigurationFailed_ResetErrorFromApp				= CobraProtocol.errCobra_MDMErrorBase+54;
	public static final int errCobra_ThirdPartyConfigurationFailed_PayloadNotFoundOnDevice	= CobraProtocol.errCobra_MDMErrorBase+55;
	public static final int errCobra_ThirdPartyConfigurationFailed_UserCancelled						= CobraProtocol.errCobra_MDMErrorBase+56;
	
	public static String kAppStoreAppUniqueID = "66950D07-3A81-4BE2-9FF5-4BB3D44A5F8C";
	public static String kInstallAbsoluteAppsConfigProfileIdentifier = "com.absolute.install_absoluteapps";
	public static String kConfigureAbsoluteAppsConfigProfileIdentifier	= "com.absolute.configure_absoluteapps";
	public static String kIsABTAppStoreKey = "IsABTAppStore";
	
	// managed application states (do not remove/replace existing values, since they're stored in the DB!)
	/*enum
	{
		eManagedAppStatus_Unknown=0,
		eManagedAppStatus_NeedsRedemption,							// 1
		eManagedAppStatus_Redeeming,										// 2
		eManagedAppStatus_Prompting,										// 3
		eManagedAppStatus_Installing,										// 4
		eManagedAppStatus_Managed,											// 5
		eManagedAppStatus_ManagedButUninstalled,				// 6
		eManagedAppStatus_UserInstalledApp,							// 7
		eManagedAppStatus_UserRejected,									// 8
		eManagedAppStatus_Failed,												// 9
		
		eManagedAppStatus_PromptingForLogin,						// 10
		eManagedAppStatus_PromptingForUpdate,						// 11
		eManagedAppStatus_PromptingForUpdateLogin,			// 12
		eManagedAppStatus_Queued,												// 13
		eManagedAppStatus_UpdateRejected,								// 14
		
		eManagedAppStatus_DeletePrompting,							// 15; Android only
		eManagedAppStatus_DeleteUserRejected						// 16; Android only
	};
	*/
	// MARK: === device info ===
	public static final int kCobra_MobileDeviceOwnership_Unknown	=	0;
	public static final int kCobra_MobileDeviceOwnership_Company	=	1;
	public static final int kCobra_MobileDeviceOwnership_User		=	2;
	public static final int kCobra_MobileDeviceOwnership_Guest		=	3;
	public static final int kCobra_MobileDeviceOwnership_Custom1	=	4;
	public static final int kCobra_MobileDeviceOwnership_Custom2	=	5;
	public static final int kCobra_MobileDeviceOwnership_Custom3	=	6;
	public static final int kCobra_MobileDeviceOwnership_Custom4	=	7;
	public static final int kCobra_MobileDeviceOwnership_Custom5	=	8;
	
	public static final int kCobra_MobileDeviceOwnership_Supervised	= 20;
	
	
	// MARK: === iOS Application Package ===
	
	public static String kCobra_iOS_AppID_Param													= "UniqueID";						//!< \a UUID
	public static String kCobra_iOS_AppSeed_Param												= "Seed";								//!< \a number
	public static String kCobra_iOS_AppAppSize_Param											= "AppSize";							//!< \a number
	public static String kCobra_iOS_AppName_Param												= "Name";								//!< \a string
	public static String kCobra_iOS_AppMinOSVersion_Param								= "MinOSVersion";				//!< \a number; add in 6.0
	public static String kCobra_iOS_AppSupportedDevices_Param						= "SupportedDevices";		//!< \a number; add in 6.0; same field as in AppStore Applications where it is called Platform
	public static String kCobra_iOS_AppIsUniversalApp_Param							= "IsUniversalApp";			//!< \a boolean; add in 6.0
	public static String kCobra_iOS_AppAppVersionString_Param						= "AppVersionString";		//!< \a string
	public static String kCobra_iOS_AppAppVersionNumber_Param						= "AppVersion";					//!< \a number
	public static String kCobra_iOS_AppAppBuildNumber_Param							= "AppBuildNumber";			//!< \a number
	public static String kCobra_iOS_AppBundleIdentifier_Param						= "BundleIdentifier";		//!< \a string
	public static String kCobra_iOS_AppOriginalFileName_Param						= "OriginalFileName";		//!< \a string; this is the name of the IPA file
	public static String kCobra_iOS_AppDisplayName_Param									= "DisplayName";					//!< \a string
	public static String kCobra_iOS_AppBinaryPackageMD5_Param						= "BinaryPackageMD5";		//!< \a string
	public static String kCobra_iOS_AppBinaryPackageName_Param						= "BinaryPackageName";		//!< \a string; this is the folder name that contains all the payload files, IPA and Icon
	public static String kCobra_iOS_AppShortDescription_Param						= "ShortDescription";		//!< \a string
	public static String kCobra_iOS_AppLongDescription_Param							= "LongDescription";			//!< \a string
	public static String kCobra_iOS_AppUpdateDescription_Param						= "UpdateDescription";		//!< \a string
	public static String kCobra_iOS_AppProfileUniqueID_Param							= "ProfileUniqueID";			//!< \a UUID
	public static String kCobra_iOS_AppPlatformType_Param								= "PlatformType";				//!< \a number; kAgentPlatformiOS, kAgentPlatformAndroid, kAgentPlatformWinMobile
	public static String kCobra_iOS_AppEncryptionKey_Param								= "EncryptionKey";				//!< \a String
	public static String kCobra_iOS_AppRemoveWhenMDMIsRemoved_Param			= "RemoveWhenMDMIsRemoved";//!< \a boolean
	public static String kCobra_iOS_AppPreventAppDataBackup_Param				= "PreventAppDataBackup";//!< \a boolean
	
	public static String kCobra_iOS_AppSourceFilePath_Param							= "SourceFilePath";			//!< \a string; Admin only
	public static String kCobra_iOS_AppSourceProvisioningProfilePath_Param= "SourceProvisioningProfilePath";//!< \a string; Admin only
	public static String kCobra_iOS_AppSourceIconFilePath_Param					= "SourceIconFilePath";//!< \a string; Admin only
	
	public static String kCobra_iOS_AttachedFileTypeList_Param						= "AttachedFileTypeList";//!< \a array; of numbers
	public static String kCobra_iOS_AttachedFileNameList_Param						= "AttachedFileNameList";//!< \a array; of strings
	public static String kCobra_iOS_AttachedFileBundleIdentifierList_Param= "AttachedFileBundleIdentifierList";//!< \a array; of strings
	public static String kCobra_iOS_AttachedFileSourcePathList_Param			= "AttachedFileSourcePathList";//!< \a array; of strings
	public static String kCobra_iOS_UploadProgressControllerName_Param		= "UploadProgressControllerName";//!< \a string; Admin only
	
	public static final int kCobra_iOS_AttachedFileType_Application				= 1;
	public static final int kCobra_iOS_AttachedFileType_ProvisioningProfile		= 2;
	public static final int kCobra_iOS_AttachedFileType_ConfigurationProfile	= 3;
	public static final int kCobra_iOS_AttachedFileType_ApplicationIcon			= 4;
	public static final int kCobra_iOS_AttachedFileType_MediaFile				= 5;
	
	// Binary values (bit field)
	public static final int kCobra_iOS_AppPlatform_iPhone										= 1;
	public static final int kCobra_iOS_AppPlatform_iPodTouch									= 2;
	public static final int kCobra_iOS_AppPlatform_iPad											= 4;
	
	// MARK: === iOS AppStore Application ===
	
	public static String kCobra_iOS_AppStoreAppID_Param									= "UniqueID";						//!< \a UUID
	public static String kCobra_iOS_AppStoreAppSeed_Param								= "Seed";								//!< \a number
	public static String kCobra_iOS_AppStoreAppName_Param								= "Name";								//!< \a string
	public static String kCobra_iOS_AppStoreAppCategory_Param						= "Category";						//!< \a string
	public static String kCobra_iOS_AppStoreAppMinOSVersion_Param				= "MinOSVersion";				//!< \a number
	public static String kCobra_iOS_AppStoreAppPlatform_Param						= "Platform";						//!< \a number
	public static String kCobra_iOS_AppStoreAppIsUniversalApp_Param			= "IsUniversalApp";			//!< \a boolean
	public static String kCobra_iOS_AppStoreAppVPPCodesPurchased_Param		= "VPPCodesPurchased";		//!< \a number
	public static String kCobra_iOS_AppStoreAppVPPCodesRedeemed_Param		= "VPPCodesRedeemed";		//!< \a number
	public static String kCobra_iOS_AppStoreAppVPPCodesRemaining_Param		= "VPPCodesRemaining";		//!< \a number
	public static String kCobra_iOS_AppStoreAppVPPOrdernumber_Param			= "VPPOrdernumber";			//!< \a string
	public static String kCobra_iOS_AppStoreAppVPPPurchaser_Param				= "VPPPurchaser";				//!< \a string
	public static String kCobra_iOS_AppStoreAppURL_Param									= "AppStoreURL";					//!< \a string
	public static String kCobra_iOS_AppStoreAppCountry_Param							= "AppStoreCountry";			//!< \a string
	public static String kCobra_iOS_AppStoreAppShortDescription_Param		= "ShortDescription";		//!< \a string
	public static String kCobra_iOS_AppStoreAppLongDescription_Param			= "LongDescription";			//!< \a string
	public static String kCobra_iOS_AppStoreAppIcon_Param								= "AppIcon";							//!< \a BLOB
	public static String kCobra_iOS_AppStoreVPPCodes_Param								= "VPPCodes";						//!< \a Array
	public static String kCobra_iOS_AppStorePlatformType_Param						= "PlatformType";				//!< \a number; kAgentPlatformiOS, kAgentPlatformAndroid, kAgentPlatformWinMobile
	public static String kCobra_iOS_AppStoreAppStoreID_Param							= "AppStoreID";					//!< \a string;
	public static String kCobra_iOS_AppStoreRemoveWhenMDMIsRemoved_Param	= "RemoveWhenMDMIsRemoved";//!< \a boolean
	public static String kCobra_iOS_AppStorePreventAppDataBackup_Param		= "PreventAppDataBackup";//!< \a boolean
	
	// Binary values (bit field)
	public static final int kCobra_iOS_AppStoreAppPlatform_iPhone					=	1;
	public static final int kCobra_iOS_AppStoreAppPlatform_iPodTouch				=	2;
	public static final int kCobra_iOS_AppStoreAppPlatform_iPad						=	4;
	
	// MARK: === iOS Configuration Profile ===
	
	public static String kCobra_iOS_ConfigurationProfileID_Param = "UniqueID";							//!< \a UUID
	public static String kCobra_iOS_ConfigurationProfileSeed_Param = "Seed";									//!< \a number
	public static String kCobra_iOS_ConfigurationProfilePayloadUUID_Param = "PayloadUUID";						//!< \a UUID
	public static String kCobra_iOS_ConfigurationProfilePayloadName_Param = "PayloadName";						//!< \a string
	public static String kCobra_iOS_ConfigurationProfilePayloadDescription_Param = "PayloadDescription";		//!< \a string
	public static String kCobra_iOS_ConfigurationProfilePayloadIdentifier_Param = "PayloadIdentifier";			//!< \a string
	public static String kCobra_iOS_ConfigurationProfilePayloadOrganization_Param = "PayloadOrganization";		//!< \a string
	public static String kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_Param = "PayloadRemovalOptions";	//!< \a integer
	public static String kCobra_iOS_ConfigurationProfileOriginalFileName_Param = "OriginalFileName";			//!< \a string
	public static String kCobra_iOS_ConfigurationProfileBinaryPackageMD5_Param = "BinaryPackageMD5";			//!< \a string
	public static String kCobra_iOS_ConfigurationProfileBinaryPackageName_Param = "BinaryPackageName";			//!< \a string
	public static String kCobra_iOS_ConfigurationProfileVariablesUsed_Param = "VariablesUsed";					//!< \a string; added in 5.4
	public static String kCobra_iOS_ConfigurationProfilePlatformType_Param = "PlatformType";					//!< \a integer; added in 6.0.3
	public static String kCobra_iOS_ConfigurationProfileConfigurationType_Param = "ConfigurationType";			//!< \a integer; added in 6.1
	
			
	
	
	public static final int kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_AllowNever = 1;
	public static final int kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_AllowWithAuthorization = 2;
	public static final int kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_AllowAlways = 3;
	
	public static final int kCobra_iOS_ConfigurationProfilePayloadConfigurationType_OS = 1;
	public static final int kCobra_iOS_ConfigurationProfilePayloadConfigurationType_ThirdParty = 2;
	

	// MARK: === iOS Provisioning Profile ===
	
	public static String kCobra_iOS_ProvisioningProfileID_Param	= "UniqueID";									//!< \a UUID
	public static String kCobra_iOS_ProvisioningProfileSeed_Param = "Seed";									//!< \a number
	public static String kCobra_iOS_ProvisioningProfileUUID_Param = "ProfileUUID";							//!< \a UUID
	public static String kCobra_iOS_ProvisioningProfileExpiryDate_Param	= "ProfileExpiryDate";					//!< \a date/time
	public static String kCobra_iOS_ProvisioningProfileName_Param = "ProfileName";							//!< \a string
	public static String kCobra_iOS_ProvisioningProfileOriginalFileName_Param = "OriginalFileName";			//!< \a string
	public static String kCobra_iOS_ProvisioningProfileBinaryPackageMD5_Param = "BinaryPackageMD5";			//!< \a string
	public static String kCobra_iOS_ProvisioningProfileBinaryPackageName_Param = "BinaryPackageName";			//!< \a string
	
	
	/* Just commented out until we need them.
	 * 
	 
	
	#define kCobra_iOS_ConfigurationProfileSourceFilePath_Param		"SourceFilePath"  //!< \a string; Admin only
	  
	
	// MARK: === iOS AppStore Application VPP===
	
	#define kCobra_iOS_AppStoreAppVPPVoucherRedeemed_Param					"VoucherRedeemed"				//!< \a number
	#define kCobra_iOS_AppStoreAppVPPRedemptionDate_Param						"RedemptionDate"				//!< \a date
	#define kCobra_iOS_AppStoreAppVPPRedemptionDeviceUDID_Param			"RedemptionDeviceUDID"	//!< \a string
	#define kCobra_iOS_AppStoreAppVPPVoucherCode_Param							"VoucherCode"						//!< \a string
	#define kCobra_iOS_AppStoreAppVPPRedemptionURL_Param						"RedemptionURL"					//!< \a string
	
	#define kCobra_iOS_AppStoreAppVPPVoucherRedeemed_No							0
	#define kCobra_iOS_AppStoreAppVPPVoucherRedeemed_Yes						1
	#define kCobra_iOS_AppStoreAppVPPVoucherRedeemed_Blocked				2
	*/

	// MARK: === Mobile Actions ===
	public static String kCobra_MobileActionID_Param															= "id";							//!< \a id
	public static String kCobra_MobileActionUniqueID_Param															= "UniqueID";							//!< \a UUID
	public static String kCobra_MobileActionSeed_Param																= "Seed";									//!< \a number
	public static String kCobra_MobileActionActionType_Param														= "ActionType";						//!< \a number
	public static String kCobra_MobileActionSupportedPlatforms_Param												= "SupportedPlatforms";		//!< \a number
	public static String kCobra_MobileActionDisplayName_Param														= "DisplayName";						//!< \a string
	public static String kCobra_MobileActionDescription_Param														= "Description";						//!< \a string
	public static String kCobra_MobileActionActionData_Param														= "ActionData";						//!< \a Node
	public static String kCobra_MobileActionLastModified_Param														= "last_modified";						//!< \a Node
	
	public static int kCobra_MobileActionActionType_SendMessage												= 1;
	public static int kCobra_MobileActionActionType_ChangeRoamingSettings									= 2;
	public static int kCobra_MobileActionActionType_SendEmail												= 3;
	public static int kCobra_MobileActionActionType_RemoveMDMProfile										= 4;
	public static int kCobra_MobileActionActionType_RemoveConfigurationProfile								= 5;
	public static int kCobra_MobileActionActionType_SendSMS													= 6;
	public static int kCobra_MobileActionActionType_FreezeDevice											= 7;
	public static int kCobra_MobileActionActionType_UpdateDeviceInformation									= 8;
	
	// this is a bit mask
	public static int kCobra_MobileActionActionType_SupportedPlatforms_iOS						= 1;
	public static int kCobra_MobileActionActionType_SupportedPlatforms_Android					= 2;
	public static int kCobra_MobileActionActionType_SupportedPlatforms_WindowsPhone				= 4;
	
	
	// MARK: === Mobile Media ===
	
	public static String kCobra_MobileMediaID_Param																		= "id";						//!< \a number
	public static String kCobra_MobileMediaUniqueID_Param																		= "UniqueID";						//!< \a UUID
	public static String kCobra_MobileMediaSeed_Param																	= "Seed";								//!< \a number
	public static String kCobra_MobileMediaDisplayName_Param														= "DisplayName";					//!< \a string
	public static String kCobra_MobileMediaCategory_Param															= "Category";						//!< \a string
	public static String kCobra_MobileMediaFilename_Param															= "Filename";					 	//!< \a string
	public static String kCobra_MobileMediaFileModDate_Param														= "FileModDate";					//!< \a date/time
	public static String kCobra_MobileMediaFileSize_Param															= "FileSize";					 	//!< \a number
	public static String kCobra_MobileMediaFileType_Param															= "FileType";					 	//!< \a string
	public static String kCobra_MobileMediaFileMD5_Param																= "FileMD5";					 		//!< \a string
	public static String kCobra_MobileMediaDescription_Param														= "Description";					//!< \a string
	public static String kCobra_MobileMediaCanLeaveApp_Param														= "CanLeaveApp";					//!< \a boolean
	public static String kCobra_MobileMediaCanEmail_Param															= "CanEmail";						//!< \a boolean
	public static String kCobra_MobileMediaCanPrint_Param															= "CanPrint";						//!< \a boolean
	public static String kCobra_MobileMediaIcon_Param																	= "Icon";								//!< \a BLOB
	public static String kCobra_MobileMediaEncryptionKey_Param													= "EncryptionKey";				//!< \a String
	public static String kCobra_MobileMediaPassphraseHash_Param												= "PassphraseHash";			//!< \a String
	public static String kCobra_MobileMediaTransferOnWifiOnly_Param										= "TransferOnWifiOnly";	//!< \a boolean; added in 6.1
	
	public static String kCobra_MobileMediaAvailabilityStartTime_Param									= "AvailabilityStartTime";//!< \a number
	public static String kCobra_MobileMediaAvailabilityEndTime_Param										= "AvailabilityEndTime";	//!< \a number
	public static String kCobra_MobileMediaAvailabilitySelector_Param									= "AvailabilitySelector";//!< \a number
	
	public static String kCobra_MobileMediaAutoDownload_Param													= "AutoDownload";				//!< \a boolean
	public static String kCobra_MobileMediaAutoDelete_Param														= "AutoDelete";					//!< \a boolean
	
	public static String kCobra_MobileMediaSourceFilePath_Param												= "SourceFilePath";			//!< \a string; Admin only
	public static String kCobra_MobileMediaIsBatchUpload_Param													= "IsBatchUpload";			//!< \a boolean; Admin only
	
	// MARK: === iOS Policy ===
	
	public static String kCobra_iOS_PolicyID_Param									= "UniqueID";						//!< \a UUID
	public static String kCobra_iOS_PolicySeed_Param								= "Seed";								//!< \a number
	public static String kCobra_iOS_PolicyName_Param								= "Name";								//!< \a string
	public static String kCobra_iOS_PolicyFilterCriteria_Param			= "FilterCriteria";			//!< \a dictionary
	//public static String kCobra_iOS_PolicyFilterQuery_Param				= "FilterQuery";					//!< \a string
	public static String kCobra_iOS_PolicyFilterSchemaVersion_Param= "SchemaVersion";				//!< \a number
	//public static String kCobra_iOS_PolicyFilterTables_Param				= "FilterTables";				//!< \a string
	public static String kCobra_iOS_PolicyFilterType_Param					= "FilterType";					//!< \a number
	
	public final static int kCobra_iOS_PolicyFilterType_None							=	0;				// pseudo filter type, not used in real smart policies
	public final static int kCobra_iOS_PolicyFilterType_SmartPolicy						=	1;
	public final static int kCobra_iOS_PolicyFilterType_SmartPolicyByInstalledApps		=	2;
	public final static int kCobra_iOS_PolicyFilterType_SmartPolicyByInstalledProfiles	= 3;
	
	public final static int kCobra_iOS_Policy_AppState_Any						=	9667;	// not a real state - used to display all assigned applications
	public final static int kCobra_iOS_Policy_AppState_Forbidden				= 0;
	public final static int kCobra_iOS_Policy_AppState_Required					= 1;
	public final static int kCobra_iOS_Policy_AppState_OnDemand					= 2;
	public final static int kCobra_iOS_Policy_AppState_PolicyLocked			= 3;
	public final static int kCobra_iOS_Policy_AppState_PolicyOptional		= 4;
	
	public final static int kCobra_iOS_Policy_ConfigProfile_Any								= 9667;	// not a real state - used to display all assigned profiles
	public final static int kCobra_iOS_Policy_ConfigProfile_Forbidden					= 0;
	public final static int kCobra_iOS_Policy_ConfigProfile_Required					= 1;
	public final static int kCobra_iOS_Policy_ConfigProfile_OnDemand					= 2;
	public final static int kCobra_iOS_Policy_ConfigProfile_PolicyLocked			= 3;
	public final static int kCobra_iOS_Policy_ConfigProfile_PolicyOptional		= 4;
	// old constants:
	//public final static int kCobra_iOS_Policy_ConfigProfile_Exclusive					kCobra_iOS_Policy_ConfigProfile_PolicyLocked
	
	public final static int kCobra_iOS_Policy_AvailabilitySelector_Always						=	0;
	public final static int kCobra_iOS_Policy_AvailabilitySelector_DailyInterval			= 1;
	public final static int kCobra_iOS_Policy_AvailabilitySelector_FixedInterval			= 2;
	
	public final static int kCobra_iOS_Policy_MediaFile_AnyState				=	0;		// not a real state - used to display all assigned media files
	public final static int kCobra_iOS_Policy_MediaFile_PolicyOptional		= 1;
	public final static int kCobra_iOS_Policy_MediaFile_PolicyLocked		= 2;
	public final static int kCobra_iOS_Policy_MediaFile_Required			= 3;
	public final static int kCobra_iOS_Policy_MediaFile_OnDemand			= 4;
	
	//TODO: dlavin added these, why are they not here already? See iOSDevicesInterface.cpp where these values are hard coded in multiple places.
	public static String kCobra_iOS_Policy_AssignmentType_Param					= "AssignmentType";	// a number, one of the above *Policy_MediaFile* or *Policy_ConfigProfile*
	public static String kCobra_iOS_Policy_MediaList_Param						= "MediaList"; // an array of UUIDs
	public static String kCobra_iOS_Policy_ConfigProfileList_Param				= "ProfileList"; // an array of UUIDs
	public static String kCobra_iOS_Policy_ApplicationList_Param				= "ApplicationList"; // an array of UUIDs
	public static String kCobra_iOS_Policy_PolicyID_Param						= "PolicyID"; // the UUID of the policy
	public static String kCobra_iOS_Policy_PolicyIDs_Param						= "PolicyIDs"; // an array of UUIDs
	public static String kCobra_iOS_Policy_AvailabilitySelector_Param			= "AvailabilitySelector"; // a number, one of the *AvailabilitySelector_* values above.
	public static String kCobra_iOS_Policy_AvailabilityStartTime_Param			= "StartTime";	// HH:MM or Date, depending on AvailabilitySelector
	public static String kCobra_iOS_Policy_AvailabilityEndTime_Param			= "EndTime";	// HH:MM or Date, depending on AvailabilitySelector
	public static String kCobra_iOS_Policy_RemovePolicyLockedProfiles_Param		= "RemovePolicyLockedProfiles"; // boolean
	
	// MARK: === Device Tracking ===
	
	/*#define kCobra_DeviceTracking_GeolocationTime_Param										"GeolocationTime"							//!< \a date/time
	#define kCobra_DeviceTracking_Latitude_Param													"Latitude"										//!< \a float
	#define kCobra_DeviceTracking_Longitude_Param													"Longitude"										//!< \a float
	#define kCobra_DeviceTracking_Quality_Param														"Quality"											//!< \a integer
	*/
	// MARK: ===Database Operation Command ===
	
	public static final int kCobra_iOS_DatabaseOperation_Command								= 4100;									//!< \a various iOS Database operations;
	
	public static String kCobra_iOS_DatabaseOperation_OperationType_Param								= "OperationType"; 			//!< \a number
	public static String kCobra_iOS_DatabaseOperation_OldData_Param											= "OldData"; 						//!< \a dictionary
	public static String kCobra_iOS_DatabaseOperation_NewData_Param											= "NewData"; 						//!< \a dictionary
	public static String kCobra_iOS_DatabaseOperation_Data_Param													= "Data"; 							//!< \a node
	
	public static String kCobra_iOS_DatabaseOperation_ConflictList_Result								= "ConflictList"; 			//!< \a array
	public static String kCobra_iOS_DatabaseOperation_DBChangeInfo_Result								= "DBChangeInfo"; 			//!< \a dictionary
	public static String kCobra_iOS_DatabaseOperation_DBChangeInfo_DeletedRecords_Param	= "DeletedRecords"; 		//!< \a array
	public static String kCobra_iOS_DatabaseOperation_DBChangeInfo_NewRecords_Param			= "NewRecords"; 				//!< \a array
	public static String kCobra_iOS_DatabaseOperation_DBChangeInfo_AdditionalInfo_Param	= "AdditionalInfo"; 		//!< \a node
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddAppPackage												=1;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddConfigProfile											=2;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveAppPackage											=4;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveConfigProfile									=5;
	//public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveProvProfile										=6
	
	//public static final int kCobra_iOS_DatabaseOperation_OperationType_AddGroupPackage											=7
	//public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveGroupPackage										=8
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddPolicy														=9;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemovePolicy													=10;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddDevicesToPolicy										=11;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveDevicesFromPolicy							=12;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddAllowedAppsToPolicy								=13;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveAllowedAppsFromPolicy					=14;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddForbiddenAppsToPolicy							=15;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveForbiddenAppsFromPolicy				=16;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddConfigProfilesToPolicy						=17;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveConfigProfilesFromPolicy				=18;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveQueuedCommands									=21;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveHistoryCommands								=22;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_ResetFailedProfiles									=23;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddAppstoreAppPackage														=24;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveAppstoreAppPackage													=25;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddRecommendedAppstoreAppsToPolicy								=26;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveRecommendedAppstoreAppsFromPolicy					=27;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveLocationTrackingRecords				=30;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_ResetFleetManagementTokenState				=31;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddMedia															=32;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveMedia													=33;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddMediaToPolicy											=34;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveMediaFromPolicy								=35;

	public static final int kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForMediaInPolicy	=38;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForConfProfilesInPolicy	=39;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_SendMDMPushNotification							=40;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RenameAppstoreAppPackage							=41;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RenameEnterpriseAppPackage						=42;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddAction														=43;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveAction													=44;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddActionToPolicy										=45;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveActionFromPolicy								=46;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_RemoveActionHistoryRecords						=47;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_ResetActions													=48;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_SetDeviceOwnership										=49;
	public static final int kCobra_iOS_DatabaseOperation_OperationType_SetEnrollmentUser										=50;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_ResetFailedInhouseApplications					=52;
	
	public static final int kCobra_iOS_DatabaseOperation_OperationType_PseudoOperation_RemoveMixedAppPackages		=9667 ;	//!< only used in admin as a pseudo operation, which is then dispatched to real operations
	public static final int kCobra_iOS_DatabaseOperation_OperationType_AddMediaPreflight												=9668 ;	//!< used internally by server when AddMedia operation is called to split up in preflight + db commit
	
	
	public static String kCobra_iOS_ProvisioningProfileData_Param												=	"ProvisioningProfileData";	//!< \a dict;
	
	public static String kCobra_iOS_PolicyDeviceList_Param														=			"DeviceList";	//!< \a dict;
	
	// Following parameter definitions are not existing in the original definition file 'iOSDenices.h'
	// TODO: we might need to move it to somewhere else in future; however, we keep them here now.
	public static String kCobra_iOS_ActionList_Param = "ActionList";
	public static String kCobra_iOS_InitialDelay_Param = "InitialDelay";
	public static String kCobra_iOS_RepeatInterval_Param = "RepeatInterval";
	public static String kCobra_iOS_RepeatCount_Param = "RepeatCount";
	public static String kCobra_iOS_ActionUniqueID_Param = "ActionUniqueID";
	public static String kCobra_iOS_iPhoneInfoRecordId_Param = "iphone_info_record_id";
	public static String kCobra_iOS_ActionToReset_Param = "ActionsToReset";
	public static String kCobra_iOS_SendMDMPush_Param = "SendMDMPush";
	public static String kCobra_iOS_ActionID_Param = "ActionID";
	public static String kCobra_iOS_FieldActions_Param = "FieldActions";
	public static String kCobra_iOS_ExecutableOptions_Param = "ExecutableOptions";
	public static String kCobra_iOS_ExecutablePartialPath_Param = "ExecutablePartialPath";
	public static String kCobra_iOS_ExecutableTypeSelector_Param = "ExecutableTypeSelector";
	public static String kCobra_iOS_ExecuteOnlyWithFullInventory_Param = "ExecuteOnlyWithFullInventory";
	public static String kCobra_iOS_CustomFieldName_Param = "Name";
	public static String kCobra_iOS_PlistDomain_Param = "PListDomain";
	public static String kCobra_iOS_PlistKey_Param = "PListKey";
	public static String kCobra_iOS_PlistLocationSelector_Param = "PListLocationSelector";
	public static String kCobra_iOS_CustomeFieldPlatForm_Param = "Platform";
	public static String kCobra_iOS_RegistryPath_Param = "RegistryPath";	
	public static String kCobra_iOS_ReplaceLineFeeds_Param = "ReplaceLineFeeds";
	public static String kCobra_iOS_RequiresAdminPrivileges_Param = "RequiresAdminPrivileges";
	public static String kCobra_iOS_ReturnExecutionErrors_Param = "ReturnExecutionErrors";
	public static String kCobra_iOS_ScriptText_Param = "ScriptText";
	public static String kCobra_iOS_CustomFieldSeed_Param = "Seed";
	public static String kCobra_iOS_SourceFile_Param = "SourceFile";
	public static String kCobra_iOS_SourceFileChecksum_Param = "SourceFileChecksum";
	public static String kCobra_iOS_SourceTypeSelector_Param = "SourceTypeSelector";
	public static String kCobra_iOS_TransferExecutableFolder_Param = "TransferExecutableFolder";
	public static String kCobra_iOS_UserContextr_Param = "UserContext";
	public static String kCobra_iOS_UserContextPassword_Param = "UserContextPassword";
	public static String kCobra_iOS_UserContextSelector_Param = "UserContextSelector";
}
