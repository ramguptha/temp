/**
 * 
 */
package com.absolute.am.webapi.listeners;

import java.io.File;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.webapi.controllers.FileUploadStatus;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.util.StringUtilities;

/**
 * @author dlavin
 *
 */
public class TempFileCleanerSessionListener implements HttpSessionListener {

    private static Logger m_logger = LoggerFactory.getLogger(TempFileCleanerSessionListener.class.getName());
    
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
		FileUploadStatus[] fileUploadStatuses = SessionState.getAllFileUploadStatusEntries(sessionEvent.getSession());
		m_logger.debug("Cleaning temp files for session [{}]. There are {} files to delete.", sessionEvent.getSession().getId(), fileUploadStatuses.length);
		
		for( int i=0; i<fileUploadStatuses.length; i++){
			String localFilePath = null;
			try {
				localFilePath = fileUploadStatuses[i].getLocalFilePath();
				// delete the local copy of the file.
				File localFile = new File(localFilePath);
				if (localFile.exists()) {
					if (!localFile.delete()) {
						m_logger.debug("Failed to delete temp file [{}].", localFilePath);
					}
				}
			} catch (Exception e) {
				m_logger.debug("Ignoring exception caught when deleting file at end of session. LocalFilePath=" + 
						localFilePath + ". Exception=" + StringUtilities.formatExceptionWithStackTrace(e));
			}
		}
		
	}

}
