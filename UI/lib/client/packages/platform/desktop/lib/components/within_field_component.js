define([
  'ember',
  'text!../templates/within_field_layout.handlebars'
], function(
  Em,
  template
  ) {
  'use strict';

  return Em.Component.extend({
    tagName: 'div',
    classNames: 'within-operator-block'.w(),
    layout: Em.Handlebars.compile(template),

    isRange: false,
    number1: null,
    number2: null,
    value: null,
    conjunctionText: 'desktop.advancedFilterComponent.betweenAnd'.tr(),
    operatorTypes: null
  });
});
