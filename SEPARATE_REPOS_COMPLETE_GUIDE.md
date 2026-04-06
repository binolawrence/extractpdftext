# 🚀 Complete Implementation Guide - Separate Frontend & Backend

## Your Exact Scenario

**Backend Repository:** `extractpdftext` (Spring Boot)  
**Frontend Repository:** `frontend-repo` (React - SEPARATE REPO)  
**Goal:** Keep them synchronized and deploy together via Docker

---

## 🎯 EXECUTION PLAN

### Phase 1: Setup (ONE TIME - 5 minutes)
### Phase 2: Daily Development (Ongoing)
### Phase 3: Deployment (Combined Docker)

---

# 📋 PHASE 1: INITIAL SETUP

## Step 1: Navigate to Backend Root

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
```

Verify you see:
```
Mode                 LastWriteTime         Length Name
----                 ---------------         ------ ----
d-----         3/25/2026   2:45 PM                src
d-----         3/25/2026   2:45 PM                target
-a----         3/25/2026   2:45 PM            2234 pom.xml
-a----         3/25/2026   2:45 PM             312 Dockerfile
-a----         3/25/2026   2:45 PM             891 docker-compose.yml
-a----         3/25/2026   2:45 PM             542 .gitignore
```

✅ You're in the right place

---

## Step 2: Check if Frontend Already Exists

```powershell
# Is frontend folder already here?
Test-Path frontend

# If it exists, check if it's a git repo
cd frontend
git remote -v
cd ..
```

**If frontend already exists as regular folder:**
```powershell
# Backup first (optional)
Copy-Item frontend frontend-backup

# Remove it
Remove-Item frontend -Recurse -Force
```

**If frontend already exists as submodule:**
```powershell
# Perfect! Skip to Step 4
# Already coordinated!
```

✅ Verified frontend status

---

## Step 3: Add Frontend as Submodule

Replace `yourusername/frontend-repo` with YOUR actual repo:

```powershell
# Add as submodule
git submodule add https://github.com/yourusername/frontend-repo.git frontend
```

**Expected output:**
```
Cloning into 'C:\Users\bfrancis\projects\extractpdftext\frontend'...
remote: Enumerating objects: 150, done.
Receiving objects: 100% (150/150), 5.24 MiB | 2.35 MiB/s, done.
Resolving deltas: 100% (45/45), done.
[submodule "frontend"]
    path = frontend
    url = https://github.com/yourusername/frontend-repo.git
```

✅ Submodule added

---

## Step 4: Verify Files

```powershell
# 1. Check .gitmodules file
Get-Content .gitmodules

# Output should look like:
# [submodule "frontend"]
#     path = frontend
#     url = https://github.com/yourusername/frontend-repo.git

# 2. Verify frontend folder exists
ls frontend | head -5

# 3. Check git status
git status

# Output should show:
# new file:   .gitmodules
# new file:   frontend
```

✅ All files in place

---

## Step 5: Commit to Backend Repository

```powershell
# Add the changes
git add .gitmodules frontend

# Commit with message
git commit -m "Add frontend as Git submodule for coordinated deployment"

# Push to backend repo
git push origin main
```

**Expected output:**
```
[main 7a8f9c0] Add frontend as Git submodule for coordinated deployment
 2 files changed, 4 insertions(+)
 create mode 100644 .gitmodules
 create mode 160000 frontend
```

✅ Committed to backend repo

---

## Step 6: Verify on GitHub

1. Open: `https://github.com/yourusername/extractpdftext`
2. Look for `frontend` folder in root
3. Should show a small folder icon with `@` symbol (indicates submodule)
4. Click it → Should show: "frontend @ [commit-hash]"
5. `.gitmodules` file should be visible

✅ Backend repo updated with submodule reference

---

## Step 7: Test Docker Still Works

```powershell
# Build images
docker-compose build

# Should see:
# Successfully built [hash]
# Successfully tagged extractpdftext-backend:latest
# Successfully tagged extractpdftext-frontend:latest
```

```powershell
# Start services
docker-compose up -d

# Check status
docker-compose ps

# All should show: Up (healthy) or Up
```

✅ Docker works perfectly with submodule!

---

# 🔄 PHASE 2: DAILY DEVELOPMENT

## Scenario A: Work Only on Backend

```powershell
# 1. Navigate to backend root
cd C:\Users\bfrancis\projects\extractpdftext

# 2. Make changes (example: edit Java file)
# Edit: src/main/java/com/extract/pdf/extractpdftext/...

# 3. Commit changes
git add .
git commit -m "Add new PDF extraction feature"

# 4. Push to backend repository
git push origin main

# Done! Backend updated independently ✓
```

---

## Scenario B: Work Only on Frontend

```powershell
# 1. Navigate to frontend subfolder
cd C:\Users\bfrancis\projects\extractpdftext\frontend

# 2. Make changes (example: edit React component)
# Edit: src/App.jsx or any React files

# 3. Commit changes IN FRONTEND REPO
git add .
git commit -m "Update React components for PDF display"

# 4. Push to FRONTEND repository (separate repo!)
git push origin main

# 5. Go back to backend root
cd ..

# 6. Update backend to reference new frontend version
git add frontend
git commit -m "Update frontend submodule to latest"

# 7. Push to backend repository
git push origin main

# Done! Frontend updated independently ✓
# But backend now tracks the new frontend version ✓
```

---

## Scenario C: Work on BOTH Backend and Frontend (Most Common)

This is the coordinated workflow:

```powershell
# 1. Start in backend root
cd C:\Users\bfrancis\projects\extractpdftext

# 2. UPDATE frontend to latest (get team's changes)
git pull --recurse-submodules origin main

# 3. Make backend changes
# Edit: src/main/java/...
# Add new API endpoint for PDF analysis

# 4. Commit backend changes
git add src/pom.xml .
git commit -m "Add PDF analysis API"

# 5. Go to frontend
cd frontend

# 6. Make corresponding frontend changes
# Edit: src/services/api.js (call new API endpoint)
# Edit: src/components/PDFAnalyzer.jsx (display results)

# 7. Commit frontend changes (to frontend repo!)
git add .
git commit -m "Integrate new PDF analysis feature"

# 8. Push frontend changes to frontend repository
git push origin main

# 9. Go back to backend root
cd ..

# 10. Update backend to point to latest frontend
git add frontend
git commit -m "Update frontend to integrate PDF analysis"

# 11. Push backend changes (which includes frontend pointer)
git push origin main

# Done! Both repos coordinated:
# - Backend repo has new API
# - Frontend repo has new UI
# - Backend knows about frontend version
# - Everything synchronized ✓
```

---

# 🐳 PHASE 3: DEPLOYMENT WITH DOCKER

## Before Deploying - Verification Checklist

```powershell
# 1. Make sure everything is committed
cd C:\Users\bfrancis\projects\extractpdftext
git status
# Should show: nothing to commit, working tree clean

# 2. Check frontend is tracked
git submodule status
# Should show: <commit-hash> frontend (HEAD detached...)

# 3. Verify .gitmodules
Get-Content .gitmodules
# Should have [submodule "frontend"] with correct URL

# 4. Latest updates
git pull --recurse-submodules origin main
```

✅ All verified

---

## Deploy to Docker

```powershell
# 1. Navigate to project root
cd C:\Users\bfrancis\projects\extractpdftext

# 2. Build both services
docker-compose build

# Expected output:
# Step 1/X in Dockerfile (backend build)...
# Step 1/X in frontend/Dockerfile (frontend build)...
# Successfully built backend
# Successfully built frontend
```

```powershell
# 3. Start all services
docker-compose up -d

# Expected output:
# Creating extractpdftext-backend ... done
# Creating extractpdftext-frontend ... done
# Creating extractpdftext-nginx ... done
```

```powershell
# 4. Verify everything running
docker-compose ps

# Expected output:
# CONTAINER ID   NAMES                       STATUS
# abc123         extractpdftext-backend      Up (healthy)
# def456         extractpdftext-frontend     Up
# ghi789         extractpdftext-nginx        Up
```

```powershell
# 5. Test endpoints
# Backend health check
curl http://localhost:8080/actuator/health

# Frontend
Start-Process "http://localhost:3000"

# Or via nginx proxy
Start-Process "http://localhost"
```

✅ Deployed successfully!

---

## After Deployment - Monitoring

```powershell
# View all service logs
docker-compose logs -f

# View specific service
docker-compose logs -f backend
docker-compose logs -f frontend

# Stop services (when done)
docker-compose stop

# Restart specific service
docker-compose restart backend

# Clean up (careful! removes containers but not volumes)
docker-compose down

# Full cleanup (includes volumes - remove storage!)
docker-compose down -v
```

---

# 🔄 TEAM COLLABORATION

## New Team Member Sets Up

```powershell
# Clone ENTIRE project (backend + frontend)
# The --recursive flag gets the submodule too!
git clone --recursive https://github.com/yourusername/extractpdftext.git

cd extractpdftext

# Start working immediately - both repos ready!
docker-compose build
docker-compose up -d
```

---

## Team Member Updates Both Repos

```powershell
# Simple command that pulls everything
git pull --recurse-submodules origin main

# Or add to .git/config to make it automatic:
# [submodule]
#     recurse = true
```

---

# ⚠️ TROUBLESHOOTING GUIDE

## Problem: "frontend" folder shows weird icon in VS Code

**This is NORMAL!** The folder icon shows it's a submodule.

To verify it's correct:
```powershell
cd frontend
git remote -v

# Should show your frontend repo URL
# Example:
# origin  https://github.com/yourusername/frontend-repo.git (fetch)
# origin  https://github.com/yourusername/frontend-repo.git (push)
```

✅ It's fine!

---

## Problem: Frontend folder is empty

```powershell
# Initialize and update submodule
git submodule init
git submodule update

# Or:
git submodule update --init --recursive
```

---

## Problem: Error when pushing frontend changes

```powershell
# Make sure you're in frontend folder
cd C:\Users\bfrancis\projects\extractpdftext\frontend

# Check remote URL
git remote -v

# If it shows wrong URL, fix it:
git remote set-url origin https://github.com/yourusername/frontend-repo.git

# Then try push again
git push origin main
```

---

## Problem: Docker build fails on frontend

```powershell
# 1. Check frontend/Dockerfile exists
Test-Path frontend/Dockerfile

# 2. Check frontend/package.json exists
Test-Path frontend/package.json

# 3. Manually test build
cd frontend
docker build -t frontend-test .

# 4. View detailed error
docker build -t frontend-test . 2>&1 | more
```

---

## Problem: ".gitmodules conflict" during merge

```powershell
# View the conflict
Get-Content .gitmodules

# Usually use their version
git checkout --theirs .gitmodules

# Or resolve manually
# Then commit
git add .gitmodules
git commit -m "Resolve gitmodules conflict"
```

---

# 📊 QUICK REFERENCE MATRIX

| Task | Command | Location |
|------|---------|----------|
| Work on backend | `git add . && git push` | Backend root |
| Work on frontend | `cd frontend` then `git add . && git push` then `cd ..` | Frontend subfolder |
| Sync everything | `git pull --recurse-submodules` | Backend root |
| Update submodule | `git submodule update --remote` | Backend root |
| See frontend version | `git submodule status` | Backend root |
| Deploy | `docker-compose build && docker-compose up -d` | Backend root |
| Clone with everything | `git clone --recursive <url>` | Any folder |

---

# 🎯 SUCCESS INDICATORS

You'll know it's working correctly when:

✅ `.gitmodules` file exists in backend root  
✅ `frontend` folder exists and shows files  
✅ `cd frontend` then `git remote -v` shows frontend repo  
✅ `git submodule status` shows commit hash  
✅ `docker-compose build` completes without errors  
✅ `docker-compose ps` shows all 3 services running  
✅ Frontend changes appear in frontend repo  
✅ Backend changes appear in backend repo  
✅ Backend tracks frontend version  
✅ Docker serves both apps on ports 3000 and 8080  

---

# 🚀 START NOW

```powershell
# If you haven't set up yet:
1. cd C:\Users\bfrancis\projects\extractpdftext
2. git submodule add https://github.com/yourusername/frontend-repo.git frontend
3. git add .gitmodules frontend
4. git commit -m "Add frontend as submodule"
5. git push origin main

# If already set up:
1. git pull --recurse-submodules
2. docker-compose build
3. docker-compose up -d
4. Open http://localhost
```

---

# 📚 REFERENCE FILES

| File | Purpose |
|------|---------|
| `.gitmodules` | Submodule configuration (auto-generated) |
| `docker-compose.yml` | Container orchestration (existing) |
| `frontend/Dockerfile` | Frontend build (in submodule) |
| `Dockerfile` | Backend build (existing) |

---

## 🎓 Key Concepts

**Submodule:** A Git repository inside another Git repository  
**Coordinated:** Backend tracks which frontend version to use  
**Separate Repos:** Each has independent commit history  
**Docker Integration:** Both built and deployed together  
**Team Friendly:** Clone once gets both repos  

---

**Your setup is now enterprise-grade with proper versioning! 🚀**


