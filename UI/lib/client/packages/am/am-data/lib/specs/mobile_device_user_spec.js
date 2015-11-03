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
      displayName: { label: 'User Display Name', format: Format.StringOrNA },
      firstName: { label: 'First Name', format: Format.StringOrNA },
      lastName: { label: 'Last Name', format: Format.StringOrNA },
      logonName: { label: 'Logon Name', format: Format.StringOrNA },
      email: { label: 'E-Mail', format: Format.StringOrNA },
      phoneNumber: { label: 'Phone Number', format: Format.ShortStringOrNA },
      department: { label: 'Department', format: Format.StringOrNA },
      company: { label: 'Company', format: Format.StringOrNA },
      street: { label: 'Street', format: Format.StringOrNA },
      city: { label: 'City', format: Format.StringOrNA },
      state: { label: 'State', format: Format.StringOrNA },
      zipCode: { label: 'ZIP/Post Code', format: Format.ShortStringOrNA },
      country: { label: 'Country', format: Format.StringOrNA },
      office: { label: 'Office', format: Format.StringOrNA },
      organizationalUnitPath: { label: 'Organizational Unit Path', format: Format.StringOrNA },
      organizationalUnit: { label: 'Organizational Unit', format: Format.StringOrNA },
      memberOf: { label: 'Member Of', format: Format.StringOrNA },
      enrollmentUserName: { label: 'Enrollment Username', format: Format.StringOrNA },
      enrollmentDomain: { label: 'Enrollment Domain', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'displayName',
        guid: 'B0F8249E-AEC2-4502-936B-7489F857E756',
        type: String
      },
      {
        attr: 'firstName',
        guid: '7FAE3540-8CE9-4AAD-89F0-7D9D5F34D6C9',
        type: String
      },
      {
        attr: 'lastName',
        guid: '64995371-03C7-4819-8FA4-E6AA49FB9DF4',
        type: String
      },
      {
        attr: 'logonName',
        guid: '9AF9B955-E467-475C-ADBF-78AC584184D5',
        type: String
      },
      {
        attr: 'email',
        guid: '545A9880-78E7-4317-8EC4-7DC65F777AF1',
        type: String
      },
      {
        attr: 'phoneNumber',
        guid: '04578CDD-8BD2-4E7F-B0B6-372B9336B6AA',
        type: String
      },
      {
        attr: 'department',
        guid: '388CDAB3-5DD7-48A7-849C-74420CB4F9A6',
        type: String
      },
      {
        attr: 'company',
        guid: '6BFA5E91-42CB-4B3A-873A-0DD888246427',
        type: String
      },
      {
        attr: 'street',
        guid: 'BA58E62A-EC4C-401E-84B4-8F0034175818',
        type: String
      },
      {
        attr: 'city',
        guid: '17410563-340F-4EB0-8419-E8DA2A0EB2A8',
        type: String
      },
      {
        attr: 'state',
        guid: '6A723C35-9D20-4E8F-A800-EF0E0F25E5F3',
        type: String
      },
      {
        attr: 'zipCode',
        guid: '73AF634E-1C6F-4871-8CFB-37107E243F02',
        type: String
      },
      {
        attr: 'country',
        guid: 'EFBECA75-2D3A-4FCF-B70D-DC17423D651A',
        type: String
      },
      {
        attr: 'office',
        guid: 'BBC05FA8-821F-4128-B3EE-FBEBF1B44529',
        type: String
      },
      {
        attr: 'organizationalUnitPath',
        guid: 'A90D3EEA-3277-4957-B2AA-708E1AC4572B',
        type: String
      },
      {
        attr: 'organizationalUnit',
        guid: '7F0723C0-A61C-46DD-BDEF-04AB86D0F3A2',
        type: String
      },
      {
        attr: 'memberOf',
        guid: '41761BCD-FF08-4E9E-91D4-CB4F257F7426',
        type: String
      },
      {
        attr: 'enrollmentUserName',
        guid: '3AB750A5-EAD9-4E0A-9EB2-F8D82EBF0E5D',
        type: String
      },
      {
        attr: 'enrollmentDomain',
        guid: '598EE05E-F121-467B-9AD1-F37FD0D2F924',
        type: String
      }

    ]
  }).create();
});
