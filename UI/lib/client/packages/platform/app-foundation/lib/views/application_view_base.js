define([
  'ember',
  'packages/platform/tipsy-extensions',
  'packages/platform/ui/global_menu_ctrl',
  'ui'
], function(
  Em,
  $,
  MenuMgr,
  UI
) {
  'use strict';

  return Em.View.extend({

    didInsertElement: function() {
      // TODO Integrate select2 with the rest of the menuing system
      $(document).on(
        'open', this, this.onDocumentOpen
      ).on(
        'click', this, this.onBodyClickOrKeypress
      ).on(
        'keypress', this, this.onBodyClickOrKeypress
      ).on(
        'mouseover', this, this.showTipsyOnMouseOver
      ).on(
        'mouseout', this, this.hideTipsyOnMouseOut
      );

      $(window).on(
        'resize', this, this.onWindowResize
      );
    },

    willDestroyElement: function() {
      $(window).off(
        'resize', this.onWindowResize
      );

      $(document).off(
        'open', this.onDocumentOpen
      ).off(
        'click', this.onBodyClickOrKeypress
      ).off(
        'keypress', this.onBodyClickOrKeyPress
      ).off(
        'mouseover', this.showTipsyOnMouseOver
      ).off(
        'mouseout', this.hideTipsyOnMouseOut
      );
    },

    onDocumentOpen: function(e) {
      var self = e.data;

      // Close other instances of select2
      // In Advanced Filter Editor we have two instances of select2 in one view
      var id = e.target.id;
      $('.select2-container').each(function() {
        // Changing 'self' back to 'this' since the change of scope would not return the 'input'.
        var input = $(this).siblings('input');
        if (id !== input.attr('id')) {
          input.select2('close');
        }
      });

      Em.run(function() {
        self.get('controller').send('noteUserActivity');
        UI.MenuController.lookup().hide();
        MenuMgr.getInstance().closeMarkedMenu();
      });
    },

    onBodyClickOrKeypress: function(e) {
      var self = e.data;
      Em.run(function() {
        self.get('controller').send('noteUserActivity');
      });
    },

    onWindowResize: function(e) {
      var self = e.data;

      var controller = self.get('controller');
      if (controller) {
        controller.send('noteLayoutMetricsChange');
      }
    },

    // Tooltips Support
    // ----------------

    tipsyOptions: function() {
      return {
        html: true,
        opacity: 1,
        gravity: $.fn.tipsy.autoNESW,
        trigger: 'manual',
        title: this.tipsyTitle
      };
    }.property(),

    // Runs in the context of the tipsy object
    tipsyTitle: function() {
      var sourceId = $(this).attr('data-tooltip-source-id');

      if (!Em.isEmpty(sourceId)) {
        var viewNode = this;

        while (viewNode !== window.document && !/ember\d+/.test(viewNode.id)) {
          viewNode = viewNode.parentNode;
        }

        // If ellipsis is detected
        if ($(this).hasClass('text-ellipsis')) {
          return (this.offsetWidth < this.scrollWidth) ? Em.$('[data-tooltip-id="' + sourceId + '"]', viewNode).html() : '';
        } else {
          return Em.$('[data-tooltip-id="' + sourceId + '"]', viewNode).html();
        }
      } else {
        var attrName = $(this).attr('data-tooltip-attr');
        var result;

        // Tipsy removes the title attribute and stores it in original-title to prevent default tooltip behaviour
        attrName = 'title' === attrName ? 'original-title' : attrName;

        // If ellipsis is detected
        if (this.offsetWidth < this.scrollWidth) {
          result = $(this).attr(attrName);
        } else if ($(this).attr('data-sticky-tooltip') === 'true') {
          result = $(this).attr(attrName);
        } else {
          result = '';
        }

        return Handlebars.Utils.escapeExpression(result);
      }
    },

    visibleTipsyElement: null,

    showTipsyOnMouseOver: function(e) {
      var self = e.data;
      var $target = $(e.target);

      var tooltipAttr = $target.attr('data-tooltip-attr');
      var tooltipSourceId = $target.attr('data-tooltip-source-id');

      if (tooltipAttr || tooltipSourceId) {
        self.hideTipsy();

        $target.tipsy(self.get('tipsyOptions')).tipsy('show');
        self.set('visibleTipsyElement', e.target);
      }
    },

    hideTipsyOnMouseOut: function(e) {
      var self = e.data;
      var visibleTipsyElement = self.get('visibleTipsyElement');

      if (visibleTipsyElement = e.target) {
        self.hideTipsy();
      }
    },

    hideTipsy: function() {
      var visibleTipsyElement = this.get('visibleTipsyElement');
      if (visibleTipsyElement) {
        this.$(visibleTipsyElement).tipsy('hide').tipsy('fixTitle').releaseTipsyData();
        this.set('visibleTipsyElement', null);
      }
    }
  });
});
