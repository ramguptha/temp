define([
  'ember',
  'packages/platform/cookie'
], function(
  Em,
  Cookie
) {
  'use strict';

  return Em.Controller.extend( {

    // TODO: Not rendered!
    title: null,

    // TODO: Not rendered!
    summary: null,

    modalWindowClass: "modal-slideshow-window",

    slidePaths: [],
    slideWidth: null,
    slideHeight: null,

    skipIntro: false,
    alwaysSkipIntro: false,
    skipCookieName: null,
    SECS_FOR_COOKIE: 3650 * 24 * 60 * 60,

    init: function() {
      this._super();
      var session = sessionStorage.getItem(this.get('skipCookieName'));
      var cookie = Cookie.read(this.get('skipCookieName'));

      // skip Intro during current session
      if (session) {
        this.set('skipIntro', true);
      }

      // skip Intro if cookie set to true
      if (cookie) {
        var alwaysSkipIntro = false;

        try {
          alwaysSkipIntro = JSON.parse(cookie);
        } catch(e) {}

        if (alwaysSkipIntro) {
          this.setProperties({
            alwaysSkipIntro: alwaysSkipIntro,
            skipIntro: alwaysSkipIntro
          });
        }
      }
    },

    alwaysSkipIntroDidChange: function() {
      Cookie.write(this.get('skipCookieName'), JSON.stringify(this.get('alwaysSkipIntro')), {
        durationInSeconds: this.get('SECS_FOR_COOKIE'),
        path: '/'
      });
    }.observes('alwaysSkipIntro'),

    onShowModal: function() {
      this.set('skipIntro', true);
      sessionStorage.setItem(this.get('skipCookieName'), 'true'); // set sessionStorage to skip Intro during current session
    }
  });
});
