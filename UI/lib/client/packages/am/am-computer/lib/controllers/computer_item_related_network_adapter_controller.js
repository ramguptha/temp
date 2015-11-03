define([
  'ember',
  'am-desktop',
  'am-data'
], function (
  Em,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({

    relatedListTitle: 'amComputer.networkAdapterTab.title'.tr(),
    userPrefsEndpointName: 'computerNetworkAdapterListColumns',
    visibleColumnNames: 'adapterName adapterIpAddress adapterSubnetMask adapterMacAddress configurationType primaryInterface routerAddress dhcpServerAddress dnsServers searchDomains tcpImplementation deviceName linkStatus adapterSpeed fullDuplex adapterVendor hardware'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemNetworkAdapterStore');
    }.property(),

    loadNetworkadapter: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'adapterName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});
