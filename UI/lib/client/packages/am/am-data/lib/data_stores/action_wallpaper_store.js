define([
  'ember',
  'packages/platform/data',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/action_wallpaper_spec'
], function(
  Em,
  AbsData,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  ActionWallpaperSpec
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: AbsData.get('Model').extend({
      Spec: ActionWallpaperSpec
    }),
    Spec: ActionWallpaperSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var actionId = query.get('context.actionId');
        if (actionId) {
          endPoint = '/api/actions/' + actionId + '/wallpaper';
        } else throw ['context required (actionId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
