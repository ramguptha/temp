package com.absolute.am.webapi.model.exception;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.Response.Status;

/**
 * Base class for exceptions that the WebAPI can throw. All abnormal failure conditions
 * are reported using this class or child classes of this.
 * 
 * @see BadRequestException
 * @see InternalServerErrorException
 *
 */
public class WebAPIException extends AMWebAPILocalizedException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a WebAPIException with the given HTTP Status Code, a message key,
	 * an errorDescription key, an array of parameters for the description,
	 * a locale and base for localization and an optional array of other key=value pairs. 
	 * The number of entries in the optional array should always be a multiple of two. Here is
	 * an example:
	 * <p>
	 * <code>
	 *  throw new WebAPIException(Status.CONFLICT, "IT_CANT_BE_DONE", "FILE_NOT_FOUND", null, "en-US", "/webapi/Webapi", "filename", "c:\blah.txt");
	 * </code>
	 * <p> 
	 * For this example, the extra optional key=value pair is filename=c:\blah.txt.
	 * @param httpStatus The HTTP status code to return.
	 * @param messageKey A key for the message describing what has failed. E.g. uploading file failed, assigning content to policy failed, etc.
	 * @param errorDescriptionKey A key for the description of the error/failure that occurred.
	 * @param locale A locale of current session.
	 * @param base A path to the property file that contains message and errorDescription strings
	 * @param descrParams An array of parameters to build a compound description string.
	 * @param contextEntries Optional context specific key=value pairs to be associated with the error.
	 */
	public WebAPIException(Status httpStatus, String messageKey, String errorDescriptionKey, Object[] descrParams, 
			String locale, String base, Object ... contextEntries) throws UnsupportedEncodingException  {
		super(httpStatus, messageKey, errorDescriptionKey, descrParams, locale, base);
	}
	
	/**
	 * Simple constructor to create a WebAPIException 
	 * with the given HTTP Status Code, a message and an errorDescription. 
	 * Unlike the other constructor for WebAPIException, this one assumes that the message and description are already localized and formatted.
	 * Here is an example:
	 * <p>
	 * <code>
	 *  throw new WebAPIException(Status.BAD_REQUEST, "Solicitud incorrecta", "Parmetro [ABC] falta.");
	 * </code>
	 * <p> 
	 * @param httpStatus The HTTP status code to return.
	 * @param message A message describing what has failed. E.g. uploading file failed, assigning content to policy failed, etc.
	 * @param errorDescription A description of the error/failure that occurred.
	 */
	public WebAPIException(Status httpStatus, String message, String description, Object ... contextEntries) {
		super(httpStatus, message, description, contextEntries);
	}
	
}
