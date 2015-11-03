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

  return AmSpec.create({
    format: {
      id: Format.ID,
      guid: Format.Guid,
      name: { labelResource: 'amData.inHouseApplicationFromPolicySpec.name', format: Format.String },
      osPlatform: { labelResource: 'amData.thirdPartyApplicationsSpec.osPlatform', format: Format.SearchableIcon },
      version: { labelResource: 'amData.inHouseApplicationFromPolicySpec.version', format: Format.ShortStringOrNA },
      buildNumber: { labelResource: 'amData.inHouseApplicationFromPolicySpec.buildNumber', format: Format.BuildNumber },
      size: { labelResource: 'amData.inHouseApplicationFromPolicySpec.size', format: Format.Bytes },
      shortDescription: { labelResource: 'amData.inHouseApplicationFromPolicySpec.shortDescription', format: Format.StringOrNA },
      longDescription: { labelResource: 'amData.inHouseApplicationFromPolicySpec.longDescription', format: Format.LongStringOrNA },
      bundleIdentifier: { labelResource: 'amData.inHouseApplicationFromPolicySpec.bundleIdentifier', format: Format.StringOrNA },
      packageName: { labelResource: 'amData.inHouseApplicationFromPolicySpec.packageName', format: Format.StringOrNA },
      minOsVersion: { labelResource: 'amData.inHouseApplicationFromPolicySpec.minOsVersion', format: Format.OSVersionPlus },
      isUniversal: { labelResource: 'amData.inHouseApplicationFromPolicySpec.isUniversal', format: Format.Boolean },
      supportedDevices: { labelResource: 'amData.inHouseApplicationFromPolicySpec.supportedDevices', format: Format.StringOrNA },
      provisioningProfile: { labelResource: 'amData.inHouseApplicationFromPolicySpec.provisioningProfile', format: Format.StringOrNA },
      provProfileExpiryDate: { labelResource: 'amData.inHouseApplicationFromPolicySpec.provProfileExpiryDate', format: Format.TimeLocal },
      assignmentRule: { labelResource: 'amData.inHouseApplicationFromPolicySpec.assignmentRule', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: '3ac26070-b7eb-47f6-8a02-308aab8e931a',
        type: Number
      },
      {
        // Name of the in-house application.
        attr: 'name',
        guid: '89450ED3-7B11-41F8-AF11-AEE369CD26B8',
        type: String
      },
      {
        // The operating system for which the application contained in the package was written
        attr: 'osPlatform',
        guid: 'FB49385A-C934-4603-A0CA-A2AA80D4F168',
        type: Number
      },
      {
        // The numeric value of the operating system for which the application contained in the package was written
        attr: 'osPlatformEnum', // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
        guid: '3e2b3fba-3441-4641-8dca-7320e0e7568e',
        type: Number
      },
      {
        // The version number of the application contained in the package.
        attr: 'version',
        guid: '6E78FCA0-EE73-4192-9667-DD039CC09E4B',
        type: String
      },
      {
        // The build number of the application contained in the package.
        attr: 'buildNumber',
        guid: 'FC7D8846-B591-43B6-AA66-2700E73D589C',
        type: String
      },
      {
        // The size the app requires on the device after installation.
        attr: 'size',
        guid: 'B2C6F1BD-C45F-40C2-8832-3FFC166497D9',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        attr: 'shortDescription',
        guid: '8E25F84C-CD69-4E7E-9ABD-2107B2FB0E0F',
        type: String
      },
      {
        attr: 'longDescription',
        guid: '0260B24F-6F83-455D-AF22-D0CF22AF4B52',
        type: String
      },
      {
        // Bundle ID (for iOS, same as package name for Android apps_
        attr: 'bundleIdentifier',
        guid: '3234134A-E61B-4BD4-AA88-3A50CD07C2AB',
        type: String
      },
      {
        // Package name for Android apps
        attr: 'packageName',
        guid: '3234134A-E61B-4BD4-AA88-3A50CD07C2AB',
        type: String
      },
      {
        // The minimum version of the mobile operating system that this app requires to run.
        attr: 'minOsVersion',
        guid: '3BC895F6-034C-428F-958E-415A3C585246',
        type: String,
        presentationType: Format.OSVersion
      },
      // iOS specific
      {
        // Whether this app runs on all three iOS hardware platforms – iPhone, iPad, and iPod touch.
        attr: 'isUniversal',
        guid: 'F5AD1373-1933-4109-AC02-25E969723229',
        type: Boolean
      },
      {
        // A comma-separated list of the hardware platforms (iPhone, iPad, iPod touch) on which this app runs.
        attr: 'supportedDevices',
        guid: '26D2E337-83E7-49CF-ACC8-DB8FEB6C315B',
        type: String
      },
      {
        // The name of the provisioning profile for installing it.
        attr: 'provisioningProfile',
        guid: '843A6257-A254-42CF-BA7E-BDFAF5323774',
        type: String
      },
      {
        // The date the provisioning profile expires.
        attr: 'provProfileExpiryDate',
        guid: '790B88EA-9539-482A-B41C-3B1D9966B508',
        type: Date
      },
      {
        attr: 'assignmentRule',
        guid: '728959A8-FFF8-416C-BC8E-C8D84EE13C5D',
        type: String
      }
    ],

    searchableNames: 'name assignmentRule osPlatform version buildNumber size shortDescription bundleIdentifier minOsVersion isUniversal supportedDevices provisioningProfile provProfileExpiryDate'.w()
  });
});
