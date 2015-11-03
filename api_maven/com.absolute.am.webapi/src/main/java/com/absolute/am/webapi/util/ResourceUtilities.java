/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.am.webapi.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ws.rs.core.MultivaluedMap;

import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.controllers.ViewHelper;
import com.absolute.am.model.Result;
import com.absolute.util.StringUtilities;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public final class ResourceUtilities {
	
	public final static String DEFAULT_LOCALE ="en_US";
	public final static String WEBAPI_BASE = "webapi/Webapi";
	public final static String NON_LOCALIZABLE_BASE = "/NonLocalizable";
	
    //algorithm matches Console's CIconPicture.cpp
	public final static byte[] pngSignature = new byte[]{ (byte)0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a }; // ASCII \211   P   N   G  \r  \n \032 \n
	public final static byte[] jpgSignature = new byte[]{ (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x00, 0x4A, 0x46, 0x49, 0x46, 0x00 }; // bytes at index 4 and 5 should be ignored in test
	
	public final static String MIME_TYPE_JPG = "image/jpg";
	public final static String MIME_TYPE_PNG = "image/png";
	
	
	/**
     * Returns suffix to construct db column name for given locale
     * Example: current locale is "es_MX", supported locales are "en,es,es_ES,ru_RU", will return "_es"
     * @param currentLocale - current session locale
     * @param supportedDbLocales - comma-separated list of locales, supported by db enum tables 
     * @return "best match" db column name suffix, such as _en
     */
	public static String getSupportedLocaleDbSuffix(String currentLocale, String supportedDbLocales) {
		List<String> supportedLocales = Arrays.asList(supportedDbLocales.split(","));
		//search for exact match
		for(String supportedLocale: supportedLocales){
			if(supportedLocale.trim().equalsIgnoreCase(currentLocale)) {
				return "_" + currentLocale.replace("-", "_");
			}
		}
		//search for language-only match
		for(String supportedLocale: supportedLocales){
			if(supportedLocale.trim().equalsIgnoreCase(getLanguageFromLocale(currentLocale))) {
				return "_" + supportedLocale.trim();
			}
		}
		//fall back to default
		return "_en";
	}

    /**
     * Retrieves resource string value for given key and locale
     * @param key the resource string identifier
     * @param base the name of the resource file
     * @param locale the locale
     * @return the resource value
     * @throws UnsupportedEncodingException 
     */
	public static String getResourceStringForLocale(String key, String base, String locale) throws UnsupportedEncodingException {
		Locale currentLocale = new Locale(getLanguageFromLocale(locale), getCountryFromLocale(locale));
        ResourceBundle resourceStrings = ResourceBundle.getBundle(base, currentLocale);
        
     	return new String(resourceStrings.getString(key).getBytes("ISO-8859-1"), "UTF-8");
	}
	
	public static String getLocalizedFormattedString(String key, String[] params, String locale, String base) throws UnsupportedEncodingException {
		String result = ResourceUtilities.getResourceStringForLocale(key, base, locale);
		if((null != params)&&(params.length > 0)) {
			result = String.format(result, (Object[])params);
		}
		return result;
	}

	/**
	 * Convenience function to retrieved a resource string from NonLocalizable.properties
	 * @param key the resource string identifier
	 * @return the resource value
	 */
	public static String getUnlocalizableString(String key, String... args) {
		try {
			return getLocalizedFormattedString(key, args, DEFAULT_LOCALE, NON_LOCALIZABLE_BASE);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
    /**
     * Extract 2-letter language code from locale (culture) code
     * Example: return "es" from "es_MX"
     * @param locale - a locale code
     * @return a language code
     */
	public static String getLanguageFromLocale(String locale) {
		String language;
		int underscoreIdx = locale.indexOf("_");
		if (underscoreIdx < 0) {
			language = locale;
		} else {
			language = locale.substring(0, underscoreIdx);
		}
		if (language.length() != 2) {
			language = new String("en");
		}
		return language;
	}
	
	/**
     * Extract country code from locale (culture) code
     * Example: return "MX" from "es_MX"
     * @param locale - a locale code
     * @return a language code or empty string if cannot retrieve a 2-letter country code
     */
	public static String getCountryFromLocale(String locale) {
		String country = "";
		int underscoreIdx = locale.indexOf("-");
		if ((underscoreIdx > 0) && (locale.substring(underscoreIdx + 1).length() == 2)) {
			country = locale.substring(underscoreIdx + 1);
		}
		return country;
	}

	/**
     * Counts the number of non-unique items within a given view
     * @param db locale suffix
     * @param instance of the dal object
     * @param the value of the item that we're checking
     * @param the name of the view to check in
     * @param the "InfoItemID" of the item within the view to check the "itemValue" against
     * @return -1 if the view couldn't be found or 0 or higher based on the non-unique hits
     */
	public static int getNonUniqueDataItemCount(
			String dbLocaleSuffix,
			IDal dal,
			String itemValue,
			String view,
			String infoItemID) throws Exception {
		
		int nonUniques = 0;
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		
		Result result = null;
		
		try {
			result = ViewHelper.getViewDetails(
					dal,
					view,
					params,
					null,
					dbLocaleSuffix);
		} catch (Exception e){
			return -1;
		}
		
		if (result != null){
			
			Object[] rows = result.getRows();
			ArrayList<Map<String, Object>> columnMetaData = result.getMetaData().getColumnMetaData();
			int dataColumnNum = -1;

			// find the data column location in the view given that the "ShortDisplayName" is unique in a view
			for(int i=0; i<columnMetaData.size(); i++){
				String shortName = (String) columnMetaData.get(i).get("InfoItemID");
				if (shortName != null && shortName.equalsIgnoreCase(infoItemID)){
					dataColumnNum=i;
					break;
				}
			}
			
			if (dataColumnNum == -1) {
				throw new Exception(ResourceUtilities.getResourceStringForLocale("COLUMN_INFO_ITEM_ID_NOT_FOUND", WEBAPI_BASE, DEFAULT_LOCALE));
			}
			
			// check if the data is unique
			for(int i=0; i<rows.length; i++){
				if (StringUtilities.arrayToString(rows[i],",").split(",")[dataColumnNum].equalsIgnoreCase(itemValue)) {
					nonUniques++;
				}
			}
		}
		
		return nonUniques;
	}

}
