define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Id Mapping
  // ==========
  //
  // Maps Node Ids to DOM Ids and back.

  return Em.Object.extend({
    validateId: function(id, strict, description) {
      // Because ids get embedded into the DOM, we only allow certain formats
      var valid = ('string' === typeof(id)) && /^[ ,\w\.\-+=|\\\/:]+$/.test(id);

      if (!valid && strict) {
        throw ['Invalid ' + (description ? description : 'id'), id];
      }

      return valid;
    },

    getRowIdForNode: function(node) {
      return node.id;
    },

    getRowAutomationIdForNode: function(node) {
      return node.id;
    },

    getNodeIdForRow: function($row) {
      return $row.getAttribute('data-id');
    }
  });
});
