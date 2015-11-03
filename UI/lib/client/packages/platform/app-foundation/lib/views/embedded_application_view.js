define([
  'ember',
  'ui',
  './application_view_base',
  'packages/platform/ui/global_menu_ctrl',
  'text!../templates/embedded_application.handlebars',
  'logger'
], function(
  Em,
  UI,
  ApplicationViewBase,
  MenuMgr,
  template,
  logger
) {
  'use strict';

  return ApplicationViewBase.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'fill-height embedded-application',
    classNameBindings: ['media.classNames'],

    didInsertElement: function() {
      this._super();

      $('body').on("click", { "self": this }, this.closeMenu);
    },

    willDestroyElement: function() {
      this._super();
      $('body').off("click", this.closeMenu);
    },

    //This function catches all clicks that are outside the menu buttons. It is used to close the open menu.
    closeMenu: function(event) {
      MenuMgr.getInstance().closeMarkedMenu(event);
      UI.MenuController.lookup().hide();
    }
  });
});
