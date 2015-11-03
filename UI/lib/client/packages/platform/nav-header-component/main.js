define([
  'ember',
  'packages/platform/desktop',
  'text!./lib/templates/nav_header.handlebars'
], function(
  Em,
  Desktop,
  template
) {
  'use strict';

  // Nav Header Component
  // ====================
  //
  // Renders a title and a search box with optional attribute selector.
  //
  // Injectable properties:
  //
  // - title: Main title
  // - subTitle: Duh
  // - iconClass: Render a span with the set icon class
  // - iconPath: Render an img with the set icon path
  // - query: A search query, passed to adhoc-search or the text field
  // - nameOptions: list of options for the query searchAttr
  // - searchFilterSupported: if true, show a search filter
  // - isSearchCaseSensitive: if true, use a placeholder in the adhoc search box that says so
  // - navActions: Options for a button group.
  // - navActionsContext: Default is parentView.context - the context under which to execute navActions.
  // - navActionsTarget: Default is navActionsContext - the target to send navActions to.

  var NavHeaderComponent = Em.Component.extend({
    layout: Em.Handlebars.compile(template),
    classNames: 'is-nav-header nav-view-controls'.w(),

    title: null,
    subTitle: null,

    iconClass: null,
    iconPath: null,

    query: null,

    nameOptions: [],
    nameOptionValuePath: 'content.name',
    nameOptionLabelPath: 'content.label',

    searchFilterSupported: true,
    isSearchCaseSensitive: false,

    navActions: null,
    navActionsContext: Em.computed.oneWay('parentView.context'),
    navActionsTarget: Em.computed.oneWay('navActionsContext'),

    hasNavActions: function() {
      return !Em.isEmpty(this.get('navActions'));
    }.property('navActions.[]')
  });

  return NavHeaderComponent.reopenClass({
    appClasses: { NavHeaderComponent: NavHeaderComponent }
  });
});
