define([
  'testing',
  'packages/platform/set'
], function(
  Testing,
  Set
) {
  'use strict';

  return Testing.package('platform/set', [
    Testing.module('main', [
      Testing.test('add / remove objects', function(assert) {
        var a = {}, b = {};

        var set = Set.create();

        // Test properties of an empty set
        assert.ok(!set.contains(a));
        assert.strictEqual(set.get('length'), 0);

        // Add an object, and assert its state
        set.addObject(a);

        assert.ok(set.contains(a));
        assert.deepEqual(set.toArray(), [a]);
        assert.strictEqual(set.get('length'), 1);

        // Add the same object again, nothing should change
        set.addObject(a);

        assert.ok(set.contains(a));
        assert.deepEqual(set.toArray(), [a]);
        assert.strictEqual(set.get('length'), 1);

        // Remove an object not within the array, nothing should change
        set.removeObject(b);

        assert.ok(set.contains(a));
        assert.deepEqual(set.toArray(), [a]);
        assert.strictEqual(set.get('length'), 1);

        // Remove sole object, should be empty again
        set.removeObject(a);

        assert.ok(!set.contains(a));
        assert.strictEqual(set.get('length'), 0);

        // Add an object, via addObjects
        set.addObjects([a]);

        assert.ok(set.contains(a));
        assert.deepEqual(set.toArray(), [a]);
        assert.strictEqual(set.get('length'), 1);

        // Add same object again, plus a second object
        set.addObjects([a, b]);

        assert.ok(set.contains(a));
        assert.ok(set.contains(b));
        assert.strictEqual(set.get('length'), 2);

        // Remove second object
        set.removeObjects([b]);

        assert.ok(set.contains(a));
        assert.ok(!set.contains(b));
        assert.strictEqual(set.get('length'), 1);
        assert.deepEqual(set.toArray(), [a]);

        // Clear
        set.clear();

        assert.ok(!set.contains(a));
        assert.strictEqual(set.get('length'), 0);
        assert.deepEqual(set.toArray(), []);
      }),

      Testing.test('observers', function(assert) {
        var a = {}, b = {};

        var lengthObserverCallCount = 0;
        var contentObserverCallCount = 0;

        var container = Em.Object.extend({
          set: function() { return Set.create(); }.property(),

          length: function() {
            return this.get('set.length');
          }.property('set.length'),

          content: function() {
            return this.get('set').toArray();
          }.property('set.[]'),

          lengthObserver: function() {
            lengthObserverCallCount += 1;
          }.observes('set.length'),

          contentObserver: function() {
            contentObserverCallCount += 1;
          }.observes('set.[]'),

          init: function() {
            this.get('set');
            this.get('set.length');
          }
        }).create();

        assert.strictEqual(container.get('length'), 0);
        assert.strictEqual(lengthObserverCallCount, 0);

        assert.deepEqual(container.get('content'), []);
        assert.strictEqual(contentObserverCallCount, 0);

        // Add an object
        container.get('set').addObject(a);

        assert.strictEqual(lengthObserverCallCount, 1);
        assert.strictEqual(container.get('length'), 1);

        assert.strictEqual(contentObserverCallCount, 1);
        assert.deepEqual(container.get('content'), [a]);

        // Remove an object
        container.get('set').removeObject(a);

        assert.strictEqual(lengthObserverCallCount, 2);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 2);
        assert.deepEqual(container.get('content'), []);

        // Add multiple objects
        container.get('set').addObjects([a, b]);

        assert.strictEqual(lengthObserverCallCount, 3);
        assert.strictEqual(container.get('length'), 2);

        assert.strictEqual(contentObserverCallCount, 3);
        assert.strictEqual(container.get('content.length'), 2);

        // Clear
        container.get('set').clear();

        assert.strictEqual(lengthObserverCallCount, 4);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 4);
        assert.strictEqual(container.get('content.length'), 0);
      })
    ])
  ]);
});
