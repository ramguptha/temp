define([
  'ember',
  'packages/platform/paged-component',
  './lib/virtual_page',
  './lib/components/item_component',

  'text!./lib/templates/paged_list.handlebars',
  'text!./lib/templates/virtual_page.handlebars'
], function(
  Em,
  PagedComponent,
  VirtualPage,
  ItemComponent,

  absListTemplate,
  virtualPageTemplate
) {
  'use strict';

  // Paged List Component
  // ====================
  //
  // Renders a list of items with infinite scrolling.
  //
  // Bindable properties:
  //
  // - pausedComponent: shown when loading is paused
  // - loadingComponent: for rendering the loading status of nodes.
  // - emptyResultComponent: shown when nothing matches the search criteria

  var RowPresentation = PagedComponent.RowPresentation.extend({ itemComponent: null });
  var defaultRowHeight = 34;

  var VirtualPageComponent = Em.Component.extend({
    layout: Em.Handlebars.compile(virtualPageTemplate),

    actions: {
      rowClick: function(row) {
        this.sendAction('rowClick', row);
      },

      showError: function(error) {
        this.sendAction('showError', error);
      },

      retryLoad: function(node) {
        this.sendAction('retryLoad', node);
      }
    }
  });

  var PagedListComponent = PagedComponent.extend({
    VirtualPage: VirtualPage,

    VirtualPageComponent: VirtualPageComponent,

    rowPresentation: RowPresentation,
    itemComponent: 'paged-list-item',

    layout: Em.Handlebars.compile(absListTemplate),
    classNames: 'is-paged-list fill reset-origin'.w(),

    pausedComponent: 'paged-list-loading',
    loadingComponent: 'paged-list-loading',
    emptyResultComponent: 'paged-list-empty-result',

    // Virtual Pages
    // -------------

    createVirtualPage: function(traceName, traceColour) {
      return this._super(traceName, traceColour).setProperties({
        itemComponent: this.itemComponent
      });
    },

    // Metrics
    // -------

    rowHeight: defaultRowHeight,

    measureVirtualContainer: function() {
      var containerClientHeight = 0;

      if (this.get('_state') === 'inDOM') {
        var $container = this.$('.virtual-container')[0];

        containerClientHeight = $container.clientHeight;
      }

      return this.get('virtualContainerMetrics').setProperties({
        containerClientHeight: containerClientHeight
      });
    },

    updateMetrics: function() {
      if ('inDOM' === this.get('_state')) {
        this.measureVirtualContainer();
      }
    },

    virtualSizerStyle: function() {
      var top = this.get('virtualContainerMetrics.virtualHeight') - 1;

      return new Em.Handlebars.SafeString('position:absolute;' + 'top:' + top + 'px;' + 'left:0px;');
    }.property('virtualContainerMetrics.virtualHeight'),

    // Event Handlers
    // --------------

    bindVirtualContainerScroll: function() {
      this.$('.virtual-container').on('scroll', this, this.virtualContainerScroll);
    },

    unbindVirtualContainerScroll: function() {
      this.$('.virtual-container').off('scroll', this.virtualContainerScroll);
    }
  });

  return PagedListComponent.reopenClass({
    appClasses: {
      PagedListComponent: PagedListComponent,
      PagedListPageComponent: VirtualPageComponent,
      PagedListItemComponent: ItemComponent,
      PagedListLoadingComponent: PagedComponent.LoadingComponent,
      PagedListEmptyResultComponent: PagedComponent.EmptyResultComponent
    },

    RowPresentation: RowPresentation,
    ItemComponent: ItemComponent,

    defaultRowHeight: defaultRowHeight
  });
});
