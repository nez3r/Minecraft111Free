@echo off
rem Необходим RetroMCP Cli - https://github.com/MCPHackers/RetroMCP-Java
echo Building...
java -jar RetroMCP-Java-CLI.jar build client
timeout /t 3 /NOBREAK >nul 2>&1

cd minecraft\dll && make && make install
cd ..\..\
echo -----------------
echo Done
:: pause

exit /b	