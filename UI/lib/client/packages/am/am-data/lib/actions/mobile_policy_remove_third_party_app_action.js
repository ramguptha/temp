define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'thirdPartyApplicationFromPolicyStore'.w(),

    description: 'Remove Third-Party App from Mobile Policies',
    endPoint: 'policy_thirdpartyapp/delete',

    toJSON: function () {
      var thirdPartyAppIds = this.get('thirdPartyAppIds');
      var mobilePolicyIds = this.get('policyId');

      var associations = new Array(thirdPartyAppIds.length * mobilePolicyIds.length);
      for (var i = 0; i < thirdPartyAppIds.length; i++) {
        for (var j = 0; j < mobilePolicyIds.length; j++) {
          associations[i * mobilePolicyIds.length + j] = {
            thirdPartyAppId: Number(thirdPartyAppIds[i]),
            policyId: Number(mobilePolicyIds[j])
          };
        }
      }
      return { associations: associations };
    }
  });
});
