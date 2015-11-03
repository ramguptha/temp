package com.absolute.am.WebConfigurationUtil;

import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * update web.xml from old version to the new version
 */

public class WebConfigurationUpdater {
		
	private static final List<String> PARAMETER_NAME_LIST = Arrays.asList(
				"com.absolute.webapi.Application.enableLiveDataUpdates",
				"com.absolute.am.webapi.ssp.controllers.Login.Enabled",
				"com.absolute.am.webapi.controllers.Login.SessionTimeoutSeconds",
				"com.absolute.am.webapi.listeners.SyncServiceSessionListener.PersistSyncSession",
				"com.absolute.webapi.Application.delayLiveDataUpdates",
				"com.absolute.am.webapi.ssp.controllers.Login.SspForceServerPrompt",
				"com.absolute.am.webapi.ssp.controllers.Login.SspForceServerPrompt",
				"com.absolute.webapi.controllers.action.wallpaperImageLimitBytes"
			);

	/**
	 * Launch the application.
	 * java -jar WebConfigurationUtil.jar PrevWebXmlPath="C:\\Temp\\web.xml" NewWebXmlPath="C:\\Temp\\new\\web.xml" PrevWebXmlPath="C:\\Temp\\web.xml" 
	 * 		SettingsFilePath="C:\\ProgramData\\Absolute Software\\AmWebApiData\\settings\\settings.txt"
	 */

	private static final String PARA_NAME_PREV_WEB_XML_PATH = "PrevWebXmlPath";
	private static final String PARA_NAME_NEW_WEB_XML_PATH = "NewWebXmlPath";
	private static final String PARA_NAME_SETTIINGS_FILE_PATH = "SettingsFilePath";
	private static final String errorTitle = "Error";
	
	private static String prevWebXmlPath = "";
	private static String newWebXmlPath = "";
	private static String settingsFilePath = "";
	
	public static void main(final String[] args) {
		if (!parseArguments(args)) {
			System.exit(0);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// update settings.txt 
					WebSettingFileUpdater WebSettingFileUpdater = new WebSettingFileUpdater(settingsFilePath);
					WebSettingFileUpdater.updateSettingFile();
					
					// update web.xml
					Hashtable<String, String> changedDataList = populateChangedDataList();
					if (changedDataList.size() > 0 ) {
						updateWebConfiguration(changedDataList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static boolean parseArguments(String[] args) {
		prevWebXmlPath = "";
		newWebXmlPath = "";

		JLabel label;
		
		if (args.length != 3) {
			label = new JLabel(String.format("Incorrect number of command line parameters. Must specify exactly 2: %s, %s", 
					new Object[] {PARA_NAME_PREV_WEB_XML_PATH, PARA_NAME_NEW_WEB_XML_PATH, PARA_NAME_SETTIINGS_FILE_PATH}));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		for (String param : args) {
			String[] tokens = param.split("=");
			if (tokens.length != 2) {
				label = new JLabel(String.format("Unexpected command line parameter: %s", new Object[] {param}));
			    label.setFont(new Font("Dialog", Font.PLAIN, 16));
				JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
				return false;
			}

			if (tokens[0].equals(PARA_NAME_PREV_WEB_XML_PATH)) {
				prevWebXmlPath = tokens[1];
			} else if (tokens[0].equals(PARA_NAME_NEW_WEB_XML_PATH)) {
				newWebXmlPath = tokens[1];
			} else if (tokens[0].equals(PARA_NAME_SETTIINGS_FILE_PATH)) {
				settingsFilePath = tokens[1];
			}else {
				label = new JLabel(String.format("Unexpected command line parameter: %s", new Object[] {param}));
			    label.setFont(new Font("Dialog", Font.PLAIN, 16));
				JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		if (prevWebXmlPath == null || prevWebXmlPath.length() == 0) {
			label = new JLabel(String.format("Missing comand line parameter: %s", PARA_NAME_PREV_WEB_XML_PATH));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label,errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (newWebXmlPath == null || newWebXmlPath.length() == 0) {
			label = new JLabel(String.format("Missing comand line parameter: %s", PARA_NAME_NEW_WEB_XML_PATH));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (settingsFilePath == null || settingsFilePath.length() == 0) {
			label = new JLabel(String.format("Missing comand line parameter: %s", PARA_NAME_SETTIINGS_FILE_PATH));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (!(new File(prevWebXmlPath)).exists()) {
			label = new JLabel(String.format("Following file does not exist:\n %s", prevWebXmlPath));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (!(new File(newWebXmlPath)).exists()) {
			label = new JLabel(String.format("Following file does not exist:\n %s", newWebXmlPath));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (!(new File(settingsFilePath)).exists()) {
			label = new JLabel(String.format("Following file does not exist:\n %s", settingsFilePath));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label, errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return prevWebXmlPath.length() > 0 && newWebXmlPath.length() > 0;
	}
	
	private static Hashtable<String, String> populateChangedDataList() {
		Hashtable<String, String> changedDataList = new Hashtable<String, String>();
		Document prevWebConfiguraitonXmlDocument= getXmlDocument(prevWebXmlPath);
		Document newWebConfiguraitonXmlDocument= getXmlDocument(newWebXmlPath);
		
		Node oldNode = null, newNode = null;
		Element oldElement = null, newElement = null;
		
		NodeList prevWebConfiguraitonNodeList = prevWebConfiguraitonXmlDocument.getElementsByTagName("context-param");
		NodeList newWebConfiguraitonNodeList = newWebConfiguraitonXmlDocument.getElementsByTagName("context-param");
		
		for (int i = 0; i < prevWebConfiguraitonNodeList.getLength(); i++) {
			oldNode = prevWebConfiguraitonNodeList.item(i);
			if (oldNode.getNodeType() == Node.ELEMENT_NODE) {
				oldElement = (Element) oldNode.getChildNodes();
				String oldName = oldElement.getElementsByTagName("param-name").item(0).getTextContent();
				String oldValue = oldElement.getElementsByTagName("param-value").item(0).getTextContent();
				
				if (oldName != null && oldValue != null && PARAMETER_NAME_LIST.contains(oldName)) {
					for (int j = 0; j < newWebConfiguraitonNodeList.getLength(); j++) {
						newNode = newWebConfiguraitonNodeList.item(j);
						if (newNode.getNodeType() == Node.ELEMENT_NODE) {
							newElement = (Element) newNode.getChildNodes();
							String newName = newElement.getElementsByTagName("param-name").item(0).getTextContent();
							if (newName != null && oldName.compareTo(newName) == 0) {
								changedDataList.put(newName, oldValue);
								break;
							} 
						}
					}
				}
				
 			}
		}

		return changedDataList;
	}
	
	private static void updateWebConfiguration(Hashtable<String, String> changedDataList) {
		Document newWebConfiguraitonXmlDocument= getXmlDocument(newWebXmlPath);
		
		Node node = null;
		Element newElement = null;
		Enumeration<String> names;
		
		NodeList newWebConfiguraitonNodeList = newWebConfiguraitonXmlDocument.getElementsByTagName("context-param");
		
		if (changedDataList.size() > 0) {
			for (int j = 0; j < newWebConfiguraitonNodeList.getLength(); j++) {
				node = newWebConfiguraitonNodeList.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					newElement = (Element) node.getChildNodes();
					String newName = newElement.getElementsByTagName("param-name").item(0).getTextContent();
					names = changedDataList.keys();
					while(names.hasMoreElements()) {
				         String name = (String) names.nextElement();
				         if (newName.compareTo(name) == 0) {
				        	 Node childNode = newElement.getElementsByTagName("param-value").item(0);
				        	 childNode.setTextContent(changedDataList.get(name).toString());
				        	 break;
				         }
				      }
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(newWebConfiguraitonXmlDocument);
				StreamResult result = new StreamResult(new File(newWebXmlPath));
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), errorTitle, JOptionPane.ERROR_MESSAGE);
			} catch (TransformerException e) {
				JOptionPane.showMessageDialog(null, "UAC: " + e.getMessage());
			}
		}
	}
	
	private static Document  getXmlDocument(String webConfigurationFilePath) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(webConfigurationFilePath);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), errorTitle, JOptionPane.ERROR_MESSAGE);
		} 
		
		return doc;
	}
}
