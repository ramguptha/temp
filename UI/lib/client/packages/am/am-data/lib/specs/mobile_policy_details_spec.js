define([
  'jquery',
  'ember',
  '../am_spec',
  '../am_formats'
], function(
    $,
    Em,
   AmSpec,
   Format
  ) {
    'use strict';
    var format = {
        name       : Format.String,
        id         : Format.ID,
        policyType : Format.Number,
        filterType : Format.String,
        filterBody : Format.String
    };

    var resource =  [
    {
      attr: 'name',
      guid: '393E6BC3-45E2-497A-A233-BC11096C58B7',
      type: String
    },
    {
      attr: 'id',
      guid: 'B783FF48-C326-4319-93D5-C327D0C2B42A',
      type: Number
    },
    {
      attr: 'policyType',
      guid: '22382928-A7FC-4711-9567-C0B66FD3AD3E',           //1 � by device, 2 � by application, 3 � by profile
      type: Number
    },
    {
      attr: 'filterType',
      guid: 'B7504474-27AD-44EF-B9E3-DA380E205D5D',                           //�OR�, �AND�
      type: String
    },
    {
      attr: 'filterBody',
      guid: 'D86CCFF3-8230-481D-B2F8-A2475263111A',                            //JSON
      type: String
/*JSON example:
    [
      {fieldName:�<name1>�,operator:�<operator1>�,value:�<value1>�},
      {fieldName:�<name2>�,operator:�<operator2>�,value:�<value2>�}
    ]
*/
    }];

    return AmSpec.create({

      format: format,
      resource: resource
    });

});