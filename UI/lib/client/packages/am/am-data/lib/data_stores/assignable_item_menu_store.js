define([
  'ember',
  'packages/platform/data',
  '../models/assignable_item_menu',
  '../specs/assignable_item_menu_spec'
], function (
  Em,
  AbsData,
  AssignableItemMenu,
  AssignableItemMenuSpec
  ) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: AssignableItemMenu,
    Spec: AssignableItemMenuSpec,
    MockData: [
      {
        id: 'content',
        ordering: 0,
        name: 'amData.assignableItemMenuStore.content'.tr(),
        route: 'am_assignable_list.content'
      },
      {
        id: 'inHouseApps',
        ordering: 1,
        name: 'amData.assignableItemMenuStore.inHouseApplications'.tr(),
        route: 'am_assignable_list.in_house_apps'
      },
      {
        id: 'thirdPartyApps',
        ordering: 2,
        name: 'amData.assignableItemMenuStore.thirdPartyApplications'.tr(),
        route: 'am_assignable_list.third_party_apps'
      },
      {
        id: 'books',
        ordering: 3,
        name: 'amData.assignableItemMenuStore.bookstoreBooks'.tr(),
        route: 'am_assignable_list.books'
      },
      {
        id: 'configProfiles',
        ordering: 4,
        name: 'amData.assignableItemMenuStore.configurationProfiles'.tr(),
        route: 'am_assignable_list.config_profiles'
      },
      {
        id: 'provisioningProfiles',
        ordering: 5,
        name: 'amData.assignableItemMenuStore.provisioningProfiles'.tr(),
        route: 'am_assignable_list.provisioning_profiles'
      },
      {
        id: 'actions',
        ordering: 6,
        name: 'amData.assignableItemMenuStore.actions'.tr(),
        route: 'am_assignable_list.actions'
      }
    ]
  });
});
