define([
  'ember',
  'am-desktop'
], function(
  Em,
  AmDesktop
) {
  'use strict';

  return AmDesktop.AmListController.extend({

    tNotAvailable: 'shared.baseline'.tr(),

    adhocSearchSupported: false,

    SummaryListView: AmDesktop.AmSelectionListView,

    visibleColumnNames: 'agentAvailability agentName machineModel osPlatform osVersion activeIpAddress currentUserName'.w(),

    dataStore: null,

    // Perform specific formatting to specific columns
    createColumns: function(names) {
      var self = this;
      var columns = self._super(names);

      columns.forEach(function(column) {
        var valueComponent;

        switch(column.get('name')) {
          case 'osPlatform':
            // We need to show specific icons for specific Operating Systems + their version in String
            valueComponent = 'am-computer-formatted-os-platform';
            break;
          case 'agentAvailability':
            valueComponent = 'am-agent-availability-icon-formatter';
            column.set('isSortable', false);
            break;
        }

        if (valueComponent) {
          column.set('valueComponent', valueComponent);
        }
      });

      return columns;
    }
  });
});
