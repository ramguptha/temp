define([
  'ember',
  'logger'
], function(
  Em,
  logger
) {
  'use strict';

  // ODATA URI Builder
  // =================
  //
  // Transforms the JSON representation of an AdvancedFilter into a valid ODATA query URI parameter.

  var NULL_LITERAL = {
    name: 'NULL',
    type: 'NULL',
    params: []
  };

  var EMPTY_STRING = {
    name: 'EMPTY_STRING',
    type: String,
    params: []
  };

  var TRUE_LITERAL = {
    name: 'TRUE',
    type: Boolean,
    params: []
  };

  var FALSE_LITERAL = {
    name: 'FALSE',
    type: Boolean,
    params: []
  };

  var lastNToOpTransformer = function(opName, offsetter) {
    return function(json) {
      var attr = json.params[0];
      var literal = json.params[1];
      var range = json.params[2]; //LastNWeeks...etc

      if (!attr || !literal) {
        throw ['Expected attr and literal params', json];
      }

      var date = new Date();
      offsetter(date, literal.params[0], range.params[0]);

      return {
        name: opName,
        params: [
          Em.copy(attr, true),
          {
            name: 'DATE',
            params: [date.toISOString()]
          }
        ]
      };
    }
  };

  var escapeString = function(str) {
    return "'" + str.replace("'", "''") + "'";
  };

  var prepareDateUTC = function(date, daysToAdd) {
    var requestedDate = new Date(date);
    return new Date(Date.UTC(
      requestedDate.getUTCFullYear(), requestedDate.getUTCMonth(), requestedDate.getUTCDate() + daysToAdd
    ));
  };

  return Em.Object.extend({
    type: 'odata',
    isGuid: false,

    toString: function() {
      return 'This is ODATA uri converter';
    },

    transforms: {
      TRUE_OP: function(json) {
        return {
          name: 'EQUALS',
          type: Boolean,
          params: [
            Em.copy(json.params[0], true),
            TRUE_LITERAL
          ]
        };
      },

      FALSE_OP: function(json) {
        return {
          name: 'EQUALS',
          type: Boolean,
          params: [
            Em.copy(json.params[0], true),
            FALSE_LITERAL
          ]
        };
      },

      IS_EMPTY: function(json) {
        var attr = json.params[0];
        var literal = json.params[1];

        if (Em.isNone(attr) || !Em.isNone(literal)) {
          throw ['Expected attr but no literal params', json];
        }

        var equalsParams = {
          name: 'EQUALS',
          type: Boolean,
          params: [
            Em.copy(attr, true),
            NULL_LITERAL
          ]
        };

        // Make sure endpoint searches for both null and '' values when field is of type String
        if (String === attr.type) {
          return {
            name: 'OR',
            params: [
              equalsParams,
              {
                name: 'EQUALS',
                type: Boolean,
                params: [
                  Em.copy(attr, true),
                  EMPTY_STRING
                ]
              }
            ]
          };
        } else {
          return equalsParams;
        }
      },

      IS_NOT_EMPTY: function(json) {
        var attr = json.params[0];
        var literal = json.params[1];

        if (Em.isNone(attr) || !Em.isNone(literal)) {
          throw ['Expected attr but no literal params', json];
        }

        var notEqualsParams = {
          name: 'NOT_EQUALS',
          type: Boolean,
          params: [
            Em.copy(attr, true),
            NULL_LITERAL
          ]
        };

        // Make sure endpoint searches for both null and '' values when field is of type String
        if (String === attr.type) {
          return {
            name: 'AND',
            params: [
              notEqualsParams,
              {
                name: 'NOT_EQUALS',
                type: Boolean,
                params: [
                  Em.copy(attr, true),
                  EMPTY_STRING
                ]
              }
            ]
          };
        } else {
          return notEqualsParams;
        }
      },

      DAY_OF: function(json) {
        var attr = json.params[0];
        var literal = json.params[1];

        if (!attr || !literal) {
          throw ['Expected attr and literal params', json];
        }

        var dayStartLiteral = Em.copy(literal, true);
        dayStartLiteral.params[0] = prepareDateUTC(literal.params[0], 0).toISOString();

        var dayEndLiteral = Em.copy(literal, true);
        dayEndLiteral.params[0] = prepareDateUTC(literal.params[0], 1).toISOString();

        return {
          name: 'AND',
          params: [
            {
              name: 'GREATER_EQUALS',
              params: [
                Em.copy(attr, true),
                dayStartLiteral
              ]
            },
            {
              name: 'LESS_THAN',
              params: [
                Em.copy(attr, true),
                dayEndLiteral
              ]
            }
          ]
        };
      },

      BETWEEN_DATE: function(json) {
        var attr = json.params[0];
        var literal1 = json.params[1];
        var literal2 = json.params[2];

        if (!attr || !literal1 || !literal2) {
          throw ['Expected attr and literal params', json];
        }

        var dayStartLiteral = Em.copy(literal1, true);
        dayStartLiteral.params[0] = prepareDateUTC(literal1.params[0], 0).toISOString();

        var dayEndLiteral = Em.copy(literal2, true);
        dayEndLiteral.params[0] = prepareDateUTC(literal2.params[0], 1).toISOString();

        return {
          name: 'AND',
          params: [
            {
              name: 'GREATER_EQUALS',
              params: [
                Em.copy(attr, true),
                dayStartLiteral
              ]
            },
            {
              name: 'LESS_THAN',
              params: [
                Em.copy(attr, true),
                dayEndLiteral
              ]
            }
          ]
        };
      },

      WITHIN: lastNToOpTransformer('GREATER_THAN', function(date, count, range) {
        switch(range) {
          case 'LastNYears':
            date.setFullYear(date.getFullYear() - count);
            break;
          case 'LastNMonths':
            date.setMonth(date.getMonth() - count);
            break;
          case 'LastNWeeks':
            date.setDate(date.getDate() - count * 7);
            break;
          case 'LastNDays':
            date.setDate(date.getDate() - count);
            break;
          case 'LastNHours':
            date.setHours(date.getHours() - count);
            break;
          case 'LastNMinutes':
            date.setMinutes(date.getMinutes() - count);
            break;
          default:
              throw ['Invalid range of the WITHIN Operator'];
            break;
        }
      }),

      NOT_WITHIN: lastNToOpTransformer('LESS_EQUALS', function(date, count, range) {
        switch(range) {
          case 'NotLastNYears':
            date.setFullYear(date.getFullYear() - count);
            break;
          case 'NotLastNMonths':
            date.setMonth(date.getMonth() - count);
            break;
          case 'NotLastNWeeks':
            date.setDate(date.getDate() - count * 7);
            break;
          case 'NotLastNDays':
            date.setDate(date.getDate() - count);
            break;
          case 'NotLastNHours':
            date.setHours(date.getHours() - count);
            break;
          case 'NotLastNMinutes':
            date.setMinutes(date.getMinutes() - count);
            break;
          default:
            throw ['Invalid range of the NOT_WITHIN Operator'];
            break;
        }
      })
    },

    conjunctions: {
      AND: 'and',
      OR: 'or'
    },

    functions: {
      SUBSTRING_OF: { serialized: 'substringof', params: [1, 0] },
      NOT_SUBSTRING_OF: { serialized: 'not substringof', params: [1, 0], wrap: true }
    },

    binaryOps: {
      EQUALS: 'eq',
      NOT_EQUALS: 'ne',
      GREATER_THAN: 'gt',
      GREATER_EQUALS: 'ge',
      LESS_EQUALS: 'le',
      LESS_THAN: 'lt',
      DAY_OF: 'ge'
    },

    trinaryOps: {
      BETWEEN_DATE: 'ge'
      //even though WITHIN and NOT_WITHIN are technically trinaryOps they are handled in the transforms above
    },

    aggregateOps: {
      ANY_AGGREGATE: 'any',
      All_AGGREGATE: 'all'
    },

    // Constants
    // ---------

    constants: {
      NULL: 'null',

      // OData does not like double quotes for empty strings
      EMPTY_STRING: "''",

      TRUE: 'true',
      FALSE: 'false'
    },

    // Value Serialization
    // -------------------

    values: {
      ATTR: {
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          // Regular fields e.g. agentStatus
          var attr = attrToEndPointNameMap[val];

          // Case of aggregate OneToOne fields  e.g. version.String
          if (attr && attr.match(/[.]/)) {
            attr = attr.replace('.', '/');
          } else if (!attr) {
            // Case of aggregate OneToMany fields e.g. adapterVersion
            // Return the fully qualified attr name of an aggregate data
            for (var key in attrToEndPointNameMap) {
              var regex = new RegExp('.' + val + '$');
              if (key.match(regex)) {
                attr = attrToEndPointNameMap[key].replace('.', '/');

                // For the sake of building 'any' function, convert the parent's name to child's name
                // 'alias' keyword would not work since there might be different aggregate data being queried
                var splittedAttr = attr.split('/');
                attr = attr.replace(splittedAttr[0], splittedAttr[1]);
              }
            }
          }

          if ('undefined' === typeof(attr)) {
            throw ['Unknown attr queried:', val, attrToEndPointNameMap];
          }

          if(/Id$/.test(attr)) {
              scope.set('isGuid', true);
          }

          return attr;
        }
      },
      STRING: {
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return escapeString(val);
        }
      },
      NUMBER: {
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return val.toString(10);
        }
      },
      INTEGER: {
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return val.toString(10);
        }
      },
      // FIXME: Implementation of DATE serializer should indicate only date not datetime
      // such as DATETIME serializer
      DATE: {
        // Crop out milliseconds
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return 'datetime' + escapeString(val.replace(/\.\d\d\dZ$/, 'Z'));
        }
      },
      DATETIME: {
        // Crop out milliseconds
        // val [object Date]
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return 'datetime' + escapeString(val.toISOString().replace(/\.\d\d\dZ$/, 'Z'));
        }
      },
      GUID: {
        serializer: function(val, type, attrToEndPointNameMap, scope) {
          return 'guid' + escapeString(val);
        }
      }
    },

    serialize: function(json, searchableAttrs, attrToEndPointNameMap, depth) {

      if ('undefined' === typeof(depth)) {
        depth = 0;
      }

      var params = Em.A(json.params);
      var behavior = this.getProperties('transforms conjunctions functions binaryOps trinaryOps aggregateOps constants values'.w());
      var spec = undefined;
      
      var expandedJson = null;
      if (expandedJson = this.expandAny(json, searchableAttrs)) {
        // NOTE: "depth", NOT "depth + 1"
        return this.serialize(expandedJson, searchableAttrs, attrToEndPointNameMap, depth);
      } else if (spec = behavior.transforms[json.name]) {
        // NOTE: "depth", NOT "depth + 1"
        return this.serialize(spec(json), searchableAttrs, attrToEndPointNameMap, depth);
      } else if (spec = behavior.conjunctions[json.name]) {
        // N parameters joined by the conjunction, with brackets if nested
        if (params.length < 2) {
          throw ['Wrong number of params:', params, spec, json];
        }

        return this.formatBrackets(
          params.map(function(param) {
            return this.serialize(param, searchableAttrs, attrToEndPointNameMap, depth + 1);
          }, this).join(' ' + spec + ' '), 
          depth > 0
        );
      } else if (spec = behavior.aggregateOps[json.name]) {
        if (params.length < 2) {
          throw ['Wrong number of params:', params, spec, json];
        }

        return this.addAnyFunction(
          this.serialize(params[0], searchableAttrs, attrToEndPointNameMap),
          this.serialize(params[1].params[0], searchableAttrs, attrToEndPointNameMap).replace(/[^\.]*\//, ''),
          this.serialize(params[1], searchableAttrs, attrToEndPointNameMap)
        );
      } else if (spec = behavior.functions[json.name]) {
        // <serialized>(params[<params[0]>], params[<params[1]>], ...)
        if (spec.params.length !== params.length) {
          throw ['Wrong number of params:', spec.params, params, spec, json];
        }

        return this.formatBrackets(
          spec.serialized + '(' + spec.params.map(function(idx) {
            return this.serialize(params[idx], searchableAttrs, attrToEndPointNameMap, depth + 1);
          }, this).join(', ') + ')',
          spec.wrap
        );
      } else if (spec = behavior.binaryOps[json.name]) {
        // 2 params joined by spec, with brackets if nested
        if (2 !== params.length) {
          throw ['Wrong number of params:', params, spec, json];
        }

        // Additional check for the equal and not_equal operators.
        // Server can't handle neither Eq nor Ne operator for the Datetime, it requires a date range.
        var pName = params[1].name;
        if ((spec=== 'eq' || spec === 'ne') && pName === 'DATE') {

          var dateTime = params[1];
          var endDate = prepareDateUTC(dateTime.params[0], 1);   // add extra day to selected date
          var startDate = prepareDateUTC(dateTime.params[0], 0); // override date with UTC date

          var p0 = this.serialize(params[0], searchableAttrs, attrToEndPointNameMap, depth + 1);
          var p1 = behavior.values.DATETIME.serializer(startDate);
          var p2 = behavior.values.DATETIME.serializer(endDate);
          var query;

          if (spec === 'eq') {
            // create equal query
            query = p0 + ' ge ' + p1  + ' and ' + p0 + ' lt ' + p2;
          } else {
            // create not_equal query
            query = p0 + ' lt ' + p1 + ' or ' + p0 + ' ge ' + p2;
          }

          return this.formatBrackets(query, depth > 0 );
        }

        return this.formatBrackets(
          this.serialize(params[0], searchableAttrs, attrToEndPointNameMap, depth + 1) + 
          ' ' + spec + ' ' +
          this.serialize(params[1], searchableAttrs, attrToEndPointNameMap, depth + 1),
          depth > 0
        );
      } else if (spec = behavior.trinaryOps[json.name]) {
        // 3 params joined by 2 specs, with brackets if nested
        if (3 !== params.length) {
          throw ['Wrong number of params:', params, spec, json];
        }

        return this.formatBrackets(
                this.serialize(params[0], searchableAttrs, attrToEndPointNameMap, depth + 1) +
                ' ' + spec + ' ' +
                this.serialize(params[1], searchableAttrs, attrToEndPointNameMap, depth + 1) + ' and ' +
                this.serialize(params[0], searchableAttrs, attrToEndPointNameMap, depth + 1) +
                    ' le ' +
                this.serialize(params[2], searchableAttrs, attrToEndPointNameMap, depth + 1),
                depth > 0
        );
      } else if (spec = behavior.constants[json.name]) {
        return spec;
      } else if (spec = behavior.values[json.name]) {
        // format a value according to the given serializer function
        if (1 !== params.length) {
          throw ['Wrong number of params:', params, spec, json];
        }

        var val = params[0];
        var type = json.type;

        return spec.serializer(val, type, attrToEndPointNameMap, this);
      } else {
        throw ['Unknown token encountered:', json.name, json];
      }
    },

    formatBrackets: function(val, bracket) {
      return (bracket ? '(' : '') + val + (bracket ? ')' : '');
    },

    // This function adds the ANY function to the query
    // e.g. this query: (BroadbandAdapters/AdapterVersion Eq 'a')
    // will be changed to: BroadbandAdapters/any(AdapterVersion: (AdapterVersion/AdapterVersion Eq 'a'))
    addAnyFunction: function(parentAttr, childAttr, formatted) {
      return parentAttr + '/any(' + childAttr + ': ' + formatted + ')';
    },

    expandAny: function(json, searchableAttrs) {
      var ANY_ATTR = 'ANY_ATTR';
      var params = json.params;

      if (ANY_ATTR === json.name) {
        // Since we look within the params array for ANY_ATTR and expand whenever we find it, we should never
        // see json.name match it
        throw ['Top level ANY_ATTR makes no sense', json];
      }

      var anyIdx = -1;
      for (var idx = 0; idx < params.length; idx++) {
        var param = params[idx];
        if (!Em.isNone(param) && ANY_ATTR === param.name) {
          anyIdx = idx;
          break;
        }
      }

      // If the params array includes an ANY_ATTR
      if (-1 !== anyIdx) {
        // Validate that there are at most 1 ANY_ATTR ops
        var countAnys = function(jsonToCount) {
          return (jsonToCount.name === ANY_ATTR ? 1 : 0) + Em.A(jsonToCount.params).reduce(countAnys, 0);
        };

        if (countAnys(json) > 1) {
          throw ['Detected multiple ANY_ATTR parameters, which is not supported.', json];
        }

        // Expand "ANY_ATTR" parameters.
        //
        // Given:
        //
        //     {
        //       name: 'SUBSTRING_OF',
        //       type: Boolean,
        //       params: [
        //         { name: 'ANY_ATTR', type: 'ANY', params: [] },
        //         { name: 'STRING', type: String, params: ['search criteria'] }
        //       ]
        //     }
        //
        // Expand to:
        //
        //     {
        //       name: 'OR',
        //       type: Boolean,
        //       params: [
        //         {
        //           name: 'SUBSTRING_OF',
        //           type: Boolean,
        //           params: [
        //             { name: 'ATTR', type: String, params: ['foo'] },
        //             { name: 'STRING', type: String, params: ['search criteria'] }
        //           ]
        //         },
        //
        //         ...
        //
        //         {
        //           name: 'SUBSTRING_OF',
        //           type: Boolean,
        //           params: [
        //             { name: 'ATTR', type: String, params: ['bar'] },
        //             { name: 'STRING', type: String, params: ['search criteria'] }
        //           ]
        //         }
        //       ]
        //     }
        var expanded = {
          name: 'OR',
          type: Boolean,
          params: searchableAttrs.map(function(attrName) {
            var searchableAttrOp = Em.copy(json, true);

            searchableAttrOp.params[anyIdx] = {
              name: 'ATTR',
              type: String,
              params: [attrName]
            };

            return searchableAttrOp;
          })
        };

        // If there is only a single searchable attr, then the OR wrapper is unnecessary and will in fact result in a
        // broken ODATA query.
        return 1 === expanded.params.length ? expanded.params[0] : expanded;
      }

      // else no ANY_ATTR to expand, return null so caller can act on that fact
      return null;
    }
  })
});
