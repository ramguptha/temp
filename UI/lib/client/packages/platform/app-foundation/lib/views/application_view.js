define([
  'ember',
  'ui',
  'packages/platform/ui/global_menu_ctrl',
  './application_view_base',
  'text!../templates/application.handlebars',
  'help',
  'logger'
], function(
  Em,
  UI,
  MenuMgr,
  ApplicationViewBase,
  template,
  Help,
  logger
) {
  'use strict';

  return ApplicationViewBase.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    classNames: 'fill-height',
    classNameBindings: ['media.classNames'],

    helpLink: null,

    // Set by "click" handler for when a menu is toggled, then later inspected by the 
    // html handler.
    toggleClick: null,

    didInsertElement: function() {
      this._super();
      var view = this;

      view.htmlClick = function(e) {
        if (e.timeStamp === view.get('toggleClick')) {
          // This event has already been handled by the click handler, but we had to let it bubble in order
          // to allow the Ember action pipeline to function.
          return;
        }

        if ($(e.target).hasClass('history-tab')){
          return;
        }

        MenuMgr.getInstance().closeMarkedMenu();
        UI.MenuController.lookup().hide();

        $('.dropdown-toggle').removeClass('active');
        $('[data-dropdown-name]').hide();
      };

      $('html').click(view.htmlClick);
    },

    willRemoveElement: function() {
      var view = this;

      $('html').unbind('click', view.htmlClick);
    }
  });
});
