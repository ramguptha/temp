package com.absolute.am.webapi.ssp.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.absolute.am.model.DefaultServerInfoResult;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.ssp.controllers.Login;

/**
 * @author ephilippov
 */
@Path ("/ssp/defaultServerInfo")
public class DefaultServerInfo {

	public static final String PROP_DEFAULT_SERVER_NAME = "com.absolute.am.webapi.controllers.DefaultServerInfo.serverName",
			PROP_DEFAULT_SERVER_PORT = "com.absolute.am.webapi.controllers.DefaultServerInfo.serverPort",
			PROP_SETTINGS_LOCATION = "com.absolute.am.webapi.controllers.DefaultServerInfo.settingsFile";
	private static long fileLastModified = 0;
	
	/**
	 * Get the default server and port from the settings/settings.txt file. This command originates from the Self-Service Portal (SSP), the application that allows end users to remotely manage computers and mobile devices enrolled under their account. Therefore, this command is executed on behalf of the end user, not the administrator.
	 * @return default server and port.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public DefaultServerInfoResult getServerInfo(@Context HttpServletRequest req){
		
		DefaultServerInfoResult serverInfo = new DefaultServerInfoResult();
		boolean forceSSPLoginPrompt = Application.getRuntimeProperty(Login.INI_SSP_FORCE_SERVER_PROMPT).equals("true");
		
		if(!forceSSPLoginPrompt){
			String serverName = Application.getRuntimeProperty(PROP_DEFAULT_SERVER_NAME),
					serverPort = Application.getRuntimeProperty(PROP_DEFAULT_SERVER_PORT);
			
			// re-read the settings.txt if the properties haven't been set yet
			if( serverName == null || serverPort == null ){
				initDefaultServerAndPort();
				
				serverName = Application.getRuntimeProperty(PROP_DEFAULT_SERVER_NAME);
				serverPort = Application.getRuntimeProperty(PROP_DEFAULT_SERVER_PORT);
			}
			
			serverInfo.setServerName(serverName);
			serverInfo.setServerPort(serverPort);
		}
		
		return serverInfo;
	}
	
	public static void initDefaultServerAndPort() {
		
		String settingsLocation = Application.getRuntimeProperty(PROP_SETTINGS_LOCATION);

		if(settingsLocation != null){			
			File settingsFile = new File(settingsLocation);
			if (new File(settingsLocation).exists()) {
				String[][] serverArray = readServerRegistration(settingsLocation);

				if (serverArray != null && serverArray.length > 0) {
					for (int i = 0; i < serverArray.length; i++) {
						if ("Yes".compareToIgnoreCase(serverArray[i][2]) == 0) {
							if (fileLastModified != settingsFile.lastModified()) {
								Application.setRuntimeProperty(PROP_DEFAULT_SERVER_NAME, serverArray[i][0]);
								Application.setRuntimeProperty(PROP_DEFAULT_SERVER_PORT, serverArray[i][1]);
								fileLastModified = settingsFile.lastModified();
								break;
							}
						}	
					}
				}
			}
		}
	}
	
	public static void setDefaultServerNameAndPort(String defaultNameValue, String defaulPortValue, String userName){
		String settingsLocation = Application.getRuntimeProperty(PROP_SETTINGS_LOCATION);
		File settingsFile = new File(settingsLocation);
		
		if (!settingsFile.exists()){
			new File(settingsFile.getParent()).mkdirs();
			
			try {
				settingsFile.createNewFile();
			} catch (Exception e) {}
		}
		
		// check if the server has been registered already
		boolean registeredAlready = false;
		String[][] registeredServers = readServerRegistration(settingsLocation);
		
		if (registeredServers != null && registeredServers.length > 0) {
			for (int i = 0; i < registeredServers.length; i++) {
				if (registeredServers[i][0].compareToIgnoreCase(defaultNameValue) == 0) {
					registeredAlready = true;
					registeredServers[i][0] = defaultNameValue;
					registeredServers[i][1] = defaulPortValue;
					registeredServers[i][2] = "Yes";
					registeredServers[i][3] = userName;
					registeredServers[i][4] = getCurrentDateTime();;
				} 
			}
		}
		// append the new one to the array of not exists
		if (!registeredAlready) {
			String[][] tempServers = new String[registeredServers.length + 1][];
			 if (registeredServers != null) {
			     System.arraycopy(registeredServers, 0, tempServers, 0, Math.min(registeredServers.length, tempServers.length));
			 }
			 tempServers[registeredServers.length] = new String[5];
			 tempServers[registeredServers.length][0] = defaultNameValue;
			 tempServers[registeredServers.length][1] = defaulPortValue;
			 tempServers[registeredServers.length][2] = "Yes";
			 tempServers[registeredServers.length][3] = userName;
			 tempServers[registeredServers.length][4] = getCurrentDateTime();
			 
			 registeredServers = tempServers; 
		}
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile, false)));
		    for (int i = 0; i < registeredServers.length; i++) {
		    	out.println(registeredServers[i][0] + "," + registeredServers[i][1] + "," + 
		    				registeredServers[i][2] + "," + registeredServers[i][3] + "," + registeredServers[i][4]);
		    }
		    
		    out.flush();
		    out.close();
		}catch (IOException e){
		    e.printStackTrace();
		}
	}
	
	private static String[][] readServerRegistration(String amServerSettingsFilePath) {
		File serverConfigFile = new File(amServerSettingsFilePath);
		
		if (!serverConfigFile.exists()){
			try {
				serverConfigFile.getParentFile().mkdirs();
				serverConfigFile.createNewFile();
			} catch (Exception e) {
				// do nothing
			}
		}
		
		BufferedReader csvFile = null;
		try {
			csvFile = new BufferedReader(new FileReader(amServerSettingsFilePath));
			LinkedList<String[]> rows = new LinkedList<String[]>();
			String dataRow = csvFile.readLine();

			while (dataRow != null){
			    rows.addLast(dataRow.split(","));
			    dataRow = csvFile.readLine();
			}

			String[][] serverArray = rows.toArray(new String[rows.size()][]);
			
			return serverArray;
		} catch (Exception e) {
			// do nothing
		} finally {
			if (csvFile != null) {
				try {
					csvFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private static String getCurrentDateTime() {
		Date date = new Date();
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd hh:mm:ss z");
        sdf.setCalendar(cal);
        cal.setTime(date);
        
        return sdf.format(date);
	}
}
