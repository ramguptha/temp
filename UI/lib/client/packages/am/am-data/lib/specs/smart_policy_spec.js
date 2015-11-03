define([
  'ember',
  '../am_spec',
  '../am_formats'
], function (
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.create({
    resource: [
      {
        attr: 'id',
        guid: 'A78A37B9-86B7-4118-84C6-25A15C6F68C8',
        type: Number
      },
      {
        attr: 'name',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'isSmartPolicy',
        guid: 'F6917EE8-9F39-43F3-B8C9-D3C461170FE5',
        type: Boolean
      },
      {
        attr: 'seed',
        guid: '65A7E9DC-2026-4178-9950-B0E26A3A8B0A',
        type: Number
      },
      {
        attr: 'guid',
        guid: 'E652CD7A-909C-465D-AE76-B97428711B6B',
        type: String
      },
      {
        attr: 'filterType',
        guid: '618ee8ca-6e1a-4c47-a761-c43f5b7f5e48',
        type: Number
      },
      {
        attr: 'filterCriteria',
        guid: 'cde4e85a-c57a-4de1-a244-83b66bf918c2',
        type: String
      }
    ]
  });
});
