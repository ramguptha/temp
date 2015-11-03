// Yes, this isn't an anonymous module! require.js builds break is this module is defined anonymously.
// So, IF THIS FILE IS EVER MOVED, YOU MUST ALSO RENAME THE MODULE TO MATCH THE NEW PATH.
define('packages/platform/paged-component/lib/row_presentation', [
  'ember',
  'packages/platform/tracer'
], function(
  Em,
  Tracer
) {
  'use strict';

  // RowPresentation
  // ---------------
  //
  // Encapsulates presentation for a row.
  
  return Em.Object.extend(Tracer.IsTraced, {
    virtualPage: null,
    pagedComponent: Em.computed.oneWay('virtualPage.pagedComponent'),
    domIdMapper: Em.computed.oneWay('virtualPage.domIdMapper'),
    rowIndex: null,

    node: null,

    nodeId: function() {
      return this.get('node').id;
    }.property('node'),

    nodeData: function() {
      return this.get('node').nodeData;
    }.property('node'),

    parentNode: function() {
      return this.get('node').parentNode;
    }.property('node'),

    parentNodeId: function() {
      var parentNode = this.get('node').parentNode;
      return parentNode ? parentNode.id : null;
    }.property('node'),

    isGroup: function() {
      return this.get('node').isGroup;
    }.property('node'),

    isEven: function() {
      return !(this.get('node').offset & 1);
    }.property('node'),

    style: new Em.Handlebars.SafeString(''),

    id: function() {
      var domIdMapper = this.get('domIdMapper');
      var node = this.get('node');
      var id = node.isDeferred ? node.id : domIdMapper.getRowIdForNode(this.get('node'));

      return id;
    }.property('node'),

    automationId: function() {
      var domIdMapper = this.get('domIdMapper');
      var node = this.get('node');
      var automationId = node.isDeferred ? 'deferred' : domIdMapper.getRowAutomationIdForNode(this.get('node'));

      return automationId;
    }.property('node'),

    // Selection
    // ---------

    isSelected: function() {
      return this.get('pagedComponent.selectedIds').contains(this.get('node.id'));
    }.property('pagedComponent.selectedIds.[]', 'node.id'),

    // Enabled / Disabled
    // ------------------
    //
    // Place default hasRowClick and isDisabled implementation in a separate property for use by sub-classes.

    isDisabledBase: function() {
      return this.get('pagedComponent.disabledIds').contains(this.get('node.id'));
    }.property('pagedComponent.disabledIds.[]', 'node.id'),

    isDisabled: Em.computed.oneWay('isDisabledBase'),

    hasRowClickBase: function() {
      return !this.get('isDisabled') && !this.get('isDeferred') && this.get('pagedComponent.hasRowClick');
    }.property('isDisabled', 'isDeferred', 'pagedComponent.hasRowClick'),

    hasRowClick: Em.computed.oneWay('hasRowClickBase'),

    // Node properties
    // ---------------

    isRecord: Em.computed.oneWay('node.isRecord'),
    isDeferred: Em.computed.oneWay('node.isDeferred'),
    isLoading: Em.computed.oneWay('nodeData.isLoading'),
    isReadTail: function() {
      return this.get('pagedComponent.readTail') === this.get('node');
    }.property('pagedComponent.readTail', 'node'),

    externalError: Em.computed.oneWay('pagedComponent.externalError'),

    error: function() {
      var error = null;

      var externalError = this.get('externalError');
      if (externalError) {
        error = externalError;
      } else {
        var isFailed = this.get('nodeData.isFailed');
        if (isFailed) {
          var lastError = this.get('nodeData.lastError');
          error = lastError || true;
        }
      }

      return error;
    }.property('nodeData.isFailed', 'nodeData.lastError', 'externalError'),

    // Strings
    // -------

    tTryAgain: Em.computed.oneWay('pagedComponent.tTryAgain'),
    tShowErrorDetail: Em.computed.oneWay('pagedComponent.tShowErrorDetail'),
    tUnableLoadData: Em.computed.oneWay('pagedComponent.tUnableLoadData')
  });
});
