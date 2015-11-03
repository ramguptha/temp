define([
  'ember',

  'packages/platform/page-view',
  './lib/nav_row_presentation',
  './lib/controllers/nav_controller',
  './lib/components/nav_list_item_component',
  './lib/controllers/nav_size_controller',

  'text!./lib/templates/nav_page_view.handlebars'
], function(
  Em,

  PageView,
  NavRowPresentation,
  NavController,
  NavListItemComponent,
  NavSizeController,

  template
) {
  'use strict';

  // Nav Page View
  // =============
  //
  // A Nav Page is a page that has a "nav" area on the left, and a "content" area on the right. The "nav" area can be
  // resized via a draggable splitter bar (driven by a NavSizeController instance), and the content of the "nav" area
  // is driven by a NavController instance.

  return PageView.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    navSizeController: Em.computed.oneWay('context.navSizeController'),
    elementInserted: false,
    splitterClicked: false,

    didInsertElement: function() {
      this.set('elementInserted', true);
      this.setupNavSplitterEvents();
      this.applyNavSettings(this.get('navSizeController.settings'));
    },

    willDestroyElement: function() {
      this.teardownNavSplitterEvents();
    },

    navWidth: 0,
    viewWidth: 0,

    navStyle: function() {
      return new Em.Handlebars.SafeString('width:' + Number(this.get('navWidth')) + 'px;');
    }.property('navWidth'),

    clientStyle: function() {
      return new Em.Handlebars.SafeString('left: ' + Number(this.get('navWidth')) + 'px;');
    }.property('navWidth', 'viewWidth'),

    navSplitterDragEnded: function(event, ui) {
      // Determine if we are now collapsed
      this.set('splitterClicked', false);
      var navWidth = this.$('.page-snap-view').position().left + ui.position.left;
      if (navWidth <= 0) {
        this.set('navSizeController.isCollapsed', true);
      } else {
        this.set('navSizeController.settings', { width: navWidth, isCollapsed: false });
      }

      this.get('controller').send('noteLayoutMetricsChange');
    },

    navSplitterCollapseToggleClicked: function() {
      this.toggleProperty('navSizeController.isCollapsed');
      this.set('splitterClicked', true);
    },

    navSettingsDidChange: function() {
      // Nothing to do if we aren't in the DOM yet.
      if (!this.get('elementInserted')) {
        return;
      }
      this.applyNavSettings(this.get('navSizeController.settings'));
    }.observes('navSizeController.settings'),

    applyNavSettings: function(settings) {
      if (settings.isCollapsed) {
        this.$('.panel-handle').removeClass('icon-arrow-4').addClass('icon-arrow-3');
        this.applyNavWidth(0);
      } else {
        this.$('.panel-handle').removeClass('icon-arrow-3').addClass('icon-arrow-4');

        // ensure that nav area does not overflow the screen
        if (this.$().width() < settings.width) {
          this.get('navSizeController').reset();
          return;
        }

        if (this.get('splitterClicked')) {
          this.applyNavWidth(settings.width);
        }
        else {
          this.applyNavWidth(settings.width);
        }
      }
    },

    applyNavWidth: function(width) {
      this.setProperties({
        navWidth: width,
        viewWidth: this.$().width() - width
      });
    },

    setupNavSplitterEvents: function() {
      var self = this;
      var dividerHandler = this.$('.snap-view-divider-container');

      this.$('.panel-handle').click(function(event) {
        self.navSplitterCollapseToggleClicked(event);
      });

      dividerHandler.draggable({
        axis: 'x',
        containment: '.page-container',
        helper: 'clone',
        cursor: 'e-resize',
        scroll: false,
        snap: false,
        snapMode: 'inner',
        snapTolerance: 150,
        stop: function (event, ui) {
          self.navSplitterDragEnded(event, ui);
        }
      });
    },

    teardownNavSplitterEvents: function() {
      this.$('.panel-handle').unbind('click');
    }
  }).reopenClass({
    appClasses: {
      NavListItemComponent: NavListItemComponent
    },

    NavController: NavController,
    NavRowPresentation: NavRowPresentation,
    NavItemView: NavListItemComponent,
    NavSizeController: NavSizeController
  });
});
