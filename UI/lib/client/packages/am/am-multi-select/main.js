define([
  'ember',
  'desktop',
  'am-data',

  './lib/controllers/in_house_application_multi_select_controller',
  './lib/controllers/third_party_application_multi_select_controller',
  './lib/controllers/mobile_configuration_profile_multi_select_controller',
  './lib/controllers/mobile_provisioning_profile_multi_select_controller',
  './lib/controllers/content_multi_select_controller',
  './lib/controllers/mobile_policy_multi_select_controller',
  './lib/controllers/mobile_device_multi_select_controller'
], function(
  Em,
  Desktop,
  AmData,

  AmInHouseApplicationMultiSelectController,
  AmThirdPartyApplicationMultiSelectController,
  AmMobileConfigProfileMultiSelectController,
  AmMobileProvProfileMultiSelectController,
  AmContentMultiSelectController,
  AmMobilePolicyMultiSelectController,
  AmMobileDeviceMultiSelectController
) {
  'use strict';

  return {
    InHouseAppMultiSelectController: AmInHouseApplicationMultiSelectController,
    ThirdPartyAppMultiSelectController: AmThirdPartyApplicationMultiSelectController,
    MobileConfigProfileMultiSelectController: AmMobileConfigProfileMultiSelectController,
    MobileProvProfileMultiSelectController: AmMobileProvProfileMultiSelectController,
    ContentMultiSelectController: AmContentMultiSelectController,
    MobilePolicyMultiSelectController: AmMobilePolicyMultiSelectController,
    MobileDeviceMultiSelectController: AmMobileDeviceMultiSelectController
  };
});
