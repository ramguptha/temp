define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Single Enumerable
  // =================
  //
  // A mutable enumerable that holds AT MOST one object. Throws when its constraints are violated.
  //
  // The single object is available for get and set via the _content_ property.
  //
  // Note that this data structure cannot store and retrieve "undefined" - it treats it as "no value".

  return Em.Object.extend(Em.MutableEnumerable, {
    // INTERNAL: the single object. Use _content_ instead or observers won't work properly.
    storage: undefined,

    // Proxy for first and only object in the array.
    //
    // Due to the inherent weirdness of Ember enumerables, setting correct property dependencies on length
    // will result in extra observer invocations! We are _forced_ to make it volatile and notify via 
    // enumerableContent\*Change().
    content: Em.computed({
      get: function() {
        return this.get('storage');
      },
      set: function(key, value) {
        var oldValue = this.get('storage');
        if (oldValue !== value) {
          var removing = (undefined !== oldValue) ? 1 : 0;
          var adding = (undefined !== value) ? 1 : 0;

          this.enumerableContentWillChange(removing, adding);
          this.set('storage', value);
          this.enumerableContentDidChange(removing, adding);
        }
        return value;
      }
    }).volatile(),

    length: function() {
      return (undefined === this.get('content')) ? 0 : 1;
    }.property().volatile(),

    nextObject: function(index, previousObject, context) {
      var next = undefined;
      if (0 === index && 0 < this.get('length')) {
        next = this.get('content');
      }
      return next;
    },

    addObject: function(obj) {
      this.enforceSingleton(1);

      this.set('content', obj);

      return obj;
    },

    removeObject: function(obj) {
      if (this.get('content') === obj) {
        this.set('content', undefined);
      }

      return obj;
    },

    addObjects: function(objects) {
      var length = Em.get(objects, 'length');
      this.enforceSingleton(length);

      if (length > 0) {
        this.set('content', objects.objectAt(0));
      }

      return this;
    },

    removeObjects: function(objects) {
      var willRemoveContent = false;
      var content = this.get('content');

      objects.forEach(function(obj) {
        if (content === obj) {
          willRemoveContent = true;
        }
      });

      if (willRemoveContent) {
        this.set('content', undefined);
      }

      return this;
    },

    enforceSingleton: function(countToBeAdded) {
      var length = this.get('length');
      if ((countToBeAdded + length) > 1) {
        throw ['Single enumerable may only hold one object at a time', length, countToBeAdded];
      }
    },

    clear: function() {
      this.set('content', undefined);
    }
  });
});
