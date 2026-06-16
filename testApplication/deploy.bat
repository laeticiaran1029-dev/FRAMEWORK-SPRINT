@echo off
setlocal EnableDelayedExpansion

cd /d "D:\WEB DYN\FRAMEWORK_fra\testApplication"

set "APP_NAME=testApplication"
set "SRC_DIR=src\main\java"
set "WEB_DIR=src\main\webapp"
set "BUILD_DIR=build"
set "LIB_DIR=lib"
set "TOMCAT=D:\apache-tomcat-10.1.55"
set "CLASSPATH=%LIB_DIR%\servlet-api.jar;%LIB_DIR%\essai.jar;%LIB_DIR%\mysql-connector-j-9.5.0.jar"

if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\classes"
mkdir "%BUILD_DIR%\WEB-INF\lib"

if exist sources.txt del sources.txt
for /R "%SRC_DIR%" %%F in (*.java) do (
    set "SOURCE_FILE=%%F"
    echo "!SOURCE_FILE:\=/!" >> sources.txt
)

javac -cp "%CLASSPATH%" -d "%BUILD_DIR%\WEB-INF\classes" @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt

xcopy "%WEB_DIR%\*" "%BUILD_DIR%\" /E /I /Y
copy /Y "%LIB_DIR%\essai.jar" "%BUILD_DIR%\WEB-INF\lib\"
copy /Y "%LIB_DIR%\mysql-connector-j-9.5.0.jar" "%BUILD_DIR%\WEB-INF\lib\"

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

call shutdown.bat
timeout /t 3 >nul
call startup.bat

pause
