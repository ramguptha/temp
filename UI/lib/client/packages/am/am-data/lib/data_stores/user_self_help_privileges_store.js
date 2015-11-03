define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../models/user_self_help_tracking_interval_period',
  '../specs/user_self_help_tracking_interval_period_spec'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  Model,
  Spec
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Model,
    Spec: Spec
  });
});
