package com.absolute.am.WebConfigurationUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

/*
 * update settings.xml from old version to the new version
 */

public class WebSettingFileUpdater {
	private static final String SETTINGS_SERVER_PORT = "serverPort", SETTINGS_SERVER_NAME = "serverName";
	private String settingFilePath = "";
	
	private String serverName = "";
	private String portNumberName = "";
	private static long fileLastModified = 0;
	
	public WebSettingFileUpdater(String settingFilePath) {
		this.settingFilePath = settingFilePath;
	}
	
	public void updateSettingFile() {
		getDefaultServerAndPort() ;
		
		if ((serverName != null && !serverName.isEmpty()) && 
				(portNumberName != null && !portNumberName.isEmpty())) {
			saveDefaultServerAndPortToCsvFile();
		}
	}
	
	private void getDefaultServerAndPort() {
		
		Properties settingsProps = new Properties();

		if(settingFilePath != null){
			FileInputStream fis = null;
			InputStreamReader isr = null;
			File settingsFile = null;
			
			try {
				settingsFile = new File(settingFilePath);
				fis = new FileInputStream(settingFilePath);
				isr = new InputStreamReader(fis, "UTF8");
				settingsProps.load(isr);
				serverName = settingsProps.getProperty(SETTINGS_SERVER_NAME);
				portNumberName = settingsProps.getProperty(SETTINGS_SERVER_PORT);
				fileLastModified = settingsFile.lastModified();
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				if( isr != null){
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if( fis != null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void saveDefaultServerAndPortToCsvFile() {
		File settingsFile = new File(settingFilePath);
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile, false)));
		    out.println(serverName + ","  + portNumberName + ",Yes,Upgraded," + usingDateFormatterWithTimeZone(fileLastModified));
		    out.flush();
		    out.close();
		}catch (IOException e){
		    e.printStackTrace();
		}
	}
	
	private String usingDateFormatterWithTimeZone(long input){
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd hh:mm:ss z");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
 
    }

}
