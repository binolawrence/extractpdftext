# Frontend Integration - Implementation Checklist

## Pre-Integration Checklist

- [ ] Read `FRONTEND_INTEGRATION_GUIDE.md` completely
- [ ] Read `FRONTEND_INTEGRATION_QUICK_START.md` for quick reference
- [ ] Understand the new architecture (Frontend → Nginx → Backend)
- [ ] Verify backend is working: `docker-compose ps`
- [ ] Understand API endpoints your frontend needs
- [ ] Have your frontend project ready (or plan to create it)

---

## Frontend Preparation

### Frontend Technology Choice
- [ ] Decided on React, Vue, Angular, or other framework
- [ ] Project structure ready
- [ ] Build process defined
- [ ] Package.json (or equivalent) prepared

### Dockerfile for Frontend
- [ ] Created Dockerfile for your frontend
- [ ] Multi-stage build (optional but recommended)
- [ ] Exposes correct port (e.g., 3000 for React)
- [ ] Includes proper environment setup

### Environment Variables
- [ ] Identified API base URL needed: `http://nginx`
- [ ] Set up .env file for local development
- [ ] Set up environment variables for Docker

### API Integration Code
- [ ] Identified all API calls in your frontend
- [ ] Updated hardcoded URLs to use `http://nginx`
- [ ] Created API client/service file
- [ ] Tested API calls locally (if possible)

---

## docker-compose.yml Configuration

- [ ] Opened `docker-compose.yml`
- [ ] Located frontend service template (bottom, commented)
- [ ] Uncommented the frontend service
- [ ] Updated build context path to your frontend location
- [ ] Updated port mapping (e.g., "80:3000")
- [ ] Updated environment variables
- [ ] Verified depends_on: nginx is set
- [ ] Verified networks: extractpdf-network is set
- [ ] Saved the file

### Example of Uncommented Section:
```yaml
frontend:
  build:
    context: ../extractpdf-frontend
    dockerfile: Dockerfile
  container_name: extractpdf-frontend
  ports:
    - "80:3000"
  environment:
    - REACT_APP_API_URL=http://nginx
    - NODE_ENV=production
  depends_on:
    - nginx
  networks:
    - extractpdf-network
  healthcheck:
    test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:3000"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 40s
```

---

## Frontend Code Updates

### API Service Setup
- [ ] Created service file (e.g., `src/services/api.js`)
- [ ] Used environment variable for API URL
- [ ] Exported functions for all API endpoints
- [ ] Added error handling

Example:
```javascript
const API_URL = process.env.REACT_APP_API_URL || 'http://nginx';

export const searchPDFs = (query) =>
  fetch(`${API_URL}/api/pdf/search?query=${query}`);

export const uploadPDF = (formData) =>
  fetch(`${API_URL}/api/pdf/upload`, {
    method: 'POST',
    body: formData
  });
```

### Component Integration
- [ ] Updated all API calls to use new service
- [ ] Removed hardcoded localhost URLs
- [ ] Added loading states
- [ ] Added error handling
- [ ] Tested components locally

### Environment Configuration
- [ ] Created `.env.local` for development
- [ ] Created `.env.production` for production
- [ ] Verified environment variables are read correctly

---

## Pre-Deployment Testing

### Local Development (Before Docker)
- [ ] Frontend builds successfully: `npm run build`
- [ ] Frontend runs locally: `npm start`
- [ ] API calls work (if backend available)
- [ ] No console errors

### Docker Build Test
- [ ] Docker image builds: `docker build -t extractpdf-frontend .`
- [ ] Image size is reasonable
- [ ] No build warnings

### Compose Setup Test
- [ ] docker-compose.yml validates: `docker-compose config`
- [ ] No YAML errors
- [ ] All services defined correctly

---

## Deployment

### Pre-Deployment
- [ ] Backed up any important files
- [ ] Verified disk space available (2GB+)
- [ ] Verified ports available (80, 8080 if used)
- [ ] Stopped any conflicting services

### Start Services
- [ ] Ran: `docker-compose up -d`
- [ ] Waited 30-40 seconds for startup
- [ ] No error messages during startup

### Post-Deployment Verification
- [ ] Ran: `docker-compose ps`
  - [ ] frontend: Up (healthy)
  - [ ] nginx: Up (healthy)
  - [ ] app1: Up (healthy)
  - [ ] app2: Up (healthy)
  - [ ] app3: Up (healthy)

- [ ] Checked frontend: `curl http://localhost` or `curl http://localhost:80`
- [ ] Checked health: `curl http://localhost/health`
- [ ] Opened browser: `http://localhost`
- [ ] Saw frontend interface loaded

---

## Integration Testing

### Frontend Functionality
- [ ] Frontend page loads without errors
- [ ] UI is responsive
- [ ] All buttons/links work
- [ ] Navigation works

### API Calls
- [ ] API calls are being made (check browser DevTools)
- [ ] API responses are successful (200 status)
- [ ] Data is displayed correctly
- [ ] No CORS errors

### Backend Integration
- [ ] Search functionality works
- [ ] PDF upload works
- [ ] Download functionality works
- [ ] All 3 backend instances are being used

### Load Balancing
- [ ] Multiple requests distribute across instances
- [ ] Can verify with: `docker-compose logs nginx`
- [ ] Least-connections algorithm working

---

## Troubleshooting Checklist

### Frontend Won't Start
- [ ] Check logs: `docker-compose logs frontend`
- [ ] Verify Dockerfile syntax
- [ ] Check dependencies installed
- [ ] Verify port not in use

### Frontend Can't Reach Backend
- [ ] Check API URL: should be `http://nginx`
- [ ] Verify backend is running: `docker-compose logs app1`
- [ ] Check network: both services on `extractpdf-network`
- [ ] Test connection: `docker-compose exec frontend curl http://nginx/health`

### API Calls Failing
- [ ] Check CORS headers (see FRONTEND_INTEGRATION_GUIDE.md)
- [ ] Verify API endpoints are correct
- [ ] Check backend logs for errors
- [ ] Verify frontend environment variables set correctly

### Services Won't Stay Running
- [ ] Check health checks in docker-compose.yml
- [ ] Review service logs for errors
- [ ] Increase start_period if needed
- [ ] Check resource limits

---

## Performance Optimization

- [ ] Nginx proxy caching configured (optional)
- [ ] Frontend assets minified
- [ ] API responses optimized
- [ ] Database queries optimized
- [ ] Monitoring in place (docker stats)

---

## Production Readiness

- [ ] All hardcoded URLs replaced with config
- [ ] Environment variables properly set
- [ ] Error handling implemented
- [ ] Logging configured
- [ ] Security headers added
- [ ] HTTPS/SSL configured (if needed)
- [ ] Backup strategy in place
- [ ] Monitoring/alerting configured

---

## Documentation

- [ ] Documented API endpoints used
- [ ] Documented environment variables
- [ ] Documented any customizations made
- [ ] Documented deployment process
- [ ] Documented troubleshooting steps

---

## Success Criteria

You'll know everything is working when:

✅ `docker-compose ps` shows all 5 services running and healthy  
✅ Browser shows your frontend at `http://localhost`  
✅ Frontend can search and display PDFs  
✅ Upload functionality works  
✅ No console errors in browser  
✅ No errors in Docker logs  
✅ Load balancing working (requests distributed)  
✅ Backend services not directly accessible from internet  

---

## Final Checklist

- [ ] All items above completed
- [ ] No outstanding issues or errors
- [ ] Frontend fully integrated with backend
- [ ] All functionality tested and working
- [ ] Documentation updated
- [ ] Ready for use/deployment

---

## Estimated Time

| Phase | Time | Status |
|-------|------|--------|
| Preparation | 30 min | ⏳ Pending |
| Docker Setup | 15 min | ⏳ Pending |
| Integration | 30 min | ⏳ Pending |
| Testing | 30 min | ⏳ Pending |
| Troubleshooting | Variable | ⏳ Pending |
| **Total** | **~2 hours** | ⏳ Pending |

---

## Notes

- Keep this checklist handy during integration
- Refer to FRONTEND_INTEGRATION_GUIDE.md for detailed steps
- Check docker-compose logs frequently when troubleshooting
- Verify one step at a time, don't skip ahead

---

**Status: Ready to Start Integration** ✅

You have everything you need to integrate your frontend with the Docker 3-instance backend setup!

**Start with:** FRONTEND_INTEGRATION_GUIDE.md

