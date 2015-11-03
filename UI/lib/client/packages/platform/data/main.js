define([
  'ember',
  './lib/namespace',
  './lib/single_result_proxy',
  './lib/data_store_shared',

  './lib/data_store',
  './lib/mock_data_store',

  './lib/data_counter',
  './lib/data_source',
  './lib/odata_data_source',
  './lib/client_searched_data_source',
  './lib/hierarchical_data_source_mixin',

  './lib/action',
  './lib/action_data',
  './lib/action_history',
  './lib/model',
  './lib/spec',
  './lib/composite_spec'
], function(
  Em,
  AbsData,
  SingleResultProxy,
  allDataStores,

  DataStore,
  MockDataStore,

  DataCounter,
  DataSource,
  ODataDataSource,
  ClientSearchedDataSource,
  HierarchicalDataSourceMixin,

  Action,
  ActionData,
  ActionHistory,
  Model,
  Spec,
  CompositeSpec
) {
  'use strict';

  return AbsData.reopen({
    allDataStores: allDataStores,

    SingleResultProxy: SingleResultProxy,

    DataStore: DataStore,
    MockDataStore: MockDataStore,

    DataCounter: DataCounter,
    DataSource: DataSource,
    ODataDataSource: ODataDataSource,
    ClientSearchedDataSource: ClientSearchedDataSource,
    HierarchicalDataSourceMixin: HierarchicalDataSourceMixin,

    Action: Action,
    ActionData: ActionData,
    ActionHistory: ActionHistory,
    Model: Model,
    Spec: Spec,
    CompositeSpec: CompositeSpec
  });
});
