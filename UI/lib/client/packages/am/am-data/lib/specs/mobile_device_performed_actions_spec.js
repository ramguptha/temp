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
      name: { labelResource: 'amData.performedActionsSpec.name', format: Format.StringOrNA },
      description: { labelResource: 'amData.performedActionsSpec.description', format: Format.StringOrNA },
      type: { labelResource: 'amData.performedActionsSpec.type', format: Format.StringOrNA },
      policyName: { labelResource: 'amData.performedActionsSpec.policyName', format: Format.StringOrNA },
      executionTime: { labelResource: 'amData.performedActionsSpec.executionTime', format: Format.ShortDateTime },
      actionUniqueId: Format.ShortDateTime
    },

    resource: [
      {
        attr: 'id',
        guid: '45711BC5-D331-407F-9384-A89963167B5C',
        type: Number
      },
      {
        attr: 'actionId',
        guid: '9F4DA532-8D7B-45EB-B44A-38986A014CC5',
        type: Number
      },
      {
        attr: 'actionUniqueId',
        guid: '97347DCA-E4E0-4E4A-851F-ABDC5F50C3C7',
        type: String
      },
      {
        attr: 'name',
        guid: '79285092-767A-4868-BEFA-5E4E84D6C97D',
        type: String
      },
      {
        attr: 'description',
        guid: '50A21BF5-A629-471D-9842-14702ED0039F',
        type: String
      },
      {
        attr: 'type',
        guid: 'F6DC6AE5-4416-493A-97BC-95878906CF18',
        type: String
      },
      {
        attr: 'policyName',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'executionTime',
        guid: '4325B819-AED3-4560-B17F-2E50E5FD792E',
        type: Date
      }
    ],

    searchableNames: 'name description type policyName executionTime'.w()
  }).create();
});
