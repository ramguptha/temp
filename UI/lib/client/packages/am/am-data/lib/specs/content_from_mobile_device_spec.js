define([
  'jquery',
  'ember',
  '../am_composite_spec',
  './content_spec',
  './mobile_policy_spec',
  './content_to_mobile_policy_assignment_spec'
], function(
  $,
  Em,
  CompositeSpec,
  ContentSpec,
  MobilePolicySpec,
  ContentToMobilePolicyAssignmentSpec
) {
  'use strict';

  // The endpoint returns all
  // mobile policy to content assignments for the mobile policies related to the device.
  return CompositeSpec.extend({
    backingSpecs: {
      content: ContentSpec,
      mobilePolicy: MobilePolicySpec,
      contentToMobilePolicyAssignment: ContentToMobilePolicyAssignmentSpec
    },

    attributeMapping: [
      { name: 'id', map: 'contentToMobilePolicyAssignment.contentToMobilePolicyAssignmentId' },
      { name: 'contentId', map: 'content.id' },
      { name: 'name', map: 'content.name' },
      { name: 'mobilePolicyId', map: 'mobilePolicy.id' },
      { name: 'mobilePolicyName', map: 'mobilePolicy.name' },
      { name: 'isSmartPolicy', map: 'mobilePolicy' },
      { name: 'mediaFileAssignmentType', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentAvailability', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentStartTime', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentEndTime', map: 'contentToMobilePolicyAssignment' }
    ],

    idNames: 'mobilePolicyId contentId'.w(),

    searchableNames: 'name mobilePolicyName isSmartPolicy mediaFileAssignmentType mediaFileAssignmentAvailability mediaFileAssignmentStartTime mediaFileAssignmentEndTime'.w()
  }).create();
});
