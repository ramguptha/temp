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

import com.absolute.am.webapi.controllers.ViewHelper;
import com.absolute.am.webapi.util.ResourceUtilities;

import test.com.absolute.testdata.configuration.Computers;
import test.com.absolute.testutil.Helpers;

/**
 * @author rchen
 *
 */
public class ComputerGetsTest extends LoggedInTest {
	private static final String NONEXISTING_COMPUTER_ID = "123456789";
	private static final String[] COMPUTER_URLS_FOR_PC = {
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_HARDWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_CPU,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_SYSTEM_SOFTWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_VOLUMES,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_SOFTWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_MISSING_PATCHES,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_MEMORY,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_AGENT_INFO,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_NETWORK_ADAPTERS
		};
	
	private static final String[] COMPUTER_URLS_FOR_MAC = {
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_HARDWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_CPU,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_SYSTEM_SOFTWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_VOLUMES,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_SOFTWARE,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_MISSING_PATCHES,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_MEMORY,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_AGENT_INFO,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_NETWORK_ADAPTERS,
			Helpers.WEBAPI_BASE_URL + COMPUTERS_API + "/{computer_id}/" + ViewHelper.VH_VIEW_INSTALLED_PROFILES
		};
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_can_get_computer_id() throws Exception
	{
		// Tests for PC
		String[] computerIds = Helpers.getComputerIdsForComputerName(logonCookie, Computers.COMPUTER_NAMES[0]);
		for (String url : COMPUTER_URLS_FOR_PC) {
			Helpers.doGETCheckStatusReturnBody(logonCookie, url.replace("{computer_id}", computerIds[0]),
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
		
		// Tests for mac
		String[] computerIds2 = Helpers.getComputerIdsForComputerName(logonCookie, Computers.COMPUTER_NAMES[0]);
		for (String url : COMPUTER_URLS_FOR_MAC) {
			Helpers.doGETCheckStatusReturnBody(logonCookie, url.replace("{computer_id}", computerIds2[0]),
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
		
		//test passed if reached this line
		Assert.assertTrue(true);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_cannot_get_for_non_existing_computer_id() throws Exception
	{
		String expectedErrorMessage = String.format(
				ResourceUtilities.getLocalizedFormattedString("NO_COMPUTER_FOUND_FOR_ID", null, locale, ResourceUtilities.WEBAPI_BASE), 
				NONEXISTING_COMPUTER_ID);
		
		for (String url : COMPUTER_URLS_FOR_MAC) {
			String result = Helpers.doGETCheckStatusReturnBody(logonCookie, url.replace("{computer_id}", NONEXISTING_COMPUTER_ID),
					HttpStatus.SC_NOT_FOUND,
					HttpStatus.SC_NOT_FOUND);
			
			Assert.assertTrue(result.contains(expectedErrorMessage));
		}
	
		//test passed if reached this line
		Assert.assertTrue(true);
	}
}
