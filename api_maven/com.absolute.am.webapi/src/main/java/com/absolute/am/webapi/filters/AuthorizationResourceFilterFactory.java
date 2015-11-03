/**
 * 
 */
package com.absolute.am.webapi.filters;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.webapi.annotations.CommandPermission;
import com.absolute.am.webapi.annotations.CommandPermission.AMCommand;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

/**
 * Jersey specific resource filter. Will have to be rewritten when resource filters are added to JAX-RS 2.0.
 * This resource filter checks that the user login has the appropriate right/command permission for
 * the given resource (i.e. endpoint), based on the {@link Right} and {@link CommandPermission} annotations.
 * <br> 
 * <br>
 * The implementation is based on Sun's RolesAllowedResourceFilterFactory. Take a look at this example:
 * http://www.jarvana.com/jarvana/view/com/sun/jersey/jersey-bundle/1.3/jersey-bundle-1.3-sources.jar!/com/sun/jersey/api/container/filter/RolesAllowedResourceFilterFactory.java?format=ok
 * @author dlavin
 */
public class AuthorizationResourceFilterFactory implements
		ResourceFilterFactory {
	
    private static Logger m_logger = LoggerFactory.getLogger(AuthorizationResourceFilterFactory.class.getName());
    
	private @Context HttpServletRequest m_servletRequest;

	/**
	 * This is the filter object created for each endpoint. It is initialized once at system startup.
	 * @author dlavin
	 */
    private class AuthorizationFilter implements ResourceFilter, ContainerRequestFilter {

        private final AMRight[] m_rightRequired;
        private final AMCommand m_commandPermissionRequired;
        
        protected AuthorizationFilter(AMRight[] rightRequired) {        	
            m_rightRequired = rightRequired;
            m_commandPermissionRequired = null;
        }
        
        protected AuthorizationFilter(AMCommand commandPermissionRequired) {        	
            m_commandPermissionRequired = commandPermissionRequired;
            m_rightRequired = null;
        }
        
        public ContainerRequestFilter getRequestFilter() {        	
            return this;
        }

        public ContainerResponseFilter getResponseFilter() {        	
            return null;
        }
        
        // Implementation of ContainerRequestFilter interface.        
        public ContainerRequest filter(ContainerRequest request) {

        	if (m_rightRequired != null) {            	
	        	if (isRightAssigned(m_rightRequired, m_servletRequest.getSession())) {
	                return request;
	            }
        	}
        	
        	if (m_commandPermissionRequired != null) {
	        	if (isCommandPermissionAssigned(m_commandPermissionRequired, m_servletRequest.getSession())) {
	                return request;
	            }
        	}
        	
        	try {
				throw new WebAPIException(Response.Status.FORBIDDEN, "COMMANDS_NO_PERMISSIONS", "COMMANDS_NO_PERMISSIONS", 
						null, SessionState.getLocale(m_servletRequest.getSession()), ResourceUtilities.WEBAPI_BASE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	
        	// this return should never run and is only here because this function must return a ContainerRequest
			return request;
        }
    }


    /**
     * Helper method to check if a specific right has been assigned to the logged on user.
     * @param right The right to check for.
     * @param session The login session. 
     * @return true when the right has been assigned to the user, false otherwise.
     */
    public static boolean isRightAssigned(AMRight[] rights, HttpSession session) {
    	
    	Map<String, Object> logonResult = SessionState.getLogonResultParameters(session);	
    	Long rightValue;
    	
    	for(AMRight right : rights){
    		rightValue = PropertyList.getElementAs(logonResult, "AdminInfo/" + right.name());
    		if (rightValue != 1) {
    			m_logger.debug("isRoleAssigned returning false. Role={} roleEnabled={}.", right, rightValue);
    			return false;
    		}
    		
    	}
    	 
    	return true;
    }
    
    /**
     * Helper method to check if a user has been assigned permission to issue a specific command.
     * @param commandPermission The command permission to check for.
     * @param session The current session.
     * @return True when the command is available to the user. False otherwise.
     */
    public static boolean isCommandPermissionAssigned(AMCommand commandPermission, HttpSession session) {
    	
    	Map<String, Object> logonResult = SessionState.getLogonResultParameters(session);	
    	Long commandPermissions = PropertyList.getElementAs(logonResult, "AdminInfo/CommandPermissions");

    	if (commandPermissions != null && ((commandPermissions & commandPermission.getBitNumberAsMask()) > 0)) {
    		return true;
    	} else {
        	m_logger.debug("isCommandPermissionAssigned returning false. commandPermission={} bitNumber={} mask={} permissions={}",
				commandPermission.toString(),
				commandPermission.getBitNumber(),
				commandPermission.getBitNumberAsMask(),
				commandPermissions
				);
    		return false;
    	}    	
    }
    

	/* (non-Javadoc)
	 * @see com.sun.jersey.spi.container.ResourceFilterFactory#create(com.sun.jersey.api.model.AbstractMethod)
	 */
	@Override
	public List<ResourceFilter> create(AbstractMethod am) {
		
		m_logger.debug("AuthorizationResourceFilterFactory.create() called, am.getResource()={}", am.getResource());
		
		// For the moment, the authorization is either a role or a command. In the future this could be extended to
		// allow combinations of the two.
		
        // Assigned Right check
		Right requiredRight = am.getAnnotation(Right.class);
        if (requiredRight != null) {
        	return Collections.<ResourceFilter>singletonList(new AuthorizationFilter(requiredRight.value()));
        	
        }
        
        // Assigned Command check
        CommandPermission requiredCommandPermission = am.getAnnotation(CommandPermission.class);
        if (requiredCommandPermission != null) {
        	return Collections.<ResourceFilter>singletonList(new AuthorizationFilter(requiredCommandPermission.value()));
        }

        // No authorization restrictions here.
        return null;
	}
}
