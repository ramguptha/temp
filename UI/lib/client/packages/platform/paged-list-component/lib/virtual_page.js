define([
  'packages/platform/paged-component'
], function(
  PagedComponent
) {
  'use strict';

  return PagedComponent.VirtualPage.extend({
    itemComponent: null,

    style: function() {
      var style = 'display:none';

      var loadedRange = this.get('loadedRange');
      if (loadedRange && 0 !== loadedRange.count) {
        style = 'top:' + this.get('virtualPageMetrics.offsetTop') + 'px;';
      }

      return new Em.Handlebars.SafeString(style);
    }.property('loadedRange', 'virtualPageMetrics.offsetTop'),

    createRowPresentation: function(rowIndex) {
      return this._super(rowIndex).setProperties({
        itemComponent: this.itemComponent
      });
    }
  });
});
