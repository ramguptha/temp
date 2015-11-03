define([
  'ember',
  'jquery',
  'formatter',
  'desktop',
  'text!../templates/smart_policy_create_smart_filter.handlebars'
], function(
    Em,
    $,
    Formatter,
    Desktop,
    template
    ) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
