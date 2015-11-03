package com.absolute.am.webapi;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;


@Provider // JAX-RS annotation for injection provider
		  // See http://jersey.java.net/nonav/documentation/latest/user-guide.html#d4e435
public class JAXRSExceptionMapper implements
         ExceptionMapper<java.lang.Exception> {
	
	private static Logger m_logger = LoggerFactory.getLogger(JAXRSExceptionMapper.class.getName());
	private static final String ERROR_DESCRIPTION = "errorDescription";
	
	public Response toResponse(@Context java.lang.Exception ex) {
		// Log the exception - always.
		m_logger.error("JAXRSExceptionMapper unhandled exception={}", ex);
    	
    	if (WebApplicationException.class.isAssignableFrom(ex.getClass())) {
    		WebApplicationException wae = (WebApplicationException)ex;    		
    		return wae.getResponse();
    		
    	} else {
    		String locale;
    		String base = ResourceUtilities.WEBAPI_BASE;
    		if(ex instanceof AMWebAPILocalizedException) {
    			locale = ((AMWebAPILocalizedException) ex).getLocale();
    			WebAPIException wae = null;
    			try {
    				wae = new WebAPIException(Status.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR_MESSAGE", null, null, locale, base, ERROR_DESCRIPTION, ex.getMessage());
    				return wae.getResponse();
    			} catch (UnsupportedEncodingException e) {
    				return ((AMWebAPILocalizedException) ex).getResponse();
    			}
    		} else {
    			String message = ex.getMessage();
    			WebAPIException wae = null;
    			wae = new WebAPIException(Status.INTERNAL_SERVER_ERROR, message, null);
				return wae.getResponse();
    		}
    	}    	
     }
}
