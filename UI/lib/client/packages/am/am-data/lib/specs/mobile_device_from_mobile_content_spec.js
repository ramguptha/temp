define([
  'ember',
  '../am_spec',
  '../am_formats'
], function (
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.create({
    format: {
      id: Format.ID,
      name: Format.LongString,
      model: Format.StringOrNA,
      osVersion: { label: 'OS Version', format: Format.OSVersion },
      serialNumber: Format.StringOrNA,
      phoneNumber: Format.ShortStringOrNA,
      lastContact: Format.TimeLocal,
      simIccId: Format.StringOrNA,
      imei: Format.StringOrNA,
      osPlatform: { label: 'OS Type', format: Format.ShortStringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: '39f3f074-b8a2-4df1-ac02-eb1f25f3f98e',
        type: Number
      },
      {
        attr: 'name',
        guid: 'FE5A9F56-228C-4BDA-99EC-8666292CB5C1',
        type: String
      },
      {
        attr: 'model',
        guid: '61479324-9E16-46FD-85E5-68F9865A7D6D',
        type: String
      },
      {
        attr: 'osVersion',
        guid: '1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'serialNumber',
        guid: 'B20868B8-CAEA-446B-BE8D-BEC97368E839',
        type: String
      },
      {
        attr: 'phoneNumber',
        guid: 'CE678571-F939-4C26-8189-6B246BD46A42',
        type: String
      },
      {
        attr: 'lastContact',
        guid: '4A8A81E0-0159-471D-B8D3-32E316CB81EF',
        type: Date
      },
      {
        attr: 'simIccId',
        guid: '4F4C3251-C024-4CB2-8531-87188F41A3BF',
        type: String
      },
      {
        attr: 'imei',
        guid: '13A9A3AF-7E98-4C08-BDE3-3384AED04E61',
        type: String
      },
      {
        attr: 'osPlatform',
        guid: '8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5',
        type: String
      },
      {
        attr: 'osPlatformEnum', // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
        guid: 'AE64A047-ACF2-40E2-B0A3-3F5565150FFA',
        type: Number
      }
    ],

    searchableNames: 'name model osVersion serialNumber phoneNumber lastContact simIccId imei osPlatform'.w()
  });
});