define([
  'packages/platform/counter',
  'query'
], function(
  Counter,
  Query
) {
  'use strict';

  // Queried Counter
  // ===============
  //
  // Provides the hooks to drive loading and resetting via a Query.SearchQuery.
  //
  // The counter uses a Query.SearchQuery (via the _searchQuery_ property) to determine what data to count.
  // Consumers may bind the _searchQuery_ (and are usually expected to do so). The count will be reloaded when
  // _searchQuery_ changes.

  return Counter.extend({
    SearchQuery: Query.Search,

    init: function() {
      this._super();

      // Observers
      this.getProperties('searchQuery.observableUri'.w());
    },

    // The Query
    // ---------
    //
    // A Queried Counter stores a search query for sub-classes to use when counting. When
    // the query URI changes, the total will be reset.

    searchQuery: Query.Search.create(),

    searchQueryUriDidChange: function() {
      // Remember, searchQuery.observableUri does not update synchronously, so don't expect it to.
      this.reset();
    }.observes('searchQuery.observableUri')
  });
});
