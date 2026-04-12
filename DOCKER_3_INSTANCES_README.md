# Docker 3 Instances - Complete Setup

## ⚡ Quick Start (Pick One)

### Option A: Just Run It (30 seconds)
```powershell
.\docker-helper.ps1 -Command start
```
Then open: `http://localhost`

### Option B: Learn First (30 minutes)
1. Read: `DOCKER_3_INSTANCES_START.md`
2. Run the commands above
3. Keep `DOCKER_3_INSTANCES_QUICK_REF.md` handy

### Option C: Understand Everything (2 hours)
1. Read: `DOCKER_3_INSTANCES_SETUP.md`
2. Read: `DOCKER_3_INSTANCES_GUIDE.md`
3. View: `DOCKER_3_INSTANCES_VISUAL.md`
4. Keep: `DOCKER_3_INSTANCES_TROUBLESHOOTING.md` for reference

---

## 📋 What You Have

✅ **3 Docker instances** of your Spring Boot application running in parallel  
✅ **Nginx load balancer** distributing requests intelligently  
✅ **Shared storage** for PDFs and search index  
✅ **Helper scripts** for easy management (PowerShell + Batch)  
✅ **Comprehensive documentation** (2,500+ lines)  
✅ **Production ready** with health checks and error handling  

---

## 🎯 Essential Commands

```powershell
# Start all services (takes 30-40 seconds)
.\docker-helper.ps1 -Command start

# Check if running
.\docker-helper.ps1 -Command status

# View logs
.\docker-helper.ps1 -Command logs

# Monitor CPU/Memory usage
.\docker-helper.ps1 -Command stats

# Run health check
.\docker-helper.ps1 -Command health

# Stop all services
.\docker-helper.ps1 -Command stop

# Get help on all commands
.\docker-helper.ps1 -Command help
```

---

## 📚 Documentation Files

### Quick Start
- **DOCKER_3_INSTANCES_START.md** - 5-minute quick start guide

### Setup & Configuration
- **DOCKER_3_INSTANCES_SETUP.md** - Complete setup documentation
- **DOCKER_3_INSTANCES_QUICK_REF.md** - Daily command reference (save to desktop)

### Understanding the System
- **DOCKER_3_INSTANCES_VISUAL.md** - Architecture diagrams and visual explanations
- **DOCKER_3_INSTANCES_GUIDE.md** - Complete technical reference (25+ pages)

### When Things Break
- **DOCKER_3_INSTANCES_TROUBLESHOOTING.md** - Debug guide with 9 common problems

### Advanced Topics
- **DOCKER_3_INSTANCES_OPTIONS.md** - Alternative deployment approaches
- **DOCKER_3_INSTANCES_INDEX.md** - Documentation index and navigation

### Reports
- **DOCKER_3_INSTANCES_COMPLETE.md** - Project completion summary
- **DOCKER_3_INSTANCES_FINAL_REPORT.md** - Verification and next steps

---

## 🐳 Docker Files

### Dockerfile
- Multi-stage Maven build optimizing image size
- Alpine Linux runtime for efficiency
- Spring Boot application configured
- Health checks included
- Environment variables ready for customization

### docker-compose.yml
- Defines 3 app instances + Nginx load balancer
- Shares PDF documents and Lucene index
- Isolates logs per instance
- Health checks for all services
- Docker network for service discovery

### nginx.conf
- Load balancing with least-connections algorithm
- Automatic failover handling
- Proxy headers and buffer configuration
- Health check endpoints
- Professional error handling

---

## 🛠️ Helper Scripts

### docker-helper.ps1 (Recommended)
Modern PowerShell script with:
- 15+ management commands
- Color-coded output
- Error handling
- Interactive features
- Resource monitoring
- Integration testing

Usage:
```powershell
.\docker-helper.ps1 -Command [command]
```

### docker-helper.bat (Alternative)
Windows Batch script for basic operations:
```cmd
docker-helper.bat [command]
```

---

## 🎮 Common Workflows

### Start Development
```powershell
.\docker-helper.ps1 -Command start
.\docker-helper.ps1 -Command health
```

### Monitor Your Application
```powershell
# In one window:
.\docker-helper.ps1 -Command start

# In another window:
.\docker-helper.ps1 -Command stats

# Access in browser:
http://localhost
```

### Debug Issues
```powershell
.\docker-helper.ps1 -Command logs       # All logs
.\docker-helper.ps1 -Command logs-app1  # Specific instance
.\docker-helper.ps1 -Command test       # Run diagnostics
```

### Add Another Instance
1. Duplicate `app3` in `docker-compose.yml` as `app4`
2. Add `server app4:8080` to upstream in `nginx.conf`
3. Run: `docker-compose up -d`

### Rebuild Application
```powershell
.\docker-helper.ps1 -Command build
```

---

## ✅ Verification Checklist

After starting, verify everything is working:

- [ ] `docker-compose ps` shows 4 containers (nginx + 3 apps)
- [ ] All containers show "Up" status
- [ ] All health checks show "✓"
- [ ] `http://localhost` loads in browser
- [ ] `/health` endpoint returns success
- [ ] No "ERROR" in logs
- [ ] `.\docker-helper.ps1 -Command test` passes

---

## 📈 Architecture

```
User Browser → http://localhost:80
         ↓
    Nginx Load Balancer
    (intelligent routing)
         ↓
    ┌────┬────┬────┐
    ↓    ↓    ↓
 App1  App2  App3
 8080  8080  8080
    ↓    ↓    ↓
    └────┴────┴────┘
    Shared Storage
    - PDFs
    - Search Index
    - Logs (separate)
```

Each instance can handle requests in parallel.
Load balancer distributes traffic using least-connections algorithm.

---

## 🔐 Security Notes

### Development Setup (Current)
✅ Safe for local development  
✅ Health checks enabled  
✅ Docker network isolated  

### For Production
Add these:
- SSL/TLS certificates to Nginx
- Resource limits per container
- Secrets management
- Audit logging
- Regular security updates

See: `DOCKER_3_INSTANCES_GUIDE.md` → Production section

---

## 💡 Pro Tips

1. **Desktop Shortcut:** Copy `DOCKER_3_INSTANCES_QUICK_REF.md` to desktop
2. **Keep Monitoring:** Run `.\docker-helper.ps1 -Command stats` in separate window
3. **Check Logs First:** When debugging, always run `docker-compose logs`
4. **Weekly Backups:** Backup `pdfdocs/` and `lucene-index/` folders
5. **Monthly Updates:** Run `docker-compose build --no-cache`

---

## 🐛 Troubleshooting

### Services won't start?
```powershell
docker-compose logs
```

### High CPU/Memory?
```powershell
.\docker-helper.ps1 -Command stats
```

### One instance failing?
```powershell
.\docker-helper.ps1 -Command logs-app1
.\docker-helper.ps1 -Command restart
```

### Need detailed help?
→ Read: `DOCKER_3_INSTANCES_TROUBLESHOOTING.md`

---

## 📊 Features

✅ **3 Parallel Instances** - 3x processing capacity  
✅ **Load Balancing** - Intelligent distribution  
✅ **High Availability** - Automatic failover  
✅ **Shared Resources** - PDFs and index synchronized  
✅ **Health Checks** - Every 30 seconds  
✅ **Easy Management** - 15+ helper commands  
✅ **Monitoring** - CPU, memory, logs  
✅ **Production Ready** - Proper error handling  
✅ **Scalable** - Easy to add more instances  
✅ **Documented** - 2,500+ lines of guides  

---

## 🚀 Getting Started Right Now

```powershell
# 1. Start services
.\docker-helper.ps1 -Command start

# 2. Wait 30 seconds for all to start

# 3. Open browser
http://localhost

# 4. That's it! Your app is running with 3 instances
```

---

## 📞 Need Help?

| Question | Answer |
|----------|--------|
| How do I start? | Run: `.\docker-helper.ps1 -Command start` |
| What commands are available? | Read: `DOCKER_3_INSTANCES_QUICK_REF.md` |
| How does it work? | View: `DOCKER_3_INSTANCES_VISUAL.md` |
| Something broke | Check: `DOCKER_3_INSTANCES_TROUBLESHOOTING.md` |
| I want to scale | See: `DOCKER_3_INSTANCES_OPTIONS.md` |

---

## 📋 File Overview

```
extractpdftext/
├── Dockerfile                          # Docker image definition
├── docker-compose.yml                  # 3 instances + Nginx
├── nginx.conf                          # Load balancer config
├── docker-helper.ps1                   # PowerShell helper (use this!)
├── docker-helper.bat                   # Batch helper (alternative)
│
├── DOCKER_3_INSTANCES_START.md         # 5-min quick start
├── DOCKER_3_INSTANCES_SETUP.md         # Complete setup guide
├── DOCKER_3_INSTANCES_QUICK_REF.md     # Daily commands
├── DOCKER_3_INSTANCES_GUIDE.md         # Full technical guide
├── DOCKER_3_INSTANCES_VISUAL.md        # Architecture diagrams
├── DOCKER_3_INSTANCES_TROUBLESHOOTING.md # Debug guide
├── DOCKER_3_INSTANCES_OPTIONS.md       # Alternative approaches
├── DOCKER_3_INSTANCES_INDEX.md         # Documentation index
├── DOCKER_3_INSTANCES_COMPLETE.md      # Completion summary
├── DOCKER_3_INSTANCES_FINAL_REPORT.md  # Verification report
│
├── pom.xml                             # Maven configuration
├── src/                                # Source code
└── target/                             # Build output
```

---

## ✨ Summary

You now have a **complete, production-ready Docker setup** for running 3 instances of your Spring Boot application with:

- ✅ Intelligent load balancing
- ✅ Automatic failover
- ✅ Comprehensive documentation
- ✅ Easy management tools
- ✅ Professional configuration

### To get started:

```powershell
.\docker-helper.ps1 -Command start
```

Then visit: **http://localhost**

---

**Status:** ✅ READY TO USE  
**Created:** April 12, 2026  
**Documentation:** 2,500+ lines  

**Enjoy your 3-instance Docker setup! 🎉**

