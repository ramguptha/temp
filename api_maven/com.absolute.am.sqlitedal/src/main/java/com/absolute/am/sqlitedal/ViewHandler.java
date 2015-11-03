/**
 * 
 */
package com.absolute.am.sqlitedal;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.dal.IViewHandler;
import com.absolute.am.dal.ResultSet;
import com.absolute.am.dal.Row;
import com.absolute.am.dal.ViewConstants;
import com.absolute.am.model.command.GenericView;
import com.absolute.am.sqlitedal.ViewConfigMgr;
import com.absolute.util.PropertyList;

/**
 * @author dlavin
 *
 */
public class ViewHandler implements IViewHandler {
	
    private static Logger m_logger = LoggerFactory.getLogger(ViewHandler.class.getName()); 

	private DatabaseHelper m_databaseHelper;		
	private HashMap<String, PropertyList> m_plists;
	private Properties m_runtimeProperties;
	
	private final String PLIST_FILE_WEB_STANDARD_VIEW_CONFIGURATIONS = "WebAPI_StandardViewDefinitions.xml";	
	private final String PLIST_FILE_EXTENDED_INFORMATION_ITEMS = "ExtendedInfoItems.xml";
	private final String PLIST_FILE_DATABASE_RELATIONS = "DatabaseRelations.xml";
	private final String PLIST_FILE_EXTENDED_DATABASE_RELATIONS = "ExtendedDatabaseRelations.xml";
	private final String PLIST_FILE_INFORMATION_ITEMS = "InformationItems.xml";	
	private static EnumHandler m_enumHandler;
	
	public ViewHandler(Properties runtimeProperties) throws ParserConfigurationException, SAXException, IOException {
		
		FileInputStream is;
		m_plists = new HashMap<String, PropertyList>();
		String viewConfigPath = (String)runtimeProperties.get(Dal.PROP_VIEW_CONFIG_FOLDER);
		ArrayList<String[]> plistNameList = new ArrayList<String[]>();
		plistNameList.add(new String[]{"WebViewDefnList", PLIST_FILE_WEB_STANDARD_VIEW_CONFIGURATIONS});
		plistNameList.add(new String[]{"InfoItemsList", PLIST_FILE_INFORMATION_ITEMS});
		plistNameList.add(new String[]{"DbRelations", PLIST_FILE_DATABASE_RELATIONS});
		plistNameList.add(new String[]{"ExtendedDbRelations", PLIST_FILE_EXTENDED_DATABASE_RELATIONS});
		plistNameList.add(new String[]{"ExtendedInfoItemsList", PLIST_FILE_EXTENDED_INFORMATION_ITEMS});
		
		for(String[] plistInfo : plistNameList){
			is = new FileInputStream(viewConfigPath + "/" + plistInfo[1]);
			m_plists.put(plistInfo[0], PropertyList.fromInputStream(is));
			is.close();
		}
		
		m_runtimeProperties = runtimeProperties;
		m_databaseHelper = new DatabaseHelper(runtimeProperties);
		m_enumHandler = new EnumHandler(runtimeProperties, false); 
	}    
	
	public ResultSet queryViewColumnMetaData(String viewInfo, String dbLocaleSuffix) throws Exception {
		MDC.put("ViewInfo", viewInfo);
		ViewConfigMgr viewConfigMgr = new ViewConfigMgr(m_runtimeProperties, m_plists, m_enumHandler);
		com.absolute.am.dal.ResultSet returnResult = new ResultSet();
		returnResult.setColumnMetaData(viewConfigMgr.getColumnMetaDataForView(viewInfo, dbLocaleSuffix));
		
		MDC.remove("ViewInfo");
        return returnResult;
	}

	@Override
	public ResultSet queryAdHocView(GenericView view, HashMap<String, String> uiParams, ArrayList<String> userParams, String dbLocaleSuffix) throws Exception{
		
		ViewConfigMgr viewConfigMgr = new ViewConfigMgr(m_runtimeProperties, m_plists, m_enumHandler);
		String viewInfo = viewConfigMgr.addNewAdHocViefDef(view.guids, view.rootTable, view.sortBy, view.sortDir, view.filter);
		
		return queryView(viewInfo, uiParams, userParams, dbLocaleSuffix);
	}
	
	/* (non-Javadoc)
	 * @see com.absolute.am.dal.IQuery#QueryView(java.lang.String)
	 */
	@Override
	public ResultSet queryView(String viewInfo,
			HashMap<String, String> uiParams, ArrayList<String> userParams, String dbLocaleSuffix) throws Exception {
		// view names are case insensitive.
		MDC.put("ViewInfo", viewInfo);
		ViewConfigMgr viewConfigMgr = new ViewConfigMgr(m_runtimeProperties, m_plists, m_enumHandler);
		com.absolute.am.dal.ResultSet returnResult = new ResultSet();
		returnResult.setColumnMetaData(viewConfigMgr.getColumnMetaDataForView(viewInfo, dbLocaleSuffix));
		
		String sqlSelectQuery = viewConfigMgr.genSqlForView(viewInfo, uiParams, userParams, dbLocaleSuffix);
		m_logger.debug("genSqlForView SQL statment = " + sqlSelectQuery);

		boolean loadExtensions = false;
		String sqlSelectCountQuery = null;
		String inlineCount = null;
		if (uiParams != null) {
			inlineCount = uiParams.get(ViewConstants.PARAM_INLINE_COUNT);
		}
		if (inlineCount !=  null && inlineCount.equalsIgnoreCase(ViewConstants.PARAM_INLINE_COUNT_ALL_PAGES)) {
			sqlSelectCountQuery = viewConfigMgr.genSelectCountQuery(sqlSelectQuery);
			m_logger.debug("GenSQL SQL allpages statment = " + sqlSelectCountQuery);
		}

		Connection connection = null;
		Statement statement = null;
		java.sql.ResultSet resultSet = null;
		try {
			// certain user search criteria forces us to use the FormatVersion SQL function which
			// is defined from loadable SQLite extensions
		    Matcher m = Pattern.compile("Format[a-zA-Z]+[(]").matcher(sqlSelectQuery);
			if(m.find()){
				loadExtensions = true;
			}
			
			connection = m_databaseHelper.connectToMainDatabase(loadExtensions);
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec. // TODO: make this configurable.

			// attach to any logical databases needed for this query
			m_databaseHelper.AttachToLogicalDatabasesForQuery(statement, sqlSelectQuery);
			
			// execute the query
			resultSet = statement.executeQuery(sqlSelectQuery);
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			while (resultSet.next()) {
				Object[] dataItems = new Object[rsMetaData.getColumnCount()];
				for (int i=0; i<rsMetaData.getColumnCount(); i++) {
					dataItems[i] = resultSet.getObject(i+1);	// columns are indexed from 1
				}
				Row row = new Row(dataItems);
				returnResult.addRow(row);
			}
			resultSet.close();
			resultSet = null;
			
			if (sqlSelectCountQuery != null) {
				resultSet = statement.executeQuery(sqlSelectCountQuery);
				resultSet.next();
				int count = resultSet.getInt(1);
				returnResult.setTotalRowsAvailable(count);
			} else {
				returnResult.setTotalRowsAvailable(0);
			}

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
		MDC.remove("ViewInfo");

        return returnResult;
	}
}
