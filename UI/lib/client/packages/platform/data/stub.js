define([
  'ember',
  'packages/platform/formats',
  'packages/platform/ajax/stub',
  './lib/data_store_base',
  './lib/model',
  './lib/spec'
], function(
  Em,
  Format,
  AjaxStub,
  DataStoreBase,
  Model,
  Spec
) {
  'use strict';

  // Data Stubs
  // ==========
  //
  // Here is a set of convenience classes for stubbing the data system.

  // Stub Data Store Base
  // --------------------
  //
  // The base class for stubbed data stores. Overrides _createDataSourceForQuery()_ to return a DataSource which is
  // driven by a stubbed Ajax state machine. To use this class, set _stubData_ to JSON-structured data, and set the
  // _stubCallback_ to a handler which will invoke the _start()_ method of asynchronous unit tests.

  var StubDataStoreBase = DataStoreBase.extend({
    name: 'STUB DATA SOURCE',

    stubData: null,

    stubCallback: null,

    createDataSourceForQuery: function(query) {
      var self = this;

      return this.DataSource.create({
        Ajax: AjaxStub.createJson(
          200,
          self.get('stubData'),
          function(ajax, verb, contentType, desc, url, vars, response) {
            var callback = self.get('stubCallback');

            if (callback) {
              return callback.apply(self, arguments);
            }
          }
        ),

        query: query
      });
    }
  });

  // Stub Spec Factory
  // -----------------
  //
  // Manufactures a trivial spec, with a String id property, plus all other properties provided, all of type
  // String.

  var makeStubSpec = function(propertyNames) {
    if (!Em.isArray(propertyNames)) {
      propertyNames = [propertyNames];
    }

    var format = { id: Format.ID };
    var resource = [{ attr: 'id', type: String }];

    propertyNames.forEach(function(name) {
      format[name] = Format.String;
      resource.push({ attr: name, type: String });
    });

    return Spec.create({ format: format, resource: resource });
  };

  // Stub Data Factory
  // -----------------
  //
  // Manufactures data structed to conform to the stubSpec.

  var makeStubData = function(spec, count, start) {

    // _start_ is an optional parameter - if not set, it will be taken as 0.
    if ('number' !== typeof(start)) {
      start = 0;
    }

    var data = new Array(count);
    for (var i = 0; i < count; i++) {
      var index = start + i;
      var itemData = { id: 'stub id ' + index };

      spec.resource.forEach(function(attrSpec) {
        if (Number === attrSpec.type) {
          itemData[attrSpec.attr] = index;
        } else if (String === attrSpec.type) {
          itemData[attrSpec.attr] = 'stub ' + attrSpec.attr + ' ' + index;
        } else throw ['Unsupported attr type', attrSpec];
      });

      data[i] = itemData;
    }

    return data;
  };

  // Stub Data Store
  // ---------------
  //
  // A "nearly complete" stubbed data store. To use, instantiate it in an asynchronous unit tests with the
  // stubCallback set to the "start" method.

  var defaultStubSpec = makeStubSpec('value');
  var defaultStubData = makeStubData(defaultStubSpec, 3);

  var StubDataStore = StubDataStoreBase.extend({
    Model: Model.extend({ Spec: defaultStubSpec }),
    Spec: defaultStubSpec,
    stubData: defaultStubData
  });

  return {
    StubDataStoreBase: StubDataStoreBase,
    StubDataStore: StubDataStore,

    StubModel: Model,
    StubSpec: Spec,

    defaultStubSpec: defaultStubSpec,
    defaultStubData: defaultStubData,

    makeStubSpec: makeStubSpec,
    makeStubData: makeStubData
  };
});
