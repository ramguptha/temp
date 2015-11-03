define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: [],

    description: 'Gather Inventory',
    endPoint: 'computercommands/gatherinventory',

    serialNumbers: null,
    forceFullInventoryEnabled: null,
    includeFontInformationEnabled: null,
    includePrinterInformationEnabled: null,
    includeStratupItemInformationEnabled: null,
    includeServiceInformationEnabled: null,

    toJSON: function() {
      return {
        serialNumbers: this.get('serialNumbers').map(function(serialNumber) {
          return serialNumber.toString();
        }),
        fullInv: this.get('forceFullInventoryEnabled'),
        withFonts: this.get('includeFontInformationEnabled'),
        withPrinters: this.get('includePrinterInformationEnabled'),
        withStartupItems: this.get('includeStratupItemInformationEnabled'),
        withServices: this.get('includeServiceInformationEnabled')
      };
    }
  });
});
