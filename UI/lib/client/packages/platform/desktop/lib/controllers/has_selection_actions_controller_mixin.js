define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Mixin.create({
    // Selection actions are actions which are application when one or more items are selected.
    selectionActions: null,

    hasSelectionActions: function() {
      return Em.isArray(this.get('selectionActions'));
    }.property('selectionActions'),

    selectionActionsWrapper: function() {
      var selectionActions = this.get('selectionActions');
      return selectionActions ? [{
        labelResource: this.get('actionsLabelPath'),
        disabledBinding: 'pagedComponent.parentView.context.selectionActionsDisabledOrNoContext',
        children: this.get('selectionActions')
      }] : null;
    }.property('selectionActions.[]', 'selectionsList.[]'),

    selectionActionsDisabled: false,
    selectionActionsDisabledOrNoContext: function() {
      return this.get('selectionActionsDisabled') || Em.isEmpty(this.get('selectionsList')) || Em.isEmpty(this.get('selectionActions'));
    }.property('selectionActionsDisabled', 'selectionsList.[]', 'selectionActions.[]'),

    selectionActionsController: null
  });
});
