define([
  'ember',
  'jquery',
  'jqueryipaddress',

  'text!../templates/ip_field.handlebars'
], function(
  Em,
  $,
  jqueryipaddress,

  template
  ) {
  'use strict';

  return Em.Component.extend({
    layout: Em.Handlebars.compile(template),

    tagName: 'div',
    classNames: 'ip_container'.w(),
    conjunctionText: 'desktop.advancedFilterComponent.ipAddressTo'.tr(),

    skipOctetsObservers: false,
    isRange: false,
    octet1: null,
    octet2: null,
    octet3: null,
    octet4: null,
    octet5: null,
    octet6: null,
    octet7: null,
    octet8: null,
    fromIp: null,
    toIp: null,

    // Parent component some times needs to force resetting the IP in case the all octets are not filled and validated
    forceIpDataUpdate: Em.computed.oneWay('targetObject.forceIpDataUpdate'),

    init: function() {
      this._super();

      // Fire observers
      this.get('forceIpDataUpdate');
    },

    didInsertElement: function() {
      Em.$('.ip').ipaddress();

      this.updateOctets();
    },

    updateOctets: function() {
      var i = 1, self = this,
        fromIp = this.get('fromIp'),
        toIp = this.get('toIp');

      this.set('skipOctetsObservers', true);
      if(!Em.isEmpty(fromIp)) {
        fromIp.split('.').forEach(function (item) {
          self.set('octet' + i++, item);
        });
      }

      if(!Em.isEmpty(toIp)) {
        toIp.split('.').forEach(function(item) {
          self.set('octet' + i++, item);
        });
      }
      this.set('skipOctetsObservers', false);
    },

    onFromIpOctetChange: function() {
      if(!this.get('skipOctetsObservers')) {
        if( !Em.isEmpty(this.get('octet1')) && !Em.isEmpty(this.get('octet2')) && !Em.isEmpty(this.get('octet3')) && !Em.isEmpty(this.get('octet4')) ) {
          this.set('fromIp', this.get('octet1') + '.' + this.get('octet2') + '.' + this.get('octet3') + '.' + this.get('octet4'));
        } else {
          this.set('fromIp', '');//intentionally '' instead of null as onFromIpChange does a isNone() check
        }
      }
    }.observes('octet1', 'octet2', 'octet3', 'octet4'),

    onToIpOctetChange: function() {
      if(!this.get('skipOctetsObservers')) {
        if (!Em.isEmpty(this.get('octet5')) && !Em.isEmpty(this.get('octet6')) && !Em.isEmpty(this.get('octet7')) && !Em.isEmpty(this.get('octet8'))) {
          this.set('toIp', this.get('octet5') + '.' + this.get('octet6') + '.' + this.get('octet7') + '.' + this.get('octet8'));
        } else {
          this.set('toIp', '');//intentionally '' instead of null as onToIpChange does a isNone() check
        }
      }
    }.observes('octet5', 'octet6', 'octet7', 'octet8'),

    onFromIpChange: function() {
      if (Em.isNone(this.get('fromIp'))) {
        this.setProperties({
          octet1: null,
          octet2: null,
          octet3: null,
          octet4: null
        });
      } else {
        this.updateOctets();
      }
    }.observes('fromIp'),

    onToIpChange: function() {
      if( Em.isNone(this.get('toIp')) ) {
        this.setProperties({
          octet5: null,
          octet6: null,
          octet7: null,
          octet8: null
        });
      } else {
        this.updateOctets();
      }
    }.observes('toIp'),

    forceResetFromIp: function() {
      if (this.get('forceIpDataUpdate')) {
        this.set('fromIp', null);
      }
    }.observes('forceIpDataUpdate')
  });
});
