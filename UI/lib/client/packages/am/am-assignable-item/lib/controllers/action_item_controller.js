define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'guid',
  'formatter',
  'locale',
  'am-data',

  'packages/platform/version-util',
  'packages/platform/child-controller',

  '../namespace',
  './action_item_related_mobile_policies_controller',
  'text!../templates/content_item_button_block.handlebars',
  './action_list_mixin'
], function (
  Em,
  Help,
  UI,
  Desktop,
  AmDesktop,
  Guid,
  Formatter,
  Locale,
  AmData,

  VersionUtil,
  ChildController,

  AmAssignableItem,
  AmActionItemRelatedMobilePoliciesController,
  buttonBlockTemplate,
  getActionListMixin
) {
  'use strict';

  return Em.Controller.extend(getActionListMixin,{
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    amAssignableListActionsController: Em.inject.controller('amAssignableListActions'),

    navTitle: 'amAssignableItem.assignableActionsDetailsPage.navigationPane.title'.tr(),
    tDecimal: 'amData.customFieldsFormatAsStore.decimal'.tr(),
    tDecimalNoSeparators: 'amData.customFieldsFormatAsStore.decimalNoSeparators'.tr(),
    tBytes: 'amData.customFieldsFormatAsStore.bytes'.tr(),

    urlForHelp: Help.uri(1073),
    id: null,
    lock: Guid.property(),
    activeTab: null,

    tabItemView: Desktop.TabItemView,
    buttonBlockView: AmDesktop.AmNavTabPageView.ButtonBlockView.extend({
      defaultTemplate: Em.Handlebars.compile(buttonBlockTemplate)
    }),

    snapContainerContentClass: 'snap-container-content',
    snapContainerIconPath: function() {
      return '../packages/platform/desktop/img/32-Commands.png';
    }.property(),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController.amAssignableListActionsController'),
      dataStore: function () {
        return AmAssignableItem.get('assignedActionsStore');
      }.property()
    }),

    relatedMobilePoliciesController: null,

    navSizeController: Em.inject.controller('amAssignableNavSize'),
    amActionsListController: function () {
      return AmAssignableItem.get('amActionsListController');
    }.property(),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.id'
      });
    }.property(),

    breadcrumb: function() {
      return UI.Breadcrumb.create({
        parentBreadcrumb: this.get('amAssignableListActionsController.breadcrumb'),
        path: 'am_assignable_list.actions',

        titleResource: 'amAssignableItem.assignableActionsDetailsPage.title',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property('amAssignableListActionsController.breadcrumb'),

    tabList: function () {
      return [
        Em.Object.create({
          labelResource: 'amAssignableItem.assignableActionsDetailsPage.tabList.details',
          item: 'details'
        }),
        Em.Object.create({
          labelResource: 'amAssignableItem.assignableActionsDetailsPage.tabList.policies',
          item: 'mobilePolicies'
        })
      ];
    }.property(),

    getContext: function() {
      var data = this.get('model.data'), action = this.getActionsList().findBy('type', data.typeEnum);

      return {
        actionId: this.get('id'),
        actionName: action.actionName,
        name: data.name,
        label: Locale.renderGlobals(action.labelResource),
        typeEnum: data.typeEnum,
        osPlatformEnum: data.osPlatformEnum,
        data: data,
        view: action.view
      };
    },

    availableDynamicPropertiesMapping: [
      Em.Object.create({name: 'VPPAccountUniqueID', translation: 'amAssignableItem.modals.actionProperties.vppAccount',
          formattedValue: function(propertyList){
            var result, promise= new Em.RSVP.Promise(function(resolve){
              AmData.get('stores.vppAccountsStore').acquire(null, { searchFilter: propertyList.VPPAccountUniqueID }, function(dataSource) {
                // should only have a single row for the provided GUID and we retrieve its name
                resolve({value: dataSource.get('content')[0].get('name')});
              });
            });

            result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
              promise: promise
            });

            return result;
          }}
      ),
      Em.Object.create({name: 'MessageText', translation: 'amAssignableItem.modals.actionProperties.messageText'}),
      Em.Object.create({name: 'EmailTo', translation: 'amAssignableItem.modals.actionProperties.emailTo'}),
      Em.Object.create({name: 'EmailCC', translation: 'amAssignableItem.modals.actionProperties.emailCc'}),
      Em.Object.create({name: 'EmailSubject', translation: 'amAssignableItem.modals.actionProperties.emailSubject'}),
      Em.Object.create({name: 'EmailMessageText', translation: 'amAssignableItem.modals.actionProperties.messageText'}),
      Em.Object.create({name: 'SMSMessageText', translation: 'amAssignableItem.modals.actionProperties.messageText'}),
      Em.Object.create({name: 'SMSPhonenumber', translation: 'amAssignableItem.modals.actionProperties.phoneNumber'}),
      Em.Object.create({name: 'VoiceRoaming', translation: 'amAssignableItem.modals.actionProperties.voiceRoaming',
        formattedValue: function(propertyList) {
          var roaming = propertyList.VoiceRoaming;
          if(roaming === 1) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.on').toString();
          } else if (roaming === 0) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.off').toString();
          } else {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.leaveAsIs').toString();
          }
        }}),
      Em.Object.create({name: 'DataRoaming', translation: 'amAssignableItem.modals.actionProperties.dataRoaming',
        formattedValue: function(propertyList) {
          var roaming = propertyList.DataRoaming;
          if(roaming === 1) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.on').toString();
          } else if (roaming === 0) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.off').toString();
          } else {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.leaveAsIs').toString();
          }
        }}),
      Em.Object.create({name: 'ChangeActivationLock', translation: 'amAssignableItem.modals.actionProperties.activationLock',
        formattedValue: function(propertyList) {
          if(propertyList.ChangeActivationLock === 1) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.allowed').toString();
          } else {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.disallowed').toString();
          }
        }}),
      Em.Object.create({condition: function(presentationContent){return presentationContent.get('typeEnum') === 9;}, translation: 'amAssignableItem.modals.actionProperties.wallpaperOptions',
        formattedValue: function(propertyList){
          var result = [];

          if(propertyList.ApplyToLockScreen === true) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.lockScreen').toString());
          }
          if(propertyList.ApplyToHomeScreen === true) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.homeScreen').toString());
          }

          return result.join(', ');
        }
      }),
      Em.Object.create({condition: function(presentationContent){return presentationContent.get('typeEnum') === 9;}, translation: 'amAssignableItem.modals.actionProperties.image',
        formattedValue: function(propertyList, controller) {
          var result, promise= new Em.RSVP.Promise(function(resolve){
            var queryContext = { context: { actionId: controller.get('id') }};

            AmData.get('stores.actionWallpaperStore').acquire(null, queryContext, function(dataSource) {
              // should only have a single row for the provided GUID and we retrieve its name
              resolve({value: 'background-image: url(' + dataSource.get('content')[0].get('content.data.wallpaper') + ')', isImage: true});
            });
          });

          result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
            promise: promise
          });

          return result;
        }}),
      Em.Object.create({condition: function(presentationContent){return presentationContent.get('typeEnum') === 9;}, translation: 'amAssignableItem.modals.actionProperties.imageDimensions',
        formattedValue: function(propertyList, controller) {
          var result, promise= new Em.RSVP.Promise(function(resolve){
            var queryContext = { context: { actionId: controller.get('id') }};

            AmData.get('stores.actionWallpaperStore').acquire(null, queryContext, function(dataSource) {
              var img = Em.$('<img src="' + dataSource.get('content')[0].get('content.data.wallpaper') + '">')[0];
              // should only have a single row for the provided GUID and we retrieve its name
              Em.run.later(this, function(){
                resolve({value: img.naturalWidth + ' x ' +  img.naturalHeight});
              }, 500);
            });
          });

          result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
            promise: promise
          });

          return result;
        }}),
      Em.Object.create({name: 'DeviceName', translation: 'amAssignableItem.modals.actionProperties.deviceName'}),
      Em.Object.create({name: 'AttentionModeEnabled', translation: 'amAssignableItem.modals.actionProperties.attentionMode',
        formattedValue: function(propertyList){
          if(propertyList.AttentionModeEnabled === true) {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.allowed').toString();
          } else {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.disallowed').toString();
          }
        }}),
      Em.Object.create({
        condition: function(presentationContent){
            return $.inArray(presentationContent.get('typeEnum'),
                   [AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID]) > -1;
          },
        translation: 'amAssignableItem.modals.actionProperties.registerOptions',
        formattedValue: function(propertyList){
          var result =[];

          if(propertyList.SendInviteViaAbsoluteApps ||
            propertyList.SendInviteViaEmail ||
            propertyList.SendInviteViaMDM ||
            propertyList.SendInviteViaSMS ||
            propertyList.SendInviteViaWebClip) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.registerAndInvite').toString());
          } else {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.registerOnly').toString());
          }

          return result.join(', ');
        }}),
      Em.Object.create({
        hidden: function(presentationContent){
          return $.inArray(presentationContent.get('typeEnum'),[AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID, AmData.specs.AmActionsSpec.SEND_VPP_INVITATION_ID]) > -1 &&
                !presentationContent.get('propertyList.SendInviteViaAbsoluteApps') &&
                !presentationContent.get('propertyList.SendInviteViaEmail') &&
                !presentationContent.get('propertyList.SendInviteViaMDM') &&
                !presentationContent.get('propertyList.SendInviteViaSMS') &&
                !presentationContent.get('propertyList.SendInviteViaWebClip');
        },
        condition: function(presentationContent){
          return $.inArray(presentationContent.get('typeEnum'), [AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID, AmData.specs.AmActionsSpec.SEND_VPP_INVITATION_ID]) > -1;
        },
        translation: 'amAssignableItem.modals.actionProperties.sendInvitation',
        formattedValue: function(propertyList){
          var result =[];

          if(propertyList.SendInviteViaAbsoluteApps) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.absoluteMessage').toString());
          }
          if(propertyList.SendInviteViaEmail) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.email').toString());
          }
          if(propertyList.SendInviteViaMDM) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.mdm').toString());
          }
          if(propertyList.SendInviteViaSMS) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.sms').toString());
          }
          if(propertyList.SendInviteViaWebClip) {
            result.push(Locale.renderGlobals('amAssignableItem.modals.actionProperties.webClip').toString());
          }

          return result.join(', ');
        }}),
      Em.Object.create({hidden: function(presentationContent){
        return presentationContent.get('propertyList.AttentionModeEnabled') === false;
      },
        name: 'LockScreenMessage',
        translation: 'amAssignableItem.modals.actionProperties.text'}),
      Em.Object.create({
        hidden: function(presentationContent){
          return $.inArray(presentationContent.get('typeEnum'),[AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID, AmData.specs.AmActionsSpec.SEND_VPP_INVITATION_ID]) > -1 &&
                 !presentationContent.get('propertyList.SendInviteViaEmail');
        },
        name: 'InviteSubject',
        translation: 'amAssignableItem.modals.actionProperties.subject'
      }),
      Em.Object.create({
        hidden: function(presentationContent){
          return $.inArray(presentationContent.get('typeEnum'),[AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID, AmData.specs.AmActionsSpec.SEND_VPP_INVITATION_ID]) > -1 &&
                !presentationContent.get('propertyList.SendInviteViaEmail') &&
                !presentationContent.get('propertyList.SendInviteViaAbsoluteApps');
        },
        name: 'InviteMessage',
        translation: 'amAssignableItem.modals.actionProperties.messageText'
      }),
      Em.Object.create({
        hidden: function(presentationContent){
          return $.inArray(presentationContent.get('typeEnum'),[AmData.specs.AmActionsSpec.REGISTER_USER_IN_VPP_ID, AmData.specs.AmActionsSpec.SEND_VPP_INVITATION_ID]) > -1 &&
                 !presentationContent.get('propertyList.SendInviteViaSMS');
        },
        name: 'InviteSMSMessage',
        translation: 'amAssignableItem.modals.actionProperties.smsText'
      }),
      Em.Object.create({name: 'PayloadIdentifier', translation: 'amAssignableItem.modals.actionProperties.profile',
          formattedValue: function(propertyList){
            var result, promise= new Em.RSVP.Promise(function(resolve){
              AmData.get('stores.configurationProfileStore').acquire(null, { searchFilter: propertyList.PayloadIdentifier }, function(dataSource) {
                // should only have a single row for the provided GUID and we retrieve its name
                var content = dataSource.get('content');
                resolve({ value: !Em.isEmpty(content) ? content[0].get('name') : null });
              });
            });

            result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
              promise: promise
            });

            return result;
          }}
      ),
      Em.Object.create({name: 'DataType', translation: 'amAssignableItem.modals.actionProperties.customField',
        formattedValue: function(propertyList, controller) {
          // 'Number' is a special case
          if(propertyList.DataType !== 2) {
            return propertyList.Name + ' ( ' + propertyList.DataTypeString + ' )';
          } else {
            var result, promise = new Em.RSVP.Promise(function(resolve){
              AmData.get('stores.customFieldStore').acquire(null, { context: { customFieldId: propertyList.FieldID }}, function (dataSource) {
                var displayTypeNum = arguments[0].get('content')[0].get('content.data.displayTypeNumber'), displayType;
                switch (displayTypeNum) {
                  // decimal
                  case 1:
                    displayType = controller.get('tDecimal');
                    break;
                  // decimalNoSeparators
                  case 2:
                    displayType = controller.get('tDecimalNoSeparators');
                    break;
                  // bytes
                  case 3:
                    displayType = controller.get('tBytes');
                    break;
                }
                resolve({value: propertyList.Name + ' ( ' + displayType + ' )'});
              });
            });

            result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
              promise: promise
            });

            return result;
          }
        }
      }),
      Em.Object.create({name: 'RemoveValue', translation: 'amAssignableItem.modals.actionProperties.dataValue',
        formattedValue: function(propertyList) {
          if(propertyList.RemoveValue === 0) {
            var value = propertyList.DataValue;

            if(propertyList.DataType !== 2) {
              if (propertyList.DataType === 6) { //'IP Address'
                value = Formatter.formatIPv4Address(value);
              } else if (propertyList.DataType === 4) { //'Date'
                value = Formatter.formatShortDateTime(new Date(value));
              } else if (propertyList.DataType === 5) { //'File Version'
                value = VersionUtil.formatIntToFileVersion(propertyList.DataValueHigh32, propertyList.DataValueLow32);
              }

              return Locale.renderGlobals('amAssignableItem.modals.actionProperties.setValue').toString() + ' ' + value;
            } else {
              var result, promise = new Em.RSVP.Promise(function(resolve){
                AmData.get('stores.customFieldStore').acquire(null, { context: { customFieldId: propertyList.FieldID }}, function (dataSource) {
                  var displayTypeNum = arguments[0].get('content')[0].get('content.data.displayTypeNumber');
                  switch (displayTypeNum) {
                    // decimal
                    case 1:
                      value = Formatter.formatDecimal(Number(value));
                      break;
                    // bytes
                    case 3:
                      value = Formatter.formatBytes(Number(value));
                      break;
                  }
                  resolve({value: Locale.renderGlobals('amAssignableItem.modals.actionProperties.setValue').toString() + ' ' + value});
                });
              });

              result = Em.ObjectProxy.extend(Em.PromiseProxyMixin).create({
                promise: promise
              });

              return result;
            }
          } else {
            return Locale.renderGlobals('amAssignableItem.modals.actionProperties.removeValue').toString();
          }
        }
      })
    ],

    isOneRecord: function () {
      return this.get('dynamicProperties.length') === 1;
    }.property('dynamicProperties'),

    dynamicProperties: function() {
      var result = [], presentationContent = this.get('model.data'), self = this;

      if(presentationContent && presentationContent.propertyList) {
        this.get('availableDynamicPropertiesMapping').forEach(function(prop) {
          // Skip hidden property
         if(typeof prop.hidden === 'undefined' || !prop.hidden(presentationContent)) {
           if (prop.name && presentationContent.propertyList.hasOwnProperty(prop.name)) {
             if (prop.formattedValue) {
               var formattedValue = prop.formattedValue(presentationContent.propertyList, self);
               if (formattedValue instanceof Em.ObjectProxy) {
                 prop.set('proxied', true);
               } else {
                 prop.set('proxied', false);
               }
               prop.set('value', formattedValue);
             } else {
               prop.set('value', presentationContent.get('propertyList.' + prop.name));
             }
             prop.set('title', Locale.renderGlobals(prop.get('translation')));

             result.push(prop);
           } else if (prop.condition && prop.condition(presentationContent)) {
             prop.set('title', Locale.renderGlobals(prop.get('translation')));
             var formattedValue = prop.formattedValue(presentationContent.propertyList, self);

             if (formattedValue instanceof Em.ObjectProxy) {
               prop.set('proxied', true);
             } else {
               prop.set('proxied', false);
             }

             prop.set('value', formattedValue);

             result.push(prop);
           }
         }
        });
      }

      return result;
    }.property('model.data.propertyList'),


    init: function () {
      this._super();
      this.setProperties({
        lock: Guid.generate(),
        relatedMobilePoliciesController: AmActionItemRelatedMobilePoliciesController.create({ parentController: this })
      });
    },

    forceUpdate: function(id) {
      if (!Em.isNone(id)) {
        Em.run.later(this, function () {
          this.loadActionItem(id, true);
        }, 1000);
      }
    },

    loadActionItem: function (id, force) {
      this.set('id', id);
      this.set('model', AmAssignableItem.get('assignedActionsStore').acquireOne(this.get('lock'), id, null, null, Em.isNone(force) ? false : force, false));
    }
  });
});
