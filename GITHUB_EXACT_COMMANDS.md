# GitHub Checkin - Exact PowerShell Commands

## Prerequisites Checklist

- [ ] GitHub account created (https://github.com)
- [ ] Git installed on your machine
- [ ] GitHub repository created at https://github.com/new
- [ ] Personal Access Token generated

---

## IMPORTANT: Before You Start

### 1. Create GitHub Repository
- Go to https://github.com/new
- Enter repository name: `extractpdftext`
- Choose Public or Private
- Click "Create repository"
- **COPY THE HTTPS URL** - You'll need it in Step 5 below

Example URL: `https://github.com/YOUR_USERNAME/extractpdftext.git`

### 2. Generate Personal Access Token
- Go to https://github.com/settings/tokens
- Click "Tokens (classic)"
- Click "Generate new token (classic)"
- Name: `extractpdftext`
- Select scope: `repo`
- Click "Generate token"
- **COPY AND SAVE THE TOKEN** - You'll paste it when git asks for password

---

## THE COMMANDS

### Open PowerShell

Press `Win + R`, type `powershell`, press Enter

Or right-click on desktop → Open PowerShell here

---

### Command 1: Navigate to Project

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
```

**Hit Enter**

---

### Command 2: Verify Git is Installed

```powershell
git --version
```

**Hit Enter**

Should show something like: `git version 2.40.0.windows.1`

---

### Command 3: Check Current Status

```powershell
git status
```

**Hit Enter**

---

### Command 4: Initialize Git

```powershell
git init
```

**Hit Enter**

---

### Command 5: Add Remote Repository

**IMPORTANT: Replace `YOUR_USERNAME` with your actual GitHub username**

```powershell
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

**Example:**
```powershell
git remote add origin https://github.com/bfrancis/extractpdftext.git
```

**Hit Enter**

---

### Command 6: Verify Remote Was Added

```powershell
git remote -v
```

**Hit Enter**

Should show:
```
origin  https://github.com/YOUR_USERNAME/extractpdftext.git (fetch)
origin  https://github.com/YOUR_USERNAME/extractpdftext.git (push)
```

---

### Command 7: Stage All Files

```powershell
git add .
```

**Hit Enter**

---

### Command 8: Check Status

```powershell
git status
```

**Hit Enter**

Should show files ready to commit (green text with "new file")

---

### Command 9: Create First Commit

```powershell
git commit -m "Initial commit: PDF text extraction application with search and display functionality"
```

**Hit Enter**

Should show: `X files changed, Y insertions(+)`

---

### Command 10: Set Main Branch

```powershell
git branch -M main
```

**Hit Enter**

---

### Command 11: Push to GitHub

```powershell
git push -u origin main
```

**Hit Enter**

---

### Command 12: Enter Credentials

When prompted:

**First prompt - Username:**
```
Username for 'https://github.com': 
```

Type your GitHub username and press Enter

**Second prompt - Password:**
```
Password for 'https://github.com/YOUR_USERNAME': 
```

**Paste your Personal Access Token** (the one you created earlier) and press Enter

**Note:** The password won't show as you type - that's normal!

---

## What Happens Next

Git will show:
```
Enumerating objects: XXX, done.
Counting objects: 100% (XXX/XXX), done.
...
 * [new branch]      main -> main
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

**This means SUCCESS! ✅**

---

## Verify on GitHub

1. Open browser
2. Go to `https://github.com/YOUR_USERNAME/extractpdftext`
3. You should see all your project files!
4. Click on files to view them
5. Click "X commits" to see commit history

---

## Copy-Paste Entire Sequence

If you want to run everything at once (just paste into PowerShell):

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git --version
git status
git init
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
git remote -v
git add .
git status
git commit -m "Initial commit: PDF text extraction application"
git branch -M main
git push -u origin main
```

**Don't forget to:**
1. Replace `YOUR_USERNAME` with your actual GitHub username
2. Have your Personal Access Token ready for when it asks

---

## After First Push

Next time you want to update:

```powershell
cd C:\Users\bfrancis\projects\extractpdftext
git add .
git commit -m "Your change description"
git push
```

That's it - no more `-u origin main` needed!

---

## Troubleshooting

### Error: "fatal: remote origin already exists"
```powershell
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/extractpdftext.git
```

### Error: "fatal: not a git repository"
```powershell
git init
```

### Error: "Authentication failed"
- Make sure you're using Personal Access Token (not your GitHub password)
- Create a new token at https://github.com/settings/tokens

### Error: "fatal: 'main' does not appear to be a git repository"
- Make sure you're in the correct directory
- Check: `pwd` (should show extractpdftext folder)

---

## Files That Will Be Pushed

**See in your GitHub repo:**
- ✅ src/ folder (all Java code)
- ✅ pom.xml
- ✅ mvnw and mvnw.cmd
- ✅ .gitignore
- ✅ All documentation files (.md files)

**NOT pushed (excluded by .gitignore):**
- ❌ target/ (build files)
- ❌ .idea/ (IDE settings)
- ❌ lucene-index/ (indexes)
- ❌ *.log files

---

## You're All Set! 🚀

Everything is ready. Just follow the commands above and your project will be on GitHub!

Questions? Check the other guide files:
- `GITHUB_CHECKIN_GUIDE.md` - Detailed explanations
- `GITHUB_QUICK_REFERENCE.md` - Another reference format


