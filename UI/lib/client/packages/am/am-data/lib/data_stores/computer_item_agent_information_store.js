define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_agent_information_spec',
  '../models/computer_item_agent_information'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerAgentInformationSpec,
  ComputerAgentInformationModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerAgentInformationModel,
    Spec: ComputerAgentInformationSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/agentinfo';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
