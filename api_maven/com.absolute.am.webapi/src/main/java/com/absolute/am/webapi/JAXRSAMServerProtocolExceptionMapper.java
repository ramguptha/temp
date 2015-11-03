package com.absolute.am.webapi;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.webapi.model.exception.WebAPIException;


@Provider // JAX-RS annotation for injection provider
		  // See http://jersey.java.net/nonav/documentation/latest/user-guide.html#d4e435
public class JAXRSAMServerProtocolExceptionMapper implements
         ExceptionMapper<AMServerProtocolException> {
	
	private static Logger m_logger = LoggerFactory.getLogger(JAXRSAMServerProtocolExceptionMapper.class.getName());
	private static final String ERROR_CODE = "errorCode";
	private static final String SERVER = "server";
	
    public Response toResponse(AMServerProtocolException ex) {

    	// Log the exception - always.
    	m_logger.error("JAXRSAMServerProtocolExceptionMapper exception={}", ex);
    	
    	
    	WebAPIException wae = new WebAPIException(
									Status.BAD_REQUEST,
									ex.getContextMessage(),
									ex.getDescription(),
									ERROR_CODE, ex.getCode(), 
									SERVER, ex.getServerHostAndPort());

    	return wae.getResponse();
    }

}
