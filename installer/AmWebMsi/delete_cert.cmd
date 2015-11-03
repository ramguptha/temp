rem parameters: %1 store, %2 hash file, %3 ipport file

set /p MYHASH= <"%2"
echo %MYHASH%


set /p MYIPPORT= <"%3"
echo %MYIPPORT%

certutil.exe -delstore %1 %MYHASH%

netsh http delete sslcert ipport=%MYIPPORT%
