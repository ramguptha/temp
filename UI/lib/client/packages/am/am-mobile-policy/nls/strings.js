define({
  root: {
    amMobilePolicies: {
      shared: {
        notUniqueNameMessage: 'Please enter a unique policy name.',

        assignmentRuleLabel: 'Assignment Rule:',
        assignmentRuleLabelAutoInstallTooltip: '<strong>Auto-install:</strong> These applications are automatically installed on devices belonging to the policy.',
        assignmentRuleLabelOnDemandTooltip: '<strong>On-demand:</strong> The users of devices belonging to the policy can install these applications from within AbsoluteApps if they desire.',
        assignmentRuleLabelAutoInstallAutoRemoveTooltip: '<strong>Auto-install, Auto-remove:</strong> These applications are automatically installed on devices added to the policy and automatically removed from devices that leave the policy.',
        assignmentRuleLabelOnDemandAutoRemoveTooltip: '<strong>On-demand, Auto-remove:</strong> The users of devices belonging to the policy can install these applications from within AbsoluteApps if they desire. The applications are automatically removed from devices that leave the policy.',
        assignmentRuleLabelForbiddenTooltip: '<strong>Forbidden:</strong> These applications cannot be installed on devices belonging to the policy.',

        assignmentRuleLabelProfileAutoInstallTooltip: '<strong>Auto-install:</strong> These profiles are automatically installed on devices belonging to the policy.',
        assignmentRuleLabeProfilelOnDemandTooltip: '<strong>On-demand:</strong> The users of devices belonging to the policy can install these profiles from within AbsoluteApps if they desire.',
        assignmentRuleLabelProfileAutoInstallAutoRemoveTooltip: '<strong>Auto-install, Auto-remove:</strong> These profiles are automatically installed on devices added to the policy and automatically removed from devices that leave the policy.',
        assignmentRuleLabelProfileOnDemandAutoRemoveTooltip: '<strong>On-demand, Auto-remove:</strong> The users of devices belonging to the policy can install these profiles from within AbsoluteApps if they desire. The profiles are automatically removed from devices that leave the policy.',
        assignmentRuleLabelProfileForbiddenTooltip: '<strong>Forbidden:</strong> These profiles cannot be installed on devices belonging to the policy.',

        assignmentRuleLabelMediaAutoInstallTooltip: '<strong>Auto-install:</strong> These media files are automatically downloaded to devices when the devices enter the policy. The files remain on a device if it leaves the policy and the device\'s user can manually delete the files, if desired.',
        assignmentRuleLabelMediaOnDemandTooltip: '<strong>On-demand:</strong> The users of devices belonging to the policy can manually download these media files if they desire. The files remain on a device if it leaves the policy and the device\'s user can manually delete the files, if desired.',
        assignmentRuleLabelMediaAutoInstallAutoRemoveTooltip: '<strong>Auto-install, Auto-remove:</strong> These media files are automatically downloaded to devices added to the policy and automatically removed from devices that leave the policy.',
        assignmentRuleLabelMediaOnDemandAutoRemoveTooltip: '<strong>On-demand, Auto-remove:</strong> The users of devices belonging to the policy can manually download these media files if they desire. The files are automatically removed from devices that leave the policy.',

        assignmentRuleOptions: {
          autoInstall: 'Auto-install',
          onDemand: 'On-demand',
          autoInstallAutoRemove: 'Auto-install, Auto-remove',
          onDemandAutoRemove: 'On-demand, Auto-remove',
          forbidden: 'Forbidden'
        },

        availabilitySelector: {
          always: 'Always',
          dailyInterval: 'Daily interval',
          fixedPeriod: 'Fixed period'
        },

        setAvailabilityTimeLabel: 'Set availability time',

        buttons: {
          addToPolicy: 'Add to Policy',
          addToPolicies: 'Add to Policies'
        }
      },

      models: {

      },

      topNavSpec: {
        mobilePoliciesTitle: 'Mobile Policies',
        allMobilePoliciesTitle: 'All Mobile Policies'
      },

      mobilePoliciesListPage: {
        title: 'Mobile Policies',

        addPolicyMenu: {
          label: 'Add Policy',
          options: {
            newPolicy: 'New Policy',
            newSmartPolicy: 'New Smart Policy'
          }
        },

        actionsMenu: {
          label: 'Actions',
          options: {
            deletePolicy: 'Delete Policy',
            deletePolicies: 'Delete Policies',
            editPolicy: 'Edit Policy',
            editSmartPolicy: 'Edit Smart Policy'
          }
        },

        totalSummary: 'Result: {{total}} mobile policies',

        gridHeaders: {
          policyName: 'Policy Name',
          smartPolicy: 'Smart Policy'
        }

      },

      mobilePolicyDetailsPage: {
        title: 'Mobile Policy Details',
        navigationPane: {
          title: 'Mobile Policies',
          readOnlyPolicy: 'Read-only policy'
        },

        body: {
          tabLabels: {
            mobileDevices: 'Mobile Devices',
            inHouseApplications: 'In-House Applications',
            thirdPartyApplications: 'Third-Party Applications',
            configurationProfiles: 'Configuration Profiles',
            content: 'Content',
            actions: 'Actions'
          },

          tabDescriptions: {
            mobileDevices: 'Devices Related to this Mobile Policy',
            inHouseApplications: 'In-House Applications Added to this Mobile Policy',
            thirdPartyApplications: 'Third-Party Applications Added to this Mobile Policy',
            configurationProfiles: 'Configuration Profiles Added to this Mobile Policy',
            content: 'Content Related to this Mobile Policy',
            actions: 'Actions Related to this Mobile Policy'
          },

          buttons: {
            addMobileDevicesToPolicy: 'Add Mobile Devices to Policy',
            addInHouseApplication: 'Add In-House Application',
            addThirdPartyApplication: 'Add Third-Party Application',
            addConfigurationProfile: 'Add Configuration Profile',
            addContentToPolicy: 'Add Content to Policy',
            addActionsToPolicy: 'Add Actions to Policy'
          },

          actionsMenu: {
            options: {
              removeFromPolicy: 'Remove from Policy',
              moveToAnotherPolicy: 'Move to Another Policy',
              removeInHouseApplication: 'Remove from Policy',
              removeThirdPartyApplication: 'Remove  from Policy',
              editPolicyAssignmentProperties: 'Edit Policy Assignment Properties',
              editActionAssignmentProperties: 'Edit Action Assignment Properties',
              removeConfigurationProfile: 'Remove from Policy',
              removeContentFromPolicy: 'Remove from Policy',
              removeActionFromPolicy: 'Remove from Policy',
              reexecuteActionOnPolicy: 'Re-execute on Policy Devices'
            }
          },

          totalSummary: 'Result: {{total}} mobile devices'
        }
      },

      modals: {

        createNewPolicy: {
          heading: 'Create a Mobile Policy',
          labelForTextField: 'Policy Name',
          placeholder: 'New Policy Name',

          buttons: {
            createPolicy: 'Create Policy'
          },

          inProgressMessage: 'Creating policy ...',
          successMessage: 'Your policy was created successfully.',
          errorMessage: 'An error occurred while creating the policy.'
        },

        createNewSmartPolicy: {
          heading: 'Create a Mobile Smart Policy',
          labelForTextField: 'Mobile Smart Policy Name',
          placeholder: 'New Smart Policy Name',

          stepLabels: {
            properties: 'Set Properties',
            createSmartFilter: 'Create Smart Filter',
            verify: 'Verify and Save'
          },

          conditionSelectionLabel: 'Contains mobile devices that match:',
          conditionSelectionOptions: {
            all: 'All of the specified conditions',
            any: 'Any of the specified conditions'
          },

          conditionSelectionAppAndProfilesLabel: 'Contains mobile devices on the following condition:',
          conditionSelectionAppAndProfilesOptions: {
            allAppMissing: 'All of the specified applications are missing',
            allAppInstalled: 'All of the specified applications are installed',
            someAppMissing: 'Some of the specified applications are missing',
            someAppInstalled: 'Some of the specified applications are installed',
            allProfilesMissing: 'All of the specified configuration profiles are missing',
            allProfilesInstalled: 'All of the specified configuration profiles are installed',
            someProfilesMissing: 'Some of the specified configuration profiles are missing',
            someProfilesInstalled: 'Some of the specified configuration profiles are installed'
          },


          radioButtonLabels: {
            mobileDevices: 'Mobile Devices',
            mobileDevicesByInstalledApplications: 'Mobile Devices by Installed Applications',
            mobileDevicesByAddedConfigurationProfiles: 'Mobile Devices by Added Configuration Profiles'
          },

          buttons: {
            createPolicy: 'Create Policy'
          },

          filterLabel: 'Filter:',
          confirmMessage: 'To create your smart policy with the filters listed below, click Save.',
          confirmMessageEdit: 'To edit your smart policy with the filters listed below, click Save.',
          inProgressMessage: 'Creating a mobile smart policy ...',
          successMessage: 'Your policy was created successfully.',
          errorMessage: 'Error creating mobile smart policy.',

          englishOnlyWarningMessage: "This filter searches against English error messages only."
        },

        deletePolicy: {
          policyHeading: 'Delete Policy',
          policiesHeading: 'Delete Policies',
          policyDescription: 'The selected policy will be permanently deleted.',
          policiesDescription: 'The selected policies will be permanently deleted.',

          buttons: {
            deletePolicy: 'Delete Policy',
            deletePolicies: 'Delete Policies'
          }

        },

        editPolicy: {
          heading: 'Edit Mobile Policy',
          description: 'Change the name of this policy.',

          buttons: {
            createPolicy: 'Edit Policy'
          }
        },

        editSmartPolicy: {
          heading: 'Edit Smart Mobile Policy',
          description: 'Change the name of this policy.',

          stepLabels: {
            properties: 'Edit Properties',
            editSmartFilter: 'Edit Smart Filter'
          },

          buttons: {
            createPolicy: 'Edit Policy'
          }
        },

        addMobileDevices: {
          heading: 'Add Mobile Devices to <strong>{{policyName}}</strong>'
        },

        addInHouseApplication: {
          headingPolicy: 'Add In-House Applications to <strong>{{policyName}}</strong>',
          headingPolicies: 'Add In-House Applications to the Selected Policies'
        },

        removeInHouseApplication: {
          heading: 'Remove In-House Applications from Policy',
          description: 'Do you really want to remove the selected in-house applications from this policy?',

          buttons: {
            removeApplication: 'Remove Applications'
          }
        },

        addThirdPartyApplication: {
          headingPolicy: 'Add Third-Party Applications to <strong>{{policyName}}</strong>',
          headingPolicies: 'Add Third-Party Applications to the Selected Policies',

          selectionOnDemandAndroidWarning: 'Android apps can only be installed "On-Demand"',
          selectionOnDemandMultipleWarning: 'Selection of multiple apps can only be installed "On-Demand"'
        },

        removeThirdPartyApplication: {
          heading: 'Remove Third-Party Applications from Policy',
          description: 'Do you really want to remove the selected third-party applications from this policy?',

          buttons: {
            removeApplication: 'Remove Applications'
          }
        },

        addConfigurationProfile: {
          heading: 'Add Configuration Profiles to <strong>{{policyName}}</strong>'
        },

        removeConfigurationProfile: {
          heading: 'Remove Configuration Profiles',
          description: 'This action will delete the selected configuration profiles from the policy.',
          descriptionWarning: 'You cannot undo this action.',

          buttons: {
            removeProfile: 'Remove Profiles'
          }
        },

        editPolicyAssignmentProperties: {
          heading: 'Edit Policy Assignment Properties for the Selected Configuration Profiles',

          buttons: {
            removeProfile: 'Remove Profiles'
          }
        },

        addContent: {
          headingPolicy: 'Add Content to <strong>{{selectedContextName}}</strong>',
          policyName: 'Policy Name',

          headingPolicies: 'Add Content to Policies',
          contentName: 'Content Name'
        },

        editContent: {
          headingEditContent: 'Edit Policy Assignment Properties for the Selected Content',
          headingEditPolicy: 'Edit Assignment Properties for <strong>{{policyName}}</strong>',
          headingEditPolicies: 'Edit Assignment Properties for the Selected Policies',

          buttons: {
            editContent: 'Edit Content'
          }
        },

        removeContent: {
          headingPolicy: 'Remove Contents from Policy',
          headingPolicies: 'Remove Content from Selected Policies',
          descriptionSelectedFile: 'Do you really want to remove the selected content file from this policy?',
          descriptionSelectedFiles: 'Do you really want to remove the selected content files from this policy?',
          descriptionPolicy: 'Do you really want to remove the content file from this policy?',
          descriptionPolicies: 'Do you really want to remove the content file from these policies?',

          buttons: {
            removeContent: 'Remove Contents'
          }
        },

        moveDevices: {
          heading: 'Move Devices to Another Policy',

          buttons: {
            moveMobileDevices: 'Move Mobile Devices'
          }
        },

        removeDevices: {
          headingDevices: 'Remove Mobile Devices from Policy',
          actionWarning: 'Note: In addition to "auto-remove" content, this operation will also remove from the selected devices any "auto-remove" applications, actions, and provisioning or configuration profiles that are associated with the policy.',

          buttons: {
            removeDevices: 'Remove Devices'
          }
        },

        reexecuteAction: {
          heading: 'Re-execute on Policy Devices',
          description: 'The selected actions will be re-executed on devices associated with this policy.',
          inProgressMessage: 'Re-executing actions...',
          successMessage: 'Actions successfully re-executed.',
          errorMessage: 'Error re-executing actions',
          buttons: {
            actionButton: 'Re-execute'
          }
        }
      }
    }
  },

  // Supported Locales
  // -----------------

  'ja': true
});
