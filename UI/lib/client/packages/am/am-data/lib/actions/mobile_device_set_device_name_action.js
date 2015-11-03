define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    // It is a long time operation, so it is not enough time to refresh changed name. Even delay refresh does not help here.
    dependentDataStoreNames: 'mobileDeviceStore mobileDeviceItemStore'.w(),

    description : 'Set Name of Mobile Device',
    endPoint    : 'commands/setdevicename',
    deviceId    : null,
    name        : null,

    toJSON: function() {
        return {
            deviceId : this.get("deviceId"),
            name     : this.get('name')
        };
    }
  });
});
