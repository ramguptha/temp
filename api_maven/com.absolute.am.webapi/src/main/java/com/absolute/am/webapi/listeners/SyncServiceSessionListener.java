/**
 * 
 */
package com.absolute.am.webapi.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.controllers.SessionState;

/**
 * @author dlavin
 *
 */
public class SyncServiceSessionListener implements HttpSessionListener {

    private static Logger m_logger = LoggerFactory.getLogger(SyncServiceSessionListener.class.getName());
    private static final String PERSIST_SYNC_SESSION = "com.absolute.am.webapi.listeners.SyncServiceSessionListener.PersistSyncSession";
    
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		Object syncServiceSessionToken = SessionState.getSyncServiceSession(sessionEvent.getSession());
		
		if (syncServiceSessionToken != null && !Application.getRuntimeProperty(PERSIST_SYNC_SESSION).equals("true")) {
			m_logger.debug("Stopping sync service session for token:{}.", syncServiceSessionToken);
			
			Application.getSyncService().stopSync(syncServiceSessionToken);
			SessionState.setSyncServiceSession(sessionEvent.getSession(), null);
			
			m_logger.debug("Sync service session stopped for token:{}.", syncServiceSessionToken);
		};
	}
}
