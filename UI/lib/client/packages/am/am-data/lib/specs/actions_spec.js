define([
  'ember',
  '../am_spec',
  '../am_formats'
], function (
  Em,
  AmSpec,
  Format
) {
  'use strict';

  return AmSpec.extend({
    REGISTER_USER_IN_VPP_ID: 11,
    SEND_VPP_INVITATION_ID: 16,

    format: {
      id: Format.ID,
      name: { labelResource: 'amData.actionsSpec.name', format: Format.StringOrNA },
      description: { labelResource: 'amData.actionsSpec.description', format: Format.StringOrNA },
      osPlatform: { labelResource: 'amData.actionsSpec.osPlatform', format: Format.StringOrNA },
      lastModified: { labelResource: 'amData.actionsSpec.lastModified', format: Format.ShortDateTime },
      type: { labelResource: 'amData.actionsSpec.type', format: Format.StringOrNA },
      seed: Format.Number
    },

    resource: [
      {
        attr: 'id',
        guid: '9F4DA532-8D7B-45EB-B44A-38986A014CC5',
        type: Number
      },
      {
        // The unique identifier of the action.
        attr: 'uuid',
        guid: '97347DCA-E4E0-4E4A-851F-ABDC5F50C3C7',
        type: String
      },
      {
        // The name of the action.
        attr: 'name',
        guid: '79285092-767A-4868-BEFA-5E4E84D6C97D',
        type: String
      },
      {
        // The description of the action, as entered by the administrator.
        attr: 'description',
        guid: '50A21BF5-A629-471D-9842-14702ED0039F',
        type: String
      },
      {
        // The numeric value of the operating system family for the action.
        // 0=none, 1=iOS, 2=Android, 3=iOS, Android, 4=Windows Phone,
        // 5=iOS, Windows Phone, 6=Android, Windows Phone, 7=iOS, Android, Windows Phone
        attr: 'osPlatformEnum',
        guid: 'CE2772D7-7CAA-47CF-8528-7FEAC802C1F5',
        type: Number
      },
      {
        // The mobile operating systems on which this action can be performed.
        attr: 'osPlatform',
        guid: '35A57B5B-D027-49E4-B39E-A729236BCFDD',
        type: String
      },
      {
        // The numeric value of the action type.
        // 1=Send Message To Device Action, 2=Set Roaming Options Action, 3=Send E-Mail Action,
        // 4=Demote to Unmanaged Device Action, 5=Remove Configuration Profile Action, 6=Send SMS (Text Message) Action,
        // 7=Freeze Device Action, 8=Update Device Information Action, 9=Set Wallpaper Action,
        // 10=Set Activation Lock Options Action, 11=Register User in VPP Action, 12=Retire User from VPP Action,
        // 13=Set Device Name Action, 14=Set Custom Field Value Action, 15=Attention Mode Action, 15=Send Vpp Invitation Action.
        attr: 'typeEnum',
        guid: '6357957A-6D6A-4454-98CA-A80AD4FD730F',
        type: Number
      },
      {
        // The type of the action, e.g., send message or freeze device.
        attr: 'type',
        guid: 'F6DC6AE5-4416-493A-97BC-95878906CF18',
        type: String
      },
      {
        // The last date/time this action was edited.
        attr: 'lastModified',
        guid: '5F132F1F-C298-46F1-BE8D-5BDD90D7F734',
        type: Date
      },
      {
        // The type of the action, e.g., send message or freeze device.
        attr: 'propertyList',
        guid: 'EE990E6-835D-4A04-8404-91EE2612DCDA',
        type: String
      },
      {
        // The version number of this record.
        attr: 'seed',
        guid: 'BC7F6947-C1F0-4F44-8369-27FC47DEB302',
        type: Number
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the result set.
      var result = this._super(query, rawData);

      result.forEach(function(raw) {
        var propertyList = raw.propertyList;

          if (propertyList) {
            switch (raw.typeEnum) {
              case 1:
                // Send Message
                raw.message = propertyList.MessageText;
                break;

              case 3:
                // Send Email
                raw.emailTo = propertyList.EmailTo;
                raw.emailCc = propertyList.EmailCC;
                raw.emailSubject = propertyList.EmailSubject;
                raw.emailMessage = propertyList.EmailMessageText;
                break;

              case 6:
                // Send SMS
                raw.smsMessage = propertyList.SMSMessageText;
                raw.phoneNumber = propertyList.SMSPhonenumber;
                break;

              case 2:
                // Set Roaming Options
                raw.dataRoaming = propertyList.DataRoaming.toString();
                raw.voiceRoaming = propertyList.VoiceRoaming.toString();
                break;

              case 10:
                // Set Activation Lock
                raw.lock = propertyList.ChangeActivationLock.toString();
                break;

              case 9:
                // Set Wallpaper
                raw.lockScreenChecked = propertyList.ApplyToLockScreen;
                raw.homeScreenChecked = propertyList.ApplyToHomeScreen;
                break;

              case 13:
                // Update Device Info
                raw.deviceName = propertyList.DeviceName;
                break;

              case 14:
                // Set Custom Fields
                raw.dataType = propertyList.DataType;
                raw.dataValue = propertyList.DataValue;
                raw.dataValueHigh32 = propertyList.DataValueHigh32;
                raw.dataValueLow32 = propertyList.DataValueLow32;
                raw.fieldId = propertyList.FieldID;
                raw.fieldName = propertyList.Name;
                raw.removeValue = propertyList.RemoveValue.toString();
                break;

              case 15:
                // Attention Mode
                raw.attentionMode = propertyList.AttentionModeEnabled.toString();
                raw.lockScreenMessage = propertyList.LockScreenMessage;
                break;

              case 7:
                // Freeze Device
                raw.passphrase = propertyList.Password;
                break;

              case 16:
              case 11:
                // Register User
                // Send VPP Invitation
                raw.inviteSubject = propertyList.InviteSubject;
                raw.inviteMessage = propertyList.InviteMessage;
                raw.inviteSMSMessage = propertyList.InviteSMSMessage;

                raw.mdmChecked = propertyList.SendInviteViaMDM;
                raw.clipChecked = propertyList.SendInviteViaWebClip;
                raw.smsChecked = propertyList.SendInviteViaSMS;
                raw.emailChecked = propertyList.SendInviteViaEmail;
                raw.absMessageChecked = propertyList.SendInviteViaAbsoluteApps;

                raw.vppRecordId = propertyList.VPPAccountRecordID;
                raw.vppUniqueId = propertyList.VPPAccountUniqueID;
                break;

              case 12:
                // Retire User
                raw.vppRecordId = propertyList.VPPAccountRecordID;
                raw.vppUniqueId = propertyList.VPPAccountUniqueID;
                break;

              case 5:
                // Remove Configuration Profile
                raw.profileId = propertyList.PayloadIdentifier;
                break;
            }
          }
      });

      return result;
    },

    searchableNames: 'name lastModified description osPlatform type'.w()
  }).create();
});
