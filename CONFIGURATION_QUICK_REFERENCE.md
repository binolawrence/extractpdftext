# Quick Reference - Configuration Setup

## TL;DR

### Fastest Setup (Properties File)

1. Edit `src/main/resources/application.properties`:
```properties
app.pdf.docs-dir=C:\\your\\path\\pdfs
app.lucene.index-dir=C:\\your\\path\\lucene-index
```

2. Run:
```bash
mvn clean install
```

---

### Alternative: Environment Variables

#### Windows:
```batch
setx PDF_DOCS_DIR "C:\your\path\pdfs"
setx LUCENE_INDEX_DIR "C:\your\path\lucene-index"
```

#### Linux/Mac:
```bash
export PDF_DOCS_DIR="/your/path/pdfs"
export LUCENE_INDEX_DIR="/your/path/lucene-index"
```

Then run the app.

---

## Configuration Priority

| Priority | Source | Override |
|----------|--------|----------|
| 1 (Highest) | Environment Variables | Overrides everything |
| 2 | application.properties | Overrides defaults |
| 3 (Lowest) | Hard-coded values | When nothing else set |

---

## Property Names

```properties
# In application.properties
app.pdf.docs-dir=C:\\path\\to\\pdfs
app.lucene.index-dir=C:\\path\\to\\index

# As environment variables
PDF_DOCS_DIR=C:\path\to\pdfs
LUCENE_INDEX_DIR=C:\path\to\index

# In YAML format
app:
  pdf:
    docs-dir: C:\path\to\pdfs
  lucene:
    index-dir: C:\path\to\index
```

---

## Verify Configuration

Look for this output when app starts:

```
===== PATH CONFIGURATION =====
PDF Docs Directory: C:\your\path\pdfs
  Source: Environment variable (PDF_DOCS_DIR)
Lucene Index Directory: C:\your\path\index
  Source: application.properties (app.lucene.index-dir)
==============================
```

---

## Files Created

| File | Purpose |
|------|---------|
| PathConfig.java | Core config component |
| application.properties | Base configuration |
| application-dev.properties | Development profile |
| application-prod.properties | Production profile |
| application-test.properties | Test profile |
| application.yml.example | YAML format example |

---

## Common Scenarios

### Local Development
```properties
app.pdf.docs-dir=C:\\Users\\YourName\\pdfs
app.lucene.index-dir=C:\\Users\\YourName\\lucene-index
```

### Production Server
```bash
export PDF_DOCS_DIR="/var/app/pdfs"
export LUCENE_INDEX_DIR="/var/app/lucene-index"
java -jar app.jar
```

### Docker
```dockerfile
ENV PDF_DOCS_DIR=/data/pdfs
ENV LUCENE_INDEX_DIR=/data/lucene-index
```

---

## Troubleshooting

**Wrong path being used?**
1. Check `echo %PDF_DOCS_DIR%` (Windows) or `echo $PDF_DOCS_DIR` (Linux)
2. Look at startup output for source indicator
3. Check application.properties has correct values

**Directory not found?**
1. Verify directory exists: `dir C:\path\to\dir`
2. Check permissions: `icacls C:\path\to\dir`
3. Use absolute paths, not relative

**Still not working?**
1. Restart terminal after setting env vars
2. Restart IDE
3. Check spelling of property names
4. Review CONFIGURATION_GUIDE.md for detailed instructions

---

## Next Steps

- Read **CONFIGURATION_GUIDE.md** for complete documentation
- Check **ENVIRONMENT_VARIABLES_IMPLEMENTATION_SUMMARY.md** for technical details
- Set your paths in `application.properties`
- Run `mvn clean install` to verify

