define([
  'ember',
  '../namespace',
  'am-data'
], function (
  Ember,
  AmMobileDevice,
  AmData
) {
  'use strict';

  return Em.Controller.extend({
    lock: null,
    content: null,

    loadUserInfo: function (deviceId) {
      var self = this;
      var mobileDeviceuserStore = AmData.get('stores.mobileDeviceUserStore');

      mobileDeviceuserStore.acquireOne(
        null, deviceId,
        function (dataSource) {
          var content = dataSource.get('content');
          self.set('model', content.get('length') === 0 ? null : content.objectAt(0).get('presentation'));
        }, 
        null, false, false
      );
    }
  });
});
