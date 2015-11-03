define([
  'ember',
  'packages/platform/data',
  '../specs/assignable_item_menu_spec'
], function (
  Em,
  AbsData,
  AssignableItemMenuSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: AssignableItemMenuSpec
  });
});
