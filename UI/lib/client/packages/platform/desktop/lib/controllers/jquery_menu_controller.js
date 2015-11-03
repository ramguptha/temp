define([
  'ember',
  'ui'
], function(
  Em,
  UI
) {
  'use strict';

  return Em.Object.extend(UI.MenuController.HasOneMenu, {
    lastWrittenContext: null,

    // Writing to context shows the menu
    context: Em.computed('showingMenu', 'lastWrittenContext', {
      get: function() {
        return this.get('showingMenu') ? this.get('lastWrittenContext') : null;
      },
      set: function(key, value) {
        this.set('lastWrittenContext', value);
        this.showMenu();
        return value;
      }
    }),

    // menuItems is expected to be populated with an array of object literals with the following structure:
    // {
    //   name: Name to display in menu item,
    //   actionName: Name of the action to send to the parent controller target,
    //   contextPath: Path to retrieve when sending the related action,
    //   disabled: Boolean that determines disabled status of menu item
    // }
    menuItems: Em.A([])
  });
});
