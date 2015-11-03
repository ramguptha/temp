/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.webapi.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.ResultSet;
import com.absolute.am.dal.ViewConstants;
import com.absolute.am.model.MetaData;
import com.absolute.am.model.Result;
import com.absolute.am.model.command.GenericView;

public class ViewHelper {
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ViewHelper.class.getName()); 
        
	public static final String VH_VIEW_NAME_ALL = "all";
	public static final String VH_VIEW_MAC_ONLY = "macs";
	public static final String VH_VIEW_PC_ONLY = "pcs";
	public static final String VH_VIEW_HARDWARE = "hardware";
	public static final String VH_VIEW_SOFTWARE = "software";
	public static final String VH_VIEW_SYSTEM_SOFTWARE = "systemsoftware";
	public static final String VH_VIEW_AGENT_INFO = "agentinfo";
	public static final String VH_VIEW_NETWORK_ADAPTERS = "networkadapters";
	public static final String VH_VIEW_CPU = "cpu";
	public static final String VH_VIEW_VOLUMES = "volumes";
	public static final String VH_VIEW_MISSING_PATCHES = "missingpatches";
	public static final String VH_VIEW_INSTALLED_PROFILES = "installedprofiles";
	public static final String VH_VIEW_INVENTORY_SERVERS = "inventoryservers";
	public static final String VH_VIEW_ADMINISTRATORS = "administrators";
	public static final String VH_VIEW_MEMORY = "memory";

	/**
	 * Prevent creating new instances.
	 */
	private ViewHelper(){};
	
	public static Result getAdHocViewDetails(
			IDal dal, 
			GenericView view,
			MultivaluedMap<String, String> uiParams, 
			ArrayList<String> userParams,
			String dbLocaleSuffix) throws Exception{
		HashMap<String, String> paramMap = getParamMap(uiParams);

		ResultSet resultSet = dal.getViewHandler().queryAdHocView(view, paramMap, userParams, dbLocaleSuffix);

		Result res = new Result();
		res.setRows(resultSet.getRowsAsObjectArrays());

		MetaData metaData = new MetaData();
		metaData.setTotalRows(resultSet.getTotalRowsAvailable());
		metaData.setColumnMetaData(resultSet.getColumnMetaData());
		res.setMetaData(metaData);

		return res;
	}
	
	public static Result getViewDetails(
			IDal dal, 
			String viewInfo,
			MultivaluedMap<String, String> uiParams, 
			ArrayList<String> userParams,
			String dbLocaleSuffix
			) throws Exception{
		HashMap<String, String> paramMap = getParamMap(uiParams);

		ResultSet resultSet = dal.getViewHandler().queryView(viewInfo, paramMap, userParams, dbLocaleSuffix);

		Result res = new Result();
		res.setRows(resultSet.getRowsAsObjectArrays());

		MetaData metaData = new MetaData();
		metaData.setTotalRows(resultSet.getTotalRowsAvailable());
		metaData.setColumnMetaData(resultSet.getColumnMetaData());
		res.setMetaData(metaData);

		return res;
	}
	
	public static ResultSet getViewResultSet(IDal dal, String viewName, 
			ArrayList<String> userParams, String dbLocaleSuffix) throws Exception {
		ResultSet resultSet = dal.getViewHandler().queryView(viewName, null, userParams, dbLocaleSuffix);
		return resultSet;
	}
	
	private static String getIntParamAsString(String paramValue, String paramDefault) {
		String returnParam = paramDefault;
		if (paramValue != null) {
			Integer topAsInt = Integer.parseInt(paramValue);
			returnParam = topAsInt.toString();
		}
		return returnParam;
	}

	/**
	 * Helper method to return the query parameters in a MultivaluedMap. This will also insert
	 * the FILTER_BY_ADMIN parameter associated with the session, if applicable.
	 * @param uriInfo the UriInfo object of the request.
	 * @param session the current session
	 * @return A MultivaluedMap of query parameters.
	 */
	public static MultivaluedMap<String, String> getQueryParameters(UriInfo ui, HttpSession session) {
		MultivaluedMap<String, String> retVal = ui.getQueryParameters();
		String filterByAdmin = SessionState.getFilterByAdmin(session);
		if (filterByAdmin != null) {
			retVal.add(ViewConstants.PARAM_FILTER_BY_ADMIN, filterByAdmin);
		}
		return retVal;
	}
	
	public static Result getViewColumnMetaDataResultSet(IDal dal, String viewName, String dbLocaleSuffix) throws Exception {
		ResultSet resultSet = dal.getViewHandler().queryViewColumnMetaData(viewName, dbLocaleSuffix);
		
		Result result = new Result();
		MetaData metaData = new MetaData();
		metaData.setColumnMetaData(resultSet.getColumnMetaData());
		result.setMetaData(metaData);

		return result;
	}
	
	private static HashMap<String, String> getParamMap(MultivaluedMap<String, String> uiParams){
		HashMap<String, String> paramMap = null;
		
		if (uiParams != null) {
			paramMap = new HashMap<String, String>();
			Iterator<String> it = uiParams.keySet().iterator();
		    while (it.hasNext()) {
		    	String key = (String) it.next();
		    	if (key.compareToIgnoreCase(ViewConstants.PARAM_TOP) == 0) {
					paramMap.put(ViewConstants.PARAM_TOP, getIntParamAsString(uiParams.getFirst(ViewConstants.PARAM_TOP), 
							ViewConstants.PARAM_TOP_DEFAULT_VALUE));
		    	} else if (key.compareToIgnoreCase(ViewConstants.PARAM_SKIP) == 0) {
					paramMap.put(ViewConstants.PARAM_SKIP, getIntParamAsString(uiParams.getFirst(ViewConstants.PARAM_SKIP), 
							ViewConstants.PARAM_SKIP_DEFAULT_VALUE));
		    	} else {
					paramMap.put(key, uiParams.getFirst(key));
		    	}
		    }
		}
		
		return paramMap;
	}
}
