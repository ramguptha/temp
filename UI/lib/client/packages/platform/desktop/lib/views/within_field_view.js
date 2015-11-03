define([
  'ember',
  'text!../templates/within_field.handlebars'
], function(
  Em,
  template
  ) {
  'use strict';

  return Em.View.extend({
    tagName: 'div',
    classNames: 'within-operator-block'.w(),
    defaultTemplate: Em.Handlebars.compile(template),

    isRange: false,
    number1: null,
    number2: null,
    value: null,
    conjunctionText: 'desktop.advancedFilterComponent.betweenAnd'.tr(),
    operatorTypes: null
  });
});
