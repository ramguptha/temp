/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.webapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.ssp.controllers.DefaultServerInfo;
import com.absolute.am.webapi.push.Manager;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;

/**
 * @author dlavin
 *
 */
public class Application extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory.getLogger(Application.class.getName());

	/**
	 * The version of the WebAPI application.
	 */
	public static final String WEB_API_VERSION = "1.7";
	
    /**
     * Key to identify session id in the MDC.
     */
    private static String MDC_SESSION_ID = "SessionId";

    /**
     * INI key name for DAL classname.
     */
	public static final String INI_DAL_CLASSNAME = "com.absolute.am.webapi.Application.DalClassName";
	
	/**
	 * INI key name for path to trusted certificates.
	 */
	public static final String INI_PATH_TO_TRUSTED_CERTS = "com.absolute.am.webapi.Application.pathToTrustedCerts";

	/**
	 * INI key name for enabling/disabling websocket push notifications
	 */
	public static final String INI_ENABLE_LIVE_DATA_UPDATES  = "com.absolute.webapi.Application.enableLiveDataUpdates";

	/**
	 * INI key name for delaying websocket push notifications by x milliseconds
	 */
	public static final String INI_DELAY_LIVE_DATA_UPDATES = "com.absolute.webapi.Application.delayLiveDataUpdates";
	
	/**
	 * A list of runtime properties configured via web.xml. This is a singleton.
	 */
	private static Properties m_runtimeProperties;
    
	/*
	 * The SyncService manager singleton.
	 */
	private static ISyncService m_syncService;

	private static Manager m_pushNotificationsManager;
	
	// Hold the map of all instances of dal for each DB
	private static HashMap<String, IDal> m_dal = new HashMap<String, IDal>();
	
	/*
	 * The JobStatus singleton.
	 */
	private static IJobStatus m_jobStatus;

	private static final String PROP_VIEW_CONFIG_FOLDER= "sqlitedal.viewConfigFolder";
	private static final String PLIST_FILE_INFO_ITEMS_ENUMS = "InfoItemEnumerations.xml";
	private static final String AGENT_PLATFORM_ENUM_PATH = "Enumerations/enum_AgentPlatform/Values";
	
	// Run time property names ( added within Login.java ) for platform enumerations
	public static final String AGENT_PLATFORM = "AGENT_PLATFORM";
	public static final String AGENT_PLATFORM_ANY = "AGENT_PLATFORM.ANY";
	public static final String AGENT_PLATFORM_MAC = "AGENT_PLATFORM.MAC_OS_X";
	public static final String AGENT_PLATFORM_WINDOWS = "AGENT_PLATFORM.WINDOWS";
	public static final String AGENT_PLATFORM_LINUX = "AGENT_PLATFORM.LINUX";
	public static final String AGENT_PLATFORM_IOS = "AGENT_PLATFORM.IOS";
	public static final String AGENT_PLATFORM_ANDROID = "AGENT_PLATFORM.ANDROID";
	public static final String AGENT_PLATFORM_WINDOWS_PHONE = "AGENT_PLATFORM.WINDOWS_PHONE";
	
	/**
	 * Initializes any global data/components required by the WebAPI application.
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
			
		m_logger.debug("Application.init() called.");
				
		// Initialize runtime properties.
		m_runtimeProperties = new Properties();
		Enumeration<String> initParamNames = config.getServletContext().getInitParameterNames();
		AtmosphereFramework framework = (AtmosphereFramework) config.getServletContext().getAttribute("APIServlet");
		
		while (initParamNames.hasMoreElements()) {
			String name = initParamNames.nextElement();
			String value = config.getServletContext().getInitParameter(name);
			m_runtimeProperties.put(name, value);
			m_logger.debug("initParam [" + name + "] = [" + value + "]");
		}
		m_runtimeProperties.put(DalBase.PROP_LOADABLE_EXTENSIONS_FILE, config.getServletContext().getRealPath("/WEB-INF/lib/ABTSQLiteExtension"));
		
		// Initialize the sync service manager
		m_syncService = new SyncServiceManager();
		
		// Initialize the push notifications manager
		m_pushNotificationsManager = new Manager(framework);
		
		// Initialize the Job Status manager
		m_jobStatus = new JobStatusManager();

		DefaultServerInfo.initDefaultServerAndPort();
		initRuntimePropertiesFromViewConfig();
	}
		
	/**
	 * Get an instance of the DAL.
	 * @param sc The servlet context, used to access INI parameters from the web.xml.
	 * @param noSync Whether we need to worry about the sync service session for this dal.
	 * @return an instance of the DAL, or null.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static IDal getDal (HttpSession httpSession, boolean noSync) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		Object session = SessionState.getSyncServiceSession(httpSession);
		String syncedDatabaseFolder;
		
		// SSP login does not create a sync service session, hence this workaround
		if( session == null ){
			syncedDatabaseFolder = "";
		} else {
	        syncedDatabaseFolder = getSyncService().getSyncedDatabaseFolder(session);
		}
		
        if(m_dal.get(syncedDatabaseFolder) == null){
    		String dalClassName = Application.getRuntimeProperty(INI_DAL_CLASSNAME);
    		if (null == dalClassName ) {
    			throw new IllegalArgumentException(ResourceUtilities.getUnlocalizableString("SETTING_IS_REQUIRED", INI_DAL_CLASSNAME));
    		}
    		
    		MDC.put("dalClassName", dalClassName);
    		Class<?> dalClass = Class.forName(dalClassName);
    		IDal dal = (IDal) dalClass.newInstance();
    		MDC.remove("dalClassName");

    		Properties newRuntimeProperties = (Properties) Application.cloneRuntimeProperties();
    		
    		// This will override any value that happen to be there already.
    		if( !noSync ){
    			newRuntimeProperties.put(DalBase.PROP_DATABASES_FOLDER, syncedDatabaseFolder);
    		}
    		
    		dal.initialize(newRuntimeProperties, noSync);
    		m_dal.put(syncedDatabaseFolder, dal);
        }

		return m_dal.get(syncedDatabaseFolder);
	}
	
	public static IDal getDal(HttpSession httpSession) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return getDal(httpSession, false);
	}
	
	/**
	 * Add the session id to the MDC.
	 * @param sessionId
	 */
	public static void putSessionIdInMDC(String sessionId) {
		MDC.put(MDC_SESSION_ID, sessionId.substring(0, 4));
	}
	
	/**
	 * Remove the session id from the MDC.
	 */
	public static void removeSessionIdFromMDC() {
		MDC.remove(MDC_SESSION_ID);
	}

	/**
	 * Get the sync service instance
	 * @return
	 */
	public static ISyncService getSyncService(){
		if (null == m_syncService) {
			throw new IllegalStateException(ResourceUtilities.getUnlocalizableString("CALLED_BEFORE_INIT", "getSyncService()"));
		}
		return m_syncService;
	}
	
	/**
	 * Get the push notifications manager instance
	 * @return
	 */
	public static Manager getPushNotificationsManager(){
		if (null == m_pushNotificationsManager) {
			throw new IllegalStateException(ResourceUtilities.getUnlocalizableString("CALLED_BEFORE_INIT", "getPushNotificationsManager()"));
		}
		return m_pushNotificationsManager;
	}
	
	/**
	 * Get the Job status manager instance
	 * @return
	 */
	public static IJobStatus getJobStatusMgr() {
		if (m_jobStatus == null) {
			throw new IllegalStateException(ResourceUtilities.getUnlocalizableString("CALLED_BEFORE_INIT", "getJobStatusMgr()"));
		}
		return m_jobStatus;
	}

	
	/**
	 * Get access to the runtime properties for the webapp.
	 * @return Properties map.
	 */
	public static Properties getRuntimeProperties() {
		
		if (m_runtimeProperties == null) {
			throw new IllegalStateException(ResourceUtilities.getUnlocalizableString("CALLED_BEFORE_INIT", "getRuntimeProperties()"));
		}
		
		return m_runtimeProperties;
	}
	
	/**
	 * Helper method to clone the runtime properties.
	 * @return
	 */
	public static Properties cloneRuntimeProperties(){
		
		Properties originalProperties = getRuntimeProperties();
		Properties clone = new Properties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			originalProperties.store(baos, "");
			baos.flush();
			clone.load(new ByteArrayInputStream(baos.toByteArray()));
		} catch (IOException e) {
			throw new RuntimeException(ResourceUtilities.getUnlocalizableString("FAILED_TO_CLONE_PROPERTIES"), e);
		}
		
		if (clone.size() != originalProperties.size()) {
			throw new RuntimeException(ResourceUtilities.getUnlocalizableString("FAILED_TO_CLONE_PROPERTIES_DEBUG", Integer.toString(clone.size()), Integer.toString(originalProperties.size())));
		}
		
		return clone;
	}
		
	/**
	 * Get a runtime property by name.
	 * @param propertyName The property to search for.
	 * @return the value of the property or null if none exists.
	 */
	public static String getRuntimeProperty(String propertyName) {
		
		Object returnValue = getRuntimeProperties().get(propertyName);
		if (null == returnValue) {
			return null;
		}
		
		return returnValue.toString();
	}

	/**
	 * Set a runtime property by name.
	 * @param propertyName The property to set.
	 * @param propertyValue The value of the property.
	 */
	public static void setRuntimeProperty(String propertyName, String propertyValue) {
		getRuntimeProperties().setProperty(propertyName, propertyValue);		
	}
	
	/**
	 * Helper method to get a runtime property and convert it to a short. 
	 * @param propertyName The name of the property to search for.
	 * @return a short value. This throws a runtime exception if the property is not found.
	 */
	public static short getRuntimePropertyAsShort(String propertyName) {
		
		String value = getRuntimeProperty(propertyName);
		short retVal = 0;
		try {
			retVal = Short.parseShort(value);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException(ResourceUtilities.getUnlocalizableString("FAILED_TO_CONVERT_VALUE_TO_NUMBER", value, propertyName));
		}
		
		return retVal;
	}
	
	/**
	 * Initialize runtime properties that are to be read from the plist files found under view_config
	 */
	private void initRuntimePropertiesFromViewConfig() {
		
		String enumsPlistLocation = m_runtimeProperties.getProperty(PROP_VIEW_CONFIG_FOLDER) + "/" + PLIST_FILE_INFO_ITEMS_ENUMS;
		try {
			PropertyList enums = PropertyList.fromInputStream(new FileInputStream(enumsPlistLocation));
			ArrayList<Map<String, Object>> agentPlatforms = PropertyList.getElementAsArrayListMap(enums, AGENT_PLATFORM_ENUM_PATH);
			
			for (Map<String, Object> agentPlatform : agentPlatforms){
				m_runtimeProperties.put(AGENT_PLATFORM + "." + agentPlatform.get("value").toString().toUpperCase(), agentPlatform.get("key").toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(ResourceUtilities.getUnlocalizableString("FAILED_TO_INIT_PLATFORM_ENUM", enumsPlistLocation), e);
		}
	}
	
}
