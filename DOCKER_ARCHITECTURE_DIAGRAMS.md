# Docker Architecture & Deployment Diagrams

## 1. Application Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                       Docker Network                           │
│              (extractpdf-network - bridge)                     │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │                    NGINX (Port 80)                       │ │
│  │            - Reverse Proxy                               │ │
│  │            - Request Routing                             │ │
│  │            - CORS Handling                               │ │
│  │            - Rate Limiting                               │ │
│  └────────────┬────────────────────────┬────────────────────┘ │
│               │                        │                      │
│       ┌───────▼─────────┐    ┌────────▼─────────┐             │
│       │  Frontend       │    │   Backend        │             │
│       │  (Port 3000)    │    │  (Port 8080)     │             │
│       │                 │    │                  │             │
│       │ React App       │    │ Spring Boot      │             │
│       │ Node.js 18      │    │ Java 17          │             │
│       │                 │    │                  │             │
│       │ Build: npm      │    │ Build: Maven     │             │
│       │ Runtime: serve  │    │ Runtime: JRE     │             │
│       └─────────────────┘    └────────┬─────────┘             │
│                                       │                       │
│                        ┌──────────────┴──────────────┐         │
│                        │    Persistent Volumes      │         │
│                        │                            │         │
│                        │  - pdf-storage             │         │
│                        │  - lucene-index            │         │
│                        │                            │         │
│                        └────────────────────────────┘         │
│                                                                │
└────────────────────────────────────────────────────────────────┘
         ▲
         │ Port 80
         │
    External Requests
```

---

## 2. Container Lifecycle

```
                    ┌──────────────┐
                    │   Git Push   │
                    └──────┬───────┘
                           │
                    ┌──────▼────────┐
                    │docker-compose │
                    │    build      │
                    └──────┬────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        │                  │                  │
   ┌────▼─────┐    ┌──────▼──────┐    ┌─────▼────┐
   │ Build    │    │   Build     │    │  Build   │
   │ Frontend │    │   Backend   │    │  Nginx   │
   │ Image    │    │   Image     │    │  Image   │
   └────┬─────┘    └──────┬──────┘    └─────┬────┘
        │                 │                 │
        └────────────┬────┴─────────────────┘
                     │
            ┌────────▼────────┐
            │docker-compose up│
            │      -d         │
            └────────┬────────┘
                     │
        ┌────┬──────┬┴────┬─────┐
        │    │      │     │     │
   ┌────▼─┐ ┌┴──┐ ┌┴───┐│┌──┐  │
   │Frontend│Backend│Nginx││Vol.│  Healthy & Running
   │Running │Running │Running│  │
   └────────┴───────┴──────┴──┘
```

---

## 3. Build Process - Multi-Stage (Backend)

```
Source Code (pom.xml, src/)
        │
        ▼
┌─────────────────────────┐
│  Stage 1: BUILDER       │
├─────────────────────────┤
│ Image: maven:3.9        │
│ - Copy pom.xml          │
│ - mvn dependency:go...  │
│ - Copy source code      │
│ - mvn clean package     │
│ ✓ Creates JAR file      │
└─────────────┬───────────┘
              │ JAR (extractpdftext-x.x.x.jar)
              ▼
        ┌──────────────┐
        │ Intermediate │
        │   Container  │
        └──────┬───────┘
               │
        ┌──────▼──────────────┐
        │  Stage 2: RUNTIME   │
        ├─────────────────────┤
        │ Image: alpine:17    │
        │ - Copy JAR from     │
        │   builder stage     │
        │ - Install curl      │
        │ - Expose port 8080  │
        │ ✓ Final Image       │
        └──────┬──────────────┘
               │
      ┌────────▼────────┐
      │ Final Image     │
      │ Size: ~250MB    │
      │ Ready to Deploy │
      └─────────────────┘
```

---

## 4. Data Flow

```
┌─────────────────┐
│   User/Browser  │
└────────┬────────┘
         │
         │ HTTP :80
         ▼
      ┌─────────────────────────┐
      │   NGINX Reverse Proxy   │
      ├─────────────────────────┤
      │ Listens on port 80      │
      │ - Routes / → Frontend   │
      │ - Routes /api/* → Back  │
      └──┬──────────────────┬───┘
         │                  │
         │                  │
   :3000 │                  │ :8080
         │                  │
    ┌────▼────────┐    ┌───▼──────────┐
    │  Frontend   │    │  Backend     │
    │  (React)    │    │  (Spring)    │
    │             │    │              │
    │  Serves UI  │    │  REST API    │
    │  Components │    │  Processing  │
    │  Pages      │    │              │
    └─────┬───────┘    └──┬───────────┘
          │                │
          │                │
          │           ┌────▼────────────┐
          │           │ Persistent Vol. │
          │           ├─────────────────┤
          │           │ - PDF Files     │
          │           │ - Index Data    │
          │           └─────────────────┘
          │
          └─────────► Browser Cache
```

---

## 5. Environment Setup

```
Local Development Machine
├── Windows 10/11
├── Docker Desktop Installed
├── PowerShell / Command Line
└── Project Root Directory
    │
    ├── docker-compose.yml ──► Orchestrates containers
    ├── Dockerfile ──────────► Backend image recipe
    ├── frontend/Dockerfile ─► Frontend image recipe
    ├── nginx.conf ──────────► Proxy configuration
    ├── pom.xml ─────────────► Maven dependencies
    ├── src/ ────────────────► Application code
    └── frontend/ ──────────► React application
```

---

## 6. Volume Mounting

```
Host Machine                    Docker Container
┌────────────────────┐         ┌────────────────┐
│                    │         │                │
│  (Windows)         │         │   Backend      │
│  C:\projects\...   │         │   Container    │
│                    │         │                │
│  ┌──────────────┐  │         │  ┌──────────┐  │
│  │ src/         │  │  Bind   │  │ /app/src │  │
│  │ pom.xml      │  │<───────>│  │          │  │
│  │ Dockerfile   │  │ Mount   │  │          │  │
│  └──────────────┘  │         │  └──────────┘  │
│                    │         │                │
│  Docker Volumes    │         │  ┌──────────┐  │
│  ┌──────────────┐  │         │  │ /app/... │  │
│  │ pdf-storage  │  │ Docker  │  │ storage/ │  │
│  │ lucene-index │  │<────────┤  │ lucene/  │  │
│  └──────────────┘  │ Volume  │  └──────────┘  │
│                    │ Mount   │                │
└────────────────────┘         └────────────────┘
```

---

## 7. Network Communication

```
                    Docker Network
                   (bridge network)
                  extractpdf-network
                         │
         ┌───────────────┼───────────────┐
         │               │               │
    ┌────▼────┐    ┌─────▼──────┐   ┌───▼────┐
    │ Frontend │    │  Backend   │   │ Nginx  │
    │:3000    │    │  :8080     │   │ :80    │
    │         │    │            │   │        │
    │ Hostname│    │ Hostname   │   │ Host   │
    │=        │    │ =          │   │ name   │
    │frontend │    │ backend    │   │=      │
    │         │    │            │   │nginx  │
    └─────┬───┘    └──────┬─────┘   └───┬───┘
          │               │             │
          └───────────────┼─────────────┘
                    │
            Can communicate:
        frontend:3000, backend:8080,
        nginx:80 via container name
        and port numbers
```

---

## 8. Docker Compose Service Dependencies

```
                docker-compose up
                        │
            ┌───────────┼───────────┐
            │           │           │
            ▼           ▼           ▼
    ┌──────────┐  ┌──────────┐ ┌──────────┐
    │Frontend  │  │Backend   │ │ Nginx    │
    │          │  │          │ │          │
    │Start ✓   │  │Start ✓   │ │Wait for: │
    │          │  │          │ │Backend   │
    │Depends:  │  │Health:   │ │Frontend  │
    │Backend   │  │Check     │ │          │
    │(healthy) │  │ every    │ │Start ✓   │
    │          │  │ 30s      │ │          │
    └──────────┘  └──────────┘ └──────────┘
         │             │             │
         │ Only after  │             │
         │ backend     │             │
         │ healthy ✓   │             │
         │             │             │
         └─────────────┴─────────────┘
                 │
         All services running
         and healthy
```

---

## 9. Deployment Workflow

```
Development
    │
    ├─► Make code changes
    │
    ├─► docker-compose build (rebuilds images)
    │
    ├─► docker-compose up -d (starts containers)
    │
    ├─► docker-compose logs -f (monitor logs)
    │
    ├─► Test application
    │    - http://localhost:3000
    │    - http://localhost:8080
    │    - http://localhost
    │
    ├─► git add .
    │
    ├─► git commit -m "message"
    │
    ├─► git push origin main
    │
    └─► Deploy to production (docker-compose on server)
```

---

## 10. Health Check Flow

```
Docker Engine
    │
    ├─► Check Backend Health
    │   └─► curl http://backend:8080/actuator/health
    │       │
    │       ├─► 200 OK ✓ (healthy)
    │       │   Continue execution
    │       │
    │       └─► 500+ ✗ (unhealthy)
    │           Restart container
    │
    ├─► Check Frontend Health
    │   └─► wget http://frontend:3000
    │       │
    │       ├─► 200 OK ✓ (healthy)
    │       │
    │       └─► Fail ✗
    │           Retry
    │
    └─► Check Nginx Health
        └─► tcp :80 reachable ✓
```

---

## 11. Complete Build & Deploy Sequence

```
User runs: docker-compose up -d

    1. Read docker-compose.yml
            │
            ├─► Backend Service
            │   ├─ Build from Dockerfile
            │   ├─ Create image: extractpdftext:latest
            │   ├─ Create container
            │   ├─ Mount volumes
            │   ├─ Set environment variables
            │   ├─ Expose port 8080
            │   ├─ Start container
            │   ├─ Wait for health check
            │   └─ Status: Running ✓
            │
            ├─► Frontend Service
            │   ├─ Build from frontend/Dockerfile
            │   ├─ Create image: extractpdftext-frontend:latest
            │   ├─ Create container
            │   ├─ Set environment variables
            │   ├─ Expose port 3000
            │   ├─ Wait for: Backend healthy ✓
            │   ├─ Start container
            │   └─ Status: Running ✓
            │
            └─► Nginx Service
                ├─ Pull nginx:alpine image
                ├─ Create container
                ├─ Mount nginx.conf
                ├─ Expose port 80
                ├─ Wait for: Backend & Frontend ✓
                ├─ Start container
                └─ Status: Running ✓

All Services Running & Ready ✓
Access: http://localhost
```

---

## Summary

- **3 Services**: Frontend, Backend, Nginx
- **3 Ports**: 3000, 8080, 80
- **2 Volumes**: pdf-storage, lucene-index
- **1 Network**: extractpdf-network (bridge)
- **Multi-stage builds** for optimization
- **Health checks** for reliability
- **Service dependencies** managed by Docker Compose

Everything is containerized, isolated, and easy to deploy! 🚀

