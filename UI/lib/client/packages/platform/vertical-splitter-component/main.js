define([
  'ember',
  'packages/platform/paged-table-component',
  'packages/platform/apply-styles',

  'text!./lib/templates/vertical_splitter.handlebars'
], function(
  Em,
  PagedTableComponent,
  applyStyles,

  template
) {
  'use strict';

  // Vertical Splitter
  // =======
  //
  // Encapsulates the interaction of a vertical divider.
  // Controller of the parent container will do the basic settings of the resizer
  // The rest of the interactions/ calculations are delegated to this component

  var VerticalSplitterComponent = Em.Component.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'is-container-for-vertical-splitter'.w(),
    topElementClass: '.resizer-top-element',
    bottomElementClass: '.resizer-bottom-element',

    applyStyles: applyStyles,

    resizer: null,

    // Top and bottom boundary from which the divider element can not pass over
    bounds: null,

    // Position of the divider element at each moment
    resizePosition: null,

    // Original position of the resizer
    origin: null,

    init: function() {
      this._super();

      // Observers
      this.getProperties('bounds resizePosition'.w());
    },

    // resizer's keys are set by the controller of the container of this vertical splitter
    resizeElementDidChange: function() {
      var resizer = this.get('resizer'),
          parentElement = resizer.parentElement,
          topLimit = resizer.topLimit,
          bottomLimit = resizer.bottomLimit;

      var resizerTop = resizer.resizeElement.offset().top,
          parentLeft = parentElement.offset().left,
          parentTop = parentElement.offset().top,
          parentHeight = parentElement.parent()[0].clientHeight,
          parentWidth = parentElement.parent()[0].clientWidth;

      this.setProperties({
        bounds: { top: (parentTop + topLimit), height: (parentHeight - (topLimit + bottomLimit)), left: parentLeft, width: parentWidth },
        resizePosition: resizerTop - (parentTop + topLimit),
        origin: resizerTop - (parentTop + topLimit)
      });
    }.observes('resizer.resizeElement'),

    boundsDidChange: function() {
      var bounds = this.get('bounds');

      var boundsStyle = {
        left: null,
        width: null,
        top: null,
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
    }.observes('bounds'),

    resizePositionDidChange: function() {
      var resizePosition = this.get('resizePosition');

      this.applyStyles(this, '.vertical-splitter-mark', { top: resizePosition + 'px', display: 'block' });
    }.observes('resizePosition'),


    // Event Handlers
    // --------------

    mouseUp: function() {
      var parentView = this.get('parentView'),
          parentElement = this.get('resizer').parentElement,
          topElement = parentElement.find(this.get('topElementClass')),
          bottomElement = parentElement.find(this.get('bottomElementClass'));

      var resizeOffset = this.get('resizePosition') - this.get('origin');

      var topElementHeight = topElement[0].offsetHeight + resizeOffset,
          bottomElementHeight = bottomElement[0].offsetHeight- resizeOffset;

      this.applyStyles(parentView, this.get('topElementClass'), {
        'max-height': topElementHeight  + 'px',
        height: topElementHeight + 'px'
      });

      this.applyStyles(parentView, this.get('bottomElementClass'), {
        'max-height': bottomElementHeight + 'px',
        height: bottomElementHeight + 'px'
      });

      this.applyStyles(this, '.client-fill', { display: 'none' });
    },

    mouseMove: function(event) {
      if (this.get('_state') === 'inDOM') {

        var $bounds = this.$('.bounds'),
            boundsTop = $bounds.offset().top,
            boundsHeight = $bounds.height(),
            resizePosition = event.clientY - boundsTop;

        // If mouse is slowly going out of top/bottom boundary of the bounds
        // reset it back to the top/bottom border
        if (resizePosition < 0) {
          resizePosition = 0;
        } else if (resizePosition > boundsHeight) {
          resizePosition = boundsHeight;
        }

        this.set('resizePosition', resizePosition);
      }
    },

    // TODO: WE DON'T OWN THE PARENT VIEW'S DOM! WE SHOULDN'T BE TOUCHING IT!
    //
    // Modify attributes, which parentView can bind instead. In the previous implementation, willDestroyElement()
    // would invoke applyStyles on this.get('parentView') to modify its DOM. It would appear that the parentView 
    // becomes invalid before willDestroyElement is invoked in Ember 1.13, so we store it on didInsertElement()
    // for use during willDestroyElement.
    //
    // We are bad people.
    cachedParentView: null,

    didInsertElement: function() {
      this._super();
      this.set('cachedParentView', this.get('parentView'));
    },

    willDestroyElement: function() {
      var cachedParentView = this.get('cachedParentView');

      if (cachedParentView && 'inDOM' === cachedParentView.get('_state')) {
        this.applyStyles(cachedParentView, this.get('topElementClass'), { 'max-height': '', height: '' });
        this.applyStyles(cachedParentView, this.get('bottomElementClass'), { 'max-height': '', height: '' });
      }
    }
  });

  return VerticalSplitterComponent.reopenClass({
    appClasses: {
      VerticalSplitterComponent: VerticalSplitterComponent
    }
  });
});
