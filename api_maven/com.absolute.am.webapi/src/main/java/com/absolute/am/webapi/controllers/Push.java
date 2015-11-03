package com.absolute.am.webapi.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.atmosphere.annotation.Suspend;

import com.absolute.am.webapi.push.EventListener;

/**
 * <h3>User API</h3>
 * <p>This API is used to manage the push notification ( aka live data updates ) via asynchronous WebSocket communication.</p>
 * 
 * @author ephilippov
 *
 */
@Path("/push")
public class Push{
	    
	/**
	 * <p>Start a WebSocket connection. Any subsequent asynchnronous communication is handled by the com.absolute.am.webapi.push classes.</p>
	 */
    @GET
    @Suspend(listeners = {EventListener.class})
    // Called on a new ( usually websocket ) connection
    public Response connect(){
    	return Response.ok().build();
    }
    
    @POST
    // Called on new messages from the client
    // This function needs to be here ( even if it's empty ) to avoid needless error spam in log
    public void message(){}
}