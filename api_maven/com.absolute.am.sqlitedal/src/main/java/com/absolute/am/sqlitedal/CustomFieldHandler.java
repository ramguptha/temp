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
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.xml.sax.SAXException;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.ICustomFieldHandler;
import com.absolute.am.dal.model.CustomField;
import com.absolute.am.dal.model.CustomFieldActionDefinition;


/**
 * @author ephilippov
 * 
 */
public class CustomFieldHandler extends BaseHandler implements ICustomFieldHandler {
	
	private SessionFactory m_sessionFactoryAdminDB;
	private DatabaseHelper m_databaseHelper;
	
	public CustomFieldHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
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
		
	    configurationAdminDB.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbsPath + "/" + DatabaseHelper.getDatabaseFileNameFromLogicalName("custom"));
	    Properties properties = configurationAdminDB.getProperties();
	    
	    ServiceRegistry serviceRegistryAdminDB = new StandardServiceRegistryBuilder().applySettings(properties).build();
	    m_sessionFactoryAdminDB = configurationAdminDB.buildSessionFactory(serviceRegistryAdminDB);
	    m_databaseHelper = new DatabaseHelper(runtimeProperties);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CustomField> getCustomFields() {
		ArrayList<CustomField> customFields = null;
		Session session = null;
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			customFields = (ArrayList<CustomField>) session.createCriteria(CustomField.class).list();
			tx.commit();
		} finally {
			session.close();
		}
		return customFields;
	}
	
	@Override
	public ArrayList<CustomField> getCustomFieldsWithActions() throws Exception {
		Connection connection = null;
		Statement statement = null;
		java.sql.ResultSet resultSet = null;
		ArrayList<CustomField> customFieldList = new ArrayList<CustomField>();
		
		try {
			connection = m_databaseHelper.connectToDatabase("custom");
			statement = connection.createStatement();

			statement.setQueryTimeout(30);

			String selectStmt = "SELECT custom_field_definitions.id, custom_field_definitions.Name, custom_field_definitions.Seed, DataType, DisplayType, EnumerationList, " +
							    "EvaluationMethod, DeviceType, VariableName, AutoAssignToAllMachines, Description, custom_field_actions.ActionId, " +
					            "custom_field_action_definitions.Platform, ReplaceLineFeeds, RequiresAdminPrivileges, " + 
							    "custom_field_action_definitions.Seed custom_field_action_definitions_Seed, ExecuteOnlyWithFullInventory, " +
							    "ReturnExecutionErrors, SourceTypeSelector, SourceFile, SourceFileChecksum, ExecutableOptions, ExecutableTypeSelector, " +
							    "TransferExecutableFolder, ExecutablePartialPath, UserContext, UserContextPassword, UserContextSelector, ScriptText, " + 
							    "PListDomain, PListKey, PListLocationSelector, RegistryPath " +
							    "FROM custom_field_definitions " +
							    "LEFT OUTER join custom_field_actions on custom_field_definitions.id=custom_field_actions.fieldId " +
							    "LEFT OUTER JOIN custom_field_action_definitions ON custom_field_actions.ActionID = custom_field_action_definitions.id " +
							    "ORDER BY custom_field_definitions.id";
			
			// execute the query
			resultSet = statement.executeQuery(selectStmt);

			String tempFieldId = "";
			CustomField customField = null;
			
			while (resultSet.next()) {
				if (!tempFieldId.equalsIgnoreCase(resultSet.getString("id"))) {
					customField = new CustomField();
					customField.id = resultSet.getString("id");
					customField.name = resultSet.getString("Name");
					customField.seed = resultSet.getInt("Seed");
					customField.dataType = resultSet.getInt("DataType");
					customField.displayType = resultSet.getInt("DisplayType");
					customField.enumerationList =  resultSet.getBytes("EnumerationList");
					customField.evaluationMethod = resultSet.getInt("EvaluationMethod");
					customField.deviceType =  resultSet.getInt("DeviceType");
					customField.variableName = resultSet.getString("VariableName");
					customField.autoAssignToAllMachines = resultSet.getInt("AutoAssignToAllMachines");
					customField.description = resultSet.getString("Description");
					customFieldList.add(customField);
					
					tempFieldId = resultSet.getString("id");
				}
				if (resultSet.getString("ActionId") != null) {
					CustomFieldActionDefinition def = new CustomFieldActionDefinition();
					
					def.id = resultSet.getString("ActionId");
					def.name = resultSet.getString("Name");
					def.platform = resultSet.getInt("Platform");
					def.replaceLineFeeds = resultSet.getInt("ReplaceLineFeeds");
					def.requiresAdminPrivileges = resultSet.getInt("RequiresAdminPrivileges");
					def.seed = resultSet.getInt("custom_field_action_definitions_Seed");
					def.executeOnlyWithFullInventory = resultSet.getInt("ExecuteOnlyWithFullInventory");
					def.returnExecutionErrors = resultSet.getInt("ReturnExecutionErrors");
					def.sourceTypeSelector = resultSet.getInt("SourceTypeSelector");
					def.sourceFile = resultSet.getString("SourceFile");
					def.sourceFileChecksum = resultSet.getString("SourceFileChecksum");
					def.executableOptions = resultSet.getString("ExecutableOptions");
					def.executableTypeSelector = resultSet.getInt("ExecutableTypeSelector");
					def.transferExecutableFolder = resultSet.getInt("TransferExecutableFolder");
					def.userContext = resultSet.getString("UserContext");
					def.userContextPassword = resultSet.getString("UserContextPassword");
					def.executablePartialPath = resultSet.getString("ExecutablePartialPath");
					def.userContextSelector = resultSet.getInt("UserContextSelector");
					def.scriptText = resultSet.getString("ScriptText");
					def.pListDomain = resultSet.getString("PListDomain");
					def.pListKey = resultSet.getString("PListKey");
					def.pListLocationSelector = resultSet.getInt("PListLocationSelector");
					def.registryPath = resultSet.getString("RegistryPath");
					
					customField.customFieldActionDefinitions.add(def);
				}
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
		
		return customFieldList;
	}
	
	@Override
	public CustomField getCustomField(String id) {
		CustomField customField = null;
		Session session = null;
		
		try {
			session = m_sessionFactoryAdminDB.openSession();
			Transaction tx = session.beginTransaction();
			customField = (CustomField) session.get(CustomField.class, id);
			tx.commit();
		} finally {
			session.close();
		}
		
		return customField;
	}	
	
	/**
	 * Close any resources held by this object.
	 */
	public void close() {
		closeSessionFactory(m_sessionFactoryAdminDB);
	}
}
