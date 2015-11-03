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
    idNames: 'profileDisplayname profileInstallDate'.w(),

    format: {
      id: Format.ID,
      profileDisplayName: { labelResource: 'amData.computerInstalledProfileSpec.profileDisplayName', format: Format.StringOrNA },
      profileType: { labelResource: 'amData.computerInstalledProfileSpec.profileType', format: Format.StringOrNA },
      profileIdentifier: { labelResource: 'amData.computerInstalledProfileSpec.profileIdentifier', format: Format.StringOrNA },
      profileInstallDate: { labelResource: 'amData.computerInstalledProfileSpec.profileInstallDate', format:  Format.TimeLocal },
      profileOrganization: { labelResource: 'amData.computerInstalledProfileSpec.profileOrganization', format: Format.StringOrNA },
      profileUninstallPolicy: { labelResource: 'amData.computerInstalledProfileSpec.profileUninstallPolicy', format: Format.StringOrNA },
      profileUser: { labelResource: 'amData.computerInstalledProfileSpec.profileUser', format: Format.StringOrNA },
      profileVerificationState: { labelResource: 'amData.computerInstalledProfileSpec.profileVerificationState', format: Format.StringOrNA },
      profileDescription: { labelResource: 'amData.computerInstalledProfileSpec.profileDescription', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'profileDisplayName',
        guid: '19E52115-792C-4A36-9B18-F656C6D1A2C6',
        type: String
      },
      {
        attr: 'profileType',
        guid: '82DFD235-2A3D-4E54-AC67-0D90AA7EAD8B',
        type: Number
      },
      {
        attr: 'profileIdentifier',
        guid: '6669BB53-6227-4B60-9862-9386F2F0A161',
        type: String
      },
      {
        attr: 'profileInstallDate',
        guid: '638DE45A-76DF-48B3-872D-8D2E485D92DB',
        type: Date
      },
      {
        attr: 'profileOrganization',
        guid: '1F2844AC-E455-4B0B-9E92-B907DE321EF3',
        type: String
      },
      {
        attr: 'profileUninstallPolicy',
        guid: 'F99954DF-18D5-48BD-8510-EBA518F21776',
        type: String
      },
      {
        attr: 'profileUser',
        guid: '00EEFA33-B6AD-4E9C-B6CE-9A6241D67889',
        type: String
      },
      {
        attr: 'profileVerificationState',
        guid: '45F75EE9-E0F9-447C-AE09-61C4FDEC5995',
        type: String
      },
      {
        attr: 'profileDescription',
        guid: '74CF6015-97A6-4D13-BAF7-E078D3A7D8A8',
        type: String
      }
    ],

    searchableNames: 'profileDisplayName profileType profileIdentifier profileInstallDate profileOrganization profileUninstallPolicy profileUser profileVerificationState profileDescription'.w()

  }).create();
});
