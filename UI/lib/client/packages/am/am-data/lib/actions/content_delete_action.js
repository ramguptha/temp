define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'contentStore contentFromMobilePolicyStore contentFromMobileDeviceStore'.w(),

    description: 'Delete Content',
    endPoint: 'content/delete',

    contentIds: null,

    toJSON: function() {
      return {
        contentIds: this.get('contentIds').map(function(id) { return Number(id); })
      };
    }
  });
});
