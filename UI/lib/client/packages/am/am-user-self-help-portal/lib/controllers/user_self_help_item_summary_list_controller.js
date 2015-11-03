define([
  'ember',
  'am-desktop'
], function(
  Em,
  AmDesktop
) {
  'use strict';

  return AmDesktop.AmListController.extend({
    visibleColumnNames: 'name model osPlatform osVersion serialNumber phoneNumber lastContact'.w(),

    SummaryListView: AmDesktop.AmSelectionListView
  });
});
