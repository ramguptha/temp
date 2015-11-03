Next Iteration Grids and Lists
==============================

Lists and grids are fundamental building blocks of both Customer Center and Absolute Manage Web Admin. However, we have reached and exceeded the core capabilities offered by our existing libraries that provide these services, and in turn are incurring technical debt as we continue to build out functionality. 

This document describes requirements, design and task breakdown for the next iteration of our grid and list controls.

Contents
--------

- [To be Documented](#tobedocumented)
- [Requirements](#requirements)
- [Analysis](#analysis)
- [Design Considerations](#considerations)
- [Design Overview](#design.overview)
  - [Top Level Classes](#design.toplevelclasses)
  - [Usage](#design.usage)
  - [Class Hierarchy and File Structure](#design.classhierarchy)
- [Design Detail](#design.detailed)
  - [HasIndex and IndexedMutableArray: For Selection Sets](#design.hasindex)
  - [ContextMenuController: For Managing Context Menu Items and Related Events](#design.contextmenucontroller)
  - [PagedData: Data for Rendering](#design.pageddata)
  - [DataPager: For Loading PagedData](#design.datapager)
  - [PagedComponent: Base Class for Components](#design.pagedcomponent)
  - [AbsTableComponent: For Rendering a Paged Table](#design.abstable)
  - [AbsNavComponent: For Rendering a Paged Nav](#design.absnav)
  - [TablePage: For Integrating the Parts of a Page Featuring a Table](#design.tablepage)
  - [AbsListComponent: For Rendering a Paged List (Out of Scope)](#design.abslist)
  - [AbsTreeComponent: For Rendering a Paged Tree (Out of Scope)](#design.abstree)
- [Scope of Work and Estimate](#scope)

<a name="tobedocumented"></a>

To be Documented
----------------

- Clarify copy related to "lazy loading" (should be "loading children from different endpoint")
- Fix expanded status in sample data for PagedData unit test plan
- Note that PagedData.next() conforms to Em.Enumerable interface
- AbsTableComponent
  - show / hide menus
  - click event dispatch
  - rendering detail
  - setup / teardown of DataTables
  - scroll related functionality
  - overlay / editor
- TablePageController/View
- Software titling work plan
- Complete usage example
- Task breakdown
- Estimate

<a name="requirements"></a>

Requirements
------------

### Functional Requirements

- F1: Arbitrary (within reason) levels of grouped content
- F2: Future support of lazy loading of group children
- F3: Aggregate columns
- F4: Inline editing
- F5: Dropdown context menus
- F6: Selection
  - F6.1: Single selection
  - F6.2: Multiple selection
- F7: Refresh while preserving scroll state
- F8: Pause / resume data loading
- F9: Dynamically change table structure / backing data source
- F10: Customized rendering
  - F10.1: Table cells
  - F10.2: Groups
  - F10.3: Headings
  - F10.4: Editor

### Non-functional Requirements

- NF1: Improved performance
- NF2: New object model that balances separation of concerns with simplicity and convenience of integration
- NF3: Must be able to exist alongside legacy grid controllers and views (no "big-bang" merges)

<a name="analysis"></a>

Analysis
--------

### Current Object Model

Platform Controller hierachy is:

- PagedController (paging, search, selection hooks, backed by data store - too much!)
  - ListController (builds out selection - selection is poorly modeled since responsibility shared with base)
    - SingleSelectController (select one)
    - MultiSelectController (select many)
    - ReportController (paged instead of virtual)
  - SummaryListController (for showing summaries instead of detail)
- NavController (proxies a PagedController, some subclasses use paged but there are no paging controls)
- SimpleListController (for list content, unpaged)
- RelatedListController (defunct?)

Platform View hierarchy is:

- PagedView
  - ListView
    - ReportView
    - SingleSelectView
    - MultiSelectView
  - NavView
- SimpleListView
- NavItemView
- RelatedListView (NOT defunct)

### Problems

- Course grained object model, with poor separation of concerns.
- Platform classes do not provide necessary hooks for clean implementation of required functionality.
- Rendering technology ([jqGrid](http://www.trirand.com/blog/)) is problematic:
  - Low quality implementation
  - Very limited capabilites when virtual rendering is used
  - Event model is poorly thought out (e.g. select behaviour)

<a name="considerations"></a>

Design Considerations
---------------------

### Scrolling

Consider a naive list component design: the entire resultset is rendered into an HTML table element. We'll call this "classic scrolling". Classic scrolling is the most natural scrolling for the user - it's just a standard web page with the results inline. Classic scrolling has problems when the resultset is large:

1. Large resultsets correspond to a large DOM, and in turn reduced browser performance when scrolling or reflowing the DOM for display.
2. Large resultsets are expensive to download in their entirety, in terms of computational and memory overhead at the endpoint, and in terms of bandwidth and / or time spent in transmission from the server to the browser.
3. When large resultsets are scrolled, the table headers disappear off of the top of the page.

There are many alternative approaches to scrolling which attempt to solve these problems, of which here are three:

1. Paged scrolling: a limited subset (a "page") of the resultset is rendered, along with controls which allow the user to choose which page to display.
  - Feels clunky.
  - Doesn't integrate well with grouped data, since there is only a single page of context to work with (and a group may be larger than that).
  - Easy for the user to keep track of their progress stepping through the resultset, even across sessions (just remember the page number).
2. Virtual scrolling: given knowledge of the structure of the resultset (groups and counts), render a scrollbar that suggests that the entire resultset is present and in the DOM, but instead just render the results that fall within the viewable area.
  - Most transparent user experience - assuming performance is there, feels like classic scrolling.
  - Determining the structure of the resultset can be as expensive as downloading it in its entirety.
  - The rendering aspects of this technique (where only the "visible" parts of the grid are in the DOM) can be used with other scrolling methods, and in fact we will use this technique with infinite scrolling.
3. Infinite scrolling: a page of the resultset is rendered along with some extra padding at the bottom if there are more records available. When the user scrolls to the bottom of the rendered content, more content is downloaded and appended to the end, and so on until all of the resultset is displayed.
  - Can be combined with virtual scrolling rendering techniques to ensure that the DOM does not become too large.
  - Poor / complicated interaction with the browser forward / backward button. It's annoying to be put back at the "beginning" of the resultset on history transition, but regaining the previous scroll position involves downloading all of the pages leading up to it again, which may take a long time from the user's perception.

We have decided on infinite scrolling, as the best tradeoff between scalability with the size of the resultset and pleasant user experience.

### Lazy Loading of Group Children

First, a little more detail about this requirement (F2). It has been decided that the future UX for grouped data will show groups initially in a "closed" state (where the group headers, but not their children are shown), then it will be up to the user to explicitely "open" groups of interest, showing their children. This will allow for endpoints to be separated for groups and their children, should that be desired.

Second, groups may be arbitrarily nested (F1). 

This will require the structure of the new grid heirarchy to differ significantly from that of the existing one because currently, the software is architected with a one-to-one correspondence between query criteria, grid views, and grid controllers.

Questions / Answers:

- Q: How will the user specify query criteria that covers multiple resources? What will the Advanced Filter Editor look like?
  - A: Yet to be determined, but one possiblity is for the advanced filter editor to show query criteria by data type.
    
    Before:

        +-----------------------+
        | Filter 1              |
        |    OR                 |
        | Filter 2              |
        +-----------------------+

          AND

        +-----------------------+
        | Filter 1              |
        |    OR                 |
        | Filter 2              |
        +-----------------------+

    After:

        +-----------------------+
        | Group Filter 1        |
        |    OR                 |
        | Group Filter 2        |
        +-----------------------+

          AND

        +-----------------------+
        | Child Filter 1        |
        |    OR                 |
        | Child Filter 2        |
        +-----------------------+

This design accomodates F2 by delegating such work to the Pager class and future work on the AdvancedFilterController / AdvancedFilterEditorView.

### Choice of Grid Rendering Technology

The following technologies were evaluated as potential replacements for jqGrid.

- [Addepar Tables](http://addepar.github.io/ember-table/#/ember-table/overview)
  - Native Ember table.
  - Did not appear to have built-in hooks for expiring loaded pages.
  - Scrolling implementation relies on antiscroll, which is a problem:
    - Antiscroll uses a custom rendered scrollbar instead of the browser native one, and the custom rendered scrollbar is designed to emulate that in OS X Lion:
      - Obviously not native on other platforms.
      - Only renders when content is hovered!
    - Antiscroll has never had a formal release, so version tracking is difficult.
    - Development of Antiscroll appears to have stopped.
- [Sencha Tables](http://dev.sencha.com/extjs/5.0.0/examples/grid/buffer-grid.html)
  - Sencha is a whole platform, with its own ramp-up and whose idioms may or not match those used in our own code.
  - [Commercially licensed.](http://www.sencha.com/products/extjs/licensing/)
  - This option was not deeply investigated.
- A custom table rendering implementation.
  - Maximum flexibility.
  - The most work to build, but that may possibly be saved later in the development cycle. But, we need to save time Right Now.
- [DataTables](http://dev.sencha.com/extjs/5.0.0/examples/grid/buffer-grid.html)
  - [MIT License](http://datatables.net/license/mit)
  - [Code quality](https://github.com/DataTables/DataTables) appears excellent. Easy to read, good object model.
  - Developers revenue model is to charge for custom work on the code, so we can expect good support if required.
  - Based on the promising attributes of this component, a proof of concept was implemented which showed that it could support grouping and virtual rendering embedded in Customer Center.

### Grouped Data and Tree Data are Structured Similarly

Grouped data is a tree structure, but currently we have separate controllers / views for trees and lists. While it's clear that tree and list data should be rendered differently, since they share a common structure, they should have share some amount of code.

### The Ember 2.0 Roadmap

The [Ember 2.0 Roadmap](https://github.com/emberjs/rfcs/pull/15) introduces some radical changes to the API. Among them:

- Deprecation of the Model/Controller/View/Template class structure in favour of Model/Component/Template. 
- "Bindings up, actions down" data flow, rather than bidirectional bindings.

In light of these trends, the new grid will be a [Component](http://emberjs.com/guides/components/) instead of a Controller/View.

<a name="design.overview"></a>

Design Overview
---------------

<a name="design.toplevelclasses"></a>

### Top Level Classes

The new grid component will be implemented alongside the existing controllers and views, so that the existing code-base may be ported gradually. Since the new grid is a component, we don't have to worry about name collisions.

To facilitate the usage of third party components, all of our components will start with "Abs". Thus:

New component classes:

- AbsTableComponent
  - The new table component.
- AbsNavComponent
  - The new nav component.
- AbsTreeComponent
  - (Out of scope) The new tree component.
- AbsListComponent
  - (Out of scope) The new list component.

Our legacy grid template contains a full "page" of content, including headings, a textbox for entering ad-hoc searches, and an advanced filter editor. The new grid template will not do so - it will just contain the grid. The surrounding content will be moved a new controller / template.

New "page" controller / template:

- TablePageController

<a name="design.usage"></a>

### Usage

Here is a sample invocation of AbsTableComponent, rendering a table of devices:

    {{abs-table
      columns=deviceColumnSpecs
      pager=deviceData
      query=query
      selections=selectedDevices
      menus=getDeviceActions
      scrollPosition=deviceScrollPosition
      paused=pauseDeviceLoading

      navigate="gotoDevice"
      scroll="storeScrollPosition"
    }}

Here's what each parameter means, in the the context of the example:

- _columns isA Array of AbsTableComponent.ColumnConfig_
  - Describes the table structure, including the label for the heading, optional templates for the heading and cells within the column, grouping and freezing.
- _pager isA PagedComponent.DataSourcedPager_
  - The paged data source.
- _sort isA Array of plain javascript object_
  - In the same format as SearchableQuery.sort.
- _selections isA IndexedMutableArray of PagedData.Node_
  - The list of selected nodes. In this case, the list of selected devices.
- _menus isA ContextMenuController_
- _navigate isA action name_
  - Standard Ember component action name mapping. In this case, map AbsTable's "navigate" action to the invokers "gotoDevice" action.
- _scroll isA action name_
  - Standard Ember component action name mapping. In this case, map AbsTable's "scroll" action to the invokers "storeScrollPosition" action.

<a name="design.classhierarchy"></a>

### Class Hierarchy and File Structure

The _TablePageController_ and _TablePageView_ will replace _ListController_ and _ListView_. Unlike _ListController/View_, _TablePageController/View_ will not render the tables - that will be deferred to _AbsTableComponent_.

_PagedComponent_ is the base class for all components which will render paged data, including _AbsTableComponent_. _PagedComponent_ uses a _DataPager_ to manage loaded data, which is in turn stored in a _PagedData_ instance. Selection sets are managed via an _IndexedMutableArray_.

_AbsTableComponent_ accepts an array of _AbsTableComponent.ColumnConfig_ - these are used to describe the layout of the table. It uses DataTables to render the content.

New files:

    packages/paged/
      main.js
      lib/
        components/
          paged_component.js
        paged_data.js
        data_pager.js
      test/
        test_main.js

    packages/queried-pager
      main.js
      lib/
        queried_pager.js
      test/
        test_main.js

    packages/data-sourced-pager
      main.js
      lib/
        data_sourced_pager.js
      test/
        test_main.js

    packages/table/
      main.js
      lib/
        components/
          abs_table_component.js
        templates/
          abs_table.handlebars
      test/
        test_main.js

    packages/nav/
      main.js
      lib/
        components/
          abs_nav_component.js
        templates/
          abs_nav.handlebars
      test/
        test_main.js

    packages/platform/enum-util/lib/
      has_index.js
      indexed_mutable_array.js

    packages/platform/desktop/lib/
      controllers/
        list_page_controller.js
      templates/
        list_page.handlebars

----------------------------------------------------------------------------------------------------

<a name="design.detailed"></a>

<a name="design.hasindex"></a>

HasIndex and IndexedMutableArray: For Selection Sets
----------------------------------------------------

IndexedMutableArrays are used to pass selection sets between the various parts of an application. IndexedMutableArrays are just Em.MutableArray + the HasIndex mixin. They allow the various components to efficiently iterate through all selected objects, and also to efficiently test wether a particular object is selected.

### HasIndex isA Em.Mixin

A common pattern in our codebase is to have a pair of linked properties, one of which is an array, and another that is a plain javascript object for fast membership testing:

    foo: function() { return Em.A(); }.property(),

    fooById: function() {
      return this.get('foo').reduce(function(elt, obj) {
        obj[elt.get('id')] = elt;
        return obj;
      }, {});
    }.property('foo.[]')

HasIndex is a mixin encapsulates that pattern for proper re-use. It is applicable to any class that extends Em.Array. This mixin will be in the enum-util package.

Properties:

- _byId isA plain javascript object_
  - Computed property analogous to _fooById_ in the above example.
- _idMapperForIndex isA String OR function(elt)_
  - The recipe for getting a unique identifier (which must be a String) from an element in the array. If a String, that property is "gotten". If a function, that function is invoked with the element, and is expected to return the identifier.
  - Default value is "id".

Methods:

- _contains(obj)_
  - Overrides the default implementation to use the _byId_ property.

#### Test Cases

- _byId_ works when _idMapperForIndex_ isA String OR function.
- _byId_ is properly regenerated when array membership changes.
- With 100000 objects in the array, _contains()_ works faster than the Em.MutableArray over 10 runs.

### IndexedMutableArray isA Em.MutableArray with HasIndex

A convenience class that captures the common use case of Em.MutableArray + HasIndex.

#### Test Cases

- Confirm that Em.MutableArray does not define properties named "byId" or "idMapperForIndex".

#### Log

- Replaced this entire component with a Set component that runs against an array after unit tests showed that a plain array outperformed this "faster" design.

----------------------------------------------------------------------------------------------------

<a name="design.contextmenucontroller"></a>

ContextMenuController: For Managing Context Menu Items and Related Events
-------------------------------------------------------------------------

ContextMenuControllers are used to drive context menu behaviour. Provides a list menu items in "ButtonGroupController" format via _getMenuItems()_, and accepts _contextMenuAction_, de-marshalling it into an application action which is dispatched to _target_.

### ContextMenuController isA Em.Controller with UI.MenuController.HasOneMenu

Properties:

- _target isA Em.ActionHandler_
  - _contextMenuAction_ is de-marshalled into an application action, and dispatched to this object.
- _showingMenu isA Boolean_
  - Via UI.MenuController.HasOneMenu.

Menu-related Methods:

- _getMenuItems(obj)_
  - Returns an array of

        {
          labelResource: "path to label",
          tooltipResource: "path to tooltip",
          actionName: "name of action",
          context: obj
        }

Actions:

- _contextMenuAction(name, obj)_
  - Sends _name_ to _target_ with _context_ set to _obj_.

----------------------------------------------------------------------------------------------------

<a name="design.pageddata"></a>

PagedData: Data for Rendering
-----------------------------

The various components and helper classes all produce and consume a double-linked tree structure which is used to control what is rendered. For performance reasons, the structure is composed of plain javascript objects rather than Ember Objects.

PagedData describes both the known and deferred parts of the data for rendering. This class lives in the Paged package.

### PagedData.NodeId isA Guid or String

All nodes in PagedData are uniquely identified by PagedData.NodeId. This isn't a real type - we just use the name in the design to denote data that conforms to these requirements. A Guid is a specially formatted string. A PagedData.NodeId is expected to consistently identify a given PagedData.Node across data loads, within the scope of the related PagedData, and the lifetime of the App. It is ok for the PagedData.NodeId for a PagedData.Node to change if the web page is reloaded.

### PagedData.Node isA plain javascript object

All nodes in PagedData share these properties.

Properties:

- _id isA PagedData.NodeId_
  - Uniquely identifies the node.
- _parentNode isA PagedData.Node OR null_
  - Refers to the parent node in the tree.
- _offset isA Number &gt;= 0 OR null_
  - Every child node knows its offset into its parents _children_ array. The root node has no parent.
- _children isA Array of PagedData.Node or null_
  - The child nodes, or null if a leaf.

### PagedData.Root isA PagedData.Node

PagedData has a PagedData.Root instance as the root of the tree.

Properties:

- _isRoot isA Boolean_
  - Marker, always true.
- _isExpanded isA Boolean_
  - Always true, the Root node is always expanded.

### PagedData.Group isA PagedData.Node

PagedData supports arbitrary levels of grouping. Each group is represented by a PagedData.Group instance in the tree.

Constructor:

- _Group(nodeData)_
  - Sets the _nodeData_ property.

Properties:

- _isGroup isA Boolean_
  - Marker, always true.
- _isExpanded_
  - Describes the open/closed rendering state of the group. If false, _children_ will be skipped by _read()_.
- _nodeData_
  - Used to render the group. Structure is arbitrary, and delegated to the owner of the related PagedData instance.

### PagedData.Deferred isA PagedData.Node

Marks the end of a partially loaded (or completely unloaded) _children_ array. When a resultset has been partially downloaded, a PagedData.Deferred instance is appended to the end of the array to mark where more content will be appended.

Properties:

- _isDeferred isA Boolean_
  - Marker, always true.
- _isLoading isA Boolean_
  - If true, the children are in the process of being loaded, and the view should render accordingly.
- _nodeData_
  - Used to render the node. Structure is arbitrary, and delegated to the owner of the related PagedData instance, but the expectation is that it hold either nothing, or the last error encountered while loading it.
- _children isA null_
  - PagedData.Deferred have no children, because they aren't loaded yet.

### PagedData.Record isA PagedData.Node

Describes a row of data for rendering.

Constructor:

- _Record(nodeData)_
  - Sets the _nodeData_ property.

Properties:

- _isRecord isA Boolean_
  - Marker, always true.
- _nodeData_
  - Used to render the row. Structure is arbitrary, and delegated to the Pager / Controller / View.
- _children isA null_
  - PagedData.Record has no children.

### PagedData isA Em.Object with Em.Enumerable

Properties:

- _root isA Root_
- _lastReadEnd isA plain javascript object_
  - Caches the offset at the end of the last read, to speed up seeks in some cases.
  - Object has the following fields:
    - _node isA PagedData.Node_
    - _offset isA Number &gt;= 0_

Em.Enumerable Properties:

- _length isA Number_
  - Via _getSize()_.

Node Traversal Methods:

- _walk(start isA PagedData.Node, visitor(node isA PagedData.Node))_
  - Walks the PagedData heirarchy in depth-last order. Stops when _visitor_ returns a truthy value.
- _next(root isA PagedData.RootNode, currentNode isA PagedData.Node)_
  - Returns the node after _currentNode_ in depth-last order, or null if there are no more.
- _prev(root isA PagedData.RootNode, currentNode isA PagedData.Node or null)_
  - Returns the node before _currentNode_ in depth-last order, or null if none come before it.

Expanded Node Traversal Methods:

- _walkExpanded(start isA PagedData.Node, visitor(node isA PagedData.Node))_
  - Walks the PagedData heirarchy in depth-last order. Stops when _visitor_ returns a truthy value. Skips children of nodes where _isExpanded_ is not _true_.
- _nextExpanded(root isA PagedData.RootNode, currentNode isA PagedData.Node)_
  - Returns the node after _currentNode_ in depth-last order, or null if there are no more. Skips children of nodes where _isExpanded_ is not _true_.
- _prevExpanded(root isA PagedData.RootNode, currentNode isA PagedData.Node or null)_
  - Returns the node before _currentNode_ in depth-last order, or null if none come before it. Skips children of nodes where _isExpanded_ is not _true_.

Read / Write Methods:

- _lookup(id isA PagedData.NodeId)_
  - Given a PagedData.NodeId, returns the related PagedData.Node or null if unavailable.
- _read(root isA PagedData.RootNode, lastReadEnd isA PagedData.Node, offset isA Number, count isA Number)_
  - Walks the PagedData heirarchy in depth-last order, returning an array of _count_ nodes (or however many are available), starting at _offset_. Updates _lastReadEnd_ with the _node_ and _offset_ that follow the last one returned, or Root / 0 if there are no more.
  - Children of nodes are skipped unless _isExpanded_ is true.
- _replace(root isA PagedData.RootNode, oldNodeParent isA PagedData.Node, oldNodeOffset isA Number, newNodes isA Array of PagedData.Node)_
  - Replaces _oldNode_ with _newNodes_ in the PagedData heirarchy, setting the properties of _newNodes_ appropriately.

Node Context Query Methods:

- _getOffset(node isA PagedData.Node)_
  - Given a PagedData.Node, calculate its total offset from the RootNode via depth-last walk. This is used to determine query offsets for data that has been grouped on the client side.
- _getRecordOffset(node isA PagedData.Node)_
  - Given a PagedData.Node, return the number of PagedData.Record nodes that precede it.
- _getLoadedStatusOfPredecessors(node isA PagedData.Node)_
  - Given a PagedData.Node, return true if no PagedData.Deferred nodes are visited by walking backwards from _node_ to the RootNode. In other words, return true if the tree is completely loaded between RootNode and _node_.

Other Methods:

- _isValid(start isA PagedData.Node)_
  - Performs a depth-walk, returning true if:
    - All nodes have properties that are set properly.
    - All node _id_s are unique.
    - Deferred nodes only appear at the end of _children_ arrays.
    - No nodes other than _start_ are a RootNode.
- _getSize()_
  - Returns the number of PagedData.Nodes.

Em.Enumerable Methods:

- _nextObject(index, previousObject, context)_
  - Via _next()_.

#### Test Cases (tests cover PagedData and related Node classes)

- Given a tree of depth 3 (from root), 3 nodes per branch, _nodeData_ numbered successively:
  - structure:

        - RootNode(expanded)
          - 1(expanded)
            - 2(expanded)
              - 3
              - 4
              - 5
            - 6(expanded)
              - 7
              - 8
              - 9
            - 10(expanded)
              - 11
              - 12
              - 13
          - 14(expanded)
            - 15(expanded)
              - 16
              - 17
              - 18
            - 19(expanded)
              - 20
              - 21
              - 22
            - 23(expanded)
              - 24
              - 25
              - 26
          - 27(expanded)
            - 28(expanded)
              - 29
              - 30
              - 31
            - 32(expanded)
              - 33
              - 34
              - 35
            - 36(expanded)
              - 37
              - 38
              - 39

  - Expanded nodes, in order:

        1, 2, 6, 10, 14, 15, 19, 23, 27, 28, 32, 36

  - _walk()_ should return nodes with _nodeData_ in ascending order.
  - Successive calls to _next()_, from _root_ to the end of the tree should return nodes with _nodeData_ in ascending order.
  - many calls to _prev()_ should return nodes in reverse order from many calls to _next()_.
  - _walkExpanded()_ should return nodes in order of the expanded nodes.
  - Successive calls to _nextExpanded()_, from _root_ to the end of the tree should return nodes in order of the expanded nodes.
  - many calls to _prevExpanded()_ should return nodes in reverse order from the expanded nodes.
  - _read()_ with 0 offset, 7 count should return nodes 1, 2, 6, 10, 15, 14, 19
  - _read()_ with 2 offset, 7 count should return nodes 6, 10, 14, 15, 19, 23, 27
  - _read()_ with 10 offset, 3 count should return nodes 32, 36
  - _read()_ with 28 offset, 1 count should return no nodes.
  - _isValid(RootNode)_ returns true.
  - _isValid(RootNode.children[0])_ returns true.
  - _isValid(RootNode.children[0].children[0])_ returns true.
- _lookup()_ and _getOffset()_ with:
  - root node id
  - a branch node id
  - a leaf node id
- _root_ points to a tree of RootNode => [Deferred] on creation. Nodes are well structured (i.e. _parentNode_, _id_, _children_ and _offset_ are set correctly)
  - _replace(root, root, 0, [Array of 2 Group => [Deferred] and 1 Deferred])_ results in well structured tree.
  - _replace(root, root.children[1], 0, [Array of 2 Record])_ results in well structured tree.
  - _replace(root, root, 2, [Array of 1 Group => [Deferred]])_ results in a well structured tree.
- _isValid()_ with RootNode => RootNode returns false.
- _isValid()_ with RootNode => [Deferred, Record] returns false.
- _isValid()_ with RootNode => [Deferred], both Nodes with the same _id_ returns false.
- _isValid()_ with RootNode => [Deferred], Deferred with _parentNode_ unset returns false.
- _isValid()_ with RootNode => [Record, Deferred], Deferred with _offset_ 0 returns false.
- _isValid()_ with RootNode => [Record, Deferred], Deferred with _offset_ null returns false.

#### Proof of concept code (with a probably unnecessary non-recursive walk implementation)

    read: function(start, count) {
      // When reading, we first return a group, then we return its children (if expanded), then we return the next
      // group.
      var resultSet = [];
      var groupedRecordsRoot = this.get('groupedRecordsRoot');

      if (!Em.isNone(groupedRecordsRoot)) {
        var currentObj = this.next(groupedRecordsRoot, groupedRecordsRoot);

        for (var seekRemaining = start; seekRemaining > 0; seekRemaining--) {
          currentObj = this.next(groupedRecordsRoot, currentObj);
        }

        for (var readRemaining = count; readRemaining >= 0 && currentObj !== this.get('END'); readRemaining--) {
          resultSet.push(currentObj);
          currentObj = this.next(groupedRecordsRoot, currentObj)
        }
      }

      return resultSet;
    },

    next: function(groupedRecordsRoot, currentObj) {
      // First child, if available
      var firstChild = currentObj.children && currentObj.children[0];
      if (firstChild) {
        return firstChild;
      }

      // Else find the next available sibling of currentObj or a parent thereof
      while (currentObj) {
        // Next sibling, if available
        var parentObj = currentObj.parentGroup;
        var nextSibling = parentObj.children[currentObj.offset + 1];
        if (nextSibling) {
          return nextSibling;
        }

        // Else try again with the parent
        currentObj = parentObj;
      }

      // Nothing! We're done.
      return this.get('END');
    },

    prev: function(groupedRecordsRoot, currentObj) {
      // Deepest child of previous sibling, if available

      // Previous sibling

      // Parent

      // Else throw
    }

### Sample PagedData Heirarchy - Initial State

Upon construction, a PagedData heirarchy is always initialized as follows:

- PagedData.Root
  - PagedData.Deferred

### Sample PagedData Heirarchy - Ungrouped Data

#### First Page Loaded

On load of the first page, the top level PagedData.Deferred is replaced with an array of PagedData.Record, and finally a single PagedData.Deferred to represent content left to load.

- PagedData.Root
  - PagedData.Record(page0.0)
  - ...
  - PagedData.Record(page0.N)
  - PagedData.Deferred

#### Second Page Loaded

When the second page of data is loaded, the PagedData.Records are appended to the end of PagedData.Roots _children_ array, and PagedData.Deferred moved to the end.

- PagedData.Root
  - PagedData.Record(page0.0)
  - ...
  - PagedData.Record(page0.N)
  - PagedData.Record(page1.0)
  - ...
  - PagedData.Record(page1.N)
  - PagedData.Deferred

#### All Pages Loaded

Once all pages are loaded, the PagedData.Deferred is removed from the end.

- PagedData.Root
  - PagedData.Record(page0.0)
  - ...
  - PagedData.Record(page0.N)
  - PagedData.Record(page1.0)
  - ...
  - PagedData.Record(page1.N)
  - ...
  - PagedData.Record(pageM.0)
  - ...
  - PagedData.Record(pageM.N)

### Sample PagedData Heirarchy - Grouped Data with Lazy Loaded Children

#### First Page of Groups Loaded

- PagedData.Root
  - PagedData.Group(page0.0)
    - PagedData.Deferred
  - ...
  - PagedData.Group(page0.N)
    - PagedData.Deferred
  - PagedData.Deferred

#### First Page of Children of First Group Loaded

Once parent groups are available, child records may be loaded at any time. If the UX is "open by default", then the children would be loaded immediately. Otherwise ("closed by default"), the children would be loaded when the group is expanded.

- PagedData.Root
  - PagedData.Group(page0.0)
    - PagedData.Record(page0.page0.0)
    - ...
    - PagedData.Record(page0.page0.N)
    - PagedData.Deferred
  - ...
  - PagedData.Group(page0.N)
    - PagedData.Deferred
  - PagedData.Deferred

### Log

- PagedData.walkExpanded() => PagedData.walkVisible()
- PagedData.read() will stop at the first Deferred.
- Separate notions of "readable" nodes from "scrollable" nodes. The idea is that reading must stop at the first deferred node that is encountered, but the scrollbar can be sized using all of the visible, loaded nodes.

----------------------------------------------------------------------------------------------------

<a name="design.datapager"></a>

DataPager: For Loading PagedData
--------------------------------

### DataPager isA Em.Object

Abstracts the loading of asynchronously loaded, paged data. This class, and all platform sub-classes, lives in the Paged package. This is an abstract class.

The PagedData lives in the _data_ property. Consumers are expected _not_ to bind it or mutate it in any way other than via DataPager interfaces.

Core Properties:

- _data isA PagedData_
  - Private to DataPager and sub-classes.

#### Reading Data

All reads are implemented via facade Methods for the corresponding methods on PagedData, via the _data_ property:

- _lookup()_
- _read()_

#### Observing Changes to Data

DataPager updates various timestamps when the contents of _data_ changes. Consumers are expected to observe them and react accordingly.

Observable Change-notification Properties:

- _pageContentChangedAt isA Date_
  - Timestamp set to present moment when the data associated with loaded PagedData.Nodes has changed, but the structure of the loaded data has not.
- _pageStructureGrewAt isA Date_
  - Timestamp set to to present moment when the page structure has grown.
- _pageStructureResetAt isA Date_
  - Timestamp set to the present moment when the page structure has completely reset.
- _pageStructureChangedAt isA Date_
  - Timestamp set to the present moment whenever the page structure has changed in a way that meets no other criteria (i.e. it didn't grow and it wasn't reset).

#### Tracking the Status of Asynchronous Requests

Loading progress is stored in the _nodeData_ of PagedData.Deferred nodes being loaded, and _pageContentChangedAt_ is updated when it changes (so that consumers can re-render if necessary).

The structure is as follows:

    {
      firstQueuedAt: timestamp the node was first queued at,
      lastQueuedAt: timestamp the node was last queued at,
      queuedCount: total number of times this node has been queued to load,

      successCallback: callback to invoke on successful load,
      errorCallback: callback to invoke on failed load,
      context: context provided with the load request,

      invokedAt: timestamp the node was last invoked at,
      loadedAt: timestamp the node was successfully loaded at (this timestamp is set for both synchronous and asynchronous loads),
      failedAt: timestamp the node last failed to load at
    }

Load Status Methods:

- _getLoadRequest(node isA PagedData.Deferred)_
- _markLoadRequestAsQueued(node isA PagedData.Deferred, context, successCallback, errorCallback)_
- _markLoadRequestAsInvoked(node isA PagedData.Deferred)_
- _markLoadRequestAsLoaded(node isA PagedData.Deferred)_
- _markLoadRequestAsFailed(node isA PagedData.Deferred)_

#### Loading Data Synchronously

To load data into the DataPager synchronously, invoke _loadData()_. 

Synchronous Load Methods:

- _loadData(startNode isA PagedData.Deferred, count isA Number, context, data, hasMore isA Boolean)_
  - Invoked from _load()_, but also suitable for invoking directly.
  - Synchronously load _data_ into _this.data_.
  - _count_ is only a guideline. There is no hard expectation that at most _count_ nodes will be loaded. For example, additional group nodes could be created in addition to _count_.
  - Performs the following operations:
    - Creates an Array of PagedData.Node via _createNodesFromData()_.
    - Walks that array, appending PagedData.Records and PagedData.Groups via PagedData.replace(), minding group boundaries via _nodeMatchesGroup()_ and _maybeCreateGroupForNode()_.
    - If _hasMore_ is true, appends additional PagedData.Deferred instances to each PagedData.Group or PagedData.Root that wasn't terminated.
    - Updates _pageStructureGrewAt_.
    - Updates the loading status via _markLoadRequestAsLoaded()_

#### Mapping Raw Data to PagedData.Nodes

_loadData_ uses _createNodesFromData()_ to transform raw data into an array of Paged.Records. Those records are in turn passed to _maybeCreateGroupForNode()_ and _nodeMatchesGroup()_ to perform client-side grouping.

Data to PagedData.Node Transform Methods:

- _createNodesFromData(startNode isA PagedData.Deferred, count isA Number, context, data)_
  - Map _data_ to an Array of PagedData.Node.
  - Meant to be overridden by sub-classes; default implementation expects _data_ to be Em.Enumerable, each element becomes the _nodeData_ of a corresponding PagedData.Record.
- _maybeCreateGroupForNode(parent isA PagedData.Group OR PagedData.Root, node isA PagedData.Node)_
  - Invoked during data load to test if a new group should be emitted for the given node.
  - Returns a new PagedData.Group if a new group is required for the node, or null otherwise.
  - If a new group _G_ is returned, this method is expected to be invoked again with the same _node_ and _G_ passed as the _parent_ parameter. In this way, a loading node can trigger the creation of multiple levels of nested groups.
  - Meant to be overridden by sub-classes; default implementation returns null.
- _nodeMatchesGroup(parent isA PagedData.Group, node isA PagedData.Record OR PagedData.Group)_
  - Invoked during data load to test if _node_ matches the potential parent _parent_.
  - Returns true if _node_ should be appended as a child to _parent_, false otherwise.
  - If false is returned and _parent_ has a parent of type PagedData.Group, this method is expected to be invoked with _parent.parentNode_ and _node_. In this way, a loading node can terminate multiple levels of nested groups.
  - Meant to be overridden by sub-classes; default implementation returns false.

#### Loading Data Asynchronously

To load data into a DataPager asynchronously, invoke _load()_ with a PagedData.Deferred, an optional context and callbacks for success and failure. _load()_ will update the node's related _nodeData_ with the details of the request, and then add it to _loadRequests_, a queue. If the provided PagedData.Deferred is already in _loadRequests_ or in progress via _loadInProgress_, then _load()_ will update that request instead.

Asynchronous Load Entry Methods:

- _load(startNode isA PagedData.Deferred, context, successCallback(context, data), errorCallback(context, detail))_
  - Standard async-style read interface. _startNode_ will be replaced by the newly loaded PagedData.Nodes.
  - _context_ is left to the caller to set. It is passed to _successCallback()_, _errorCallback()_, _get()_ and _loadData()_ unmodified.
  - Only a single _load()_ invocation for a particular node be "in flight" at a time. If _load()_ is invoked again on the same node before a previous invocation has completed, the _successCallback()_, _errorCallback()_, and _context_ from the previous invocation will be overwritten by the new ones.
  - Defers the work of acquiring the raw data to _getNodes()_.

Asynchronous Loading Properties:

- _loadRequests isA Em.MutableArray of PagedData.Deferred_
- _loadInProgress isA PagedData.Deferred_
  - Stores load request that is currently in progress.

_nextLoadRequest()_ is responsible for invoking the next load request, if none is already in progress. It is invoked when a new request is queued in _loadRequests_ (but not by observer, in order to prevent observer loops), when the paused status of loading changes, and when a load completes. _nextLoadRequest()_ will do nothing if loading is paused, or if _loadInProgress_ already stores a load in progress. Otherwise, _nextLoadRequest()_ will take a PagedData.Deferred from the head from _loadRequests_, make sure that it is still in the _data_ tree (it may have been overwritten by a previous load), and invoke it with _invokeLoadRequest()_.

_invokeLoadRequest()_ wraps the successCallback and errorCallback() of the request in its own handlers, calls _getNodes()_ with the request details (and wrapped callbacks), and sets _loadInProgress_ to the node. The wrappers for the handlers capture the load request, and in turn check the value of _loadInProgress_ against it before invoking the wrapped handlers. In this way, a cancelled request will be dropped.

To cancel all pending loads, invoke _cancelLoad()_.

Asynchronous Loading Queue Management Methods:

- _nextLoadRequest()_
- _invokeLoadRequest(request is a plain javascript object)_
- _cancelAllLoadRequests()_
  - Clears _loadRequests_ and _loadInProgress_. Any requests already in progress will not have their handlers invoked.

Asynchronous Load Methods:

- _getNodes(startNode isA PagedData.Deferred, count isA Number, context, successCallback(context, data), errorCallback(context, detail))_
  - Invoked from _load()_ to retrieve the requested data. On success, _get_ is expected to invoke _successCallback(context, data)_, and on failure _get_ is expected to invoke _errorCallback(context, data)_.
  - _count_ is set to _pageSize_. 
  - _count_ is only a guideline. There is no hard expectation that at most _count_ nodes will be loaded.
  - Default implementation throws "Implement me".

#### Pausing and Resuming Data Loading

Data loading can be paused and resumed by setting and clearing _paused_.

Bindable Load-related Properties (triggers):

- _paused isA Boolean_
  - Stops data loading when true.

Internal Load-related Methods (trigger observers):

- _pausedDidChange()_
  - Observes _paused_.
  - If cleared, invokes _nextLoadRequest()_.

#### Other Data Mutations

Other Mutation Methods:

- _reset()_
  - Replaces the structure of _data_ with Root => Deferred.
  - Clears _total_ and _countError_.
  - Clears all requests in progess via _cancelAllRequests()_
  - Updates _pageStructureResetAt_.
- _update(PagedData.Node node, nodeData)_
  - Updates the _nodeData_ property of _node_
  - Updates _pageContentChangedAt_.
- _replace(root isA PagedData.RootNode, oldNodeParent isA PagedData.Node, oldNodeOffset isA Number, newNodes isA Array of PagedData.Node)_
  - Invokes _data.replace()_.
  - Updates _pageStructureChangedAt_.

Properties:

- _pageSize isA Number &gt;= 0_
- _loadedCount isA Number &gt;= 0_
  - A volatile computed property that returns the number of PagedData.Record and PagedData.Group nodes in the tree.
  - Implemented via _data.walk()_
- _loadedRecordCount isA Number &gt;= 0_
  - A volatile computed property that returns the number of PagedData.Record nodes in the tree.
  - Implemented via _data.walk()_

#### Counting All Data (Including Unloaded Data)

Counting is done asynchronously, in the same manner as _load()_, except that instead of having a queue of counts, there is only _countInProgress_, which is immediately written to when _count()_ is invoked.

Count Properties:

- _total isA Number or null_
- _countInProgress isA plain javascript object_
  - When _count()_ is invoked, _countInProgress_ is set to { start: new Date(), success: successCallback, error: errorCallback }. Wrapper functions that capture the value of _countInProgress_ are passed to _getCount()_. They check the value of _countInProgress_ before calling the real _successCallback()_ or _errorCallback()_. If the value of _countInProgress_ matches, the relevant callback is invoked, and _countInProgress_ is cleared. Otherwise nothing is done.
  - Consumers may use the "set" status of this property for feedback purposes, but the structure of the content that the property stores should be considered to be private.
- _countError_
  - If the errorCallback() is invoked from _count()_, then the _detail_ parameter is stored here.

Count Methods:

- _count(context, successCallback(context, count), errorCallback(context, detail))_
  - Standard async-style count interface. Separate from read for optimization purposes.
  - Clears _countError_ and _total_.
  - Default implementation throws "Implement me".
  - _context_ is left to the caller to set. It is passed to the _successCallback()_ and _errorCallback()_ unmodified.
  - Only a single _count()_ invocation may be "in flight" at a time. If _count()_ is invoked again before a previous invocation has completed, the _successCallback()_ and _errorCallback()_ from the previous invocation will do nothing.
  - When invoked, _countInProgress_ is set as per the description of that property.
- _getCount(context, successCallback(context, total isA Number), errorCallback(context, detail))_
  - Invoked from _count()_ to retrieve the requested data. On success, _get_ is expected to invoke _successCallback(context, data)_, and on failure _get_ is expected to invoke _errorCallback(context, data)_.
  - Default implementation throws "Implement me".

#### Test Cases

- Given a DataPager with nothing loaded yet into its PagedData:
  - Validate initial state:

        - RootNode
          - Deferred

  - _loadedCount_ should return 0.
  - Invoke _loadData(this.data.children[0], 3, null, [0, 1, 2, 3], true)_
  - Validate new state:

        - RootNode
          - 0
          - 1
          - 2
          - 3
          - Deferred

  - _loadedCount_ should return 4.
  - Invoke _loadData(this.data.children[4], 3, null, [4], false)_
  - Validate new state:

        - RootNode
          - 0
          - 1
          - 2
          - 3
          - 4

  - _loadedCount_ should return 5.
- Given a sub-class of DataPager:
  - Following methods overridden:

        recordMatchesGroup(group, record) { return false; },

        maybeCreateGroupForNode(parent, record) { return parent instanceof PagedData.Root ? new PagedData.Group(record.nodeData) : null; }

  - Initial state:

        - RootNode
          - Deferred

  - _loadedCount_ should return 0.
  - Invoke _loadData(this.data.children[0], 3, null, [0, 1, 2, 3], true)_
  - Validate new state:

        - RootNode
          - Group 0
            - Record 0
          - Group 1
            - Record 1
          - Group 2
            - Record 2
          - Group 3
            - Record 3
            - Deferred
          - Deferred

  - _loadedCount_ should return 8.
  - Invoke _loadData(this.data.children[3].children[1], 3, null, [4], false)_
  - Validate new state:

        - RootNode
          - Group 0
            - Record 0
          - Group 1
            - Record 1
          - Group 2
            - Record 2
          - Group 3
            - Record 3
          - Group 4
            - Record 4

  - _loadedCount_ should return 10.

### ArrayPager isA DataPager

Presents an array of objects.

Properties:

- _sourceData isA Array_
  - The array data for paging. Elements of _sourceData_ will become the _nodeData_ of the corresponding PagedData.Nodes.
- _loadDelayInMilliseconds isA Number &ge; 0_
  - Artificial delay for _load()_ callbacks.
  - Default is 0.
- _countDelayInMilliseconds isA Number &ge; 0_
  - Artificial delay for _count()_ callbacks.
  - Default is 0.

Methods:

- _getNodes(startNode isA PagedData.Deferred, count isA Number, context, successCallback(context, data), errorCallback(context, detail))_
  - Waits _loadDelayInMilliseconds_, then invokes _load()_ with a slice of _sourceData_, with the offset equal to the number of PagedData.Record nodes that precede _startNode_ (via _loadedRecordCount_, and count equal to _count_ (or up to the end of _sourceData_).
- _count(context, successCallback(context, count), errorCallback(context, detail))_
  - Waits _countDelayInMilliseconds_, then returns _successCallback(context, Em.get(this, 'sourceData.length'))_.

### QueriedPager isA DataPager

Provides the hooks to drive loading and resetting via a Query.SearchQuery.

Has its own package - queried-pager.

The pager uses a Query.SearchQuery (via the _query_ property) to determine what data to retrieve, and in what order. Consumers may bind the _query_ (and are usually expected to do so). Data will be reloaded when _query_ changes.

Bindable Query Properties:

- _query isA Query.SearchQuery_
  - The Query used to sort and filter data. _query_ is passed as _context_ to all _pager_ loading / counting methods (as per DataSourcedPager interface).

Query Methods:

- _queryUriDidChange()_
  - observes _query.observableUri_, invokes _reset()_.

Load Methods:

- _getGroupedAttrForParent(groupedAttrNames, parent isA PagedData.Node)_
  - Determines the depth of _parent_, using that to index into the _query.group_ array. Returns the related value from _groupedAttrNames_ or null.

### DataSourcedPager isA QueriedPager

Presents DataStore sourced data via the Pager interface. All acquire() and count() calls against the DataStore are **unlocked** - null is passed for the _owner_ parameter for those invocations. DataSourcedPager performs grouping on the client side.

Has its own package - data-sourced-pager.

Properties:

- _dataStore isA DataStore_

The pager uses a Query.SearchQuery (via the _query_ property) to determine what data to retrieve, and in what order. Consumers may bind the _query_ (and are usually expected to do so). Data will be reloaded when _query_ changes.

Load Methods:

- _getNodes(startNode isA PagedData.Deferred, count isA Number, context, successCallback(context, data), errorCallback(context, detail))_
  - Invokes _dataStore.acquire()_ with a copy of _query_ that has _limit_ set to _pageSize_ and _offset_ set to _getRecordOffset(standIn)_.
- _getCount(context, successCallback(context, total isA Number), errorCallback(context, detail))_
  - Invokes _dataStore.count()_ with _query_.
- _getGroupedAttrForParent(groupedAttrNames, parent isA PagedData.Node)_
  - Determines the depth of _parent_, using that to index into the _query.group_ array. Returns the related value from _groupedAttrNames_ or null.
- _maybeCreateGroupForNode(parent isA PagedData.Node, record isA PagedData.Record)_
  - Uses _getGroupedAttrForParent()_ to get an attribute name. If there's an attribute name, queries it and uses that as the _nodeData_ in the created group. Otherwise returns null.
- _recordMatchesGroup(PagedData.Group parent, PagedData.Record record)_
  - Uses _getGroupedAttrForParent()_ to get an attribute name. If there's an attribute name, queries it and compares it with the _nodeData_ of the _parent_.

Count Methods:

- _count(context, successCallback(context, count), errorCallback(context, detail))_
  - Invokes _dataStore.count()_ with _query_.

### Log

- page\*At timestamps renamed to \*At.
- Redesigned the loader, again, to be sub-class driven.

----------------------------------------------------------------------------------------------------

<a name="design.pagedcomponent"></a>

PagedComponent: Base Class for Paged Components
-----------------------------------------------

### PagedComponent isA Em.Component

This abstract base class provides the following services to sub-classes:

- Paging
- Selection
- Context Menus
- Navigation
- Scrolling

The core property of PagedComponent is the PagedData, via _pager_.

Core Properties:

- _pager isA DataPager_
  - The _pager_ stores and manages all data for rendering.

#### Pausing and Resuming Data Loading

Data loading can be paused and resumed by setting and clearing _paused_.

Bindable Load-related Properties (triggers):

- _paused isA Boolean_
  - Stops data loading when true.

Internal Load-related Methods (trigger observers):

- _resumeLoad(loadRequest isA plain javascript object)_
  - Typically invoked with the value of _loadRequest_.
  - Checks the value of _pager.loadContext_ against _loadRequest_. Does nothing if they are the same. Otherwise:
    - Invokes _pager.load()_, using the properties of _loadRequest_ (see _loadRequest_ for the structure), and _context_ set to the _request_ itself.
    - The _successCallback()_ and _errorCallback()_ will clear _loadRequest_.
- _pauseLoad()_
  - Invokes _pager.cancelLoad()_
- _loadRequestDidChange()_
  - Observes _loadRequest_.
  - If set, invokes _ensureLoad()_ with the request.
- _pausedDidChange()_
  - Observes _pausedOverall_.
  - If set, invokes _pauseLoad()_.
  - If cleared, and if _loadRequest_ is also set, invokes _resumeLoad()_ with the request.

#### Triggering Renders

Consumers of a PagedComponent can force it to re-render by touching _resetRequiredAt_. A full reload can be triggered by touching _refreshRequiredAt_. Finally, data loading can be paused and resumed by setting and clearing _paused_.

Bindable Load-related Properties (triggers):

- _resetRequiredAt isA Date_
  - Timestamp set to present moment to reload its data completely. This is expected to reset the scroll position.
- _refreshRequiredAt isA Date_
  - Timestamp set to to present moment to notify the component that the view should be re-rendered. The structure of the data is expected to have remained the same, and so the scroll position will be preserved.

Each of the triggers is observed, and the appropriate behaviour run when the trigger changes.

Internal Load-related Methods (trigger observers):

- _resetDidTrigger()_
  - Observes _resetRequiredAt_.
  - Invokes _reset()_
- _reset()_
  - Invokes _pager.reset()_.
- _refreshDidTrigger()_
  - Observes _refreshRequiredAt_.
  - Invokes _refresh()_
- _refresh()_
  - Invokes _pageContentDidChange()_.

Load-related Properties (everything else):

- _countDelayInMilliseconds isA Number &gt;= 0_
  - For performance reasons, we have the option to delay invocation of counting.
- _pausedInternally isA Boolean_
  - Stops data loading when true. This property may NOT be bound by consumers.
- _pausedOverall isA Boolean_
  - Dynamic property, _paused_ OR _pausedInternally_.
- _loadInProgress_
  - facade for _pager.loadInProgress_

All rendering is handled by sub-classes. PagedComponent provides several entry points, in order to allow sub-classes to update the DOM in a performant and user-friendly manner. The rendering callbacks are triggered by changes to _pager.data_.

Abstract Rendering Callback Methods:

- _pageContentDidChange()_
  - Invoked when the structure of _pager.data_ has not changed.
  - Sub-classes are expected to re-render in place, without changing scroll position or other kinds of state.
  - Default implementation does nothing.
- _pageStructureDidReset()_
  - Invoked when the structure of _pager.data_ has been reset completely.
  - Default implementation does nothing.
- _pageStructureDidGrow()_
  - Invoked when PagedData.Deferred nodes have been replaced with newly loaded content.
  - Default implementation does nothing.
- _pageStructureDidChange()_
  - Invoked when the structure of _pager.data_ has changed in a way that does not meet the criteria of any of the other callbacks (e.g. when a PagedData.Node has been removed).
  - Default implementation does nothing.

Consumers of a PagedComponent may bind various triggers to force it to re-render, or re-load completely. Consumers may also pause data loading.

PagedComponent monitors App.isLocalizing, so that it can re-render itself when that flag changes.

#### Localization

Localization-related Methods:

- _isLocalizingDidChange()_
  - Observes App.isLocalizing
  - Invokes _pageContentDidChange()_ to force data to re-render in place.

#### Selection

Selection-related Properties:

- _hasSelection isA Boolean_
  - Computed property, returns true if _hasSingleSelection_ or _hasMultipleSelection_ is true.
- _hasSingleSelection isA Boolean_
  - If true, enables selection behaviour. Only a single item may be selected at a time.
  - Default false.
- _hasMultipleSelection isA Boolean_
  - If true, enables selection behaviour. Multiple items may be selected at a time.
  - Default false.
- _selections isA IndexedMutableArray_
  - An indexed list of the selected PagedData.Nodes.

#### Menus

Menu-related Properties:

- _menus isA ContextMenuController_
  - Handles context menu display / content / action dispatch.

#### Scroll Position

Scroll-related Properties:

- _scrollPosition isA Number_
  - Invokers may force the scroll position to a certain place by setting this property (e.g. to restore a saved position).

Navigation-related Properties:

- _hasNavigation isA Boolean OR function(PagedData.Node obj)_
  - If true, rows are expected to be styled like links, and to send _navigate(obj)_ actions appropriately.

Actions:

- _navigate(obj isA PagedData.Node)_
  - "Go to" the obj.
- _scroll(offset isA Number)_
  - Notification when scroll position has changed.

----------------------------------------------------------------------------------------------------

<a name="design.abstable"></a>

AbsTableComponent: For Rendering a Paged Table
----------------------------------------------

### AbsTableComponent isA PagedComponent

Renders tabular data using [DataTables](http://datatables.net). Virtual scrolling is implemented via the [Scroller Extension](http://datatables.net/extensions/scroller/). Frozen columns are implemented via the [Fixed Columns Extension](http://datatables.net/extensions/fixedcolumns/).

Data-Tables Properties:

- _dataTablesConfig isA [DataTables options](http://datatables.net/reference/option/)_
  - Dynamic property automatically updated when the following dependent properties change:
    - _columns.[]_
  - Config generated by _dataTablesConfigFromControllerConfig()_
- _dataTablesFixedExtConfig isA [DataTables Fixed Columns Extension options](http://datatables.net/extensions/fixedcolumns/options)_
  - Dynamic property automatically updated when the following dependent properties change:
    - _columns.[]_
  - config properties are as follows:
    - _iLeftColumns_ to the number of elements in _columns_ that have _isFrozen_ set.
    - _sHeightMatch_: "auto"

Properties:

- _columns isA Array of AbsTableComponent.ColumnConfig_
- _groups isA Array of AbsTableComponent.GroupConfig_
- _template isA Compiled Handlebars Template_

Event Methods:

- _click(evt isA Event)_
  - Invokes each of the _try\*()_ methods in turn, stopping with the first that returns true (handled).
- _trySelectionCheckboxClick(evt isA Event)_
  - Checks to see if a selection checkbox was clicked, and if so updates _selections_ accordingly and returns true.
- _tryContextMenuToggleClick(evt isA Event)_
  - Checks to see if a context menu toggle was clicked, and if so updates _menu_ accordingly and returns true.
- _tryAnchorClick(evt isA Event)_
  - Checks to see if an anchor was clicked, and if so dispatches a _navigate_ event and returns true.

View Methods:

- _didInsertElement()_
  - Invokes _dataTablesSetup()_
- _willRemoveElement()_
  - Invokes _dataTablesTeardown()_

DataTables Related Methods:

- _dataTablesSetup(config isA plain javascript object)_
- _dataTablesTeardown()_
- _dataTablesConfigDidChange() observes dataTablesConfig_
  - Does nothing if _state_ isn't "inDOM".
  - Tears down and rebuilds DataTables.
  - Updates _resetRequiredAt_
- _dataTablesConfigFromControllerConfig(config isA Array of AbsTableComponent.ColumnConfig)_
  - Generates a DataTables config from a AbsTableComponent.ColumnConfig.
  - Sub-classes may override this for more specialized behaviour, but hopefully this won't be necessary with the new implementation (much of the jqGrid related technical debt lies in a similar place).
  - Config properties are set as follows:
    - _paging_: true
    - _ordering_: false
    - _searching_: false
    - _info_: false
    - _scrollY_: "auto"
    - _scrollX_: true
    - _scrollCollapse_: false
    - _scroller_: { loadingIndicator: false, rowHeight: 30, serverWait: 20 }
    - _serverSide_: true
    - _dom_: "tS"
    - _deferRender_: true
    - _ajax_
      - Set to wrapper function that invokes _dataTablesLoad()_ in AbsTableComponent scope.
    - _headerCallback_
      - Set to wrapper function that invokes _dataTablesHeaderDidRender()_ in AbsTableComponent scope.
    - _rowCallback_
      - Set to wrapper function that invokes _dataTablesRowDidRender()_ in AbsTableComponent scope.
    - _columnDefs_
      - Maps each element _e_ of the _columns_ property to an element _f_ of _columnDefs_ as follows:
        - _e.isFrozen_ is ignored (handled by _dataTablesFixedExtConfig_).
        - _e.isSortable_ to _f.orderable_.
        - _e.labelResource_ is resolved and passed as _f.title_.
        - _f.render_ is set to a wrapper function that invokes _dataTablesRenderCell()_ in AbsTableComponent scope.
      - If _hasSelection_ is true or _menus_ is set, an additional columnDef is prepended for the "input" cell.
- _dataTablesFixedExtConfigFromControllerConfig(config isA Array of AbsTableComponent.ColumnConfig)_
  - config properties are as follows:
    - _ileftcolumns_ to the number of elements in _columns_ that have _isfrozen_ set.
    - _sheightmatch_: "auto"
- _dataTablesRenderCell(data, type, row, meta)_
  - [DataTables cell render function](http://datatables.net/reference/option/columns.render)
- _dataTablesHeaderDidRender(thead, data, start, end, display)_
  - [DataTables headerCallback](http://datatables.net/reference/option/headerCallback)
- _dataTablesRowDidRender(row, data)_
  - [DataTables rowCallback](http://datatables.net/reference/option/rowCallback)
- _dataTablesLoad(data, callback, settings)_
  - [DataTables AJAX load function](http://datatables.net/reference/option/ajax)

Rendering Methods (entry points):

- _renderRecordCell(record isA PagedData.Record, column isA AbsTableComponent.ColumnConfig)_
  - Renders a cell related to a PagedData.Record.
  - Returns a string of HTML or an array of DOM Nodes suitable for insertion into a TD.
- _renderGroupRow(group isA PagedData.Group, groupConfig isA AbsTableComponent.GroupConfig)_
  - Renders a row related to a PagedData.Group.
  - Returns a string of HTML or an array of DOM Nodes suitable for insertion into a TD.
- _renderInputCell()_
  - Renders a cell for the form fields related to a PagedData.Record (selection checkbox and / or context menu).
  - Returns a string of HTML or an array of DOM Nodes suitable for insertion into a TD.

Rendering Methods (helpers):

- _replaceRow(trNode isA Node, content isA String)_
- _renderSelectionCheckbox()_
- _renderAnchor(actionName isA String, context)_
- _renderContextMenuToggle(isShowingMenu isA Boolean)_

### AbsTableComponent.ColumnConfig

Properties:

- _id isA String_
  - Uniquely identifies this column config within the scope of the related configuration.
- _columnData_
  - Used to render the column header. Structure is arbitrary.

Behaviour-related Properties:

- _isFrozen isA Boolean_
  - If true, the column will remain in place irrespective of horizontal scrolling. Frozen columns, if any, are expected to be "left-most" in the column order. A column may not be frozen and grouped at the same time.
- _isSortable isA Boolean_
  - If true, the view is expected to render affordances for sorting by this column.

Rendering-related Properties:

- _labelResource isA String_
  - Path to the label.
- _headerFormat isA function(context isA plain javascript object) OR null_
  - Handlebars-compatible rendering function, accepts _columnData_.
  - Optional. If unset, the resolved _labelResource_ is rendered directly.
- _cellFormat isA function(context isA plain javascript object) OR null_
  - At render time, _cellFormat()_ is invoked with the value of the corresponding _nodeData_ property of the related _PagedData.Node_.

### AbsTableComponent.GroupConfig

Properties:

- _id isA String_
  - Uniquely identifies this group config within the scope of the related configuration.
- _rowFormat isA function(context isA plain javascript object) OR null_
  - At render time, _rowFormat()_ is invoked with the value of the corresponding _nodeData_ property of the related _PagedData.Group_.

### Log

- AbsTableComponent.ColumnConfig: headerFormat, cellFormat to renderHeader and renderCell.

----------------------------------------------------------------------------------------------------

<a name="design.absnav"></a>

AbsNavComponent: For Rendering a Paged Nav
------------------------------------------

TBD.

----------------------------------------------------------------------------------------------------

<a name="design.tablepage"></a>

TablePage: For Integrating the Parts of a Page Featuring a Table
----------------------------------------------------------------

The new grid component will render _only_ the grid. Surrounding content that used to be managed by the ListController / ListView will instead be managed by the TablePageController and related template (there will be no corresponding view).

### TablePageController

Properties from Desktop.ListController (same behaviour):

- _name isA String_
- _summary isA String_
- _urlForHelp isA String_
- _listActions isA Array of plain javascript object_
- _hasSelectionActions isA Boolean_
- _selectionActionsController isA ButtonGroupController_
- _searchFilterSupported isA Boolean_
- _selectColumnsSupported isA Boolean_
- _advancedFilterSupported isA Boolean_
- _showFilterEditor isA Boolean_
- _showFilter isA Boolean_
- _advancedFilterController isA AdvancedFilterController_
- _searchableColumnSpecs isA Array of plain javascript object_
- _listTitle isA String_
- _totalSummary isA String_

Properties for binding to AbsTable:

- _pagedData isA PagedComponent.Data_
  - To be bound to AbsTable.data.
- _visibleColumnNames isA Array of String_
- _columnConfig isA Array of AbsTableComponent.ColumnConfig_
- _query isA SearchQuery_
  - query.sort to be bound to AbsTable.sort.
- _itemActions isA Array of plain javascript object_
  - To be bound to AbsTable.menu.
- _selectionsList isA Array of Model_
  - To be bound to AbsTable.selections.

Actions from ListController:

- _selectColumns_
- _toggleAdvancedFilterEditor_

#### Test Cases

- _totalSummary_ should be reset when _query.observableUri_ changes.
- _columnConfig_ should be driven by _visibleColumnNames_.

----------------------------------------------------------------------------------------------------

<a name="design.abslist"></a>

AbsListComponent: For Rendering a Paged List (Out of Scope)
-----------------------------------------------------------

### List2Controller isA Paged2Controller

_Out of scope._ Provies configuration and pages for list rendering, including menu behaviour.

### List2View

_Out of scope._ Renders a list.

### ListNav2View

_Out of scope._ Renders a Nav-style list.

----------------------------------------------------------------------------------------------------

<a name="design.abstree"></a>

AbsTreeComponent: For Rendering a Paged Tree (Out of Scope)
-----------------------------------------------------------

### Tree2Controller isA Paged2Controller

_Out of scope._ Provides configuration for tree rendering, including menu behaviour.

### Tree2View

_Out of scope._ Renders a tree.

### TreeNav2View

_Out of scope._ Renders a Nav-style tree.

----------------------------------------------------------------------------------------------------

<a name="scope"></a>

Scope of Work and Estimate
--------------------------

Work will be done in several stages. In the first stage, the base classes and new package will be implemented. Once this work is complete, the rest of the code may be ported in a piecemeal fashion.

### Stage One Estimate: Core Libraries

- First iteration of core classes:
  - IndexedMutableArray: 1 person hour
  - PagedData: 8 person hours
  - DataPager: 8 person hours
  - DataSourcedPager: 8 person hours
  - PagedComponent: 16 person hours
  - AbsTableComponent: 24 person hours
  - AbsNavComponent: 16 person hours
- **Total: 81 person hours**

### Stage Two Estimate: Reports

The reporting system will be the first component ported to the new grid, as customers will see the largest benefit from work.

This estimate is dramatically higher than that of later components because of its complexity, and because it will be the first component to be ported. It is expected that some weaknesses in the design will be encountered and resolved at this stage.

- Reporting Port:
  - Data: 32 person hours
  - Nav: 16 person hours
- **Total: 48 person hours**

### Stage Three Estimate: Software Titling

Software titling is the only component other than reports which is currently limited by our existing grid component (i.e. it is the only other grid with explicit paging). All other components may be ported in a piecemeal fashion as development resources are available to do so.

This estimate is also much higher than that of later components for the same reasons as in Stage Two. It is expected that further (hopefully more minor) weaknesses in the design will be encountered and resolved at this stage.

- Software Titling Port:
  - Data: 24 person hours
  - Nav: 8 person hours
- **Total: 32 person hours**

### Stage Four Estimate: All Other Components

    dduchene@dduchene4:packages[develop]$ egrep -r 'PagedController|ListController|SingleSelectController|MultiSelectController|ReportController|SummaryListController|NavController|SimpleListController|RelatedListController' am-* | grep extend | wc
          73     278   11345

    dduchene@dduchene4:packages[develop]$ egrep -r 'PagedView|ListView|ReportView|SingleSelectView|MultiSelectView|NavView|SimpleListView|NavItemView|RelatedListView' am-* | grep extend | wc
          10      44    1356

    dduchene@dduchene4:packages[develop]$ egrep -r 'PagedController|ListController|SingleSelectController|MultiSelectController|ReportController|SummaryListController|NavController|SimpleListController|RelatedListController' cc-* | grep extend | wc
          34     149    4658

    dduchene@dduchene4:packages[develop]$ egrep -r 'PagedView|ListView|ReportView|SingleSelectView|MultiSelectView|NavView|SimpleListView|NavItemView|RelatedListView' cc-* | grep extend | wc
          24      81    2585

As can be seen in these rough metrics, there are about 73 grid-related controllers in Absolute Manage, and 10 grid-related views. Customer Center has 34 grid-related controllers, and 24 grid-related views. Once the base classes and the initial port of the Software Titling Page and Nav are in place, assume that each controller and view requires two hours to port on average.

- **Customer Center Total: 108 person hours**
- **Absolute Manage Total: 166 person hours**
