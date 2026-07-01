@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
set QUIET=--quiet
set JAR_FILE=target\Study29.jar
set MAIN_CLASS=kp.Application
cd ..
chcp 65001 > nul 2>&1
@powershell -Command Write-Host "START" -foreground "Green"
call %M2_HOME%\bin\mvn %QUIET% clean package
@powershell -Command Write-Host "FINISH" -foreground "Red"
pause
set ARG=CAS_02
call %JAVA_HOME%\bin\java --enable-native-access=ALL-UNNAMED -cp %JAR_FILE% %MAIN_CLASS% %ARG%
pause