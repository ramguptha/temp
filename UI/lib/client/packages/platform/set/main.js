define([
  'ember-core'
], function(
  Em
) {
  'use strict';

  // Set
  // ====
  //
  // Implements a set of objects. Add, remove, contains are all LINEAR, but very fast. Also implements _clear()_.
  return Em.Object.extend(Em.MutableEnumerable, {
    content: function() {
      return Em.A();
    }.property(),

    length: function() {
      return this.get('content.length');
    }.property('content.length').volatile(),

    contains: function(obj) {
      return this.get('content').contains(obj);
    },

    nextObject: function(index, previousObject, context) {
      return this.get('content').nextObject(index, previousObject, context);
    },

    addObject: function(obj) {
      var content = this.get('content');

      if (!content.contains(obj)) {
        this.enumerableContentWillChange();
        content.addObject(obj);
        this.enumerableContentDidChange();
      }

      return obj;
    },

    removeObject: function(obj) {
      var content = this.get('content');

      if (content.contains(obj)) {
        this.enumerableContentWillChange();
        content.removeObject(obj);
        this.enumerableContentDidChange();
      }

      return obj;
    },

    addObjects: function(objects) {
      var objectsToAdd = [];
      var content = this.get('content');

      objects.forEach(function(obj) {
        if (!content.contains(obj)) {
          objectsToAdd.push(obj);
        }
      });

      if (objectsToAdd.length > 0) {
        this.enumerableContentWillChange();
        content.addObjects(objectsToAdd);
        this.enumerableContentDidChange();
      }

      return this;
    },

    removeObjects: function(objects) {
      var objectsToRemove = [];
      var content = this.get('content');

      objects.forEach(function(obj) {
        if (content.contains(obj)) {
          objectsToRemove.push(obj);
        }
      });

      if (objectsToRemove.length > 0) {
        this.enumerableContentWillChange();
        content.removeObjects(objectsToRemove);
        this.enumerableContentDidChange();
      }

      return this;
    },

    clear: function() {
      var content = this.get('content');

      if (content.get('length') > 0) {
        this.enumerableContentWillChange();
        content.clear();
        this.enumerableContentDidChange();
      }

      return this;
    }
  });
});
