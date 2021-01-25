@echo off
set JAVA_HOME=C:\opt\jdk1.8.0_251

set folder=%1
if "%folder%"=="" set folder=imgs

set start=%time%
%JAVA_HOME%\bin\java -jar java_test_task.jar %folder%
set end=%time%

echo Start=%start%; End=%end%.

pause