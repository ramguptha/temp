define([
  'ember',
  'packages/platform/data',
  '../specs/in_house_application_spec'
], function(
  Em,
  AbsData,
  InHouseApplicationSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: InHouseApplicationSpec
  });
});
