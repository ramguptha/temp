/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;


public class BookstoreBooksTest extends LoggedInTest {
	private static final String BOOKSTORE_BOOKS_API = Helpers.WEBAPI_BASE_URL + "api/books/";
	private static final String BOOKSTORE_BOOKS_VIEWS_API = BOOKSTORE_BOOKS_API + "views";
	private static final String NON_EXISTING_BOOK_ID = "123456789";
	public static String cookie = null;
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {
		can_get_list_of_views();
		can_get_view_all();
		cannot_get_view_nonexisting();
		can_get_icon_for_existing_book_id();
		cannot_get_icon_for_non_existing_book_id();
	}

	public void can_get_list_of_views() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		StringBuilder views = new StringBuilder();
		views.append("{");
		views.append("\"viewDescriptions\":[{\"viewName\":\"all\",\"viewDisplayName\":\"All Bookstore Books\",\"id\":0}]");
		views.append("}");
		String expected = views.toString();
		
		String actual = test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				BOOKSTORE_BOOKS_VIEWS_API,
				HttpStatus.SC_OK,
				HttpStatus.SC_OK);

		boolean isSame = (actual.compareToIgnoreCase(expected) == 0);
		
		org.junit.Assert.assertTrue(isSame);
	}
	
	public void can_get_view_all() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String actual = test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				BOOKSTORE_BOOKS_VIEWS_API + "/all",
				HttpStatus.SC_OK,
				HttpStatus.SC_OK);

		boolean isExpectedResult = (actual.indexOf("{\"metaData\":{") == 0
							&& actual.indexOf("\"rows\":[") > 0
							);
		
		org.junit.Assert.assertTrue(isExpectedResult);
	}
	
	public void cannot_get_view_nonexisting() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				BOOKSTORE_BOOKS_VIEWS_API + "/nonexisting_view",
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);

		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
	
	public void can_get_icon_for_existing_book_id() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String[] bookIds = Helpers.getBookIdsForBookNames(logonCookie, "");
		
		if( bookIds.length > 0){
			test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
					logonCookie,
					BOOKSTORE_BOOKS_API + bookIds[0] + "/icon",
					HttpStatus.SC_OK,
					HttpStatus.SC_OK);
		}
		
		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
	
	public void cannot_get_icon_for_non_existing_book_id() throws IllegalStateException, IOException, KeyManagementException, NoSuchAlgorithmException {

		test.com.absolute.testutil.Helpers.doGETCheckStatusReturnBody(
				logonCookie,
				BOOKSTORE_BOOKS_API + NON_EXISTING_BOOK_ID + "/icon",
				HttpStatus.SC_NOT_FOUND,
				HttpStatus.SC_NOT_FOUND);
		
		//test passed if reached this line
		org.junit.Assert.assertTrue(true);
	}
}