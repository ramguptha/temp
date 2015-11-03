define([
  './resolve_globals',
  './render'
], function(
  resolveGlobals,
  render
) {
  'use strict';

  // renderGlobals(path, context)
  // ----------------------------
  //
  // Resolve and render a resource in a single step, via resolveGlobals() / render()
  return function(path, context) {
    context = context || {};
    return render(resolveGlobals(path), context);
  }
});
