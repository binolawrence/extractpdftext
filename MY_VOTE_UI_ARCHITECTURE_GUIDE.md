# React App Integration - Visual Architecture Guide

## System Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Internet Users                                 │
│                   http://mycommune.org:80                             │
└────────────────────────┬─────────────────────────────────────────────┘
                         │
                         │ REQUEST
                         ▼
    ┌────────────────────────────────────────────┐
    │      Your Host System (Linux Server)       │
    │      /etc/nginx/sites-enabled/my-vote-ui  │
    │                                            │
    │     ┌─────────────────────────────────┐    │
    │     │   External Nginx (Port 80)      │    │
    │     │                                  │    │
    │     │  ┌────────────────────────────┐ │    │
    │     │  │ Serves React App           │ │    │
    │     │  │ From:/var/www/my-vote-ui   │ │    │
    │     │  │       /dist                 │ │    │
    │     │  │                             │ │    │
    │     │  │ location / {                │ │    │
    │     │  │   try_files $uri $uri/     │ │    │
    │     │  │   /index.html               │ │    │
    │     │  │ }                           │ │    │
    │     │  └────────────────────────────┘ │    │
    │     │                                  │    │
    │     │  ┌────────────────────────────┐ │    │
    │     │  │ API Routing                │ │    │
    │     │  │                             │ │    │
    │     │  │ location /api/ {            │ │    │
    │     │  │   proxy_pass http://       │ │    │
    │     │  │   localhost:8081/          │ │    │
    │     │  │ }                           │ │    │
    │     │  └────────────────────────────┘ │    │
    │     └──────┬──────────────────────────┘    │
    │            │                                │
    │            │ Port 8081                      │
    │            ▼                                │
    │     ┌──────────────────────────┐           │
    │     │ Docker Container Network │           │
    │     │                          │           │
    │     │  ┌────────────────────┐  │           │
    │     │  │  Docker Nginx      │  │           │
    │     │  │  (Load Balancer)   │  │           │
    │     │  │                    │  │           │
    │     │  │ least_conn         │  │           │
    │     │  │ algorithm          │  │           │
    │     │  │                    │  │           │
    │     │  │ ┌────┐┌────┐┌────┐│  │           │
    │     │  │ │app1││app2││app3││  │           │
    │     │  │ │ ↓  ││ ↓  ││ ↓  ││  │           │
    │     │  │ │ 0  ││ 1  ││ 2  ││  │ (pick    │
    │     │  │ │ con││ con││ con││  │  least   │
    │     │  │ │    ││    ││    ││  │  busy)   │
    │     │  │ └────┘└────┘└────┘│  │           │
    │     │  └────────────────────┘  │           │
    │     │            │             │           │
    │     │            ▼             │           │
    │     │  ┌────────────────────┐  │           │
    │     │  │  Backend Instance  │  │           │
    │     │  │  (Spring Boot)     │  │           │
    │     │  │  Selected via LB   │  │           │
    │     │  │                    │  │           │
    │     │  │  Process Request   │  │           │
    │     │  │  Return Response   │  │           │
    │     │  └────────────────────┘  │           │
    │     └──────────────────────────┘           │
    └────────────────────────────────────────────┘
```

---

## Request Flow Diagram

### User Request for React App

```
Browser Request
   ↓
http://mycommune.org/
   ↓
External Nginx Port 80
   ↓
location / {
   try_files $uri $uri/ /index.html;
}
   ↓
Serves: /var/www/my-vote-ui/dist/index.html
   ↓
Browser receives HTML
   ↓
React.js loads
   ↓
Ready for user interaction
```

### API Request from React

```
React Component
   ↓
fetch('/api/pdf/search?query=test')
   ↓
Browser sends request to localhost:80
   ↓
External Nginx Port 80
   ↓
location /api/ {
   proxy_pass http://localhost:8081/;
}
   ↓
Forwards to Docker Nginx Port 8081
   ↓
Docker Nginx (Load Balancer)
   ├─ App1 (2 connections)
   ├─ App2 (1 connection) ← SELECTED (least)
   └─ App3 (3 connections)
   ↓
Request sent to App2
   ↓
Spring Boot App2
   ├─ Search PDFs
   ├─ Query database
   └─ Return results
   ↓
Response back through Nginx
   ↓
External Nginx
   ↓
Browser receives JSON
   ↓
React updates UI
   ↓
User sees results
```

---

## Load Balancing Algorithm

```
┌─────────────────────────────────────────────────────────┐
│        Docker Nginx - Least Connections Algorithm       │
└─────────────────────────────────────────────────────────┘

INITIAL STATE:
  App1: 0 active connections
  App2: 0 active connections  
  App3: 0 active connections

REQUEST 1 ARRIVES:
  Check: App1(0), App2(0), App3(0)
  Select: App1 (first, all equal)
  ──→ Route to App1
  
  App1: 1 active connection

REQUEST 2 ARRIVES:
  Check: App1(1), App2(0), App3(0)
  Select: App2 (least = 0)
  ──→ Route to App2
  
  App1: 1 active connection
  App2: 1 active connection

REQUEST 3 ARRIVES:
  Check: App1(1), App2(1), App3(0)
  Select: App3 (least = 0)
  ──→ Route to App3
  
  App1: 1 active connection
  App2: 1 active connection
  App3: 1 active connection

REQUEST 4 ARRIVES:
  Check: App1(1), App2(1), App3(1)
  Select: App1 (first, all equal now)
  ──→ Route to App1
  
  App1: 2 active connections
  App2: 1 active connection
  App3: 1 active connection

REQUEST 5 ARRIVES (long running):
  Check: App1(2), App2(1), App3(1)
  Select: App2 (least = 1)
  ──→ Route to App2
  
  App1: 2 active connections
  App2: 2 active connections
  App3: 1 active connection

REQUEST 6 ARRIVES:
  Check: App1(2), App2(2), App3(1)
  Select: App3 (least = 1)
  ──→ Route to App3
  
  App1: 2 active connections
  App2: 2 active connections
  App3: 2 active connections

RESULT: Load perfectly balanced across all 3 instances ✓
```

---

## Comparison: Before vs After

### BEFORE (Single Instance)

```
┌─────────────────────────────┐
│   React App @ Port 80       │
│                             │
│  Serves frontend            │
│  Proxies /api/ to 8080      │
└──────────────┬──────────────┘
               │
               ▼
    ┌──────────────────┐
    │ Single Spring    │
    │ Boot @ 8080      │
    │                  │
    │ SINGLE POINT OF  │
    │ FAILURE          │
    │                  │
    │ If this crashes: │
    │ ❌ All users     │
    │    affected      │
    └──────────────────┘
```

**Problem:**
- ❌ No redundancy
- ❌ Limited scalability
- ❌ Single point of failure
- ❌ No load balancing

### AFTER (Load Balanced)

```
┌─────────────────────────────┐
│   React App @ Port 80       │
│                             │
│  Serves frontend            │
│  Proxies /api/ to 8081      │
└──────────────┬──────────────┘
               │
               ▼
    ┌──────────────────────┐
    │  Docker Nginx        │
    │  Load Balancer       │
    │  @ Port 8081         │
    │                      │
    │  Least-Connections   │
    │  Algorithm           │
    │                      │
    │  ┌──┬──┬──┐         │
    │  ├──┤S1├──┤         │
    │  │S2├──┤S3│         │
    │  └──┴──┴──┘         │
    │                      │
    │  Health Checks       │
    │  Every 30 seconds    │
    └──────────────────────┘
           │  │  │
    ┌──────┘  │  └──────┐
    │         │         │
    ▼         ▼         ▼
 ┌────┐   ┌────┐   ┌────┐
 │App1│   │App2│   │App3│
 │8080│   │8080│   │8080│
 │ ✓  │   │ ✓  │   │ ✓  │
 └────┘   └────┘   └────┘
```

**Benefits:**
- ✅ Redundancy (3 instances)
- ✅ Load balancing
- ✅ Automatic failover
- ✅ Better performance
- ✅ Easy scaling

---

## Port Traffic Flow

```
INTERNET
   │
   │ :80 (HTTP)
   ▼
┌──────────────────────────────┐
│  Your Linux Server           │
│  External Nginx (:80)        │
└──────────────────────────────┘
   │
   │ Static files → /var/www/my-vote-ui/dist/
   ├─→ React HTML/CSS/JS
   │
   │ /api/* → localhost:8081
   ▼
┌──────────────────────────────────────────┐
│        Docker Container Network          │
│                                          │
│   Docker Nginx (:8081 from host)        │
│   Nginx (:80 internal)                  │
│   Load Balancer                         │
│                                          │
└──────────────────────────────────────────┘
   │
   │ Least-connections distribution
   │
   ├─→ App1 (:8080 internal)
   ├─→ App2 (:8080 internal)
   └─→ App3 (:8080 internal)
```

---

## Configuration Comparison

| Aspect | Before | After |
|--------|--------|-------|
| **Frontend Port** | 80 | 80 (same) |
| **API Endpoint** | localhost:8080 | localhost:8081 |
| **Backend Instances** | 1 | 3 |
| **Load Balancing** | None | Least-Connections |
| **Failover** | None | Automatic |
| **Scalability** | Limited | Easy |
| **Availability** | Single point | Redundant |

---

## Verification Checklist

```
✓ Docker containers running
  docker-compose ps
  Should show: extractpdf-nginx, app1, app2, app3

✓ Port 8081 exposed
  docker-compose ps
  Should show: 0.0.0.0:8081->80/tcp

✓ Nginx config correct
  sudo nginx -t
  Should return: syntax OK

✓ External Nginx reloaded
  curl http://mycommune.org/
  Should return React app HTML

✓ Docker Nginx accessible
  curl http://localhost:8081/health
  Should return: 200 OK

✓ API calls working
  curl http://mycommune.org/api/pdf/list
  Should return: PDF list

✓ Load balancing active
  docker-compose logs app1 app2 app3
  Should see: requests distributed across instances
```

---

## Summary

**Your React app now has:**
- ✅ Frontend served from `/var/www/my-vote-ui/dist/` (Port 80)
- ✅ Backend API calls routed through Docker Nginx (Port 8081)
- ✅ 3 backend instances with automatic load balancing
- ✅ Automatic failover if any instance fails
- ✅ Better performance and reliability

**The key change:**
- **Before:** `proxy_pass http://localhost:8080/`
- **After:** `proxy_pass http://localhost:8081/`

**One line change, massive improvement!** 🚀

