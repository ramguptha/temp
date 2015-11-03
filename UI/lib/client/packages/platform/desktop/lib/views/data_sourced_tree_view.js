define([
  'handlebars',
  'ember',
  'tree',
  'locale',
  'ui',
  'packages/platform/activity-monitor',

  './tree_view',

  'logger'
], function(
  Handlebars,
  Em,
  $,
  Locale,
  UI,
  ActivityMonitor,

  TreeView,

  logger
) {
  'use strict';

  return TreeView.extend({
    CheckboxTemplate: Handlebars.compile(
      '<input class="is-checkbox-for-select-row checkbox-left" type="checkbox" value="{{id}}"/>'
    ),

    RowBackgroundTemplate: Handlebars.compile(
      '<div class="row-background"><div class="checkbox-contextmenu-container">{{{checkbox}}}</div>'
    ),

    ContextMenuButtonTemplate: Handlebars.compile(
      '<button type="button" class="is-button-for-context-menu btn btn-small contextmenu-right dropdown-toggle" data-node-id="{{nodeId}}"><span class="icon-arrow-2"></span></button>'
    ),

    MenuItemTemplate: Handlebars.compile(
      '<li><button type="button" class="is-button-for-{{actionClassName}} btn-dropdown" data-action-name="{{actionName}}">{{label}}</button></li>'
    ),

    activeMenuClass: 'btn-menu-active',

    init: function() {
      this._super();
      this.get('visibleTreeMenus');
    },

    didInsertElement: function() {
      var self = this;

      this._super();

      // Hide any tree menus when user scrolls
      this.$('div.scrollable-container').scroll(function() {
        var visibleTreeMenus  = self.get('visibleTreeMenus').forEach(function(menu) {
          self.get('menuController').hide(menu);
        });
      });
    },

    willDestroyElement: function() {
      this.$('div.scrollable-container').off('scroll');

      this._super();
    },

    onCreateLi: function(node, $li) {
      this._super(node, $li);

      if (this.get('isMultiSelect')) {
        var checkbox = this.CheckboxTemplate({ id: node.id });
        var background = this.RowBackgroundTemplate({ checkbox: checkbox });

        $li.children().prepend(background).find('.checkbox-contextmenu-container').append(this.createMenuBtn(node));
      }
    },

    change: function(e) {
      logger.log(e);
      // Ignore change events from the text field.
      var target = this.$(e.target);
      var type = target.attr('type');
      if (type === 'checkbox' || type === 'radio') {
        var checked = target.prop('checked');
        var nodeId = e.target.value;
        var node = this.$tree('getNodeById', nodeId);
        this.get('treeController').select(nodeId, checked === true, node);

        this.updateHighlightClass($(node.element), checked);
      }
    },

    reloadDataOnTreeChange: function() {
      this._super();

      var $tree = this.$(this.get('treeSelector'));

      // scroll to and highlight the newly created node if there is one
      if (!$.isEmptyObject(this.get('treeController.newModelsById'))) {
        this.goToNewNode($tree, $tree.tree('getTree'));
      }
    }.observes('treeController.tree'),

    goToNewNode: function($tree, treeNodes) {
      var self = this;

      if (treeNodes.hasChildren()) {
        treeNodes.iterate(function(node) {
          self.goToNewNode($tree, node);
          if (node.isNew) {
            var newNode = self.$('button[data-node-id="' + node.id + '"]');
            if (!Em.isEmpty(newNode)) {
              var parentNode = node.parent;
              $tree.tree('openNode', parentNode);
              // scroll to new node
              $tree.scrollTop($tree.scrollTop() + (newNode.closest('.row-background').position().top - $tree.position().top) - ($tree.height()/2) + (newNode.closest('.row-background').height()/2));
              // highlight new node
              newNode.closest('.row-background').addClass('highlighted-ok', 2000).removeClass('highlighted-ok', 5000, function() {
                self.get('treeController').resetNewModelsById();
              });
            }
          }
        });
      }
    },

    createMenuBtn: function(node) {
      var self = this;

      if (!node.actions) {
        return null;
      }

      var toggleMenu = function(evt) {
        ActivityMonitor.stopAndNote(evt);
        evt.preventDefault();

        var nodeId = $(evt.target).closest('button').attr('data-node-id');

        self.get('treeController.flattenedTree').findBy('id', nodeId).toggleMenu();
      };

      var contextMenuBtn = this.ContextMenuButtonTemplate({ nodeId: node.id });

      return $(contextMenuBtn).on('click', null, this, toggleMenu);
    },

    menuController: function() {
      return UI.MenuController.lookup();
    }.property(),

    // Visible menus that are related to this view
    visibleTreeMenus: function() {
      var flattenedTree = this.get('treeController.flattenedTree');

      return this.get('menuController.visibleMenus').filter(function(menu) {
        return -1 !== flattenedTree.indexOf(menu);
      });
    }.property('treeController.flattenedTree.[]', 'menuController.visibleMenus.[]'),

    visibleMenuDidChange: function() {
      var self = this;

      if ('inDOM' !== this.get('_state')) {
        return;
      }

      var treeNodesThatShouldHaveVisibleMenus = this.get('visibleTreeMenus');
      var treeNodesThatNeedMenus = Em.copy(treeNodesThatShouldHaveVisibleMenus);

      // Remove existing menus other than those that should be visible, and determine those to add
      var $treeContainer = this.$(this.get('treeSelector'));

      $treeContainer.find('.dropdown-menu-buttons, .dropdown-menu-buttons-up').each(function(idx, domNode) {
        var domNode = self.$(domNode);
        var menuButton = domNode.siblings('button');
        var menu = treeNodesThatShouldHaveVisibleMenus.findBy('id', menuButton.attr('data-node-id'));

        if (!menu) {
          domNode.remove();
          menuButton.removeClass(self.activeMenuClass);
        } else {
          treeNodesThatNeedMenus.removeObject(menu);
        }
      });

      // Add desired menus that are not already present
      treeNodesThatNeedMenus.forEach(function(treeNode) {
        var menuButton = self.$('button[data-node-id=' + treeNode.get('id') + ']');
        var menuDom = self.createMenu(menuButton, treeNode);
        menuButton.after(menuDom);
        menuButton.addClass(self.activeMenuClass);
      });
    }.observes('visibleTreeMenus.[]'),

    createMenu: function($domNode, treeNode) {
      var self = this;

      var contextMenu = this.createContextMenu($domNode);

      $.each(treeNode.get('actions'), function(index, action) {
        var menuItem = self.createMenuItem(action);

        if(action.separator) {
          $('<li class="context-menu-separator"></li>').appendTo(contextMenu);
        }

        menuItem.appendTo(contextMenu);
      });

      return contextMenu;
    },

    getMenuVerticalDirection: function($domNode) {
      var self = this;

      //get the horizontal y coordinate of the middle of the viewport relative the window.
      var viewport = $('div.scrollable-container');
      var vpGlobalYcoordinate = viewport.offset().top;
      var vpHeight = viewport.height();
      var middleOfViewPort = vpGlobalYcoordinate + vpHeight / 2;
      //figure out if the <li> is above or below the middle of the viewport.
      //if above === true draw to the bottom else draw to the top
      return ($domNode.offset().top <= middleOfViewPort) ? 'dropdown-menu-buttons': 'dropdown-menu-buttons-up';
    },

    createContextMenu: function($domNode) {
      return $('<ul></ul>').addClass(this.getMenuVerticalDirection($domNode));
    },

    createMenuItem: function(action) {
      var self = this;
      var actionName = action.actionName;
      var actionClassName = actionName.dasherize();

      var menuItemLabel = Locale.render(Locale.resolveGlobals(action.labelResource)).toString();
      var menuItem = $(
        this.MenuItemTemplate({ actionClassName: actionClassName, actionName: actionName, label: menuItemLabel })
      );

      menuItem.on('click', 'button', function(e) {
        self.get('menuController').hide();

        // TODO: Rewrite to use contextPath in a sensible way instead of setting up broken data to satisfy this code.
        self.get('treeController.target').send( $(e.target).attr('data-action-name'), action.contextPath.context );

        ActivityMonitor.stopAndNote(e);
        e.preventDefault();
      });

      return menuItem;
    },

    disableMenuItem: function(menuItem) {
      menuItem.find('button').prop('disabled', true).addClass('disabled');
    }
  });
});
