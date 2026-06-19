@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
set JAR_FILE=target\Study29.jar
set MAIN_CLASS=kp.Application
cd ..

:menu
set KEY=
set ARG=
echo.
echo - - - - - - - - - - - - - - -
echo [A] Neo4j Schema Discovery
echo [B] Neo4j Departments and Employees
echo [C] Neo4j Northwind Initialization
echo [D] Neo4j Northwind Read All
echo [E] Neo4j Queries
echo - - - - - - - - - - - - - - -
echo [F] PostgreSQL Schema Discovery (for table schema 'public')
echo [G] PostgreSQL Departments and Employees
echo - - - - - - - - - - - - - - -
echo [H] Oracle Schema Discovery (only Departments and Employees)
echo [I] Oracle Departments and Employees
echo - - - - - - - - - - - - - - -
echo [J] MySQL Schema Discovery (for table schema 'kp_database')
echo [K] MySQL Departments and Employees
echo - - - - - - - - - - - - - - -
echo [L] MS SQL Server Schema Discovery (only Departments and Employees)
echo [M] MS SQL Server Departments and Employees
echo - - - - - - - - - - - - - - -
echo [N] Redis Schema Discovery
echo [O] Redis Departments and Employees
echo [P] Redis Probabilistic Data Types
echo - - - - - - - - - - - - - - -
echo [Q] MongoDB Collections and Indexes Discovery
echo [R] MongoDB Departments and Employees
echo - - - - - - - - - - - - - - -
echo [S] Elasticsearch Schema Discovery
echo [T] Elasticsearch Departments and Employees
::echo [U]
::echo [V]
::echo [W]
::echo [X]
::echo [Y]
::echo [Z]
echo - - - - - - - - - - - - - - -
echo any other key quits
set /P KEY="Select the key: "
if /i "%KEY:~0,1%"=="A" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_01
  call :RunApplication
) else if /i "%KEY%"=="B" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_02
  call :RunApplication
) else if /i "%KEY%"=="C" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_03
  call :RunApplication
) else if /i "%KEY%"=="D" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_04
  call :RunApplication
) else if /i "%KEY%"=="E" (
  set LABEL=[%KEY%] run application
  set ARG=NEO_05
  call :RunApplication
) else if /i "%KEY%"=="F" (
  set LABEL=[%KEY%] run application
  set ARG=POS_01
  call :RunApplication
) else if /i "%KEY%"=="G" (
  set LABEL=[%KEY%] run application
  set ARG=POS_02
  call :RunApplication
) else if /i "%KEY%"=="H" (
  set LABEL=[%KEY%] run application
  set ARG=ORA_01
  call :RunApplication
) else if /i "%KEY%"=="I" (
  set LABEL=[%KEY%] run application
  set ARG=ORA_02
  call :RunApplication
) else if /i "%KEY%"=="J" (
  set LABEL=[%KEY%] run application
  set ARG=MYS_01
  call :RunApplication
) else if /i "%KEY%"=="K" (
  set LABEL=[%KEY%] run application
  set ARG=MYS_02
  call :RunApplication
) else if /i "%KEY%"=="L" (
  set LABEL=[%KEY%] run application
  set ARG=SQL_01
  call :RunApplication
) else if /i "%KEY%"=="M" (
  set LABEL=[%KEY%] run application
  set ARG=SQL_02
  call :RunApplication
) else if /i "%KEY%"=="N" (
  set LABEL=[%KEY%] run application
  set ARG=RED_01
  call :RunApplication
) else if /i "%KEY%"=="O" (
  set LABEL=[%KEY%] run application
  set ARG=RED_02
  call :RunApplication
) else if /i "%KEY%"=="P" (
  set LABEL=[%KEY%] run application
  set ARG=RED_03
  call :RunApplication
) else if /i "%KEY%"=="Q" (
  set LABEL=[%KEY%] run application
  set ARG=MON_01
  call :RunApplication
) else if /i "%KEY%"=="R" (
  set LABEL=[%KEY%] run application
  set ARG=MON_02
  call :RunApplication
) else if /i "%KEY%"=="S" (
  set LABEL=[%KEY%] run application
  set ARG=ELA_01
  call :RunApplication
) else if /i "%KEY%"=="T" (
  set LABEL=[%KEY%] run application
  set ARG=ELA_02
  call :RunApplication
@REM ) else if /i "%KEY%"=="U" (
@REM ) else if /i "%KEY%"=="V" (
@REM ) else if /i "%KEY%"=="W" (
@REM ) else if /i "%KEY%"=="X" (
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
%JAVA_HOME%\bin\java -cp %JAR_FILE% %MAIN_CLASS% %ARG%
call :RedLabelAndPause
cls
goto :eof
:: =================================================================================================================================================
:RedLabelAndPause
powershell -Command Write-Host "FINISH %LABEL%" -foreground "Red"
pause
goto :eof
:: =================================================================================================================================================