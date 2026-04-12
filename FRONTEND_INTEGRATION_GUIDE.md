# Frontend Integration Guide - Docker 3 Instances

## Architecture Overview

Your new architecture will be:

```
┌─────────────────────────────────┐
│   External User (Port 80)        │
└──────────────┬──────────────────┘
               │
         ┌─────▼──────┐
         │  Frontend   │
         │  App Port 80│ (React, Vue, Angular, etc.)
         └─────┬──────┘
               │
        ┌──────▼───────┐
        │ Nginx (Int)  │  (Internal only - no external port)
        │ Load Balancer│  API endpoint: http://nginx/
        └──────┬───────┘
               │
    ┌──────────┼──────────┐
    │          │          │
┌───▼──┐  ┌──▼───┐  ┌───▼──┐
│ App1 │  │ App2 │  │ App3 │
│ 8080 │  │ 8080 │  │ 8080 │
└──────┘  └──────┘  └──────┘
```

**Key Points:**
- ✅ Frontend app is accessible externally on port 80
- ✅ Backend services (app1, app2, app3) are NOT exposed externally
- ✅ Frontend communicates with backend through nginx internally
- ✅ Nginx handles load balancing across 3 backend instances
- ✅ Security improved: backend only accessible from frontend

---

## Changes Made to docker-compose.yml

### Before (Old Setup)
```yaml
nginx:
  ports:
    - "80:80"      # ❌ Backend exposed directly on port 80
    - "443:443"
```

### After (New Setup)
```yaml
nginx:
  # No ports exposed - internal only
  expose:
    - "80"         # ✅ Internal only, not exposed externally
  # Frontend accesses at: http://nginx/
```

---

## How to Integrate Your Frontend App

### Option 1: React/Node.js Frontend (Recommended)

**Step 1: Uncomment the frontend service in docker-compose.yml**

```yaml
frontend:
  build:
    context: ../frontend        # Path to your React project
    dockerfile: Dockerfile
  container_name: extractpdf-frontend
  ports:
    - "80:3000"                 # Frontend on port 80
  environment:
    - REACT_APP_API_URL=http://nginx
    - NODE_ENV=production
  depends_on:
    - nginx
  networks:
    - extractpdf-network
```

**Step 2: In your React app, make API calls to the backend:**

```javascript
// In your React component
const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost';

// Call PDF extraction endpoint
fetch(`${API_BASE}/api/pdf/upload`, {
  method: 'POST',
  body: formData
})

// Call search endpoint
fetch(`${API_BASE}/api/search?query=Durairaj`)

// Check health
fetch(`${API_BASE}/health`)
```

**Step 3: Create Dockerfile for your React app**

```dockerfile
# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Runtime stage
FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/build ./build
COPY --from=builder /app/package*.json ./
RUN npm install --only=production
EXPOSE 3000
CMD ["npm", "start"]
```

### Option 2: Angular Frontend

```yaml
frontend:
  build:
    context: ../frontend-angular
    dockerfile: Dockerfile
  ports:
    - "80:80"
  environment:
    - API_URL=http://nginx
  depends_on:
    - nginx
  networks:
    - extractpdf-network
```

**In your Angular environment config:**

```typescript
// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'http://nginx'
};

// In your service:
constructor(private http: HttpClient) {
  const baseUrl = environment.apiUrl;
  this.http.get(`${baseUrl}/api/pdf/list`);
}
```

### Option 3: Vue.js Frontend

```yaml
frontend:
  build:
    context: ../frontend-vue
    dockerfile: Dockerfile
  ports:
    - "80:80"
  environment:
    - VUE_APP_API_URL=http://nginx
  depends_on:
    - nginx
  networks:
    - extractpdf-network
```

**In your Vue.js app:**

```javascript
// In main.js or api.js
export const API_BASE = process.env.VUE_APP_API_URL || 'http://localhost';

// In your component:
this.$http.get(`${API_BASE}/api/pdf/search`);
```

---

## Available Endpoints for Frontend

All endpoints accessed through `http://nginx` (internally) or direct backend URLs:

### Health Check
```
GET http://nginx/health
```

### PDF Upload
```
POST http://nginx/api/pdf/upload
Content-Type: multipart/form-data
```

### PDF List
```
GET http://nginx/api/pdf/list
```

### PDF Search
```
GET http://nginx/api/pdf/search?query=text
```

### PDF Download
```
GET http://nginx/api/pdf/download/{filename}
```

### Indexing
```
POST http://nginx/api/index
```

---

## Running Everything Together

### Step 1: Start All Services
```bash
# Build and start (frontend + backend)
docker-compose up -d

# Or just the backend (if frontend is not in docker-compose.yml yet)
docker-compose up -d app1 app2 app3 nginx
```

### Step 2: Verify All Services Running
```bash
docker-compose ps

# Expected output:
# NAME                  STATUS              PORTS
# extractpdf-frontend   Up (healthy)        0.0.0.0:80->3000/tcp
# extractpdf-nginx      Up (healthy)        (no ports)
# extractpdf-app1       Up (healthy)        (no ports)
# extractpdf-app2       Up (healthy)        (no ports)
# extractpdf-app3       Up (healthy)        (no ports)
```

### Step 3: Access Your Application
```
Open browser: http://localhost
```

**You'll see your frontend app, which communicates with the backend through nginx!**

---

## Communication Flow

### Request from Frontend to Backend

```
1. User makes request in browser
   http://localhost/search?query=test

2. Frontend app (port 80) receives request
   Routes to React/Vue/Angular component

3. Component needs PDF data
   Calls: http://nginx/api/pdf/search?query=test

4. Nginx receives request (internal network)
   Uses load balancing to route to backend
   
5. Nginx picks least-connected backend
   ├─ If App1: 2 connections
   ├─ If App2: 1 connection ← PICKED (least connections)
   └─ If App3: 3 connections

6. Request sent to App2
   Processes search request
   Returns results

7. Nginx returns response to Frontend

8. Frontend app receives data
   Updates UI with results
   
9. User sees results in browser
```

---

## Key Configuration Points

### Environment Variables in Frontend

```yaml
frontend:
  environment:
    # Development
    - REACT_APP_API_URL=http://nginx

    # Production (external)
    # - REACT_APP_API_URL=https://your-domain.com

    # For debugging
    - DEBUG=true
```

### Nginx Health Check Endpoint

Frontend can call this to verify backend is available:

```javascript
async function checkBackendHealth() {
  try {
    const response = await fetch('http://nginx/health');
    if (response.ok) {
      console.log('Backend is healthy');
      return true;
    }
  } catch (error) {
    console.error('Backend is down:', error);
    return false;
  }
}

// Call on app startup
window.addEventListener('load', checkBackendHealth);
```

---

## Network Communication

### Internal Communication (Frontend to Backend)
```
Frontend container → Nginx container
                  → App1/App2/App3 containers
(All on same Docker network: extractpdf-network)
```

### Security Benefits
- ✅ Backend is NOT exposed to internet
- ✅ Backend only accessible from frontend
- ✅ Frontend is the only external interface
- ✅ Load balancer (nginx) handles distribution internally
- ✅ Can add authentication/authorization at frontend

---

## Common Issues & Solutions

### Issue 1: Frontend Can't Connect to Backend

**Error:** `Cannot fetch http://nginx/api/...`

**Solution:** Ensure:
1. Frontend and backend on same network (`extractpdf-network`)
2. Frontend depends on nginx: `depends_on: - nginx`
3. Frontend starts after nginx: `start_period: 40s`
4. API URL is correct: `http://nginx` (not `http://localhost`)

**Fix:**
```yaml
frontend:
  depends_on:
    - nginx      # ← Add this
  networks:
    - extractpdf-network  # ← Ensure same network
  environment:
    - API_URL=http://nginx  # ← Use container name, not localhost
```

### Issue 2: Port 80 Still in Use

**Error:** `Port 80 is already allocated`

**Solution:** Change port in docker-compose.yml:
```yaml
frontend:
  ports:
    - "8080:3000"  # Changed to 8080
```

Then access: `http://localhost:8080`

### Issue 3: Frontend Getting 502/503 Errors

**Error:** `Bad Gateway` or `Service Unavailable`

**Cause:** Backend services slow to start

**Solution:** Increase start period:
```yaml
frontend:
  healthcheck:
    start_period: 60s  # Increase from 40s
```

### Issue 4: CORS Issues

**Error:** `Access to XMLHttpRequest blocked by CORS policy`

**Solution:** Add CORS headers in nginx.conf:
```nginx
location / {
    proxy_pass http://extractpdf_backend;
    
    # Add CORS headers
    add_header 'Access-Control-Allow-Origin' '*';
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization';
}
```

---

## Monitoring & Debugging

### Check Frontend Logs
```bash
docker-compose logs frontend -f
```

### Check Nginx Logs
```bash
docker-compose logs nginx -f
```

### Check Backend Logs
```bash
docker-compose logs app1 -f
```

### Test Connection from Frontend
```bash
# Get into frontend container
docker-compose exec frontend /bin/sh

# Test backend connectivity
curl http://nginx/health
curl http://nginx/api/pdf/list
```

---

## Next Steps

1. **Prepare your frontend project structure:**
   ```
   projects/
   ├── extractpdftext/       (Current backend)
   │   ├── docker-compose.yml
   │   └── ...
   └── extractpdf-frontend/  (Your frontend)
       ├── Dockerfile
       ├── package.json
       └── src/
   ```

2. **Uncomment the frontend service in docker-compose.yml**

3. **Update API endpoints in your frontend code**
   - Replace `localhost:8080` with `http://nginx`
   - Or use environment variable `REACT_APP_API_URL`

4. **Build frontend Dockerfile**
   - Multi-stage build (recommended)
   - Node.js for build, lightweight runtime for production

5. **Start everything:**
   ```bash
   docker-compose up -d
   ```

6. **Verify:**
   ```bash
   docker-compose ps
   curl http://localhost/health
   ```

---

## Architecture Comparison

### Before (Direct Exposure)
```
Browser → Port 80 → Nginx → 3 Backend Instances
(Backend exposed to internet)
```

### After (Frontend Mediation)
```
Browser → Port 80 → Frontend App → Nginx (internal) → 3 Backend Instances
(Backend hidden, only accessible from frontend)
```

---

## Production Deployment

When moving to production:

1. **Add SSL/TLS to frontend:**
   ```yaml
   frontend:
     ports:
       - "443:3000"  # HTTPS
   ```

2. **Update API URLs:**
   ```yaml
   environment:
     - REACT_APP_API_URL=https://your-domain.com
   ```

3. **Add authentication:**
   - Implement in frontend
   - Pass tokens to backend API calls

4. **Update nginx config:**
   ```nginx
   # Add security headers
   add_header Strict-Transport-Security "max-age=31536000";
   add_header X-Content-Type-Options "nosniff";
   ```

---

## Summary

✅ **Backend is now internal-only**
✅ **Frontend handles external requests on port 80**
✅ **Frontend communicates with backend through nginx**
✅ **Backend instances still load-balanced by nginx**
✅ **Better security: backend hidden from internet**

**Ready to integrate your frontend app!**

For questions on any of the configurations above, refer to the main documentation files.

