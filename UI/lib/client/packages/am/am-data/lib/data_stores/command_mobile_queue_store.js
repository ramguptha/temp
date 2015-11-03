define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/command_mobile_queue_spec',
  '../models/command_mobile_queue'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  CommandQueueSpec,
  CommandQueue
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: CommandQueue,
    Spec: CommandQueueSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) endPoint = '/api/commands/queued/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allqueuedcommands'
      });
    }
  });
});
