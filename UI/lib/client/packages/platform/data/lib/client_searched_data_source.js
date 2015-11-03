define([
  'ember',
  'query',
  './data_source'
], function(
  Em,
  Query,
  DataSource
) {
  'use strict';

  return DataSource.extend({
    guid: null,
    content: null,
    dataSource: null,

    init: function() {
      this._super();
      this.setProperties('content', Em.A());
    },

    load: function() {
      var self = this;

      // NOTE NOTE NOTE: We do not call _super() here.

      this.get('dataSource').freshen(false, function(dataSource) {
        var query = self.get('query');
        var dataStore = self.get('query.store');

        var matchedContent = dataStore.performSearch(query, dataSource);
        var unpagedContent = dataStore.performSort(query, matchedContent);
        var pagedContent = dataStore.performPaging(query, unpagedContent);

        self.setProperties({
          content: pagedContent,
          isLastPage: unpagedContent.get('length') <= (query.get('limit') + query.get('offset'))
        });

        self.loadComplete();
      });
    },

    invalidatedAt: Em.computed.oneWay('dataSource.invalidatedAt'),
    loadFailedAt: Em.computed.oneWay('dataSource.loadFailedAt'),
    lastLoadError: Em.computed.oneWay('dataSource.lastLoadError')
  });
});
