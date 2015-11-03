/**
 * 
 */
package com.absolute.am.dal;

/**
 * @author dlavin
 *
 */
public abstract class DalBase {
	/**
	 * Runtime property that indicates the full path to the local folder
	 * where the database is located. This property will be set via
	 * setRuntimeProperties().
	 */
	public final static String PROP_DATABASES_FOLDER = "dal.databasesfolder";
	
	/**
	 * Runtime property that indicates the current sync session token. This 
	 * property will be set via setRuntimeProperties().
	 */
	public final static String PROP_SYNC_SESSION_TOKEN = "dal.syncsessiontoken";
	
	/**
	 * Runtime property that indicates the full path to the LoadableSqliteExtensions.dll library
	 */
	public final static String PROP_LOADABLE_EXTENSIONS_FILE = "dal.loadableExtensionsFile";	
	
	/**
	 * Runtime property that indicates the full path to the local file
	 * where the webapi can store it's housekeeping data. This property will be
	 * set via initialize().
	 */
	public final static String PROP_WEBAPI_DATABASE_FILE = "dal.webapidatabasefile";
	
	/**
	 * Runtime property that indicates the full path to the local file
	 * where the user preferences can store user preference data. This property will be
	 * set via initialize().
	 */
	public final static String PROP_USER_PREFS_DATABASE_FILE = "com.absolute.am.sqlitedal.UserPrefHandler.pathToUserPrefsDatabase";
	
	/**
	 * Runtime property that indicates the name of the admin db configuration file for hibernate.
	 */
	public final static String PROP_ADMIN_DB_CFG_NAME = "dal.admindbcfgname";
	
	/**
	 * Runtime property that indicates the name of the enum db configuration file for hibernate.
	 */
	public final static String PROP_ENUM_DB_CFG_NAME = "dal.enumdbcfgname";
	
	/**
	 * Runtime property that indicates the locales of the database supported.
	 */
	public final static String PROP_WEBAPI_DATABASE_SUPPORTED_LOCALES = "dbSupportedLocales";
	
}
