/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.ISyncService;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;

/**
 * <h3>Sync Service Endpoints</h3>
 * <p>A collection of endpoints to be used by the sync-service. Unless something has gone terribly wrong, a 200 OK response should be returned by all of these.</p>
 * @author dlavin
 *
 */
@Path ("/syncnotify")
@Produces(MediaType.APPLICATION_JSON )
public class SyncNotify {
	@Context ServletContext sc;
    private static Logger m_logger = LoggerFactory.getLogger(SyncNotify.class.getName());
    
    //English-only as this controller is not for the UI
    private static final String m_Base = ResourceUtilities.NON_LOCALIZABLE_BASE;
    private static final String m_Locale = ResourceUtilities.DEFAULT_LOCALE;
    
    /**
     * <p>Notification that a synchronization activity has started for a session.</p>
     * 
     * @param bodyStream
     * @return
     * @throws UnsupportedEncodingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
	@Path ("/syncstarted")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response PostSyncStarted(
			InputStream bodyStream) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		
		PropertyList syncStartedEvent = PropertyList.fromInputStream(bodyStream);
		if (syncStartedEvent == null ||
				syncStartedEvent != null && syncStartedEvent.isEmpty()) {
			throw new BadRequestException("SYNCSTARTEVENT_IS_EMPTY", null, m_Locale, m_Base);
		}			

		try {
			ISyncService syncService = Application.getSyncService();
			syncService.notifySyncStarted(syncStartedEvent);
		} catch (Exception e) {
			throw new BadRequestException("SYNCSTARTEVENT_COULD_NOT_BE_PROCESSED", null, m_Locale, m_Base, "Exception", e);
		}		
		
		return Response.ok().build();
	}
	
	/**
	 * <p>Notification that a synchronization activity has completed for a session.</p>
	 *
	 * @param bodyStream - the specifics of the event ( ex. which tables were updated? )
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Path ("/synccompleted")
	@POST
	// Not sure what the correct type is for @Consumes is but it's not MediaType.APPLICATION_FORM_URLENCODED
	public Response PostSyncCompleted(InputStream bodyStream) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		
		PropertyList syncCompletedEvent = PropertyList.fromInputStream(bodyStream);
		if (syncCompletedEvent == null ||
				syncCompletedEvent != null && syncCompletedEvent.isEmpty()) {
			throw new BadRequestException("SYNCSTARTEVENT_IS_EMPTY", null, m_Locale, m_Base);
		}			

		try {
			ISyncService syncService = Application.getSyncService();
			syncService.notifySyncCompleted(syncCompletedEvent);
		} catch (Exception e) {
			throw new BadRequestException("SYNCSTARTEVENT_COULD_NOT_BE_PROCESSED", null, m_Locale, m_Base, "Exception", e);
		}				
		
		Object[] arrTables = PropertyList.getElementAs(syncCompletedEvent, "SyncCompletedEvent/UpdatedTables");
		if ( arrTables != null ){	
			Application.getPushNotificationsManager().tablesUpdated(Arrays.copyOf(arrTables, arrTables.length, String[].class));
		}
		
		m_logger.debug("sync services synccompleted received.");

		return Response.ok().build();
	}	

	/**
	 * <p>The SyncService has sent a notification that it has been restarted. A startSync() should be requested for all
	 * current sync sessions.</p> 
	 *
	 * @return
	 */
	@Path ("/startup")
	@POST
	public Response postStartup() {
		m_logger.debug("sync services startup received.");
		ISyncService syncService = Application.getSyncService();
		syncService.notifyStartup();
		
		return Response.ok().build();
	}
	
	/**
	 * <p>The SyncService has been shutdown. All current sync services are disabled.</p>
	 * 
	 * @return
	 */
	@Path ("/shutdown")
	@POST
	public Response postShutdown() {
		
		m_logger.debug("sync services shutdown received.");
		ISyncService syncService = Application.getSyncService();
		syncService.notifyShutdown();
		
		return Response.ok().build();
	}	
}
