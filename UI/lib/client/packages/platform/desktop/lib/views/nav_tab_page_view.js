define([
  'ember',
  './has_nav_tabs',
  './combo_box_view',
  './nav_page_view',
  './keyboard_events_mixin',
  'text!../templates/nav_tab_page.handlebars'
], function(
  Em,
  HasNavTabs,
  ComboBoxView,
  NavPageView,
  KeyboardEvents,
  template
) {
  'use strict';

  var NavTabPageView = NavPageView.extend(KeyboardEvents, HasNavTabs, {
    ComboBoxView: ComboBoxView,
    defaultTemplate: Em.Handlebars.compile(template)
  });

  NavTabPageView.reopenClass({
    ButtonBlockView: Em.View.extend({
      classNames: 'float-right button-block-container'
    }),

    WarningView: Em.View.extend({
      classNames: 'message warning'
    })
  });

  return NavTabPageView;
});
