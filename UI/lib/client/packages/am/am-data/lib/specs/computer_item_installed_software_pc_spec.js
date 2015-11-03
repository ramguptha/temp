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
    idNames: 'instSoftwareName instSoftwareSize'.w(),

    format: {
      id: Format.ID,
      instSoftwareName: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareName', format: Format.StringOrNA },
      instSoftwareVersionString: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareVersionString', format: Format.StringOrNA },
      instSoftwareSize: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareSize', format:  Format.BytesOrNA },
      instSoftwareInstallationDate: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareInstallationDate', format:  Format.ShortDateTime },
      identificationType: { labelResource: 'amData.computerInstalledSoftwareSpec.identificationType', format: Format.StringOrNA },
      instSoftwareProductId: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareProductId', format: Format.StringOrNA },

      // PC
      instSoftwareCompany: { labelResource: 'amData.computerInstalledSoftwareSpec.instSoftwareCompany', format: Format.StringOrNA },
      uninstallable:  { labelResource: 'amData.computerInstalledSoftwareSpec.uninstallable', format: Format.BooleanOrNA },
      isHotfix:  { labelResource: 'amData.computerInstalledSoftwareSpec.isHotfix', format: Format.BooleanOrNA },
      installLocation:  { labelResource: 'amData.computerInstalledSoftwareSpec.installLocation', format: Format.StringOrNA },
      registeredCompany: { labelResource: 'amData.computerInstalledSoftwareSpec.registeredCompany', format: Format.StringOrNA },
      registeredOwner:  { labelResource: 'amData.computerInstalledSoftwareSpec.registeredOwner', format: Format.StringOrNA },
      installedBy: { labelResource: 'amData.computerInstalledSoftwareSpec.installedBy', format: Format.StringOrNA }

    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'instSoftwareName',
        guid: '99CA5095-FAAD-44EA-AF62-0611FB4B7A34',
        type: String
      },
      {
        attr: 'instSoftwareCompany',
        guid: '06D54552-EAFA-4E22-99AA-AFC48F4101CD',
        type: String
      },
      {
        attr: 'instSoftwareVersionString',
        guid: '848FB3B0-C3DA-44B1-88E5-09ACF5DFAB3D',
        type: String
      },
      {
        attr: 'instSoftwareSize',
        guid: 'C5E8FEDB-D337-433E-9AFA-37AAD0ED05F0',
        type: Number
      },
      {
        attr: 'instSoftwareInstallationDate',
        guid: '35F693ED-11F4-415D-85B1-4896C1328C4D',
        type: Date
      },
      {
        attr: 'uninstallable',
        guid: '7C3A6B0D-8326-4D74-B229-DEA219C9E7CF',
        type: Boolean
      },
      {
        attr: 'isHotfix',
        guid: 'A9FE1109-427B-4E1B-9BFC-DA39F7D3F922',
        type: Boolean
      },
      {
        attr: 'identificationType',
        guid: '3A0E02CB-F164-408B-8454-788D551CB2B5',
        type: String
      },
      {
        attr: 'installLocation',
        guid: 'AACEBD12-8DB5-4D7E-B57E-91525D69651D',
        type: String
      },
      {
        attr: 'instSoftwareProductId',
        guid: '368889FB-F7AF-46D7-919C-C3019319508E',
        type: String
      },
      {
        attr: 'registeredCompany',
        guid: '40288D43-9FFE-4390-BE11-85AA8A1BC3EF',
        type: String
      },
      {
        attr: 'registeredOwner',
        guid: 'A3B194F5-EA5C-4997-87F4-75C00B9DAC16',
        type: String
      },
      {
        attr: 'installedBy',
        guid: '3881F566-BA82-4B70-AD0A-F982385AA4C2',
        type: String
      },
      {
        attr: 'instSoftwareInfo',
        guid: 'D5CFBB6A-3FCF-41D3-84A8-276F173FCB12',
        type: String
      }
    ],

    searchableNames: 'instSoftwareName instSoftwareVersionString instSoftwareSize instSoftwareInstallationDate identificationType instSoftwareProductId  instSoftwareCompany uninstallable isHotfix installLocation registeredCompany registeredOwner installedBy'.w()
  }).create();
});
