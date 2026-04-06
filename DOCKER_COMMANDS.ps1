# Docker Compose Quick Commands for extractpdftext Project
# Run these commands from project root

# ============================================================================
# BUILDING & STARTING
# ============================================================================

# Build all Docker images
docker-compose build

# Build specific service (no cache)
docker-compose build --no-cache backend

# Start all services in background
docker-compose up -d

# Start services and show logs
docker-compose up

# ============================================================================
# MONITORING
# ============================================================================

# Check running services status
docker-compose ps

# View logs from all services
docker-compose logs

# View logs from specific service (follow in real-time)
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f nginx

# View last 50 lines of logs
docker-compose logs --tail=50

# ============================================================================
# STOPPING & CLEANUP
# ============================================================================

# Stop all running services
docker-compose stop

# Stop specific service
docker-compose stop backend

# Stop and remove containers
docker-compose down

# Stop, remove containers, and remove volumes
docker-compose down -v

# Remove all stopped containers
docker container prune

# Remove all unused images
docker image prune -a

# Remove all unused volumes
docker volume prune

# ============================================================================
# DEBUGGING
# ============================================================================

# Execute command in running container
docker-compose exec backend bash
docker-compose exec frontend sh
docker-compose exec nginx sh

# Check network connectivity
docker-compose exec backend ping frontend
docker-compose exec frontend curl http://backend:8080

# View environment variables in container
docker-compose exec backend env | findstr "SPRING"

# ============================================================================
# DEVELOPMENT WORKFLOW
# ============================================================================

# Rebuild and restart after code changes
docker-compose down
docker-compose build --no-cache backend
docker-compose up -d

# Rebuild only frontend
docker-compose build frontend
docker-compose up -d frontend

# View real-time logs during development
docker-compose logs -f backend frontend

# ============================================================================
# HEALTH CHECKS
# ============================================================================

# Test backend health
docker-compose exec backend curl http://localhost:8080/actuator/health

# Test frontend
docker-compose exec frontend curl http://localhost:3000

# Test through Nginx
curl http://localhost

# ============================================================================
# DOCKER INFO
# ============================================================================

# List all Docker volumes
docker volume ls

# Inspect specific volume
docker volume inspect extractpdftext_pdf-storage

# View images
docker images | findstr extractpdftext

# View all containers (running and stopped)
docker ps -a

# View Docker system information
docker system df

# ============================================================================
# ADVANCED OPERATIONS
# ============================================================================

# Push to Docker Hub
docker login
docker tag extractpdftext-backend:latest yourusername/extractpdftext-backend:latest
docker push yourusername/extractpdftext-backend:latest

# Force recreate containers
docker-compose up -d --force-recreate

# Rebuild and restart
docker-compose up -d --build

# Scale service (multiple instances)
docker-compose up -d --scale backend=2

# ============================================================================
# USEFUL ALIASES (Add to PowerShell Profile)
# ============================================================================

# Add to C:\Users\bfrancis\Documents\PowerShell\profile.ps1
# Set-Alias dcup 'docker-compose up -d'
# Set-Alias dcdown 'docker-compose down'
# Set-Alias dclogs 'docker-compose logs -f'
# Set-Alias dcps 'docker-compose ps'

