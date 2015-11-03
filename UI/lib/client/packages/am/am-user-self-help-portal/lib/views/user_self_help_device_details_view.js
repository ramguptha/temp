define([
  'ember',
  'text!../templates/user_self_help_device_details.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    willDestroyElement: function() {
      Em.$(".tipsy").remove();
      this._super();
    }
  });
});
