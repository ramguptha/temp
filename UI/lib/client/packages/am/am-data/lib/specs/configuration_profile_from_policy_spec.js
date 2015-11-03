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

  return AmSpec.create({
    format: {
      id: Format.ID,
      name: { labelResource: 'amData.configurationProfileFromPolicySpec.name', format: Format.StringOrNA },
      description: { labelResource: 'amData.configurationProfileFromPolicySpec.description', format: Format.StringOrNA },
      organization: { labelResource: 'amData.configurationProfileFromPolicySpec.organization', format: Format.StringOrNA },
      osPlatform: { labelResource: 'amData.configurationProfileFromPolicySpec.osPlatform', format: Format.ShortStringOrNA },
      profileType: { labelResource: 'amData.configurationProfileFromPolicySpec.profileType', format: Format.ShortStringOrNA },
      identifier: { labelResource: 'amData.configurationProfileFromPolicySpec.identifier', format: Format.LongStringOrNA },
      uuid: {labelResource: 'amData.configurationProfileFromPolicySpec.uuid', format: Format.UUIDOrNA },
      allowRemoval: { labelResource: 'amData.configurationProfileFromPolicySpec.allowRemoval', format: Format.StringOrNA },
      variablesUsed: { labelResource: 'amData.configurationProfileFromPolicySpec.variablesUsed', format: Format.StringOrNA },
      availabilitySelector: { labelResource: 'amData.configurationProfileFromPolicySpec.availabilitySelector', format: Format.StringOrNA },
      availabilitySelectorNumeric: Format.Number,
      profileStartTime: { labelResource: 'amData.configurationProfileFromPolicySpec.profileStartTime', format: Format.MediaFileAssignmentTime },
      profileEndTime: { labelResource: 'amData.configurationProfileFromPolicySpec.profileEndTime', format: Format.MediaFileAssignmentTime },
      assignmentRule: { labelResource: 'amData.configurationProfileFromPolicySpec.assignmentRule', format: Format.StringOrNA },
      assignmentRuleNumeric: Format.EnumMediaFileAssignmentType
    },

    resource: [
      {
        attr: 'id',
        guid: 'C26CD124-C5C9-479F-BF4A-DA972862D002',
        type: Number
      },
      {
        attr: 'name',
        guid: '05167DCF-6C7A-4370-BF74-12224B0833A9',
        type: String
      },
      {
        attr: 'description',
        guid: '9EC5CC77-2976-417B-97BB-D2B3510EF34E',
        type: String
      },
      {
        // The organization which has issued the configuration profile.
        attr: 'organization',
        guid: 'FEB99186-5565-46ED-9C63-79C9112EEF4B',
        type: String
      },
      {
        // The unique identifier of the configuration profile, as entered by the creator of the profile.
        attr: 'identifier',
        guid: 'BB46E7ED-33F8-4AB7-84C6-928F5CF75F35',
        type: String
      },
      {
        // The automatically created globally unique ID of the configuration profile.
        attr: 'uuid',
        guid: '0E761FE2-59CF-4535-97C0-F31743FE231C',
        type: String
      },
      {
        // String value of the mobile OS platform to which this configuration profile applies.
        attr: 'osPlatform',
        guid: '1E896098-CCC0-4909-810E-CF62B4397949',
        type: String
      },
      {
        // Enumerated value of the mobile OS platform to which this configuration profile applies.
        attr: 'osPlatformEnum', // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
        guid: '5051ED56-E428-41CF-ABC7-78FBEADDD10A',
        type: Number
      },
      {
        // The type of profile – device profile or app profile. Device profiles contain settings for the hardware or
        // operating system, app profiles contain settings for individual apps.
        attr: 'profileType',
        guid: 'B091D31C-A60B-4D5E-814D-64CAEE93E62B',
        type: String
      },
      {
        // Whether the profile may be removed from the device by the user.
        // Possible values include, “Never“, “Always“, and “With authentication“.
        attr: 'allowRemoval',
        guid: '7C878B81-13F0-45D2-A021-51A58E77C7B8',
        type: String
      },
      {
        // A comma-separated list of variables that are used in this configuration profile.
        attr: 'variablesUsed',
        guid: '4AC2F645-7BD6-4423-A5AC-FD71254718B2',
        type: String
      },
      {
        attr: 'availabilitySelector',
        guid: '5E21C61C-B825-482A-980E-D2D7B8B127F1',
        type: Number
      },
      {
        attr: 'availabilitySelectorNumeric',
        guid: 'C82E9DA8-8237-4505-9CB0-F48F618D0A94',
        type: Number
      },
      {
        attr: 'profileStartTime',
        guid: '403D5844-93EA-4768-90EE-CF33B0CFE890',
        type: String
      },
      {
        attr: 'profileEndTime',
        guid: '8B580CF8-8F0D-4A05-BC83-659C243B540D',
        type: String
      },
      {
        attr: 'assignmentRule',
        guid: '43D0F8B1-EE69-473C-9927-87CE3E15EA04',
        type: String
      },
      {
        attr: 'assignmentRuleNumeric',
        guid: '885AF907-C2C6-42AA-AE09-FAC2FA2E1968',
        type: Number
      }
    ],

    searchableNames: 'name assignmentRule description organization identifier profileType uuid allowRemoval availabilitySelector profileStartTime profileEndTime'.w()
  });
});
