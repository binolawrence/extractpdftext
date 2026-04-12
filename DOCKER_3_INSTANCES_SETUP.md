# Docker 3 Instances - Complete Setup Summary

## ✅ What Has Been Created

### Core Files

1. **Dockerfile** - Multi-stage build configuration
   - Builds Spring Boot application with Maven
   - Optimized Alpine Linux runtime
   - Health checks included
   - Environment variables configured

2. **docker-compose.yml** - Complete orchestration setup
   - 3 Spring Boot instances (app1, app2, app3)
   - Nginx load balancer (least-connection algorithm)
   - Shared volumes for PDFs and search index
   - Docker network for service discovery
   - Health checks for all services

3. **nginx.conf** - Load balancing configuration
   - Reverse proxy with intelligent routing
   - Failover handling
   - Buffering and timeout settings
   - Proxy headers for client info

### Helper Tools

4. **docker-helper.bat** - Windows batch script
   - Simple command interface
   - 15+ helper commands
   - Color-coded output
   - Integration testing

5. **docker-helper.ps1** - Windows PowerShell script
   - Modern PowerShell version
   - Better error handling
   - Rich formatting
   - Interactive commands

### Documentation

6. **DOCKER_3_INSTANCES_GUIDE.md** - Comprehensive guide
   - Architecture overview
   - Quick start instructions
   - Management commands
   - Troubleshooting guide
   - Production considerations

7. **DOCKER_3_INSTANCES_QUICK_REF.md** - Quick reference
   - Essential commands
   - Common scenarios with solutions
   - Performance tuning tips
   - Production checklist

8. **DOCKER_3_INSTANCES_OPTIONS.md** - Alternative approaches
   - 5 different deployment options
   - Pros/cons comparison
   - When to use each option
   - Migration path planning

---

## 🚀 Getting Started (Quick Start)

### Step 1: Start Services

**Option A - Using PowerShell (Recommended for Windows):**
```powershell
.\docker-helper.ps1 -Command start
```

**Option B - Using Batch:**
```cmd
docker-helper.bat start
```

**Option C - Direct Docker Compose:**
```bash
docker-compose up -d
```

### Step 2: Verify Everything Is Running

```powershell
.\docker-helper.ps1 -Command status
```

Expected output:
```
NAME                    STATUS              PORTS
extractpdf-nginx        Up (healthy)        0.0.0.0:80->80/tcp
extractpdf-app1         Up (healthy)        8080/tcp
extractpdf-app2         Up (healthy)        8080/tcp
extractpdf-app3         Up (healthy)        8080/tcp
```

### Step 3: Test Health

```powershell
.\docker-helper.ps1 -Command health
```

### Step 4: Access Application

Open browser: `http://localhost`

---

## 📊 Architecture

```
Internet Request (http://localhost)
         ↓
    ┌─────────────────┐
    │   Nginx:80      │
    │ Load Balancer   │
    └────────┬────────┘
             │
    ┌────────┼────────┐
    │        │        │
   ┌▼─┐   ┌─▼┐   ┌───▼┐
   │A1│   │A2│   │ A3 │
   │80│   │80│   │ 80 │
   └┬─┘   └─┬┘   └──┬─┘
    │      │       │
    └──────┼───────┘
           │
      Shared Storage
    ┌──────────────────┐
    │   PDFs Folder    │
    │  Lucene Index    │
    │  App Logs (sep)  │
    └──────────────────┘
```

---

## 📁 File Structure

```
extractpdftext/
├── Dockerfile                         # Docker image build config
├── docker-compose.yml                 # Orchestration (3 instances + nginx)
├── nginx.conf                         # Load balancer config
├── docker-helper.bat                  # Windows batch helper
├── docker-helper.ps1                  # Windows PowerShell helper
│
├── DOCKER_3_INSTANCES_GUIDE.md         # Comprehensive guide
├── DOCKER_3_INSTANCES_QUICK_REF.md     # Quick reference
├── DOCKER_3_INSTANCES_OPTIONS.md       # Alternative approaches
├── DOCKER_3_INSTANCES_SETUP.md         # This file
│
├── pom.xml                            # Maven build config
├── src/                               # Source code
└── target/                            # Built artifacts

Runtime Directories (created on first run):
├── pdfdocs/                           # PDF documents (shared)
├── lucene-index/                      # Search index (shared)
└── logs/
    ├── app1/                          # Instance 1 logs
    ├── app2/                          # Instance 2 logs
    └── app3/                          # Instance 3 logs
```

---

## 🎮 Common Commands

### Start/Stop

```powershell
# Start all services
.\docker-helper.ps1 -Command start

# Check status
.\docker-helper.ps1 -Command status

# Stop all services
.\docker-helper.ps1 -Command stop
```

### Monitoring

```powershell
# View all logs (live)
.\docker-helper.ps1 -Command logs

# View specific instance
.\docker-helper.ps1 -Command logs-app1

# Monitor CPU/Memory
.\docker-helper.ps1 -Command stats

# Health check
.\docker-helper.ps1 -Command health
```

### Maintenance

```powershell
# Restart all
.\docker-helper.ps1 -Command restart

# Rebuild and restart
.\docker-helper.ps1 -Command build

# Open shell in container
.\docker-helper.ps1 -Command shell
.\docker-helper.ps1 -Command shell -Container app2

# Run tests
.\docker-helper.ps1 -Command test

# Full cleanup (removes everything)
.\docker-helper.ps1 -Command clean
```

---

## 🔧 Configuration

### Change Port Number

Edit `docker-compose.yml`:

```yaml
nginx:
  ports:
    - "8080:80"  # Changed from 80:80
```

Then restart:
```bash
docker-compose restart
```

Access: `http://localhost:8080`

### Add 4th Instance

1. Add service in `docker-compose.yml`:

```yaml
  app4:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: extractpdf-app4
    environment:
      - SPRING_APPLICATION_NAME=extractpdftext-app4
      - SERVER_PORT=8080
      - INSTANCE_NAME=app4
    volumes:
      - ./pdfdocs:/app/pdfdocs:rw
      - ./lucene-index:/app/lucene-index:rw
      - ./logs/app4:/app/logs:rw
    networks:
      - extractpdf-network
    expose:
      - "8080"
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

2. Add to `nginx.conf` upstream:

```nginx
upstream extractpdf_backend {
    least_conn;
    server app1:8080 max_fails=2 fail_timeout=30s;
    server app2:8080 max_fails=2 fail_timeout=30s;
    server app3:8080 max_fails=2 fail_timeout=30s;
    server app4:8080 max_fails=2 fail_timeout=30s;  # Add this line
}
```

3. Restart:

```bash
docker-compose up -d
```

### Change PDF Directory

Edit `docker-compose.yml` for each app:

```yaml
  app1:
    volumes:
      - /your/pdf/path:/app/pdfdocs:rw  # Changed
      - ./lucene-index:/app/lucene-index:rw
```

### Set Memory Limits

Add to each service:

```yaml
  app1:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

---

## 🐛 Troubleshooting

### Services Not Starting

```bash
# Check Docker daemon
docker ps

# Check logs
docker-compose logs

# Rebuild
docker-compose down
docker-compose up -d --build
```

### High CPU Usage

```bash
# Monitor usage
docker stats

# Limit CPU in docker-compose.yml
# See "Change PDF Directory" section above
```

### Port Already in Use

```bash
# Find what's using port 80
netstat -ano | findstr ":80"

# Change port in docker-compose.yml
# Or kill the process using that port
taskkill /PID <PID> /F
```

### One Instance Failing

```bash
# Check its logs
docker-compose logs app1

# Restart it
docker-compose restart app1

# Full rebuild
docker-compose up -d --force-recreate app1
```

### Shared Index Lock Error

```bash
# Delete lock file
docker-compose down
rm -rf lucene-index/*
docker-compose up -d
```

---

## 📈 Performance Tips

### 1. Resource Allocation

```yaml
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 2G
```

### 2. JVM Settings

Edit `Dockerfile`:

```dockerfile
ENV JAVA_OPTS="-Xmx1g -Xms512m"
```

### 3. Nginx Buffer Sizes

Edit `nginx.conf`:

```nginx
proxy_buffer_size 256k;
proxy_buffers 4 512k;
proxy_busy_buffers_size 512k;
```

### 4. Connection Timeouts

```nginx
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
```

---

## 📚 Documentation Index

- **DOCKER_3_INSTANCES_GUIDE.md** - Full reference guide
- **DOCKER_3_INSTANCES_QUICK_REF.md** - Common commands & scenarios
- **DOCKER_3_INSTANCES_OPTIONS.md** - Alternative deployment approaches

---

## ✨ Key Features

✅ **3 Independent Instances** - Parallel processing capability
✅ **Load Balancing** - Intelligent traffic distribution
✅ **High Availability** - Automatic failover
✅ **Shared Resources** - Consistent PDF access
✅ **Health Checks** - Automatic monitoring
✅ **Easy Scaling** - Add/remove instances easily
✅ **Log Isolation** - Separate logs per instance
✅ **Docker Native** - Uses standard Docker/Compose
✅ **Windows Friendly** - Helper scripts included
✅ **Production Ready** - Proper configuration

---

## 🎯 Next Steps

1. **Run**: `.\docker-helper.ps1 -Command start`
2. **Test**: `.\docker-helper.ps1 -Command test`
3. **Monitor**: `.\docker-helper.ps1 -Command stats`
4. **Access**: `http://localhost`
5. **Customize**: Edit `docker-compose.yml` as needed
6. **Deploy**: Use same files for cloud deployment

---

## 📞 Support

### Quick Help

```powershell
.\docker-helper.ps1 -Command help
```

### Check Everything

```powershell
.\docker-helper.ps1 -Command test
```

### View Logs

```powershell
.\docker-helper.ps1 -Command logs
```

---

## 🔐 Security Considerations

### For Production:

1. **Add SSL/TLS** to nginx
2. **Use secrets** for sensitive data
3. **Restrict network** access
4. **Enable authentication** on endpoints
5. **Regular updates** to base images
6. **Scan images** for vulnerabilities
7. **Use non-root** container user
8. **Limit resources** per container

See DOCKER_3_INSTANCES_GUIDE.md for detailed production setup.

---

## 📊 Scalability Path

```
Current: 3 instances (Compose)
    ↓ (if needed)
Future: Dynamic scaling (Docker Swarm)
    ↓ (if needed)
Production: Kubernetes cluster
    ↓ (if needed)
Cloud: AWS ECS / Google Cloud Run
```

---

## ✅ Verification Checklist

- [ ] Docker is installed (`docker --version`)
- [ ] Docker Compose is installed (`docker-compose --version`)
- [ ] All files created successfully
- [ ] Services start without errors (`docker-compose up -d`)
- [ ] All 3 instances are healthy (`docker-compose ps`)
- [ ] Load balancer responds (`curl http://localhost`)
- [ ] Logs are being written (`docker-compose logs`)
- [ ] Helper scripts work (`.\docker-helper.ps1 -Command help`)

---

## 🎉 You're All Set!

Everything is ready. Start with:

```powershell
.\docker-helper.ps1 -Command start
```

Then access: `http://localhost`

Enjoy your 3-instance Docker setup!

