# Docker 3 Instances - Visual Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     User/Client Requests                         │
│                      http://localhost                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │                 │
                    │   Nginx:80      │
                    │  Load Balancer  │
                    │ (least-conn)    │
                    │                 │
                    └─────┬───────────┘
                          │
          ┌───────────────┼───────────────┐
          │               │               │
    ┌─────▼─────┐   ┌─────▼─────┐   ┌────▼──────┐
    │  Spring   │   │  Spring   │   │  Spring   │
    │  Boot     │   │  Boot     │   │  Boot     │
    │  App 1    │   │  App 2    │   │  App 3    │
    │ :8080     │   │ :8080     │   │  :8080    │
    │           │   │           │   │           │
    │ Instance  │   │ Instance  │   │ Instance  │
    │ Name: 1   │   │ Name: 2   │   │ Name: 3   │
    └─────┬─────┘   └─────┬─────┘   └─────┬─────┘
          │               │               │
          └───────────────┼───────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
  ┌─────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐
  │   Logs     │  │   Logs      │  │   Logs      │
  │  App 1/    │  │  App 2/     │  │  App 3/     │
  │  logs      │  │  logs       │  │  logs       │
  └────────────┘  └─────────────┘  └─────────────┘
        │                 │                 │
        └─────────────────┴─────────────────┘
                          │
             ┌────────────┼────────────┐
             │            │            │
        ┌────▼───┐   ┌────▼───┐   ┌───▼────┐
        │  PDF   │   │Lucene  │   │ Logs   │
        │ Folder │   │ Index  │   │Shared  │
        │Shared  │   │Shared  │   │        │
        └────────┘   └────────┘   └────────┘
```

---

## Request Flow - Load Balancing

```
User Request
    │
    ▼
┌─────────────────┐
│   Nginx (80)    │
└────────┬────────┘
         │
         ├─→ App1:8080 (5 connections) ──┐ least_conn: route to App2
         │                                │
         ├─→ App2:8080 (2 connections) ◄─┤ (lowest connection count)
         │
         └─→ App3:8080 (4 connections)

New Request comes in...
    │
    ▼
Route to App2 (only 2 active)
    │
    ▼
Process Request
    │
    ▼
Return Response
```

---

## Data Flow - Shared Resources

```
App1          App2          App3
 │             │             │
 └─────────────┼─────────────┘
               │
        ┌──────▼──────┐
        │   Shared    │
        │   PDF Docs  │
        │  ./pdfdocs  │
        └──────▬──────┘
               │
        ┌──────▼──────┐
        │   Shared    │
        │   Lucene    │
        │    Index    │
        └─────────────┘

Each instance can:
- Read PDFs from shared folder
- Update search index
- Share search results
```

---

## Container Network - Docker Internal

```
┌─────────────────────────────────────────┐
│      extractpdf-network (bridge)        │
│                                         │
│  ┌──────────┐  ┌──────────┐             │
│  │nginx:80  │  │app1:8080 │             │
│  │          │  │          │             │
│  │IP:172... │  │IP:172... │             │
│  └──────────┘  └──────────┘             │
│       │             │                   │
│       └─────────────┘                   │
│           │                             │
│  ┌──────────────────┐  ┌──────────┐    │
│  │                  │  │app2:8080 │    │
│  │     Shared       │  │          │    │
│  │    Volumes       │  │IP:172... │    │
│  │                  │  └──────────┘    │
│  │ - pdfdocs        │       │          │
│  │ - lucene-index   │       │          │
│  │ - logs           │  ┌──────────┐    │
│  │                  │  │app3:8080 │    │
│  └──────────────────┘  │          │    │
│                         │IP:172... │    │
│                         └──────────┘    │
│                             │           │
│                             └───────────┘
│                                         │
└─────────────────────────────────────────┘
```

---

## Service Dependencies & Health Checks

```
┌─────────────────────────────────┐
│   Nginx                         │
│ depends_on: [app1, app2, app3]  │
│ healthcheck: /health (30s)      │
│ port: 80:80                     │
└────────────┬────────────────────┘
             │
    ┌────────┼────────┐
    │        │        │
    ▼        ▼        ▼
┌─────┐  ┌─────┐  ┌─────┐
│App1 │  │App2 │  │App3 │
├─────┤  ├─────┤  ├─────┤
│HC: ✓│  │HC: ✓│  │HC: ✓│  (HC = Health Check)
│Port:│  │Port:│  │Port:│
│8080 │  │8080 │  │8080 │
└─────┘  └─────┘  └─────┘

Health check status:
• Running: ✓ (Green)
• Starting: ⏳ (Yellow)
• Unhealthy: ✗ (Red)
• Exited: ✗ (Gray)
```

---

## Lifecycle - Startup Sequence

```
Time    Event                           Status
────────────────────────────────────────────
T0      User runs:                     
        docker-compose up -d
        │
T1      Pulling images                 ⏳
        │
T2      Creating network               ⏳
        │
T3      Starting app1                  ⏳ Starting
        │
T4      Starting app2                  ⏳ Starting
        │
T5      Starting app3                  ⏳ Starting
        │
T10     App1 ready                     ✓ Running
        │
T15     App2 ready                     ✓ Running
        │
T20     App3 ready                     ✓ Running
        │
T25     Starting nginx                 ⏳ Starting
        │
T30     Nginx ready                    ✓ Running
        │
T35     All services healthy           ✓ Ready
        │
        Service available at:
        http://localhost
```

---

## Failure & Recovery - Auto-Failover

```
Normal State:
  Nginx → App1 ✓
       → App2 ✓
       → App3 ✓

App2 crashes:
  Nginx → App1 ✓
       → App2 ✗ (unhealthy - marked down)
       → App3 ✓

Nginx routes to App1 and App3 only:
  ┌─────────────────────┐
  │  Load Balancer      │
  │  (automatic)        │
  └────────┬────────────┘
           │
      ┌────┴─────┐
      │           │
      ▼           ▼
    App1 ✓      App3 ✓

App2 recovers:
  Nginx → App1 ✓
       → App2 ✓ (back online)
       → App3 ✓

All services healthy again
```

---

## Volume Mapping - Host to Container

```
Host Machine                    Container
─────────────────────────────────────────

C:\...\extractpdftext\
│
├─ pdfdocs/              ──→  /app/pdfdocs/
│  (Host PDF files)           (All 3 apps read/write)
│
├─ lucene-index/         ──→  /app/lucene-index/
│  (Host index data)          (Shared search index)
│
└─ logs/
   ├─ app1/              ──→  /app/logs (app1 container)
   ├─ app2/              ──→  /app/logs (app2 container)
   └─ app3/              ──→  /app/logs (app3 container)

All mapped as:
- Read/Write (rw)
- Persistent (survives container restart)
- Shared (accessible across instances)
```

---

## Port Mapping & Network

```
External World          Docker Host          Container
──────────────────────────────────────────────────────

User:
http://localhost          Nginx:80             Port 80
                            ↓
                          (routes)
                            ↓
                      App1:8080 (internal)
                      App2:8080 (internal)
                      App3:8080 (internal)

Note: Only port 80 is exposed to the outside.
App ports 8080 are internal to Docker network.
```

---

## Load Balancing Algorithm - Least Connections

```
Initial State:
  App1: 0 connections
  App2: 0 connections
  App3: 0 connections

Request 1:
  Route to App1 (all equal, pick first)
  App1: 1 connection ◄── REQUEST 1

Request 2:
  Route to App2 (least is App2/App3)
  App2: 1 connection ◄── REQUEST 2

Request 3:
  Route to App3
  App3: 1 connection ◄── REQUEST 3

Request 4:
  Route to App1 (all equal now, pick least used by round-robin)
  App1: 2 connections ◄── REQUEST 4

Long-running Request on App2 finishes:
  App2: 0 connections ◄── REQUEST next goes here

This keeps connections balanced across all instances.
```

---

## CPU/Memory Distribution

```
Total System Resources
┌─────────────────────────────────────┐
│                                     │
│  Available: 8GB RAM, 4 CPU Cores   │
│                                     │
├──────────────────────────────────┤
│                                     │
│  Nginx:        100MB, 0.2 CPU      │
│                                     │
├──────────────────────────────────┤
│                                     │
│  App1:         512MB, 0.8 CPU      │
│  App2:         512MB, 0.8 CPU      │
│  App3:         512MB, 0.8 CPU      │
│                                     │
├──────────────────────────────────┤
│                                     │
│  System/Docker: 2.4GB, 1.6 CPU    │
│                                     │
└─────────────────────────────────────┘

Distribution with 3 instances:
- Equal load sharing
- No single point bottleneck
- 3x processing capacity
```

---

## Log Flow - Isolation

```
App1                   App2                   App3
 │                      │                      │
 ▼                      ▼                      ▼
stdout/stderr      stdout/stderr          stdout/stderr
 │                      │                      │
 ▼                      ▼                      ▼
┌───────────────┐  ┌───────────────┐  ┌────────────────┐
│ Container     │  │ Container     │  │ Container      │
│ Log Driver    │  │ Log Driver    │  │ Log Driver     │
└───────────────┘  └───────────────┘  └────────────────┘
 │                      │                      │
 ▼                      ▼                      ▼
/app/logs          /app/logs              /app/logs
 │                      │                      │
 ▼                      ▼                      ▼
Host:                Host:                  Host:
logs/app1/         logs/app2/              logs/app3/
application.log    application.log         application.log

View all:
  docker-compose logs

View specific:
  docker-compose logs app1
```

---

## Docker Network - Service Discovery

```
Nginx tries to reach: http://app1:8080
                          ↓
       Docker DNS resolver (127.0.0.11:53)
                          ↓
       Looks up 'app1' in network
                          ↓
       Finds container IP: 172.20.0.2
                          ↓
       Routes traffic to: 172.20.0.2:8080
                          ↓
       App1 receives request ✓

This automatic service discovery means:
- No hardcoded IPs needed
- Services can be restarted
- Network handles routing
- Works transparently
```

---

## Monitoring & Observability

```
┌────────────────────────────────────┐
│     Docker Monitoring Stack        │
├────────────────────────────────────┤
│                                    │
│  ┌──────────────────────────────┐  │
│  │   docker stats               │  │
│  │  Shows:                      │  │
│  │  - CPU usage                 │  │
│  │  - Memory usage              │  │
│  │  - Network I/O               │  │
│  │  - Disk I/O                  │  │
│  └──────────────────────────────┘  │
│                                    │
│  ┌──────────────────────────────┐  │
│  │   docker-compose logs        │  │
│  │  Shows:                      │  │
│  │  - Application logs          │  │
│  │  - Error messages            │  │
│  │  - Performance metrics       │  │
│  └──────────────────────────────┘  │
│                                    │
│  ┌──────────────────────────────┐  │
│  │   docker-compose ps          │  │
│  │  Shows:                      │  │
│  │  - Container status          │  │
│  │  - Health status             │  │
│  │  - Uptime                    │  │
│  └──────────────────────────────┘  │
│                                    │
└────────────────────────────────────┘
```

---

## Scaling Scenarios

### Current: 3 Instances
```
Nginx
 │
 ├─→ App1
 ├─→ App2
 └─→ App3

Load per instance: 33% (roughly)
```

### If Scaled to 5 Instances
```
Nginx
 │
 ├─→ App1
 ├─→ App2
 ├─→ App3
 ├─→ App4 (new)
 └─→ App5 (new)

Load per instance: 20% (roughly)
```

### If Scaled Down to 2 Instances
```
Nginx
 │
 ├─→ App1
 └─→ App2

Load per instance: 50% (roughly)
```

---

This visual guide helps understand how Docker orchestrates your 3-instance setup!

