define([
  'ember',
  'help',
  'desktop'
], function(
  Em,
  Help,
  Desktop
) {
  'use strict';

  return Desktop.ModalColumnChooserController.extend( {
    actions: {
      // Update the visibleColumnNames on the listController and the adhocSearchableNames on the query accordingly
      done: function() {
        var visibleColumnNames = this.get('visibleColumnNames');
        var listController = this.get('listController');

        listController.set('visibleColumnNames', visibleColumnNames);
        var spec = listController.get('dataStore.Spec');
        listController.set('searchQuery.adhocSearchableNames', spec.filterSearchableNames(visibleColumnNames));

        this.send('closeModal');
      },
      close: function() {
        this.send('closeModal');
      }
    },

    displayClose: true,

    listController: Em.computed.alias('model.listController'),

    urlForHelp: Help.uri(1044),

    columns: function() {
      return this.get('listController.columns');
    }.property('listController.columns.[]'),

    mandatoryNames: function() {
      return this.get('listController.dataStore.Spec.mandatoryNames');
    }.property('listController.dataStore.Spec.mandatoryNames'),

    availableColumnLabels: function() {
      return this.names2Labels(this.get('availableColumnNames'));
    }.property('availableColumnNames'),

    name2Label: function(colName) {
      var FieldConfig = this.get('listController').FieldConfig;
      var spec = this.get('listController.spec');
      var label;

      spec.get('names').forEach(function(name) {
        if (name === colName) {
          label = FieldConfig.create({
            id: name,
            spec: spec
          }).renderLabel();
        }
      });

      if (label === null) {
        throw new Error('name2Label() --> Could not find the name for label: ' + label);
      }

      return label;
    },

    label2Name: function(label) {
      var name = null;
      this.get('columns').forEach(function(column) {
        if (column.renderLabel() === label) {
          name = column.get('id');
        }
      });

      if (name === null) {
        throw new Error('name2Label() --> Could not find the name for label: ' + label);
      }

      return name;
    },

    onShowModal: function() {
      var listController = this.get('model.listController');

      this.setProperties({
        allColumnNames: listController.get('dataStore.Spec.searchableNames'),
        visibleColumnNames: listController.get('visibleColumnNames')
      });
    }
  });
});
