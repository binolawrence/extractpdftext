# Configuration Guide - Properties/YAML & Environment Variables

## Overview

The application now supports flexible configuration through **three methods** with clear priority:

1. **Environment Variables** (Highest Priority)
2. **application.properties / application.yml** (Medium Priority)
3. **Hard-coded Defaults** (Lowest Priority)

This allows you to configure paths for different environments without modifying code.

---

## Configuration Methods

### Method 1: Environment Variables (Highest Priority)

Set system environment variables before running the application:

#### Windows (Command Prompt)
```batch
setx PDF_DOCS_DIR "C:\path\to\pdfdocs"
setx LUCENE_INDEX_DIR "C:\path\to\lucene-index"
```

#### Windows (PowerShell)
```powershell
[Environment]::SetEnvironmentVariable("PDF_DOCS_DIR", "C:\path\to\pdfdocs", "User")
[Environment]::SetEnvironmentVariable("LUCENE_INDEX_DIR", "C:\path\to\lucene-index", "User")
```

#### Linux/Mac
```bash
export PDF_DOCS_DIR="/path/to/pdfdocs"
export LUCENE_INDEX_DIR="/path/to/lucene-index"
```

**Precedence:** Overrides both properties files and defaults

---

### Method 2: Application Properties File (Medium Priority)

#### Using `application.properties`

Edit `src/main/resources/application.properties`:

```properties
# PDF Documents Directory
app.pdf.docs-dir=C:\\Users\\bfrancis\\projects\\pdfdocs

# Lucene Index Directory
app.lucene.index-dir=C:\\Users\\bfrancis\\projects\\lucene-index
```

**Note:** Use double backslashes `\\` on Windows paths in .properties files

#### Using `application.yml` (Alternative)

Edit or rename `application.yml.example` to `application.yml`:

```yaml
app:
  pdf:
    docs-dir: C:\Users\bfrancis\projects\pdfdocs
  lucene:
    index-dir: C:\Users\bfrancis\projects\lucene-index
```

**Note:** YAML format handles backslashes naturally

**Precedence:** Overrides hard-coded defaults, but is overridden by environment variables

---

### Method 3: Hard-coded Defaults (Lowest Priority)

Built-in defaults in `PathConfig.java`:

```java
private static final String DEFAULT_PDF_DOCS_DIR = "C:\\Users\\bfrancis\\projects\\pdfdocs";
private static final String DEFAULT_LUCENE_INDEX_DIR = "C:\\Users\\bfrancis\\projects\\lucene-index";
```

**Used when:** No environment variables or properties file values are set

---

## Environment-Specific Configurations

### Development Profile

Run with development settings:

```bash
mvn clean install -Dspring.profiles.active=dev
```

Uses `application-dev.properties`:
```properties
app.pdf.docs-dir=C:\\Users\\bfrancis\\projects\\pdfdocs
app.lucene.index-dir=C:\\Users\\bfrancis\\projects\\lucene-index
logging.level.com.extract.pdf.extractpdftext=DEBUG
```

### Production Profile

Deploy with production settings:

```bash
java -Dspring.profiles.active=prod -jar app.jar
```

Uses `application-prod.properties`:
```properties
app.pdf.docs-dir=/var/extractpdftext/pdfdocs
app.lucene.index-dir=/var/extractpdftext/lucene-index
logging.level.root=WARN
```

### Test Profile

Run tests with test settings:

```bash
mvn test -Dspring.profiles.active=test
```

Uses `application-test.properties`:
```properties
app.pdf.docs-dir=target/test-pdfdocs
app.lucene.index-dir=target/test-lucene-index
```

---

## Configuration Priority (Complete Order)

1. **Environment Variables** (PDF_DOCS_DIR, LUCENE_INDEX_DIR)
2. **Profile-specific properties** (application-{profile}.properties)
3. **Base application.properties** (application.properties)
4. **Hard-coded Defaults** (PathConfig class)

**Example:** If `PDF_DOCS_DIR` environment variable is set, it will be used regardless of what's in properties files.

---

## Property Names Reference

| Purpose | Property Key | Environment Variable |
|---------|--------------|----------------------|
| PDF Documents Directory | `app.pdf.docs-dir` | `PDF_DOCS_DIR` |
| Lucene Index Directory | `app.lucene.index-dir` | `LUCENE_INDEX_DIR` |

---

## Usage Examples

### Example 1: Development on Windows

```batch
# Set environment variables
setx PDF_DOCS_DIR "C:\dev\pdfs"
setx LUCENE_INDEX_DIR "C:\dev\lucene"

# Restart terminal and run
mvn clean install
```

### Example 2: Production Deployment on Linux

```bash
# Set environment variables for the process
export PDF_DOCS_DIR="/mnt/storage/pdfs"
export LUCENE_INDEX_DIR="/mnt/storage/lucene"

# Run the application
java -Dspring.profiles.active=prod -jar extractpdftext-0.0.1-SNAPSHOT.jar
```

### Example 3: Docker Deployment

**Dockerfile:**
```dockerfile
FROM openjdk:17-alpine
COPY target/extractpdftext-*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

**Docker Compose:**
```yaml
services:
  app:
    build: .
    environment:
      - PDF_DOCS_DIR=/data/pdfs
      - LUCENE_INDEX_DIR=/data/lucene
    volumes:
      - /mnt/storage/pdfs:/data/pdfs
      - /mnt/storage/lucene:/data/lucene
```

---

## Startup Output

When the application starts, you'll see:

```
===== PATH CONFIGURATION =====
PDF Docs Directory: C:\Users\bfrancis\projects\pdfdocs
  Source: Environment variable (PDF_DOCS_DIR)
Lucene Index Directory: C:\Users\bfrancis\projects\lucene-index
  Source: application.properties (app.lucene.index-dir)
==============================
```

This indicates which source is providing each configuration.

---

## File Locations

### Configuration Files
- `src/main/resources/application.properties` - Base configuration
- `src/main/resources/application-dev.properties` - Development profile
- `src/main/resources/application-prod.properties` - Production profile
- `src/main/resources/application-test.properties` - Test profile
- `src/main/resources/application.yml.example` - Example YAML format

### Source Code
- `src/main/java/com/extract/pdf/extractpdftext/config/PathConfig.java` - Configuration component

---

## Switching Between .properties and .yml

### To use YAML instead of properties:

1. Backup current configuration:
   ```bash
   cp src/main/resources/application.properties src/main/resources/application.properties.bak
   ```

2. Create application.yml from the example:
   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

3. Optional: Delete application.properties (Spring will prefer .yml)

**Note:** Spring Boot loads both formats, but .yml is preferred if both exist

---

## Troubleshooting

### Issue: Wrong paths being used

1. Check environment variables:
   ```bash
   # Windows
   echo %PDF_DOCS_DIR%
   
   # Linux/Mac
   echo $PDF_DOCS_DIR
   ```

2. Review startup logs for "PATH CONFIGURATION" section

3. Check application.properties is not overriding values

### Issue: "Directory not found" error

1. Verify paths exist:
   ```bash
   # Windows
   dir C:\Users\bfrancis\projects\pdfdocs
   
   # Linux/Mac
   ls -la /path/to/pdfdocs
   ```

2. Ensure application has read/write permissions

3. Check for typos in property names (use `app.pdf.docs-dir`, not `app.pdf.docsdir`)

### Issue: Environment variables not taking effect

1. Restart the terminal/IDE after setting environment variables
2. For Docker, ensure variables are passed with `-e` flag
3. For system variables, restart the application

---

## Best Practices

✅ **DO:**
- Use environment variables for deployment-specific configuration
- Use profiles for different environments (dev, test, prod)
- Use absolute paths to avoid ambiguity
- Ensure directories exist before starting the application
- Use double backslashes in .properties files on Windows
- Document your configuration choices

❌ **DON'T:**
- Hardcode sensitive paths in source code
- Mix configuration methods inconsistently
- Use relative paths in production
- Forget to create required directories
- Commit environment-specific passwords/tokens to git

---

## Related Files

- **PathConfig.java** - Core configuration component
- **PDFUploadController.java** - Uses path configuration
- **PDFIndexService.java** - Uses path configuration
- **PDFSearchService.java** - Uses path configuration

