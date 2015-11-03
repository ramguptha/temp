Modern Ember
============

This document describes how the legacy Ember code (1.0RC-pre.2) will be ported to "modern" Ember (1.0+, 1.5.1 at the time of writing).

It is highly recommended that readers understand [the EmberJS Guides](http://emberjs.com/guides/) before reading this document.

Differences Between Legacy and Modern Ember
-------------------------------------------

### Class Initialization

Both legacy and modern Ember allow new instances to be created with an object literal for parameters, but the interpretation of that literal has changed. In legacy, the object literal can be thought of as the parameter for Object.reopen(). In modern, the literal can be thought of as the parameter for Object.setProperties().

The difference is often minor in practice, but nonetheless the difference is fundamental. In legacy, the behaviour of an object can be changed on construction. In modern, one can simply pass properties for it to be initialized with (and trying to modify behaviour will throw).

### Object Lifecycle and Scoping Between Object Instances

In legacy, most application level objects (particularly, controllers) were created on app startup. Controllers could scope each other via the shared "App" object. We placed strong restrictions on that by using require() and some other state chart tricks.

In modern, there is now the notion of an object container, but user level code is discouraged from using directly. Instead, routes have controllerFor(), and controllers have needs / controllers. Most objects are created lazily as required.

### Mapping Between URLs and App State

In legacy, the application structure is completely described by a state chart. Each state in the legacy state chart is referred to as a route, which may or may not be annotated with a portion of a URL path.

In modern, the application structure is very strongly encouraged to be flat. A route can be thought of as a container that brings together a controller, model and view for some particular kind of data or idea. There is a separate router that maps URLs to routes.

### In Modern Ember, the Property Cache is Fragile

Consider the following (flawed) class:

    BrokenClass = Em.Object.extend({
      foo: 0,

      fooPlusOne: function() {
        return this.get('foo') + 1;
      }.property('foo'),

      fooPlusTwo: function() {
        return this.get('fooPlusOne') + 1;
      }.property('foo')
    });

Note that the property dependency does not match what is retrieved in the implementation - foo vs. fooPlusOne.

In theory, they have the same meaning, and this code will work as "expected" in legacy Ember.

In Modern Ember, it _may_ not, though the exact circumstances under which the property cache is not invalidated are still unknown. It works for trivial examples. When porting to modern ember, it did not work for complex dependency chains like Query.uri.

Furthermore, in legacy Ember, a write to fooPlusOne or fooPlusTwo will be ignored.

    // Legacy
    // ======
    works = BrokenClass.create();

    // returns 1
    works.get('fooPlusOne');

    works.set('fooPlusOne', 2);

    // still returns 1
    works.get('fooPlusOne');

In modern Ember, the write will be accepted!

    // Modern
    // ======
    broken = BrokenClass.create();

    // returns 1
    broken.get('fooPlusOne');

    broken.set('fooPlusOne', 2);

    // returns 2!
    broken.get('fooPlusOne');

### Other Minor Changes

Some API calls are stricter in modern (in general, the trend has been towards increased strictness). For example, Enumeration.pushObjects() would accept a single object as a parameter in legacy, and do the right thing. In modern, that will throw.

Design Goals for the Modern Ember Port
--------------------------------------

More than anything, the goal is for our "modern Ember" code to be simple and canonical. This will cost us boilerplate and also subject us to some Ember warts such as a flat global namespace, but the benefit is that anyone familier with Ember should be able to immediately grasp what we're up to. 

Require.js and the Application Namespace
----------------------------------------

In modern ember, as in legacy, there is the temptation to put everything into the application namespace. We avoid this in our legacy implementation by using Require.js for almost all of our dependency management. In modern ember, we will use the application namespace for classes needed by embers built-in dependency resolver _only_. Everything else will still be resolved using require.js.

Design - Package Structure and Routing
--------------------------------------

Every package that will be routable will return a javascript object in the following form:

    return {
      // These properties will become properties of the App (i.e. part of the shared global namespace) via App.reopen()
      appClasses: {
      },

      // Optional: these actions will be merged into the ApplicationRoute.
      appActions: {
      },

      // Optional: this method will be invoked with the application instance when it is ready.
      initialize: function(app) {
      },

      // This method will be invoked by the router while setting up the routing table.
      buildRoutes: function(router) {
      }
    };

See lib/client/packages/platform/ui/lib/application\_base.js, mergePackage(pkg) for the implementation.

Since we are injecting names into a shared global namespace, appClasses names should contain the name of the package as a prefix to help protect against collision.

Similarly for routes added to the routing table.

Example (see cc-reports/main.js in the platform\_55854\_ember-upgrade branch):

    // CcReports
    // =========

    var appClasses = {   
      CcReportsRoute: UI.Route,
      CcReportsController: CcReportsLandingController,
      CcReportsView: Desktop.NavContentPageView,

      // ...

      CcReportsIndexRoute: UI.Route.extend({
        renderTemplate: function() {
          this.render('cc_reports_index', { outlet: 'pageContent' });
        }
      }),

      // ...

      CcReportsShowConfigRoute: UI.Route.extend({
        // ...
      }),
      CcReportsShowConfigController: CcReportConfigItemController,
      CcReportsShowConfigView: CcReportConfigItemRelatedReportResultsView,

      // ...

      CcReportDevicesRoute: UI.Route.extend({
        controllerName: 'cc_reports_show_config'
      }),
      CcReportDevicesView: Desktop.NavContentPageView,
      
      // ...

      CcReportDevicesShowDeviceRoute: UI.Route.extend(),
      CcReportDevicesShowDeviceController: CcReportResultSetRelatedDeviceItemController,
      CcReportDevicesShowDeviceView: CcDevice.DeviceItemSummaryView
    };

    return {
      appClasses: appClasses,

      buildRoutes: function(router) {
        router.resource('cc_reports', { path: '/cc_reports' }, function() {
          this.route('show_config', { path: '/:report_config_id' });
        });

        router.resource('cc_report_devices', { path: '/cc_reports/:report_config_id/devices' }, function() {
          this.route('show_device', { path: '/:device_id' });
        });
      }
    };

In this example, note that even though the URL and IA for devices related to reports is nested, both the namespace and the routes are relatively flat - there's a resource for the page layout, and the rest is dropped in. This is helped by the "controllerName" property available to routes, which allows the CcReportDevicesRoute to just use the report config item controller directly (CcReportsShowConfigController).

Porting Guidelines for the Legacy Statechart
--------------------------------------------

Consider cc-reports as an example (see the example above for source code). cc-reports has the following IA:

- Landing
- Report Config Item
  - Related Device, if applicable

The statechart is as follows:

- Report Configs
  - Landing (/)
  - Report Config Item (/reports)
    - Index (/:reportConfigId)
    - Related Device (/:reportConfigId/devices/:deviceId)

Although the statechart is helpful to look at, the important thing is the IA. In the cc-reports IA, Landing and Report Config Item share the same layout, with a nav pane used for navigation. Related Device also has a nav pane. These "nav pane" layouts become modern ember resources.

*Resources*

- CcReports
- CcReportDevices

Within the CcReports resource, the "Landing" part of the IA becomes the index: CcReportsIndex. The Report Config Item section becomes: CcReportsShowConfig.

Within the CcReportDevices resource, the "Landing" is left unmodified (nothing will link to it), and the Related Device part of the IA becomes: CcReportDevicesShowDevice. Since Related Device has a nav pane populated with the results of the related report, the CcReportDevicesController is really the CcReportConfigItemController, via CcReportDeviceRoute.controllerName.

To sum up, states with nav panes become modern ember resources. Landings become the resource index. Item states become Show.

Design - Modal System
---------------------

### Legacy Implementation

Currently, the modal implemetation lives in the Desktop package (desktop/lib/modal\_manager.js). The ModalManager is available from the App namespace via "modalController". To show a modal dialogue, callers invoke modalController.show(), passing in a modal statechart which will drive the modal content.

Internally, the ModalManager coordinates with 5 layer controllers (desktop/lib/modal\_layer.js), showing new modal content in the "lowest" available modal layer (i.e. the lowest layer that has no other layers on top of it).

The ModalManager instantiates all controllers within the provided statechart, and injects the Layer Controller in as well. The Layer Controller becomes the "router" for the modal content.

The view heirarchy is as follows:

- ApplicationView
  - Modal Frame (desktop/lib/templates/modal\_frame.handlebars)
    - Modal Layer (desktop/lib/templates/modal\_layer.handlebars)
      - Modal Content (i.e. content driven by the modal statechart), usually with either Modal Action Layout (desktop/lib/templates/modal\_action\_layout.handlebars) or Modal Wizard Layout (desktop/lib/templates/modal\_wizard\_layout.handlebars)

### Porting Concerns

The primary porting concern is that the notion of a "modal statechart" does not map well to modern ember. It could be ported, of course, but this violates the intention of sticking to canonical patterns. Thankfully, there are very few places in the code where modal content really needs to be statechart driven.

Modals in our codebase follow one of three patterns:

- Wizard (> 2 "screens"):
  - AM:
    - New Content Wizard
  - CC:
    - New Fixed Group Wizard
    - New Smart Group Wizard
- Action (2 "screens" - data entry and action status):
  - Many!
- Everything else (ColumnChooser, Intro, etc. - 1 "screen")

Given the small number of multi-state Wizards in our codebase, an updated modal design that requires more extensive code changes is acceptable.

### How Discourse Does Modals

[Discourse](http://discourse.org) is often held out as a canonical example of a large, well constructed Ember app. So, it is useful to examine how modals are implemented there.

In Discourse, all routes inherit from Discourse.Route. Discourse.Route includes showModal(), which is completely included below:

    showModal: function(router, name, model) {
      router.controllerFor('modal').set('modalClass', null);
      router.render(name, {into: 'modal', outlet: 'modalBody'});
      var controller = router.controllerFor(name);
      if (controller) {
        if (model) {
          controller.set('model', model);
        }
        if(controller && controller.onShow) {
          controller.onShow();
        }
        controller.set('flashMessage', null);
      }
    }

The modal frame, in turn, is part of the application view, via the render helper in the application template:

    {{render "modal"}}

Shared modal actions, such as "close" are handled by the application route via bubbling.

Points of note:

- Modal content is displayed by Routes, not Controllers.
- Only one modal at a time (we support up to 5, but of course prefer 1 and more than 2 is basically rediculous).
- Very simple! But, supporting multiple layers of modal content is where our complexity is.

### Modern Design

As with the legacy implementation, the modern modal implementation "pretends" to be a router. "Modals" have their own controller and view, but no route. When a modal is shown, the controller is resolved from the application container, and its target set to a "modal layer controller" (whose target in turn is set to the modal container controller). It is then rendered as a new item in a CollectionView, one item per modal layer.

To show modals, all application routes will inherit from UI.Route, which will have "showModal()" just like Discourse.

Controllers for modal content will have "onShow()" invoked when they are displayed, if they implement it. They will have "onClose()" invoked when they are closed, if they implement it.

Design - Modal Actions
----------------------

*Legacy Implementation*

States:

- confirmAction
  - In CC, most modal actions misuse this state to collect input from the user. Or, this state is poorly named. Similarly, AM.
- doAction
  - Perform the related action on state entry, and show the result of it when available.

*Modern Design*

Abandon state chart approach completely, and just have a boolean for "actionStarted". Use handlebars *if* to show either the confirmationView or the inProgressView depending on the state of the boolean.

Design - Modal Wizards
----------------------

Move the state chart into a property of the top level wizard route and inject the top level wizard route into each state as a "router" property on init(). Move all actions into the top level wizard route. Change all "connectOutlets" calls to router.render() instead.

Porting Concerns for Modal Actions
----------------------------------

### Case Study: the "Save As" Action in CcReports

Summary:

- Chose "CcReportsSaveConfigAs" as the base name for "save as" components.
- Added "saveAsReport" action to CcReportsShowConfigRoute:

        saveAsReport: function() {
          this.showModal('cc_reports_save_config_as', this.get('controller.model'));
        }
      
- Added view and controller (modals don't get Routes) to the App namespace. Note that the actionController is now just the controller for the name:

        CcReportsSaveConfigAsView: Desktop.ModalActionView,
        CcReportsSaveConfigAsController: CcReportSaveAsController

- Replaced all instances of "bindAttr" with "bind-attr" in the template, and all bindings to scope with context set to controller instead of view.
- Modified the controller:
  - Replaced init() with initProperties().
  - onSuccessCallback: changed transitionTo() to transitionToRoute().
