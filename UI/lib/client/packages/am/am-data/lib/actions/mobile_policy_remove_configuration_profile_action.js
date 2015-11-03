define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'configurationProfileFromPolicyStore'.w(),

    description: 'Remove Configuration Profile from Mobile Policies',
    endPoint: 'policy_configurationprofile/delete',

    toJSON: function () {
      var configProfileIds = this.get('configProfileIds');
      var mobilePolicyIds = this.get('policyId');

      var associations = new Array(configProfileIds.length * mobilePolicyIds.length);
      for (var i = 0; i < configProfileIds.length; i++) {
        for (var j = 0; j < mobilePolicyIds.length; j++) {
          associations[i * mobilePolicyIds.length + j] = {
            configurationProfileId: Number(configProfileIds[i]),
            policyId: Number(mobilePolicyIds[j])
          };
        }
      }
      return { associations: associations };
    }
  });
});
