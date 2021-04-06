@echo off
setlocal
set BATCH_HOME=..\
set LIB_HOME=%BATCH_HOME%lib\

set JAVA_HOME=C:\PROGRA~1\Java\jdk1.7.0_80\jre
set JAVACMD="%JAVA_HOME%\bin\java"

set CLASSPATH="%BATCH_HOME%\classes"
set CLASSPATH=%CLASSPATH%;"%LIB_HOME%kwic-imageio-T.1.0.3.jar"

%JAVACMD% -Xms512m -Xmx512m -classpath %CLASSPATH% -Dfile.encoding=UTF-8 Startup F:\User\ScrapingFax F:\User\ScrapingFax\logs
endlocal
