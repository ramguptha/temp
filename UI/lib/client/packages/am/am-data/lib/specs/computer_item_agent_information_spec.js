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
      agentName: { labelResource: 'amData.computerItemAgentInformationSpec.agentName', format: Format.StringOrNA },
      agentActiveIp: { labelResource: 'amData.computerItemAgentInformationSpec.agentActiveIp', format: Format.IPv4Address },
      agentVersion: { labelResource: 'amData.computerItemAgentInformationSpec.agentVersion', format: Format.AgentVersion },
      agentBuildNumber: { labelResource: 'amData.computerItemAgentInformationSpec.agentBuildNumber', format: Format.StringOrNA },
      agentSerialNumber: { labelResource: 'amData.computerItemAgentInformationSpec.agentSerialNumber', format: Format.StringOrNA },
      computerOnline: { labelResource: 'amData.computerItemAgentInformationSpec.computerOnline', format: Format.BooleanOrNA },
      machineModel: { labelResource: 'amData.computerItemAgentInformationSpec.machineModel', format: Format.StringOrNA },
      osPlatform: { labelResource: 'amData.computerItemAgentInformationSpec.osPlatform', format: Format.ShortStringOrNA },
      osPlatformNumber: Format.Number,
      osVersion: { labelResource: 'amData.computerItemAgentInformationSpec.osVersion', format: Format.OSVersionComputer },
      sdServerAddress: { labelResource: 'amData.computerItemAgentInformationSpec.sdServerAddress', format: Format.StringOrNA },
      lmServerAddress: { labelResource: 'amData.computerItemAgentInformationSpec.lmServerAddress', format: Format.StringOrNA },
      sdServerCheckInterval: { labelResource: 'amData.computerItemAgentInformationSpec.sdServerCheckInterval', format: Format.NumberOrNA },
      lmServerCheckInterval: { labelResource: 'amData.computerItemAgentInformationSpec.lmServerCheckInterval', format: Format.NumberOrNA },
      includedInOsPatchManagement: { labelResource: 'amData.computerItemAgentInformationSpec.includedInOsPatchManagement', format: Format.BooleanOrNA },
      includedInThirdPartyPatchManagement: { labelResource: 'amData.computerItemAgentInformationSpec.includedInThirdPartyPatchManagement', format: Format.BooleanOrNA },
      useOnlyAbsoluteManageForOsUpdates:  { labelResource: 'amData.computerItemAgentInformationSpec.useOnlyAbsoluteManageForOsUpdates', format: Format.BooleanOrNA },
      absoluteRemoteEnabled: { labelResource: 'amData.computerItemAgentInformationSpec.absoluteRemoteEnabled', format: Format.BooleanOrNA },
      absoluteRemotePort: { labelResource: 'amData.computerItemAgentInformationSpec.absoluteRemotePort', format: Format.StringOrNA },
      absoluteRemoteUserConfirmationRequired: { labelResource: 'amData.computerItemAgentInformationSpec.absoluteRemoteUserConfirmationRequired', format: Format.BooleanOrNA },
      computerOwnership: { labelResource: 'amData.computerItemAgentInformationSpec.computerOwnership', format: Format.StringOrNA },
      computerEnrollmentDate: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentDate', format: Format.TimeLocal },
      computerEnrolledInMdm: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrolledInMdm', format: Format.BooleanOrNA },
      computerEnrolledViaEnrollmentProgram: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrolledViaEnrollmentProgram', format: Format.BooleanOrNA },
      computerEnrollmentProgramRegistrationDate: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentProgramRegistrationDate', format: Format.TimeLocal },
      computerEnrollmentProfileAssignmentDate: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentProfileAssignmentDate', format: Format.TimeLocal },
      computerEnrollmentProfileInstallationDate: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentProfileInstallationDate', format: Format.TimeLocal },
      computerEnrollmentProfileUuid: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentProfileUuid', format: Format.StringOrNA },
      computerEnrollmentStatus: { labelResource: 'amData.computerItemAgentInformationSpec.computerEnrollmentStatus', format: Format.StringOrNA },
      computerDeviceIdentifierUdid: { labelResource: 'amData.computerItemAgentInformationSpec.computerDeviceIdentifierUdid', format: Format.StringOrNA },
      computerIsTracked: { labelResource: 'amData.computerItemAgentInformationSpec.computerIsTracked', format: Format.BooleanOrNA },
      lastHeartbeat: { labelResource: 'amData.computerItemAgentInformationSpec.lastHeartbeat', format: Format.TimeLocal },
      recordCreationDate: { labelResource: 'amData.computerItemAgentInformationSpec.recordCreationDate', format: Format.TimeLocal },
      clientInformation1: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation1', format: Format.StringOrNA },
      clientInformation2: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation2', format: Format.StringOrNA },
      clientInformation3: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation3', format: Format.StringOrNA },
      clientInformation4: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation4', format: Format.StringOrNA },
      clientInformation5: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation5', format: Format.StringOrNA },
      clientInformation6: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation6', format: Format.StringOrNA },
      clientInformation7: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation7', format: Format.StringOrNA },
      clientInformation8: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation8', format: Format.StringOrNA },
      clientInformation9: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation9', format: Format.StringOrNA },
      clientInformation10: { labelResource: 'amData.computerItemAgentInformationSpec.clientInformation10', format: Format.StringOrNA },
//      computerEsn: Format.StringOrNA,
      computerManufacturer: { labelResource: 'amData.computerItemAgentInformationSpec.computerManufacturer', format: Format.StringOrNA }
//      computerDeviceFreezeStatus: Format.StringOrNA,
//      computerDeviceFreezeStatusNumber: Format.Number
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'agentName',
        guid: '5148916D-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'agentActiveIp',
        guid: '80C4AD10-9D81-4991-8268-571277135E05',
        type: Number
      },
      {
        attr: 'agentVersion',
        guid: '5148B620-C9FF-11D9-83AD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'agentBuildNumber',
        guid: '5148E115-C9FF-11D9-83AD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'agentSerialNumber',
        guid: '4DC29689-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'computerOnline',
        guid: '5ADC0C72-D44A-11D9-BE66-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'machineModel',
        guid: 'CD19617B-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
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
        attr: 'sdServerAddress',
        guid: 'EF319D80-C88A-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'lmServerAddress',
        guid: 'EF325A47-C88A-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'sdServerCheckInterval',
        guid: 'EF31D9B9-C88A-11D9-AF8D-000D93B66ADA',
        type: Number
      },
      {
        attr: 'lmServerCheckInterval',
        guid: 'EF327FB5-C88A-11D9-AF8D-000D93B66ADA',
        type: Number
      },
      {
        attr: 'includedInOsPatchManagement',
        guid: '4A1EA42E-B555-459D-9A13-D837EF92B692',
        type: Boolean
      },
      {
        attr: 'includedInThirdPartyPatchManagement',
        guid: '8DE666A6-92D3-487F-8BA3-47C4CA779DE1',
        type: Boolean
      },
      {
        attr: 'useOnlyAbsoluteManageForOsUpdates',
        guid: 'A554CAD0-4FCD-4963-BD2C-9DACFA5A1B61',
        type: Boolean
      },
      {
        attr: 'absoluteRemoteEnabled',
        guid: '063DBF79-CD26-49CB-855E-E96003E0DB3B',
        type: Boolean
      },
      {
        attr: 'absoluteRemotePort',
        guid: '3621BE16-DBEE-4D57-A6AB-53141E526F98',
        type: Number
      },
      {
        attr: 'absoluteRemoteUserConfirmationRequired',
        guid: 'DAD8BA48-5271-43C1-AC29-8D21722B0CD9',
        type: Boolean
      },
      {
        attr: 'computerOwnership',
        guid: '8D23701B-91A5-4C67-A8FE-61261F0C723B',
        type: String
      },
      {
        attr: 'computerEnrollmentDate',
        guid: 'FD7E5C2E-F679-41E2-9C81-705AC84A6F53',
        type: Date
      },
      {
        attr: 'computerEnrolledInMdm',
        guid: '70B05BDE-0F2B-491F-9ABF-5A2B68E1BBDF',
        type: Boolean
      },
      {
        attr: 'computerEnrolledViaEnrollmentProgram',
        guid: '4E2134C5-326D-4CE9-B733-168516B5A154',
        type: Boolean
      },
      {
        attr: 'computerEnrollmentProgramRegistrationDate',
        guid: 'BFEF34CC-2F41-4567-A19C-F1C78D9CB455',
        type: Date
      },
      {
        attr: 'computerEnrollmentProfileAssignmentDate',
        guid: '77245F64-70DF-48EF-860F-6545370DC42A',
        type: Date
      },
      {
        attr: 'computerEnrollmentProfileInstallationDate',
        guid: '50EE7BCC-63BA-48D5-9545-E0BFCBB287B4',
        type: Date
      },
      {
        attr: 'computerEnrollmentProfileUuid',
        guid: '8242C83A-1F71-4A8C-AA30-75CA51405DB3',
        type: String
      },
      {
        attr: 'computerEnrollmentStatus',
        guid: '4695D5B4-56B0-4596-ABA5-3A699F8DBB9F',
        type: String
      },
      {
        attr: 'computerDeviceIdentifierUdid',
        guid: '8F56C086-DE97-4246-9F0F-8835A1CA753E',
        type: String
      },
      {
        attr: 'computerIsTracked',
        guid: 'EAB802A6-AF7A-4EFC-A1A0-310E3C4B2D71',
        type: Boolean
      },
      {
        attr: 'lastHeartbeat',
        guid: '3CAEE6D8-66C8-46A7-BCAD-2F21863468C5',
        type: Date
      },
      {
        attr: 'recordCreationDate',
        guid: '35FAD54F-F96E-4B82-BACB-FC999FF38DD1',
        type: Date
      },
      {
        attr: 'clientInformation1',
        guid: 'E13CC225-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation2',
        guid: 'E13CEC9F-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation3',
        guid: 'E13D06EA-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation4',
        guid: 'E13D207C-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation5',
        guid: 'E13D392E-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation6',
        guid: 'E13D5251-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation7',
        guid: 'E13D6AF9-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation8',
        guid: 'E13D83AE-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation9',
        guid: 'E13D9E4E-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'clientInformation10',
        guid: 'E13DB863-C889-11D9-AF8D-000D93B66ADA',
        type: String
      },
      {
        attr: 'computerEsn',
        guid: '2B9A31CA-8578-4517-B804-325EAE4E3C5D',
        type: String
      },
      {
        attr: 'computerManufacturer',
        guid: 'AC515518-E4F4-4B59-826D-C11FD985B1E6',
        type: String
      },
      {
        attr: 'computerDeviceFreezeStatus',
        guid: '8D285318-F26B-11E3-BEE9-F8B156E2AB92',
        type: String
      },
      {
        attr: 'computerDeviceFreezeStatusNumber',
        guid: 'BD4402EC-82B0-408A-9CAA-DAA79B175CA9',
        type: Number
      }

    ],

    searchableNames: 'agentName agentActiveIp agentVersion agentBuildNumber sdServerAddress lmServerAddress sdServerCheckInterval lmServerCheckInterval includedInOsPatchManagement includedInThirdPartyPatchManagement useOnlyAbsoluteManageForOsUpdates absoluteRemoteEnabled absoluteRemotePort absoluteRemoteUserConfirmationRequired computerOwnership computerIsTracked lastHeartbeat recordCreationDate clientInformation1 clientInformation2 clientInformation3 clientInformation4 clientInformation5 clientInformation6 clientInformation7 clientInformation8 clientInformation9 clientInformation10'.w()

  }).create();
});
