# Docker 3 Instances - Quick Reference

## Start Everything (5 seconds)

```bash
docker-compose up -d
```

## Check Status

```bash
docker-compose ps
```

Expected output:
```
NAME                    STATUS              PORTS
extractpdf-nginx        Up (healthy)        0.0.0.0:80->80/tcp
extractpdf-app1         Up (healthy)        8080/tcp
extractpdf-app2         Up (healthy)        8080/tcp
extractpdf-app3         Up (healthy)        8080/tcp
```

## Access Application

```
http://localhost
```

## View All Logs

```bash
docker-compose logs -f
```

## View Specific Instance

```bash
# App1 logs
docker-compose logs -f app1

# Nginx logs
docker-compose logs -f nginx
```

## Stop Everything

```bash
docker-compose down
```

## Restart Specific Instance

```bash
docker-compose restart app1
```

## Rebuild and Start

```bash
docker-compose up -d --build
```

## Monitor CPU/Memory

```bash
docker stats
```

## Check Instance Health

```bash
curl http://localhost/health
```

## SSH into Container

```bash
docker-compose exec app1 /bin/sh
```

## View Container Details

```bash
docker-compose exec app1 env
```

## Remove Everything

```bash
docker-compose down -v  # Also removes volumes
```

## Rebuild Specific Service

```bash
docker-compose up -d --build app1
```

---

## Architecture Summary

**Nginx (Load Balancer)**
- Listens on port 80
- Routes to 3 instances using least-connections algorithm
- Health checks every 30 seconds

**App Instance 1, 2, 3**
- Each runs Spring Boot on internal port 8080
- Share PDF documents folder
- Share Lucene search index
- Have separate log directories

**Shared Storage**
- `./pdfdocs/` - PDF documents
- `./lucene-index/` - Search index
- `./logs/` - App logs per instance

---

## Common Scenarios

### "I want to add a 4th instance"

Edit `docker-compose.yml`, add:

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

Also update `nginx.conf` upstream:

```nginx
upstream extractpdf_backend {
    least_conn;
    server app1:8080 max_fails=2 fail_timeout=30s;
    server app2:8080 max_fails=2 fail_timeout=30s;
    server app3:8080 max_fails=2 fail_timeout=30s;
    server app4:8080 max_fails=2 fail_timeout=30s;  # Add this
}
```

Then restart:

```bash
docker-compose up -d
```

### "One instance is using all the CPU"

Limit CPU per instance in `docker-compose.yml`:

```yaml
app1:
  deploy:
    resources:
      limits:
        cpus: '1.0'
        memory: 1G
```

### "I want to run on a different port"

Edit `nginx` service in `docker-compose.yml`:

```yaml
nginx:
  ports:
    - "8000:80"  # Changed from 80:80
```

Then access: `http://localhost:8000`

### "I need to update application code"

Option 1 - Full rebuild:
```bash
docker-compose up -d --build
```

Option 2 - Rebuild specific service:
```bash
docker-compose up -d --build app1
```

### "I want to use a different PDF directory"

Edit each app in `docker-compose.yml`:

```yaml
app1:
  volumes:
    - /path/to/your/pdfs:/app/pdfdocs:rw  # Changed from ./pdfdocs
```

### "How do I check if load balancing is working?"

Check Nginx logs:
```bash
docker-compose logs nginx | grep "upstream:"
```

Or monitor requests:
```bash
docker-compose exec nginx tail -f /var/log/nginx/access.log
```

---

## Failure Scenarios

### Container Crashed
Nginx automatically routes to healthy instances. Check logs:
```bash
docker-compose logs app1
docker-compose restart app1
```

### Shared Index Lock
If Lucene index is locked, delete and rebuild:
```bash
rm -rf lucene-index/*
docker-compose restart
```

### Out of Disk Space
Check usage:
```bash
df -h
docker system df
```

Clean up:
```bash
docker system prune -a
```

### High Memory
Monitor and set limits:
```bash
docker stats
# Then add limits to docker-compose.yml
```

---

## Performance Tuning

### JVM Memory per Instance

Edit `Dockerfile`:

```dockerfile
ENV JAVA_OPTS="-Xmx1g -Xms512m"
```

Then in `docker-compose.yml`:

```yaml
app1:
  environment:
    - JAVA_OPTS=-Xmx2g -Xms1g
```

### Nginx Buffer Size

Edit `nginx.conf`:

```nginx
proxy_buffer_size 256k;
proxy_buffers 4 512k;
```

### Connection Pooling

In your Spring Boot app, ensure connection pooling:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## Production Checklist

- [ ] Test all 3 instances are running
- [ ] Verify load balancing with repeated requests
- [ ] Check shared volume consistency
- [ ] Set up log rotation
- [ ] Enable SSL/TLS in Nginx
- [ ] Set resource limits
- [ ] Add monitoring (docker stats, Prometheus)
- [ ] Document custom configurations
- [ ] Test failover scenarios
- [ ] Backup shared volumes regularly

