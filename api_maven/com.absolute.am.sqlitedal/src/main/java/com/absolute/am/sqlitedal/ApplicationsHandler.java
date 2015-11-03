/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
import com.absolute.am.dal.IApplicationsHandler;
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.am.sqlitedal.DatabaseHelper;


/**
 * @author klavin
 * 
 */
public class ApplicationsHandler extends BaseHandler implements IApplicationsHandler{
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ApplicationsHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;
	private DatabaseHelper m_databaseHelper;
	
	private final String SQL_SELECT_APPLICATION_ICON_BLOB = 
	  		  "SELECT AppIcon FROM iOS_appstore_applications WHERE id = %1$d";
	private final String SQL_SELECT_BOOK_ICON_BLOB = 
	  		  "SELECT BookIcon FROM bookstore_books WHERE id = %1$d";
	
	public ApplicationsHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	    m_databaseHelper = new DatabaseHelper(runtimeProperties);
	}

	@Override
	public iOSApplications getInHouseApplication(long applicationId)
			throws Exception {
		iOSApplications inHouseApplication = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			inHouseApplication = (iOSApplications) session.get(iOSApplications.class, applicationId);
			tx.commit();
		} finally {
			session.close();
		}
		return inHouseApplication;
	}

	@Override
	public iOSAppStoreApplications getThirdPartyApplication(long applicationId)
			throws Exception {
		iOSAppStoreApplications thirdPartyApplication = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			thirdPartyApplication = (iOSAppStoreApplications) session.get(iOSAppStoreApplications.class, applicationId);
			tx.commit();
		} finally {
			session.close();
		}
		return thirdPartyApplication;
	}

	@Override
	public byte[] getIcon(long id, iconType type)
			throws Exception {
		byte[] icon = null;
		String sqlSelect;
		
		if( iconType.thirdPartyApp == type){
			sqlSelect = SQL_SELECT_APPLICATION_ICON_BLOB;
		} else if ( iconType.book == type){
			sqlSelect = SQL_SELECT_BOOK_ICON_BLOB;
		} else {
			return null;
		}
		
		String selectStatement = String.format(sqlSelect, id);
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = m_databaseHelper.connectToMainDatabase();
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // TODO: make this configurable.

	    	resultSet = statement.executeQuery(selectStatement);
	    	
	    	if (resultSet.next()) {
	    		icon = resultSet.getBytes(1);
	    	}

			resultSet.close();
			resultSet = null;
		} finally {
			if (resultSet != null) {
				resultSet.close();
				resultSet = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
		
		return icon;
	}

	@Override
	public UUID[] getInHouseAppUniqueIds(List<Long> inHouseAppIds) throws Exception {
		UUID[] ihaUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueID FROM iOSApplications where id IN :idList");
			query.setParameterList("idList", inHouseAppIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<UUID> inHouseAppUUIDs = new ArrayList<UUID>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				inHouseAppUUIDs.add(UUID.fromString(uniqueId));
			}
			tx.commit();
			ihaUniqueIds = inHouseAppUUIDs.toArray(new UUID[inHouseAppUUIDs.size()]);
		} finally {
			session.close();
		}
		return ihaUniqueIds;
	}

	@Override
	public UUID[] getThirdPartyAppUniqueIds(List<Long> thirdPartyAppIds) throws Exception {
		UUID[] thpUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueID FROM iOSAppStoreApplications where id IN :idList");
			query.setParameterList("idList", thirdPartyAppIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<UUID> thirdPartyAppUUIDs = new ArrayList<UUID>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				thirdPartyAppUUIDs.add(UUID.fromString(uniqueId));
			}
			tx.commit();
			thpUniqueIds = thirdPartyAppUUIDs.toArray(new UUID[thirdPartyAppUUIDs.size()]);
		} finally {
			session.close();
		}
		return thpUniqueIds;
	}

	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}

}
