/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package test.com.absolute.am.sqlitedal;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IComputerHandler;
import com.absolute.am.dal.model.ComputerInfo;

/**
 * @author rchen
 *
 */
public class TestComputerHandler {
	
	private static final long COMPUTER_ID_QAAMDP2 = 4;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_computer() throws Exception {
		IDal dal = Util.getDal();
		IComputerHandler computerHandler = dal.getComputerHandler();

		ComputerInfo computer = computerHandler.getComputer(COMPUTER_ID_QAAMDP2);
		assertTrue(computer.getId() == COMPUTER_ID_QAAMDP2);
	}
}
