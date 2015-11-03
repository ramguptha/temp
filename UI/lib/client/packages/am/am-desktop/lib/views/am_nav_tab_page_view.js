define([
  'ember',
  'packages/platform/nav-page-view',
  './has_nav_tabs',
  'text!../templates/am_nav_tab_page.handlebars'
], function(
  Em,
  NavPageView,
  HasNavTabs,
  template
) {
  'use strict';

  var NavTabPageView = NavPageView.extend(HasNavTabs, {
    layout: Em.Handlebars.compile(template)
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
