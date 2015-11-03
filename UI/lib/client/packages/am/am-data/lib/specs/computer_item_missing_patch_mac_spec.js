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
      missingPatchReleaseDate: { labelResource: 'amData.computerMissingPathSpec.missingPatchReleaseDate', format: Format.ShortDate },
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
        attr: 'missingPatchReleaseDate',
        guid: '2BF580F2-0457-4F25-BF42-E8968A4033F7',
        type: Date
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

    searchableNames: 'missingPatchName missingPatchReleaseDate missingPatchVersion'.w()

  }).create();
});
