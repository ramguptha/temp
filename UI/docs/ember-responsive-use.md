Ember-Responsive
================

This document describes how to use [Ember-Responsive plugin](https://freshbooks.github.io/ember-responsive/)

The goal of ember-responsive is to give you a simple, Ember-aware way of dealing with media queries. All you need to do is tell it your application's breakpoints and it'll expose the rest for you.

### Breakpoints are set in application_base.js

    App.responsive({
      media: {
        mobile:  '(max-width: 768px)',
        tablet:  '(min-width: 769px) and (max-width: 992px)',
        desktop: '(min-width: 993px) and (max-width: 1200px)',
        jumbo:   '(min-width: 1201px)'
      }
    });

Note: Media-queries syntax can be used in 'media' object - i.e. mobile: '(max-width: 768px) and (orientation: landscape)'.

You can then query those breakpoints in your controllers, components, routes, and views:

    this.get('media.isMobile'); // => true

### Template use:

    {{#if media.isDesktop}}
      Desktop view!
    {{/if}}

### Bind classes to Views:
Media-query classes can be bound to any view if necessary. For now it is bound to root-level container in embedded_application_view.js.

    Ember.View.extend({
      classNameBindings: ['media.classNames']
    });
