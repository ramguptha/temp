/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
 * Reproduction or transmission in whole or in part, in any form or by any means,
 * electronic, mechanical or otherwise, is prohibited without the prior written
 * consent of the copyright owner.
 */
package com.absolute.am.sqlitedal;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.dal.IComputerAdminAccessHandler;

/**
 * @author maboulkhoudoud
 * 
 */
public class ComputerAdminAccessHandler implements IComputerAdminAccessHandler {

	private static Logger m_logger = LoggerFactory.getLogger(ComputerAdminAccessHandler.class.getName());
	private static HashMap<String, String> m_adminUUIDToDataSignatures = new HashMap<String, String>();

	private DatabaseHelper m_databaseHelper;

	public ComputerAdminAccessHandler(Properties runtimeProperties) {

		m_databaseHelper = new DatabaseHelper(runtimeProperties);
	}

	private Connection getConnection() throws Exception {
		return m_databaseHelper.connectToMainDatabase();
	}

	/**
	 * SQL INSERT statement to populate the admin_computer_info table with the
	 * agent_info_record_id values found in the admin_appointments_agents table.
	 * These are computers that have been manually assigned to the administrator.
	 */
	private final String SQL_INSERT_ADMIN_AND_COMPUTER_FOR_MANUAL_APPOINTMENT = "insert into admin_computer_info (AdminUUID, agent_info_record_id) "
			+ "select "
			+ "    '%1$s' as AdminUUID, agent_info.id as agent_info_record_id "
			+ "from "
			+ "    admin_appointments_agents "
			+ "left join "
			+ "    agent_info on agent_info.AgentSerial = admin_appointments_agents.AgentSerial "
			+ "where "
			+ "    admin_appointments_agents.admin_appointments_record_id = %2$d ";

	/**
	 * SQL INSERT statement to populate the admin_computer_info table with the
	 * computer_info_record_id values returned by the filter query associated
	 * with the administrative group.
	 */
	private final String SQL_INSERT_ADMIN_AND_COMPUTER_BY_FILTER = "insert into admin_computer_info (AdminUUID, agent_info_record_id) "
			+ "select '%1$s' as AdminUUID, __computer_info_id as agent_info_record_id from (%2$s)";

	private final String SQL_SELECT_APPOINTMENTS_FOR_ADMIN = "SELECT "
			+ "admin_appointments.id, "
			+ "admin_appointments.FilterQuery "
			+ "FROM "
			+ "	ump.admins "
			+ "LEFT JOIN "
			+ "admin_appointments_admins ON admin_appointments_admins.admins_record_id = admins.id "
			+ "LEFT JOIN "
			+ "admin_appointments ON admin_appointments.id = admin_appointments_admins.admin_appointments_record_id "
			+ "WHERE "
			+ "admin_appointments_admins.admins_record_id in (SELECT id FROM admins where AdminUUID='%1$s') "
			+ "AND admin_appointments.DeviceType = 1"; // 1 is Computer.

	private final String SQL_SELECT_NEWEST_ROWID = "select max(rowid) from admin_computer_info where AdminUUID='%1$s'";
	private final String SQL_DELETE_OLDEST_ADMIN_ENTRIES = "delete from admin_computer_info where AdminUUID='%1$s' and rowid <= %2$d";

	/**
	 * SQL INSERT statement to populate the admin_computers_webapi table with
	 * the agent_info_record_id values found in the
	 * admin_appointments_agents table. These are computers that
	 * administrators have been manually assigned to.
	 */
	private final String SQL_INSERT_ALL_MANUALLY_ASSIGNED_ADMINS = "INSERT INTO admin_computers_webapi (AdminUUID, agent_info_record_id) "
			+ "SELECT admins.AdminUUID, agent_info.id "
			+ "FROM admin_appointments_agents "
			+ "LEFT JOIN admin_appointments_admins "
			+ "ON admin_appointments_agents.admin_appointments_record_id = admin_appointments_admins.admin_appointments_record_id "
			+ "LEFT JOIN admins ON admin_appointments_admins.admins_record_id = admins.id "
			+ "LEFT JOIN agent_info ON admin_appointments_agents.AgentSerial = agent_info.AgentSerial ";

	/**
	 * SQL INSERT statement to populate the admin_computers_webapi table with
	 * the values for given computer id, found in the
	 * admin_appointments_computers table. These are administrators that have
	 * been manually assigned to given computers.
	 */
	private final String SQL_INSERT_ALL_MANUALLY_ASSIGNED_ADMINS_FOR_COMPUTER = SQL_INSERT_ALL_MANUALLY_ASSIGNED_ADMINS
			+ " WHERE agent_info.id = %1$d";

	/**
	 * SQL SELECT statement to get AdminUUIDs and respective computer selection
	 * SQL queries for smart appointments.
	 */
	private final String SQL_SELECT_SMART_APPOINTMENT_ADMINS_AND_COMPUTERS_QUERIES = "SELECT a.AdminUUID, aa.FilterQuery "
			+ "FROM admin_appointments_admins aaa "
			+ "INNER JOIN admin_appointments aa ON aa.id = aaa.admin_appointments_record_id "
			+ "INNER JOIN admins a ON a.id = aaa.admins_record_id "
			+ "WHERE aa.FilterQuery IS NOT NULL " + "AND aa.DeviceType=1";

	/**
	 * SQL DELETE statement to delete records from admin_computers_webapi table
	 * for given computer id.
	 */
	private final String SQL_DELETE_ADMINS_FOR_COMPUTER = "DELETE FROM admin_computers_webapi WHERE agent_info_record_id = %1$s";

	/**
	 * SQL INSERT statement to populate the admin_computers_webapi table with
	 * the values for given computer id, found in the admin_appointments_agents
	 * table. These are administrators that have been assigned to given
	 * computer.
	 */
	private final String SQL_INSERT_ADMIN_AND_COMPUTER_FOR_SMART_APPOINTMENT = "INSERT INTO admin_computers_webapi (AdminUUID, agent_info_record_id) "
			+ "VALUES ('%1$s', %2$d)";

	@Override
	public void prepareAccessForAdmin(String adminUUID) throws Exception {
		String currentSignature = getCurrentSignature(adminUUID);
		prepareAccessForAdmin(adminUUID, true, currentSignature);
	}

	/**
	 * Internal helper method to prepare the data mapping what computers an Admin
	 * has access to.
	 * 
	 * @param adminUUID
	 *            the AdminUUID of the user
	 * @param wipeOldRecords
	 *            when true, the new mappings will be created, and then the old
	 *            mappings will be deleted
	 * @param currentSignature
	 *            this is the signature of the data used to determine if the map
	 *            needs to change, it will be saved once the data is inserted
	 * @throws Exception
	 */
	private void prepareAccessForAdmin(String adminUUID,
			boolean wipeOldRecords, String currentSignature) throws Exception {

		// when adminUUID is null, there are no access restrictions.
		if (adminUUID != null && adminUUID.length() > 0) {

			synchronized (WebAPIDB.getInstance()) {

				Statement stmt = null;
				Connection conn = getConnection();

				try {

					java.sql.ResultSet rs = null;
					stmt = conn.createStatement();
					long previousNewestRowid = -1;

					try {

						if (wipeOldRecords) {
							String sqlQuery = String.format(
									SQL_SELECT_NEWEST_ROWID, adminUUID);

							m_databaseHelper.AttachToLogicalDatabasesForQuery(
									stmt, sqlQuery);

							rs = stmt.executeQuery(sqlQuery);
							while (rs.next()) {
								previousNewestRowid = rs.getLong(1);
								break;
							}
						}
					} finally {
						if (null != rs) {
							rs.close();
							rs = null;
						}
					}

					String sqlQuery = String.format(
							SQL_SELECT_APPOINTMENTS_FOR_ADMIN, adminUUID);

					if (!wipeOldRecords) { // else already attached
						m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
								sqlQuery);
					}

					// A map of assignments and their filter query strings
					HashMap<Integer, String> idToFilterMap = new HashMap<Integer, String>();
					try {
						MDC.put("sql", sqlQuery);
						rs = stmt.executeQuery(sqlQuery);

						while (rs.next()) {
							// m_logger.debug("id={} FilterQuery={}",
							// rs.getInt("id"), rs.getString("FilterQuery"));
							idToFilterMap.put(rs.getInt("id"),
									rs.getString("FilterQuery"));
						}
						MDC.remove("sql");
					} finally {
						if (null != rs) {
							rs.close();
							rs = null;
						}
					}

					// iterate through the results, retrieving the ids
					for (Integer admin_appointments_record_id : idToFilterMap
							.keySet()) {

						String insertStatement = null;
						String filterQuery = idToFilterMap
								.get(admin_appointments_record_id);

						// manual assignment
						if (filterQuery == null
								|| (filterQuery != null && filterQuery.length() == 0)) {
							insertStatement = String
									.format(SQL_INSERT_ADMIN_AND_COMPUTER_FOR_MANUAL_APPOINTMENT,
											adminUUID,
											admin_appointments_record_id);
						} else { // smart group assignment
							insertStatement = String.format(
									SQL_INSERT_ADMIN_AND_COMPUTER_BY_FILTER,
									adminUUID, filterQuery);
						}

						MDC.put("sql", insertStatement);
						stmt.executeUpdate(insertStatement);
						MDC.remove("sql");
					}

					if (wipeOldRecords && previousNewestRowid != -1) {

						String wipeStatement = String.format(
								SQL_DELETE_OLDEST_ADMIN_ENTRIES, adminUUID,
								previousNewestRowid);

						m_logger.debug("admin access wipe statement={}",
								wipeStatement);
						MDC.put("sql", wipeStatement);
						stmt.executeUpdate(wipeStatement);
						MDC.remove("sql");

					}

				} finally {

					if (null != stmt) {
						stmt.close();
					}

					if (null != conn) {
						conn.close();
					}
				}

				m_adminUUIDToDataSignatures.put(adminUUID, currentSignature);
			}
		}
	}

	/**
	 * Query used to determine if an Admin has access to a specific computerid.
	 */
	private final String SQL_SELECT_IF_ADMIN_HAS_ACCESS = "select count(*) as assigned from admin_computer_info where adminuuid='%1$s' and agent_info_record_id=%2$d";

	public boolean adminHasAccessToComputer(String adminUUID, long computerId)
			throws Exception {

		Connection conn = getConnection();
		boolean retVal = false; // assume that access is denied

		if (adminUUID != null && adminUUID.length() > 0) {

			try {
				long assigned = 0;
				String sqlQuery = String.format(SQL_SELECT_IF_ADMIN_HAS_ACCESS,
						adminUUID, computerId);

				java.sql.ResultSet rs = null;
				Statement stmt = conn.createStatement();
				try {
					m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
							sqlQuery);
					MDC.put("sql", sqlQuery);
					rs = stmt.executeQuery(sqlQuery);

					while (rs.next()) {
						assigned = rs.getLong("assigned");
						break;
					}
					MDC.remove("sql");
				} finally {
					rs.close();
					stmt.close();
				}

				// If the computer has been assigned to this Admin, then they are
				// granted access
				if (assigned > 0) {
					retVal = true;
				}

			} finally {
				if (null != conn)
					conn.close();
			}
		} else {
			// if there is no adminUUID, then access is allowed.
			retVal = true;
		}

		return retVal;
	}

	/**
	 * Query to calculate a signature of the data that is likely to impact Admin
	 * access to computers. When this signature changes, the system regenerates
	 * the list of computers the Admin has access to.
	 */
	private static final String SQL_GET_DATA_SIGNATURE = "select group_concat(x) from ( "
			+ "select max(rowid) as x from agent_info union "
			+ "select max(last_modified) from admin_appointments union "
			+ "select max(last_modified) from admin_appointments_agents)";

	@Override
	public void refreshAccessForAdmin(String adminUUID) throws Exception {

		// when adminUUID is null, there are no access restrictions
		if (adminUUID != null && adminUUID.length() > 0) {
			String currentSignature = getCurrentSignature(adminUUID);

			boolean fullRefreshHappened = false;
			boolean adminUUIDKeyIsKnown = false;
			synchronized (WebAPIDB.getInstance()) {
				adminUUIDKeyIsKnown = m_adminUUIDToDataSignatures
						.containsKey(adminUUID);
				if (!adminUUIDKeyIsKnown || 
						m_adminUUIDToDataSignatures.get(adminUUID) == null ||
						(adminUUIDKeyIsKnown && m_adminUUIDToDataSignatures
						.get(adminUUID).compareToIgnoreCase(currentSignature) != 0)) {
					prepareAccessForAdmin(adminUUID, adminUUIDKeyIsKnown,
							currentSignature);
					fullRefreshHappened = true;
				}
			}

			m_logger.debug(
					"currentSignatureValue={} adminUUIDKeyIsKnown={} fullRefreshHappened={}",
					currentSignature, adminUUIDKeyIsKnown, fullRefreshHappened);
		}
	}

	/**
	 * Populates webapi.admin_computers_webapi table with data for all
	 * computers. Currently not being used.
	 */
	@Override
	public void refreshAllAdmins() throws Exception {

		synchronized (WebAPIDB.getInstance()) {

			Statement stmt = null;
			Connection conn = getConnection();

			try {

				stmt = conn.createStatement();

				String sqlQuery = "DELETE FROM admin_computers_webapi";
				m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
						sqlQuery);

				MDC.put("sql", sqlQuery);
				stmt.executeUpdate(sqlQuery);
				MDC.remove("sql");

				// populate data for manually assigned admins
				sqlQuery = SQL_INSERT_ALL_MANUALLY_ASSIGNED_ADMINS;
				MDC.put("sql", sqlQuery);
				stmt.executeUpdate(sqlQuery);
				MDC.remove("sql");

			} finally {

				if (null != stmt) {
					stmt.close();
				}

				if (null != conn) {
					conn.close();
				}
			}

			// populate data for admins assigned via smart appointment groups
			populateAllSmartAppointmentAdmins();
		}
	}

	/**
	 * Populates webapi.admin_computers_webapi table with data for given
	 * computer.
	 * 
	 */
	@Override
	public void refreshAdminsForComputer(long computerId) throws Exception {

		synchronized (WebAPIDB.getInstance()) {

			String deleteStatement = String.format(
					SQL_DELETE_ADMINS_FOR_COMPUTER, computerId);
			String insertStatement = String.format(
					SQL_INSERT_ALL_MANUALLY_ASSIGNED_ADMINS_FOR_COMPUTER,
					computerId);

			Statement stmt = null;
			Connection conn = getConnection();

			try {

				stmt = conn.createStatement();

				String sqlQuery = deleteStatement;
				m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
						sqlQuery);

				MDC.put("sql", sqlQuery);
				stmt.executeUpdate(sqlQuery);
				MDC.remove("sql");

				// populate data for manually assigned admins
				sqlQuery = insertStatement;
				MDC.put("sql", sqlQuery);
				stmt.executeUpdate(sqlQuery);
				MDC.remove("sql");

			} finally {

				if (null != stmt) {
					stmt.close();
				}

				if (null != conn) {
					conn.close();
				}
			}

			// populate data for admins assigned via smart appointment groups
			populateSmartAppointmentAdminsForComputer(computerId);
		}
	}

	private void populateAllSmartAppointmentAdmins() throws Exception {

		synchronized (WebAPIDB.getInstance()) {

			Statement stmt = null;
			Connection conn = getConnection();

			try {

				java.sql.ResultSet rs = null;
				stmt = conn.createStatement();

				String sqlQuery = SQL_SELECT_SMART_APPOINTMENT_ADMINS_AND_COMPUTERS_QUERIES;

				m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
						sqlQuery);

				// A map of admin UUIDs and respective computer selection queries
				HashMap<String, String> adminUuidToComputersSelectionSql = new HashMap<String, String>();
				try {
					MDC.put("sql", sqlQuery);
					rs = stmt.executeQuery(sqlQuery);
					MDC.remove("sql");
					while (rs.next()) {
						String uuid = rs.getString("AdminUUID");
						String query = rs.getString("FilterQuery");
						if (uuid.length() > 0 && query.length() > 0) {
							adminUuidToComputersSelectionSql.put(uuid, query);
						}
					}

				} finally {
					if (null != rs) {
						rs.close();
						rs = null;
					}
				}

				String insertStatement = null;

				// iterate through the results, retrieving the computer ids
				for (Map.Entry<String, String> item : adminUuidToComputersSelectionSql
						.entrySet()) {

					String uuid = item.getKey();
					sqlQuery = item.getValue();

					// A map of admin UUIDs and respective computer IDs
					List<Long> computerIds = new ArrayList<Long>();
					try {
						MDC.put("sql", sqlQuery);
						rs = stmt.executeQuery(sqlQuery);
						MDC.remove("sql");
						while (rs.next()) {
							Long computerInfoId = rs.getLong("__agent_info_id");
							computerIds.add(computerInfoId);
						}
					} finally {
						if (null != rs) {
							rs.close();
							rs = null;
						}
					}

					for (Long devId : computerIds) {
						if (null != devId) {
							insertStatement = String
									.format(SQL_INSERT_ADMIN_AND_COMPUTER_FOR_SMART_APPOINTMENT,
											uuid, devId);
							MDC.put("sql", insertStatement);
							stmt.executeUpdate(insertStatement);
							MDC.remove("sql");
						}
					}
				}

			} finally {

				if (null != stmt) {
					stmt.close();
				}

				if (null != conn) {
					conn.close();
				}
			}
		}
	}

	private void populateSmartAppointmentAdminsForComputer(long computerId)
			throws Exception {

		synchronized (WebAPIDB.getInstance()) {

			Statement stmt = null;
			Connection conn = getConnection();

			try {

				java.sql.ResultSet rs = null;
				stmt = conn.createStatement();

				String sqlQuery = SQL_SELECT_SMART_APPOINTMENT_ADMINS_AND_COMPUTERS_QUERIES;

				m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
						sqlQuery);

				// A map of admin UUIDs and respective computer selection queries
				HashMap<String, String> adminUuidToComputersSelectionSql = new HashMap<String, String>();
				try {
					MDC.put("sql", sqlQuery);
					rs = stmt.executeQuery(sqlQuery);
					MDC.remove("sql");
					while (rs.next()) {
						String uuid = rs.getString("AdminUUID");
						String query = rs.getString("FilterQuery");
						if (uuid.length() > 0 && query.length() > 0) {
							adminUuidToComputersSelectionSql.put(uuid, query);
						}
					}

				} finally {
					if (null != rs) {
						rs.close();
						rs = null;
					}
				}

				String insertStatement = null;

				// iterate through the results, retrieving the computer ids
				for (Map.Entry<String, String> item : adminUuidToComputersSelectionSql
						.entrySet()) {

					String uuid = item.getKey();
					sqlQuery = item.getValue();

					// A map of admin UUIDs and respective computer IDs
					List<Long> computerIds = new ArrayList<Long>();
					try {
						MDC.put("sql", sqlQuery);
						rs = stmt.executeQuery(sqlQuery);
						MDC.remove("sql");
						while (rs.next()) {
							Long agentInfoId = rs.getLong("__agent_info_id");
							computerIds.add(agentInfoId);
						}
					} finally {
						if (null != rs) {
							rs.close();
							rs = null;
						}
					}

					for (Long devId : computerIds) {
						// only populate data for given computer
						if (computerId == devId) {
							insertStatement = String
									.format(SQL_INSERT_ADMIN_AND_COMPUTER_FOR_SMART_APPOINTMENT,
											uuid, devId);
							MDC.put("sql", insertStatement);
							stmt.executeUpdate(insertStatement);
							MDC.remove("sql");
						}
					}
				}

			} finally {

				if (null != stmt) {
					stmt.close();
				}

				if (null != conn) {
					conn.close();
				}
			}

		}
	}

	/**
	 * Helper method to get the current signature of the data used to calculate
	 * what computers an Admin has access to.
	 * 
	 * @param adminUUID
	 * @return
	 * @throws Exception
	 */
	private String getCurrentSignature(String adminUUID) throws Exception {

		// A "signature" is used to check if the database has changed
		// significantly since the last refresh.
		// If there is no signature, then a refresh is required.
		// If there was a signature, then calculate the current signature and
		// compare it to the previous one.
		// If they are the same, do nothing. Else refresh the data.
		String currentSignatureValue = "";
		Connection conn = getConnection();

		try {

			java.sql.ResultSet rs = null;
			Statement stmt = conn.createStatement();

			try {
				String sqlQuery = SQL_GET_DATA_SIGNATURE;

				m_databaseHelper.AttachToLogicalDatabasesForQuery(stmt,
						sqlQuery);

				rs = stmt.executeQuery(sqlQuery);
				while (rs.next()) {
					currentSignatureValue = rs.getString(1);
					break;
				}

			} finally {
				if (rs != null) {
					rs.close();
				}

				stmt.close();
			}

		} finally {
			if (null != conn)
				conn.close();
		}

		return currentSignatureValue;
	}
}
