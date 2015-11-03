package com.absolute.am.ConfigurationApp;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class AMWebApiSettings {

	public boolean liveDataUpdatesEnabled = true;
	public boolean sspLoginEnabled = true;
	public long sessionTimeoutInSecond = 600;		// in second
	public boolean synchSessionAfrterDisconnectEnabled = true;
	public long dataRefreshingDelay = 2000;			// in millisecond
	public boolean sspForceServerPrompt  = true;
	public long wallpaperFileSize = 4194304; 		// in kilo-byte
	public long userPreferenceFileSize = 512000;	// in kilo-byte
	public String amWebApiConfigurationFilePath;
	public String helpWebPageUri = "";
	
	private static final String PARAMETER_NAME_LIVE_DATA_UPDATES = "com.absolute.webapi.Application.enableLiveDataUpdates";
	private static final String PARAMETER_NAME_SSP_LOGIN_ENABLED = "com.absolute.am.webapi.ssp.controllers.Login.Enabled";
	private static final String PARAMETER_NAME_SESSION_TIMEOUT = "com.absolute.am.webapi.controllers.Login.SessionTimeoutSeconds";
	private static final String PARAMETER_NAME_SYNCH_SESSION_AFTER_DISCONNECT = "com.absolute.am.webapi.listeners.SyncServiceSessionListener.PersistSyncSession";
	private static final String PARAMETER_NAME_DATA_REFRESHING_DELAY = "com.absolute.webapi.Application.delayLiveDataUpdates";
	private static final String PARAMETER_NAME_SSP_FORCE_SERVER_PROMPT = "com.absolute.am.webapi.ssp.controllers.Login.SspForceServerPrompt";
	private static final String PARAMETER_NAME_WALLPAPER_SIZE = "com.absolute.webapi.controllers.action.wallpaperImageLimitBytes";
	private static final String PARAMETER_NAME_USER_PREFERENCE_FILE_LIMIT = "com.absolute.webapi.controllers.userprefs.requestLengthLimit";
	
	private Document configurationXmlFileDocument = null;
	
	public AMWebApiSettings(String configurationFilePath) {
		amWebApiConfigurationFilePath = configurationFilePath;			
		configurationXmlFileDocument = getXmlDocument();
		parseDocument();
		
		String envFilePath = WizardHelper.getJarContainingFolder(AMWebApiSettings.class) + "\\" + WizardHelper.AM_WEB_UI_ENV_FILE_NAME;
		helpWebPageUri = WizardHelper.parseAMWebUiEnvironmentSettings(envFilePath, WizardHelper.ENV_SETTING_KEY_HELP_CONFIGURATIONAPP_ROOT);
	}
	
	public boolean saveSettings() {
		boolean saved = true;
		boolean dataChanged = false;
		//String rootNodeName = configurationXmlFileDocument.getDocumentElement().getNodeName();
		Node node = null;
		Element eElement = null;
		
		NodeList nList = configurationXmlFileDocument.getElementsByTagName("context-param");
		
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				eElement = (Element) node.getChildNodes();
				String name = eElement.getElementsByTagName("param-name").item(0).getTextContent();
				Node childNode = eElement.getElementsByTagName("param-value").item(0);
				
				switch (name) {
		         case PARAMETER_NAME_LIVE_DATA_UPDATES:
		        	 childNode.setTextContent(Boolean.toString(liveDataUpdatesEnabled));
		        	 dataChanged = true;
		             break;
		         case PARAMETER_NAME_SSP_LOGIN_ENABLED:
		        	 childNode.setTextContent(Boolean.toString(sspLoginEnabled));
		        	 dataChanged = true;
		             break;
		         case PARAMETER_NAME_SESSION_TIMEOUT:
		        	 childNode.setTextContent(Long.toString(sessionTimeoutInSecond));
	        		 dataChanged = true;
		        	 break;
		         case PARAMETER_NAME_SYNCH_SESSION_AFTER_DISCONNECT:
		        	 childNode.setTextContent(Boolean.toString(synchSessionAfrterDisconnectEnabled));
		        	 dataChanged = true;
		             break;
		         case PARAMETER_NAME_DATA_REFRESHING_DELAY:
		        	 childNode.setTextContent(Long.toString(dataRefreshingDelay));
	        		 dataChanged = true;
		        	 break;
		         case PARAMETER_NAME_SSP_FORCE_SERVER_PROMPT:
		        	 //childNode.setTextContent(Boolean.toString(sspForceServerPrompt));
		        	 //dataChanged = true;
		             break;		        	 
		         case PARAMETER_NAME_WALLPAPER_SIZE:
		        	 childNode.setTextContent(Long.toString(wallpaperFileSize));
	        		 dataChanged = true;
		        	 break;
		         case PARAMETER_NAME_USER_PREFERENCE_FILE_LIMIT:
		        	 //childNode.setTextContent(Long.toString(userPreferenceFileSize));
	        		 //dataChanged = true;
		        	 break;
		         default:
		             // do nothing
				}
 			}
		}
		
		if (dataChanged) {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(configurationXmlFileDocument);
				StreamResult result = new StreamResult(new File(amWebApiConfigurationFilePath));
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				saved = false;
			} catch (TransformerException e) {
				JOptionPane.showMessageDialog(null, "UAC: " + e.getMessage());
				saved = false;
			}
		}
		
		return saved;
	}
	
	private Document  getXmlDocument() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(amWebApiConfigurationFilePath);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private void parseDocument() {
		Node node = null;
		Element element = null;
		
		NodeList nList = configurationXmlFileDocument.getElementsByTagName("context-param");
		
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node.getChildNodes();
				String name = element.getElementsByTagName("param-name").item(0).getTextContent();
				String value = element.getElementsByTagName("param-value").item(0).getTextContent();
				
				switch (name) {
		         case PARAMETER_NAME_LIVE_DATA_UPDATES:
		        	 liveDataUpdatesEnabled = Boolean.parseBoolean(value);
		             break;
		         case PARAMETER_NAME_SSP_LOGIN_ENABLED:
		        	 sspLoginEnabled = Boolean.parseBoolean(value);
		             break;
		         case PARAMETER_NAME_SESSION_TIMEOUT:
		        	 sessionTimeoutInSecond = Long.parseLong(value);
		        	 break;
		         case PARAMETER_NAME_SYNCH_SESSION_AFTER_DISCONNECT:
		        	 synchSessionAfrterDisconnectEnabled = Boolean.parseBoolean(value);
		             break;
		         case PARAMETER_NAME_DATA_REFRESHING_DELAY:
		        	 dataRefreshingDelay = Long.parseLong(value);
		        	 break;
		         case PARAMETER_NAME_SSP_FORCE_SERVER_PROMPT:
		        	 sspForceServerPrompt = Boolean.parseBoolean(value);
		             break;		        	 
		         case PARAMETER_NAME_WALLPAPER_SIZE:
		        	 wallpaperFileSize = Long.parseLong(value);
		        	 break;
		         case PARAMETER_NAME_USER_PREFERENCE_FILE_LIMIT:
		        	 userPreferenceFileSize = Long.parseLong(value);
		        	 break;
		         default:
		             // do nothing
				}
 			}
		}
		
	}
}
