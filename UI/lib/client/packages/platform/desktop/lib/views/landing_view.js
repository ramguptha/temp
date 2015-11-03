define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.View.extend({
    layout: Em.Handlebars.compile('<div class="landing-wrapper">{{yield}}</div>')
  });
});
