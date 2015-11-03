define([
  'ember',
  'locale',
  'ui',
  'text!./lib/templates/button_group.handlebars'
], function(
  Em,
  Locale,
  UI,
  template
) {
  'use strict';

  // Button Group Component
  // ======================
  //
  // Renders a dynamic group of buttons and menus
  //
  // Bindable properties:
  //
  // - content: Describes the buttons and menus. An array of objects described next.
  // - buttonContext: Button specs with a contextPath get their action parameters from this object.
  // - buttonTarget: The standard Component action binding mechanism isn't suitable for dynamic action names. Instead, dispatch all actions to the target directly. Defaults to _buttonContext_.
  // - disabled: Disable all buttons and menus if true.
  //
  // Elements of the _content_ Array
  // -------------------------------
  //
  // The elements of the array passed to the content property are objects with the following properties:
  //
  // - labelResource: the path to the resource for the button label.
  // - tooltipResource: the path to the resource for the tooltip.
  // - iconOnly: if true, does not render a label.
  // - iconClassNames: the class name for the button icon. If not set, there will be no icon.
  // - actionName: the name of the action to invoke when the button is clicked. The action will be sent to the buttonTarget.
  // - contextPath: optional path to a parameter for the action. The path is relative to the buttonContext.
  // - additionalButtonClassNames: optional additional class names for the button.
  // - disabled: if true, disable the button.
  // - children: option array of _content_, turns this button into a menu toggle.

  // Spec
  // ----

  var ButtonPresentation = Em.Object.extend(UI.MenuController.HasOneMenu, {
    component: null,

    labelResource: null,
    label: Locale.translated({ property: 'labelResource' }),

    tooltipResource: null,
    tooltip: Locale.translated({ property: 'tooltipResource' }),

    // DEPRECATED: remove once AM is localized
    name: null,
    labelFinal: function() {
      var label = this.get('label');
      return Em.isNone(label) ? this.get('name') : label;
    }.property('label', 'name'),

    additionalButtonClassNames: Em.A(),
    iconClassNames: Em.A(),

    actionName: null,
    disabled: false,

    isChild: false,

    children: null,
    showingChildren: Em.computed.oneWay('showingMenu'),

    hasChildren: function() {
      return Em.isArray(this.get('children'));
    }.property('children'),

    hasTooltip: function(){
      return !Em.isEmpty(this.get('tooltip'));
    }.property(),

    tooltipID: function() {
      return Em.String.dasherize(this.get('label').toString());
    }.property('label'),

    buttonClass: function() {
      var classNames = Em.A();

      if (this.get('isChild')) {
        // Children get btn-dropdown and the action marker only
        classNames.push('btn-dropdown');
      } else {
        // Top level buttons get the whole thing.
        classNames.addObjects(this.get('component.buttonClassNames'));
        classNames.addObjects(this.get('additionalButtonClassNames'));

        if (!Em.isEmpty(this.get('children'))) {
          classNames.push('is-button-for-dropdown-toggle');

          if (this.get('showingChildren')) {
            classNames.push('down');
          } else {
            classNames.push('up');
          }
        }
      }

      var actionName = this.get('actionName');
      if (!Em.isEmpty(actionName)) {
        classNames.push('is-button-for-' + actionName.dasherize());
      }

      return classNames.join(' ');
    }.property('isChild', 'showingChildren', 'children.[]', 'actionName', 'component.buttonClassNames', 'additionalButtonClassNames'),

    iconClass: function() {
      return this.get('iconClassNames').join(' ');
    }.property('iconClassNames'),

    init: function() {
      'iconClassNames additionalButtonClassNames'.w().forEach(function(name) {
        var value = this.get(name);
        if ('string' === typeof(value)) {
          this.set(name, Em.A(value.split()));
        }
      }, this);
    }
  });

  // The Component
  // -------------

  var ButtonGroupComponent = Em.Component.extend({
    ButtonPresentation: ButtonPresentation,

    layout: Em.Handlebars.compile(template),
    classNames: 'is-button-group'.w(),

    actions: {
      toggleMenu: function(buttonSpec) {
        buttonSpec.toggleMenu();
      },

      buttonClick: function(buttonSpec) {
        var context = buttonSpec.get('context');
        var contextPath = buttonSpec.get('contextPath');
        var buttonContext = this.get('buttonContext');
        var actionName = buttonSpec.get('actionName');

        if (contextPath) {
          context = ('this' === contextPath) ? buttonContext : buttonContext.get(contextPath);
        }

        $('.tipsy').remove(); // destroy tooltip;
        this.get('buttonTarget').send(buttonSpec.get('actionName'), context);
      }
    },

    // Injectable, Required
    content: null,

    // Injectable
    buttonContext: Em.computed.oneWay('parentView.context'),

    // Injectable
    buttonTarget: Em.computed.oneWay('buttonContext'),

    // Shared across all buttons
    buttonClassNames: 'tooltip-s btn btn-small'.w(),

    presentation: function() {
      var self = this;

      var buildButtonPresentation = function(spec, isChild) {
        var names = 'labelResource name tooltipResource iconOnly iconClassNames actionName contextPath context additionalButtonClassNames disabled children'.w();
        var attrs = {};
        names.forEach(function(name) {
          var value = Em.get(spec, name);
          if (undefined !== value) {
            attrs[name] = value;
          }

          name = name + 'Binding';
          value = Em.get(spec, name);
          if (undefined !== value) {
            attrs[name] = value;
          }
        });

        if (attrs.children) {
          attrs.children = Em.A(attrs.children).map(function(childSpec) {
            return buildButtonPresentation(childSpec, true);
          });
        }

        attrs.component = self;
        attrs.isChild = isChild;

        return self.ButtonPresentation.create(attrs);
      };

      var content = this.get('content');
      if (!Em.isEmpty(content)) {
        content = content.map(function(spec) { return buildButtonPresentation(spec, false); });
      }

      return content;
    }.property('content')
  });

  return ButtonGroupComponent.reopenClass({
    ButtonPresentation: ButtonPresentation,

    appClasses: {
      ButtonGroupComponent: ButtonGroupComponent
    }
  });
});
