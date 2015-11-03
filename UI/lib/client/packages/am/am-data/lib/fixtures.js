define([
  'ember',
  'packages/platform/ajax'
], function(Em, Ajax) {
  
  var devices = {
    one: Em.Object.create({ id: 'one', name: 'Device 1', os: 'iOS' }),
    two: Em.Object.create({ id: 'two', name: 'Device 2', os: 'Android' }),
    three: Em.Object.create({ id: 'three', name: 'Device 3', os: 'Android' })
  };

  var content = {
    ten: Em.Object.create({ id: 'ten', name: 'Content 10', category: 'document' }),
    eleven: Em.Object.create({ id: 'eleven', name: 'Content 11', category: 'document' }),
    twelve: Em.Object.create({ id: 'twelve', name: 'Content 12', category: 'video' })
  };

  var categories = {
    all: Em.Object.create({ id: 'all', name: 'All Content' }),
    document: Em.Object.create({ id: 'document', name: 'Word Docs' }),
    video: Em.Object.create({ id: 'video', name: 'Movie' })
  };

  var policies = {
    twenty: Em.Object.create({ id: 'twenty', name: 'Policy 20' }),
    twentyOne: Em.Object.create({ id: 'twentyOne', name: 'Policy 21' })
  };

  var contentDeployments = {
    thirty: Em.Object.create({
      id: 'thirty',
      contentId: 'ten',
      policyId: 'twenty',
      availabilityStart: '9:00 AM',
      availabilityEnd: '5:00 PM'
    }),

    thirtyOne: Em.Object.create({
      id: 'thirtyOne',
      contentId: 'eleven',
      policyId: 'twenty',
      availabilityStart: '10:00 AM',
      availabilityEnd: '6:00 PM'
    }),

    thirtyTwo: Em.Object.create({
      id: 'thirtyTwo',
      contentId: 'eleven',
      policyId: 'twentyOne',
      availabilityStart: '11:00 AM',
      availabilityEnd: '2:00 PM'
    }),

    thirtyThree: Em.Object.create({
      id: 'thirtyThree',
      contentId: 'twelve',
      policyId: 'twentyOne',
      availabilityStart: '12:00 AM',
      availabilityEnd: '3:00 PM'
    })
  };

  var policyDeviceAssignments = {
    forty: Em.Object.create({ id: 'forty', deviceId: 'one', policyId: 'twenty' }),
    fortyOne: Em.Object.create({ id: 'fortyOne', deviceId: 'three', policyId: 'twenty' }),
    fortyTwo: Em.Object.create({ id: 'fortyTwo', deviceId: 'one', policyId: 'twentyOne' })
  };

  return Em.Object.extend({
    
    properties: 'server'.w(),
    server: null,
  
    builtInDeviceGroups: [
      Em.Object.create({
        id: 'all',
        name: 'All Devices',
        devices: [devices.one, devices.two, devices.three]
      }),
      Em.Object.create({
        id: 'ios',
        name: 'All iOS Devices',
        devices: [devices.one]
      }),
      Em.Object.create({
        id: 'android',
        name: 'All Android Devices',
        devices: [devices.two, devices.three]
      })
    ],

    plainDeviceGroups: [
      Em.Object.create({
        id: 'oneAndThree',
        name: 'Devices 1 and 3',
        devices: [devices.one, devices.three]
      })
    ],

    getBuiltInDeviceGroups: function() {
      return this.get('builtInDeviceGroups');
    },

    getBuiltInDeviceGroup: function(id) {
      return this.get('builtInDeviceGroups').find(function(group) { return group.get('id') === id; });
    },

    getPlainDeviceGroups: function() {
      return this.get('plainDeviceGroups');
    },

    getPlainDeviceGroup: function(id) {
      return this.get('plainDeviceGroups').find(function(group) { return group.get('id') === id; });
    },
    
    getDevice: function(id) {
      return devices[id];
    },

    getContentList: function() {
      return [content.ten, content.eleven, content.twelve];
    },

    getContent: function(id) {
      return content[id];
    },
  
    getMobilePolicyList: function() {
      return [policies.twenty, policies.twentyOne];
    },

    getMobilePolicy: function(id) {
      return policies[id];
    }
    
  }); 
});
