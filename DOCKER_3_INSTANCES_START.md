# 🚀 DOCKER 3 INSTANCES - GETTING STARTED (5 MINUTES)

## ⚡ Quick Start

### Step 1: Start Services (takes 30 seconds)
```powershell
.\docker-helper.ps1 -Command start
```

### Step 2: Verify Health (takes 10 seconds)
```powershell
.\docker-helper.ps1 -Command health
```

### Step 3: Open in Browser
```
http://localhost
```

**DONE! ✓**

---

## 🎮 Top 5 Commands You'll Use

| Command | What It Does |
|---------|--------------|
| `.\docker-helper.ps1 -Command start` | Start all 3 instances |
| `.\docker-helper.ps1 -Command status` | Check if running |
| `.\docker-helper.ps1 -Command logs` | View live logs |
| `.\docker-helper.ps1 -Command stats` | Monitor CPU/Memory |
| `.\docker-helper.ps1 -Command stop` | Stop all services |

---

## 🐛 Something Broken?

### Service won't start?
```powershell
docker-compose logs
```

### High CPU/Memory?
```powershell
.\docker-helper.ps1 -Command stats
```

### Need detailed logs?
```powershell
.\docker-helper.ps1 -Command logs-app1
```

### Full reset?
```powershell
docker-compose down
docker-compose up -d
```

---

## 📚 Documentation by Need

| I want to... | Read this |
|--------------|-----------|
| Learn everything | DOCKER_3_INSTANCES_SETUP.md |
| See diagrams | DOCKER_3_INSTANCES_VISUAL.md |
| Fix a problem | DOCKER_3_INSTANCES_TROUBLESHOOTING.md |
| See all commands | DOCKER_3_INSTANCES_QUICK_REF.md |
| Understand architecture | DOCKER_3_INSTANCES_GUIDE.md |
| Scale up or change | DOCKER_3_INSTANCES_OPTIONS.md |

---

## ✅ Verify It's Working

Run this command:
```powershell
.\docker-helper.ps1 -Command test
```

You'll see:
```
✓ All services running
✓ Load balancer responding
✓ All instances healthy
✓ Shared volumes working
✓ No errors in logs
```

---

## 🏗️ What You Have

**3 Running Instances:**
- App1 (Port 8080 internal)
- App2 (Port 8080 internal)  
- App3 (Port 8080 internal)

**Load Balancer:**
- Nginx (Port 80 external)

**Shared Storage:**
- PDF Documents folder
- Lucene search index
- App logs (separate per instance)

---

## 🎯 Architecture

```
    http://localhost (Port 80)
             ↓
         Nginx Load Balancer
             ↓
    ┌───────┼───────┐
    ↓       ↓       ↓
  App1    App2    App3
```

Each instance handles requests equally using intelligent load balancing.

---

## 💡 Pro Tips

1. **Keep this open:** DOCKER_3_INSTANCES_QUICK_REF.md
2. **Monitor in separate window:** `.\docker-helper.ps1 -Command stats`
3. **Check logs first:** `docker-compose logs` (when debugging)
4. **Regular backups:** Backup `pdfdocs/` and `lucene-index/` folders

---

## ❌ Common Issues

### "Port 80 already in use"
Edit docker-compose.yml, change port to 8080
```yaml
nginx:
  ports:
    - "8080:80"  # Changed from 80:80
```

### "Out of memory"
Reduce JVM heap in Dockerfile:
```dockerfile
ENV JAVA_OPTS="-Xmx512m -Xms256m"
```

### "One instance keeps crashing"
Check logs:
```powershell
docker-compose logs app1 --tail=50
```

### "I want to restart one instance"
```bash
docker-compose restart app1
```

---

## 📞 Need Help?

1. Run: `.\docker-helper.ps1 -Command test`
2. Check: `docker-compose logs`
3. Read: DOCKER_3_INSTANCES_TROUBLESHOOTING.md
4. Ask: "How do I..." in Google

---

## ✨ You're Ready!

```powershell
.\docker-helper.ps1 -Command start
```

Then: **http://localhost**

Enjoy! 🎉

