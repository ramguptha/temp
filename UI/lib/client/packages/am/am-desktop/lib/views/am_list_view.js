define([
  'ember',
  'text!../templates/am_list.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  // AmListView
  // ==========
  //
  // This view contains the template of the AM lists. There are few flags to switch these components on or off:
  // 1- Header component
  //  1.a- Drop down selection button
  //  1.b- Small horizontal selection buttons
  // 2- Show/Hide column button
  // 3- Button for Actions list
  // 4- Search box
  //
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'list-container content-container fill-height flex-container'.w()
  });
});

