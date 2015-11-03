$(function () {
  "use strict";

  var socket;
  var transport = 'websocket';

  // We are now ready to cut the request
  var request = {
    url: '/com.absolute.am.webapi/api/push',
    contentType: 'application/json',
    transport: transport,
    reconnectOnServerError: false,
    maxReconnectOnClose: 0,
    fallbackTransport: 'none',
    origin: ''
  };

  request.onOpen = function(response) {
    transport = response.transport;
  };

  request.onMessage = function (response) {
    console.log('Got message ' + response.responseBody);
  };

  socket = atmosphere.subscribe(request);

  $('#goSubscribe').click(function() {
    socket.push(atmosphere.util.stringifyJSON(
    {
      action: "subscribe",
      endpoint: $('#subscribe').val()
    }
    ));
  });
  
  $('#goUnsubscribe').click(function() {
    socket.push(atmosphere.util.stringifyJSON(
    {
      action: "unsubscribe",
      endpoint: $('#unsubscribe').val()
    }
    ));
  });
  
  $('#listAllSubscriptions').click(function() {
    socket.push(atmosphere.util.stringifyJSON(
    {
      action: "list"
    }
    ));
  });
});