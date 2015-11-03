define([
  'ember',
  'packages/platform/locale',
  'packages/platform/offset-monitor',
  'packages/platform/ui',

  'text!./lib/templates/context_menu.handlebars',
  'text!./lib/templates/option.handlebars'
], function(
  Em,
  Locale,
  OffsetMonitor,
  UI,

  contextMenuTemplate,
  optionTemplate
) {
  'use strict';

  // Context Menu
  // ============
  //
  // Manages a context menu associated with a row.
  //
  // Bindable properties:
  //
  // - content: Describes the menus. An array of objects described next.
  // - menuContext: Menu specs with a contextPath get their action parameters from this object.
  // - menuTarget: The standard Component action binding mechanism isn't suitable for dynamic action names. Instead, dispatch all actions to the target directly. Defaults to _menuContext_.
  // - hideTrigger: Observe this value and hide the menu if it changes.
  // - disabled: If set, disables the entire context menu
  //
  // Elements of the _content_ Array
  // -------------------------------
  //
  // The elements of the array passed to the content property are objects with the following properties:
  //
  // - labelResource: the path to the resource for the menu label.
  // - tooltipResource: the path to the resource for the tooltip.
  // - actionName: the name of the action to invoke when the menu is clicked. The action will be sent to the menuTarget.
  // - contextPath: optional path to a parameter for the action. The path is relative to the menuContext.
  // - disabled: if true, disable the menu option.

  // Options Container
  // -----------------
  //
  // In order to make sure that menus are truly rendered on top of all other content, render menu options into an 
  // application-level container component.

  var optionsContainerComponent = null;

  var ContextMenuOptionsContainerComponent = Em.Component.extend({
    OffsetMonitor: OffsetMonitor,

    classNames: 'is-context-menu-options-container'.w(),
    attributeBindings: 'style'.w(),

    style: function() {
      var offset = this.get('offset');
      return new Em.Handlebars.SafeString('top: ' + offset.top + 'px;' + 'left: ' + offset.left + 'px;');
    }.property('offset'),

    offset: { left: 0, top: 0 },

    layout: Em.Handlebars.compile('{{#if options}}{{context-menu-options options=options}}{{/if}}'),

    offsetMonitor: function() {
      return this.OffsetMonitor.create({
        offsetDidChange: function() {
          var contextMenuComponent = this.objectAt(0).get('component');
          contextMenuComponent.hideMenu();
        },

        offsetDidChangeContext: this
      });
    }.property(),

    ensureElementPositionMonitoring: function() {
      if (!this.get('scheduledElementPositionMonitor')) {
        this.monitorElementPosition();
      }
    },

    options: null,

    showOptions: function(options, offset, monitoredElement) {
      // showOptions() and hideOptions() may be invoked out of order. So, ensure that showOptions() cleans up before
      // doing anything.
      var currentOptions = this.get('options');
      if (currentOptions !== options) {
        this.hideOptions(currentOptions);
        this.set('options', options);
      }

      this.setProperties({
        offset: offset
      });

      this.get('offsetMonitor').monitor(monitoredElement);
    },

    hideOptions: function(options) {
      if (this.get('options') === options) {
        this.setProperties({
          offset: { top: 0, left: 0 }
        });

        this.get('offsetMonitor').stop();

        this.set('options', null);
      }
    },

    init: function() {
      this._super();

      if (optionsContainerComponent) {
        throw ['There may only be one ContextMenuOptionsContainerView at a time', optionsContainerComponent];
      }

      optionsContainerComponent = this;
    },

    destroy: function() {
      optionsContainerComponent = null;

      this._super();
    }
  });

  // Options View
  // ------------
  //
  // When showing menu options, the options view is added to the optionsContainerComponent.
  var ContextMenuOptionsComponent = Em.Component.extend({
    tagName: 'ul',
    classNames: 'is-context-menu-options'.w(),

    layout: Em.Handlebars.compile(optionTemplate),

    options: null
  });

  // Spec
  // ----

  var MenuPresentation = Em.Object.extend({
    contextMenuComponent: null,

    labelResource: null,
    label: Locale.translated({ property: 'labelResource' }),

    tooltipResource: null,
    tooltip: Locale.translated({ property: 'tooltipResource' }),

    actionName: null,
    contextPath: null,
    disabled: false,

    hasTooltip: function(){
      return !Em.isEmpty(this.get('tooltip'));
    }.property(),

    tooltipID: function() {
      return Em.String.dasherize(this.get('label').toString());
    }.property('label'),

    menuClass: function() {
      var classNames = 'is-menu-option btn-dropdown'.w();

      var actionName = this.get('actionName');
      if (!Em.isEmpty(actionName)) {
        classNames.push('is-menu-for-' + actionName.dasherize());
      }

      return classNames.join(' ');
    }.property('actionName')
  });

  var ContextMenuComponent = Em.Component.extend(UI.MenuController.HasOneMenu, {
    MenuPresentation: MenuPresentation,

    classNameBindings: ':is-context-menu showingMenu:down:up'.w(),
    layout: Em.Handlebars.compile(contextMenuTemplate),

    actions: {
      toggleMenu: function() {
        this.toggleMenu();
      },

      menuClick: function(option) {
        var context = option.get('context');
        var contextPath = option.get('contextPath');

        if (contextPath) {
          var menuContext = this.get('menuContext');
          context = ('this' === contextPath) ? menuContext : menuContext.get(contextPath);
        }

        $('.tipsy').remove(); // destroy tooltip;
        this.get('menuTarget').send(option.get('actionName'), context);

        this.hideMenu();
      }
    },

    // Injectable, Required
    content: null,

    // Injectable
    menuContext: Em.computed.oneWay('parentView.context'),

    // Injectable
    menuTarget: Em.computed.oneWay('buttonContext'),

    // Injectable
    hideTrigger: null,

    // Injectable
    disabled: false,

    options: function() {
      var self = this;

      return (this.get('content') || Em.A()).map(function(spec) {
        return self.MenuPresentation.create({
          contextMenuComponent: self,

          labelResource: Em.get(spec, 'labelResource'),
          tooltipResource: Em.get(spec, 'tooltipResource'),
          actionName: Em.get(spec, 'actionName'),
          contextPath: Em.get(spec, 'contextPath'),
          disabled: Em.get(spec, 'disabled')
        });
      });
    }.property('content'),

    showOrHideMenuOnShowingMenuChange: function() {
      var options = this.get('options');

      if (this.get('showingMenu') && 'inDOM' === this.get('_state')) {
        var element = this.$();
        var elementOffset = element.offset();
        var menuOffset = { top: elementOffset.top + 5, left: elementOffset.left + 25 };

        optionsContainerComponent.showOptions(options, menuOffset, element);
      } else {
        optionsContainerComponent.hideOptions(options);
      }
    }.observes('showingMenu').on('init'),

    hideMenuOnHideTriggerChange: function() {
      // Observer
      this.get('hideTrigger');

      this.hideMenu();
    }.observes('hideTrigger').on('init'),

    init: function() {
      this._super();

      if (!optionsContainerComponent) {
        throw ['An optionsContainerComponent must be rendered before instantiating a context menu', this, optionsContainerComponent];
      }
    },

    destroy: function() {
      this.hideMenu();

      var optionsView = this.get('optionsView');
      if (optionsView) {
        optionsView.destroy();
      }

      this._super();
    }
  });

  return ContextMenuComponent.reopenClass({
    appClasses: {
      ContextMenuComponent: ContextMenuComponent,
      ContextMenuOptionsComponent: ContextMenuOptionsComponent,
      ContextMenuOptionsContainerComponent: ContextMenuOptionsContainerComponent
    }
  });
});
