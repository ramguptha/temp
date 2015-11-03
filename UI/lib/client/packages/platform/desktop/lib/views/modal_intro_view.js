define([
//  'jquery',
  'ember',
  'slider',
  '../layouts/modal_intro_layout',
  'logger'
], function(
//    $,
    Em,
    $,
    ModalSlideshowLayout,
    logger
    ) {
  'use strict';

  return Em.View.extend({
    layout: ModalSlideshowLayout,

    width: function() {
      return this.get('controller.slideWidth');
    }.property('controller.slideWidth'),

    height: function() {
      return this.get('controller.slideHeight');
    }.property('controller.slideHeight'),

    didInsertElement: function() {
      this._super();
      var self = this;

      $('.intro-slides').bjqs({
        width:        self.get('width'),
        height:       self.get('height'),
        animtype:     'slide',
        responsive:   true,
        automatic:    true,
        animduration: 450, // how fast the animation are
        animspeed:    15000, // the delay between each slide
        usecaptions:  false,
        nexttext:     '',   // text/html inside next UI element
        prevtext:     ''
      });
    },

    willDestroyElement: function() {
      this._super();
      // TODO: shutdown slideshow engine
    }
  });
});