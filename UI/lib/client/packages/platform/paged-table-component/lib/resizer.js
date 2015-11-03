define([
  'ember',
  'packages/platform/apply-styles',

  'text!./templates/resizer.handlebars'
], function(
  Em,
  applyStyles,

  resizerTemplate
) {
  'use strict';

  // Resizer
  // =======
  //
  // Encapsulates the resizing interaction.

  // View
  // ----

  var View = Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(resizerTemplate),
    classNames: 'is-container-for-resizer'.w(),

    applyStyles: applyStyles,

    resizer: null,

    init: function() {
      this._super();

      // Observers
      this.getProperties('resizer.bounds resizer.resizePosition'.w());
    },

    boundsDidChange: function() {
      var bounds = this.get('resizer.bounds');

      var boundsStyle = {
        top: null,
        left: null,
        width: null,
        height: null
      };

      if (bounds) {
        boundsStyle.left = bounds.left + 'px';
        boundsStyle.width = bounds.width + 'px';
        boundsStyle.top = bounds.top + 'px';
        boundsStyle.height = bounds.height + 'px';
      }

      this.applyStyles(this, '.bounds', boundsStyle);
      this.applyStyles(this, '.client-fill', { display: 'block' });
    }.observes('resizer.bounds'),

    resizePositionDidChange: function() {
      var resizePosition = this.get('resizer.resizePosition');
      this.applyStyles(this, '.resize-mark', { left: resizePosition + 'px' });
    }.observes('resizer.resizePosition'),

    // Event Handlers
    // --------------

    mouseUp: function(e) {
      // Update width of column to resizer.resizePosition + padding
      var resizer = this.get('resizer'),
        context = this.get('context'),
        columns = context.get('columns'),
        id = resizer.get('columnId');

      columns.forEach(function(column) {
        if (id === column.get('id')) {
          column.set('width', resizer.get('resizePosition') + resizer.get('padding'));
        }
      });

      context.set('columns', columns);
      this.set('resizer.bounds', null);
      this.applyStyles(this, '.client-fill', { display: 'none' });

      context.sendAction('resize', id);
    },

    mouseMove: function(e) {
      if (this.get('_state') === 'inDOM') {
        var $bounds = this.$('.bounds')[0];

        var boundsLeft = this.$($bounds).offset().left,
          resizePosition = e.pageX - boundsLeft,
          resizeOrigin = this.get('resizer.origin');

        // If mouse is slowly going out of left boundary of the bounds
        // reset it back to the origin
        // There is no limitation on the moving to the right
        this.set('resizer.resizePosition', resizePosition < 0 ? resizeOrigin : resizePosition);
      }
    },

    mouseLeave: function(e) {
      this.set('resizer.resizePosition', this.get('resizer.origin'));
    }
  });

  return Em.Object.extend({
    // Left boundary from which the resize elements can not pass over and in turn column's width can not be lower than
    bounds: null,

    // Position of the resize element at each moment
    resizePosition: null,

    // Used to reset the position of the resize in case the mouse went pass the boundary of the bounds
    origin: null,

    // Id of the left column to the resize that is passed through the startResizing action
    columnId: null,

    // The padding is used to limit the minimum width that a user can resize a column to
    // 42 is the lucky number :) it is the minimum width which doesn't break layout for ellipsis plus sorting icon on a header
    padding: 42

  }).reopenClass({
    View: View
  });
});
