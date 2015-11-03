package com.absolute.am.webapi.ssp.controllers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraUserCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.IEnumHandler;
import com.absolute.am.model.UserDevice;
import com.absolute.am.model.UserDevicesList;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.controllers.SessionState;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

/**
 * @author ephilippov
 */
@Path ("/ssp/userdevices")
public class UserDevices {
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;

	/**
	 * Returns all devices managed by this user. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @return all devices managed by this user. See the the UserDevice class for all the possible fields that may be returned.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public UserDevicesList getUserDevices() throws Exception {
				
		UserDevicesList result = new UserDevicesList();
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		sendCommand(result, CommandFactory.createSSPGetUserDeviceCommand(session.getAttribute(Login.SESSION_TOKEN_PARAM).toString()), session, locale);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void sendCommand(UserDevicesList result, CobraUserCommand command, HttpSession session, String locale) throws Exception {
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		ArrayList<UserDevice> resultArr;
		PropertyList amServerGetDevicesResult = null;
		Object[] deviceList;
		IEnumHandler enumHandler = Application.getDal(session, true).getEnumHandler();
		
		if( result.getUserDevices() != null ){
			resultArr = (ArrayList<UserDevice>) Arrays.asList(result.getUserDevices());
		} else {
			resultArr = new ArrayList<UserDevice>();
		}
		
		try {
			amServerGetDevicesResult = amServerProtocol.sendCommandAndGetResponse(command);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		
		deviceList = (Object[]) ((HashMap<String, Object>)amServerGetDevicesResult.get("CommandResultParameters")).get("DeviceList");
		
		if( deviceList != null ){		
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = {String.class, String.class};
			Method getEnumValueForKey = IEnumHandler.class.getMethod("getEnumValueForKey", parameterTypes);
			
			for(int i = 0; i < deviceList.length; i++){
				resultArr.add(new UserDevice((HashMap<String, Object>) deviceList[i], getEnumValueForKey, enumHandler));
			}
			
			if (resultArr.size() > 0){
				result.setUserDevices(Arrays.copyOf(resultArr.toArray(), resultArr.size(), UserDevice[].class));
			}
		}
	}
}
