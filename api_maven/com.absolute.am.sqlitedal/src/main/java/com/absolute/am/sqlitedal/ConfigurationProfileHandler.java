/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.IConfigurationProfileHandler;
import com.absolute.am.dal.model.ConfigurationProfile;

public class ConfigurationProfileHandler extends BaseHandler implements IConfigurationProfileHandler {
	
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ConfigurationProfileHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;

	public ConfigurationProfileHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
		String dbsPath = (String)runtimeProperties.get(DalBase.PROP_DATABASES_FOLDER);
		String adminDbCfgPath = (String)runtimeProperties.get(DalBase.PROP_ADMIN_DB_CFG_NAME);
		File adminDbCfgFile = new File(adminDbCfgPath);
		Configuration configurationAdminDB;
		
		if (null == dbsPath ||
				(dbsPath!= null && dbsPath.length()==0)) {
			throw new IllegalArgumentException("Runtime properties must include [" + DalBase.PROP_DATABASES_FOLDER + "].");
		}
		
		// create the configuration differently depending on the type of path provided
		if( adminDbCfgFile.exists()){
			configurationAdminDB = new Configuration().configure(adminDbCfgFile);
		} else {
			configurationAdminDB = new Configuration().configure(adminDbCfgPath);
		}
		
	    configurationAdminDB.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbsPath + "/" + DatabaseHelper.getDatabaseFileNameFromLogicalName("main"));
	    Properties properties = configurationAdminDB.getProperties();
	    
	    ServiceRegistry serviceRegistryAdminDB = new StandardServiceRegistryBuilder().applySettings(properties).build();       
	    m_sessionFactoryAdminDB = configurationAdminDB.buildSessionFactory(serviceRegistryAdminDB);
	}

	@Override
	public ConfigurationProfile getConfigurationProfile(long configurationProfileId)
			throws Exception {
		ConfigurationProfile configurationProfile = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			configurationProfile = (ConfigurationProfile) session.get(ConfigurationProfile.class, configurationProfileId);
			tx.commit();
		} finally {
			session.close();
		}
		return configurationProfile;
	}

	@Override
	public UUID[] getConfigurationProfileUniqueIds(List<Long> configurationProfileIds) throws Exception {
		UUID[] cpUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM iOsConfigurationProfiles where id IN :idList");
			query.setParameterList("idList", configurationProfileIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<UUID> configurationProfileUUIDs = new ArrayList<UUID>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				configurationProfileUUIDs.add(UUID.fromString(uniqueId));
			}
			tx.commit();
			cpUniqueIds = configurationProfileUUIDs.toArray(new UUID[configurationProfileUUIDs.size()]);
		} finally {
			session.close();
		}
		return cpUniqueIds;
	}

	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}

}

