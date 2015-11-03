define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'commandComputerHistoryStore'.w(),
    refreshDelay: 2500,

    description: 'Delete Computer Command from History',
    endPoint: 'computercommands/history/delete',

    commandIds: null,

    toJSON: function() {
      return {
        commandIds: this.get('commandIds').map(function(id) { return Number(id); })
      };
    }
  });
});
