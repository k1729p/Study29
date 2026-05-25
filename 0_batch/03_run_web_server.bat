@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
set JARFILE=target\Study29-jar-with-dependencies.jar 
set MAINCLASS=kp.web.httpserver.WebServerLauncher
cd ..
%JAVA_HOME%\bin\java -cp %JARFILE% %MAINCLASS%