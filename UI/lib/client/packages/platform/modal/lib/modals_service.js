define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Modals Service
  // ==============
  //
  // Owns modal display.
  return Em.Service.extend({
    ModalLayerActionHandler: Em.Controller.extend({
      modals: null,
      modalLayerSpec: null,

      actions: {
        closeModal: function() {
          this.get('modalLayerSpec').close();
        }
      },

      target: function() {
        var target = null;

        var container = this.get('modals').container;
        var applicationController = container.lookup('controller:application');
        if (applicationController) {
          var currentRoute = container.lookup('route:' + applicationController.get('currentRouteName'));

          if (currentRoute) {
            target = currentRoute;
          }
        }

        return target;
      }.property().volatile()
    }),

    ModalLayerSpec: Em.Object.extend({
      // The parent controller
      modals: null,

      // The name passed to showModal()
      name: null,

      // View class to render in the modal layer
      viewName: null,

      // Controller instance for the modal layer
      controller: null,

      // Model to back the controller
      model: null,

      actionHandler: function() {
        var modals = this.get('modals');
        return modals.ModalLayerActionHandler.create({
          modals: modals,
          modalLayerSpec: this
        });
      }.property(),

      // Are we the top modal being rendered?
      isTop: function() {
        return this.get('modals.topModal') === this;
      }.property('modals.topModal'),

      zIndexBase: null,

      backdropStyle: function() {
        return new Em.Handlebars.SafeString('z-index:' + Number(this.get('zIndexBase')));
      }.property('zIndexBase'),

      modalStyle: function() {
        return new Em.Handlebars.SafeString('z-index:' + Number(this.get('zIndexBase') + 1));
      }.property('zIndexBase'),

      close: function() {
        var controller = this.get('controller');

        if (controller.onCloseModal) {
          controller.onCloseModal();
        }

        this.get('modals').removeModal(this);
      }
    }),

    modalLayerSpecs: function() {
      return Em.A();
    }.property(),

    topModal: Em.computed.oneWay('modalLayerSpecs.lastObject'),

    removeModal: function(modalLayerSpec) {
      this.get('modalLayerSpecs').removeObject(modalLayerSpec);
    },

    // The first modal is assigned zIndexBase (Select 2 menu z-index is 9999)
    zIndexBase: 2000,

    // Appended modals are assigned the zIndexBase of the previous top modal plus zIndexModalIncrement
    zIndexModalIncrement: 100,

    // Options for showModal:
    //
    // - name: from Route.HasShowModal.showModal, the name of the controller and optionally the view. Passed to the 
    //   ModalLayerSpec, but otherwise ignored.
    // - controller: controller instance to back the modal
    // - viewName: view for the modal
    // - model: model instance to hand to controller
    showModal: function(options) {
      var controller = options.controller;
      var modalLayerSpecs = this.get('modalLayerSpecs');

      if (modalLayerSpecs.find(function(spec) { return spec.get('controller') === controller; })) {
        throw ['Cannot use the same controller to back two visible modals', options];
      }

      var zIndex = this.get('zIndexBase');
      if (this.get('modalLayerSpecs.length') > 0) {
        zIndex = this.get('modalLayerSpecs.lastObject.zIndexBase') + this.get('zIndexModalIncrement');
      }

      var spec = this.ModalLayerSpec.create({
        modals: this,
        name: options.name,
        viewName: options.viewName,
        controller: controller,
        model: options.model,
        zIndexBase: zIndex
      });

      controller.set('target', spec.get('actionHandler'));
      controller.set('model', options.model);

      modalLayerSpecs.pushObject(spec);

      if (controller.onShowModal) {
        controller.onShowModal(options.model);
      }
    },

    // Return true if controller specified by options is currently backing a visible modal.
    isShowingModal: function(options) {
      return this.get('modalLayerSpecs').some(function(spec) {
        return spec.get('controller') === options.controller;
      });
    }
  });
});
