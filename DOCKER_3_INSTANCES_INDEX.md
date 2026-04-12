# Docker 3 Instances - Complete Documentation Index

**Last Updated:** April 12, 2026

## 📋 Quick Navigation

### Start Here
1. **DOCKER_3_INSTANCES_SETUP.md** ← **START HERE**
   - Overview of what was created
   - Quick start in 3 steps
   - File structure
   - Verification checklist

### For Different Needs

**I want to...**

| Need | Document | Section |
|------|----------|---------|
| Get started quickly | DOCKER_3_INSTANCES_QUICK_REF.md | All |
| Understand everything | DOCKER_3_INSTANCES_GUIDE.md | All |
| See visual diagrams | DOCKER_3_INSTANCES_VISUAL.md | All |
| Debug a problem | DOCKER_3_INSTANCES_TROUBLESHOOTING.md | Step-by-step |
| Explore other options | DOCKER_3_INSTANCES_OPTIONS.md | All |
| Run a command | DOCKER_3_INSTANCES_QUICK_REF.md | Common Scenarios |

---

## 📚 Complete Documentation Set

### 1. **DOCKER_3_INSTANCES_SETUP.md** (THIS IS YOUR START GUIDE)
**Purpose:** Overview and quick start
**Contains:**
- What was created
- Quick start (3 steps)
- File structure
- Common configuration changes
- Verification checklist

**Read this first** for overview and basic setup.

---

### 2. **DOCKER_3_INSTANCES_QUICK_REF.md** (COPY TO DESKTOP)
**Purpose:** Daily reference - copy to desktop for easy access
**Contains:**
- Essential commands (10+ commands)
- How to check status
- How to view logs
- How to restart services
- How to handle common scenarios

**Keep handy** while working with Docker.

**Quick Commands Summary:**
```powershell
# Start
.\docker-helper.ps1 -Command start

# Status
.\docker-helper.ps1 -Command status

# Logs
.\docker-helper.ps1 -Command logs

# Stop
.\docker-helper.ps1 -Command stop
```

---

### 3. **DOCKER_3_INSTANCES_GUIDE.md** (COMPREHENSIVE REFERENCE)
**Purpose:** Complete technical reference
**Contains:**
- Full architecture explanation (with diagram)
- All management commands explained
- Load balancing details
- Shared resources documentation
- Environment variables
- Health checks
- Performance considerations
- Production deployment guidance
- Scaling options
- Troubleshooting basics

**Read this** when you need to understand the "why" or do advanced configuration.

---

### 4. **DOCKER_3_INSTANCES_VISUAL.md** (VISUAL LEARNER GUIDE)
**Purpose:** ASCII diagrams and visual explanations
**Contains:**
- System architecture diagram
- Request flow diagram
- Data flow diagram
- Network topology
- Container dependencies
- Service discovery flow
- Failure/recovery scenarios
- Volume mapping visualization
- Port mapping explanation
- Load balancing visualization
- CPU/Memory distribution
- Lifecycle/startup sequence
- Scaling scenarios

**Read this** if you prefer visual explanations.

---

### 5. **DOCKER_3_INSTANCES_OPTIONS.md** (DECISION GUIDE)
**Purpose:** Compare different deployment approaches
**Contains:**
- Option 1: Docker Compose (current implementation) ✓
- Option 2: Dynamic scaling (flexible instance count)
- Option 3: Docker Swarm (multi-node clustering)
- Option 4: Kubernetes (enterprise-grade)
- Option 5: Cloud platforms (AWS, GCP, Azure)
- Comparison matrix
- When to use each option
- Migration path

**Read this** if:
- You need to scale beyond 3 instances
- You want to compare approaches
- You're planning for future growth

---

### 6. **DOCKER_3_INSTANCES_TROUBLESHOOTING.md** (PROBLEM SOLVER)
**Purpose:** Step-by-step debugging guide
**Contains:**
- Quick diagnosis flow
- 9 common problems with solutions:
  1. Services not starting
  2. Services crash immediately
  3. Services running but not responding
  4. High CPU usage
  5. High memory usage
  6. One instance failing
  7. Shared volume issues
  8. Network connectivity
  9. Build failures
- Common error messages with fixes
- Preventive maintenance
- Quick reference table

**Read this** when something is broken.

---

## 🛠️ Helper Scripts

### **docker-helper.ps1** (RECOMMENDED - PowerShell)
**Windows PowerShell script** with 15+ commands

**Usage:**
```powershell
# Show help
.\docker-helper.ps1 -Command help

# Start services
.\docker-helper.ps1 -Command start

# View logs
.\docker-helper.ps1 -Command logs

# Monitor resources
.\docker-helper.ps1 -Command stats

# Run tests
.\docker-helper.ps1 -Command test

# Open shell in container
.\docker-helper.ps1 -Command shell -Container app2
```

**Why use this?**
- ✅ Modern PowerShell syntax
- ✅ Color-coded output
- ✅ Better error messages
- ✅ Interactive features
- ✅ Easier to read

---

### **docker-helper.bat** (ALTERNATIVE - Batch)
**Windows Batch script** for basic operations

**Usage:**
```cmd
REM Show help
docker-helper.bat help

REM Start services
docker-helper.bat start

REM View logs
docker-helper.bat logs

REM Stop
docker-helper.bat stop
```

**Why use this?**
- Lightweight
- No PowerShell required
- Basic operations

---

## 🐳 Core Docker Files

### **Dockerfile**
**Purpose:** Build Docker image from source code
**Contains:**
- Multi-stage Maven build
- Alpine Linux runtime
- Spring Boot JAR
- Environment configuration
- Health checks

**When to modify:**
- Change Java heap size
- Update base image
- Add system packages
- Modify build parameters

---

### **docker-compose.yml**
**Purpose:** Orchestrate 3 instances + Nginx
**Contains:**
- Nginx service (load balancer)
- App1, App2, App3 services
- Shared volumes definition
- Docker network setup
- Health checks
- Environment variables

**When to modify:**
- Add/remove instances
- Change port numbers
- Modify volume paths
- Update environment variables
- Add resource limits

---

### **nginx.conf**
**Purpose:** Load balancing and reverse proxy configuration
**Contains:**
- Upstream backend definition
- Load balancing algorithm (least-conn)
- Proxy settings
- Headers configuration
- Timeouts and buffering
- Health endpoints

**When to modify:**
- Change load balancing algorithm
- Add SSL/TLS
- Modify proxy headers
- Add caching
- Configure geographic routing

---

## 📊 What Gets Created at Runtime

When you run `docker-compose up -d`, these directories are created:

```
extractpdftext/
├── pdfdocs/                  # PDF documents (shared)
├── lucene-index/             # Search index (shared)
└── logs/
    ├── app1/                 # Instance 1 logs
    ├── app2/                 # Instance 2 logs
    └── app3/                 # Instance 3 logs
```

---

## 🎯 Common Tasks

### Task 1: Start All Services

**Command:**
```powershell
.\docker-helper.ps1 -Command start
```

**What happens:**
1. Builds Docker image (if needed)
2. Starts Nginx (port 80)
3. Starts App1, App2, App3
4. Creates shared volumes
5. Performs health checks
6. Ready for requests in 30-40 seconds

---

### Task 2: Add a 4th Instance

**Steps:**

1. Edit `docker-compose.yml` - duplicate app3 as app4
2. Edit `nginx.conf` - add `server app4:8080` to upstream
3. Create log directory: `mkdir logs/app4`
4. Restart: `docker-compose up -d`

**See:** DOCKER_3_INSTANCES_QUICK_REF.md → "Add 4th instance" scenario

---

### Task 3: Debug Why App2 Is Slow

**Commands:**
```powershell
# Check resource usage
.\docker-helper.ps1 -Command stats

# View app2 logs
.\docker-helper.ps1 -Command logs-app2

# Check exact errors
docker-compose logs app2 | grep ERROR

# Shell into container
.\docker-helper.ps1 -Command shell -Container app2
```

**See:** DOCKER_3_INSTANCES_TROUBLESHOOTING.md → Problem 4 (High CPU)

---

### Task 4: Deploy to Production

**Preparation:**
1. Read: DOCKER_3_INSTANCES_GUIDE.md → Production Deployment
2. Add SSL/TLS to nginx.conf
3. Set resource limits in docker-compose.yml
4. Configure logging driver
5. Add monitoring

**Deployment:**
```bash
# Build production image
docker-compose build --no-cache

# Push to registry (if using one)
docker tag extractpdftext:latest myregistry/extractpdftext:v1.0
docker push myregistry/extractpdftext:v1.0

# Deploy on production server
docker-compose -f docker-compose-prod.yml up -d
```

**See:** DOCKER_3_INSTANCES_GUIDE.md → Production Deployment section

---

## 💡 Pro Tips

### Tip 1: Copy Helper Scripts to PATH
```powershell
# So you can run from anywhere:
Copy-Item docker-helper.ps1 C:\Windows\System32\docker-helper.ps1

# Then run from anywhere:
docker-helper -Command start
```

### Tip 2: Create Aliases
```powershell
# In PowerShell profile:
New-Alias -Name dstart -Value 'docker-helper -Command start'
New-Alias -Name dstop -Value 'docker-helper -Command stop'
New-Alias -Name dlogs -Value 'docker-helper -Command logs'

# Then use:
dstart  # Starts services
dstop   # Stops services
dlogs   # Views logs
```

### Tip 3: Monitor in Background
```powershell
# In separate PowerShell window:
.\docker-helper.ps1 -Command stats

# While you work in another window
```

### Tip 4: Automate Daily Backup
```powershell
# Create backup script
$BackupDir = "C:\backups\extractpdf"
Copy-Item pdfdocs $BackupDir -Recurse -Force
Copy-Item lucene-index $BackupDir -Recurse -Force
```

---

## 🚨 Critical Files Summary

| File | Purpose | Importance |
|------|---------|-----------|
| Dockerfile | Build image | 🔴 Critical |
| docker-compose.yml | Orchestration | 🔴 Critical |
| nginx.conf | Load balancing | 🔴 Critical |
| docker-helper.ps1 | Helper tool | 🟢 Important |
| DOCKER_3_INSTANCES_SETUP.md | Setup guide | 🟢 Important |
| DOCKER_3_INSTANCES_QUICK_REF.md | Daily reference | 🟡 Useful |
| DOCKER_3_INSTANCES_GUIDE.md | Complete reference | 🟡 Useful |
| DOCKER_3_INSTANCES_VISUAL.md | Visual diagrams | 🟡 Useful |
| DOCKER_3_INSTANCES_TROUBLESHOOTING.md | Debug guide | 🟡 Useful |
| DOCKER_3_INSTANCES_OPTIONS.md | Alternative approaches | 🟡 Reference |

---

## 🔍 Finding Information

### By Problem

| Problem | Document | Section |
|---------|----------|---------|
| Services won't start | TROUBLESHOOTING | Step 1 |
| Application is slow | TROUBLESHOOTING | Step 4 |
| High memory usage | TROUBLESHOOTING | Step 5 |
| Need to scale | OPTIONS | Comparison |
| Want to understand architecture | VISUAL | All |
| Need production setup | GUIDE | Production |
| Don't know a command | QUICK_REF | All |

### By Role

| Role | Start With | Then Read |
|------|-----------|-----------|
| **Developer** | QUICK_REF | GUIDE |
| **DevOps** | GUIDE | OPTIONS |
| **SysAdmin** | TROUBLESHOOTING | GUIDE |
| **Newcomer** | SETUP | QUICK_REF |
| **Visual Learner** | VISUAL | GUIDE |

---

## ✅ Verification Checklist

After setup, verify everything works:

```
Docker Installation
  ☐ docker --version
  ☐ docker-compose --version
  ☐ Docker daemon running

Files Created
  ☐ Dockerfile exists
  ☐ docker-compose.yml exists
  ☐ nginx.conf exists
  ☐ docker-helper.ps1 exists

Services Running
  ☐ docker-compose ps shows 4 containers
  ☐ All containers say "Up"
  ☐ All health checks pass

Application Accessible
  ☐ curl http://localhost works
  ☐ Browser can access http://localhost
  ☐ Health endpoint responds: /health

Helper Tools Working
  ☐ docker-helper.ps1 -Command help works
  ☐ docker-helper.ps1 -Command status works
  ☐ docker-helper.ps1 -Command health works

Documentation
  ☐ All .md files exist
  ☐ Can access documentation
  ☐ Understand basic commands
```

---

## 🎓 Learning Path

**Day 1:**
1. Read: DOCKER_3_INSTANCES_SETUP.md
2. Run: `.\docker-helper.ps1 -Command start`
3. Access: `http://localhost`
4. Stop: `.\docker-helper.ps1 -Command stop`

**Day 2:**
1. Read: DOCKER_3_INSTANCES_VISUAL.md
2. Run: `.\docker-helper.ps1 -Command test`
3. Run: `.\docker-helper.ps1 -Command logs`
4. Understand the architecture

**Day 3:**
1. Read: DOCKER_3_INSTANCES_GUIDE.md
2. Modify docker-compose.yml (port number, resource limits)
3. Rebuild: `docker-compose up -d --build`
4. Test: `.\docker-helper.ps1 -Command test`

**Week 2:**
1. Read: DOCKER_3_INSTANCES_OPTIONS.md
2. Plan for scaling if needed
3. Add monitoring/logging
4. Document your setup

---

## 📞 Support Resources

### Inside This Project
- Quick Reference: DOCKER_3_INSTANCES_QUICK_REF.md
- Troubleshooting: DOCKER_3_INSTANCES_TROUBLESHOOTING.md
- Helper: `.\docker-helper.ps1 -Command help`

### External Resources
- Docker Docs: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/
- Nginx: https://nginx.org/en/docs/
- Spring Boot: https://spring.io/projects/spring-boot

### Commands for Help
```powershell
# In project directory:
.\docker-helper.ps1 -Command help

# View Docker help:
docker --help
docker-compose --help

# View Nginx help:
docker-compose exec nginx nginx -h
```

---

## 🎉 You're All Set!

You have a complete, production-ready Docker 3-instance setup with:

✅ **Infrastructure Code**
- Dockerfile (multi-stage build)
- docker-compose.yml (3 instances + load balancer)
- nginx.conf (intelligent routing)

✅ **Helper Tools**
- docker-helper.ps1 (PowerShell)
- docker-helper.bat (Batch)

✅ **Comprehensive Documentation**
- Setup guide
- Quick reference
- Complete guide
- Visual diagrams
- Troubleshooting guide
- Alternative options

✅ **Ready to Deploy**
- Just run: `.\docker-helper.ps1 -Command start`
- Access: `http://localhost`
- Monitor: `.\docker-helper.ps1 -Command stats`

---

## 📝 Version History

| Date | Version | Changes |
|------|---------|---------|
| 2026-04-12 | 1.0 | Initial release |

---

**Questions? Check the appropriate documentation file above!**

**Last Help Command:**
```powershell
.\docker-helper.ps1 -Command test
```

This runs integration tests and shows if everything is working correctly.

Good luck! 🚀

