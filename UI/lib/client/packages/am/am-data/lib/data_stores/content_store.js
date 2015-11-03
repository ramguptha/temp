define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/content_spec',
  '../models/content'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  ContentSpec,
  Content
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Content,
    Spec: ContentSpec,

    createDataSourceForQuery: function(query) {
      var endPoint, pushEndpoint;

      if (query.isSearch) {
        endPoint = '/api/views/allmobilecontent';
        pushEndpoint = 'allmobilecontent';
      } else if (query.isSingleton) endPoint = '/api/content';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: pushEndpoint
      });
    }
  });
});
