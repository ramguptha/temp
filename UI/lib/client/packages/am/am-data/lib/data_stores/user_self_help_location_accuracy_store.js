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
      { id: 0, selectedLocationTitle: 'Best' },
      { id: 1, selectedLocationTitle: 'Nearest 10 meters' },
      { id: 2, selectedLocationTitle: 'Nearest 100 meters' },
      { id: 3, selectedLocationTitle: 'One kilometer' },
      { id: 4, selectedLocationTitle: 'Three kilometers' }
    ]
  });
});
