# Logging Configuration Guide

## Overview
This application uses **SLF4J with Logback** (Spring Boot default) for comprehensive logging across all classes. Log levels are configurable via `application.properties` files for different profiles.

---

## Log Levels (from lowest to highest verbosity)

| Level | Usage | When to Use |
|-------|-------|-----------|
| **TRACE** | Very detailed debugging information | Rarely used, deepest debugging |
| **DEBUG** | Detailed information for developers | Development and testing |
| **INFO** | General application information | Normal operation, key milestones |
| **WARN** | Warning messages for potential issues | Issues that don't stop execution |
| **ERROR** | Error messages with stack traces | Errors that need attention |
| **FATAL** | Critical errors (Logback uses ERROR) | System-critical failures |

---

## Configuration Files

### 1. **application.properties** (Default/Production-Ready)
Located: `src/main/resources/application.properties`

```properties
# Default log level for the entire application
logging.level.root=INFO

# Application-specific packages (DEBUG for development insights)
logging.level.com.extract.pdf.extractpdftext=DEBUG
logging.level.com.extract.pdf.extractpdftext.controller=DEBUG
logging.level.com.extract.pdf.extractpdftext.service=DEBUG
logging.level.com.extract.pdf.extractpdftext.util=DEBUG
logging.level.com.extract.pdf.extractpdftext.config=DEBUG

# Spring Framework logging levels
logging.level.org.springframework.web=INFO
logging.level.org.springframework.boot.web=INFO

# Third-party library logging (minimal for performance)
logging.level.org.apache.lucene=WARN
logging.level.org.apache.pdfbox=WARN
logging.level.org.apache.tika=WARN

# Log output format
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# File logging configuration
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=10
logging.file.total-size-cap=100MB
```

### 2. **application-dev.properties** (Development Profile)
Located: `src/main/resources/application-dev.properties`

```properties
# Verbose logging for development
logging.level.root=INFO
logging.level.com.extract.pdf.extractpdftext=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.boot.web=DEBUG

# Detailed pattern including method name and line number
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36}.%M(%L) - %msg%n

# Development log file
logging.file.name=logs/application-dev.log
```

### 3. **application-prod.properties** (Production Profile)
Located: `src/main/resources/application-prod.properties`

```properties
# Minimal logging in production (only WARN and ERROR)
logging.level.root=WARN
logging.level.com.extract.pdf.extractpdftext=INFO

# Concise log pattern
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Production log file with larger retention
logging.file.name=/var/log/extractpdftext/application.log
logging.file.max-size=50MB
logging.file.max-history=30
logging.file.total-size-cap=1GB
```

### 4. **application-test.properties** (Test Profile)
Located: `src/main/resources/application-test.properties`

```properties
# Debug level for test diagnostics
logging.level.root=WARN
logging.level.com.extract.pdf.extractpdftext=DEBUG

# Test log file
logging.file.name=target/test-logs/application-test.log
```

---

## Running with Different Profiles

### Development Environment (Verbose Logging)
```bash
mvn clean install -Dspring.profiles.active=dev
# or
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production Environment (Minimal Logging)
```bash
mvn clean install -Dspring.profiles.active=prod
# or
java -jar extractpdftext-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Test Environment
```bash
mvn test -Dspring.profiles.active=test
```

---

## Classes with Logging Implemented

### Controllers
- **PDFController**: Search and loadPDF endpoint logging
- **PDFUploadController**: PDF upload and processing logging

### Services
- **PDFIndexService**: Lucene indexing operations logging
- **PDFSearchService**: Search query execution and result logging
- **PDFProcessingService**: PDF page processing logging

### Configuration
- **PathConfig**: Path resolution and configuration source logging
- **CorsConfig**: CORS configuration logging
- **ApplicationStartupListener**: Application startup event logging

### Utilities
- **StringUtils**: String pattern matching and extraction logging
- **OcrProcessor**: OCR text extraction logging
- **PDFTextExtractor**: PDF text extraction logging

### Main Application
- **ExtractpdftextApplication**: Application startup and shutdown logging

---

## Log Pattern Explanation

```
%d{yyyy-MM-dd HH:mm:ss.SSS} - Timestamp with milliseconds
[%thread]                      - Thread name
%-5level                       - Log level (padded to 5 chars)
%logger{36}                    - Logger name (shortened to 36 chars)
.%M(%L)                        - Method name and line number (dev only)
- %msg%n                       - Log message and newline
```

### Example Log Output
```
2024-04-08 14:23:45.123 [main] INFO  c.e.p.e.ExtractpdftextApplication - Starting ExtractPDF Text Application...
2024-04-08 14:23:45.234 [main] INFO  c.e.p.e.config.PathConfig - ===== PATH CONFIGURATION =====
2024-04-08 14:23:45.235 [main] INFO  c.e.p.e.config.PathConfig - PDF Docs Directory: C:\Users\bfrancis\projects\pdfdocs
2024-04-08 14:23:46.125 [http-nio-8080-exec-1] INFO  c.e.p.e.controller.PDFController - Search endpoint called - Name: Ramesh, FatherName: Raj, HusbandName: null
2024-04-08 14:23:46.234 [http-nio-8080-exec-1] DEBUG c.e.p.e.service.PDFSearchService - Normalized to 2 valid terms
```

---

## Log File Locations

| Profile | Location | Max Size | History |
|---------|----------|----------|---------|
| Default | `logs/application.log` | 10MB | 10 files |
| Dev | `logs/application-dev.log` | 10MB | 10 files |
| Prod | `/var/log/extractpdftext/application.log` | 50MB | 30 files |
| Test | `target/test-logs/application-test.log` | 10MB | 5 files |

---

## Changing Log Level at Runtime (Advanced)

If using Spring Boot Actuator endpoints, you can change log levels dynamically:

```bash
# Check current log level
curl http://localhost:8080/actuator/loggers/com.extract.pdf.extractpdftext

# Change log level to TRACE
curl -X POST http://localhost:8080/actuator/loggers/com.extract.pdf.extractpdftext \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"TRACE"}'
```

---

## Best Practices

1. **Use DEBUG** for development - provides detailed method flow tracking
2. **Use INFO** for normal operation - key milestones and completion events
3. **Use WARN** for recoverable issues - conditions that should be investigated
4. **Use ERROR** with exceptions - always include stack traces
5. **Avoid TRACE** in production - too verbose and impacts performance
6. **Use lazy evaluation** - e.g., `logger.debug("Value: {}", expensiveOperation())` only evaluates if DEBUG is enabled

---

## Example: Adding Logging to a New Class

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyNewService {
    
    private static final Logger logger = LoggerFactory.getLogger(MyNewService.class);
    
    public void myMethod(String input) {
        logger.info("Method called with input: {}", input);
        try {
            // Business logic
            logger.debug("Processing step 1 completed");
            logger.debug("Processing step 2 completed");
            logger.info("Operation completed successfully");
        } catch (Exception e) {
            logger.error("Error processing input: {}", input, e);
            throw e;
        }
    }
}
```

---

## Troubleshooting

### Logs Not Appearing?
1. Check if log level is correctly set in properties file
2. Verify active profile: `--spring.profiles.active=dev`
3. Check console output in IDE
4. Look for log files in configured directories

### Too Many Logs?
1. Increase log level to WARN or ERROR
2. Disable DEBUG for specific packages
3. Use production profile for smaller log volume

### Log File Not Created?
1. Ensure logs directory exists or Spring Boot creates it
2. Check file permissions on Windows/Linux
3. Verify path in logging.file.name configuration


