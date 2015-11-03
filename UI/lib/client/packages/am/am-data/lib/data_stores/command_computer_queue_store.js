define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/command_computer_queue_spec',
  '../models/command_computer_queue'
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
      if (query.isSearch) endPoint = '/api/computercommands/queued/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'commandhistoryforcomputers'
      });
    }
  });
});
