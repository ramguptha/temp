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
    idNames: 'missingPatchName missingPatchReleaseDate'.w(),

    format: {
      id: Format.ID,
      missingPatchName: { labelResource: 'amData.computerMissingPathSpec.missingPatchName', format: Format.StringOrNA },

      missingPatchIsMandatory: { labelResource: 'amData.computerMissingPathSpec.missingPatchIsMandatory', format: Format.BooleanOrNA },
      missingPatchReleaseDate: { labelResource: 'amData.computerMissingPathSpec.missingPatchReleaseDate', format: Format.ShortDate },
      missingPatchAction: { labelResource: 'amData.computerMissingPathSpec.missingPatchAction', format: Format.StringOrNA },
      missingPatchInstallDeadline: { labelResource: 'amData.computerMissingPathSpec.missingPatchInstallDeadline', format: Format.ShortDate },
      missingPatchLanguage: { labelResource: 'amData.computerMissingPathSpec.missingPatchLanguage', format: Format.StringOrNA },
      missingPatchVersion: { labelResource: 'amData.computerMissingPathSpec.missingPatchVersion', format: Format.StringOrNA },
      missingPatchSeverity: { labelResource: 'amData.computerMissingPathSpec.missingPatchSeverity', format: Format.BooleanOrNA },
      missingPatchIsOsPatch: { labelResource: 'amData.computerMissingPathSpec.missingPatchIsOsPatch', format: Format.BooleanOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'missingPatchName',
        guid: '0AC6E418-FC83-4986-A2BF-77C929EA6DBC',
        type: String
      },
      {
        attr: 'missingPatchIsMandatory',
        guid: '35909C11-B8BD-4BCE-B687-F68393D7F9F1',
        type: Boolean
      },
      {
        attr: 'missingPatchReleaseDate',
        guid: '2BF580F2-0457-4F25-BF42-E8968A4033F7',
        type: Date
      },
      {
        attr: 'missingPatchAction',
        guid: '20A8B069-4348-4D4A-B6EA-0C2499A4204A',
        type: String
      },
      {
        attr: 'missingPatchInstallDeadline',
        guid: '2B7BDA9E-3652-47CA-A17D-CC5BCF763C25',
        type: Date
      },
      {
        attr: 'missingPatchLanguage',
        guid: 'BBC2F65A-AC67-45D1-9B26-8550EC32DC57',
        type: String
      },
      {
        attr: 'missingPatchVersion',
        guid: '3F7BE619-CF7F-402D-AB9B-386B40BF8F73',
        type: String
      },
      {
        attr: 'missingPatchSeverity',
        guid: '77955C05-2602-43C2-B804-094DC0D09114',
        type: String
      },
      {
        attr: 'missingPatchIsOsPatch',
        guid: '9BACB0B5-3F51-4215-9F02-29E3C8463088',
        type: String
      }
    ],

    searchableNames: 'missingPatchName missingPatchIsMandatory missingPatchReleaseDate missingPatchAction missingPatchInstallDeadline missingPatchLanguage missingPatchVersion'.w()

  }).create();
});
