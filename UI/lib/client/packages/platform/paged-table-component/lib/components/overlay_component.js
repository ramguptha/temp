define([
  'ember',
  'packages/platform/paged-component',
  'text!../templates/overlay.handlebars'
], function(
  Em,
  PagedComponent,
  template
) {
  'use strict';

  // Overlay Component
  // =================
  //
  // Base class for rendering nodes in an overlay. Invokers of PagedTable may override this by injecting the
  // the overlayComponent property.

  return PagedComponent.ItemComponent.extend({
    layout: Em.Handlebars.compile(template),
    classNameBindings: 'this.row.isEven:even:odd :is-container-for-group :fill :flex-container :overlay'.w(),
    attributeBindings: 'style',

    presentation: null,

    style: Em.computed.oneWay('presentation.nodeStyle'),

    click: function(e) {
      this.get('presentation.row.columnPresentation.absTable').send('overlayClick', this.get('presentation'));
    }
  });
});
