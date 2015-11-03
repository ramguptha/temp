define([
  'ember',
  'packages/platform/presenter',
  'packages/platform/is-data-presenter',
  'packages/platform/selection',
  'packages/platform/single-enumerable',
  'packages/platform/query',
  'packages/platform/data-pager',
  'packages/platform/data-list-component',
  '../nav_row_presentation',
  '../components/nav_list_item_component'
], function(
  Em,
  Presenter,
  IsDataPresenter,
  Selection,
  SingleEnumerable,
  Query,
  DataPager,
  DataListComponent,
  NavRowPresentation,
  NavListItemComponent
) {
  'use strict';

  // Nav Controller
  // ==============
  //
  // Convenience controller for nav portions of a nav page. Nav Controllers have a _lot_ in common with 
  // Data Table Controllers. Notable differences:
  //
  // - Default selector.
  // - searchable* is not a proper subset of visible*.

  return Em.Controller.extend({
    Selection: Selection,
    SingleEnumerable: SingleEnumerable,
    Search: Query.Search,
    DataPager: DataPager,
    Presenter: Presenter.extend(IsDataPresenter),

    dataPager: null,
    searchQuery: Em.computed.oneWay('dataPager.searchQuery'),
    dataStore: Em.computed.oneWay('dataPager.dataStore'),
    spec: Em.computed.oneWay('dataStore.Spec'),

    rowHeight: DataListComponent.defaultRowHeight,
    rowPresentation: NavRowPresentation,
    itemComponent: 'nav-list-item',
    classNames: 'is-nav',

    navActions: null,
    navActionsContext: function() { return this; }.property(),
    navActionsTarget: Em.computed.oneWay('navActionsContext'),

    // Selection
    // ---------

    selector: function() {
      return this.Selection.SetOne.create();
    }.property(),

    selections: function() {
      return this.SingleEnumerable.create({
        parentController: this,
        contentBinding: 'parentController.selectedId'
      });
    }.property(),

    selectedId: null,

    // Ad-hoc Search
    // -------------

    searchableNames: 'name'.w(),

    searchableOptions: function() {
      var self = this;
      var spec = this.get('spec');

      return (this.get('searchableNames') || Em.A()).map(function(name) {
        var presenter = self.Presenter.create({
          id: name,
          spec: spec
        });

        return {
          name: name,
          label: presenter.renderLabel()
        }
      });
    }.property('spec', 'searchableNames.[]'),

    searchFilterSupported: function() {
      return 0 < this.get('searchableOptions.length');
    }.property('searchableOptions.[]'),

    isSearchCaseSensitive: false
  });
});
