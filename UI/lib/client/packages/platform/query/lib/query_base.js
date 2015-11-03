define([
  'jquery',
  'ember',
  'logger'
], function(
  $,
  Em,
  logger
) {
  'use strict';

  // QueryBase
  // =========
  //
  // Base class for all queries.
  return Em.Object.extend(Em.Copyable, {
    init: function() {
      this._super();
      this.set('observableUri', this.get('uri'));
    },

    // Marker.
    isQuery: true,

    // It is possible to flag a query an invalid. This is for situations where a query context needs to 
    // be built asynchronously, and we don't want anything monitoring the final URI to act on it until
    // the context is ready.
    isValid: true,

    // If true, this query is intended to return a count of the related data, not the data itself.
    isCounter: false,

    // The data store
    store: null,

    // If true, the data_source for this query will reload when it is invalidated. Not to be changed
    // after initial instantiation.
    autoRefresh: false,

    // Context to run the query in (e.g. parent objects)
    context: null,

    baseNames: 'context store autoRefresh isValid isCounter'.w(),

    // Convenience method for uri generation
    serialize: function(type, names) {
      var prefix = 'urn:query:' + this.get('store.name') + ':';

      // Invalid queries all get the same URI for the given data store
      if (!this.get('isValid')) {
        return prefix + 'invalid';
      }

      var attrs = this.getProperties(names);

      var context = this.get('context');
      var contextNames = [];
      for (var name in context) {
        contextNames.push(name);
      }

      // Build args, an array of name value pairs (an array since we need to be ordered)
      var args = contextNames.sort().map(function(name) {
        return { name: name, value: context[name] };
      });

      names.forEach(function(name) { 
        var val = attrs[name];
        if (val) {
          if (Em.isArray(val)) {
              // Serialize the elements of the array. Note that we are quite restricted in the types we
              // allow for the elements of the array; string, number or object.
              val.forEach(function(item, i) {
                if ('string' === typeof(item) || 'number' === typeof(item)) {
                  args.push({ name: name + '[' + i + ']', value: item });
                } else {
                  for (var subName in item) {
                    args.push({ name: name + '[' + i + '].' + subName, value: item[subName] });
                  }
                }
              })
          } else {
            args.push({ name: name, value: val });
          }
        }
      });

      if (this.get('autoRefresh')) {
        args.push({ name: 'autoRefresh', value: 'true' });
      }

      var serialized = prefix + type + '?' + $.param(args);
      logger.log('AM_DATA: QUERY: serialize', serialized, args, type, attrs, names, context);
      return serialized;
    },

    // A unique string for the given query
    uri: function() { throw 'required'; }.property(),

    // URI properties often have many dependencies, which may change without actually changing the result.
    // This value may be observed when you REALLY need to only know when the result has changed.
    observableUri: null,

    // When the observable Uri last changed
    observableUriUpdatedAt: null,

    // Ensure that only a single update to observableUri is scheduled at a time
    observableUriUpdateScheduled: false,

    // Throttle updates to observableUri to keep computational costs sane
    observableUriUpdateDelayInMilliseconds: 250,

    uriDidChange: function() {
      if (!this.get('observableUriUpdateScheduled')) {
        Em.run.later(this, function() {
          if (this.get('observableUri') !== this.get('uri')) {
            this.setProperties({
              observableUri: this.get('uri'),
              observableUriUpdatedAt: new Date()
            });
          }

          this.set('observableUriUpdateScheduled', false);
        }, this.get('observableUriUpdateDelayInMilliseconds'));

        this.set('observableUriUpdateScheduled', true);
      }
    }.observes('uri'),

    copy: function() {
      throw 'not implemented';
    },

    // Filter data based on the parameters encapsulated by this query.
    run: function(searchableAttrNames, data) {
      return this.page(this.search(searchableAttrNames, data));
    }
  });
});
