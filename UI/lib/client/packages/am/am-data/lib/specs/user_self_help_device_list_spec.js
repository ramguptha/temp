define([
  'ember',
  '../am_spec_behaviour',
  '../am_formats',
  'packages/platform/locale-config'
  ], function(
  Em,
  AmSpecBehaviour,
  Format,
  LocaleConfig
  ) {
  'use strict';

  return AmSpecBehaviour.extend({
    names: ['name'],
    idNames: ['identifier'],

    format: {
      identifier: Format.ID,
      name: { labelResource: 'amData.mobileDeviceSpec.name', format: Format.LongString },
      phoneNumber: Format.ShortStringOrNA,
      passcodePresent: Format.Number,
      deviceModel: Format.StringOrNA,
      batteryLevel: Format.Percent,
      osVersion: Format.OSVersion,
      osPlatform: Format.ShortStringOrNA,
      deviceType: Format.Number,
      isTablet: Format.Number,
      deviceCapacity: Format.Bytes,
      isComputer: Format.Boolean,
      batteryMaxCapacity: Format.Number,
      batteryFullyCharged: Format.Number,
      batteryDesignCapacity: Format.Number,
      batteryCurrentCapacity: Format.Number,
      deviceSerialNumber: Format.ShortStringOrNA
    },


    resource: [
      {
        attr: 'identifier',
        sourceAttr:'identifier',
        type: String
      },
      {
        attr: 'name',
        sourceAttr:'name',
        type: String
      },
      {
        attr: 'phoneNumber',
        sourceAttr:'phoneNumber',
        type: String
      },
      {
        attr: 'passcodePresent',
        sourceAttr:'passcodePresent',
        type: Number
      },
      {
        attr: 'deviceModel',
        sourceAttr:'deviceModel',
        type: String
      },
      {
        attr: 'batteryLevel',
        sourceAttr:'batteryLevel',
        type: Number
      },
      {
        attr: 'osVersion',
        sourceAttr:'osVersion',
        type: Number
      },
      {
        attr: 'osPlatform',
        sourceAttr:'osPlatform',
        type: String
      },
      {
        attr: 'deviceType',
        sourceAttr:'deviceType',
        type: Number
      },
      {
        attr: 'isTablet',
        sourceAttr:'isTablet',
        type: Number
      },
      {
        attr: 'deviceCapacity',
        sourceAttr:'deviceCapacity',
        type: Number
      },
      {
        attr: 'isComputer',
        sourceAttr:'isComputer',
        type: Boolean
      },
      {
        attr: 'batteryCurrentCapacity',
        sourceAttr:'batteryCurrentCapacity',
        type: Number
      },
      {
        attr: 'batteryDesignCapacity',
        sourceAttr:'batteryDesignCapacity',
        type: Number
      },
      {
        attr: 'batteryFullyCharged',
        sourceAttr:'batteryFullyCharged',
        type: Number
      },
      {
        attr: 'batteryMaxCapacity',
        sourceAttr:'batteryMaxCapacity',
        type: Number
      },
      {
        attr: 'batteryLevelDate',
        sourceAttr:'batteryLevelDate',
        type: Date
      },
      {
        attr: 'deviceSerialNumber',
        sourceAttr:'deviceSerialNumber',
        type: String
      }

    ],

    mapRawResultSetData: function(query, rawData) {
      var transformedNames = rawData.userDevices ? rawData.userDevices.map(function(rawValue){
        return {
          identifier: rawValue.deviceIdentifier ? rawValue.deviceIdentifier : rawValue.agentSerial,
          name: rawValue.deviceName + '[****]' + rawValue.deviceType + '[****]' + rawValue.isTablet + '[****]' + rawValue.deviceModel,
          phoneNumber: rawValue.phoneNumber,
          passcodePresent: parseInt(rawValue.passcodePresent),
          deviceModel: rawValue.deviceModel,
          batteryLevel: rawValue.batteryLevel ? parseInt(rawValue.batteryLevel) : null,
          osVersion: parseInt(rawValue.osVersion),
          osPlatform: rawValue.osPlatform,
          deviceType: parseInt(rawValue.deviceType),
          isTablet: parseInt(rawValue.isTablet),
          deviceCapacity: parseInt(rawValue.deviceCapacity),
          batteryMaxCapacity: parseInt(rawValue.batteryMaxCapacity),
          batteryFullyCharged: parseInt(rawValue.batteryFullyCharged),
          batteryDesignCapacity: parseInt(rawValue.batteryDesignCapacity),
          batteryCurrentCapacity: parseInt(rawValue.batteryCurrentCapacity),
          isComputer: rawValue.agentSerial ? true : false,
          batteryLevelDate: LocaleConfig.momentLocal(rawValue.batteryLevelModifiedDate, 'YYYY-MM-DD[T]HH:mm:ssZ', true),
          deviceSerialNumber: rawValue.deviceSerialNumber
        };
      }) : null;

      return this._super(query, transformedNames ? transformedNames : []);
    },

    mapRawCounterData: function(query, rawData) {
      return rawData.userDevices ? rawData.userDevices.length : 0;
    }
  }).create();
});
