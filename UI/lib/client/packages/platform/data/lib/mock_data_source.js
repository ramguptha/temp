define([
  'ember',
  './data_source',
  'logger'
], function(
  Em,
  DataSource,
  logger
) {
  'use strict';

  return DataSource.extend({
    load: function() {
      var self = this;

      // Simulate just enough delay to keep us asynchronous (and hence, hopefully, out of trouble)
      var loadImpl = function() {
        var dataStore = self.get('query.store');
        var query = self.get('query');
        var materializedObjects = dataStore.get('materializedObjects');

        if (self.get('isCounter')) {
          var matchedContent = dataStore.performSearch(query, materializedObjects);

          content = self.get('content');
          content.replace(0, content.get('length'), [
            self.DataCounter.create({ total: matchedContent.get('length') })
          ]);
        } else if (query.isSearch) {
          var matchedContent = dataStore.performSearch(query, materializedObjects);
          var unpagedContent = dataStore.performSort(query, matchedContent);

          var content = self.get('content');
          content.replace(0, content.get('length'), dataStore.performPaging(query, unpagedContent));

          self.set('isLastPage', unpagedContent.get('length') <= (query.get('offset') + query.get('limit')));
        } else if (query.isSingleton) {
          var obj = dataStore.get('materializedObjectsById')[query.get('id')];

          var content = self.get('content');
          content.replace(0, content.get('length'), [obj]);
        } else {
          throw ['Unsupported query type', query];
        }

        self.loadComplete();
      };

      Em.run.later(this, loadImpl, 100);
    }
  });
});
