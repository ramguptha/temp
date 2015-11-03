package com.absolute.am.ConfigurationApp;

import java.io.UnsupportedEncodingException;

import com.absolute.util.exception.AMWebAPILocalizableException;

public class LocalizedException extends Exception {

	private static final long serialVersionUID = 1L;
	private String localizedMsg;
	private String localizedDescr;
	private String m_Lang;
	
	public LocalizedException (AMWebAPILocalizableException e, String lang) {
		super(e);
		m_Lang = lang;
		loadFromLocalizableException(e); 
	}
	
	private void loadFromLocalizableException(AMWebAPILocalizableException e)  {

		String key  = (String)e.getParam(AMWebAPILocalizableException.MESSAGE_KEY);
		try
		{
			loadMsg(e, m_Lang, key);
		} catch(UnsupportedEncodingException ex) {
			try {
				loadMsg(e, LocalizationUtil.DEFAULT_LANG, key);
			} catch (UnsupportedEncodingException e1) {
				localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], language [%s]", key, m_Lang);
			}
		}
		
		key  = (String)e.getParam(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY);
		try
		{
			loadDescr(e, m_Lang, key);
		} catch(UnsupportedEncodingException ex) {
			try {
				loadDescr(e, LocalizationUtil.DEFAULT_LANG, key);
			} catch (UnsupportedEncodingException e1) {
				localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], language [%s]", key, m_Lang);
			}
		}
			
	}


	private void loadMsg(AMWebAPILocalizableException e, String lang, String key) throws UnsupportedEncodingException {
		String format = LocalizationUtil.getResourceStringForLocale(key, lang );
		Object[] params = (Object[]) e.getParam(AMWebAPILocalizableException.MESSAGE_KEY_PARAMS);
		if(null != params && 0 < params.length) {
			localizedMsg = String.format(format, params);
		} else {
			localizedMsg = format;
		}
		
	}
	
	private void loadDescr(AMWebAPILocalizableException e, String lang, String key) throws UnsupportedEncodingException {
		String format = LocalizationUtil.getResourceStringForLocale(key, lang );
		Object[] params = (Object[]) e.getParam(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY_PARAMS);
		if(null != params && 0 < params.length) {
			localizedDescr = String.format(format, params);
		} else {
			localizedDescr = format;
		}
	}
	
	@Override
	public String toString() {
		return String.format(LocalizationUtil.getResourceStringForLocale("EXCEPTION_ERROR", m_Lang) + ": %s ", localizedMsg);
	}
	
	@Override
	public String getLocalizedMessage() {
		return String.format(
				LocalizationUtil.getResourceStringForLocale("EXCEPTION_ERROR", m_Lang) + ": %s ; \n" + 
				LocalizationUtil.getResourceStringForLocale("EXCEPTION_DESCRIPTION", m_Lang) + ": %s", localizedMsg, localizedDescr);
	}
	
	@Override
	public String getMessage() {
		return String.format(
				LocalizationUtil.getResourceStringForLocale("EXCEPTION_ERROR", m_Lang) + ": %s ; \n" + 
				LocalizationUtil.getResourceStringForLocale("EXCEPTION_DESCRIPTION", m_Lang) + ": %s", localizedMsg, localizedDescr);
	}
	
	public String getMsg() {
		return localizedMsg;
	}

	public String getDescr() {
		return localizedDescr;
	}
}