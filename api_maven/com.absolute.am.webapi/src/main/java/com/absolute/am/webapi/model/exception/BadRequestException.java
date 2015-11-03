/**
 * 
 */
package com.absolute.am.webapi.model.exception;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.Response.Status;

/**
 * Throw this exception when HTTP Status 400 Bad Request should be returned to the client.
 * 
 */
public class BadRequestException extends AMWebAPILocalizedException {

	private static final long serialVersionUID = 1L;	
	
	/**
	 * Constructs a Bad Request exception with a message and optional context data.
	 * @param errorDescriptionKey An identifier of description resource string for the error that occurred.
	 * @param descrParams An array of parameters to create compound description string
	 * @param locale Locale code
	 * @param base Path to resource file
	 * @param contextEntries Optional context data to include in the response.
	 * @throws UnsupportedEncodingException
	 */
	public BadRequestException(String errorDescriptionKey, Object[] descrParams, String locale, String base, Object ... contextEntries) throws UnsupportedEncodingException {
		super(Status.BAD_REQUEST, "BAD_REQUEST", errorDescriptionKey, descrParams, locale, base, contextEntries);
	}
}