# Docker 3 Instances - Alternative Options

This document describes different approaches to run 3 instances, each with different benefits.

## Option 1: Docker Compose with Load Balancer (RECOMMENDED)

**What we created above.**

### Pros
✅ Simple setup with single `docker-compose up -d`
✅ Nginx handles intelligent load balancing
✅ Easy to add/remove instances
✅ Perfect for local development and small deployments
✅ Built-in health checks
✅ Shared volumes for PDFs and index

### Cons
❌ Not scalable beyond manual configuration
❌ Requires pre-defining number of instances
❌ Single machine deployment only

### When to Use
- Local development
- Small production (< 10 servers)
- Known fixed workload (3 instances)
- Simple shared storage

### Command
```bash
docker-compose up -d
```

---

## Option 2: Docker Compose with Dynamic Scaling

**For flexible instance count without editing files.**

### Setup

Create a template docker-compose.yml:

```yaml
version: '3.8'

services:
  app:
    build: .
    environment:
      - SPRING_APPLICATION_NAME=extractpdftext
      - SERVER_PORT=8080
    volumes:
      - ./pdfdocs:/app/pdfdocs
      - ./lucene-index:/app/lucene-index
    networks:
      - extractpdf-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - app
    networks:
      - extractpdf-network

networks:
  extractpdf-network:
    driver: bridge
```

### Scale to N Instances

```bash
# Scale to 3 instances
docker-compose up -d --scale app=3

# Scale to 5 instances (adds 2 more)
docker-compose up -d --scale app=5

# Scale down to 2
docker-compose up -d --scale app=2
```

### Pros
✅ Dynamic scaling without editing files
✅ Quickly change instance count
✅ Less repetitive configuration
✅ Good for testing different loads

### Cons
❌ Nginx config needs dynamic upstream generation
❌ Slightly more complex setup
❌ Need additional tools for production

### When to Use
- Load testing
- Development with varying workloads
- Quick prototyping
- Learning/experimentation

---

## Option 3: Docker Swarm (Multi-Node Clustering)

**For true distributed deployment.**

### Setup

```bash
# Initialize Swarm
docker swarm init

# Create overlay network
docker network create -d overlay extractpdf-network

# Deploy service with 3 replicas
docker service create \
  --name extractpdf \
  --mode replicated \
  --replicas 3 \
  --network extractpdf-network \
  --publish 80:80 \
  extractpdf:latest
```

### Scaling

```bash
# Scale to 5 instances
docker service scale extractpdf=5

# Scale down to 2
docker service scale extractpdf=2

# Check status
docker service ps extractpdf
```

### Pros
✅ True multi-node clustering
✅ Automatic load balancing (Ingress)
✅ Rolling updates
✅ Service discovery built-in
✅ Swarm-native health management
✅ Scale with single command

### Cons
❌ More complex setup
❌ Requires cluster management
❌ Overkill for single machine
❌ Different from Compose workflow
❌ Steeper learning curve

### When to Use
- Multi-server deployment
- Need true distributed system
- Automatic failover required
- Enterprise environments

### Commands

```bash
# Deploy stack
docker stack deploy -c docker-compose-swarm.yml extractpdf

# View services
docker service ls

# View replicas
docker service ps extractpdf

# Scale
docker service scale extractpdf_app=5

# Update image
docker service update --image extractpdf:v2 extractpdf_app

# View logs
docker service logs extractpdf_app
```

---

## Option 4: Kubernetes (Enterprise-Grade)

**For large-scale production deployments.**

### Setup

Create `k8s-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: extractpdf
spec:
  replicas: 3
  selector:
    matchLabels:
      app: extractpdf
  template:
    metadata:
      labels:
        app: extractpdf
    spec:
      containers:
      - name: extractpdf
        image: extractpdf:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        volumeMounts:
        - name: pdfdocs
          mountPath: /app/pdfdocs
        - name: lucene-index
          mountPath: /app/lucene-index
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 40
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
      volumes:
      - name: pdfdocs
        persistentVolumeClaim:
          claimName: pdfdocs-pvc
      - name: lucene-index
        persistentVolumeClaim:
          claimName: lucene-index-pvc

---
apiVersion: v1
kind: Service
metadata:
  name: extractpdf-service
spec:
  type: LoadBalancer
  selector:
    app: extractpdf
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

### Deploy

```bash
# Apply deployment
kubectl apply -f k8s-deployment.yaml

# Check status
kubectl get deployments
kubectl get pods
kubectl get svc

# Scale to 5 replicas
kubectl scale deployment extractpdf --replicas=5

# View logs
kubectl logs -l app=extractpdf

# Port forward for local testing
kubectl port-forward svc/extractpdf-service 80:80
```

### Pros
✅ Industry standard for large deployments
✅ Advanced orchestration features
✅ Auto-scaling policies
✅ Rolling updates, rollbacks
✅ Secrets management
✅ Persistent volume management
✅ Multi-cloud support
✅ Monitoring and logging integration

### Cons
❌ Complex setup and learning curve
❌ Requires Kubernetes cluster
❌ Infrastructure overhead
❌ Overkill for small deployments
❌ More operational overhead

### When to Use
- Large enterprise deployments
- Multi-region deployments
- 100+ servers
- Complex orchestration needs
- Cloud providers (AWS EKS, GCP GKE, Azure AKS)

---

## Option 5: Container Orchestration Platforms (Managed)

**For cloud-based deployments without managing infrastructure.**

### AWS ECS Example

```bash
# Create task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Create service with 3 tasks
aws ecs create-service \
  --cluster extractpdf-cluster \
  --service-name extractpdf \
  --task-definition extractpdf:1 \
  --desired-count 3 \
  --launch-type FARGATE

# Scale to 5 tasks
aws ecs update-service \
  --cluster extractpdf-cluster \
  --service extractpdf \
  --desired-count 5
```

### Pros
✅ Zero infrastructure management
✅ Automatic scaling
✅ Pay only for usage
✅ CDN integration
✅ Monitoring included
✅ Auto-failover

### Cons
❌ Vendor lock-in
❌ Potential cost surprises
❌ Less control
❌ Data residency concerns

### Options
- **AWS ECS/Fargate** - Amazon's container service
- **Google Cloud Run** - Serverless containers
- **Azure Container Instances** - Microsoft's service
- **DigitalOcean App Platform** - Simpler alternative

### When to Use
- Cloud-first strategy
- Don't want to manage infrastructure
- Auto-scaling important
- Can tolerate vendor lock-in

---

## Comparison Matrix

| Feature | Compose | Swarm | Kubernetes | Cloud |
|---------|---------|-------|------------|-------|
| **Complexity** | ⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Learning Curve** | Easy | Medium | Hard | Hard |
| **Setup Time** | Minutes | Hours | Days | Hours |
| **Scalability** | Manual | Auto | Auto | Auto |
| **Multi-Node** | No | Yes | Yes | Yes |
| **Multi-Region** | No | No | Yes | Yes |
| **Cost** | Free | Free | $$ | $ (cloud) |
| **Production Ready** | Small | Medium | Enterprise | Enterprise |
| **Best For** | Dev/Testing | Small prod | Large prod | Cloud |

---

## Recommendation

### For Your Project

**Use Docker Compose with Load Balancer (Option 1)** because:

1. ✅ You already use Docker
2. ✅ Perfect for 3 instances
3. ✅ Easy to understand and maintain
4. ✅ Quick startup/shutdown
5. ✅ Good for development and small production
6. ✅ Minimal overhead
7. ✅ Can scale to Option 2 or 4 later if needed

### Migration Path

```
Development: Docker Compose (Option 1)
    ↓
Load Testing: Dynamic Scaling (Option 2)
    ↓
Small Production: Docker Compose + monitoring
    ↓
Large Production: Kubernetes (Option 4)
    ↓
Cloud-Native: Managed Service (Option 5)
```

---

## Advanced Configurations

### A/B Testing (Canary Deployments)

Route 10% to new version, 90% to old:

```nginx
upstream extractpdf_backend {
    server app1:8080 weight=1;  # New version
    server app2:8080 weight=9;  # Old version
    server app3:8080 weight=9;  # Old version
}
```

### Blue-Green Deployment

Keep two separate environments, switch traffic:

```bash
# Run "green" environment (new)
docker-compose -f docker-compose-green.yml up -d

# Test
curl http://localhost

# Once verified, switch Nginx upstream
# Then delete "blue" environment
docker-compose -f docker-compose-blue.yml down
```

### Geographic Load Balancing

Nginx can route based on IP:

```nginx
geo $region {
    default 0;
    10.0.0.0/8 1;
    192.168.0.0/16 2;
}

upstream backend_region1 {
    server app1:8080;
}

upstream backend_region2 {
    server app2:8080;
    server app3:8080;
}

location / {
    proxy_pass http://backend_region$region;
}
```

---

## Decision Tree

```
Do you need:
├─ Single machine deployment?
│  └─ Yes → Option 1 (Docker Compose) ✓
│
├─ Multiple machines?
│  ├─ Don't want to manage K8s?
│  │  └─ Yes → Option 3 (Docker Swarm)
│  │
│  └─ Can manage K8s?
│     ├─ Yes → Option 4 (Kubernetes)
│     └─ No → Option 5 (Cloud Platform)
│
└─ Need cloud-native?
   └─ Yes → Option 5
```

---

## Next Steps

1. **Stick with Option 1** - Current implementation with Compose
2. **Monitor performance** - Use `docker stats`
3. **Plan for growth** - Know when to migrate
4. **Document configuration** - Keep setup documented
5. **Test failure scenarios** - Kill containers, test recovery
6. **Backup strategy** - Backup shared volumes
7. **Monitoring setup** - Add prometheus/grafana
8. **Security hardening** - Add SSL/TLS, authentication

For questions about scaling or migration, refer back to this guide!

