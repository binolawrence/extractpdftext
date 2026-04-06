# Docker Compose Quick Start Script for extractpdftext (PowerShell)

$ErrorActionPreference = "Stop"

Write-Host "===================================================================" -ForegroundColor Green
Write-Host "Extract PDF Text - Docker Setup" -ForegroundColor Green
Write-Host "===================================================================" -ForegroundColor Green
Write-Host ""

# Check if Docker is installed
try {
    $dockerVersion = docker --version
    Write-Host "[OK] Docker found: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker is not installed!" -ForegroundColor Red
    Write-Host "Please install Docker Desktop from https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

# Check if Docker Compose is installed
try {
    $composeVersion = docker-compose --version
    Write-Host "[OK] Docker Compose found: $composeVersion" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker Compose is not installed!" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Menu
Write-Host "Choose an action:" -ForegroundColor Cyan
Write-Host "1) Build images"
Write-Host "2) Start services (docker-compose up)"
Write-Host "3) Stop services (docker-compose down)"
Write-Host "4) View logs"
Write-Host "5) Clean up (remove containers, images, volumes)"
Write-Host "6) Full rebuild and start"
Write-Host ""

$choice = Read-Host "Enter choice (1-6)"

switch ($choice) {
    "1" {
        Write-Host "Building Docker images..." -ForegroundColor Yellow
        docker-compose build
        Write-Host "[OK] Build complete!" -ForegroundColor Green
    }
    "2" {
        Write-Host "Starting services..." -ForegroundColor Yellow
        docker-compose up -d
        Start-Sleep -Seconds 3
        Write-Host "[OK] Services started!" -ForegroundColor Green
        docker-compose ps
        Write-Host ""
        Write-Host "Access your application at:" -ForegroundColor Cyan
        Write-Host "  - Frontend: http://localhost:3000"
        Write-Host "  - Backend: http://localhost:8080"
        Write-Host "  - Via Nginx: http://localhost"
    }
    "3" {
        Write-Host "Stopping services..." -ForegroundColor Yellow
        docker-compose down
        Write-Host "[OK] Services stopped!" -ForegroundColor Green
    }
    "4" {
        Write-Host "Showing logs (Press Ctrl+C to exit)..." -ForegroundColor Yellow
        docker-compose logs -f
    }
    "5" {
        Write-Host "Cleaning up Docker artifacts..." -ForegroundColor Yellow
        docker-compose down -v
        docker system prune -a --force
        Write-Host "[OK] Cleanup complete!" -ForegroundColor Green
    }
    "6" {
        Write-Host "Full rebuild and start..." -ForegroundColor Yellow
        docker-compose down -v
        docker-compose build --no-cache
        docker-compose up -d
        Start-Sleep -Seconds 3
        Write-Host "[OK] Full rebuild complete!" -ForegroundColor Green
        docker-compose ps
    }
    default {
        Write-Host "[ERROR] Invalid choice!" -ForegroundColor Red
        exit 1
    }
}

