define([
  'ember',
  'text!../templates/modal_action_status.handlebars',
  'text!../templates/modal_action_layout.handlebars',
  './keyboard_events_mixin'
], function(
  Em,
  modalActionStatusTemplate,
  modalActionLayoutTemplate,
  KeyboardEventsMixin
) {
  'use strict';

  return Em.View.extend(KeyboardEventsMixin, {
    defaultTemplate: Em.Handlebars.compile(modalActionStatusTemplate),
    layout: Em.Handlebars.compile(modalActionLayoutTemplate),

    setFocus: function() {
      Ember.run.scheduleOnce('afterRender', this, function(){
        var el = this.$('button.btn-action');
        if (el) {
          el.focus();
        }
      });
    }.observes('controller.showOkBtn')
  });
});
