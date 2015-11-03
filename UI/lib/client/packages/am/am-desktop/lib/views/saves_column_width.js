define([
  'ember',
  'packages/platform/storage'
], function(
  Em,
  Storage
) {
  'use strict';

  return Em.Mixin.create({
    actions: {
      resize: function(id) {
        this.updateColumnSettings(id);
      }
    },

    updateColumnSettings: function(id) {
      // Do not overwrite the existing settings. Only add the new setting.
      var column = this.get('visibleColumns').findBy('id', id);
      if (!column) { return; }

      var settings = this.get('settings') ? this.get('settings') : { columnsSettings: {} };

      if (!settings.columnsSettings) {
        settings['columnsSettings'] = {};
      }
      settings.columnsSettings[id] = column.get('width');
      this.set('settings', settings);
    },

    // Store column width
    settings: function(key, value) {
      var storageKey = this.get('storageKey') + this.get('id');

      if (2 === arguments.length) {
        Storage.write(storageKey, value, true);
        return value;
      } else {
        return this.validateStorage(Storage.read(storageKey, true));
      }
    }.property('id'),

    //Validate if column width is a number
    validateStorage: function(value) {
      if (Em.isNone(value)) {
        return null;
      }
      var columnsSettings = value.columnsSettings;
      if (typeof(value) === 'object' && typeof(columnsSettings) === 'object') {
        for (var key in columnsSettings) {
          if (typeof(columnsSettings[key]) !== 'number') {
            return null;
          }
        }
        return value;
      }
      return null;
    },

    createColumns: function(names) {
      var columns = this._super(names);

      // Check this specific grid in the local storage,
      // if any settings regarding the columns of this report is available, use them
      var settings = this.get('settings'), columnsSettings = {};

      if (!Em.isNone(settings) && !Em.isNone(settings.columnsSettings)) {
        columnsSettings = settings.columnsSettings;
      }

      columns.forEach(function(column, idx) {
        var name = column.get('name');

        // Get width from local storage, if available
        var width = columnsSettings[name] || column.get('width');

        column.set('width', width);
      });

      return columns;
    },

    visibleColumns: function() {
      return this.createColumns(this.get('visibleColumnNames'));
    }.property(
      'spec.resourceByName',
      'settings',
      'valueFormatter',
      'visibleColumnNames.[]'
    ),

    searchableColumns: function() {
      return this.createColumns(this.get('searchableColumnNames'));
    }.property(
      'spec.resourceByName',
      'settings',
      'valueFormatter',
      'searchableColumnNames.[]'
    )
  });
});