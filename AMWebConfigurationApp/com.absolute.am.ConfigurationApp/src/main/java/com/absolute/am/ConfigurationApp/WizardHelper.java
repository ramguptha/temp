package com.absolute.am.ConfigurationApp;

import java.awt.Cursor;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class WizardHelper {
	public static final String REGISTRY_KEY_APACHE_TOMCAT = "SOFTWARE\\Apache Software Foundation\\Tomcat\\8.0\\Tomcat8";
	public static final String AM_WEB_API_CONFIGURATION_FILE_PATH = "webapps\\com.absolute.am.webapi\\WEB-INF\\web.xml";
	public static final String WIZARD_SETTINGS_FILE_NAME = "wizard_settings.txt";
	public static final String AM_WEB_UI_ENV_FILE_NAME  = "env.js";
	public static final String TOMCAT_EXECUTABLE_FILE_NAME = "Tomcat8.exe";
	public static final String SUBFOLDER_NAME_BIN = "bin";
	
	// settings for the UI
	public static final Font ABSOLUTE_PLAINT_FONT = new Font("Tahoma", Font.PLAIN, 12);
	public static final Font ABSOLUTE_BOLD_FONT = new Font("Tahoma", Font.BOLD, 12);
	public static final String ABSOLUTE_HELP_IMAGE_FILE_PATH = "/images/question-mark.png";
	public static final String ABSOLUTE_DEFAULT_LANGUAGE = "en";

	// default values for AM server registration form
	public static final int PORT_NUMBER_MAX_LEN = 4 ;
	public static final String PORT_NUMBER_DEFAULT = "3971";
	public static final String PROP_SERVER_NAME = "serverName";
	public static final String PROP_SERVER_PORT = "serverPort";
	
	// Command Factory Login
	public static final String COMMAND_FACTORY_LOGIN_USERNMAE = "FakeLogin";
	public static final String COMMAND_FACTORY_LOGIN_PASSWORD = "FakePassword";
	
	public static final String ENV_SETTING_KEY_HELP_CONFIGURATIONAPP_ROOT = "helpConfigurationAppRoot";
	public static final String DATE_TIME_FORMAT_LONG = "yyyy/MMM/dd hh:mm:ss z";
	
	public static final int DATA_REFRESH_DELAY_MINIMUM_VALUE = 10;	// in millisecond
	public static final int DATA_REFRESH_DELAY_MAXIMUM_VALUE = 600000;	// in millisecond
	public static final int SESSION_TIMEOUT_MINIMUM_VALUE = 10;	// in second
	public static final int SESSION_TIMEOUT_MAXIMUM_VALUE = 10800;	// in second
	public static final int WALLPAPER_SIZE_MINIMUM_VALUE = 1024;		// in byte
	public static final int WALLPAPER_SIZE_MAXIMUM_VALUE = 10485760;	// in byte
	
	// properties
	public String settingsFilePath = "";		// AM web Api setting file (settings.txt)
	public String tomcatExecutableFile = "";	// i.e. Tomcat8.exe
	public String amWebApiConfigurationFile = "";
	
	public WizardHelper(){
		// We're assuming that the settings file for this app is in the same folder as the .jar
		Properties appSettingsProperties = new Properties();

		try {
			URI jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
			URI jarLocationParent = jarLocation.getPath().endsWith("/") ? jarLocation.resolve("..") : jarLocation.resolve(".");
			
			// check if the wizard setting file ("wizard_settings.txt) exists
			String wizardSettingFilePath = jarLocationParent.getPath() + WIZARD_SETTINGS_FILE_NAME;
			File wizardSettingsFile = new File(wizardSettingFilePath);
			if (wizardSettingsFile.exists()) {
				appSettingsProperties.load(new InputStreamReader(new FileInputStream(wizardSettingsFile.getAbsolutePath()), "UTF8"));
			} else {
				JOptionPane.showMessageDialog(
						null, 
						"The wizard setting file '" + WIZARD_SETTINGS_FILE_NAME + "' doesn't exist in the following folder:\r\n" + wizardSettingFilePath, 
						"Wizard Setting File Not Exists", 
						JOptionPane.ERROR_MESSAGE
						) ;
			}
		} catch (Exception e) {}
		
		settingsFilePath = appSettingsProperties.getProperty("settingsFile");
		tomcatExecutableFile = appSettingsProperties.getProperty("tomcatExecutable");
		amWebApiConfigurationFile = appSettingsProperties.getProperty("amWebApiConfigurationFile");
	}
	
	public static String getJarContainingFolder(@SuppressWarnings("rawtypes") Class aclass) {
		  CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
		  String jarPath = "";
		  File jarFile;
		  
		  try {
			  if (codeSource.getLocation() != null) {
					jarFile = new File(codeSource.getLocation().toURI());
			  }
			  else {
			    String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
			    String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
					jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
				
			    jarFile = new File(jarFilePath);
			  }
			  
			  jarPath = jarFile.getParentFile().getAbsolutePath();
			  
		  } catch (Exception e) {
			// do nothing;	
		  } 
		  
		  return jarPath;
	}
	
	public static String toHexString(byte bytes[]) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < bytes.length; ++i) {
			if (i > 0) {
				buf.append(":");
			}
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}
		return buf.toString();
	}
	
	// create number-formatted text-field
	public static JTextField createNumberTextFile(int x, int y, int width, int height) {
		JTextField txtField = new JTextField();
		txtField.setHorizontalAlignment(SwingConstants.LEFT);
		txtField.setBounds(x, y, width, height);
		txtField.setColumns(10);
		
		return txtField;
	}
	
	// create hyper-link
	public static SwingHyperLink createSwingHyperLink(String url) {
		SwingHyperLink link = new SwingHyperLink("", url);
		link.setIcon(new ImageIcon(ConfigurationWizard.class.getResource(ABSOLUTE_HELP_IMAGE_FILE_PATH)));
		link.setBounds(548, 9, 33, 34);
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		return link;
	}
	
	// craete normal label
	public static JLabel createLabel(String text, int x, int y, int width, int height) {
		return createLabel(
				text,
				x, y, width, height,
				SwingConstants.LEFT,
				ABSOLUTE_BOLD_FONT
				);
	}
	
	public static JLabel createLabel(String text, int x, int y, int width, int height, int alignment) {
		return createLabel(
				text,
				x, y, width, height,
				alignment,
				ABSOLUTE_BOLD_FONT
				);
	}
	
	public static JLabel createLabel(String text, int x, int y, int width, int height, int alignment, Font font) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		label.setHorizontalAlignment(alignment);
		label.setFont(font);
		
		return label;
	}

	// create plain-text-font label 
	public static JLabel createPlainLabel(String text, int x, int y, int width, int height) {
		return createLabel(
				text,
				x, y, width, height,
				SwingConstants.LEFT,
				ABSOLUTE_PLAINT_FONT
				);
	}
	
	public static void restartTomcat(String tomcatExecutableFilePath) {
		Process process;
		
		try {
			process = Runtime.getRuntime().exec("cmd /C \"" + tomcatExecutableFilePath + "\" stop");
			process.waitFor();
			process = Runtime.getRuntime().exec("cmd /C \"" + tomcatExecutableFilePath + "\" start");
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Give the users group full permissions
	// WARNING: this code will only work when the registration wizard is ran through the installer
	// as the installer runs Java as an Administrator
	public static void changeUacPermission(String fileFullPath) {
		try {
			Runtime.getRuntime().exec("icacls \"" + fileFullPath + "\" /grant Users:F");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 public static String getJavaExecutablePath() {
	    	String path = "";
	    	String javaHome = System.getProperty("java.home");
	        File file = new File(javaHome);
	        file = new File(file, "bin");
	        file = new File(file, "javaw.exe");
	        
	        if (file.exists()) {
	        	path = file.toString();
	        }
	        
	        return path;
	    }
	 
	 public static String parseAMWebUiEnvironmentSettings(String envFilePath, String envSettingKey) {
		 // get the Json string
		 File envFile = new File(envFilePath);
		 if (envFile.exists()) {
			 try {
	                byte[] bytes = Files.readAllBytes(envFile.toPath());
	                String content = new String(bytes,"UTF-8");
	                if (content.length() > 0) {
	                	int beginIndex = content.indexOf("AM_ENV=") + "AM_ENV=".length();
	                	int endIndex = content.lastIndexOf(";");
	                	String jsonString = content.substring(beginIndex, endIndex).replaceAll("'","\"");

	                	Map<String,String> map = new HashMap<String,String>();
	                	ObjectMapper mapper = new ObjectMapper();
	                		
	                	mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	                	map = mapper.readValue(jsonString, new TypeReference<HashMap<String,String>>(){});	
	                	
	                	String uri = map.get(envSettingKey);
	                	if (uri !=null && uri.length() > 0 && (uri.startsWith("http://") || uri.startsWith("https://"))) {
	                		return uri;
	                	}
	                }
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
		 }
		 
		 return null;
	 }
	 
	public static String getApacheTomcatInstallPath() {
		String path = "";
		
		try {
			path = WinRegistry.readString(
			        WinRegistry.HKEY_LOCAL_MACHINE,
			        REGISTRY_KEY_APACHE_TOMCAT,
			        "InstallPath");
		} catch (Exception e) {
			// do nothing
		}
		
		return path;
	}
	 
	public static boolean validateNumberString(String numberString, int minValue, int maxValue) {
		boolean valid = true;
		
		try {
			if (numberString == null || numberString.length() == 0) {
				valid = false;
			} else if (!numberString.matches("\\d+?")) {
				valid = false;
			} else if (Integer.parseInt(numberString) <= 0) {
				valid = false;
			} else if (minValue > 0 && Integer.parseInt(numberString) < minValue) {
				valid = false;
			} else if (maxValue > 0 && Integer.parseInt(numberString) > maxValue) {
				valid = false;
			}
		} catch (Exception ex) {
			valid = false;
		}
		
		return valid;
	 }
}
