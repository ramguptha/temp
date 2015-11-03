define([
  'jquery',
  'ember',
  'text!../templates/advanced_filter.handlebars'
], function(
    $,
    Em,
    template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'advanced-search'
  });
});
