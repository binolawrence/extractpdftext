@echo off
REM Docker Compose Quick Start Script for extractpdftext (Windows)

setlocal enabledelayedexpansion

echo ===================================================================
echo Extract PDF Text - Docker Setup
echo ===================================================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed!
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo [OK] Docker found
docker --version
echo.

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose is not installed!
    pause
    exit /b 1
)

echo [OK] Docker Compose found
docker-compose --version
echo.

REM Menu
echo Choose an action:
echo 1) Build images
echo 2) Start services (docker-compose up)
echo 3) Stop services (docker-compose down)
echo 4) View logs
echo 5) Clean up (remove containers, images, volumes)
echo 6) Full rebuild and start
echo.

set /p choice="Enter choice (1-6): "

if "%choice%"=="1" (
    echo Building Docker images...
    docker-compose build
    echo [OK] Build complete!
) else if "%choice%"=="2" (
    echo Starting services...
    docker-compose up -d
    timeout /t 3 /nobreak
    echo [OK] Services started!
    docker-compose ps
    echo.
    echo Access your application at:
    echo   - Frontend: http://localhost:3000
    echo   - Backend: http://localhost:8080
    echo   - Via Nginx: http://localhost
) else if "%choice%"=="3" (
    echo Stopping services...
    docker-compose down
    echo [OK] Services stopped!
) else if "%choice%"=="4" (
    echo Showing logs (Press Ctrl+C to exit)...
    docker-compose logs -f
) else if "%choice%"=="5" (
    echo Cleaning up Docker artifacts...
    docker-compose down -v
    docker system prune -a --force
    echo [OK] Cleanup complete!
) else if "%choice%"=="6" (
    echo Full rebuild and start...
    docker-compose down -v
    docker-compose build --no-cache
    docker-compose up -d
    timeout /t 3 /nobreak
    echo [OK] Full rebuild complete!
    docker-compose ps
) else (
    echo [ERROR] Invalid choice!
    pause
    exit /b 1
)

pause

