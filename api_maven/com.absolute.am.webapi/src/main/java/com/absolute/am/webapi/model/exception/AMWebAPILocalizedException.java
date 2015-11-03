package com.absolute.am.webapi.model.exception;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.absolute.util.exception.AMWebAPILocalizableException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.StringUtilities;
import com.absolute.am.model.exception.ExceptionInfo;

public class AMWebAPILocalizedException extends javax.ws.rs.WebApplicationException {

	private static final long serialVersionUID = 1L;
	private String localizedMsg;
	private String localizedDescr;
	private String m_Locale;
	private String m_Base;
	
	private static final String MESSAGE = "message";
	private static final String ERROR_DESCRIPTION = "errorDescription";
	
	
	public AMWebAPILocalizedException (AMWebAPILocalizableException e, String locale, String base) {
		super(e);
		m_Locale = locale;
		m_Base = base;
		loadFromLocalizableException(e); 
	}
	
	public AMWebAPILocalizedException (Status httpStatus, String errorMessageKey, String errorDescriptionKey, Object[] descriptionParams, 
							String locale, String base, Object ... contextEntries) throws UnsupportedEncodingException {
		super(Response
				.status(httpStatus)
				.type(MediaType.APPLICATION_JSON + ";charset=utf-8")
				.entity(new ExceptionInfo(
						MESSAGE, 
						ResourceUtilities.getResourceStringForLocale(errorMessageKey, base, locale),
						ERROR_DESCRIPTION, 
						ResourceUtilities.getLocalizedFormattedString(errorDescriptionKey, StringUtilities.objectArrayToStringArray(descriptionParams), locale, base),
						contextEntries))
				.build());
		m_Locale = locale;
		m_Base = base;
	}
	
	public AMWebAPILocalizedException (String messageKey, String locale, String base) {
		try {
			m_Locale = locale;
			m_Base = base;
			localizedMsg = ResourceUtilities.getResourceStringForLocale(messageKey, base, locale );
		} catch (UnsupportedEncodingException e) {
			localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], locale [%s], base [%s]", messageKey, locale, base);
		}
	}
	
	//constructor for a pre-localized exception
	public AMWebAPILocalizedException (Status httpStatus, String message, String description, Object ... contextEntries) {
		super(Response
		.status(httpStatus)
		.type(MediaType.APPLICATION_JSON + ";charset=utf-8")
		.entity(new ExceptionInfo(
				MESSAGE, 
				message,
				ERROR_DESCRIPTION, 
				description,
				contextEntries))
		.build());
	}


	public AMWebAPILocalizedException (String messageKey, String descriptionKey, String locale, String base) {
			m_Locale = locale;
			m_Base = base;
		try {
			localizedMsg = ResourceUtilities.getResourceStringForLocale(messageKey, base, locale );
		} catch (UnsupportedEncodingException e) {
			localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], locale [%s], base [%s]", messageKey, locale, base);
		}
		try {
			localizedDescr = ResourceUtilities.getResourceStringForLocale(descriptionKey, base, locale );
		} catch (UnsupportedEncodingException e) {
			localizedDescr = String.format("UnsupportedEncodingException, resource key [%s], locale [%s], base [%s]", descriptionKey, locale, base);
		} 
	}

	public String getLocale() {
		return this.m_Locale;
	}


	private void loadFromLocalizableException(AMWebAPILocalizableException e)  {

		String key  = (String)e.getParam(AMWebAPILocalizableException.MESSAGE_KEY);
		try
		{
			loadMsg(e, m_Locale, key);
		} catch(UnsupportedEncodingException ex) {
			try {
				loadMsg(e, ResourceUtilities.DEFAULT_LOCALE, key);
			} catch (UnsupportedEncodingException e1) {
				localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], locale [%s], base [%s]", key, m_Locale, m_Base);
			}
		}
		
		key  = (String)e.getParam(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY);
		try
		{
			loadDescr(e, m_Locale, key);
		} catch(UnsupportedEncodingException ex) {
			try {
				loadDescr(e, ResourceUtilities.DEFAULT_LOCALE, key);
			} catch (UnsupportedEncodingException e1) {
				localizedMsg = String.format("UnsupportedEncodingException, resource key [%s], locale [%s], base [%s]", key, m_Locale, m_Base);
			}
		}
			
	}


	private void loadMsg(AMWebAPILocalizableException e, String locale, String key) throws UnsupportedEncodingException {
		String format = ResourceUtilities.getResourceStringForLocale(key, m_Base, locale );
		Object[] params = (Object[]) e.getParam(AMWebAPILocalizableException.MESSAGE_KEY_PARAMS);
		if(null != params && 0 < params.length) {
			localizedMsg = String.format(format, params);
		} else {
			localizedMsg = format;
		}
		
	}
	
	private void loadDescr(AMWebAPILocalizableException e, String locale, String key) throws UnsupportedEncodingException {
		String format = ResourceUtilities.getResourceStringForLocale(key, m_Base, locale );
		Object[] params = (Object[]) e.getParam(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY_PARAMS);
		if(null != params && 0 < params.length) {
			localizedDescr = String.format(format, params);
		} else {
			localizedDescr = format;
		}
	}
	
	@Override
	public String toString() {
		try {
			return String.format(ResourceUtilities.getResourceStringForLocale("EXCEPTION_ERROR", m_Base, m_Locale ) + ": %s ", localizedMsg);
		} catch (UnsupportedEncodingException e) {
			return String.format("%s", localizedMsg);
		}
	}
	
	@Override
	public String getLocalizedMessage() {
		try {
		return String.format(
				ResourceUtilities.getResourceStringForLocale("EXCEPTION_ERROR", m_Base, m_Locale ) + ": %s ; \n" + 
				ResourceUtilities.getResourceStringForLocale("EXCEPTION_DESCRIPTION", m_Base, m_Locale ) + ": %s", localizedMsg, localizedDescr);
		} catch (UnsupportedEncodingException e) {
			return String.format("%s;\n%s", localizedMsg, localizedDescr);
		}
	}
	
	@Override
	public String getMessage() {
		try {
			return String.format(
					ResourceUtilities.getResourceStringForLocale("EXCEPTION_ERROR", m_Base, m_Locale ) + ": %s ; \n" + 
					ResourceUtilities.getResourceStringForLocale("EXCEPTION_DESCRIPTION", m_Base, m_Locale ) + ": %s", localizedMsg, localizedDescr);
			} catch (UnsupportedEncodingException e) {
				return String.format("%s;\n%s", localizedMsg, localizedDescr);
			}
	}
}

