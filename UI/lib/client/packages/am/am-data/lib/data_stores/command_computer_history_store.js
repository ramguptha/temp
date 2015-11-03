define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/command_computer_history_spec',
  '../models/command_computer_history'
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
      if (query.isSearch) endPoint = '/api/computercommands/history/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'queuedcommandsforcomputers'
      });
    }
  });
});
