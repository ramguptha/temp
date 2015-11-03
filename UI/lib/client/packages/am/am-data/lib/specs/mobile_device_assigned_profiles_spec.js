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
    format: {
      id: Format.ID,
      profileName: { labelResource: 'amData.mobileDeviceAssignedProfilesSpec.profileName', format: Format.StringOrNA },
      policyName: { labelResource: 'amData.mobileDeviceAssignedProfilesSpec.policyName', format: Format.StringOrNA },
      availabilitySelector: { labelResource: 'amData.mobileDeviceAssignedProfilesSpec.availabilitySelector', format: Format.StringOrNA },
      profileStartTime: {labelResource: 'amData.mobileDeviceAssignedProfilesSpec.profileStartTime', format: Format.MediaFileAssignmentTime },
      profileEndTime: { labelResource: 'amData.mobileDeviceAssignedProfilesSpec.profileEndTime', format: Format.MediaFileAssignmentTime }
    },

    resource: [
      {
        attr: 'id',
        guid: 'C26CD124-C5C9-479F-BF4A-DA972862D002',
        type: Number
      },
      {
        attr: 'profileName',
        guid: '05167DCF-6C7A-4370-BF74-12224B0833A9',
        type: String
      },
      {
        attr: 'policyName',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'platformType',
        guid: '1E896098-CCC0-4909-810E-CF62B4397949',
        type: String
      },
      {
        attr: 'availabilitySelector',
        guid: '5E21C61C-B825-482A-980E-D2D7B8B127F1',
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
      }
    ],

    searchableNames: 'profileName policyName availabilitySelector profileStartTime profileEndTime'.w()
  }).create();
});

