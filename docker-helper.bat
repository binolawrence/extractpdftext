@echo off
REM Docker 3 Instances - Helper Script for Windows
REM Usage: docker-helper.bat [command]

setlocal enabledelayedexpansion

if "%~1"=="" goto show_help

REM Convert input to lowercase
set "command=%~1"
for %%A in (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) do set "command=!command:%%A=%%a!"

REM Parse commands
if "!command!"=="start" goto cmd_start
if "!command!"=="stop" goto cmd_stop
if "!command!"=="status" goto cmd_status
if "!command!"=="logs" goto cmd_logs
if "!command!"=="logs-app1" goto cmd_logs_app1
if "!command!"=="logs-app2" goto cmd_logs_app2
if "!command!"=="logs-app3" goto cmd_logs_app3
if "!command!"=="logs-nginx" goto cmd_logs_nginx
if "!command!"=="restart" goto cmd_restart
if "!command!"=="build" goto cmd_build
if "!command!"=="health" goto cmd_health
if "!command!"=="stats" goto cmd_stats
if "!command!"=="clean" goto cmd_clean
if "!command!"=="shell" goto cmd_shell
if "!command!"=="test" goto cmd_test
if "!command!"=="help" goto show_help

echo Unknown command: %command%
goto show_help

:cmd_start
echo.
echo [+] Starting all services...
docker-compose up -d
if %errorlevel% equ 0 (
    echo [✓] Services started successfully
    echo.
    echo Access application at: http://localhost
    timeout /t 3 /nobreak
    docker-compose ps
) else (
    echo [✗] Failed to start services
    exit /b 1
)
goto :eof

:cmd_stop
echo.
echo [+] Stopping all services...
docker-compose down
if %errorlevel% equ 0 (
    echo [✓] Services stopped successfully
) else (
    echo [✗] Failed to stop services
    exit /b 1
)
goto :eof

:cmd_status
echo.
echo [+] Checking service status...
docker-compose ps
goto :eof

:cmd_logs
echo.
echo [+] Displaying logs (press Ctrl+C to stop)...
docker-compose logs -f
goto :eof

:cmd_logs_app1
echo.
echo [+] Displaying app1 logs (press Ctrl+C to stop)...
docker-compose logs -f app1
goto :eof

:cmd_logs_app2
echo.
echo [+] Displaying app2 logs (press Ctrl+C to stop)...
docker-compose logs -f app2
goto :eof

:cmd_logs_app3
echo.
echo [+] Displaying app3 logs (press Ctrl+C to stop)...
docker-compose logs -f app3
goto :eof

:cmd_logs_nginx
echo.
echo [+] Displaying nginx logs (press Ctrl+C to stop)...
docker-compose logs -f nginx
goto :eof

:cmd_restart
echo.
echo [+] Restarting all services...
docker-compose restart
if %errorlevel% equ 0 (
    echo [✓] Services restarted successfully
    timeout /t 2 /nobreak
    docker-compose ps
) else (
    echo [✗] Failed to restart services
    exit /b 1
)
goto :eof

:cmd_build
echo.
echo [+] Building Docker image...
docker-compose build
if %errorlevel% equ 0 (
    echo [✓] Build successful
    echo [+] Starting services...
    docker-compose up -d
) else (
    echo [✗] Build failed
    exit /b 1
)
goto :eof

:cmd_health
echo.
echo [+] Checking application health...
echo.
timeout /t 2 /nobreak
for /l %%i in (1,1,5) do (
    echo [Attempt %%i] Testing http://localhost/health
    curl -s http://localhost/health
    if !errorlevel! equ 0 (
        echo.
        echo [✓] Application is healthy
        goto :eof
    )
    timeout /t 1 /nobreak
)
echo.
echo [✗] Application health check failed
exit /b 1
goto :eof

:cmd_stats
echo.
echo [+] Monitoring resource usage (press Ctrl+C to stop)...
echo.
:stats_loop
cls
echo [Docker Statistics - Press Ctrl+C to exit]
echo.
docker stats --no-stream
timeout /t 2 /nobreak
goto stats_loop
goto :eof

:cmd_clean
echo.
echo [!] WARNING: This will remove all containers and volumes!
echo.
set /p confirm="Continue? (y/N): "
if /i "!confirm!"=="y" (
    echo.
    echo [+] Cleaning up Docker resources...
    docker-compose down -v
    if %errorlevel% equ 0 (
        echo [✓] Cleanup successful
    ) else (
        echo [✗] Cleanup failed
        exit /b 1
    )
) else (
    echo [✓] Cleanup cancelled
)
goto :eof

:cmd_shell
set "container=%~2"
if "!container!"=="" (
    set "container=app1"
)
echo.
echo [+] Opening shell in !container!...
docker-compose exec !container! /bin/sh
goto :eof

:cmd_test
echo.
echo [+] Running integration tests...
echo.

REM Check if services are running
docker-compose ps | findstr "extractpdf-app1" >nul
if %errorlevel% neq 0 (
    echo [✗] Services not running. Start with: docker-helper.bat start
    exit /b 1
)

echo [1/5] Testing load balancer (http://localhost)...
curl -s -o /dev/null -w "HTTP Status: %%{http_code}\n" http://localhost
if %errorlevel% neq 0 (
    echo [✗] Failed to connect to load balancer
    exit /b 1
)

echo [2/5] Testing App1 health...
curl -s -o /dev/null -w "HTTP Status: %%{http_code}\n" http://localhost/health
if %errorlevel% neq 0 (
    echo [✗] App1 health check failed
    exit /b 1
)

echo [3/5] Testing instance naming...
for /l %%i in (1,1,3) do (
    curl -s http://localhost/health | findstr "app%%i" >nul
    if !errorlevel! equ 0 (
        echo [✓] App%%i responding
    ) else (
        echo [?] App%%i status unknown
    )
)

echo [4/5] Checking Docker volumes...
docker volume ls | findstr "extractpdftext"

echo [5/5] Checking logs for errors...
docker-compose logs | findstr "ERROR" >nul
if %errorlevel% equ 0 (
    echo [!] WARNING: Errors found in logs. Check with: docker-helper.bat logs
) else (
    echo [✓] No errors in logs
)

echo.
echo [✓] All tests completed!
goto :eof

:show_help
echo.
echo Docker 3 Instances Helper Script for Windows
echo =============================================
echo.
echo Usage: docker-helper.bat [command]
echo.
echo Commands:
echo   start           - Start all services (app1, app2, app3, nginx)
echo   stop            - Stop all services
echo   restart         - Restart all services
echo   status          - Show service status
echo   logs            - View all logs (Ctrl+C to exit)
echo   logs-app1       - View app1 logs
echo   logs-app2       - View app2 logs
echo   logs-app3       - View app3 logs
echo   logs-nginx      - View nginx logs
echo   build           - Rebuild Docker image and restart
echo   health          - Check if application is healthy
echo   stats           - Monitor CPU/Memory usage (Ctrl+C to exit)
echo   shell [app]     - Open shell in container (default: app1)
echo   test            - Run integration tests
echo   clean           - Remove all containers and volumes
echo   help            - Show this help message
echo.
echo Examples:
echo   docker-helper.bat start
echo   docker-helper.bat logs-app1
echo   docker-helper.bat shell app2
echo   docker-helper.bat test
echo.
echo Quick Start:
echo   docker-helper.bat start       (starts all services)
echo   docker-helper.bat health      (verifies services are working)
echo   docker-helper.bat test        (runs integration tests)
echo.
echo Access the application at: http://localhost
echo.
goto :eof

