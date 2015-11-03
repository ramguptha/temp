/**
 * Created with JetBrains WebStorm.
 * User: dsimonov
 * Date: 14/05/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
define([
  'ember',
  'logger'
], function(
    Em,
    logger
    ) {
  'use strict';
  return Em.Object.extend({

    type: null,

    init: function() {
      this._super();
      this.get('type', 'am');
    },

    serialise: function(advancedQuery) {
      var URIString = this.get('type');
      return URIString;
    },

    deserialize: function(odataURIstring) {
      var query = {};
      return query;
    },

    toString: function() {
      return 'This is AM_ODATA uri converter';
    }
  })
});