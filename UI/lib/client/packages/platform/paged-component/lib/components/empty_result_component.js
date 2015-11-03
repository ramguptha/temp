define([
  'ember',
  'text!../templates/empty_result.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.Component.extend({
    layout: Em.Handlebars.compile(template),
    classNames: 'is-empty-resultset'
  });
});
