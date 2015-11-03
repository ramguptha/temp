/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.dal;

import java.util.ArrayList;
import java.util.HashMap;
import com.absolute.am.model.command.GenericView;

/**
 * @author dlavin
 * 
 */

public interface IViewHandler {

	// TODO: put desired language in here?
	// TODO: include meta data controls?
	/**
	 * Get the results associated with view viewName
	 * @param viewName: The view to query
	 * @param uiParams: user specified params like $top, $search
	 * @param userParams: view params like an id to query for
	 * @param loadExtensions: load the custom SQLite extensions if true
	 * @return
	 * @throws Exception
	 */
	public ResultSet queryView(String viewInfo, HashMap<String, String> uiParams,
			ArrayList<String> userParams, String dbLocaleSuffix) throws Exception;

	/**
	 * Query a user defined view as defined by the passed in parameters.
	 * 
	 * @param GUIDs
	 * @param rootTable
	 * @param uiParams
	 * @param userParams
	 * @param dbLocaleSuffix
	 * @return
	 * @throws Exception
	 */
	public ResultSet queryAdHocView(GenericView view, HashMap<String, String> uiParams, ArrayList<String> userParams, String dbLocaleSuffix) throws Exception;
	/**
	 * 
	 * @param viewInfo
	 * @return Returns a ResultSet with just the column metadata filled in.
	 * @throws Exception
	 */
	public ResultSet queryViewColumnMetaData(String viewInfo, String dbLocaleSuffix) throws Exception; 	

}
