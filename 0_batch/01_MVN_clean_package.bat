@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
cd ..
@powershell -Command Write-Host "START" -foreground "Green"
call %M2_HOME%\bin\mvn --quiet clean package
@powershell -Command Write-Host "FINISH" -foreground "Red"
pause