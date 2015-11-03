define([
  'ember',
  'help',
  'desktop',
  'am-desktop',
  'query',
  'guid',

  'am-data',
  'packages/platform/advanced-filter',
  'packages/platform/locale-config',

  './smart_policy_filter_controller',

  'text!../templates/smart_policy_set_properties.handlebars',
  '../views/smart_policy_create_smart_filter_view',
  '../views/smart_policy_confirm_devices_view'
], function (
  Em,
  Help,
  Desktop,
  AmDesktop,
  Query,
  Guid,

  AmData,
  AdvancedFilter,
  LocaleConfig,

  SmartPolicyFilterController,

  SmartPolicySetPropertiesTemplate,
  SmartPolicyCreateSmartFilterView,
  ConfirmDevicesForSmartPolicyView
  ) {
  'use strict';

  // New Mobile Smart Policy Controller
  // ==================================
  //
  // Mobile Smart Policies consist of a set of filter criteria which are 'AND'ed or 'OR'ed together.
  return AmDesktop.ModalActionController.extend({
    actions: {
      onConfirmAction: function() {
        this.get('wizard').send('next');
      },

      gotoPreviousStep: function() {
        this.get('wizard').send('prev');
      },

      selectFilterBy: function(selection) {
        this.filterByButtonChanged(selection);
      }
    },

    actionDescription: null,

    advancedFilterViewClassNames: 'show-details-container create-smart-group',
    addModalClass: 'smart-policy-controller-window',

    tPlaceholder: 'amMobilePolicies.modals.createNewSmartPolicy.placeholder'.tr(),
    tNotUniqueNameMessage: 'amMobilePolicies.shared.notUniqueNameMessage'.tr(),
    tEditSmartHeading: 'amMobilePolicies.modals.editSmartPolicy.heading'.tr(),
    tEditProperties: 'amMobilePolicies.modals.editSmartPolicy.stepLabels.properties'.tr(),
    tEditSmartFilter: 'amMobilePolicies.modals.editSmartPolicy.stepLabels.editSmartFilter'.tr(),
    tInProgressMessage: 'shared.modals.inProgressMessage'.tr(),
    tSuccessMessage: 'shared.modals.successMessage'.tr(),
    tErrorMessage: 'shared.modals.errorMessage'.tr(),

    heading: 'amMobilePolicies.modals.createNewSmartPolicy.heading'.tr(),
    headingIconClass: 'icon-smartgroupcreate',
    placeholder: function () {
      return this.get('tPlaceholder').toString();
    }.property(),

    actionWarning: '',
    actionButtonLabel: 'shared.buttons.save'.tr(),

    inProgressMsg: 'amMobilePolicies.modals.createNewSmartPolicy.inProgressMessage'.tr(),
    errorMsg: 'amMobilePolicies.modals.createNewSmartPolicy.errorMessage'.tr(),
    successMsg: 'amMobilePolicies.modals.createNewSmartPolicy.successMessage'.tr(),
    errorMessage: null,

    setPropertiesTxt: 'amMobilePolicies.modals.createNewSmartPolicy.stepLabels.properties'.tr(),
    createSmartFilterTxt: 'amMobilePolicies.modals.createNewSmartPolicy.stepLabels.createSmartFilter'.tr(),
    verifyAndSaveTxt: 'amMobilePolicies.modals.createNewSmartPolicy.stepLabels.verify'.tr(),

    statusMsgDetails: null,
    statusMsgItem: null,

    message: null,

    isNotChanged: true,
    inProgress: false,

    defaultPageDataFilter: null,
    showOkBtn: false,
    nameLengthRemaining: null,
    isConfirmationView: false,
    smartPolicyParamsLock: null,
    newPolicy: true,

    policyNameChangedTimeout: null,

    urlForHelp: null,

    // Views
    // -----
    //
    // One view per wizard 'step'.

    propertiesView: Em.View.extend(Desktop.KeyboardEventsMixin, {
      defaultTemplate: Em.Handlebars.compile(SmartPolicySetPropertiesTemplate),
      layout: Desktop.ModalWizardLayoutTemplate,

      didInsertElement: function() {
        this._super();
        this.$('input').first().focus();

        // terrible, unforgivable HAX
        Em.$('#filterByNone').prop('checked', this.get('controller.filterByNone'));
        Em.$('#filterByApp').prop('checked', this.get('controller.filterByApp'));
        Em.$('#filterByProfile').prop('checked', this.get('controller.filterByProfile'));
      }
    }),

    // The device selection and device confirmation steps use the same view.
    createFilterView: SmartPolicyCreateSmartFilterView,

    confirmationView: ConfirmDevicesForSmartPolicyView,

    // Wizard State
    // ------------
    //
    // This statemanager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'properties',
        controller: this,

        states: {
          properties: Em.State.create({
            enter: function() {
              this.get('parentState.controller').set('selectedOption', null);
            },

            next: function(manager) {
              var controller = this.get('parentState.controller'), filterBy = controller.get('filterBy'), defaultOption;

              if(controller.get('newPolicy') && !controller.get('advancedFilterController.filter.isComplete')) {
                controller.set('advancedFilterController.isActionBtnDisabledForFilters', true);
              }

              if(Em.isNone(controller.get('selectedOption'))) {
                // make sure we have a valid selection on the filter page based on the smart policy type selection
                if(filterBy === 1) {
                  defaultOption = controller.get('allAnyOptions')[0].value;
                } else if(filterBy === 2) {
                  defaultOption = controller.get('allAppOptions')[0].value;
                } else {
                  defaultOption = controller.get('allProfilesOptions')[0].value;
                }

                controller.set('selectedOption', defaultOption);
              }

              manager.transitionTo('createFilterState');
            },

            onChooseParentAction: function(router, evt) {
              router.transitionTo('onChooseParentState');
            },

            onCancelParentAction: function(router, evt) {}
          }),

          createFilterState: Em.State.create({
            next: function(manager) {
              manager.transitionTo('confirmDevicesState');
            },

            prev: function(manager) {
              manager.transitionTo('properties');
            }
          }),

          confirmDevicesState: Em.State.create({
            next: function(manager) {
              manager.transitionTo('status');
            },

            prev: function(manager) {
              manager.transitionTo('createFilterState');
            }
          }),

          'status': Em.State.create({
            enter: function() {
              this.get('parentState.controller').sendActionRequest();
            }
          })
        }
      });
    }.property(),

    showingProperties: function() {
      return 'properties' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingCreateFilterState: function() {
      return 'createFilterState' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingConfirmation: function() {
      return 'confirmDevicesState' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingStatus: function() {
      return 'status' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    initProperties: function() {
      var advancedFilterController = SmartPolicyFilterController.create({
        parentController: this,
        dataStoreSpecBinding: 'parentController.dataStoreSpec',
        filterBinding: 'parentController.advancedFilter'
      });

      // Can't use a dynamic property for advancedFilterController because then the view won't pick it up on
      // initialization. Grr.
      this.setProperties({
        advancedFilterController: advancedFilterController,
        smartPolicyParamsLock: Guid.generate()
      });

      if (!Em.isNone(this.get('model'))) {
        this.set('context', this.get('model'));
      }

      this.setProperties({
        urlForHelp: Help.uri(1025),
        errorMessage: null,
        policyName: null,
        buildFilterReadyFlag: 0
      });

      this.setupForEditing();

      this.get('wizard').transitionTo('properties');
    },

    init: function () {
      this._super();
    },

    oldSmartPolicy: null,

    // Do some setup in case we're editing a smart policy instead of creating a new one
    setupForEditing: function() {
      // not editing a new policy
      if ( Em.isNone(this.get('context')) ) {
        // pre-select the first button
        this.filterByButtonChanged('filterByNone');

        return;
      }

      var policy = this.get('context.policy'), smartPolicyStore = AmData.get('stores.smartPolicyStore'), self = this;

      smartPolicyStore.acquireOne(null, this.get('context.policy.id'),
        function () {
          self.set('oldSmartPolicy', this.get('content.content.data'));

          // filterBy and allAnySelectValue both have observers so the ordering is important here
          var filterType = this.get('content.content.data.filterType');

          if(filterType === 1) {
            self.filterByButtonChanged('filterByNone');
          } else if(filterType === 2) {
            self.filterByButtonChanged('filterByApp');
          } else {
            self.filterByButtonChanged('filterByProfile');
          }

          //self.set('allAnySelectValue', this.get('content.content.data.filterCriteria.Operator'));
          self.set('context.filterCriteria', this.get('content.content.data.filterCriteria'));

          // Set filter
          var filter = self.get('advancedFilterController.filter');
          var newFilter = null;
          var operator = this.get('content.content.data.filterCriteria.Operator');

          // All or Some (AND or OR)
          if(self.get('filterBy') === self.FILTER_BY_OPTIONS.filterByNone) {
            if( operator === 'AND') {
              newFilter = self.setOptions(self, filter, 'AND');
            } else {
              newFilter = self.setOptions(self, filter, 'OR');
            }
          } else {
            if( operator === 'OR') {
              newFilter = self.setOptions(self, filter, 'allAppMissing');
            } else {
              newFilter = self.setOptions(self, filter, 'someAppMissing');
            }
          }

          // Do not remove. Switch Missing or Installed (NOT IN or IN)
          if ( !Em.isNone(this.get('content.content.data.filterCriteria.ContainmentOperator')) ) {
            self.set('missingInstalledSelectValue', this.get('content.content.data.filterCriteria.ContainmentOperator'));
          }

          self.set('advancedFilterController.filter', newFilter);

          // Set control state
          self.setControlState(self, operator);
          self.incrementProperty('buildFilterReadyFlag');
        }, null, false, false
      );

      this.set('advancedFilterController.isActionBtnDisabledForFilters', false);

      this.setProperties({
        policyName: $('<textarea />').html(policy.get('name').toString()).val(),
        heading: this.get('tEditSmartHeading'),
        byNoneBtnDisabled: true,
        byAppBtnDisabled: true,
        byProfileBtnDisabled: true,
        setPropertiesTxt: this.get('tEditProperties'),
        createSmartFilterTxt: this.get('tEditSmartFilter'),
        newPolicy: false,

        urlForHelp: Help.uri(1023),

        inProgressMsg: this.get('tInProgressMessage'),
        successMsg: this.get('tSuccessMessage'),
        errorMsg: this.get('tErrorMessage')
      });
    },

    // Mobile Policy Basic Properties
    // ------------------------------

    // Properties to disable the radio buttons
    byNoneBtnDisabled: false,
    byAppBtnDisabled: false,
    byProfileBtnDisabled: false,

    // Name of the policy.
    policyName: null,

    // Device selection criteria are based on properties of the device, or of related apps, or of related
    // configuration profiles.
    filterBy: null,

    // TODO: Build or integrate a proper, working radio button component instead of working around the brokenness
    // of the third party ember component currently being used.
    FILTER_BY_OPTIONS: {
      filterByNone: 1,
      filterByApp: 2,
      filterByProfile: 3
    },

    filterByNone: true,
    filterByApp: false,
    filterByProfile: false,

    filterByButtonChanged: function(name) {
      var options = this.get('FILTER_BY_OPTIONS');

      this.set(name, true);

      if(name === 'filterByNone') {
        this.setProperties({
          filterByApp: false,
          filterByProfile: false,
          filterBy: options[name]
        });
      } else if(name === 'filterByApp') {
        this.setProperties({
          filterByNone: false,
          filterByProfile: false,
          filterBy: options[name]
        });
      } else {
        this.setProperties({
          filterByNone: false,
          filterByApp: false,
          filterBy: options[name]
        });
      }

      // terrible, unforgivable HAX
      Em.run.later(this, function() {
        Em.$('#filterByNone').prop('checked', this.get('filterByNone'));
        Em.$('#filterByApp').prop('checked', this.get('filterByApp'));
        Em.$('#filterByProfile').prop('checked', this.get('filterByProfile'));
      }, 100);

      this.resetAdvancedFilterController(this.get('advancedFilterController'), options[name]);
    },

    // properties that hold the values of the drop downs
    missingInstalledSelectValue: null,

    isActionBtnDisabled: true,

    checkNameUniqueness: function (policyName, self) {
      if (!Em.isNone(self.get('policyName')) && !Em.isEmpty(self.get('policyName').trim())) {
        var mobilePolicyStore = AmData.get('stores.mobilePolicyStore'), context = self.get('context');

        if ((!Em.isNone(context) && policyName.trim().toUpperCase() != context.policy.name.trim().toUpperCase()) || Em.isNone(context)) {
          mobilePolicyStore.acquire(Guid.generate(),
            { store: mobilePolicyStore, searchAttr: 'name', searchFilter: policyName.trim() },
            function (datasource) {
              for (var i = 0; i < datasource.get('length'); i++) {
                if (datasource.objectAt(i).get('data.name').trim().toUpperCase() === policyName.trim().toUpperCase()) {
                  if (!(!Em.isNone(this.get('context')) && this.get('context.policy.name').trim().toUpperCase() == policyName.trim().toUpperCase() )) {
                    self.set('isActionBtnDisabled', true);
                    self.set('errorMessage', self.get('tNotUniqueNameMessage'));
                  }

                  return;
                }
              }
            }
          );
        }

        self.set('isActionBtnDisabled', false);
        self.set('errorMessage', '');
      }
    },

    onPolicyNameChanged: function () {
      var policyName = this.get('policyName');

      if (Em.isNone(policyName) || Em.isEmpty(policyName.trim())) {
        this.set('isActionBtnDisabled', true);
        this.set('errorMessage', '');
      } else {
        policyName = policyName.trim();
        // Let's not spam the server with requests.
        // This should send a request not more often than once every second.
        if (!Em.isNone(this.get('policyNameChangedTimeout'))) {
          Em.run.cancel(this.get('policyNameChangedTimeout'));
          this.set('policyNameChangedTimeout', null);
        }

        this.set('policyNameChangedTimeout', Em.run.later(this, this.checkNameUniqueness, policyName, this, 100));
      }
    }.observes('policyName'),

    // Device Selection Criteria
    // -------------------------
    advancedFilterController: null,
    advancedFilter: null,
    dataStoreSpec: null,

    warningMessagesForInfoItems: [
        {
          itemGuid: 'F2D366DA-13F3-43EB-8E58-6EB933EA6C9B',
          warningMessage: 'amMobilePolicies.modals.createNewSmartPolicy.englishOnlyWarningMessage',
          condition: function(){
            return LocaleConfig.locale() !== 'en-us';
          }
        }
    ],

    resetAdvancedFilterController: function(advancedFilterController, filterBy) {
      var adHocStore = AmData.get('stores.adHocStore'), endPoint = null, self = this;

      switch (filterBy) {
        case this.FILTER_BY_OPTIONS.filterByNone:
          endPoint = 'api/infoitems/filtercriteria/smartpolicies/bymobiledevices';
          break;
        case this.FILTER_BY_OPTIONS.filterByApp:
          endPoint = 'api/infoitems/filtercriteria/smartpolicies/byinstalledapplications';
          break;
        case this.FILTER_BY_OPTIONS.filterByProfile:
          endPoint = 'api/infoitems/filtercriteria/smartpolicies/byinstalledconfigprofiles';
          break;
        case null:
          return;
        default:
          throw ['Unknown filterBy value', filterBy];
      }

      advancedFilterController.set('filter', AdvancedFilter.AndFilter.create());
      this.set('isActionBtnDisabled', true);

      adHocStore.acquire(endPoint,
      function(data, textStatus, jqXHR, spec) {
        spec.resource.forEach(function(item) {
          // Currently only string types can have warning messages
          if( item.type === String ) {
            self.get('warningMessagesForInfoItems').forEach(function (warningItem) {
              if( warningItem.itemGuid === item.guid && warningItem.condition() ) {
                item.warningMessage = warningItem.warningMessage;
              }
            });
          }
        });

        self.set('dataStoreSpec', spec);
        self.onPolicyNameChanged();

        advancedFilterController.set('dataStoreSpec', spec);
        self.incrementProperty('buildFilterReadyFlag');
      });
    },

    // we need multiple AJAX calls to finish before we may run buildFilter()
    // this is the counter for those
    buildFilterReadyFlag: 0,

    readyToEdit: function() {
      if(this.get('buildFilterReadyFlag') === 2) {
        this.buildFilter();
      }
    }.observes('buildFilterReadyFlag'),

    tAllAppMissing: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.allAppMissing'.tr(),
    tAllAppInstalled: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.allAppInstalled'.tr(),
    tSomeAppMissing: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.someAppMissing'.tr(),
    tSomeAppInstalled: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.someAppInstalled'.tr(),
    tAllProfilesMissing: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.allProfilesMissing'.tr(),
    tAllProfilesInstalled: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.allProfilesInstalled'.tr(),
    tSomeProfilesMissing: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.someProfilesMissing'.tr(),
    tSomeProfilesInstalled: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionAppAndProfilesOptions.someProfilesInstalled'.tr(),

    // Match ANY or match ALL of the filter criteria
    tOptionAll: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionOptions.all'.tr(),
    tOptionAny: 'amMobilePolicies.modals.createNewSmartPolicy.conditionSelectionOptions.any'.tr(),

    allAppOptions: function(){
      return Em.A([
        Em.Object.create({ name: this.get('tAllAppMissing'), value: 'allAppMissing' }),
        Em.Object.create({ name: this.get('tAllAppInstalled'), value: 'allAppInstalled' }),
        Em.Object.create( { name: this.get('tSomeAppMissing'), value: 'someAppMissing' }),
        Em.Object.create({ name: this.get('tSomeAppInstalled'), value: 'someAppInstalled' })
      ]);
    }.property(),

    allProfilesOptions: function(){
      return Em.A([
        Em.Object.create({ name: this.get('tAllProfilesMissing'), value: 'allProfilesMissing' }),
        Em.Object.create({ name: this.get('tAllProfilesInstalled'), value: 'allProfilesInstalled' }),
        Em.Object.create({ name: this.get('tSomeProfilesMissing'), value: 'someProfilesMissing' }),
        Em.Object.create({ name: this.get('tSomeProfilesInstalled'), value: 'someProfilesInstalled' })
      ]);
    }.property(),

    allAnyOptions: function(){
      return Em.A([
        Em.Object.create({ name: this.get('tOptionAll'), value: 'AND' }),
        Em.Object.create({ name: this.get('tOptionAny'), value: 'OR' })
      ]);
    }.property(),

    allProfilesOptionsSelectedName: function() {
      var self = this;

      return this.get('allProfilesOptions').find(function(element) {
        return element.value === self.get('selectedOption');
      }).name;
    }.property('allProfilesOptions', 'selectedOption'),

    allAppsOptionsSelectedName: function() {
      var self = this;

      return this.get('allAppOptions').find(function(element) {
        return element.value === self.get('selectedOption');
      }).name;
    }.property('allAppOptions', 'selectedOption'),

    allAnyOptionsSelectedName: function() {
      var self = this;

      return this.get('allAnyOptions').find(function(element) {
        return element.value === self.get('selectedOption');
      }).name;
    }.property('allAnyOptions', 'selectedOption'),

    selectedOption: null,

    onSelectedOptionChanged: function() {
      if(!Em.isNone(this.get('selectedOption'))) {
        var newFilter = this.setOptions(this, this.get('advancedFilterController.filter'), this.get('selectedOption'));
        this.set('advancedFilterController.filter', newFilter);
      }
    }.observes('selectedOption'),

    setOptions: function(self, filter, value) {
      var newFilter = null;
      switch (value) {
        case 'allAppMissing':
        case 'allProfilesMissing':
          newFilter = AdvancedFilter.AndFilter.create({ operands: filter.get('operands') });
          self.set('missingInstalledSelectValue', 'NOT IN');
          break;
        case 'allAppInstalled':
        case 'allProfilesInstalled':
          newFilter = AdvancedFilter.AndFilter.create({ operands: filter.get('operands') });
          self.set('missingInstalledSelectValue', 'IN');
          break;
        case 'someAppMissing':
        case 'someProfilesMissing':
          newFilter = AdvancedFilter.OrFilter.create({ operands: filter.get('operands') });
          self.set('missingInstalledSelectValue', 'NOT IN');
          break;
        case 'someAppInstalled':
        case 'someProfilesInstalled':
          newFilter = AdvancedFilter.OrFilter.create({ operands: filter.get('operands') });
          self.set('missingInstalledSelectValue', 'IN');
          break;
        case 'AND':
          newFilter = AdvancedFilter.AndFilter.create({ operands: filter.get('operands') });
          break;
        case 'OR':
          newFilter = AdvancedFilter.OrFilter.create({ operands: filter.get('operands') });
          break;
        default:
          throw ['Unknown filter type specified', value];
          break;
      }
      return newFilter;
    },

    setControlState:  function (self, operator) {
      var filterBy = self.get('filterBy');
      switch (filterBy) {
        case self.FILTER_BY_OPTIONS.filterByNone:
          if( operator === 'AND') {
            self.set('selectedOption', 'AND');
          } else {
            self.set('selectedOption', 'OR');
          }
          break;
        case self.FILTER_BY_OPTIONS.filterByApp:
          switch (self.get('missingInstalledSelectValue')) {
            case 'NOT IN':
              if( operator === 'AND') {
                self.set('selectedOption', 'someAppMissing');
              } else {
                self.set('selectedOption', 'allAppMissing');
              }
              break;
            case 'IN':
              if( operator === 'AND') {
                self.set('selectedOption', 'allAppInstalled');
              } else {
                self.set('selectedOption', 'someAppInstalled');
              }
              break;
          }
          break;
        case self.FILTER_BY_OPTIONS.filterByProfile:
          switch (self.get('missingInstalledSelectValue')) {
            case 'NOT IN':
              if( operator === 'AND') {
                self.set('selectedOption', 'someProfilesMissing');
              } else {
                self.set('selectedOption', 'allProfilesMissing');
              }
              break;
            case 'IN':
              if( operator === 'AND') {
                self.set('selectedOption', 'allProfilesInstalled');
              } else {
                self.set('selectedOption', 'someProfilesInstalled');
              }
              break;
          }
          break;
      }
    },

    // Build filters when editing an existing smart policy
    buildFilter: function() {
      if ( !this.get('newPolicy') && Em.isEmpty(this.get('advancedFilterController.filter.operands')) ) { // only if we're building filters from a retrieved smart policy
        var self = this;
        this.set('advancedFilterController.filter.operands', Em.A());

        AmData.filterSerialization.serializeToFilter(this.get('context.filterCriteria'), this.get('advancedFilterController')).forEach(function(item) {
          self.get('advancedFilterController.filter.operands').addObject(item);
        });
      }
    },

    buildAction: function () {
      this.set('urlForHelp', null);

      if (this.get('newPolicy')) {
        return AmData.get('actions.AmMobilePolicyCreateSmartPolicyAction').create({
          name: this.get('policyName'),
          filterType: this.get('filterBy'),
          smartPolicyUserEditableFilter: AmData.filterSerialization.serializeToObj(
            this.get('advancedFilter'),
            this.get('dataStoreSpec'),
            this.get('filterBy'),
            this.get('missingInstalledSelectValue'),
            this.get('selectedOption')
          )
        });
      } else { // editing an existing policy
        return AmData.get('actions.AmMobilePolicyUpdateSmartPolicyAction').create({
          oldPolicy: this.get('oldSmartPolicy'),
          newPolicyName:this.get('policyName'),
          newPolicyFilters: AmData.filterSerialization.serializeToObj(
            this.get('advancedFilter'),
            this.get('dataStoreSpec'),
            this.get('filterBy'),
            this.get('missingInstalledSelectValue'),
            this.get('selectedOption')
          )
        });
      }
    }
  });
});
