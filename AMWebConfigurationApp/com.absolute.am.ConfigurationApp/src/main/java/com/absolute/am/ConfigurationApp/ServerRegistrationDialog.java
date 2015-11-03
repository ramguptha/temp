package com.absolute.am.ConfigurationApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolSettings;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CobraRegisterSyncSvcCertCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.util.FileUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class ServerRegistrationDialog extends JDialog {

	private String m_serverUniqueId;
	private String m_webApiCertFolder;
	private short m_syncSvcPortNumber;
	
	private byte[] m_AMserverCertificate;
	private byte[] m_syncServiceServerCertificate;
	private byte[] m_serverCertificateHash;
	private boolean m_restartTomeCatRequired = false;

	private String lang = WizardHelper.ABSOLUTE_DEFAULT_LANGUAGE;
	private static String errorTitle = "";
	private String messageText = "";
	
	private Window parentWindow = null, currentWindow = null;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtServerName;
	private JTextField txtPortNumber;
	private JTextField txtUsername;
	private JPasswordField  txtPassword;
	private JCheckBox chkSsp;
	private JTable tblServer;
	private WizardHelper wizardHelper = new WizardHelper();
	private boolean dataDirty = false;
	
	DocumentListener textFieldChandeListener = new DocumentListener() {
		  public void changedUpdate(DocumentEvent e) {
		    dataDirty = true;
		  }
		  public void removeUpdate(DocumentEvent e) {
			  dataDirty = true;
		  }
		  public void insertUpdate(DocumentEvent e) {
			  dataDirty = true;
		  }
	};
	ItemListener checkBoxItemListener =  new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
    	  dataDirty = true;
      }
    };
    
	public ServerRegistrationDialog(Window parent, JTable tblServer, String webApiCertFolder, short syncSvcPortNumber) {
		super.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/absolute-manage.jpg")));
		errorTitle = LocalizationUtil.getResourceStringForLocale("ERROR", lang);
		
		this.parentWindow = parent;
		this.tblServer = tblServer;
		this.m_webApiCertFolder = webApiCertFolder;
		m_syncSvcPortNumber = syncSvcPortNumber;
		
		setResizable(false);
		setTitle(LocalizationUtil.getResourceStringForLocale("REGISTER_AM_SERVER", lang));
		setBounds(100, 100, 520, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		contentPanel.add(WizardHelper.createPlainLabel(
				"<html>"+ LocalizationUtil.getResourceStringForLocale("DESCRIPTION_ENTER_LOGIN_DETAILS", lang) +"</html>",
				23, 23, 347, 14));
		contentPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("AM_SERVER_NAME", lang) + ":", 
				43, 66, 108, 14, SwingConstants.RIGHT));
		contentPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("PORT_NUMBER", lang) + ":",  
				43, 91, 108, 14, SwingConstants.RIGHT));
		contentPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("ADMIN_USERNAME", lang) + ":", 
				43, 116, 108, 14, SwingConstants.RIGHT));
		contentPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("PASSWORD_LABEL", lang) + ":", 
				43, 141, 108, 14, SwingConstants.RIGHT));
		
		chkSsp = new JCheckBox("Make this AM Server the default for SSP user logins");
		chkSsp.setBounds(160, 163, 315, 23);
		chkSsp.setFont(WizardHelper.ABSOLUTE_PLAINT_FONT);
		chkSsp.setSelected(!(getDefaultServerName(wizardHelper.settingsFilePath).length() > 0));
		chkSsp.addItemListener(checkBoxItemListener);
		contentPanel.add(chkSsp);
		
		txtServerName = new JTextField();
		txtServerName.setText("");
		txtServerName.setBounds(160, 63, 189, 20);
		contentPanel.add(txtServerName);
		txtServerName.setColumns(10);
		txtServerName.getDocument().addDocumentListener(textFieldChandeListener);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setBounds(161, 88, 97, 20);
		txtPortNumber.setColumns(10);
		txtPortNumber.setText(WizardHelper.PORT_NUMBER_DEFAULT);
		contentPanel.add(txtPortNumber);
		txtPortNumber.getDocument().addDocumentListener(textFieldChandeListener);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(160, 113, 189, 20);
		contentPanel.add(txtUsername);
		txtUsername.getDocument().addDocumentListener(textFieldChandeListener);
		
		txtPassword = new JPasswordField ();
		txtPassword.setColumns(10);
		txtPassword.setBounds(160, 138, 189, 20);
		txtPassword.getDocument().addDocumentListener(textFieldChandeListener);
		contentPanel.add(txtPassword);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnSaveServerRegistration = new JButton(LocalizationUtil.getResourceStringForLocale("SAVE", lang));
				btnSaveServerRegistration.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						currentWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						// trim the space for the server name textbox
						txtServerName.setText(txtServerName.getText().trim());
						if (validateForm()) {
							Window win = SwingUtilities.getWindowAncestor(txtServerName);
							
							final JDialog processDialog = new JDialog(win, "Dialog", ModalityType.APPLICATION_MODAL);
						    final JProgressBar progressBar = new JProgressBar(0, 100);
						    
							SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
								
							    @Override
							    protected Void doInBackground() throws Exception {
							    	
									if (register(txtServerName.getText(), Short.parseShort(txtPortNumber.getText()), 
											txtUsername.getText(), String.valueOf(txtPassword.getPassword()), progressBar)) {
										
										m_restartTomeCatRequired = isDefaultServerChanged();
										String[][] registeredServers = populateRegisteredServers();
										progressBar.setValue(m_restartTomeCatRequired? 86: 90);
										progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_5", lang));
										if (!saveServerRegistrationToLocal(registeredServers)) {
											messageText = LocalizationUtil.getResourceStringForLocale("FAILS_TO_SAVE_REGISTRATION_INFO", lang, 
													wizardHelper.settingsFilePath);
											JOptionPane.showMessageDialog(
													currentWindow, 
										    		messageText,
										    		errorTitle, 
										    		JOptionPane.ERROR_MESSAGE);
										} else {
											// restart tomcat
											if (m_restartTomeCatRequired) {
												try {
													progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_6", lang));
													String tomcatExecutableFilePath = WizardHelper.getApacheTomcatInstallPath() + "\\" + WizardHelper.SUBFOLDER_NAME_BIN
															+ "\\" + wizardHelper.tomcatExecutableFile;									
													WizardHelper.restartTomcat(tomcatExecutableFilePath);
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
											processDialog.dispose();
											
											String tempStr = "<html><p>" +
													"<b>" + LocalizationUtil.getResourceStringForLocale("REGISTRATION_SUCCEED_MESSAGE_1", lang, new Object[] {txtServerName.getText()}) + ".</b>" +
													"</p><p>" +
													LocalizationUtil.getResourceStringForLocale("REGISTRATION_SUCCEED_MESSAGE_2", lang) +
													"<br />" +
													m_serverUniqueId +
													"</p></html>";
													
											JLabel label = new JLabel(tempStr);
										    label.setFont(new Font("Dialog", Font.PLAIN, 16));
										    JOptionPane.showMessageDialog(
										    		currentWindow, 
										    		label,
										    		LocalizationUtil.getResourceStringForLocale("REGISTRATION_COMPLETE", lang), 
										    		JOptionPane.INFORMATION_MESSAGE);
										    
										    // close the dialog window
										    close();
										}
									}
							
									return null;
						         }
						      };
						      mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

						         @Override
						         public void propertyChange(PropertyChangeEvent evt) {
						            if (evt.getPropertyName().equals("state")) {
						               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
						            	   processDialog.dispose();
						               }
						            }
						         }
						      });
						      mySwingWorker.execute();
						      
						      Color prpcessPanelBackGorundColor = Color.LIGHT_GRAY;
						      progressBar.setString(" ");
						      //progressBar.setIndeterminate(true);
						      progressBar.setForeground(new Color(41,85,150));	//dark blue
						      progressBar.setStringPainted(true);
						      progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						      JPanel processPanel = new JPanel(new BorderLayout());
						      processPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						      processPanel.setBackground(prpcessPanelBackGorundColor);
						      processPanel.add(progressBar, BorderLayout.SOUTH);
						      JLabel lblProcessing = new JLabel(LocalizationUtil.getResourceStringForLocale("REGISTRATION_PROCESSING_MESSAGE", lang));
						      lblProcessing.setHorizontalAlignment(JLabel.CENTER);
						      lblProcessing.setForeground(prpcessPanelBackGorundColor);
						      lblProcessing.setForeground(Color.BLACK);
						      processPanel.add(lblProcessing, BorderLayout.CENTER);
						      processDialog.setUndecorated(true);
						      processDialog.getContentPane().setBackground(prpcessPanelBackGorundColor);
						      processDialog.setPreferredSize(new Dimension(450,100));
						      processDialog.add(processPanel);
						      processDialog.pack();
						      processDialog.setLocationRelativeTo(win);
						      processDialog.setVisible(true);
							
						} 
						// reset the mouse cursor back to default
						currentWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				});
				
				btnSaveServerRegistration.setActionCommand("OK");
				buttonPane.add(btnSaveServerRegistration);
				getRootPane().setDefaultButton(btnSaveServerRegistration);
			}
			{
				JButton btnCancelServerRegistration = new JButton(LocalizationUtil.getResourceStringForLocale("CANCEL", lang));
				btnCancelServerRegistration.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (dataDirty) {
							if (JOptionPane.showConfirmDialog(
									currentWindow, 
									LocalizationUtil.getResourceStringForLocale("MESSAGE_SAVE_SERVER_REGISTRATION", lang), 
									LocalizationUtil.getResourceStringForLocale("REGISTER_AM_SERVER", lang), 
									JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								close();
							}
						} else if (parentWindow == null){
							 String txtNoServerRegisteredMessage = "<html>" +
										LocalizationUtil.getResourceStringForLocale("NO_SERVER_REGISTERED_MESSAGE_1", lang) +
										"<br />" +
										LocalizationUtil.getResourceStringForLocale("NO_SERVER_REGISTERED_MESSAGE_2", lang) +
										"</html>";
			            		
								JLabel label2 = new JLabel(txtNoServerRegisteredMessage);
							    label2.setFont(new Font("Dialog", Font.PLAIN, 16));
							String txtNoServerRegisteredTitle = LocalizationUtil.getResourceStringForLocale("QUIT_WITHOUT_REGISTERING", lang);
							if (JOptionPane.showConfirmDialog(
									currentWindow,
								    label2,
								    txtNoServerRegisteredTitle,
								    JOptionPane.YES_NO_OPTION) == 0) {
								close();
							}
						} else {
							close();
						}
					}
				});
				btnCancelServerRegistration.setActionCommand("Cancel");
				buttonPane.add(btnCancelServerRegistration);
			}
		}

		currentWindow = SwingUtilities.windowForComponent(contentPanel);
		show(parentWindow);
	}
	
	private void show(Window parentWindow) {
		if (parentWindow != null) {
			int x = parentWindow.getX(); int y = parentWindow.getY();
			setModal(true);
			setLocationRelativeTo(parentWindow);
			setLocation(x + 25, y + 40);
		} else {
			setLocationRelativeTo(null);	// center of the screen
		}
		
		setVisible(true);
	}
	
	private void close() {
		if (parentWindow != null) {
			setVisible(false);
		} else {
			System.exit(0);
		}
	}
	
	private boolean register(String serverValue, short portValue, String usernameValue, String passwordValue, JProgressBar progressBar) {
		String tempStr = "";
		String lang = WizardHelper.ABSOLUTE_DEFAULT_LANGUAGE;
		try {
			progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_1", lang));
			progressBar.setValue(20);
			if (!getServerCertificateAndUniqueId(serverValue, portValue, lang)) {
				return false;
			}
			progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_2", lang));
			progressBar.setValue(40);
			if (!getSyncServiceCertificate(m_syncSvcPortNumber, lang)) {
				return false;
			}
			progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_3", lang));
			progressBar.setValue(60);
			if (!registerSyncServiceCertificatWithAMServer(serverValue, portValue, usernameValue, passwordValue, lang)) {
				return false;
			}
			progressBar.setString(LocalizationUtil.getResourceStringForLocale("PROGRESS_BAR_MESSAGE_4", lang));
			progressBar.setValue(80);
			if (!saveAMServerCertificateInWebAPIFolder(lang)) {
				return false;
			}
			
	        return true;
	        
		} catch (LocalizedException le) {
			tempStr = LocalizationUtil.getResourceStringForLocale("FAILS_TO_REGISTER_CERTIFICATE", 
					lang, le.getMsg(), le.getDescr());
			JOptionPane.showMessageDialog(currentWindow, tempStr, 
					LocalizationUtil.getResourceStringForLocale("REGISTER_CERTIFICATE", lang),
					JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException fe) {
			tempStr = "<html><p>" +
					LocalizationUtil.getResourceStringForLocale("ENTER_VALID_PORT_NUMBER", lang) +
					"</p></html>";
			JOptionPane.showMessageDialog(currentWindow, tempStr, 
					LocalizationUtil.getResourceStringForLocale("REGISTER_CERTIFICATE", lang),
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			tempStr = "<html><p>" + 
					LocalizationUtil.getResourceStringForLocale("UNEXPECTED_ERROR_OCCURRED", lang) +
					"<br />" +
					LocalizationUtil.getResourceStringForLocale("ERROR_MESSAGE", lang) +
					"</p><p>" +
					e.getMessage() +
					"</p></html>";
			JOptionPane.showMessageDialog(currentWindow, tempStr, 
					LocalizationUtil.getResourceStringForLocale("REGISTER_CERTIFICATE", lang),
					JOptionPane.ERROR_MESSAGE);
		} 
		
		return false;
	}
	
	private String[][] populateRegisteredServers() {
		File serverConfigFile = new File(wizardHelper.settingsFilePath);
		if (!serverConfigFile.exists()) {
			try {
				serverConfigFile.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		
		// check if the server has been registered already
		boolean registeredAlready = false;
		String[][] registeredServers = readServerRegistration(wizardHelper.settingsFilePath);
		
		for (int i = 0; i < registeredServers.length; i++) {
			if (chkSsp.isSelected()) {
				registeredServers[i][2] = "No";
			}
			if (registeredServers[i][0].compareToIgnoreCase(txtServerName.getText()) == 0) {
				registeredAlready = true;
				registeredServers[i][0] = txtServerName.getText();
				registeredServers[i][1] = txtPortNumber.getText();
				registeredServers[i][2] = chkSsp.isSelected()? "Yes" : "No";
				registeredServers[i][3] = txtUsername.getText();
				registeredServers[i][4] = getCurrentDateTime();
			} 
		}
		// append the new one to the array of not exists
		if (!registeredAlready) {
			String[][] tempServers = new String[registeredServers.length + 1][];
			 if (registeredServers != null) {
			     System.arraycopy(registeredServers, 0, tempServers, 0, Math.min(registeredServers.length, tempServers.length));
			 }
			 tempServers[registeredServers.length] = new String[5];
			 tempServers[registeredServers.length][0] = txtServerName.getText();
			 tempServers[registeredServers.length][1] = txtPortNumber.getText();
			 tempServers[registeredServers.length][2] = chkSsp.isSelected()? "Yes" : "No";
			 tempServers[registeredServers.length][3] = txtUsername.getText();
			 tempServers[registeredServers.length][4] = getCurrentDateTime();
			 
			 registeredServers = tempServers; 
		}
		
		return registeredServers;
	}
	
	
	private boolean saveServerRegistrationToLocal(String[][] servers) {
		File serverConfigFile = new File(wizardHelper.settingsFilePath);
		
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(serverConfigFile, false)));
		    for (int i = 0; i < servers.length; i++) {
		    	out.println(servers[i][0] + ","  + servers[i][1] + "," + servers[i][2] + "," + servers[i][3] + "," + servers[i][4]);
		    }
		    
		    out.flush();
		    out.close();
		    
			if (parentWindow != null) {
				tblServer.setModel(new DefaultTableModel(
						servers,
						new String[] {
								LocalizationUtil.getResourceStringForLocale("AM_SERVER_NAME", lang), 
								LocalizationUtil.getResourceStringForLocale("PORT_NUMBER", lang),
								LocalizationUtil.getResourceStringForLocale("SSP_DEFAULT", lang),
						}
					));
			}
		    
		    return true;
		  }catch (IOException e){
		      e.printStackTrace();
		  }
		
		return false;
	}
	
	public static String[][] readServerRegistration(String amServerSettingsFilePath) {
		File serverConfigFile = new File(amServerSettingsFilePath);
		WizardHelper.changeUacPermission(amServerSettingsFilePath);
		
		if (!serverConfigFile.exists()){
			try {
				serverConfigFile.getParentFile().mkdirs();
				serverConfigFile.createNewFile();
			} catch (Exception e) {
				// do nothing
			}
		}
		
		BufferedReader CSVFile = null;
		try {
			CSVFile = new BufferedReader(new FileReader(amServerSettingsFilePath));
			LinkedList<String[]> rows = new LinkedList<String[]>();
			String dataRow = CSVFile.readLine();

			while (dataRow != null){
			    rows.addLast(dataRow.split(","));
			    dataRow = CSVFile.readLine();
			}

			String[][] serverArray = rows.toArray(new String[rows.size()][]);
			
			return serverArray;
		} catch (Exception e) {
			// do nothing
		} finally {
			if (CSVFile != null) {
				try {
					CSVFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private boolean validateForm() {
		boolean valid = true;
		
		String serverValue = txtServerName.getText();
		String portValue = txtPortNumber.getText();
		String usernameValue = txtUsername.getText();
		String passwordValue = new String(txtPassword.getPassword());
				
		if (valid && (serverValue == null || serverValue.length() == 0)) {
			JOptionPane.showMessageDialog(currentWindow, 
						LocalizationUtil.getResourceStringForLocale("ENTER_SERVER_NAME", lang),
						errorTitle, JOptionPane.ERROR_MESSAGE);
			txtServerName.requestFocusInWindow();
			valid = false;
		}
		if (valid && (usernameValue == null || usernameValue.length() == 0)) {
			JOptionPane.showMessageDialog(currentWindow, 
					LocalizationUtil.getResourceStringForLocale("ENTER_USER_NAME", lang),
					errorTitle, JOptionPane.ERROR_MESSAGE);
			txtUsername.requestFocusInWindow();
			valid = false;
		}
		if (valid && (passwordValue == null || passwordValue.trim().length() == 0)) {
			JOptionPane.showMessageDialog(currentWindow, 
					LocalizationUtil.getResourceStringForLocale("ENTER_PASSWORD", lang),
					errorTitle, JOptionPane.ERROR_MESSAGE);
			txtPassword.requestFocusInWindow();
			valid = false;
		}
		if (valid && !WizardHelper.validateNumberString(portValue, 0, 9999)) {
			JOptionPane.showMessageDialog(
					currentWindow, 
					LocalizationUtil.getResourceStringForLocale("ENTER_VALID_PORT_NUMBER", lang),
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
			valid = false;;
		} 
		
		return valid;
	}

	private boolean getServerCertificateAndUniqueId(String serverName, Short serverPort, String lang) throws LocalizedException {
		AMServerProtocol amServerProtocol = null;
		String exceptionMessage = LocalizationUtil.getResourceStringForLocale("UNKNOWN", lang);
		
		try {
			// Start clean.
			m_AMserverCertificate = null;
			m_serverUniqueId = null;
			m_serverCertificateHash = null;
			
			CobraAdminMiscDatabaseCommand fakeLoginCommand = CommandFactory.createLoginCommand(
					"FakeLogin", "FakePassword");

			AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings(
					serverName, serverPort, "");
			amServerProtocol = new AMServerProtocol(protocolSettings);

			amServerProtocol.sendCommandAndGetResponse(fakeLoginCommand);
			m_AMserverCertificate = amServerProtocol.getCertificateProvidedByPeer().getEncoded();
			m_serverUniqueId = amServerProtocol.getPeerServerUniqueId();
			ByteArrayInputStream bis = new ByteArrayInputStream(m_AMserverCertificate);
			m_serverCertificateHash = FileUtilities.hashFile(bis, m_AMserverCertificate.length, "SHA-1");
						
		} catch (AMWebAPILocalizableException e) {
			LocalizedException ex = new LocalizedException(e, lang);
			exceptionMessage = ex.getDescr();
		} catch (Exception e) {
			exceptionMessage = e.getMessage();		
		} finally {
			if (amServerProtocol != null) {
				try {
					amServerProtocol.close();
				} catch (IOException e) {
				}
				amServerProtocol = null;
			}
		}
		
		if (m_AMserverCertificate == null || m_serverUniqueId == null || m_serverCertificateHash == null) {
			String tempStr = "<html><p>" + LocalizationUtil.getResourceStringForLocale("COULD_NOT_RETRIEVE_SERVER_CERTIFICATE", lang) +
					"<br />" + 
					exceptionMessage +
					"</p><p>" +
					LocalizationUtil.getResourceStringForLocale("CHECK_THAT_SERVER_NAME_AND_PORT_CORRECT", lang, new Object[] {serverName, serverPort}) +
					"</p></html>";
			JOptionPane.showMessageDialog(
					currentWindow, 
					tempStr,
					LocalizationUtil.getResourceStringForLocale("RETRIEVE_SERVER_CERTIFICATE", lang), 
					JOptionPane.ERROR_MESSAGE);

			return false;
		}
		return true;
	}

	private boolean getSyncServiceCertificate(short port, String lang) throws LocalizedException {

		String exceptionMessage = LocalizationUtil.getResourceStringForLocale("UNKNOWN", lang);
		AMServerProtocolSettings amServerProtocolSettingsLocalhost = new AMServerProtocolSettings("localhost", port, null);
		AMServerProtocol amServerProtocolLocalhost = new AMServerProtocol(amServerProtocolSettingsLocalhost);
		
		try {
			// Start clean.
			m_syncServiceServerCertificate = null;
			
			// Send a bogus command to the Sync Service, in order to retrieve it's SSL certificate.
			CobraAdminMiscDatabaseCommand fakeLoginCommand = CommandFactory.createLoginCommand(
					WizardHelper.COMMAND_FACTORY_LOGIN_USERNMAE, 
					WizardHelper.COMMAND_FACTORY_LOGIN_PASSWORD);
			
			amServerProtocolLocalhost.sendCommandAndGetResponse(fakeLoginCommand);
			m_syncServiceServerCertificate = amServerProtocolLocalhost.getCertificateProvidedByPeer().getEncoded();
		} catch (AMWebAPILocalizableException e) {
			LocalizedException ex = new LocalizedException(e, lang);
			exceptionMessage = ex.getDescr();
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
		} finally {
			if (amServerProtocolLocalhost != null) {
				try {
					amServerProtocolLocalhost.close();				
				} catch (Exception e) {
					// cleaning up, not much more we can do.
				}
				amServerProtocolLocalhost = null;
			}
		}
		
		if (m_syncServiceServerCertificate == null) {
			String tempStr = "<html><p>" + LocalizationUtil.getResourceStringForLocale("COULD_NOT_RETRIEVE_SYNC_SERVICE_CERTIFICATE", lang) +
					"<br />" + 
					exceptionMessage +
					"</p><p>" +
					LocalizationUtil.getResourceStringForLocale("CHECK_THAT_SYNC_SERVICE_RUNNING", lang) +
					"</p></html>";
			
			JOptionPane.showMessageDialog(
					currentWindow, 
					tempStr,
					"Retrieve Synch Service Certificate", 
					JOptionPane.ERROR_MESSAGE);
					
			return false;
		}
		return true;

	}
	
	private boolean registerSyncServiceCertificatWithAMServer(String serverName, short serverPort, String userName, String password, String lang)
			throws LocalizedException {
		String exceptionMessage = LocalizationUtil.getResourceStringForLocale("UNKNOWN", lang);
		long protocolResult = -1;
		String serverError = "";
		
		AMServerProtocol amServerProtocol = null;
		try {
			
			AMServerProtocolSettings protocolSettings = new AMServerProtocolSettings(
					serverName, serverPort, "");
			amServerProtocol = new AMServerProtocol(protocolSettings);
			
			CobraRegisterSyncSvcCertCommand command = CommandFactory.createRegisterSyncSvcCertCommand(
					userName, password, 
					m_syncServiceServerCertificate);

			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "");
			protocolResult  = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			if (protocolResult != 0) {
				String errorDesc = PropertyList.getElementAsString(finalResult, CobraProtocol.kCobra_XML_CommandResultErroString);
				serverError = LocalizationUtil.getResourceStringForLocale("SERVER_RETURNED_ERROR", lang, new Object[] {serverName, errorDesc, protocolResult});
			}
						
		} catch (AMWebAPILocalizableException e) {
			LocalizedException ex = new LocalizedException(e, lang);
			exceptionMessage = ex.getDescr();
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
		} finally {
			if (amServerProtocol != null) {
				try {
					amServerProtocol.close();
				} catch (IOException ioe) {
					// ignore
				}
				amServerProtocol = null;
			}
		}

		if (!serverError.isEmpty()) {
			exceptionMessage = serverError + "<br />" + exceptionMessage;
		}
		
		if (protocolResult != 0) {
			String tempStr = LocalizationUtil.getResourceStringForLocale("FAILS_TO_REGISTER_WITH_AM_SERVER",
					lang, exceptionMessage);
			
			JOptionPane.showMessageDialog(
					currentWindow, 
					tempStr,
					errorTitle,
					JOptionPane.ERROR_MESSAGE);
					
			return false;
		}
		return true;
	}

	private boolean saveAMServerCertificateInWebAPIFolder(String lang) {
		String exceptionMessage = LocalizationUtil.getResourceStringForLocale("UNKNOWN", lang),
				pathToSaveCertFile = m_webApiCertFolder + "\\" + m_serverUniqueId + ".pem";
		byte[] oldCertFileHash = null;
		boolean writeNewCertFile = true;
		
		// only write the certificate file if it doesn't already exist and if its hash is different
		if (new File(pathToSaveCertFile).exists()) {
			try {
				byte[] oldCertFileBytes;
				PemReader reader = new PemReader(new FileReader(pathToSaveCertFile));
				oldCertFileBytes = reader.readPemObject().getContent();
				ByteArrayInputStream bis = new ByteArrayInputStream(oldCertFileBytes);
				oldCertFileHash = FileUtilities.hashFile(bis, oldCertFileBytes.length, "SHA-1");
				
				reader.close();
			} catch (IOException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
						
			if( Arrays.equals(m_serverCertificateHash, oldCertFileHash) ) {
				writeNewCertFile = false;
			}
		}
		
		if( writeNewCertFile ) {
			try {
				// save the AM server cert to m_webApiCertFolder 
	            PemWriter writer = new PemWriter(new FileWriter(pathToSaveCertFile));
	            PemObject pemObject = new PemObject("CERTIFICATE", m_AMserverCertificate);
	            writer.writeObject(pemObject);
	            writer.flush();
	            writer.close();
			} catch (IOException e) {
				exceptionMessage = e.getMessage();
				String tempStr = "<html><p>" + LocalizationUtil.getResourceStringForLocale("COULD_NOT_SAVE_CERTIFICATE_IN_WEBAPI_FOLDER", lang) +
						"<br />" +
						exceptionMessage + 
						"</p></html>";
				JOptionPane.showMessageDialog(
						currentWindow, 
						tempStr,
						errorTitle, 
						JOptionPane.ERROR_MESSAGE);

				return false;
			} 
		}
		
		return true;		
	}

	private boolean isDefaultServerChanged() {
		boolean changed = false;
		String defaultServerName = getDefaultServerName(wizardHelper.settingsFilePath);
		String newDefaultServerName = chkSsp.isSelected()? txtServerName.getText() : "";
		
		// calculate if the Tomcat need to be restarted
		if ((defaultServerName != null && defaultServerName.length() > 0) && (newDefaultServerName != null && newDefaultServerName.length() > 0)) {
			changed = (defaultServerName.compareToIgnoreCase(newDefaultServerName) != 0);
		} else if (defaultServerName.isEmpty() && (newDefaultServerName != null && newDefaultServerName.length() > 0)) {
			changed = true;
		}  else if (txtServerName.getText().compareToIgnoreCase(defaultServerName) == 0 && !chkSsp.isSelected()) {
			// remove the default server
			changed = true;			
		}
		
		return changed;
	}
	
	// get the default server name which has been set up in the 'Setting.txt' file
	private String getDefaultServerName(String amServerSettingsFilePath) {
		String serverName = "";
		String[][] registeredServers = readServerRegistration(wizardHelper.settingsFilePath);
		
		for (int i = 0; i < registeredServers.length; i++) {
			if ("Yes".compareToIgnoreCase(registeredServers[i][2]) == 0) {
				serverName = registeredServers[i][0];
			}	
		}
		
		return serverName;
	}
	
	private String getCurrentDateTime() {
		Date date = new Date();
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat(WizardHelper.DATE_TIME_FORMAT_LONG);
        sdf.setCalendar(cal);
        cal.setTime(date);
        
        return sdf.format(date);
	}
}

