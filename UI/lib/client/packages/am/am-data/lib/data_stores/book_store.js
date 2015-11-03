define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/book_spec',
  '../models/content'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  BookSpec,
  Content
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Content,
    Spec: BookSpec,

    createDataSourceForQuery: function (query) {
      var endPoint;
      if (query.isSearch) endPoint = '/api/books/views/all';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allinhouseapplications'
      });
    }
  });
});
