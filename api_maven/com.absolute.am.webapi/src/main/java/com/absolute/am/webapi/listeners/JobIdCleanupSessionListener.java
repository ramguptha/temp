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
public class JobIdCleanupSessionListener implements HttpSessionListener {

    private static Logger m_logger = LoggerFactory.getLogger(JobIdCleanupSessionListener.class.getName());
    
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		String[] jobIds = SessionState.getJobIds(sessionEvent.getSession());
		m_logger.debug("Removing jobIds for session [{}]. There are {} jobIds to delete.", sessionEvent.getSession().getId(), jobIds);
		
		for(String jobId : jobIds){
			Application.getJobStatusMgr().deleteJobStatusDetails(jobId);
		}
		
	}

}
