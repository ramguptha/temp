/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.sqlitedal;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import com.absolute.am.dal.DalBase;

/**
 * @author klavin
 *
 */
public class DatabaseHelper {
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(DatabaseHelper.class.getName()); 
	
	private String dbsFolderPath, m_webapiDbFile, m_loadableExtensionsFile;
	
	private static final HashMap<String, String> dbNamesToFileNames = new HashMap<String, String>(); 
	
	private static HashMap<String, String> getDBNamesToFileNames() {
		if (dbNamesToFileNames.isEmpty()) {
			synchronized(dbNamesToFileNames) {
				if (dbNamesToFileNames.isEmpty()) {
					dbNamesToFileNames.put("main", "database.db");
					dbNamesToFileNames.put("enum", "enumvalues.db");
					dbNamesToFileNames.put("custom", "customfields.db");					
					dbNamesToFileNames.put("ump", "Database.UMP.db");					
				}
			}
		}
		return dbNamesToFileNames;
	}
	/**
	 * Helper method to return the file name of the database, given the logical name.
	 * @param logicalName - the logical name as used in queries, e.g. main, enum, etc.
	 * @return the filename. This method throws an IllegalArgumentException if the logical
	 * name is not known. 
	 */
	public static String getDatabaseFileNameFromLogicalName(String logicalName) {
		if (!getDBNamesToFileNames().containsKey(logicalName)) {
			throw new IllegalArgumentException("logical database name not known:" + logicalName);
		}
		return getDBNamesToFileNames().get(logicalName);
	}
	
	//private static final String dbLogicalNames = "main,enum,custom";
	public static final String defaultDatabaseLogicalName = "main";

	public DatabaseHelper(Properties runtimeProperties) {
		String dbsPath = (String)runtimeProperties.get(DalBase.PROP_DATABASES_FOLDER);

		if (null == dbsPath ||
				(dbsPath!= null && dbsPath.length()==0)) {
			throw new IllegalArgumentException("Runtime properties must include [" + DalBase.PROP_DATABASES_FOLDER + "].");
		}
		setDbsFolderPath(dbsPath);
		
		m_webapiDbFile = (String)runtimeProperties.get(DalBase.PROP_WEBAPI_DATABASE_FILE);
		m_loadableExtensionsFile = (String)runtimeProperties.get(DalBase.PROP_LOADABLE_EXTENSIONS_FILE);
		
		if (null == m_webapiDbFile ||
				(m_webapiDbFile!= null && m_webapiDbFile.length()==0)) {
			throw new IllegalArgumentException("Runtime properties must include [" + DalBase.PROP_WEBAPI_DATABASE_FILE + "].");
		}

	}

    public HashMap<String, String> getDatabasesToAttachForQuery(String query)
    {    	
    	HashMap<String, String> retVal = new HashMap<String, String>();

    	for (String dbName: getDBNamesToFileNames().keySet()) {
    		if (dbName.equals(defaultDatabaseLogicalName)) {
    			continue;
    		}
    		
  			retVal.put(dbName, GetDatabaseFilePath(dbName));
    	}
    	
    	// always include the webapi db.
    	retVal.put("webapi", m_webapiDbFile);
    	
        return retVal;
    }
    
	private void setDbsFolderPath(String path) {
		if (!path.endsWith(File.separator)) {
			this.dbsFolderPath = path + File.separator;
		} else {
			this.dbsFolderPath = path;
		}
	}
		
	private String GetDatabaseFilePath(String dbName){
		if (dbName.compareToIgnoreCase("webapi") == 0) {
			return m_webapiDbFile;
		}
		
		if (!getDBNamesToFileNames().containsKey(dbName)) {
			throw new IllegalArgumentException("Unknown database logical name [" + dbName + "].");
		}
		return dbsFolderPath + getDBNamesToFileNames().get(dbName);		
	}

	public void AttachToLogicalDatabasesForQuery(Statement statement, HashMap<String, String> databasesToAttachTo) throws SQLException {
		//m_logger.debug("databasesToAttachTo={}", databasesToAttachTo);
    	for (String dbLogicalName: databasesToAttachTo.keySet()) {    		
    		String attachStatement = "ATTACH DATABASE '" +
    				databasesToAttachTo.get(dbLogicalName) + 
    				"' AS " + 
    				dbLogicalName + 
    				";";
    		MDC.put("sql", attachStatement);
    		statement.execute(attachStatement);
    		MDC.remove("sql");
    	}
    }

    public void AttachToLogicalDatabasesForQuery(Statement statement, String query) throws SQLException {
    	HashMap<String, String> databasesToAttachTo = getDatabasesToAttachForQuery(query);
    	AttachToLogicalDatabasesForQuery(statement, databasesToAttachTo);
    }
	
	public Connection connectToMainDatabase() throws Exception {	
        return connectToMainDatabase(false);
	}
	
	public Connection connectToMainDatabase(boolean loadExtensions) throws Exception {
		Connection connection = null;
		String mainDB = GetDatabaseFilePath("main");
		String connectString = "jdbc:sqlite:" + mainDB;
		Class.forName("org.sqlite.JDBC");
		
		if(loadExtensions){
			// we must explicitly enable load extensions prior to calling load_extension()
			SQLiteConfig config = new SQLiteConfig();
			config.enableLoadExtension(true);
			connection = DriverManager.getConnection(connectString, config.toProperties());
			setupLoadableExtensionsForConnection(connection);
		} else {
			connection = DriverManager.getConnection(connectString);
		}
		
		return connection;
	}
	
	public Connection connectToDatabase(String dbName) throws Exception {	
		Connection connection = null;
		// load the sqlite-JDBC driver using the current class loader
		String realDbName = GetDatabaseFilePath(dbName);
		Class.forName("org.sqlite.JDBC");
		// create a database connection
		connection = DriverManager.getConnection("jdbc:sqlite:" + realDbName);

        return connection;
	}

	private void setupLoadableExtensionsForConnection(Connection connection) throws SQLException{
		Statement statement = connection.createStatement();
		String query = "SELECT load_extension('" + m_loadableExtensionsFile + "');", 
				enumDbPath = new File(dbsFolderPath, "EnumValues.db").getAbsolutePath(),
				customFieldDbPath = new File(dbsFolderPath, "CustomFields.db").getAbsolutePath();
		
		statement.execute(query);
		
		query = "SELECT ABTInitExtensions('" + enumDbPath + "', 'en', '" + customFieldDbPath + "');";
		statement.execute(query);
	}
}
