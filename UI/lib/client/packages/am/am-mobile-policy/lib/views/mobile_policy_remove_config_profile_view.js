define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_policy_remove_config_profile.handlebars',
], function (
  Em,
  Desktop,
  UI,
  removeConfigProfileTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(removeConfigProfileTemplate),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
