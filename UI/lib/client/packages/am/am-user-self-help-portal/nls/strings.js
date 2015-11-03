define({
  root: {
    amUserSelfServicePortal: {
      shared: {
        passcodeErrorMessageMobile: 'Passcode must be between 4 and 16 characters.',
        passcodeErrorMessageComputer: 'Password must be 6 characters.',
        passcodesDontMatchErrorMessageMobile: 'Passcodes do not match.',
        passcodesDontMatchErrorMessageComputer: 'Passwords do not match.',
        noDataToDisplay: 'No Results'
      },

      deviceList: {
        title: 'My Devices'
      },

      deviceDetails: {
        title: 'Device Details',
        actionsLabel: 'Commands',
        phoneNumber: 'Phone Number: {{phoneNumber}}',
        osText: 'OS',
        osVersion: 'OS Version',
        passcodePresent: 'Passcode',
        batteryLevel: 'Battery',
        modelNumber: 'Model',
        identifierUDID: 'Identifier (UDID)',
        serialNumber: 'Serial Number',
        deviceCapacity: 'Device Capacity',
        pcPlatform: 'Windows',
        appleMacPlatform: 'OS X',
        appleTvPlatform: 'Apple TV',
        androidPhonePlatform: 'Android',
        androidTabletPlatform: 'Android',
        iOsPhonePlatform: 'iOS',
        iOsTabletPlatform: 'iOS',
        windowsPhonePlatform: 'Windows Phone',
        detectedDateTooltip: '<p>Detected date:</p><p>{{batteryLevelDate}}</p>',

          commands: {
          deviceLockTitle: 'Lock Device',
          resetTrackingPassphraseIOsTitle: 'Clear Passcode',
          resetTrackingPassphraseAndroidTitle: 'Set Passcode',
          remoteDataDeleteTitle: 'Erase Device',
          sendMessageTitle: 'Send Message',
          trackDeviceTitle: 'Track Device',
          isAndroidAndSupportsNoCommandsMessage: 'Commands not enabled for this device. To enable commands for Android devices, ensure they have Absolute Mobile Device Management configured and AbsoluteApps installed.',
          isIOSAndSupportsNoCommandsMessage: 'Commands not enabled for this device. To enable commands for iOS devices, ensure they have Absolute Mobile Device Management configured.',
          isWinPhoneAndSupportsNoCommandsMessage: 'Commands not enabled for this device. To enable commands for Windows Phone devices, ensure they have Absolute Mobile Device Management configured.',
          userHasNoCommandPermissionsMessage: 'You are currently not authorized to issue any commands for this device.'
        }
      },

      modals: {

        deviceLock: {
          headingMobile: 'Lock Device',
          headingComputer: 'Lock Computer',
          enterNewPasscodeMobile: '<label>Enter a new passcode</label><div class="text-italic">(optional)</div>',
          typePasscodeAgainMobile: 'Type the passcode again',
          enterNewPasscodeComputer: '<label>Enter a new password</label>',
          typePasscodeAgainComputer: 'Type the password again',
          progressMsgMobileDevice: 'Locking device ...',
          progressMsgComputer: 'Locking computer ...',
          successMsg: 'The Device Lock command was queued successfully and will run the next time this device is online.',
          errorMsg: 'Error locking devices.',
          actionMsg1MobileDevice: 'Locks your device using its existing passcode, if present.',
          actionMsg2MobileDevice: 'This device does not have a passcode. You can choose to lock this device with a passcode.',
          actionMsgOsxComputer: '<p>Enter a password for unlocking the computer. The password must be exactly six characters long.</p><p>This password must be entered locally on the locked computer to unlock it. It is not possible to unlock computers remotely.</p><p>Note that unlocking a locked computer without this password (for example, if it is lost) is only possible by contacting Apple.</p>',
          actionAllOtherMsg: 'Lock the selected device.',
          enterMessage: '<label>Enter message</label><label class="text-italic"> (optional):</label>',
          enterPhoneNumber: '<label>Enter phone number</label><label class="text-italic"> (optional):</label>',
          messageSizeNote: 'Note: You have exceeded the maximum of {{maxMessageSize}} characters. Your message will be truncated',
          buttons: {
            actionButtonLabel: 'Lock'
          }
        },

        remoteDataDelete: {
          actionMsgOsxComputer: '<p>Erases all data from your computer’s writable internal and external drives, including the operating system, and locks the computer.</p><p>Enter a password for unlocking your computer. The password must be exactly six characters long and must be entered locally on the computer to unlock it.</p><p>Unlocking your computer without this password is possible only by contacting Apple.</p>',
          actionMsgMobileDevices: '<p>Erases all user data on your device and any applications you have installed, effectively resetting the device to its factory condition.</p>',
          heading: 'Erase Device',
          actionWarning: 'WARNING: You cannot recover erased data.',
          inProgressMsg: 'Erasing data ...',
          successMsg: 'Erase Device command was successfully queued and will run the next time this device is online.',
          errorMsg: 'Error erasing data.',
          buttons: {
            actionButtonLabel: 'Erase Device'
          },
          deleteInternalStorageOnly: 'Erase internal storage only',
          deleteInternalStorageSDCard: 'Erase internal storage and SD card'
        },

        resetTrackingPasscode: {
          heading: 'Reset Tracking Passphrase',
          actionWarning: 'Do you really want to reset the passphrase on this device?',
          actionDetails: 'Resetting the passphrase will require the user of the device to enter a new passphrase before being able to use Absolute App.',
          inProgressMsg: 'Resetting Tracking Passphrase ...',
          successMsg: 'The Reset Tracking Passphrase command was queued successfully and will run the next time this device is online.',
          errorMsg: "Resetting Tracking Passphrase.",
          actionButtonLabelClearPasscodes: 'Reset Passphrase',
          unsupportedDevicesMessage: 'This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Reset Tracking Passphrase request.',
          buttons: {
            actionButtonLabel: 'Reset Passphrase'
          }
        },

        trackDevice: {
          heading: 'Track Device',
          inProgressMsg: 'Setting tracking for the selected mobile device ...',
          successMsg: 'The Track Device command was queued successfully and will run the next time this device is online.',
          errorMsg: "Setting tracking for the selected mobile device.",
          actionButtonLabelClearPasscodes: 'Track Device',
          unsupportedDevicesMessage: 'This command does not apply to {{unsupportedDeviceCount}} of the {{deviceCount}} selected devices. The following table lists the devices that are not included in this Reset Tracking Passphrase request.',
          labels: {
            actionDetails: 'Set tracking for the selected mobile device:',
            trackDevice: 'Track Device',
            activationPassphrase: 'Activation Passphrase:',
            activationPassphrasePlaceholder: 'Enter Activation Passphrase',
            trackingInterval: 'Tracking Interval:',
            trackingIntervalPlaceholder: 'Enter Tracking Interval:',
            locationAccuracy: 'Location Accuracy:'
          },
          buttons: {
            actionButtonLabel: 'Track Device'
          },
          help: {
            trackDeviceStrong: 'Track device:',
            trackDeviceText: 'Checking this option enables tracking of the selected device, unchecking it disables it.',
            activationPassphraseStrong: 'Activation passphrase:',
            activationPassphraseText: 'The pin needed to access the selected mobile device.',
            trackingIntervalStrong: 'Tracking interval:',
            trackingIntervalText: 'The interval in which a location record for the device is recorded.',
            locationAccuracyStrong: 'Location accuracy:',
            locationAccuracyText: 'The maximum accuracy with which the device position is recorded.'
          }
        },

        sendMessage: {
          heading: 'Send Message',
          enterMessage: 'Enter message',
          inProgressMsg: 'Sending message ...',
          successMsg: 'The message was successfully queued for sending to the device.',
          errorMsg: 'Error sending message.',
          messageSizeNote: 'Note: You have exceeded the maximum of {{maxMessageSize}} characters. Your message will be truncated',
          removeMessageAfter: 'Remove message after',
          timeOut: '(min:sec)',
          addCancelButton: 'Add Cancel button to message dialog',
          messageWrongTime: 'Wrong time format',
          buttons: {
            actionButtonLabel: 'Send Message'
          }
        },

        clearPasscode: {
          inProgressMsg: 'Clearing passcode ...',
          successMsg: 'The Clear Passcode command was queued successfully and will run the next time this device is online.',
          errorMsg: "Error Clearing Device Passcodes.",
          errorDetailsMsg: "An error occurred while clearing the device passcode: ",
          enterNewPasscode: 'Enter a new passcode',
          typeThePasscodeAgain: 'Type the passcode again',
          clearAllFields: 'Clear all fields',
          leaveAllFieldsEmpty: 'Leave all fields empty to clear the passcode.',
          actionButtonLabelClearPasscode: 'Clear Passcode',
          headingClearPasscode: 'Clear Passcode'
        },

        setPasscode: {
          inProgressMsg: 'Setting passcode ...',
          successMsg: 'The Set Passcode command was queued successfully and will run the next time this device is online.',
          errorMsg: "Error Setting Passcode.",
          errorDetailsMsg: "An error occurred while setting the device passcode: ",
          headingSetPasscode: 'Set Passcode',
          actionButtonLabelSetPasscode: 'Set Passcode'
        }

      }

    }
  },

  // Supported Locales
  // -----------------

  'ja': true
});
