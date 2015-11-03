/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.webapi.controllers;

import java.util.ArrayList;

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
import com.absolute.am.dal.model.ComputerInfo;
import com.absolute.am.webapi.Application;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;


/**
 * <h3>Computers API</h3>
 * <p>The Computers API is used to:
 * <ul>
 *    <li>query all computers in the system</li>
 *    <li>query the related details (such as CPU, volumes, and memory) of a computer specified with id</li>
 * </ul>
 * </p>
 * 
 * @author klavin
 *
 */

@Path ("/computers")
public class Computers {
	
	private static Logger m_logger = LoggerFactory.getLogger(Computers.class.getName()); 
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

    public static final String VIEW_NAME_ALL_COMPUTERS = "allcomputers";
	public static final String VIEW_NAME_MAC_ONLY = "maconly";
	public static final String VIEW_NAME_PC_ONLY= "pconly";
	
	// PC specific views
	public static final String VIEW_NAME_HARDWARE_FOR_PC = "hardwareforpc";
	public static final String VIEW_NAME_CPU_FOR_PC = "cpuforpc";
	public static final String VIEW_NAME_SYSTEM_SOFTWARE_FOR_PC = "systemsoftwareforpc";
	public static final String VIEW_NAME_VOLUMES_FOR_PC = "volumesforpc";
	public static final String VIEW_NAME_INSTALLED_SOFTWARE_FOR_PC = "installedsoftwareforpc";
	public static final String VIEW_NAME_MISSING_SOFTWARE_FOR_PC = "missingsoftwareforpc";
	
	// Mac Specific views
	public static final String VIEW_NAME_HARDWARE_FOR_MAC = "hardwareformac";
	public static final String VIEW_NAME_CPU_FOR_MAC = "cpuformac";
	public static final String VIEW_NAME_SYSTEM_SOFTWARE_FOR_MAC = "systemsoftwareformac";
	public static final String VIEW_NAME_VOLUMES_FOR_MAC = "volumesformac";
	public static final String VIEW_NAME_INSTALLED_SOFTWARE_FOR_MAC = "installedsoftwareformac";
	public static final String VIEW_NAME_MISSING_SOFTWARE_FOR_MAC = "missingsoftwareformac";
	public static final String VIEW_NAME_INSTALLED_PROFILES_FOR_MAC = "installedprofilesformac";
	
	// Shared PC/Mac views
	public static final String VIEW_NAME_MEMORY_FOR_COMPUTER= "memoryforcomputer";
	public static final String VIEW_NAME_AGENT_INFORMATION_FOR_COMPUTER= "agentinfoforcomputer";
	public static final String VIEW_NAME_NETWORK_ADAPTERS_FOR_COMPUTER = "networkadaptersforcomputer";
	public static final String VIEW_NAME_ADMINISTRATORS_FOR_COMPUTER = "administratorsforcomputer";
	public static final String VIEW_NAME_INVENTORY_SERVERS_FOR_COMPUTER = "inventoryserversforcomputer";
	
	// Possible platforms
	private static final Integer AGENT_PLATFORM_MAC = 1;
	private static final Integer AGENT_PLATFORM_PC = 2;
	private static final Integer AGENT_PLATFORM_LINUX = 3;

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	// made this public to test code can see it.
	public static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Computers", 0),
		new ViewDescription(VIEW_NAME_MAC_ONLY, "Macs Only", 1),
		new ViewDescription(VIEW_NAME_PC_ONLY, "PCs Only", 2)
	};

	public static final ViewDescription[] m_pcViewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_INVENTORY_SERVERS, "Inventory Servers for this PC", 0),
		new ViewDescription(ViewHelper.VH_VIEW_ADMINISTRATORS, "Administrators for this PC", 1),
		new ViewDescription(ViewHelper.VH_VIEW_HARDWARE, "Hardware Information for this PC", 2),
		new ViewDescription(ViewHelper.VH_VIEW_CPU, "CPU Information for this PC", 3),
		new ViewDescription(ViewHelper.VH_VIEW_SYSTEM_SOFTWARE, "System Software Information for this PC", 4),
		new ViewDescription(ViewHelper.VH_VIEW_VOLUMES, "Volumes Information for this PC", 5),
		new ViewDescription(ViewHelper.VH_VIEW_SOFTWARE, "Installed Software Information for this PC", 6),
		new ViewDescription(ViewHelper.VH_VIEW_MISSING_PATCHES, "Missing Patches for this PC", 7),
		new ViewDescription(ViewHelper.VH_VIEW_MEMORY, "Memory Information for this PC", 8),
		new ViewDescription(ViewHelper.VH_VIEW_AGENT_INFO, "Agent Information for this PC", 9),
		new ViewDescription(ViewHelper.VH_VIEW_NETWORK_ADAPTERS, "Network Adapter Information for this PC",10)		
	};
	
	public static final ViewDescription[] m_macViewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_INVENTORY_SERVERS, "Inventory Servers for this Mac", 0),
		new ViewDescription(ViewHelper.VH_VIEW_ADMINISTRATORS, "Administrators for this Mac", 1),
		new ViewDescription(ViewHelper.VH_VIEW_HARDWARE, "Hardware Information for this Mac", 2),
		new ViewDescription(ViewHelper.VH_VIEW_CPU, "CPU Information for this Mac", 3),
		new ViewDescription(ViewHelper.VH_VIEW_SYSTEM_SOFTWARE, "System Software Information for this Mac", 4),
		new ViewDescription(ViewHelper.VH_VIEW_VOLUMES, "Volumes Information for this Mac", 5),
		new ViewDescription(ViewHelper.VH_VIEW_SOFTWARE, "Installed Software Information for this Mac", 6),
		new ViewDescription(ViewHelper.VH_VIEW_MISSING_PATCHES, "Missing Patches for this Mac", 7),
		new ViewDescription(ViewHelper.VH_VIEW_INSTALLED_PROFILES, "Installed Profiles for this Mac", 8),
		new ViewDescription(ViewHelper.VH_VIEW_MEMORY, "Memory Information for this Mac", 9),
		new ViewDescription(ViewHelper.VH_VIEW_AGENT_INFO, "Agent Information for this Mac", 10),
		new ViewDescription(ViewHelper.VH_VIEW_NETWORK_ADAPTERS, "Network Adapter Information for this Mac", 11)
	};

	/**
	 * <p>Returns a list of named views available for computers. Please refer to <strong>/api/mobiledevices/views</strong> –
	 *    GET for an example of the response to this request.</p>
	 * <p>The returned view names include "all" (allcomputers), "maconly" (Macs Only), and "pconly" (PCs Only).</p>   
	 *    
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return Returns all list of available views for computers.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForComputers() throws Exception  {
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}

	/**
	 * <p>Returns the list of views available for the computer specified with id.</p>
	 *
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given Id of the computer
	 * @return Returns data from given computer id.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/views")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public ViewDescriptionList getViewsForComputer(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {	
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getSystemSoftwareForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		IDal dal = Application.getDal(session);
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		ViewDescriptionList result = new ViewDescriptionList();	
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			result.setViewDescriptions(m_macViewDescriptions);
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			result.setViewDescriptions(m_pcViewDescriptions);
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view. 
	 *    The result is a multi-row result set, but the exact content depends on the definition of the view.</p>
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
			viewname = VIEW_NAME_ALL_COMPUTERS;
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
	 * <p>Returns a list of the hardware installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param computerId The given computer id
	 * @return Returns a list of hardware data.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/hardware")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getHardwareForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getHardwareForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);				
		
		Result result = null;
		IDal dal = Application.getDal(session);
		
		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_HARDWARE_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_HARDWARE_FOR_PC;
		} else if (agentPlatform == AGENT_PLATFORM_LINUX) {
			//UI is expected some results
			viewName = VIEW_NAME_HARDWARE_FOR_MAC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}	
	
	/**
	 * <p>Returns a list of the CPU data of the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of CPU data.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/cpu")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getCPUForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getNetworkAdaptersForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_CPU_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_CPU_FOR_PC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the system software installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of system softwares.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/systemsoftware")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getSystemSoftwareForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getSystemSoftwareForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);				
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_SYSTEM_SOFTWARE_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_SYSTEM_SOFTWARE_FOR_PC;
		} else if (agentPlatform == AGENT_PLATFORM_LINUX) {
			//UI is expected some results
			viewName = VIEW_NAME_SYSTEM_SOFTWARE_FOR_MAC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the hard dive volumes of the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of hard dive volumes.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/volumes")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getVolumesForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getVolumesForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_VOLUMES_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_VOLUMES_FOR_PC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the software installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of software.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/software")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getInstalledSoftwareForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getInstalledSoftwareForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_INSTALLED_SOFTWARE_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_INSTALLED_SOFTWARE_FOR_PC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of patches that are missing from the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given computer id
	 * @return Returns a list of missing patches.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/missingpatches")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getMissingPatchesForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getMissingPatchesForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		// Grab the basic agent information from the agent_info table
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		// Based off of the information we now have about the device, lets find out which view to use.
		String viewName = null;
		int agentPlatform = computerInfo.getAgentPlatform();
		if (agentPlatform == AGENT_PLATFORM_MAC) {
			viewName = VIEW_NAME_MISSING_SOFTWARE_FOR_MAC;
		} else if (agentPlatform == AGENT_PLATFORM_PC) {
			viewName = VIEW_NAME_MISSING_SOFTWARE_FOR_PC;
		} else {
			// Throw NOT_FOUND as we do not support Linux (or other) for this implementation.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the profiles installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given computer id
	 * @return Returns a list of missing patches.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/installedprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getInstalledProfilesForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getInstalledProfilesForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_INSTALLED_PROFILES_FOR_MAC, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the memory data of the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given computer id
	 * @return Returns a list of memory data.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/memory")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getMemoryForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getMemoryForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);				
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_MEMORY_FOR_COMPUTER, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of details on the Absolute Manage Agent installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.
	 *      The response includes information about the agent, such as version, build number, and IP address.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of agent info.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/agentinfo")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getAgentInfoForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getAgentInfoForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);				
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_AGENT_INFORMATION_FOR_COMPUTER, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the network adapters installed on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given computer id
	 * @return Returns a list of network adapters.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/networkadapters")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getNetworkAdaptersForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getNetworkAdaptersForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_NETWORK_ADAPTERS_FOR_COMPUTER, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns the administrator users created on the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param computerId The given computer id
	 * @return Returns a list of the administrators.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/administrators")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getAdministratorsForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getAdministratorsForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ADMINISTRATORS_FOR_COMPUTER, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the inventory servers associated with the specified computer. The response is a multi-row <strong>ResultSet</strong> with meta data.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param id The given computer id
	 * @return Returns a list of the inventory servers.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/inventoryservers")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no computer with this id.")
		})
	public Result getInventoryServersForComputerId(
			@Context UriInfo ui,
			@PathParam("id") long computerId) throws Exception  {		
		
		MDC.put("computerId", "" + computerId);
		m_logger.debug("getInventoryServersForComputerId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getComputerAdminAccessHandler().adminHasAccessToComputer(SessionState.getFilterByAdmin(session), computerId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}
		
		ComputerInfo computerInfo = dal.getComputerHandler().getComputer(computerId);
		if (computerInfo == null) {
			throw new NotFoundException("NO_COMPUTER_FOUND_FOR_ID", new Object[]{computerId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(computerId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_INVENTORY_SERVERS_FOR_COMPUTER, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("computerId");
		
		return result;
	}
}
