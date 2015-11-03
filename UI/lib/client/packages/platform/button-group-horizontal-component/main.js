define([
  'ember',

  'packages/platform/button-group-component',
  'text!./lib/templates/button_group_horizontal.handlebars'
], function(
  Em,

  ButtonGroupComponent,
  template
) {
  'use strict';

  // Button Group Horizontal Component
  // ======================
  //
  // Renders a dynamic group of buttons and menus that are laid out horizontally
  // as opposed to a regular drop down list of options
  //

  // Spec
  // ----

  var ButtonPresentation = ButtonGroupComponent.ButtonPresentation.extend({

    showingChildren: true,

    tooltipName: function() {
      var name = this.get('name');
      return name ? name.toString() : null;
    }.property('name'),

    buttonClass: function() {
      var classNames = Em.A();

      // Top level buttons get the whole thing.
      classNames.addObjects(this.get('component.buttonClassNames'));
      classNames.addObjects(this.get('additionalButtonClassNames'));

      var actionName = this.get('actionName');
      if (!Em.isEmpty(actionName)) {
        classNames.push('is-button-for-' + actionName.dasherize());
      }

      return classNames.join(' ');
    }.property('isChild', 'children.[]', 'actionName', 'component.buttonClassNames', 'additionalButtonClassNames')
  });

  // The Component
  // -------------

  var ButtonGroupHorizontalComponent = ButtonGroupComponent.extend({
    ButtonPresentation: ButtonPresentation,

    layout: Em.Handlebars.compile(template)
  });

  return ButtonGroupHorizontalComponent.reopenClass({
    ButtonPresentation: ButtonPresentation,

    appClasses: {
      ButtonGroupHorizontalComponent: ButtonGroupHorizontalComponent
    }
  });
});
