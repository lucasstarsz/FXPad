@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\javaw"



pushd %DIR% & start "formatfx" %JAVA_EXEC% %CDS_JVM_OPTS%  -p "%~dp0/../app" -m formatfx/org.lucasstarsz.fxpad.RTFXMain  %* & popd
