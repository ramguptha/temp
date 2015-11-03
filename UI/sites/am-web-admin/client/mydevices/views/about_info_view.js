define([
  'ember',
  'desktop',
  'text!../templates/about_info.handlebars'
], function(
  Em,
  Desktop,
  aboutInfoTemplate
) {
  return Em.View.extend({
    template: Em.Handlebars.compile(aboutInfoTemplate),
    layout: Desktop.get('ModalActionLayoutTemplate')
  });
});
