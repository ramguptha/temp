define([
  'ember'
], function(
  Em
) {

  'use strict';

  return Em.ObjectProxy.extend({
    dataSource: null,

    content: function() {
      var dataSource = this.get('dataSource');
      return Em.isNone(dataSource) ? undefined : dataSource.objectAt(0);
    }.property('dataSource', 'dataSource.[]'),

    release: function(owner) {
      this.get('dataSource').release(owner);
    },

    freshen: function(force, loadedCallback, loadFailedCallback, callbackScope) {
      return this.get('dataSource').freshen(
        force,
        function(dataSource) { loadedCallback.call(callbackScope, this); },
        function(dataSource) { loadFailedCallback.call(callbackScope, this); },
        this
      );
    }
  })
});
