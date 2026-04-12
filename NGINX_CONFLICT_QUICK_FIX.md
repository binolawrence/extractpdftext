# System Nginx + Docker - Conflict Resolution - Quick Start

## 🎯 Your Conflict

You have:
1. **System Nginx** - Serving React app on port 80
2. **Docker Backend** - 3 Spring Boot instances (trying to expose on port 8081)

**Conflict:** These need to work together, not separately.

---

## ⚡ Quick Fix (5 Steps)

### Step 1: Verify System Nginx is Running

```bash
sudo systemctl status nginx

# If not running:
sudo systemctl start nginx
```

### Step 2: Check Port 8081 is Available

```bash
sudo lsof -i :8081

# If something is using it:
sudo kill -9 <PID>
```

### Step 3: Clean Start Docker

```bash
cd /path/to/extractpdftext
docker-compose down -v
docker-compose up -d
```

Wait 10 seconds...

### Step 4: Verify Docker is Running

```bash
docker-compose ps

# Expected: 4 containers, all "Up (healthy)"
```

### Step 5: Update System Nginx Config

Edit: `/etc/nginx/sites-available/my-vote-ui`

```nginx
location /api/ {
    proxy_pass http://localhost:8081/;  # ← Port 8081 (Docker)
}
```

Then reload:
```bash
sudo nginx -t && sudo systemctl reload nginx
```

---

## ✅ Test It Works

```bash
# Test Docker backend
curl http://localhost:8081/health

# Test through System Nginx
curl http://localhost/api/pdf/list
curl http://mycommune.org/api/pdf/list

# Test in browser
# http://mycommune.org
```

---

## 🔍 If Still Not Working

Run diagnostic script:
```bash
bash diagnose-conflict.sh
```

This will check:
- ✓ System Nginx status
- ✓ Port availability
- ✓ Docker services
- ✓ Network connectivity
- ✓ Configuration validity

---

## 📝 Common Issues

### "Address already in use: 8081"
```bash
# Kill the process using 8081
sudo lsof -i :8081
sudo kill -9 <PID>
docker-compose up -d
```

### "Connection refused" when testing
```bash
# Make sure Docker is running
docker-compose ps

# If containers not running:
docker-compose up -d
sleep 10
docker-compose ps
```

### Nginx returns 502/503
```bash
# Docker backend might be slow to start
docker-compose logs app1 app2 app3

# Wait and try again
sleep 30
curl http://localhost:8081/health
```

### Nginx config syntax error
```bash
# Check where the error is
sudo nginx -t

# Fix and reload
sudo nano /etc/nginx/sites-available/my-vote-ui
sudo nginx -t
sudo systemctl reload nginx
```

---

## 🎉 Expected Result

```
http://mycommune.org (Port 80 - System Nginx)
         ↓
Serves React app
         ↓
API calls to /api/*
         ↓
System Nginx proxies to localhost:8081
         ↓
Docker Nginx load balancer
         ↓
Routes to app1, app2, or app3
         ↓
Backend returns data
         ↓
React updates UI
         ↓
User sees results ✓
```

---

## 📞 Need Full Guide?

Read: `SYSTEM_NGINX_DOCKER_CONFLICT_RESOLUTION.md`

---

**Status:** Follow 5 quick steps above  
**Time:** ~5 minutes  
**Result:** Full integration working ✅

