define([
  'ember-states',
  'ember-responsive',
  'ember-template-compiler',
  'packages/platform/send-ember-action',
  'locale'
], function(
  Ember,
  Responsive,
  templateCompiler,
  sendEmberAction,
  Locale
) {
  'use strict';

  // Ember Overrides
  // ===============
  // 
  // Here are our local customizations of default Ember behaviour.
  //
  // NOTE NOTE NOTE
  // --------------
  //
  // Many of the required packages perform customizations themselves. They are included here to
  // ensure that dependencies resolve in the correct order.

  // Add "disabled" option support to Em.Select.
  Ember.SelectOption.reopen({
    attributeBindings: 'disabled'.w(),
    disabled: Em.computed.oneWay('content.disabled')
  });

  return Ember;
});
