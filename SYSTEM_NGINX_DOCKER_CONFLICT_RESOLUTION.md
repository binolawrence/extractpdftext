# System Nginx + Docker Backend - Conflict Resolution Guide

## Problem Identification

There's a conflict between:
1. **System Nginx** (running on your host - port 80)
2. **Docker Nginx** (load balancer - trying to expose port 8081)
3. **Backend Services** (Docker containers - internal port 8080)

---

## 🔍 Conflict Diagnosis

### Most Likely Conflicts

**Conflict 1: Port 80 Already in Use**
```
System Nginx is already listening on port 80
└─ This is CORRECT and EXPECTED ✓
└─ System Nginx should stay on port 80
└─ Serves your React app
```

**Conflict 2: Port 8081 May Not Be Available**
```
Something else might be using port 8081
├─ Previous Docker container
├─ Old service not cleaned up
└─ Other application
```

**Conflict 3: Docker Network/DNS Issues**
```
Docker containers can't communicate properly
├─ Network driver issues
├─ DNS resolution problems
└─ Port mapping conflicts
```

---

## ✅ Correct Architecture

```
SYSTEM:
  Port 80 → System Nginx
           ├─ Serves React app (/var/www/my-vote-ui/dist/)
           └─ Proxies /api/* to localhost:8081

DOCKER:
  Port 8081 → Docker Nginx (load balancer)
             ├─ Routes to app1 (:8080 internal)
             ├─ Routes to app2 (:8080 internal)
             └─ Routes to app3 (:8080 internal)
```

**This architecture should NOT conflict** because:
- ✅ System Nginx on port 80
- ✅ Docker Nginx on port 8081
- ✅ Different ports = no conflict
- ✅ Backend on internal port 8080 (not exposed)

---

## 🔧 Step-by-Step Resolution

### Step 1: Verify System Nginx is Running

```bash
# Check if system nginx is running
sudo systemctl status nginx

# If running, you should see:
# ● nginx.service - A high performance web server and a reverse proxy server
#   Loaded: loaded (/lib/systemd/nginx.service; enabled; vendor preset: enabled)
#   Active: active (running) since ...
```

If nginx is NOT running:
```bash
sudo systemctl start nginx
sudo systemctl enable nginx
```

### Step 2: Verify System Nginx Config is Correct

```bash
# Check nginx syntax
sudo nginx -t

# Should return:
# nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# nginx: configuration file /etc/nginx/nginx.conf test is successful
```

If there are errors, see troubleshooting section below.

### Step 3: Check What's Using Port 8081

```bash
# Find what's using port 8081
sudo lsof -i :8081

# If nothing shows up, port is available ✓
# If something shows up, note the process ID (PID)

# Kill conflicting process if needed:
sudo kill -9 <PID>
```

### Step 4: Stop Docker Services (Clean Start)

```bash
# Go to extractpdftext directory
cd /path/to/extractpdftext

# Stop and remove all Docker containers
docker-compose down -v

# Verify everything stopped
docker-compose ps
# Should return empty
```

### Step 5: Verify Docker is Ready

```bash
# Check Docker daemon is running
docker ps

# Should work without errors
```

### Step 6: Start Docker Services Fresh

```bash
# Start all services
docker-compose up -d

# Wait 10 seconds for startup
sleep 10

# Check status
docker-compose ps

# Expected output:
# NAME                 STATUS              PORTS
# extractpdf-nginx     Up (healthy)        0.0.0.0:8081->80/tcp
# extractpdf-app1      Up (healthy)        
# extractpdf-app2      Up (healthy)        
# extractpdf-app3      Up (healthy)        
```

### Step 7: Test Docker Backend is Accessible

```bash
# Test from host machine
curl http://localhost:8081/health

# Should return health status (200 OK)
```

If this fails, see troubleshooting section.

### Step 8: Verify System Nginx Still Works

```bash
# Check system nginx is still running
sudo systemctl status nginx

# Should show: active (running)

# Test nginx is responding
curl http://localhost/

# Should return React app HTML
```

### Step 9: Update System Nginx Config

**File:** `/etc/nginx/sites-available/my-vote-ui`

Ensure the `/api/` location block has:

```nginx
location /api/ {
    proxy_pass http://localhost:8081/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

### Step 10: Reload System Nginx

```bash
# Test config
sudo nginx -t

# Reload
sudo systemctl reload nginx
```

### Step 11: Test Full Integration

```bash
# Test React app loads
curl http://mycommune.org/

# Test API call through Docker
curl http://mycommune.org/api/pdf/list

# Test direct Docker access
curl http://localhost:8081/api/pdf/list

# All should return valid responses
```

---

## 🛠️ Troubleshooting Specific Conflicts

### Issue 1: "Address already in use" on port 8081

**Problem:** Port 8081 is taken

**Solution A: Find and kill conflicting process**
```bash
sudo lsof -i :8081
sudo kill -9 <PID>
docker-compose up -d
```

**Solution B: Use different port**
```bash
# Edit docker-compose.yml
# Change: ports: - "8081:80"
# To:     ports: - "8082:80"

# Then update system nginx:
# Change: proxy_pass http://localhost:8081/;
# To:     proxy_pass http://localhost:8082/;

# Reload nginx:
sudo systemctl reload nginx
```

### Issue 2: System Nginx Conflicts with Docker

**Problem:** Docker containers can't communicate with host

**Solution:**
```bash
# Check Docker bridge network
docker network ls

# Should show: extractpdf-network (bridge)

# If missing, recreate:
docker-compose down
docker network create extractpdf-network
docker-compose up -d
```

### Issue 3: Nginx Config Syntax Error

**Problem:** `nginx -t` shows errors

**Solution:**
```bash
# Check config syntax
sudo nginx -t

# It will show which line has error
# Example: "/etc/nginx/sites-available/my-vote-ui:25: unexpected ";"

# Fix the error and test again
sudo nano /etc/nginx/sites-available/my-vote-ui
sudo nginx -t
```

### Issue 4: "Connection refused" from System Nginx to Docker

**Problem:** System nginx can't reach localhost:8081

**Solution:**
```bash
# Check Docker is running
docker-compose ps

# Should show all 4 services UP

# Check port 8081 is listening
sudo netstat -tlnp | grep 8081

# Should show: LISTEN on 0.0.0.0:8081

# If not, restart Docker
docker-compose restart nginx
```

### Issue 5: DNS Resolution Issues

**Problem:** Containers can't find each other

**Solution:**
```bash
# Stop everything
docker-compose down

# Remove network
docker network rm extractpdf-network

# Start fresh
docker-compose up -d

# Docker will recreate network
```

---

## 📋 Verification Checklist

After resolution, verify everything works:

```bash
☐ System Nginx running
  sudo systemctl status nginx
  
☐ System Nginx config valid
  sudo nginx -t (returns OK)

☐ Docker services running
  docker-compose ps (shows 4 UP)

☐ Port 8081 available
  sudo lsof -i :8081 (shows Docker Nginx)

☐ Docker backend responds
  curl http://localhost:8081/health (200 OK)

☐ System Nginx responds
  curl http://localhost/ (returns HTML)

☐ React app loads
  curl http://mycommune.org/ (returns HTML)

☐ API calls work through System Nginx
  curl http://mycommune.org/api/pdf/list (returns JSON)

☐ Direct Docker API calls work
  curl http://localhost:8081/api/pdf/list (returns JSON)
```

---

## 🚨 Emergency Rollback

If everything breaks, return to single instance:

```bash
# Stop Docker
docker-compose down

# Update System Nginx to use single instance
# Edit: /etc/nginx/sites-available/my-vote-ui
# Change: proxy_pass http://localhost:8081/;
# Back to: proxy_pass http://localhost:8080/;

# Reload System Nginx
sudo systemctl reload nginx

# Start single Spring Boot on port 8080 (if available)
# Or run your original setup
```

---

## 🎯 Port Mapping Summary

| Service | Port | Host/Docker | Purpose |
|---------|------|------------|---------|
| System Nginx | 80 | Host | Serves React + proxies API |
| Docker Nginx | 8081 | Host (exposed) | Load balancer |
| Docker Nginx | 80 | Docker internal | Receives from Docker port 8081 |
| Spring Boot x3 | 8080 | Docker internal | Backend apps |

---

## 💡 Best Practices

1. **Always check nginx syntax before reloading**
   ```bash
   sudo nginx -t
   ```

2. **Use different ports to avoid conflicts**
   - System: 80, 443
   - Docker external: 8081, 8082, 8083
   - Docker internal: 8080

3. **Clean start when troubleshooting**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

4. **Monitor logs**
   ```bash
   docker-compose logs -f
   tail -f /var/log/nginx/error.log
   ```

---

## 📞 Need More Help?

Check these files for additional information:
- MY_VOTE_UI_QUICK_SETUP.md
- MY_VOTE_UI_DOCKER_INTEGRATION.md
- MY_VOTE_UI_ARCHITECTURE_GUIDE.md
- DOCKER_COMPOSE_VERIFICATION_REPORT.md

---

## ✅ Expected Result After Resolution

```
Users access: http://mycommune.org (System Nginx Port 80)
         ↓
React app loads from: /var/www/my-vote-ui/dist/
         ↓
React makes API call to: /api/pdf/search
         ↓
System Nginx routes to: localhost:8081 (Docker Nginx)
         ↓
Docker Nginx load balances to: app1, app2, or app3
         ↓
Backend processes request and returns response
         ↓
React updates UI
         ↓
User sees results ✓
```

---

**Status:** Ready to troubleshoot and resolve nginx conflicts
**Next Step:** Run diagnostic commands above to identify specific conflict

