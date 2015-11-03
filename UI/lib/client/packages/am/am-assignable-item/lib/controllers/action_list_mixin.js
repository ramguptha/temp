define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Mixin.create({
    getActionsList: function() {
      var resource = 'amAssignableItem.assignableActionsPage.header.buttons.addActionMenu.options.',
        divider = 'amAssignableItem.assignableActionsPage.header.buttons.addActionMenu.divider';
      //var label = Locale.renderGlobals(action.label).toString();

      // type: 0 ones are for the sake of adding separator and group the various New Actions together
      return Em.A([
        {
          type: 1,
          labelResource: resource + 'sendMessage',
          actionName: 'newSendMessageAction',
          osPlatformEnum: 3
        },
        {
          type: 3,
          labelResource: resource + 'sendEmail',
          actionName: 'newSendEmailAction',
          osPlatformEnum: 7
        },
        {
          type: 6,
          labelResource: resource + 'sendSms',
          actionName: 'newSendSmsAction',
          osPlatformEnum: 7
        },
        {
          labelResource: divider,
          type: 0
        },
        {
          type: 2,
          labelResource: resource + 'setRoamingOptions',
          actionName: 'newSetRoamingAction',
          osPlatformEnum: 1
        },
        {
          type: 10,
          labelResource: resource + 'setActivationLockOptions',
          actionName: 'newSetActivationAction',
          osPlatformEnum: 1
        },
        {
          type: 9,
          labelResource: resource + 'setWallpaper',
          actionName: 'newSetWallpaperAction',
          osPlatformEnum: 1
        },
        {
          type: 13,
          labelResource: resource + 'setDeviceName',
          actionName: 'newSetDeviceNameAction',
          osPlatformEnum: 3
        },
        {
          type: 14,
          labelResource: resource + 'setCustomFieldValue',
          actionName: 'newSetCustomFieldAction',
          osPlatformEnum: 7
        },
        {
          type: 8,
          labelResource: resource + 'updateDeviceInfo',
          actionName: 'newUpdateDeviceInfoAction',
          osPlatformEnum: 7
        },
        {
          labelResource: divider,
          type: 0
        },
        {
          type: 15,
          labelResource: resource + 'attentionMode',
          actionName: 'newAttentionModeAction',
          osPlatformEnum: 3
        },
        {
          type: 7,
          labelResource: resource + 'freezeDevice',
          actionName: 'newFreezeDeviceAction',
          osPlatformEnum: 2
        },
        {
          labelResource: divider,
          type: 0
        },
        {
          type: 16,
          labelResource: resource + 'sendVppInvitation',
          actionName: 'newSendVppInvitationAction',
          osPlatformEnum: 1
        },
        {
          type: 11,
          labelResource: resource + 'registerUserInVpp',
          actionName: 'newRegisterUserAction',
          osPlatformEnum: 1
        },
        {
          type: 12,
          labelResource: resource + 'retireUserFromVpp',
          actionName: 'newRetireUserAction',
          osPlatformEnum: 1
        },
        {
          labelResource: divider,
          type: 0
        },
        {
          type: 5,
          labelResource: resource + 'removeConfigurationProfile',
          actionName: 'newRemoveConfigurationAction',
          osPlatformEnum: 1
        },
        {
          type: 4,
          labelResource: resource + 'demoteToUnmanagedDevice',
          actionName: 'newDemoteAction',
          osPlatformEnum: 3
        }
      ]);
    }
  });
});