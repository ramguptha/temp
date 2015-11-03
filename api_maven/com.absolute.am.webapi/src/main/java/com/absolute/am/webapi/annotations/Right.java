package com.absolute.am.webapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation used to mark endpoints as requiring authorization. The client should be assigned the 
 * named right in the AM Console.
 * See also {@link CommandPermission} and {@link com.absolute.am.webapi.filters.AuthorizationResourceFilterFactory}.
 * @author dlavin
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Right {
	AMRight[] value();
	
	/**
	 * Endpoints are marked with the {@link Right} annotation to indicate what Right is required 
	 * to access the endpoint. This enumeration is the list of all known Rights defined in AM. 
	 */
	public enum AMRight {
		AllowChangeCustomFields,
		AllowEnterCustomFieldData,
		AllowViewCustomFields,
		AllowManageiOSDevices,
		AllowModifyMobileActions,
		AllowModifyMobileMedia,
		AllowModifyiOSApplications,
		AllowModifyiOSConfigurationProfiles,
		AllowModifyiOSPolicies,
		AllowRemoveiOSDeviceRecords,
		AllowRemoveiOSHistoryCommands,
		AllowViewMobileDeviceTrackingData,
		AllowViewCommandsWindow,
		IsSuperAdmin,
		CanLogin,
		CanSeeAllRecords
	}
}
