package com.absolute.util.exception;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Base class for exceptions that the WebAPI can throw. All abnormal failure conditions
 * are reported using this class or child classes of this.
 */
public class AMWebAPILocalizableException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * These are the names of the fields that are returned in all exceptions.
	 * When an exception is being thrown however, the thrower can add additional
	 * named fields in the contextEntries parameter of the constructor.
	 */
	public static final String MESSAGE_KEY = "messageKey";
	public static final String MESSAGE_KEY_PARAMS = "messageKeyParams";
	public static final String ERROR_DESCRIPTION_KEY = "errorDescriptionKey";
	public static final String ERROR_DESCRIPTION_KEY_PARAMS = "errorDescriptionKeyParams";

	private Map<String, Object> m_params;

	public void setParam( Map<String, Object> params) {
		m_params = params;
	}
	
	public Object getParam( String key) {
		return m_params.get(key);
	}
	/**
	 * Constructs a WebAPIException with the given HTTP Status Code, a message,
	 * an errorDescription and an optional array of other key=value pairs. 
	 * The number of entries in the optional array should always be a multiple of two. Here is
	 * an example:
	 * <p>
	 * <code>
	 *  throw new WebAPIException(Status.CONFLICT, "It can't be done.", "File not found", "filename", "c:\blah.txt");
	 * </code>
	 * <p> 
	 * For this example, the extra optional key=value pair is filename=c:\blah.txt.
	 * @param <HttpSession>
	 * @param httpStatus The HTTP status code to return.
	 * @param message A message describing what has failed. E.g. uploading file failed, assigning content to policy failed, etc.
	 * @param errorDescription A description of the error/failure that occurred.
	 * @param contextEntries Optional context specific key=value pairs to be associated with the error.
	 * @throws UnsupportedEncodingException 
	 */

	public AMWebAPILocalizableException(Exception e,  Map<String, Object> params) {

		super(e);
		m_params = params;
	}
	
	public AMWebAPILocalizableException( Map<String, Object> params) {
		super();
		m_params = params;
		
	}
	
}