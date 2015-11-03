define([
  'ember',
  '../am_spec',
  '../am_formats',
  'formatter'
], function(
  Em,
  AmSpec,
  Format,
  Formatter
  ) {
  'use strict';

  // clone NumberOrNA and increase its width by a little bit
  var longNumberOrNA = JSON.parse(JSON.stringify(Format.NumberOrNA));
  longNumberOrNA.width = 125;
  longNumberOrNA.formatter = Formatter.formatNumberOrNA;

  return AmSpec.extend({
    format: {
      id: Format.ID,
      guid: Format.Guid,
      name: { labelResource: 'amData.thirdPartyApplicationsSpec.name', format: Format.String },
      osPlatform: { labelResource: 'amData.thirdPartyApplicationsSpec.osPlatform', format: Format.SearchableIcon },
      category: { labelResource: 'amData.thirdPartyApplicationsSpec.category', format: Format.ShortString },
      minOsVersion: { labelResource: 'amData.thirdPartyApplicationsSpec.minOsVersion', format: Format.OSVersionPlus },
      shortDescription: { labelResource: 'amData.thirdPartyApplicationsSpec.shortDescription', format: Format.StringOrNA },
      longDescription: { labelResource: 'amData.thirdPartyApplicationsSpec.longDescription', format: Format.LongStringOrNA },
      icon: { labelResource: 'amData.thirdPartyApplicationsSpec.icon', format: Format.SearchableIcon },
      appStoreURL: { labelResource: 'amData.thirdPartyApplicationsSpec.appStoreURL', format: Format.LongHyperlinkToNewPage },
      isUniversal: { labelResource: 'amData.thirdPartyApplicationsSpec.isUniversal', format: Format.BooleanOrNA },
      supportedDevices: { labelResource: 'amData.thirdPartyApplicationsSpec.supportedDevices', format: Format.StringOrNA },
      preventDataBackup: { labelResource: 'amData.thirdPartyApplicationsSpec.preventDataBackup', format: Format.BooleanOrNA },
      removeWhenMDMIsRemoved: { labelResource: 'amData.thirdPartyApplicationsSpec.removeWhenMDMIsRemoved', format: Format.BooleanOrNA },
      hasRedemptionCode: { labelResource: 'amData.thirdPartyApplicationsSpec.hasRedemptionCode', format: Format.NumberToBooleanOrNA  },
      vppCodesPurchased: { labelResource: 'amData.thirdPartyApplicationsSpec.vppCodesPurchased', format: Format.NumberOrNA },
      vppCodesRedeemed: { labelResource: 'amData.thirdPartyApplicationsSpec.vppCodesRedeemed', format: Format.NumberOrNA },
      vppCodesRemaining: { labelResource: 'amData.thirdPartyApplicationsSpec.vppCodesRemaining', format: Format.NumberOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'f8a332e7-90db-4483-a9d3-bf9c0a982ac2',
        type: Number
      },
      {
        // Name of the 3rd party application.
        attr: 'name',
        guid: '8DF6E81A-0FEF-446B-8403-301D2A4CC066',
        type: String
      },
      {
        // The operating system for which the application contained in the package was written
        attr: 'osPlatform',
        guid: 'DC5A1403-C78F-4B72-8206-05CC525B975B',
        type: String
      },
      {
        // Enumerated value of the operating system for which the application contained in the package was written
        attr: 'osPlatformEnum', // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
        guid: '45a4bb52-ae32-476b-951c-54cc53e7a8ab',
        type: Number
      },
      {
        // The category of the application package, as specified by the administrator.
        attr: 'category',
        guid: '745C0044-39B8-4E41-AC01-7612AD9BB209',
        type: String
      },
      {
        // The minimum version of the mobile operating system that this app requires to run.
        attr: 'minOsVersion',
        guid: '605E53AD-01F3-41DA-8760-4E7C924C9C2E',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'shortDescription',
        guid: 'B3E986B2-4AD7-4736-AEA4-7409F1BDC272',
        type: String
      },
      {
        attr: 'longDescription',
        guid: '76CAFF78-72D0-4EAC-A27F-FEF7B5BB5DE7',
        type: String
      },
      {
        // This has to be populated by a query to a separate endpoint /api/thirdpartyapps/{id}/icon
        attr: 'icon',
        guid: 'XXX',
        type: String
      },
      {
        // The URL of the app’s page in the App Store.
        attr: 'appStoreURL',
        guid: 'A7E42F93-0268-44BC-9658-E99B04ABF6D5',
        type: String
      },

      // iOS specific
      {
        // Whether this app runs on all three iOS hardware platforms – iPhone, iPad, and iPod touch.
        attr: 'isUniversal',
        guid: 'FCAE9AA6-7353-4757-8D0A-A181D01A8203',
        type: Boolean
      },
      {
        // A comma-separated list of the hardware platforms (iPhone, iPad, iPod touch) on which this app runs.
        attr: 'supportedDevices',
        guid: '9FB59B66-C327-4039-B53A-708B874E980F',
        type: String
      },
      {
        // The application data of this app is not included in standard device backups via iTunes.
        // This is always false for Android apps.
        attr: 'preventDataBackup',
        guid: '807BE3D0-96D6-43F2-A82C-5439B287860C',
        type: Boolean
      },
      {
        attr: 'removeWhenMDMIsRemoved',
        guid: '452BE116-436C-4262-8B0D-5CFDBE0DB349',
        type: Boolean
      },
      {
        // Whether it has a VPP codes (derived from vppCodesPurchased > 0)
        attr: 'hasRedemptionCode',
        guid: 'E14798AD-BEB9-4D9B-A956-E61331BE3F8F',
        type: Number
      },
      {
        // The total number of App Store volume purchase program codes that have been imported for this app.
        attr: 'vppCodesPurchased',
        guid: 'E14798AD-BEB9-4D9B-A956-E61331BE3F8F',
        type: Number
      },
      {
        // The number of Apple Store volume purchase program codes that have already been used to install a copy of
        // this app on an administered mobile device.
        attr: 'vppCodesRedeemed',
        guid: '6D0A8615-21D6-4610-95B6-7A6DED11BC46',
        type: Number
      },
      {
        // The remaining number of Apple Store volume purchase program codes that are available for installing this
        // app on an administered mobile device.
        attr: 'vppCodesRemaining',
        guid: '3870F52A-140E-4FD0-A2D0-5788238DFEF7',
        type: Number
      }
    ],

    //TODO grid remove (investigate if this mapping is needed or not)
    // for the sake of formatting icons it is fixed only using strings in the new grid's am_value_views
    /*mapRawResultSetData: function(query, rawData) {
      // Inject the context into the result set.
      var result = this._super(query, rawData);
      result.forEach(function(raw) {
        raw.osPlatform = raw.osPlatformEnum + '|' +  raw.osPlatform;
      });
      return result;
    },*/

    searchableNames: 'name icon osPlatform category minOsVersion isUniversal supportedDevices shortDescription preventDataBackup preventDataBackup removeWhenMDMIsRemoved vppCodesPurchased vppCodesRedeemed vppCodesRemaining'.w()
  }).create();
});
