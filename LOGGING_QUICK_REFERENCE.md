# Logging Quick Reference Guide

## 🚀 Quick Start

### Development (Verbose Logging)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
**Result**: DEBUG level logging with method names and line numbers in `logs/application-dev.log`

### Production (Minimal Logging)
```bash
java -jar extractpdftext-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
**Result**: WARN level logging in `/var/log/extractpdftext/application.log`

### Testing
```bash
mvn test -Dspring.profiles.active=test
```
**Result**: DEBUG level logging in `target/test-logs/application-test.log`

---

## 📊 Profile Comparison

| Feature | Dev | Default | Prod |
|---------|-----|---------|------|
| **Root Level** | INFO | INFO | WARN |
| **App Level** | DEBUG | DEBUG | INFO |
| **Spring Level** | DEBUG | INFO | WARN |
| **File Location** | logs/app-dev.log | logs/app.log | /var/log/extractpdftext/app.log |
| **Max Size** | 10MB | 10MB | 50MB |
| **Pattern** | With method & line | Standard | Concise |
| **Use Case** | Development | General use | Production |

---

## 📝 Log Pattern Reference

```
%d{yyyy-MM-dd HH:mm:ss.SSS}  → Timestamp with milliseconds
[%thread]                     → Thread name
%-5level                      → Log level (padded)
%logger{36}                   → Logger name (shortened)
.%M(%L)                       → Method name and line (dev only)
- %msg%n                      → Message and newline
```

**Example:**
```
2026-04-08 14:23:45.123 [main] INFO  c.e.p.e.controller.PDFController - Search endpoint called
```

---

## 🎯 Logging in Code

### Add logger to any class:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyService {
    private static final Logger logger = LoggerFactory.getLogger(MyService.class);
    
    public void myMethod() {
        logger.info("Starting operation");
        logger.debug("Debug details: {}", value);
        logger.error("Error occurred", exception);
    }
}
```

---

## 🔧 Change Log Level at Runtime

Using Spring Boot Actuator:
```bash
# Check current level
curl http://localhost:8080/actuator/loggers/com.extract.pdf.extractpdftext

# Set to TRACE (verbose)
curl -X POST http://localhost:8080/actuator/loggers/com.extract.pdf.extractpdftext \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"TRACE"}'

# Set to WARN (minimal)
curl -X POST http://localhost:8080/actuator/loggers/com.extract.pdf.extractpdftext \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"WARN"}'
```

---

## 🐛 Troubleshooting

### Problem: No logs appearing
**Solution**: Check active profile
```bash
# In logs, look for:
# "Application is ready" message
# If missing, profile not activated
```

### Problem: Too many logs
**Solution**: Reduce log level in properties file
```properties
logging.level.com.extract.pdf.extractpdftext=INFO  # From DEBUG
```

### Problem: Logs slow down application
**Solution**: Use production profile
```bash
java -jar app.jar --spring.profiles.active=prod
```

---

## 📋 Classes with Logging

**Controllers**: PDFController, PDFUploadController
**Services**: PDFSearchService, PDFIndexService, PDFProcessingService
**Config**: PathConfig, CorsConfig, ApplicationStartupListener
**Utils**: StringUtils, OcrProcessor, PDFTextExtractor
**Main**: ExtractpdftextApplication

---

## 💡 Best Practices

✅ Use **INFO** for business events
✅ Use **DEBUG** for development flow tracking
✅ Use **WARN** for recoverable issues
✅ Use **ERROR** for failures with exceptions
✅ Use lazy evaluation: `logger.debug("Value: {}", expensiveCall())`
✅ Include relevant context in logs
✅ Log at entry and exit of important methods
✅ Use different profiles for different environments

---

## 📂 Log File Locations

| Profile | Path |
|---------|------|
| dev | `logs/application-dev.log` |
| default | `logs/application.log` |
| prod | `/var/log/extractpdftext/application.log` |
| test | `target/test-logs/application-test.log` |

**Note**: Create logs directory if it doesn't exist: `mkdir logs`

---

## 🔍 Checking Logs

### Console output (development)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" 2>&1 | grep "INFO\|ERROR"
```

### View log file (production)
```bash
tail -f /var/log/extractpdftext/application.log
```

### Count errors
```bash
grep "ERROR" logs/application.log | wc -l
```

### Search for specific operation
```bash
grep "Search endpoint called" logs/application.log
```

---

## 📞 Support

- **Configuration Issues**: See `LOGGING_CONFIGURATION.md`
- **Implementation Details**: See `LOGGING_IMPLEMENTATION_COMPLETE.md`
- **Active Profiles**: Use `-Dspring.profiles.active=profile-name`
- **Log Levels**: INFO (default), DEBUG (dev), WARN (prod)


