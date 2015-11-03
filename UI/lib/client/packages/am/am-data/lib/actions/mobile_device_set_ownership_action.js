define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({

    description : 'Set Ownership of Mobile Devices',
    endPoint    : 'commands/setownership',
    deviceIds   : null,
    ownership   : null,

    toJSON: function() {
      var devs = this.deviceIds.map(function (id) { return Number(id); });
      var ownership = this.get('ownership');
      return { deviceIds: devs, ownershipType: ownership };
    }
  });
});
