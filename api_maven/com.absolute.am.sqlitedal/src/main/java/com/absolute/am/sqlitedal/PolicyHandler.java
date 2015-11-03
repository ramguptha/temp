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
import java.sql.Statement;
import java.util.ArrayList;
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
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.model.iOsPolicies;

/**
 * @author klavin
 * 
 */
public class PolicyHandler extends BaseHandler implements IPolicyHandler{
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(PolicyHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;
	private DatabaseHelper m_databaseHelper;

	public PolicyHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	public iOsPolicies getPolicy(long policyId) throws Exception {
		iOsPolicies policy = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			policy = (iOsPolicies) session.get(iOsPolicies.class, policyId);
			tx.commit();
		} finally {
			session.close();
		}
		return policy;
	}

	@Override
	public String[] getPolicyUniqueIdsAsString(List<Long> policyIds) throws Exception {
		String[] policyUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM iOsPolicies where id IN :idList");
			query.setParameterList("idList", policyIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<String> policyUUIDsAsString = new ArrayList<String>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				policyUUIDsAsString.add(uniqueId);
			}
			tx.commit();
			policyUniqueIds = policyUUIDsAsString.toArray(new String[policyUUIDsAsString.size()]);
		} finally {
			session.close();
		}
		return policyUniqueIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSmartPolicyIdsForDeviceAsString(long deviceId) throws Exception {
		StringBuilder smartPolicyIds = new StringBuilder();
		Session session = null;
		List<Object> policyFilterQueries = null;
		// Get the policy id, filter table and filter queries for all smart policies
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT id, filterTables, filterQuery FROM iOsPolicies WHERE filterQuery is not null");
			policyFilterQueries = (List<Object>)query.list();
			tx.commit();
		} finally {
			session.close();
		}
		
		if (policyFilterQueries != null) {
			Connection connection = null;
			Statement statement = null;
			java.sql.ResultSet resultSet = null;
			try {
				connection = m_databaseHelper.connectToMainDatabase(true);
				statement = connection.createStatement();
				statement.setQueryTimeout(30); // set timeout to 30 sec. // TODO: make this configurable.

				boolean attached = false;
				
				for (Iterator<Object> iterator = policyFilterQueries.iterator(); iterator.hasNext();){
					Object[] query = (Object[])iterator.next();
					StringBuilder selectBuilder = new StringBuilder(); 
					String filterTables = (String) query[1];
					String filterQuery = (String) query[2];
					
					if (!filterTables.contains("iphone_info")) {
						throw new Exception("filterTables does not contain iphone_info");
					}
					selectBuilder.append(filterQuery);
					selectBuilder.append(" AND __iphone_info_id = " + deviceId);
					filterQuery = selectBuilder.toString();

					// We dont want to attach the the same database twice
					// so keep track of what we've already attached to
					if (!attached) {
						m_databaseHelper.AttachToLogicalDatabasesForQuery(statement, filterQuery);
						attached=true;	// only want to do this once
					}
					
			    	resultSet = statement.executeQuery(filterQuery);
					if (resultSet.next()) {
						if (smartPolicyIds.length() > 0) {
							smartPolicyIds.append(",");
						}
						smartPolicyIds.append(query[0]);
					}
					resultSet.close();
					resultSet = null;
				}
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
		}
		return smartPolicyIds.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getNonSmartPolicyIdsForDeviceAsString(String deviceUniqueId) throws Exception {
		StringBuilder policyIds = new StringBuilder();
		Session session = null;
		List<Object> queryResult = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT iOSPoliciesRecordId FROM iOsPoliciesDevices WHERE deviceUniqueId = '" + deviceUniqueId + "'");
			queryResult = (List<Object>)query.list();
			for (Iterator<Object> iterator = queryResult.iterator(); iterator.hasNext();){
				long result = (Long)iterator.next();
				if (policyIds.length() != 0) {
					policyIds.append(",");
				}
				policyIds.append(result);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return policyIds.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getPolicyIdsForMediaAsString(String mediaUniqueID)
			throws Exception {
		StringBuilder policyIds = new StringBuilder();
		Session session = null;
		List<Object> queryResult = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT iOSPoliciesRecordId FROM iOsPoliciesMedia WHERE mediaUniqueId = '" + mediaUniqueID + "'");
			queryResult = (List<Object>)query.list();
			for (Iterator<Object> iterator = queryResult.iterator(); iterator.hasNext();){
				long result = (Long)iterator.next();
				if (policyIds.length() != 0) {
					policyIds.append(",");
				}
				policyIds.append(result);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return policyIds.toString();
	}

	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}
	
}
