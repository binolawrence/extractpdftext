# Git Submodule Commands - Copy & Paste Ready

# ============================================
# SETUP (DO ONCE)
# ============================================

# 1. Add frontend as submodule
git submodule add https://github.com/yourusername/frontend-repo.git frontend

# 2. Commit it
git add .gitmodules frontend
git commit -m "Add frontend as submodule"
git push origin main


# ============================================
# DAILY WORKFLOW
# ============================================

# Update both repos to latest
git pull --recurse-submodules origin main

# Work in backend, then push
git add .
git commit -m "Your message"
git push origin main

# Work in frontend
cd frontend
git add .
git commit -m "Frontend changes"
git push origin main
cd ..

# Update backend to track new frontend commit
git add frontend
git commit -m "Update frontend submodule"
git push origin main


# ============================================
# CLONING
# ============================================

# New developers clone WITH frontend included
git clone --recursive https://github.com/yourusername/extractpdftext.git


# ============================================
# PULLING UPDATES
# ============================================

# Pull backend and update frontend
git pull --recurse-submodules origin main

# OR manual update
git fetch origin
git submodule update --remote --merge


# ============================================
# STATUS & VERIFICATION
# ============================================

# See what commit frontend is at
git submodule status

# Detailed submodule info
git config --file .gitmodules --list

# Check frontend repo is valid
cd frontend
git remote -v
cd ..


# ============================================
# DOCKER DEPLOYMENT
# ============================================

# Build both backend and frontend
docker-compose build

# Start services
docker-compose up -d

# Verify running
docker-compose ps

# View logs
docker-compose logs -f


# ============================================
# TROUBLESHOOTING
# ============================================

# Frontend folder empty or not initialized
git submodule init
git submodule update

# Fix submodule URL
git config submodule.frontend.url https://github.com/yourusername/frontend-repo.git
git config --file .gitmodules submodule.frontend.url https://github.com/yourusername/frontend-repo.git
git config -f .gitmodules --get submodule.frontend.url

# Re-add if completely broken
git rm --cached frontend
git submodule add https://github.com/yourusername/frontend-repo.git frontend

# Remove a submodule entirely (if needed)
git rm frontend
git config --file .gitmodules --remove-section submodule.frontend
git add .gitmodules
git commit -m "Remove frontend submodule"


# ============================================
# ADVANCED
# ============================================

# Update all submodules recursively
git submodule foreach git pull origin main

# Update submodule to specific commit
cd frontend
git checkout <commit-hash>
cd ..
git add frontend
git commit -m "Pin frontend to specific commit"

# Clone with specific branch for submodule
git clone --recursive --branch develop https://github.com/yourusername/extractpdftext.git


