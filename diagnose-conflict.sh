#!/bin/bash
# System Nginx + Docker Backend - Diagnostic Script
# Usage: bash diagnose-conflict.sh

echo "═══════════════════════════════════════════════════════════════"
echo "       System Nginx + Docker Backend - Diagnostics"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check 1: System Nginx Status
echo "[1/10] Checking System Nginx Status..."
if sudo systemctl status nginx > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} System Nginx is running"
else
    echo -e "${RED}✗${NC} System Nginx is NOT running"
    echo "    Fix: sudo systemctl start nginx"
fi
echo ""

# Check 2: Nginx Config Syntax
echo "[2/10] Checking Nginx Config Syntax..."
if sudo nginx -t > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Nginx config syntax is OK"
else
    echo -e "${RED}✗${NC} Nginx config has syntax errors"
    echo "    Run: sudo nginx -t (to see details)"
fi
echo ""

# Check 3: Port 80 Usage
echo "[3/10] Checking Port 80 (System Nginx)..."
if sudo lsof -i :80 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Port 80 is in use (expected)"
    sudo lsof -i :80 | grep -v COMMAND
else
    echo -e "${YELLOW}!${NC} Port 80 is NOT in use (System Nginx not listening)"
fi
echo ""

# Check 4: Port 8081 Usage
echo "[4/10] Checking Port 8081 (Docker Nginx)..."
if sudo lsof -i :8081 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Port 8081 is in use (Docker Nginx)"
    sudo lsof -i :8081 | grep -v COMMAND
else
    echo -e "${YELLOW}!${NC} Port 8081 is NOT in use (Docker Nginx not running)"
fi
echo ""

# Check 5: Docker Status
echo "[5/10] Checking Docker Service..."
if docker ps > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Docker is running"
else
    echo -e "${RED}✗${NC} Docker is NOT running"
    echo "    Fix: sudo systemctl start docker"
fi
echo ""

# Check 6: Docker Containers Status
echo "[6/10] Checking Docker Containers..."
if [ $(docker ps -q | wc -l) -eq 0 ]; then
    echo -e "${RED}✗${NC} No Docker containers running"
    echo "    Fix: cd /path/to/extractpdftext && docker-compose up -d"
else
    echo -e "${GREEN}✓${NC} Docker containers running:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
fi
echo ""

# Check 7: Docker Network
echo "[7/10] Checking Docker Network..."
if docker network inspect extractpdf-network > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Docker network 'extractpdf-network' exists"
else
    echo -e "${RED}✗${NC} Docker network 'extractpdf-network' NOT found"
    echo "    Fix: docker-compose up -d (will create network)"
fi
echo ""

# Check 8: Test Docker Backend
echo "[8/10] Testing Docker Backend (localhost:8081)..."
if curl -s http://localhost:8081/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Docker backend is responding"
    echo "    Response: $(curl -s http://localhost:8081/health)"
else
    echo -e "${RED}✗${NC} Docker backend is NOT responding"
    echo "    Fix: Check docker-compose logs -f"
fi
echo ""

# Check 9: Test System Nginx
echo "[9/10] Testing System Nginx (localhost:80)..."
if curl -s http://localhost/ > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} System Nginx is responding"
else
    echo -e "${RED}✗${NC} System Nginx is NOT responding"
    echo "    Fix: sudo systemctl restart nginx"
fi
echo ""

# Check 10: Test Full Integration
echo "[10/10] Testing Full Integration (localhost/api/pdf/list)..."
if curl -s http://localhost/api/pdf/list > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Full integration is working"
    echo "    API is responding through system nginx to Docker backend"
else
    echo -e "${RED}✗${NC} Full integration is NOT working"
    echo "    Fix: Check all checks above"
fi
echo ""

echo "═══════════════════════════════════════════════════════════════"
echo "                    Diagnostics Complete"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Summary:"
echo "  • If all checks show ✓ (green) = Everything is working"
echo "  • If any check shows ✗ (red) = Fix the issue (see suggestions)"
echo "  • If any check shows ! (yellow) = May be issue, check"
echo ""
echo "Next Steps:"
echo "  1. Fix any ✗ (red) errors first"
echo "  2. Fix any ! (yellow) warnings"
echo "  3. Run this script again to verify fixes"
echo ""

