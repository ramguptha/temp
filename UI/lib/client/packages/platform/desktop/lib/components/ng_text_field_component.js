define([
  'ember'
  ], function(
    Em
    ) {
  'use strict';

  return Em.Component.extend({
    tagName: 'input',
    attributeBindings: ['accept', 'autocomplete', 'autosave', 'dir', 'formaction', 'formenctype', 'formmethod', 'formnovalidate', 'formtarget', 'height', 'inputmode', 'lang', 'list', 'max', 'min', 'multiple', 'name', 'pattern', 'size', 'step', 'type', 'value', 'width'],
    classNames: ['ember-text-field'],
    type: 'text',

    tPlaceholder: 'shared.placeholders.enterValue'.tr(),

    warningMessage: null,

    value: null,
    input: function(event) {
      this.set('value', event.target.value);
    },

    didInsertElement: function() {
      var self = this, warningMessage = this.get('warningMessage');

      if( !Em.isNone(warningMessage) ) {
        this.$().tipsy({fade: true, title: function(){ return warningMessage;}, trigger: 'focus', gravity: 's', className: 'input-tipsy'});
      }

      this.$().attr('placeholder', self.get('tPlaceholder')).focus();
      this.$().keydown(function(event) {
        // Allow: backspace, delete, tab, escape, enter. (comma is not allowed)
        if ( $.inArray(event.keyCode,[46,8,9,27,13]) !== -1 ||
          // Allow: Ctrl+A
            (event.keyCode == 65 && event.ctrlKey === true) ||
          // Allow: home, end, left, right
            (event.keyCode >= 35 && event.keyCode <= 39)) {
          // let it happen, don't do anything
        } else {
          //the comma key was pressed. Do not let it through.
          if(event.keyCode == 188 && event.shiftKey == false) {
            event.preventDefault();
          } else if(((event.keyCode == 188 && event.shiftKey) || (event.keyCode == 190 && event.shiftKey)) && !Em.isEmpty(self.get('value'))) {
            //do not let < or > chars to be pressed when any other char is already in the box, because it causes error 500 on the server. [bug 56621]
            event.preventDefault();
          } else if (/<|>/.test(self.get('value'))) {
            //if < or > were already entered, prevent any other character from being entered
            event.preventDefault();
          }
        }
      });

      // required for FF and IE to move the cursor to the end of the input for when this components gets re-rendered when the user is typing
      this.$().focus().val('').val(this.get('value'));
    }
  });
});
