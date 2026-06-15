@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set JAR_FILE=target\Study29.jar
set MAIN_CLASS=kp.gui.GuiClientLauncher
cd ..
%JAVA_HOME%\bin\java -cp %JAR_FILE% %MAIN_CLASS%
::pause