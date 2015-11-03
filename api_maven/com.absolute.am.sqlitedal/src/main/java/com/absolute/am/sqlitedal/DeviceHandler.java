/**
 * 
 */
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
import com.absolute.am.dal.IDeviceHandler;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.dal.model.IPhoneInstalledConfigurationProfile;
import com.absolute.am.dal.model.IPhoneInstalledProvisioningProfile;
import com.absolute.am.dal.model.IPhoneInstalledSoftwareInfo;


/**
 * @author klavin
 * 
 */
public class DeviceHandler extends BaseHandler implements IDeviceHandler{
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(DeviceHandler.class.getName()); 
    
	private SessionFactory m_sessionFactoryAdminDB;

	public DeviceHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
	public IPhoneInfo getDevice(long deviceId) throws Exception {
		IPhoneInfo device = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			device = (IPhoneInfo) session.get(IPhoneInfo.class, deviceId);
			tx.commit();
		} finally {
			session.close();
		}
		return device;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IPhoneInfo> getDeviceForName(String deviceName) throws Exception {
		List<IPhoneInfo> devices = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			
			Criteria crit = session.createCriteria(IPhoneInfo.class);
			crit.add(Restrictions.eq("deviceName", deviceName));
			devices = crit.list();
		} finally {
			session.close();
		}
		return devices;
	}
	
	@Override
	public String[] getMobileDeviceUniqueIdsAsString(List<Long> deviceIds)
			throws Exception {
		String[] deviceUniqueIds = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("SELECT uniqueId FROM IPhoneInfo WHERE id IN :idList");
			query.setParameterList("idList", deviceIds);
			@SuppressWarnings("unchecked")
			List<String> uniqueIds = (List<String>)query.list();
			ArrayList<String> deviceUUIDsAsString = new ArrayList<String>();

			for (Iterator<String> iterator = uniqueIds.iterator(); iterator.hasNext();){
				String uniqueId = (String)iterator.next();
				deviceUUIDsAsString.add(uniqueId);
			}
			tx.commit();
			deviceUniqueIds = deviceUUIDsAsString.toArray(new String[deviceUUIDsAsString.size()]);
		} finally {
			session.close();
		}
		return deviceUniqueIds;
	}

	@Override
	public IPhoneInstalledSoftwareInfo getDetailsForInstalledSoftwareId(long installedSoftwareId) throws Exception {
		IPhoneInstalledSoftwareInfo swDetails = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			swDetails = (IPhoneInstalledSoftwareInfo) session.get(IPhoneInstalledSoftwareInfo.class, installedSoftwareId);
			tx.commit();
		} finally {
			session.close();
		}
		return swDetails;
	}

	@Override
	public IPhoneInstalledConfigurationProfile getDetailsForInstalledConfigurationProfileId(long configurationProfileId) throws Exception {
		IPhoneInstalledConfigurationProfile configProfile = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			configProfile = (IPhoneInstalledConfigurationProfile) session.get(IPhoneInstalledConfigurationProfile.class, configurationProfileId);
			tx.commit();
		} finally {
			session.close();
		}
		return configProfile;
	}

	@Override
	public IPhoneInstalledProvisioningProfile getDetailsForInstalledProvisioningProfileId(long provisioningProfileId) throws Exception {
		IPhoneInstalledProvisioningProfile provisioningProfile = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			provisioningProfile = (IPhoneInstalledProvisioningProfile) session.get(IPhoneInstalledProvisioningProfile.class, provisioningProfileId);
			tx.commit();
		} finally {
			session.close();
		}
		return provisioningProfile;
	}

	
	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}
}
