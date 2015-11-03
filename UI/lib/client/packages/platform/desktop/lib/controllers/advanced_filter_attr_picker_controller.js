
define([
  'ember',
  'ui',
  'logger',
  'formatter',
  'packages/platform/activity-monitor',
  'packages/platform/ui/global_menu_ctrl',

  './tree_controller',
  '../views/tree_view'
], function(
  Em,
  UI,
  logger,
  Formatter,
  ActivityMonitor,
  MenuMgr,

  TreeController,
  TreeView
  ) {
  'use strict';

  var TreeView = TreeView.extend({
    didInsertElement: function() {
      this._super();

      this.$('input').trigger('focus');

      // Fix position of dropdown picker panel, when many filters are set
      var $picker = this.get('parentView').$();
      var pickerOffset = $picker.offset();
      var pickerHeight = $picker.height();
      this.$().offset({top: pickerOffset.top + pickerHeight, left: pickerOffset.left});

      this.onTreeReloaded();
      this.goToSelectedNode();
    },

    willDestroyElement: function() {
      var treeController = this.get('treeController');
      treeController.clearSearchQuery();
    },

    onCreateLi: function(node, $li) {
      this._super(node, $li);

      // Children attributes will have a background
      if (!Em.isEmpty(node.parent)) {
        $li.addClass('filter-attr-child');
      }
    },

    // @override
    onTreeNodeSelected: function(evt) {
      var controller = this.get('controller');
      var node = evt.node;

      // Since this is a single select tree, first reset selection then add the new node to the selection
      controller.select(node.id, true, node);

      ActivityMonitor.stopAndNote(evt, controller);
    },

    goToSelectedNode: function() {
      var $tree = this.$(this.get('treeSelector'));
      var $treeContainer = $tree.parent('div.group-list-wrapper');
      var $row = $tree.find('.' + this.get('selectedClass'));

      // Collapse all opened node if no selected nodes exists
      var tree = $tree.tree('getTree');

      tree.iterate(function (node) {
        if (node.hasChildren()) {
          $tree.tree('closeNode', node, false);
        }
        return true;
      });

      // Find selected node and expand it
      var selectedNode = $tree.tree('getSelectedNode');
      if (selectedNode) {
        $tree.tree('openNode', selectedNode.parent, false);
      }

      // Scroll to selected node
      if ($row.length > 0) {
        $treeContainer.scrollTop($treeContainer.scrollTop() + ($row.position().top - $treeContainer.position().top) - ($treeContainer.height() / 2) + ($row.height() / 2));
      }
    }
  });

  var TreeController = TreeController.extend({
    TreeView: TreeView,

    tree: function() {
      var columnSpecs = this.get('parentController.columnSpecs'),
        searchFilter = this.get('searchFilter'),
        tree = Em.A();

      // Filter the columnSpecs if the user is searching
      if (typeof searchFilter !== 'undefined' && searchFilter !== null) {
        columnSpecs = this.filterColumnSpecs(Em.A(), columnSpecs, searchFilter);
      }

      if (!Em.isEmpty(columnSpecs)) {
        tree = this.buildTree(columnSpecs, null, this, this.buildNode);
      }

      return tree;
    }.property('parentController.columnSpecs.[]', 'searchFilter'),

    init: function() {
      this._super();

      // Observers
      this.getProperties('tree');
    },

    buildTree: function(data, parentName, nodeBuilderScope, nodeBuilder) {
      var self = this;
      var roots = [];
      var nonRoots = [];

      data.forEach(function(obj) {
        if (obj.parent === parentName) {
          roots.push(obj);
        } else {
          if (obj.name.indexOf('.') === -1) {
            obj['parent'] = null;
            roots.push(obj);
          } else {
            obj['parent'] = obj.name.split('.')[0];
            nonRoots.push(obj);
          }
        }
      });

      if (0 === roots.get('length')) {
        return null;
      }

      return roots.map(function(obj) {
        return nodeBuilder.call(nodeBuilderScope, obj, self.buildTree(nonRoots, obj.name, nodeBuilderScope, nodeBuilder));
      });
    },

    buildNode: function(model, children) {
      return {
        id: model.name,
        automationId: 'is-option-for-filter-' + model.name,
        name: model.parent ? this.formatChildLabel(model.name) : model.label,
        qualifiedName: model.label,
        model: model,
        children: children,
        parent: model.parent,
        isSelectable: Em.isEmpty(children) ? true : false
      }
    },

    // Only display the label of children
    formatChildLabel: function(name) {
      var childName = name.split('.')[1];
      return Formatter.camelCaseToTitleCase(childName);
    },

    // @override
    select: function(id, selected, node) {
      this.set('selectedItemId', id);

      this._super(id, selected, node);
    },

    getParent: function(node) {
      if (node.parent) {
        var columnSpecs = this.get('parentController.columnSpecs');
        return columnSpecs.findBy('name', node.parent);
      }
    },

    // Include in the columnSpecs only matched results and the parent of matched results
    filterColumnSpecs: function(filteredColumnSpecs, columnSpecs, searchFilter) {
      var self = this;
      var lowercaseFilter = searchFilter.toLowerCase();

      columnSpecs.forEach(function (node) {
        if (!Em.isEmpty(node.children)) {
          self.filterColumnSpecs(filteredColumnSpecs, node.children, searchFilter);
        } else {
          if (-1 !== node.label.toLowerCase().indexOf(lowercaseFilter)) {
            var parent = self.getParent(node);
            if (!Em.isEmpty(parent) && !filteredColumnSpecs.contains(parent)) {
              filteredColumnSpecs.pushObject(parent);
            }
            if (!filteredColumnSpecs.contains(node)) {
              filteredColumnSpecs.pushObject(node);
            }
          }
        }
      });

      return filteredColumnSpecs;
    }
  });

  // Advanced Filter AttrPicker Controller
  // ======================================
  //
  // The controller class controls the structure and representation of the advanced filter attrs
  // using Tree modules

  return Em.Controller.extend(UI.MenuController.HasOneMenu, {
    TreeController: TreeController,
    treeController: function() {
      return this.TreeController.create({
        parentController: this
      });
    }.property(),

    actions: {
      toggleShowFilterAttrPicker: function() {
        this.toggleMenu();
      }
    },

    tPlaceholder: 'desktop.advancedFilterComponent.dataFieldPrompt'.tr(),

    columnSpecs: null,

    selectedAttrLabel: function() {
      var selectedAttr = this.get('selectedAttr');
      return !Em.isEmpty(selectedAttr) ? selectedAttr.qualifiedName : this.get('tPlaceholder');
    }.property('selectedAttr'),

    updateAttrName: function() {
      var selectionsList = this.get('treeController.selectionsList');
      if (!Em.isEmpty(selectionsList)) {
        var selectedId = selectionsList.objectAt(0).id;

        if (this.get('parentController.attrName') !== selectedId) {
          this.set('parentController.attrName', selectedId);
        }
      }
    }.observes('treeController.selectionsList.@each.id'),

    selectedAttr: Em.computed('treeController.selectionsList.[]', {
      set: function(name, value) {
        var treeController = this.get('treeController');
        var node = treeController.get('flattenedTree').findBy('id', value);

        treeController.select(value, true, node);
        value = node;
        return value;
      },
      get: function() {
        return this.get('treeController.selectionsList').objectAt(0);
      }
    })
  });
});
