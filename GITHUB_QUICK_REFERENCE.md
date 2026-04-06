# Quick GitHub Checkin - Command Reference

## STEP BY STEP - Copy and Paste These Commands

### Step 1: Open PowerShell and Navigate to Project
```powershell
cd C:\Users\bfrancis\projects\extractpdftext
```

### Step 2: Check if Git is Installed
```powershell
git --version
```

### Step 3: Initialize Git (if not already done)
```powershell
git init
```

### Step 4: Check Current Git Status
```powershell
git status
```

### Step 5: Add Remote Repository

**First, create a repository on GitHub:**
- Go to https://github.com/new
- Repository name: `extractpdftext`
- Make it Public or Private (your choice)
- Click "Create repository"
- Copy the HTTPS URL (looks like: `https://github.com/USERNAME/extractpdftext.git`)

**Then run:**
```powershell
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

**Replace `YOUR_USERNAME` with your GitHub username**

Example:
```powershell
git remote add origin https://github.com/bfrancis/extractpdftext.git
```

### Step 6: Verify Remote Added
```powershell
git remote -v
```

Should show:
```
origin  https://github.com/YOUR_USERNAME/extractpdftext.git (fetch)
origin  https://github.com/YOUR_USERNAME/extractpdftext.git (push)
```

### Step 7: Stage All Files
```powershell
git add .
```

### Step 8: Check What Will Be Committed
```powershell
git status
```

### Step 9: Create First Commit
```powershell
git commit -m "Initial commit: PDF text extraction application with search and display functionality"
```

### Step 10: Create Main Branch and Push
```powershell
git branch -M main
git push -u origin main
```

### Step 11: Enter Your GitHub Credentials

When prompted:
- **Username**: Your GitHub username
- **Password**: Your GitHub Personal Access Token

To create a Personal Access Token:
1. Go to GitHub.com → Settings → Developer settings → Personal access tokens
2. Click "Tokens (classic)"
3. Click "Generate new token (classic)"
4. Name it "extractpdftext"
5. Select scopes: `repo`
6. Click "Generate token"
7. Copy the token and paste it when git asks for password

### Step 12: Verify on GitHub

Open browser and go to:
```
https://github.com/YOUR_USERNAME/extractpdftext
```

You should see all your project files!

---

## All Commands in Sequence (Copy & Paste)

```powershell
# Navigate to project
cd C:\Users\bfrancis\projects\extractpdftext

# Check git installed
git --version

# Initialize
git init

# Check status
git status

# Add remote (CHANGE YOUR_USERNAME TO YOUR GITHUB USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git

# Verify remote
git remote -v

# Stage files
git add .

# Check status again
git status

# Commit
git commit -m "Initial commit: PDF text extraction application"

# Set main branch and push
git branch -M main
git push -u origin main
```

When `git push` prompts for password, use your Personal Access Token (not your GitHub password).

---

## What Gets Pushed

✅ **Included:**
- All Java source code
- Maven POM file
- Application properties
- Documentation files
- .gitignore

❌ **Excluded (by .gitignore):**
- target/ folder (build artifacts)
- .idea/ folder (IDE config)
- lucene-index/ folder (index files)
- *.log files
- hs_err_*.log (JVM errors)

---

## Verify Success

After `git push` completes:
1. Go to https://github.com/YOUR_USERNAME/extractpdftext
2. You should see your files
3. Click on "X commits" to see commit history
4. Click on files to view them

---

## Troubleshooting

### "fatal: remote origin already exists"
```powershell
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

### "fatal: not a git repository"
```powershell
git init
```

### "permission denied" or "authentication failed"
- Make sure you're using Personal Access Token (not password)
- Or use SSH key setup

---

## Next Time (After Initial Checkin)

To update code on GitHub:
```powershell
git add .
git commit -m "Your message describing changes"
git push
```

That's it! No need for `-u origin main` next time.

---

## Recommended: Create README.md

Create a `README.md` file in project root with project description.
See GITHUB_CHECKIN_GUIDE.md for sample README content.

---

**You're ready to push to GitHub! 🚀**


