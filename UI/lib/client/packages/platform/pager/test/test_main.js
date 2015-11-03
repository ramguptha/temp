define([
  'testing',
  'guid',
  '../lib/node_types',
  '../lib/paged_data',
  '../lib/pager'
], function(
  Testing,
  Guid,
  NodeType,
  PagedData,
  Pager
) {
  'use strict';

  return Testing.package('platform/paged', [
    Testing.module('node_type', [
      Testing.test('root instanceof, initialization', function(assert) {
        var root = new NodeType.Root();
        assert.ok(root instanceof NodeType.Node, 'root instanceof Node');
        assert.ok(root.isNode, 'node marker is set');
        assert.ok(root.isRoot, 'root marker is set');
        assert.ok(root instanceof NodeType.Root, 'root instanceof Root');
        assert.ok(root.isExpanded, 'root.isExpanded');
        assert.strictEqual(root.offset, 0, 'root.offset');
      }),

      Testing.test('depth', function(assert) {
        var root = new NodeType.Root();
        var group = new NodeType.Group(Guid.generate());
        root.appendChild(group);
        var record = new NodeType.Record(Guid.generate());
        group.appendChild(record);

        assert.strictEqual(root.depth, 0, 'Root has depth 0');
        assert.strictEqual(group.depth, 1, 'Group has depth 1');
        assert.strictEqual(record.depth, 2, 'Record has depth 2');
      })
    ]),

    Testing.module('paged_data', [
      Testing.test('traversal, mutation', function(assert) {
        var paged = PagedData.create();
        var root = paged.get('root');
        var deferred = root.children[0];

        var walked = [];
        var addToWalked = function(node) {
          walked.push(node);
        };

        paged.walk(root, addToWalked);

        assert.deepEqual(walked, [root, deferred], 'walk in order');
        assert.strictEqual(root, paged.lookup(root, root.id), 'lookup root');
        assert.strictEqual(deferred, paged.lookup(root, deferred.id), 'lookup deferred');
        assert.deepEqual([], paged.read(root, 0, 10), 'read returns empty array');

        var newNodes = [new NodeType.Record(Guid.generate()), new NodeType.Record(Guid.generate())];
        root.appendChildren(newNodes);

        var walked = [];
        paged.walk(root, addToWalked);

        assert.deepEqual(walked, [root, newNodes[0], newNodes[1]], 'walk after replace deferred with 2 records');

        var walked = [];
        paged.walkVisible(root, addToWalked);

        assert.deepEqual(walked, newNodes, 'walk visible returns records only');
        assert.deepEqual(paged.read(root, paged.get('lastReadEnd'), 0, 10), newNodes, 'read returns records only');

        paged.reset(root);
        var deferred = root.children[0];
        var walked = [];
        paged.walk(root, addToWalked);

        assert.deepEqual(walked, [root, deferred]);
      }),

      Testing.test('read, getReadLength, getScrollLength', function(assert) {
        var paged = PagedData.create();
        var root = paged.get('root');

        assert.deepEqual(
          paged.read(root, paged.get('lastReadEnd'), 0, 10),
          [root.children[0]],
          'read from default tree should return single deferred'
        );
        assert.strictEqual(paged.getReadLength(), 1, '1 readable node when initialized');
        assert.strictEqual(paged.getScrollLength(), 1, '1 visible node when initialized');
        assert.strictEqual(paged.getReadTail(), root.children[0], 'tail is first deferred when initialized');

        root.appendChildren([
          new NodeType.Group(Guid.generate()),
          new NodeType.Group(Guid.generate()),
          new NodeType.Deferred(Guid.generate())
        ]);

        root.children[0].appendChildren([
          new NodeType.Record(Guid.generate()),
          new NodeType.Deferred(Guid.generate())
        ]);

        root.children[1].appendChildren([
          new NodeType.Record(Guid.generate()),
          new NodeType.Deferred(Guid.generate())
        ]);

        assert.deepEqual(
          paged.read(root, paged.get('lastReadEnd'), 0, 10),
          root.children,
          'read with closed groups should return direct children of root'
        );
        assert.strictEqual(paged.getReadLength(), 3, '3 readable nodes with 2 closed groups and a deferred');
        assert.strictEqual(paged.getScrollLength(), 3, '3 visible nodes with 2 closed groups and a deferred');
        assert.strictEqual(paged.getReadTail(), root.children[2], 'tail of closed groups is next deferred');

        root.children[1].isExpanded = true;

        assert.deepEqual(
          paged.read(root, paged.get('lastReadEnd'), 0, 10),
          [root.children[0], root.children[1], root.children[1].children[0], root.children[1].children[1]],
          'read with second group open'
        );
        assert.strictEqual(paged.getReadLength(), 4, '4 readable nodes');
        assert.strictEqual(paged.getScrollLength(), 5, '5 visible nodes');
        assert.strictEqual(paged.getReadTail(), root.children[1].children[1], 'tail of second expanded group');
      })
    ]),

    Testing.module('pager', [
      Testing.test('insert, getRootOffset', function(assert) {
        var pager = Pager.create();

        pager.appendNodes(pager.get('root'), [
          new NodeType.Record(Guid.generate()),
          new NodeType.Record(Guid.generate())
        ]);

        assert.strictEqual(pager.getReadOffset(pager.get('root').children[1]), 1, 'Second record has offset 1');
      })
    ])
  ]);
});
