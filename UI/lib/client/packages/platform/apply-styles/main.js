define(function() {
  'use strict';

  // applyStyles(view, selector, { name1: style1, name2: style2, ... })
  // ==================================================================
  //
  // Apply named styles to first elt matched by selector, if they are different from those currently set.
  //
  // If no elements match selector, tries again afterRender.

  return function(view, selector, stylesByName) {
    var find = function() {
      if ('inDOM' === view.get('_state')) {
        return view.$(selector)[0];
      } else {
        return null;
      }
    }

    var apply = function() {
      for (var name in stylesByName) {
        if (stylesByName.hasOwnProperty(name)) {
          var style = stylesByName[name];

          if ($elt.style[name] !== style) {
            $elt.style[name] = style;
          }
        }
      }
    }

    var $elt = find();

    if ($elt) {
      apply();
    } else {
      Em.run.schedule('afterRender', function() {
        $elt = find();

        if ($elt) {
          apply();
        }
      });
    }
  };
});