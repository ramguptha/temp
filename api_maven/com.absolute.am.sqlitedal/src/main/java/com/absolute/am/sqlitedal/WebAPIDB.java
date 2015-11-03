/**
 * 
 */
package com.absolute.am.sqlitedal;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.absolute.am.dal.DalBase;

/**
 * @author dlavin
 * The purpose of this class is to initialize the WebAPI database, and to synchronize access when necessary.
 */
class WebAPIDB {
	
	private static Logger m_logger = LoggerFactory.getLogger(WebAPIDB.class.getName()); 
    
	private static WebAPIDB m_webapiDb = null;
	
	private WebAPIDB(Properties runtimeProperties) throws ClassNotFoundException, SQLException, IOException {
		
		String webapiDbFile = (String)runtimeProperties.get(DalBase.PROP_WEBAPI_DATABASE_FILE);
		if (null == webapiDbFile ||
				(webapiDbFile!= null && webapiDbFile.length()==0)) {
			throw new IllegalArgumentException("Runtime properties must include [" + DalBase.PROP_WEBAPI_DATABASE_FILE + "].");
		}		

		initWebApiDatabase(webapiDbFile);
	}
	
	private synchronized void initWebApiDatabase(String dbFile) throws ClassNotFoundException, SQLException, IOException {

		MDC.put("webapiDbFile", dbFile);

		Class.forName("org.sqlite.JDBC");

		Connection conn = null;
		try {
			// If the parent folder doesn't exist, create it. sqlite will create the file itself.
			File dbFileObj = new File(dbFile);			
			if (!dbFileObj.exists()) {
				dbFileObj = dbFileObj.getParentFile();
				if (dbFileObj != null && !dbFileObj.exists())
					dbFileObj.mkdirs();
			}
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);


			Statement stmt = conn.createStatement();
			try {
				stmt.setQueryTimeout(30); // set timeout to 30 sec.
				stmt.executeUpdate("PRAGMA journal_mode=WAL;");
				stmt.executeUpdate("PRAGMA encoding = \"UTF-16le\";");				
	
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin_iphone_info ("
									+ "AdminUUID varchar(36) COLLATE NOCASE, "
									+ "iphone_info_record_id integer,"
									+ "last_modified varchar(30) default current_timestamp, "
									+ "UNIQUE(AdminUUID,iphone_info_record_id) ON CONFLICT REPLACE)");
				
				// table to hold admin-to-mobile device records for administrators assigned both manually and via smart appointment groups
				// populated by code in AdminAccessHandler
				// the view "administratorsfordevice" reads directly from this table
				// note that non-unique entries get replaced, so that UI shows any admin name only once,
				// even when the same admin assigned both manually to specific device and using smart appointment group
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin_mobile_devices_webapi ("
									+ "AdminUUID varchar(36) COLLATE NOCASE, "
									+ "iphone_info_record_id integer,"
									+ "last_modified varchar(30) default current_timestamp, "
									+ "UNIQUE(AdminUUID,iphone_info_record_id) ON CONFLICT REPLACE)");
				
				// We need to create these same tables for their computer counterparts, since there can be ID conflicts on one table (phone vs computer)
				// we can't have a shared table at this point in time.
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin_computer_info ("
									+ "AdminUUID varchar(36) COLLATE NOCASE, "
									+ "agent_info_record_id integer,"
									+ "last_modified varchar(30) default current_timestamp, "
									+ "UNIQUE(AdminUUID,agent_info_record_id) ON CONFLICT REPLACE)");

				// table to hold admin-to-computer records for administrators assigned both manually and via smart appointment groups
				// populated by code in ComputerAdminAccessHandler
				// the view "administratorsfordevice" reads directly from this table
				// note that non-unique entries get replaced, so that UI shows any admin name only once,
				// even when the same admin assigned both manually to specific device and using smart appointment group
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin_computers_webapi ("
									+ "AdminUUID varchar(36) COLLATE NOCASE, "
									+ "agent_info_record_id integer,"
									+ "last_modified varchar(30) default current_timestamp, "
									+ "UNIQUE(AdminUUID,agent_info_record_id) ON CONFLICT REPLACE)");
				
				// Note that webapi.db gets attached before other tables and older versions of AM Web contain the table enum_ErrorMsg
				// that's now duplicated in EnumValues.db. This table must be explicitly dropped otherwise localized API calls will fail.
				stmt.executeUpdate("DROP TABLE IF EXISTS enum_ErrorMsg");
			} finally {
				stmt.close();
			}

		} finally {
			
			if (conn != null)
				conn.close();
		}
		
		String encoding = getDbEncoding(dbFile);
		m_logger.debug("encoding={}", encoding);
		MDC.remove("webapiDbFile");
	}
	
	private String getDbEncoding(String dbFile) throws ClassNotFoundException, SQLException {
		MDC.put("webapiDbFile", dbFile);
		String encoding = "";

		Class.forName("org.sqlite.JDBC");

		Connection conn = null;
		try {
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);

			Statement stmt = conn.createStatement();
			try {
				stmt.setQueryTimeout(30); // set timeout to 30 sec.
				ResultSet rs = stmt.executeQuery("PRAGMA encoding");
				while (rs.next()) {
					encoding = rs.getString(1);
					break;
				}
				rs.close();
			} finally {
				stmt.close();	
			}

		} finally {			
			if (conn != null)
				conn.close();
		}
		
		MDC.remove("webapiDbFile");
		return encoding;
	}
	
	public static WebAPIDB getInstance() {
		if (m_webapiDb == null) {
			throw new IllegalStateException("Attempt to access WebAPIDB before it is initialized.");
		}
		return m_webapiDb;
	}
	
	public static synchronized WebAPIDB getInstance(Properties runtimeProperties) throws ClassNotFoundException, SQLException, IOException {
		if (m_webapiDb == null) {
			m_webapiDb = new WebAPIDB(runtimeProperties);
		}
		return m_webapiDb;
	}

}
