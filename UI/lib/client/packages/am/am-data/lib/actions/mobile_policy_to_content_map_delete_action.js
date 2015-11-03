define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobilePolicyFromContentStore contentFromMobilePolicyStore contentFromMobileDeviceStore'.w(),

    description: 'Remove Content from Mobile Policies',
    endPoint: 'policy_content/delete',

    contentIds: Em.A(),
    mobilePolicyIds: Em.A(),

    toJSON: function() {
      var contentIds = this.get('contentIds');
      var mobilePolicyIds = this.get('mobilePolicyIds');

      var associations = new Array(contentIds.length * mobilePolicyIds.length);
      for (var i = 0; i < contentIds.length; i++) {
        for (var j = 0; j < mobilePolicyIds.length; j++) {
          associations[i * mobilePolicyIds.length + j] = {
            contentId: Number(contentIds[i]),
            policyId: Number(mobilePolicyIds[j])
          };
        }
      }
      return { associations: associations };
    }
  });
});
