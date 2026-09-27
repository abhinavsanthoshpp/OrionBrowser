@echo off
REM 🌌 Orion Browser - Windows Desktop Launcher
REM Architect & Developer: Abhinav Santhosh
REM Copyright (c) 2026 Abhinav Santhosh. All Rights Reserved.

title Orion Browser
cd /d "%~dp0"

echo 🌌 Starting Orion Browser for Windows...
echo Architected & Built by Abhinav Santhosh

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed or not in PATH.
    echo Please install Node.js from https://nodejs.org to run Orion Desktop.
    pause
    exit /b 1
)

if not exist "node_modules\" (
    echo Installing desktop browser packages...
    call npm install
)

echo Launching Orion Desktop...
call npx electron . %*
