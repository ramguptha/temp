define({
  "root": {
    "amAssignableItem": {
        "shared": {},
        "models": {},
        "topNavSpec": {
            "assignableItemsTitle": "Assignable Items",
            "list": {
                "content": "Mobile Content",
                "inHouseApplications": "In-House Applications",
                "thirdPartyApplications": "Third-Party Applications",
                "bookstoreBooks": "Bookstore Books",
                "configurationProfiles": "Configuration Profiles",
                "provisioningProfiles": "Provisioning Profiles",
                "actions": "Actions"
            }
        },
        "assignableItemsListPage": {
            "navTitle": "Assignable Items",
            "body": {
                "totalSummary": "Result: {{total}} items"
            }
        },
        "assignableContentsPage": {
            "title": "Assignable Content",
            "header": {
                "buttons": {
                    "addContent": "Add Content"
                }
            },
            "body": {
                "actionsMenu": {
                    "options": {
                        "editContent": "Edit Content",
                        "deleteContent": "Delete Content"
                    }
                }
            }
        },
        "assignableActionsDetailsPage": {
            "title": "Assignable Action Details",
            "tabList": {
                "details": "Details",
                "policies": "Policies"
            },
            "tabDetails": {
                "header": "Action Details",
                "type": "Type"
            },
            "tabPolicies": {
                "header": "Smart Policies Related to this Action",
                "buttons": {
                    "addActionToPolicies": "Add Action to Policies"
                },
                "actionsMenu": {
                    "options": {
                        "editPolicyAssignmentProperties": "Edit Policy Assignment Properties",
                        "removeActionFromPolicies": "Remove Action from Policies"
                    }
                }
            },
            "navigationPane": {
                "title": "All Actions"
            }
        },
        "assignableContentDetailsPage": {
            "title": "Assignable Content Details",
            "navigationPane": {
                "title": "All Content"
            },
            "tabList": {
                "details": "Details",
                "policies": "Policies",
                "mobileDevices": "Mobile Devices"
            },
            "tabDetails": {
                "header": "Content Details",
                "leaveAbsoluteSafeLabel": "Leave AbsoluteSafe",
                "noPasscodePresentLabel": "Passcode Present",
                "wiFiDownloadOnlyLabel": "Wi-Fi Download Only",
                "fileEmailAllowed": "File allowed to be emailed",
                "filePrintAllowed": "File allowed to be printed",
                "filenameHeader": "File name",
                "fileTypeHeader": "File type",
                "categoryHeader": "Category",
                "filesizeHeader": "File size",
                "lastModifiedHeader": "Last modified",
                "descriptionHeader": "Description"
            },
            "tabPolicies": {
                "header": "Mobile Policies Related to this Content",
                "buttons": {
                    "addContentToPolicies": "Add Content To Policies"
                },
                "actionsMenu": {
                    "options": {
                        "editPolicyAssignmentProperties": "Edit Policy Assignment Properties",
                        "removeContentFromPolicies": "Remove Content From Policies"
                    }
                }
            },
            "tabMobileDevices": {
                "header": "Mobile Devices Related to this Content"
            }
        },
        "assignableInHouseApplicationsPage": {
            "title": "Assignable In-House Applications",
            "breadcrumbsTitle": "Assignable In-House Apps"
        },
        "assignableThirdPartyApplicationsPage": {
            "title": "Assignable Third-Party Applications",
            "breadcrumbsTitle": "Assignable Third-Party Apps"
        },
        "assignableBookstoreBooksPage": {
            "title": "Assignable Bookstore Books"
        },
        "assignableConfigurationProfilesPage": {
            "title": "Assignable Configuration Profiles"
        },
        "assignableProvisioningProfilesPage": {
            "title": "Assignable Provisioning Profiles"
        },
        "assignableActionsPage": {
            "title": "Assignable Actions",
            "header": {
                "buttons": {
                    "addActionMenu": {
                        "label": "Add Action",
                        "divider": "-----------------------------------------------",
                        "options": {
                            "sendMessage": "Send Message To Device",
                            "setRoamingOptions": "Set Roaming Options",
                            "sendEmail": "Send Email",
                            "demoteToUnmanagedDevice": "Demote To Unmanaged Device",
                            "removeConfigurationProfile": "Remove Configuration Profile",
                            "sendSms": "Send SMS",
                            "freezeDevice": "Freeze Device",
                            "updateDeviceInfo": "Update Device Information",
                            "setWallpaper": "Set Wallpaper",
                            "setActivationLockOptions": "Set Activation Lock Options",
                            "sendVppInvitation": "Send VPP Invitation",
                            "registerUserInVpp": "Register User In VPP",
                            "retireUserFromVpp": "Retire User From VPP",
                            "setDeviceName": "Set Device Name",
                            "setCustomFieldValue": "Set Custom Field Value",
                            "attentionMode": "Set Attention Mode"
                        }
                    }
                }
            },
            "body": {
                "actionsMenu": {
                    "options": {
                        "newAction": "New Action",
                        "editAction": "Edit Action",
                        "deleteAction": "Delete Actions",
                        "duplicateAction": "Duplicate Action"
                    }
                }
            }
        },
        "modals": {
            "addContent": {
                "heading": "Add New Mobile Content",
                "labelForTextField": "Mobile Smart Policy Name",
                "placeholder": "New Smart Policy Name",
                "stepLabels": {
                    "uploadFiles": "Upload Files",
                    "assignProperties": "Assign Properties",
                    "assignPolicy": "Assign Policy",
                    "finish": "Finish"
                },
                "uploadFileStep": {
                    "dropFilesAreaDescription": "Drop files here or click to choose files ...",
                    "successUploadMessage": "Successfully uploaded {{numberOfUploadedFiles}} out of {{numberOfTotalFiles}} files",
                    "hint": "Hint: You can edit file names",
                    "uploadingMessage": "Uploading files ...",
                    "secondsLeft": "seconds left",
                    "uploadingOf": " of ",
                    "validation": {
                        "existOnServer": "\"{{nameOfFile}}\" already exists on the server.",
                        "renameFileToContinue": "Rename the file below to continue.",
                        "renameItToSave": "Rename the file to save changes.",
                        "fileNameIsBlank": "Enter a name for the file below.",
                        "nameIsBlank": "Enter a name.",
                        "uploadFailed": "Upload of file \"{{nameOfFile}}\" failed with the following error:",
                        "selectFilesNotFolder": "Select one or more readable files, not directories."
                    }
                },
                "assignPropertiesStep": {
                    "categoryLabel": "Enter a category:",
                    "categoryMenu": {
                        "options": {
                            "documents": "Documents",
                            "multimedia": "Multimedia",
                            "pictures": "Pictures",
                            "other": "Other"
                        }
                    },
                    "categoryInputPlaceholder": "Enter a category",
                    "categoryDescriptionPlaceholder": "Add a description ...",
                    "assignPermissionsLabel": "Assign permissions to {{numberOfFiles}} content files:",
                    "canLeaveCheckbox": "Files can leave AbsoluteSafe",
                    "canEmailCheckbox": "User can email files",
                    "canPrintCheckbox": "User can print files",
                    "downloadOnlyOverWiFiCheckbox": "Download files only over Wi-Fi",
                    "passphraseLabel": "Passphrase:",
                    "enterPassphrasePlaceholder": "Enter a passphrase",
                    "confirmPassphrasePlaceholder": "Type the passphrase again",
                    "validation": {
                        "passphraseNotMatched": "Passphrases do not match"
                    },
                    uniqueFileNameError: "Please enter a unique name for the file \"{{nameOfFile}}\" below."
                },
                "buttons": {
                    "addContent": "Add Content"
                },
                "inProgressMessage": "Adding new content...",
                "successMessage": "New content successfully added",
                "errorMessage": "Error Saving Edits"
            },
            "deleteActions": {
                "heading": "Delete Actions",
                "description": "The selected actions will be permanently deleted.",
                "buttons": {
                    "deleteAction": "Delete Actions"
                }
            },
            "deleteAction": {
                "heading": "Delete Action",
                "description": "The selected action will be permanently deleted.",
                "buttons": {
                    "deleteAction": "Delete Action"
                }
            },
            "deleteContent": {
                "heading": "Delete Content",
                "description": "The selected content will be permanently deleted.",
                "buttons": {
                    "deleteContent": "Delete Content"
                }
            },
            "editContent": {
                "heading": "Edit Content",
                "labelForName": "Name:",
                "labelForPermissions": "Permissions:"
            },
            "actionProperties": {
                "indicatesRequiredField": "Indicates required field",
                "charactersRemainingInField": "{{characterCount}} characters remaining",
                "customField": "Custom field",
                "actionName": "Action name",
                "actionType": "Action type",
                "name": "Name",
                "description": "Description",
                "actionMessage": "The users of the target devices can alter these settings.",
                "targetPlatforms": "Target platforms",
                "ios": "iOS",
                "iosOnly": "iOS only",
                "android": "Android",
                "windows": "Windows Phone",
                "iosAndroid": "iOS, Android",
                "iosAndroidWindows": "iOS, Android, Windows Phone",
                "iosWindows": "iOS, Windows Phone",
                "androidWindows": "Android, Windows Phone",
                "none": "none",
                "lastModified": "Last Modified",
                "messageText": "Message text",
                "voiceRoaming": "Voice roaming",
                "dataRoaming": "Data roaming",
                "on": "On",
                "off": "Off",
                "leaveAsIs": "Leave as is",
                "emailTo": "To",
                "emailCc": "CC",
                "emailSubject": "Subject",
                "message": "Message",
                "phoneNumber": "Phone number",
                "activationLock": "Activation lock",
                "disallow": "Disallow",
                "allow": "Allow",
                "disallowed": "Disallowed",
                "allowed": "Allowed",
                "deviceName": "Device name",
                "dataType": "Data type",
                "dataValue": "Value",
                "setValue": "Set to",
                "removeValue": "Remove",
                "attentionMode": "Attention mode",
                "enable": "Enable",
                "disable": "Disable",
                "enabled": "Enabled",
                "disabled": "Disabled",
                "attentionMessage": "Attention message",
                "newPasscode": "New passcode",
                "verification": "Verification",
                "passphraseNotMatched": "Passcodes do not match",
                "passphraseErrorMessage": "Passcodes must be between 4 and 16 characters.",
                "image": "Image",
                "imageDimensions": "Image dimensions",
                "wallpaperOptions": "Wallpaper options",
                "wallpaperPicture": "Wallpaper picture",
                "lockScreen": "Lock screen",
                "homeScreen": "Home screen",
                "selectImage": "Select image",
                "imageFormat": "PNG or JPEG format",
                "noImageSelected": "No image selected.",
                "readingImage": "Reading Image ...",
                "wallpaperInfoTitleTooltip": "iOS Screen Resolution Reference",
                "wallpaperInfoiPhone6PlusTooltip": "<strong>iPhone 6 Plus:</strong> 2208 x 1242 pixels (2208 x 2208 to support rotation)",
                "wallpaperInfoiPhone6Tooltip": "<strong>iPhone 6:</strong> 1134 x 750",
                "wallpaperInfoiPhone5Tooltip": "<strong>iPhone 5:</strong> 1136 x 640",
                "wallpaperInfoiPhone4Tooltip": "<strong>iPhone 4:</strong> 960 x 640",
                "wallpaperInfoiPadRetinaTooltip": "<strong>iPad retina models:</strong> 2048 x 1536 (2048 x 2048 to support rotation)",
                "wallpaperInfoiPadTooltip": "<strong>iPad:</strong> 1024 x 768 (1024 x 1024)",
                "vppAccount": "VPP account",
                "registerOptions": "Register options",
                "registerOnly": "Register users only",
                "registerAndInvite": "Register and invite users",
                "registerOnlyMessage": "If you select this option, users must be invited at a later time to associate their Apple ID with the Volume Purchase Program (VPP) before they can download and use apps through the program.",
                "sendInvitation": "Send invitation",
                "mdm": "MDM dialog",
                "webClip": "Web Clip (iOS home screen bookmark)",
                "email": "Email",
                "absoluteMessage": "AbsoluteApps message",
                "sms": "SMS",
                "smsText": "SMS text",
                "text": "Text",
                "subject": "Subject",
                "registerEmailSubject": "Register in the Apple Volume Purchase Program (VPP)",
                "registerSmsText": "To receive company-paid apps, please visit this URL to register your Apple ID with the ${MDU_Company} VPP account: ${MD_VPPInviteURL}",
                "registerEmailDisplayTitle": "${MDU_DisplayName}:",
                "registerEmailDisplayText": "Please visit the URL below and register your Apple ID with the ${MDU_Company} Apple Volume Purchase Program (VPP) account:",
                "registerEmailUrlTitle": "${MD_VPPInviteURL}",
                "registerEmailUrlText": "This will enable you to receive company-paid apps on your Apple device at no cost to you, and will enable the company to assign those apps to you automatically.",
                "profile": "Profile",
                "iosProfilesTitle": "iOS configuration profiles",
                "androidProfilesTitle": "Android configuration profiles",
                "validation": {
                    "requiredPlatformMessage": "You must select at least one platform.",
                    "duplicateNameMessage": "Enter a unique action name.",
                    "requiredScreenOptionMessage": "You must select at least one option.",
                    "requiredRoamingOptionMessage": "Both options cannot be \"Leave as is\" because there is no action to perform.",
                    "fileVersionWrongMessage": "File version is not valid.",
                    "emptyListOfOptionsMessage": {
                        "configProfiles": "No configuration profiles available.",
                        "customFields": "No custom fields available.",
                        "vppAccounts": "No VPP accounts available."
                    }
                }
            },
            "action": {
                "saveAndAssignButton": "Save and Assign to Policies...",
                "create": {
                    "inProgressMessage": "Adding new action ...",
                    "successMessage": "New action successfully added.",
                    "errorMessage": "Error saving new action"
                },
                "duplicate": {
                    "inProgressMessage": "Duplicating an action ...",
                    "successMessage": "New action successfully added.",
                    "errorMessage": "Error duplicating the action"
                },
                "policyAssignment": {
                    "policyName": "Policy name",
                    "message": "Actions can only be assigned to smart policies.",
                    "addActionToPolicies": {
                        "heading": "Add Action to Policies"
                    },
                    "addActionsToPolicy": {
                        "heading": "Add Actions to <strong>{{selectedContextName}}</strong>"
                    },
                    "timeComponent": {
                        "delayTitle": "Action start delay",
                        "repeatTitle": "Action repeat",
                        "repeat": "Repeat",
                        "frequency": "Frequency",
                        "numberOfTimes": "times",
                        "numberValidation": "You must enter a number in this field."
                    },
                    "edit": {
                        "heading": "Edit Assignment Properties for <strong>{{policyName}}</strong>"
                    },
                    "remove": {
                        "headingPolicy": "Remove Action from Policy",
                        "headingPolicies": "Remove Action from Selected Policies",
                        "descriptionPolicies": "Do you really want to remove the action from these policies?",
                        "headingAction": "Remove Actions from Policy",
                        "descriptionActions": "Do you really want to remove the selected actions from this policy?",
                        "buttonActions": "Remove Actions",
                        "buttonAction": "Remove Action"
                    }
                }
            }
        }
    }
},
  "ja": true,
});