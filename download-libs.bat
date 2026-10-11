@echo off
setlocal EnableDelayedExpansion

:: ============================================================
:: Telechargement des dependances Spring + MySQL
:: Sprint 5-bis : connexion du framework a Spring
:: Les jars sont ranges dans FRAMEWORK_fra\lib
:: Ce script ne modifie AUCUN fichier existant du projet.
:: ============================================================

set "PROJECT_ROOT=C:\web dyn\FRAMEWORK_fra"
set "LIB_DIR=%PROJECT_ROOT%\lib"
set "SPRING_VERSION=6.1.14"
set "PGJDBC_VERSION=42.7.4"
set "GSON_VERSION=2.11.0"
set "MAVEN=https://repo1.maven.org/maven2"

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

set /a ERRORS=0

echo.
echo === Spring Framework %SPRING_VERSION% ===
echo.

for %%M in (spring-core spring-jcl spring-beans spring-context spring-expression spring-aop spring-web spring-jdbc spring-tx) do (
    set "JAR=%%M-%SPRING_VERSION%.jar"
    if exist "%LIB_DIR%\!JAR!" (
        echo [deja la] !JAR!
    ) else (
        echo [telech.] !JAR!
        curl -sfL -o "%LIB_DIR%\!JAR!" "%MAVEN%/org/springframework/%%M/%SPRING_VERSION%/!JAR!"
        if errorlevel 1 (
            echo [ECHEC  ] !JAR!
            set /a ERRORS+=1
            if exist "%LIB_DIR%\!JAR!" del "%LIB_DIR%\!JAR!"
        )
    )
)

echo.
echo === Driver JDBC PostgreSQL %PGJDBC_VERSION% ===
echo.

set "PJAR=postgresql-%PGJDBC_VERSION%.jar"
if exist "%LIB_DIR%\%PJAR%" (
    echo [deja la] %PJAR%
) else (
    echo [telech.] %PJAR%
    curl -sfL -o "%LIB_DIR%\%PJAR%" "%MAVEN%/org/postgresql/postgresql/%PGJDBC_VERSION%/%PJAR%"
    if errorlevel 1 (
        echo [ECHEC  ] %PJAR%
        echo           Cette version n existe peut-etre pas.
        echo           Versions disponibles :
        echo           https://repo1.maven.org/maven2/org/postgresql/postgresql/
        echo           Corrige PGJDBC_VERSION en haut de ce script puis relance.
        set /a ERRORS+=1
        if exist "%LIB_DIR%\%PJAR%" del "%LIB_DIR%\%PJAR%"
    )
)

echo.
echo === Gson %GSON_VERSION% (Sprint 6 : API JSON) ===
echo.

set "GJAR=gson-%GSON_VERSION%.jar"
if exist "%LIB_DIR%\%GJAR%" (
    echo [deja la] %GJAR%
) else (
    echo [telech.] %GJAR%
    curl -sfL -o "%LIB_DIR%\%GJAR%" "%MAVEN%/com/google/code/gson/gson/%GSON_VERSION%/%GJAR%"
    if errorlevel 1 (
        echo [ECHEC  ] %GJAR%
        set /a ERRORS+=1
        if exist "%LIB_DIR%\%GJAR%" del "%LIB_DIR%\%GJAR%"
    )
)

echo.
echo ============================================================
echo Contenu de %LIB_DIR% :
echo.
dir /B "%LIB_DIR%\*.jar" 2>nul
echo.
if "!ERRORS!"=="0" (
    echo Tous les jars ont ete recuperes.
) else (
    echo !ERRORS! telechargement^(s^) en echec -- voir les lignes ECHEC ci-dessus.
)
echo ============================================================
pause
