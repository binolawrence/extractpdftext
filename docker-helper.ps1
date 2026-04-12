# Docker 3 Instances - Helper Script for Windows PowerShell
# Usage: .\docker-helper.ps1 -Command start

param(
    [Parameter(Mandatory=$true, Position=0)]
    [ValidateSet('start', 'stop', 'status', 'logs', 'logs-app1', 'logs-app2', 'logs-app3', 'logs-nginx',
                 'restart', 'build', 'health', 'stats', 'clean', 'shell', 'test', 'help')]
    [string]$Command,

    [Parameter(Mandatory=$false, Position=1)]
    [string]$Container = 'app1'
)

function Write-Header {
    param([string]$Message)
    Write-Host ""
    Write-Host "[+] $Message" -ForegroundColor Cyan
    Write-Host ""
}

function Write-Success {
    param([string]$Message)
    Write-Host "[✓] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[✗] $Message" -ForegroundColor Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[!] $Message" -ForegroundColor Yellow
}

function Test-DockerCompose {
    try {
        $null = docker-compose --version 2>$null
        return $true
    } catch {
        return $false
    }
}

function Cmd-Start {
    Write-Header "Starting all services..."

    if (-not (Test-DockerCompose)) {
        Write-Error "Docker Compose is not installed or not in PATH"
        exit 1
    }

    docker-compose up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Services started successfully"
        Write-Host "Access application at: http://localhost" -ForegroundColor Cyan
        Write-Host ""
        Start-Sleep -Seconds 3
        docker-compose ps
    } else {
        Write-Error "Failed to start services"
        exit 1
    }
}

function Cmd-Stop {
    Write-Header "Stopping all services..."
    docker-compose down

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Services stopped successfully"
    } else {
        Write-Error "Failed to stop services"
        exit 1
    }
}

function Cmd-Status {
    Write-Header "Service status"
    docker-compose ps
}

function Cmd-Logs {
    Write-Header "Displaying logs (press Ctrl+C to stop)"
    docker-compose logs -f
}

function Cmd-LogsApp1 {
    Write-Header "Displaying app1 logs (press Ctrl+C to stop)"
    docker-compose logs -f app1
}

function Cmd-LogsApp2 {
    Write-Header "Displaying app2 logs (press Ctrl+C to stop)"
    docker-compose logs -f app2
}

function Cmd-LogsApp3 {
    Write-Header "Displaying app3 logs (press Ctrl+C to stop)"
    docker-compose logs -f app3
}

function Cmd-LogsNginx {
    Write-Header "Displaying nginx logs (press Ctrl+C to stop)"
    docker-compose logs -f nginx
}

function Cmd-Restart {
    Write-Header "Restarting all services..."
    docker-compose restart

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Services restarted successfully"
        Start-Sleep -Seconds 2
        docker-compose ps
    } else {
        Write-Error "Failed to restart services"
        exit 1
    }
}

function Cmd-Build {
    Write-Header "Building Docker image..."
    docker-compose build

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Build successful"
        Write-Header "Starting services..."
        docker-compose up -d
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Services started"
        } else {
            Write-Error "Failed to start services"
            exit 1
        }
    } else {
        Write-Error "Build failed"
        exit 1
    }
}

function Cmd-Health {
    Write-Header "Checking application health..."

    $maxAttempts = 5
    for ($i = 1; $i -le $maxAttempts; $i++) {
        Write-Host "[Attempt $i/$maxAttempts] Testing http://localhost/health" -ForegroundColor Blue
        try {
            $response = Invoke-WebRequest -Uri "http://localhost/health" -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                Write-Host ""
                Write-Success "Application is healthy"
                $response.Content | ConvertFrom-Json | Format-List
                return
            }
        } catch {
            # Continue to next attempt
        }
        Start-Sleep -Seconds 1
    }

    Write-Host ""
    Write-Error "Application health check failed"
    exit 1
}

function Cmd-Stats {
    Write-Header "Monitoring resource usage (press Ctrl+C to stop)"
    Write-Host ""

    while ($true) {
        Clear-Host
        Write-Host "[Docker Statistics - Press Ctrl+C to exit]" -ForegroundColor Cyan
        Write-Host ""
        docker stats --no-stream
        Start-Sleep -Seconds 2
    }
}

function Cmd-Clean {
    Write-Header "Cleanup - Removing containers and volumes"
    Write-Warning "This will remove ALL containers and volumes!"
    Write-Host ""

    $confirm = Read-Host "Continue? (y/N)"

    if ($confirm -eq "y" -or $confirm -eq "Y") {
        Write-Header "Cleaning up Docker resources..."
        docker-compose down -v

        if ($LASTEXITCODE -eq 0) {
            Write-Success "Cleanup successful"
        } else {
            Write-Error "Cleanup failed"
            exit 1
        }
    } else {
        Write-Success "Cleanup cancelled"
    }
}

function Cmd-Shell {
    Write-Header "Opening shell in $Container..."
    docker-compose exec $Container /bin/sh
}

function Cmd-Test {
    Write-Header "Running integration tests..."

    # Check if services are running
    $running = docker-compose ps | Select-String "extractpdf-app1"
    if (-not $running) {
        Write-Error "Services not running. Start with: .\docker-helper.ps1 -Command start"
        exit 1
    }

    Write-Host "[1/5] Testing load balancer (http://localhost)..." -ForegroundColor Blue
    try {
        $response = Invoke-WebRequest -Uri "http://localhost" -UseBasicParsing -TimeoutSec 5
        Write-Host "HTTP Status: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Error "Failed to connect to load balancer"
        exit 1
    }

    Write-Host "[2/5] Testing App health endpoint..." -ForegroundColor Blue
    try {
        $response = Invoke-WebRequest -Uri "http://localhost/health" -UseBasicParsing -TimeoutSec 5
        Write-Host "HTTP Status: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Error "Health check failed"
        exit 1
    }

    Write-Host "[3/5] Checking container status..." -ForegroundColor Blue
    $ps = docker-compose ps
    Write-Host $ps -ForegroundColor Green

    Write-Host "[4/5] Checking Docker volumes..." -ForegroundColor Blue
    docker volume ls | Select-String "extractpdftext" | ForEach-Object { Write-Host $_ -ForegroundColor Green }

    Write-Host "[5/5] Checking logs for errors..." -ForegroundColor Blue
    $errors = docker-compose logs | Select-String "ERROR"
    if ($errors) {
        Write-Warning "Errors found in logs:"
        $errors | ForEach-Object { Write-Host $_ -ForegroundColor Yellow }
        Write-Host "(Check with: .\docker-helper.ps1 -Command logs)" -ForegroundColor Yellow
    } else {
        Write-Success "No errors in logs"
    }

    Write-Host ""
    Write-Success "All tests completed!"
}

function Show-Help {
    $help = @"

Docker 3 Instances Helper Script for Windows PowerShell
========================================================

Usage: .\docker-helper.ps1 -Command <command> [-Container <name>]

Commands:
  start           - Start all services (app1, app2, app3, nginx)
  stop            - Stop all services
  restart         - Restart all services
  status          - Show service status
  logs            - View all logs (Ctrl+C to exit)
  logs-app1       - View app1 logs
  logs-app2       - View app2 logs
  logs-app3       - View app3 logs
  logs-nginx      - View nginx logs
  build           - Rebuild Docker image and restart
  health          - Check if application is healthy
  stats           - Monitor CPU/Memory usage (Ctrl+C to exit)
  shell           - Open shell in container (default: app1)
  test            - Run integration tests
  clean           - Remove all containers and volumes
  help            - Show this help message

Parameters:
  -Command        - Command to execute (required)
  -Container      - Container name for shell command (default: app1)

Examples:
  .\docker-helper.ps1 -Command start
  .\docker-helper.ps1 -Command logs-app1
  .\docker-helper.ps1 -Command shell -Container app2
  .\docker-helper.ps1 -Command test
  .\docker-helper.ps1 -Command stats

Quick Start:
  .\docker-helper.ps1 -Command start       (starts all services)
  .\docker-helper.ps1 -Command health      (verifies services are working)
  .\docker-helper.ps1 -Command test        (runs integration tests)

Access the application at: http://localhost

"@
    Write-Host $help
}

# Main execution
switch ($Command) {
    'start'         { Cmd-Start }
    'stop'          { Cmd-Stop }
    'status'        { Cmd-Status }
    'logs'          { Cmd-Logs }
    'logs-app1'     { Cmd-LogsApp1 }
    'logs-app2'     { Cmd-LogsApp2 }
    'logs-app3'     { Cmd-LogsApp3 }
    'logs-nginx'    { Cmd-LogsNginx }
    'restart'       { Cmd-Restart }
    'build'         { Cmd-Build }
    'health'        { Cmd-Health }
    'stats'         { Cmd-Stats }
    'clean'         { Cmd-Clean }
    'shell'         { Cmd-Shell }
    'test'          { Cmd-Test }
    'help'          { Show-Help }
    default         { Show-Help }
}

