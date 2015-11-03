define([
  'ember',
  './namespace',
  'locale',
  'packages/platform/ajax',
  'packages/platform/data',
  'packages/am/am-push-notifications'
], function(
  Em,
  AmData,
  Locale,
  Ajax,
  AbsData,
  push
) {
  'use strict';

  // A DataSource is returned when a store is queried.
  return AbsData.get('DataSource').extend({
    // A dataSource has an endPoint
    endPoint: null,

    // Some dataSources have endpoints for push notifications
    pushEndpoint: null,

    freshen: function(force, loadedCallback, loadFailedCallback, callbackScope) {
      this._super(force, loadedCallback, loadFailedCallback, callbackScope);

      if( this.get('pushEndpoint') && callbackScope.dataStore) {
        push.subscribe(this.get('pushEndpoint'), callbackScope.get('dataStore'));
      }
    },
    
    loginPathRedirectOnError: '../login/?sessionTimedOut=true',

    // redirect all 401s ( aka session just exploded ) back to the login page
    loadFailed: function(errorDetail) {
      this._super(errorDetail);

      if( errorDetail.jqXHR.status === 401 ) {
        document.location = this.get('loginPathRedirectOnError');
      }
    },

    // JSON endPoint to request from
    uri: function() {
      var query = this.get('query');
      var endPoint = this.get('endPoint');
      var isCounter = this.get('isCounter');

      return AmData.get('urlRoot') + this.pathForUri(endPoint, query) + this.cgiParamsForUri(query, isCounter);
    }.property(),

    pathForUri: function(endPoint, query) {
      if (query.get('isSingleton')) {
        return endPoint + '/' + query.get('id');
      } else {
        return endPoint;
      }
    },

    cgiParamsForUri: function(query, isCounter) {
      var self = this;
      var mappedAttributes = query.get('store.Spec.resource');

      var params = '';

      var paramNameMapper = {
        sort: '$orderby',
        searchFilter: '$search',
        limit: '$top',
        offset: '$skip'
      };

      if (query.get('isSearch')) {
        if (query.get('advancedFilter.isValid')) {
          throw 'Advanced filtering not supported for AM View Data Source';
        }

        var queryVars = 'sort searchFilter limit offset'.w().map(function(name) {
          var value = query.get(name);
          var paramName = null;

          if ('sort' === name && value) {
            if (value.length === 1) {
              var firstValue = value[0];
              // Get guid of sorted row
              var sortedAttrSpec = mappedAttributes.find(function(attrSpec) {
                return attrSpec.attr === firstValue.attr;
              });
              value = sortedAttrSpec.guid;
              value = value.concat(' ' + firstValue.dir);
              paramName = paramNameMapper[name];
            } else {
              throw ['AM does not support multi level of sorting'];
            }
          } else if ('searchFilter' === name && value) {
            // Get guid of searched row
            var searchedAttr = query.get('searchAttr');
            if (!Em.isEmpty(searchedAttr)) {
              var searchedAttrSpec = mappedAttributes.find(function(attrSpec) {
                return attrSpec.attr === searchedAttr;
              });
              paramName = paramNameMapper[name] + ':' + searchedAttrSpec.guid;
            } else {
              paramName = paramNameMapper[name];
            }
          } else if ('limit' === name) {
            if (!isCounter) {
              // Try to get one more than asked for, so that we can set isLastPage
              value += 1;
            } else {
              // Else get a single row
              value = 1;
            }
            paramName = paramNameMapper[name];
          } else if ('offset' === name) {
            if (!isCounter) {
              paramName = paramNameMapper[name];
            } // else don't specify offset
          } else {
            paramName = paramNameMapper[name];
          }

          return (paramName && value) ? (paramName + '=' + self.esc(value)) : null;
        }).compact();

        if (isCounter) {
          queryVars.push('$inlinecount=allpages');
        }

        params = (queryVars.length > 0 ? ('?' + queryVars.join('&')) : '');
      }

      return params;
    }
  });
});
