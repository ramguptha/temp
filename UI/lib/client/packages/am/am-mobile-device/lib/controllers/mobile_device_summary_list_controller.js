define([
  'ember',
  'am-desktop'
], function(
  Em,
  AmDesktop
) {
  'use strict';

  return AmDesktop.AmListController.extend({
    dataStore: null,
    visibleColumnNames: 'name model osPlatform osVersion serialNumber phoneNumber lastContact'.w(),
    adhocSearchSupported: false,

    SummaryListView: AmDesktop.AmSelectionListView
  });
});
