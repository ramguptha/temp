define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobilePolicyStore', 'mobilePolicyFromMobileDeviceStore', 'mobilePolicyFromContentStore', 'mobileDevicePerformedActionsStore'],
    verb: 'post',

    description: 'Update an Existing Smart Policy',
    endPoint: function() {
      return 'policies/smart/' + this.get('oldPolicy.id');
    }.property('oldPolicy.id'),

    oldPolicy: null,
    newPolicyName: null,
    newPolicyFilters: null,

    toJSON: function() {
      return {
        name: this.get('newPolicyName').trim(),
        filterType: this.get('oldPolicy.filterType'),
        id: this.get('oldPolicy.id'),
        uniqueID: this.get('oldPolicy.guid'),
        seed: this.get('oldPolicy.seed'),
        smartPolicyUserEditableFilter: this.get('newPolicyFilters')
      }
    }
  });
});