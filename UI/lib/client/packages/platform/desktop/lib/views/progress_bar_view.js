define([
  'ember',
  'text!../templates/progress_bar.handlebars'
], function(
  Em,
  progressBarTemplate
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(progressBarTemplate)
  });
});
