define([
  'ember',
  './query_base'
], function(
  Em,
  Query
) {
  'use strict';

  // SingletonQuery
  // ==============
  //
  // A SingletonQuery encapsulates criteria that will return at most a single object.
  // SingletonQuery inherits from [QueryBase](./query_base.html)
  var SingletonQuery = Query.extend({
    // Marker.
    isSingleton: true,

    // By default, SingletonQueries refresh automatically when the related DataStore is invalidated.
    autoRefresh: true,

    // This is the id of the queried object.
    id: null,

    uri: function() {
      return this.serialize('singleton', ['id']);
    }.property('isValid', 'context', 'store', 'store.name', 'id'),

    copy: function() {
      var names = this.get('baseNames');
      names.push('id');
      return SingletonQuery.create(this.getProperties(names));
    },

    performSearch: function(data) {
      return data.filter(function(obj) {
        return String(obj.get('id')) === String(this.get('id'));
      }, this);
    },

    performSort: function(data) {
      return data;
    },

    performPaging: function(data) {
      return data;
    }
  });

  return SingletonQuery;
});
