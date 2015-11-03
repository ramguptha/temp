@echo off
REM Do not change the sequence without thorough testing!

net stop Tomcat7
net stop Tomcat8

net stop "Absolute Software AM Sync Service"

net stop http /y
net start http
net start w3svc

%WINDIR%\System32\inetsrv\appcmd.exe delete site "AmWebUi"

sc delete "Absolute Software AM Sync Service"

takeown /R /D Y /F "C:\Program Files (x86)\Absolute Software\AMSyncServices"
takeown /R /D Y /F "C:\Program Files\Absolute Software\AMSyncServices"
takeown /R /D Y /F "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\com.absolute.am.webapi"
takeown /R /D Y /F "C:\Program Files\Apache Software Foundation\Tomcat 8.0\webapps\com.absolute.am.webapi"

rd "C:\Program Files (x86)\Absolute Software\AMCertRegistration" /s /q
rd "C:\Program Files\Absolute Software\AMCertRegistration" /s /q

del "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Absolute Manage\Web Admin Certificate Registration.lnk" /f /q
del "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Absolute Manage\Web Admin Configuration Tool.lnk" /f /q

rd "C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Absolute Manage"

rd "C:\Program Files\Absolute Software\AmWebUi" /s /q

del "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\com.absolute.am.webapi.war" /f /q
del "C:\Program Files\Apache Software Foundation\Tomcat 8.0\webapps\com.absolute.am.webapi.war" /f /q

rd "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\com.absolute.am.webapi" /s /q
rd "C:\Program Files\Apache Software Foundation\Tomcat 8.0\webapps\com.absolute.am.webapi" /s /q

cd %1

call delete_cert.cmd %2 com.absolute.webapi.cert.hash.txt com.absolute.webapi.cert.ipport.txt

del /f /q certutil.exe

del /f /q delete_cert.cmd

del /f /q com.absolute.am.webapi.tomcatport.txt

cd\
   
takeown /R /D Y /F "C:\ProgramData\Absolute Software\AMSyncServices"

rd "C:\amwebapi" /s /q

rd "C:\ProgramData\Absolute Software\AMSyncServices" /s /q
rd "C:\ProgramData\Absolute Software\AmWebApiData" /s /q
	
rd "C:\ProgramData\Absolute Software"

rd "C:\Program Files (x86)\Absolute Software\AMSyncServices" /s /q
rd "C:\Program Files\Absolute Software\AMSyncServices" /s /q
rd "C:\Program Files\Absolute Software\AMWebConfigurationTool" /s /q

rd "C:\Program Files (x86)\Absolute Software"

rd "C:\Program Files\Absolute Software"

rd %3 /s /q

net start Tomcat8
