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

import com.absolute.am.dal.ColumnConstants;
import com.absolute.am.dal.IApplicationsHandler.iconType;
import com.absolute.am.dal.IDal;
import com.absolute.am.webapi.Application;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.ImageUtilities;


/**
 * <h3>Books API</h3>
 * <p>This API is used to query Books.</p>
 * 
 * @author ephilippov
 *
 */
@Path ("/books")
public class BookstoreBooks {
	
	private static Logger m_logger = LoggerFactory.getLogger(BookstoreBooks.class.getName());
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String VH_VIEW_NAME_ALL_BOOKSTORE_BOOKS_PROFILES = "allbookstorebooks";
	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Bookstore Books", 0),
	};
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	/**
	 * <p>Returns all list of available views for this end-point.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForBookstoreBooks() throws Exception  {
		m_logger.debug("BookstoreBooks.getViewsForBookstoreBooks called");
				
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Returns data from given view.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param viewname name of the view
	 * @return
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getView(
			@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		m_logger.debug("BookstoreBooks.getView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if(viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VH_VIEW_NAME_ALL_BOOKSTORE_BOOKS_PROFILES;
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
	
	/**
	 * <p>Get the icon image for the book. The response is a binary data with appropriate MIME type specified in the response Content-Type header. 
	 *    Two formats of images are supported: PNG and JPEG. If the icon is of unknown format, an empty response is returned.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param bookId id of the book
	 * @return Returns the icon blob associated with a given book id 
	 * @throws Exception 
	 */
	@GET @Path("/{id}/icon")
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A book with this {id} could not be found.")
		})
	public Response getBookIconById(@PathParam("id") long bookId) throws Exception  {

		MDC.put("bookId", "" + bookId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		byte[] bookIcon = null;
		Response response = null;
		
		IDal dal = Application.getDal(session);

		// Check whether the book id exists
		// TODO: this code is only temporary and we should be checking for a valid book using something like:
		//if (null == dal.getBooksHandler().getBooks(bookId)) { ... }
		if (1 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal, 
				String.valueOf(bookId), VH_VIEW_NAME_ALL_BOOKSTORE_BOOKS_PROFILES, ColumnConstants.COLUMN_INFO_ITEM_ID_BOOKSTORE_BOOKS_ID) ) {
			throw new NotFoundException("BOOKS_ID_NOT_FOUND", null, locale, m_Base);
		}
		
		bookIcon = dal.getApplicationsHandler().getIcon(bookId, iconType.book);
		
		if(ImageUtilities.compareImageBytes(bookIcon, ResourceUtilities.pngSignature, false)) {
			//PNG icon
			response = Response.ok(bookIcon).type(ResourceUtilities.MIME_TYPE_PNG).build();
		} else if(ImageUtilities.compareImageBytes(bookIcon, ResourceUtilities.jpgSignature, true)) {
			//JPEG icon
			response = Response.ok(bookIcon).type(ResourceUtilities.MIME_TYPE_JPG).build();
		} else {
			//unknown format - return empty response
			response = Response.noContent().build();
		}
		
		MDC.remove("bookId");
		
		return response;
	}	
}
