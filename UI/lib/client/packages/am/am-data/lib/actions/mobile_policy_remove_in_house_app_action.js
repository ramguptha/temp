define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'inHouseApplicationFromPolicyStore'.w(),

    description: 'Remove In-House App from Mobile Policies',
    endPoint: 'policy_inhouseapp/delete',

    toJSON: function () {
      var inHouseAppIds = this.get('inHouseAppIds');
      var mobilePolicyIds = this.get('policyId');

      var associations = new Array(inHouseAppIds.length * mobilePolicyIds.length);
      for (var i = 0; i < inHouseAppIds.length; i++) {
        for (var j = 0; j < mobilePolicyIds.length; j++) {
          associations[i * mobilePolicyIds.length + j] = {
            inHouseAppId: Number(inHouseAppIds[i]),
            policyId: Number(mobilePolicyIds[j])
          };
        }
      }
      return { associations: associations };
    }
  });
});
