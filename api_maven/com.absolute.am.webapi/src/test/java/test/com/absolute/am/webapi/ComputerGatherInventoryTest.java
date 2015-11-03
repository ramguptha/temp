/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package test.com.absolute.am.webapi;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.Computers;
import test.com.absolute.testutil.Helpers;

/**
 * @author maboulkhoudoud
 *
 */
public class ComputerGatherInventoryTest extends LoggedInTest {
	private static final String GATHER_INVENTORY_COMMAND_API = COMPUTER_COMMANDS_API
			+ "/gatherinventory";
	
	String[] m_computerSerials;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		m_computerSerials = Helpers.getComputerSerialsForComputerNames(logonCookie, Computers.COMPUTER_NAMES[0]);
		
		can_gather_inventory_to_computers();
		cant_gather_inventory_empty_deviceid_list();
	}

	public void can_gather_inventory_to_computers() throws Exception {
		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"serialNumbers\":[\"");
		sb.append(m_computerSerials[0]);
		sb.append("\"],");
		sb.append("\"fullInv\":true,");
		sb.append("\"withFonts\":false,");
		sb.append("\"withPrinters\":false,");
		sb.append("\"withServices\":false,");
		sb.append("\"withStartupItems\":false");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL
				+ GATHER_INVENTORY_COMMAND_API, sb.toString(),
				HttpStatus.SC_NO_CONTENT,
				HttpStatus.SC_NO_CONTENT
				);
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}

	public void cant_gather_inventory_empty_deviceid_list() throws Exception {

		StringBuilder sb = new StringBuilder();
		// an empty post
		sb.append("{");
		sb.append("\"serialNumbers\":[");
		sb.append("],");
		sb.append("\"fullInv\":true,");
		sb.append("\"withFonts\":false,");
		sb.append("\"withPrinters\":false,");
		sb.append("\"withServices\":false,");
		sb.append("\"withStartupItems\":false");
		sb.append("}");

		System.out.println("Request body=" + sb.toString());

		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL
				+ GATHER_INVENTORY_COMMAND_API, sb.toString(), HttpStatus.SC_BAD_REQUEST);

		//test passed if reached this line
		Assert.assertTrue(true);
	}
}
