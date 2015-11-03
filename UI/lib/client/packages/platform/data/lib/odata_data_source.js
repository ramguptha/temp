define([
  'ember',
  'packages/platform/ajax',
  'packages/platform/aggregate',

  './data_source',
  './odata_uri_builder',
  'logger'
], function(
  Em,
  Ajax,
  Aggregate,

  DataSource,
  ODataURIBuilder,
  logger
  ) {
  'use strict';

  // A DataSource is returned when a store is queried.
  return DataSource.extend({
    name: 'OData Data Source',

    // A dataSource has an endPoint
    endPoint: null,

    // Singleton
    uriBuilder: ODataURIBuilder.create(),

    // JSON endPoint to request from
    uri: function() {
      var self = this;
      var query = this.get('query');
      var endPoint = this.get('endPoint');
      var params = '';
      var filter = [];

      var mappedAttributes = Em.A();
      this.get('spec.resource').forEach(function(resource) {
        var type = resource.type;
        var attr = resource.attr;
        var sourceAttr = resource.sourceAttr;

        if (Aggregate.Base.detectInstance(type)) {
          type.get('Spec.resource').forEach(function(subResource) {
            mappedAttributes.pushObject({
              attr: attr + '.' + subResource.attr,
              sourceAttr: sourceAttr + '.' + subResource.sourceAttr,
              type: subResource.type
            });
          })
        }

        mappedAttributes.pushObject(resource);
      });

      var paramNameMapper = {
        sort: '$orderby',
        mergedFilter: '$filter',
        limit: '$top',
        offset: '$skip'
      };

      if (query.get('isSearch')) {
        var isCounter = this.get('isCounter');

        var queryVars = 'mergedFilter sort limit offset'.w().map(function(name) {
          var value = query.get(name);
          var paramName = null;

          if ('sort' === name && value) {
            var result = [];
            value.forEach(function(item) {
              // Get endpoint name of sorted row
              var sortedAttrSpec = mappedAttributes.find(function(attrSpec) {
                return attrSpec.attr === item.attr;
              });

              // This replacement is not happening due to the issue Java endpoints have
              // with having '/' in odata for separating aggregate data.
              // But this issue so far is happening only on $orderby not for filtering
              // value = self.sourceAttr(sortedAttrSpec).replace('.', '/');
              value = self.sourceAttr(sortedAttrSpec);
              result.push(value.concat(' ' + item.dir));
            })
            value = result.join(',');
            paramName = paramNameMapper[name];
          } else if ('mergedFilter' === name) {
            var optimize = query.get('mergedFilter').optimize();
            var jsonTree = optimize ? optimize.toJSON() : null;
            if (jsonTree) {
              var attrToEndPointNameMap = {};
              $.each(mappedAttributes, function(idx, spec) {
                attrToEndPointNameMap[spec.attr] = self.sourceAttr(spec);
              });

              paramName = paramNameMapper[name];
              //TODO: working on the DATE_OF operator
              value = this.get('uriBuilder').serialize(jsonTree, query.get('adhocSearchableNames'), attrToEndPointNameMap);
            }
          } else if ('limit' === name) {
            if (!isCounter) {
              // Try to get one more than asked for, so that we can set isLastPage
              value += 1;
              paramName = paramNameMapper[name];
            } // else don't specify limit
          } else if ('offset' === name) {
            if (!isCounter) {
              paramName = paramNameMapper[name];
            } // else don't specify offset
          } else {
            paramName = paramNameMapper[name];
          }

          return (paramName && value) ? (paramName + '=' + this.esc(value)) : null;
        }, this).compact();

        this.mergeContext(filter, query.get('context'));

        if (0 < filter.length) {
          queryVars.push('$filter=' + filter.join(' and '));
        }
        params = (queryVars.length > 0 ? ('?' + queryVars.join('&')) : '');
      } else if (query.get('isSingleton')) {
        params = '/' + query.get('id');
      }

      return endPoint + params;
    }.property(),

    mergeContext: function(filter, context) {
      return filter;
    },

    escapeValue: function(val) {
      switch (typeof(val)) {
        case 'string':
          return "'" + val.replace("'", "''") + "'";
        default:
          return val;
      }
    },

    sourceAttr: function(spec) {
      return spec.sourceAttr || spec.attr;
    }
  });
});

