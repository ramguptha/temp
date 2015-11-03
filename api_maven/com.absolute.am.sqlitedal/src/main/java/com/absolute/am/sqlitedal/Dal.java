/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.IActionHandler;
import com.absolute.am.dal.IApplicationsHandler;
import com.absolute.am.dal.IAdminAccessHandler;
import com.absolute.am.dal.IComputerAdminAccessHandler;
import com.absolute.am.dal.IComputerHandler;
import com.absolute.am.dal.IConfigurationProfileHandler;
import com.absolute.am.dal.IContentHandler;
import com.absolute.am.dal.ICustomFieldHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IDeviceHandler;
import com.absolute.am.dal.IEnumHandler;
import com.absolute.am.dal.IGlobalDataHandler;
import com.absolute.am.dal.IInfoItemHandler;
import com.absolute.am.dal.IProvisioningProfileHandler;
import com.absolute.am.dal.IUserPrefHandler;
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.IViewHandler;
import com.absolute.am.sqlitedal.UserPrefHandler;

/**
 * @author dlavin
 *
 */
public class Dal implements IDal {

	private static Logger m_logger = LoggerFactory.getLogger(IDal.class.getName());

	private Properties runtimeProperties;

	public final static String PROP_VIEW_CONFIG_FOLDER = "sqlitedal.viewConfigFolder";

	private ContentHandler m_contentHandler = null;
	private DeviceHandler m_deviceHandler = null;
	private ComputerAdminAccessHandler m_computerAdminAccessHandler = null;
	private PolicyHandler m_policyHandler = null;
	private EnumHandler m_enumHandler = null;
	private InfoItemHandler m_infoItemHandler = null;
	private GlobalDataHandler m_globalDataHandler = null;
	private UserPrefHandler m_userPrefHandler = null;
	private ViewHandler m_viewHandler = null;
	private ApplicationsHandler m_applicationsHandler = null;
	private ConfigurationProfileHandler m_configurationProfileHandler = null;
	private ProvisioningProfileHandler m_provisioningProfileHandler = null;
	private AdminAccessHandler m_adminAccessHandler = null;
	private ComputerHandler m_computerHandler = null;
	private ActionHandler m_actionHandler = null;
	private CustomFieldHandler m_customFieldHandler = null;

	private boolean m_hasBeenClosed = false, m_noSync = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absolute.am.dal.IDal#setRuntimeProperties(java.util.Properties)
	 */
	@Override
	public void initialize(Properties runtimeProperties, boolean noSync) {

		m_noSync = noSync;

		if (!noSync) {
			String databasesFolder = runtimeProperties.getProperty(DalBase.PROP_DATABASES_FOLDER);
			m_logger.debug("Checking if [" + databasesFolder + "] exists.");
			File dbFolder = new File(databasesFolder);
			if (!dbFolder.exists()) {

				m_logger.debug("Checking if [" + databasesFolder + "] exists.");
				dbFolder = new File(databasesFolder);
				if (!dbFolder.exists()) {
					throw new IllegalArgumentException("Databases folder [" + databasesFolder + "] does not exist.");
				}

				// Override the runtime properties with the correct path, so we
				// don't have to check this again.
				runtimeProperties.setProperty(DalBase.PROP_DATABASES_FOLDER, databasesFolder);
			}

			// Force initialization of the webapi database.
			try {
				WebAPIDB.getInstance(runtimeProperties);
			} catch (IOException e) {
				throw new RuntimeException("Failed to initialize WebAPIDB." + e.toString());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to initialize WebAPIDB. " + e.toString());
			} catch (SQLException e) {
				throw new RuntimeException("Failed to initialize WebAPIDB." + e.toString());
			}
		}

		this.runtimeProperties = runtimeProperties;
	}

	/**
	 * Helper method to check on the state of the DAL. If the DAL has been
	 * closed already, then subsequent attempts to get a handler should fail.
	 */
	private void assertNotClosed() {
		if (m_hasBeenClosed) {
			throw new IllegalStateException("DAL has been closed already.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absolute.am.dal.IDal#getViewHandler()
	 */
	@Override
	public IViewHandler getViewHandler() throws ParserConfigurationException, SAXException, IOException {

		assertNotClosed();
		if (null == m_viewHandler) {
			m_viewHandler = new ViewHandler(runtimeProperties);
		}
		return m_viewHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absolute.am.dal.IDal#getUserPrefHandler()
	 */
	@Override
	public IUserPrefHandler getUserPrefHandler() throws ParserConfigurationException, SAXException, IOException {

		assertNotClosed();
		if (null == m_userPrefHandler) {
			m_userPrefHandler = new UserPrefHandler(runtimeProperties);
		}
		return m_userPrefHandler;
	}

	@Override
	public IContentHandler getContentHandler() throws Exception {

		assertNotClosed();
		if (null == m_contentHandler) {
			m_contentHandler = new ContentHandler(runtimeProperties);
		}
		return m_contentHandler;
	}

	@Override
	public IPolicyHandler getPolicyHandler() throws Exception {

		assertNotClosed();
		if (null == m_policyHandler) {
			m_policyHandler = new PolicyHandler(runtimeProperties);
		}
		return m_policyHandler;
	}

	@Override
	public IEnumHandler getEnumHandler() throws Exception {
		assertNotClosed();
		if (null == m_enumHandler) {
			m_enumHandler = new EnumHandler(runtimeProperties, m_noSync);
		}
		return m_enumHandler;
	}

	@Override
	public IInfoItemHandler getInfoItemHandler() throws Exception {
		assertNotClosed();
		if (null == m_infoItemHandler) {
			m_infoItemHandler = new InfoItemHandler(runtimeProperties);
		}
		return m_infoItemHandler;
	}

	@Override
	public IGlobalDataHandler getGlobalDataHandler() throws Exception {
		assertNotClosed();
		if (null == m_globalDataHandler) {
			m_globalDataHandler = new GlobalDataHandler(runtimeProperties);
		}
		return m_globalDataHandler;
	}

	@Override
	public IDeviceHandler getDeviceHandler() throws Exception {

		assertNotClosed();
		if (null == m_deviceHandler) {
			m_deviceHandler = new DeviceHandler(runtimeProperties);
		}
		return m_deviceHandler;
	}

	@Override
	public IApplicationsHandler getApplicationsHandler() throws Exception {
		assertNotClosed();
		if (null == m_applicationsHandler) {
			m_applicationsHandler = new ApplicationsHandler(runtimeProperties);
		}
		return m_applicationsHandler;
	}

	@Override
	public IAdminAccessHandler getAdminAccessHandler() throws Exception {

		assertNotClosed();
		if (null == m_adminAccessHandler) {
			m_adminAccessHandler = new AdminAccessHandler(runtimeProperties);
		}
		return m_adminAccessHandler;
	}

	@Override
	public IConfigurationProfileHandler getConfigurationProfileHandler() throws Exception {
		assertNotClosed();
		if (null == m_configurationProfileHandler) {
			m_configurationProfileHandler = new ConfigurationProfileHandler(runtimeProperties);
		}
		return m_configurationProfileHandler;
	}

	@Override
	public IProvisioningProfileHandler getProvisioningProfileHandler() throws Exception {
		assertNotClosed();
		if (null == m_provisioningProfileHandler) {
			m_provisioningProfileHandler = new ProvisioningProfileHandler(runtimeProperties);
		}
		return m_provisioningProfileHandler;
	}

	@Override
	public IComputerAdminAccessHandler getComputerAdminAccessHandler() throws Exception {

		assertNotClosed();
		if (null == m_computerAdminAccessHandler) {
			m_computerAdminAccessHandler = new ComputerAdminAccessHandler(runtimeProperties);
		}
		return m_computerAdminAccessHandler;
	}

	@Override
	public IComputerHandler getComputerHandler() throws Exception {

		assertNotClosed();
		if (null == m_computerHandler) {
			m_computerHandler = new ComputerHandler(runtimeProperties);
		}
		return m_computerHandler;
	}

	@Override
	public IActionHandler getActionHandler() throws Exception {

		assertNotClosed();
		if (null == m_actionHandler) {
			m_actionHandler = new ActionHandler(runtimeProperties);
		}
		return m_actionHandler;
	}

	@Override
	public ICustomFieldHandler getCustomFieldHandler() throws Exception {

		assertNotClosed();
		if (null == m_customFieldHandler) {
			m_customFieldHandler = new CustomFieldHandler(runtimeProperties);
		}
		return m_customFieldHandler;
	}
	
}
