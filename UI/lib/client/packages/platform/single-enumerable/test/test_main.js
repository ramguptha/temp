define([
  'testing',
  'packages/platform/single-enumerable'
], function(
  Testing,
  SingleEnumerable
) {
  'use strict';

  return Testing.package('platform/single-enumerable', [
    Testing.module('main', [
      Testing.test('add / remove objects', function(assert) {
        var a = {}, b = {};

        var single = SingleEnumerable.create();

        // Test properties of an empty single-enumerable
        assert.ok(!single.contains(a));
        assert.strictEqual(single.get('length'), 0);

        // Add an object, and assert its state
        single.addObject(a);

        assert.ok(single.contains(a));
        assert.deepEqual(single.toArray(), [a]);
        assert.strictEqual(single.get('length'), 1);

        // Add the same object again, should throw
        assert.throws(function() {
          single.addObject(a);
        });

        // Remove an object not within the array, nothing should change
        single.removeObject(b);

        assert.ok(single.contains(a));
        assert.deepEqual(single.toArray(), [a]);
        assert.strictEqual(single.get('length'), 1);

        // Remove sole object, should be empty again
        single.removeObject(a);

        assert.ok(!single.contains(a));
        assert.strictEqual(single.get('length'), 0);

        // Add an object, via addObjects
        single.addObjects([a]);

        assert.ok(single.contains(a));
        assert.deepEqual(single.toArray(), [a]);
        assert.strictEqual(single.get('length'), 1);

        // Add same object again, plus a second object, should throw, content should be unchanged
        assert.throws(function() {
          single.addObjects([a, b]);
        });

        assert.ok(single.contains(a));
        assert.ok(!single.contains(b));
        assert.strictEqual(single.get('length'), 1);

        // Remove second object, should do nothing
        single.removeObjects([b]);

        assert.ok(single.contains(a));
        assert.ok(!single.contains(b));
        assert.strictEqual(single.get('length'), 1);
        assert.deepEqual(single.toArray(), [a]);

        // Set content object should update collection
        single.set('content', b);
        assert.deepEqual(single.toArray(), [b]);

        // Clear
        single.clear();

        assert.ok(!single.contains(a));
        assert.strictEqual(single.get('length'), 0);
        assert.deepEqual(single.toArray(), []);
      }),

      Testing.test('observers', function(assert) {
        var a = {}, b = {};

        var lengthObserverCallCount = 0;
        var contentObserverCallCount = 0;

        var container = Em.Object.extend({
          single: function() { return SingleEnumerable.create(); }.property(),

          length: function() {
            return this.get('single.length');
          }.property('single.length'),

          content: function() {
            return this.get('single').toArray();
          }.property('single.[]'),

          lengthObserver: function() {
            lengthObserverCallCount += 1;
          }.observes('single.length'),

          contentObserver: function() {
            contentObserverCallCount += 1;
          }.observes('single.[]'),

          init: function() {
            this.get('single');
            this.get('single.length');
          }
        }).create();

        assert.strictEqual(container.get('length'), 0, 'initial length');
        assert.strictEqual(lengthObserverCallCount, 0, 'initial observer call count');

        assert.deepEqual(container.get('content'), [], 'initial content');
        assert.strictEqual(contentObserverCallCount, 0, 'initial content observer call count');

        // Add an object
        container.get('single').addObject(a);

        assert.strictEqual(lengthObserverCallCount, 1, 'length observer after add one');
        assert.strictEqual(container.get('length'), 1, 'length after add one');

        assert.strictEqual(contentObserverCallCount, 1, 'content observer after add one');
        assert.deepEqual(container.get('content'), [a], 'content after add one');

        // Set content object should trigger observers
        container.get('single').set('content', b, 'set content object');
        assert.deepEqual(container.get('single').toArray(), [b], 'array after setting content object of single');
        assert.deepEqual(container.get('content'), [b], 'content after setting content object of single');

        assert.strictEqual(lengthObserverCallCount, 1, 'length observer not fired after update content');
        assert.strictEqual(container.get('length'), 1, 'length unchanged after update content');

        assert.strictEqual(contentObserverCallCount, 2, 'content observer fired after update content');
        assert.strictEqual(container.get('content.length'), 1, 'content length unchanged after update content');

        // Remove an object
        container.get('single').removeObject(b);

        assert.strictEqual(lengthObserverCallCount, 2);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 3);
        assert.deepEqual(container.get('content'), []);

        // Add multiple objects
        assert.throws(function() {
          container.get('single').addObjects([a, b]);
        });

        assert.strictEqual(lengthObserverCallCount, 2);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 3);
        assert.strictEqual(container.get('content.length'), 0);

        // Clear
        container.get('single').clear();

        assert.strictEqual(lengthObserverCallCount, 2);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 3);
        assert.strictEqual(container.get('content.length'), 0);
      })
    ])
  ]);
});
