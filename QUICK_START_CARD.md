# 🚀 QUICK START CARD - Print This!

## For Separate Backend & Frontend Repos

---

## ⚡ 5 MINUTE SETUP

```powershell
# 1. Go to backend root
cd C:\Users\bfrancis\projects\extractpdftext

# 2. Add frontend as submodule
git submodule add https://github.com/yourusername/frontend-repo.git frontend

# 3. Commit it
git add .gitmodules frontend
git commit -m "Add frontend as submodule"
git push origin main

# 4. Test deployment
docker-compose build
docker-compose up -d

# 5. Verify
docker-compose ps

# DONE! ✅
```

---

## 🔄 DAILY COMMANDS

### Backend Only
```powershell
git add . && git commit -m "Message" && git push
```

### Frontend Only
```powershell
cd frontend && git add . && git commit -m "Message" && git push && cd ..
git add frontend && git commit -m "Update" && git push
```

### Both Repos
```powershell
git pull --recurse-submodules origin main
# Make changes
# Commit + push both
```

### Check Status
```powershell
git status
git submodule status
docker-compose ps
```

---

## 🐳 DOCKER COMMANDS

```powershell
# Build
docker-compose build

# Start
docker-compose up -d

# Status
docker-compose ps

# Logs
docker-compose logs -f

# Stop
docker-compose stop

# Clean
docker-compose down

# Access
http://localhost:8080     # Backend
http://localhost:3000     # Frontend
http://localhost          # Nginx proxy
```

---

## 📋 VERIFY SETUP

- [ ] `.gitmodules` exists in backend root
- [ ] `frontend/` folder has React files
- [ ] `git submodule status` shows commit hash
- [ ] `docker-compose ps` shows 3 services
- [ ] `http://localhost` loads in browser

---

## ⚠️ TROUBLESHOOTING

| Issue | Fix |
|-------|-----|
| Frontend empty | `git submodule init && git submodule update` |
| Can't push frontend | `cd frontend` first, then push |
| Docker fails | Check `frontend/Dockerfile` exists |
| Merge conflicts | Usually in `.gitmodules`, accept theirs |
| Check frontend version | `git submodule status` |

---

## 📚 DETAILED GUIDES

- `SEPARATE_REPOS_COMPLETE_GUIDE.md` - Full guide
- `SUBMODULE_COMMANDS.sh` - All commands
- `SUBMODULE_SETUP_CHECKLIST.md` - Step-by-step
- `SUBMODULE_VISUAL_GUIDE.md` - Diagrams

---

## 🎯 THE SOLUTION

**Git Submodules** = Separate repos that stay coordinated

```
Backend Repo
└── frontend/ (→ Link to Frontend Repo)
    
When you push:
- Backend changes → Backend repo
- Frontend changes → Frontend repo  
- Backend tracks frontend version

When you deploy:
docker-compose build  # Builds both
docker-compose up -d  # Deploys both
```

---

## ✨ KEY POINTS

✅ Each repo completely independent  
✅ Backend knows frontend version  
✅ Docker handles both automatically  
✅ Clone gets both repos (--recursive)  
✅ Team-friendly setup  
✅ Production-ready  

---

**Print this card and keep it handy!** 📌


