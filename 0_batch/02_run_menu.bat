@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
set JVM_ARGS=--enable-native-access=ALL-UNNAMED
set JVM_ARGS=%JVM_ARGS% --sun-misc-unsafe-memory-access=allow
set JAR_FILE=target\Study29.jar
set MAIN_CLASS=kp.Application
cd ..

:menu
set KEY=
set ARG=
echo.
echo - - - - - - - - - - - - - - -
echo [A] Cassandra Schema Discovery
echo [B] Cassandra Departments and Employees
echo - - - - - - - - - - - - - - -
echo [C] Chroma Schema Discovery
echo [D] Chroma Departments and Employees
echo - - - - - - - - - - - - - - -
echo [E] Elasticsearch Schema Discovery
echo [F] Elasticsearch Departments and Employees
echo - - - - - - - - - - - - - - -
echo [G] Neo4j Schema Discovery
echo [H] Neo4j Departments and Employees
echo [I] Neo4j Northwind Initialization
echo [J] Neo4j Northwind Read All
echo [K] Neo4j Queries
echo - - - - - - - - - - - - - - -
echo [L] MongoDB Collections and Indexes Discovery
echo [M] MongoDB Departments and Employees
echo - - - - - - - - - - - - - - -
echo [N] MySQL Schema Discovery (for table schema 'kp_database')
echo [O] MySQL Departments and Employees
echo - - - - - - - - - - - - - - -
echo [P] Oracle Schema Discovery (only Departments and Employees)
echo [Q] Oracle Departments and Employees
echo - - - - - - - - - - - - - - -
echo [R] PostgreSQL Schema Discovery (for table schema 'public')
echo [S] PostgreSQL Departments and Employees
echo - - - - - - - - - - - - - - -
echo [T] Redis Schema Discovery
echo [U] Redis Departments and Employees
echo [V] Redis Probabilistic Data Types
echo - - - - - - - - - - - - - - -
echo [W] SQL Server Schema Discovery (only Departments and Employees)
echo [X] SQL Server Departments and Employees
::echo [Y]
::echo [Z]
echo - - - - - - - - - - - - - - -
echo any other key quits
set /P KEY="Select the key: "
if /i "%KEY:~0,1%"=="A" (
  set LABEL=[%KEY%] run application
  set ARG=CAS_01
  call :RunApplication
) else if /i "%KEY%"=="B" (
  set LABEL=[%KEY%] run application
  set ARG=CAS_02
  call :RunApplication
) else if /i "%KEY%"=="C" (
  set LABEL=[%KEY%] run application
  set ARG=CHR_01
  call :RunApplication
) else if /i "%KEY%"=="D" (
  set LABEL=[%KEY%] run application
  set ARG=CHR_02
  call :RunApplication
) else if /i "%KEY%"=="E" (
  set LABEL=[%KEY%] run application
  set ARG=ELA_01
  call :RunApplication
) else if /i "%KEY%"=="F" (
  set LABEL=[%KEY%] run application
  set ARG=ELA_02
  call :RunApplication
) else if /i "%KEY%"=="G" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_01
  call :RunApplication
) else if /i "%KEY%"=="H" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_02
  call :RunApplication
) else if /i "%KEY%"=="I" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_03
  call :RunApplication
) else if /i "%KEY%"=="J" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_04
  call :RunApplication
) else if /i "%KEY%"=="K" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_05
  call :RunApplication
) else if /i "%KEY%"=="L" (
  set LABEL=[%KEY%] run application
  set ARG=MON_01
  call :RunApplication
) else if /i "%KEY%"=="M" (
  set LABEL=[%KEY%] run application
  set ARG=MON_02
  call :RunApplication
) else if /i "%KEY%"=="N" (
  set LABEL=[%KEY%] run application
  set ARG=MYS_01
  call :RunApplication
) else if /i "%KEY%"=="O" (
  set LABEL=[%KEY%] run application
  set ARG=MYS_02
  call :RunApplication
) else if /i "%KEY%"=="P" (
  set LABEL=[%KEY%] run application
  set ARG=ORA_01
  call :RunApplication
) else if /i "%KEY%"=="Q" (
  set LABEL=[%KEY%] run application
  set ARG=ORA_02
  call :RunApplication
) else if /i "%KEY%"=="R" (
  set LABEL=[%KEY%] run application
  set ARG=POS_01
  call :RunApplication
) else if /i "%KEY%"=="S" (
  set LABEL=[%KEY%] run application
  set ARG=POS_02
  call :RunApplication
) else if /i "%KEY%"=="T" (
  set LABEL=[%KEY%] run application
  set ARG=RED_01
  call :RunApplication
) else if /i "%KEY%"=="U" (
  set LABEL=[%KEY%] run application
  set ARG=RED_02
  call :RunApplication
) else if /i "%KEY%"=="V" (
  set LABEL=[%KEY%] run application
  set ARG=RED_03
  call :RunApplication
) else if /i "%KEY%"=="W" (
  set LABEL=[%KEY%] run application
  set ARG=SQL_01
  call :RunApplication
) else if /i "%KEY%"=="X" (
  set LABEL=[%KEY%] run application
  set ARG=SQL_02
  call :RunApplication
@REM ) else if /i "%KEY%"=="Y" (
@REM ) else if /i "%KEY%"=="Z" (
) else (
  goto :eof
)
cls
goto menu
:: =================================================================================================================================================
:RunApplication
cls
%JAVA_HOME%\bin\java %JVM_ARGS% -cp %JAR_FILE% %MAIN_CLASS% %ARG%
call :RedLabelAndPause
cls
goto :eof
:: =================================================================================================================================================
:RedLabelAndPause
powershell -Command Write-Host "FINISH %LABEL%" -foreground "Red"
pause
goto :eof
:: =================================================================================================================================================