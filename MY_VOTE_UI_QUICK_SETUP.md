# React App + Docker Backend - Quick Setup

## The Change (One Line)

**Old:** `proxy_pass http://localhost:8080/;`  
**New:** `proxy_pass http://localhost:8081/;`

That's it! Your React app now connects to the load-balanced Docker backend instead of a single instance.

---

## Quick Setup (5 Steps)

### Step 1: Start Docker Backend
```bash
cd /path/to/extractpdftext
docker-compose up -d
```

### Step 2: Update Nginx Config
**File:** `/etc/nginx/sites-available/my-vote-ui`

Change the `/api/` location:
```nginx
location /api/ {
    proxy_pass http://localhost:8081/;  # ← Change 8080 to 8081
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_connect_timeout 30s;
    proxy_send_timeout 30s;
    proxy_read_timeout 30s;
    proxy_buffering on;
    proxy_buffer_size 128k;
    proxy_buffers 4 256k;
    proxy_busy_buffers_size 256k;
}
```

### Step 3: Test Configuration
```bash
sudo nginx -t
```

### Step 4: Reload Nginx
```bash
sudo systemctl reload nginx
```

### Step 5: Verify
```bash
# Test Docker backend
curl http://localhost:8081/api/pdf/list

# Test through Nginx
curl http://mycommune.org/api/pdf/list

# Open browser
# http://mycommune.org
```

---

## Architecture Change

```
BEFORE:
React (Port 80) → Single Backend (Port 8080)

AFTER:
React (Port 80) → Nginx (Port 8081) → 3 Backends (Load Balanced)
                  └─ Least connections algorithm
                  └─ Auto failover
                  └─ 3x capacity
```

---

## What Happens Now

1. **User visits** `http://mycommune.org/`
   → React app loads from `/var/www/my-vote-ui/dist/`

2. **React makes API call** to `/api/pdf/search`
   → External Nginx receives at port 80
   → Forwards to Docker Nginx at port 8081

3. **Docker Nginx** (load balancer)
   → Picks least-connected backend (app1, app2, or app3)
   → Forwards request

4. **Backend instance** processes request
   → Returns response through load balancer
   → Response goes back to React app

5. **React updates UI** with data

---

## Port Mapping

| Port | Service | Direction |
|------|---------|-----------|
| 80 | External Nginx | ← Internet → React App |
| 8081 | Docker Nginx | ← External Nginx → Backend Load Balancer |
| 8080 | Spring Boot x3 | ← Docker Nginx → Backend Instances |

---

## Benefits

✅ **Load Balancing** - Requests distributed across 3 instances  
✅ **High Availability** - If 1 instance fails, 2 keep running  
✅ **Better Performance** - 3x processing capacity  
✅ **Easy Scaling** - Add more instances without changing nginx config  
✅ **Automatic Failover** - Unhealthy instances automatically removed  

---

## Testing

### Test 1: Backend is Running
```bash
curl http://localhost:8081/health
# Response: 200 OK with health status
```

### Test 2: Nginx is Proxying
```bash
curl http://mycommune.org/api/pdf/list
# Same response as above
```

### Test 3: React App Works
```
Open: http://mycommune.org
Try: Search, upload, or any API call
Check: DevTools Network tab to see API calls
```

### Test 4: Load Balancing Works
```bash
# Make multiple API calls
for i in {1..10}; do
  curl http://localhost:8081/api/pdf/list
done

# Check which instances handled requests
docker-compose logs app1 app2 app3
# You should see requests from different instances
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Connection Refused" | `docker-compose up -d` to start backend |
| API calls fail | Check `curl http://localhost:8081/api/pdf/list` |
| Nginx won't reload | Check with `sudo nginx -t` |
| React app loads but API fails | Verify port 8081 in nginx config (not 8080) |
| High latency | Check `docker stats` for resource issues |

---

## Files Provided

1. **MY_VOTE_UI_NGINX_CONFIG.conf**
   - Complete ready-to-use nginx configuration
   - Copy/paste into `/etc/nginx/sites-available/my-vote-ui`

2. **MY_VOTE_UI_DOCKER_INTEGRATION.md**
   - Comprehensive setup guide
   - Architecture details
   - Troubleshooting section

---

## Commands Reference

```bash
# Start Docker backend
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app1

# Monitor resources
docker stats

# Test backend
curl http://localhost:8081/health

# Reload external nginx
sudo nginx -t && sudo systemctl reload nginx

# View nginx access log
tail -f /var/log/nginx/access.log

# View nginx error log
tail -f /var/log/nginx/error.log
```

---

## Before & After

### BEFORE
```
React App
    ↓
Nginx (Port 80)
    ↓
Single Spring Boot (Port 8080) ← Single point of failure
    ↓
Database
```

### AFTER
```
React App
    ↓
Nginx (Port 80)
    ↓
Docker Nginx (Port 8081) ← Load balancer
    ↓
Spring Boot x3 ← High availability
    ↓
Database (shared)
```

---

## Summary

**One Change:** Update nginx config port from **8080** to **8081**  
**One Benefit:** Your React app now has load-balanced, high-availability backend  
**One File:** See `MY_VOTE_UI_NGINX_CONFIG.conf` for complete configuration  

**Start with:** `MY_VOTE_UI_DOCKER_INTEGRATION.md` for detailed instructions

---

**Ready to deploy?** Follow the 5 steps above! 🚀

