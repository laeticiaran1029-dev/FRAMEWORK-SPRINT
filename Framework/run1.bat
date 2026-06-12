@echo off
setlocal EnableDelayedExpansion

cd /d "D:\WEB DYN\FRAMEWORK_fra\Framework"

set "APP_NAME=framework"
set "SRC_DIR=src\main\java"
set "WEB_DIR=src\main\webapp"
set "BUILD_DIR=build"
set "LIB_DIR=lib"
set "TOMCAT=D:\apache-tomcat-10.1.55"
set "SERVLET_API_JAR=%LIB_DIR%\servlet-api.jar"

if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\classes"

if exist sources.txt del sources.txt
for /R "%SRC_DIR%" %%F in (*.java) do (
    set "SOURCE_FILE=%%F"
    echo "!SOURCE_FILE:\=/!" >> sources.txt
)

javac -cp "%SERVLET_API_JAR%" -d "%BUILD_DIR%\WEB-INF\classes" @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt

if exist "%WEB_DIR%" (
    xcopy "%WEB_DIR%\*" "%BUILD_DIR%\" /E /I /Y
)

cd /d "%BUILD_DIR%"
jar -cvf "%APP_NAME%.war" *
if errorlevel 1 (
    echo WAR creation failed.
    pause
    exit /b 1
)

cd ..

copy /Y "%BUILD_DIR%\%APP_NAME%.war" "%TOMCAT%\webapps"
if errorlevel 1 (
    echo WAR deployment failed.
    pause
    exit /b 1
)

cd /d "%TOMCAT%\bin"

netstat -ano | findstr /R /C:":8005 .*LISTENING" >nul
if not errorlevel 1 (
    call shutdown.bat
    timeout /t 3 >nul
) else (
    echo Tomcat is not running. Starting it now...
)

call startup.bat

pause
