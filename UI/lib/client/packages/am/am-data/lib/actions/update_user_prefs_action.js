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

    description : 'Update User Preferences',
    endPoint    : 'user/prefs/???',
    value       : null,

    toJSON: function() {
        return this.value;
    }
  });
});
