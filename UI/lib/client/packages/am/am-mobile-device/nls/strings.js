define({
  "root": {
    "amMobileDevice": {
        "shared": {
            "passcodeErrorMessage": "Passcode must be between 4 and 16 characters.",
            "passcodesDontMatchErrorMessage": "Passcodes do not match",
            "devicesSelected": "{{deviceCountDetails}} devices selected."
        },
        "topNavSpec": {
            "mobileDevicesTitle": "Mobile Devices",
            "allMobileDevicesTitle": "All Mobile Devices"
        },
        "commandsLabel": "Commands",
        "mobileDevicesListPage": {
            "mobileDevicesTitle": "Mobile Devices",
            "totalSummary": "Result: {{total}} mobile devices",
            "navTitle": "Mobile Devices",
            "commands": {
                "lockDevice": "Lock Device",
                "setActivationLock": "Set Activation Lock Options",
                "clearPasscode": "Clear Passcode",
                "clearPasscodeMultiple": "Clear and Set Passcode",
                "remoteErase": "Erase Device",
                "sendMessage": "Send Message",
                "updateDeviceInfo": "Update Device Info",
                "setRoamingOptions": "Set Roaming Options",
                "installApplication": "Install Application",
                "installConfigProfile": "Install Configuration Profile",
                "installProvisioningProfile": "Install Provisioning Profile",
                "setDeviceName": "Set Device Name",
                "setDeviceOrganizationInfo": "Set Organization Info",
                "setDeviceOwnership": "Set Device Ownership",
                "setDeviceEnrollmentUser": "Set Device Enrollment User",
                "retryAllCommand": "Retry All ..."
            }
        },
        "devicePage": {
            "title": "Mobile Device Details",
            "unmanagedDeviceMessage": "This device is not managed. Commands are not enabled.",
            "deviceDescription": "Model: {{modelUI}} | Ownership: {{ownershipUI}} | ID: {{udidUI}} | Serial Number: {{serialNumberUI}}",
            "commands": {
                "deviceLockTitle": "Device Lock",
                "deviceLockDescription": "Issue a device lock for {{deviceName}}",
                "setPasscodeTitle": "Clear and Set Passcode",
                "clearPasscodeTitle": "Clear Passcode",
                "setPasscodeDescription": "Reset the passcode for {{deviceName}}",
                "remoteDataDeleteTitle": "Erase Device",
                "remoteDataDeleteDescription": "Erase all data from {{deviceName}}",
                "sendMessageTitle": "Send Message",
                "sendMessageDescription": "Send a message to {{deviceName}}",
                "updateDeviceInfoTitle": "Update Device Info",
                "updateDeviceInfoDescription": "Request that {{deviceName}} re-send its device information",
                "setRoamingOptionsTitle": "Set Roaming Options",
                "setRoamingOptionsDescription": "Set data and voice roaming options for {{deviceName}}",
                "installApplicationTitle": "Install Application",
                "installApplicationDescription": "Install in-house or third-party application on {{deviceName}}",
                "installConfigurationProfileTitle": "Install Configuration Profile",
                "installConfigurationProfileDescription": "Install configuration profile on {{deviceName}}",
                "installProvisioningProfileTitle": "Install Provisioning Profile",
                "installProvisioningProfileDescription": "Install provisioning profile on {{deviceName}}",
                "setDeviceOwnershipTitle": "Set Device Ownership",
                "setDeviceOwnershipDescription": "Set device ownership on {{deviceName}}",
                "setDeviceEnrollmentUserTitle": "Set Device Enrollment User",
                "setDeviceEnrollmentUserDescription": "Set device enrollment user on {{deviceName}}",
                "setDeviceNameTitle": "Set Device Name",
                "setDeviceNameDescription": "Set device name on {{deviceName}}",
                "setDeviceOrganizationInfoTitle": "Set Organization Info",
                "setDeviceOrganizationInfoDescription": "Set device organization info on {{deviceName}}",
                "retryAllCommandTitle": "Retry All ...",
                "retryAllCommandDescription": "Retry all commands on {{deviceName}}",
                "setActivationLockTitle": "Set Activation Lock Options",
                "setActivationLockDescription": "Set activation lock options on {{deviceName}}",
                "isAndroidAndSupportsNoCommandsMessage": "Commands not enabled for this device. To enable commands for Android devices, ensure they have Absolute Mobile Device Management configured and AbsoluteApps installed.",
                "isIOSAndSupportsNoCommandsMessage": "Commands not enabled for this device. To enable commands for iOS devices, ensure they have Absolute Mobile Device Management configured.",
                "isWinPhoneAndSupportsNoCommandsMessage": "Commands not enabled for this device. To enable commands for Windows Phone devices, ensure they have Absolute Mobile Device Management configured.",
                "userHasNoCommandPermissionsMessage": "You are currently not authorized to issue any commands for this device."
            },
            "tabLabels": {
                "aboutDevice": "About Device",
                "mobilePolicies": "Mobile Policies",
                "applications": "Applications",
                "certificates": "Certificates",
                "configurationProfiles": "Configuration Profiles",
                "provisioningProfiles": "Provisioning Profiles",
                "assignedItems": "Assigned Items",
                "assignedItemsThirdPartyApps": "Third-Party Apps",
                "assignedItemsInHouseApps": "In-House Apps",
                "assignedItemsContent": "Content",
                "assignedItemsConfigurationProfiles": "Configuration Profiles",
                "administrators": "Administrators",
                "user": "User",
                "actions": "Performed Actions",
                "customFields": "Custom Fields"
            },
            "aboutDeviceTab": {
                "title": "Device Details",
                "phoneNumber": "Phone Number: {{phoneNumber}}",
                "osVersion": "OS Version",
                "lastContact": {
                    "value": "Last Contact",
                    "comment": "JA - last accessed date and time"
                },
                "passcodePresent": "Passcode Present",
                "batteryLevel": "Battery Level",
                "modelNumber": "Model Number",
                "serialNumber": "Serial Number",
                "identifierUDID": "Identifier (UDID)",
                "deviceGUID": "Device GUID",
                "identity": "Identity",
                "osBuildNumber": "OS Build Number",
                "osLanguage": "OS Language",
                "ownership": "Ownership",
                "isManaged": "Managed",
                "jailBrokenLabelRooted": "Rooted",
                "jailBrokenLabelJailBroken": "Jail Broken",
                "recordCreated": "Record Creation Date",
                "absoluteAppsVersion": "AbsoluteApps Version",
                "absoluteAppsBuildNo": "AbsoluteApps Build Number",
                "hasPersistence": "Supports Persistence",
                "isMdmProfileUpToDate": "MDM Profile Up-to-date",
                "productionDate": "Production Date",
                "age": "Age",
                "warrantyInfo": "Warranty Info",
                "warrantyEndDate": "Warranty End",
                "isPasscodeCompliant": "Passcode Compliant",
                "isPasscodeCompliantWithProfiles": "Passcode Compliant with Profiles",
                "storageCapacity": "Storage Capacity",
                "deviceCapacity": "Device Capacity",
                "usedCapacity": "Used Capacity",
                "availableCapacity": "Available Capacity",
                "storage": "Storage",
                "storageType": "Type",
                "storageTotalSpace": "Total Space",
                "storageAvailableSpace": "Available Space",
                "internalStorage": "Internal Storage",
                "sDCard1NonRemovable": "SD Card 1 (non-removable)",
                "sDCard2Removable": "SD Card 2 (removable)",
                "networking": "Networking",
                "isGpsCapable": "GPS Capable",
                "wifiNetwork": "Wi-Fi Network",
                "homeNetwork": "Home Network",
                "publicIpAddress": "Public IP Address",
                "cellIpAddress": "Cell IP Address",
                "wifiIpAddress": "Wi-Fi IP Address",
                "wifiMacAddress": "Wi-Fi MAC Address",
                "bluetoothMacAddress": "Bluetooth MAC Address",
                "hardware": "Hardware",
                "tablet": "Tablet",
                "manufacturer": "Manufacturer",
                "cpuName": "CPU Name",
                "cpuSpeed": "CPU Speed",
                "displayResolution": "Display Resolution",
                "board": "Board",
                "kernelVersion": "Kernel Version",
                "deviceInfo": "Device Info",
                "hardwareEncryption": "Hardware Encryption",
                "systemMemory": "System Memory",
                "memoryTotal": "RAM Total",
                "memoryAvailable": "RAM Available",
                "cacheTotal": "Cache Total",
                "cacheAvailable": "Cache Available",
                "cellularInformation": "Cellular Information",
                "currentCarrierNetwork": "Current Carrier Network",
                "cellularTechnology": "Cellular Technology",
                "isRoaming": "Roaming",
                "cellularNetworkType": "Cellular Data Network Type",
                "imei": "IMEI/MEID",
                "simIccId": "SIM ICC Identifier",
                "currentMcc": "Current Mobile Country Code",
                "currentMnc": "Current Mobile Network Code",
                "homeMcc": "Home Mobile Country Code",
                "homeMnc": "Home Mobile Network Code",
                "imeiSv": "Mobile Device IMEISV",
                "dataRoamingEnabled": "Data Roaming Enabled",
                "voiceRoamingEnabled": "Voice Roaming Enabled",
                "carrierSettingsVersion": "Carrier Settings Version",
                "modemFirmwareVersion": "Modem Firmware Version",
                "enableOutboundSMS": "Enable Outbound SMS",
                "remoteWipe": "Remote Wipe",
                "remoteWipeSupported": "Wipe Supported",
                "remoteWipeStatus": "Wipe Status",
                "remoteWipeStatusNote": "Wipe Status Note",
                "wipeRequestTime": "Wipe Request Time",
                "wipeSentTime": "Wipe Sent Time",
                "wipeAckTime": "Wipe Acknowledge Time",
                "lastWipeRequestor": "Last Wipe Requester",
                "exchangeServer": "Exchange Server",
                "accessState": "Access State",
                "accessStateReason": "Access State Reason",
                "numberOfFoldersSynched": "Number of Folders Synchronized",
                "organizationalInfo": "Organization Info",
                "organizationName": "Name",
                "organizationPhone": "Phone",
                "organizationEMail": "Email",
                "organizationAddress": "Address",
                "organizationCustom": "Custom",
                "lastChangedItems": "Last Changed Items",
                "lastInfoUpdate": "Device Information",
                "lastInstalledSwUpdate": "Installed Software",
                "lastConfigProfileUpdate": "Installed Configuration Profiles",
                "lastCertificateUpdate": "Installed Certificates",
                "lastProvisioningProfileUpdate": "Installed Provisioning Profiles",
                "lastPolicyUpdate": "Policy Update"
            },
            "mobilePoliciesTab": {
                "title": "Mobile Policies Related to this Mobile Device",
                "buttons": {
                    "addMobileDevice": "Add Mobile Device to Policies",
                    "removeMobileDevice": "Remove Mobile Device from Policies"
                }
            },
            "applicationsTab": {
                "title": "Applications Installed on this Mobile Device",
                "buttons": {
                    "installApplication": "Install Application",
                    "uninstallApplication": "Uninstall Applications"
                }
            },
            "certificatesTab": {
                "title": "Certificates Associated with this Mobile Device"
            },
            "configurationProfilesTab": {
                "title": "Configuration Profiles Installed on this Mobile Device",
                "buttons": {
                    "installConfigurationProfile": "Install Configuration Profile",
                    "uninstallConfigurationProfile": "Uninstall from Device"
                }
            },
            "provisioningProfilesTab": {
                "title": "Provisioning Profiles Installed on this Mobile Device",
                "buttons": {
                    "installProvisioningProfile": "Install Provisioning Profile",
                    "uninstallProvisioningProfile": "Uninstall Provisioning Profile"
                }
            },
            "assignedThirdPartyApplicationsTab": {
                "title": "Assigned Third-Party Applications for this Mobile Device"
            },
            "assignedInHouseApplicationsTab": {
                "title": "Assigned In-House Applications for this Mobile Device"
            },
            "assignedContentTab": {
                "title": "Assigned Content for this Mobile Device"
            },
            "assignedConfigurationProfilesTab": {
                "title": "Assigned Configuration Profiles for this Mobile Device"
            },
            "customFieldsTab": {
                "title": "Custom Field Data for this Mobile Device",
                "commands": {
                    "deleteCustomFieldData": "Remove Custom Field Data",
                    "editCustomFieldData": "Edit Custom Field Value"
                }
            },
            "administratorsTab": {
                "title": "Administrators of this Mobile Device"
            },
            "userTab": {
                "title": "Active/Open Directory User Details",
                "displayName": "Display Name",
                "firstName": "First Name",
                "lastName": "Last Name",
                "logonName": "Logon Name",
                "email": "Email",
                "phoneNumber": "Phone Number",
                "organizationalUnit": "Organizational Unit",
                "organizationalUnitPath": "Organizational Unit Path",
                "memberOf": "Member Of",
                "company": "Company",
                "department": "Department",
                "office": "Office",
                "street": "Street",
                "city": "City",
                "state": "State/Province",
                "zipCode": "ZIP/Postal Code",
                "country": "Country",
                "mDmServerEnrollment": "MDM Server Enrollment",
                "enrollmentUserName": "Username",
                "enrollmentDomain": "Domain",
                "noDataToDisplay": "There is no user data to display."
            },
            "performedActionsTab": {
                "title": "Performed Actions on this Mobile Device",
                "commands": {
                    "removeActionCommand": "Remove from List",
                    "reapplyActionCommand": "Re-execute on Device"
                }
            }
        },
        "modals": {
            "installApplication": {
                "headingOneDevice": "Install Applications on <strong>{{deviceName}}</strong>",
                "headingManyDevices": "Install Applications on the Selected Devices",
                "inHouseAppCaption": "In-House Applications:",
                "thirdPartyAppCaption": "Third-Party Applications:",
                "inProgressMsg": "Installing applications ...",
                "successMsg": "The Application Install command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error installing applications.",
                "errorMsgMultiplePlatforms": "Applications cannot be installed on multiple devices running different operating systems",
                "errorMsgMultiplePlatformsDetails": "Ensure that all selected devices run the same operating system.",
                "buttons": {
                    "actionButtonLabel": "Install"
                }
            },
            "deviceLock": {
                "headingOneDevice": "Lock Device",
                "headingManyDevices": "Lock Devices",
                "enterNewPasscode": "<label>Enter a new passcode</label><div class=\"text-italic\">(optional)</div>",
                "typePasscodeAgain": "Type the passcode again",
                "lockingOneDeviceProgressMsg": "Locking {{deviceCount}} device ...",
                "lockingManyDevicesProgressMsg": "Locking {{deviceCount}} devices ...",
                "successMsg": "The Device Lock command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error locking devices.",
                "actionMsg1": "The selected devices will be locked using their existing passcodes, if present.",
                "actionMsg1Singular": "This device will be locked using its existing passcode, if present.",
                "actionMsg2": "The selected devices do not have passcodes. You can choose to lock the devices with a passcode.",
                "actionMsg2Singular": "This device does not have a passcode. You can choose to lock this device with a passcode.",
                "actionMsg3": "Some of selected devices do not have a passcode. You can choose to lock the devices with a passcode. All other devices will be locked using their existing passcodes.",
                "actionMsg4": "<p>Some of the selected Android devices do not have a passcode. You can choose to lock these devices with a passcode. All other Android devices will be locked using their existing passcodes.</p> <p>iOS devices will be locked using their existing passcodes, if present.</p>",
                "actionMsg5": "<p>The selected Android devices do not have passcodes. You can choose to lock these devices with a passcode.</p><p>iOS devices will be locked using their existing passcodes, if present.</p>",
                "actionAllOtherMsg": "Lock the selected devices.",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Lock Device request.",
                "buttons": {
                    "actionButtonLabel": "Lock"
                }
            },
            "clearAndSetPasscode": {
                "heading": "Clear and Set Passcode",
                "inProgressMsg": "Clearing and Setting passcode ...",
                "successMsg": "The Clear and Set Passcode command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error Clearing and Setting Passcode.",
                "actionButtonLabel": "Clear Passcode",
                "actionButtonLabelSetNewPasscodeEnabled": "Clear and Set Passcode",
                "setNewPasscodeCheckBoxTitleMixed": "Set new passcode (Android devices only)",
                "setNewPasscodeCheckBoxTitleAndroid": "Set new passcode",
                "enterNewPasscode": "New passcode (4-16 characters)",
                "typeThePasscodeAgain": "Confirm new passcode"
            },
            "clearPasscode": {
                "heading": "Clear Passcode",
                "inProgressMsg": "Clearing passcode ...",
                "successMsg": "The Clear Passcode command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error Clearing Device Passcodes.",
                "actionButtonLabel": "Clear Passcode",
                "actionWarningiOsDevice": "Are you sure you want to clear the passcode on the selected devices? You cannot reverse this action.",
                "actionWarningiOsDeviceSingular": "Are you sure you want to clear the passcode on this device? You cannot reverse this action.",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this request."
            },
            "remoteDataDelete": {
                "deleteInternalStorageOnly": "Erase internal storage only",
                "deleteInternalStorageSDCard": "Erase internal storage and SD card",
                "heading": "Erase Device",
                "actionWarning": "WARNING: You cannot recover erased data.",
                "inProgressMsg": "Erasing data ...",
                "successMsg": "Erase Device command was successfully queued and will run the next time this device is online.",
                "errorMsg": "Error erasing data.",
                "errorDetailsMsg": "An error occurred while erasing the data: ",
                "confirmPromptOneDevice": "Erase data from the selected {{deviceCountDetails}} device.",
                "confirmPromptManyDevices": "Erase data from the selected {{deviceCountDetails}} devices.",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Erase Device request.",
                "buttons": {
                    "actionButtonLabel": "Erase Device"
                }
            },
            "sendMessage": {
                "headingOneDevice": "Send Message to <strong>{{deviceName}}</strong>",
                "headingManyDevices": "Send Message to the Selected Devices",
                "enterMessage": "Enter message",
                "inProgressMsg": "Sending message ...",
                "successMsg": "The message was successfully queued for sending to the devices.",
                "errorMsg": "Error sending message.",
                "errorDetailsMsg": "An error occurred while sending the message: ",
                "messageSizeNote": "Note: You have exceeded the maximum of {{maxMessageSize}} characters. Your message will be truncated",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Send Message request.",
                "buttons": {
                    "actionButtonLabel": "Send Message"
                }
            },
            "updateDeviceInfo": {
                "heading": "Update Device Information for the Selected Devices",
                "inProgressMsg": "Updating device information ...",
                "successMsg": "The Update Device Info command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error updating device information.",
                "errorDetailsMsg": "An error occurred while updating the device information: ",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Update Device Info request.",
                "buttons": {
                    "actionButtonLabel": "Update Info"
                }
            },
            "installConfigurationProfile": {
                "headingOneDevice": "Install Configuration Profiles on <strong>{{deviceName}}</strong>",
                "headingManyDevices": "Install Configuration Profiles on the Selected Devices",
                "inProgressMsg": "Installing configuration profiles ...",
                "successMsg": "The Install Configuration Profile command was queued successfully and will run the next time this device is online.",
                "errorDetailsMsg": "An error occurred while installing the configuration profile: ",
                "errorMsg": "Error installing configuration profiles.",
                "errorMsgMultiplePlatforms": "Configuration profiles cannot be installed on multiple devices running different operating systems",
                "errorMsgMultiplePlatformsDetails": "Ensure that all selected devices run the same operating system.",
                "buttons": {
                    "actionButtonLabel": "Install"
                }
            },
            "setDeviceOwnership": {
                "isUndefined": "Undefined",
                "isCompany": "The company",
                "isUser": "The user (personal device)",
                "isGuest": "A guest",
                "heading": "Set Device Ownership",
                "confirmPrompt": "Set the ownership type for this device.",
                "inProgressMsg": "Setting device ownership ...",
                "successMsg": "Ownership successfully updated.",
                "errorMsg": "Error setting ownership.",
                "errorDetailsMsg": "An error occurred while setting the device ownership: ",
                "buttons": {
                    "actionButtonLabel": "Set Ownership"
                }
            },
            "setDeviceEnrollmentUser": {
                "userName": "Username",
                "domain": "Domain",
                "clearAllFields": "Clear all fields",
                "leaveAllFieldsEmpty": "Leave all fields empty to remove the enrollment user.",
                "heading": "Set Device Enrollment User",
                "confirmPromptOneDevice": "Leave both fields empty to assign the device to no user at all.",
                "confirmPromptManyDevices": "New enrollment user for selected devices:",
                "actionWarningManyDevices": "The enrollment user will be updated. Leave both fields empty to assign the selected devices to no user at all.",
                "inProgressMsg": "Setting enrollment user ...",
                "successMsg": "Enrollment user successfully updated.",
                "errorMsg": "Error setting enrollment user.",
                "errorMessageUserNameBlank": "The username cannot be left blank when the domain is set.",
                "errorDetailsMsg": "An error occurred while setting device enrollment user: ",
                "buttons": {
                    "actionButtonLabel": "Set User"
                }
            },
            "setDeviceName": {
                "newName": "New Name",
                "heading": "Set Device Name",
                "inProgressMsg": "Setting device name ...",
                "successMsg": "The Set Device Name command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error setting device name.",
                "errorMsgUniqueDeviceName": "Please enter a unique mobile device name.",
                "errorDetailsMsg": "An error occurred while setting the device name:",
                "buttons": {
                    "actionButtonLabel": "Set Name"
                }
            },
            "retryAll": {
                "heading": "Retry All ...",
                "inProgressMsg": "Retrying ...",
                "successMsg": "The \"Retry All\" command was successfully queued",
                "errorMsg": "Error retrying all.",
                "errorDetailsMsg": "An error occurred while retrying all: ",
                "btnProfiles": "Retry All Failed Profiles",
                "btnApps": "Retry All Failed Applications",
                "btnToken": "Retry Pushing Fleet Management Token",
                "actionDescriptionProfile": "This command reattempts any failed installations of configuration or provisioning profiles assigned to a device through policies the next time the device reports in.",
                "headingProfile": "Retry all failed profiles",
                "actionWarningProfile": "Do you really want to reattempt all profile installations that have previously failed for the selected devices?",
                "actionDescriptionApps": "This command reattempts any failed installations of applications assigned to a device through policies the next time the device reports in.",
                "headingApps": "Retry all failed applications",
                "actionWarningApps": "Do you really want to reattempt all application installations that have previously failed for the selected devices?",
                "actionDescriptionToken": "This will ...",
                "headingToken": "Retry Pushing Fleet Management Token",
                "actionWarningToken": "Do you really want to retry ...?",
                "buttons": {
                    "actionButtonLabel": "Retry"
                }
            },
            "setRoamingOptions": {
                "heading": "Set Roaming Options",
                "voiceRoamingEnabled": "Enable voice roaming (iOS 7+ only)",
                "dataRoamingEnabled": "Enable data roaming",
                "inProgressMsg": "Setting roaming options ...",
                "successMsg": "The Set Roaming Options command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error setting roaming options.",
                "errorDetailsMsg": "An error occurred while setting roaming options: ",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Set Roaming Options request.",
                "unsupportedAttentionMessage": "Voice roaming option may <strong>not</strong> be applicable for all the selected devices.",
                "buttons": {
                    "actionButtonLabel": "Set Roaming Options"
                }
            },
            "installProvisioningProfile": {
                "headingOneDevice": "Install Provisioning Profiles on <strong>{{deviceName}}</strong>",
                "headingManyDevices": "Install Provisioning Profiles on the Selected Devices",
                "inProgressMsg": "Installing provisioning profiles ...",
                "successMsg": "The Install Provisioning Profile command was queued successfully and will run the next time this device is online.",
                "errorDetailsMsg": "An error occurred while installing the provisioning profile: ",
                "errorMsg": "Error installing provisioning profiles.",
                "errorMsgMultiplePlatforms": "Provisioning profiles cannot be installed on multiple devices running different operating systems",
                "errorMsgMultiplePlatformsDetails": "Ensure that all selected devices run the same operating system.",
                "buttons": {
                    "actionButtonLabel": "Install"
                }
            },
            "setOrganizationInfo": {
                "heading": "Set Organization Info",
                "inProgressMsg": "Setting organization info ...",
                "successMsg": "The Set Organization Info command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error setting organization info.",
                "errorDetailsMsg": "An error occurred while setting organization info: ",
                "name": "Name",
                "phoneNumber": "Phone Number",
                "email": "Email",
                "address": "Address",
                "comments": "Comments",
                "clearAllFields": "Clear all fields",
                "leaveAllFieldsEmpty": "Leave all fields empty to remove all organization information.",
                "buttons": {
                    "actionButtonLabel": "Set Info"
                }
            },
            "addMobileDeviceToPolicies": {
                "heading": "Add Mobile Device to Policies",
                "buttons": {
                    "actionButtonLabel": "Add Policy"
                }
            },
            "removeMobileDeviceFromPolicy": {
                "headingOneDevice": "Remove Mobile Device from Policy",
                "headingManyDevices": "Remove Mobile Device from Policies",
                "actionWarning": "Note: In addition to \"auto-remove\" content, this operation will also remove from the device any \"auto-remove\" applications, actions, and provisioning or configuration profiles that are associated with the selected policies.",
                "buttons": {
                    "actionButtonLabel": "Remove"
                }
            },
            "uninstallApplications": {
                "heading": "Uninstall Applications",
                "descriptionOneDevice": "This action attempts to delete the selected application from the device.",
                "descriptionManyDevices": "This action attempts to delete the selected applications from the device.",
                "actionWarning": "You cannot undo this action.",
                "inProgressMsg": "Uninstalling applications ...",
                "successMsg": "The Uninstall Application command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error uninstalling applications.",
                "errorDetailsMsg": "An error occurred while uninstalling the application: ",
                "buttons": {
                    "actionButtonLabel": "Uninstall"
                }
            },
            "uninstallConfigurationProfile": {
                "headingOneProfile": "Uninstall Configuration Profile",
                "headingManyProfiles": "Uninstall Configuration Profiles",
                "descriptionOneDevice": "This action attempts to delete the selected configuration profile from the device.",
                "descriptionManyDevices": "This action attempts to delete the selected configuration profiles from the device.",
                "actionWarning": "You cannot undo this action.",
                "inProgressMsg": "Uninstalling configuration profiles ...",
                "successMsg": "The Uninstall Configuration Profile command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error uninstalling configuration profiles.",
                "errorDetailsMsg": "An error occurred while uninstalling the configuration profile: ",
                "buttons": {
                    "actionButtonLabel": "Uninstall"
                }
            },
            "uninstallProvisioningProfile": {
                "headingOneProfile": "Uninstall Provisioning Profile",
                "headingManyProfiles": "Uninstall Provisioning Profiles",
                "descriptionOneDevice": "This action attempts to delete the selected provisioning profile from the device.",
                "descriptionManyDevices": "This action attempts to delete the selected provisioning profiles from the device.",
                "actionWarning": "You cannot undo this action.",
                "inProgressMsg": "Initiating provisioning profiles uninstall ...",
                "successMsg": "The Uninstall Provisioning Profile command was queued successfully and will run the next time this device is online.",
                "errorMsg": "Error uninstalling provisioning profiles.",
                "errorDetailsMsg": "An error occurred while uninstalling the provisioning profile: ",
                "buttons": {
                    "actionButtonLabel": "Uninstall"
                }
            },
            "setActivationLock": {
                "heading": "Set Activation Lock Options",
                "confirmPrompt": "Do you want to allow the activation lock feature on the selected devices?",
                "actionDescription0": "Clicking 'Allow Activation' means that the activation lock feature will be activated and deactivated together with 'Find My iPhone'.",
                "actionDescription1": "Clicking 'Disallow Activation' means that the activation lock feature will not be enabled, even when 'Find My iPhone' is switched on (but will be disabled when 'Find My iPhone' is switched off).",
                "actionDescription2": "Note: Activation lock protection that has previously been enabled is not disabled by this command and stays enabled until 'Find My iPhone' is next switched off.",
                "actionButtonLabel": "Set",
                "inProgressMsg": "Setting activation lock options ...",
                "successMsg": "New activation lock options have been successfully submitted.",
                "errorMsg": "Error setting activation lock options.",
                "errorDetailsMsg": "An error occurred while setting activation lock options: ",
                "unsupportedDevicesMessage": "This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Set Activation Lock Options request.",
                "buttons": {
                    "allowActivation": "Allow Activation",
                    "disallowActivation": "Disallow Activation"
                }
            },
            "deleteCustomFieldData": {
                "heading": "Remove Custom Field Data",
                "description": "The selected custom field data will be removed for the device.",
                "inProgressMessage": "Deleting custom field data...",
                "successMessage": "Custom field data successfully removed.",
                "errorMessage": "Error removed custom field data.",
                "buttons": {
                    "deleteLabel": "Remove"
                }
            },
            "editCustomFieldData": {
                "heading": "Edit Mobile Custom Field Value",
                "description": "Edit custom field value for device {{deviceName}}",
                "inProgressMessage": "Editing custom field value...",
                "successMessage": "Field value successfully edited.",
                "errorMessage": "Error editing custom field value",
                "buttons": {
                    "actionButtonLabel": "Save"
                },
                "types": {
                    "text": "Text",
                    "date": "Date",
                    "decimal": "Decimal",
                    "decimalWithSeparator": "Decimal no Separators",
                    "bytes": "Bytes",
                    "fileVersion": "File Version",
                    "ipAddress": "IP Address"
                }
            },
            "removePerformedAction": {
                "heading": "Remove from List",
                "description": "The selected performed actions will be removed from the list.",
                "buttons": {
                    "deleteLabel": "Remove"
                }
            },
            "reapplyPerformedAction": {
                "heading": "Re-execute on Device",
                "description": "The selected actions will be re-executed on this device.",
                "inProgressMessage": "Re-executing actions ...",
                "successMessage": "Actions successfully re-executed.",
                "errorMessage": "Error re-executing actions",
                "buttons": {
                    "deleteLabel": "Re-execute"
                }
            }
        }
    }
},
  "ja": true,
});