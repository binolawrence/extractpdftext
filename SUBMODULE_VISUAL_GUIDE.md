# 📊 Git Submodule Architecture - Visual Guide

## The Big Picture

```
┌─────────────────────────────────────────────────────────────┐
│                    YOUR SETUP                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ GitHub Repository 1: extractpdftext (BACKEND)               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Files:                                                      │
│  ├── pom.xml                                                │
│  ├── src/                                                   │
│  ├── Dockerfile                                             │
│  ├── docker-compose.yml                                     │
│  ├── nginx.conf                                             │
│  ├── .gitmodules  ◄────── References frontend repo         │
│  ├── .git/                                                  │
│  └── frontend/  ◄────── SUBMODULE LINK (not real copy)     │
│                                                              │
│ When cloned, this folder links to:                         │
│ https://github.com/yourusername/frontend-repo.git          │
│                                                              │
└─────────────────────────────────────────────────────────────┘

                          ↓↓↓ LINKED ↓↓↓

┌─────────────────────────────────────────────────────────────┐
│ GitHub Repository 2: frontend-repo (FRONTEND)               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Files:                                                      │
│  ├── package.json                                           │
│  ├── src/                                                   │
│  ├── public/                                                │
│  ├── Dockerfile                                             │
│  ├── .git/                                                  │
│  └── ...frontend files...                                   │
│                                                              │
│ Independent repo with own:                                 │
│ • Commit history                                            │
│ • Branches                                                  │
│ • Pull requests                                             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## How They Stay Coordinated

### 1️⃣ DEVELOPMENT WORKFLOW

```
┌─ Your Computer ─┐
│                 │
│  ┌───────────────────────────────────────────┐
│  │ extractpdftext (Backend Repo Root)         │
│  ├───────────────────────────────────────────┤
│  │                                            │
│  │ Backend Development:                       │
│  │ ├─ Edit: src/main/java/...                │
│  │ ├─ Commit: "Backend feature"              │
│  │ └─ Push: to backend repo (Main)            │
│  │                                            │
│  │ Frontend Development:                      │
│  │ ├─ Go: cd frontend/                        │
│  │ ├─ Edit: src/App.jsx                       │
│  │ ├─ Commit: "Frontend feature"              │
│  │ └─ Push: to frontend repo (Main)           │
│  │                                            │
│  │ Coordinate Versions:                       │
│  │ ├─ Go: cd ..                               │
│  │ ├─ Add: git add frontend                   │
│  │ ├─ Commit: "Update frontend v1.2"          │
│  │ └─ Push: to backend repo (Main)            │
│  │                                            │
│  └───────────────────────────────────────────┘
│                                               │
└───────────────────────────────────────────────┘
```

### 2️⃣ COMMIT FLOW

```
Your Code Changes
       │
       ├─── Backend Changes
       │    ├─ Edit pom.xml
       │    ├─ Edit Java files
       │    └─ git add . && git commit
       │       │
       │       ▼
       │   Backend Repo Commit
       │   (extractpdftext main)
       │
       └─── Frontend Changes
            ├─ Edit React files
            ├─ Edit package.json
            └─ git add . && git commit
               │
               ▼
           Frontend Repo Commit
           (frontend-repo main)
               
               ▼
               
       Backend knows frontend version
       (via .gitmodules and commit hash)
```

### 3️⃣ PULL REQUEST WORKFLOW

```
Team Member's Changes:

1. Backend PR (to extractpdftext repo)
   ├─ Review & Merge to main
   ├─ Trigger CI/CD
   └─ Tag release

2. Frontend PR (to frontend-repo repo)
   ├─ Review & Merge to main
   ├─ Trigger CI/CD
   └─ Tag release

3. Backend Updates Submodule
   ├─ git pull --recurse-submodules
   ├─ Merges both PRs
   ├─ git add frontend
   ├─ git commit "Merge frontend updates"
   └─ Push to backend main
```

---

## Docker Orchestration

```
┌──────────────────────────────────────────────────────┐
│ docker-compose.yml (Backend Root)                    │
├──────────────────────────────────────────────────────┤
│                                                      │
│  services:                                           │
│                                                      │
│  backend:                                            │
│  ├─ build: context: .                               │
│  │         dockerfile: Dockerfile                   │
│  ├─ Creates: extractpdftext-backend image           │
│  ├─ Port: 8080                                      │
│  └─ Volume: pdf-storage, lucene-index               │
│                                                      │
│  frontend:                                           │
│  ├─ build: context: ./frontend  ◄── SUBMODULE       │
│  │         dockerfile: Dockerfile                   │
│  ├─ Creates: extractpdftext-frontend image          │
│  ├─ Port: 3000                                      │
│  └─ Env: REACT_APP_API_URL=backend:8080             │
│                                                      │
│  nginx:                                              │
│  ├─ image: nginx:alpine                             │
│  ├─ Port: 80 → backend:8080 or frontend:3000        │
│  └─ Config: nginx.conf                              │
│                                                      │
│  networks:                                           │
│  └─ extractpdf-network: bridge                      │
│                                                      │
└──────────────────────────────────────────────────────┘
       │
       ├─────────────────────────────────┐
       │                                 │
       ▼                                 ▼
    Backend Container              Frontend Container
    (extractpdftext:8080)           (node:3000)
       │                                 │
       └─────────────────────────────────┘
               │
               ▼ (nginx:80)
         Access: localhost


When you run:
docker-compose build
└─ Automatically:
   1. Builds from ./Dockerfile (backend)
   2. Builds from ./frontend/Dockerfile (frontend via submodule)
   3. Starts all 3 services
   4. Connects them via network
```

---

## Version Tracking

```
Release Timeline:

┌─────────────────────────────────────────────────────┐
│ 2026-04-06 - Version 1.0.0                          │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Backend:                                            │
│ ├─ Repo: extractpdftext                            │
│ ├─ Commit: abc123def456                            │
│ ├─ Tag: v1.0.0                                     │
│ └─ Features: API v1, PDF extraction                │
│                                                     │
│ Frontend:                                           │
│ ├─ Repo: frontend-repo                             │
│ ├─ Commit: xyz789abc123                            │
│ ├─ Tag: v1.0.0                                     │
│ └─ Features: React UI, dark mode                   │
│                                                     │
│ Submodule Reference:                                │
│ ├─ Backend .gitmodules points to:                  │
│ └─ frontend-repo @ xyz789abc123 (v1.0.0)           │
│                                                     │
│ Docker Image:                                       │
│ ├─ extractpdftext:1.0.0                            │
│ │  ├─ Backend from abc123def456                    │
│ │  └─ Frontend from xyz789abc123                   │
│ └─ Combined version: 1.0.0                         │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## File Structure on Your Computer

```
C:\Users\bfrancis\projects\
│
├── extractpdftext/              ◄── BACKEND REPO (git clone)
│   │
│   ├── .git/                    ◄── Backend repo metadata
│   ├── .gitmodules              ◄── Submodule configuration
│   ├── pom.xml
│   ├── src/
│   ├── Dockerfile               
│   ├── docker-compose.yml       
│   │
│   └── frontend/                ◄── SUBMODULE (not real clone)
│       │
│       ├── .git                 ◄── Link to frontend repo
│       ├── package.json
│       ├── src/
│       ├── Dockerfile
│       └── ...frontend files...
│
└── frontend-repo/               (Optional: separate clone elsewhere)
    ├── .git/
    ├── package.json
    └── ...same files as above...
```

---

## Data Flow - From User to Containers

```
┌──────────────┐
│   Browser    │
│ localhost:80 │
└────────┬─────┘
         │
         ▼ (HTTP request)
    ┌─────────────┐
    │   Nginx     │ (Port 80)
    │   Proxy     │
    └────┬──────┬─┘
         │      │
    ┌────▼┐  ┌──▼──────┐
    │  /  │  │ /api/*  │
    ▼     ▼  ▼         ▼
 Frontend  Backend
 :3000     :8080
 (React)   (Spring)
 │         │
 ├─────┬───┘
 │     │
 ▼     ▼
Volumes
├── frontend build
├── PDF files
└── Lucene index

But where does it come from?

docker-compose.yml (points to)
    │
    ├─→ ./Dockerfile (Backend source)
    │   ├─→ from Maven 3.9
    │   ├─→ COPY ./src
    │   └─→ RUN mvn clean package
    │
    └─→ ./frontend/Dockerfile (Frontend source via SUBMODULE)
        ├─→ from node:18
        ├─→ COPY ./src from frontend/src
        ├─→ RUN npm build
        └─→ Serves built files

Both source directories come from your repos!
```

---

## Key Points Summary

| Aspect | Details |
|--------|---------|
| **Backend Location** | `https://github.com/yourusername/extractpdftext.git` |
| **Frontend Location** | `https://github.com/yourusername/frontend-repo.git` (SEPARATE) |
| **How Connected** | Git Submodule in `.gitmodules` |
| **Local Backend Path** | `C:\Users\bfrancis\projects\extractpdftext` |
| **Local Frontend Path** | `C:\Users\bfrancis\projects\extractpdftext\frontend` |
| **Frontend Changes** | Pushed to frontend repo (separate) |
| **Backend Coordination** | Backend commits track frontend version |
| **Docker Build** | Uses both repos to build images |
| **Deploy Command** | `docker-compose up -d` |
| **Version Control** | Each repo has own history + submodule link |

---

## Workflow Summary

```
Make Changes
    │
    ├─ Backend: git add . && git push
    │
    ├─ Frontend:
    │  ├─ cd frontend
    │  ├─ git add . && git push
    │  └─ cd ..
    │
    └─ Coordinate:
       ├─ git add frontend
       ├─ git commit "Update frontend"
       └─ git push

Both repos updated & coordinated! ✓

Deploy with:
docker-compose build && docker-compose up -d

Works perfectly! 🚀
```

---

This is how your separate repos stay coordinated together! 🎯


