define([
  'ember',
  '../am_spec',
  '../am_formats'
], function(
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.extend({
    format: {
      id: Format.ID,
      name: { labelResource: 'amData.configurationProfileSpec.name', format: Format.StringOrNA },
      description: { labelResource: 'amData.configurationProfileSpec.description', format: Format.StringOrNA },
      organization: { labelResource: 'amData.configurationProfileSpec.organization', format: Format.StringOrNA },
      osPlatform: { labelResource: 'amData.thirdPartyApplicationsSpec.osPlatform', format: Format.SearchableIcon },
      profileType: { labelResource: 'amData.configurationProfileSpec.profileType', format: Format.ShortStringOrNA },
      identifier: { labelResource: 'amData.configurationProfileSpec.identifier', format: Format.LongStringOrNA },
      uuid: {labelResource: 'amData.configurationProfileSpec.uuid', format: Format.UUIDOrNA },
      allowRemoval: { labelResource: 'amData.configurationProfileSpec.allowRemoval', format: Format.ShortStringOrNA },
      variablesUsed: { labelResource: 'amData.configurationProfileSpec.variablesUsed', format: Format.StringOrNA }
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
      }
    ],

    searchableNames: 'name description organization osPlatform profileType identifier uuid allowRemoval variablesUsed'.w()
  }).create();
});
