define([
  'jquery',
  'ember',

  './lib/query_base',
  './lib/singleton_query',
  './lib/search_query',

  'logger'
], function(
  $,
  Em,

  Query,
  SingletonQuery,
  SearchQuery,

  logger
) {
  'use strict';

  return { 
    Base: Query,
    Singleton: SingletonQuery,
    Search: SearchQuery
  };
});
