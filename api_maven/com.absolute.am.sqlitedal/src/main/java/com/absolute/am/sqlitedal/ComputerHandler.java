/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.sqlitedal;

import java.io.IOException;
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
import com.absolute.am.dal.IComputerHandler;
import com.absolute.am.dal.model.ComputerInfo;

/**
 * @author rchen
 * 
 */

public class ComputerHandler extends BaseHandler implements IComputerHandler{
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ComputerHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;

	public ComputerHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	public ComputerInfo getComputer(long computerId) throws Exception {
		ComputerInfo computer = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			computer = (ComputerInfo) session.get(ComputerInfo.class, computerId);
			tx.commit();
		} finally {
			session.close();
		}
		return computer;
	}

	// This method is not used.
	// TODO: The query 'SELECT uniqueId FROM ComputerInfo WHERE id IN :idList' seems to be wrong because
	// there's no property named 'uniqueId' in the model class 'ComputerInfo'.
	// The query should probably be: SELECT agentSerial FROM ComputerInfo WHERE id IN :idList
	@Override
	public String[] getComputerUniqueIdsAsString(List<Long> computerIds)
			throws Exception {
		String[] computerUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM ComputerInfo WHERE id IN :idList");
			query.setParameterList("idList", computerIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<String> computerUUIDsAsString = new ArrayList<String>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				computerUUIDsAsString.add(uniqueId);
			}
			tx.commit();
			computerUniqueIds = computerUUIDsAsString.toArray(new String[computerUUIDsAsString.size()]);
		} finally {
			session.close();
		}
		return computerUniqueIds;
	}
	
	/**
	 * Close any resources held by this object.
	 */ 
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}

}
