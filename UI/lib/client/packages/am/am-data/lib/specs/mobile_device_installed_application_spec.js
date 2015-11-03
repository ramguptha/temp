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
      id: Format.ID,
      name: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.name', format: Format.MediumString },
      versionString: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.versionString', format: Format.StringOrNA },
      buildNumber: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.buildNumber', format: Format.NumberOrNA },
      bundleIdentifier: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.bundleIdentifier', format: Format.MediumStringOrNA },
      packageName: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.packageName', format: Format.MediumStringOrNA },
      appSize: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.appSize', format: Format.Bytes },
      appStatus: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.appStatus', format: Format.StringOrNA },
      dataSize: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.dataSize', format: Format.Bytes },
      preventDataBackup: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.preventDataBackup', format: Format.BooleanOrNA },
      boundToMDM: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.boundToMDM', format: Format.BooleanOrNA },
      appInstallDirectory: { labelResource: 'amData.mobileDeviceInstalledApplicationSpec.appInstallDirectory', format: Format.LongString }
    },

    resource: [
      {
        attr: 'id',
        guid: '05f36c10-41f6-444a-b3ce-0a63d5806ab6',
        type: Number
      },
      {
        // The name of the installed application.
        attr: 'name',
        guid: '5C7C9375-88D7-479F-A27A-4C1E038E8746',
        type: String
      },
      {
        // The version number / string of the application.
        attr: 'versionString',
        guid: 'A63444E6-8194-48E0-B23C-FDC5F69417B3',
        type: String
      },
      {
        // The build number of the application.
        attr: 'buildNumber',
        guid: 'D08F3C15-9DC0-4A12-8E35-A95570A9CEF0',
        type: String
      },
      {
        // Bundle Id for iOS
        attr: 'bundleIdentifier',
        guid: '233FF13A-0A51-422E-85E5-FF19281B3966',
        type: String
      },
      {
        // Package name for Android apps
        attr: 'packageName',
        guid: '233FF13A-0A51-422E-85E5-FF19281B3966',
        type: String
      },
      {
        // The size of the application the app code (or APK size for Android apps).
        attr: 'appSize',
        guid: '44E49BBC-9950-455E-A717-0F209BDDB2A3',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        // The amount of data memory the installed app uses.
        attr: 'dataSize',
        guid: 'ACEA2AB5-297C-4173-8896-DC7641A1E3ED',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        // The app status, whether Managed.
        attr: 'appStatus',
        guid: '9C23FED5-3244-4A06-A104-66BAA60A454E',
        type: String
      },
      {
        // The application data of this app is not included in standard device backups via iTunes.
        // This is always false for Android apps.
        attr: 'preventDataBackup',
        guid: 'C63228D6-AC6A-46DC-8F53-155C1D4CCFDA',
        type: Boolean
      },
      {
        // Whether the application is bound to MDM so that it is removed when MDM is removed
        attr: 'boundToMDM',
        guid: 'D583EF89-B803-440B-BA10-37C29B3EF47D',
        type: Boolean
      },
      {
        // Directory where the app is installed (for Android apps)
        attr: 'appInstallDirectory',
        guid: 'CEC0828A-CB27-44F7-9986-824D3A80F23F',
        type: Boolean
      }
    ],

    searchableNames: 'name versionString buildNumber bundleIdentifier appSize dataSize appStatus preventDataBackup boundToMDM appInstallDirectory'.w()
  }).create();
});
