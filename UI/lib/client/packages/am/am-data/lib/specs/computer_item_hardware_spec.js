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
    format: {

      //Both Platforms
      id: Format.ID,
      computerSerialNumber: { labelResource: 'amData.computerHardwareTabSpec.computerSerialNumber', format: Format.StringOrNA },
      primaryMacAddress: { labelResource: 'amData.computerHardwareTabSpec.primaryMacAddress', format:  Format.ShortStringOrNA },
      dateTime: { labelResource: 'amData.computerHardwareTabSpec.dateTime', format: Format.TimeLocal },
      bootRomInformation: { labelResource: 'amData.computerHardwareTabSpec.bootRomInformation', format:  Format.StringOrNA },
      memorySlots: { labelResource: 'amData.computerHardwareTabSpec.memorySlots', format:  Format.ShortStringOrNA },
      memoryModuleCount: { labelResource: 'amData.computerHardwareTabSpec.memoryModuleCount', format:  Format.ShortStringOrNA },
      physicalMemory: { labelResource: 'amData.computerHardwareTabSpec.physicalMemory', format:  Format.Bytes },
      swapSpaceTotal: { labelResource: 'amData.computerHardwareTabSpec.swapSpaceTotal', format:  Format.Bytes },
      swapSpaceUsed: { labelResource: 'amData.computerHardwareTabSpec.swapSpaceUsed', format:  Format.Bytes },
      swapSpaceFree: { labelResource: 'amData.computerHardwareTabSpec.swapSpaceFree', format:  Format.Bytes },
      volumeCount: { labelResource: 'amData.computerHardwareTabSpec.volumeCount', format:  Format.ShortStringOrNA },
      ataDeviceCount: { labelResource: 'amData.computerHardwareTabSpec.ataDeviceCount', format:  Format.ShortStringOrNA },
      scsiDeviceCount: { labelResource: 'amData.computerHardwareTabSpec.scsiDeviceCount', format:  Format.ShortStringOrNA },
      fireWireDeviceCount: { labelResource: 'amData.computerHardwareTabSpec.fireWireDeviceCount', format:  Format.ShortStringOrNA },
      usbDeviceCount: { labelResource: 'amData.computerHardwareTabSpec.usbDeviceCount', format:  Format.ShortStringOrNA },
      pciDeviceCount: { labelResource: 'amData.computerHardwareTabSpec.pciDeviceCount', format:  Format.ShortStringOrNA },
      displayCount: { labelResource: 'amData.computerHardwareTabSpec.displayCount', format:  Format.ShortStringOrNA },
      osPlatform: { labelResource: 'amData.computerHardwareTabSpec.osPlatform', format:  Format.ShortStringOrNA },
      osPlatformNumber: Format.Number,

      //PC specific
      biosVendor: { labelResource: 'amData.computerHardwareTabSpec.biosVendor', format:  Format.StringOrNA },
      biosData: { labelResource: 'amData.computerHardwareTabSpec.biosData', format:  Format.DateLocal },
      biosVersion: { labelResource: 'amData.computerHardwareTabSpec.biosVersion', format:  Format.ShortStringOrNA },
      biosType: { labelResource: 'amData.computerHardwareTabSpec.biosType', format:  Format.ShortStringOrNA },
      smbiosVersion: { labelResource: 'amData.computerHardwareTabSpec.smbiosVersion', format:  Format.ShortStringOrNA },
      mainboardManufacturer: { labelResource: 'amData.computerHardwareTabSpec.mainboardManufacturer', format:  Format.ShortStringOrNA },
      mainboardProductName: { labelResource: 'amData.computerHardwareTabSpec.mainboardProductName', format:  Format.ShortStringOrNA },
      mainboardSerialNumber: { labelResource: 'amData.computerHardwareTabSpec.mainboardSerialNumber', format:  Format.ShortStringOrNA },
      mainboardType: { labelResource: 'amData.computerHardwareTabSpec.mainboardType', format:  Format.ShortStringOrNA },
      mainboardVersion: { labelResource: 'amData.computerHardwareTabSpec.mainboardVersion', format:  Format.ShortStringOrNA },
      mainboardAssetTag: { labelResource: 'amData.computerHardwareTabSpec.mainboardAssetTag', format:  Format.ShortStringOrNA },
      systemEnclosureManufacturer: { labelResource: 'amData.computerHardwareTabSpec.systemEnclosureManufacturer', format:  Format.ShortStringOrNA },
      systemEnclosureSerialNumber: { labelResource: 'amData.computerHardwareTabSpec.systemEnclosureSerialNumber', format:  Format.ShortStringOrNA },
      systemEnclosureType: { labelResource: 'amData.computerHardwareTabSpec.systemEnclosureType', format:  Format.ShortStringOrNA },
      systemEnclosureVersion: { labelResource: 'amData.computerHardwareTabSpec.systemEnclosureVersion', format:  Format.ShortStringOrNA },
      systemEnclosureAssetTag: { labelResource: 'amData.computerHardwareTabSpec.systemEnclosureAssetTag', format:  Format.ShortStringOrNA },
      computerManufacturer: { labelResource: 'amData.computerHardwareTabSpec.computerManufacturer', format:  Format.ShortStringOrNA },
      computerVersion: { labelResource: 'amData.computerHardwareTabSpec.computerVersion', format:  Format.ShortStringOrNA },
      computerModel: { labelResource: 'amData.computerHardwareTabSpec.computerModel', format:  Format.ShortStringOrNA },
      computerServiceTag: { labelResource: 'amData.computerHardwareTabSpec.computerServiceTag', format:  Format.ShortStringOrNA },
      computerWarrantyInfo: { labelResource: 'amData.computerHardwareTabSpec.computerWarrantyInfo', format:  Format.ShortStringOrNA },
      computerWarrantyEnd: { labelResource: 'amData.computerHardwareTabSpec.computerWarrantyEnd', format:  Format.ShortStringOrNA },

      //Mac specific
      computerType: { labelResource: 'amData.computerHardwareTabSpec.computerType', format:  Format.ShortStringOrNA },
      smcVersion: { labelResource: 'amData.computerHardwareTabSpec.smcVersion', format:  Format.StringOrNA },
      computerAge: { labelResource: 'amData.computerHardwareTabSpec.computerAge', format:  Format.IntervalInSeconds },
      computerProductionDate: { labelResource: 'amData.computerHardwareTabSpec.computerProductionDate', format:  Format.DateLocal },
      computerProductionFactory: { labelResource: 'amData.computerHardwareTabSpec.computerProductionFactory', format:  Format.StringOrNA },
      appleProductName: { labelResource: 'amData.computerHardwareTabSpec.appleProductName', format:  Format.StringOrNA },
      applePurchaseDate: { labelResource: 'amData.computerHardwareTabSpec.applePurchaseDate', format:  Format.DateLocal },
      appleWarrantyInfo: { labelResource: 'amData.computerHardwareTabSpec.appleWarrantyInfo', format:  Format.StringOrNA },
      appleWarrantyEnd: { labelResource: 'amData.computerHardwareTabSpec.appleWarrantyEnd', format:  Format.StringOrNA },
      swapSpaceEncrypted: { labelResource: 'amData.computerHardwareTabSpec.swapSpaceEncrypted', format: Format.BooleanOrNA }
    },

    resource: [
      //Both Platforms
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'computerSerialNumber',
        guid: 'CD1A5E9A-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
      },
      {
        attr: 'primaryMacAddress',
        guid: 'EDF77D4F-CA13-11D9-AECD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'dateTime',
        guid: 'D5E5AA04-C944-11D9-A26F-000D93B66ADA',
        type: Date
      },
      {
        attr: 'bootRomInformation',
        guid: '98364E58-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
      },
      {
        attr: 'memorySlots',
        guid: '3258B279-CA14-11D9-AECD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'memoryModuleCount',
        guid: '32596274-CA14-11D9-AECD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'physicalMemory',
        guid: 'EDF7F16A-CA13-11D9-AECD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'swapSpaceTotal',
        guid: '5A1DCD4C-C358-405B-9BCB-5A458BDB2195',
        type: Number
      },
      {
        attr: 'swapSpaceUsed',
        guid: '807E4FF8-780A-480C-BCAF-FC4590066F03',
        type: Number
      },
      {
        attr: 'swapSpaceFree',
        guid: 'ACB1F304-C839-4EA9-924A-A9E2EDDB0F1A',
        type: Number
      },
      {
        attr: 'volumeCount',
        guid: 'EEC817CA-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'ataDeviceCount',
        guid: 'EEC95988-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'scsiDeviceCount',
        guid: 'EECCFEE7-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'fireWireDeviceCount',
        guid: 'EECE23AC-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'usbDeviceCount',
        guid: 'EECFBB5B-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'pciDeviceCount',
        guid: 'EECBC71C-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'displayCount',
        guid: 'EECA8CC2-CADF-11D9-BACB-000D93B66ADA',
        type: Number
      },
      {
        attr: 'osPlatform',
        guid: '671D1208-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'osPlatformNumber',
        guid: '5A34E172-BD0F-4AD3-AB80-5C03F344843A',
        type: Number
      },
      //PC specific
      {
        attr: 'biosVendor',
        guid: '5877F59E-B63E-4703-A529-5BEA1E997C77',
        type: String
      },
      {
        attr: 'biosData',
        guid: '6F6AE98B-5D39-4AEE-B61B-8E08463EBDD9',
        type: Date
      },
      {
        attr: 'biosVersion',
        guid: 'E983BF52-660B-4A3B-A006-7DEDD3C540F7',
        type: String
      },
      {
        attr: 'biosType',
        guid: 'B3A649A3-61C7-438E-B1A5-5EFC241B9497',
        type: String
      },
      {
        attr: 'smbiosVersion',
        guid: '91E59CB9-3C1A-4302-A5CD-E1EA15988DFA',
        type: String
      },
      {
        attr: 'mainboardManufacturer',
        guid: '1279FE58-5468-463B-96E5-CE5578FB3A88',
        type: String
      },
      {
        attr: 'mainboardProductName',
        guid: '2AF64685-50BE-47F3-BE12-DFE6CA75D8CD',
        type: String
      },
      {
        attr: 'mainboardSerialNumber',
        guid: 'F07530F3-A0BA-4A1C-9C96-148A0B874FF2',
        type: String
      },
      {
        attr: 'mainboardType',
        guid: 'C81D5EA6-ADCE-475A-9FDB-8BB6BD8B72F6',
        type: String
      },
      {
        attr: 'mainboardVersion',
        guid: '7CF3967D-49FB-4ADE-8C1B-59FA5D5CFC2B',
        type: String
      },
      {
        attr: 'mainboardAssetTag',
        guid: '1311AD65-8D30-43C1-89A2-2225B1FCBA77',
        type: String
      },
      {
        attr: 'systemEnclosureManufacturer',
        guid: 'C140537E-287A-4118-93F5-E7608CF9B7A1',
        type: String
      },
      {
        attr: 'systemEnclosureSerialNumber',
        guid: '4E298435-B385-4335-AA98-38C3A49B29EE',
        type: String
      },
      {
        attr: 'systemEnclosureType',
        guid: '1BA514AB-EAF0-4840-A279-EC49600E4BAA',
        type: String
      },
      {
        attr: 'systemEnclosureVersion',
        guid: '5F74D24A-8FAA-4B0A-8C86-3514CE745963',
        type: String
      },
      {
        attr: 'systemEnclosureAssetTag',
        guid: '26CF4B8A-20AC-4043-8970-68F1D02C7650',
        type: String
      },
      {
        attr: 'computerManufacturer',
        guid: 'AC515518-E4F4-4B59-826D-C11FD985B1E6',
        type: String
      },
      {
        attr: 'computerVersion',
        guid: 'E531B0CF-E838-4535-A6BF-A8AE1C1AA3BF',
        type: String
      },
      {
        attr: 'computerModel',
        guid: 'B6F789DB-06AF-449E-8FBD-7191FC5BAE85',
        type: String
      },
      {
        attr: 'computerServiceTag',
        guid: '41360B62-A8D3-43A2-802F-C8AEB3E55CF7',
        type: String
      },
      {
        attr: 'computerWarrantyInfo',
        guid: '7B3B73CB-A22C-4488-9D53-40B2F7534F83',
        type: String
      },
      {
        attr: 'computerWarrantyEnd',
        guid: '2C7B1ED8-FDCC-4FA2-962E-59CD3256BC6B',
        type: String
      },
      //Mac specific
      {
        attr: 'computerType',
        guid: 'CD19617B-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
      },
      {
        attr: 'smcVersion',
        guid: 'E82F2573-C789-4B4C-8A19-0646FFA0603E',
        type: String
      },
      {
        attr: 'computerAge',
        guid: '71B3EB97-8E81-448F-B332-B7F503BCE408',
        type: Date
      },
      {
        attr: 'computerProductionDate',
        guid: 'CD19B5BC-CA0B-11D9-AC5B-000D93B66ADA',
        type: Date
      },
      {
        attr: 'computerProductionFactory',
        guid: 'CD1A04E0-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
      },
      {
        attr: 'appleProductName',
        guid: 'A2042C08-CC05-45F9-A7BE-0E4B935AA4C2',
        type: String
      },
      {
        attr: 'applePurchaseDate',
        guid: 'AC47581B-4A18-4FE3-9327-2A7B743D7979',
        type: Date
      },
      {
        attr: 'appleWarrantyInfo',
        guid: '7B3B73CB-A22C-4488-9D53-40B2F7534F83',
        type: String
      },
      {
        attr: 'appleWarrantyEnd',
        guid: '2C7B1ED8-FDCC-4FA2-962E-59CD3256BC6B',
        type: String
      },
      {
        attr: 'swapSpaceEncrypted',
        guid: '057AEB82-F289-438D-B451-03EBEAAB2357',
        type: String
      }
    ],

    searchableNames: 'computerSerialNumber primaryMacAddress dateTime bootRomInformation memorySlots memoryModuleCount physicalMemory swapSpaceTotal swapSpaceUsed swapSpaceFree volumeCount ataDeviceCount scsiDeviceCount firewireDeviceCount usbDeviceCount pciDeviceCount displayCount osPlatform biosVendor biosData biosVersion smbiosVersion mainboardManufacturer mainboardProductName mainboardSerialNumber mainboardType mainboardVersion mainboardAssetTag systemEnclosureManufacturer systemEnclosureSerialNumber systemEnclosureType systemEnclosureVersion systemEnclosureAssetTag computerManufacturer computerVersion computerModel computerServiceTag computerWarrantyInfo computerWarrantyEnd computerType smcVersion computerAge computerProductionDate computerProductionFactory appleProductName applePurchaseDate appleWarrantyInfo appleWarrantyEnd swapSpaceEncrypted'.w()

  }).create();
});
