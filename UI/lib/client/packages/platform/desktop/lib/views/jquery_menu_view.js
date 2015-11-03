define([
  'ember',
  'jquery',
  'packages/platform/activity-monitor',
  'logger'
], function(
  Em,
  $,
  ActivityMonitor,
  logger
) {
  'use strict';

  return Em.Object.extend({
    // An array of [context, menu DOM container] tuples
    // will be filled in the parent view
    contextToMenuContainerMapping: Em.A(),

    menuController: null,

    init: function() {
      // Observers
      this.get('menuController.context');
    },

    contextDidChange: function() {
      var self = this;

      var parentView = this.get('parentView');
      if ('inDOM' !== parentView.get('_state')) {
        // We're not in the DOM yet. Try again later.
        Em.run.next(this, this.contextDidChange);
        return;
      }

      // Find the corresponding menu container in contextToContainerMapping for the context (if any),
      // and show the menu
      var context = this.get('menuController.context');

      var contextSelector = null;
      this.get('contextToMenuContainerMapping').find(function(mapping) {
        if (context === mapping[0]) {
          contextSelector = mapping[1];
          return true;
        }
      });

      var activeClass = 'btn-menu-active';

      // Remove all menus that don't match the context
      parentView.$('button.contextmenu-right + ul').each(function(idx, ul) {
        var $ul = $(ul);
        var $button = $ul.prev('button');
        var selector = $button.attr('data-contextmenu-selector');

        if (selector !== contextSelector) {
          $button.removeClass(activeClass);
          $ul.remove();
        }
      });

      // Add missing menus
      if (context && contextSelector) {
        var $menuContainer = parentView.$(contextSelector);
        if (0 === $menuContainer.find('button.contextmenu-right + ul').length) {
          this.createMenuDropdown($menuContainer);
          var $button = $menuContainer.find('button.contextmenu-right');
          $button.addClass(activeClass).attr('data-contextmenu-selector', contextSelector);
        }
      }
    }.observes('menuController.context'),

    createMenuDropdown: function($menuContainer) {
      var self = this;
      var $menu = this.get('parentView').$('<ul></ul>').addClass(this.getMenuVerticalDirection($menuContainer));

      $.each(this.get('menuController.menuItems'), function(idx, action) {
        var $menuItem = self.get('parentView').$('<li><button type="button" class="btn-dropdown"' +
          'data-action-name="' + action.actionName + '">' + action.name + '</button></li>');

        if (true === action.disabled) {
          $menuItem.find('button').prop('disabled', true).addClass('disabled');
        }
        $menuItem.on('click', 'button', function(e) {
          e.preventDefault();
          ActivityMonitor.stopAndNote(e);

          self.get('menuController.parentController').send($(e.target).attr('data-action-name'), action.contextPath);
          self.get('menuController').hideMenu();
        });

        $menuItem.appendTo($menu);
      });

      //attach the UL. It's not visible until you press the button.
      $menuContainer.closest('.contextmenu-container').append($menu);
    },

    getMenuVerticalDirection: function($container) {
      var self = this;

      var viewport = this.get('parentView').$('div.scrollable-container');

      //get the horizontal y coordinate of the middle of the viewport relative the window.
      var vpGlobalYcoordinate = viewport.offset().top;
      var vpHeight = viewport.height();
      var middleOfViewPort = vpGlobalYcoordinate + vpHeight/2;

      //figure out if the <li> is above or below the middle of the viewport.
      //if above, draw to the bottom else, draw to the top
      return ($container.offset().top <= middleOfViewPort) ? 'dropdown-menu-buttons': 'dropdown-menu-buttons-up';
    },

    onMenuButtonClick: function(context) {
      // Parent view is responsible for routing button clicks to this handler.
      // If context is the same as that in the controller, clear it in the controller.
      // If context is different in the controller.
      var controller = this.get('menuController');

      if (context === controller.get('context')) {
        controller.hideMenu();
      } else {
        controller.set('context', context);
      }
    }
  });
});
