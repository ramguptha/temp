define([
  'handlebars',
  'ember',
  'tree',
  'ui',

  './searchable_select_view',
  'text!../templates/tree.handlebars',
  'logger'
], function(
  Handlebars,
  Em,
  $,
  UI, // UI is needed because handlebars invokes window.sendEmberAction()

  SearchableSelectView,
  template,
  logger
) {
  // Can't use strict with apply/arguments
  // 'use strict';

  return Em.View.extend({
    Select: Em.Select,
    SearchableSelect: SearchableSelectView,

    EmptyStatusTemplate: Handlebars.compile(
      '<div class="empty-list-layout"><span class="icon-square-attention1 empty-list-icon"></span><p>{{noResults}}</p><p class="empty-message">{{noItemsToShow}}</p></div>'
    ),

    defaultTemplate: Em.Handlebars.compile(template),

    tNoResults: 'shared.noResults'.tr(),
    tNoItems: 'shared.noItemsToDisplay'.tr(),

    isMultiSelect: Em.computed.oneWay('treeController.isMultiSelect'),
    isNavigationEnabled: Em.computed.oneWay('treeController.isNavigationEnabled'),

    // this property is to keep track of previously searched filter
    tmpSearchFilter: '',

    treeController: function() {
      var name = this.get('controllerName');
      return name ? this.get('context.' + name) : this.get('controller');
    }.property('controller', 'controllerName'),

    treeClass: 'tree-view-container',
    selectedClass: 'jqtree-selected',

    treeSelector: function() {
      return '.' + this.get('treeClass');
    }.property('treeClass'),

    $tree: function() {
      var treeElements = this.$(this.get('treeSelector'));
      return treeElements.tree.apply(treeElements, arguments);
    },

    init: function() {
      this._super();

      // Kick off observers
      this.getProperties('treeController.tree'.w());
    },

    // it doesn't make sense why this observer is needed in addition to the .getProperties above
    // but IE will not call reloadDataOnTreeChange() otherwise
    foo: function() {}.observes('treeController.searchFilter'),

    didInsertElement: function() {
      this._super();

      var self = this;
      var data = this.get('treeController.tree') || Em.A();
      logger.log('DESKTOP: TREE_VIEW: INSERT: ', data);

      this.$tree({
        data: data,
        autoOpen: false,
        dragAndDrop: false,
        autoEscape: false,
        saveState: true,
        closedIcon: '',
        openedIcon: '',

        onCreateLi: function(node, $li) {
          self.onCreateLi(node, $li);
        },

        onCanSelectNode: function(node) {
          // This check is to make sure resetting selection on tree doesn't fail in onCanSelectNode
          if (node === null) {
            return true;
          }
          return self.onCanSelectNode(node);
        }

      }).bind('tree.select', function(e) {
        self.onTreeNodeSelected(e);
      }).bind('tree.init', function(e) {
        self.onTreeReloaded(e);
      });

      this.$(this.get('treeSelector')).tree('selectNode', null);  // clear node selection

      var scrollPosition = this.get('treeController.scrollPosition');
      this.$(this.get('treeSelector')).scrollTop(scrollPosition);

      // add a scrolling specific class to the scrollable container for jquery-menu.getMenuVerticalDirection()
      $('.' + this.treeClass).addClass('scrollable-container');
    },

    willDestroyElement: function() {
      this.get('treeController').clearSearchQuery();
      this.set('treeController.scrollPosition', this.$(this.get('treeSelector')).scrollTop());
      this.get('treeController').clearSearchQuery();

      // Apparently this is the canonical way to remove jqTree.
      this.$(this.treeSelector).remove();

      $('html').off('click');
    },

    onCreateLi: function(node, $li) {
      // Support automation by marking up our row with descriptive classes
      $li.find('.row-container')
        .attr('data-is-container-for-id', node.id)
        .attr('data-is-container-for-automation-id', node.automationId);
      $li.find('.jqtree-title').addClass('is-name name');

      var $emptyListLayout = this.$('.tree-view-container .empty-list-layout');
      //remove empty list message if any
      if($emptyListLayout) {
        $emptyListLayout.remove();
      }

      $li.children().prepend('<div class="row-empty-background"></div>');

      if (!node.isSelectable) {
        $li.addClass('not-selectable').find('div.jqtree-element').addClass('not-selectable');
      }
    },

    onCanSelectNode: function(node) {
      // Do not allow selection if user clicks on an already selected node, or a non-selectable node
      // (which is implemented in buildNode of each subclass if necessary)
      if (node && node.isSelectable && node.id !== this.$tree('getSelectedNode').id) {
        return true;
      }

      return false;
    },

    onTreeNodeSelected: function(e) {
      var treeController = this.get('treeController');

      if (this.get('isNavigationEnabled')) {
        // Drill down to the selected node
        treeController.set('selectedItemId', e.node.id);
        this.navigate(e.node.id);
      } else {
        // Add the node to the selectionsList
        treeController.select(e.node.id, true, e.node);
      }
    },

    navigate: function(id) {
      this.get('treeController').send('gotoTreeItem', id);
    },

    updateHighlightClass: function(elem, checked) {
      var selectedClass = this.get('selectedClass');
      if (checked) {
        elem.addClass(selectedClass);
      } else {
        elem.removeClass(selectedClass);
      }
    },

    onTreeReloaded: function(e) {
      var selectedItemId = this.get('treeController.selectedItemId');
      if (!Em.isEmpty(selectedItemId)) {
        this.highlightSelectedNode(selectedItemId);
      }
    },

    highlightSelectedNode: function(selectedItemId) {
      if ('inDOM' !== this.get('_state') || Em.isEmpty(this.$tree('getTree').children)) {
        return;
      }

      // Do not re select the selected node
      if (this.$tree('getSelectedNode').id !== selectedItemId) {
        var activeNode = this.$tree('getNodeById', selectedItemId);
        this.$tree('selectNode', activeNode);
      }
    },

    reloadDataOnTreeChange: function() {
      if ('inDOM' !== this.get('_state')) {
        return;
      }

      var treeController = this.get('treeController');
      var $tree = this.$(this.get('treeSelector'));

      // Do not update reload the tree if user got here upon focusing on the search-box
      // This workaround is to prevent reloading when searchFilter is empty.
      // Use tmpSearchFilter to ensure user didn't get here on pressing backspace in the search-box
      var searchFilter = treeController.get('searchFilter');
      if (this.get('tmpSearchFilter') !== '' || searchFilter !== '') {
        this.set('tmpSearchFilter', searchFilter);

        var data = treeController.get('tree');
        logger.log('DESKTOP: TREE_VIEW: RELOAD: ', data);

        this.$tree('loadData', data);

        if (data.length === 0) {
          this.showEmptyStatus();
        }
      }

      // this code is for the sake of search functionality
      // need to expand the parent if the child matches the search criteria
      var searchFilter = treeController.get('searchFilter');
      if (null !== searchFilter && searchFilter.length > 0) {
        this.updateFilteredNodes($tree, $tree.tree('getTree'), searchFilter.toLowerCase());
      }
    }.observes('treeController.tree'),

    showEmptyStatus: function() {
      if (0 === this.$('.tree-view-container .empty-list-layout').length) {
        this.$('.tree-view-container').append(
          this.EmptyStatusTemplate({ noResults: this.get('tNoResults'), noItemsToShow: this.get('tNoItems') })
        );
      }
    },

    // Based on the existence of searchableColumnSpecs(in data related trees),
    // and also the search attributes, update the presentation of the nodes
    updateFilteredNodes: function($tree, tree, searchFilter) {
      var self = this;
      var treeController = this.get('treeController');
      var columnNames = treeController.get('searchableColumnNames');

      if (tree.hasChildren()) {
        tree.iterate(function (node) {
          self.updateFilteredNodes($tree, node, searchFilter);
          $tree.tree('openNode', node, true);

          var searchAttr = treeController.get('searchAttr');
          // If user defined a specific attribute to search through
          if (!Em.isEmpty(searchAttr)) {
            self.formatSearchResult(node, searchAttr, searchFilter)
          } else {
            columnNames.forEach(function (name) {
              if (!Em.isEmpty(name)) {
                self.formatSearchResult(node, name, searchFilter);
              }
            });
          }
        });
      }
    },

    // Format the matched the text with underline/bold fonts
    // This logic of formatting the markup was originally taken from Select2.markMatch() implementation
    formatSearchResult: function (node, name, searchFilter) {
      var markup = [];
      var $element = $(node.element).find('.' + name).first();
      var text = $element.text();

      var match = text.toUpperCase().indexOf(searchFilter.toUpperCase()),
        length = searchFilter.length;

      if (match < 0) {
        return;
      }

      markup.push(text.substring(0, match));
      markup.push('<span class="search-result-match">');
      markup.push(text.substring(match, match + length));
      markup.push('</span>');
      markup.push(text.substring(match + length, text.length));

      $element.html(markup.join(''));
    }
  });
});
