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
      name: { labelResource: 'amData.mobileDeviceSpec.name', format: Format.LongString },
      model: { labelResource: 'amData.mobileDeviceSpec.model', format: Format.StringOrNA },
      osVersion: { labelResource: 'amData.mobileDeviceSpec.osVersion', format: Format.OSVersion },
      serialNumber: { labelResource: 'amData.mobileDeviceSpec.serialNumber', format: Format.StringOrNA },
      lastContact:  { labelResource: 'amData.mobileDeviceSpec.lastContact', format: Format.TimeLocal },
      osPlatform: { labelResource: 'amData.mobileDeviceSpec.osPlatform', format: Format.ShortStringOrNA },
      modelNumber: Format.StringOrNA,
      isManaged: { labelResource: 'amData.mobileDeviceSpec.isManaged', format: Format.Boolean },
      absoluteAppsVersion: { labelResource: 'amData.mobileDeviceSpec.absoluteAppsVersion', format: Format.OSVersion },
      dataRoamingEnabled: { labelResource: 'amData.mobileDeviceSpec.dataRoamingEnabled', format: Format.BooleanOrNA },
      voiceRoamingEnabled: { labelResource: 'amData.mobileDeviceSpec.voiceRoamingEnabled', format: Format.BooleanOrNA },
      isPasscodePresent: { labelResource: 'amData.mobileDeviceSpec.isPasscodePresent', format: Format.Boolean },
      osBuildNumber: { labelResource: 'amData.mobileDeviceSpec.osBuildNumber', format: Format.StringOrNA },
      publicIpAddress: Format.IPv4Address,
      cellIpAddress: Format.IPv4Address,
      wifiIpAddress: Format.IPv4Address,
      deviceCapacity: Format.BytesOrNA,
      availableCapacity: Format.BytesOrNA,
      internalStorageAvailable: Format.BytesOrNA,
      internalStorageTotal: Format.BytesOrNA,
      sdcard1Available: Format.BytesOrNA,
      sdcard1Total: Format.BytesOrNA,
      sdcard2Available: Format.BytesOrNA,
      sdcard2Total: Format.BytesOrNA,
      cacheAvailable: Format.BytesOrNA,
      cacheTotal: Format.BytesOrNA,
      memoryAvailable: Format.BytesOrNA,
      memoryTotal: Format.BytesOrNA,
      storageAvailable: Format.BytesOrNA,
      storageTotal: Format.BytesOrNA,
      batteryLevel: Format.Number,
      isGpsCapable: { labelResource: 'amData.mobileDeviceSpec.isGpsCapable', format: Format.Boolean },
      isJailBroken: { labelResource: 'amData.mobileDeviceSpec.isJailBroken', format: Format.Boolean },
      isMdmProfileUpToDate: { labelResource: 'amData.mobileDeviceSpec.isMdmProfileUpToDate', format: Format.BooleanOrNA },
      hasPersistence: Format.Boolean,
      isTablet: { labelResource: 'amData.mobileDeviceSpec.isTablet', format: Format.Boolean },
      isPasscodeCompliant: { labelResource: 'amData.mobileDeviceSpec.isPasscodeCompliant', format: Format.Boolean },
      isPasscodeCompliantWithProfiles: { labelResource: 'amData.mobileDeviceSpec.isPasscodeCompliantWithProfiles', format: Format.Boolean},
      cellularTechnology: {labelResource: 'amData.mobileDeviceSpec.cellularTechnology', format: Format.ShortStringOrNA },
      cellularTechnologyNumeric: Format.Number,
      isRoaming: Format.Boolean,
      cpuSpeed: Format.ClockSpeed,
      displayResolution: Format.StringOrNA,
      lastInfoUpdate: Format.TimeLocal,
      lastInstalledSwUpdate: Format.TimeLocal,
      lastConfigProfileUpdate: Format.TimeLocal,
      lastCertificateUpdate: Format.TimeLocal,
      lastProvisioningProfileUpdate: Format.TimeLocal,
      age: Format.IntervalInSeconds,
      productionDate: Format.DateLocal,
      phoneNumber: Format.ShortStringOrNA,
      wifiNetwork: Format.StringOrNA,
      imei: Format.StringOrNA,
      modemFirmwareVersion: Format.StringOrNA,
      cpuName: Format.StringOrNA,
      bluetoothMacAddress: Format.StringOrNA,
      wifiMacAddress: Format.StringOrNA,
      currentCarrierNetwork: Format.StringOrNA,
      homeNetwork: Format.StringOrNA,
      currentMcc: Format.StringOrNA,
      currentMnc: Format.StringOrNA,
      homeMcc: Format.StringOrNA,
      homeMnc: Format.StringOrNA,
      cellularNetworkType: Format.StringOrNA,
      carrierSettingsVersion: Format.StringOrNA,
      simIccId: Format.StringOrNA,
      imeiSv: Format.StringOrNA,
      productName: Format.StringOrNA,
      board: Format.StringOrNA,
      brand: Format.StringOrNA,
      deviceInfo: Format.StringOrNA,
      udid: Format.StringOrNA,
      deviceGUID:  Format.StringOrNA,
      identity: Format.StringOrNA,
      osLanguage: Format.StringOrNA,
      lastPolicyUpdate:  Format.TimeLocal,
      enableOutboundSMS: Format.BooleanOrNA,
      numberOfFoldersSynched: Format.NumberOrNA,
      remoteWipeSupported: Format.BooleanOrNA,
      remoteWipeStatus: Format.StringOrNA,
      remoteWipeStatusNote: Format.StringOrNA,
      wipeAckTime: Format.TimeLocal,
      wipeRequestTime: Format.TimeLocal,
      wipeSentTime: Format.TimeLocal,
      lastWipeRequestor: Format.StringOrNA,
      accessState: Format.StringOrNA,
      accessStateReason: Format.StringOrNA,
      ownership: Format.Ownership,
      ownershipNumeric: Format.Number,
      organizationName: Format.StringOrNA,
      organizationPhone: Format.StringOrNA,
      organizationEMail: Format.StringOrNA,
      organizationAddress: Format.StringOrNA,
      organizationCustom: Format.StringOrNA,
      hardwareEncryption: Format.StringOrNA
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
        attr: 'lastContact',
        guid: '4A8A81E0-0159-471D-B8D3-32E316CB81EF',
        type: Date
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
      },
      {
        attr: 'isManaged',
        guid: '26B03C68-0BF5-41ED-AD06-85903D5FBDFE',
        type: Boolean
      },
      {
        attr: 'absoluteAppsVersion',
        guid: '00C2627C-E3D9-4C50-8948-0D96DDB36ACF',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'cellularTechnology',
        guid: '57856DAA-29CA-4721-B68B-101E321D30B6',
        type: String
      },
      {
        attr: 'cellularTechnologyNumeric',
        guid: '30CAB748-A9B1-4B82-A333-DBCDD1A61956',
        type: Number
      },
      {
        attr: 'dataRoamingEnabled',
        guid: 'D18E5EEA-9468-4B91-B7D0-0FA44BAFEA9B',
        type: Boolean
      },
      {
        attr: 'voiceRoamingEnabled',
        guid: '86249F65-ED6F-4866-A503-68ABD46C3E5A',
        type: Boolean
      },
      {
        attr: 'isPasscodePresent',
        guid: 'F5384281-6943-487F-A0F8-FC4EA254C489',
        type: Boolean
      },
      {
        attr: 'manufacturer',
        guid: '408A8D10-D908-4A9E-A00C-3FFB27E7EA81',
        type: String
      },
      {
        attr: 'modelNumber',
        guid: '7D6F0E3D-D704-4907-B9CF-9AA96EC7E222',
        type: String
      },
      {
        attr: 'osBuildNumber',
        guid: 'EFD8C1F6-770D-4C5B-B502-AE74A50B1D42',
        type: String
      },
      {
        attr: 'ownership',
        guid: 'ED7A22F2-1DF5-4CE6-88EC-04EAD7DE5537',
        type: String
      },
      {
        attr: 'ownershipNumeric',
        guid: 'AF5048CA-DB6A-4E6B-9125-A0E03405ECF2',
        type: Number
      },
      {
        attr: 'udid',
        guid: '3110ECD0-3F33-4A13-93EB-7CAE925A16EB',
        type: String
      },
      {
        attr: 'status',
        guid: '0AE8D853-F25E-483F-8C19-CF844984809D',
        type: Number
      },
      {
        attr: 'phoneNumber',
        guid: 'CE678571-F939-4C26-8189-6B246BD46A42',
        type: String
      },
      
      {
        attr: 'imei',
        guid: '13A9A3AF-7E98-4C08-BDE3-3384AED04E61',
        type: String
      },
      {
        attr: 'modemFirmwareVersion',
        guid: 'F31F25E8-4EB0-4D52-8A2F-DC965457A310',
        type: String
      },
      {
        attr: 'warrantyInfo',
        guid: '526A7065-3CAA-4B5D-AE29-1E30D0261928',
        type: String
      },
      {
        attr: 'warrantyEnd',
        guid: 'F6F8BC2C-8E87-4D02-9FED-4C951D217B18',
        type: Date
      },
      {
        attr: 'absoluteAppsBuildNo',
        guid: 'A19FE28D-F322-4FFC-9F4C-1F3D96E21BB6',
        type: String,
        presentationType: Format.NumberOrNA
      },
      {
        attr: 'cpuName',
        guid: '4DF6B545-CA3F-4EBD-B6DD-8ECC3E3C776E',
        type: String
      },
      {
        attr: 'cpuSpeed',
        guid: 'A84D4A52-718A-4F25-A0D7-056FF9B726EB',
        type: Number
      },
      {
        attr: 'deviceCapacity',
        guid: '436762EE-9925-4AEB-9932-1DF4D5058DA3',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        attr: 'availableCapacity',
        guid: 'C0C3CD62-7182-4FCF-A321-2AE08B0AB5EA',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        attr: 'displayResolution',
        guid: '4B21C643-3CB8-4E3D-9979-3D5A8674616C',
        type: String
      },
      {
        attr: 'bluetoothMacAddress',
        guid: '4AD58389-E909-4BBB-A0F6-1750DEE40A02',
        type: String
      },
      {
        attr: 'wifiMacAddress',
        guid: '50C20BDE-BA6F-499E-9E9B-85B2F7B98A9C',
        type: String
      },
      {
        attr: 'publicIpAddress',
        guid: '04D1D3C0-1251-412E-87C6-32EE6B8FC81C',
        type: Number
      },
      {
        attr: 'cellIpAddress',
        guid: 'BFA5FDC4-E2C6-4ACE-B8D5-18AB6ED006D3',
        type: Number
      },
      {
        attr: 'wifiIpAddress',
        guid: 'B237140F-DDC9-4447-A4EE-9A5C97D5E8BF',
        type: Number
      },
      {
        attr: 'wifiNetwork',
        guid: '993F69DB-0655-48A2-8424-E6CE14D8EB09',
        type: String
      },
      {
        attr: 'isGpsCapable',
        guid: '579705E2-7D8A-4D76-81A9-9316CA6636BA',
        type: Boolean
      },
      {
        attr: 'batteryLevel',
        guid: 'AB240972-4F99-497D-B730-F1F905E677B0',
        type: Number
      },
      {
        attr: 'isJailBroken',
        guid: 'F9B2CC21-8E0A-48A8-BA72-B6102396D210',
        type: Boolean
      },
      {
        attr: 'recordCreated',
        guid: '32D4B717-BA81-4AE2-B2EB-A39E28683E75',
        type: Date
      },
      {
        attr: 'isMdmProfileUpToDate', // iOS only?
        guid: 'F9F14DB1-733C-4EE0-A42F-23C63F1AE6E7',
        type: Boolean
      },
      {
        attr: 'productionDate',
        guid: '57A443D5-0D3D-4F2C-A5F2-CC8EB70A6FD2',
        type: Date
      },
      {
        attr: 'age', // in seconds
        guid: '09851292-1AF4-43D4-9405-20A1E96BD4BE',
        type: Number
      },
      {
        attr: 'hasPersistence', // Currently Android only for mobile devices
        guid: '1DD4A442-31EC-47FE-AD79-E71240A54A4A',
        type: Boolean
      },
      {
        attr: 'kernelVersion', // Android only
        guid: 'F318B7CF-49BB-4E8E-AC84-D98F9C176432',
        type: String
      },
      {
        attr: 'isTablet',
        guid: 'FA7F74E7-7E68-4C6A-ABFE-F8EFABD2F291',
        type: Boolean
      },
      {
        attr: 'hardwareEncryption',
        guid: 'BA3396E4-8D3D-49CB-AB53-BF8FFEDF6CDD',
        type: String
      },
      {
        attr: 'isPasscodeCompliant',
        guid: 'C8BFD781-0A1A-4B3A-972F-73CEDE36E5E6',
        type: Boolean
      },
      {
        attr: 'isPasscodeCompliantWithProfiles',
        guid: 'D441BFB3-0BE6-4AEF-9231-2C20AF2D8D8C',
        type: Boolean
      },
      
      {
        attr: 'isRoaming',
        guid: '356CD21B-E7B0-45D4-B076-D6987E5894C9',
        type: Boolean
      },
      
      {
        attr: 'currentCarrierNetwork',
        guid: 'B78A5D4B-C334-4A01-B2E6-EF9E8CE23858',
        type: String
      },
      {
        attr: 'homeNetwork',
        guid: 'CCB83C55-6937-47EF-B50B-2604D4F70C2C',
        type: String
      },
      {
        attr: 'currentMcc',
        guid: 'F7BB7DD2-8C6A-4A64-996A-F6F577223D42',
        type: String
      },
      {
        attr: 'currentMnc',
        guid: '6EF63EA0-8B4E-4F90-A2F3-DAFB90894A85',
        type: String
      },
      {
        attr: 'homeMcc',
        guid: '0019EAE6-8229-4B35-9E6A-8BC24E311452',
        type: String
      },
      {
        attr: 'homeMnc',
        guid: '6958FE5D-A9E7-4BEC-9FA4-06C4229656D0',
        type: String
      },
      {
        attr: 'cellularNetworkType',// EDGE, GPRS, UMTS etc, Only available for Android devices
        guid: 'C208DD93-9183-4D8E-80CD-535C9A790316',
        type: String
      },
      {
        attr: 'carrierSettingsVersion',
        guid: '46B236E1-0C38-42A7-A440-EFA32FA1472B',
        type: String
      },
      {
        attr: 'simIccId',
        guid: '4F4C3251-C024-4CB2-8531-87188F41A3BF',
        type: String
      },
      {
        attr: 'imeiSv', // Android only.
        guid: 'A00821D7-4BAA-4127-8BFC-638EA99DD305',
        type: String
      },
      {
        attr: 'productName', // Android only. From 'product' system property
        guid: 'B5A40A74-EE51-4FF4-B124-AABF85A54A9B',
        type: String
      },
      {
        attr: 'board', // Name or type of motherboard. Applicable to Mobile devices?
        guid: 'D0C48404-CF64-44E4-B213-5F21F935CF85',
        type: String
      },
      {
        attr: 'brand', // Android only.
        guid: '9982EB46-B451-4486-8818-8CABFDFD9B65',
        type: String
      },
      {
        attr: 'deviceInfo', // Android only. Additional info the manufacturer puts in the 'device' system property. Useful?
        guid: '48F6E858-5EC6-428C-B3FF-EC72ECE02875',
        type: String
      },
      {
        attr: 'internalStorageAvailable', // Android only.
        guid: '2DAB6D9A-0565-4EA7-8DFE-BF2C5136BDF1',
        type: Number
      },
      {
        attr: 'internalStorageTotal', // Android only.
        guid: '61813AFE-0DE8-423D-AF83-AFB32896F8B5',
        type: Number
      },
      {
        attr: 'sdcard1Available', // Android only. Available storage capacity on 1st (non-removable) SD card
        guid: '041E2F5F-648A-4E38-B1BB-FE4533C06F55',
        type: Number
      },
      {
        attr: 'sdcard1Total', // Android only. Total storage on 1st (non-removable) SD card
        guid: 'E65FE2B8-7E55-4FA0-8032-0B46F4DE2F45',
        type: Number
      },
      {
        attr: 'sdcard2Available', // Android only. Available storage capacity on 2nd (removable) SD card
        guid: '12F12508-6BCF-4BEE-A8E5-BF959700FC69',
        type: Number
      },
      {
        attr: 'sdcard2Total', // Android only. Total storage on 2nd (removable) SD card
        guid: 'D2D946A8-E375-418C-AEC3-B2D757E76597',
        type: Number
      },
      {
        attr: 'cacheAvailable', // Android only. Amount of cache memory free.
        guid: '46F86C2A-4938-4D61-BD8B-2355D4D9C4BC',
        type: Number
      },
      {
        attr: 'cacheTotal', // Android only. Total amount of cache memory.
        guid: 'F337E930-3F7C-4268-BE14-3E7CF8A283FD',
        type: Number
      },
      {
        attr: 'memoryAvailable', // Android only. Amount of RAM free.
        guid: 'B9EDEE94-F424-486F-AABB-2B00428919CA',
        type: Number
      },
      {
        attr: 'memoryTotal', // Android only. Total amount of RAM.
        guid: '3B85F80B-FD65-4D37-A7BF-C5E10785B70C',
        type: Number
      },
      {
        attr: 'storageAvailable', // Android only. Amount of storage memory (excluding SD cards) free.
        guid: '84AC1286-71E4-48EA-92A1-7E8B7004A7DD',
        type: Number
      },
      {
        attr: 'storageTotal', // Android only. Total amount of storage memory (excluding SD cards).
        guid: '7DF3F627-28DF-4400-8731-7EE302E74B26',
        type: Number
      },
      {
        // The most recent time at which the information stored on the server for the mobile device changed.
        attr: 'lastInfoUpdate',
        guid: 'A4C6561D-6501-4A4D-BA06-458D08D58219',
        type: Date
      },
      {
        // The most recent time at which the information stored on the server for the software installed on the mobile device changed
        attr: 'lastInstalledSwUpdate',
        guid: 'B1839B88-559B-43AB-97CF-B52E48E89701',
        type: Date
      },
      {
        // The most recent time at which the information stored on the server for the configuration profiles installed on the mobile device changed."
        attr: 'lastConfigProfileUpdate',
        guid: 'D3BFF608-1B09-4756-9AD8-F4E23EF4D245',
        type: Date
      },
      {
        // The most recent time at which the information stored on the server for the certificates installed on the mobile device changed.
        attr: 'lastCertificateUpdate',
        guid: '4BCB20E3-2D3C-41D8-B9BC-8C27E4424216',
        type: Date
      },
      {
        // The most recent time at which the information stored on the server for the provisioning profiles installed on the mobile device changed."
        attr: 'lastProvisioningProfileUpdate',
        guid: '9FD80B98-92B3-4692-A500-A20B0F6734E7',
        type: Date
      },
      // Added for Windows Phone
      {
        attr: 'lastPolicyUpdate',
        guid: '92C9F5F3-5EA8-4F2C-8B80-5973374E2198',
        type: Date
      },
      {
        attr: 'deviceGUID',
        guid: 'FA0FF09F-F91A-4B98-805D-F008900B1C06',
        type: String
      },
      {
        attr: 'identity',
        guid: 'ED9859FF-67D2-4449-AB7F-DAF18512E18D',
        type: String
      },
      {
        attr: 'osLanguage',
        guid: 'FB4815BD-3D22-4BFF-8D86-D32DE90E9C16',
        type: String
      },
      {
        attr: 'enableOutboundSMS',
        guid: 'F045116C-8709-4930-8BEB-FB28A09F4173',
        type: Boolean
      },
      {
        attr: 'remoteWipeSupported',
        guid: '5A9DD3C0-57EB-4A10-AA6E-071721AFF122',
        type: Boolean
      },
      {
        attr: 'wipeAckTime',
        guid: 'F08661F4-ACCF-4DF6-A3B0-DF6208B08D8C',
        type: Date
      },
      {
        attr: 'wipeRequestTime',
        guid: '3FD31693-68CC-479C-9D1F-A462B0113A28',
        type: Date
      },
      {
        attr: 'wipeSentTime',
        guid: 'F89F16DF-04CF-432F-87D4-B08A7A689CA1',
        type: Date
      },
      {
        attr: 'lastWipeRequestor',
        guid: '5947962B-5D20-4310-A369-4AFEF79A8077',
        type: String
      },
      {
        attr: 'remoteWipeStatus',
        guid: '64E19432-4301-4193-BF63-1CA24AED1AD6',
        type: String
      },
      {
        attr: 'remoteWipeStatusNote',
        guid: 'ADA0E4B2-BAA1-4B08-8686-4F1740D4CB2E',
        type: String
      },
      // Exchange Server access / sync
      {
        attr: 'accessState',
        guid: '4112CC61-58B7-450F-87FB-4729EE170EA4',
        type: String
      },
      {
        attr: 'accessStateReason',
        guid: '0365297A-2BC8-4659-95CE-97DA42AA5740',
        type: String
      },
      {
        attr: 'numberOfFoldersSynched',
        guid: '29E2ED52-305C-4CB9-9151-90BB98F48D51',
        type: Number
      },
      {
        attr: 'enrollmentUser',
        guid: '3AB750A5-EAD9-4E0A-9EB2-F8D82EBF0E5D',
        type: String
      },
      {
        attr: 'enrollmentDomain',
        guid: '598EE05E-F121-467B-9AD1-F37FD0D2F924',
        type: String
      },
      //Organizational Info
      {
        attr: 'organizationName',
        guid: 'B63F9803-ED89-4317-A4A3-6FC4D1195790',
        type: String
      },
      {
        attr: 'organizationAddress',
        guid: '48AF6161-D263-4259-A61D-CBB0C3ACC096',
        type: String
      },
      {
        attr: 'organizationPhone',
        guid: '7D33539A-F61A-4A6A-A976-0BE481942809',
        type: String
      },
      {
        attr: 'organizationEMail',
        guid: '15D5E067-6D20-4129-A6F5-C821C16CFA10',
        type: String
      },
      {
        attr: 'organizationCustom',
        guid: 'B037D905-C4CA-493A-B5F1-CE37B16F3B6B',
        type: String
      },
      {
        attr: 'isSupervised',
        guid: 'C1E9E103-7891-4824-A79E-507107FF0091',
        type: Boolean
      }
    ],

    searchableNames: 'name model osVersion serialNumber lastContact osPlatform isManaged absoluteAppsVersion cellularTechnology dataRoamingEnabled voiceRoamingEnabled isPasscodePresent'.w()
  }).create();
});
