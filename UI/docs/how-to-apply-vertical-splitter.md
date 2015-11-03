Vertical-Splitter Component
===========================

This document describes how to use Vertical Splitter component (lib/client/packages/platform/vertical-splitter-component/)

The component supposed to be used for splitting content with movable splitter in vertical direction.

It will expect the structure of a parent panel with two resizable panels inside and a splitter container between them.
Top panel must have the class 'resizer-top-element' and bottom one 'resizer-bottom-element' accordingly.
Top and bottom panels doesn't have to be a direct child of a parent if they wrapped in flexible height elements.
See example below:

Note! Since the action of resizing involves with the DOM elements, we put the action and controlling part of the
resizer object in the view that owns the component.

### Template use:
      <div class="[Parent container]">
       <div class="resizer-top-element">
         ...
       </div>

       <div class="vertical-splitter" {{action startResize on="mouseDown" target=view}}></div>
       {{vertical-splitter resizer=view.resizer}}

       <div class="resizer-bottom-element">
         ...
       </div>
      </div>

By default 'containment' area is limited by parent container, but it can be defined by using 'topLimit' and 'bottomLimit' properties.
Splitter container element also can be defined in 'resizeElement' property.

### View use:

       Class -> SelectDevicesForFixedGroupView

       return Em.View.extend({        ...
        resizer: {
          resizeElement: null,
          parentElement: null,
          topLimit: 0,
          bottomLimit: 0
        },

        actions: {
          startResize: function() {
            var resizeElement = $('.vertical-splitter');

            this.set('resizer', {
              topLimit: 100,
              bottomLimit: 160,
              resizeElement: resizeElement,
              parentElement: resizeElement.parent()
            });
          }
        }
        ...
