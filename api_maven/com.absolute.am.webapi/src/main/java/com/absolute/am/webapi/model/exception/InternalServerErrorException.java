/**
 * 
 */
package com.absolute.am.webapi.model.exception;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.Response.Status;

/**
 * Throw this exception when HTTP Status 500 Internal Server Error should be returned to the client.
 * 
 */
public class InternalServerErrorException extends WebAPIException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InternalServerError exception with a message and optionally
	 * with some context data.
	 * @param errorDescriptionKey An identifier of description resource string for the error that occurred.
	 * @param descrParams An array of parameters to create compound description string
	 * @param locale Locale code
	 * @param base Path to resource file
	 * @param contextEntries Optional context data to include in the response - 
	 * See the parameter with the same name at {@link WebAPIException#WebAPIException(Status, String, String, Object[], String, String, Object...)} 
	 * for details on how to use this.
	 * @throws UnsupportedEncodingException 
	 *  
	 */
	public InternalServerErrorException(String messageKey, String errorDescriptionKey, Object[] descrParams, 
			String locale, String base, Object ... contextEntries) throws UnsupportedEncodingException {
		super(Status.INTERNAL_SERVER_ERROR, messageKey, errorDescriptionKey, descrParams, locale, base, contextEntries);
	}
}
