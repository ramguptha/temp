define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Mixin.create({
    layout: Em.Handlebars.compile('<div class="page-view">{{yield}}</div>'),
    classNames: 'fill-height'
  });
});
