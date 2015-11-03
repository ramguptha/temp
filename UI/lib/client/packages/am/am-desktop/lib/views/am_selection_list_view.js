define([
  'ember',
  'text!../templates/am_selection_list.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'list-container flex-container fill-height'.w()
  });
});
