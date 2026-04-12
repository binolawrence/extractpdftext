# Docker Multi-Instance Setup Guide (3 Instances)

## Overview

This Docker configuration runs **3 instances** of your Spring Boot PDF extraction application with automatic load balancing via Nginx.

### Architecture

```
┌─────────────────────────────────────────────┐
│          User Requests (Port 80)             │
└────────────────┬────────────────────────────┘
                 │
         ┌───────▼────────┐
         │  Nginx (80)    │
         │ Load Balancer  │
         └───────┬────────┘
                 │
      ┌──────────┼──────────┐
      │          │          │
   ┌──▼──┐   ┌──▼──┐   ┌──▼──┐
   │App1 │   │App2 │   │App3 │
   │8080 │   │8080 │   │8080 │
   └─────┘   └─────┘   └─────┘
      │          │          │
      └──────────┼──────────┘
                 │
         (Shared PDF/Index)
```

## Files Created

1. **Dockerfile** - Multi-stage build for optimized image size
2. **docker-compose.yml** - Orchestrates 3 app instances + Nginx
3. **nginx.conf** - Load balancer configuration with least-conn strategy

## Features

✅ **3 Independent Instances** - Each runs on port 8080 internally
✅ **Load Balancing** - Nginx distributes traffic using least-connections algorithm
✅ **Health Checks** - Automatic detection of unhealthy instances
✅ **Shared Volumes** - All instances share PDF documents and Lucene index
✅ **Isolated Logs** - Each instance has separate log directory
✅ **Auto-Restart** - Containers restart on failure
✅ **Service Discovery** - Docker networking handles service discovery
✅ **Environment Variables** - Configurable per instance

## Quick Start

### Prerequisites

- Docker Engine (version 20.10+)
- Docker Compose (version 2.0+)
- Git bash or similar terminal (for Unix commands)

### 1. Build and Start All Services

```bash
# Build the Docker image and start 3 instances with load balancer
docker-compose up -d

# View logs from all services
docker-compose logs -f

# View logs from specific service
docker-compose logs -f app1
docker-compose logs -f nginx
```

### 2. Access the Application

```bash
# Access through load balancer
http://localhost

# Check health of all instances
curl http://localhost/health

# Direct instance access (for testing)
curl http://localhost:8081 (app1)
curl http://localhost:8082 (app2)
curl http://localhost:8083 (app3)
```

## Management Commands

### View Running Containers

```bash
docker-compose ps
```

### Scale Instances

To run **5 instances** instead of 3:

```bash
docker-compose up -d --scale app=5
```

But you'll need to modify docker-compose.yml first to use dynamic service names.

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app1
docker-compose logs -f nginx

# Last 100 lines
docker-compose logs --tail=100 app1
```

### Stop All Services

```bash
docker-compose down
```

### Remove Containers and Volumes

```bash
# Keep images, remove containers
docker-compose down

# Also remove volumes (data loss!)
docker-compose down -v

# Remove images too
docker-compose down --rmi all
```

### Restart Services

```bash
# Restart everything
docker-compose restart

# Restart specific service
docker-compose restart app1

# Recreate (stop, remove, recreate, start)
docker-compose up -d --force-recreate
```

## Load Balancing Strategy

**Least Connections (least_conn)** - Nginx sends requests to the backend with the fewest active connections.

This is ideal for:
- Long-running PDF processing requests
- Varying request durations
- Balanced workload distribution

## Shared Resources

### PDFs Directory
- **Host**: `./pdfdocs/`
- **Container**: `/app/pdfdocs/`
- All 3 instances read/write from the same directory

### Lucene Index
- **Host**: `./lucene-index/`
- **Container**: `/app/lucene-index/`
- Shared search index across instances

### Logs
- **App1**: `./logs/app1/`
- **App2**: `./logs/app2/`
- **App3**: `./logs/app3/`

## Environment Variables

Each instance can be customized:

```yaml
environment:
  - SPRING_APPLICATION_NAME=extractpdftext-app1
  - SERVER_PORT=8080
  - SPRING_PROFILES_ACTIVE=prod
  - PDF_DOCS_DIR=/app/pdfdocs
  - LUCENE_INDEX_DIR=/app/lucene-index
  - INSTANCE_NAME=app1
```

### Custom Configuration

Edit `docker-compose.yml` to modify:

```yaml
app1:
  environment:
    - JVM_OPTS=-Xmx1g -Xms512m  # JVM memory settings
    - CUSTOM_PROPERTY=value
```

## Health Checks

Each container has automated health checks:

```bash
# Check container health
docker-compose ps

# Manual health check
curl http://localhost/health

# View detailed health status
docker inspect extractpdf-app1 | grep -A 20 '"Health"'
```

## Performance Considerations

### Network
- Nginx + 3 instances = low latency internal communication
- Keep containers on the same Docker network

### Storage
- Shared volumes may have slight I/O contention
- Consider using volume drivers for better performance (e.g., NFS)

### Memory
- Each Java instance: ~512MB JVM minimum
- Total: ~2GB+ recommended
- Monitor with: `docker stats`

### CPU
- 3 instances can utilize multi-core processors
- Monitor CPU usage: `docker stats`

## Troubleshooting

### One Instance Failing

```bash
# Check logs
docker-compose logs app1

# Restart failing instance
docker-compose restart app1

# Recreate and start
docker-compose up -d --force-recreate app1
```

### All Instances Down

```bash
# Check if containers are running
docker-compose ps

# View all logs
docker-compose logs

# Restart all
docker-compose restart

# Full rebuild
docker-compose down
docker-compose up -d --build
```

### Nginx Not Routing

```bash
# Check nginx logs
docker-compose logs nginx

# Verify upstream connectivity
docker-compose exec nginx ping app1
```

### High CPU/Memory Usage

```bash
# Monitor resource usage
docker stats

# Limit resources in docker-compose.yml:
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

## Production Deployment

### Considerations

1. **Persistent Volumes** - Use named volumes instead of bind mounts
2. **Network** - Expose only Nginx, keep apps internal
3. **Security** - Add SSL/TLS to Nginx
4. **Logging** - Use log drivers (ELK, Splunk, etc.)
5. **Monitoring** - Add Prometheus + Grafana
6. **Scaling** - Use Docker Swarm or Kubernetes

### SSL/TLS Setup

Edit `nginx.conf` to add:

```nginx
listen 443 ssl http2;
ssl_certificate /etc/nginx/certs/cert.pem;
ssl_certificate_key /etc/nginx/certs/key.pem;
```

Then mount certificates:

```yaml
nginx:
  volumes:
    - ./certs:/etc/nginx/certs:ro
```

## Alternative: Docker Swarm Scaling

For dynamic scaling, use docker-compose templates:

```bash
# Scale to 5 instances
docker-compose up -d --scale app=5
```

## Comparison: Deployment Options

| Option | Pros | Cons |
|--------|------|------|
| **Docker Compose (Current)** | Simple, Local development | Not for production at scale |
| **Docker Swarm** | Multi-node clustering | Limited compared to Kubernetes |
| **Kubernetes** | Enterprise-grade, Auto-scaling | Complex setup, Learning curve |
| **Cloud Platforms** | Managed, Auto-scaling, CDN | Vendor lock-in, Cost |

## Next Steps

1. Update paths in your application configuration
2. Add SSL/TLS certificates for production
3. Set up centralized logging
4. Add database persistence if needed
5. Implement auto-scaling policies
6. Add monitoring and alerting

## Support

For issues, check:
1. Docker logs: `docker-compose logs`
2. Container status: `docker-compose ps`
3. Network connectivity: `docker network inspect extractpdftext_extractpdf-network`
4. Resource usage: `docker stats`

