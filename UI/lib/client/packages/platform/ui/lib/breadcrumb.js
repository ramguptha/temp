define([
  'ember',
  'locale'
], function(
  Em,
  Locale
) {
  'use strict';

  // Breadcrumb
  // ==========
  //
  // A Breadcrumb is used to show the user where that "are" in the information architecture. As they navigate
  // deeper into the application hierarchy, each "step" between their current location and the application landing
  // renders as a node in the breadcrumbs list.
  return Em.Object.extend({
    // Provided on creation by the owner, optional.
    parentBreadcrumb: null,

    // Path to transition to.
    path: null,

    // Optional context for the path to transition to, as an array. This context is _just_ the context
    // that applies to the route related to _path_.
    context: null,

    // Title to show.
//    title: null,

    titleResource: null,
    title: Locale.translated({ property: 'titleResource' }),

    // This breadcrumbs context appended to the end of the parentBreadcrumbs context.
    fullContext: function() {
      var parentBreadcrumbContext = this.get('parentBreadcrumb.fullContext');
      var fullContext = Em.copy(Em.makeArray(parentBreadcrumbContext));

      var context = this.get('context');
      if (Em.isArray(context)) {
        fullContext.pushObjects(context);
      }

      return fullContext;
    }.property('context', 'parentBreadcrumb.fullContext.[]'),

    chain: function() {
      var parentBreadcrumbChain = this.get('parentBreadcrumb.chain');
      var chain = Em.copy(Em.makeArray(parentBreadcrumbChain));

      chain.unshiftObject(this);

      return chain;
    }.property('parentBreadcrumb.chain.[]')
  });
});
