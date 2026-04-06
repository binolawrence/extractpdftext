# ✅ Submodule Setup Checklist - Step by Step

## 🎯 YOUR SITUATION

- **Backend Repo:** `https://github.com/yourusername/extractpdftext.git`
- **Frontend Repo:** `https://github.com/yourusername/frontend-repo.git` (SEPARATE)
- **Goal:** Keep them coordinated in Docker

---

## ✅ SETUP STEPS (Do These Once)

### Step 1: Verify You're in Backend Directory
```powershell
cd C:\Users\bfrancis\projects\extractpdftext
pwd  # Should show extractpdftext folder
```
- [ ] Confirmed in correct directory

### Step 2: Check if Frontend Already Exists Locally

```powershell
# If frontend folder exists but NOT as submodule:
ls -la frontend  # (or dir frontend)

# If it shows as a linked folder/submodule:
git submodule status
```
- [ ] Checked current state

### Step 3: Add Frontend as Submodule

```powershell
# Replace with YOUR actual frontend repo URL
git submodule add https://github.com/yourusername/frontend-repo.git frontend
```

**Expected output:**
```
Cloning into 'frontend'...
remote: Enumerating objects...
[submodule "frontend"]
    path = frontend
    url = https://github.com/yourusername/frontend-repo.git
```

- [ ] Submodule added successfully

### Step 4: Verify Files Created

```powershell
# Check these files exist:
Get-Content .gitmodules
ls -la frontend
```

**Should show:**
- `.gitmodules` file with frontend configuration
- `frontend` folder with your frontend code

- [ ] Files verified

### Step 5: Commit to Backend Repo

```powershell
git add .gitmodules frontend
git commit -m "Add frontend as submodule - coordinating separate repos"
git push origin main
```

**Expected output:**
```
[main xxxxx] Add frontend as submodule
 2 files changed, 4 insertions(+)
 create mode 100644 .gitmodules
 create mode 160000 frontend
```

- [ ] Committed and pushed to backend

### Step 6: Verify in GitHub

1. Go to: `https://github.com/yourusername/extractpdftext`
2. Look for `frontend` folder with @ symbol (indicates submodule)
3. `.gitmodules` file should be visible

- [ ] Verified in GitHub

---

## 🔄 DAILY WORKFLOW

### When Working on Backend Only

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git add your-backend-files
git commit -m "Backend changes"
git push origin main
```
- [ ] Backend changes pushed

### When Working on Frontend Only

```powershell
# Go to frontend submodule
cd C:\Users\bfrancis\projects\extractpdftext\frontend

# Make and commit changes
git add .
git commit -m "Frontend changes"
git push origin main  # This pushes to FRONTEND repo

# Go back to backend
cd ..

# Update backend to point to latest frontend
git add frontend
git commit -m "Update frontend submodule"
git push origin main  # This pushes to BACKEND repo
```
- [ ] Frontend changes pushed to frontend repo
- [ ] Backend updated to track new frontend version

### When Working on Both

```powershell
# 1. Start in backend root
cd C:\Users\bfrancis\projects\extractpdftext

# 2. Make backend changes
git add backend-files
git commit -m "Backend feature X"

# 3. Make frontend changes
cd frontend
git add .
git commit -m "Frontend for feature X"
git push origin main

# 4. Go back to backend
cd ..

# 5. Update backend to latest frontend
git add frontend
git commit -m "Update frontend for feature X"

# 6. Push both updates
git push origin main
```
- [ ] Both repos updated and coordinated

---

## 🐳 DOCKER VERIFICATION

Your Docker setup already works! Test it:

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# Build both services
docker-compose build

# Start services
docker-compose up -d

# Verify running
docker-compose ps
```

Expected output:
```
CONTAINER ID   NAMES                    STATUS
xxxxx          extractpdftext-backend   Up (healthy)
xxxxx          extractpdftext-frontend  Up
xxxxx          extractpdftext-nginx     Up
```

- [ ] Docker builds successfully
- [ ] All services running

---

## 📥 PULLING UPDATES (Getting Latest From Both)

### Simple: Pull Backend with Frontend

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# This pulls backend AND updates frontend submodule
git pull --recurse-submodules origin main
```

### Manual: Update Submodule Separately

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# Fetch backend updates
git fetch origin

# Update frontend submodule to latest
git submodule update --remote --merge
```

- [ ] Updates pulled successfully

---

## ⚠️ TROUBLESHOOTING

### Problem: "frontend" folder shows empty or weird

**Solution:**
```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git submodule init
git submodule update
```

- [ ] Issue resolved

### Problem: Frontend changes not showing in backend

**Solution:**
```powershell
# Verify you're in frontend repo
cd C:\Users\bfrancis\projects\extractpdftext\frontend
git remote -v  # Should show YOUR frontend repo URL

# If wrong, fix it:
cd ..
git config submodule.frontend.url https://github.com/yourusername/correct-frontend-repo.git
git submodule sync
git submodule update --remote
```

- [ ] Issue resolved

### Problem: "git push" says nothing changed

**Solution:**
```powershell
# Make sure you're in correct directory
pwd

# Add ALL changes
git add -A

# Commit with message
git commit -m "Your message"

# Push
git push origin main
```

- [ ] Issue resolved

### Problem: Submodule URL is wrong

**Solution:**
```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# View current config
cat .gitmodules

# Or fix it:
git config submodule.frontend.url https://github.com/yourusername/frontend-repo.git
git config --file .gitmodules submodule.frontend.url https://github.com/yourusername/frontend-repo.git
git config -f .gitmodules --get submodule.frontend.url  # Verify

git add .gitmodules
git commit -m "Fix frontend submodule URL"
```

- [ ] Issue resolved

---

## 📊 STATUS CHECK

Run these commands to verify everything is working:

```powershell
cd C:\Users\bfrancis\projects\extractpdftext

# 1. Check backend status
git status

# 2. Check submodule status
git submodule status
# Output should show: <commit-hash> frontend (<branch>)

# 3. Check .gitmodules
Get-Content .gitmodules

# 4. Check frontend is a git repo
cd frontend
git remote -v
cd ..

# 5. Verify Docker still works
docker-compose config
```

All should work without errors!

- [ ] All status checks passed

---

## 🎯 FINAL VERIFICATION

```powershell
# 1. Both repos coordinated?
git submodule status  # Shows frontend commit

# 2. Can deploy together?
docker-compose build  # Completes successfully

# 3. Frontend changes sync?
cd frontend
git log --oneline -1  # Shows your latest commit

# 4. Backend tracks frontend version?
cd ..
git log --oneline -1 | grep -i frontend  # Or similar

# Should show your recent "Update frontend" commit
```

- [ ] All verifications passed

---

## 📋 QUICK REFERENCE - After Setup

| Need | Command |
|------|---------|
| Clone with both | `git clone --recursive https://github.com/yourusername/extractpdftext.git` |
| Update both | `git pull --recurse-submodules` |
| See frontend commit | `git submodule status` |
| Work in frontend | `cd frontend` then `git add .` and `git push` |
| Work in backend | `git add backend-files` and `git push` |
| Deploy | `docker-compose up -d` |
| Check all synced | `git submodule status` and `git status` |

---

## 🚀 YOU'RE DONE!

When all checkboxes are checked, your repos are:
- ✅ Coordinated together
- ✅ Working with Docker
- ✅ Independently versioned
- ✅ Easy to update
- ✅ Ready for team collaboration

**Next time someone clones your backend, they get:**
```powershell
git clone --recursive https://github.com/yourusername/extractpdftext.git
# Both backend AND frontend automatically!
```

🎉 **Your repos are now perfectly coordinated!**


