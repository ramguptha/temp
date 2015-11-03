package com.absolute.am.ConfigurationApp;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationUtil {
	
	public final static String DEFAULT_LANG = "en";
	public final static String DEFAULT_LOCALE = "en-US";
	public final static String BASE = "Wizard";
	private final static String ERROR_CANNOT_LOCALIZE_RESOURCE_STRING = "Resource string cannot be localized (UnsupportedEncodingException): %s";
	private final static String ERROR_MISSING_RESOURCE_STRING = "Resource string missing, key: %s, language: %s";
	private final static String[] SUPPORTED_LANGS = new String[] {"en", "es", "fr", "de", "ja", "no", "sv"};
	
    /**
     * Retrieves resource string value for given key and language
     * @param key the resource string identifier
     * @param lang the language
     * @return the resource string
     * @throws UnsupportedEncodingException 
     */
	public static String getResourceStringForLocale(String key, String lang, Object... params) {
		
		Locale currentLocale;
        ResourceBundle resourceStrings;
        currentLocale = new Locale(lang);
        resourceStrings = ResourceBundle.getBundle(BASE, currentLocale);
        String ret, retDecoded;
        
        try {
        	ret = resourceStrings.getString(key);
	        retDecoded = new String(ret.getBytes("UTF-8"), "UTF-8");
	        if((null != params)&&(params.length > 0)) {
	        	retDecoded = String.format(retDecoded, params);
	        }
        } catch (MissingResourceException e) {
        	retDecoded = String.format(ERROR_MISSING_RESOURCE_STRING, (Object[])new String[]{key, lang});
        } catch (UnsupportedEncodingException e) {
	    	retDecoded = String.format(ERROR_CANNOT_LOCALIZE_RESOURCE_STRING, (Object[])new String[]{key});
	    }
        
     	return retDecoded;
	}
	
	public static String getSystemLanguage() {
		Locale locale = Locale.getDefault();
		String lang = locale.getLanguage();
		return getSupportedLang(lang);
	}
	
	private static String getSupportedLang(String currentLang) {
		List<String> supportedLangs = Arrays.asList(SUPPORTED_LANGS);
		//search for exact match
		for(String supportedLang: supportedLangs){
			if(supportedLang.equalsIgnoreCase(new Locale(currentLang).getLanguage())) {
				return supportedLang;
			}
		}
		//if not found - search for partial match
		//example: "ja_JP_JP"
		//see "Special cases" in http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html
		//modify this code if 3-letter language codes are used
		for(String supportedLang: supportedLangs){
			if(supportedLang.equalsIgnoreCase(
					new Locale(currentLang).getLanguage().substring(0, 2))) {
				return supportedLang;
			}
		}
		//fall back to default
		return DEFAULT_LANG;
	}	

}
