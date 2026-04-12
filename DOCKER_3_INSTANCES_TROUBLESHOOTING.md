# Docker 3 Instances - Troubleshooting Guide

## Quick Diagnosis Flow

```
Problem Detected
        │
        ▼
   Run docker-compose ps
        │
    ┌───┴────┐
    │        │
    ▼        ▼
Not    Some Missing?
Running  (go to step 1)
(go to
 step 1)
```

---

## Step-by-Step Troubleshooting

### 1. Services Not Starting

**Symptoms:**
- `docker-compose ps` shows nothing
- Error when running `docker-compose up`

**Diagnosis:**
```powershell
# Check Docker is running
docker ps

# Check Docker daemon
docker info

# Check docker-compose syntax
docker-compose config
```

**Solutions:**

A. Docker not installed
```bash
# Install Docker Desktop for Windows
# Visit: https://www.docker.com/products/docker-desktop
```

B. Docker daemon not running
```bash
# Start Docker Desktop
# Or in PowerShell as Admin:
Start-Service Docker
```

C. Invalid docker-compose.yml
```bash
# Validate syntax
docker-compose config

# Check for common errors:
# - Missing colons after keys
# - Invalid indentation (use spaces, not tabs)
# - Undefined services
```

D. Missing Dockerfile
```bash
# Check if Dockerfile exists
ls Dockerfile

# Rebuild
docker-compose build
```

---

### 2. Services Start But Crash Immediately

**Symptoms:**
- `docker-compose ps` shows "Exit 1"
- Status changes from "Starting" to "Exited"

**Diagnosis:**
```powershell
# Check logs
docker-compose logs app1

# Look for:
# - "Application failed to start"
# - "Port already in use"
# - "Out of memory"
# - "Class not found"
```

**Solutions:**

A. Port in use
```powershell
# Find what's using port 80
netstat -ano | findstr ":80"

# Kill the process (if safe)
taskkill /PID <PID> /F

# Or change port in docker-compose.yml
# nginx:
#   ports:
#     - "8080:80"  (changed from 80:80)
```

B. Out of memory
```bash
# Check available memory
Get-ComputerInfo | Select-Object CsPhyicallyInstalledSystemMemory

# Reduce JVM heap in Dockerfile:
# ENV JAVA_OPTS="-Xmx512m -Xms256m"
```

C. Build failed
```bash
# Rebuild with output
docker-compose build --no-cache

# Check for Maven errors
# Check for missing dependencies
```

D. Application configuration error
```bash
# Check logs for configuration
docker-compose logs | grep -i "configuration\|error\|failed"

# Verify application.properties exists
# Check environment variables
```

---

### 3. Services Running But Not Responding

**Symptoms:**
- `docker-compose ps` shows "Up"
- But `http://localhost` times out

**Diagnosis:**
```powershell
# Check if Nginx is responding
curl -v http://localhost

# Check if backend apps are responding
docker-compose exec nginx curl http://app1:8080

# Check Nginx logs
docker-compose logs nginx
```

**Solutions:**

A. Nginx not responding
```bash
# Check Nginx status
docker-compose ps nginx

# Restart Nginx
docker-compose restart nginx

# Check configuration
docker-compose exec nginx nginx -t
```

B. Backend apps not responding
```bash
# Check app health
docker-compose logs app1 | tail -20

# Restart apps
docker-compose restart app1 app2 app3

# Full rebuild
docker-compose up -d --force-recreate
```

C. Network issue
```bash
# Test network connectivity
docker-compose exec nginx ping app1

# Check Docker network
docker network ls
docker network inspect extractpdftext_extractpdf-network
```

D. Firewall blocking
```bash
# On Windows, allow Docker through firewall
# Check Windows Defender Firewall
# Or:
netsh advfirewall firewall add rule name="Allow localhost" `
    dir=in action=allow protocol=tcp localport=80
```

---

### 4. High CPU Usage

**Symptoms:**
- `docker stats` shows > 50% CPU
- System running slow
- One or more containers using high CPU

**Diagnosis:**
```powershell
# Monitor in real-time
docker stats

# Check which container is using CPU
docker stats --no-stream | Sort-Object -Property CPU -Descending

# Check app logs for errors
docker-compose logs app1
```

**Solutions:**

A. JVM not tuned
```dockerfile
# Edit Dockerfile - limit heap
ENV JAVA_OPTS="-Xmx1g -Xms512m"
```

B. Infinite loops in application
```bash
# Check logs for errors
docker-compose logs app1 | tail -50

# Look for:
# - Continuous error retries
# - Memory leaks (continuous GC)
# - Infinite processing loops
```

C. Too many instances for system
```bash
# Check system resources
Get-Process | Select-Object -Property Name, CPU, Memory | 
  Sort-Object -Property Memory -Descending | Select-Object -First 10

# Scale down instances if needed
# Or upgrade system RAM
```

D. Nginx misconfiguration
```bash
# Check proxy settings
docker-compose exec nginx cat /etc/nginx/nginx.conf

# Look for high buffer sizes or timeouts
```

---

### 5. High Memory Usage

**Symptoms:**
- `docker stats` shows > 1GB memory
- System out of memory errors
- Containers getting killed

**Diagnosis:**
```powershell
# Check memory usage
docker stats --no-stream

# Check total system memory
Get-ComputerInfo | Select-Object -Property CsTotalPhysicalMemory

# Check for memory leaks
docker-compose logs | grep -i "memory\|heap\|gc"
```

**Solutions:**

A. JVM heap too large
```dockerfile
# Edit Dockerfile
ENV JAVA_OPTS="-Xmx512m -Xms256m"  # Reduce from 1g

# Rebuild
docker-compose up -d --build
```

B. Docker memory limit
```yaml
# Edit docker-compose.yml
deploy:
  resources:
    limits:
      memory: 512M  # Limit per container
```

C. Too many instances
```bash
# Remove unnecessary containers
docker-compose down

# Reduce number of instances in docker-compose.yml
```

D. Memory leak in application
```bash
# Check logs for errors
docker-compose logs | grep -i "memory\|heap\|leak"

# Monitor over time
docker stats app1

# If memory keeps growing, restart container
docker-compose restart app1
```

---

### 6. One Instance Failing

**Symptoms:**
- One container shows "Exited" or "Unhealthy"
- Other instances working fine
- Nginx routing around it

**Diagnosis:**
```powershell
# Check specific container
docker-compose logs app1

# Check container status
docker-compose ps | grep app1

# Check resource limits
docker inspect extractpdf-app1 | grep -A 10 "Memory"
```

**Solutions:**

A. Immediate recovery
```bash
# Restart the container
docker-compose restart app1

# Or recreate it
docker-compose up -d --force-recreate app1
```

B. Check logs for errors
```bash
# View detailed logs
docker-compose logs app1 --tail=100

# Look for:
# - java.lang.OutOfMemoryError
# - NullPointerException
# - Connection refused
```

C. Resource exhaustion
```bash
# Check if out of disk space
docker exec extractpdf-app1 df -h

# Check if out of memory
docker stats extractpdf-app1
```

D. Persistent failures
```bash
# Full rebuild of that instance
docker-compose down app1
docker-compose up -d app1

# Or rebuild from scratch
docker-compose up -d --build app1
```

---

### 7. Shared Volume Issues

**Symptoms:**
- PDFs not accessible
- Search index corrupted
- Index lock errors

**Diagnosis:**
```bash
# Check volume mounts
docker inspect extractpdf-app1 | grep -A 5 "Mounts"

# Check volume contents
docker-compose exec app1 ls -la /app/pdfdocs

# Check permissions
docker-compose exec app1 ls -la /app/lucene-index
```

**Solutions:**

A. Index lock
```bash
# Stop services
docker-compose down

# Remove lock
rm -rf lucene-index/write.lock

# Restart
docker-compose up -d
```

B. Stale index
```bash
# Backup and clear index
cp -r lucene-index lucene-index.backup
rm -rf lucene-index/*

# Restart services
docker-compose up -d

# Apps will rebuild index
```

C. PDFs not found
```bash
# Check if pdfdocs folder exists
ls pdfdocs/

# Check permissions
chmod 755 pdfdocs/

# Verify volume mount in docker-compose.yml
# Should be: ./pdfdocs:/app/pdfdocs:rw
```

D. Permission denied
```bash
# Check file permissions
ls -la pdfdocs/

# Make readable by Docker
chmod -R 755 pdfdocs/
chmod -R 755 lucene-index/
```

---

### 8. Network Connectivity Issues

**Symptoms:**
- Instances can't reach each other
- Nginx can't reach backend
- External services unreachable

**Diagnosis:**
```bash
# Test from Nginx to App
docker-compose exec nginx curl http://app1:8080

# Test DNS
docker-compose exec nginx nslookup app1

# Check network
docker network inspect extractpdftext_extractpdf-network

# Check routing
docker-compose exec app1 route -n
```

**Solutions:**

A. Service discovery issue
```bash
# Ensure service names in docker-compose.yml match
# Are they: app1, app2, app3 (not app_1, app_2, etc)?

# Restart with fresh network
docker-compose down
docker-compose up -d
```

B. Network misconfigured
```bash
# Check docker-compose.yml networks section
# Should have:
# networks:
#   - extractpdf-network

# All services should use same network
```

C. DNS not working
```bash
# Restart Docker daemon
# Windows: Restart Docker Desktop
# Or in PowerShell as Admin:
Restart-Service Docker
```

---

### 9. Build Failures

**Symptoms:**
- `docker-compose build` fails
- Maven compilation errors
- Dependency download failures

**Diagnosis:**
```bash
# Run with output
docker-compose build --no-cache

# Check Maven cache
docker-compose exec app1 ls -la /root/.m2/repository

# Check internet connectivity
docker-compose exec app1 ping google.com
```

**Solutions:**

A. Maven dependency issue
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
docker-compose build --no-cache
```

B. Internet timeout
```bash
# Increase timeout in pom.xml
# Or use Maven mirror
# Or build from local cache
docker-compose build --build-arg MAVEN_OPTS="-DconnectionTimeout=60000"
```

C. Java compilation error
```bash
# Check Java version
docker-compose exec app1 java -version

# Should be Java 17 (or version in Dockerfile)

# Check source code for syntax errors
docker-compose build | grep -i "error:"
```

---

## Common Error Messages & Fixes

### "Port 80 is already allocated"

```powershell
# Find what's using it
netstat -ano | findstr ":80"

# Kill it (if safe)
taskkill /PID <PID> /F

# Or change port in docker-compose.yml:
# ports:
#   - "8080:80"
```

### "Cannot connect to Docker daemon"

```powershell
# Restart Docker
Restart-Service Docker

# Or start Docker Desktop
# Then try again
```

### "Out of memory: Unable to allocate 512MB"

```bash
# Reduce JVM heap
# Edit Dockerfile ENV JAVA_OPTS

# Reduce number of instances
# Remove from docker-compose.yml
```

### "Connection refused"

```bash
# Services still starting
# Wait 30 seconds and retry

# Or check logs
docker-compose logs
```

### "Address already in use"

```bash
# Kill the process
lsof -ti:8080 | xargs kill -9

# Or change port
```

---

## Preventive Maintenance

### Regular Health Checks

```powershell
# Daily
.\docker-helper.ps1 -Command health

# Weekly
.\docker-helper.ps1 -Command test

# Monthly
.\docker-helper.ps1 -Command stats
```

### Log Rotation

```bash
# Keep logs manageable
docker-compose logs --tail=100 > logs.txt

# Regularly backup
cp -r logs logs.backup.$(date +%Y%m%d)
```

### Cleanup

```powershell
# Monthly cleanup
docker system prune -a

# Specific cleanup
docker image prune
docker volume prune
docker network prune
```

---

## Getting Help

### Where to Look

1. **Application Logs**
   ```bash
   docker-compose logs app1
   ```

2. **Docker Events**
   ```bash
   docker events --filter type=container
   ```

3. **Resource Usage**
   ```bash
   docker stats
   ```

4. **Network Inspection**
   ```bash
   docker network inspect extractpdftext_extractpdf-network
   ```

### Debug Mode

Add to docker-compose.yml for more logging:

```yaml
app1:
  environment:
    - JAVA_OPTS=-Xmx1g -Xms512m -XX:+PrintGCDetails
    - SPRING_JPA_SHOW_SQL=true
    - LOGGING_LEVEL_ROOT=DEBUG
```

---

## Quick Reference

| Problem | Command |
|---------|---------|
| Services won't start | `docker-compose logs` |
| High CPU | `docker stats` |
| High Memory | `docker inspect <container>` |
| Network issue | `docker network inspect` |
| Rebuild needed | `docker-compose up -d --build` |
| Full cleanup | `docker-compose down -v` |
| Check health | `curl http://localhost/health` |
| View logs | `docker-compose logs -f` |

---

## Still Having Issues?

1. Check all documentation in workspace
2. Run `.\docker-helper.ps1 -Command test`
3. Check logs: `docker-compose logs`
4. Monitor resources: `docker stats`
5. Rebuild: `docker-compose up -d --build`
6. Last resort: `docker-compose down -v` then start fresh

Good luck!

