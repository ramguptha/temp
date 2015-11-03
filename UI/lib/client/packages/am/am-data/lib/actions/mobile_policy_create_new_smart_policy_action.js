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

    description: 'Create New Smart Policy',
    endPoint: 'policies/smart',

    name: null,
    filterType: null,
    smartPolicyUserEditableFilter: null,

    toJSON: function() {
      return this.getProperties('name filterType smartPolicyUserEditableFilter'.w());
    }
  });
});