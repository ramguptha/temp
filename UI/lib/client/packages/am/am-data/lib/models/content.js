define([
  'ember',
  'packages/platform/data',
  '../specs/content_spec'
], function(
  Em,
  AbsData,
  AmContentSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: AmContentSpec
  });
});
