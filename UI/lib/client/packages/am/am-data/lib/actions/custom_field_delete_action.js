define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'actionsStore customFieldStore'.w(),
    refreshDelay: 2500,

    description: 'Delete Custom Fields',
    endPoint: 'customfields/delete',

    fieldIds: null,

    toJSON: function() {
      return {
        ids: this.get('fieldIds')
      };
    }
  });
});
