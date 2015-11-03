define([
  'jquery',
  'ember',
  'ui',
  './searchable_select_view'
], function(
  $,
  Em,
  UI,
  SearchableSelectView
  ) {
  // Spinner fails with use strict
  // 'use strict';

  //This View is used for lists that do not need pagination and jqGrid.
  return Em.View.extend({
    SearchableSelect: SearchableSelectView,

    tNoResults: 'shared.noResults'.tr(),
    tNoItems: 'shared.noItemsToDisplay'.tr(),

    // The Page Controller and related properties
    // ------------------------------------------
    pageController: function() {
      var name = this.get('controllerName');
      return name ? this.get('controller.' + name) : this.get('controller');
    }.property('controller', 'controllerName'),

    query: function() {
      return this.get('pageController.query');
    }.property('pageController.query'),

    // TODO: There are some very ugly, weird boundary issues between PagedView and ListView right now. Clean them up.
    selectionEnabled: function() {
      return this.get('pageController.selectionEnabled');
    }.property('pageController.selectionEnabled'),

    // If false, will leave selections in place when view is taken down
    clearSelectionsOnDestroy: true,

    spinner: null,

    updateEmptyStatus: function() {
      // totalLoadedRecords doesn't necessarily reflect all loaded pages at call time
      Em.run.next(this, function() {
        if (this.get('_state') !== 'inDOM') {
          return;
        }

        var controller = this.get('pageController');

        if (!controller) {
          // Hmm ...
          return;
        }

        if (0 === controller.get('totalLoadedRecords')) {
          if (0 === this.$('div.accordion-view-container > ul > li').length && 0 ===this.$('div.accordion-view-container > div.empty-list-layout').length) {
            this.$('div.accordion-view-container').append(
                this.$('<div class="empty-list-layout"><span class="icon-square-attention1 empty-list-icon">' +
                    '</span><span class="no-results-message"><p>' +  this.get('tNoResults') +
                    '</p><p class="empty-message">' + this.get('tNoItems') + '</p></span></div>')
            );
          }
        } else {
          this.$('div.empty-list-layout').remove();
        }
      });
      //todo: figure out why the pageController doesn't work in observer
    }.observes('controller.totalLoadedRecords'),

    willDestroyElement: function() {
      this._super();

      // Clear selections
      if (this.get('selectionEnabled') && !this.get('clearSelectionsOnDestroy')) {
        this.get('pageController').clearSelectionsList(true);
      }
    }
  });
});
