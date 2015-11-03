/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import com.absolute.am.dal.IGlobalDataHandler;

public class GlobalDataHandler extends BaseHandler implements IGlobalDataHandler{

	private SessionFactory m_sessionFactoryAdminDB;

	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(GlobalDataHandler.class.getName()); 

	public GlobalDataHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	public int getDatabaseSchemaVersion() throws Exception {
		int schemaVersion = 0;
		
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT schemaVersion FROM GlobalData");

			@SuppressWarnings("unchecked")
			List<Integer> result = (List<Integer>)query.list();
			Iterator<Integer> iterator = result.iterator();
			if (iterator.hasNext()) {
				schemaVersion = iterator.next();
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			session.close();
		}
		return schemaVersion;
	}	
	
	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}

}
