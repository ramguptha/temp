define([
  'ember',
  'text!../templates/mobile_device_settings.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    didInsertElement: function() {
      var self = this;
      $('.snap-container-content').on('click', '.title-collapsible', (function(event){
        var slidableElement = $(this).next('.content-collapsible');
        var keyTag = $(this).parent().attr("data-name").camelize();
        var controller = self.get('controller');

        if (slidableElement.is(':visible') ){
          controller.set('slideSettings.' + keyTag, false);
          slidableElement.slideUp();
        } else {
          controller.set('slideSettings.' + keyTag, true);
          slidableElement.slideDown();
        }
      }));
    },
    willDestroyElement: function(evt) {
      $('.snap-container-content').off();
    }
  });
});
