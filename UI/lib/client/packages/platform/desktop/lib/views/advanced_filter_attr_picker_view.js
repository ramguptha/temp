define([
  'ember',
  'ui',
  'packages/platform/activity-monitor',
  'packages/platform/ui/global_menu_ctrl',

  'text!../templates/advanced_filter_attr_picker.handlebars'
], function(
  Em,
  UI,
  ActivityMonitor,
  MenuMgr,

  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'filter-attr-picker',

    didInsertElement: function() {
      this._super();

      // Disable the default behaviour of the menu manager that closes any element that is not
      // in the view upon clicking, if the target is an item in tree or 'x' sign on search input.
      this.$().on('click', '.filter-attr-picker-view-panel', this, function(evt) {
        var self = evt.data;

        var tree = self.$('.tree-view-container')[0];
        var target = evt.target;
        if (!$.contains(tree, target) && !$(target).hasClass('clear-search-input')) {
          ActivityMonitor.stopAndNote(evt, self.get('controller'));
        }
      });
    },

    willDestroyElement: function() {
      this.$().off();
    }
  });
});
