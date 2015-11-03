define([
  'ember',
  'packages/platform/data',
  './spec_behaviour_mixin'
], function(
  Em,
  AbsData,
  SpecBehaviourMixin
) {
  'use strict';

  // AmSpec
  // ============
  // An extension of Spec
  return AbsData.Spec.extend(SpecBehaviourMixin);
});
