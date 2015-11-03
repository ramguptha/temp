define([
  'packages/platform/pager',
  'query'
], function(
  Pager,
  Query
) {
  'use strict';

  // Queried Pager
  // =============
  //
  // Provides the hooks to drive loading and resetting via a Query.Search.
  //
  // The pager uses a Query.Search (via the _searchQuery_ property) to determine what data to retrieve, and in
  // what order. Consumers may bind the _searchQuery_ (and are usually expected to do so). Data will be reloaded when
  // _searchQuery_ changes.

  return Pager.extend({
    SearchQuery: Query.Search,

    init: function() {
      this._super();

      // Observers
      this.getProperties('cachedSearchQueryUri searchQuery.observableUri'.w());
    },

    // The Query
    // ---------
    //
    // A Queried Pager stores a search query for sub-classes to use when retrieving and grouping data. When
    // the query URI changes, the content will reset and rendering will begin afresh.

    searchQuery: null,

    group: Em.computed.oneWay('searchQuery.group'),

    // In some cases (such as report config loading), the entire query is overwritten on refresh. Be extra careful
    // to avoid unneeded resets.
    cachedSearchQueryUri: null,

    cachedSearchQueryUriDidChange: function() {
      this.reset();
    }.observes('cachedSearchQueryUri'),

    searchQueryUriDidChange: function() {
      var uri = this.get('searchQuery.observableUri');
      if (this.get('cachedSearchQueryUri') !== uri) {
        this.set('cachedSearchQueryUri', uri);
      }
    }.observes('searchQuery.observableUri'),

    // Grouping
    // --------
    //
    // If grouping is being done client-side, sub-classes may retrieve the relevant attribute for grouping
    // from the query via _getGroupedAttrForParent()_. It will return the attribute name at the index
    // corresponding to the depth of the given parent node.

    getGroupedAttrForParent: function(groupedAttrNames, parentNode) {
      var depth = 0;

      while (!(parentNode.parentNode instanceof this.NodeTypes.Root)) {
        depth += 1;
        parentNode = parentNode.parentNode;
      }

      return groupedAttrNames[depth];
    }
  });
});
