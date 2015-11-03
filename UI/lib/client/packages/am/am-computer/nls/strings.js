define({
  root: {
    amComputer: {
      shared: {
        computersSelected: '{{deviceCountDetails}} computers selected.'
      },

      computerListPage: {
        title: 'Computers',
        totalSummary: 'Result: {{total}} computers',
        computersTitle: 'Computers',
        allComputersTitle: 'All Computers',
        snapContainerSubTitle: 'Computer Type: {{machineModel}}',
        commandsTitle: 'Commands',
        groupNavTitle: 'COMPUTERS',
        commands: {
          sendMessage: 'Send Message',
          sendMessageDescription: 'Send a message to {{agentName}}',
          gatherInventory: 'Gather Inventory',
          gatherInventoryDescription: 'Gather inventory from {{agentName}}',
          deviceFreeze: 'Device Freeze',
          deviceFreezeDescription: 'Device Freeze for {{agentName}}',
          deviceUnfreeze: 'Device Unfreeze',
          deviceUnfreezeDescription: 'Device Unfreeze for {{agentName}}',
          dataDelete: 'Data Delete',
          dataDeleteDescription: 'Data Delete for {{agentName}}'
        }
      },

      tabLabels: {
        aboutComputer: 'About Computer',
        hardware: 'Hardware',
        cpu: 'CPU',
        systemSoftware: 'System Software',
        memory: 'Memory',
        volume: 'Volumes',
        networkAdapter: 'Network Adapters',
        missingPatch: 'Missing Patches',
        installedSoftware: 'Installed Software',
        installedProfiles: 'Installed Profiles'
      },

      aboutComputerTab: {
        title: 'Computer Details',
        commandsTitle: 'Commands'
      },

      hardwareTab: {
        title: 'Hardware Details'
      },

      cpuTab: {
        title: 'CPU Details'
      },

      systemSoftwareTab: {
        title: 'System Software'
      },

      memoryTab: {
        title: 'Memory'
      },

      volumeTab: {
        title: 'Volumes'
      },

      networkAdapterTab: {
        title: 'Network Adapters'
      },

      missingPatchTab: {
        title: 'Missing Patches'
      },

      installedSoftwareTab: {
        title: 'Installed Software'
      },

      installedProfileTab: {
        title: 'Installed Profiles'
      },

      modals: {
        sendMessage: {
          headingOneDevice: 'Send Message to <strong>{{deviceName}}</strong>',
          headingManyDevices: 'Send Message to the Selected Computers',
          enterMessage: 'Enter message',
          inProgressMsg: 'Sending message...',
          successMsg: 'The Message was successfully queued for sending to the computers.',
          errorMsg: 'Error sending message.',
          messageSizeNote: 'Note: You have exceeded the maximum of {{maxMessageSize}} characters. Your message will be truncated',
          unsupportedDevicesMessage: 'This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected computers. The following table lists the computers that are not included in this Send Message request.',
          buttons: {
            actionButtonLabel: 'Send Message'
          }
        },

        gatherInventory: {
          headingOneDevice: 'Gather Inventory from <strong>{{deviceName}}</strong>',
          headingManyDevices: 'Gather Inventory from the Selected Computers',
          inProgressMsg: 'Gathering inventory...',
          successMsg: 'The Gather Inventory command was queued successfully and will run the next time this device is online.',
          errorMsg: 'Error Sending Gather Inventory.',
          unsupportedDevicesMessage: 'This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected computers. The following table lists the computers that are not included in this Gather Inventory request.',
          forceFullInventoryCheckbox: 'Force full inventory',
          includeFontInformationCheckbox: 'Include font information',
          includePrinterInformationCheckbox: 'Include printer information',
          includeStartupItemInformationCheckbox: 'Include startup item information',
          includeServiceInformationCheckbox: 'Include service information',
          includeStartupItemInformationCheckboxOsxOnly: 'Include startup item information (OS X only)',
          includeServiceInformationCheckboxWindowsOnly: 'Include service information (Windows only)',
          warningMessage: 'Computer information that has changed since the last update is always gathered, even if you do not select any of these options.',
          buttons: {
            actionButtonLabel: 'Gather Inventory'
          }
        }

      }

    }
  },

  // Supported Locales
  // -----------------

  'ja': true
});
