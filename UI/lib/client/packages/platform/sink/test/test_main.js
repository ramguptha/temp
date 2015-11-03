define([
  'testing',
  'ember',
  'packages/platform/sink'
], function(
  Testing,
  Ember,
  Sink
) {
  'use strict';

  return Testing.package('platform/sink', [
    Testing.module('main', [
      Testing.test('add / remove objects', function(assert) {
        var a = {};

        var sink = Sink.create();

        // Test properties of sink in initial state (i.e. empty)
        assert.ok(!sink.contains(a));
        assert.strictEqual(sink.get('length'), 0);

        // Add an object, still empty!
        sink.addObject(a);

        assert.ok(!sink.contains(a));
        assert.deepEqual(sink.toArray(), []);
        assert.strictEqual(sink.get('length'), 0);

        // Remove object, still empty!
        sink.removeObject(a);

        assert.ok(!sink.contains(a));
        assert.strictEqual(sink.get('length'), 0);

        // Enumeration should be over empty set
        sink.forEach(function() {
          assert.ok(false, 'Should not run');
        });
      }),

      Testing.test('observers', function(assert) {
        var a = {}, b = {};

        var lengthObserverCallCount = 0;
        var contentObserverCallCount = 0;

        var container = Em.Object.extend({
          sink: function() { return Sink.create(); }.property(),

          length: function() {
            return this.get('sink.length');
          }.property('sink.length'),

          content: function() {
            return this.get('sink').toArray();
          }.property('sink.[]'),

          lengthObserver: function() {
            lengthObserverCallCount += 1;
          }.observes('sink.length'),

          contentObserver: function() {
            contentObserverCallCount += 1;
          }.observes('sink.[]'),

          init: function() {
            this.get('sink');
            this.get('sink.length');
          }
        }).create();

        assert.strictEqual(container.get('length'), 0);
        assert.strictEqual(lengthObserverCallCount, 0);

        assert.deepEqual(container.get('content'), []);
        assert.strictEqual(contentObserverCallCount, 0);

        // Add an object - nothing happens
        container.get('sink').addObject(a);

        assert.strictEqual(lengthObserverCallCount, 0);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 0);
        assert.deepEqual(container.get('content'), []);

        // Remove an object - nothing happens
        container.get('sink').removeObject(a);

        assert.strictEqual(lengthObserverCallCount, 0);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 0);
        assert.deepEqual(container.get('content'), []);

        // Add multiple objects
        container.get('sink').addObjects([a, b]);

        assert.strictEqual(lengthObserverCallCount, 0);
        assert.strictEqual(container.get('length'), 0);

        assert.strictEqual(contentObserverCallCount, 0);
        assert.strictEqual(container.get('content.length'), 0);
      })
    ])
  ]);
});
