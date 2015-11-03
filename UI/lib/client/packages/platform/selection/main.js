define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Selection
  // =========
  //
  // Implements selection behaviour. Components delegate their selection behaviour to a Selector.

  var Selector = Em.Object.extend({

    // Returns true if the id is now selected, false otherwise.
    updateOneSelection: function(selectedIds, allSelectableIds, id) {
      throw 'Implement me';
    },

    // Returns true is all ids are now selected, false otherwise.
    updateAllSelections: function(selectedIds, allSelectableIds, isAllSelected) {
      throw 'Implement me';
    },

    enabled: true,
    canSelectMultiple: false,
    canSelectAll: false
  });

  // Toggle selection of a single item
  var ToggleOne = Selector.extend({
    updateOneSelection: function(selectedIds, allSelectableIds, id) {
      var selected = selectedIds.contains(id);

      if (this.get('enabled')) {
        if (selected) {
          selectedIds.removeObject(id);

          selected = false;
        } else if (allSelectableIds.contains(id)) {
          selectedIds.beginPropertyChanges();
          selectedIds.clear();
          selectedIds.addObject(id);
          selectedIds.endPropertyChanges();

          selected = true;
        }
      }

      return selected;
    },

    updateAllSelections: Em.K
  });

  // Set selection of a single item
  var SetOne = Selector.extend({
    updateOneSelection: function(selectedIds, allSelectableIds, id) {
      var selected = selectedIds.contains(id);

      if (this.get('enabled')) {
        if (!selected && allSelectableIds.contains(id)) {
          selectedIds.beginPropertyChanges();
          selectedIds.clear();
          selectedIds.addObject(id);
          selectedIds.endPropertyChanges();

          selected = true;
        }
      }

      return selected;
    },

    updateAllSelections: Em.K
  });

  // Toggle selection of multiple items
  var ToggleMany = Selector.extend({
    updateOneSelection: function(selectedIds, allSelectableIds, id) {
      var selected = selectedIds.contains(id);

      if (this.get('enabled')) {
        if (selected) {
          selectedIds.removeObject(id);
        } else if (allSelectableIds.contains(id)) {
          selectedIds.addObject(id);
          selected = true;
        }
      }

      return selected;
    },

    updateAllSelections: function(selectedIds, allSelectableIds, isAllSelected) {
      if (this.get('enabled')) {

        // In case there are some ids that are filtered out and not in the context of allSelectableIds at the moment
        // and so they are not visible in the grid, we keep the original selectedIds before clearing it
        var hiddenSelectedIds = selectedIds.filter(function(id) {
          return !allSelectableIds.contains(id);
        });

        selectedIds.beginPropertyChanges();
        selectedIds.clear();

        if (!isAllSelected) {
          selectedIds.addObjects(allSelectableIds);
        }

        selectedIds.addObjects(hiddenSelectedIds);
        selectedIds.endPropertyChanges();

        isAllSelected = !isAllSelected;
      }

      return isAllSelected;
    },

    canSelectMultiple: true,
    canSelectAll: true
  });

  // Disable selection completely
  var Disabled = Selector.extend({
    updateOneSelection: function(selectedIds, allSelectableIds, id) {
      return selectedIds.contains(id);
    },

    updateAllSelections: function(selectedIds, allSelectableIds, isAllSelected) {
      return isAllSelected;
    },

    enabled: false,
    canSelectMultiple: false,
    canSelectAll: false
  });

  return {
    Selector: Selector,
    ToggleOne: ToggleOne,
    SetOne: SetOne,
    ToggleMany: ToggleMany,
    Disabled: Disabled
  };
});
