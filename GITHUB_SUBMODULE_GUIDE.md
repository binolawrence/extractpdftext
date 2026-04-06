# 🔗 Git Submodule Setup - Coordinating Separate Repos

## Overview

Using Git submodules allows you to:
- ✅ Keep backend and frontend in separate repositories
- ✅ Maintain independent versioning for each
- ✅ Update each repo independently
- ✅ Coordinate versions together
- ✅ Deploy them together with Docker

---

## 📋 Prerequisites

- Backend repo: `https://github.com/yourusername/extractpdftext.git`
- Frontend repo: `https://github.com/yourusername/frontend-repo.git` (or similar)
- Both repos already exist on GitHub

---

## 🚀 SETUP - One Time Only

### Step 1: Update Backend Repository (Main Project)

Go to your backend repo and add frontend as a submodule:

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# Add frontend as submodule
git submodule add https://github.com/yourusername/frontend-repo.git frontend

# This creates:
# - .gitmodules file (tracks submodule configuration)
# - frontend/ folder (contains frontend repo)
```

### Step 2: Verify Submodule Added

```powershell
# Check status
git status

# You should see:
# new file:   .gitmodules
# new file:   frontend
```

### Step 3: Commit Submodule Configuration

```powershell
git add .gitmodules frontend
git commit -m "Add frontend as submodule"
git push origin main
```

### Step 4: Verify .gitmodules File

```powershell
# View the configuration file
Get-Content .gitmodules

# Should show:
# [submodule "frontend"]
#     path = frontend
#     url = https://github.com/yourusername/frontend-repo.git
```

---

## 📥 CLONING - For Other Developers

When someone clones your backend repo with the submodule:

```powershell
# Clone with submodules (PREFERRED)
git clone --recursive https://github.com/yourusername/extractpdftext.git

# OR if already cloned without --recursive:
cd extractpdftext
git submodule init
git submodule update
```

---

## 🔄 DAILY WORKFLOW - Both Repos Coordinated

### Scenario 1: Work in Backend Only

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# Make changes in backend
git add .
git commit -m "Backend feature"
git push origin main
```

### Scenario 2: Work in Frontend Only

```powershell
cd C:\Users\bfrancis\projects\extractpdftext\frontend

# Make changes in frontend
git add .
git commit -m "Frontend feature"
git push origin main

# Go back to backend root
cd ..
```

### Scenario 3: Work in Both (Most Common)

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# 1. Update frontend to latest version
git submodule update --remote frontend

# 2. Make backend changes
git add backend-files/
git commit -m "Backend changes"

# 3. Go to frontend
cd frontend

# 4. Make frontend changes
git add .
git commit -m "Frontend changes"
git push origin main

# 5. Go back to backend root
cd ..

# 6. Update backend to point to new frontend commit
git add frontend
git commit -m "Update frontend submodule to latest"
git push origin main
```

---

## 📦 PULLING - Getting Latest Changes

### Pull All Updates (Backend + Frontend)

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# Fetch backend updates
git pull origin main

# Fetch frontend updates
git submodule update --remote --merge
```

### Or Simple One-Line Update

```powershell
git pull --recurse-submodules origin main
```

---

## 🐳 DOCKER - Works Seamlessly

Your existing `docker-compose.yml` already works perfectly!

```yaml
frontend:
  build:
    context: ./frontend    # This is the submodule folder
    dockerfile: Dockerfile
  # ... rest of config
```

When you run Docker:
```powershell
# Everything works automatically
docker-compose build
docker-compose up -d
```

---

## ⚠️ COMMON ISSUES & SOLUTIONS

### Issue 1: Frontend Folder Shows as Gray/Linked

**This is NORMAL!** It means it's properly linked as a submodule.

Verify it's correct:
```powershell
git status

# Should show:
# frontend (commit hash here)
```

### Issue 2: "No submodule mapping found in .gitmodules"

**Solution:**
```powershell
# Check .gitmodules exists
Get-Content .gitmodules

# If empty, re-add:
git submodule add https://github.com/yourusername/frontend-repo.git frontend
```

### Issue 3: Frontend Not Updating When Pushing

**Solution:**
```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# The submodule points to a specific commit
# Update to latest frontend main branch:
cd frontend
git checkout main
git pull origin main
cd ..

# Now commit this new reference in backend
git add frontend
git commit -m "Update frontend to latest"
git push origin main
```

### Issue 4: Merge Conflicts in .gitmodules

**Solution:**
```powershell
# Accept their version usually
git checkout --theirs .gitmodules

# Or manually edit .gitmodules to match both entries
git add .gitmodules
git commit -m "Resolve gitmodules conflict"
```

---

## 🎯 VERSIONING STRATEGY

### Approach 1: Tag Releases Together (RECOMMENDED)

```powershell
# When ready to release both:

# 1. Update frontend to release commit
cd frontend
git tag v1.0.0
git push origin v1.0.0
cd ..

# 2. Update backend with frontend pointer
git add frontend
git commit -m "Release v1.0.0"
git tag v1.0.0
git push origin --tags
```

### Approach 2: Independent Versioning

Each repo has its own version:
- Backend: v2.5.0
- Frontend: v1.3.2

Track in a version file:

```powershell
# Create VERSIONS.md in backend root
```

**File: VERSIONS.md**
```
# Component Versions

- Backend: v2.5.0 (https://github.com/yourusername/extractpdftext)
- Frontend: v1.3.2 (https://github.com/yourusername/frontend-repo)

Last Updated: 2026-04-06
```

---

## 📊 STATUS CHECK

### View Current Submodule State

```powershell
git submodule status

# Shows:
# <hash> frontend (<branch name>)
```

### View Both Repos Status

```powershell
# Backend status
git status

# Frontend status
cd frontend
git status
cd ..
```

---

## 🔧 ADVANCED - Update Submodule Automatically

### Create Helper Script: update-all.ps1

```powershell
# Save as: update-all.ps1

Write-Host "Updating all submodules to latest..." -ForegroundColor Green

# Update backend
git pull origin main

# Update frontend submodule
git submodule update --remote --merge

# Show what was updated
git status

Write-Host "Done! Run: git add . && git commit -m 'Update submodules'" -ForegroundColor Yellow
```

Run it:
```powershell
.\update-all.ps1
```

---

## 📋 CHECKLIST - Before Pushing

Before each push, verify:

- [ ] Backend changes committed
- [ ] Frontend changes pushed (separate repo)
- [ ] Submodule points to correct frontend commit
- [ ] `.gitmodules` file not modified unexpectedly
- [ ] Run `docker-compose build` to test both together
- [ ] All changes pushed to both repos

---

## 🚀 RECOMMENDED WORKFLOW

### Daily Development

```powershell
# Morning: Get latest everything
git pull --recurse-submodules origin main

# Work in backend
cd C:\Users\bfrancis\projects\extractpdftext
# ... make changes ...
git add .
git commit -m "Feature description"

# Work in frontend
cd frontend
# ... make changes ...
git add .
git commit -m "Feature description"
git push origin main

# Back to backend
cd ..
git add frontend
git commit -m "Update frontend"
git push origin main

# Evening: Test everything with Docker
docker-compose build
docker-compose up -d
docker-compose ps
```

---

## 💾 BACKUP REPOS

Keep both repos backed up:

```powershell
# Backup backend
git push origin main

# Backup frontend (from submodule folder)
cd frontend
git push origin main
cd ..
```

---

## 🔐 SECURITY NOTE

**Submodule URL Best Practices:**

```powershell
# If using private repos, configure SSH:
git config --global url."git@github.com:".insteadOf "https://github.com/"

# Then submodule URL can be:
git submodule add git@github.com:yourusername/frontend-repo.git frontend

# Or use personal access token in URL:
git submodule add https://username:token@github.com/yourusername/frontend-repo.git frontend
```

---

## 📚 REFERENCE COMMANDS

| Task | Command |
|------|---------|
| Add submodule | `git submodule add <url> <path>` |
| Init submodules | `git submodule init` |
| Update submodules | `git submodule update --remote` |
| Clone with submodules | `git clone --recursive <url>` |
| Pull with submodules | `git pull --recurse-submodules` |
| View submodule status | `git submodule status` |
| Update to latest | `git submodule update --remote --merge` |

---

## 🎯 SUCCESS INDICATORS

You'll know it's working when:

✅ `frontend` folder exists and shows as a separate repo  
✅ `.gitmodules` file exists with correct URL  
✅ `docker-compose build` works without issues  
✅ `git status` shows both repos sync'd  
✅ Changes in frontend repo reflect when you update submodule  
✅ Pushing backend also pins frontend commit version  

---

## 🆘 STILL HAVING ISSUES?

Check these files:
1. `.gitmodules` - Should exist in backend root
2. `.git/config` - Should have submodule references
3. `frontend/.git` - Should be a file, not folder

If frontend/.git is a folder (not file), something went wrong:
```powershell
# Remove and re-add
Remove-Item frontend -Recurse
git reset HEAD frontend
git submodule add https://github.com/yourusername/frontend-repo.git frontend
```

---

## 📞 QUICK REFERENCE

**I have separate repos and want to:**

- **Sync both together** → `git pull --recurse-submodules`
- **Update frontend** → Go to frontend folder, work, push, then `git add frontend` in backend
- **See what changed** → `git submodule status`
- **Deploy together** → `docker-compose build` (works automatically)
- **Release version** → Create tags in both repos pointing to same commit
- **Clone project** → `git clone --recursive <backend-url>`

---

**You're now coordinated! Both repos work together seamlessly.** 🚀


