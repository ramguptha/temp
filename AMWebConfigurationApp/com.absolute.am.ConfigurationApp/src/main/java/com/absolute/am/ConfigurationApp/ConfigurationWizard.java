package com.absolute.am.ConfigurationApp;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ConfigurationWizard {
	
	private static String webApiCertificateFolder = "";
	private static short syncServicePortNumber = 0;
	private static String caller = "";
	
	private JFrame frmAbsoluteWeb;
	private JPanel serverPanel;
	private JPanel settingsPanel;
	
	private JTable tblServer;
	private JTextField txtSessionTimeout;
	private JTextField txtDataRefreshDelay;
	private JTextField txtWallpaperFileSize;
	private JCheckBox chkLiveDataUpdate;
	private JCheckBox chkSspLogin;
	//private JCheckBox chkStoreTheDefault;
	private JCheckBox chkSyncSessionAfterDisconnect;
	JLabel lblDataRefreshing;
	JLabel lblDataRefreshingMilliseconds;
	
	ServerRegistrationDialog serverRegistrationDialog;
	private JButton btnAddServer;
	private JButton btnSaveSettings; 
	private JButton btnRestoreDefaultValues;
	
	private String localAMWebAPiConfigurationFilePath = WizardHelper.getJarContainingFolder(ConfigurationWizard.class) + "\\web.xml";
	private AMWebApiSettings webApiSettings;
	private WizardHelper wizardHelper = new WizardHelper();
	
	private static String lang = WizardHelper.ABSOLUTE_DEFAULT_LANGUAGE;
	private static String errorTitle = "";
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
	    
	/**
	 * Launch the application.
	 */

	public static void main(final String[] args) {
		if (!parseArguments(args, lang)) {
			System.exit(0);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					ConfigurationWizard window = new ConfigurationWizard();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ConfigurationWizard() {
		errorTitle = LocalizationUtil.getResourceStringForLocale("ERROR", lang);
		
		// check the Tomcat is installed on this computer; if not, exit directly
		String tomcatExecutableFilePath = WizardHelper.getApacheTomcatInstallPath() + "\\" + WizardHelper.SUBFOLDER_NAME_BIN
				+ "\\" + wizardHelper.tomcatExecutableFile;
		if (!(new File(tomcatExecutableFilePath)).exists()) {
			JOptionPane.showMessageDialog(
					null, 
					LocalizationUtil.getResourceStringForLocale("TOMCAT_IS_MISSING", lang, tomcatExecutableFilePath), 
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} 
		
		if (caller.compareToIgnoreCase("Installation") == 0) {
			serverRegistrationDialog = new ServerRegistrationDialog(null, null, webApiCertificateFolder, syncServicePortNumber);
			serverRegistrationDialog.setVisible(true);
			return;
		} else {
			initialize();
			
			String amWebApiConfigurationFilePath = getAMWebApiConfigurationFilePath();
			if (amWebApiConfigurationFilePath != null && amWebApiConfigurationFilePath.length() > 0) {
				webApiSettings = new AMWebApiSettings(amWebApiConfigurationFilePath);
				initializeWebAdminSetting(webApiSettings);
				copyAMWebApiWebConfigirationToLocal();
				frmAbsoluteWeb.setVisible(true);
				resetWebAdminSettings();
			
				// set dataDirty to false
				dataDirty = false;
				
				// Show the error message if the help page uri not set
				if (webApiSettings.helpWebPageUri == null) {
					JOptionPane.showMessageDialog(
							SwingUtilities.windowForComponent(btnSaveSettings), 
							LocalizationUtil.getResourceStringForLocale("FAILS_TO_RETIREVE_HELP_PAGE_URL", lang,
									WizardHelper.getJarContainingFolder(AMWebApiSettings.class) 
									+ "\\" + WizardHelper.AM_WEB_UI_ENV_FILE_NAME),
							errorTitle,
							JOptionPane.ERROR_MESSAGE
							);
				} else {
					// Add help page links which is used to open help page
					serverPanel.add(WizardHelper.createSwingHyperLink(webApiSettings.helpWebPageUri));
					settingsPanel.add(WizardHelper.createSwingHyperLink(webApiSettings.helpWebPageUri));
				}
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAbsoluteWeb = new JFrame();
		frmAbsoluteWeb.setResizable(false);
		frmAbsoluteWeb.setTitle(LocalizationUtil.getResourceStringForLocale("WEB_ADMIN_CONFIGURATION_TOOL", lang));
		frmAbsoluteWeb.setBounds(100, 100, 598, 424);
		frmAbsoluteWeb.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/absolute-manage.jpg")));

		frmAbsoluteWeb.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmAbsoluteWeb.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
            
            @Override
            public void windowClosed(WindowEvent e) {}
        });

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmAbsoluteWeb.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		serverPanel = new JPanel();
		tabbedPane.addTab(LocalizationUtil.getResourceStringForLocale("AM_SERVERS", lang), null, serverPanel, null);
		serverPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(LocalizationUtil.getResourceStringForLocale("REGISTER_AM_SERVER", lang));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(65, 11, 387, 34);
		serverPanel.add(lblNewLabel);
		
		JLabel lblyouCanRegister = new JLabel("<html>" + LocalizationUtil.getResourceStringForLocale("DESCRIPTION_WENADMIN_SERVER_TAB", lang) + "\r\n<hr />\r\n</html>");
		lblyouCanRegister.setHorizontalAlignment(SwingConstants.LEFT);
		lblyouCanRegister.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblyouCanRegister.setBounds(39, 38, 513, 58);
		serverPanel.add(lblyouCanRegister);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(39, 135, 509, 135);
		serverPanel.add(scrollPane);
		
		tblServer = new JTable();
		tblServer.setEnabled(false);
		String[][] serverRegistrationData = ServerRegistrationDialog.readServerRegistration(
					wizardHelper.settingsFilePath);
		
		tblServer.setModel(new DefaultTableModel(
			serverRegistrationData,
			new String[] {
					LocalizationUtil.getResourceStringForLocale("AM_SERVER_NAME", lang), 
					LocalizationUtil.getResourceStringForLocale("PORT_NUMBER", lang),
					LocalizationUtil.getResourceStringForLocale("SSP_DEFAULT", lang),
			}
		));
		tblServer.getColumnModel().getColumn(0).setPreferredWidth(280);
		tblServer.getColumnModel().getColumn(1).setPreferredWidth(202);
		tblServer.getColumnModel().getColumn(2).setPreferredWidth(182);
		scrollPane.setViewportView(tblServer);
		
		btnAddServer = new JButton(LocalizationUtil.getResourceStringForLocale("REGISTER_AM_SERVER", lang));
		btnAddServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Window parentWindow = SwingUtilities.windowForComponent(btnAddServer);
				serverRegistrationDialog = new ServerRegistrationDialog(parentWindow, tblServer, webApiCertificateFolder, syncServicePortNumber);
			}
		});
		btnAddServer.setBounds(39, 102, 145, 23);
		serverPanel.add(btnAddServer);
		
		JButton btnClose = new JButton(LocalizationUtil.getResourceStringForLocale("CLOSE", lang));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		btnClose.setBounds(433, 312, 115, 23);
		serverPanel.add(btnClose);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setPreferredSize(new Dimension(0, 0));
		
		settingsPanel = new JPanel();
		tabbedPane.addTab(LocalizationUtil.getResourceStringForLocale("WEB_ADMIN_SETTINGS", lang), null, settingsPanel, null);
		settingsPanel.setLayout(null);
		
		JLabel label_1 = new JLabel(LocalizationUtil.getResourceStringForLocale("CONFIGURATION_WEB_ADMIN_SETTINGS", lang));
		label_1.setBounds(167, 11, 240, 17);
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		settingsPanel.add(label_1);
		
		chkLiveDataUpdate = new JCheckBox(LocalizationUtil.getResourceStringForLocale("ENABLE_LIVE_DATA_UPDATES", lang));
		chkLiveDataUpdate.addItemListener(checkBoxItemListener);
		chkLiveDataUpdate.setBounds(39, 105, 171, 23);
		chkLiveDataUpdate.setSelected(false);
		settingsPanel.add(chkLiveDataUpdate);
		chkLiveDataUpdate.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent actionEvent) {
		    	resetWebAdminSettings();
		    }
		});
		
		JPanel pnlLiveDataUpdate = new JPanel();
		pnlLiveDataUpdate.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlLiveDataUpdate.setBounds(31, 116, 330, 44);
		settingsPanel.add(pnlLiveDataUpdate);
		pnlLiveDataUpdate.setLayout(null);
		
		
		lblDataRefreshing = WizardHelper.createPlainLabel(
				LocalizationUtil.getResourceStringForLocale("LIMIT_REFRESH_RATE", lang), 
				30, 16, 147, 14);
		pnlLiveDataUpdate.add(lblDataRefreshing);
		txtDataRefreshDelay = WizardHelper.createNumberTextFile(146, 14, 70, 20);
		txtDataRefreshDelay.getDocument().addDocumentListener(textFieldChandeListener);
		pnlLiveDataUpdate.add(txtDataRefreshDelay);
		lblDataRefreshingMilliseconds = WizardHelper.createPlainLabel(
				LocalizationUtil.getResourceStringForLocale("MILLISECONDS", lang), 
				222, 16, 75, 14);
		pnlLiveDataUpdate.add(lblDataRefreshingMilliseconds);

		chkSspLogin = new JCheckBox(LocalizationUtil.getResourceStringForLocale("ENABLE_SSP_LOGIN", lang));
		chkSspLogin.addItemListener(checkBoxItemListener);
		chkSspLogin.setBounds(32, 167, 132, 17);
		chkSspLogin.setSelected(false);
		settingsPanel.add(chkSspLogin);
		chkSspLogin.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent actionEvent) {
		    	resetWebAdminSettings();
		    }
		});
		
		chkSyncSessionAfterDisconnect = new JCheckBox("<html>" + LocalizationUtil.getResourceStringForLocale("CONTINUE_SYNCHRONIZING_LOCAL_DATA", lang) + "</html>");
		chkSyncSessionAfterDisconnect.addItemListener(checkBoxItemListener);
		chkSyncSessionAfterDisconnect.setBounds(32, 186, 550, 43);
		chkSyncSessionAfterDisconnect.setVerticalTextPosition(SwingConstants.TOP);
		chkSyncSessionAfterDisconnect.setSelected(true);
		settingsPanel.add(chkSyncSessionAfterDisconnect);

		btnSaveSettings = new JButton(LocalizationUtil.getResourceStringForLocale("SAVE_SETTINGS", lang));
		btnSaveSettings.setBounds(333, 314, 113, 23);
		settingsPanel.add(btnSaveSettings);
		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveWebAdminSettings();
			}
		});
		
		settingsPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("SESSION_TIMEOUT", lang), 
				32, 233, 152, 14));
		txtSessionTimeout = WizardHelper.createNumberTextFile(140, 230, 59, 20);
		txtSessionTimeout.getDocument().addDocumentListener(textFieldChandeListener);
		settingsPanel.add(txtSessionTimeout);
		settingsPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("SECONDS", lang)
				, 208, 233, 70, 14));
		settingsPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("WALLPAPER_IMAGE_FILE_SIZE_LIMIT", lang), 
				32, 258, 332, 17));
		txtWallpaperFileSize = WizardHelper.createNumberTextFile(347, 256, 86, 20);
		txtWallpaperFileSize.getDocument().addDocumentListener(textFieldChandeListener);
		settingsPanel.add(txtWallpaperFileSize);
		settingsPanel.add(WizardHelper.createLabel(
				LocalizationUtil.getResourceStringForLocale("BYTES", lang), 
				440, 252, 100, 28));
		
		JButton btnClose2 = new JButton(LocalizationUtil.getResourceStringForLocale("CLOSE", lang));
		btnClose2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		btnClose2.setBounds(453, 314, 108, 23);
		settingsPanel.add(btnClose2);
		
		JLabel lblpleaseFillUp = new JLabel("<html>" + LocalizationUtil.getResourceStringForLocale("DESCRIPTION_WENADMIN_SETTINGS_TAB", lang) + " \r\n<hr />\r\n</html>");
		lblpleaseFillUp.setHorizontalAlignment(SwingConstants.LEFT);
		lblpleaseFillUp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblpleaseFillUp.setBounds(21, 39, 543, 50);
		settingsPanel.add(lblpleaseFillUp);
		
		btnRestoreDefaultValues = new JButton(LocalizationUtil.getResourceStringForLocale("RSTORE_DEFAULT", lang));
		btnRestoreDefaultValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(
						SwingUtilities.windowForComponent(btnRestoreDefaultValues), 
						LocalizationUtil.getResourceStringForLocale("DESCRIPTION_RESTORE_DEFAULT_SETTINGS", lang), 
						LocalizationUtil.getResourceStringForLocale("RESTORE_DEFAULT_SETTINGS", lang), 
						JOptionPane.YES_NO_OPTION
						) == JOptionPane.YES_OPTION) {
					// retrieve the default setting, and reset the form with the default setting
					AMWebApiSettings defaultSettings = new AMWebApiSettings(localAMWebAPiConfigurationFilePath);
					initializeWebAdminSetting(defaultSettings);
					resetWebAdminSettings();
				}
			}
		});
		
		btnRestoreDefaultValues.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnRestoreDefaultValues.setBounds(32, 314, 130, 23);
		settingsPanel.add(btnRestoreDefaultValues);
		
		frmAbsoluteWeb.getRootPane().setDefaultButton(btnAddServer);
		btnAddServer.requestFocus();
	}
	 
	private static boolean parseArguments(String[] args, String lang) {
		webApiCertificateFolder = "";
		syncServicePortNumber = 0;
		caller = "";
		JLabel label;
		
		if (args.length != 2 && args.length != 3) {
			label = new JLabel(LocalizationUtil.getResourceStringForLocale("INCORRECT_NUMBER_OF_PARAMS", lang, new Object[] {"WebAPICertFolder", "SyncSvcPortNumber", "Caller"}));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label,
					errorTitle, JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (args.length == 2) {
			args = Arrays.copyOf(args, args.length + 1);
			args[args.length - 1] = "Caller=Configuration";
		}
		
		for (String param : args) {
			String[] tokens = param.split("=");
			if (tokens.length != 2) {
				label = new JLabel(LocalizationUtil.getResourceStringForLocale("UNEXPECTED_PARAM", lang, new Object[] {param}));
			    label.setFont(new Font("Dialog", Font.PLAIN, 16));
				JOptionPane.showMessageDialog(null, label,
						errorTitle, JOptionPane.ERROR_MESSAGE);
	            break;
			}

			if (tokens[0].equals("WebAPICertFolder")) {
				webApiCertificateFolder = tokens[1];
			} else if (tokens[0].equals("SyncSvcPortNumber")) {
				syncServicePortNumber = Short.parseShort(tokens[1]);
			} else if (tokens[0].equals("Caller")) {
				caller = tokens[1];
			}else {
				label = new JLabel(LocalizationUtil.getResourceStringForLocale("UNEXPECTED_PARAM", lang, new Object[] {param}));
			    label.setFont(new Font("Dialog", Font.PLAIN, 16));
				JOptionPane.showMessageDialog(null, label,
						errorTitle, JOptionPane.ERROR_MESSAGE);
				break;
			}
		}

		if (webApiCertificateFolder == null || webApiCertificateFolder.length() == 0) {
			label = new JLabel(LocalizationUtil.getResourceStringForLocale("MISSING_PARAM", lang, new Object[] {"WebAPICertFolder"}));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label,
					errorTitle, JOptionPane.ERROR_MESSAGE);
		} else if (syncServicePortNumber == 0) {
			label = new JLabel(LocalizationUtil.getResourceStringForLocale("MISSING_PARAM", lang, new Object[] {"SyncSvcPortNumber"}));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label,
					errorTitle, JOptionPane.ERROR_MESSAGE);
		} else if (caller == null || caller.length() == 0) {
			label = new JLabel(LocalizationUtil.getResourceStringForLocale("MISSING_PARAM", lang, new Object[] {"Caller"}));
		    label.setFont(new Font("Dialog", Font.PLAIN, 16));
			JOptionPane.showMessageDialog(null, label,
					errorTitle, JOptionPane.ERROR_MESSAGE);
		}
		
		return webApiCertificateFolder.length() > 0 &&
				syncServicePortNumber > 0 &&
				caller.length() > 0;
	}
	
	private boolean copyAMWebApiWebConfigirationToLocal() {
		boolean backupExists = false;
		
		Window currentWindow = SwingUtilities.windowForComponent(btnAddServer);
		File file = new File(localAMWebAPiConfigurationFilePath);
		
		if (!(new File(webApiSettings.amWebApiConfigurationFilePath)).exists()) {
			JOptionPane.showMessageDialog(
					currentWindow, 
					LocalizationUtil.getResourceStringForLocale("WEB_API_CONFIGURATION_FILE_MISSING", lang), 
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
			return backupExists;
		}
		
		if (!file.exists()) {
			try {
				Files.copy(
						Paths.get(webApiSettings.amWebApiConfigurationFilePath), 
						Paths.get(localAMWebAPiConfigurationFilePath), 
						new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING });
				backupExists = true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(
						currentWindow,
						LocalizationUtil.getResourceStringForLocale("FAILS_TO_COPY_WEB_API_CONFIGURATION_FILE", lang), 
						errorTitle, 
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			backupExists = true;
		}
		
		return backupExists;
	}
	
	private void initializeWebAdminSetting(AMWebApiSettings settings) {
		if (settings != null) {
			chkLiveDataUpdate.setSelected(settings.liveDataUpdatesEnabled);
			chkSspLogin.setSelected(settings.sspLoginEnabled);
			txtSessionTimeout.setText(String.valueOf(settings.sessionTimeoutInSecond));
			chkSyncSessionAfterDisconnect.setSelected(settings.synchSessionAfrterDisconnectEnabled);
			txtDataRefreshDelay.setText(String.valueOf(settings.dataRefreshingDelay));
			txtWallpaperFileSize.setText(String.valueOf(settings.wallpaperFileSize));
		}
	}
	
	private boolean saveWebAdminSettings() {
		boolean saved = false;
		if (validateAMWebApiSetting()) {
			if (webApiSettings.saveSettings()) {
				dataDirty = false;
				JOptionPane.showMessageDialog(
						SwingUtilities.windowForComponent(btnSaveSettings), 
						LocalizationUtil.getResourceStringForLocale("SAVE_SETTING_SUCCEED_MESSAGE", lang), 
						LocalizationUtil.getResourceStringForLocale("WEB_ADMIN_SETTINGS", lang),
						JOptionPane.INFORMATION_MESSAGE
						);
				saved = true;
			} else {
				JOptionPane.showMessageDialog(
						SwingUtilities.windowForComponent(btnSaveSettings), 
						LocalizationUtil.getResourceStringForLocale("FAILS_TO_SAVE_SETTINGS", lang),
						errorTitle, 
						JOptionPane.ERROR_MESSAGE
						);
			}
		}
		
		return saved;
	}
	
	private void resetWebAdminSettings()
	{
		txtDataRefreshDelay.setEnabled(chkLiveDataUpdate.isSelected());
		lblDataRefreshing.setForeground(chkLiveDataUpdate.isSelected()? Color.BLACK : Color.GRAY);
		lblDataRefreshingMilliseconds.setForeground(lblDataRefreshing.getForeground());
	}

	private boolean validateAMWebApiSetting() {		
		webApiSettings.liveDataUpdatesEnabled = chkLiveDataUpdate.isSelected();
		webApiSettings.sspLoginEnabled = chkSspLogin.isSelected();
		webApiSettings.synchSessionAfrterDisconnectEnabled = chkSyncSessionAfterDisconnect.isSelected();
		String dataRefreshingDelayAsString = txtDataRefreshDelay.getText();
		String sessionTimeoutInSecondAsString = txtSessionTimeout.getText();
		String wallpaperFileSizeAsString = txtWallpaperFileSize.getText();
		
		if (webApiSettings.liveDataUpdatesEnabled) {
			if (WizardHelper.validateNumberString(dataRefreshingDelayAsString, 
					WizardHelper.DATA_REFRESH_DELAY_MINIMUM_VALUE, 
					WizardHelper.DATA_REFRESH_DELAY_MAXIMUM_VALUE)) {
				webApiSettings.dataRefreshingDelay = Long.parseLong(dataRefreshingDelayAsString);
			} else {
				JOptionPane.showMessageDialog(
					SwingUtilities.windowForComponent(btnSaveSettings),
					LocalizationUtil.getResourceStringForLocale("ENTER_LIMIT_REFRESH_RATE", lang,
							WizardHelper.DATA_REFRESH_DELAY_MINIMUM_VALUE, 
							WizardHelper.DATA_REFRESH_DELAY_MAXIMUM_VALUE),
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		
		if (WizardHelper.validateNumberString(sessionTimeoutInSecondAsString, 
				WizardHelper.SESSION_TIMEOUT_MINIMUM_VALUE, 
				WizardHelper.SESSION_TIMEOUT_MAXIMUM_VALUE)) {
			webApiSettings.sessionTimeoutInSecond = Long.parseLong(sessionTimeoutInSecondAsString);
		} else {
			JOptionPane.showMessageDialog(
					SwingUtilities.windowForComponent(btnSaveSettings),
					LocalizationUtil.getResourceStringForLocale("ENTER_SESSION_TIMEOUT", lang,
							WizardHelper.SESSION_TIMEOUT_MINIMUM_VALUE, 
							WizardHelper.SESSION_TIMEOUT_MAXIMUM_VALUE),
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// the wallpaper image cannot be bigger than 10M
		if (WizardHelper.validateNumberString(wallpaperFileSizeAsString, 
				WizardHelper.WALLPAPER_SIZE_MINIMUM_VALUE, 
				WizardHelper.WALLPAPER_SIZE_MAXIMUM_VALUE)) {
			webApiSettings.wallpaperFileSize = Long.parseLong(wallpaperFileSizeAsString);
		} else {
			JOptionPane.showMessageDialog(
					SwingUtilities.windowForComponent(btnSaveSettings),
					LocalizationUtil.getResourceStringForLocale("ENTER_WALLPAPER_IMAGE_FILE_SIZE", lang,
							WizardHelper.WALLPAPER_SIZE_MINIMUM_VALUE, 
							WizardHelper.WALLPAPER_SIZE_MAXIMUM_VALUE),
					errorTitle,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	private String getAMWebApiConfigurationFilePath() {
		String amWebApiConfigurationFilePath = "";
		// calculate the AM Web API configuration file path: 
		// use the file path stored in the wizard-setting.txt files; if not found inside of the file, then 
		//	retrieve the Tomcat Install folder from Windows registry table, and add '\\webapps\\com.absolute.am.webapi\\WEB-INF\\' as sub-foler
		//  if file path is still empty, then use default one valed in the 'WizardHelper' class
		if (wizardHelper.amWebApiConfigurationFile != null && wizardHelper.amWebApiConfigurationFile.length() > 0 ) {
			amWebApiConfigurationFilePath = wizardHelper.amWebApiConfigurationFile;
		} else {
			String tomcatInstallPath = WizardHelper.getApacheTomcatInstallPath();
			if (tomcatInstallPath != null && tomcatInstallPath.length() > 0) {
				amWebApiConfigurationFilePath = tomcatInstallPath + "\\" + WizardHelper.AM_WEB_API_CONFIGURATION_FILE_PATH;
			}
		}
		
		if (amWebApiConfigurationFilePath.length() == 0) {
			JOptionPane.showMessageDialog(
					SwingUtilities.windowForComponent(btnSaveSettings), 
					LocalizationUtil.getResourceStringForLocale("FAILS_TO_RETRIEVE_API_CONFIG_FILE_LOCATION_SETTING", lang),
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
		} else {
			if (!(new File(amWebApiConfigurationFilePath).exists())) {
				JOptionPane.showMessageDialog(
					SwingUtilities.windowForComponent(btnSaveSettings),
					LocalizationUtil.getResourceStringForLocale("WEB_API_CONFIGURATION_FILE_MISSING_2", lang, amWebApiConfigurationFilePath), 
					errorTitle, 
					JOptionPane.ERROR_MESSAGE);
				
				amWebApiConfigurationFilePath = "";
			}
		}
		
		return amWebApiConfigurationFilePath;
	}
	
	private void close()
	{
		if (dataDirty) {
			Object[] options = { "Save", "Don't Save", "Cancel" };
			int choice = JOptionPane.showOptionDialog(
					SwingUtilities.windowForComponent(btnSaveSettings),
					LocalizationUtil.getResourceStringForLocale("MESSAGE_SAVE_WEB_ADMIN_SETTINGS", lang),
					LocalizationUtil.getResourceStringForLocale("WEB_ADMIN_SETTINGS", lang),
					JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					options, 
					options[0]);
	
			if (choice == JOptionPane.YES_OPTION) {
				if (saveWebAdminSettings()) {
					System.exit(0);
				}
			} else if (choice == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else {
				// do nothing for clicking 'Cancel'
			}
			
		} else {
			System.exit(0);
		}

	}
}


