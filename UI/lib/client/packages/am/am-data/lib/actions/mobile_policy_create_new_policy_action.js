define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobilePolicyStore', 'mobilePolicyFromMobileDeviceStore', 'mobilePolicyFromContentStore'],

    description : 'Create New Fixed Policy',
    endPoint: 'policies/standard',
    name: null,

    toJSON: function() {
      return {  name: this.get('name') };
    }
  });
});
