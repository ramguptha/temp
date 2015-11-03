define([
  'ember',
  'text!./lib/templates/clearable_text_field.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  // Clearable Text Field
  // ====================
  //
  // Displays an "X" button to clear any input when non-empty.
  //
  // Injectable properties:
  //
  // - placeHolder
  // - value
  var ClearableTextFieldComponent = Em.Component.extend({
    layout: Em.Handlebars.compile(template),
    classNames: 'clearable-input'.w(),

    actions: {
      clear: function () {
        sendEmberAction('noteUserActivity');
        this.set('value', null);
      }
    },

    value: null,
    placeholder: null,

    clearButtonStyle: function() {
      // on first run 'value' equals Handlebars.SafeString
      // when x pressed, value is set to null;
      var value = this.get('value');
      var style = 'display:none';

      if (!Em.isEmpty(value) && !Em.isEmpty(value.toString())) {
        style = '';
      }

      return new Em.Handlebars.SafeString(style);
    }.property('value')
  });

  return ClearableTextFieldComponent.reopenClass({
    appClasses: {
      ClearableTextFieldComponent: ClearableTextFieldComponent
    }
  });
});
