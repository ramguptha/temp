define([
  'ember',
  'formatter',
  'packages/platform/advanced-filter',
  './query_base'
], function(
  Em,
  Formatter,
  AdvancedFilter,
  QueryBase
) {
  'use strict';

  // SearchQuery
  // ===========
  //
  // A SearchQuery encapsulates criteria that will return an arbitrary number of objects.
  var SearchQuery = QueryBase.extend({
    // Marker.
    isSearch: true,

    // A query optionally describes how the resultset should be sorted.
    //
    // It is structured as follows: [{ attr: 'attr name', dir: 'asc or desc'}, ...]
    sort: null,

    // A query optionally has a grouping parameter, which describes how aggregate functions (count, min, max)
    // should be partitiioned, and also how the resultset should be rendered.
    //
    // It is structured as follows: ['attr1', 'attr2', ...]
    group: null,

    limit: 1000,
    offset: 0,

    // searchFilter is often exposed to the user through search boxes. 
    searchAttr: null,
    searchFilter: null,

    // A query has a list of searchable columns which is an intersection of
    // visibleColumnNames and the Spec.searchableNames
    // Note! fields of type EnumType and Aggregate OneToMany are also filtered out from this list
    adhocSearchableNames: Em.A(),

    // advancedFilter is often exposed to the user via the AdvancedQuery[View / Controller]
    advancedFilter: null,
    advancedFilterAsString: function() {
      // _Must_ get property deps during get
      this.get('advancedFilter.asString');

      var filter = this.get('advancedFilter');
      var optimized = filter ? filter.optimize() : null;

      return optimized ? optimized.get('asString') : null;
    }.property('advancedFilter.asString'),

    // contextFilter is never exposed to the user. It is for use by the internals of the system when
    // additional "scope" is needed for a search query.
    contextFilter: null,
    contextFilterAsString: function() {
      // _Must_ get property deps during get
      this.get('contextFilter.asString');

      var filter = this.get('contextFilter');
      var optimized = filter ? filter.optimize() : null;

      return optimized ? optimized.get('asString') : null;
    }.property('contextFilter.asString'),

    // the searchFilter, advancedFilter, and the contextFilter are AND'ed together into a single, unified
    // AdvancedFilter for querying.
    mergedFilter: function() {
      var searchAttr = this.get('searchAttr');
      var searchFilter = this.get('searchFilter');

      // _Must_ get property deps during get
      this.getProperties('advancedFilterAsString contextFilterAsString'.w());
      
      var searchFilterCriteria = searchFilter;

      var mergedFilter = AdvancedFilter.AndFilter.create();

      if (!Em.isEmpty(searchFilterCriteria)) {
        var searchFilter = AdvancedFilter.SubstringOfOp.create({
          lval: Em.isEmpty(searchAttr) ?
            AdvancedFilter.AnyAttrConstant.create() :
            AdvancedFilter.Attr.create({ value: searchAttr, type: AdvancedFilter.TYPE_STRING }),
          rval: AdvancedFilter.StringLiteral.create({ value: searchFilterCriteria })
        });

        mergedFilter.get('operands').addObject(searchFilter);
      }

      var advancedFilter = this.get('advancedFilter');
      if (advancedFilter) {
        mergedFilter.get('operands').addObject(advancedFilter.copy());
      }

      var contextFilter = this.get('contextFilter');
      if (contextFilter) {
        mergedFilter.get('operands').addObject(contextFilter.copy());
      }

      return mergedFilter;
    }.property('searchAttr', 'searchFilter', 'advancedFilterAsString', 'contextFilterAsString'),

    mergedFilterAsString: function() {
      // _Must_ get property deps during get
      this.get('mergedFilter.asString');

      var filter = this.get('mergedFilter');
      var optimized = filter ? filter.optimize() : null;

      return optimized ? optimized.get('asString') : null;
    }.property('mergedFilter.asString'),

    attrNames: 'sort group isCounter searchAttr searchFilter'.w(),
    attrNamesWithoutSearch: 'sort group isCounter'.w(),

    names: function() {
      var names = [];
      return names.concat(this.get('attrNames')).concat(this.get('pageNames'));
    }.property(),

    namesWithoutSearch: function() {
      var names = [];
      return names.concat(this.get('attrNamesWithoutSearch')).concat(this.get('pageNames'));
    }.property(),

    pageNames: 'limit offset'.w(),

    performPaging: function(data) {
      var query = this.getProperties('offset', 'limit');
      return data.slice(query.offset, query.offset + query.limit);
    },

    filterSearchAttr: function(names) {
      if (Em.isEmpty(this.get('searchFilter'))) {
        names = names.filter(function(name) {
          return name !== 'searchAttr';
        });
      }

      return names;
    },

    pageFreeUri: function() {
      var attrNames = this.get('baseNames').concat(this.get('attrNamesWithoutSearch')).concat(['mergedFilterAsString']);
      return this.serialize('search', attrNames);
    }.property(
      'isValid',
      'isCounter',
      'context',
      'store.name',
      'sort.@each.attr',
      'sort.@each.dir',
      'group.[]',
      'mergedFilterAsString'
    ),

    uri: function() {
      // Validate sort
      var sort = this.get('sort');
      if (!Em.isNone(sort)) {
        var sortError = 'sort should be an array of objects { attr: \' \', dir: \' \' }';
        if (!Em.isArray(sort)) {
          throw sortError;
        } else {
          sort.forEach(function(items) {
            if (!items.hasOwnProperty('attr') || !items.hasOwnProperty('dir')) {
              throw sortError;
            }
            for (var item in items) {
              if (!(item === 'attr' || item === 'dir')) {
                throw sortError;
              }
            }
          });
        }
      }

      // Validate group
      var group = this.get('group');
      if (!Em.isNone(group)) {
        if (!Em.isArray(group) || Em.A(group).some(function(obj) { 'string' !== typeof(obj); })) {
          throw 'group should be an array of property names';
        }
      }

      return this.serialize('search', this.get('namesWithoutSearch').concat(['mergedFilterAsString']));
    }.property(
      'isValid',
      'isCounter',
      'context',
      'store.name',
      'sort.@each.attr',
      'sort.@each.dir',
      'group.[]',
      'mergedFilterAsString',
      'limit',
      'offset'
    ),

    copy: function() {
      var names = this.get('names').concat(this.get('baseNames'));
      var properties = this.getProperties(names);
      properties.advancedFilter = Em.copy(this.get('advancedFilter'));
      properties.contextFilter = Em.copy(this.get('contextFilter'));
      properties.sort = Em.copy(this.get('sort'), true);
      properties.group = Em.copy(this.get('group'), true);
      properties.adhocSearchableNames = Em.copy(this.get('adhocSearchableNames'), true);
      return SearchQuery.create(properties);
    },

    performSearch: function(data) {
      var query = this.getProperties('searchAttr searchFilter advancedFilter contextFilter adhocSearchableNames'.w());

      if ((!Em.isNone(query.advancedFilter) && query.advancedFilter.get('isValid')) ||
          (!Em.isNone(query.contextFilter) && query.contextFilter.get('isValid'))) {
        throw ['Advanced Filter is not supported for client side search: ', query.advancedFilter, query.contextFilter];
      }

      // filter data
      if (!Em.isEmpty(query.searchFilter)) {
        var attrNames = query.searchAttr ?  Em.A([query.searchAttr]) : query.adhocSearchableNames;
        var filter = query.searchFilter.toLowerCase();
        var decodedHTML = document.createElement('div');

        data = data.filter(function(obj) {
          return attrNames.some(function(attrName) {
            var value = obj.get('presentation.' + attrName);

            if (!Em.isEmpty(value)) {
              decodedHTML.innerHTML = value.string ? value.string : value;
              return decodedHTML.firstChild.nodeValue.toLocaleLowerCase().indexOf(filter) !== -1;
            }

            return false;
          });
        });
      }

      return Em.A(data);
    },

    performSort: function(data) {
      // no really, just an array please
      data = Em.A(data).toArray();

      var sort = this.get('sort');

      // sort data
      if (!Em.isEmpty(sort)) {
        data = data.sort(function(leftObj, rightObj) {
          for (var i = 0, j = sort.length; i < j; i++) {
            var spec = sort.objectAt(i);
            var leftVal = leftObj.get(spec.attr) || leftObj.get('data').get(spec.attr);
            var rightVal = rightObj.get(spec.attr) || rightObj.get('data').get(spec.attr);

            // locale transform, if available
            if (!Em.isNone(leftVal) && leftVal.toLocaleString && !Em.isNone(rightVal) && rightVal.toLocaleString) {
              leftVal = leftVal.toLocaleString().toLocaleLowerCase();
              rightVal = rightVal.toLocaleString().toLocaleLowerCase();
            }

            if ((Em.isNone(leftVal) && Em.isNone(rightVal)) || (leftVal === rightVal)) {
              // if last spec
              if (i + 1 === j) {
                return 0;
              } else {
                continue;
              }
            } else {
              var result = null;

              // case insensitive comparison for strings
              if (Em.isNone(leftVal)) {
                result = 1;
              } else if (Em.isNone(rightVal)) {
                result = -1;
              } else if ('string' === typeof(leftVal) && 'string' === typeof(rightVal)) {
                result = leftVal.localeCompare(rightVal);
              } else {
                result = leftVal > rightVal ? 1 : -1;
              }

              // reverse the sense of the comparison if we are descending
              return result * (spec.dir === 'asc' ? 1 : -1);
            }
          }
        });
      }

      return Em.A(data);
    }
  });

  return SearchQuery;
});
