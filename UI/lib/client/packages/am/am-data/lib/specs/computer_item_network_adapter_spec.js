define([
  'ember',
  '../am_spec',
  '../am_formats'
], function(
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.extend({
    idNames: 'adapterName adapterIpAddress'.w(),

    format: {
      id: Format.ID,
      adapterName: { labelResource: 'amData.computerNetworkAdapterSpec.adapterName', format: Format.StringOrNA },
      adapterIpAddress: { labelResource: 'amData.computerNetworkAdapterSpec.adapterIpAddress', format: Format.IPv4Address },
      adapterSubnetMask: { labelResource: 'amData.computerNetworkAdapterSpec.adapterSubnetMask', format: Format.IPv4Address },
      adapterMacAddress: { labelResource: 'amData.computerNetworkAdapterSpec.adapterMacAddress', format: Format.StringOrNA },
      configurationType: { labelResource: 'amData.computerNetworkAdapterSpec.configurationType', format: Format.StringOrNA },
      primaryInterface: { labelResource: 'amData.computerNetworkAdapterSpec.primaryInterface', format: Format.BooleanOrNA },
      routerAddress: { labelResource: 'amData.computerNetworkAdapterSpec.routerAddress', format: Format.IPv4Address },
      dhcpServerAddress: { labelResource: 'amData.computerNetworkAdapterSpec.dhcpServerAddress', format: Format.IPv4Address },
      dnsServers: { labelResource: 'amData.computerNetworkAdapterSpec.dnsServers', format: Format.StringOrNA },
      searchDomains: { labelResource: 'amData.computerNetworkAdapterSpec.searchDomains', format: Format.StringOrNA },
      tcpImplementation: { labelResource: 'amData.computerNetworkAdapterSpec.tcpImplementation', format: Format.StringOrNA },
      deviceName: { labelResource: 'amData.computerNetworkAdapterSpec.deviceName', format: Format.StringOrNA },
      linkStatus: { labelResource: 'amData.computerNetworkAdapterSpec.linkStatus', format: Format.StringOrNA },
      adapterSpeed: { labelResource: 'amData.computerNetworkAdapterSpec.adapterSpeed', format: Format.NetworkSpeed },
      fullDuplex: { labelResource: 'amData.computerNetworkAdapterSpec.fullDuplex', format: Format.BooleanOrNA },
      adapterVendor: { labelResource: 'amData.computerNetworkAdapterSpec.adapterVendor', format: Format.StringOrNA },
      hardware: { labelResource: 'amData.computerNetworkAdapterSpec.hardware', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'adapterName',
        guid: 'BAF462E1-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'adapterIpAddress',
        guid: 'BAF60D06-CAC1-11D9-AAF6-000D93B66ADA',
        type: Number
      },
      {
        attr: 'adapterSubnetMask',
        guid: 'BAFDB312-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'adapterMacAddress',
        guid: 'BAFA2FA6-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'configurationType',
        guid: 'BAF69D86-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'primaryInterface',
        guid: 'BAF714E4-CAC1-11D9-AAF6-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'routerAddress',
        guid: 'BAFBFAE6-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'dhcpServerAddress',
        guid: 'BAF4E876-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'dnsServers',
        guid: 'BAF5722C-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'searchDomains',
        guid: 'BAFC94AF-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'tcpImplementation',
        guid: 'BAFE419E-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'deviceName',
        guid: 'BAFACC2E-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'linkStatus',
        guid: 'BAF80EB6-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'adapterSpeed',
        guid: 'BAF98873-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'fullDuplex',
        guid: 'BAF790B1-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'adapterVendor',
        guid: 'BAFED721-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      },
      {
        attr: 'hardware',
        guid: 'BAFB5BC4-CAC1-11D9-AAF6-000D93B66ADA',
        type: String
      }
    ],

    searchableNames: 'adapterName adapterIpAddress adapterSubnetMask adapterMacAddress configurationType primaryInterface routerAddress dhcpServerAddress dnsServers searchDomains tcpImplementation deviceName linkStatus adapterSpeed fullDuplex adapterVendor hardware'.w()

  }).create();
});
