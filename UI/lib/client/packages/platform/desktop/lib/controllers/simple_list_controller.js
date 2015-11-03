define([
  'jquery',
  'ember',

  'guid',
  'query',

  './has_selection_actions_controller_mixin',

  'logger'
], function(
  $,
  Em,

  Guid,
  Query,

  HasSelectionActionsControllerMixin,

  logger
  ) {
  'use strict';

  return Em.Controller.extend(HasSelectionActionsControllerMixin, {
    lock: null,
    query: null,

    actionsLabelPath: 'shared.buttons.actions',
    listActions: null,
    listActionsController: null,

    scrollPosition: 0,

    model: function() {
      return this.get('dataSource');
    }.property('dataSource'),

    byId: function() {
      var byId = {};

      (this.get('model') || Em.A()).forEach(function(obj) {
        byId[obj.get('id')] = obj;
      });

      return byId;
    }.property('model.[]'),

    dataStore: null,
    dataSource: null,

    searchableColumnSpecs: function() {
      var spec = this.get('dataStore.Spec');
      return this.get('query.adhocSearchableNames').map(function(name) {
        return spec.getPresentationForAttr(name);
      });
    }.property('dataStore', 'query.adhocSearchableNames.[]'),

    init: function() {
      this._super();
      this.setProperties({
        lock: Guid.generate()
      });

      this.clearSelectionsList();
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

    selectionEnabled: true,
    selectionsList: null,
    selectionsHash: null,

    select: function(id, selected) {
      // TODO: Optimize.
      var obj = this.get('dataSource').findBy('id', id);
      if (!obj) {
        throw ['Unknown object id', id];
      }

      var selectionsList = this.get('selectionsList');
      var selectionsHash = this.get('selectionsHash');

      if (selected) {
        this.get('selectionsList').pushObject(obj);
        this.get('selectionsHash')[id] = true;
      } else {
        this.get('selectionsList').removeObject(obj);
        this.get('selectionsHash')[id] = false;
      }

      logger.log('DESKTOP: SIMPLE_LIST_CONTROLLER: select', selectionsList, selectionsHash);
    },

    clearSelectionsList: function() {
      this.setProperties({
        selectionsList: Em.A(),
        selectionsHash: {}
      });
    },

    clearSelectionsListWhenDataStoreInvalidates: function() {
      this.clearSelectionsList();
    }.observes('dataStore.invalidatedAt'),

    totalLoadedRecords: Em.computed.oneWay('model.length')
  });
});
