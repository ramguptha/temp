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
      osPlatform: { labelResource: 'amData.computerSystemSoftwareSpec.osPlatform', format: Format.ShortStringOrNA },
      osPlatformNumber: Format.Number,
      osVersion: { labelResource: 'amData.computerSystemSoftwareSpec.osVersion', format: Format.OSVersionComputer },
      osBuildNumber:  { labelResource: 'amData.computerSystemSoftwareSpec.osBuildNumber', format: Format.ShortStringOrNA },
      osLanguage: { labelResource: 'amData.computerSystemSoftwareSpec.osLanguage', format: Format.ShortStringOrNA },
      computerBootTime: { labelResource: 'amData.computerSystemSoftwareSpec.computerBootTime', format: Format.TimeLocal },
      computerUpTime: { labelResource: 'amData.computerSystemSoftwareSpec.computerUpTime', format: Format.IntervalInSeconds },
      currentUserName: { labelResource: 'amData.computerSystemSoftwareSpec.currentUserName', format: Format.ShortStringOrNA },
      currentUserAccount: { labelResource: 'amData.computerSystemSoftwareSpec.currentUserAccount', format: Format.ShortStringOrNA },
      currentUserIsAdmin: { labelResource: 'amData.computerSystemSoftwareSpec.currentUserIsAdmin', format: Format.BooleanOrNA },
      lastUserAccount: { labelResource: 'amData.computerSystemSoftwareSpec.lastUserAccount', format: Format.ShortStringOrNA },
      lastUserName: { labelResource: 'amData.computerSystemSoftwareSpec.lastUserName', format: Format.ShortStringOrNA },
      adComputerName: { labelResource: 'amData.computerSystemSoftwareSpec.adComputerName', format: Format.ShortStringOrNA },
      adComputerOrganizationalUnit: { labelResource: 'amData.computerSystemSoftwareSpec.adComputerOrganizationalUnit', format: Format.ShortStringOrNA },
      adComputerOrganizationalUnitPath: { labelResource: 'amData.computerSystemSoftwareSpec.adComputerOrganizationalUnitPath', format: Format.ShortStringOrNA },
      adUserOrganizationalUnit: { labelResource: 'amData.computerSystemSoftwareSpec.adUserOrganizationalUnit', format: Format.ShortStringOrNA },
      adUserOrganizationalUnitPath: { labelResource: 'amData.computerSystemSoftwareSpec.adUserOrganizationalUnitPath', format: Format.ShortStringOrNA },
      diskEncryptionProduct: { labelResource: 'amData.computerSystemSoftwareSpec.diskEncryptionProduct', format: Format.ShortStringOrNA },
      diskEncryptionVersion: { labelResource: 'amData.computerSystemSoftwareSpec.diskEncryptionVersion', format: Format.ShortStringOrNA },
      diskEncryptionStatus: { labelResource: 'amData.computerSystemSoftwareSpec.diskEncryptionStatus', format: Format.ShortStringOrNA },
      diskEncryptionAlgorithm: { labelResource: 'amData.computerSystemSoftwareSpec.diskEncryptionAlgorithm', format: Format.ShortStringOrNA },
      diskEncryptionKeySize: { labelResource: 'amData.computerSystemSoftwareSpec.diskEncryptionKeySize', format: Format.ShortStringOrNA },
      fileVaultSupported: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultSupported', format: Format.BooleanOrNA },
      fileVaultEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultEnabled', format: Format.BooleanOrNA },
      fileVaultAuthenticatedRestartSupported: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultAuthenticatedRestartSupported', format: Format.BooleanOrNA },
      fileVaultHasPersonalRecoveryKey: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultHasPersonalRecoveryKey', format: Format.BooleanOrNA },
      fileVaultHasInstitutionalRecoveryKey: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultHasInstitutionalRecoveryKey', format: Format.BooleanOrNA },
      fileVaultUnlockedUsingRecoveryKey: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultUnlockedUsingRecoveryKey', format: Format.BooleanOrNA },
      fileVaultRecoveryKeyStoredOnServer: { labelResource: 'amData.computerSystemSoftwareSpec.fileVaultRecoveryKeyStoredOnServer', format: Format.BooleanOrNA },
      gmtDelta: { labelResource: 'amData.computerSystemSoftwareSpec.gmtDelta', format: Format.ShortStringOrNA },
      daylightSavingTime: { labelResource: 'amData.computerSystemSoftwareSpec.daylightSavingTime', format: Format.BooleanOrNA },
      fastUserSwitchingEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.fastUserSwitchingEnabled', format: Format.BooleanOrNA },
      firewallEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.firewallEnabled', format: Format.BooleanOrNA },
      installedSoftwareCount: { labelResource: 'amData.computerSystemSoftwareSpec.installedSoftwareCount', format: Format.ShortStringOrNA },
      missingOsPatchesCount: { labelResource: 'amData.computerSystemSoftwareSpec.missingOsPatchesCount', format: Format.ShortStringOrNA },
      processCount: { labelResource: 'amData.computerSystemSoftwareSpec.processCount', format: Format.ShortStringOrNA },
      fileCount: { labelResource: 'amData.computerSystemSoftwareSpec.fileCount', format: Format.ShortStringOrNA },
      fontCount: { labelResource: 'amData.computerSystemSoftwareSpec.fontCount', format: Format.ShortStringOrNA },
      printerCount: { labelResource: 'amData.computerSystemSoftwareSpec.printerCount', format: Format.ShortStringOrNA },
      timbuktuAccess: { labelResource: 'amData.computerSystemSoftwareSpec.timbuktuAccess', format: Format.BooleanOrNA },
      vncAccess: { labelResource: 'amData.computerSystemSoftwareSpec.vncAccess', format: Format.BooleanOrNA },
      networkAdapterCount: { labelResource: 'amData.computerSystemSoftwareSpec.networkAdapterCount', format: Format.ShortStringOrNA },
      absoluteRemoteEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.absoluteRemoteEnabled', format: Format.BooleanOrNA },
      remoteDesktopScreenSharing: { labelResource: 'amData.computerSystemSoftwareSpec.remoteDesktopScreenSharing', format: Format.BooleanOrNA },

      //PC specific
      osServicePack: { labelResource: 'amData.computerSystemSoftwareSpec.osServicePack', format: Format.ShortStringOrNA },
      osInstallationDate: { labelResource: 'amData.computerSystemSoftwareSpec.osInstallationDate', format: Format.TimeLocal },
      osActivated: { labelResource: 'amData.computerSystemSoftwareSpec.osActivated', format: Format.BooleanOrNA },
      osActivationGracePeriod: { labelResource: 'amData.computerSystemSoftwareSpec.osActivationGracePeriod', format: Format.IntervalInSeconds },
      osSerialNumber: { labelResource: 'amData.computerSystemSoftwareSpec.osSerialNumber', format: Format.ShortStringOrNA },
      osProductId: { labelResource: 'amData.computerSystemSoftwareSpec.osProductId', format: Format.ShortStringOrNA },
      osIsVolumeLicensed: { labelResource: 'amData.computerSystemSoftwareSpec.osIsVolumeLicensed', format: Format.BooleanOrNA },
      virtualMachine: { labelResource: 'amData.computerSystemSoftwareSpec.virtualMachine', format: Format.ShortStringOrNA },
      securityIdentifier: { labelResource: 'amData.computerSystemSoftwareSpec.securityIdentifier', format: Format.ShortStringOrNA },
      osSoftwareUpdatesEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.osSoftwareUpdatesEnabled', format: Format.BooleanOrNA },
      allowRemoteAssistance: { labelResource: 'amData.computerSystemSoftwareSpec.allowRemoteAssistance', format: Format.BooleanOrNA },
      allowRemoteControlViaAssistance: { labelResource: 'amData.computerSystemSoftwareSpec.allowRemoteControlViaAssistance', format: Format.BooleanOrNA },
      pcAnywhereAccess: { labelResource: 'amData.computerSystemSoftwareSpec.pcAnywhereAccess', format: Format.BooleanOrNA },
      damewareAccess: { labelResource: 'amData.computerSystemSoftwareSpec.damewareAccess', format: Format.BooleanOrNA },
      defenderInstalled: { labelResource: 'amData.computerSystemSoftwareSpec.defenderInstalled', format: Format.BooleanOrNA },
      defenderEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.defenderEnabled', format: Format.BooleanOrNA },
      defenderRealTimeProtection: { labelResource: 'amData.computerSystemSoftwareSpec.defenderRealTimeProtection', format: Format.BooleanOrNA },
      defenderAutoScanEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.defenderAutoScanEnabled', format: Format.BooleanOrNA },
      defenderEngineVersion: { labelResource: 'amData.computerSystemSoftwareSpec.defenderEngineVersion', format: Format.ShortStringOrNA },
      defenderDefinitionVersion: { labelResource: 'amData.computerSystemSoftwareSpec.defenderDefinitionVersion', format: Format.ShortStringOrNA },
      windowsServiceCount: { labelResource: 'amData.computerSystemSoftwareSpec.windowsServiceCount', format: Format.ShortStringOrNA },

      //Mac specific
      darwinVersion: { labelResource: 'amData.computerSystemSoftwareSpec.darwinVersion', format: Format.ShortStringOrNA },
      personalFileSharing: { labelResource: 'amData.computerSystemSoftwareSpec.personalFileSharing', format: Format.BooleanOrNA },
      windowsFileSharing: { labelResource: 'amData.computerSystemSoftwareSpec.windowsFileSharing', format: Format.BooleanOrNA },
      personalWebSharing: { labelResource: 'amData.computerSystemSoftwareSpec.personalWebSharing', format: Format.BooleanOrNA },
      remoteLogin: { labelResource: 'amData.computerSystemSoftwareSpec.remoteLogin', format: Format.BooleanOrNA },
      ftpAccess: { labelResource: 'amData.computerSystemSoftwareSpec.ftpAccess', format: Format.BooleanOrNA },
      remoteAppleEvents: { labelResource: 'amData.computerSystemSoftwareSpec.remoteAppleEvents', format: Format.BooleanOrNA },
      printerSharing: { labelResource: 'amData.computerSystemSoftwareSpec.printerSharing', format: Format.BooleanOrNA },
      remoteManagement: { labelResource: 'amData.computerSystemSoftwareSpec.remoteManagement', format: Format.BooleanOrNA },
      wakeOnLanEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.wakeOnLanEnabled', format: Format.BooleanOrNA },
      wakeOnLanSupported: { labelResource: 'amData.computerSystemSoftwareSpec.wakeOnLanSupported', format: Format.BooleanOrNA },
      osUpdateUtilityEnabled: { labelResource: 'amData.computerSystemSoftwareSpec.osUpdateUtilityEnabled', format: Format.BooleanOrNA },
      startupItemCount: { labelResource: 'amData.computerSystemSoftwareSpec.startupItemCount', format: Format.ShortStringOrNA }
    },

    resource: [
      //Both Platforms
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
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
      {
        attr: 'osVersion',
        guid: '671D7D4A-CA16-11D9-839A-000D93B66ADA',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'osBuildNumber',
        guid: '671C9B43-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'osLanguage',
        guid: 'B1BBB6E6-B58B-4FD5-BE83-22E2B5594738',
        type: String
      },
      {
        attr: 'computerBootTime',
        guid: '457F25E7-CE56-45F8-BCFA-855B122AA6C5',
        type: Date
      },
      {
        attr: 'computerUpTime',
        guid: '96DFF0D1-56BF-4981-86CF-AE77054CB8DB',
        // Do not change, can be null. Does not work correctly
        type: Number
      },
      {
        attr: 'currentUserName',
        guid: '671839DC-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'currentUserAccount',
        guid: '9F59FBBD-254D-493A-8C66-5580E5685A18',
        type: String
      },
      {
        attr: 'currentUserIsAdmin',
        guid: 'FBC96ED6-D377-11D9-B22C-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'lastUserAccount',
        guid: '3DB7A953-9A67-4E70-B8AB-2ADD4415D355',
        type: String
      },
      {
        attr: 'lastUserName',
        guid: '7E583602-B544-4034-8609-2662633F3C2E',
        type: String
      },
      {
        attr: 'adComputerName',
        guid: '3666EF30-1FE3-4D79-9AA7-FA4F0F39DCC7',
        type: String
      },
      {
        attr: 'adComputerOrganizationalUnit',
        guid: '53A54262-7A5A-46CB-8E42-E6DE8D68B807',
        type: String
      },
      {
        attr: 'adComputerOrganizationalUnitPath',
        guid: 'F4E80FFD-7FE5-42BE-BEA1-D819C64AA781',
        type: String
      },
      {
        attr: 'adUserOrganizationalUnit',
        guid: 'D2CE0659-BC74-4B26-809B-A0013E432EE5',
        type: String
      },
      {
        attr: 'adUserOrganizationalUnitPath',
        guid: 'AD4D0AC4-9ED7-4F96-8005-3EAB61B2D428',
        type: String
      },
      {
        attr: 'diskEncryptionProduct',
        guid: 'F879438E-F187-4B0D-A54C-8E070B9A6AFA',
        type: String
      },
      {
        attr: 'diskEncryptionVersion',
        guid: 'FEC173C0-D192-43F1-AF3F-754FC4F12D92',
        type: String
      },
      {
        attr: 'diskEncryptionStatus',
        guid: '7E3D7710-2665-4436-97EF-4CE9FEAE4738',
        type: String
      },
      {
        attr: 'diskEncryptionAlgorithm',
        guid: '6AFF00FD-BF88-4503-8F57-7130483EC723',
        type: String
      },
      {
        attr: 'diskEncryptionKeySize',
        guid: '6D52C10C-8A1B-4587-BBF3-0645FFFF4F33',
        type: String
      },
      {
        attr: 'fileVaultSupported',
        guid: 'FFBAFE56-8D7C-4E33-9E8C-B172216AEA3F',
        type: Boolean
      },
      {
        attr: 'fileVaultEnabled',
        guid: '7249F1DB-9D0F-4C57-8C16-223C431609E6',
        type: Boolean
      },
      {
        attr: 'fileVaultAuthenticatedRestartSupported',
        guid: '50C9C454-1FC9-4EF2-9574-FD25ECDB8CF8',
        type: Boolean
      },
      {
        attr: 'fileVaultHasPersonalRecoveryKey',
        guid: '66670D63-7399-4101-A0E7-71DB3BD6A260',
        type: Boolean
      },
      {
        attr: 'fileVaultHasInstitutionalRecoveryKey',
        guid: '01E90D84-537D-4BC5-BBC0-C0EF07DA1647',
        type: Boolean
      },
      {
        attr: 'fileVaultUnlockedUsingRecoveryKey',
        guid: 'CEEFC450-ECFC-48A8-9271-47B581466B33',
        type: Boolean
      },
      {
        attr: 'fileVaultRecoveryKeyStoredOnServer',
        guid: '919B0CB7-A5D0-4699-9A38-3868602412F3',
        type: Boolean
      },
      {
        attr: 'gmtDelta',
        guid: '6718F504-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'daylightSavingTime',
        guid: '671894C6-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'fastUserSwitchingEnabled',
        guid: 'D5865A31-1823-487F-BC43-53B509532D27',
        type: Boolean
      },
      {
        attr: 'firewallEnabled',
        guid: '8C45AEF4-BAEC-41AF-9EE3-4EAE8314D6B4',
        type: Boolean
      },
      {
        attr: 'installedSoftwareCount',
        guid: 'F08D19C1-0038-4D23-805F-94972C11E01D',
        type: String
      },
      {
        attr: 'missingOsPatchesCount',
        guid: '22A4CC35-184E-4522-B516-33F0793C7578',
        type: String
      },
      {
        attr: 'processCount',
        guid: 'EEC6F0EB-CADF-11D9-BACB-000D93B66ADA',
        type: String
      },
      {
        attr: 'fileCount',
        guid: 'BD096687-85A9-468D-B36D-41E76F43B8A3',
        type: String
      },
      {
        attr: 'fontCount',
        guid: 'EED24D2A-CADF-11D9-BACB-000D93B66ADA',
        type: String
      },
      {
        attr: 'printerCount',
        guid: 'EED37F57-CADF-11D9-BACB-000D93B66ADA',
        type: String
      },
      {
        attr: 'timbuktuAccess',
        guid: 'DBF763B4-D681-4E95-8549-2295493043DF',
        type: Boolean
      },
      {
        attr: 'vncAccess',
        guid: 'B753788C-9346-4A49-A6A1-4E5104B3381C',
        type: Boolean
      },
      {
        attr: 'networkAdapterCount',
        guid: 'EED11D16-CADF-11D9-BACB-000D93B66ADA',
        type: String
      },
      {
        attr: 'absoluteRemoteEnabled',
        guid: '063DBF79-CD26-49CB-855E-E96003E0DB3B',
        type: Boolean
      },
      {
        attr: 'remoteDesktopScreenSharing',
        guid: '42CF4558-73FA-47AB-BDF1-D61EEC66D67F',
        type: Boolean
      },
      //PC specific
      {
        attr: 'osServicePack',
        guid: '962DA3CA-4DF3-48AE-B29A-D2D6B15BF12C',
        type: String
      },
      {
        attr: 'osInstallationDate',
        guid: 'C87A8D8E-4842-4827-9F2D-C876632CFE6F',
        type: Date
      },
      {
        attr: 'osActivated',
        guid: '862CC82B-9640-4262-BBB0-07C03C9CC676',
        type: Boolean
      },
      {
        attr: 'osActivationGracePeriod',
        guid: '32D03862-34B4-4566-9371-481A3053C32C',
        type: Number
      },
      {
        attr: 'osSerialNumber',
        guid: 'B34A9369-BEFA-46AC-A90A-27349A9C29CF',
        type: String
      },
      {
        attr: 'osProductId',
        guid: '41D6B8C8-43EC-4327-8FC6-AB9827B85DFB',
        type: String
      },
      {
        attr: 'osIsVolumeLicensed',
        guid: '7D31E4DC-3F72-478D-90F8-A861762EEC82',
        type: Boolean
      },
      {
        attr: 'virtualMachine',
        guid: '571CBBDA-6AD4-4CBA-83D3-DC7010DF448E',
        type: String
      },
      {
        attr: 'securityIdentifier',
        guid: 'A184D586-4BA8-4A86-B4FC-4F12535BA8B8',
        type: String
      },
      {
        attr: 'osSoftwareUpdatesEnabled',
        guid: '20D385CC-8913-4ED6-B4E3-D96226B51265',
        type: Boolean
      },
      {
        attr: 'allowRemoteAssistance',
        guid: 'C6AF8257-168A-41F0-8C53-972B1418B683',
        type: String
      },
      {
        attr: 'allowRemoteControlViaAssistance',
        guid: 'D1B35D7F-A2F8-4A63-A5D5-BC87DF0891F3',
        type: Boolean
      },
      {
        attr: 'pcAnywhereAccess',
        guid: '5573ED0B-C537-4B63-8B3D-F0C853FDDA5B',
        type: Boolean
      },
      {
        attr: 'damewareAccess',
        guid: '7A6A3F4E-15CA-4FCE-BD42-F1A63193F902',
        type: Boolean
      },
      {
        attr: 'defenderInstalled',
        guid: '6FC63A90-D43B-4A68-B7E9-5D83C3BD9C20',
        type: Boolean
      },
      {
        attr: 'defenderEnabled',
        guid: '8AD7A056-C5A0-460F-BF63-5A7F1AE47B43',
        type: Boolean
      },
      {
        attr: 'defenderRealTimeProtection',
        guid: '116A34A8-A31F-4DF2-B025-2CC6D0C3DA92',
        type: Boolean
      },
      {
        attr: 'defenderAutoScanEnabled',
        guid: '9FD87A94-7C5B-484D-92E5-FBCF57D8A14A',
        type: Boolean
      },
      {
        attr: 'defenderEngineVersion',
        guid: '7E6A223D-BE8D-4298-BC76-8758E26BB556',
        type: String
      },
      {
        attr: 'defenderDefinitionVersion',
        guid: 'FD3F3A4C-7BD9-49D0-B6D8-1D4511E371F0',
        type: String
      },
      {
        attr: 'windowsServiceCount',
        guid: 'A7CDFF00-6B94-463E-BB9F-42550AFBEDB0',
        type: String
      },
      //Mac specific
      {
        attr: 'darwinVersion',
        guid: '671A2140-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'personalFileSharing',
        guid: '6719C03D-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'windowsFileSharing',
        guid: '671C3443-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'personalWebSharing',
        guid: '671BB252-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'remoteLogin',
        guid: '671B4BF4-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'ftpAccess',
        guid: '67196068-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'remoteAppleEvents',
        guid: '671AEB8A-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'printerSharing',
        guid: '671A84FA-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'remoteManagement',
        guid: '3CE6F778-132B-4A63-9F93-44F581743B52',
        type: Boolean
      },
      {
        attr: 'wakeOnLanEnabled',
        guid: '671E56C4-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'wakeOnLanSupported',
        guid: '671EC4F6-CA16-11D9-839A-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'osUpdateUtilityEnabled',
        guid: '20D385CC-8913-4ED6-B4E3-D96226B51265',
        type: Boolean
      },
      {
        attr: 'startupItemCount',
        guid: 'EED4C028-CADF-11D9-BACB-000D93B66ADA',
        type: String
      }
    ],

    searchableNames: 'osPlatform osVersion'.w()

  }).create();
});
