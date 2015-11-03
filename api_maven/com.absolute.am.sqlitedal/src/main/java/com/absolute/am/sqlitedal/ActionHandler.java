/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.sqlitedal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.IActionHandler;
import com.absolute.am.dal.model.MobileAction;

/**
 * @author rchen
 * 
 */

public class ActionHandler extends BaseHandler implements IActionHandler {
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ActionHandler.class.getName()); 
	
	private SessionFactory m_sessionFactoryAdminDB;
	
	public ActionHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
		String dbsPath = (String)runtimeProperties.get(DalBase.PROP_DATABASES_FOLDER);
		if (null == dbsPath ||
				(dbsPath!= null && dbsPath.length()==0)) {
			throw new IllegalArgumentException("Runtime properties must include [" + DalBase.PROP_DATABASES_FOLDER + "].");
		}
		Configuration configurationAdminDB = new Configuration().configure("AdminDatabase.cfg.xml");	    
		configurationAdminDB.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbsPath + "/" + DatabaseHelper.getDatabaseFileNameFromLogicalName("main"));
		Properties properties = configurationAdminDB.getProperties();
	    
		ServiceRegistry serviceRegistryAdminDB = new StandardServiceRegistryBuilder().applySettings(properties).build();       
		m_sessionFactoryAdminDB = configurationAdminDB.buildSessionFactory(serviceRegistryAdminDB);
	}
	
	@Override
	public MobileAction getAction(long actionId) throws Exception {
		MobileAction action = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			action = (MobileAction) session.get(MobileAction.class, actionId);
			tx.commit();
		} finally {
			session.close();
		}
		return action;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] getActionUniqueIdsForActionNames(String actionName) throws Exception {
		String[] actionUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			
			Criteria crit = session.createCriteria(MobileAction.class);
			crit.add(Restrictions.eq("displayName", actionName));
			
			ArrayList<String> actionUUIDsAsString = new ArrayList<String>();
			List<MobileAction> actions = crit.list();
			
			for(MobileAction action : actions){
				actionUUIDsAsString.add(action.getUniqueID());
			}
			
			actionUniqueIds = actionUUIDsAsString.toArray(new String[actionUUIDsAsString.size()]);
		} finally {
			session.close();
		}
		
		return actionUniqueIds;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] getActionUniqueIds(List<Long> actionIds) throws Exception {
		String[] actionUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			
			Criteria crit = session.createCriteria(MobileAction.class);
			crit.add(Restrictions.in("id", actionIds) );
			
			ArrayList<String> actionUUIDsAsString = new ArrayList<String>();
			List<MobileAction> actions = crit.list();
			
			for(MobileAction action : actions){
				actionUUIDsAsString.add(action.getUniqueID());
			}
			
			actionUniqueIds = actionUUIDsAsString.toArray(new String[actionUUIDsAsString.size()]);
		} finally {
			session.close();
		}
		
		return actionUniqueIds;
	}

	
	/**
	 * Close any resources held by this object.
	 */ 
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}
}
