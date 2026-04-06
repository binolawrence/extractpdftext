# GitHub Checkin Steps - Complete Guide

## Prerequisites

1. **Git installed** - Check if installed:
   ```
   git --version
   ```
   
2. **GitHub account** - Create one at https://github.com if you don't have it

3. **Repository created on GitHub** - Create a new repo at https://github.com/new

---

## Step 1: Create .gitignore (Maven Project)

Create a `.gitignore` file in project root to exclude build artifacts:

```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Project specific
*.log
lucene-index/
index-folder/
hs_err_*.log
replay_*.log
```

---

## Step 2: Initialize Git (if not already done)

```bash
cd C:\Users\bfrancis\projects\extractpdftext
git init
```

---

## Step 3: Add Remote Repository

Replace `USERNAME` and `REPO_NAME` with your GitHub username and repository name:

```bash
git remote add origin https://github.com/USERNAME/REPO_NAME.git
```

**Example:**
```bash
git remote add origin https://github.com/bfrancis/extractpdftext.git
```

**Verify remote was added:**
```bash
git remote -v
```

---

## Step 4: Stage All Files

```bash
git add .
```

**Check what will be committed:**
```bash
git status
```

---

## Step 5: Create Initial Commit

```bash
git commit -m "Initial commit: PDF text extraction application

- PDF rendering and display functionality
- Search and indexing capabilities
- Text extraction utilities
- Generic pattern matching for data extraction
- Mobile-optimized PDF viewing at 100 DPI"
```

---

## Step 6: Push to GitHub

### For HTTPS (Username/Token):
```bash
git branch -M main
git push -u origin main
```

When prompted for credentials:
- **Username**: Your GitHub username
- **Password**: Your GitHub Personal Access Token (not password)

**To create a Personal Access Token:**
1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Click "Tokens (classic)"
3. Click "Generate new token"
4. Select scopes: `repo`, `write:repo_hook`
5. Click "Generate token"
6. Copy and save the token (you won't see it again)

### For SSH (Recommended):
If you prefer SSH, set it up first:
```bash
git remote set-url origin git@github.com:USERNAME/REPO_NAME.git
git push -u origin main
```

---

## Complete Workflow (All Steps Together)

```bash
# Navigate to project
cd C:\Users\bfrancis\projects\extractpdftext

# Initialize git
git init

# Add remote (replace USERNAME and REPO_NAME)
git remote add origin https://github.com/USERNAME/REPO_NAME.git

# Verify remote
git remote -v

# Stage all files
git add .

# Check status
git status

# Create commit
git commit -m "Initial commit: PDF text extraction application"

# Push to GitHub
git branch -M main
git push -u origin main
```

---

## Verify on GitHub

1. Go to your repository: `https://github.com/USERNAME/REPO_NAME`
2. You should see all your files
3. Click on specific files to view them
4. See commit history in "Commits"

---

## Update README on GitHub (Recommended)

Create a `README.md` file for your project:

```markdown
# Extract PDF Text

A Java Spring Boot application for extracting, searching, and displaying text from PDF documents.

## Features

- PDF text extraction and search
- Full-text indexing with Lucene
- Voter data extraction from voter roll PDFs
- RESTful API for search and retrieval
- Mobile-optimized PDF viewing
- Generic pattern matching for structured data extraction

## Technology Stack

- Java 17
- Spring Boot
- Apache PDFBox
- Apache Lucene
- Maven

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/com/extract/pdf/extractpdftext/
│   │   ├── controller/      # REST API endpoints
│   │   ├── service/         # Business logic
│   │   ├── util/            # Utility classes
│   │   └── pojo/            # Data models
│   └── resources/
│       └── application.properties
└── test/
    └── java/                # Unit tests
```

## API Endpoints

- `GET /pdf/search` - Search PDF documents
- `GET /pdf/view` - View PDF as image

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
```

---

## Troubleshooting

### Error: "fatal: remote origin already exists"
```bash
git remote remove origin
git remote add origin https://github.com/USERNAME/REPO_NAME.git
```

### Error: "Could not read from remote repository"
- Check if you're using HTTPS or SSH consistently
- Verify your Personal Access Token is correct (for HTTPS)
- Check SSH keys are configured (for SSH)

### Large Files Warning
If you get warnings about large files:
```bash
git lfs install
git lfs track "*.pdf"
git add .gitattributes
```

### Files Not Showing on GitHub
- Make sure files aren't in `.gitignore`
- Try: `git add -f filename` to force add

---

## Common Git Commands After Initial Push

### Check Git Status
```bash
git status
```

### Make Changes and Push Again
```bash
git add .
git commit -m "Your message"
git push
```

### View Commit History
```bash
git log --oneline
```

### Create a New Branch
```bash
git checkout -b feature-name
```

---

## Summary

1. ✅ Create `.gitignore`
2. ✅ Initialize Git: `git init`
3. ✅ Add remote: `git remote add origin ...`
4. ✅ Stage files: `git add .`
5. ✅ Commit: `git commit -m "..."`
6. ✅ Push: `git push -u origin main`
7. ✅ Verify on GitHub
8. ✅ (Optional) Create README.md

**Your project will be on GitHub! 🚀**


