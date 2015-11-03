package com.absolute.am.Elevator;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.swing.JOptionPane;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;

public class PrivilegesElevator 
{
	private static final String SYSTEM_PROPERTY_NAME_JAVA_HOME = "java.home";
	private static final String SUB_FOLDER_NAME_BIN = "bin";
	private static final String JAVA_EXECUTABLE_FILE_NAME = "javaw.exe";
	
	// the code is referring to the following web page:
	// http://stackoverflow.com/questions/13758458/add-a-uac-shield-to-a-button-and-retain-its-background-image/14980972#14980972
	// "C:\Program Files\Java\jre7\bin\javaw.exe" -jar 
	//				"C:\Program Files\Absolute Software\AMCertRegistration\ABT_AM_RegisterWizard.jar" 
	//				WebAPICertFolder="C:\ProgramData\Absolute Software\AmWebApiData\certificates" 
	//				SyncSvcPortNumber=9668 
	//				Caller="Installation"		-- Configuration
    public static void main( String[] args ) throws URISyntaxException
    {
    	CodeSource codeSource = PrivilegesElevator.class.getProtectionDomain().getCodeSource();
		String jarDir = new File(codeSource.getLocation().toURI().getPath()).getParentFile().getPath();
    	String jarFilePath = jarDir + "\\AMWebConfigurationApp.jar";
    	String javaExecuateblePath = getJavaExecutablePath();
    	
    	String parameterString = "";
    	if (args != null) {
    		for(String para : args) {
    			parameterString = parameterString + " \"" + para + "\"";
    		}
    	}
    	
    	// check if the Java executable file (java.exe or javaw.exe) exists
    	if (javaExecuateblePath.length() == 0) {
    		JOptionPane.showMessageDialog(null, "Cannot find the Java execuatble file, please check if the JRE has been installed on the computer properly.\r\n",
					"Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}
    	
    	// Check if the JAR file exists in the same folder as 'Elevator' JAR file
    	if (!new File(jarFilePath).exists()) {
    		JOptionPane.showMessageDialog(null, "The following jar file doesn't exists:\r\n" + jarFilePath,
					"Error", JOptionPane.ERROR_MESSAGE);
    	} else {
    		executeAsAdministrator(javaExecuateblePath, " -jar \"" + jarFilePath + "\" " + parameterString);
    	}
    	
    	System.exit(0);
    }
    
    public static void executeAsAdministrator(String command, String args)
    {
        Shell32X.SHELLEXECUTEINFO execInfo = new Shell32X.SHELLEXECUTEINFO();
        execInfo.lpFile = new WString(command);
        if (args != null)
            execInfo.lpParameters = new WString(args);
        execInfo.nShow = Shell32X.SW_SHOWDEFAULT;
        execInfo.fMask = Shell32X.SEE_MASK_NOCLOSEPROCESS;
        execInfo.lpVerb = new WString("runas");
        boolean result = Shell32X.INSTANCE.ShellExecuteEx(execInfo);

        if (!result)
        {
            int lastError = Kernel32.INSTANCE.GetLastError();
            String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
            throw new RuntimeException("Error elevation privileges: " + lastError + ": " + errorMessage + " (apperror=" + execInfo.hInstApp + ")");
        }
    }
    
    private static String getJavaExecutablePath() {
    	String path = "";
    	String javaHome = System.getProperty(SYSTEM_PROPERTY_NAME_JAVA_HOME);
        File file = new File(javaHome);
        file = new File(file, SUB_FOLDER_NAME_BIN);
        file = new File(file, JAVA_EXECUTABLE_FILE_NAME);
        
        if (file.exists()) {
        	path = file.toString();
        }
        
        return path;
    }
}
