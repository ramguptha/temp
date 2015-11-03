define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/command_mobile_history_spec',
  '../models/command_mobile_history'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  CommandHistorySpec,
  CommandHistory
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: CommandHistory,
    Spec: CommandHistorySpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) endPoint = '/api/commands/history/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allcommandshistory'
      });
    }
  });
});
