/**
 * 
 */
package com.absolute.am.webapi.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.codehaus.enunciate.Facet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.model.Result;
import com.absolute.am.model.command.GenericView;

/**
 * The views API is used to generate the results of the views configured by the View Configuration API and built-in system views. 
 * 
 * <table>
 * <tr><td>URI template</td><td>HTTP Method</td><td>Description</td></tr>
 * <tr>
 * 	<td><strong>/api/views/{viewname}</strong></td>
 * 	<td>GET</td>
 * 	<td>
 * 		<p>Returns a list of objects from the named view.</p>
 *		<p>Built-in preconfigured views include:</p>
 * 		<ul>
 * 			<li>allmobiledevices</li>
 * 			<li>alliosdevices</li>
 * 			<li>alliphones</li>
 * 			<li>allipads</li>
 * 			<li>allcomputers</li>
 * 			<li>allipodtouchdevices</li>
 * 			<li>allandroiddevices</li>
 * 			<li>allandroidphones</li>
 * 			<li>allandroidtablets</li>
 * 			<li>allwindowsphonedevices</li>
 * 			<li>allmobilecontent</li>
 * 			<li>allmobilepolicies</li>
 * 			<li>allwindowsphonedevices</li>
 * 			<li>allmobileactions</li>
 * 		</ul>
 *  </td>
 * </tr>
 * </table>
 * 
 * <p>Views are also available under their resource type. Use the below end points to query the views available for that end point.</p>
 * <pre>
 * /api/mobiledevices/views
 * /api/content/views
 * /api/policies/views
 * /api/thirdpartyapps/views
 * /api/inhouseapps/views
 * /api/computers/views
 * /api/actions/views
 * </pre>
 * 
 * <p>The details of a given view can be queried using the following API:</p>
 * <pre>
 * /api/resource_name/views/{viewname}, 
 * /api/mobiledevices/views/allmobiledevices
 * </pre>
 * <p>This API will list all of the mobile devices that are defined in the system.</p>
 * <p>Note: Each view definition includes a sort order, however the user can provide an override as part of the query string. See the $orderby parameter.</p>
 * 
 * 
 */
@Path("/views")
public class Views {
    @SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(Views.class.getName()); 

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */   
	private @Context HttpServletRequest m_servletRequest;
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	/**
	 * <p>Returns a list of data specified by the given <strong>viewname</strong>, for example, all mobile devices (allmobiledevices), all policies(allpolicies), or all computers(allcomputers).</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 *    
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns the list of data specified by the given viewname.
	 * @throws Exception
	 */
	@GET @Path("/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		MDC.put("viewname", viewname);
		
		Result result = null;
		HttpSession session = m_servletRequest.getSession();
		IDal dal = Application.getDal(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
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
	 * <p>This endpoint is used to send in a user defined view and to get back the data.</p>   
	 *  
	 * <p>A sample ( JSON ) request is as follows:</p>
	 * <pre>
	 * {
	 *		"guids" : ["39f3f074-b8a2-4df1-ac02-eb1f25f3f98e", "FE5A9F56-228C-4BDA-99EC-8666292CB5C1", "8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5"],
	 * 		"rootTable" : "iphone_info",
	 * 		"sortBy" : "FE5A9F56-228C-4BDA-99EC-8666292CB5C1",
	 * 		"sortDir" : "Ascending",
	 * 		"filter" : {"CompareValue" : [{"CachedInfoItemName" : "Mobile Device OS Platform", "CompareValue" : "Android", "CompareValue2": "", "CompareValueUnits" : "Minutes", "InfoItemID" : "8D8EB50B-EAE0-4D4B-8FA7-CA4C1DE220E5", "IsCustomField" : false, "Operator" : "==", "UseNativeType" : false}], "CriteriaFieldType" : 0, "Operator" : "AND"}
	 * }
	 * </pre>
	 * 
	 * <p>"sortDir" is optional and defaults to ascending. "filter" and "sortBy" are also completely optional. If "sortBy" guid must be present in the "guids" list.</p>
	 * 
	 * <p>Command permissions bit required:<br/>
	 * None</p>
	 * 
	 * <p>Rights required:</br>
	 *    IsSuperAdmin, CanLogin</p>
	 *
	 * @param view GenericView
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/generic")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right({AMRight.IsSuperAdmin, AMRight.CanLogin})
	@Facet(name = "exclude")
	public Result getGenericView(GenericView view,
			@Context UriInfo ui) throws Exception{
		
		if( view.guids == null || view.rootTable == null) {
			String locale = SessionState.getLocale(m_servletRequest.getSession());
			
			throw new BadRequestException("COMMANDS_MISSING_REQ_PARAMETERS", null, locale, m_Base);
		}
		
		Result result = null;
		HttpSession session = m_servletRequest.getSession();
		IDal dal = Application.getDal(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		result = ViewHelper.getAdHocViewDetails(
				dal,
				view,
				ui.getQueryParameters(),
				null,
				dbLocaleSuffix);
		
		return result;
	}
}
