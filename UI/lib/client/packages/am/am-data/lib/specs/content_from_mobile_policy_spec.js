define([
  'ember',
  '../am_composite_spec',
  './content_spec',
  './content_to_mobile_policy_assignment_spec'
], function(
  Em,
  AmCompositeSpec,
  ContentSpec,
  ContentToMobilePolicyAssignmentSpec
) {
  'use strict';

  return AmCompositeSpec.extend({
    backingSpecs: {
      content: ContentSpec,
      contentToMobilePolicyAssignment: ContentToMobilePolicyAssignmentSpec
    },

    attributeMapping: [
      { name: 'id', map: 'content' },
      { name: 'name', map: 'content' },
      { name: 'description', map: 'content' },
      { name: 'mediaFileName', map: 'content' },
      { name: 'type', map: 'content' },
      { name: 'mediaCategory', map: 'content' },
      { name: 'modified', map: 'content' },
      { name: 'mediaFileSize', map: 'content' },
      { name: 'wifiOnly', map: 'content' },
      { name: 'canLeaveAbsSafe', map: 'content' },
      { name: 'canBeEmailed', map: 'content' },
      { name: 'canBePrinted', map: 'content' },
      { name: 'hashedPassword', map: 'content' },
      { name: 'guid', map: 'content' },
      { name: 'seed', map: 'content' },

      { name: 'contentToMobilePolicyAssignmentId', map: 'contentToMobilePolicyAssignment' },
      { name: 'policyName', map: 'contentToMobilePolicyAssignment' },
      { name: 'isSmartPolicy', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentType', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentAvailability', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentStartTime', map: 'contentToMobilePolicyAssignment' },
      { name: 'mediaFileAssignmentEndTime', map: 'contentToMobilePolicyAssignment' },
    ],

    // TO ADD: "contentToMobilePolicyAssignmentId"
    searchableNames: 'name mediaCategory canLeaveAbsSafe mediaFileSize mediaFileAssignmentType mediaFileAssignmentAvailability mediaFileAssignmentStartTime mediaFileAssignmentEndTime modified'.w()
  }).create();
});
