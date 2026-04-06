#!/bin/bash
# Docker Compose Quick Start Script for extractpdftext

set -e

echo "==================================================================="
echo "Extract PDF Text - Docker Setup"
echo "==================================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed!${NC}"
    echo "Please install Docker Desktop from https://www.docker.com/products/docker-desktop"
    exit 1
fi

echo -e "${GREEN}✅ Docker found${NC}"
docker --version

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker Compose found${NC}"
docker-compose --version
echo ""

# Menu
echo "Choose an action:"
echo "1) Build images"
echo "2) Start services (docker-compose up)"
echo "3) Stop services (docker-compose down)"
echo "4) View logs"
echo "5) Clean up (remove containers, images, volumes)"
echo "6) Full rebuild and start"
echo ""

read -p "Enter choice (1-6): " choice

case $choice in
    1)
        echo -e "${YELLOW}Building Docker images...${NC}"
        docker-compose build
        echo -e "${GREEN}✅ Build complete!${NC}"
        ;;
    2)
        echo -e "${YELLOW}Starting services...${NC}"
        docker-compose up -d
        sleep 3
        echo -e "${GREEN}✅ Services started!${NC}"
        docker-compose ps
        echo ""
        echo "Access your application at:"
        echo "  - Frontend: http://localhost:3000"
        echo "  - Backend: http://localhost:8080"
        echo "  - Via Nginx: http://localhost"
        ;;
    3)
        echo -e "${YELLOW}Stopping services...${NC}"
        docker-compose down
        echo -e "${GREEN}✅ Services stopped!${NC}"
        ;;
    4)
        echo -e "${YELLOW}Showing logs (Press Ctrl+C to exit)...${NC}"
        docker-compose logs -f
        ;;
    5)
        echo -e "${YELLOW}Cleaning up Docker artifacts...${NC}"
        docker-compose down -v
        docker system prune -a --force
        echo -e "${GREEN}✅ Cleanup complete!${NC}"
        ;;
    6)
        echo -e "${YELLOW}Full rebuild and start...${NC}"
        docker-compose down -v
        docker-compose build --no-cache
        docker-compose up -d
        sleep 3
        echo -e "${GREEN}✅ Full rebuild complete!${NC}"
        docker-compose ps
        ;;
    *)
        echo -e "${RED}❌ Invalid choice!${NC}"
        exit 1
        ;;
esac

