define([
  'ember',
  'atmosphere',
  'packages/am/am-session'
], function(
  Em,
  Atmosphere,
  AmSession
) {
  'use strict';

  return Em.Object.create({
    initialize: function() {
      if( AmSession.getEnableLiveDataUpdates() === true ) {
        var config = this.get('pushNotificationRequestConfig'),
          subscribedEndpointStore = this.get('subscribedEndpointStore'),
          self = this;

        this.set('minTimeBetweenRefreshes', AmSession.getDelayLiveDataUpdates());

        config.onMessage = function(response) {
          var jsonResponse = JSON.parse(response.responseBody),
            endPointRefreshScheduled = self.get('endPointRefreshScheduled'),
            minTimeBetweenRefreshes = self.get('minTimeBetweenRefreshes');

          // if we got a message with updated = true then reload the jqGrid table by invaliding the data store
          if (jsonResponse.updated && !endPointRefreshScheduled[jsonResponse.endpoint]) {
            setTimeout(function(){
              subscribedEndpointStore[jsonResponse.endpoint].invalidate();
              endPointRefreshScheduled[jsonResponse.endpoint] = false;
            }, minTimeBetweenRefreshes);

            endPointRefreshScheduled[jsonResponse.endpoint] = true;
          }
        };

        config.onOpen = function() {
          self.set('isSocketActive', true);
        };

        this.set('pushNotificationSocket', Atmosphere.subscribe(config));
      }
    },

    minTimeBetweenRefreshes: null,

    // a map of booleans for each endpoint representing whether a refresh has been scheduled for it or not
    endPointRefreshScheduled: {},

    subscribedEndpointStore: {},

    pushNotificationSocket: null,

    isSocketActive: false,

    pushNotificationRequestConfig: {
      url: '/com.absolute.am.webapi/api/push',
      contentType: 'application/json',
      transport: 'websocket',
      reconnectOnServerError: false,
      maxReconnectOnClose: 0,
      fallbackTransport: 'none'
    },

    subscribe: function(endpoint, store) {
      if( this.get('isSocketActive') && !(endpoint in this.get('subscribedEndpointStore')) ) {
        this.get('subscribedEndpointStore')[endpoint] = store;

        this.get('pushNotificationSocket').push(Atmosphere.util.stringifyJSON(
          {
            action: "subscribe",
            endpoint: endpoint
          }
        ));
      }
    }
  });
});