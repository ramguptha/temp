define([
  'testing',
  'locale',
  'packages/platform/aggregate',
  'packages/platform/date-type',
  'packages/platform/advanced-filter',

  '../lib/controllers/advanced_filter_controller',
  '../lib/controllers/advanced_filter_attr_picker_controller',
  '../lib/controllers/hierarchy_controller',
  '../lib/controllers/paged_controller',
  '../lib/controllers/tree_controller',
  '../lib/views/tree_view',
  '../lib/views/date_time_picker_view_base',
  '../lib/views/between_date_field_view',

  '../lib/localized_date_time_pickers'
], function(
  Testing,
  Locale,
  Aggregate,
  DateType,
  AdvancedFilter,

  AdvancedFilterController,
  AdvancedFilterPickerController,
  HierachyController,
  PagedController,
  TreeController,
  TreeView,
  DateTimePickerViewBase,
  BetweenDateFieldView,

  LocalizedDateTimePickers
) {
  'use strict';

  var AF = AdvancedFilter;
  var AFC = AdvancedFilterController;

  var filter = AF.AndFilter.create({ params: Em.A([AF.OrFilter.create()]) });
  var advancedFilterController = AFC.create({
    filter: filter,
    dataStoreSpec: Em.Object.create({
      searchableNames: 'stringAttr numberAttr dateAttr boolAttr aggregateOneToManyAttr.child'.w(),
      resourceByName: {
        id: { type: String },
        stringAttr: { type: String },
        numberAttr: { type: Number },
        dateAttr: { type: Date },
        boolAttr: { type: Boolean },
        aggregateOneToManyAttr: { type: Aggregate.OneToMany},
        'aggregateOneToManyAttr.child': { type: String }
      },

      getPresentationForAttr: function (name) {
        var byName = this.get('resourceByName');
        return { name: name, label: name, type: byName[name].type };
      }
    })
  });

  return Testing.package('desktop', [
    Testing.module('advanced_filter_controller', [
      Testing.test('AdvancedFilter => FilterController mapping', function(assert) {

        assert.strictEqual(advancedFilterController.get('filter'), filter, 'Advanced filter is assigned');
        var andFilterController = advancedFilterController.get('filterNodeController');
        assert.ok(
          AFC.AndFilterController.detectInstance(andFilterController),
          'filterNodeController isA AndFilterController'
        );
        assert.strictEqual(andFilterController.get('filter'), filter, 'AndFilterController shares filter with AdvancedFilterController');

        var supportedColumnSpecs = advancedFilterController.get('supportedColumnSpecs');
        assert.strictEqual(supportedColumnSpecs.get('length'), 5, 'Should be 5 supported specs');
      })
    ]),

    Testing.module('localized_time_pickers', [
      Testing.test('mapMomentToPickerFormat', function(assert) {
        assert.strictEqual(
          LocalizedDateTimePickers.mapMomentToPickerFormat(
            'D. MMM YYYY', 
            LocalizedDateTimePickers.momentToDatePickerFormatMap
          ),
          'd. M yy',
          'Date format should map'
        );
      })
    ]),

    Testing.module('date_time_picker_view_base', [
      Testing.test('timezone transform properties', function(assert) {
        var view = DateTimePickerViewBase.extend({
          isShowingUtc: false,

          format: function(valueAsDate) {
            return DateType.isValid(valueAsDate) ? valueAsDate.toISOString() : null;
          },

          parse: function(valueAsString) {
            return 'string' === typeof(valueAsString) ? (new Date(valueAsString)) : null;
          },

          updateValueOnTimeChange: function() {},
          updateTimeOnParsedValueChange: function() {}
        }).create();

        var date = new Date('2014-12-25T20:00:43.092Z');
        var dateInDST = new Date('2014-06-01T20:00:43.092Z');

        Em.A([date, dateInDST]).forEach(function(d) {
          Em.run(function() {
            view.set('valueAsDateInViewTimezone', d);

            var offsetInMilliseconds = d.getTimezoneOffset() * 60 * 1000;
            var dInUtc = new Date(d.getTime() + offsetInMilliseconds);

            assert.strictEqual(dInUtc.getTime(), view.get('valueAsDateInUtc').getTime());

            view.set('valueAsDateInUtc', d);
            var dInView = new Date(d.getTime() - offsetInMilliseconds);
            assert.strictEqual(dInView.getTime(), view.get('valueAsDateInViewTimezone').getTime());
          });
        });
      })
    ]),

    Testing.module('between_date_field_view', [
      Testing.test('timezone transform properties', function(assert) {
        var StubbedView = BetweenDateFieldView.extend({
          isShowingUtc: false,

          _getFromInput: function() {
            var self = this;
            return { val: function() { return self.get('value1'); } };
          },

          _getToInput: function() {
            var self = this;
            return { val: function() { return self.get('value2'); } };
          }
        });

        var view = StubbedView.create();

        var date1 = new Date((new Date('2014-12-25T00:00:00.000Z')).getTime());
        var date2 = new Date('2014-12-27T00:00:00.000Z');
        
        view.set('value1', Locale.date(date1));
        assert.strictEqual(
          view.get('fromDate').getTime(),
          DateType.incrementByUtcOffset(date1).getTime(), 
          'fromDate should be translated from the local timezone'
        );
        assert.strictEqual(view.get('toDate'), null, 'value2 has not been set, so toDate should be unset as well');

        view.set('isShowingUtc', true);
        assert.strictEqual(
          view.get('fromDate').getTime(),
          date1.getTime(),
          'When switching to showing UTC, value1 and fromDate should be the same'
        );

        view.set('value1', Locale.date(date2));
        assert.strictEqual(
          view.get('fromDate').getTime(),
          date2.getTime(),
          'Now that we are showing UTC, sets to value1 should set the same value to fromDate'
        );

        view = StubbedView.create();
        view.set('value2', Locale.date(date2));
        assert.strictEqual(
          view.get('toDate').getTime(),
          DateType.incrementByUtcOffset(date2).getTime(), 
          'toDate should be translated from the local timezone'
        );
      })
    ]),

    //Work in progress. Got blocked by the fact that API is down.
    Testing.module('hierarchy_controller', [
      Testing.test('hierarchy', function(assert) {
        var h = HierachyController.create();
        assert.equal(h.get('count'), null, 'count is null');
      })
    ]),

    Testing.module('paged_controller', [
      Testing.test('showErrorDetail', function(assert) {

        // When showErrorDetail() is invoked, the PagedController is supposed to send "showCommunicationError" to
        // its target, with the context set to the rawPageLoadError, or the first lastLoadError of its enumerated
        // types.

        // Double extend! The ActionHandler Mixin adds behaviour that only applies when further extended.
        var receiver = Em.Object.extend(Em.ActionHandler).extend({
          actions: {
            showCommunicationError: function(details) { this.set('error', details.error); }
          },

          error: null
        }).create();

        var PAGE_LOAD_ERROR = 'page load error';
        var ENUM_LOAD_ERROR = 'enum load error';

        var controller = PagedController.extend({
          dataStore: null,
          rawPageLoadError: PAGE_LOAD_ERROR,
          enumColumnTypes: [Em.Object.create(), Em.Object.create({ lastLoadError: ENUM_LOAD_ERROR })]
        }).create({
          target: receiver
        });

        assert.strictEqual(controller.get('target'), receiver);

        controller.showErrorDetail();
        assert.strictEqual(controller.get('target.error'), PAGE_LOAD_ERROR);

        controller.set('rawPageLoadError', null);

        controller.showErrorDetail();
        assert.strictEqual(controller.get('target.error'), ENUM_LOAD_ERROR);
      })
    ]),

    Testing.module('advanced_filter_picker_controller', [
      Testing.test('selectedAttr', function(assert) {
        var controller = AdvancedFilterPickerController.create({
          content: Em.A([
            {
              name: 'agentStatus',
              label: 'Agent Status',
              type: String
            },
            {
              name: 'broadbandAdapters.adapterVersion',
              label: 'Broadband Adapters Adapter Version',
              type: String
            },
            {
              name: 'broadbandAdapters',
              label: 'Broadband Adapters',
              type: Aggregate.OneToMany
            }])
        });

        var content = controller.get('content');
        var treeController = controller.get('treeController');
        var tree = treeController.get('tree');

        var treeParentNode = tree[1];
        var treeChildNode = tree[1].children[0];

        assert.equal(tree.length, 2);
        assert.equal(content.length, 3);

        assert.equal(treeParentNode.children.length, 1);


        assert.equal(tree[0].id, 'agentStatus');
        assert.equal(tree[0].name, 'Agent Status');
        assert.equal(tree[0].qualifiedName, 'Agent Status');

        assert.equal(treeParentNode.id, 'broadbandAdapters');
        assert.equal(treeParentNode.name, 'Broadband Adapters');
        assert.equal(treeParentNode.qualifiedName, 'Broadband Adapters');
        assert.equal(treeParentNode.isSelectable, false);

        assert.equal(treeChildNode.id, 'broadbandAdapters.adapterVersion');
        assert.equal(treeChildNode.name, 'Adapter Version');
        assert.equal(treeChildNode.qualifiedName, 'Broadband Adapters Adapter Version');
        assert.equal(treeChildNode.parent, 'broadbandAdapters');
      })
    ]),

    Testing.module('tree_controller', [
      Testing.test('select', function(assert) {
        var controller = TreeController.create({ tree: Em.A() });
        var view = TreeView.create({ controller: controller })

        var node1 = { id: '1' };
        var node2 = { id: '2' };

        // Test cases for isMultiSelect: false (default value)
        assert.equal(controller.get('isMultiSelect'), false, 'MultiSelect is false');

        controller.select(null, true, node1);
        assert.equal(controller.get('selectionsList').length, 0, 'id is null');

        controller.select('1', true);
        assert.equal(controller.get('selectionsList').length, 0, 'node is not passed to select');

        controller.select('1', true, node1);
        assert.equal(controller.get('selectionsList').length, 1, 'node1 is passed to select');

        controller.select('1', true, node1);
        assert.equal(controller.get('selectionsList').length, 1, 'reselect the selected node');

        controller.select('2', true, node2);
        assert.equal(controller.get('selectionsList').length, 1, 'select a second node. The first one will be popped out since multiSelect is false');
        assert.equal(controller.get('selectionsList').objectAt(0).id, '2', 'The selected node is the last selected one.');
        assert.equal(controller.get('selectedItemId'), null, 'selectedItemId will be set only when navigation is enabled');

        controller.select('2', false, node2);
        assert.equal(controller.get('selectionsList').length, 0, 'Deselect the node');

        // Test cases for isMultiSelect: true
        controller.clearSelectionsList();
        assert.equal(controller.get('selectionsList').length, 0, 'Cleared the selectionsList');

        controller.set('isMultiSelect', true);
        assert.equal(controller.get('isMultiSelect'), true, 'MultiSelect is true');

        controller.select('1', true, node1);
        assert.equal(controller.get('selectionsList').length, 1, 'node1 is passed to select');

        controller.select('1', true, node1);
        assert.equal(controller.get('selectionsList').length, 1, 'reselect the selected node');

        controller.select('2', true, node2);
        assert.equal(controller.get('selectionsList').length, 2, 'Select a new node');

        controller.select('2', false, node2);
        assert.equal(controller.get('selectionsList').length, 1, 'Deselect one of the nodes');
        assert.equal(controller.get('selectionsList').objectAt(0).id, '1', 'The selected node is the other node.');

        // Test cases for isNavigationEnabled: true
        controller.set('isNavigationEnabled', true);
        assert.equal(controller.get('isNavigationEnabled'), true, 'isNavigationEnabled is true');

        controller.clearSelectionsList();
        assert.equal(controller.get('selectionsList').length, 0, 'Cleared the selectionsList');

        var e = { node: node1 }
        view.onTreeNodeSelected(e);
        assert.equal(controller.get('selectionsList').length, 0, 'Selection when navigation is enabled does not use selectionsList');
        assert.equal(controller.get('selectedItemId'), '1', 'selectedItemId is set when navigation enabled.');
      })
    ])
  ]);
});
