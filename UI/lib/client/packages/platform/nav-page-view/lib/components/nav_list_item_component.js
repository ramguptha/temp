define([
  'packages/platform/data-list-component'
], function(
  DataListComponent
) {
  'use strict';

  // Nav Item Component
  // ==================
  //
  // Renders a line item in a Nav
  return DataListComponent.ItemComponent.extend({ classNames: 'nav-view-item-container' })
});
