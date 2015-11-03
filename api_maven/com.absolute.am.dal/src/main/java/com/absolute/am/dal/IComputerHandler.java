/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.dal;

import java.util.List;

import com.absolute.am.dal.model.ComputerInfo;

/**
 * @author rchen
 * 
 */

public interface IComputerHandler {

	/**
	 * Get all the details for a given device
	 * @param polciyId
	 * @return
	 * @throws Exception
	 */
	public ComputerInfo getComputer(long computerId) throws Exception;
	
	/**
	 * Get UniqueIds for a given list of computer Ids
	 * @param computerIds
	 * @return UniqueIds
	 * @throws Exception
	 */
	public String[] getComputerUniqueIdsAsString(List<Long> computerIds) throws Exception;
	
}
