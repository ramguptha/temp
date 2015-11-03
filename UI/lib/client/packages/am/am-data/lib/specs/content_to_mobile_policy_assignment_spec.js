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
      contentToMobilePolicyAssignmentId: { label: 'Id', format: Format.ID },
      policyName: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.policyName', format: Format.LongString },
      isSmartPolicy: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.isSmartPolicy', format: Format.Boolean },
      mediaFileAssignmentType: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.mediaFileAssignmentType', format: Format.EnumMediaFileAssignmentType },
      mediaFileAssignmentAvailability: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.mediaFileAssignmentAvailability', format: Format.EnumMediaFileAvailabilitySelector },
      mediaFileAssignmentStartTime: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.mediaFileAssignmentStartTime', format: Format.MediaFileAssignmentTime },
      mediaFileAssignmentEndTime: { labelResource: 'amData.contentToMobilePolicyAssignmentSpec.mediaFileAssignmentEndTime', format: Format.MediaFileAssignmentTime }
    },

    resource: [
      {
        attr: 'contentToMobilePolicyAssignmentId',
        guid: '60A0B825-A024-46CE-81CC-0ED5AB19DA4A',
        type: Number
      },
      {
        attr: 'policyName',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'isSmartPolicy',
        guid: 'F6917EE8-9F39-43F3-B8C9-D3C461170FE5',
        type: Boolean
      },
      {
        attr: 'mediaFileAssignmentType',
        guid: 'CD4E2B6E-96FD-4811-B675-82857B19E904',
        type: Number
      },
      {
        attr: 'mediaFileAssignmentAvailability',
        guid: '9DDF696F-F8A9-44A9-9F94-5A761E46905A',
        type: Number
      },
      {
        attr: 'mediaFileAssignmentStartTime',
        guid: '4DEE87F0-149D-4087-9FCD-51BA4EDE6E7A',
        type: String
      },
      {
        attr: 'mediaFileAssignmentEndTime',
        guid: 'E7EBAF1E-FACC-40F0-9AA5-20C7421ABBD4',
        type: String
      }
    ]
  }).create();
});
