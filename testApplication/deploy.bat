@echo off
setlocal EnableDelayedExpansion

:: 1. Se placer dans le bon dossier (Modifié pour C:)
cd /d "C:\web dyn\FRAMEWORK_fra\testApplication"

:: 2. Variables avec CHEMINS ABSOLUS (Modifiés pour C:)
set "APP_NAME=testApplication"
set "SRC_DIR=C:\web dyn\FRAMEWORK_fra\testApplication\src\main\java"
set "WEB_DIR=C:\web dyn\FRAMEWORK_fra\testApplication\src\main\webapp"
set "BUILD_DIR=C:\web dyn\FRAMEWORK_fra\testApplication\build"
set "LIB_DIR=C:\web dyn\FRAMEWORK_fra\testApplication\lib"
set "TOMCAT=C:\apache-tomcat-10.1.55"

:: 3. Classpath absolu (inclut Tomcat ET tes libs - MODIFIÉ AVEC framework.jar)
set "CLASSPATH=%TOMCAT%\lib\servlet-api.jar;%LIB_DIR%\framework.jar;%LIB_DIR%\mysql-connector-j-9.5.0.jar;%TOMCAT%\bin\bootstrap.jar"

:: 4. Dossiers
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\classes"
mkdir "%BUILD_DIR%\WEB-INF\lib"

:: 5. Copie lib
if exist "%LIB_DIR%\*.jar" (
    copy /Y "%LIB_DIR%\*.jar" "%BUILD_DIR%\WEB-INF\lib\"
)

:: 6. Compilation
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

:: 7. Vues
if exist "%WEB_DIR%" (
    xcopy "%WEB_DIR%\*" "%BUILD_DIR%\" /E /I /Y
)

:: 8. WAR
cd /d "%BUILD_DIR%"
jar -cvf "%APP_NAME%.war" *
if errorlevel 1 (
    echo WAR creation failed.
    pause
    exit /b 1
)

:: 9. Deploy
cd ..
copy /Y "%BUILD_DIR%\%APP_NAME%.war" "%TOMCAT%\webapps\"
if errorlevel 1 (
    echo WAR deployment failed.
    pause
    exit /b 1
)

:: 10. Tomcat
cd /d "%TOMCAT%\bin"

netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if not errorlevel 1 (
    call shutdown.bat
    timeout /t 3 >nul
) else (
    echo Tomcat is not running. Starting it now...
)

call startup.bat
echo Deploiement termine !
pause