define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'commandMobileHistoryStore'.w(),
    refreshDelay: 2500,

    description: 'Delete Command from History',
    endPoint: 'commands/history/delete',

    commandIds: null,

    toJSON: function() {
      return {
        commandIds: this.get('commandIds').map(function(id) { return Number(id); })
      };
    }
  });
});
