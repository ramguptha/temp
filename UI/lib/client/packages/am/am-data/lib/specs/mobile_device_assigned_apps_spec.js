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
    idNames: 'appName policyName'.w(),
    
    format: {
      appName: { labelResource: 'amData.mobileDeviceAssignedApsSpec.appName', format: Format.StringOrNA },
      policyName: { labelResource: 'amData.mobileDeviceAssignedApsSpec.policyName', format: Format.StringOrNA },
      rule: { labelResource: 'amData.mobileDeviceAssignedApsSpec.rule', format: Format.StringOrNA },
      icon: { labelResource: 'amData.mobileDeviceAssignedApsSpec.icon', format: Format.Icon }
    },

    resource: [
      {
        attr: 'id',
        guid: 'f8a332e7-90db-4483-a9d3-bf9c0a982ac2',
        type: Number
      },
      {
        attr: 'appName',
        guid: '8DF6E81A-0FEF-446B-8403-301D2A4CC066',
        type: String
      },
      {
        attr: 'policyName',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'rule',
        guid: '3CDDA1CB-D62E-4F5B-A29D-5CCFE1CDA013',
        type: String
      },
      {
        attr: 'platformType',
        guid: 'DC5A1403-C78F-4B72-8206-05CC525B975B',
        type: String
      },
      {
        attr: 'minOsVersion',
        guid: '605E53AD-01F3-41DA-8760-4E7C924C9C2E',
        type: Number
      },
      {
        // This has to be populated by a query to a separate endpoint /api/thirdpartyapps/{id}/icon
        attr: 'icon',
        guid: 'XXX',
        type: String
      }
    ],

    searchableNames: 'appName icon policyName rule'.w()
  }).create();
});
