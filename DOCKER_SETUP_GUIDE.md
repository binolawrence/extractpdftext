# Docker & Docker Compose Setup Guide

## Overview

This guide covers dockerizing your Spring Boot backend application and combining it with a React frontend using Docker Compose. The setup includes:

- **Spring Boot Backend**: Java 17 application on port 8080
- **React Frontend**: Node.js React app on port 3000
- **Nginx Reverse Proxy**: Routes traffic on port 80
- **Volumes**: Persistent storage for PDFs and Lucene indexes

---

## Prerequisites

1. **Docker Desktop** - Download from https://www.docker.com/products/docker-desktop
2. **Docker Compose** - Included with Docker Desktop
3. Verify installation:
   ```bash
   docker --version
   docker-compose --version
   ```

---

## Project Structure

```
extractpdftext/
├── Dockerfile                 # Spring Boot backend
├── frontend.Dockerfile        # React frontend (if using React)
├── docker-compose.yml         # Orchestration file
├── nginx.conf                 # Nginx configuration
├── .dockerignore              # Files to exclude from Docker builds
├── pom.xml                    # Maven configuration
├── src/                       # Java source code
├── frontend/                  # React app (if exists)
│   ├── package.json
│   ├── src/
│   ├── public/
│   └── Dockerfile
└── ...
```

---

## Step 1: Verify Spring Boot Application

Update your `application.properties` to be Docker-friendly:

```properties
spring.application.name=extractpdftext
spring.profiles.active=${SPRING_PROFILES_ACTIVE:default}
server.port=${SERVER_PORT:8080}
server.servlet.context-path=/
```

Add to `pom.xml` (optional - for actuator health checks):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

---

## Step 2: Create React Frontend (if you don't have one)

If you need to create a React app to interact with the backend:

```bash
cd C:\Users\bfrancis\projects\extractpdftext
npx create-react-app frontend
cd frontend
npm install axios
```

Create `.env` file in frontend:
```
REACT_APP_API_URL=http://localhost:8080
```

---

## Step 3: Build Docker Images

Navigate to project root and build:

```bash
cd C:\Users\bfrancis\projects\extractpdftext

# Build all images
docker-compose build

# Or build specific service
docker-compose build backend
docker-compose build frontend
```

**Output:**
- Backend image tagged as `extractpdftext-backend:latest`
- Frontend image tagged as `extractpdftext-frontend:latest`

---

## Step 4: Run with Docker Compose

### Start all services:

```bash
docker-compose up -d
```

### View logs:

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f nginx
```

### Check service status:

```bash
docker-compose ps
```

### Expected output:

```
NAME                              STATUS         PORTS
extractpdftext-backend            Up (healthy)   0.0.0.0:8080->8080/tcp
extractpdftext-frontend           Up             0.0.0.0:3000->3000/tcp
extractpdftext-nginx              Up             0.0.0.0:80->80/tcp
```

---

## Step 5: Access Your Application

- **Frontend**: http://localhost:3000 (or http://localhost via Nginx)
- **Backend API**: http://localhost:8080 (or http://localhost/api via Nginx)
- **Health Check**: http://localhost:8080/actuator/health

---

## Step 6: Common Docker Compose Commands

### Stop all services:
```bash
docker-compose down
```

### Stop and remove volumes:
```bash
docker-compose down -v
```

### Restart services:
```bash
docker-compose restart
docker-compose restart backend
```

### View images:
```bash
docker-compose images
```

### Execute command in running container:
```bash
docker-compose exec backend bash
docker-compose exec frontend sh
```

### View container logs:
```bash
docker-compose logs backend --tail=100 --follow
```

---

## Step 7: Environment Variables

### Backend (docker-compose.yml):

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SERVER_PORT=8080
  - LOGGING_LEVEL_ROOT=INFO
```

### Frontend (docker-compose.yml):

```yaml
environment:
  - REACT_APP_API_URL=http://backend:8080
  - NODE_ENV=production
```

---

## Step 8: Volume Management

### Persistent storage:

```bash
# View all volumes
docker volume ls

# Inspect volume
docker volume inspect extractpdftext_pdf-storage

# Remove unused volumes
docker volume prune
```

---

## Advanced: Production Deployment

### Enable HTTPS with Nginx

1. Generate SSL certificates:
   ```bash
   openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365
   ```

2. Place in `ssl/` directory

3. Uncomment HTTPS section in `nginx.conf`

4. Rebuild and restart:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

---

## Troubleshooting

### Port already in use:

```bash
# Change ports in docker-compose.yml
# Or kill process using port
lsof -i :8080          # Check port usage
netstat -ano | findstr :8080  # Windows PowerShell
```

### Container won't start:

```bash
docker-compose logs backend
docker-compose logs frontend
```

### Network issues:

```bash
# Check network
docker network ls
docker network inspect extractpdftext_extractpdf-network
```

### Rebuild without cache:

```bash
docker-compose build --no-cache
docker-compose up -d
```

### Clear all Docker artifacts:

```bash
# WARNING: Removes all unused containers, images, networks
docker system prune -a

# Remove specific container
docker-compose down
docker system prune
```

---

## Performance Optimization

### Reduce image size:

1. Use Alpine Linux for base images (already done)
2. Multi-stage builds (already implemented)
3. Minimize layers in Dockerfile

### Docker resource limits:

Add to `docker-compose.yml`:

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

---

## CI/CD Integration

### Push images to Docker Hub:

```bash
# Login
docker login

# Tag images
docker tag extractpdftext-backend:latest yourusername/extractpdftext-backend:latest
docker tag extractpdftext-frontend:latest yourusername/extractpdftext-frontend:latest

# Push
docker push yourusername/extractpdftext-backend:latest
docker push yourusername/extractpdftext-frontend:latest
```

### Update docker-compose.yml to pull from registry:

```yaml
services:
  backend:
    image: yourusername/extractpdftext-backend:latest
  frontend:
    image: yourusername/extractpdftext-frontend:latest
```

---

## Complete Workflow

```bash
# 1. Navigate to project
cd C:\Users\bfrancis\projects\extractpdftext

# 2. Build images
docker-compose build

# 3. Start services
docker-compose up -d

# 4. Check status
docker-compose ps

# 5. View logs
docker-compose logs -f

# 6. Test application
# Visit http://localhost:3000

# 7. Stop services
docker-compose down
```

---

## Summary Checklist

- ✅ `Dockerfile` created for Spring Boot backend
- ✅ `frontend.Dockerfile` created for React frontend
- ✅ `docker-compose.yml` configured with all services
- ✅ `nginx.conf` set up as reverse proxy
- ✅ `.dockerignore` excludes unnecessary files
- ✅ Volumes configured for persistent storage
- ✅ Health checks implemented
- ✅ Environment variables configured
- ✅ Network bridge established between services

Your application is now fully dockerized and ready for deployment! 🚀

