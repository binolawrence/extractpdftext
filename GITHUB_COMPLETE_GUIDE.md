# Complete GitHub Checkin Instructions - All You Need

## 🎯 YOUR GOAL
Push the Extract PDF Text project to GitHub

## ⏱️ TIME
10 minutes total

---

## PART 1: PREPARATION (5 minutes)

### Step A: Create GitHub Repository

1. Go to https://github.com/new
2. Fill in:
   - **Repository name:** `extractpdftext`
   - **Description:** (optional) PDF text extraction and search
   - **Public/Private:** Choose one
3. Click "Create repository"
4. **IMPORTANT:** Copy the HTTPS URL shown (looks like `https://github.com/YOUR_USERNAME/extractpdftext.git`)

### Step B: Create Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click "Tokens (classic)"
3. Click "Generate new token (classic)"
4. Fill in:
   - **Token name:** `extractpdftext`
   - **Expiration:** 90 days (or your choice)
   - **Select scopes:** Check `repo`
5. Click "Generate token"
6. **IMPORTANT:** Copy the token (you won't see it again!)

### Step C: Check Git Installation

Open PowerShell and run:
```powershell
git --version
```

Should show version like `git version 2.40.0.windows.1`

---

## PART 2: PUSH TO GITHUB (5 minutes)

### Open PowerShell

1. Press `Win + R`
2. Type `powershell`
3. Press Enter

### Run Commands

Navigate to your project:
```powershell
cd C:\Users\bfrancis\projects\extractpdftext
```

Initialize git:
```powershell
git init
```

Add remote (REPLACE `YOUR_USERNAME`):
```powershell
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

Example:
```powershell
git remote add origin https://github.com/bfrancis/extractpdftext.git
```

Verify it worked:
```powershell
git remote -v
```

Stage all files:
```powershell
git add .
```

Create commit:
```powershell
git commit -m "Initial commit: PDF text extraction application with search and display functionality"
```

Set main branch and push:
```powershell
git branch -M main
git push -u origin main
```

When prompted:
- **Username:** Your GitHub username
- **Password:** Your Personal Access Token (from Step B above)

---

## PART 3: VERIFY (1 minute)

Open browser and go to:
```
https://github.com/YOUR_USERNAME/extractpdftext
```

You should see:
- All your project files
- Commit history
- README and other files

**SUCCESS! ✅**

---

## ⚡ COPY-PASTE ALL COMMANDS

If you want to run everything at once, copy this entire block (but remember to replace YOUR_USERNAME):

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git init
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
git remote -v
git add .
git commit -m "Initial commit: PDF text extraction application"
git branch -M main
git push -u origin main
```

---

## ❌ COMMON ISSUES

### "fatal: remote origin already exists"
```powershell
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

### "fatal: not a git repository"
```powershell
git init
```

### "Authentication failed"
- Make sure you're using the **Personal Access Token** (not your password)
- Create a new token if needed

### "fatal: 'main' does not appear to be a git repository"
Make sure you're in the right directory:
```powershell
cd C:\Users\bfrancis\projects\extractpdftext
```

---

## 📚 FILES INCLUDED

**Will be uploaded:**
- ✅ src/ (Java code)
- ✅ pom.xml
- ✅ mvnw scripts
- ✅ Documentation

**Excluded by .gitignore:**
- ❌ target/ (build files)
- ❌ .idea/ (IDE config)
- ❌ lucene-index/ (indexes)
- ❌ *.log files

---

## 🔄 AFTER FIRST PUSH

To update your code:

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git add .
git commit -m "Your change description"
git push
```

---

## ✅ CHECKLIST

- [ ] GitHub account created
- [ ] Repository created on GitHub
- [ ] HTTPS URL copied
- [ ] Personal Access Token created
- [ ] Token saved safely
- [ ] Git installed
- [ ] PowerShell open
- [ ] Commands run successfully
- [ ] Verified on GitHub
- [ ] Backup complete!

---

## 🎉 YOU'RE DONE!

Your project is now on GitHub!

You can:
- Access it from anywhere
- Share it with others
- Track changes
- Back up your code
- Collaborate with team members

Next time, just use:
```powershell
git add .
git commit -m "Your message"
git push
```

---

## 📞 QUICK REFERENCE

| Task | Command |
|------|---------|
| Check status | `git status` |
| View history | `git log --oneline` |
| Stage files | `git add .` |
| Commit | `git commit -m "..."` |
| Push | `git push` |
| Check remote | `git remote -v` |
| Remove remote | `git remote remove origin` |

---

**That's it! Happy coding! 🚀**


