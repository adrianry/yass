@echo off
set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.7.0_45
set version=SNAPSHOT
set /p version=Version [ MAJOR.MINOR or 'enter' for %version% ]?:
cmd /c C:\development\gradle-1.9\bin\gradle.bat -Pversion=%version%
pause
