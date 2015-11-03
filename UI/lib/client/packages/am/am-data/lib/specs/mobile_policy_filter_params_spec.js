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
      DisplayName: Format.String,
      InfoItemID: Format.String,
      ColumnDataType: Format.String,
      EnumData: Format.StringOrNA  
    };

    var resource =  [
    {
      attr: 'DisplayName',
      guid: 'E89E190D-FDEB-48C7-94C6-8D61FEBADFC2',
      type: String
    },
    {
      attr: 'InfoItemID',
      guid: '970A7837-2AA2-461A-881D-C22D2344E56C',
      type: String
    },
    {
      attr: 'ColumnDataType',
      guid: 'BA9865B1-5481-4874-B014-4D8BBC517805',
      type: String
    },
    {
      attr: 'EnumData',
      guid: 'A8F4F04D-37D2-423A-AD6E-30A1554C364F',
      type: String
    }];

    return AmSpec.create({

      format: format,
      resource: resource
       
    });

});