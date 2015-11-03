define([
  'ember',
  'jquery',
  '../namespace',

  'packages/platform/enum-type',
  'packages/platform/ip-type',
  'packages/platform/information-item',
  'packages/platform/interval-type',

  '../am_spec'
], function(
  Em,
  $,
  AmData,

  EnumType,
  IpType,
  InformationItem,
  IntervalType,

  AmSpec
  ) {
  'use strict';

  // This is the most #YOLO of the stores meant to make AJAX calls that would be impractical through the standard stores.
  // ( ex. If the spec is not known ahead of time )
  // This store will use AJAX to GET the data from AM's backend API based on the provided endpoint name. The result will be
  // retrieved from the cache on any subsequent calls to the same endpoint unless 'force === true'.
  return Em.Object.extend({
    // cached as an array[endPoint][data, textStatus, jqXHR, spec]
    cache: [],

    // Perform an AJAX call to an endPoint and then call the passed in callbacks
    acquire: function(endPoint, loadedCallback, loadFailedCallback, force){
      if (Em.isEmpty(endPoint)) {
        throw ['Invalid endpoint provided'];
      }

      var fullEndPoint = AmData.get('urlRoot') + '/' + endPoint, self = this, cache = this.get('cache');

      // use the cache if we can
      if( !Em.isNone(cache[endPoint]) && force != true ){
        loadedCallback(cache[endPoint][0], cache[endPoint][1], cache[endPoint][2], cache[endPoint][3]);
      } else {
        $.ajax(fullEndPoint)
          .done(function (data, textStatus, jqXHR) {
            // cache the result
            cache[endPoint] = [data, textStatus, jqXHR, self.buildSpec(Em.Object.create(data))];

            if (!Em.isNone(loadedCallback)) {
              loadedCallback(cache[endPoint][0], cache[endPoint][1], cache[endPoint][2], cache[endPoint][3]);
            }
          })
          .fail(function (jqXHR, textStatus, errorThrown) {
            cache[endPoint] = undefined;

            if (!Em.isNone(loadFailedCallback)) {
              loadFailedCallback(jqXHR, textStatus, errorThrown);
            }
          });
      }
    },

    // Generate a spec based on the retrieved metaData
    buildSpec: function(data){
      var resource = [],  metaData = data.get('metaData.columnMetaData'), self = this;

      metaData.forEach(function(item){
        if ( !$.isEmptyObject(item) ) {
          if ( Em.isNone(item.EnumerationValues)) {
            var type = item.DisplayType ? item.DisplayType : item.ColumnDataType;
            resource.push(
              {
                attr: item.DisplayName,
                guid: item.InfoItemID,
                type: self.getResourceType(type),
                isCustomField: item.DisplayName ? true : false
              }
            );
          } else { //format the resource differently if it has an enumeration present
            var options = [];
            item.EnumerationValues.forEach(function(item){
              options.push({
                name: item.name,
                id: item.name  // WARNING: name and id are the same thing
              })
            });

            resource.push({
              attr: item.DisplayName,
              guid: item.InfoItemID,
              type: EnumType.create({options: options}),
              isCustomField: item.DisplayName ? true : false
            });
          }
        }
      });

      // sort alphabetically by name
      resource = resource.sort(function(a,b) {
        var textA = a.attr.toUpperCase();
        var textB = b.attr.toUpperCase();
        return (textA < textB) ? -1 : (textA > textB) ? 1 : 0;
      });

      return AmSpec.create({
        resource: resource,

        // overriding because camel case sucks
        getPresentationForAttr: function(name) {
          var spec = this.get('resourceByName')[name];
          var formatOptions = this.get('format')[name];

          var format, label;
          if (Em.isNone(formatOptions)) {
            // Enumerated is one type of attributes that do not have format
            label = name;
          }

          return {
            name: name,
            label: label,
            format: format,
            type: spec.type,
            editable: spec.editable ? spec.editable : false
          }
        }
      });
    },

    // Given a string type, such as those found in metaData.columnMetaData, return a javascript type
    getResourceType: function(type){
      if( type.search(/decimalnothousandssep/i) != -1 ) {
        var noThousandsSepType = InformationItem.create();
        noThousandsSepType.regex = /^-?\d+$/ig;
        noThousandsSepType.type = 'no-thousands-sep';

        return noThousandsSepType;
      } else if( type.search(/number/i) != -1 || type.search(/decimal/i) != -1 ){
        return Number;
      } else if( type.search(/dateinterval/i) != -1 || type.search(/availabilitytime/i) != -1 || type.search(/timeintervalbrief/i) != -1 ){
        return InformationItem.create();
      } else if( type.search(/date/i) != -1 ){
        return Date;
      } else if( type.search(/boolean/i) != -1 ) {
        return Boolean;
      } else if( type.search(/ipv4address/i) != -1 ) { // WARNING: only IPv4 is currently supported
        return IpType.create();
      } else if( type.search(/version/i) != -1 ) {
        var versionType = InformationItem.create()
        versionType.regex = /^(\d+\.\d+){1}(\.\d+)?$/g;
        versionType.type = 'version';

        return versionType;
      } else if( type.search(/clockspeed/i) != -1 ) {
        var clockSpeedType = InformationItem.create();
        clockSpeedType.regex = /^\d+\s?(hz)?$/ig;
        clockSpeedType.type = 'clock-speed';

        return clockSpeedType;
      } else if( type.search(/smartbytes/i) != -1 ) {
        var smartBytesType = InformationItem.create();
        smartBytesType.regex = /^\d+\s?(byte|bytes)?$/ig;
        smartBytesType.type = 'smart-bytes';

        return smartBytesType;
      } else if( type.search(/distance/i) != -1 ) {
        var distanceType = InformationItem.create();
        distanceType.regex = /^\d+\s?m?$/ig;
        distanceType.type = 'distance';

        return distanceType;
      } else if( type.search(/percentage/i) != -1 ) {
        var distanceType = InformationItem.create();
        distanceType.regex = /^\d+\s?%?$/g;
        distanceType.type = 'percentage';

        return distanceType;
      } else if( type.search(/dateinterval/i) != -1 || type.search(/timeintervalnoseconds/i) != -1 ) {
        return IntervalType.create();
      } else {
        return String;
      }
    }
  });
});
