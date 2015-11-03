/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.dal;

import java.util.List;

import com.absolute.am.dal.model.MobileAction;

/**
 * @author rchen
 * 
 */

public interface IActionHandler {
	/**
	 * Get all the details for a given action
	 * @param actionId
	 * @return
	 * @throws Exception
	 */
	public MobileAction getAction(long actionId) throws Exception;
	public String[] getActionUniqueIdsForActionNames(String actionName)
			throws Exception;
	public String[] getActionUniqueIds(List<Long> actionIds)
			throws Exception;
}
