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

  return AmSpec.create({
    idNames: 'appName policyName'.w(),

    format: {
      appName: { labelResource: 'amData.mobilePolicyAssignedApsSpec.appName', format: Format.StringOrNA },
      policyName: { labelResource: 'amData.mobilePolicyAssignedApsSpec.policyName', format: Format.StringOrNA },
      rule: { labelResource: 'amData.mobilePolicyAssignedApsSpec.rule', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: '3ac26070-b7eb-47f6-8a02-308aab8e931a',
        type: Number
      },
      {
        attr: 'appName',
        guid: '89450ED3-7B11-41F8-AF11-AEE369CD26B8',
        type: String
      },
      {
        attr: 'policyName',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'rule',
        guid: '728959A8-FFF8-416C-BC8E-C8D84EE13C5D',
        type: String
      },
      {
        attr: 'platformType',
        guid: 'FB49385A-C934-4603-A0CA-A2AA80D4F168',
        type: String
      },
      {
        attr: 'minOsVersion',
        guid: '3BC895F6-034C-428F-958E-415A3C585246',
        type: Number
      }
    ],

    searchableNames: 'appName policyName rule'.w()
  });
});
