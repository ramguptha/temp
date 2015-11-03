define([
  'ember',
  'desktop',
  'packages/platform/ui',
  'query',
  'am-data',
  'guid',
  'am-desktop',

  './lib/namespace',
  './lib/controllers/custom_field_list_controller',
  './lib/controllers/custom_field_item_delete_controller',
  './lib/controllers/custom_field_item_add_edit_controller',

  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  Desktop,
  UI,
  Query,
  AmData,
  Guid,
  AmDesktop,

  AmCustomField,
  AmCustomFieldListController,
  AmCustomFieldDeleteController,
  AmCustomFieldAddEditController,

  strings,
  Locale
) {
  'use strict';


  var appClasses = {

    // Landing - Custom Fields List
    // ------------------------------

    AmCustomFieldListRoute: UI.NoSetupRoute.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        deleteCustomFieldAction: function (selectionsList) {
          var ids = Em.A([]);
          for (var i = 0; i < selectionsList.length; i++) {
            ids.pushObject(selectionsList[i]);
          }

          this.showModal({ name: 'am_custom_field_delete', model: { fieldIds: ids } });
        },
        addCustomFieldAction: function () {
          this.showModal({ name: 'am_custom_field_edit', model: { fieldId: null, currentMode: 'AddMode' } });
        },
        editCustomFieldAction: function (selectionsList) {
          this.showModal({ name: 'am_custom_field_edit', model: { fieldId: selectionsList.objectAt(0), currentMode: 'EditMode' } });
        },
        duplicateCustomFieldAction: function (selectionsList) {
          this.showModal({ name: 'am_custom_field_edit', model: { fieldId: selectionsList.objectAt(0), currentMode: 'DuplicateMode' } });
        }
      },

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);
        controller.clearSelections();
      }
    }),
    AmCustomFieldListController: AmCustomFieldListController,
    AmCustomFieldListView: AmDesktop.AmListView,

    // Modals
    // ------

    // Delete Custom Field
    AmCustomFieldDeleteController: AmCustomFieldDeleteController,
    AmCustomFieldDeleteView: Desktop.ModalActionView,


    // Add or Edit Custom Field
    AmCustomFieldEditController: AmCustomFieldAddEditController,
    AmCustomFieldEditView: Desktop.ModalActionView
  };

  return {
    buildRoutes: function(router) {
      router.route('am_custom_field_list', { path: '/am_custom_fields' });
    },

    initialize: function() {
      AmCustomField.reopen({
        customFieldStore: function () {
          return AmData.get('stores.customFieldStore');
        }.property(),

        customFieldSpec: function () {
          return AmData.get('specs.AmCustomFieldSpec');
        }.property()

      });
    },

    appClasses: appClasses,
    appStrings: strings,

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amCustomField.topNavSpec.administrationTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-gear-1',

      routes: [
        {
          name: 'amCustomField.topNavSpec.customFieldTitle'.tr(),
          path: 'am_custom_field_list'
        }
      ]
    }
  };
});
