define([
  'jquery',
  'ember',
  'packages/platform/storage'
], function(
  $,
  Em,
  Storage
  ) {
  'use strict';

  return Em.Object.extend({
    storageKey: null,

    defaultSettings: {
      width: 300,
      isCollapsed: false
    },

    validate: function(value) {
      if (null !== value &&
        typeof(value) === 'object' &&
        typeof(value.width) === 'number' &&
        typeof(value.isCollapsed) === 'boolean') {
        return value;
      }

      return null;
    },

    reset: function() {
      this.set('settings', $.extend({}, this.get('defaultSettings')));
    },

    settings: Em.computed({
      get: function(){
        var storageKey = this.get('storageKey');
        return this.validate(Storage.read(storageKey, true)) || this.get('defaultSettings');
      },
      set: function(key, value) {
        var storageKey = this.get('storageKey');
        Storage.write(storageKey, value, true);
        return value;
      }
    }),

    width: Em.computed('settings', {
      set: function(key, value) {
        var settings = this.get('settings');
        // Need a new object for Ember to register a change to the property
        this.set('settings', $.extend({}, settings, { width: value }));
        return value;
      },
      get: function() {
        return this.get('settings').width;
      }
    }),

    isCollapsed: Em.computed('settings', {
      set: function(key, value) {
        var settings = this.get('settings');
        // Need a new object for Ember to register a change to the property
        this.set('settings', $.extend({}, settings, { isCollapsed: value }));
        return value;
      },
      get: function() {
        return this.get('settings').isCollapsed;
      }
    })
  });
});