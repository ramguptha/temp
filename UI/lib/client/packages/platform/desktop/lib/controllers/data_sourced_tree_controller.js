define([
  'jquery',
  'ember',

  'ui',
  'query',
  'logger',

  './tree_controller',
  './has_selection_actions_controller_mixin'
], function(
  $,
  Em,

  UI,
  Query,
  logger,

  TreeController,
  HasSelectionActionsControllerMixin
) {
  'use strict';

  return TreeController.extend(HasSelectionActionsControllerMixin, {
    TreeNode: TreeController.TreeNode.extend(UI.MenuController.HasOneMenu, {
      actions: null,
      isNew: null
    }),

    query: null,

    // Tree controllers only work with DataStores / DataSources
    model: Em.computed.alias('dataSource'),

    dataStore: null,
    dataSource: null,

    actionsLabelPath: 'shared.buttons.actions',

    listActions: null,

    // In datasource/query dependant trees, the query.searchFilter/Attr would be bound to parent's corresponding properties
    searchAttr: Em.computed.alias('query.searchAttr'),
    searchFilter: Em.computed.alias('query.searchFilter'),

    // properties to keep track of newly created nodes
    newModelsById: {},
    lastLoadedModelsById: {},

    tree: function() {
      var dataSource = this.get('dataSource');
      return dataSource.buildTree(dataSource, null, this, this.buildNode) || [];
    }.property('dataSource.[]', 'newModelsById'),

    searchableColumnSpecs: function() {
      var spec = this.get('dataStore.Spec');
      var columnSpecs = this.get('query.adhocSearchableNames').map(function(name) {
        return spec.getPresentationForAttr(name);
      });

      return columnSpecs.get('length') > 0 ? columnSpecs : null;
    }.property('dataStore', 'query.adhocSearchableNames.[]'),

    hasSearchableColumnSpecs: function() {
      // "Empty" searchable columns list still has the "all" option i.e. 1 item
      return this.get('searchableColumnSpecs.length') > 2;
    }.property('searchableColumnSpecs.[]'),

    searchableColumnNames: function() {
      var columnNames = Em.A();

      if (this.get('hasSearchableColumnSpecs')) {
        columnNames = this.get('searchableColumnSpecs').mapBy('name');
        // The first name is null which is corresponding to 'All' option
        columnNames.removeAt(0);
      } else {
        // Default searchable field
        columnNames = ['name'];
      }

      return columnNames;
    }.property('hasSearchableColumnSpecs'),

    init: function() {
      this._super();

      // Observers
      this.getProperties('dataSource.invalidatedAt dataSource.loadedAt');
    },

    freshenDataSourceOnInvalidate: function() {
      var dataSource = this.get('dataSource');
      if (dataSource && 'function' === typeof(dataSource.freshen)) {
        dataSource.freshen();
      }
    }.observes('dataSource.invalidatedAt'),

    acquireNewDataSourceOnQueryChange: function() {
      this.set('dataSource', this.get('dataStore').acquire(this.get('lock'), this.get('query')));
    }.observes('query.observableUri').on('init'),

    resetSelectionsOnDataSourceChange: function() {
      this.clearSelectionsList();
    }.observes('dataSource.loadedAt'),

    // @Override
    select: function(id, selected, node) {
      var obj = this.get('dataSource').findBy('id', id);
      if (!obj) {
        throw ['Unknown object id', id];
      }

      this._super(id, selected, obj);
    },

    setLastAndNewModelIds: function() {
      var lastModels = this.get('lastLoadedModelsById');
      if (!$.isEmptyObject(lastModels)) {
        var curModels = this.get('dataSource.byId');
        var newModelsById = {};
        for (var curModelId in curModels) {
          if (!lastModels.hasOwnProperty(curModelId)) {
            newModelsById[curModelId] = curModels[curModelId];
          }
        }
        if (!$.isEmptyObject(newModelsById)) {
          this.setProperties({
            lastLoadedModelsById: curModels,
            newModelsById: newModelsById
          });
        }
      } else {
        this.set('lastLoadedModelsById', this.get('dataSource.byId'));
      }
    }.observes('dataSource.loadedAt'),

    reset: function() {
      this._super();

      this.set('selectedItemId', null);
      this.resetNewModelsById();
    },

    clearSearchQuery: function() {
      var query = this.get('query');
      if (!Em.isNone(query)) {
        query.setProperties({
          searchFilter: null,
          searchAttr: null
        });
      }
    },

    resetNewModelsById: function() {
      this.set('newModelsById', {});
    }.observes('query.observableUri')
  });
});
