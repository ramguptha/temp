package com.absolute.am.webapi.push;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import com.absolute.am.webapi.Application;

@SuppressWarnings("rawtypes")
public class EventListener extends WebSocketEventListenerAdapter {
	
	@Override
    public void onMessage(WebSocketEvent event) {
		AtmosphereResource atmosphereResource = event.webSocket().resource();		
		Application.getPushNotificationsManager().handleMessage(event.message().toString(), atmosphereResource);
    }
    
    @Override
    public void onDisconnect(WebSocketEvent event) {
    	// WARNING: we might have a potential memory leak where broadcasters are assigned to atmosphereResources but are never removed
    	// Possibly try to remove them if it proves to be an issue?
    	AtmosphereResource atmosphereResource = event.webSocket().resource();
    	Application.getPushNotificationsManager().removeUser(atmosphereResource.uuid());
    }
}