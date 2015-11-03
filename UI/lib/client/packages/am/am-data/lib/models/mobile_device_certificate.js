define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_certificate_spec'
], function(
  Em,
  AbsData,
  MobileDeviceCertificateSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceCertificateSpec
  });
});
