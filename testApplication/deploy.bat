@echo off
setlocal EnableDelayedExpansion

:: 1. Se placer dans le dossier de testApplication (celui qui contient CE
::    script, quel que soit l'endroit ou le projet a ete deplace/clone)
cd /d "%~dp0"

:: -- Dossier racine du projet (parent de testApplication\) --
for %%I in ("%~dp0..") do set "ROOT_DIR=%%~fI"

:: 2. Variables (chemins deduits de l'emplacement du script)
set "APP_NAME=testApplication"
set "SRC_DIR=%~dp0src\main\java"
set "WEB_DIR=%~dp0src\main\webapp"
set "BUILD_DIR=%~dp0build"
set "LIB_DIR=%~dp0lib"

:: -- TOMCAT : utilise CATALINA_HOME s'il est defini dans l'environnement,
::    sinon retombe sur cette valeur par defaut a adapter une seule fois --
if defined CATALINA_HOME (
    set "TOMCAT=%CATALINA_HOME%"
) else (
    set "TOMCAT=C:\apache-tomcat-10.1.55"
)

:: -- Sprint 5-bis : dossier des jars Spring + MySQL (racine du projet) --
set "SPRING_LIB=%ROOT_DIR%\lib"

:: 3. Classpath absolu (Tomcat + framework.jar + tous les jars de lib\)
set "CLASSPATH=%TOMCAT%\lib\servlet-api.jar;%LIB_DIR%\framework.jar;%SPRING_LIB%\*;%TOMCAT%\bin\bootstrap.jar"

:: 4. Dossiers
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\classes"
mkdir "%BUILD_DIR%\WEB-INF\lib"

:: 5. Copie lib (framework.jar + jars Spring/MySQL) dans le WAR
if exist "%LIB_DIR%\*.jar" (
    copy /Y "%LIB_DIR%\*.jar" "%BUILD_DIR%\WEB-INF\lib\"
)
if exist "%SPRING_LIB%\*.jar" (
    copy /Y "%SPRING_LIB%\*.jar" "%BUILD_DIR%\WEB-INF\lib\"
)

:: 6. Compilation
if exist sources.txt del sources.txt
for /R "%SRC_DIR%" %%F in (*.java) do (
    set "SOURCE_FILE=%%F"
    echo "!SOURCE_FILE:\=/!" >> sources.txt
)

javac -parameters -cp "%CLASSPATH%" -d "%BUILD_DIR%\WEB-INF\classes" @sources.txt

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