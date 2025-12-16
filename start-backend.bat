@echo off
REM Quick Start Script for Review API Backend

echo ================================
echo  REVIEW API BACKEND QUICK START
echo ================================
echo.

echo [1/4] Checking Java version...
java -version
if errorlevel 1 (
    echo ERROR: Java not found! Please install Java 17+
    pause
    exit /b 1
)
echo.

echo [2/4] Building Maven project...
cd backend
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)
echo.

echo [3/4] Starting Spring Boot Application...
echo Server will run at http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

call mvn spring-boot:run

pause
