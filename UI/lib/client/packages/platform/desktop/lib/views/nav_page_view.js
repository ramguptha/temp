define([
  'ember',
  './page_view'
], function(
  Em,
  PageView
) {
  'use strict';

  return PageView.extend({
    navSizeControllerBinding: 'controller.navSizeController',
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

    navSplitterDragEnded: function(event, ui) {
      // Determine if we are now collapsed
      this.set('splitterClicked', false);
      var navWidth = this.$('.page-snap-view').position().left + ui.position.left;
      if (navWidth <= 0) {
        this.set('navSizeController.isCollapsed', true);
      } else {
        this.set('navSizeController.settings', { width: navWidth, isCollapsed: false });
      }
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
        this.applyNavWidthAnimated(0);
      } else {
        this.$('.panel-handle').removeClass('icon-arrow-3').addClass('icon-arrow-4');

        // ensure that nav area does not overflow the screen
        if (this.$().width() < settings.width) {
          this.get('navSizeController').reset();
          return;
        }

        if (this.get('splitterClicked')) {
          this.applyNavWidthAnimated(settings.width);
        }
        else {
          this.applyNavWidth(settings.width);
        }
      }
    },

    applyNavWidthAnimated: function(width) { // to animate expand/collapse on handler click
      // content area left margin & width
      this.$('.page-snap-view').animate({left: width}, 200);
      // nav width
      this.$('.page-snap-view').siblings('div').animate({width: width}, 200);
    },

    applyNavWidth: function(width) {
      // content area left margin & width
      this.$('.page-snap-view').css('left', width);
      this.$('.page-snap-view').siblings('div').css('width', this.$().width() - width);

      // nav width
      this.$('.page-snap-view').siblings('div').css('width', width);
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
  });
});
