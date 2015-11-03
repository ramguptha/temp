define([
  'ember',
  'locale',
  'text!./lib/templates/collection_header.handlebars'
], function(
  Em,
  Locale,
  template
) {
  'use strict';

  // Collection Header
  // =================
  //
  // Renders the top frame of a "main content" collection.
  //
  // Bindable properties:
  //
  // - counter
  // - selections
  // - selectionActions: Options for a button group. These will show up as menu items under an "Actions" button.
  // - selectionActionsDisabled: If true, force the "Actions" button disabled.
  // - selectionActionsContext: Default is parentView.context - the context under which to execute selectionActions.
  // - selectionActionsTarget: Default is selectionActionsContext - the target to send selectionActions to.
  // - hasTotal
  // - totalSummary
  // - rActionsLabel
  //
  // Bindable actions:
  //
  // - showErrorDetail
  var CollectionHeaderComponent = Em.Component.extend({
    actions: {
      showErrorDetail: function(error) {
        this.sendAction('showErrorDetail', error);
      }
    },

    layout: Em.Handlebars.compile(template),
    classNames: 'is-collection-header list-top-frame'.w(),

    counter: null,
    selections: null,
    selectionActions: null,
    selectionActionsDisabled: false,
    selectionActionsContext: Em.computed.oneWay('parentView.context'),
    selectionActionsTarget: Em.computed.oneWay('selectionActionsContext'),

    hasTotal: false,
    totalSummary: '',

    // In some of the AM views, the selection button groups are laid out horizontally
    // In which case we use different component for button groups
    isButtonGroupHorizontal: false,

    hasSelectionActions: function() {
      return Em.isArray(this.get('selectionActions'));
    }.property('selectionActions'),

    selectionActionsWrapper: function() {
      var selectionActions = this.get('selectionActions');
      return selectionActions ? [{
        labelResource: this.get('rActionsLabel'),
        disabledBinding: 'component.parentView.context.selectionActionsDisabledOrNoContext',
        children: this.get('selectionActions')
      }] : null;
    }.property('selectionActions.[]', 'selections.[]'),

    selectionActionsDisabledOrNoContext: function() {
      var noContext = Em.isEmpty(this.get('selections')) || Em.isEmpty(this.get('selectionActions'));
      return this.get('selectionActionsDisabled') || noContext;
    }.property('selectionActionsDisabled', 'selections.[]', 'selectionActions.[]'),

    rActionsLabel: 'shared.buttons.actions'
  });

  return CollectionHeaderComponent.reopenClass({
    appClasses: {
      CollectionHeaderComponent: CollectionHeaderComponent
    }
  });
});
