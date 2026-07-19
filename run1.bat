@echo off
setlocal EnableDelayedExpansion

:: 1. Se placer dans le dossier du Framework
cd /d "C:\web dyn\FRAMEWORK_fra\Framework"

:: 2. Définition des variables
set "APP_NAME=framework"
set "JAR_NAME=framework.jar"
set "SRC_DIR=src\main\java"
set "WEB_DIR=src\main\webapp"
set "BUILD_DIR=build"
set "TOMCAT=C:\apache-tomcat-10.1.55"
set "SERVLET_API_JAR=%TOMCAT%\lib\servlet-api.jar"


set "SPRING_LIB=C:\web dyn\FRAMEWORK_fra\lib"

:: -- NOUVEAU : Chemin vers le dossier lib de testApplication --
set "TEST_APP_LIB=C:\web dyn\FRAMEWORK_fra\testApplication\lib"

:: 3. Nettoyage et préparation du dossier build
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\classes"

:: 4. Compilation des classes Java
if exist sources.txt del sources.txt
for /R "%SRC_DIR%" %%F in (*.java) do (
    set "SOURCE_FILE=%%F"
    echo "!SOURCE_FILE:\=/!" >> sources.txt
)

javac -cp "%SERVLET_API_JAR%;%SPRING_LIB%\*" -d "%BUILD_DIR%\WEB-INF\classes" @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt


echo.
echo --- Creation du fichier %JAR_NAME% ---
cd /d "%BUILD_DIR%\WEB-INF\classes"
jar -cvf "%JAR_NAME%" *
if errorlevel 1 (
    echo JAR creation failed.
    pause
    exit /b 1
)

cd /d "C:\web dyn\FRAMEWORK_fra\Framework"

echo.
echo --- Copie du JAR vers testApplication ---
if not exist "%TEST_APP_LIB%" mkdir "%TEST_APP_LIB%"
copy /Y "%BUILD_DIR%\WEB-INF\classes\%JAR_NAME%" "%TEST_APP_LIB%\"
if errorlevel 1 (
    echo Echec de la copie du JAR.
    pause
    exit /b 1
)
echo Copie reussie dans : %TEST_APP_LIB%
echo.


if exist "%WEB_DIR%" (
    xcopy "%WEB_DIR%\*" "%BUILD_DIR%\" /E /I /Y >nul
)


cd /d "%BUILD_DIR%"
jar -cvf "%APP_NAME%.war" * >nul
if errorlevel 1 (
    echo WAR creation failed.
    pause
    exit /b 1
)
cd ..

copy /Y "%BUILD_DIR%\%APP_NAME%.war" "%TOMCAT%\webapps\" >nul
if errorlevel 1 (
    echo WAR deployment failed.
    pause
    exit /b 1
)


cd /d "%TOMCAT%\bin"

netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if not errorlevel 1 (
    call shutdown.bat
    timeout /t 3 >nul
) else (
    echo Tomcat is not running. Starting it now...
)

call startup.bat
echo ===================================================
echo Compilation du Framework et deploiement termines !
echo Le fichier %JAR_NAME% a ete copie avec succes !
echo ===================================================
pause