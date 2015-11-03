define([
  'ember',
  'packages/platform/enum-util',
  '../transient_controller'
], function(
  Em,
  EnumUtil,
  TransientController
) {
  'use strict';

  return Em.Controller.extend(TransientController, {
    tSearch: 'shared.search.placeHolder'.tr(),

    headingIconClass: "icon-column-sort",
    heading: 'desktop.showHideColumns.title'.tr(),
    addModalClass: "show-hide-columns-window",

    searchCriteria: '',

    // Visible columns data provider for the right list
    visibleColumnNames: null,

    // All available columns data provider for the left list
    availableColumnNames: function() {
      var withoutVisibleColumnNames = EnumUtil.exclude(
        this.get('allColumnNames'), null, this.get('visibleColumnNames'), null
      );

      return this.filterByLabelAndSearchCriteria(this.get('searchCriteria'), withoutVisibleColumnNames);
    }.property('allColumnNames.[]', 'visibleColumnNames.[]', 'searchCriteria'),

    availableColumnLabels: function() {
      return this.get('availableColumnNames').map(this.name2Label);
    }.property('availableColumnNames.[]'),

    // This property is expected to include the pool of all available column names for chooser, 
    // including mandatoryColumnNames and groupedColumnNames.
    allColumnNames: null,

    groupedColumnNames: null,
    mandatoryColumnNames: null,

    isActionBtnDisabled: function() {
      var isDisabled = true;
      if (Em.isEmpty(this.get('visibleColumnNames'))) {
        isDisabled = true;
      } else {
        isDisabled = false;
      }
      return isDisabled;
    }.property('visibleColumnNames.[]'),

    commitChanges: function(router) {
      throw 'Implement me';
    },

    names2Labels: function(names) {
      return names.map(this.name2Label);
    },

    labels2Names: function(labels) {
      return labels.map(this.label2Name);
    },

    name2Label: function(colName) {
      var label = null;
      this.get('columnSpecs').forEach(function(spec) {
        if (spec.name === colName) {
          label = spec.label;
        }
      });

      if (label === null) {
        throw new Error('name2Label() --> Could not find the label for name: ' + colName);
      }

      return label;
    },

    label2Name: function(label) {
      var name = null;
      this.get('columnSpecs').forEach(function(spec) {
        if (spec.name === label) {
          name = spec.label;
        }
      });

      if (name === null) {
        throw new Error('name2Label() --> Could not find the name for label: ' + label);
      }

      return name;
    },

    filterByLabelAndSearchCriteria: function(searchCriteria, columnNames) {
      var self = this;

      if (Em.isEmpty(searchCriteria)) {
        return columnNames;
      }

      var pairs = columnNames.map(function(name) {
        return { name: name, label: self.name2Label(name) };
      });

      return pairs.filter(function(pair) {
        return pair.label.toLowerCase().indexOf(searchCriteria.toLowerCase()) !== -1;
      }).mapBy('name');
    }
  });
});
