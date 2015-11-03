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
import com.absolute.am.dal.IProvisioningProfileHandler;
import com.absolute.am.dal.model.ProvisioningProfile;

public class ProvisioningProfileHandler extends BaseHandler implements IProvisioningProfileHandler {
	
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ProvisioningProfileHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;

	public ProvisioningProfileHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	public ProvisioningProfile getProvisioningProfile(long provisioningProfileId)
			throws Exception {
		ProvisioningProfile provisioningProfile = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			provisioningProfile = (ProvisioningProfile) session.get(ProvisioningProfile.class, provisioningProfileId);
			tx.commit();
		} finally {
			session.close();
		}
		return provisioningProfile;
	}

	@Override
	public UUID[] getProvisioningProfileUniqueIds(List<Long> provisioningProfileIds) throws Exception {
		UUID[] ppUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM ProvisioningProfile where id IN :idList");
			query.setParameterList("idList", provisioningProfileIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<UUID> provisioningProfileUUIDs = new ArrayList<UUID>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				provisioningProfileUUIDs.add(UUID.fromString(uniqueId));
			}
			tx.commit();
			ppUniqueIds = provisioningProfileUUIDs.toArray(new UUID[provisioningProfileUUIDs.size()]);
		} finally {
			session.close();
		}
		return ppUniqueIds;
	}

	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}

}

