define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Menus
  // =====
  //
  // The Menu Controller maintains a list of visible menus. Other objects monitor this list and use it to determine 
  // if their menus should be visible.
  //
  // When using menu-related action helpers, such as showMenu, hideMenu and so-on, bubbles=false _must_ be set.

  // HasMenus
  // --------
  //
  // Add this Mixin to a Controller or Component for simplified access to menu functionality. Classes that include
  // HasMenus are expected to invoke registerMenuNames() to name all related menus, and in turn use monitorMenu() to
  // generate properties for each menu.
  var HasMenus = Em.Mixin.create({
    actions: {
      showMenu: function(name) {
        this.get('menuController').show(this.get('menus')[name]);
      },

      hideMenu: function(name) {
        this.get('menuController').hide(this.get('menus')[name]);
      },

      toggleMenu: function(name) {
        this.get('menuController').toggle(this.get('menus')[name]);
      }
    },

    // Menus are often controlled by objects that are not part of the standard Ember MVC hierarchy. Since such
    // objects have no container, resolve the menuController via the usual evil means.
    menuController: function() {
      return this.container.lookup('controller:menu')
    }.property(),

    menus: function() {
      throw 'Must be overridden via UI.MenuController.registerMenuNames()';
    }.property()
  });

  // Create a property that returns an object literal with each name in names keyed to another object literal.
  // These unique objects will be passed to the MenuController as tokens for the given menu name.
  HasMenus.registerMenuNames = function(names) {
    return function() {
      var menus = {};

      names.forEach(function(name) {
        menus[name] = {};
      });

      return menus;
    }.property();
  };

  // Create a boolean property that returns true when the menuController is showing the menu denoted by name,
  // false otherwise.
  HasMenus.monitorMenu = function(name) {
    return function() {
      return this.get('menuController.visibleMenus').contains(this.get('menus')[name]);
    }.property('menuController.visibleMenus.[]', 'menus');
  };

  // HasOneMenu
  // ----------
  //
  // Add this Mixin to any Ember Class to make it responsible for showing and hiding a menu.
  var HasOneMenu = Em.Mixin.create({

    // Menus are often controlled by objects that are not part of the standard Ember MVC hierarchy. Since such
    // objects have no container, resolve the menuController via the usual evil means.
    menuController: function() {
      return window.App.__container__.lookup('controller:menu');
    }.property(),
    
    // True if menu is visible. Mixees are expected to monitor this property to show / hide menu content.
    showingMenu: function() {
      return this.get('menuController.visibleMenus').contains(this);
    }.property('menuController.visibleMenus.[]'),

    // this.showingMenu is changed as a side-effect of mutations to the menu controller. Get the latest value
    // of the property to ensure that bindings fire and properties invalidate. It's frustrating that this is
    // necessary.
    showMenu: function() {
      this.get('menuController').show(this);
      this.get('showingMenu');
    },

    hideMenu: function() {
      this.get('menuController').hide(this);
      this.get('showingMenu');
    },

    toggleMenu: function() {
      this.get('menuController').toggle(this);
      this.get('showingMenu');
    }
  });

  // HasOneMenuWithActions
  // ---------------------
  //
  // HasOneMenu ... with actions.
  var HasOneMenuWithActions = Em.Mixin.create(HasOneMenu, {
    actions: {
      showMenu: function() {
        this.showMenu();
      },

      hideMenu: function() {
        this.hideMenu();
      },

      toggleMenu: function() {
        this.toggleMenu();
      }
    }
  });

  // MenuController
  // --------------
  var MenuController = Em.Controller.extend({

    // The list of visible menus
    visibleMenus: function() {
      return Em.A();
    }.property(),

    // Replace the content of visibleMenus with this menu
    show: function(menu) {
      this.get('visibleMenus').setObjects([menu]);
      window.GlobalMenuMgr.closeMarkedMenu();
    },

    // Add this menu to the visible list
    push: function(menu) {
      this.get('visibleMenus').pushObject(menu);
    },

    // Remove menu if specified, or clear visibleMenus completely with no parameters
    hide: function(menu) {
      if (!Em.isNone(menu)) {
        this.get('visibleMenus').removeObject(menu);
      } else {
        this.get('visibleMenus').clear();
      }
    },

    // If menu is visible, remove it. Otherwise, show it.
    toggle: function(menu) {
      if (this.get('visibleMenus').contains(menu)) {
        this.hide(menu);
      } else {
        this.show(menu);
      }
    },

    init: function() {
      // Observers
      this.get('visibleMenus');
    },

    visibleMenusDidChange: function() {
      this.send('noteUserActivity');
    }.observes('visibleMenus.[]')
  });

  return MenuController.reopenClass({
    HasMenus: HasMenus,
    HasOneMenu: HasOneMenu,
    HasOneMenuWithActions: HasOneMenuWithActions,

    // Lots of things need access to the MenuController! Especially views. Clean up access, when needs won't do.
    lookup: function() {
      return window.App.__container__.lookup('controller:menu');
    }
  });
});
