define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_custom_field_spec'
], function(
  Em,
  AbsData,
  MobileDeviceCustomFieldsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceCustomFieldsSpec
  });
});
