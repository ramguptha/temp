define([
  'jquery',
  'ember',
  '../am_composite_spec',
  './mobile_policy_spec',
  './content_to_mobile_policy_assignment_spec'
], function(
  $,
  Em,
  AmCompositeSpec,
  MobilePolicySpec,
  ContentToMobilePolicyAssignmentSpec
) {
  'use strict';

  return AmCompositeSpec.extend({
    backingSpecs: {
      mobilePolicy: MobilePolicySpec,
      contentToMobilePolicyAssignment: ContentToMobilePolicyAssignmentSpec
    },

    attributeMapping: [
      { name: 'id', map: 'mobilePolicy' },
      { name: 'guid', map: 'mobilePolicy' },
      { name: 'name', map: 'mobilePolicy' },

      { name: 'contentToMobilePolicyAssignmentId', map: 'contentToMobilePolicyAssignment' },
      { name: 'policyName', map: 'contentToMobilePolicyAssignment' },
      { name: 'isSmartPolicy', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentType', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentAvailability', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentStartTime', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentEndTime', map: 'contentToMobilePolicyAssignment' }
    ],

    searchableNames: 'name guid isSmartPolicy mediaFileAssignmentType mediaFileAssignmentAvailability mediaFileAssignmentStartTime mediaFileAssignmentEndTime'.w()
  }).create();
});
