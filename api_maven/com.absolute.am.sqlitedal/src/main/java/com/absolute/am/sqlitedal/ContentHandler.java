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

import org.hibernate.Hibernate;
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
import com.absolute.am.dal.IContentHandler;
import com.absolute.am.dal.model.MobileMedia;

/**
 * @author klavin
 * 
 */
public class ContentHandler extends BaseHandler implements IContentHandler{
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ContentHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;

	public ContentHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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

	/**
	 * {@link IContentHandler#GetContent(Long)}
	 */
	@Override
	public MobileMedia getContent(long contentId) throws Exception {
		MobileMedia mobileMedia = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			mobileMedia = (MobileMedia) session.get(MobileMedia.class, contentId);
			tx.commit();
		} finally {
			session.close();
		}
		return mobileMedia;
	}

	@Override
	public UUID[] getMediaUniqueIds(List<Long> contentIds) throws Exception {
		UUID[] mmUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM MobileMedia where id IN :idList");
			query.setParameterList("idList", contentIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<UUID> mediaUUIDs = new ArrayList<UUID>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				mediaUUIDs.add(UUID.fromString(uniqueId));
			}
			tx.commit();
			mmUniqueIds = mediaUUIDs.toArray(new UUID[mediaUUIDs.size()]);
		} finally {
			session.close();
		}
		return mmUniqueIds;
	}

	/**
	 * {@link IContentHandler#getContentByDisplayName(String)}
	 */
	@Override
	public MobileMedia getContentByDisplayName(String displayName) {
		MobileMedia mobileMedia = null;
		Session session = null;
		String querySafeDisplayName = displayName.replaceAll("'", "''");
		try {
			String queryString = " from MobileMedia as mm where mm.displayName='" + querySafeDisplayName + "'";
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(queryString);
			@SuppressWarnings("unchecked")
			Iterator<MobileMedia> mmIterator = query.iterate();
			// only expecting 1 result
			if (mmIterator.hasNext()) {
				mobileMedia = mmIterator.next();
				// TODO: hibernate is using lazy loading, don't know how to override that. This line forces the object to be loaded.
				// TODO: Without this line, access to the object later on would fail because the session will be gone.
				// TODO: Need a better solution to this issue.
				Hibernate.initialize(mobileMedia);
			}
			tx.commit();
		} finally {
			session.close();
		}
		return mobileMedia;
	}

	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);		
	}

}
