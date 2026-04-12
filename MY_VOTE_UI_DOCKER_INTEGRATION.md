# React App + Docker Backend Integration Guide

## Architecture Overview

Your new setup will have this architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                   Internet Users                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                ┌────────▼─────────┐
                │ Your Host System  │ (Port 80 - External Nginx)
                │ /etc/nginx/       │
                └────────┬─────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
    React App    API Calls to   Static Assets
   (Served from   localhost:8081
    /var/www/)    (Docker nginx
                   load balancer)
         │               │
         └───────────────┼───────────────┘
                         │
         ┌───────────────▼───────────────┐
         │  Docker Container Network      │
         │  (localhost:8081)              │
         │                                │
         │   Nginx Load Balancer          │
         │   (least-connections)          │
         │                                │
         │   ┌────────────────────────┐   │
         │   │ Backend Instances:      │   │
         │   │ • App1 (:8080)         │   │
         │   │ • App2 (:8080)         │   │
         │   │ • App3 (:8080)         │   │
         │   └────────────────────────┘   │
         └────────────────────────────────┘
```

## Data Flow

### User Request for React App
```
1. Browser: GET http://mycommune.org/
2. External Nginx: Serves from /var/www/my-vote-ui/dist/
3. Browser: Receives React app HTML + JavaScript
4. React: Loads and ready to handle user interactions
```

### API Call from React App
```
1. React Component: fetch('/api/pdf/search?query=xyz')
2. External Nginx: Receives at localhost:80/api/
3. External Nginx: Forwards to localhost:8081/
   (via proxy_pass http://localhost:8081/)
4. Docker Nginx: Receives at port 8081
5. Docker Nginx: Applies load balancing (least-connections)
6. Docker Nginx: Routes to App1, App2, or App3
7. Backend App: Processes request
8. Response: Flows back through nginx to React app
9. React: Updates UI with results
```

---

## Setup Instructions

### Step 1: Start Docker Backend

```bash
cd /path/to/extractpdftext
docker-compose up -d
```

**Verify:**
```bash
docker-compose ps
```

Expected output:
```
NAME                 STATUS
extractpdf-nginx     Up (healthy)    0.0.0.0:8081->80/tcp
extractpdf-app1      Up (healthy)
extractpdf-app2      Up (healthy)
extractpdf-app3      Up (healthy)
```

### Step 2: Update Your Nginx Configuration

**File:** `/etc/nginx/sites-available/my-vote-ui`

Replace your current configuration with:

```nginx
server {
    listen 80;
    server_name mycommune.org www.mycommune.org;
    
    root /var/www/my-vote-ui/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    location /api/ {
        proxy_pass http://localhost:8081/;  # ✅ Changed from 8080 to 8081
        
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $server_name;
        
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
        
        proxy_buffering on;
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    error_page 404 /index.html;
}
```

**Key Change:**
- ❌ BEFORE: `proxy_pass http://localhost:8080/;`
- ✅ AFTER: `proxy_pass http://localhost:8081/;`

### Step 3: Test Nginx Configuration

```bash
# Check syntax
sudo nginx -t

# Output should be:
# nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
# nginx: configuration file /etc/nginx/nginx.conf test is successful
```

### Step 4: Reload Nginx

```bash
sudo systemctl reload nginx

# Or if using service:
sudo service nginx reload
```

### Step 5: Verify Everything Works

**Test 1: React App Loads**
```bash
curl http://mycommune.org/
# Should return HTML of your React app
```

**Test 2: Health Check from Docker**
```bash
curl http://localhost:8081/health
# Should return health status from backend
```

**Test 3: API Call**
```bash
curl http://localhost:8081/api/pdf/list
# Should return list of PDFs
```

**Test 4: API Call through External Nginx**
```bash
curl http://mycommune.org/api/pdf/list
# Should return same as above
```

**Test 5: From Browser**
1. Open: `http://mycommune.org/`
2. Open DevTools (F12)
3. Go to Network tab
4. Try a search or API action
5. Should see API calls going to `/api/...`
6. Status should be 200 (success)

---

## What's Happening Now

### Before (Single Instance)
```
React App → localhost:8080 → Single Spring Boot Instance
```
**Problem:** Single point of failure, limited scalability

### After (Load Balanced)
```
React App → localhost:8081 → Docker Nginx → 3 Backend Instances
            (External)      (Load Balanced)  (High Availability)
```
**Benefits:**
- ✅ Load distributed across 3 instances
- ✅ If one instance fails, others handle requests
- ✅ Better performance and reliability
- ✅ Easy to scale (add more instances)

---

## Port Explanation

| Port | Service | Purpose | Exposed |
|------|---------|---------|---------|
| **80** | External Nginx | Serves React app, proxies API | ✅ External (Internet) |
| **8081** | Docker Nginx | Load balances to 3 backends | ✅ Host (localhost) |
| **8080** | Spring Boot x3 | Backend instances | ❌ Internal (Docker only) |

---

## Testing Load Balancing

### Verify Requests are Distributed

**Method 1: Check Docker Logs**
```bash
# Watch as requests come in
docker-compose logs -f app1 app2 app3

# Make API calls and see which instance handles each request
curl http://localhost:8081/api/pdf/list
curl http://localhost:8081/api/pdf/search?query=test
```

**Method 2: Check Nginx Logs**
```bash
# See how nginx is routing requests
docker-compose logs nginx

# Look for lines like:
# "upstream: ..." showing which backend was selected
```

**Method 3: Add Custom Header**
Modify your React app to add instance tracking:
```javascript
fetch('/api/pdf/list')
  .then(r => r.headers.get('X-Served-By'))  // Gets instance name
  .then(instance => console.log('Served by:', instance))
```

---

## Troubleshooting

### Issue 1: "Connection Refused" on localhost:8081

**Cause:** Docker containers not running

**Solution:**
```bash
# Check if Docker is running
docker-compose ps

# If not running:
cd /path/to/extractpdftext
docker-compose up -d

# Verify:
docker-compose logs app1
```

### Issue 2: React App Loads but API Calls Fail

**Cause:** Nginx configuration not reloaded, or Docker port not mapped

**Solution:**
```bash
# 1. Verify port mapping
docker-compose ps
# Should show: 0.0.0.0:8081->80/tcp

# 2. Test direct connection
curl http://localhost:8081/api/pdf/list

# 3. If that works, reload external nginx
sudo nginx -t
sudo systemctl reload nginx

# 4. Try again
curl http://mycommune.org/api/pdf/list
```

### Issue 3: Docker Containers Keep Crashing

**Cause:** Insufficient resources or configuration error

**Solution:**
```bash
# Check logs
docker-compose logs app1 app2 app3

# Check resources
docker stats

# Restart
docker-compose restart
```

### Issue 4: Nginx "502 Bad Gateway" Error

**Cause:** Docker nginx not responding or connection timeout

**Solution:**
```bash
# Increase timeouts in nginx config:
location /api/ {
    proxy_connect_timeout 60s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;
    # ...
}

# Then reload:
sudo nginx -t
sudo systemctl reload nginx
```

---

## Performance Monitoring

### Monitor Backend Load

```bash
# See request distribution
docker-compose logs nginx | grep "upstream:"

# Monitor resources
docker stats

# Check health of each instance
curl http://localhost:8081/api/pdf/list
```

### Monitor Nginx (External)

```bash
# Check external nginx logs
tail -f /var/log/nginx/access.log

# Check for errors
tail -f /var/log/nginx/error.log
```

---

## Configuration Summary

| Component | Config | Notes |
|-----------|--------|-------|
| External React App | Port 80 (Nginx sites-enabled) | Unchanged |
| API Proxy Target | localhost:8081 | ✅ **Changed from 8080** |
| Docker Nginx | Port 8081 (exposed) | Maps to port 80 inside Docker |
| Backend Instances | Internal port 8080 | Not exposed to host |
| Load Balancing | Least-connections | Distributes across 3 instances |

---

## Security Notes

### Current Setup
- ✅ React app publicly accessible (intended)
- ✅ Backend only accessible through nginx proxies
- ✅ Direct backend access blocked (not exposed)
- ✅ Load balancer handles all traffic

### For Production
- 🔒 Add SSL/TLS certificates (Let's Encrypt)
- 🔒 Enable HTTPS on external nginx
- 🔒 Add rate limiting on API endpoints
- 🔒 Add authentication/authorization
- 🔒 Use secrets for sensitive data

---

## Next Steps

1. ✅ **Backup current config:** `cp /etc/nginx/sites-available/my-vote-ui /etc/nginx/sites-available/my-vote-ui.backup`

2. ✅ **Update nginx config:** Replace with the configuration above

3. ✅ **Test:** `sudo nginx -t`

4. ✅ **Reload:** `sudo systemctl reload nginx`

5. ✅ **Verify:** Test React app and API calls

6. ✅ **Monitor:** Watch `docker-compose logs` and `/var/log/nginx/access.log`

---

## Files Provided

1. **MY_VOTE_UI_NGINX_CONFIG.conf** - Your updated nginx config ready to use
2. **MY_VOTE_UI_DOCKER_INTEGRATION.md** - This detailed guide

## Quick Reference

```bash
# Start Docker backend
cd /path/to/extractpdftext
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Reload external nginx
sudo nginx -t && sudo systemctl reload nginx

# Test connections
curl http://localhost:8081/api/pdf/list
curl http://mycommune.org/api/pdf/list
```

---

**Setup Complete!** Your React app now connects to a load-balanced, 3-instance backend instead of a single instance. 🚀

