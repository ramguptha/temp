define([
  'ember',
  'packages/platform/data',
  '../specs/content_from_mobile_device_spec'
], function(
  Em,
  AbsData,
  AmContentFromMobileDeviceSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: AmContentFromMobileDeviceSpec
  });
});
