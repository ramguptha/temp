/**
 * 
 */
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.am.dal.IUserPrefHandler;
import com.absolute.am.dal.model.UserPreference;

/**
 *
 *
 */

public class UserPrefHandler implements IUserPrefHandler {

	/**
	 * This is a sqlitedal specific setting retrieved from the web.xml. It is the full path to the UserPrefs.db file.
	 */
	private static final String INI_PATH_TO_USER_PREFS_DATABASE = "com.absolute.am.sqlitedal.UserPrefHandler.pathToUserPrefsDatabase";
	
    private static Logger m_logger = LoggerFactory.getLogger(UserPrefHandler.class.getName());
    
    private String m_userPrefsDbPath;
	private static boolean m_UserPrefsDbExists = false;	
	
	
	private Connection getConnection () throws Exception {
		
		if(!m_UserPrefsDbExists) 
	    	InitUserDb();
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = null;
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + m_userPrefsDbPath);
			return conn;
			
		} catch (SQLException e) {
	    	m_logger.error("User Prefs: UserPrefHandler: Cannot open database '" + m_userPrefsDbPath + "'.");
			throw new Exception("Application error, cannot open UserPrefs database,"
									+ "\nexception message = " + e.getMessage());
		}
	}
	
	
	public UserPrefHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
		
		// TODO: This database has to be stored somewhere else. It can't go here.
		if (runtimeProperties == null || 
				(runtimeProperties != null && runtimeProperties.isEmpty())) {
			throw new IllegalArgumentException("runtimeProperties cannot be null or empty");
		}
		
		String dbsPath = (String)runtimeProperties.get(INI_PATH_TO_USER_PREFS_DATABASE);
		
		if (null == dbsPath ||
				(dbsPath!= null && dbsPath.length()==0)) {
			m_logger.error("User Prefs: UserPrefHandler: Runtime properties must include [" + INI_PATH_TO_USER_PREFS_DATABASE + "].");
			throw new IllegalArgumentException("Runtime properties must include [" + INI_PATH_TO_USER_PREFS_DATABASE + "].");
		}
		
		m_userPrefsDbPath = dbsPath;		
	}


	@Override
	public synchronized void putPref(String userUID, String keyId, String value,
					String contentType, int isFile, String destFilePath)
			throws Exception {
		
	    Connection conn = getConnection();
	    
	    try {
	    	  String sqlQuery = "INSERT OR REPLACE INTO preferences (UserUID, Key, Value, ContentType, IsFile, FilePath) "
					+ "VALUES('" + userUID + "', '" + keyId + "', '" + value + "', '" + contentType + "', "
		    		+ isFile + ", '" + destFilePath + "')";

		      Statement stmt = conn.createStatement();
		      stmt.setQueryTimeout(30);  // set timeout to 30 sec.
		      stmt.executeUpdate(sqlQuery);
			  stmt.close();

		} finally {
			try {
				if(null != conn)
		    		conn.close();
			} finally {}
		}
	    
	}

	@Override
	public UserPreference getPref(String userUID, String keyId)
			throws Exception {

		UserPreference result = null;
		
		Connection conn = getConnection();
	    		
	    try {	    	
              String sqlQuery = "SELECT UserUID, Key, Value, ContentType, IsFile, FilePath, LastUpdated FROM preferences " +
					"WHERE UserUID = '" + userUID + "' AND Key = '" + keyId + "' ORDER BY LastUpdated DESC";
			
		      Statement stmt = conn.createStatement();
		      stmt.setQueryTimeout(30);  // set timeout to 30 sec.
		      stmt.setMaxRows(1);
		      java.sql.ResultSet rs = stmt.executeQuery(sqlQuery);
		      
		      if (rs.next()) {
		    	  result = new UserPreference(rs.getString("Key"),  
		    			  			rs.getString("ContentType"),
		    			  			rs.getString("Value"),
		    			  			rs.getInt("IsFile"),
		    			  			rs.getString("FilePath"));
		      }

		      rs.close();
			  stmt.close();
		} finally {
			try {
				if(null != conn)
		    		conn.close();
			} finally {}
		}
		
		return result;
	}

	@Override
	public ArrayList<UserPreference> getUserPrefList(String userUID) throws Exception {
		
	    ArrayList<UserPreference> result = new ArrayList<UserPreference>();
	    
	    Connection conn = getConnection();       
	    
	    try {
	    	  String sqlQuery = "SELECT UserUID, Key, Value, ContentType, IsFile, FilePath FROM preferences "
					+ "WHERE UserUID = '" + userUID + "'";

		      Statement stmt = conn.createStatement();
		      stmt.setQueryTimeout(30);  // set timeout to 30 sec.
		      java.sql.ResultSet rs = stmt.executeQuery(sqlQuery);
		      
		      while (rs.next()) {
		    	  UserPreference row = new UserPreference(rs.getString("Key"),  
		    			  			rs.getString("ContentType"),
		    			  			rs.getString("Value"),
		    			  			rs.getInt("IsFile"),
		    			  			rs.getString("FilePath"));
		    	  result.add(row);
		       }

		      rs.close();
			  stmt.close();
		} finally {
			try {
				if(null != conn)
		    		conn.close();
			} finally {}
		}
			
	    return result;	    		      
	}

	@Override
	public synchronized void deletePref(String userUID, String keyId) throws Exception {

	    Connection conn = getConnection();
	    		
	    try {
			  String sqlQuery = "DELETE FROM preferences " +
					"WHERE UserUID = '" + userUID + "' AND Key = '" + keyId + "'";
	    	
		      Statement stmt = conn.createStatement();
		      stmt.setQueryTimeout(30);  // set timeout to 30 sec.
		      stmt.executeUpdate(sqlQuery);
			  stmt.close();

		} finally {
			try {
				if(null != conn)
		    		conn.close();
			} finally {}
		}
		
	}
	
	
	/**
	 * Creates UserPrefsDb if db file does not exist
	 * @return
	 * @throws Exception 
	 */
	private synchronized void InitUserDb()  throws Exception
	{
		if (!m_UserPrefsDbExists) {
			
		    File dbFile = new File(m_userPrefsDbPath);
		    
			if (!dbFile.exists())
			{
			    Class.forName("org.sqlite.JDBC");
			    
			    Connection conn = null;
			    try {
			      conn = DriverManager.getConnection("jdbc:sqlite:" + m_userPrefsDbPath);
	
			      Statement stmt = conn.createStatement();
			      stmt.setQueryTimeout(30);  // set timeout to 30 sec.
			      
			      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS preferences (" +
			      		"UserUID 		TEXT, " +
			      		"Key 			TEXT, " +
			      		"Value 			TEXT, " +
			      		"ContentType 	TEXT, " +
			      		"IsFile			NUMERIC default 0, " +
			      		"FilePath 		TEXT, " +
			      		"LastUpdated 	DATETIME default current_timestamp, " +
			      		"UNIQUE(UserUID,Key) ON CONFLICT REPLACE)");
			      stmt.close();
			      
				  m_UserPrefsDbExists = true;
		
			    } catch(SQLException e) {
			    	m_logger.error("Failed to create database {} e={}.", m_userPrefsDbPath, e);
					throw new Exception("Application error, cannot create the database '" + m_userPrefsDbPath + "'", e);
			    } finally {
			      try {
			        if(conn != null)
			          conn.close();
			      } catch(SQLException e) {
			    	  m_logger.error("Failed to close database connection to {} e={}", m_userPrefsDbPath, e);			        
			      }
			    }
		    
			}		    
		}
	}
}
