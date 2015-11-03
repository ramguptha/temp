/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import com.absolute.am.command.AMServerProtocolSettings;


/**
 * @author dlavin
 *
 */
public class SessionState {

	public static final String LOGON_RESULT_PARAMETERS = "loginResultParameters";
	public static final String ADMIN_UUID = "adminUUID";
	public static final String LOCALE = "locale";
	public static final String LOCALE_DB_SUFFIX = "localeDbSuffix";
	public static final String FILE_UPLOAD_STATUS_PREFIX = "fileUploadStatus";
	public static final String AM_SERVER_PROTOCOL_SETTINGS = "AMServerProtocolSettings";
	public static final String SYNC_SERVICE_SESSION = "syncServiceSession";
	public static final String SESSION_JOB_ID_PREFIX = "JobIdForSession_";
	public static final String SESSION_ID_PREFIX = "_SessionId_";
	public static final String FILTER_BY_ADMIN = "filterByAdmin";

	public static AMServerProtocolSettings getAMServerProtocolSettings(HttpSession session) {
		return (AMServerProtocolSettings)session.getAttribute(AM_SERVER_PROTOCOL_SETTINGS);
	}
	
	public static void setAMServerProtocolSettings(HttpSession session, AMServerProtocolSettings amServerProtocolSettings) {
		session.setAttribute(AM_SERVER_PROTOCOL_SETTINGS, amServerProtocolSettings);
	}	
		
	public static UUID getAdminUUID(HttpSession session) {
		return (UUID)session.getAttribute(ADMIN_UUID);
	}
	
	public static void setAdminUUID(HttpSession session, UUID adminUUID) {
		session.setAttribute(ADMIN_UUID, adminUUID);
	}	
	
	public static String getLocale(HttpSession session) {
		return (String)session.getAttribute(LOCALE);
	}	
	
	public static void setLocale(HttpSession session, String locale) {
		session.setAttribute(LOCALE, locale);
	}
	
	public static String getLocaleDbSuffix(HttpSession session) {
		return (String)session.getAttribute(LOCALE_DB_SUFFIX);
	}
	
	public static void setLocaleDbSuffix(HttpSession session, String localeDbSuffix) {
		session.setAttribute(LOCALE_DB_SUFFIX, localeDbSuffix);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getLogonResultParameters(HttpSession session) {
		return (Map<String, Object>)session.getAttribute(LOGON_RESULT_PARAMETERS);
	}
	
	public static void setLogonResultParameters(HttpSession session, Map<String, Object> logonResultParameters) {
		session.setAttribute(LOGON_RESULT_PARAMETERS, logonResultParameters);
	}
	
	public static FileUploadStatus getFileUploadStatus(HttpSession session, String fileName) {		
		return (FileUploadStatus)session.getAttribute(FILE_UPLOAD_STATUS_PREFIX + fileName);
	}
	
	public static void setFileUploadStatus(
			HttpSession session, 
			String fileName, 
			FileUploadStatus fileUploadStatus) {
		String attributeName = FILE_UPLOAD_STATUS_PREFIX + fileName;
		
		if (null == fileUploadStatus) {
			session.removeAttribute(attributeName);
		} else {
			session.setAttribute(FILE_UPLOAD_STATUS_PREFIX + fileName, fileUploadStatus);	
		}		
	}
	
	/**
	 * Helper method to retrieve all FileUploadStatus objects from the session state.
	 * @param session the session to retrieve the FileUploadStatus objects from.
	 * @return array of FileUploadStatus objects, possibly empty.
	 */
	public static FileUploadStatus[] getAllFileUploadStatusEntries(HttpSession session) {
		ArrayList<FileUploadStatus> retVal = new ArrayList<FileUploadStatus>();
		Enumeration<String> attributeEnumeration = session.getAttributeNames();
		while (attributeEnumeration.hasMoreElements()) {
			String attributeName = attributeEnumeration.nextElement();
			if (attributeName.startsWith(FILE_UPLOAD_STATUS_PREFIX)) {
				retVal.add((FileUploadStatus)session.getAttribute(attributeName));
			}
		}
		return retVal.toArray(new FileUploadStatus[retVal.size()]);		
	}
	
	/**
	 * Get the sync service session token.
	 * @param session
	 * @return the token is an opaque object, possibly null
	 */
	public static Object getSyncServiceSession(HttpSession session) {
		return session.getAttribute(SYNC_SERVICE_SESSION);
	}
	
	/**
	 * Set the sync service session token.
	 * @param session
	 * @param syncServiceSession - an opaque token object
	 */
	public static void setSyncServiceSession(HttpSession session, Object syncServiceSession) {
		if (syncServiceSession == null) {
			session.removeAttribute(SYNC_SERVICE_SESSION);
		} else {
			session.setAttribute(SYNC_SERVICE_SESSION, syncServiceSession);
		}
	}	
	
	public static String[] getJobIds(HttpSession session) {
		String currentSessionId = session.getId();
		ArrayList<String> retVal = new ArrayList<String>();
		Enumeration<String> attributeEnumeration = session.getAttributeNames();
		while (attributeEnumeration.hasMoreElements()) {
			String attributeName = attributeEnumeration.nextElement();
			if (attributeName.startsWith(SESSION_JOB_ID_PREFIX)) {
				String attribute = (String)session.getAttribute(attributeName);
				// the jobId attribute looks like: JobIdForSession_GUID_SessionId_SESSIONID
				// Parse out the session Id and make sure we are only returning
				// jobId's for this session
				String[] substrings = attribute.split(SESSION_ID_PREFIX);
				if (substrings.length == 2) {
					String sessionIdPart = substrings[1];
					if (currentSessionId.compareTo(sessionIdPart) == 0) {
						retVal.add(substrings[0]);
					}
				}
			}
		}
		return retVal.toArray(new String[retVal.size()]);		
	}
	
	public static void setJobId(HttpSession session, String jobId) {
		// the jobId attribute looks like: JobIdForSession_GUID_SessionId_SESSIONID
		String attributeName = SESSION_JOB_ID_PREFIX + jobId;
		String attributeValue = jobId + SESSION_ID_PREFIX + session.getId();		
		session.setAttribute(attributeName, attributeValue);	
	}
	
	public static void setFilterByAdmin(HttpSession session, String value) {
		if (value == null) {
			session.removeAttribute(FILTER_BY_ADMIN);
		} else {
			session.setAttribute(FILTER_BY_ADMIN, value);
		}
	}
	
	public static String getFilterByAdmin(HttpSession session) {
		return (String)session.getAttribute(FILTER_BY_ADMIN);
	}

}
