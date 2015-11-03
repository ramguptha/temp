define([
  'ember'
], function(
  Em
) {
  'use strict';

  // HasNavTabs
  // ==========
  //
  // This mixin adds nested menus functionality to Nav Tabs.
  return Em.Mixin.create({
    didInsertElement: function() {
      var self = this;
      self._super();

      self.$('.snap-menu-tabs').on('click', 'li', function(event) {
        if (self.$(event.target).parent().hasClass('menu-parent')) {
          var slidableMenu = self.$(this).children('ul');

          if (slidableMenu.is(':visible')){
            slidableMenu.parent().removeClass('menu-opened').addClass('menu-closed');
            slidableMenu.slideUp();
          } else {
            slidableMenu.parent().removeClass('menu-closed').addClass('menu-opened');
            slidableMenu.slideDown();
          }
        }
      })
    },

    willClearRender: function() {
      this.$('.snap-menu-tabs').off();

      this._super();
    }
  });
});
