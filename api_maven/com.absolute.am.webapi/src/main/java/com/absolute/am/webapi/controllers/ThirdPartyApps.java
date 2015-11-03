/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.dal.IApplicationsHandler.iconType;
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
import com.absolute.util.ImageUtilities;


/**
 * <h3>3rd-Party Applications API</h3>
 * <p>This API is used to query all 3rd-Party applications.</p>
 * 
 * @author klavin
 *
 */
@Path ("/thirdpartyapps")
public class ThirdPartyApps {
	
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(ThirdPartyApps.class.getName()); 

    public static final String VIEW_NAME_ALL_THIRD_PARTY_APPLICATIONS = "allthirdpartyapplications";
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
    
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	
	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All In-house Applications", 0)
	};

	
	/**
	 * <p>Get a list of named views available for applications. 
	 *    Please refer to /api/mobiledevices/views – GET for an example of the response to this request.</p>
	 *    
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
	public ViewDescriptionList getViewsForThirdPartyApplications() throws Exception  {

		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view.</p>
	 *    
	 * <p>The result is a multi-row result set that should include the same columns as in the Admin Console. 
	 *    For the "All" view columns are: App Name, OS Platform, Category, Min OS Version, Universal, Supported Devices,
	 *    Short Description,Prevent Data Backup, Remove when MDM is Removed, VPP Codes Purchased, VPP Codes Redeemed, VPP Codes Remaining.</p>   
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
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
			viewname = VIEW_NAME_ALL_THIRD_PARTY_APPLICATIONS;
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
	
	/**
	 * <p>Get the icon image for the third party application. 
	 *    The response is a binary data with appropriate MIME type specified in the response Content-Type header.
	 *    Two formats of images are supported: PNG and JPEG. If the icon is of unknown format, an empty response is returned.</p>
	 *    
	 * <p>Rights required:</br>
	 *    None</p>
	 * 
	 * @param appId Third party application Id
	 * @return Returns the icon blob associated with given third-party app
	 * @throws Exception 
	 */
	@GET @Path("/{id}/icon")
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A third party application with this {id} could not be found.")
		})
	public Response getThirdPartyAppIconById(@PathParam("id") long appId) throws Exception  {

		MDC.put("appId", "" + appId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		byte[] appIcon = null;
		Response response = null;
		
		IDal dal = Application.getDal(session);
		
		// check if the id is valid
		if (null == dal.getApplicationsHandler().getThirdPartyApplication(appId)) {
			throw new NotFoundException("THIRDPARTYAPPS_ID_NOT_FOUND", null, locale, m_Base);
		}

		appIcon = dal.getApplicationsHandler().getIcon(appId, iconType.thirdPartyApp);

		if(ImageUtilities.compareImageBytes(appIcon, ResourceUtilities.pngSignature, false)) {
			//PNG icon
			response = Response.ok(appIcon).type(ResourceUtilities.MIME_TYPE_PNG).build();
		} else if(ImageUtilities.compareImageBytes(appIcon, ResourceUtilities.jpgSignature, true)) {
			//JPEG icon
			response = Response.ok(appIcon).type(ResourceUtilities.MIME_TYPE_JPG).build();
		} else {
			//unknown format - return empty response
			response = Response.noContent().build();
		}

		MDC.remove("appId");
		
		return response;
	}
}