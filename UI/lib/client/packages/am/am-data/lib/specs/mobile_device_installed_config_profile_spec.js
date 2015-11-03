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
      name: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.name', format: Format.StringOrNA },
      description: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.description', format: Format.StringOrNA },
      organization: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.organization', format: Format.StringOrNA },
      profileType: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.profileType', format: Format.ShortStringOrNA },
      identifier: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.identifier', format: Format.LongStringOrNA },
      uuid: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.uuid', format: Format.UUIDOrNA },
      isEncrypted: {labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.isEncrypted', format: Format.BooleanOrNA },
      isManaged: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.isManaged', format: Format.BooleanOrNA },
      allowRemoval: { labelResource: 'amData.mobileDeviceInstalledConfigurationProfileSpec.allowRemoval', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A389FCDE-ACC1-434C-B5AE-84801632C0B5',
        type: Number
      },
      {
        attr: 'name',
        guid: 'B78AAB04-4384-431F-A473-C555DDC649DD',
        type: String
      },
      {
        attr: 'description',
        guid: 'B2D31F8A-BE85-442C-83B2-BA0E1579EBC6',
        type: String
      },
      {
        // The organization which has issued the configuration profile.
        attr: 'organization',
        guid: 'E2AE18C1-B9AC-49C0-89A3-B7F410038D37',
        type: String
      },
      {
        // The unique identifier of the configuration profile, as entered by the creator of the profile.
        attr: 'identifier',
        guid: '6AA7C2C9-C66B-47AE-8481-07C6D551CD4B',
        type: String
      },
      {
        // The automatically created globally unique ID of the configuration profile.
        attr: 'uuid',
        guid: '0B2D8180-E77E-4736-A329-F7CB83A5BB77',
        type: String
      },
      {
        // The type of profile – device profile or app profile. Device profiles contain settings for the hardware or
        // operating system, app profiles contain settings for individual apps.
        attr: 'profileType',
        guid: '52B11D85-3AA2-4034-ACAB-958DFF81B6E1',
        type: String
      },
      {
        // Whether the profile is encrypted or not.
        attr: 'isEncrypted',
        guid: '4543AD1C-A764-4288-B672-110EE7A9A548',
        type: Boolean
      },
      {
        // Whether the profile is managed.
        attr: 'isManaged',
        guid: 'A3EAFEBA-833A-4F7B-AA66-74FC11A669A3',
        type: Boolean
      },
      {
        // Whether the profile may be removed from the device by the user.
        // Possible values include, “Never“, “Always“, and “With authentication“.
        attr: 'allowRemoval',
        guid: '7E977334-7B48-4EA3-808A-3357DE876B2E',
        type: String
      }
    ],

    searchableNames: 'name description organization identifier profileType uuid isEncrypted isManaged allowRemoval'.w()
  }).create();
});
