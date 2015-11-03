define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/command_computer_detail_spec',
  '../models/command_computer_detail'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  CommandDetailSpec,
  CommandDetailModel
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: CommandDetailModel,
    Spec: CommandDetailSpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/computercommands';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
