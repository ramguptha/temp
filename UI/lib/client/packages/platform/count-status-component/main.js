define([
  'ember',
  'locale',
  'text!./lib/templates/count_status.handlebars'
], function(
  Em,
  Locale,
  template
) {
  'use strict';

  var CountStatusComponent = Em.Component.extend({
    actions: {
      showError: function(error) {
        this.sendAction('showErrorDetail', { error: error });
      },

      retryLoad: function() {
        var counter = this.get('counter');
        if (counter) {
          counter.count();
        }
      }
    },

    layout: Em.Handlebars.compile(template),

    counter: null,

    // Strings
    // -------

    // Try Again
    rTryAgain: 'shared.buttons.tryAgain',
    tTryAgain: Locale.translated({ property: 'rTryAgain' }),

    // Show Error Detail
    rShowErrorDetail: 'shared.errors.showErrorDetail',
    tShowErrorDetail: Locale.translated({ property: 'rShowErrorDetail' }),

    // Unable to Load Data
    rUnableLoadData: 'shared.errors.unableLoadData',
    tUnableLoadData: Locale.translated({ property: 'rUnableLoadData' })

  });

  return CountStatusComponent.reopenClass({
    appClasses: {
      CountStatusComponent: CountStatusComponent
    }
  });
});
