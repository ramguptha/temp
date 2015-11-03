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
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;


/**
 * <h3>In-House Applications API</h3>
 * <p>This API is used to query all In-house applications.</p>
 *
 * @author klavin
 *
 */

@Path ("/inhouseapps")
public class InHouseApps {
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(InHouseApps.class.getName()); 

    public static final String VIEW_NAME_ALL_IN_HOUSE_APPLICATIONS = "allinhouseapplications";
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	// made this public to test code can see it.
	public static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All In-house Applications", 0)
	};
	
	/**
	 * <p>Get a list of named views available for applications. 
	 *    Please refer to /api/mobiledevices/views – GET for an example of the response to this request.</p>
	 * 
	 * <p>Currently there is only one view and it is called "All".</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * 
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public ViewDescriptionList getViewsForInHouseApplications() throws Exception  {

		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view. The result is a multi-row result set, but the exact content depends on the definition of the view.</p>
	 * <p>The result set should include the same columns as in the Admin Console: Mobile App Name, Platform, Version, Build number, Size, Short Description,
	 *    Bundle Identifier, Min OS Version, Universal, Supported Devices, iOS Provisioning Profile Name, iOS Provisioning Profile Expiry Date.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 *    
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "The <strong>viewName</strong> is not found.")
		})
	public Result getView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
				
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_IN_HOUSE_APPLICATIONS;
		}

		Result result = null;
		IDal dal = Application.getDal(session);

		result = ViewHelper.getViewDetails(
				dal,
				viewname, 
				ui.getQueryParameters(),
				null,
				dbLocaleSuffix);
		
		MDC.remove("viewname");
		return result;
	}

}
