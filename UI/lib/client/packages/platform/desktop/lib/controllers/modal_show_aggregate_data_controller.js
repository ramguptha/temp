define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  return Em.Controller.extend({
    modalWindowClass: 'modal-window-generic',
    title: null,
    subTitle: null,
    count: null,

    // Header of the columns
    columnHeaders: Em.A(),

    // List of records
    aggregateContent: {},

    onShowModal: function(options) {
      var option = options.objectAt(0);

      // Get the related data's spec from the parent spec
      var subSpec = option.spec.type.get('Spec');
      var columnNames = subSpec.get('names');
      var columnHeaders = columnNames.map(function(field) {
        return subSpec.getPresentationForAttr(field).label;
      });

      // Get the content of this aggregate field of the selected id
      var aggregateContent = {
        rows: option.columnData.map(function(data) {
          return columnNames.map(function(name) {
            return data.get(name);
          });
        })
      };

      this.setProperties({
        title: option.title,
        subTitle: null,
        columnHeaders: columnHeaders,
        aggregateContent: aggregateContent,
        count: aggregateContent.rows.length
      });
    }
  });
});
