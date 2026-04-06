# 🚀 Docker Quick Reference Card

## Files Created

| File | Purpose |
|------|---------|
| `Dockerfile` | Spring Boot backend containerization |
| `docker-compose.yml` | Orchestrate all services |
| `nginx.conf` | Reverse proxy configuration |
| `frontend.Dockerfile` | React frontend containerization |
| `.dockerignore` | Exclude files from Docker build |
| `.env.example` | Environment variables template |
| `application-docker.properties` | Docker-specific Spring config |
| `docker-start.ps1` | Interactive PowerShell menu |
| `docker-start.bat` | Interactive Windows batch menu |
| `docker-start.sh` | Interactive Bash menu |
| `DOCKER_SETUP_GUIDE.md` | Comprehensive documentation |
| `DOCKER_IMPLEMENTATION_SUMMARY.md` | Implementation overview |
| `DOCKER_COMMANDS.ps1` | Command reference |

---

## 🎯 Get Started in 30 Seconds

### Option 1: Interactive Menu (Recommended)
```powershell
.\docker-start.ps1
```
Then choose option 2 to start services.

### Option 2: Manual Commands
```powershell
# Build images
docker-compose build

# Start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

---

## 📍 Access Points

```
Frontend:     http://localhost:3000
Backend:      http://localhost:8080
Via Nginx:    http://localhost
Health Check: http://localhost:8080/actuator/health
```

---

## 🔧 Essential Commands

| Command | What it does |
|---------|-------------|
| `docker-compose build` | Build all Docker images |
| `docker-compose up -d` | Start all services in background |
| `docker-compose down` | Stop and remove containers |
| `docker-compose logs -f` | View live logs from all services |
| `docker-compose ps` | Show running services status |
| `docker-compose exec backend bash` | Access backend container shell |

---

## 📊 Services

| Service | Port | Image |
|---------|------|-------|
| Backend | 8080 | Spring Boot (Java 17) |
| Frontend | 3000 | React (Node.js 18) |
| Nginx | 80 | Alpine Nginx |

---

## 🐛 Quick Troubleshooting

**Port already in use?**
```powershell
netstat -ano | findstr :8080
Stop-Process -Id <PID> -Force
```

**Container won't start?**
```powershell
docker-compose logs backend
docker-compose build --no-cache backend
```

**Need to clean everything?**
```powershell
docker-compose down -v
docker system prune -a --force
```

---

## 📖 Full Documentation

- **Setup Guide**: `DOCKER_SETUP_GUIDE.md`
- **Implementation**: `DOCKER_IMPLEMENTATION_SUMMARY.md`
- **Commands**: `DOCKER_COMMANDS.ps1`

---

## ✨ Architecture at a Glance

```
    User
      ↓
   :80 (Nginx)
      ↓
  ┌─────────────┐
  │   :3000     │  ← Frontend (React)
  │   :8080     │  ← Backend (Spring Boot)
  └─────────────┘
      ↓
  ┌─────────────┐
  │  Volumes    │  ← Persistent storage
  │ PDF Storage │
  │ Lucene Index│
  └─────────────┘
```

---

## 🎓 Learning Resources

- Docker Compose Docs: https://docs.docker.com/compose/
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker/
- Nginx: https://nginx.org/en/docs/

---

**Everything is ready! Start with:**
```powershell
.\docker-start.ps1
```

Choose option 2 to launch your services! 🚀

