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

    description: 'Add/edit custom field data for a device',
    endPoint: 'customfields_mobiledevice',

    deviceId: null,
    items: null,

    toJSON: function() {
      var items = [];
      this.get('items').forEach(function(item) {
        items.push({
          'id': item.id,
          'type': item.type,
          'value': item.value,
          'valueHigh32': item.valueHigh32,
          'valueLow32': item.valueLow32
        });
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
