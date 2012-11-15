

@REM App launcher script
@REM
@REM Environment:
@REM JAVA_HOME - location of a JDK home dir (mandatory)
@REM APP_OPTS  - JVM options (optional)

@setlocal
@echo off
set APP_HOME=%~dp0
set ERROR_CODE=0
set APP_JAR=wicket.jar
set MAIN_CLASS=com.mle.wicket.WicketStart
rem We use the value of the JAVACMD environment variable if defined
set _JAVACMD=%JAVACMD%
if "%_JAVACMD%"=="" (
    if not "%JAVA_HOME%"=="" (
        if exist "%JAVA_HOME%\bin\java.exe" set "_JAVACMD=%JAVA_HOME%\bin\java.exe"
    )
)
if "%_JAVACMD%"=="" set _JAVACMD=java

rem We use the value of the JAVA_OPTS environment variable if defined
set _JAVA_OPTS=%JAVA_OPTS%
if "%_JAVA_OPTS%"=="" set _JAVA_OPTS=-Xmx512M -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=128m

:run

"%_JAVACMD%" %_JAVA_OPTS% %APP_OPTS% -cp "%APP_HOME%%APP_JAR%;%APP_HOME%lib/*" %MAIN_CLASS% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end

@endlocal

exit /B %ERROR_CODE%



