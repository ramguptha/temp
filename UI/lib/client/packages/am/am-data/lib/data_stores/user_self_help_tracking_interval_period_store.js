define([
  'ember',
  'packages/platform/data',
  '../models/user_self_help_tracking_interval_period',
  '../specs/user_self_help_tracking_interval_period_spec'
], function(
  Em,
  AbsData,
  Model,
  Spec
  ) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: Model,
    Spec: Spec,
    MockData: [
      { id: 0, intervalPeriodTitle: 'Seconds' },
      { id: 1, intervalPeriodTitle: 'Minutes' },
      { id: 2, intervalPeriodTitle: 'Hours' },
      { id: 3, intervalPeriodTitle: 'Days' }
    ]
  });
});
