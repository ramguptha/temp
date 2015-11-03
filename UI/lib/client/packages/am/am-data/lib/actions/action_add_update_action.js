define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  var propertyListToEndpointMap = {
    message: 'MessageText',

    emailTo: 'EmailTo',
    emailCc: 'EmailCC',
    emailSubject: 'EmailSubject',
    emailMessage: 'EmailMessageText',

    smsMessage: 'SMSMessageText',
    phoneNumber: 'SMSPhonenumber',

    dataRoaming: 'DataRoaming',
    voiceRoaming: 'VoiceRoaming',

    lock: 'ChangeActivationLock',

    lockScreenChecked: 'ApplyToLockScreen',
    homeScreenChecked: 'ApplyToHomeScreen',
    wallpaper: 'WallpaperPicture',

    deviceName: 'DeviceName',

    fieldId: 'FieldID',
    fieldName: 'Name',
    dataType: 'DataType',
    dataValue: 'DataValue',
    dataValueHigh32: 'DataValueHigh32',
    dataValueLow32: 'DataValueLow32',
    removeValue: 'RemoveValue',

    attentionMode: 'AttentionModeEnabled',
    lockScreenMessage: 'LockScreenMessage',

    passphrase: 'Password',

    inviteSubject: 'InviteSubject',
    inviteMessage: 'InviteMessage',
    inviteSMSMessage: 'InviteSMSMessage',
    mdmChecked: 'SendInviteViaMDM',
    clipChecked: 'SendInviteViaWebClip',
    smsChecked: 'SendInviteViaSMS',
    emailChecked: 'SendInviteViaEmail',
    absMessageChecked: 'SendInviteViaAbsoluteApps',

    vppRecordId: 'VPPAccountRecordID',
    vppUniqueId: 'VPPAccountUniqueID',

    profileId: 'PayloadIdentifier'
  };

  return AmAction.extend({
    dependentDataStoreNames: 'actionsStore actionsFromPolicyStore mobileDevicePerformedActionsStore'.w(),

    description: 'Create, Edit or Duplicate an action',
    endPoint: function() {
      var id = this.get('id');

      // Duplicate and Create actions would not have an id to pass
      return 'actions' + (id ? '/' + id : '') ;
    }.property('id'),

    verb: 'post',

    // Properties required to be posted for editing an action
    id: null,
    seed: null,

    name: null,
    actionDescription: null,
    typeEnum: null,
    osPlatformEnum: null,
    propertyList: null,

    toJSON: function() {
      var attrs = this.getProperties('id seed name actionDescription typeEnum osPlatformEnum propertyList'.w());

      var actionData = {};
      var propertyList = attrs.propertyList;

      for (var property in propertyList) {
        if (propertyList.hasOwnProperty(property)) {
          var mappedProperty = propertyListToEndpointMap[property];

          if (mappedProperty) {
            var value = propertyList[property];

            if(typeof value === 'string') {
              value = value.trim();
            }

            actionData[mappedProperty] = value;
          }
        }
      }

      var data =  {
        name: attrs.name.trim(),
        description: attrs.actionDescription.trim(),
        actionType: attrs.typeEnum,
        supportedPlatforms: attrs.osPlatformEnum,
        actionData: actionData
      };

      // Only editing an action will pass the seed to the endpoint
      if (attrs.seed) {
        data['seed'] = attrs.seed;
      }

      return data;
    }
  });
});
