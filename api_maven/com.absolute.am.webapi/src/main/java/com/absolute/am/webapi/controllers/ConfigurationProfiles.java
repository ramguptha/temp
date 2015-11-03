/**
 * 
 */
package com.absolute.am.webapi.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.Application;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;


/**
 * <h3>Configuration Profiles API</h3>
 * <p>This API is used to query Configuration Profiles. </p>
 * 
 * @author gefimov
 *
 */
@Path ("/configurationprofiles")
public class ConfigurationProfiles {
	
	private static Logger m_logger = LoggerFactory.getLogger(ConfigurationProfiles.class.getName());
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String VH_VIEW_NAME_ALL_CONFIGURATION_PROFILES = "allconfigurationprofiles";
	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Configuration Profiles", 0),
	};



	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	/**
	 * <p>Get a list of named views available for Configuration Profiles. 
	 *    Please refer to /api/mobiledevices/views – GET for an example of the response to this request. </p>
	 * <p>Currently there is only one view and it is called "All".</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForConfigurationProfiles() throws Exception  {
		m_logger.debug("ConfigurationProfiles.getViewsForConfigurationProfiles called");
				
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view. The result is a multi-row result set that should include the same columns as in the Admin Console. 
	 *    For the "All" view columns are: Mobile Profile Name, Mobile Profile Description, Mobile Profile Organization, 
	 *    Mobile Platform Type,Mobile Profile Type, Mobile Profile Identifier, Mobile Profile UUID,Mobile Profile Allow Removal and
	 *    Mobile Profile Variables Used.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns data from given view.
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The <strong>viewName</strong> is not found.")
		})
	public Result getView(
			@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		m_logger.debug("ConfigurationProfiles.getView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if(viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VH_VIEW_NAME_ALL_CONFIGURATION_PROFILES;
		}
		
		Result result = null;
		IDal dal = Application.getDal(session);
	
		dal.getAdminAccessHandler().refreshAccessForAdmin(SessionState.getFilterByAdmin(session));
		result = ViewHelper.getViewDetails(
				dal, 
				viewname,
				ViewHelper.getQueryParameters(ui,  session),
				null,
				dbLocaleSuffix);
			
		MDC.remove("viewname");
		
		return result;
	}
	
}
