/**
 * 
 */
package com.absolute.am.webapi.controllers;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;


/**
 * @author dlavin
 *
 */
@Path ("/tests")
public class Test {
	@Context ServletContext sc;

    private static Logger m_logger = LoggerFactory.getLogger(Test.class.getName()); 
    
	/**
	 * Test endpoint to generate an unexpected exception. Used to confirm that unexpected
	 * exceptions are mapped to WebAPIException types and handled gracefully. 	
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/unhandledexception")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUnhandledException() throws Exception {
		MDC.put("getUnhandledException.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing InterruptedException for test purposes.");
		// any type of exception will do, as long as it is not a WebAPIException.
		throw new InterruptedException("Something bad happened.");
	}
	
	/**
	 * Test endpoint to generate a WebAPIException with message, errorDescription
	 * and some context key value pairs.
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/webapiexception")
	@Produces(MediaType.TEXT_PLAIN)
	public String getWebAPIException() throws Exception {
		MDC.put("getWebAPIException.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing WebAPIException for test purposes.");
		throw new AMWebAPILocalizedException(Status.CONFLICT, "UPLOADING_CONTENT_FAILED", "FILE_NOT_FOUND", null,
				ResourceUtilities.DEFAULT_LOCALE, ResourceUtilities.NON_LOCALIZABLE_BASE, "code", -10, "filename", "Whatever.txt");
	}
	

	/**
	 * Test endpoint to generate a BadRequestException with status code, message, errorDescription
	 * and some context key value pairs.
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/badrequestexception")
	@Produces(MediaType.TEXT_PLAIN)
	public String getBadRequestException() throws Exception {
		MDC.put("getBadRequestException.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing getBadRequestException for test purposes.");
		// Note that in this case there is no error code and no context data.
		throw new BadRequestException("PARAMETER_DEVICEID_MISSING", null, ResourceUtilities.DEFAULT_LOCALE, ResourceUtilities.NON_LOCALIZABLE_BASE);
	}

	/**
	 * Test endpoint to generate a BadRequestException with status code, message, errorDescription
	 * and some context key value pairs.
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/internalservererrorexception")
	@Produces(MediaType.TEXT_PLAIN)
	public String getInternalServerErrorException() throws Exception {
		MDC.put("getInternalServerErrorException.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing InternalServerErrorException for test purposes.");
		// Note that in this case there is no error code and no context data.
		throw new InternalServerErrorException("THIS_IS_INTERNAL_ERROR", "SETTING_INI_XYZ_NOT_CONFIGURED", null, ResourceUtilities.DEFAULT_LOCALE, ResourceUtilities.NON_LOCALIZABLE_BASE);
	}
	
	/**
	 * Test endpoint to generate an AMServerProtocolException with a message.
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/amserverprotocolexception1")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAMServerProtocolException1() throws Exception {
		MDC.put("getAMServerProtocolException1.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing AMServerProtocolException for test purposes.");
		// Note that in this case there is no error code and no context data.
		throw new AMServerProtocolException("Uploading the content failed.", "myserver:myport", "Could not find file.");
	}
	
	/**
	 * Test endpoint to generate an AMServerProtocolException with a message.
	 * @return
	 * @throws Exception
	 */
	@GET @Path("/amserverprotocolexception2")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAMServerProtocolException2() throws Exception {
		MDC.put("getAMServerProtocolException2.someSetting", "Fake context specific data that should be logged automatically.");
		m_logger.debug("Throwing AMServerProtocolException for test purposes.");
		// Note that in this case there is no error code and no context data.
		throw new AMServerProtocolException("Uploading the content failed.", "myserver:myport", PropertyList.fromString(sampleErrorResponse));
	}	
	
	private static String sampleErrorResponse = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">" +
	"<plist version=\"1.0\">" +
	"<dict>" +
	"    <key>CommandID</key>" +
	"    <integer>4100</integer>" +
	"    <key>CommandResultError</key>" +
	"    <integer>536882922</integer>" +
	"    <key>CommandResultErrorString</key>" +
	"    <string>A media definition with the same name already exists.</string>" +
	"    <key>CommandResultErrorDebugInfo</key>" +
	"    <string>This is the debug portion of the fake error.</string>" +	
	"    <key>CommandVersion</key>" +
	"    <integer>1</integer>" +
	"</dict>" +
	"</plist>";
}
