define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: [],

    description: 'Send message to Computers',
    endPoint: 'computercommands/sendmessage',

    serialNumbers: null,
    message: null,

    toJSON: function() {
      return {
        serialNumbers: this.get('serialNumbers').map(function(serialNumber) {
          return serialNumber.toString();
        }),
        message: this.get('message')
      };
    }
  });
});
