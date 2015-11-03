/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.io.FileNotFoundException;
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

import org.codehaus.enunciate.jaxrs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.IPhoneInfo;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.controllers.ViewHelper;
import com.absolute.am.webapi.Application;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.StringUtilities;


/**
 * <h3>Mobile Devices API</h3>
 * 
 * <p>This API provides access to detailed asset data for each type of mobile device managed by AM. 
 * Currently that includes devices running iOS, Android, and Windows Phone 7. The API is defined as follows.</p>
 * 
 * @author Daragh Lavin
 */
@Path ("/mobiledevices")
public class MobileDevices {
	
	private static Logger m_logger = LoggerFactory.getLogger(MobileDevices.class.getName());
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
    
	private static final String VIEW_NAME_ONE_MOBILE_DEVICE = "onemobiledevice";
	private static final String VIEW_NAME_ALL_MOBILE_DEVICES = "allmobiledevices";
	private static final String VIEW_NAME_POLICICES_FOR_DEVICES= "policiesfordevice";
	private static final String VIEW_NAME_CONTENT_FOR_POLICICES_FOR_DEVICES= "contentforpoliciesfordevices";
	private static final String VIEW_NAME_ALL_IOS_DEVICES = "alliosdevices";
	private static final String VIEW_NAME_ALL_IPHONES = "alliphones";
	private static final String VIEW_NAME_ALL_IPADS = "allipads";
	private static final String VIEW_NAME_ALL_IPOD_TOUCH_DEVICES = "allipodtouchdevices";
	private static final String VIEW_NAME_ALL_ANDROID_DEVICES = "allandroiddevices";
	private static final String VIEW_NAME_ALL_ANDROID_PHONES = "allandroidphones";
	private static final String VIEW_NAME_ALL_ANDROID_TABLETS = "allandroidtablets";
	private static final String VIEW_NAME_ALL_WINDOWS_PHONE_DEVICES = "allwindowsphonedevices";
	private static final String VIEW_NAME_DEVICE_INSTALLED_SOFTWARE_DETAILS = "deviceinstalledsoftwaredetails";
	private static final String VIEW_NAME_CONFIGURATION_PROFILES_FOR_DEVICE = "configurationprofilesfordevice";
	private static final String VIEW_NAME_USER_FOR_DEVICE = "userdetailsfordevice";
	private static final String VIEW_NAME_DEVICE_CERTIFICATES_FOR_DEVICE = "certificatesfordevice";
	private static final String VIEW_NAME_ADMINISTRATORS_FOR_DEVICE = "administratorsfordevice";
	private static final String VIEW_NAME_CUSTOM_FIELDS_FOR_DEVICE = "customfieldsfordevice";
	private static final String VIEW_NAME_PROVISIONING_PROFILES_FOR_DEVICE = "provisioningprofilesfordevice";
	private static final String VIEW_NAME_CONFIGURATION_PROFILES_FOR_POLICICES_FOR_DEVICE = "configurationprofilesforpoliciesfordevice";
	private static final String VIEW_NAME_3RD_PARTY_APPLICATIONS_FOR_POLICICES_FOR_DEVICE = "thirdpartyapplicationsforpoliciesfordevice";
	private static final String VIEW_NAME_IN_HOUSE_APPLICATIONS_FOR_POLICICES_FOR_DEVICE = "inhouseapplicationsforpoliciesfordevice";
	private static final String VIEW_NAME_DEVICE_ACTIONS_FOR_DEVICE = "actionsfordevice";
	
	// made this public to test code can see it.
	public static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Mobile Devices", 0),
		new ViewDescription(VIEW_NAME_ALL_IOS_DEVICES, "All iOS Devices", 1),
		new ViewDescription(VIEW_NAME_ALL_IPHONES, "All iPhones", 2),
		new ViewDescription(VIEW_NAME_ALL_IPADS, "All iPads", 3),
		new ViewDescription(VIEW_NAME_ALL_IPOD_TOUCH_DEVICES, "All iPod Touch Devices", 4),
		new ViewDescription(VIEW_NAME_ALL_ANDROID_DEVICES, "All Android Devices", 5),
		new ViewDescription(VIEW_NAME_ALL_ANDROID_PHONES, "All Android Phones", 6),
		new ViewDescription(VIEW_NAME_ALL_ANDROID_TABLETS, "All Android Tablets", 7),
		new ViewDescription(VIEW_NAME_ALL_WINDOWS_PHONE_DEVICES, "All Windows Phone Devices", 8),
		new ViewDescription(VIEW_NAME_ALL_MOBILE_DEVICES, "All Mobile Devices", 9)
	};
	

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	/**
	 * <p>Get a list of named views available for mobile devices. This API returns a ViewDescriptionList type object. 
	 *    A ViewDescriptionList contains an array of ViewDescription objects.</p>
	 * <p>The attributes of each ViewDescription include:</p>
	 * <p><strong>viewName</strong> – this is the value passed to /api/mobiledevices/views/{viewName} to get the output of the view.<br/>
	 *		<strong>viewDisplayName</strong> – this is a user-friendly name/title for the view.<br/>
	 *		<strong>Id</strong> – this is an identifier for the view that is unique within this list.</p>
	 * <p>This is an example of a response:</p>
	 * <pre>
	 * { "viewDescriptions": [
	 * &emsp;{
	 * &emsp;		"viewName":"all",
	 * &emsp;		"viewDisplayName":"All Mobile Devices",
	 * &emsp;		"id":0},
	 * &emsp;		{	"viewName":"alliosdevices",
	 * &emsp;			"viewDisplayName":"All iOS Devices",
	 * &emsp;			"id":1},
	 * &emsp;		{	"viewName":"alliphones",
	 * &emsp;			"viewDisplayName":"All iPhones",
	 * &emsp;			"id":2}
	 * ]}
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @return Returns list of named views available for mobile devices. This API returns a ViewDescriptionList type object. A ViewDescriptionList contains an array of ViewDescription objects.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForMobileDevices() throws Exception  {
		m_logger.debug("MobileDevices.getViewsForMobileDevices called");
				
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Get the output of a named view. The result is a multi-row result set, but the exact content depends on the definition of the view. </p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param viewname The value passed to /api/mobiledevices/views/{<strong>viewName</strong>} to get the output of the view.<br/>
	 * @return The result is a multi-row result set, but the exact content depends on the definition of the view. 
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The <strong>viewName</strong> is not found. ")
		})
	public Result getView(
			@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		m_logger.debug("MobileDevices.getView called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_MOBILE_DEVICES;
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
	 * <p>Return the asset with the given <a href="#idTag">id</a>. </p>
	 * <p>All known single dimensional attributes of the asset are returned. The result is a single row result set. </p>
	 * <p>NOTE: "single dimensional" refers to attributes that have a one-to-one relationship with the asset (e.g., make or model). 
	 *    Attributes with a one-to-many relationship are not returned (e.g., the list of software applications installed).</p>
	 * 
	 * <p>The result contains the same attributes as seen in the device view of the Admin Console (approximately 80 items, too many to list here). 
	 *    As the same endpoint is used for all device types, the client is responsible for determining which properties do not apply for each device type
	 *    (e.g., Jailbroken applies only to iOS devices).</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id <span id="idTag"></span>
	 * @return Returns The asset with the given id. 
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
	  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
	})
	public Result getMobileDeviceForId(
			@Context UriInfo ui,
			@PathParam("id") String deviceId) throws Exception  {
			
		MDC.put("deviceId", deviceId);
		m_logger.debug("getMobileDeviceForId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
				
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), Long.parseLong(deviceId))) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}
			
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(deviceId);			
		
		result = ViewHelper.getViewDetails(
					dal, 
					VIEW_NAME_ONE_MOBILE_DEVICE, 
					ViewHelper.getQueryParameters(ui, session), 
					userParams,
					dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		if (result.getRows().length < 1) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}
		
		return result;
	}
	
	/**
	 * <p>Get a list of policies that this device has been assigned to. 
	 *    The response is a multi-row result set with meta data. The attributes returned include: Policy Name, Smart Policy.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of policies that this device has been assigned to. The response is a multi-row result set with meta data. The attributes returned include: Policy Name, Smart Policy.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/policies")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getPoliciesForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getPoliciesForMobileDeviceForId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
			
		Result result = null;
		IDal dal = Application.getDal(session);
			
		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);	
		}
		
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		String uniqueID = null;
		if (deviceInfo != null) {
			uniqueID = deviceInfo.getUniqueId();
		}
		if (uniqueID == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		String smartPolicyIds = dal.getPolicyHandler().getSmartPolicyIdsForDeviceAsString(deviceId);

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(uniqueID);
		userParams.add(smartPolicyIds);			
		
		result = ViewHelper.getViewDetails(
					dal, 
					VIEW_NAME_POLICICES_FOR_DEVICES, 
					ViewHelper.getQueryParameters(ui, session),
					userParams,
					dbLocaleSuffix);
			
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * TBD: Not Implemented.
	 * <p>Get the location history for a device. The response is a multi-row result set (possibly empty) with meta data.</p>
	 * <p>The result set should include the same columns as in the Admin Console: Tracking Time, Latitude, Longitude, Location Accuracy,
	 *    Mobile Device Cell IP Address, Mobile Device Public IP Address, Mobile Device Wi-Fi IP Address, Mobile Device Battery Level, Mobile Device Battery </p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns the location history for a device. The response is a multi-row result set (possibly empty) with meta data.
	 * @throws Exception 
	 */
	/* dlavin: disabling access to this. @GET @Path("/{id}/locations")
	 @Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getLocationForMobileDeviceForId(@Context HttpServletRequest req,
			@Context UriInfo ui,
			@PathParam("id") String deviceId) throws Exception  {
		
		MDC.put("deviceId", deviceId);
		m_logger.debug("getLocationForMobileDeviceForId called.");

		Result result = null;
		IDal dal = Application.getDal(m_servletRequest.getSession());
		
		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(m_servletRequest.getSession()), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new WebApplicationException(Response.Status.NOT_FOUND);		
		}
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(deviceId);
		result = ViewHelper.getViewDetails(dal, VIEW_NAME_IOS_DEVICE_TRACKING, 
				ui.getQueryParameters(), userParams);
		
		MDC.remove("deviceId");
		
		return result;
	}
	*/

	/**
	 * <p>Get a list of mobile content assigned to this device, and details on the policy that caused the assignment. The response is a multi-row result set with meta data.</p>
	 * <p>The attributes returned should match those shown in the Admin Console: Media Name, Policy Name, Rule, Availability Time Start, Availability Time End.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of mobile content assigned to this device, and details on the policy that caused the assignment. The response is a multi-row result set with meta data.
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/assigned/content")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getMediaForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getMediaForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);			
		}

		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		String uniqueID = null;
		if (deviceInfo != null) {
			uniqueID = deviceInfo.getUniqueId();
		}
		if (uniqueID == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);	
		}
		
		String smartPolicyIds = dal.getPolicyHandler().getSmartPolicyIdsForDeviceAsString(deviceId);
		String nonSmartPolicyIds = dal.getPolicyHandler().getNonSmartPolicyIdsForDeviceAsString(uniqueID);

		StringBuilder policyIds = new StringBuilder();
		policyIds.append(smartPolicyIds);
		if (nonSmartPolicyIds.length() > 0) {
			if (policyIds.length() > 0) {
				policyIds.append(",");
			}
			policyIds.append(nonSmartPolicyIds);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyIds.toString());
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CONTENT_FOR_POLICICES_FOR_DEVICES, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of assigned profiles and details on which policy caused the assignment. The response is a multi-row result set with meta data.</p>
	 * <p>The attributes of the assigned configuration profile, returned by the endpoint:
 			Id,  Profile Name, Policy Name, Rule, Platform Type, Availability Time Start, Availability Time End.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of assigned profiles and details on which policy caused the assigned. The response is a multi-row result set with meta data.
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/assigned/configurationprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getAssignedConfigurationProfilesForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getAssignedConfigurationProfilesForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
	
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);			
		}

		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		String uniqueID = null;
		if (deviceInfo != null) {
			uniqueID = deviceInfo.getUniqueId();
		}
		if (uniqueID == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);	
		}
		
		String smartPolicyIds = dal.getPolicyHandler().getSmartPolicyIdsForDeviceAsString(deviceId);
		String nonSmartPolicyIds = dal.getPolicyHandler().getNonSmartPolicyIdsForDeviceAsString(uniqueID);

		StringBuilder policyIds = new StringBuilder();
		policyIds.append(smartPolicyIds);
		if (nonSmartPolicyIds.length() > 0) {
			if (policyIds.length() > 0) {
				policyIds.append(",");
			}
			policyIds.append(nonSmartPolicyIds);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyIds.toString());
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CONFIGURATION_PROFILES_FOR_POLICICES_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of assigned third-party applications and the details on which policy caused the assignment. The response is a multi-row result set with meta data.</p>
	 * <p>In the AM Console, this data is available under Device/Assigned Apps, Policies & Media.
			Note that in AM Console both third-party and in-house applications are combined in one view.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of assigned third-party applications and the details on which policy caused the assignment. The response is a multi-row result set with meta data. 
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/assigned/thirdpartyapplications")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getAssignedApplicationsForDeviceId(
		@Context UriInfo ui,
		@PathParam("id") long deviceId) throws Exception  {		
	
		m_logger.debug("getAssignedApplicationsForDeviceId called.");
		
		return getAssignedApplications(deviceId, ui, VIEW_NAME_3RD_PARTY_APPLICATIONS_FOR_POLICICES_FOR_DEVICE);
	
	}
	
	/**
	 * <p>Get a list of assigned in-house applications and the details on which policy caused the assignment. The response is a multi-row result set with meta data.</p>
	 * <p>In the AM Console, this data is available under Device/Assigned Apps, Policies & Media. 
		  Note that in the AM Console, both third-party and in-house applications are combined in one view.</p>
	 * <p>The attributes of the assigned application returned by the endpoint: Id, Application Name, Policy Name, Policy Rule, Platform Type, Min OS Version.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId the given device id
	 * @return Returns list of assigned in-house applications and the details on which policy caused the assignment. The response is a multi-row result set with meta data.
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/assigned/inhouseapplications")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getAssignedInHouseApplicationsForDeviceId(
		@Context UriInfo ui,
		@PathParam("id") long deviceId) throws Exception  {		
	
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getAssignedInHouseApplicationsForDeviceId called.");
		
		return getAssignedApplications(deviceId, ui, VIEW_NAME_IN_HOUSE_APPLICATIONS_FOR_POLICICES_FOR_DEVICE);
	}
	
	/**
	 * <p>Get a list of applications that are installed on this device. The response is a multi-row result set with meta data.</p>
	 * <p>In the AM Console, this data is available under Device/Applications.</p>
	 * <p>The result set should include the same columns as in the Admin Console: Name, Version String, Build Number, Bundle Identifier,
	 *    App Size, App Data Size, App Status, Prevent Data Backup, Bound to MDM,  Mobile Device Installed App Data Directory</p>
	 * <p>NOTE: The columns visible in the AM Console are different for iOS and Android devices.
	 *    The result set returned will contain the super set of columns and the UI can choose what it wants to display.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of applications that are installed on this device. The response is a multi-row result set with meta data. 
	 * @throws Exception 
	 */
	@GET @Path("/{id}/applications")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getApplicationsForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getApplicationsForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_DEVICE_INSTALLED_SOFTWARE_DETAILS, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;

	}
	
	/**
	 * <p>Get a list of configuration profiles installed on this device. The result is a multi-row result set with meta data.</p>
	 * <p>The columns returned should match that in the Admin Console: Name, Profile Description, Profile Organization, Profile Identifier,
	 *    Mobile Device Installed Profile Type, Profile UUID, Profile Encrypted, Profile Managed, Profile Allow Removal</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of configuration profiles installed on this device. The result is a multi-row result set with meta data.
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/configurationprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getConfigurationProfilesForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getConfigurationProfilesForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CONFIGURATION_PROFILES_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of the provisioning profiles that have been installed/applied on the device. The response is a multi-row result set with meta data.</p>
	 * <p>The result set should include the same columns as shown in the Admin Console: Profile Name, Profile Expiry Date, Profile UUID</p>
	 * 
     * <p>Rights required:</br>
	 *    None</p>
	 *    
	 * @param deviceId The given device id
	 * @return Returns a multiple row result set with the provisioning profiles installed on this device.
	 * @throws Exception 
	 * @throws NotFoundException 
	 */
	@GET @Path("/{id}/provisioningprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getProvisioningProfilesForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getProvisioningProfilesForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_PROVISIONING_PROFILES_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get the user-specific attributes for the user associated with this device. The response is a single row result set with meta data.</p>
	 * <p>The result set includes the same attributes as seen in the Admin Console: Display Name, First Name, Last Name, Log-on Name, E-Mail, 
	 *    Phone Number, Department, Company, Street, City, State, ZIP code, Country, Office, Organizational Unit Path, Organizational Unit, Is Member Of, Enrollment Username, Enrollment Domain</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of applications that are installed on this device. The response is a multi-row result set with meta data. 
	 * @throws Exception 
	 */	
	@GET @Path("/{id}/user")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getUserForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getUserForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);	
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_USER_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of certificates that are installed on the device. The response is a multi-row result set with meta data.</p>
	 * <p>The result set should include the same columns as shown in the Admin Console: Certificate Name, Certificate Is Identity</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of certificates that are installed on the device. The response is a multi-row result set with meta data. 
	 * @throws Exception 
	 */
	@GET @Path("/{id}/certificates")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getCertificatesForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getCertificatesForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);		
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_DEVICE_CERTIFICATES_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of administrators that are assigned to manage this device. The result is a multi-row result set with meta data.</p>
	 * <p>The columns returned should match that in the Admin Console: Administrator Name</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId the given device id
	 * @return Returns list of administrators that are assigned to manage this device. The result is a multi-row result set with meta data.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/administrators")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getAdministratorsForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getAdministratorsForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		//dal.getAdminAccessHandler().refreshAllAdmins();
		//only update data for given device
		dal.getAdminAccessHandler().refreshAdminsForDevice(deviceId);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);		
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ADMINISTRATORS_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("deviceId");
		
		return result;
	}

	/**
	 * <p>Get a list of the actions that have been performed on this device. The response is a multi-row result set with meta data. 
	 * The following attributes are returned: Action Name, Action Type, Description, Supported Platforms, Policy Name
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId the given device id
	 * @return Returns list of actions that have been performed on this device.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/actions")
	@Produces({ MediaType.APPLICATION_JSON })
	@Right(AMRight.AllowManageiOSDevices)
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
		})
	public Result getActionsForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getActionsForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);		
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_DEVICE_ACTIONS_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of custom fields associated with this device. The response is a multi-row result set with meta data.</p>
	 * <p>The result set should include the same columns as in the Admin Console: Information, Data</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of custom fields associated with this device. The response is a multi-row result set with meta data.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/customfields")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		@ResponseCode ( code = 404, condition = "A device with this {id} could not be found.")
	})
	public Result getCustomFieldsForDeviceId(
			@Context UriInfo ui,
			@PathParam("id") long deviceId) throws Exception  {		
		
		MDC.put("deviceId", "" + deviceId);
		m_logger.debug("getCustomFieldsForDeviceId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);	
		}
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(Long.toString(deviceId));

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CUSTOM_FIELDS_FOR_DEVICE, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("deviceId");
		
		return result;
	}
	
	/**
	 * TBD: Not Implemented.
	 * <p>Get a list of actions that have been performed on this device. The response is a multi-row result set with meta data.</p>
	 * <p>The attributes returned should match those shown in the Admin Console: Action Name, Action Type, Action Execution Time, Policy Name</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowManageiOSDevices </p>
	 * 
	 * @param deviceId The given device id
	 * @return Returns list of applications that are installed on this device. The response is a multi-row result set with meta data. 
	 * @throws Exception 
	 */
//	public Result getPerformedactions ForDeviceId(
	//		@Context UriInfo ui,
		//	@PathParam("id") long deviceId) throws Exception  {		
	
	
	/**
	 * Private procedure for shared functionality to retrieve assigned applications for a device.
	 * 
	 * @deviceId id of the device
	 * @ui ui object passed with the request
	 * @viewName the name of the view to fetch the assigned applications from
	 * 
	 * @return result set retrieved from the view for the given device id
	 */
	private Result getAssignedApplications(long deviceId, UriInfo ui, String viewName) throws Exception {
		MDC.put("deviceId", "" + deviceId);
		
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		if (!dal.getAdminAccessHandler().adminHasAccessToDevice(SessionState.getFilterByAdmin(session), deviceId)) {
			// Why NOT_FOUND? Because as far as this user is concerned, the device does not exist.
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);			
		}
	
		IPhoneInfo deviceInfo = dal.getDeviceHandler().getDevice(deviceId);
		String uniqueID = null;
		if (deviceInfo == null) {
			throw new NotFoundException("NO_DEVICE_FOUND_FOR_ID", new Object[]{deviceId}, locale, m_Base);
		} else {
			uniqueID = deviceInfo.getUniqueId();
		}
		
		String smartPolicyIds = dal.getPolicyHandler().getSmartPolicyIdsForDeviceAsString(deviceId);
		String nonSmartPolicyIds = dal.getPolicyHandler().getNonSmartPolicyIdsForDeviceAsString(uniqueID);

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(StringUtilities.concatenateCommaSeparatedLists(smartPolicyIds,nonSmartPolicyIds));
		
		result = ViewHelper.getViewDetails(
				dal,
				viewName, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
			
		MDC.remove("deviceId");
		
		return result;
	}
	

	
}
