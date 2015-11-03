define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobileDeviceCustomFieldStore'.w(),
    refreshDelay: 3500,

    description: 'Remove custom field data for a device',
    endPoint: 'customfields_mobiledevice/delete',

    deviceId: null,
    itemIds: null,

    toJSON: function() {
      var items = [];
      this.get('itemIds').forEach(function(item) {
        items.push({"id": item});
      });

      return {
        associations : [{
          deviceIds: [this.get('deviceId')],
          items: items
        }]
      };
    }
  });
});
