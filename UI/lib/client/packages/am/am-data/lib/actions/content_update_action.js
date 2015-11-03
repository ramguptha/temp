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

    description: 'Update Content',
    endPoint: function() {
      var content = this.get('content');
      return content ? 'content/' + content.get('id') : undefined;
    }.property('content.id'),

    content: null,

    toJSON: function() {
      return this.get('content');
    }
  });
});
