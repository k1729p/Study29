@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set JVM_ARGS=--enable-native-access=ALL-UNNAMED
set JVM_ARGS=%JVM_ARGS% --sun-misc-unsafe-memory-access=allow
set JAR_FILE=target\Study29.jar
set MAIN_CLASS=kp.gui.GuiClientLauncher
cd ..
%JAVA_HOME%\bin\java %JVM_ARGS% -cp %JAR_FILE% %MAIN_CLASS%
::pause